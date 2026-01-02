package io.sunbit.app.test.regression;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import io.sunbit.app.test.support.AbstractApiIntegrationTest;

@Tag("regression")
class ApiRegressionIT extends AbstractApiIntegrationTest {

    @Test
    void loginWithInvalidPasswordShouldReturnUnauthorized() {
        Map<String, String> body = Map.of(
            "email", DEFAULT_USER_EMAIL,
            "password", "Invalid!9");

        ResponseEntity<Void> response = restTemplate.exchange(
            "/api/v1/auth/login",
            HttpMethod.POST,
            jsonEntity(body),
            Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void adminCanCreateAndFetchExpenseById() {
        String adminToken = loginAsAdmin();
        Map<String, Object> payload = new HashMap<>();
        payload.put("concept", "Client Dinner");
        payload.put("note", "Regression create expense");
        payload.put("expenseDate", LocalDateTime.of(2024, 3, 15, 20, 0).toString());
        payload.put("amount", 142.75);
        payload.put("expenseUserId", defaultUserId);

        ResponseEntity<Map<String, Object>> createResponse = restTemplate.exchange(
            "/api/v1/expense/",
            HttpMethod.POST,
            jsonEntityForToken(payload, adminToken),
            MAP_TYPE);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Map<String, Object> created = createResponse.getBody();
        assertThat(created).isNotNull();
        long expenseId = extractLongValue(created, "id");
        assertThat(created.get("concept")).isEqualTo("Client Dinner");
        assertThat(extractLongValue(created, "expenseUserId")).isEqualTo(defaultUserId);

        ResponseEntity<Map<String, Object>> fetchResponse = restTemplate.exchange(
            "/api/v1/expense/" + expenseId,
            HttpMethod.GET,
            emptyEntityForToken(adminToken),
            MAP_TYPE);

        assertThat(fetchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> fetched = fetchResponse.getBody();
        assertThat(fetched).isNotNull();
        assertThat(extractLongValue(fetched, "id")).isEqualTo(expenseId);
        assertThat(fetched.get("concept")).isEqualTo("Client Dinner");
    }

    @Test
    void adminCanUpdateExistingExpense() {
        String adminToken = loginAsAdmin();
        Map<String, Object> updatePayload = new HashMap<>();
        updatePayload.put("id", defaultExpenseId);
        updatePayload.put("concept", "Team Lunch - Updated");
        updatePayload.put("note", "Adjusted subtotal for regression test");
        updatePayload.put("expenseDate", LocalDateTime.of(2024, 2, 2, 12, 0).toString());
        updatePayload.put("amount", 99.99);
        updatePayload.put("expenseUserId", defaultUserId);

        ResponseEntity<Map<String, Object>> updateResponse = restTemplate.exchange(
            "/api/v1/expense/",
            HttpMethod.PUT,
            jsonEntityForToken(updatePayload, adminToken),
            MAP_TYPE);

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> updated = updateResponse.getBody();
        assertThat(updated).isNotNull();
        assertThat(extractLongValue(updated, "id")).isEqualTo(defaultExpenseId);
        assertThat(updated.get("concept")).isEqualTo("Team Lunch - Updated");

        ResponseEntity<List<Map<String, Object>>> listResponse = restTemplate.exchange(
            "/api/v1/expense/user/" + defaultUserId,
            HttpMethod.GET,
            emptyEntityForToken(adminToken),
            LIST_TYPE);

        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Map<String, Object>> expenses = listResponse.getBody();
        assertThat(expenses).isNotNull();
        Map<String, Object> matching = expenses.stream()
            .filter(expense -> extractLongValue(expense, "id") == defaultExpenseId)
            .findFirst()
            .orElse(null);
        assertThat(matching).isNotNull();
        assertThat(matching.get("concept")).isEqualTo("Team Lunch - Updated");
        assertThat(extractLongValue(matching, "expenseUserId")).isEqualTo(defaultUserId);
    }

    @Test
    void userCannotViewAnotherUsersExpenses() {
        long otherPositionId = insertPosition("Quality Analyst");
        long otherEmployeeId = insertEmployee(
            "Maria",
            "Gomez",
            "maria.gomez@example.com",
            otherPositionId);
        long otherUserId = insertExpenseUser(
            otherEmployeeId,
            "Maria",
            "Gomez",
            "regression.user@test.local",
            "User!9",
            "ROLE_USER");
        insertExpense(
            otherUserId,
            "Private Expense",
            "Should remain hidden",
            LocalDateTime.of(2024, 4, 10, 9, 0),
            45.00);

        ResponseEntity<String> response = restTemplate.exchange(
            "/api/v1/expense/user/" + otherUserId,
            HttpMethod.GET,
            emptyEntityForToken(loginAsDefaultUser()),
            String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("NOT possible to SHOW the user's expenses");
    }

    @Test
    void adminCanDeleteExpense() {
        String adminToken = loginAsAdmin();
        long temporaryExpenseId = insertExpense(
            defaultUserId,
            "Temporary Expense",
            "To be deleted",
            LocalDateTime.of(2024, 5, 1, 8, 0),
            20.00);

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
            "/api/v1/expense/" + temporaryExpenseId,
            HttpMethod.DELETE,
            emptyEntityForToken(adminToken),
            Void.class);

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<List<Map<String, Object>>> postDelete = restTemplate.exchange(
            "/api/v1/expense/user/" + defaultUserId,
            HttpMethod.GET,
            emptyEntityForToken(adminToken),
            LIST_TYPE);

        assertThat(postDelete.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Map<String, Object>> expenses = postDelete.getBody();
        assertThat(expenses).isNotNull();
        boolean stillPresent = false;
        for (Map<String, Object> expense : expenses) {
            if (extractLongValue(expense, "id") == temporaryExpenseId) {
                stillPresent = true;
                break;
            }
        }
        assertThat(stillPresent).isFalse();
    }

    @Test
    void payrollCreationBindsToEmployeeAndExpenseUser() {
        String adminToken = loginAsAdmin();
        Map<String, Object> payload = new HashMap<>();
        payload.put("amount", 3300.00);
        payload.put("payrollDate", LocalDateTime.of(2024, 3, 31, 0, 0).toString());
        payload.put("employeeId", defaultEmployeeId);

        ResponseEntity<Map<String, Object>> createResponse = restTemplate.exchange(
            "/api/v1/payroll/",
            HttpMethod.POST,
            jsonEntityForToken(payload, adminToken),
            MAP_TYPE);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> created = createResponse.getBody();
        assertThat(created).isNotNull();
        long payrollId = extractLongValue(created, "id");
        assertThat(extractLongValue(created, "employeeId")).isEqualTo(defaultEmployeeId);

        ResponseEntity<List<Map<String, Object>>> listResponse = restTemplate.exchange(
            "/api/v1/payroll/user/" + defaultUserId,
            HttpMethod.GET,
            emptyEntityForToken(adminToken),
            LIST_TYPE);

        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Map<String, Object>> payrolls = listResponse.getBody();
        assertThat(payrolls).isNotNull();
        boolean found = false;
        for (Map<String, Object> payroll : payrolls) {
            if (extractLongValue(payroll, "id") == payrollId) {
                found = true;
                break;
            }
        }
        assertThat(found).isTrue();
    }
}
