package io.sunbit.app.security.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import io.sunbit.app.security.entity.ExpenseUser;
import io.sunbit.app.security.entity.Role;

public interface IUserService {

	public List<ExpenseUser> findAll() throws Exception;

	public Optional<ExpenseUser> findById(Long id) throws Exception;

	public Boolean delete(Long id) throws Exception;

	public ExpenseUser save(ExpenseUser user) throws Exception;

	public ExpenseUser update(Long userId, ExpenseUser user) throws Exception;

	public Optional<ExpenseUser> findByEmail(String userEmail) throws Exception;

	public ExpenseUser assignRoleToUser(Long userId, Long roleId) throws Exception;

	public ExpenseUser removeRoleFromUser(Long userId, Long roleId) throws Exception;

	public Collection<Role> getUserRoles(Long userId) throws Exception;
}
