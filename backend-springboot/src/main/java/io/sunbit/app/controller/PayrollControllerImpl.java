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

import io.sunbit.app.entity.Employee;
import io.sunbit.app.entity.Payroll;
import io.sunbit.app.service.PayrollServiceImpl;

@CrossOrigin(origins = "*")
@RequestMapping("api/v1/payroll")
@RestController
public class PayrollControllerImpl implements IPayrollController<Payroll> {
	@Autowired
	private PayrollServiceImpl payrollService;

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/")
	public ResponseEntity<?> getAllPayroll() {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(payrollService.findAll());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("{\"error\":\"Error. Please, Try it later. It is NOT possible to SHOW all payrolls\"}");
		}
	}

	@Override
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	@GetMapping("/employee/{employeeId}")
	// @ResponseBody
	public ResponseEntity<?> getAllPayrollByEmployeeId(@PathVariable("employeeId") Long employeeId) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(payrollService.findAllPayrollByEmployeeId(employeeId));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					"{\"error\":\"Error. Please, Try it later. It is NOT possible to SHOW the employee's payrolls.\"}");
		}
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	@GetMapping("/{payrollId}")
	// @ResponseBody
	public ResponseEntity<?> getPayrollById(@PathVariable("payrollId") Long payrollId) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(payrollService.findById(payrollId));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					"{\"error\":\"Error. Please, Try it later. It is NOT possible to SHOW the payroll which you find.\"}");
		}
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	@PostMapping("/")
	// @ResponseBody
	public ResponseEntity<?> savePayroll(@RequestBody @Valid Payroll payroll) {
		ResponseEntity<Payroll> responseEntity;
		try {
			Employee employee = payroll.getEmployee();
			responseEntity = ResponseEntity.status(HttpStatus.OK).body(payrollService.save(payroll));
			employee.addPayroll(payroll);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("{\"error\":\"Error. Please, Try it later. It is NOT possible to SAVE the entity.\"}");
		}
		return responseEntity;
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping("/{payrollId}")
	// @ResponseBody
	public ResponseEntity<?> deletePayroll(@PathVariable("payrollId") Long payrollId) {
		ResponseEntity<Boolean> responseEntity;
		try {
			Payroll payroll = payrollService.findById(payrollId);
			Employee employee = payroll.getEmployee();
			employee.removePayroll(payroll);// ******** Check if it's allow.......***********
			responseEntity = ResponseEntity.status(HttpStatus.OK).body(payrollService.delete(payrollId));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("{\"error\":\"Error. Please, Try it later. It is NOT possible to SAVE the payroll.\"}");
		}
		return responseEntity;
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@PutMapping("/{payrollId}")
	public ResponseEntity<?> updatePayroll(@RequestBody @Valid Payroll payroll,
			@PathVariable("payrollId") Long payrollId) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(payrollService.update(payrollId, payroll));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					"{\"error\":\"Error. Please, Try it later. It is NOT possible UPDATE the payroll which you are looking for.\"}");
		}
	}
}