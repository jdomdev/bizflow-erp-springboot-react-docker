package io.sunbit.app.dto;

import java.util.ArrayList;
import java.util.List;

import io.sunbit.app.entity.Employee;
import io.sunbit.app.entity.Payroll;
import io.sunbit.app.entity.Position;

public class EmployeeMapper {
	// 1 - With ID.
	// A - DTO to Employee.
	public static Employee dtoToEmployeeWithId(EmployeeDto employeeDto) {
		if (isNullList(employeeDto.getPayrollDtos())) {
			employeeDto.setPayrollDtos(initializePayrollDtos(employeeDto.getPayrollDtos()));
		}
		   return new Employee(
				   employeeDto.getId(),
				   employeeDto.getName(),
				   employeeDto.getSurname(),
				   employeeDto.getBirthDate(),
				   PositionMapper.dtoToPosition(employeeDto.getPositionDto()),
				   employeeDto.getEmail(),
				   dtosToPayrolls(employeeDto.getPayrollDtos()));
	}

	// B - Employee to DTO.
	public static EmployeeDto employeeToDtoWithId(Employee employee) {
		if (isNullList(employee.getPayrolls())) {
			employee.setPayrolls(initializePayrolls(employee.getPayrolls()));
		}
		   return new EmployeeDto(
				   employee.getId(),
				   employee.getName(),
				   employee.getSurname(),
				   employee.getBirthDate(),
				   PositionMapper.positionToDtoWithId(employee.getPosition()),
				   payrollsToDtos(employee.getPayrolls()));
	}

	// 2 - Without ID.
	// A - DTO to Employee.
	public static Employee dtoToEmployee(EmployeeDto employeeDto) {
		if (isNullList(employeeDto.getPayrollDtos())) {
			employeeDto.setPayrollDtos(initializePayrollDtos(employeeDto.getPayrollDtos()));
		}
		   Position position = PositionMapper.dtoToPositionWithId(employeeDto.getPositionDto());
		   return new Employee(
			   employeeDto.getName(),
			   employeeDto.getSurname(),
			   employeeDto.getBirthDate(),
			   position,
			   employeeDto.getEmail(),
			   dtosToPayrolls(employeeDto.getPayrollDtos()));
	}

	// B - Employee to DTO.
	public static EmployeeDto employeeToDto(Employee employee) {
		       return new EmployeeDto(
			       employee.getName(),
			       employee.getSurname(),
			       employee.getBirthDate(),
			       PositionMapper.PositionToDto(employee.getPosition()));
	}

	private static Boolean isNullList(List<?> list) {
		boolean isNull = false;
		if (list == null)
			isNull = true;
		return isNull;
	}
	private static List<PayrollDto> initializePayrollDtos(List<PayrollDto> payrollDtos) {
		return payrollDtos = new ArrayList<>();
	}
	private static List<Payroll> initializePayrolls(List<Payroll> payrolls) {
		return payrolls = new ArrayList<>();
	}
	// B - Payroll/DTO lists.
	private static List<Payroll> dtosToPayrolls(List<PayrollDto> payrollDtos) {
		List<Payroll> payrolls = new ArrayList<>();
		for (PayrollDto payrollDto : payrollDtos) {
			payrolls.add(PayrollMapper.dtoToPayroll(payrollDto));
		}
		return payrolls;
	}

	private static List<PayrollDto> payrollsToDtos(List<Payroll> payrolls) {
		List<PayrollDto> payrollDtos = new ArrayList<>();
		for (Payroll payroll : payrolls) {
			payrollDtos.add(PayrollMapper.payrollToDto(payroll));
		}
		return payrollDtos;
	}
}
