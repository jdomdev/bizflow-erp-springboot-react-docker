package io.sunbit.app.test.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.sunbit.app.dao.IEmployeeDao;
import io.sunbit.app.entity.Employee;
import io.sunbit.app.entity.Position;
import io.sunbit.app.service.EmployeeServiceImpl;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private IEmployeeDao employeeDao;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private Position position;

    @BeforeEach
    public void setup() {
        position = new Position();
        position.setId(1L);
        position.setName("Developer");

        employee = new Employee();
        employee.setId(1L);
        employee.setName("John");
        employee.setSurname("Smith");
        employee.setEmail("john.smith@example.com");
        employee.setBirthDate(LocalDateTime.of(1990, 1, 1, 0, 0));
        employee.setPosition(position);
    }

    @Test
    public void testFindAll() throws Exception {
        // Given
        List<Employee> employees = Arrays.asList(employee);
        when(employeeDao.findAll()).thenReturn(employees);

        // When
        List<Employee> result = employeeService.findAll();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("John");
        verify(employeeDao, times(1)).findAll();
    }

    @Test
    public void testSave() throws Exception {
        // Given
        when(employeeDao.save(any(Employee.class))).thenReturn(employee);

        // When
        Employee savedEmployee = employeeService.save(employee);

        // Then
        assertThat(savedEmployee).isNotNull();
        assertThat(savedEmployee.getId()).isEqualTo(1L);
        assertThat(savedEmployee.getName()).isEqualTo("John");
        verify(employeeDao, times(1)).save(any(Employee.class));
    }

    @Test
    public void testFindByEmail() throws Exception {
        // Given
        when(employeeDao.findByEmail("john.smith@example.com")).thenReturn(Optional.of(employee));

        // When
        Employee foundEmployee = employeeService.findByEmail("john.smith@example.com");

        // Then
        assertThat(foundEmployee).isNotNull();
        assertThat(foundEmployee.getEmail()).isEqualTo("john.smith@example.com");
        verify(employeeDao, times(1)).findByEmail("john.smith@example.com");
    }

    @Test
    public void testDelete() throws Exception {
        // Given
        Long employeeId = 1L;
        when(employeeDao.existsById(employeeId)).thenReturn(true);

        // When
        Boolean result = employeeService.delete(employeeId);

        // Then
        assertThat(result).isTrue();
        verify(employeeDao, times(1)).deleteById(employeeId);
    }
}
