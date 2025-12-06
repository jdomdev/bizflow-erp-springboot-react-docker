package io.sunbit.app.test.expense;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import io.sunbit.app.dao.IEmployeeDao;
import io.sunbit.app.dao.IExpenseDao;
import io.sunbit.app.dao.IPositionDao;
import io.sunbit.app.entity.Employee;
import io.sunbit.app.entity.Expense;
import io.sunbit.app.entity.ExpenseStatus;
import io.sunbit.app.entity.Payroll;
import io.sunbit.app.util.DateUtil;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ExpenseTest {

	@Autowired
	IExpenseDao expenseDao;
	@Autowired
	IEmployeeDao employeeDao;
	@Autowired
	IPositionDao positionDao;

	@Test
	@DisplayName(value = "Test 1 -> test expense saving\n"
			+ "1.1 - savedExpense.isNotNull()\n"
			+ "1.2 - savedExpense.getId().isGreaterThan(0)\n")
	public void testExpenseSaving() {
		// Find or create an employee for testing
		Optional<Employee> optEmployee = employeeDao.findById(1L);
		Employee employee;
		if (optEmployee.isPresent()) {
			employee = optEmployee.get();
		} else {
			// Create a minimal employee for testing
			employee = new Employee();
			employee.setName("Test");
			employee.setSurname("Employee");
			employee.setEmail("test.employee@example.com");
			employee.setBirthDate(LocalDateTime.of(1990, 1, 1, 0, 0));
			employee = employeeDao.save(employee);
		}

		Expense newExpense = new Expense(
			"Conference Travel",
			"Business trip to conference",
			LocalDateTime.now(),
			150.00,
			employee
		);
		
		Expense savedExpense = expenseDao.save(newExpense);

		assertThat(savedExpense).isNotNull();
		assertThat(savedExpense.getId()).isGreaterThan(0);
		assertThat(savedExpense.getStatus()).isEqualTo(ExpenseStatus.PENDING);
		assertThat(savedExpense.getAmount()).isEqualTo(150.00);
	}

	@Test
	@DisplayName(value = "Test 2 -> test expense updating")
	public void testExpenseUpdating() {
		// First, save an expense
		Optional<Employee> optEmployee = employeeDao.findById(1L);
		if (!optEmployee.isPresent()) {
			Employee employee = new Employee();
			employee.setName("Test");
			employee.setSurname("Employee");
			employee.setEmail("test2.employee@example.com");
			employee.setBirthDate(LocalDateTime.of(1990, 1, 1, 0, 0));
			employeeDao.save(employee);
			optEmployee = Optional.of(employee);
		}

		Expense expense = new Expense(
			"Taxi",
			"City travel",
			DateUtil.formattingDate(LocalDateTime.of(2022, 03, 12, 10, 24, 00)),
			46.1,
			optEmployee.get()
		);
		expense = expenseDao.save(expense);

		// Now update it
		expense.setConcept("Taxi - Updated");
		expense.setAmount(50.0);
		
		Expense updatedExpense = expenseDao.save(expense);
		
		assertThat(updatedExpense).isNotNull();
		assertThat(updatedExpense.getId()).isGreaterThan(0);
		assertThat(updatedExpense.getConcept()).isEqualTo("Taxi - Updated");
		assertThat(updatedExpense.getAmount()).isEqualTo(50.0);
	}
	
	@Test
	@DisplayName(value = "Test 3 -> test expense status workflow")
	public void testExpenseStatusWorkflow() {
		// Create an expense
		Optional<Employee> optEmployee = employeeDao.findById(1L);
		if (!optEmployee.isPresent()) {
			Employee employee = new Employee();
			employee.setName("Test");
			employee.setSurname("Employee");
			employee.setEmail("test3.employee@example.com");
			employee.setBirthDate(LocalDateTime.of(1990, 1, 1, 0, 0));
			employeeDao.save(employee);
			optEmployee = Optional.of(employee);
		}

		Expense expense = new Expense(
			"Office Supplies",
			"Purchase of office supplies",
			LocalDateTime.now(),
			75.50,
			optEmployee.get()
		);
		expense = expenseDao.save(expense);
		
		// Test initial status
		assertThat(expense.getStatus()).isEqualTo(ExpenseStatus.PENDING);
		assertThat(expense.isPending()).isTrue();
		
		// Test approve
		expense.approve("admin@example.com");
		expense = expenseDao.save(expense);
		
		assertThat(expense.getStatus()).isEqualTo(ExpenseStatus.APPROVED);
		assertThat(expense.isApproved()).isTrue();
		assertThat(expense.getApprovedBy()).isEqualTo("admin@example.com");
		assertThat(expense.getApprovalDate()).isNotNull();
	}
	
	@Test
	@DisplayName(value = "Test 4 -> test finding expenses by status")
	public void testFindByStatus() {
		// Create expenses with different statuses
		Optional<Employee> optEmployee = employeeDao.findById(1L);
		if (!optEmployee.isPresent()) {
			Employee employee = new Employee();
			employee.setName("Test");
			employee.setSurname("Employee");
			employee.setEmail("test4.employee@example.com");
			employee.setBirthDate(LocalDateTime.of(1990, 1, 1, 0, 0));
			employeeDao.save(employee);
			optEmployee = Optional.of(employee);
		}

		Expense pendingExpense = new Expense(
			"Pending Expense",
			"Test pending",
			LocalDateTime.now(),
			100.0,
			optEmployee.get()
		);
		expenseDao.save(pendingExpense);
		
		Expense approvedExpense = new Expense(
			"Approved Expense",
			"Test approved",
			LocalDateTime.now(),
			200.0,
			optEmployee.get()
		);
		approvedExpense.approve("admin@example.com");
		expenseDao.save(approvedExpense);
		
		// Test finding by status
		var pendingExpenses = expenseDao.findByStatus(ExpenseStatus.PENDING);
		assertThat(pendingExpenses).isNotEmpty();
		assertThat(pendingExpenses.stream().anyMatch(e -> e.getConcept().equals("Pending Expense"))).isTrue();
		
		var approvedExpenses = expenseDao.findByStatus(ExpenseStatus.APPROVED);
		assertThat(approvedExpenses).isNotEmpty();
		assertThat(approvedExpenses.stream().anyMatch(e -> e.getConcept().equals("Approved Expense"))).isTrue();
	}
}

