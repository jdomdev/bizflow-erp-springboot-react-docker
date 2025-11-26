package io.sunbit.app.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface IPayrollController<Payroll> {

	public ResponseEntity<?> getAllPayrollByEmployeeId(@PathVariable Long employeeId);

	public ResponseEntity<?> getAllPayroll();

	public ResponseEntity<?> getPayrollById(@PathVariable Long payrollId);

	public ResponseEntity<?> savePayroll(@RequestBody @Valid Payroll payroll);

	public ResponseEntity<?> updatePayroll(@RequestBody @Valid Payroll payroll,
			@PathVariable Long payrollId);

	public ResponseEntity<?> deletePayroll(@PathVariable Long payrollId);
}
