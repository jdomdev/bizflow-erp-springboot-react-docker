package io.sunbit.app.test.expense;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import io.sunbit.app.dao.IEmployeeDao;
import io.sunbit.app.dao.IExpenseDao;
import io.sunbit.app.dao.IPositionDao;
import io.sunbit.app.entity.Employee;
import io.sunbit.app.entity.Expense;

@DataJpaTest
@TestPropertySource(locations = "classpath:application.properties")
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
		// First create an employee
		Employee employee = new Employee();
		employee.setName("John");
		employee.setSurname("Doe Smith"); // Must be at least 5 characters
		employee.setBirthDate(LocalDateTime.of(1990, 1, 1, 0, 0));
		employee.setEmail("johndoe@mail.com");
		employee.setPosition(positionDao.findByNameIgnoreCase("Developer").orElse(null));
		Employee savedEmployee = employeeDao.save(employee);

		// Now create an expense for this employee
		Expense newExpense = new Expense();
		newExpense.setConcept("Taxi");
		newExpense.setDate(LocalDateTime.now());
		newExpense.setAmount(46.1);
		newExpense.setEmployee(savedEmployee);
		newExpense.setNote("Business trip expense");
		
		Expense savedExpense = expenseDao.save(newExpense);

		assertThat(savedExpense).isNotNull();
		assertThat(savedExpense.getId()).isGreaterThan(0);
		assertThat(savedExpense.getAmount()).isEqualTo(46.1);
	}
}
