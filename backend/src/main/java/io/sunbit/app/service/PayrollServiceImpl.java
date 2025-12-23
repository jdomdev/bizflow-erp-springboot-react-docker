package io.sunbit.app.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.sunbit.app.dao.IEmployeeDao;
import io.sunbit.app.dao.IPayrollDao;
import io.sunbit.app.entity.Employee;
import io.sunbit.app.entity.Payroll;
import io.sunbit.app.exception.BadRequestException;
import io.sunbit.app.exception.ResourceNotFoundException;
import io.sunbit.app.security.dao.IUserDao;
import io.sunbit.app.security.entity.ExpenseUser;
import io.sunbit.app.util.DateUtil;

@Service
public class PayrollServiceImpl implements IPayrollService {
	@Override
	public List<Payroll> findAllPayrollByExpenseUserId(Long expenseUserId) throws Exception {
		try {
			return payrollDao.findAllByExpenseUser_Id(expenseUserId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	@Autowired
	private IPayrollDao payrollDao;

	@Autowired
	private IEmployeeDao employeeDao;

	@Autowired
	private IUserDao userDao;

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
			return payrollDao.save(payroll);
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
			Payroll oldPayroll = optionalPayroll.get();
			if (oldPayroll != null) {
				syncAssociations(payroll);
				LocalDateTime parsedDate = DateUtil.formattingDate(payroll.getPayrollDate());
				payroll.setPayrollDate(parsedDate);
				payrollUpdated = payrollDao.save(payroll);
			}
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
}
