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

    /**
     * Safely extracts a long value from a JSON response map.
     * Handles various numeric types that JSON libraries might return (Integer, Long, Double, etc.).
     *
     * @param map the JSON response map
     * @param key the key to extract
     * @return the long value, or null if the key doesn't exist or value is null
     * @throws IllegalArgumentException if the value cannot be converted to a long
     */
    private Long extractLong(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        throw new IllegalArgumentException(
            String.format("Expected numeric value for key '%s' but got %s", key, value.getClass().getName())
        );
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
        assertThat(extractLong(body, "id")).isEqualTo(defaultEmployeeId);
        assertThat(body.get("email")).isEqualTo(DEFAULT_EMPLOYEE_EMAIL);
        assertThat(extractLong(body, "positionId")).isEqualTo(defaultPositionId);
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
        assertThat(extractLong(firstExpense, "expenseUserId")).isEqualTo(defaultUserId);
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
        assertThat(extractLong(firstPayroll, "expenseUserId")).isEqualTo(defaultUserId);
        assertThat(extractLong(firstPayroll, "employeeId")).isEqualTo(defaultEmployeeId);
    }
}
