package io.sunbit.app.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import io.sunbit.app.entity.Position;

public interface IPositionController<Position> {

	public ResponseEntity<?> getAllPosition();

	public ResponseEntity<?> getPositionById(@PathVariable Long positionId);

	public ResponseEntity<?> savePosition(@RequestBody @Valid Position position);

	public ResponseEntity<?> updatePosition(@RequestBody @Valid Position position,
			@PathVariable Long positionId);

	public ResponseEntity<?> deletePosition(@PathVariable Long positionId);
}
