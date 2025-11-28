package io.sunbit.app.util;

import org.springframework.beans.factory.annotation.Autowired;

import io.sunbit.app.dao.IExpenseDao;
import io.sunbit.app.entity.Expense;

public class ExpenseUtil {

	@Autowired
	private static IExpenseDao expenseDao;

	public static Expense existsExpenseInDb(Expense expense) {
		Expense searchedExpense = expenseDao.findByAmountAndDateAndConceptAndEmployeeId(
				expense.getAmount(),
				expense.getDate(),
				expense.getConcept(),
				expense.getEmployee().getId()).get();
		return searchedExpense;
	}

	public static String giveMeExpenseEmployeeEmail(Expense expense) {
		String email = null;
		if (existsExpenseInDb(expense) != null) {
			email = expense.getEmployee().getEmail();
		}
		return email;
	}

}
