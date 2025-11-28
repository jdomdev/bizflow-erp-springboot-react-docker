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
	private PositionDto positionDto;
	private List<ExpenseDto> expenseDtos;
	private List<PayrollDto> payrollDtos;

	// Constructor without id.
	public EmployeeDto(String name, String surname, LocalDateTime birthDate, PositionDto positionDto,
			List<ExpenseDto> expenseDtos, List<PayrollDto> payrollDtos) {
		this.name = name;
		this.surname = surname;
		this.birthDate = birthDate;
		this.positionDto = positionDto;
		this.expenseDtos = expenseDtos;
		this.payrollDtos = payrollDtos;
	}
}
