package io.sunbit.app.security.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.sunbit.app.security.entity.Role;

@Repository
public interface IRoleDao extends JpaRepository<Role, Long> {

	public Optional<Role> findByName(String roleName);

	public boolean existsByName(String string);
}
