package io.sunbit.app.security.dao;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import io.sunbit.app.security.dao.IRoleDao;

@DataJpaTest
@TestPropertySource(locations = "classpath:application.properties")
public class IRoleDaoTest {
    @Autowired
    IRoleDao roleDao;

    @Test
    void testFindByName() {
        // Roles are initialized from data.sql
        boolean roleAdminExists = roleDao.existsByName("ROLE_ADMIN");
        boolean roleUserExists = roleDao.existsByName("ROLE_USER");

        assertTrue(roleAdminExists, "ROLE_ADMIN should exist");
        assertTrue(roleUserExists, "ROLE_USER should exist");
    }
}
