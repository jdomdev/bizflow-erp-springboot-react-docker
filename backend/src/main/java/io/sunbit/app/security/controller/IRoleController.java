package io.sunbit.app.security.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import io.sunbit.app.security.entity.Role;

public interface IRoleController {

	public ResponseEntity<?> getAllRole();

	public ResponseEntity<?> getRoleById(@PathVariable Long roleId);

	public ResponseEntity<?> saveRole(@RequestBody @Valid Role role);

	public ResponseEntity<?> updateRole(@RequestBody @Valid Role role,
			@PathVariable Long roleId);

	public ResponseEntity<?> deleteRole(@PathVariable Long roleId);
}
