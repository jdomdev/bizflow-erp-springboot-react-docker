package io.sunbit.app.test.employee;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import io.sunbit.app.dao.IEmployeeDao;
import io.sunbit.app.dao.IPositionDao;
import io.sunbit.app.entity.Employee;
import io.sunbit.app.entity.Expense;
import io.sunbit.app.entity.Payroll;
import io.sunbit.app.util.DateUtil;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class EmployeeTest {

	@Autowired
	IEmployeeDao employeeDao;
	@Autowired
	IPositionDao positionDao;

	@Test
	@DisplayName(value = "Test 1 -> test employee saving\n"
			+ "1.1 - savedEmployee.isNotNull()\n"
			+ "1.2 - savedEmployee.getId().isGreaterThan(0)\n")
	public void testEmployeeSaving() {
		List<Expense> expenses = new ArrayList<>();
		List<Payroll> payrolls = new ArrayList<>();
		// 1960-10-30 23:34:42', 25
		Employee newEmployee = new Employee(
				"Diego",
				"Maradona",
				DateUtil.formattingDate(LocalDateTime.of(1960, 10, 30, 23, 34, 42)),
				positionDao.findByNameIgnoreCase("Project Manager").get(),
				"diegomaradona@mail.com",
				expenses,
				payrolls);
		Employee savedEmployee = employeeDao.save(newEmployee);

		assertThat(savedEmployee).isNotNull();
		assertThat(savedEmployee.getId()).isGreaterThan(0);
	}

	/*
	 * @Test
	 * public void testEmployeeUpdating() {
	 * Optional<Employee> oldEmployee =
	 * employeeDao.findByNameAndSurnameAllIgnoreCase("Diego", "Maradona");
	 * 
	 * // Test.
	 * System.out.println("Old Employee --> " + oldEmployee.toString());
	 * 
	 * Employee updatedEmployee = null;
	 * if (oldEmployee != null) {
	 * Employee employeeToUp = new Employee(
	 * // 56L,
	 * "Diego",
	 * "Maradona",
	 * DateUtil.formattingDate(LocalDateTime.of(1978, 07, 22, 22, 35, 18)),
	 * positionDao.findByNameIgnoreCase("devops").get(),
	 * "diegomaradona@mail.com",
	 * new ArrayList<Expense>(),
	 * new ArrayList<Payroll>());
	 * updatedEmployee = employeeDao.save(employeeToUp);
	 * }
	 * 
	 * assertThat(updatedEmployee).isNotNull();
	 * assertThat(updatedEmployee.getId()).isGreaterThan(0);
	 * }
	 */
}
