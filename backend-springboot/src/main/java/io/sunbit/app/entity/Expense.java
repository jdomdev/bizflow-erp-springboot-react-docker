package io.sunbit.app.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "expense")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
// @Audited
@RequiredArgsConstructor
@ToString(exclude = {"employee", "attachments"})
public class Expense implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "concept", nullable = false)
	@Length(min = 3, max = 128, message = "Concept must be between 3 and 128 characters")
	@NonNull
	private String concept;
	
	@Column(name = "note", nullable = true)
	@Length(max = 500, message = "Note cannot exceed 500 characters")
	private String note;
	
	@Column(name = "expense_date", nullable = false)
	@NotNull(message = "Expense date is required")
	@PastOrPresent(message = "Expense date cannot be in the future")
	@NonNull
	private LocalDateTime date;
	
	@Column(name = "amount", nullable = false)
	@NotNull(message = "Amount is required")
	@DecimalMin(value = "0.01", message = "Amount must be greater than 0")
	@NonNull
	private Double amount;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private ExpenseStatus status = ExpenseStatus.PENDING;
	
	@Column(name = "approved_by")
	private String approvedBy;
	
	@Column(name = "approval_date")
	private LocalDateTime approvalDate;
	
	@Column(name = "rejection_reason", length = 500)
	private String rejectionReason;
	
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id", nullable = false)
	@NonNull
	private Employee employee;
	
	@OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<ExpenseAttachment> attachments = new ArrayList<>();

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
		if (status == null) {
			status = ExpenseStatus.PENDING;
		}
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	// Constructor without note.
	public Expense(Long id, String concept, LocalDateTime date, Double amount, Employee employee) {
		this.id = id;
		this.concept = concept;
		this.date = date;
		this.amount = amount;
		this.employee = employee;
		this.status = ExpenseStatus.PENDING;
	}
	
	// Constructor with note, without id
	public Expense(String concept, String note, LocalDateTime date, Double amount, Employee employee) {
		this.concept = concept;
		this.note = note;
		this.date = date;
		this.amount = amount;
		this.employee = employee;
		this.status = ExpenseStatus.PENDING;
	}
	
	// Constructor with note and id
	public Expense(Long id, String concept, String note, LocalDateTime date, Double amount, Employee employee) {
		this.id = id;
		this.concept = concept;
		this.note = note;
		this.date = date;
		this.amount = amount;
		this.employee = employee;
		this.status = ExpenseStatus.PENDING;
	}
	
	// Helper methods for attachments
	public void addAttachment(ExpenseAttachment attachment) {
		attachments.add(attachment);
		attachment.setExpense(this);
	}
	
	public void removeAttachment(ExpenseAttachment attachment) {
		attachments.remove(attachment);
		attachment.setExpense(null);
	}
	
	// Helper methods for approval workflow
	public void approve(String approverEmail) {
		this.status = ExpenseStatus.APPROVED;
		this.approvedBy = approverEmail;
		this.approvalDate = LocalDateTime.now();
		this.rejectionReason = null;
	}
	
	public void reject(String approverEmail, String reason) {
		this.status = ExpenseStatus.REJECTED;
		this.approvedBy = approverEmail;
		this.approvalDate = LocalDateTime.now();
		this.rejectionReason = reason;
	}
	
	public boolean isPending() {
		return ExpenseStatus.PENDING.equals(this.status);
	}
	
	public boolean isApproved() {
		return ExpenseStatus.APPROVED.equals(this.status);
	}
	
	public boolean isRejected() {
		return ExpenseStatus.REJECTED.equals(this.status);
	}
}
