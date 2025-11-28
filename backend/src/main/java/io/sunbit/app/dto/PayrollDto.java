package io.sunbit.app.dto;

import java.time.LocalDateTime;

import io.sunbit.app.entity.Payroll;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class PayrollDto extends Payroll {

	private static final long serialVersionUID = 1L;
	private Long id;
	@NonNull
	private Double amount;
	@NonNull
	private LocalDateTime date;
	@NonNull
	private EmployeeDto employeeDto;
}
