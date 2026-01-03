
package io.sunbit.app.test.user;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
// ...existing code...

import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import io.sunbit.app.security.dao.IRoleDao;
import io.sunbit.app.security.entity.Role;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RoleTest {

	@Autowired
	private IRoleDao roleDao;

	@Test
	@Order(1)
	public void createRoleTest() {
		if (!roleDao.existsByName("ROLE_ADMIN")) {
			roleDao.save(new Role("ROLE_ADMIN"));
		}
		if (!roleDao.existsByName("ROLE_USER")) {
			roleDao.save(new Role("ROLE_USER"));
		}
		long roleNumbers = roleDao.count();
		assertThat(roleNumbers).isGreaterThanOrEqualTo(2);
	}

	@Test
	@Order(2)
	public void testListRoles() {
		List<Role> roles = roleDao.findAll();
		assertThat(roles.size()).isGreaterThan(0);
	}

	@Test
	@Order(3)
	public void testFindRoleByName() {
		Role role = roleDao.findByName("ROLE_ADMIN").orElse(null);
		assertThat(role).isNotNull();
		assertThat(role.getName()).isEqualTo("ROLE_ADMIN");
	}

	@Test
	@Order(4)
	public void testDeleteRole() {
		Role role = new Role("ROLE_DELETE");
		roleDao.save(role);
		Long id = role.getId();
		roleDao.delete(role);
			   assertThat(roleDao.findById(java.util.Objects.requireNonNull(id))).isEmpty();
	}
}
