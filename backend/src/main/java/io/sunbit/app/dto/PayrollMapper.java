package io.sunbit.app.dto;

import io.sunbit.app.entity.Payroll;

public class PayrollMapper {
	// Without ID.
	public static Payroll dtoToPayroll(PayrollDto dtoPayroll) {
		return new Payroll(
				dtoPayroll.getAmount(),
				dtoPayroll.getDate(),
				EmployeeMapper.dtoToEmployeeWithId(dtoPayroll.getEmployeeDto()));
	}

	public static PayrollDto payrollToDto(Payroll payroll) {
		return new PayrollDto(
				payroll.getAmount(),
				payroll.getDate(),
				EmployeeMapper.employeeToDtoWithId(payroll.getEmployee()));
	}

	// With ID.
	public static Payroll dtoToPayrollWithId(PayrollDto dtoPayroll) {
		return new Payroll(
				dtoPayroll.getId(),
				dtoPayroll.getAmount(),
				dtoPayroll.getDate(),
				EmployeeMapper.dtoToEmployeeWithId(dtoPayroll.getEmployeeDto()));
	}

	public static PayrollDto payrollToDtoWithId(Payroll payroll) {
		return new PayrollDto(
				payroll.getId(),
				payroll.getAmount(),
				payroll.getDate(),
				EmployeeMapper.employeeToDtoWithId(payroll.getEmployee()));
	}
}
