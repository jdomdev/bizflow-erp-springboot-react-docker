package io.sunbit.app.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "expense_attachment")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "expense")
public class ExpenseAttachment implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "file_name", nullable = false, length = 255)
	@NotNull(message = "File name is required")
	@Length(max = 255, message = "File name cannot exceed 255 characters")
	private String fileName;
	
	@Column(name = "file_path", nullable = false, length = 500)
	@NotNull(message = "File path is required")
	@Length(max = 500, message = "File path cannot exceed 500 characters")
	private String filePath;
	
	@Column(name = "file_type", length = 100)
	@Length(max = 100, message = "File type cannot exceed 100 characters")
	private String fileType;
	
	@Column(name = "file_size")
	private Long fileSize;
	
	@Column(name = "uploaded_at", nullable = false, updatable = false)
	private LocalDateTime uploadedAt;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "expense_id", nullable = false)
	@JsonBackReference
	private Expense expense;
	
	@PrePersist
	protected void onCreate() {
		uploadedAt = LocalDateTime.now();
	}
}
