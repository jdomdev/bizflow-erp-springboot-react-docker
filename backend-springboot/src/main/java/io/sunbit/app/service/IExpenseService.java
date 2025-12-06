package io.sunbit.app.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import io.sunbit.app.entity.Expense;
import io.sunbit.app.entity.ExpenseAttachment;
import io.sunbit.app.entity.ExpenseStatus;

public interface IExpenseService {

	public List<Expense> findAll() throws Exception;

	public Expense findById(Long id, String headerAuth) throws Exception;

	public Expense save(Expense expense, String headerAuth) throws Exception;

	public Expense update(Expense expense, String headerAuth) throws Exception;

	public Boolean delete(Long id) throws Exception;

	public List<Expense> findAllByEmployeeId(Long employeeId, String headerAuth) throws Exception;

	public Expense findByAmountAndDateAndConceptAndEmployeeId(Double amount,
			LocalDateTime expenseDate, String concept,
			Long employeeId,
			String headerAuth) throws Exception;
	
	/**
	 * Approve an expense
	 */
	public Expense approve(Long expenseId, String headerAuth) throws Exception;
	
	/**
	 * Reject an expense with a reason
	 */
	public Expense reject(Long expenseId, String reason, String headerAuth) throws Exception;
	
	/**
	 * Find all expenses by status
	 */
	public List<Expense> findByStatus(ExpenseStatus status, String headerAuth) throws Exception;
	
	/**
	 * Upload an attachment for an expense
	 */
	public ExpenseAttachment uploadAttachment(Long expenseId, MultipartFile file, String headerAuth) throws Exception;
	
	/**
	 * Get all attachments for an expense
	 */
	public List<ExpenseAttachment> getAttachments(Long expenseId, String headerAuth) throws Exception;
	
	/**
	 * Delete an attachment
	 */
	public Boolean deleteAttachment(Long attachmentId, String headerAuth) throws Exception;
}

