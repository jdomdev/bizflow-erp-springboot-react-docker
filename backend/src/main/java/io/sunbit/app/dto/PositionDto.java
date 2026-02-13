package io.sunbit.app.dto;

import java.math.BigDecimal;

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
public class PositionDto {

	private static final long serialVersionUID = 1L;
	private Long id;
	@NonNull
	private String name;
	private String description;
	private BigDecimal baseSalary;
}
