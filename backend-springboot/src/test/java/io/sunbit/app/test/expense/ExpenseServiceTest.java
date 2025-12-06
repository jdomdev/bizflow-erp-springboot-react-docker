package io.sunbit.app.test.expense;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.sunbit.app.dao.IEmployeeDao;
import io.sunbit.app.dao.IExpenseAttachmentDao;
import io.sunbit.app.dao.IExpenseDao;
import io.sunbit.app.entity.Employee;
import io.sunbit.app.entity.Expense;
import io.sunbit.app.entity.ExpenseStatus;
import io.sunbit.app.security.jwt.JwtAuthenticationUtil;
import io.sunbit.app.service.ExpenseServiceImpl;
import io.sunbit.app.util.EmployeeUtil;

@ExtendWith(MockitoExtension.class)
public class ExpenseServiceTest {

	@Mock
	private IExpenseDao expenseDao;
	
	@Mock
	private IEmployeeDao employeeDao;
	
	@Mock
	private IExpenseAttachmentDao attachmentDao;
	
	@Mock
	private JwtAuthenticationUtil jwtAuthUtil;
	
	@Mock
	private EmployeeUtil employeeUtil;
	
	@InjectMocks
	private ExpenseServiceImpl expenseService;
	
	private Employee testEmployee;
	private Expense testExpense;
	private String adminToken;
	private String userToken;
	
	@BeforeEach
	void setUp() {
		// Setup test data
		testEmployee = new Employee();
		testEmployee.setId(1L);
		testEmployee.setName("John");
		testEmployee.setSurname("Doe");
		testEmployee.setEmail("john.doe@example.com");
		testEmployee.setBirthDate(LocalDateTime.of(1990, 1, 1, 0, 0));
		
		testExpense = new Expense(
			"Conference Travel",
			"Business trip",
			LocalDateTime.now(),
			150.0,
			testEmployee
		);
		testExpense.setId(1L);
		
		adminToken = "Bearer admin.token.here";
		userToken = "Bearer user.token.here";
	}
	
	@Test
	@DisplayName("Should save expense when user is authorized")
	void testSaveExpenseAuthorized() throws Exception {
		// Given
		when(jwtAuthUtil.isAdminTokenUser(anyString())).thenReturn(false);
		when(employeeUtil.matchEmployeeUserEmail(any(Employee.class), anyString())).thenReturn(true);
		when(expenseDao.save(any(Expense.class))).thenReturn(testExpense);
		
		// When
		Expense savedExpense = expenseService.save(testExpense, userToken);
		
		// Then
		assertThat(savedExpense).isNotNull();
		assertThat(savedExpense.getId()).isEqualTo(1L);
		verify(expenseDao).save(any(Expense.class));
	}
	
	@Test
	@DisplayName("Should throw SecurityException when saving expense without authorization")
	void testSaveExpenseUnauthorized() throws Exception {
		// Given
		when(jwtAuthUtil.isAdminTokenUser(anyString())).thenReturn(false);
		when(employeeUtil.matchEmployeeUserEmail(any(Employee.class), anyString())).thenReturn(false);
		
		// When & Then
		assertThatThrownBy(() -> expenseService.save(testExpense, userToken))
			.isInstanceOf(SecurityException.class)
			.hasMessageContaining("Unauthorized");
	}
	
	@Test
	@DisplayName("Should find expense by ID when authorized")
	void testFindByIdAuthorized() throws Exception {
		// Given
		when(expenseDao.findById(1L)).thenReturn(Optional.of(testExpense));
		when(jwtAuthUtil.isAdminTokenUser(anyString())).thenReturn(false);
		when(employeeDao.findById(1L)).thenReturn(Optional.of(testEmployee));
		when(employeeUtil.matchEmployeeUserEmail(any(Employee.class), anyString())).thenReturn(true);
		
		// When
		Expense foundExpense = expenseService.findById(1L, userToken);
		
		// Then
		assertThat(foundExpense).isNotNull();
		assertThat(foundExpense.getId()).isEqualTo(1L);
	}
	
	@Test
	@DisplayName("Should throw exception when expense not found")
	void testFindByIdNotFound() {
		// Given
		when(expenseDao.findById(anyLong())).thenReturn(Optional.empty());
		
		// When & Then
		assertThatThrownBy(() -> expenseService.findById(999L, adminToken))
			.isInstanceOf(Exception.class)
			.hasMessageContaining("not found");
	}
	
	@Test
	@DisplayName("Admin should approve pending expense")
	void testApproveExpenseByAdmin() throws Exception {
		// Given
		when(jwtAuthUtil.isAdminTokenUser(anyString())).thenReturn(true);
		when(jwtAuthUtil.getEmailFromToken(anyString())).thenReturn("admin@example.com");
		when(expenseDao.findById(1L)).thenReturn(Optional.of(testExpense));
		when(expenseDao.save(any(Expense.class))).thenReturn(testExpense);
		
		// When
		Expense approvedExpense = expenseService.approve(1L, adminToken);
		
		// Then
		assertThat(approvedExpense.getStatus()).isEqualTo(ExpenseStatus.APPROVED);
		assertThat(approvedExpense.getApprovedBy()).isEqualTo("admin@example.com");
		assertThat(approvedExpense.getApprovalDate()).isNotNull();
		verify(expenseDao).save(any(Expense.class));
	}
	
	@Test
	@DisplayName("Non-admin should not approve expense")
	void testApproveExpenseByNonAdmin() {
		// Given
		when(jwtAuthUtil.isAdminTokenUser(anyString())).thenReturn(false);
		
		// When & Then
		assertThatThrownBy(() -> expenseService.approve(1L, userToken))
			.isInstanceOf(SecurityException.class)
			.hasMessageContaining("Only administrators can approve");
	}
	
	@Test
	@DisplayName("Should not approve non-pending expense")
	void testApproveNonPendingExpense() {
		// Given
		testExpense.setStatus(ExpenseStatus.APPROVED);
		when(jwtAuthUtil.isAdminTokenUser(anyString())).thenReturn(true);
		when(expenseDao.findById(1L)).thenReturn(Optional.of(testExpense));
		
		// When & Then
		assertThatThrownBy(() -> expenseService.approve(1L, adminToken))
			.isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("Only pending expenses can be approved");
	}
	
	@Test
	@DisplayName("Admin should reject pending expense with reason")
	void testRejectExpenseByAdmin() throws Exception {
		// Given
		String rejectionReason = "Missing receipts";
		when(jwtAuthUtil.isAdminTokenUser(anyString())).thenReturn(true);
		when(jwtAuthUtil.getEmailFromToken(anyString())).thenReturn("admin@example.com");
		when(expenseDao.findById(1L)).thenReturn(Optional.of(testExpense));
		when(expenseDao.save(any(Expense.class))).thenReturn(testExpense);
		
		// When
		Expense rejectedExpense = expenseService.reject(1L, rejectionReason, adminToken);
		
		// Then
		assertThat(rejectedExpense.getStatus()).isEqualTo(ExpenseStatus.REJECTED);
		assertThat(rejectedExpense.getApprovedBy()).isEqualTo("admin@example.com");
		assertThat(rejectedExpense.getRejectionReason()).isEqualTo(rejectionReason);
		verify(expenseDao).save(any(Expense.class));
	}
	
	@Test
	@DisplayName("Should not reject without reason")
	void testRejectWithoutReason() {
		// Given
		when(jwtAuthUtil.isAdminTokenUser(anyString())).thenReturn(true);
		
		// When & Then
		assertThatThrownBy(() -> expenseService.reject(1L, "", adminToken))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("Rejection reason is required");
		
		assertThatThrownBy(() -> expenseService.reject(1L, null, adminToken))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("Rejection reason is required");
	}
	
	@Test
	@DisplayName("Should update pending expense")
	void testUpdatePendingExpense() throws Exception {
		// Given
		when(expenseDao.findById(1L)).thenReturn(Optional.of(testExpense));
		when(employeeDao.findById(1L)).thenReturn(Optional.of(testEmployee));
		when(jwtAuthUtil.isAdminTokenUser(anyString())).thenReturn(true);
		when(expenseDao.save(any(Expense.class))).thenReturn(testExpense);
		
		testExpense.setConcept("Updated Concept");
		
		// When
		Expense updatedExpense = expenseService.update(testExpense, adminToken);
		
		// Then
		assertThat(updatedExpense).isNotNull();
		verify(expenseDao).save(any(Expense.class));
	}
	
	@Test
	@DisplayName("Should not update approved expense")
	void testUpdateApprovedExpense() {
		// Given
		testExpense.setStatus(ExpenseStatus.APPROVED);
		when(expenseDao.findById(1L)).thenReturn(Optional.of(testExpense));
		when(employeeDao.findById(1L)).thenReturn(Optional.of(testEmployee));
		when(jwtAuthUtil.isAdminTokenUser(anyString())).thenReturn(true);
		
		// When & Then
		assertThatThrownBy(() -> expenseService.update(testExpense, adminToken))
			.isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("Cannot update an expense that has been approved or rejected");
	}
	
	@Test
	@DisplayName("Should find all expenses by employee ID")
	void testFindAllByEmployeeId() throws Exception {
		// Given
		List<Expense> expenses = Arrays.asList(testExpense);
		when(employeeDao.findById(1L)).thenReturn(Optional.of(testEmployee));
		when(jwtAuthUtil.isAdminTokenUser(anyString())).thenReturn(true);
		when(expenseDao.findAllByEmployeeId(1L)).thenReturn(expenses);
		
		// When
		List<Expense> foundExpenses = expenseService.findAllByEmployeeId(1L, adminToken);
		
		// Then
		assertThat(foundExpenses).isNotEmpty();
		assertThat(foundExpenses).hasSize(1);
		assertThat(foundExpenses.get(0).getId()).isEqualTo(1L);
	}
	
	@Test
	@DisplayName("Should find expenses by status as admin")
	void testFindByStatus() throws Exception {
		// Given
		List<Expense> pendingExpenses = Arrays.asList(testExpense);
		when(jwtAuthUtil.isAdminTokenUser(anyString())).thenReturn(true);
		when(expenseDao.findByStatus(ExpenseStatus.PENDING)).thenReturn(pendingExpenses);
		
		// When
		List<Expense> foundExpenses = expenseService.findByStatus(ExpenseStatus.PENDING, adminToken);
		
		// Then
		assertThat(foundExpenses).isNotEmpty();
		assertThat(foundExpenses.get(0).getStatus()).isEqualTo(ExpenseStatus.PENDING);
	}
	
	@Test
	@DisplayName("Non-admin should not find expenses by status")
	void testFindByStatusAsNonAdmin() {
		// Given
		when(jwtAuthUtil.isAdminTokenUser(anyString())).thenReturn(false);
		
		// When & Then
		assertThatThrownBy(() -> expenseService.findByStatus(ExpenseStatus.PENDING, userToken))
			.isInstanceOf(SecurityException.class)
			.hasMessageContaining("Only administrators can view expenses by status");
	}
	
	@Test
	@DisplayName("Should delete expense")
	void testDeleteExpense() throws Exception {
		// Given
		when(expenseDao.existsById(1L)).thenReturn(true);
		
		// When
		Boolean deleted = expenseService.delete(1L);
		
		// Then
		assertThat(deleted).isTrue();
		verify(expenseDao).deleteById(1L);
	}
	
	@Test
	@DisplayName("Should throw exception when deleting non-existent expense")
	void testDeleteNonExistentExpense() {
		// Given
		when(expenseDao.existsById(anyLong())).thenReturn(false);
		
		// When & Then
		assertThatThrownBy(() -> expenseService.delete(999L))
			.isInstanceOf(Exception.class)
			.hasMessageContaining("not found");
	}
}
