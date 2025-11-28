package io.sunbit.app.security.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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

import io.sunbit.app.security.entity.Role;
import io.sunbit.app.security.service.RoleServiceImpl;

@CrossOrigin(origins = "*")
@RequestMapping("api/v1/role")
@RestController
public class RoleControllerImpl implements IRoleController {

	@Lazy
	@Autowired
	private RoleServiceImpl roleService;

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/")
	public ResponseEntity<?> getAllRole() {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(roleService.findAll());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("{\"error\":\"Error. Please, Try it later. It is NOT possible to SHOW all the roles\"}");
		}
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@GetMapping("/{roleId}")
	public ResponseEntity<?> getRoleById(@PathVariable("roleId") Long roleId) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(roleService.findById(roleId));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					"{\"error\":\"Error. Please, Try it later. It is NOT possible to SHOW the role who you find.\"}");
		}
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@PostMapping("/")
	public ResponseEntity<?> saveRole(@Valid @RequestBody Role role) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(roleService.save(role));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("{\"error\":\"Error. Please, Try it later. NOT possible to SAVE the user.\"}");
		}
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping("/{roleId}")
	public ResponseEntity<?> updateRole(@RequestBody Role role,
			@PathVariable("roleId") Long roleId) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(roleService.update(roleId, role));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					"{\"error\":\"Error. Please, Try it later. it is NOT possible UPDATE the user who you looking for.\"}");
		}
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping("/{roleId}")
	public ResponseEntity<?> deleteRole(@PathVariable("roleId") Long roleId) {
		try {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(roleService.delete(roleId));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					"{\"error\":\"Error. Please, Try it later. It is NOT possible DELETE the user who you looking for.\"}");
		}
	}
}
