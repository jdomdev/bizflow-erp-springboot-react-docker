package io.sunbit.app.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import io.sunbit.app.entity.Expense;

public interface IExpenseController {

	public ResponseEntity<?> getAllExpenseByEmployeeId(@PathVariable Long employeeId,
			@RequestHeader String token);

	public ResponseEntity<?> getAllExpense();

	public ResponseEntity<?> getExpenseById(@PathVariable Long expenseId,
			@RequestHeader String token);

	public ResponseEntity<?> saveExpense(@RequestBody @Valid Expense expense,
			@RequestHeader String token);

	public ResponseEntity<?> updateExpense(@RequestBody @Valid Expense expense,
			/* @PathVariable Integer expenseId, */
			@RequestHeader String token);

	public ResponseEntity<?> deleteExpense(@PathVariable Long expenseId,
			@RequestHeader String token);
}
