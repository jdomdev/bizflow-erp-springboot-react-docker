package io.sunbit.app.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

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

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "name", nullable = false, length = 128)
	@Length(min = 3, max = 128)
	@NonNull
	private String name;
	@Column(name = "surname", nullable = false, length = 255)
	@Length(min = 5, max = 255)
	@NonNull
	private String surname;
	@Column(name = "birth_date")
	// @Temporal(TemporalType.TIMESTAMP)
	@NonNull
	private LocalDateTime birthDate;
	@Column(name = "email", nullable = false, length = 255)
	@Length(min = 3, max = 255)
	@NonNull
	private String email;
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
			List<Expense> expenses, List<Payroll> payrolls) {
		this.name = name;
		this.surname = surname;
		this.birthDate = birthDate;
		this.position = position;
		this.email = email;
		this.expenses = expenses;
		this.payrolls = payrolls;
	}

	// Constructor with id.
	public Employee(Long id, String name, String surname, LocalDateTime birthDate, Position position, String email,
			List<Expense> expenses, List<Payroll> payrolls) {
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.birthDate = birthDate;
		this.position = position;
		this.email = email;
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
