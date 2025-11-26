package io.sunbit.app.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.sunbit.app.entity.Position;
import io.sunbit.app.service.PositionServiceImpl;

@CrossOrigin(origins = "*")
@RequestMapping("api/v1/position")
@RestController
public class PositionControllerImpl implements IPositionController<Position> {

	@Autowired
	private PositionServiceImpl positionService;

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/")
	public ResponseEntity<?> getAllPosition() {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(positionService.findAll());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("{\"error\":\"Error. Please, Try it later. It is NOT possible to SHOW all employee types\"}");
		}
	}

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/{positionId}")
	// @ResponseBody
	public ResponseEntity<?> getPositionById(@PathVariable("positionId") Long positionId) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(positionService.findById(positionId));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					"{\"error\":\"Error. Please, Try it later. NOT possible to SHOW the employee type which you find.\"}");
		}
	}

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/")
	// @ResponseBody
	public ResponseEntity<?> savePosition(@RequestBody @Valid Position position) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(positionService.save(position));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("{\"error\":\"Error. Please, Try it later. It is NOT possible to SAVE the employee type.\"}");
		}
	}

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping("/{positionId}")
	// @ResponseBody
	public ResponseEntity<?> deletePosition(@PathVariable("positionId") Long positionId) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(positionService.delete(positionId));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					"{\"error\":\"Error. Please, Try it later. It is NOT possible to DELETE the employee type.\"}");
		}
	}

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping("/{positionId}")
	public ResponseEntity<?> updatePosition(@RequestBody @Valid Position position,
			@PathVariable("employeeTypeId") Long positionId) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(positionService.update(positionId, position));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					"{\"error\":\"Error. Please, Try it later. It is NOT possible UPDATE the employee type which you are looking for.\"}");
		}
	}
}
