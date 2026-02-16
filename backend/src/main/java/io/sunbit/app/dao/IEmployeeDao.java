package io.sunbit.app.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.sunbit.app.entity.Employee;

@Repository
public interface IEmployeeDao extends JpaRepository<Employee, Long> {

	// @Query(value="SELECT * FROM employee WHERE employee.name=?1 and
	// employee.surname=?2", nativeQuery=true)
	Optional<Employee> findByNameAndSurnameAllIgnoreCase(String name, String surname);

	Optional<Employee> findByEmail(String email);
}
