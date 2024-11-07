package io.sunbit.app.test.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

//import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.transaction.annotation.Transactional;

import io.sunbit.app.security.dao.IRoleDao;
import io.sunbit.app.security.dao.IUserDao;
import io.sunbit.app.security.entity.ExpenseUser;
import io.sunbit.app.security.entity.Role;

//@SpringBootTest
// @TestPropertySource(locations = "classpath:application-sample.properties")
@DataJpaTest
/*
 * Spring Boot no intentará reemplazar tu base de datos real
 * con H2 para ejecutar las pruebas:
 */
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserTest {

	@Autowired
	private IUserDao userDao;
	@Autowired
	private IRoleDao roleDao;
	@Autowired
	private TestEntityManager entityManager;

	@Test
	public void createRoleTesting() {
		Role roleAdmin = new Role("ROLE_ADMIN");
		Role roleUser = new Role("ROLE_USER");
		entityManager.persist(roleAdmin);
		entityManager.persist(roleUser);
	}

	@Test
	@Transactional
	public void testRolesExist() {
		boolean roleAdminExists = roleDao.existsByName("ROLE_ADMIN");
		boolean roleUserExists = roleDao.existsByName("ROLE_USER");

		assertTrue(roleAdminExists, "ROLE_ADMIN should exist");
		assertTrue(roleUserExists, "ROLE_USER should exist");
	}

	@Test
	// @Transactional
	public void testUserSaving() {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		// 83 Rihanna Fenty 1988-02-20 18:22:17.000 17
		// akirakurosawa@sunbit.com - Akira Kurosawa - kurosawa1234
		final String ROLE_ADMIN = "ROLE_ADMIN";
		String rawPassword = "kurosawa1234";
		String encodedPassword = passwordEncoder.encode(rawPassword);
		ensureRoleExist();
		// Obtén el rol, lanza una excepción si no se encuentra
		Optional<Role> adminRoleOpt = roleDao.findByName(ROLE_ADMIN);
		// Role roleAdmin = roleDao.getReferenceById(1L);
		Role adminRole = new Role();
		ExpenseUser newUser = new ExpenseUser();
		ExpenseUser savedUser = new ExpenseUser();
		if (adminRoleOpt.isPresent()) {
			adminRole = adminRoleOpt.get();
			newUser.setEmail("akirakurosawa@sunbit.com");
			newUser.setName("Akira");
			newUser.setPassword(encodedPassword);
			newUser.setSurname("Kurosawa");
			newUser.addRole(adminRole);
			savedUser = userDao.save(newUser);
			// entityManager.persist(newUser);
			assertThat(savedUser)
					.withFailMessage("El usuario guardado no debe ser nulo")
					.isNotNull();
			/*
			 * assertThat(savedUser.getId())
			 * .withFailMessage("El ID del usuario guardado debe ser mayor a 0")
			 * .isGreaterThan(0);
			 */
			assertThat(adminRole)
					.withFailMessage("El role de administrador no debe ser nulo")
					.isNotNull();
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("Admin Role Optional EMPTY: ").append(adminRoleOpt);
		}
	}

	public void ensureRoleExist() {
		if (!roleDao.findByName("ROLE_ADMIN").isPresent()) {
			Role roleAdmin = new Role("ROLE_ADMIN");
			entityManager.persist(roleAdmin);
		}
		if (!roleDao.findByName("ROLE_USER").isPresent()) {
			Role roleUser = new Role("ROLE_USER");
			entityManager.persist(roleUser);
		}
	}

	/*
	 * @Test
	 * public void testAssignRoleToUser() {
	 * Optional<ExpenseUser> optUser = userDao.findByEmail("rihannafenty@mail.com");
	 * Optional<Role> optRole = roleDao.findByName("ROLE_ADMIN");
	 * ExpenseUser user = optUser.get();
	 * user.addRole(optRole.get());
	 * ExpenseUser updatedUser = userDao.save(user);
	 * 
	 * assertThat(updatedUser.getRoles().size() == 2);
	 * }
	 */

}
