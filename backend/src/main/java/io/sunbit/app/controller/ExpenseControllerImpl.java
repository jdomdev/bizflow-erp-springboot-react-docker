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

import io.sunbit.app.entity.Expense;
import io.sunbit.app.entity.NotificationType;
import io.sunbit.app.service.ExpenseServiceImpl;
import io.sunbit.app.service.INotificationService;
import io.sunbit.app.security.dao.IUserDao;
import io.sunbit.app.security.entity.ExpenseUser;
import io.sunbit.app.security.jwt.JwtAuthenticationUtil;

@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/expense")
@RestController
public class ExpenseControllerImpl implements IExpenseController {

	@Autowired
	private ExpenseServiceImpl expenseService;
	
	@Autowired
	private JwtAuthenticationUtil jwtAuthUtil;
	
	@Autowired
	private INotificationService notificationService;
	
	@Autowired
	private IUserDao userDao;

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping
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
	@GetMapping("/user/{expenseUserId}")
	@ResponseBody
	public ResponseEntity<?> getAllExpenseByExpenseUserId(@PathVariable("expenseUserId") Long expenseUserId,
			@RequestHeader("Authorization") String headerAuth) {
		try {
			return ResponseEntity.status(HttpStatus.OK)
					.body(expenseService.findAllByExpenseUserId(expenseUserId, headerAuth));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("{\"error\":\"Error. Please, Try it later. NOT possible to SHOW the user's expenses.\"}");
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
	public ResponseEntity<?> saveExpense(@RequestBody @Valid io.sunbit.app.dto.ExpenseCreateRequest request,
			@RequestHeader("Authorization") String headerAuth) {
		ResponseEntity<?> responseEntity = null;
		try {
			// Convertir ExpenseCreateRequest a Expense
			io.sunbit.app.entity.Expense expense = new io.sunbit.app.entity.Expense();
			expense.setConcept(request.getConcept());
			expense.setNote(request.getNote());
			expense.setExpenseDate(request.getExpenseDate());
			expense.setAmount(request.getAmount());
			
			// Get userId: from request or extract from JWT token
			Long userId = request.getExpenseUserId();
			if (userId == null) {
				String token = headerAuth.replace("Bearer ", "");
				Integer tokenUserId = jwtAuthUtil.extractTokenUserId(token);
				if (tokenUserId != null) {
					userId = tokenUserId.longValue();
				} else {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body("{\"error\":\"Could not determine user ID from token\"}");
				}
			}
			
			// Buscar el ExpenseUser por ID
			io.sunbit.app.security.entity.ExpenseUser expenseUser = new io.sunbit.app.security.entity.ExpenseUser();
			expenseUser.setId(userId);
			expense.setExpenseUser(expenseUser);
			
			Object savedExpense = expenseService.save(expense, headerAuth);
			
			// Send notification to admins about new expense (exclude creator if admin)
			final Long creatorId = userId;
			try {
				ExpenseUser user = userDao.findById(userId).orElse(null);
				String userName = user != null && user.getEmployee() != null 
					? user.getEmployee().getName() + " " + user.getEmployee().getSurname() 
					: "Usuario #" + userId;
				
				notificationService.createForRoleExcludingUser(
					"ADMIN",
					creatorId,
					NotificationType.EXPENSE_CREATED,
					"Nuevo gasto registrado",
					String.format("%s ha registrado un gasto de %.2f€: %s", 
						userName, expense.getAmount(), expense.getConcept())
				);
			} catch (Exception notifError) {
				// Log but don't fail the expense creation
				notifError.printStackTrace();
			}
			
			responseEntity = ResponseEntity.status(HttpStatus.CREATED).body(savedExpense);
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
	public ResponseEntity<?> deleteExpense(@PathVariable("expenseId") Long expenseId,
			@RequestHeader("Authorization") String headerAuth) {
		try {
			// Get expense info before deletion for notification
			String token = headerAuth.replace("Bearer ", "");
			Long deleterId = jwtAuthUtil.extractTokenUserId(token).longValue();
			
			// Get expense details before deletion
			Object expenseData = expenseService.findById(expenseId, headerAuth);
			String expenseConcept = "Gasto #" + expenseId;
			Double expenseAmount = 0.0;
			Long ownerId = null;
			
			if (expenseData instanceof io.sunbit.app.entity.Expense) {
				io.sunbit.app.entity.Expense exp = (io.sunbit.app.entity.Expense) expenseData;
				expenseConcept = exp.getConcept();
				expenseAmount = exp.getAmount();
				if (exp.getExpenseUser() != null) {
					ownerId = exp.getExpenseUser().getId();
				}
			}
			
			Boolean deleted = expenseService.delete(expenseId);
			
			// Send notification to other admins about deletion
			if (deleted) {
				try {
					ExpenseUser deleter = userDao.findById(deleterId).orElse(null);
					String deleterName = deleter != null && deleter.getEmployee() != null 
						? deleter.getEmployee().getName() + " " + deleter.getEmployee().getSurname() 
						: "Admin #" + deleterId;
					
					notificationService.createForRoleExcludingUser(
						"ADMIN",
						deleterId,
						NotificationType.EXPENSE_DELETED,
						"Gasto eliminado",
						String.format("%s ha eliminado un gasto de %.2f€: %s", 
							deleterName, expenseAmount, expenseConcept)
					);
					
					// Notify the expense owner if different from deleter
					if (ownerId != null && !ownerId.equals(deleterId)) {
						notificationService.createForUser(
							ownerId,
							NotificationType.EXPENSE_DELETED,
							"Tu gasto ha sido eliminado",
							String.format("Un administrador ha eliminado tu gasto de %.2f€: %s", 
								expenseAmount, expenseConcept)
						);
					}
				} catch (Exception notifError) {
					notifError.printStackTrace();
				}
			}
			
			return ResponseEntity.status(HttpStatus.OK).body(deleted);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("{\"error\":\"Error. Please, Try it later. NOT possible to DELETE the expense.\"}");
		}
	}

	@Override
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	@PutMapping("/")
	public ResponseEntity<?> updateExpense(@RequestBody @Valid Expense expense,
			@RequestHeader("Authorization") String headerAuth) {
		try {
			String token = headerAuth.replace("Bearer ", "");
			Long editorId = jwtAuthUtil.extractTokenUserId(token).longValue();
			
			Object updatedExpense = expenseService.update(expense, headerAuth);
			
			// Send notification about expense update
			if (updatedExpense != null) {
				try {
					ExpenseUser editor = userDao.findById(editorId).orElse(null);
					String editorName = editor != null && editor.getEmployee() != null 
						? editor.getEmployee().getName() + " " + editor.getEmployee().getSurname() 
						: "Usuario #" + editorId;
					
					// Notify other admins
					notificationService.createForRoleExcludingUser(
						"ADMIN",
						editorId,
						NotificationType.EXPENSE_UPDATED,
						"Gasto modificado",
						String.format("%s ha modificado un gasto: %s (%.2f€)", 
							editorName, expense.getConcept(), expense.getAmount())
					);
				} catch (Exception notifError) {
					notifError.printStackTrace();
				}
			}
			
			return ResponseEntity.status(HttpStatus.OK).body(updatedExpense);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					"{\"error\":\"Error. Please, Try it later. NOT possible UPDATE the expense which you are looking for.\"}");
		}
	}
}
