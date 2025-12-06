package io.sunbit.app.util;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.sunbit.app.dao.IEmployeeDao;
import io.sunbit.app.entity.Employee;
import io.sunbit.app.security.dao.IUserDao;
import io.sunbit.app.security.entity.ExpenseUser;
import io.sunbit.app.security.jwt.JwtAuthenticationUtil;

@Component
public class EmployeeUtil {

	@Autowired
	private IEmployeeDao employeeDao;
	@Autowired
	private JwtAuthenticationUtil jwtAuthUtil;
	@Autowired
	private IUserDao userDao;

	public Boolean existsInDb(Employee employee) {
		boolean exists = false;
		try {
			if ((employeeDao.findById(employee.getId())) != null) {
				exists = true;
			}
		} catch (Exception e) {
			// Log error appropriately
			return false;
		}
		return exists;
	}

	public Boolean matchEmployeeUserEmail(Employee employee, String token) throws Exception {
		boolean isMatch = false;
		long tokenUserId = jwtAuthUtil.extractTokenUserId(token);
		Optional<ExpenseUser> optTokenUser = userDao.findById(tokenUserId);
		if (optTokenUser.isPresent() && optTokenUser.get().getEmail().equalsIgnoreCase(employee.getEmail()))
			isMatch = true;
		else
			throw new Exception("ERROR -> The token user email DOESN'T MATCH with the employee email.");
		return isMatch;
	}
}
