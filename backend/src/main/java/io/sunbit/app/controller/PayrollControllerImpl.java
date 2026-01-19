package io.sunbit.app.controller;

import java.time.format.TextStyle;
import java.util.Locale;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.sunbit.app.entity.Employee;
import io.sunbit.app.entity.NotificationType;
import io.sunbit.app.entity.Payroll;
import io.sunbit.app.security.dao.IUserDao;
import io.sunbit.app.security.entity.ExpenseUser;
import io.sunbit.app.service.INotificationService;
import io.sunbit.app.service.PayrollServiceImpl;

@CrossOrigin(origins = "*")
@RequestMapping("api/v1/payroll")
@RestController
public class PayrollControllerImpl implements IPayrollController<Payroll> {
	@Autowired
	private PayrollServiceImpl payrollService;
	
	@Autowired
	private INotificationService notificationService;
	
	@Autowired
	private IUserDao userDao;

	/**
	 * Obtiene todas las nóminas del usuario autenticado.
	 * Busca por expense_user_id Y por employee_id (si el usuario tiene empleado vinculado).
	 * Esto cubre tanto freelances como empleados internos.
	 */
	@GetMapping("/my")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<?> getMyPayrolls(Authentication authentication) {
		try {
			String email = authentication.getName();
			return ResponseEntity.status(HttpStatus.OK).body(payrollService.findAllByUserEmail(email));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("{\"error\":\"Error. Please, try again later. It is NOT possible to SHOW your payrolls.\"}");
		}
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping
	public ResponseEntity<?> getAllPayroll() {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(payrollService.findAll());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("{\"error\":\"Error. Please, Try it later. It is NOT possible to SHOW all payrolls\"}");
		}
	}

	@Override
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	@GetMapping("/employee/{employeeId}")
	// @ResponseBody
	public ResponseEntity<?> getAllPayrollByEmployeeId(@PathVariable("employeeId") Long employeeId) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(payrollService.findAllPayrollByEmployeeId(employeeId));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					"{\"error\":\"Error. Please, Try it later. It is NOT possible to SHOW the employee's payrolls.\"}");
		}
	}

	@Override
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	@GetMapping("/user/{expenseUserId}")
	public ResponseEntity<?> getAllPayrollByExpenseUserId(@PathVariable("expenseUserId") Long expenseUserId) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(payrollService.findAllPayrollByExpenseUserId(expenseUserId));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					"{\"error\":\"Error. Please, Try it later. It is NOT possible to SHOW the user's payrolls.\"}");
		}
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	@GetMapping("/{payrollId}")
	// @ResponseBody
	public ResponseEntity<?> getPayrollById(@PathVariable("payrollId") Long payrollId) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(payrollService.findById(payrollId));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					"{\"error\":\"Error. Please, Try it later. It is NOT possible to SHOW the payroll which you find.\"}");
		}
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/")
	public ResponseEntity<?> savePayroll(@RequestBody @Valid Payroll payroll) {
		ResponseEntity<Payroll> responseEntity;
		try {
			// Capture employee ID before saving (proxy may not be loaded after save)
			Long employeeId = payroll.getEmployeeId();
			Long expenseUserId = payroll.getExpenseUserId();
			
			Payroll savedPayroll = payrollService.save(payroll);
			Employee employee = savedPayroll.getEmployee();
			if (employee != null) {
				employee.addPayroll(savedPayroll);
			}
			
			// Send notification to the employee/user about new payroll
			try {
				ExpenseUser targetUser = null;
				
				// First try by employeeId
				if (employeeId != null) {
					targetUser = userDao.findByEmployee_Id(employeeId).orElse(null);
				}
				// If not found and expenseUserId provided, use that
				if (targetUser == null && expenseUserId != null) {
					targetUser = userDao.findById(expenseUserId).orElse(null);
				}
				
				if (targetUser != null) {
					String monthName = savedPayroll.getPayrollDate() != null 
						? savedPayroll.getPayrollDate().getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"))
						: "este mes";
					
					notificationService.createForUser(
						targetUser.getId(),
						NotificationType.PAYROLL_AVAILABLE,
						"Nómina disponible",
						String.format("Tu nómina de %s está disponible. Importe: %.2f€", 
							monthName, savedPayroll.getAmount())
					);
				}
			} catch (Exception notifError) {
				notifError.printStackTrace();
			}
			
			responseEntity = ResponseEntity.status(HttpStatus.OK).body(savedPayroll);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("{\"error\":\"Error. Please, Try it later. It is NOT possible to SAVE the entity.\"}");
		}
		return responseEntity;
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping("/{payrollId}")
	public ResponseEntity<?> deletePayroll(@PathVariable("payrollId") Long payrollId) {
		ResponseEntity<Boolean> responseEntity;
		try {
			Payroll payroll = payrollService.findById(payrollId);
			
			// Store info for notification before deletion
			Long employeeId = payroll.getEmployeeId();
			Long expenseUserId = payroll.getExpenseUserId();
			String monthName = payroll.getPayrollDate() != null 
				? payroll.getPayrollDate().getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"))
				: "reciente";
			Double salary = payroll.getAmount();
			
			Employee employee = payroll.getEmployee();
			if (employee != null) {
				employee.removePayroll(payroll);
			}
			Boolean deleted = payrollService.delete(payrollId);
			
			// Notify employee/user about deleted payroll
			if (deleted) {
				try {
					ExpenseUser targetUser = null;
					
					if (employeeId != null) {
						targetUser = userDao.findByEmployee_Id(employeeId).orElse(null);
					}
					if (targetUser == null && expenseUserId != null) {
						targetUser = userDao.findById(expenseUserId).orElse(null);
					}
					
					if (targetUser != null) {
						notificationService.createForUser(
							targetUser.getId(),
							NotificationType.PAYROLL_REMINDER,
							"Nómina eliminada",
							String.format("Tu nómina de %s (%.2f€) ha sido eliminada del sistema.", 
								monthName, salary)
						);
					}
				} catch (Exception notifError) {
					notifError.printStackTrace();
				}
			}
			
			responseEntity = ResponseEntity.status(HttpStatus.OK).body(deleted);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("{\"error\":\"Error. Please, Try it later. It is NOT possible to DELETE the payroll.\"}");
		}
		return responseEntity;
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@PutMapping("/{payrollId}")
	public ResponseEntity<?> updatePayroll(@RequestBody @Valid Payroll payroll,
			@PathVariable("payrollId") Long payrollId) {
		try {
			Payroll updatedPayroll = payrollService.update(payrollId, payroll);
			
			// Notify employee/user about updated payroll
			if (updatedPayroll != null) {
				try {
					Long employeeId = updatedPayroll.getEmployeeId();
					Long expenseUserId = updatedPayroll.getExpenseUserId();
					
					ExpenseUser targetUser = null;
					if (employeeId != null) {
						targetUser = userDao.findByEmployee_Id(employeeId).orElse(null);
					}
					if (targetUser == null && expenseUserId != null) {
						targetUser = userDao.findById(expenseUserId).orElse(null);
					}
					
					if (targetUser != null) {
						String monthName = updatedPayroll.getPayrollDate() != null 
							? updatedPayroll.getPayrollDate().getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"))
							: "reciente";
						
						notificationService.createForUser(
							targetUser.getId(),
							NotificationType.PAYROLL_AVAILABLE,
							"Nómina actualizada",
							String.format("Tu nómina de %s ha sido actualizada. Nuevo importe: %.2f€", 
								monthName, updatedPayroll.getAmount())
						);
					}
				} catch (Exception notifError) {
					notifError.printStackTrace();
				}
			}
			
			return ResponseEntity.status(HttpStatus.OK).body(updatedPayroll);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					"{\"error\":\"Error. Please, Try it later. It is NOT possible UPDATE the payroll which you are looking for.\"}");
		}
	}
}