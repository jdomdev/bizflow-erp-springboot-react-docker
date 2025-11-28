package io.sunbit.app.test.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import io.sunbit.app.security.dao.IRoleDao;
import io.sunbit.app.security.entity.Role;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class RoleTest {

	@Autowired
	private IRoleDao roleDao;

	@Test
	public void createRoleTest() {
		// Role adminRole = new Role("USER_ROLE");
		// Role userRole = new Role("ADMIN_ROLE");
		/*
		 * En Spring Boot la denominación correcta para los Roles es "ROLE_..."
		 * porque si no, no funciona la anotación '@PreAuthorize("hasRole('ADMIN')")'
		 */
		Role roleAdmin = new Role("ROLE_ADMIN");
		Role roleUser = new Role("ROLE_USER");

		roleDao.saveAll(List.of(roleAdmin, roleUser));

		long roleNumbers = roleDao.count();
		assertEquals(2, roleNumbers);
	}

	/*
	 * @Test
	 * public void testListRoles() {
	 * List<Role> roles = roleDao.findAll();
	 * assertThat(roles.size()).isGreaterThan(0);
	 * roles.forEach(System.out::println);
	 * }
	 */
}
