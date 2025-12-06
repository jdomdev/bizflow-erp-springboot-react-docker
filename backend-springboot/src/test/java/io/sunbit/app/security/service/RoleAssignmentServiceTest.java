package io.sunbit.app.security.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.sunbit.app.exception.RoleNotFoundException;
import io.sunbit.app.security.dao.IRoleDao;
import io.sunbit.app.security.dao.IUserDao;
import io.sunbit.app.security.entity.ExpenseUser;
import io.sunbit.app.security.entity.Role;

@ExtendWith(MockitoExtension.class)
public class RoleAssignmentServiceTest {

    @Mock
    private IUserDao userDao;

    @Mock
    private IRoleDao roleDao;

    @InjectMocks
    private UserServiceImpl userService;

    private ExpenseUser testUser;
    private Role adminRole;
    private Role userRole;

    @BeforeEach
    public void setUp() {
        adminRole = new Role(1L, "ROLE_ADMIN");
        userRole = new Role(2L, "ROLE_USER");

        Collection<Role> roles = new ArrayList<>();
        roles.add(userRole);

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
    public void testAssignRoleToUser_WhenUserExists_ShouldAddRole() throws Exception {
        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));
        when(roleDao.findById(1L)).thenReturn(Optional.of(adminRole));
        when(userDao.save(any(ExpenseUser.class))).thenReturn(testUser);

        ExpenseUser result = userService.assignRoleToUser(1L, 1L);

        assertThat(result).isNotNull();
        verify(userDao).save(any(ExpenseUser.class));
    }

    @Test
    public void testAssignRoleToUser_WhenUserNotFound_ShouldThrowException() {
        when(userDao.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            userService.assignRoleToUser(1L, 1L);
        });

        assertThat(exception.getMessage()).contains("User not found");
    }

    @Test
    public void testAssignRoleToUser_WhenRoleNotFound_ShouldThrowException() {
        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));
        when(roleDao.findById(999L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RoleNotFoundException.class, () -> {
            userService.assignRoleToUser(1L, 999L);
        });

        assertThat(exception.getMessage()).contains("Role not found");
    }

    @Test
    public void testAssignRoleToUser_WhenUserAlreadyHasRole_ShouldNotDuplicate() throws Exception {
        // User already has userRole
        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));
        when(roleDao.findById(2L)).thenReturn(Optional.of(userRole));

        ExpenseUser result = userService.assignRoleToUser(1L, 2L);

        assertThat(result).isNotNull();
        // Should not save if role already exists
        verify(userDao, never()).save(any(ExpenseUser.class));
    }

    @Test
    public void testRemoveRoleFromUser_WhenUserExists_ShouldRemoveRole() throws Exception {
        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));
        when(roleDao.findById(2L)).thenReturn(Optional.of(userRole));
        when(userDao.save(any(ExpenseUser.class))).thenReturn(testUser);

        ExpenseUser result = userService.removeRoleFromUser(1L, 2L);

        assertThat(result).isNotNull();
        verify(userDao).save(any(ExpenseUser.class));
    }

    @Test
    public void testRemoveRoleFromUser_WhenUserNotFound_ShouldThrowException() {
        when(userDao.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            userService.removeRoleFromUser(1L, 2L);
        });

        assertThat(exception.getMessage()).contains("User not found");
    }

    @Test
    public void testRemoveRoleFromUser_WhenRoleNotFound_ShouldThrowException() {
        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));
        when(roleDao.findById(999L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RoleNotFoundException.class, () -> {
            userService.removeRoleFromUser(1L, 999L);
        });

        assertThat(exception.getMessage()).contains("Role not found");
    }

    @Test
    public void testGetUserRoles_WhenUserExists_ShouldReturnRoles() throws Exception {
        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));

        Collection<Role> roles = userService.getUserRoles(1L);

        assertThat(roles).isNotNull();
        assertThat(roles).hasSize(1);
        assertThat(roles).contains(userRole);
    }

    @Test
    public void testGetUserRoles_WhenUserNotFound_ShouldThrowException() {
        when(userDao.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            userService.getUserRoles(1L);
        });

        assertThat(exception.getMessage()).contains("User not found");
    }
}
