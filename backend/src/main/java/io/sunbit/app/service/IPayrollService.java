package io.sunbit.app.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.sunbit.app.dto.PayrollSearchCriteria;
import io.sunbit.app.entity.Employee;
import io.sunbit.app.entity.Payroll;

public interface IPayrollService {

	public List<Payroll> findAll() throws Exception;

	public Payroll findById(Long id) throws Exception;

	public Payroll save(Payroll payroll) throws Exception;

	public Payroll update(Long id, Payroll payroll) throws Exception;

	public Boolean delete(Long id) throws Exception;

	public List<Payroll> findAllPayrollByEmployeeId(Long employeeId) throws Exception;

	public List<Payroll> findAllPayrollByExpenseUserId(Long expenseUserId) throws Exception;

	public Boolean findByDateAndEmployeeAllIgnoreCase(LocalDateTime PayrollDate, Employee employee) throws Exception;
	
	// Paginated methods
	public Page<Payroll> findAllPaginated(Pageable pageable) throws Exception;
	
	public Page<Payroll> findWithFilters(PayrollSearchCriteria criteria, Pageable pageable, String headerAuth) throws Exception;
}
