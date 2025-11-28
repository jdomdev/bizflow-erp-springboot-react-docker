package io.sunbit.app.dto;

import io.sunbit.app.entity.Position;

public class PositionMapper {
	// Without ID.
	public static Position dtoToPosition(PositionDto positionDto) {
		return new Position(
				positionDto.getName());
	}

	public static PositionDto PositionToDto(Position position) {
		return new PositionDto(
				position.getName());
	}

	// With ID.
	public static Position dtoToPositionWithId(PositionDto positionDto) {
		return new Position(
				positionDto.getId(),
				positionDto.getName());
	}

	public static PositionDto positionToDtoWithId(Position position) {
		return new PositionDto(
				position.getId(),
				position.getName());
	}
}
