package io.sunbit.app.security.controller;

import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.sunbit.app.security.dto.UserProfileResponse;
import io.sunbit.app.security.dto.UserUpdateRequest;
import io.sunbit.app.security.entity.ExpenseUser;
import io.sunbit.app.security.entity.Role;
import io.sunbit.app.security.service.UserServiceImpl;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/v1/user")
public class UserControllerImpl implements IUserController {
	@Lazy
	@Autowired
	private UserServiceImpl userService;

	@GetMapping("/profile")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<?> getProfile(Authentication authentication) {
		try {
			// Usar email como identificador único
			String email = authentication.getName();
			ExpenseUser user = userService.findByEmail(email).orElseThrow(() -> new Exception("User not found"));
			List<String> roles = user.getRoles().stream()
				.map(Role::getName)
				.collect(Collectors.toList());
			return ResponseEntity.ok(new UserProfileResponse(user.getEmail(), roles));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching profile");
		}
	}

	@PutMapping("/profile")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<?> updateProfile(@RequestBody UserUpdateRequest request, Authentication authentication) {
		try {
			// Usar email como identificador único
			String email = authentication.getName();
			ExpenseUser user = userService.findByEmail(email).orElseThrow(() -> new Exception("User not found"));
			if (request.getEmail() != null && !request.getEmail().isEmpty()) {
				user.setEmail(request.getEmail());
			}
			if (request.getPassword() != null && !request.getPassword().isEmpty()) {
				user.setPassword(request.getPassword());
			}
			ExpenseUser updatedUser = userService.update(user.getId(), user);
			List<String> roles = updatedUser.getRoles().stream()
				.map(Role::getName)
				.collect(Collectors.toList());
			return ResponseEntity.ok(new UserProfileResponse(updatedUser.getEmail(), roles));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating profile: " + e.getMessage());
		}
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout() {
		return ResponseEntity.ok().build();
	}

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/")
	public ResponseEntity<?> getAllUser() {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(userService.findAll());
		} catch (Exception e) {
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					"{\"error\":\"Error. Please, Try it later. It is NOT possible DELETE the user who you looking for.\"}");
		}
	}
}
