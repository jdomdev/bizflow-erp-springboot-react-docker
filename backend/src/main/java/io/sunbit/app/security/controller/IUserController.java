package io.sunbit.app.security.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import io.sunbit.app.dto.ExpenseUserDto;

public interface IUserController {

	public ResponseEntity<?> getAllUser();

	public ResponseEntity<?> getUserById(@PathVariable Long userId);

	public ResponseEntity<?> saveUser(@RequestBody @Valid ExpenseUserDto userDto);

	public ResponseEntity<?> updateUser(@RequestBody @Valid ExpenseUserDto userDto,
			  @PathVariable Long userId);

	public ResponseEntity<?> deleteUser(@PathVariable Long userId);
}
