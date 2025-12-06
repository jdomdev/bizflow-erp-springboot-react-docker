package io.sunbit.app.test.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import io.sunbit.app.security.dao.IRoleDao;
import io.sunbit.app.security.dao.IUserDao;
import io.sunbit.app.security.entity.ExpenseUser;
import io.sunbit.app.security.entity.Role;

@DataJpaTest
@TestPropertySource(locations = "classpath:application.properties")
public class UserTest {

	@Autowired
	private IUserDao userDao;
	
	@Autowired
	private IRoleDao roleDao;

	@Test
	public void testRolesExist() {
		// Roles are initialized from data.sql
		boolean roleAdminExists = roleDao.existsByName("ROLE_ADMIN");
		boolean roleUserExists = roleDao.existsByName("ROLE_USER");

		assertTrue(roleAdminExists, "ROLE_ADMIN should exist");
		assertTrue(roleUserExists, "ROLE_USER should exist");
	}

	@Test
	public void testUserSaving() {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String rawPassword = "kurosawa1234";
		String encodedPassword = passwordEncoder.encode(rawPassword);
		
		// Get the admin role from the database (initialized from data.sql)
		Role adminRole = roleDao.findByName("ROLE_ADMIN")
				.orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));
		
		// Create new user
		ExpenseUser newUser = new ExpenseUser();
		newUser.setEmail("akirakurosawa@sunbit.com");
		newUser.setName("Akira");
		newUser.setPassword(encodedPassword);
		newUser.setSurname("Kurosawa");
		newUser.addRole(adminRole);
		
		// Save user
		ExpenseUser savedUser = userDao.save(newUser);
		
		// Assertions
		assertThat(savedUser)
				.withFailMessage("El usuario guardado no debe ser nulo")
				.isNotNull();
		assertThat(savedUser.getId())
				.withFailMessage("El ID del usuario guardado debe ser mayor a 0")
				.isGreaterThan(0);
		assertThat(savedUser.getRoles())
				.withFailMessage("El usuario debe tener al menos un rol")
				.isNotEmpty();
		assertThat(adminRole)
				.withFailMessage("El role de administrador no debe ser nulo")
				.isNotNull();
	}
}
