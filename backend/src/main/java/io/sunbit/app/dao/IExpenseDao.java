package io.sunbit.app.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.sunbit.app.entity.Expense;
import io.sunbit.app.security.entity.ExpenseUser;

@Repository
public interface IExpenseDao extends JpaRepository<Expense, Long>, JpaSpecificationExecutor<Expense> {

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
		    
		// Paginated queries
		Page<Expense> findAllByExpenseUser_Id(Long expenseUserId, Pageable pageable);
		
		// Search with filters - using JPQL for better flexibility
		// Note: Using COALESCE to handle null search parameter correctly with PostgreSQL
		// For dates: using COALESCE with far past/future defaults to avoid null type inference issues
		@Query("SELECT e FROM Expense e WHERE " +
		       "(:userId IS NULL OR e.expenseUser.id = :userId) AND " +
		       "(COALESCE(:search, '') = '' OR LOWER(e.concept) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) OR LOWER(COALESCE(e.note, '')) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))) AND " +
		       "(:minAmount IS NULL OR e.amount >= :minAmount) AND " +
		       "(:maxAmount IS NULL OR e.amount <= :maxAmount) AND " +
		       "e.expenseDate >= COALESCE(:startDate, CAST('1900-01-01' AS timestamp)) AND " +
		       "e.expenseDate <= COALESCE(:endDate, CAST('2099-12-31' AS timestamp))")
		Page<Expense> findWithFilters(
		    @Param("userId") Long userId,
		    @Param("search") String search,
		    @Param("minAmount") Double minAmount,
		    @Param("maxAmount") Double maxAmount,
		    @Param("startDate") LocalDateTime startDate,
		    @Param("endDate") LocalDateTime endDate,
		    Pageable pageable);
}
