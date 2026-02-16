package io.sunbit.app.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.sunbit.app.dao.IEmployeeDao;
import io.sunbit.app.dao.IPayrollDao;
import io.sunbit.app.dao.PayrollSpecifications;
import io.sunbit.app.dto.PayrollSearchCriteria;
import io.sunbit.app.entity.Employee;
import io.sunbit.app.entity.Payroll;
import io.sunbit.app.exception.BadRequestException;
import io.sunbit.app.exception.ResourceNotFoundException;
import io.sunbit.app.security.dao.IUserDao;
import io.sunbit.app.security.entity.ExpenseUser;
import io.sunbit.app.security.jwt.JwtAuthenticationUtil;
import io.sunbit.app.util.DateUtil;

@Service
public class PayrollServiceImpl implements IPayrollService {

	@Autowired
	private IPayrollDao payrollDao;

	@Autowired
	private IEmployeeDao employeeDao;

	@Autowired
	private IUserDao userDao;
	
	@Autowired
	private JwtAuthenticationUtil jwtAuthUtil;

	/**
	 * Obtiene todas las nóminas del usuario por su email.
	 * Busca por expense_user_id Y por employee_id (si el usuario tiene empleado vinculado).
	 * Esto cubre tanto freelances (solo expense_user_id) como empleados internos (employee_id).
	 * 
	 * @param email El email del usuario autenticado
	 * @return Lista de nóminas del usuario (sin duplicados)
	 */
	public List<Payroll> findAllByUserEmail(String email) throws Exception {
		try {
			Optional<ExpenseUser> userOpt = userDao.findByEmail(email);
			if (userOpt.isEmpty()) {
				throw new ResourceNotFoundException("User", "email", email);
			}
			
			ExpenseUser user = userOpt.get();
			Set<Payroll> payrollSet = new HashSet<>();
			
			// Buscar nóminas por expense_user_id (freelances y usuarios con nóminas directas)
			List<Payroll> byUserId = payrollDao.findAllByExpenseUser_Id(user.getId());
			payrollSet.addAll(byUserId);
			
			// Si el usuario tiene empleado vinculado, buscar también por employee_id
			Employee employee = user.getEmployee();
			if (employee != null) {
				List<Payroll> byEmployeeId = payrollDao.findAllByEmployee_Id(employee.getId());
				payrollSet.addAll(byEmployeeId);
			}
			
			return new ArrayList<>(payrollSet);
		} catch (ResourceNotFoundException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public List<Payroll> findAllPayrollByExpenseUserId(Long expenseUserId) throws Exception {
		try {
			return payrollDao.findAllByExpenseUser_Id(expenseUserId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public List<Payroll> findAllPayrollByEmployeeId(Long id) throws Exception {
		try {
			return payrollDao.findAllByEmployee_Id(id);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public Boolean findByDateAndEmployeeAllIgnoreCase(LocalDateTime date, Employee employee) throws Exception {
		return payrollDao.findByPayrollDateAndEmployeeAllIgnoreCase(date, employee);
	}

	@Override
	public List<Payroll> findAll() throws Exception {
		try {
			return payrollDao.findAll();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	@Override
	       public Payroll findById(Long id) throws Exception {
		       if (id == null) {
			       throw new IllegalArgumentException("id no puede ser nulo");
		       }
		       try {
			       Optional<Payroll> optionalPayroll = payrollDao.findById(id);
			       return optionalPayroll.get();
		       } catch (Exception e) {
			       e.printStackTrace();
			       throw new Exception(e.getMessage());
		       }
	       }

	@Override
	@Transactional
	public Payroll save(Payroll payroll) throws Exception {
		try {
			syncAssociations(payroll);
			LocalDateTime parsedDate = DateUtil.formattingDate(payroll.getPayrollDate());
			payroll.setPayrollDate(parsedDate);
			Payroll savedPayroll = payrollDao.save(payroll);
			// Notification is handled in PayrollControllerImpl
			return savedPayroll;
		} catch (BadRequestException | ResourceNotFoundException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	@Override
	@Transactional
	public Payroll update(Long id, Payroll payroll) throws Exception {
		if (id == null) {
			throw new IllegalArgumentException("id no puede ser nulo");
		}
		Payroll payrollUpdated = null;
		try {
			Optional<Payroll> optionalPayroll = payrollDao.findById(id);
			if (optionalPayroll.isEmpty()) {
				throw new ResourceNotFoundException("Payroll", "id", id);
			}
			// Ensure the path ID is used (not any ID from request body)
			payroll.setId(id);
			syncAssociations(payroll);
			LocalDateTime parsedDate = DateUtil.formattingDate(payroll.getPayrollDate());
			payroll.setPayrollDate(parsedDate);
			payrollUpdated = payrollDao.save(payroll);
		} catch (BadRequestException | ResourceNotFoundException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return payrollUpdated;
	}

	@Override
	@Transactional
	       public Boolean delete(Long id) throws Exception {
		       if (id == null) {
			       throw new IllegalArgumentException("id no puede ser nulo");
		       }
		       boolean isDeleted = false;
		       try {
			       if (payrollDao.existsById(id)) {
				       payrollDao.deleteById(id);
				       isDeleted = true;
			       } else {
				       throw new Exception();
			       }
		       } catch (Exception e) {
			       e.printStackTrace();
			       throw new Exception(e.getMessage());
		       }
		       return isDeleted;
	       }

	private void syncAssociations(Payroll payroll) {
		ExpenseUser expenseUser = payroll.getExpenseUser();
		Employee employee = payroll.getEmployee();

		if (expenseUser != null && expenseUser.getId() != null) {
			ExpenseUser managedExpenseUser = userDao.findById(expenseUser.getId())
					.orElseThrow(() -> new ResourceNotFoundException("ExpenseUser", "id", expenseUser.getId()));
			payroll.setExpenseUser(managedExpenseUser);

			Employee linkedEmployee = managedExpenseUser.getEmployee();
			if (employee == null && linkedEmployee != null) {
				payroll.setEmployee(linkedEmployee);
			} else if (employee != null && linkedEmployee != null
					&& !linkedEmployee.getId().equals(employee.getId())) {
				throw new BadRequestException("employeeId", employee.getId(),
						"Employee is not linked with the provided expense user");
			}
		}

		if (employee != null && employee.getId() != null) {
			Employee managedEmployee = employeeDao.findById(employee.getId())
					.orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employee.getId()));
			payroll.setEmployee(managedEmployee);

			if (payroll.getExpenseUser() == null) {
				userDao.findByEmployee_Id(managedEmployee.getId()).ifPresent(payroll::setExpenseUser);
			}
		}
	}
	
	@Override
	public Page<Payroll> findAllPaginated(Pageable pageable) throws Exception {
		try {
			return payrollDao.findAll(pageable);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
	@Override
	public Page<Payroll> findWithFilters(PayrollSearchCriteria criteria, Pageable pageable, String headerAuth) throws Exception {
		String token = headerAuth.split(" ")[1].trim();
		boolean isAdmin = jwtAuthUtil.isAdminTokenUser(token);
		
		// If not admin, filter by their own userId
		Long effectiveUserId = criteria.getUserId();
		if (!isAdmin) {
			Integer tokenUserId = jwtAuthUtil.extractTokenUserId(token);
			effectiveUserId = tokenUserId != null ? tokenUserId.longValue() : null;
		}
		
		// Parse dates if provided
		LocalDateTime startDate = criteria.getStartDate();
		LocalDateTime endDate = criteria.getEndDate();
		
		// Use Specifications for dynamic query building (avoids PostgreSQL null type inference issues)
		return payrollDao.findAll(
			PayrollSpecifications.withFilters(
				effectiveUserId,
				criteria.getSearch(),
				criteria.getMinAmount(),
				criteria.getMaxAmount(),
				startDate,
				endDate
			),
			pageable
		);
	}
}
