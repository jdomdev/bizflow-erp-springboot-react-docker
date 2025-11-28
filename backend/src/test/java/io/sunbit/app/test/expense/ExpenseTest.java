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

		Expense newExpense = new Expense(

		);
		Expense savedExpense = expenseDao.save(newExpense);

		assertThat(savedExpense).isNotNull();
		assertThat(savedExpense.getId()).isGreaterThan(0);
	}

	@Test
	public void testExpenseUpdating() {
		Optional<Expense> optOldExpense = expenseDao.findByAmountAndDateAndConceptAndEmployeeId(
				46.1,
				DateUtil.formattingDate(LocalDateTime.of(2022, 03, 12, 10, 24, 00)),
				"Taxi",
				58L);

		// Test.
		System.out.println("TEST: Old Expense --> " + optOldExpense.get().toString());

		Expense updatedExpense = null;
		if (optOldExpense != null) {
			// Expense expense = new
			// Integer id,String concept,LocalDateTime date,Double amount,Employee employee

			/*
			 * public Employee(String name, String surname, LocalDateTime birthDate,
			 * Position position, String email,
			 * List<Expense> expenses, List<Payroll> payrolls)
			 */
			Expense expenseToUp = new Expense(
					13L,
					"Taxiiiiiiiii",
					DateUtil.formattingDate(LocalDateTime.of(2022, 03, 12, 10, 24, 00)),
					46.1,
					new Employee(
							58L,
							"Sylvester",
							"Stewart",
							DateUtil.formattingDate(LocalDateTime.of(1984, 06, 15, 11, 24, 00)),
							positionDao.findByNameIgnoreCase("scrum master").get(),
							"slystone@gmail.com",
							new ArrayList<Expense>(),
							new ArrayList<Payroll>()));
			updatedExpense = expenseDao.save(expenseToUp);
		}
		assertThat(updatedExpense).isNotNull();
		assertThat(updatedExpense.getId()).isGreaterThan(0);
	}
}
