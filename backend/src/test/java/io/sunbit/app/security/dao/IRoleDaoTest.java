package io.sunbit.app.security.dao;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import io.sunbit.app.security.dao.IRoleDao;
import io.sunbit.app.security.entity.Role;

@DataJpaTest
public class IRoleDaoTest {
    @Autowired
    IRoleDao roleDao;
    @Autowired
    TestEntityManager testEntityManager;

    @Test
    void testFindByName() {
        boolean roleAdminExists = roleDao.existsByName("ROLE_ADMIN");
        boolean roleUserExists = roleDao.existsByName("ROLE_USER");

        assertTrue(roleAdminExists, "ROLE_ADMIN should exist");
        assertTrue(roleUserExists, "ROLE_USER should exist");
    }
}
