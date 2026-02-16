package io.sunbit.app.dto;

import java.time.LocalDateTime;
import java.util.List;

import io.sunbit.app.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public class EmployeeDto {

	private Long id;
	@NonNull
	private String name;
	@NonNull
	private String surname;
	@NonNull
	private LocalDateTime birthDate;
	@NonNull
	private Long positionId;
	private String email;
	private List<ExpenseDto> expenseDtos;
	private List<PayrollDto> payrollDtos;

	// Constructor con id
	public EmployeeDto(Long id, String name, String surname, LocalDateTime birthDate, Long positionId, String email, List<PayrollDto> payrollDtos) {
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.birthDate = birthDate;
		this.positionId = positionId;
		this.email = email;
		this.payrollDtos = payrollDtos;
	}

	// Constructor sin id
	public EmployeeDto(String name, String surname, LocalDateTime birthDate, Long positionId, String email, List<ExpenseDto> expenseDtos, List<PayrollDto> payrollDtos) {
		this.name = name;
		this.surname = surname;
		this.birthDate = birthDate;
		this.positionId = positionId;
		this.email = email;
		this.expenseDtos = expenseDtos;
		this.payrollDtos = payrollDtos;
	}
}
