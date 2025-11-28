package io.sunbit.app.security.dto;

import java.util.Collection;
import java.util.HashSet;

import io.sunbit.app.security.entity.ExpenseUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto extends ExpenseUser {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String name;
	private String surname;
	private String email;
	private String password;
	private Collection<RoleDto> roleDtos = new HashSet<>();

	// Constructor WITHOUT id.
	public UserDto(String name, String surname, String email, String password, Collection<RoleDto> roleDtos) {
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.password = password;
		this.roleDtos = roleDtos;
	}
}
