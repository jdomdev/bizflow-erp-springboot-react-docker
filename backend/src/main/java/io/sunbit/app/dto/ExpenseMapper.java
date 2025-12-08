package io.sunbit.app.dto;

import io.sunbit.app.entity.Expense;

public class ExpenseMapper {
	// Without ID.
	    public static Expense dtoToExpense(ExpenseDto expenseDto) {
		    return new Expense(
			    null, expenseDto.getConcept(),
			    expenseDto.getNote(),
			    expenseDto.getExpenseDate(),
			    expenseDto.getAmount(),
			    ExpenseUserMapper.dtoToExpenseUserWithId(expenseDto.getExpenseUserDto()));
	    }

	    public static ExpenseDto expenseToDto(Expense expense) {
		    return new ExpenseDto(
			    expense.getConcept(),
			    expense.getNote(),
			    expense.getDate(),
			    expense.getAmount(),
			    ExpenseUserMapper.expenseUserToDtoWithId(expense.getExpenseUser()));
	    }

	    // With ID.
	    public static Expense dtoToExpenseWithId(ExpenseDto expenseDto) {
		    return new Expense(
			    expenseDto.getId(),
			    expenseDto.getConcept(),
			    expenseDto.getNote(),
			    expenseDto.getExpenseDate(),
			    expenseDto.getAmount(),
			    ExpenseUserMapper.dtoToExpenseUserWithId(expenseDto.getExpenseUserDto()));
	    }

	    public static ExpenseDto expenseToDtoWithId(Expense expense) {
		    return new ExpenseDto(
			    expense.getId(),
			    expense.getConcept(),
			    expense.getNote(),
			    expense.getDate(),
			    expense.getAmount(),
			    ExpenseUserMapper.expenseUserToDtoWithId(expense.getExpenseUser()));
	    }
}
