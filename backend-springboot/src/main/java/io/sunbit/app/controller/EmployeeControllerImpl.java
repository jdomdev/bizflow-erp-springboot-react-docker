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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.sunbit.app.entity.Employee;
import io.sunbit.app.service.EmployeeServiceImpl;

@CrossOrigin(origins = "*")
@RequestMapping("api/v1/employee")
@RestController
public class EmployeeControllerImpl implements IEmployeeController {

	@Autowired
	EmployeeServiceImpl employeeService;

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/")
	public ResponseEntity<?> getAllEmployee() {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(employeeService.findAll());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("{\"error\":\"Error. Please, Try it later. It is NOT possible to SHOW all employees\"}");
		}
	}

	@Override
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	@GetMapping("/{employeeId}")
	// @ResponseBody
	public ResponseEntity<?> getEmployeeById(@PathVariable("employeeId") Long employeeId,
			@RequestHeader("Authorization") String headerAuth) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(employeeService.findById(employeeId, headerAuth));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					"{\"error\":\"Error. Please, Try it later. NOT possible to SHOW the payroll which you find.\"}");
		}
	}

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/")
	// @ResponseBody
	public ResponseEntity<?> saveEmployee(@RequestBody @Valid Employee employee) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(employeeService.save(employee));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("{\"error\":\"Error. Please, Try it later. It is NOT possible to SAVE the employee.\"}");
		}
	}

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping("/{employeeId}")
	// @ResponseBody
	public ResponseEntity<?> deleteEmployee(@PathVariable("employeeId") Long employeeId) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(employeeService.delete(employeeId));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("{\"error\":\"Error. Please, Try it later. It is NOT possible to DELETE the employee.\"}");
		}
	}

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping("/{employeeId}")
	public ResponseEntity<?> updateEmployee(@RequestBody @Valid Employee employee,
			@PathVariable("employeeId") Long employeeId) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(employeeService.update(employeeId, employee));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					"{\"error\":\"Error. Please, Try it later. It is NOT possible UPDATE the employee who you are looking for.\"}");
		}
	}

	@Override
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	@GetMapping("/{name}/{surname}")
	public ResponseEntity<?> getEmployeeByNameAndSurname(@PathVariable("name") String name,
			@PathVariable("surname") String surname,
			@RequestHeader("Authorization") String headerAuth) {
		try {
			return ResponseEntity.status(HttpStatus.OK)
					.body(employeeService.findByNameAndSurnameAllIgnoreCase(name, surname, headerAuth));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					"{\"error\":\"Error. Please, Try it later. It is NOT possible to FIND the employee who you are looking for.\"}");
		}
	}

}
