package io.sunbit.app.security.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.sunbit.app.security.configuration.AppSecurityConfig;
import io.sunbit.app.security.entity.ExpenseUser;
import io.sunbit.app.security.entity.Role;
import io.sunbit.app.security.jwt.JwtAuthenticationFilter;
import io.sunbit.app.security.jwt.JwtAuthenticationUtil;
import io.sunbit.app.security.service.UserServiceImpl;

@WebMvcTest(UserControllerImpl.class)
@Import(AppSecurityConfig.class)
public class UserControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private JwtAuthenticationUtil jwtAuthUtil;

    @MockBean
    private JwtAuthenticationFilter jwtAuthFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private ExpenseUser testUser;
    private Role adminRole;
    private Role userRole;

    @BeforeEach
    public void setUp() {
        adminRole = new Role(1L, "ROLE_ADMIN");
        userRole = new Role(2L, "ROLE_USER");

        Collection<Role> roles = new ArrayList<>();
        roles.add(userRole);
        roles.add(adminRole);

        testUser = new ExpenseUser(
            1L,
            "Test",
            "User",
            "test@example.com",
            "password123",
            roles
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetAllUsers_WithAdminRole_ShouldReturnOk() throws Exception {
        List<ExpenseUser> users = List.of(testUser);
        when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/api/v1/user/"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetAllUsers_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/user/"))
            .andExpect(status().isForbidden());
    }

    @Test
    public void testGetAllUsers_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/user/"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetUserById_WithAdminRole_ShouldReturnOk() throws Exception {
        when(userService.findById(1L)).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/api/v1/user/1"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetUserById_WithUserRole_ShouldReturnOk() throws Exception {
        when(userService.findById(1L)).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/api/v1/user/1"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateUser_WithAdminRole_ShouldReturnOk() throws Exception {
        when(userService.save(any(ExpenseUser.class))).thenReturn(testUser);

        String userJson = objectMapper.writeValueAsString(testUser);

        mockMvc.perform(post("/api/v1/user/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testCreateUser_WithUserRole_ShouldReturnForbidden() throws Exception {
        String userJson = objectMapper.writeValueAsString(testUser);

        mockMvc.perform(post("/api/v1/user/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateUser_WithAdminRole_ShouldReturnOk() throws Exception {
        when(userService.update(eq(1L), any(ExpenseUser.class))).thenReturn(testUser);

        String userJson = objectMapper.writeValueAsString(testUser);

        mockMvc.perform(put("/api/v1/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testUpdateUser_WithUserRole_ShouldReturnForbidden() throws Exception {
        String userJson = objectMapper.writeValueAsString(testUser);

        mockMvc.perform(put("/api/v1/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteUser_WithAdminRole_ShouldReturnOk() throws Exception {
        when(userService.delete(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/user/1"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testDeleteUser_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/api/v1/user/1"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAssignRoleToUser_WithAdminRole_ShouldReturnOk() throws Exception {
        when(userService.assignRoleToUser(1L, 1L)).thenReturn(testUser);

        mockMvc.perform(post("/api/v1/user/1/roles/1"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testAssignRoleToUser_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/user/1/roles/1"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testRemoveRoleFromUser_WithAdminRole_ShouldReturnOk() throws Exception {
        when(userService.removeRoleFromUser(1L, 1L)).thenReturn(testUser);

        mockMvc.perform(delete("/api/v1/user/1/roles/1"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testRemoveRoleFromUser_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/api/v1/user/1/roles/1"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetUserRoles_WithUserRole_ShouldReturnOk() throws Exception {
        when(userService.getUserRoles(1L)).thenReturn(testUser.getRoles());

        mockMvc.perform(get("/api/v1/user/1/roles"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetUserRoles_WithAdminRole_ShouldReturnOk() throws Exception {
        when(userService.getUserRoles(1L)).thenReturn(testUser.getRoles());

        mockMvc.perform(get("/api/v1/user/1/roles"))
            .andExpect(status().isOk());
    }
}
