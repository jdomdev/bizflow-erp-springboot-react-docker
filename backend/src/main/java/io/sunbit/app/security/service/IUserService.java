package io.sunbit.app.security.service;

import java.util.List;
import java.util.Optional;

import io.sunbit.app.security.entity.ExpenseUser;

public interface IUserService {

	public List<ExpenseUser> findAll() throws Exception;

	public Optional<ExpenseUser> findById(Long id) throws Exception;

	public Boolean delete(Long id) throws Exception;

	public ExpenseUser save(ExpenseUser user) throws Exception;

	public ExpenseUser update(Long userId, ExpenseUser user) throws Exception;

	public Optional<ExpenseUser> findByEmail(String userEmail) throws Exception;
}
