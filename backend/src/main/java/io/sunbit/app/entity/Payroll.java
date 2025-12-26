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

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import io.sunbit.app.security.entity.ExpenseUser;

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
	private LocalDateTime payrollDate;
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id", nullable = true)
	private Employee employee;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "expense_user_id", nullable = true)
	private ExpenseUser expenseUser;

	@JsonGetter("employeeId")
	public Long getEmployeeId() {
		return employee != null ? employee.getId() : null;
	}

	@JsonSetter("employeeId")
	public void setEmployeeId(Long employeeId) {
		if (employeeId == null) {
			this.employee = null;
			return;
		}
		Employee reference = new Employee();
		reference.setId(employeeId);
		this.employee = reference;
	}

	@JsonGetter("expenseUserId")
	public Long getExpenseUserId() {
		return expenseUser != null ? expenseUser.getId() : null;
	}

	@JsonSetter("expenseUserId")
	public void setExpenseUserId(Long expenseUserId) {
		if (expenseUserId == null) {
			this.expenseUser = null;
			return;
		}
		ExpenseUser reference = new ExpenseUser();
		reference.setId(expenseUserId);
		this.expenseUser = reference;
	}
}
