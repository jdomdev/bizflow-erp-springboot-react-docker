package io.sunbit.app.dto;

import io.sunbit.app.entity.Position;

public class PositionMapper {
	// Without ID.
	public static Position dtoToPosition(PositionDto positionDto) {
		Position position = new Position(positionDto.getName());
		position.setDescription(positionDto.getDescription());
		position.setBaseSalary(positionDto.getBaseSalary());
		return position;
	}

	public static PositionDto positionToDto(Position position) {
		PositionDto dto = new PositionDto();
		dto.setId(position.getId());
		dto.setName(position.getName());
		dto.setDescription(position.getDescription());
		dto.setBaseSalary(position.getBaseSalary());
		return dto;
	}

	// With ID (for updates).
	public static Position dtoToPositionWithId(PositionDto positionDto) {
		Position position = new Position(positionDto.getName());
		position.setId(positionDto.getId());
		position.setDescription(positionDto.getDescription());
		position.setBaseSalary(positionDto.getBaseSalary());
		return position;
	}

	// Deprecated - use positionToDto instead
	public static PositionDto positionToDtoWithId(Position position) {
		return positionToDto(position);
	}

	// Deprecated - use positionToDto instead
	public static PositionDto PositionToDto(Position position) {
		return positionToDto(position);
	}
}
