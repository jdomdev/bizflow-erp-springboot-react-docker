package io.sunbit.app.test.expense;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
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
import io.sunbit.app.entity.Expense;
import io.sunbit.app.security.entity.ExpenseUser;
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
	@DisplayName("Test expense saving")
	public void testExpenseSaving() {
		ExpenseUser expenseUser = new ExpenseUser();
		expenseUser.setId(58L);
		expenseUser.setName("Sylvester");
		expenseUser.setSurname("Stewart");
		expenseUser.setEmail("slystone@gmail.com");
		expenseUser.setPassword("dummyPassword");
		Expense newExpense = new Expense(
			null,
			"Taxi",
			"Nota de taxi",
			DateUtil.formattingDate(LocalDateTime.of(2022, 3, 12, 10, 24, 0)),
			46.1,
			expenseUser
		);
		Expense savedExpense = expenseDao.save(newExpense);
		assertThat(savedExpense).isNotNull();
		assertThat(savedExpense.getId()).isGreaterThan(0);
	}


	@Test
	@DisplayName("Test expense updating")
	public void testExpenseUpdating() {
		ExpenseUser testUser = new ExpenseUser();
		testUser.setId(58L);
		Optional<Expense> optOldExpense = expenseDao.findByAmountAndExpenseDateAndConceptAndExpenseUser(
			46.1,
			DateUtil.formattingDate(LocalDateTime.of(2022, 3, 12, 10, 24, 0)),
			"Taxi",
			testUser);
		assertThat(optOldExpense).isPresent();
		Expense expense = optOldExpense.get();
		expense.setConcept("Taxi actualizado");
		Expense updatedExpense = expenseDao.save(expense);
		assertThat(updatedExpense.getConcept()).isEqualTo("Taxi actualizado");
	}

	@Test
	@DisplayName("Test expense deleting")
	public void testExpenseDeleting() {
		ExpenseUser testUser = new ExpenseUser();
		testUser.setId(58L);
		Optional<Expense> optOldExpense = expenseDao.findByAmountAndExpenseDateAndConceptAndExpenseUser(
			46.1,
			DateUtil.formattingDate(LocalDateTime.of(2022, 3, 12, 10, 24, 0)),
			"Taxi actualizado",
			testUser);
		assertThat(optOldExpense).isPresent();
		Expense expense = optOldExpense.get();
		Long id = expense.getId();
		expenseDao.delete(expense);
		assertThat(expenseDao.findById(id)).isEmpty();
	}

	@Test
	@DisplayName("Test expense finding by id")
	public void testExpenseFindingById() {
		ExpenseUser expenseUser = new ExpenseUser();
		expenseUser.setId(59L);
		Expense expense = new Expense(
			null,
			"Hotel",
			"Nota de hotel",
			DateUtil.formattingDate(LocalDateTime.of(2022, 4, 10, 12, 0, 0)),
			120.0,
			expenseUser
		);
		Expense savedExpense = expenseDao.save(expense);
		Expense foundExpense = expenseDao.findById(savedExpense.getId()).orElse(null);
		assertThat(foundExpense).isNotNull();
		assertThat(foundExpense.getConcept()).isEqualTo("Hotel");
	}

	@Test
	@DisplayName("Test expense-user relation")
	public void testExpenseUserRelation() {
		ExpenseUser expenseUser = new ExpenseUser();
		expenseUser.setId(60L);
		expenseUser.setName("Juan");
		expenseUser.setSurname("Pérez");
		expenseUser.setEmail("juanperez@gmail.com");
		expenseUser.setPassword("dummyPassword");
		Expense expense = new Expense(
			null,
			"Comida",
			"Nota de comida",
			DateUtil.formattingDate(LocalDateTime.of(2022, 5, 1, 14, 0, 0)),
			30.0,
			expenseUser
		);
		Expense savedExpense = expenseDao.save(expense);
		assertThat(savedExpense.getExpenseUser()).isNotNull();
		assertThat(savedExpense.getExpenseUser().getEmail()).isEqualTo("juanperez@gmail.com");
	}
}
