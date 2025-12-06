package io.sunbit.app.test.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import io.sunbit.app.security.dao.IRoleDao;
import io.sunbit.app.security.entity.Role;

@DataJpaTest
@TestPropertySource(locations = "classpath:application.properties")
public class RoleTest {

	@Autowired
	private IRoleDao roleDao;

	@Test
	public void testRolesAreInitialized() {
		// Roles are initialized from data.sql
		List<Role> roles = roleDao.findAll();
		
		assertThat(roles).isNotEmpty();
		assertThat(roles.size()).isGreaterThanOrEqualTo(2);
		
		// Verify specific roles exist
		assertThat(roleDao.existsByName("ROLE_ADMIN")).isTrue();
		assertThat(roleDao.existsByName("ROLE_USER")).isTrue();
	}
}
