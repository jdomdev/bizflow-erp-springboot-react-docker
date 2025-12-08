package io.sunbit.app.util;

import org.springframework.beans.factory.annotation.Autowired;

import io.sunbit.app.dao.IExpenseDao;
import io.sunbit.app.entity.Expense;

public class ExpenseUtil {

	@Autowired
	private static IExpenseDao expenseDao;

	public static Expense existsExpenseInDb(Expense expense) {
		Expense searchedExpense = expenseDao.findByAmountAndExpenseDateAndConceptAndExpenseUser(
			expense.getAmount(),
			expense.getExpenseDate(),
			expense.getConcept(),
			expense.getExpenseUser()).orElse(null);
		return searchedExpense;
	}

	public static String giveMeExpenseEmployeeEmail(Expense expense) {
		String email = null;
		if (existsExpenseInDb(expense) != null) {
			email = expense.getExpenseUser().getEmail();
		}
		return email;
	}

}
