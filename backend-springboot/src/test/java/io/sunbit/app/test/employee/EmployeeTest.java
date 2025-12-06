package io.sunbit.app.test.employee;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import io.sunbit.app.dao.IEmployeeDao;
import io.sunbit.app.dao.IPositionDao;
import io.sunbit.app.entity.Employee;

@DataJpaTest
@TestPropertySource(locations = "classpath:application.properties")
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
		// Create employee with all required fields
		Employee newEmployee = new Employee();
		newEmployee.setName("Diego");
		newEmployee.setSurname("Maradona");
		newEmployee.setBirthDate(LocalDateTime.of(1960, 10, 30, 23, 34, 42));
		newEmployee.setEmail("diegomaradona@mail.com");
		newEmployee.setPosition(positionDao.findByNameIgnoreCase("Project Manager")
				.orElseThrow(() -> new RuntimeException("Position 'Project Manager' not found")));
		
		Employee savedEmployee = employeeDao.save(newEmployee);

		assertThat(savedEmployee).isNotNull();
		assertThat(savedEmployee.getId()).isGreaterThan(0);
	}
}
