package io.sunbit.app.test.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.sunbit.app.dao.IExpenseDao;
import io.sunbit.app.entity.Employee;
import io.sunbit.app.entity.Expense;
import io.sunbit.app.entity.Position;
import io.sunbit.app.security.jwt.JwtAuthenticationUtil;
import io.sunbit.app.service.ExpenseServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ExpenseServiceTest {

    @Mock
    private IExpenseDao expenseDao;

    @Mock
    private JwtAuthenticationUtil jwtAuthUtil;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    private Expense expense;
    private Employee employee;

    @BeforeEach
    public void setup() {
        Position position = new Position();
        position.setId(1L);
        position.setName("Developer");

        employee = new Employee();
        employee.setId(1L);
        employee.setName("John");
        employee.setSurname("Smith");
        employee.setEmail("john.smith@example.com");
        employee.setBirthDate(LocalDateTime.of(1990, 1, 1, 0, 0));
        employee.setPosition(position);

        expense = new Expense();
        expense.setId(1L);
        expense.setConcept("Taxi");
        expense.setAmount(50.0);
        expense.setDate(LocalDateTime.now());
        expense.setNote("Business trip");
        expense.setEmployee(employee);
    }

    @Test
    public void testFindAll() throws Exception {
        // Given
        List<Expense> expenses = Arrays.asList(expense);
        when(expenseDao.findAll()).thenReturn(expenses);

        // When
        List<Expense> result = expenseService.findAll();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getConcept()).isEqualTo("Taxi");
        verify(expenseDao, times(1)).findAll();
    }

    @Test
    public void testDelete() throws Exception {
        // Given
        Long expenseId = 1L;
        when(expenseDao.existsById(expenseId)).thenReturn(true);

        // When
        Boolean result = expenseService.delete(expenseId);

        // Then
        assertThat(result).isTrue();
        verify(expenseDao, times(1)).deleteById(expenseId);
    }

    @Test
    public void testFindAllByEmployeeId() throws Exception {
        // Given
        String headerAuth = "Bearer token";
        List<Expense> expenses = Arrays.asList(expense);
        when(jwtAuthUtil.isAdminTokenUser(any(String.class))).thenReturn(true);
        when(expenseDao.findAllByEmployeeId(1L)).thenReturn(expenses);

        // When
        List<Expense> result = expenseService.findAllByEmployeeId(1L, headerAuth);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getEmployee().getId()).isEqualTo(1L);
        verify(expenseDao, times(1)).findAllByEmployeeId(1L);
    }
}
