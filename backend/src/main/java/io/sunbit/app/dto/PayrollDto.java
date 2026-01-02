package io.sunbit.app.dto;

import java.time.LocalDateTime;

import io.sunbit.app.entity.Payroll;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class PayrollDto extends Payroll {

	private static final long serialVersionUID = 1L;
	private Long id;
	@NonNull
	private Double amount;
	@NonNull
	private LocalDateTime payrollDate;
	private EmployeeDto employeeDto;
	private ExpenseUserDto expenseUserDto;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Double getAmount() {
		return amount;
	}

	@Override
	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Override
	public LocalDateTime getPayrollDate() {
		return payrollDate;
	}

	@Override
	public void setPayrollDate(LocalDateTime payrollDate) {
		this.payrollDate = payrollDate;
	}

	public EmployeeDto getEmployeeDto() {
		return employeeDto;
	}

	public void setEmployeeDto(EmployeeDto employeeDto) {
		this.employeeDto = employeeDto;
	}

	public ExpenseUserDto getExpenseUserDto() {
		return expenseUserDto;
	}

	public void setExpenseUserDto(ExpenseUserDto expenseUserDto) {
		this.expenseUserDto = expenseUserDto;
	}
}
