package io.sunbit.app.security.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.sunbit.app.security.entity.ExpenseUser;

@Repository
public interface IUserDao extends JpaRepository<ExpenseUser, Long> {

	public Optional<ExpenseUser> findByEmail(String userEmail);

}
