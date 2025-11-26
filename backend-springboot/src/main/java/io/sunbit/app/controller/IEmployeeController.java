package io.sunbit.app.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import io.sunbit.app.entity.Employee;

public interface IEmployeeController {

	public ResponseEntity<?> getAllEmployee();

	public ResponseEntity<?> getEmployeeById(@PathVariable Long employeeId,
			@RequestHeader String headerAuth);

	public ResponseEntity<?> saveEmployee(@RequestBody @Valid Employee employee);

	public ResponseEntity<?> updateEmployee(@RequestBody @Valid Employee employee,
			@PathVariable Long employeeId);

	public ResponseEntity<?> deleteEmployee(@PathVariable Long employeeId);

	public ResponseEntity<?> getEmployeeByNameAndSurname(@PathVariable String name,
			@PathVariable String surname,
			@RequestHeader String headerAuth);
}
