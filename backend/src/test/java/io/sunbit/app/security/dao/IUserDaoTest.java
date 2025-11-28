package io.sunbit.app.security.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;

import io.sunbit.app.security.dao.IRoleDao;
import io.sunbit.app.security.dao.IUserDao;
import io.sunbit.app.security.entity.ExpenseUser;
import io.sunbit.app.security.entity.Role;

@DataJpaTest
// @Rollback(false)
// @AutoConfigureTestDatabase(replace = Replace.NONE)
public class IUserDaoTest {
    @Autowired
    IRoleDao roleDao;
    @Autowired
    IUserDao userDao;
    @Autowired
    TestEntityManager testEntityManager;

    // Code that runs BEFORE Unit Tests
    @BeforeEach
    void setUp() {

    }

    @Test
    public void insertExpenseUser() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        /* 83 Rihanna Fenty 1988-02-20 18:22:17.000 17 */
        final String ROLE_ADMIN = "ROLE_ADMIN";
        String rawPassword = "fenty1234";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        Optional<Role> adminRoleOpt = roleDao.findByName(ROLE_ADMIN);
        // Role roleAdmin = roleDao.getReferenceById(1L);
        Role adminRole = new Role();
        ExpenseUser newUser = new ExpenseUser();
        ExpenseUser savedUser = new ExpenseUser();
        if (adminRoleOpt.isPresent()) {
            adminRole = adminRoleOpt.get();
            newUser.setEmail("rihannafenty@mail.com");
            newUser.setName("Rihanna");
            newUser.setPassword(encodedPassword);
            newUser.setSurname("Fenty");
            newUser.addRole(adminRole);
            savedUser = userDao.save(newUser);

            /*
             * Role adminRole = new Role(ROLE_ADMIN);
             * ArrayList<Role> roles = new ArrayList<Role>();
             * roles.add(adminRole);
             * ExpenseUser eUser = ExpenseUser.builder()
             * .email("rihannafenty@mail.com")
             * .password(encodedPassword)
             * .name("Rihanna")
             * .surname("Fenty")
             * .roles(roles)
             * .build();
             * testEntityManager.persist(eUser);
             */
            assertEquals(newUser, savedUser);
        }
    }
}
