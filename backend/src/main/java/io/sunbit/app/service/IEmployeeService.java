package io.sunbit.app.service;

import java.util.List;

import io.sunbit.app.entity.Employee;

public interface IEmployeeService {
	public List<Employee> findAll() throws Exception;

	public Employee findById(Long id, String headerAuth) throws Exception;

	public Employee save(Employee employee) throws Exception;

	public Employee update(Long id, Employee employee) throws Exception;

	public Boolean delete(Long id) throws Exception;

	public Employee findByNameAndSurnameAllIgnoreCase(String employeeName,
			String employeeSurname,
			String headerAuth) throws Exception;

	public Employee findByEmail(String email) throws Exception;
}
