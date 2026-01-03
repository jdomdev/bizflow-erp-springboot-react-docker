package io.sunbit.app.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.sunbit.app.dao.IEmployeeDao;
import io.sunbit.app.dao.IPositionDao;
import io.sunbit.app.entity.Employee;
import io.sunbit.app.entity.Position;
import io.sunbit.app.exception.BadRequestException;
import io.sunbit.app.exception.ResourceNotFoundException;
import io.sunbit.app.util.DateUtil;

@Service
public class EmployeeServiceImpl implements IEmployeeService {

	@Autowired
	IEmployeeDao employeeDao;
	@Autowired
	IPositionDao positionDao;

	@Override
	public Employee findByNameAndSurnameAllIgnoreCase(String name, String surname, String HeaderAuth) throws Exception {
		Optional<Employee> optionalEmployee = employeeDao.findByNameAndSurnameAllIgnoreCase(name, surname);
		Employee searchedEmployee = null;
		if (!optionalEmployee.isEmpty()) {
			searchedEmployee = optionalEmployee.get();
		}
		return searchedEmployee;
	}

	@Override
	public List<Employee> findAll() throws Exception {
		try {
			return employeeDao.findAll();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public Employee findById(@jakarta.validation.constraints.NotNull Long id, String headerAuth) throws Exception {
		try {
			Optional<Employee> optionalEmployee = employeeDao.findById(id);
			return optionalEmployee.get();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	@Override
	@Transactional
	public Employee save(Employee employee) throws Exception {
		try {
			syncPosition(employee);
			LocalDateTime parsedDate = DateUtil.formattingDate(employee.getBirthDate());
			employee.setBirthDate(parsedDate);
			return employeeDao.save(employee);
		} catch (BadRequestException | ResourceNotFoundException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	@Override
	@Transactional
	public Employee update(Long id, Employee employee) throws Exception {
		try {
			Optional<Employee> optionalEmployee = employeeDao.findById(id);
			if (optionalEmployee.isEmpty()) {
				throw new io.sunbit.app.exception.ResourceNotFoundException("Employee", "id", id);
			}
			Employee existingEmployee = optionalEmployee.get();
			// Solo actualiza los campos enviados (no null)
			if (employee.getName() != null) existingEmployee.setName(employee.getName());
			if (employee.getSurname() != null) existingEmployee.setSurname(employee.getSurname());
			if (employee.getBirthDate() != null) {
				LocalDateTime parsedDate = DateUtil.formattingDate(employee.getBirthDate());
				existingEmployee.setBirthDate(parsedDate);
			}
			if (employee.getEmail() != null) existingEmployee.setEmail(employee.getEmail());
			if (employee.getPosition() != null && employee.getPosition().getId() != null) {
				syncPosition(employee);
				existingEmployee.setPosition(employee.getPosition());
			}
			return employeeDao.save(existingEmployee);
		} catch (BadRequestException | ResourceNotFoundException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	@Override
	@Transactional
	public Boolean delete(@jakarta.validation.constraints.NotNull Long id) throws Exception {
		boolean isDeleted = false;
		try {
			if (employeeDao.existsById(id)) {
				employeeDao.deleteById(id);
				isDeleted = true;
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return isDeleted;
	}

	@Override
	public Employee findByEmail(String email) throws Exception {
		try {
			Optional<Employee> optEmployee = employeeDao.findByEmail(email);
			return optEmployee.orElse(null);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	private void syncPosition(Employee employee) {
		if (employee == null) {
			throw new BadRequestException("employee", null, "Empleado no puede ser nulo");
		}
		Position position = employee.getPosition();
		if (position == null || position.getId() == null) {
			throw new BadRequestException("positionId", null, "Debe proporcionar un identificador de posición válido");
		}
		Position managedPosition = positionDao.findById(position.getId())
			.orElseThrow(() -> new ResourceNotFoundException("Position", "id", position.getId()));
		employee.setPosition(managedPosition);
	}
}
