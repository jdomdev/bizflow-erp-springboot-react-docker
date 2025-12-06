package io.sunbit.app.security.controller;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import io.sunbit.app.security.entity.ExpenseUser;
import io.sunbit.app.security.service.UserServiceImpl;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/v1/user")
public class UserControllerImpl implements IUserController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserControllerImpl.class);
	
	@Lazy
	@Autowired
	private UserServiceImpl userService;

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/")
	public ResponseEntity<?> getAllUser() {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(userService.findAll());
		} catch (Exception e) {
			LOGGER.error("Error retrieving all users", e);
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("{\"error\":\"Error. Please, Try it later. It is NOT possible to SHOW all the users\"}");
		}
	}

	@Override
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	@GetMapping("/{userId}")
	public ResponseEntity<?> getUserById(@PathVariable("userId") Long userId) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(userService.findById(userId));
		} catch (Exception e) {
			LOGGER.error("Error retrieving user with id: {}", userId, e);
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("{\"error\":\"Error. Please, Try it later. NOT possible to SHOW the user who you find.\"}");
		}
	}

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/")
	public ResponseEntity<?> saveUser(@RequestBody @Valid ExpenseUser user) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(userService.save(user));
		} catch (Exception e) {
			LOGGER.error("Error saving user", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("{\"error\":\"Error. Please, Try it later. NOT possible to SAVE the user.\"}");
		}
	}

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping("/{userId}")
	public ResponseEntity<?> updateUser(@RequestBody @Valid ExpenseUser user,
			@PathVariable("userId") Long userId) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(userService.update(userId, user));
		} catch (Exception e) {
			LOGGER.error("Error updating user with id: {}", userId, e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					"{\"error\":\"Error. Please, Try it later. it is NOT possible UPDATE the user who you looking for.\"}");
		}
	}

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping("/{userId}")
	public ResponseEntity<?> deleteUser(@PathVariable("userId") Long userId) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(userService.delete(userId));
		} catch (Exception e) {
			LOGGER.error("Error deleting user with id: {}", userId, e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					"{\"error\":\"Error. Please, Try it later. It is NOT possible DELETE the user who you looking for.\"}");
		}
	}

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/{userId}/roles/{roleId}")
	public ResponseEntity<?> assignRoleToUser(@PathVariable("userId") Long userId, 
			@PathVariable("roleId") Long roleId) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(userService.assignRoleToUser(userId, roleId));
		} catch (Exception e) {
			LOGGER.error("Error assigning role {} to user {}", roleId, userId, e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("{\"error\":\"Error. Could not assign role to user: " + e.getMessage() + "\"}");
		}
	}

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping("/{userId}/roles/{roleId}")
	public ResponseEntity<?> removeRoleFromUser(@PathVariable("userId") Long userId, 
			@PathVariable("roleId") Long roleId) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(userService.removeRoleFromUser(userId, roleId));
		} catch (Exception e) {
			LOGGER.error("Error removing role {} from user {}", roleId, userId, e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("{\"error\":\"Error. Could not remove role from user: " + e.getMessage() + "\"}");
		}
	}

	@Override
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	@GetMapping("/{userId}/roles")
	public ResponseEntity<?> getUserRoles(@PathVariable("userId") Long userId) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(userService.getUserRoles(userId));
		} catch (Exception e) {
			LOGGER.error("Error retrieving roles for user {}", userId, e);
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("{\"error\":\"Error. Could not retrieve user roles: " + e.getMessage() + "\"}");
		}
	}
}
