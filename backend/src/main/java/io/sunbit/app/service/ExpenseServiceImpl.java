package io.sunbit.app.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.sunbit.app.dao.IExpenseDao;
import io.sunbit.app.entity.Expense;
import io.sunbit.app.security.dao.IUserDao;
import io.sunbit.app.security.entity.ExpenseUser;
import io.sunbit.app.security.jwt.JwtAuthenticationUtil;
import io.sunbit.app.util.DateUtil;

@Service
public class ExpenseServiceImpl implements IExpenseService {

	@Autowired
	private IExpenseDao expenseDao;

	@Autowired
	private JwtAuthenticationUtil jwtAuthUtil;

	@Autowired
	private IUserDao userDao;

	public Expense findByAmountAndExpenseDateAndConceptAndExpenseUserId(Double amount,
		LocalDateTime expenseDate,
		String concept,
		Long expenseUserId,
		String headerAuth) throws Exception {
		Optional<Expense> optSearchedExpense = Optional.empty();
		String token = headerAuth.split(" ")[1].trim();
		if (jwtAuthUtil.isAdminTokenUser(token)
			|| isRequestingOwnExpenses(expenseUserId, token)) {
			optSearchedExpense = expenseDao.findByAmountAndExpenseDateAndConceptAndExpenseUserId(amount,
				expenseDate,
				concept,
				expenseUserId);
			// Remove misplaced @Override and method declarations from inside another method.
			// The following methods should be declared at the class level, not nested inside another method.
		}
		if (optSearchedExpense.isPresent()) {
			Expense expense = optSearchedExpense.get();
			loadExpenseUser(expense);
			System.out.println("Searched Expense from ExpenseServiceImpl class\n"
				+ "expenseDao.findByAmountAndExpenseDateAndConceptAndExpenseUserId():\n"
				+ "Concept: " + expense.getConcept());
			return expense;
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
		if (jwtAuthUtil.isAdminTokenUser(token)) {
			ExpenseUser expenseUser = resolveExpenseUser(expense.getExpenseUser());
			expense.setExpenseUser(expenseUser);
			savedExpense = expenseDao.save(expense);
			savedExpense.setExpenseUser(expenseUser);
		}
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
			if (jwtAuthUtil.isAdminTokenUser(token)) {
				Expense expense = optExpense.get();
				loadExpenseUser(expense);
				return expense;
			}
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
				ExpenseUser expenseUser = resolveExpenseUser(expense.getExpenseUser());
				expense.setExpenseUser(expenseUser);
				expenseUpdated = expenseDao.save(expense);
				expenseUpdated.setExpenseUser(expenseUser);
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
	public Expense findByAmountAndDateAndConceptAndExpenseUserId(Double amount, LocalDateTime expenseDate, String concept, Long expenseUserId, String headerAuth) throws Exception {
		Optional<Expense> optSearchedExpense = Optional.empty();
		String token = headerAuth.split(" ")[1].trim();
		if (jwtAuthUtil.isAdminTokenUser(token)
			|| isRequestingOwnExpenses(expenseUserId, token)) {
			optSearchedExpense = expenseDao.findByAmountAndExpenseDateAndConceptAndExpenseUserId(amount,
				expenseDate,
				concept,
				expenseUserId);
		}
		if (optSearchedExpense.isPresent()) {
			Expense expense = optSearchedExpense.get();
			loadExpenseUser(expense);
			return expense;
		} else {
			throw new Exception("Expense not found");
		}
	}

	@Override
	public List<Expense> findAllByExpenseUserId(Long expenseUserId, String headerAuth) throws Exception {
		String token = headerAuth.split(" ")[1].trim();
		if (jwtAuthUtil.isAdminTokenUser(token)
			|| isRequestingOwnExpenses(expenseUserId, token)) {
			List<Expense> expenses = expenseDao.findAllByExpenseUserId(expenseUserId);
			expenses.forEach(this::loadExpenseUser);
			return expenses;
		} else {
			throw new Exception("Unauthorized access");
		}
	}

	private boolean isRequestingOwnExpenses(Long expenseUserId, String token) {
		if (expenseUserId == null || token == null || token.isBlank()) {
			return false;
		}
		Integer tokenUserId = jwtAuthUtil.extractTokenUserId(token);
		return tokenUserId != null && expenseUserId.equals(tokenUserId.longValue());
	}

	private ExpenseUser resolveExpenseUser(ExpenseUser expenseUser) {
		if (expenseUser == null || expenseUser.getId() == null) {
			throw new IllegalArgumentException("Expense user id must not be null");
		}
		return userDao.findById(expenseUser.getId())
			.orElseThrow(() -> new IllegalArgumentException("Expense user not found"));
	}

	private void loadExpenseUser(Expense expense) {
		if (expense == null) {
			return;
		}
		ExpenseUser relatedUser = expense.getExpenseUser();
		if (relatedUser != null && relatedUser.getId() != null) {
			if (relatedUser.getEmail() != null && relatedUser.getName() != null) {
				return;
			}
			expense.setExpenseUser(resolveExpenseUser(relatedUser));
		}
	}
}
