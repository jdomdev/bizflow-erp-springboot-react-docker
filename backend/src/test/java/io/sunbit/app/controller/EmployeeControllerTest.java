package io.sunbit.app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doAnswer;
import org.mockito.Mockito;

import io.sunbit.app.entity.Employee;
import io.sunbit.app.entity.Position;
import io.sunbit.app.service.EmployeeServiceImpl;
import io.sunbit.app.security.jwt.JwtAuthenticationFilter;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeServiceImpl employeeService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private Employee testEmployee1;
    private Employee testEmployee2;
    private Position testPosition;

    @BeforeEach
    void setUp() throws Exception {
        Mockito.reset(jwtAuthenticationFilter);
        doAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);
            FilterChain filterChain = invocation.getArgument(2);
            try {
                filterChain.doFilter(request, response);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class), any(FilterChain.class));

        // Setup test position
        testPosition = new Position();
        testPosition.setId(1L);
        testPosition.setName("Software Engineer");

        // Setup test employees
        testEmployee1 = new Employee();
        testEmployee1.setId(1L);
        testEmployee1.setName("John");
        testEmployee1.setSurname("Doe");
        testEmployee1.setEmail("john.doe@test.com");
        testEmployee1.setBirthDate(LocalDateTime.of(1990, 1, 1, 0, 0));
        testEmployee1.setPosition(testPosition);

        testEmployee2 = new Employee();
        testEmployee2.setId(2L);
        testEmployee2.setName("Jane");
        testEmployee2.setSurname("Smith");
        testEmployee2.setEmail("jane.smith@test.com");
        testEmployee2.setBirthDate(LocalDateTime.of(1992, 5, 15, 0, 0));
        testEmployee2.setPosition(testPosition);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllEmployees_Success() throws Exception {
        // Arrange
        List<Employee> employees = Arrays.asList(testEmployee1, testEmployee2);
        when(employeeService.findAll()).thenReturn(employees);

        // Act & Assert
        mockMvc.perform(get("/api/v1/employee")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("John"))
                .andExpect(jsonPath("$[0].surname").value("Doe"))
                .andExpect(jsonPath("$[1].name").value("Jane"))
                .andExpect(jsonPath("$[1].surname").value("Smith"));
    }

    @Test
    void testGetAllEmployees_Unauthorized() throws Exception {
        // Act & Assert - without authentication
        mockMvc.perform(get("/api/v1/employee")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetAllEmployees_Forbidden() throws Exception {
        // Act & Assert - with USER role (needs ADMIN)
        mockMvc.perform(get("/api/v1/employee")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetEmployeeById_Success() throws Exception {
        // Arrange
        when(employeeService.findById(anyLong(), anyString())).thenReturn(testEmployee1);

        // Act & Assert
        mockMvc.perform(get("/api/v1/employee/1")
                .header("Authorization", "Bearer fake-token")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john.doe@test.com"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetEmployeeById_AsUser_Success() throws Exception {
        // Arrange - USER role can also access this endpoint
        when(employeeService.findById(anyLong(), anyString())).thenReturn(testEmployee1);

        // Act & Assert
        mockMvc.perform(get("/api/v1/employee/1")
                .header("Authorization", "Bearer fake-token")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateEmployee_Success() throws Exception {
        // Arrange
        String employeeJson = """
            {
                "name": "New",
                "surname": "Employee",
                "email": "new.employee@test.com",
                "birthDate": "1995-03-20T00:00:00",
                "position": {
                    "id": 1,
                    "name": "Software Engineer"
                }
            }
            """;
        
        Employee savedEmployee = new Employee();
        savedEmployee.setId(3L);
        savedEmployee.setName("New");
        savedEmployee.setSurname("Employee");
        savedEmployee.setEmail("new.employee@test.com");
        
        when(employeeService.save(any(Employee.class))).thenReturn(savedEmployee);

        // Act & Assert
        mockMvc.perform(post("/api/v1/employee/")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(employeeJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New"))
                .andExpect(jsonPath("$.surname").value("Employee"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testCreateEmployee_Forbidden() throws Exception {
        // Arrange
        String employeeJson = """
            {
                "name": "New",
                "surname": "Employee",
                "email": "new.employee@test.com",
                "birthDate": "1995-03-20T00:00:00",
                "position": {"id": 1}
            }
            """;

        // Act & Assert - USER role cannot create employees
        mockMvc.perform(post("/api/v1/employee/")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(employeeJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateEmployee_Success() throws Exception {
        // Arrange
        String updatedEmployeeJson = """
            {
                "id": 1,
                "name": "John Updated",
                "surname": "Doe Jr",
                "email": "john.updated@test.com",
                "birthDate": "1990-01-01T00:00:00",
                "position": {
                    "id": 1,
                    "name": "Software Engineer"
                }
            }
            """;
        
        Employee updatedEmployee = new Employee();
        updatedEmployee.setId(1L);
        updatedEmployee.setName("John Updated");
        updatedEmployee.setSurname("Doe Jr");
        updatedEmployee.setEmail("john.updated@test.com");
        
        when(employeeService.update(anyLong(), any(Employee.class))).thenReturn(updatedEmployee);

        // Act & Assert
        mockMvc.perform(put("/api/v1/employee/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(updatedEmployeeJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Updated"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteEmployee_Success() throws Exception {
        // Arrange
        when(employeeService.delete(anyLong())).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/employee/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testDeleteEmployee_Forbidden() throws Exception {
        // Act & Assert - USER role cannot delete employees
        mockMvc.perform(delete("/api/v1/employee/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetEmployeeByNameAndSurname_Success() throws Exception {
        // Arrange
        when(employeeService.findByNameAndSurnameAllIgnoreCase(anyString(), anyString(), anyString()))
                .thenReturn(testEmployee1);

        // Act & Assert
        mockMvc.perform(get("/api/v1/employee/John/Doe")
                .header("Authorization", "Bearer fake-token")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.surname").value("Doe"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetEmployeeByNameAndSurname_AsUser_Success() throws Exception {
        // Arrange - USER role can also search employees
        when(employeeService.findByNameAndSurnameAllIgnoreCase(anyString(), anyString(), anyString()))
                .thenReturn(testEmployee2);

        // Act & Assert
        mockMvc.perform(get("/api/v1/employee/Jane/Smith")
                .header("Authorization", "Bearer fake-token")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane"));
    }
}
