package io.sunbit.app.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.sunbit.app.dao.IEmployeeDao;
import io.sunbit.app.dao.IExpenseAttachmentDao;
import io.sunbit.app.dao.IExpenseDao;
import io.sunbit.app.entity.Employee;
import io.sunbit.app.entity.Expense;
import io.sunbit.app.entity.ExpenseAttachment;
import io.sunbit.app.entity.ExpenseStatus;
import io.sunbit.app.security.jwt.JwtAuthenticationUtil;
import io.sunbit.app.util.DateUtil;
import io.sunbit.app.util.EmployeeUtil;

@Service
public class ExpenseServiceImpl implements IExpenseService {

	private static final Logger log = LoggerFactory.getLogger(ExpenseServiceImpl.class);

	@Autowired
	private IEmployeeDao employeeDao;
	@Autowired
	private IExpenseDao expenseDao;
	@Autowired
	private IExpenseAttachmentDao attachmentDao;
	@Autowired
	private JwtAuthenticationUtil jwtAuthUtil;
	@Autowired
	private EmployeeUtil employeeUtil;
	
	@Value("${expense.upload.dir:uploads/expenses}")
	private String uploadDir;

	@Override
	public List<Expense> findAllByEmployeeId(Long employeeId, String headerAuth) throws Exception {
		try {
			String token = headerAuth.split(" ")[1].trim();
			Optional<Employee> optEmployee = employeeDao.findById(employeeId);
			if (optEmployee.isEmpty()) {
				throw new Exception("Employee not found with ID: " + employeeId);
			}
			
			if (jwtAuthUtil.isAdminTokenUser(token)
					|| employeeUtil.matchEmployeeUserEmail(optEmployee.get(), token)) {
				return expenseDao.findAllByEmployeeId(employeeId);
			}
			throw new SecurityException("Unauthorized access to employee expenses");
		} catch (SecurityException e) {
			log.error("Security violation when accessing expenses for employee {}: {}", employeeId, e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("Error finding expenses by employee ID {}: {}", employeeId, e.getMessage());
			throw new Exception("Error finding expenses: " + e.getMessage());
		}
	}

	@Override
	public Expense findByAmountAndDateAndConceptAndEmployeeId(Double amount,
			LocalDateTime date,
			String concept,
			Long employeeId,
			String headerAuth) throws Exception {
		try {
			String token = headerAuth.split(" ")[1].trim();
			Optional<Employee> optEmployee = employeeDao.findById(employeeId);
			if (optEmployee.isEmpty()) {
				throw new Exception("Employee not found with ID: " + employeeId);
			}
			
			if (jwtAuthUtil.isAdminTokenUser(token)
					|| employeeUtil.matchEmployeeUserEmail(optEmployee.get(), token)) {
				Optional<Expense> optSearchedExpense = expenseDao.findByAmountAndDateAndConceptAndEmployeeId(amount,
						date,
						concept,
						employeeId);
				return optSearchedExpense.orElseThrow(() -> new Exception("Expense not found"));
			}
			throw new SecurityException("Unauthorized access to expense");
		} catch (Exception e) {
			log.error("Error searching expense: {}", e.getMessage());
			throw e;
		}
	}

	@Override
	@Transactional
	public Expense save(Expense expense, String headerAuth) throws Exception {
		try {
			LocalDateTime parsedDate = DateUtil.formattingDate(expense.getDate());
			expense.setDate(parsedDate);
			
			String token = headerAuth.split(" ")[1].trim();
			if (jwtAuthUtil.isAdminTokenUser(token) || employeeUtil.matchEmployeeUserEmail(expense.getEmployee(), token)) {
				// Set default status if not set
				if (expense.getStatus() == null) {
					expense.setStatus(ExpenseStatus.PENDING);
				}
				Expense savedExpense = expenseDao.save(expense);
				log.info("Expense saved successfully with ID: {}", savedExpense.getId());
				return savedExpense;
			}
			throw new SecurityException("Unauthorized to save expense");
		} catch (Exception e) {
			log.error("Error saving expense: {}", e.getMessage());
			throw e;
		}
	}

	@Override
	public List<Expense> findAll() throws Exception {
		try {
			return expenseDao.findAll();
		} catch (Exception e) {
			log.error("Error finding all expenses: {}", e.getMessage());
			throw new Exception("Error finding all expenses: " + e.getMessage());
		}
	}

	@Override
	public Expense findById(Long id, String headerAuth) throws Exception {
		try {
			String token = headerAuth.split(" ")[1].trim();
			Optional<Expense> optExpense = expenseDao.findById(id);
			if (optExpense.isEmpty()) {
				throw new Exception("Expense not found with ID: " + id);
			}
			
			Expense expense = optExpense.get();
			Optional<Employee> optEmployee = employeeDao.findById(expense.getEmployee().getId());
			if (optEmployee.isEmpty()) {
				throw new Exception("Employee not found for expense");
			}
			
			if (jwtAuthUtil.isAdminTokenUser(token) || 
				employeeUtil.matchEmployeeUserEmail(optEmployee.get(), token)) {
				return expense;
			}
			throw new SecurityException("Unauthorized access to expense");
		} catch (Exception e) {
			log.error("Error finding expense by ID {}: {}", id, e.getMessage());
			throw e;
		}
	}

	@Override
	@Transactional
	public Expense update(Expense expense, String headerAuth) throws Exception {
		try {
			String token = headerAuth.split(" ")[1].trim();
			Optional<Expense> optExpense = expenseDao.findById(expense.getId());
			if (optExpense.isEmpty()) {
				throw new Exception("Expense not found with ID: " + expense.getId());
			}
			
			Expense existingExpense = optExpense.get();
			Optional<Employee> optEmployee = employeeDao.findById(existingExpense.getEmployee().getId());
			if (optEmployee.isEmpty()) {
				throw new Exception("Employee not found for expense");
			}
			
			if (jwtAuthUtil.isAdminTokenUser(token) || 
				employeeUtil.matchEmployeeUserEmail(optEmployee.get(), token)) {
				
				// Don't allow updating approved/rejected expenses
				if (existingExpense.isApproved() || existingExpense.isRejected()) {
					throw new IllegalStateException("Cannot update an expense that has been approved or rejected");
				}
				
				LocalDateTime parsedDate = DateUtil.formattingDate(expense.getDate());
				expense.setDate(parsedDate);
				Expense expenseUpdated = expenseDao.save(expense);
				log.info("Expense updated successfully with ID: {}", expenseUpdated.getId());
				return expenseUpdated;
			}
			throw new SecurityException("Unauthorized to update expense");
		} catch (Exception e) {
			log.error("Error updating expense: {}", e.getMessage());
			throw e;
		}
	}

	@Override
	@Transactional
	public Boolean delete(Long id) throws Exception {
		try {
			if (expenseDao.existsById(id)) {
				expenseDao.deleteById(id);
				log.info("Expense deleted successfully with ID: {}", id);
				return true;
			}
			throw new Exception("Expense not found with ID: " + id);
		} catch (Exception e) {
			log.error("Error deleting expense {}: {}", id, e.getMessage());
			throw new Exception("Error deleting expense: " + e.getMessage());
		}
	}
	
	@Override
	@Transactional
	public Expense approve(Long expenseId, String headerAuth) throws Exception {
		try {
			String token = headerAuth.split(" ")[1].trim();
			
			// Only admins can approve
			if (!jwtAuthUtil.isAdminTokenUser(token)) {
				throw new SecurityException("Only administrators can approve expenses");
			}
			
			Optional<Expense> optExpense = expenseDao.findById(expenseId);
			if (optExpense.isEmpty()) {
				throw new Exception("Expense not found with ID: " + expenseId);
			}
			
			Expense expense = optExpense.get();
			
			if (!expense.isPending()) {
				throw new IllegalStateException("Only pending expenses can be approved");
			}
			
			String approverEmail = jwtAuthUtil.getEmailFromToken(token);
			expense.approve(approverEmail);
			Expense approvedExpense = expenseDao.save(expense);
			log.info("Expense {} approved by {}", expenseId, approverEmail);
			return approvedExpense;
		} catch (Exception e) {
			log.error("Error approving expense {}: {}", expenseId, e.getMessage());
			throw e;
		}
	}
	
	@Override
	@Transactional
	public Expense reject(Long expenseId, String reason, String headerAuth) throws Exception {
		try {
			String token = headerAuth.split(" ")[1].trim();
			
			// Only admins can reject
			if (!jwtAuthUtil.isAdminTokenUser(token)) {
				throw new SecurityException("Only administrators can reject expenses");
			}
			
			if (reason == null || reason.trim().isEmpty()) {
				throw new IllegalArgumentException("Rejection reason is required");
			}
			
			Optional<Expense> optExpense = expenseDao.findById(expenseId);
			if (optExpense.isEmpty()) {
				throw new Exception("Expense not found with ID: " + expenseId);
			}
			
			Expense expense = optExpense.get();
			
			if (!expense.isPending()) {
				throw new IllegalStateException("Only pending expenses can be rejected");
			}
			
			String approverEmail = jwtAuthUtil.getEmailFromToken(token);
			expense.reject(approverEmail, reason);
			Expense rejectedExpense = expenseDao.save(expense);
			log.info("Expense {} rejected by {}", expenseId, approverEmail);
			return rejectedExpense;
		} catch (Exception e) {
			log.error("Error rejecting expense {}: {}", expenseId, e.getMessage());
			throw e;
		}
	}
	
	@Override
	public List<Expense> findByStatus(ExpenseStatus status, String headerAuth) throws Exception {
		try {
			String token = headerAuth.split(" ")[1].trim();
			
			// Only admins can view all expenses by status
			if (!jwtAuthUtil.isAdminTokenUser(token)) {
				throw new SecurityException("Only administrators can view expenses by status");
			}
			
			return expenseDao.findByStatus(status);
		} catch (Exception e) {
			log.error("Error finding expenses by status {}: {}", status, e.getMessage());
			throw e;
		}
	}
	
	@Override
	@Transactional
	public ExpenseAttachment uploadAttachment(Long expenseId, MultipartFile file, String headerAuth) throws Exception {
		try {
			String token = headerAuth.split(" ")[1].trim();
			
			Optional<Expense> optExpense = expenseDao.findById(expenseId);
			if (optExpense.isEmpty()) {
				throw new Exception("Expense not found with ID: " + expenseId);
			}
			
			Expense expense = optExpense.get();
			
			// Check authorization
			if (!jwtAuthUtil.isAdminTokenUser(token) && 
				!employeeUtil.matchEmployeeUserEmail(expense.getEmployee(), token)) {
				throw new SecurityException("Unauthorized to upload attachment");
			}
			
			// Validate file
			if (file.isEmpty()) {
				throw new IllegalArgumentException("File is empty");
			}
			
			// Create upload directory if it doesn't exist
			Path uploadPath = Paths.get(uploadDir);
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}
			
			// Generate unique file name
			String originalFilename = file.getOriginalFilename();
			String fileExtension = originalFilename != null && originalFilename.contains(".") 
				? originalFilename.substring(originalFilename.lastIndexOf(".")) 
				: "";
			String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
			Path filePath = uploadPath.resolve(uniqueFileName);
			
			// Save file
			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
			
			// Create attachment record
			ExpenseAttachment attachment = new ExpenseAttachment();
			attachment.setFileName(originalFilename);
			attachment.setFilePath(filePath.toString());
			attachment.setFileType(file.getContentType());
			attachment.setFileSize(file.getSize());
			attachment.setExpense(expense);
			
			ExpenseAttachment savedAttachment = attachmentDao.save(attachment);
			log.info("Attachment uploaded for expense {}: {}", expenseId, originalFilename);
			return savedAttachment;
			
		} catch (IOException e) {
			log.error("Error uploading file for expense {}: {}", expenseId, e.getMessage());
			throw new Exception("Error uploading file: " + e.getMessage());
		} catch (Exception e) {
			log.error("Error uploading attachment for expense {}: {}", expenseId, e.getMessage());
			throw e;
		}
	}
	
	@Override
	public List<ExpenseAttachment> getAttachments(Long expenseId, String headerAuth) throws Exception {
		try {
			String token = headerAuth.split(" ")[1].trim();
			
			Optional<Expense> optExpense = expenseDao.findById(expenseId);
			if (optExpense.isEmpty()) {
				throw new Exception("Expense not found with ID: " + expenseId);
			}
			
			Expense expense = optExpense.get();
			
			// Check authorization
			if (!jwtAuthUtil.isAdminTokenUser(token) && 
				!employeeUtil.matchEmployeeUserEmail(expense.getEmployee(), token)) {
				throw new SecurityException("Unauthorized to view attachments");
			}
			
			return attachmentDao.findByExpenseId(expenseId);
		} catch (Exception e) {
			log.error("Error getting attachments for expense {}: {}", expenseId, e.getMessage());
			throw e;
		}
	}
	
	@Override
	@Transactional
	public Boolean deleteAttachment(Long attachmentId, String headerAuth) throws Exception {
		try {
			String token = headerAuth.split(" ")[1].trim();
			
			Optional<ExpenseAttachment> optAttachment = attachmentDao.findById(attachmentId);
			if (optAttachment.isEmpty()) {
				throw new Exception("Attachment not found with ID: " + attachmentId);
			}
			
			ExpenseAttachment attachment = optAttachment.get();
			Expense expense = attachment.getExpense();
			
			// Check authorization
			if (!jwtAuthUtil.isAdminTokenUser(token) && 
				!employeeUtil.matchEmployeeUserEmail(expense.getEmployee(), token)) {
				throw new SecurityException("Unauthorized to delete attachment");
			}
			
			// Delete physical file
			try {
				Path filePath = Paths.get(attachment.getFilePath());
				Files.deleteIfExists(filePath);
			} catch (IOException e) {
				log.warn("Could not delete physical file: {}", attachment.getFilePath());
			}
			
			// Delete database record
			attachmentDao.deleteById(attachmentId);
			log.info("Attachment deleted: {}", attachmentId);
			return true;
			
		} catch (Exception e) {
			log.error("Error deleting attachment {}: {}", attachmentId, e.getMessage());
			throw e;
		}
	}
}
