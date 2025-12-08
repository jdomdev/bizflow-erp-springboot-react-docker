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
	private IExpenseDao expenseDao;

	@Autowired
	private IEmployeeDao employeeDao;

	@Autowired
	private JwtAuthenticationUtil jwtAuthUtil;

	@Autowired
	private EmployeeUtil employeeUtil;

	public Expense findByAmountAndExpenseDateAndConceptAndEmployeeId(Double amount,
		LocalDateTime expenseDate,
		String concept,
		Long employeeId,
		String headerAuth) throws Exception {
		Optional<Expense> optSearchedExpense = Optional.empty();
		String token = headerAuth.split(" ")[1].trim();
		if (jwtAuthUtil.isAdminTokenUser(token)
			|| (employeeId != null && employeeUtil.matchEmployeeUserEmail(employeeDao.findById(employeeId).get(), token))) {
			optSearchedExpense = expenseDao.findByAmountAndExpenseDateAndConceptAndExpenseUserId(amount,
				expenseDate,
				concept,
				employeeId);
			// Remove misplaced @Override and method declarations from inside another method.
			// The following methods should be declared at the class level, not nested inside another method.
		}
		if (optSearchedExpense.isPresent()) {
			System.out.println("Searched Expense from ExpenseServiceImpl class\n"
				+ "expenseDao.findByAmountAndExpenseDateAndConceptAndEmployeeIdAllIgnoreCase():\n"
				+ "Concept: " + optSearchedExpense.get().getConcept());
			return optSearchedExpense.get();
		} else {
			throw new Exception("Expense not found");
		}
	}

	@Override
	@Transactional
	public Expense save(Expense expense, String headerAuth) throws Exception {
		Expense savedExpense = new Expense();
		LocalDateTime parsedDate = DateUtil.formattingDate(expense.getExpenseDate());
		expense.setExpenseDate(parsedDate);
		String token = headerAuth.split(" ")[1].trim();
		// Only admin can save expenses, or add custom user validation here if needed
		if (jwtAuthUtil.isAdminTokenUser(token))
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
		if (id == null) {
			throw new IllegalArgumentException("Expense id must not be null");
		}
		String token = headerAuth.split(" ")[1].trim();
		Optional<Expense> optExpense = expenseDao.findById(id);
		if (optExpense.isPresent()) {
			// Only admin can view any expense, or add custom user validation here if needed
			if (jwtAuthUtil.isAdminTokenUser(token))
				return optExpense.get();
		}
		return optExpense.orElse(null);
	}

	@Override
	@Transactional
	public Expense update(Expense expense, String headerAuth) throws Exception {
		Expense expenseUpdated = null;
		String token = headerAuth.split(" ")[1].trim();
		if (expense.getId() == null) {
			throw new IllegalArgumentException("Expense id must not be null");
		}
		Long expenseId = expense.getId();
		if (expenseId != null) {
			Optional<Expense> optExpense = expenseDao.findById(expenseId);
		if (optExpense.isPresent()) {
			// Only admin can update expenses, or add custom user validation here if needed
			if (jwtAuthUtil.isAdminTokenUser(token)) {
				LocalDateTime parsedDate = DateUtil.formattingDate(expense.getExpenseDate());
				expense.setExpenseDate(parsedDate);
				expenseUpdated = expenseDao.save(expense);
			}
		}
		return expenseUpdated;
		}	throw new IllegalArgumentException("Expense id must not be null");
	}

	@Override
	@Transactional
	public Boolean delete(Long id) throws Exception {
		boolean isDeleted = false;
		try {
			if (id != null && expenseDao.existsById(id)) {
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

	@Override
	public Expense findByAmountAndDateAndConceptAndEmployeeId(Double amount, LocalDateTime expenseDate, String concept, Long employeeId, String headerAuth) throws Exception {
		// You may reuse the logic from findByAmountAndExpenseDateAndConceptAndEmployeeId if appropriate
		Optional<Expense> optSearchedExpense = Optional.empty();
		String token = headerAuth.split(" ")[1].trim();
		if (jwtAuthUtil.isAdminTokenUser(token)
			|| (employeeId != null && employeeUtil.matchEmployeeUserEmail(employeeDao.findById(employeeId).get(), token))) {
			optSearchedExpense = expenseDao.findByAmountAndExpenseDateAndConceptAndExpenseUserId(amount,
				expenseDate,
				concept,
				employeeId);
		}
		if (optSearchedExpense.isPresent()) {
			return optSearchedExpense.get();
		} else {
			throw new Exception("Expense not found");
		}
	}

	@Override
	public List<Expense> findAllByEmployeeId(Long employeeId, String headerAuth) throws Exception {
		String token = headerAuth.split(" ")[1].trim();
		if (jwtAuthUtil.isAdminTokenUser(token)
			|| (employeeId != null && employeeUtil.matchEmployeeUserEmail(employeeDao.findById(employeeId).get(), token))) {
			return expenseDao.findAllByEmployeeId(employeeId);
		} else {
			throw new Exception("Unauthorized access");
		}
	}
}
