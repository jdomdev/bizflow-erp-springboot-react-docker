
package io.sunbit.app.test.employee;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import io.sunbit.app.dao.IEmployeeDao;
import io.sunbit.app.dao.IPositionDao;
import io.sunbit.app.entity.Employee;
import io.sunbit.app.entity.Payroll;
import io.sunbit.app.util.DateUtil;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EmployeeTest {

	@Autowired
	IEmployeeDao employeeDao;
	@Autowired
	IPositionDao positionDao;

	@Test
	@DisplayName("Test employee saving")
	@Order(1)
	public void testEmployeeSaving() {
		List<Payroll> payrolls = new ArrayList<>();
		Employee newEmployee = new Employee(
			"Diego",
			"Maradona",
			DateUtil.formattingDate(LocalDateTime.of(1960, 10, 30, 23, 34, 42)),
			positionDao.findByNameIgnoreCase("Project Manager").get(),
			"diegomaradona@mail.com",
			payrolls);
		Employee savedEmployee = employeeDao.save(newEmployee);
		assertThat(savedEmployee).isNotNull();
		assertThat(savedEmployee.getId()).isGreaterThan(0);
	}

	@Test
	@DisplayName("Test employee updating")
	@Order(2)
	public void testEmployeeUpdating() {
		Employee employee = employeeDao.findByNameAndSurnameAllIgnoreCase("Diego", "Maradona").orElse(null);
		assertThat(employee).isNotNull();
		employee.setEmail("updatedmaradona@mail.com");
		Employee updatedEmployee = employeeDao.save(employee);
		assertThat(updatedEmployee.getEmail()).isEqualTo("updatedmaradona@mail.com");
	}

	@Test
	@DisplayName("Test employee deleting")
	@Order(3)
	public void testEmployeeDeleting() {
		Employee employee = employeeDao.findByNameAndSurnameAllIgnoreCase("Diego", "Maradona").orElse(null);
		assertThat(employee).isNotNull();
		Long id = employee.getId();
		employeeDao.delete(employee);
		assertThat(employeeDao.findById(id)).isEmpty();
	}

	@Test
	@DisplayName("Test employee finding by id")
	@Order(4)
	public void testEmployeeFindingById() {
		Employee employee = new Employee(
			"Lionel",
			"Messi",
			DateUtil.formattingDate(LocalDateTime.of(1987, 6, 24, 0, 0, 0)),
			positionDao.findByNameIgnoreCase("Developer").get(),
			"lionelmessi@mail.com",
			new ArrayList<>());
		Employee savedEmployee = employeeDao.save(employee);
		Employee foundEmployee = employeeDao.findById(savedEmployee.getId()).orElse(null);
		assertThat(foundEmployee).isNotNull();
		assertThat(foundEmployee.getName()).isEqualTo("Lionel");
	}

	@Test
	@DisplayName("Test employee-payroll relation")
	@Order(5)
	public void testEmployeePayrollRelation() {
		Employee employee = new Employee(
			"Carlos",
			"Tevez",
			DateUtil.formattingDate(LocalDateTime.of(1984, 2, 5, 0, 0, 0)),
			positionDao.findByNameIgnoreCase("Tester").get(),
			"carlostevez@mail.com",
			new ArrayList<>());
		Employee savedEmployee = employeeDao.save(employee);
		Payroll payroll = new Payroll();
		payroll.setAmount(1000.0);
		payroll.setPayrollDate(DateUtil.formattingDate(LocalDateTime.now()));
		payroll.setEmployee(savedEmployee);
		savedEmployee.getPayrolls().add(payroll);
		employeeDao.save(savedEmployee);
		assertThat(savedEmployee.getPayrolls()).isNotEmpty();
		assertThat(savedEmployee.getPayrolls().get(0).getAmount()).isEqualTo(1000.0);
	}
}
