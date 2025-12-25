package io.sunbit.app.test.support;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ActiveProfiles("test")
@SpringBootTest(
    classes = AbstractApiIntegrationTest.IntegrationTestConfig.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "spring.main.allow-bean-definition-overriding=true")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractApiIntegrationTest {

    protected static final ParameterizedTypeReference<Map<String, Object>> MAP_TYPE =
        new ParameterizedTypeReference<>() {};
    protected static final ParameterizedTypeReference<List<Map<String, Object>>> LIST_TYPE =
        new ParameterizedTypeReference<>() {};

    protected static final String DEFAULT_EMPLOYEE_EMAIL = "laura.lopez@example.com";
    protected static final String DEFAULT_USER_EMAIL = "smoke.api@test.local";
    protected static final String DEFAULT_USER_PASSWORD = "Smoke!9";
    protected static final String ADMIN_EMPLOYEE_EMAIL = "admin.manager@example.com";
    protected static final String ADMIN_USER_EMAIL = "smoke.admin@test.local";
    protected static final String ADMIN_USER_PASSWORD = "Admin!9";

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    protected long defaultPositionId;
    protected long defaultEmployeeId;
    protected long defaultUserId;
    protected long defaultExpenseId;
    protected long defaultPayrollId;
    protected long adminEmployeeId;
    protected long adminUserId;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private String cachedUserToken;
    private String cachedAdminToken;

    @BeforeEach
    void initializeDefaultData() {
        purgeData();
        seedBaselineData();
        additionalSetup();
    }

    protected void additionalSetup() {
        // Extension hook for subclasses
    }

    protected void purgeData() {
        jdbcTemplate.update("DELETE FROM payroll");
        jdbcTemplate.update("DELETE FROM expense");
        jdbcTemplate.update("DELETE FROM user_role");
        jdbcTemplate.update("DELETE FROM expense_user");
        jdbcTemplate.update("DELETE FROM employee");
        jdbcTemplate.update("DELETE FROM role");
        jdbcTemplate.update("DELETE FROM position");
        cachedUserToken = null;
        cachedAdminToken = null;
    }

    protected void seedBaselineData() {
        defaultPositionId = insertPosition("Project Manager");
        defaultEmployeeId = insertEmployee("Laura", "Lopez", DEFAULT_EMPLOYEE_EMAIL, defaultPositionId);
        defaultUserId = insertExpenseUser(defaultEmployeeId,
            "Laura",
            "Lopez",
            DEFAULT_USER_EMAIL,
            DEFAULT_USER_PASSWORD,
            "ROLE_USER");

        long adminPositionId = insertPosition("Operations Lead");
        adminEmployeeId = insertEmployee("Alex", "Admin", ADMIN_EMPLOYEE_EMAIL, adminPositionId);
        adminUserId = insertExpenseUser(adminEmployeeId,
            "Alex",
            "Admin",
            ADMIN_USER_EMAIL,
            ADMIN_USER_PASSWORD,
            "ROLE_ADMIN",
            "ROLE_USER");

        defaultExpenseId = insertExpense(defaultUserId,
            "Team Lunch",
            "Smoke fixture data",
            LocalDateTime.of(2024, 2, 1, 12, 0),
            85.50);

        defaultPayrollId = insertPayroll(defaultEmployeeId,
            defaultUserId,
            LocalDateTime.of(2024, 2, 28, 0, 0),
            3200.00);
    }

    protected long insertPosition(String name) {
        SimpleJdbcInsert positionInsert = new SimpleJdbcInsert(jdbcTemplate)
            .withTableName("position")
            .usingGeneratedKeyColumns("id");
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("name", name);
        return positionInsert.executeAndReturnKey(params).longValue();
    }

    protected long insertEmployee(String name, String surname, String email, long positionId) {
        SimpleJdbcInsert employeeInsert = new SimpleJdbcInsert(jdbcTemplate)
            .withTableName("employee")
            .usingGeneratedKeyColumns("id");
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("name", name)
            .addValue("surname", surname)
            .addValue("birth_date", Timestamp.valueOf(LocalDateTime.of(1990, 1, 5, 0, 0)))
            .addValue("email", email)
            .addValue("position_id", positionId);
        return employeeInsert.executeAndReturnKey(params).longValue();
    }

    protected long insertExpenseUser(long employeeId,
        String name,
        String surname,
        String email,
        String rawPassword,
        String... roles) {
        String encodedPassword = passwordEncoder.encode(rawPassword);
        SimpleJdbcInsert userInsert = new SimpleJdbcInsert(jdbcTemplate)
            .withTableName("expense_user")
            .usingGeneratedKeyColumns("id");
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("name", name)
            .addValue("surname", surname)
            .addValue("email", email)
            .addValue("password", encodedPassword)
            .addValue("employee_id", employeeId);
        long userId = userInsert.executeAndReturnKey(params).longValue();
        for (String roleName : roles) {
            long roleId = ensureRoleExists(roleName);
            jdbcTemplate.update("INSERT INTO user_role (user_id, role_id) VALUES (?, ?)", userId, roleId);
        }
        return userId;
    }

    protected long ensureRoleExists(String roleName) {
        List<Long> ids = jdbcTemplate.queryForList(
            "SELECT id FROM role WHERE name = ?",
            Long.class,
            roleName);
        if (!ids.isEmpty()) {
            return ids.get(0);
        }
        SimpleJdbcInsert roleInsert = new SimpleJdbcInsert(jdbcTemplate)
            .withTableName("role")
            .usingGeneratedKeyColumns("id");
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("name", roleName);
        return roleInsert.executeAndReturnKey(params).longValue();
    }

    protected long insertExpense(long expenseUserId,
        String concept,
        String note,
        LocalDateTime expenseDate,
        double amount) {
        SimpleJdbcInsert expenseInsert = new SimpleJdbcInsert(jdbcTemplate)
            .withTableName("expense")
            .usingGeneratedKeyColumns("id");
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("concept", concept)
            .addValue("note", note)
            .addValue("expense_date", Timestamp.valueOf(expenseDate))
            .addValue("amount", amount)
            .addValue("expense_user_id", expenseUserId);
        return expenseInsert.executeAndReturnKey(params).longValue();
    }

    protected long insertPayroll(long employeeId,
        long expenseUserId,
        LocalDateTime payrollDate,
        double amount) {
        SimpleJdbcInsert payrollInsert = new SimpleJdbcInsert(jdbcTemplate)
            .withTableName("payroll")
            .usingGeneratedKeyColumns("id");
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("amount", amount)
            .addValue("payroll_date", Timestamp.valueOf(payrollDate))
            .addValue("employee_id", employeeId)
            .addValue("expense_user_id", expenseUserId);
        return payrollInsert.executeAndReturnKey(params).longValue();
    }

    protected String loginAsDefaultUser() {
        if (cachedUserToken == null) {
            cachedUserToken = authenticate(DEFAULT_USER_EMAIL, DEFAULT_USER_PASSWORD);
        }
        return cachedUserToken;
    }

    protected String loginAsAdmin() {
        if (cachedAdminToken == null) {
            cachedAdminToken = authenticate(ADMIN_USER_EMAIL, ADMIN_USER_PASSWORD);
        }
        return cachedAdminToken;
    }

    private String authenticate(String email, String password) {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            "/api/v1/auth/login",
            HttpMethod.POST,
            jsonEntity(body),
            MAP_TYPE);
        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new IllegalStateException("Unable to authenticate user " + email);
        }
        Object token = response.getBody().get("accessToken");
        if (token instanceof String tokenValue) {
            return tokenValue;
        }
        throw new IllegalStateException("Authentication response missing access token for " + email);
    }

    protected HttpHeaders headersForToken(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    protected HttpEntity<Void> emptyEntityForToken(String token) {
        return new HttpEntity<>(headersForToken(token));
    }

    protected <T> HttpEntity<T> jsonEntityForToken(T body, String token) {
        HttpHeaders headers = headersForToken(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    protected <T> HttpEntity<T> jsonEntity(T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @ComponentScan(basePackages = "io.sunbit.app")
    @EntityScan(basePackages = {
        "io.sunbit.app.entity",
        "io.sunbit.app.security.entity",
        "io.sunbit.app.security.controller",
        "io.sunbit.app.controller"})
    @EnableJpaRepositories(basePackages = {
        "io.sunbit.app.dao",
        "io.sunbit.app.security.dao"})
    static class IntegrationTestConfig {
        // Shares application configuration for API integration tests
    }
}
