package io.sunbit.app.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.sunbit.app.security.entity.ExpenseUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.hibernate.validator.constraints.Length;

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
@ToString
public class Expense implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "concept", nullable = false)
	@Length(min = 3, max = 128)
	@NonNull
	private String concept;
	@Column(name = "note", nullable = false)
	@Length(min = 3, max = 255)
	// @NonNull
	private String note;
	@Column(name = "expense_date", nullable = false)
	// @Temporal(TemporalType.TIMESTAMP)
	@NonNull
	private LocalDateTime expenseDate;
	@Column(name = "amount", nullable = false)
	@NonNull
	private Double amount;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "expense_user_id", nullable = false)
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	@NonNull
	private ExpenseUser expenseUser;

	// Constructor without note.

	// Existing constructors...

	// getters and setters...
}
