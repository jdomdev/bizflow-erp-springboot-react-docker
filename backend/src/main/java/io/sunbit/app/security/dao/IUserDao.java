
package io.sunbit.app.security.dao;

import java.util.Optional;

import io.sunbit.app.security.entity.ExpenseUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserDao extends JpaRepository<ExpenseUser, Long> {
    Optional<ExpenseUser> findByEmail(String email);

    Optional<ExpenseUser> findByEmployee_Id(Long employeeId);
}
