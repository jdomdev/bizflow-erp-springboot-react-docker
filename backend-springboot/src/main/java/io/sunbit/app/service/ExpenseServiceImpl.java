package io.sunbit.app.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.sunbit.app.dao.IEmployeeDao;
import io.sunbit.app.dao.IExpenseDao;
import io.sunbit.app.entity.Expense;
import io.sunbit.app.security.jwt.JwtAuthenticationUtil;
import io.sunbit.app.util.DateUtil;
import io.sunbit.app.util.EmployeeUtil;

@Service
public class ExpenseServiceImpl implements IExpenseService {

	@Autowired
	private IEmployeeDao employeeDao;
	@Autowired
	private IExpenseDao expenseDao;
	@Autowired
	private JwtAuthenticationUtil jwtAuthUtil;
	@Autowired
	private static EmployeeUtil employeeUtil;

	@Override
	public List<Expense> findAllByEmployeeId(Long employeeId, String headerAuth) throws Exception {
		List<Expense> expenses = null;
		try {
			String token = headerAuth.split(" ")[1].trim();
			if (jwtAuthUtil.isAdminTokenUser(token)
					|| employeeUtil.matchEmployeeUserEmail(employeeDao.findById(employeeId).get(), token))
				expenses = expenseDao.findAllByEmployeeId(employeeId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return expenses;
	}

	@Override
	public Expense findByAmountAndDateAndConceptAndEmployeeId(Double amount,
			LocalDateTime date,
			String concept,
			Long employeeId,
			String headerAuth) throws Exception {
		Optional<Expense> optSearchedExpense = null;
		String token = headerAuth.split(" ")[1].trim();
		if (jwtAuthUtil.isAdminTokenUser(token)
				|| employeeUtil.matchEmployeeUserEmail(employeeDao.findById(employeeId).get(), token)) {
			optSearchedExpense = expenseDao.findByAmountAndDateAndConceptAndEmployeeId(amount,
					date,
					concept,
					employeeId);
			// Test
			System.out.println("Searched Expense from ExpenseServiceImpl class\n"
					+ "expenseDao.findByAmountAndExpenseDateAndConceptAndEmployeeIdAllIgnoreCase():\n"
					+ "Concept: " + optSearchedExpense.get().getConcept());
		}
		return optSearchedExpense.get();
	}

	@Override
	@Transactional
	public Expense save(Expense expense, String headerAuth) throws Exception {
		Expense savedExpense = new Expense();
		LocalDateTime parsedDate = DateUtil.formattingDate(expense.getDate());
		expense.setDate(parsedDate);
		String token = headerAuth.split(" ")[1].trim();
		if (jwtAuthUtil.isAdminTokenUser(token) || employeeUtil.matchEmployeeUserEmail(expense.getEmployee(), token))
			savedExpense = expenseDao.save(expense);
		return savedExpense;
	}

	@Override
	public List<Expense> findAll() throws Exception {
		try {
			return expenseDao.findAll();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public Expense findById(Long id, String headerAuth) throws Exception {
		String token = headerAuth.split(" ")[1].trim();
		Optional<Expense> optExpense = expenseDao.findById(id);
		if (optExpense.isPresent()) {
			if (jwtAuthUtil.isAdminTokenUser(token) || employeeUtil
					.matchEmployeeUserEmail(employeeDao.findById(optExpense.get().getEmployee().getId()).get(), token))
				return optExpense.get();
		}
		return optExpense.get();
	}

	@Override
	@Transactional
	public Expense update(Expense expense, String headerAuth) throws Exception {
		Expense expenseUpdated = null;
		String token = headerAuth.split(" ")[1].trim();
		Optional<Expense> optExpense = expenseDao.findById(expense.getId());
		if (optExpense.isPresent()) {
			if (jwtAuthUtil.isAdminTokenUser(token) || employeeUtil.matchEmployeeUserEmail(
					employeeDao.findById(optExpense.get().getEmployee().getId()).get(), token)) {
				LocalDateTime parsedDate = DateUtil.formattingDate(expense.getDate());
				expense.setDate(parsedDate);
				expenseUpdated = expenseDao.save(expense);
			}
		}
		return expenseUpdated;
	}

	@Override
	@Transactional
	public Boolean delete(Long id) throws Exception {
		boolean isDeleted = false;
		try {
			if (expenseDao.existsById(id)) {
				expenseDao.deleteById(id);
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
