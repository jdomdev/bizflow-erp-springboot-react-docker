package io.sunbit.app.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import io.sunbit.app.dao.IEmployeeDao;
import io.sunbit.app.dao.IPositionDao;
import io.sunbit.app.entity.Employee;
import io.sunbit.app.entity.Position;
import io.sunbit.app.exception.BadRequestException;
import io.sunbit.app.exception.ResourceNotFoundException;
import io.sunbit.app.security.dao.IUserDao;
import io.sunbit.app.security.entity.ExpenseUser;
import io.sunbit.app.util.DateUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmployeeServiceImpl implements IEmployeeService {

	@Autowired
	IEmployeeDao employeeDao;
	@Autowired
	IPositionDao positionDao;
	@Autowired
	@Lazy
	IUserDao userDao;

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
			
			// Save employee first to get the ID
			Employee savedEmployee = employeeDao.save(employee);
			
			// Vinculación automática inversa: Si existe un user con el mismo email, vincularlo
			// Esto complementa la vinculación User → Employee que ya existe en UserServiceImpl
			if (savedEmployee.getEmail() != null) {
				Optional<ExpenseUser> matchingUser = userDao.findByEmail(savedEmployee.getEmail());
				if (matchingUser.isPresent()) {
					ExpenseUser user = matchingUser.get();
					// Vincular si el user no tiene employee asignado
					if (user.getEmployee() == null) {
						user.setEmployee(savedEmployee);
						userDao.save(user);
						log.info("Auto-linked employee {} with existing user {}", 
							savedEmployee.getId(), user.getId());
					} else if (!user.getEmployee().getId().equals(savedEmployee.getId())) {
						// User ya tiene otro employee - conflicto que el admin debe resolver
						log.warn("User {} (email: {}) already linked to different employee {}. New employee {} not auto-linked.",
							user.getId(), user.getEmail(), user.getEmployee().getId(), savedEmployee.getId());
					}
				}
			}
			
			return savedEmployee;
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
			String oldEmail = existingEmployee.getEmail();
			
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
			
			Employee savedEmployee = employeeDao.save(existingEmployee);
			
			// Si el email cambió, gestionar vinculación con usuarios
			String newEmail = savedEmployee.getEmail();
			if (newEmail != null && !newEmail.equals(oldEmail)) {
				// Primero, desvincular al usuario anterior (si existe y apunta a este empleado)
				if (oldEmail != null) {
					Optional<ExpenseUser> oldUser = userDao.findByEmail(oldEmail);
					if (oldUser.isPresent() && oldUser.get().getEmployee() != null 
							&& oldUser.get().getEmployee().getId().equals(savedEmployee.getId())) {
						oldUser.get().setEmployee(null);
						userDao.save(oldUser.get());
						log.info("Unlinked employee {} from old user {} after email change", 
							savedEmployee.getId(), oldUser.get().getId());
					}
				}
				
				// Luego, vincular con usuario que tenga el nuevo email
				Optional<ExpenseUser> matchingUser = userDao.findByEmail(newEmail);
				if (matchingUser.isPresent()) {
					ExpenseUser user = matchingUser.get();
					if (user.getEmployee() == null) {
						user.setEmployee(savedEmployee);
						userDao.save(user);
						log.info("Auto-linked employee {} with user {} after email update", 
							savedEmployee.getId(), user.getId());
					}
				}
			}
			
			return savedEmployee;
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
