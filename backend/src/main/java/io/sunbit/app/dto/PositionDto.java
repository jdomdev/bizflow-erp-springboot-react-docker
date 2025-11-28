package io.sunbit.app.dto;

import io.sunbit.app.entity.Position;
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
public class PositionDto extends Position {

	private static final long serialVersionUID = 1L;
	private Long id;
	@NonNull
	private String name;
}
