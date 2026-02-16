package io.sunbit.app.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.sunbit.app.dto.ExpenseSearchCriteria;
import io.sunbit.app.entity.Expense;

public interface IExpenseService {

	public List<Expense> findAll() throws Exception;

	public Expense findById(Long id, String headerAuth) throws Exception;

	public Expense save(Expense expense, String headerAuth) throws Exception;

	public Expense update(/* Integer id, */Expense expense, String headerAuth) throws Exception;

	public Boolean delete(Long id) throws Exception;

	public List<Expense> findAllByExpenseUserId(Long expenseUserId, String headerAuth) throws Exception;

	public Expense findByAmountAndDateAndConceptAndExpenseUserId(Double amount,
			LocalDateTime expenseDate, String concept,
			Long expenseUserId,
			String headerAuth) throws Exception;
			
	// Paginated methods
	public Page<Expense> findAllPaginated(Pageable pageable) throws Exception;
	
	public Page<Expense> findWithFilters(ExpenseSearchCriteria criteria, Pageable pageable, String headerAuth) throws Exception;
}
