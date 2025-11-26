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
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payroll")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
// @Audited
public class Payroll implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "amount", nullable = false)
	@NonNull
	private Double amount;
	@Column(name = "payroll_date", nullable = false)
	// @Temporal(TemporalType.TIMESTAMP)
	@NonNull
	private LocalDateTime date;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id") // "id"->ERROR
	// @JsonIgnore
	@NonNull
	private Employee employee;
}
