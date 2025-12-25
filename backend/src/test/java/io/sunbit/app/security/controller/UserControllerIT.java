package io.sunbit.app.security.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.sunbit.app.security.dto.UserUpdateRequest;
import io.sunbit.app.security.entity.ExpenseUser;
import io.sunbit.app.security.service.UserServiceImpl;

@SpringBootTest(properties = "app.jwt.secret=testsecretkeymustbelongenoughforhs512algorithmtoworkproperly1234567890")
@AutoConfigureMockMvc
@Transactional
public class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private io.sunbit.app.security.dao.IRoleDao roleDao;

    @BeforeEach
    void setUp() throws Exception {
        // Ensure ROLE_USER exists
        if (roleDao.findByName("ROLE_USER").isEmpty()) {
            io.sunbit.app.security.entity.Role role = new io.sunbit.app.security.entity.Role();
            role.setName("ROLE_USER");
            roleDao.save(role);
        }

        // Ensure we have a user for testing
        if (userService.findByEmail("it_test@example.com").isEmpty()) {
            ExpenseUser user = new ExpenseUser();
            user.setEmail("it_test@example.com");
            user.setPassword("password");
            user.setName("IT");
            user.setSurname("Test");
            io.sunbit.app.security.entity.Role role = roleDao.findByName("ROLE_USER").orElseThrow();
            java.util.List<Long> roleIds = java.util.Collections.singletonList(role.getId());
            userService.save(user, roleIds);
        }
    }

    @Test
    @WithMockUser(username = "it_test@example.com", roles = "USER")
    void getProfile_ShouldReturnRealProfile() throws Exception {
        mockMvc.perform(get("/api/v1/user/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("it_test@example.com"));
    }

    @Test
    @WithMockUser(username = "it_test@example.com", roles = "USER")
    void updateProfile_ShouldUpdateRealDatabase() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setEmail("updated_it@example.com");

        mockMvc.perform(put("/api/v1/user/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated_it@example.com"));

        // Verify in DB
        assert (userService.findByEmail("updated_it@example.com").isPresent());
    }
}
