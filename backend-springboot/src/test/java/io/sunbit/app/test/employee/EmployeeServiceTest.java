package io.sunbit.app.test.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import io.sunbit.app.dao.IPositionDao;
import io.sunbit.app.entity.Employee;
import io.sunbit.app.entity.Employee.EmployeeStatus;
import io.sunbit.app.entity.Position;
import io.sunbit.app.service.EmployeeServiceImpl;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

	@Mock
	private IEmployeeDao employeeDao;

	@Mock
	private IPositionDao positionDao;

	@InjectMocks
	private EmployeeServiceImpl employeeService;

	private Employee testEmployee;
	private Position testPosition;

	@BeforeEach
	public void setup() {
		testPosition = new Position(1L, "Software Developer");
		testEmployee = new Employee(
				1L,
				"John",
				"Doe",
				LocalDateTime.of(1990, 1, 15, 0, 0),
				testPosition,
				"john.doe@example.com",
				LocalDate.of(2023, 1, 1),
				EmployeeStatus.ACTIVE,
				new ArrayList<>(),
				new ArrayList<>());
	}

	@Test
	@DisplayName("Test findAll - should return list of employees")
	public void testFindAll() throws Exception {
		// Given
		List<Employee> employees = Arrays.asList(testEmployee);
		when(employeeDao.findAll()).thenReturn(employees);

		// When
		List<Employee> result = employeeService.findAll();

		// Then
		assertThat(result).isNotNull();
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getName()).isEqualTo("John");
		verify(employeeDao, times(1)).findAll();
	}

	@Test
	@DisplayName("Test findById - should return employee when found")
	public void testFindById() throws Exception {
		// Given
		when(employeeDao.findById(1L)).thenReturn(Optional.of(testEmployee));

		// When
		Employee result = employeeService.findById(1L, "Bearer token");

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(1L);
		assertThat(result.getName()).isEqualTo("John");
		assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
		verify(employeeDao, times(1)).findById(1L);
	}

	@Test
	@DisplayName("Test save - should save and return employee")
	public void testSave() throws Exception {
		// Given
		Employee newEmployee = new Employee(
				"Jane",
				"Smith",
				LocalDateTime.of(1992, 5, 20, 0, 0),
				testPosition,
				"jane.smith@example.com",
				LocalDate.of(2023, 2, 1),
				EmployeeStatus.ACTIVE,
				new ArrayList<>(),
				new ArrayList<>());
		when(employeeDao.save(any(Employee.class))).thenReturn(testEmployee);

		// When
		Employee result = employeeService.save(newEmployee);

		// Then
		assertThat(result).isNotNull();
		verify(employeeDao, times(1)).save(any(Employee.class));
	}

	@Test
	@DisplayName("Test update - should update existing employee")
	public void testUpdate() throws Exception {
		// Given
		Employee updatedEmployee = new Employee(
				1L,
				"John",
				"Doe Updated",
				LocalDateTime.of(1990, 1, 15, 0, 0),
				testPosition,
				"john.doe@example.com",
				LocalDate.of(2023, 1, 1),
				EmployeeStatus.ACTIVE,
				new ArrayList<>(),
				new ArrayList<>());
		when(employeeDao.findById(1L)).thenReturn(Optional.of(testEmployee));
		when(employeeDao.save(any(Employee.class))).thenReturn(updatedEmployee);

		// When
		Employee result = employeeService.update(1L, updatedEmployee);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getSurname()).isEqualTo("Doe Updated");
		verify(employeeDao, times(1)).findById(1L);
		verify(employeeDao, times(1)).save(any(Employee.class));
	}

	@Test
	@DisplayName("Test update - should throw exception when employee not found")
	public void testUpdateEmployeeNotFound() {
		// Given
		Employee updatedEmployee = new Employee(
				99L,
				"John",
				"Doe",
				LocalDateTime.of(1990, 1, 15, 0, 0),
				testPosition,
				"john.doe@example.com",
				LocalDate.of(2023, 1, 1),
				EmployeeStatus.ACTIVE,
				new ArrayList<>(),
				new ArrayList<>());
		when(employeeDao.findById(99L)).thenReturn(Optional.empty());

		// When & Then
		assertThrows(Exception.class, () -> {
			employeeService.update(99L, updatedEmployee);
		});
	}

	@Test
	@DisplayName("Test delete - should delete employee when exists")
	public void testDelete() throws Exception {
		// Given
		when(employeeDao.existsById(1L)).thenReturn(true);

		// When
		Boolean result = employeeService.delete(1L);

		// Then
		assertThat(result).isTrue();
		verify(employeeDao, times(1)).existsById(1L);
		verify(employeeDao, times(1)).deleteById(1L);
	}

	@Test
	@DisplayName("Test delete - should throw exception when employee not found")
	public void testDeleteEmployeeNotFound() {
		// Given
		when(employeeDao.existsById(99L)).thenReturn(false);

		// When & Then
		assertThrows(Exception.class, () -> {
			employeeService.delete(99L);
		});
	}

	@Test
	@DisplayName("Test findByEmail - should return employee when found")
	public void testFindByEmail() throws Exception {
		// Given
		when(employeeDao.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testEmployee));

		// When
		Employee result = employeeService.findByEmail("john.doe@example.com");

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
		verify(employeeDao, times(1)).findByEmail("john.doe@example.com");
	}

	@Test
	@DisplayName("Test findByNameAndSurname - should return employee when found")
	public void testFindByNameAndSurname() throws Exception {
		// Given
		when(employeeDao.findByNameAndSurnameAllIgnoreCase("John", "Doe"))
				.thenReturn(Optional.of(testEmployee));

		// When
		Employee result = employeeService.findByNameAndSurnameAllIgnoreCase("John", "Doe", "Bearer token");

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getName()).isEqualTo("John");
		assertThat(result.getSurname()).isEqualTo("Doe");
		verify(employeeDao, times(1)).findByNameAndSurnameAllIgnoreCase("John", "Doe");
	}

	@Test
	@DisplayName("Test findByNameAndSurname - should return null when not found")
	public void testFindByNameAndSurnameNotFound() throws Exception {
		// Given
		when(employeeDao.findByNameAndSurnameAllIgnoreCase("Jane", "Smith"))
				.thenReturn(Optional.empty());

		// When
		Employee result = employeeService.findByNameAndSurnameAllIgnoreCase("Jane", "Smith", "Bearer token");

		// Then
		assertThat(result).isNull();
		verify(employeeDao, times(1)).findByNameAndSurnameAllIgnoreCase("Jane", "Smith");
	}
}
