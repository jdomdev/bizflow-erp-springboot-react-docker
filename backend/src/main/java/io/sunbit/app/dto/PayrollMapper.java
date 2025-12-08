package io.sunbit.app.dto;

import io.sunbit.app.entity.Payroll;

public class PayrollMapper {
	// Without ID.
	    public static Payroll dtoToPayroll(PayrollDto dtoPayroll) {
		    return new Payroll(
			    dtoPayroll.getAmount(),
			    dtoPayroll.getPayrollDate(),
			    EmployeeMapper.dtoToEmployeeWithId(dtoPayroll.getEmployeeDto()),
			    ExpenseUserMapper.dtoToExpenseUserWithId(dtoPayroll.getExpenseUserDto()));
	    }

	    public static PayrollDto payrollToDto(Payroll payroll) {
		    return new PayrollDto(
			    payroll.getAmount(),
			    payroll.getDate(),
			    EmployeeMapper.employeeToDtoWithId(payroll.getEmployee()),
			    ExpenseUserMapper.expenseUserToDtoWithId(payroll.getExpenseUser()));
	    }

	    // With ID.
	    public static Payroll dtoToPayrollWithId(PayrollDto dtoPayroll) {
		    return new Payroll(
			    dtoPayroll.getId(),
			    dtoPayroll.getAmount(),
			    dtoPayroll.getPayrollDate(),
			    EmployeeMapper.dtoToEmployeeWithId(dtoPayroll.getEmployeeDto()),
			    ExpenseUserMapper.dtoToExpenseUserWithId(dtoPayroll.getExpenseUserDto()));
	    }

	    public static PayrollDto payrollToDtoWithId(Payroll payroll) {
		    return new PayrollDto(
			    payroll.getId(),
			    payroll.getAmount(),
			    payroll.getDate(),
			    EmployeeMapper.employeeToDtoWithId(payroll.getEmployee()),
			    ExpenseUserMapper.expenseUserToDtoWithId(payroll.getExpenseUser()));
	    }
}
