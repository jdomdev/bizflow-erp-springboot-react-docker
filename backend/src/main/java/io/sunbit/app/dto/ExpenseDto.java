package io.sunbit.app.dto;

import java.time.LocalDateTime;

import io.sunbit.app.entity.Expense;
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
public class ExpenseDto extends Expense {

	private static final long serialVersionUID = 1L;
	private Long id;
	@NonNull
	private String concept;
	@NonNull
	private String note;
	@NonNull
	private LocalDateTime date;
	@NonNull
	private Double amount;
	@NonNull
	private EmployeeDto employeeDto;
}
