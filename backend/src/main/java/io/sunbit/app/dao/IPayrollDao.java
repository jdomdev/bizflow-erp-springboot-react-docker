package io.sunbit.app.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.sunbit.app.entity.Employee;
import io.sunbit.app.entity.Payroll;

@Repository
public interface IPayrollDao extends JpaRepository<Payroll, Long> {

	// @Query(value="SELECT * FROM payroll WHERE payroll.employee_id_fk=?1",
	// nativeQuery=true)
	// List<Payroll> findAllPayrollByEmployeeId(Integer employeeId);

	List<Payroll> findAllByEmployee_Id(Long employeeId);
	List<Payroll> findAllByExpenseUser_Id(Long expenseUserId);

	Boolean findByPayrollDateAndEmployeeAllIgnoreCase(LocalDateTime payrollDate, Employee employee);
}
