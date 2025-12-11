package io.sunbit.app.test.user;

import static org.assertj.core.api.Assertions.assertThat;
// ...existing code...

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
		Role roleAdmin = new Role("ROLE_ADMIN");
		Role roleUser = new Role("ROLE_USER");
			   roleDao.saveAll(List.of(roleAdmin, roleUser)); // Unchecked warning is safe here
		long roleNumbers = roleDao.count();
		assertThat(roleNumbers).isGreaterThanOrEqualTo(2);
	}

	@Test
	public void testListRoles() {
		List<Role> roles = roleDao.findAll();
		assertThat(roles.size()).isGreaterThan(0);
	}

	@Test
	public void testFindRoleByName() {
		Role role = roleDao.findByName("ROLE_ADMIN").orElse(null);
		assertThat(role).isNotNull();
		assertThat(role.getName()).isEqualTo("ROLE_ADMIN");
	}

	@Test
	public void testDeleteRole() {
		Role role = new Role("ROLE_DELETE");
		roleDao.save(role);
		Long id = role.getId();
		roleDao.delete(role);
			   assertThat(roleDao.findById(java.util.Objects.requireNonNull(id))).isEmpty();
	}
}
