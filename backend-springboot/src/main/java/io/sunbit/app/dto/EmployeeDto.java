package io.sunbit.app.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import io.sunbit.app.entity.Employee;
import io.sunbit.app.entity.Employee.EmployeeStatus;
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
public class EmployeeDto extends Employee {

	private static final long serialVersionUID = 1L;
	private Long id;
	@NonNull
	private String name;
	@NonNull
	private String surname;
	@NonNull
	private LocalDateTime birthDate;
	@NonNull
	private String email;
	private LocalDate startDate;
	private EmployeeStatus status;
	@NonNull
	private PositionDto positionDto;
	private List<ExpenseDto> expenseDtos;
	private List<PayrollDto> payrollDtos;

	// Constructor without id - for creation
	public EmployeeDto(String name, String surname, LocalDateTime birthDate, String email,
			LocalDate startDate, EmployeeStatus status, PositionDto positionDto,
			List<ExpenseDto> expenseDtos, List<PayrollDto> payrollDtos) {
		this.name = name;
		this.surname = surname;
		this.birthDate = birthDate;
		this.email = email;
		this.startDate = startDate;
		this.status = status;
		this.positionDto = positionDto;
		this.expenseDtos = expenseDtos;
		this.payrollDtos = payrollDtos;
	}
}
