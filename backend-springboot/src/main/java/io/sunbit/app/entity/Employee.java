package io.sunbit.app.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "employee", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
// @Audited
@ToString
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Employee implements Serializable {

	public enum EmployeeStatus {
		ACTIVE, INACTIVE, TERMINATED
	}

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "name", nullable = false, length = 128)
	@NotBlank(message = "Name is required")
	@Length(min = 3, max = 128, message = "Name must be between 3 and 128 characters")
	@NonNull
	private String name;
	
	@Column(name = "surname", nullable = false, length = 255)
	@NotBlank(message = "Surname is required")
	@Length(min = 2, max = 255, message = "Surname must be between 2 and 255 characters")
	@NonNull
	private String surname;
	
	@Column(name = "birth_date")
	@PastOrPresent(message = "Birth date must be in the past or present")
	@NonNull
	private LocalDateTime birthDate;
	
	@Column(name = "email", nullable = false, length = 255)
	@NotBlank(message = "Email is required")
	@Email(message = "Email must be valid")
	@Length(max = 255, message = "Email must not exceed 255 characters")
	@NonNull
	private String email;
	
	@Column(name = "start_date")
	@NotNull(message = "Start date is required")
	private LocalDate startDate;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	@NotNull(message = "Status is required")
	private EmployeeStatus status = EmployeeStatus.ACTIVE;
	
	@OneToOne
	@JoinColumn(name = "id") // <-position_id(olds employee_type_id/employee_type_id_fk)
	@NonNull
	private Position position;

	@OneToMany(mappedBy = "employee", targetEntity = Expense.class, cascade = { CascadeType.MERGE, CascadeType.REMOVE,
			CascadeType.REFRESH, CascadeType.DETACH }, orphanRemoval = false)
	// @JsonIgnore
	@JsonBackReference
	private List<Expense> expenses = new ArrayList<>();

	@OneToMany(mappedBy = "employee", targetEntity = Payroll.class, cascade = { CascadeType.MERGE, CascadeType.REFRESH,
			CascadeType.DETACH }, orphanRemoval = false)
	@JsonIgnore
	// @JsonBackReference
	private List<Payroll> payrolls = new ArrayList<>();

	// Constructor without id.
	public Employee(String name, String surname, LocalDateTime birthDate, Position position, String email,
			LocalDate startDate, EmployeeStatus status, List<Expense> expenses, List<Payroll> payrolls) {
		this.name = name;
		this.surname = surname;
		this.birthDate = birthDate;
		this.position = position;
		this.email = email;
		this.startDate = startDate;
		this.status = status != null ? status : EmployeeStatus.ACTIVE;
		this.expenses = expenses;
		this.payrolls = payrolls;
	}

	// Constructor with id.
	public Employee(Long id, String name, String surname, LocalDateTime birthDate, Position position, String email,
			LocalDate startDate, EmployeeStatus status, List<Expense> expenses, List<Payroll> payrolls) {
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.birthDate = birthDate;
		this.position = position;
		this.email = email;
		this.startDate = startDate;
		this.status = status != null ? status : EmployeeStatus.ACTIVE;
		this.expenses = expenses;
		this.payrolls = payrolls;
	}

	public void addExpense(Expense expense) {
		expenses.add(expense);
	}

	public void addPayroll(Payroll payroll) {
		payrolls.add(payroll);
	}

	public void removeExpense(Expense expense) {
		expenses.remove(expense);
	}

	public void removePayroll(Payroll payroll) {
		payrolls.remove(payroll);
	}
}
