package io.sunbit.app.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.sunbit.app.entity.Employee;
import io.sunbit.app.entity.Expense;
import io.sunbit.app.service.ExpenseServiceImpl;

@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/expense")
@RestController
public class ExpenseControllerImpl implements IExpenseController {

	@Autowired
	private ExpenseServiceImpl expenseService;

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/")
	public ResponseEntity<?> getAllExpense() {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(expenseService.findAll());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("{\"error\":\"Error. Please, Try it later. NOT possible to SHOW all expenses\"}");
		}
	}

	@Override
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	@GetMapping("/employee/{employeeId}")
	@ResponseBody
	public ResponseEntity<?> getAllExpenseByEmployeeId(@PathVariable("employeeId") Long employeeId,
			@RequestHeader("Authorization") String headerAuth) {
		try {
			return ResponseEntity.status(HttpStatus.OK)
					.body(expenseService.findAllByEmployeeId(employeeId, headerAuth));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("{\"error\":\"Error. Please, Try it later. NOT possible to SHOW the employee's expenses.\"}");
		}
	}

	@Override
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	@GetMapping("/{expenseId}")
	// @ResponseBody
	public ResponseEntity<?> getExpenseById(@PathVariable("expenseId") Long expenseId,
			@RequestHeader("Authorization") String headerAuth) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(expenseService.findById(expenseId, headerAuth));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					"{\"error\":\"Error. Please, Try it later. NOT possible to SHOW the expense which you find.\"}");
		}
	}

	@Override
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	@PostMapping("/")
	// @ResponseBody
	public ResponseEntity<?> saveExpense(@RequestBody @Valid Expense expense,
			@RequestHeader("Authorization") String headerAuth) {
		ResponseEntity<?> responseEntity = null;
		try {
			responseEntity = ResponseEntity.status(HttpStatus.OK).body(expenseService.save(expense, headerAuth));
		} catch (Exception e) {
			e.printStackTrace();
			responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("{\"error\":\"Error. Please, Try it later. NOT possible to SAVE the expense.\"}");
		}
		return responseEntity;
	}

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping("/{expenseId}")
	// @ResponseBody
	public ResponseEntity<?> deleteExpense(@PathVariable("expenseId") Long expenseId,
			@RequestHeader("Authorization") String headerAuth) {
		ResponseEntity<Boolean> responseEntity;
		try {
			Expense expense = expenseService.findById(expenseId, headerAuth);
			Employee employee = expense.getEmployee();
			employee.removeExpense(expense);// ******** Check if it's allow.......***********
			responseEntity = ResponseEntity.status(HttpStatus.OK).body(expenseService.delete(expenseId));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("{\"error\":\"Error. Please, Try it later. NOT possible to SAVE the expense.\"}");
		}
		return responseEntity;
	}

	@Override
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	@PutMapping("/")
	public ResponseEntity<?> updateExpense(@RequestBody @Valid Expense expense,
			/* @PathVariable("expenseId")Integer expenseId, */
			@RequestHeader("Authorization") String headerAuth) {
		try {
			return ResponseEntity.status(HttpStatus.OK)
					.body(expenseService.update(/* expenseId, */expense, headerAuth));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					"{\"error\":\"Error. Please, Try it later. NOT possible UPDATE the expense which you are looking for.\"}");
		}
	}
}
