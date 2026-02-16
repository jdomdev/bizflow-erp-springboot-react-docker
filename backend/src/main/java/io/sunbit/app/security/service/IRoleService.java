package io.sunbit.app.security.service;

import java.util.List;
import java.util.Optional;

import io.sunbit.app.security.entity.Role;

public interface IRoleService {

	public List<Role> findAll() throws Exception;

	public Optional<Role> findById(Long id) throws Exception;

	public Role save(Role role) throws Exception;

	public Role update(Long id, Role role) throws Exception;

	public Boolean delete(Long id) throws Exception;
}
