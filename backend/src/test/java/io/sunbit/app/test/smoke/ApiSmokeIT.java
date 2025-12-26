package io.sunbit.app.test.smoke;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import io.sunbit.app.test.support.AbstractApiIntegrationTest;

@Tag("smoke")
class ApiSmokeIT extends AbstractApiIntegrationTest {

    @Test
    void loginShouldReturnAccessToken() {
        assertThat(loginAsDefaultUser()).isNotBlank();
    }

    @Test
    void getEmployeeProfileShouldReturnExpectedPayload() {
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            "/api/v1/employee/" + defaultEmployeeId,
            HttpMethod.GET,
            emptyEntityForToken(loginAsDefaultUser()),
            MAP_TYPE);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(extractLongValue(body, "id")).isEqualTo(defaultEmployeeId);
        assertThat(body.get("email")).isEqualTo(DEFAULT_EMPLOYEE_EMAIL);
        assertThat(extractLongValue(body, "positionId")).isEqualTo(defaultPositionId);
    }

    @Test
    void listExpensesShouldReturnSmokeFixture() {
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
            "/api/v1/expense/user/" + defaultUserId,
            HttpMethod.GET,
            emptyEntityForToken(loginAsDefaultUser()),
            LIST_TYPE);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Map<String, Object>> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body).isNotEmpty();
        Map<String, Object> firstExpense = body.get(0);
        assertThat(firstExpense.get("concept")).isEqualTo("Team Lunch");
        assertThat(extractLongValue(firstExpense, "expenseUserId")).isEqualTo(defaultUserId);
    }

    @Test
    void listPayrollsShouldReturnSmokeFixture() {
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
            "/api/v1/payroll/user/" + defaultUserId,
            HttpMethod.GET,
            emptyEntityForToken(loginAsDefaultUser()),
            LIST_TYPE);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Map<String, Object>> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body).isNotEmpty();
        Map<String, Object> firstPayroll = body.get(0);
        assertThat(extractLongValue(firstPayroll, "expenseUserId")).isEqualTo(defaultUserId);
        assertThat(extractLongValue(firstPayroll, "employeeId")).isEqualTo(defaultEmployeeId);
    }
}
