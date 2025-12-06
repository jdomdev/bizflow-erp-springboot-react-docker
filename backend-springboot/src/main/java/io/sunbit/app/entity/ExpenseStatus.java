package io.sunbit.app.entity;

/**
 * Enum representing the approval status of an expense.
 */
public enum ExpenseStatus {
	/**
	 * Expense has been submitted but not yet reviewed
	 */
	PENDING,
	
	/**
	 * Expense has been reviewed and approved
	 */
	APPROVED,
	
	/**
	 * Expense has been reviewed and rejected
	 */
	REJECTED
}
