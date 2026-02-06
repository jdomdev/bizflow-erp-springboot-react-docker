package io.sunbit.app.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.sunbit.app.entity.Employee;
import io.sunbit.app.entity.Payroll;

@Repository
public interface IPayrollDao extends JpaRepository<Payroll, Long>, JpaSpecificationExecutor<Payroll> {

	// @Query(value="SELECT * FROM payroll WHERE payroll.employee_id_fk=?1",
	// nativeQuery=true)
	// List<Payroll> findAllPayrollByEmployeeId(Integer employeeId);

	List<Payroll> findAllByEmployee_Id(Long employeeId);
	List<Payroll> findAllByExpenseUser_Id(Long expenseUserId);

	Boolean findByPayrollDateAndEmployeeAllIgnoreCase(LocalDateTime payrollDate, Employee employee);
	
	// Paginated queries
	Page<Payroll> findAllByEmployee_Id(Long employeeId, Pageable pageable);
	Page<Payroll> findAllByExpenseUser_Id(Long expenseUserId, Pageable pageable);
	
	// Search with filters - using JPQL
	// Note: Using COALESCE to handle null search parameter correctly with PostgreSQL
	@Query("SELECT p FROM Payroll p JOIN p.employee e WHERE " +
	       "(:userId IS NULL OR p.expenseUser.id = :userId) AND " +
	       "(COALESCE(:search, '') = '' OR LOWER(e.name) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) OR LOWER(e.surname) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))) AND " +
	       "(:minAmount IS NULL OR p.amount >= :minAmount) AND " +
	       "(:maxAmount IS NULL OR p.amount <= :maxAmount) AND " +
	       "(:startDate IS NULL OR p.payrollDate >= :startDate) AND " +
	       "(:endDate IS NULL OR p.payrollDate <= :endDate)")
	Page<Payroll> findWithFilters(
	    @Param("userId") Long userId,
	    @Param("search") String search,
	    @Param("minAmount") Double minAmount,
	    @Param("maxAmount") Double maxAmount,
	    @Param("startDate") LocalDateTime startDate,
	    @Param("endDate") LocalDateTime endDate,
	    Pageable pageable);
}
