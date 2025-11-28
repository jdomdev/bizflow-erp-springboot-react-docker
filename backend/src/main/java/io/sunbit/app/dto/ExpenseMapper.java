package io.sunbit.app.dto;

import io.sunbit.app.entity.Expense;

public class ExpenseMapper {
	// Without ID.
	public static Expense dtoToExpense(ExpenseDto expenseDto) {
		return new Expense(
				null, expenseDto.getConcept(),
				expenseDto.getNote(),
				expenseDto.getDate(),
				expenseDto.getAmount(),
				EmployeeMapper.dtoToEmployeeWithId(expenseDto.getEmployeeDto()));
	}

	public static ExpenseDto expenseToDto(Expense expense) {
		return new ExpenseDto(
				expense.getConcept(),
				expense.getNote(),
				expense.getDate(),
				expense.getAmount(),
				EmployeeMapper.employeeToDtoWithId(expense.getEmployee()));
	}

	// With ID.
	public static Expense dtoToExpenseWithId(ExpenseDto expenseDto) {
		return new Expense(
				expenseDto.getId(),
				expenseDto.getConcept(),
				expenseDto.getNote(),
				expenseDto.getDate(),
				expenseDto.getAmount(),
				EmployeeMapper.dtoToEmployeeWithId(expenseDto.getEmployeeDto()));
	}

	public static ExpenseDto expenseToDtoWithId(Expense expense) {
		return new ExpenseDto(
				expense.getId(),
				expense.getConcept(),
				expense.getNote(),
				expense.getDate(),
				expense.getAmount(),
				EmployeeMapper.employeeToDtoWithId(expense.getEmployee()));
	}
}
