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

import io.sunbit.app.security.dto.UserUpdateRequest;
import io.sunbit.app.dto.ExpenseUserDto;
import io.sunbit.app.dto.ExpenseUserMapper;
import io.sunbit.app.security.service.UserServiceImpl;
import io.sunbit.app.security.entity.ExpenseUser;

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
			ExpenseUserDto dto = ExpenseUserMapper.expenseUserToDtoWithId(user);
			return ResponseEntity.ok(dto);
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
			// Adaptar para UserUpdateRequest: solo email y password, roles no se actualizan aquí
			if (request.getEmail() != null && !request.getEmail().isEmpty()) {
				user.setEmail(request.getEmail());
			}
			if (request.getPassword() != null && !request.getPassword().isEmpty()) {
				user.setPassword(request.getPassword());
			}
			ExpenseUser updatedUser = userService.update(user.getId(), user, java.util.Collections.emptyList());
			ExpenseUserDto dto = ExpenseUserMapper.expenseUserToDtoWithId(updatedUser);
			return ResponseEntity.ok(dto);
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
	@GetMapping
	public ResponseEntity<?> getAllUser() {
		try {
			List<ExpenseUserDto> dtos = userService.findAll().stream()
				.map(ExpenseUserMapper::expenseUserToDtoWithId)
				.collect(Collectors.toList());
			return ResponseEntity.status(HttpStatus.OK).body(dtos);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("{\"error\":\"Error. Please, Try it later. It is NOT possible to SHOW all the users.\"}");
		}
	}

	@Override
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	@GetMapping("/{userId}")
	public ResponseEntity<?> getUserById(@PathVariable Long userId) {
		try {
			ExpenseUser user = userService.findById(userId).orElseThrow(() -> new Exception("User not found"));
			ExpenseUserDto dto = ExpenseUserMapper.expenseUserToDtoWithId(user);
			return ResponseEntity.status(HttpStatus.OK).body(dto);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("{\"error\":\"Error. Please, Try it later. NOT possible to SHOW the user you are looking for.\"}");
		}
	}

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/")
	public ResponseEntity<?> saveUser(@RequestBody @Valid ExpenseUserDto userDto) {
		try {
			ExpenseUser user = ExpenseUserMapper.dtoToExpenseUserWithId(userDto);
			ExpenseUser saved = userService.save(user, userDto.getRoleIds());
			ExpenseUserDto dto = ExpenseUserMapper.expenseUserToDtoWithId(saved);
			return ResponseEntity.status(HttpStatus.OK).body(dto);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("{\"error\":\"Error. Please, Try it later. NOT possible to SAVE the user.\"}");
		}
	}

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping("/{userId}")
	public ResponseEntity<?> updateUser(@RequestBody @Valid ExpenseUserDto userDto,
			@PathVariable Long userId) {
		try {
			ExpenseUser user = ExpenseUserMapper.dtoToExpenseUserWithId(userDto);
			ExpenseUser updated = userService.update(userId, user, userDto.getRoleIds());
			ExpenseUserDto dto = ExpenseUserMapper.expenseUserToDtoWithId(updated);
			return ResponseEntity.status(HttpStatus.OK).body(dto);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					"{\"error\":\"Error. Please, Try it later. It is NOT possible to UPDATE the user you are looking for.\"}");
		}
	}

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping("/{userId}")
	public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(userService.delete(userId));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					"{\"error\":\"Error. Please, Try it later. It is NOT possible to DELETE the user you are looking for.\"}");
		}
	}
}
