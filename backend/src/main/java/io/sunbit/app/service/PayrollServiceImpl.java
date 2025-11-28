package io.sunbit.app.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.sunbit.app.dao.IPayrollDao;
import io.sunbit.app.entity.Employee;
import io.sunbit.app.entity.Payroll;
import io.sunbit.app.util.DateUtil;

@Service
public class PayrollServiceImpl implements IPayrollService {

	@Autowired
	private IPayrollDao payrollDao;

	@Override
	public List<Payroll> findAllPayrollByEmployeeId(Long id) throws Exception {
		try {
			// return payrollDao.findAllPayrollByEmployeeId(employeeId);
			return payrollDao.findAllByEmployeeId(id);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public Boolean findByDateAndEmployeeAllIgnoreCase(LocalDateTime date, Employee employee) throws Exception {
		return payrollDao.findByDateAndEmployeeAllIgnoreCase(date, employee);
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
			LocalDateTime parsedDate = DateUtil.formattingDate(payroll.getDate());
			payroll.setDate(parsedDate);
			return payrollDao.save(payroll);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	@Override
	@Transactional
	public Payroll update(Long id, Payroll payroll) throws Exception {
		Payroll payrollUpdated = null;
		try {
			Optional<Payroll> optionalPayroll = payrollDao.findById(id);
			Payroll oldPayroll = optionalPayroll.get();
			if (oldPayroll != null) {
				LocalDateTime parsedDate = DateUtil.formattingDate(payroll.getDate());
				payroll.setDate(parsedDate);
				payrollUpdated = payrollDao.save(payroll);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return payrollUpdated;
	}

	@Override
	@Transactional
	public Boolean delete(Long id) throws Exception {
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
}
