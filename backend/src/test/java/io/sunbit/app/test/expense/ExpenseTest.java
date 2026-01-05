package io.sunbit.app.test.expense;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import io.sunbit.app.dao.IEmployeeDao;
import io.sunbit.app.dao.IExpenseDao;
import io.sunbit.app.dao.IPositionDao;
import io.sunbit.app.entity.Employee;
import io.sunbit.app.entity.Expense;
import io.sunbit.app.entity.Position;
import io.sunbit.app.security.dao.IRoleDao;
import io.sunbit.app.security.dao.IUserDao;
import io.sunbit.app.security.entity.ExpenseUser;
import io.sunbit.app.security.entity.Role;
import io.sunbit.app.util.DateUtil;

@ActiveProfiles("test")
@DataJpaTest
class ExpenseTest {

	@Autowired
	IExpenseDao expenseDao;
	@Autowired
	IEmployeeDao employeeDao;
	@Autowired
	IPositionDao positionDao;
	@Autowired
	IUserDao userDao;
	@Autowired
	IRoleDao roleDao;

	private ExpenseUser persistedUser;

	@BeforeEach
	void setUp() {
		Role role = roleDao.findByName("ROLE_USER").orElseGet(() -> roleDao.save(new Role("ROLE_USER")));
		Position position = positionDao.save(new Position("QA Analyst"));
		Employee employee = employeeDao.save(new Employee(
			"Sylvester",
			"Stewart",
			LocalDateTime.of(1990, 1, 5, 0, 0),
			position,
			uniqueEmail(),
			new ArrayList<>()));

		ExpenseUser user = new ExpenseUser();
		user.setName("Sylvester");
		user.setSurname("Stewart");
		user.setEmail(employee.getEmail());
		user.setPassword("dummyPassword");
		user.setEmployee(employee);
		user.addRole(role);
		persistedUser = userDao.save(user);
	}

	@Test
	@DisplayName("Persists a new expense")
	void saveExpensePersistsEntity() {
		Expense savedExpense = expenseDao.save(createExpense(
			"Taxi",
			"Nota de taxi",
			LocalDateTime.of(2022, 3, 12, 10, 24),
			46.1));
		assertThat(savedExpense.getId()).isNotNull();
		assertThat(savedExpense.getExpenseUser().getId()).isEqualTo(persistedUser.getId());
	}

	@Test
	@DisplayName("Updates an existing expense")
	void updateExpenseChangesValues() {
		Expense savedExpense = expenseDao.save(createExpense(
			"Taxi",
			"Nota de taxi",
			LocalDateTime.of(2022, 3, 12, 10, 24),
			46.1));
		savedExpense.setConcept("Taxi actualizado");
		Expense updatedExpense = expenseDao.save(savedExpense);
		assertThat(updatedExpense.getConcept()).isEqualTo("Taxi actualizado");
	}

	@Test
	@DisplayName("Deletes an expense")
	void deleteExpenseRemovesEntity() {
		Expense savedExpense = expenseDao.save(createExpense(
			"Taxi",
			"Nota de taxi",
			LocalDateTime.of(2022, 3, 12, 10, 24),
			46.1));
		Long id = savedExpense.getId();
		expenseDao.delete(savedExpense);
		assertThat(expenseDao.findById(id)).isEmpty();
	}

	@Test
	@DisplayName("Finds an expense by id")
	void findExpenseByIdReturnsEntity() {
		Expense savedExpense = expenseDao.save(createExpense(
			"Hotel",
			"Nota de hotel",
			LocalDateTime.of(2022, 4, 10, 12, 0),
			120.0));
		assertThat(expenseDao.findById(savedExpense.getId())).isPresent();
		assertThat(expenseDao.findById(savedExpense.getId()).orElseThrow().getConcept()).isEqualTo("Hotel");
	}

	@Test
	@DisplayName("Maintains the expense-user relation")
	void saveExpenseKeepsUserRelation() {
		Expense savedExpense = expenseDao.save(createExpense(
			"Comida",
			"Nota de comida",
			LocalDateTime.of(2022, 5, 1, 14, 0),
			30.0));
		assertThat(savedExpense.getExpenseUser()).isNotNull();
		assertThat(savedExpense.getExpenseUser().getEmail()).isEqualTo(persistedUser.getEmail());
		assertThat(savedExpense.getExpenseUser().getRoles()).extracting(Role::getName).contains("ROLE_USER");
	}

	private Expense createExpense(String concept, String note, LocalDateTime expenseDate, double amount) {
		return new Expense(
			null,
			concept,
			note,
			DateUtil.formattingDate(expenseDate),
			amount,
			persistedUser);
	}

	private String uniqueEmail() {
		String token = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
		return "s.stewart_" + token + "@example.com";
	}
}
