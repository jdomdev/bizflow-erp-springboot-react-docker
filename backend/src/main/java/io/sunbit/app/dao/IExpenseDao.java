package io.sunbit.app.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.sunbit.app.entity.Expense;
import io.sunbit.app.security.entity.ExpenseUser;

@Repository
public interface IExpenseDao extends JpaRepository<Expense, Long> {

	    @Query(value = "SELECT * FROM expense WHERE expense.expense_user_id=?1", nativeQuery = true)
	    List<Expense> findAllByExpenseUserId(Long expenseUserId);

		Optional<Expense> findByAmountAndExpenseDateAndConceptAndExpenseUser(Double amount, LocalDateTime expenseDate, String concept, ExpenseUser expenseUser);

		// Búsqueda directa por id (recomendado para búsquedas únicas)
		Optional<Expense> findById(Long id);
		
	    @Query(value = "SELECT * FROM expense WHERE expense.amount=?1 "
		    + "and expense.expense_date=?2 "
		    + "and expense.concept=?3 "
		    + "and expense.expense_user_id=?4", nativeQuery = true)
	    Optional<Expense> findByAmountAndExpenseDateAndConceptAndExpenseUserId(Double amount,
		    LocalDateTime expenseDate,
		    String concept,
		    Long expenseUserId);
}
