package io.sunbit.app.security.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.sunbit.app.security.dto.UserUpdateRequest;
import io.sunbit.app.security.entity.ExpenseUser;
import io.sunbit.app.security.entity.Role;
import io.sunbit.app.security.service.UserServiceImpl;

@SpringBootTest(properties = "app.jwt.secret=testsecretkeymustbelongenoughforhs512algorithmtoworkproperly1234567890")
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @Autowired
    private ObjectMapper objectMapper;

    private ExpenseUser testUser;

    @BeforeEach
    void setUp() {
        testUser = new ExpenseUser();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setName("Test");
        testUser.setSurname("User");

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        testUser.setRoles(roles);
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void getProfile_ShouldReturnUserProfile() throws Exception {
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/api/v1/user/profile"))
                .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.name").value("Test"))
            .andExpect(jsonPath("$.roleIds[0]").value(1));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void updateProfile_ShouldUpdateAndReturnProfile() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setEmail("newemail@example.com");

        ExpenseUser updatedUser = new ExpenseUser();
        updatedUser.setId(1L);
        updatedUser.setEmail("newemail@example.com");
        updatedUser.setName("Test");
        updatedUser.setSurname("User");
        updatedUser.setRoles(testUser.getRoles());

        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(userService.update(eq(1L), any(ExpenseUser.class), any(List.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/v1/user/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newemail@example.com"));
    }
}
