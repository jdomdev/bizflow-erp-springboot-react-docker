package io.sunbit.app.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.sunbit.app.entity.ExpenseAttachment;

@Repository
public interface IExpenseAttachmentDao extends JpaRepository<ExpenseAttachment, Long> {
	
	/**
	 * Find all attachments for a specific expense
	 */
	List<ExpenseAttachment> findByExpenseId(Long expenseId);
}
