package io.sunbit.app.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.sunbit.app.dao.ExpenseSpecifications;
import io.sunbit.app.dao.IExpenseDao;
import io.sunbit.app.dto.ExpenseSearchCriteria;
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
		
		// Allow admin to save any expense, or user to save their own expense
		Long expenseUserId = expense.getExpenseUser() != null ? expense.getExpenseUser().getId() : null;
		boolean isAdmin = jwtAuthUtil.isAdminTokenUser(token);
		boolean isOwnExpense = isRequestingOwnExpenses(expenseUserId, token);
		
		if (isAdmin || isOwnExpense) {
			ExpenseUser expenseUser = resolveExpenseUser(expense.getExpenseUser());
			expense.setExpenseUser(expenseUser);
			savedExpense = expenseDao.save(expense);
			savedExpense.setExpenseUser(expenseUser);
		} else {
			throw new SecurityException("User is not authorized to create this expense");
		}
		return savedExpense;
	}

	@Override
	@Transactional
	public List<Expense> findAll() throws Exception {
		try {
			List<Expense> expenses = expenseDao.findAll();
			expenses.forEach(this::loadExpenseUser);
			return expenses;
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
		Optional<Expense> optExpense = expenseDao.findById(expenseId);
		
		if (optExpense.isPresent()) {
			Expense existingExpense = optExpense.get();
			Long existingOwnerId = existingExpense.getExpenseUser().getId();
			
			// Allow update if user is admin OR if user is the owner of the expense
			boolean isAdmin = jwtAuthUtil.isAdminTokenUser(token);
			boolean isOwner = isRequestingOwnExpenses(existingOwnerId, token);
			
			if (isAdmin || isOwner) {
				LocalDateTime parsedDate = DateUtil.formattingDate(expense.getExpenseDate());
				expense.setExpenseDate(parsedDate);
				
				// If expenseUser is not provided in the request, use the existing one
				ExpenseUser expenseUser;
				if (expense.getExpenseUser() != null && expense.getExpenseUser().getId() != null) {
					expenseUser = resolveExpenseUser(expense.getExpenseUser());
				} else {
					expenseUser = existingExpense.getExpenseUser();
				}
				expense.setExpenseUser(expenseUser);
				
				expenseUpdated = expenseDao.save(expense);
				expenseUpdated.setExpenseUser(expenseUser);
			} else {
				throw new SecurityException("User is not authorized to update this expense");
			}
		} else {
			throw new IllegalArgumentException("Expense not found with id: " + expenseId);
		}
		return expenseUpdated;
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
	
	@Override
	public Page<Expense> findAllPaginated(Pageable pageable) throws Exception {
		try {
			Page<Expense> expenses = expenseDao.findAll(pageable);
			expenses.forEach(this::loadExpenseUser);
			return expenses;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
	@Override
	public Page<Expense> findWithFilters(ExpenseSearchCriteria criteria, Pageable pageable, String headerAuth) throws Exception {
		String token = headerAuth.split(" ")[1].trim();
		boolean isAdmin = jwtAuthUtil.isAdminTokenUser(token);
		boolean isManager = jwtAuthUtil.isManagerTokenUser(token);
		
		// If not admin or manager, filter by their own userId
		Long effectiveUserId = criteria.getUserId();
		if (!isAdmin && !isManager) {
			Integer tokenUserId = jwtAuthUtil.extractTokenUserId(token);
			effectiveUserId = tokenUserId != null ? tokenUserId.longValue() : null;
		}
		
		// Parse dates if provided
		LocalDateTime startDate = criteria.getStartDate() != null ? DateUtil.formattingDate(criteria.getStartDate()) : null;
		LocalDateTime endDate = criteria.getEndDate() != null ? DateUtil.formattingDate(criteria.getEndDate()) : null;
		
		// Use Specifications for dynamic query building (avoids PostgreSQL null type inference issues)
		Page<Expense> expenses = expenseDao.findAll(
			ExpenseSpecifications.withFilters(
				effectiveUserId,
				criteria.getSearch(),
				criteria.getMinAmount(),
				criteria.getMaxAmount(),
				startDate,
				endDate
			),
			pageable
		);
		
		expenses.forEach(this::loadExpenseUser);
		return expenses;
	}
}
