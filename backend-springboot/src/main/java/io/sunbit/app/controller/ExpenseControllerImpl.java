package io.sunbit.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.sunbit.app.entity.Employee;
import io.sunbit.app.entity.Expense;
import io.sunbit.app.entity.ExpenseAttachment;
import io.sunbit.app.entity.ExpenseStatus;
import io.sunbit.app.service.ExpenseServiceImpl;

@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/expense")
@RestController
public class ExpenseControllerImpl implements IExpenseController {

	private static final Logger log = LoggerFactory.getLogger(ExpenseControllerImpl.class);

	@Autowired
	private ExpenseServiceImpl expenseService;

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/")
	public ResponseEntity<?> getAllExpense() {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(expenseService.findAll());
		} catch (Exception e) {
			log.error("Error getting all expenses: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(createErrorResponse("Error retrieving all expenses"));
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
		} catch (SecurityException e) {
			log.error("Security violation: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(createErrorResponse("Unauthorized access to employee expenses"));
		} catch (Exception e) {
			log.error("Error getting expenses by employee ID: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(createErrorResponse("Error retrieving employee expenses"));
		}
	}

	@Override
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	@GetMapping("/{expenseId}")
	public ResponseEntity<?> getExpenseById(@PathVariable("expenseId") Long expenseId,
			@RequestHeader("Authorization") String headerAuth) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(expenseService.findById(expenseId, headerAuth));
		} catch (SecurityException e) {
			log.error("Security violation: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(createErrorResponse("Unauthorized access to expense"));
		} catch (Exception e) {
			log.error("Error getting expense by ID: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(createErrorResponse("Expense not found"));
		}
	}

	@Override
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	@PostMapping("/")
	public ResponseEntity<?> saveExpense(@RequestBody @Valid Expense expense,
			@RequestHeader("Authorization") String headerAuth) {
		try {
			return ResponseEntity.status(HttpStatus.CREATED).body(expenseService.save(expense, headerAuth));
		} catch (SecurityException e) {
			log.error("Security violation: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(createErrorResponse("Unauthorized to create expense"));
		} catch (Exception e) {
			log.error("Error saving expense: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(createErrorResponse("Error saving expense: " + e.getMessage()));
		}
	}

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping("/{expenseId}")
	public ResponseEntity<?> deleteExpense(@PathVariable("expenseId") Long expenseId,
			@RequestHeader("Authorization") String headerAuth) {
		try {
			Expense expense = expenseService.findById(expenseId, headerAuth);
			Employee employee = expense.getEmployee();
			employee.removeExpense(expense);
			return ResponseEntity.status(HttpStatus.OK).body(expenseService.delete(expenseId));
		} catch (Exception e) {
			log.error("Error deleting expense: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(createErrorResponse("Error deleting expense"));
		}
	}

	@Override
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	@PutMapping("/")
	public ResponseEntity<?> updateExpense(@RequestBody @Valid Expense expense,
			@RequestHeader("Authorization") String headerAuth) {
		try {
			return ResponseEntity.status(HttpStatus.OK)
					.body(expenseService.update(expense, headerAuth));
		} catch (SecurityException e) {
			log.error("Security violation: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(createErrorResponse("Unauthorized to update expense"));
		} catch (IllegalStateException e) {
			log.error("Invalid state: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(createErrorResponse(e.getMessage()));
		} catch (Exception e) {
			log.error("Error updating expense: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(createErrorResponse("Error updating expense"));
		}
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping("/{expenseId}/approve")
	public ResponseEntity<?> approveExpense(@PathVariable("expenseId") Long expenseId,
			@RequestHeader("Authorization") String headerAuth) {
		try {
			Expense approvedExpense = expenseService.approve(expenseId, headerAuth);
			return ResponseEntity.status(HttpStatus.OK).body(approvedExpense);
		} catch (SecurityException e) {
			log.error("Security violation: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(createErrorResponse("Only administrators can approve expenses"));
		} catch (IllegalStateException e) {
			log.error("Invalid state: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(createErrorResponse(e.getMessage()));
		} catch (Exception e) {
			log.error("Error approving expense: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(createErrorResponse("Error approving expense"));
		}
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping("/{expenseId}/reject")
	public ResponseEntity<?> rejectExpense(@PathVariable("expenseId") Long expenseId,
			@RequestBody Map<String, String> request,
			@RequestHeader("Authorization") String headerAuth) {
		try {
			String reason = request.get("reason");
			if (reason == null || reason.trim().isEmpty()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(createErrorResponse("Rejection reason is required"));
			}
			Expense rejectedExpense = expenseService.reject(expenseId, reason, headerAuth);
			return ResponseEntity.status(HttpStatus.OK).body(rejectedExpense);
		} catch (SecurityException e) {
			log.error("Security violation: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(createErrorResponse("Only administrators can reject expenses"));
		} catch (IllegalStateException e) {
			log.error("Invalid state: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(createErrorResponse(e.getMessage()));
		} catch (Exception e) {
			log.error("Error rejecting expense: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(createErrorResponse("Error rejecting expense"));
		}
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/status/{status}")
	public ResponseEntity<?> getExpensesByStatus(@PathVariable("status") String status,
			@RequestHeader("Authorization") String headerAuth) {
		try {
			ExpenseStatus expenseStatus = ExpenseStatus.valueOf(status.toUpperCase());
			List<Expense> expenses = expenseService.findByStatus(expenseStatus, headerAuth);
			return ResponseEntity.status(HttpStatus.OK).body(expenses);
		} catch (IllegalArgumentException e) {
			log.error("Invalid status: {}", status);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(createErrorResponse("Invalid status. Valid values: PENDING, APPROVED, REJECTED"));
		} catch (Exception e) {
			log.error("Error getting expenses by status: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(createErrorResponse("Error retrieving expenses by status"));
		}
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	@PostMapping(value = "/{expenseId}/attachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> uploadAttachment(@PathVariable("expenseId") Long expenseId,
			@RequestParam("file") MultipartFile file,
			@RequestHeader("Authorization") String headerAuth) {
		try {
			ExpenseAttachment attachment = expenseService.uploadAttachment(expenseId, file, headerAuth);
			return ResponseEntity.status(HttpStatus.CREATED).body(attachment);
		} catch (SecurityException e) {
			log.error("Security violation: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(createErrorResponse("Unauthorized to upload attachment"));
		} catch (IllegalArgumentException e) {
			log.error("Invalid argument: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(createErrorResponse(e.getMessage()));
		} catch (Exception e) {
			log.error("Error uploading attachment: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(createErrorResponse("Error uploading attachment"));
		}
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	@GetMapping("/{expenseId}/attachment")
	public ResponseEntity<?> getAttachments(@PathVariable("expenseId") Long expenseId,
			@RequestHeader("Authorization") String headerAuth) {
		try {
			List<ExpenseAttachment> attachments = expenseService.getAttachments(expenseId, headerAuth);
			return ResponseEntity.status(HttpStatus.OK).body(attachments);
		} catch (SecurityException e) {
			log.error("Security violation: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(createErrorResponse("Unauthorized to view attachments"));
		} catch (Exception e) {
			log.error("Error getting attachments: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(createErrorResponse("Error retrieving attachments"));
		}
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	@DeleteMapping("/attachment/{attachmentId}")
	public ResponseEntity<?> deleteAttachment(@PathVariable("attachmentId") Long attachmentId,
			@RequestHeader("Authorization") String headerAuth) {
		try {
			Boolean deleted = expenseService.deleteAttachment(attachmentId, headerAuth);
			return ResponseEntity.status(HttpStatus.OK).body(deleted);
		} catch (SecurityException e) {
			log.error("Security violation: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(createErrorResponse("Unauthorized to delete attachment"));
		} catch (Exception e) {
			log.error("Error deleting attachment: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(createErrorResponse("Error deleting attachment"));
		}
	}
	
	private Map<String, String> createErrorResponse(String message) {
		Map<String, String> error = new HashMap<>();
		error.put("error", message);
		return error;
	}
}
