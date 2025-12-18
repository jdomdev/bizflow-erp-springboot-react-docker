// import org.springframework.lang.NonNull;
package io.sunbit.app.security.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import io.sunbit.app.entity.Employee;
import io.sunbit.app.exception.RoleNotFoundException;
import io.sunbit.app.security.dao.IRoleDao;
import io.sunbit.app.security.dao.IUserDao;
import io.sunbit.app.security.entity.ExpenseUser;
import io.sunbit.app.security.entity.Role;
import io.sunbit.app.service.EmployeeServiceImpl;

@Service
public class UserServiceImpl implements IUserService, UserDetailsService {

	private final String ROLE_USER = "ROLE_USER";
	@Autowired
	private IUserDao userDao;
	@Autowired
	private IRoleDao roleDao;
	@Autowired
	@Lazy
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private EmployeeServiceImpl employeeService;
	@Autowired
	private EntityManager entityManager;

	@Override
	@Transactional
		@SuppressWarnings("null")
		public ExpenseUser save(ExpenseUser user) throws Exception {
		       ExpenseUser savedUser = new ExpenseUser();
		       ExpenseUser settedUser = new ExpenseUser();
		       try {
			       try {
				       employeeService.findByEmail(user.getEmail());
			       } catch (Exception e) {
				       // Employee not found, which is OK for signup
			       }

			       if (user.getRoles() == null) {
				       user.setRoles(new ArrayList<>());
			       }

			       if (user.getRoles().isEmpty()) {
				       Role defaultRole = roleDao.findByName(ROLE_USER)
					       .orElseThrow(() -> new RoleNotFoundException("Default role ROLE_USER is missing"));
				       user.getRoles().add(defaultRole);
			       }

			       if ((user.getPassword() != null) && !user.getPassword().startsWith("$2")) {
				       user.setPassword(passwordEncoder.encode(user.getPassword()));
			       }

			       settedUser = setUser(user);
			       savedUser = userDao.save(settedUser);
		       } catch (Exception e) {
			       throw e;
		       }
		       return savedUser;
	       }

	private ExpenseUser setUser(ExpenseUser user) {
		ExpenseUser settedUser = new ExpenseUser();
		if (user.getId() != null)
			settedUser.setId(user.getId());
		settedUser.setEmail(user.getEmail());
		settedUser.setName(user.getName());
		settedUser.setSurname(user.getSurname());
		// Password is already encoded in the caller, just use it as is
		settedUser.setPassword(user.getPassword());

		if ((user.getEmployee() != null) && (user.getEmployee().getId() != null)) {
			Employee employeeRef = entityManager.getReference(Employee.class, user.getEmployee().getId());
			settedUser.setEmployee(employeeRef);
		} else {
			settedUser.setEmployee(null);
		}

		settedUser.setRoles(new ArrayList<>());
		Collection<Role> requestRoles = (user.getRoles() != null) ? user.getRoles() : List.of();
		for (Role role : requestRoles) {
			if (role.getId() == null) {
				throw new RoleNotFoundException("Role id is required for assignment");
			}
			Optional<Role> optRole = roleDao.findById(role.getId());
			if (optRole.isEmpty()) {
				throw new RoleNotFoundException(
					"The Role id: " + role.getId() + " is not present in the database");
			}
			settedUser.getRoles().add(optRole.get());
		}
		return settedUser;
	}

	@Override
	@Transactional
	@SuppressWarnings("null")
	public ExpenseUser update(Long id, ExpenseUser user) throws Exception {
		ExpenseUser updatedUser = new ExpenseUser();
		try {
			Optional<ExpenseUser> optionalUser = userDao.findById(id);
			if (!optionalUser.isEmpty()) {
				ExpenseUser currentUser = optionalUser.get();
				if (user.getPassword() == null || user.getPassword().isBlank()) {
					user.setPassword(currentUser.getPassword());
				} else if (!user.getPassword().startsWith("$2")) {
					user.setPassword(passwordEncoder.encode(user.getPassword()));
				}

				if (user.getRoles() == null) {
					user.setRoles(new ArrayList<>());
				}

				user.setId(currentUser.getId());
				ExpenseUser settedUser = setUser(user);
				updatedUser = userDao.save(settedUser);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return updatedUser;
	}

	// Load User by 'email', NOT by name.
	@Override
	public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
		Optional<ExpenseUser> optionalUser = userDao.findByEmail(userEmail);
		if (optionalUser.isEmpty())
			throw new UsernameNotFoundException("User or Password INVALIDS");
		return optionalUser.get();
	}

	public Collection<? extends GrantedAuthority> mappAuthorityRole(Collection<Role> roles) {
		return roles
				.stream()
				.map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
	}

	@Override
	public Optional<ExpenseUser> findByEmail(String email) throws Exception {
		Optional<ExpenseUser> optionalUser = userDao.findByEmail(email);
		return optionalUser;
	}

	@Override
	public List<ExpenseUser> findAll() throws Exception {
		return userDao.findAll();
	}

	@Override
	@SuppressWarnings("null")
	public Optional<ExpenseUser> findById(Long id) throws Exception {
		Optional<ExpenseUser> optUser = userDao.findById(id);
		return optUser;
	}

	@Override
	@Transactional
	@SuppressWarnings("null")
	public Boolean delete(Long id) throws Exception {
		boolean isDeleted = false;
		Optional<ExpenseUser> optUser = userDao.findById(id);
		if (optUser.isPresent()) {
			optUser.get().getRoles().clear();
			userDao.delete(optUser.get());
			isDeleted = true;
		}
		return isDeleted;
	}
}
