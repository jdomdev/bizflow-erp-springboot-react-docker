package io.sunbit.app.security.service;

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
	public ExpenseUser save(ExpenseUser user) throws Exception {
		ExpenseUser savedUser = new ExpenseUser();
		ExpenseUser settedUser = new ExpenseUser();
		try {
			// Try to find associated employee, but it's optional for signup
			Employee employee = null;
			try {
				employee = employeeService.findByEmail(user.getEmail());
			} catch (Exception e) {
				// Employee not found, which is OK for signup
				employee = null;
			}

			// Assign default USER role if not already assigned
			if ((user.getRoles() == null) || (user.getRoles().size() == 0)) {
				user.getRoles().add(roleDao.findByName(ROLE_USER).get());
			}

			settedUser = setUser(user);
			savedUser = userDao.save(settedUser);
			// entityManager.persist(settedUser);
		} catch (Exception e) {
			// System.out.println("e.getCause(): " + e.getCause());
			throw new Exception(e.getCause());
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

		for (Role role : user.getRoles()) {
			try {
				if ((roleDao.findById(role.getId()) != null)) {
					settedUser.getRoles().add(entityManager.merge(role));
				}
			} catch (RoleNotFoundException e) {
				System.out.println(e.getMessage());
				throw new RoleNotFoundException(
						"The Role name: " + role.getName() + " with id: " + role.getId() + " IS NOT in Data Base");
			}
		}
		return settedUser;
	}

	@Override
	@Transactional
	public ExpenseUser update(Long id, ExpenseUser user) throws Exception {
		ExpenseUser updatedUser = new ExpenseUser();
		try {
			Optional<ExpenseUser> optionalUser = userDao.findById(id);
			if (!optionalUser.isEmpty()) {
				// User settedUser = setUser(optionalUser.get());
				// updatedUser = userDao.save(settedUser);
				user.setPassword(passwordEncoder.encode(user.getPassword()));
				updatedUser = userDao.save(user);
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
		if (optionalUser == null || optionalUser.isEmpty())
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
		ExpenseUser searchedUser = null;
		Optional<ExpenseUser> optionalUser = userDao.findByEmail(email);
		if (!optionalUser.isEmpty()) {
			searchedUser = optionalUser.get();
		}
		return Optional.ofNullable(searchedUser);
	}

	@Override
	public List<ExpenseUser> findAll() throws Exception {
		return userDao.findAll();
	}

	@Override
	public Optional<ExpenseUser> findById(Long id) throws Exception {
		Optional<ExpenseUser> optUser = userDao.findById(id);
		return optUser;
	}

	@Override
	@Transactional
	public Boolean delete(Long id) throws Exception {
		boolean isDeleted = false;
		Optional<ExpenseUser> optUser = userDao.findById(id);
		if (optUser != null) {
			optUser.get().getRoles().clear();
			userDao.delete(optUser.get());
			isDeleted = true;
		}
		return isDeleted;
	}
}
