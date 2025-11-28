package io.sunbit.app.security.dto;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import io.sunbit.app.security.entity.ExpenseUser;
import io.sunbit.app.security.entity.Role;

public class UserMapper {
	// With ID.
	public static ExpenseUser dtoToUserWithId(UserDto userDto) {
		return new ExpenseUser(
				userDto.getId(),
				userDto.getName(),
				userDto.getSurname(),
				userDto.getEmail(),
				userDto.getPassword(),
				dtosToRoles(userDto.getRoleDtos()));
	}

	public static UserDto userToDtoWithId(ExpenseUser user) {
		return new UserDto(
				user.getId(),
				user.getName(),
				user.getSurname(),
				user.getEmail(),
				user.getPassword(),
				rolesToDtos(user.getRoles()));
	}

	// Without ID.
	public static ExpenseUser dtoToUser(UserDto userDto) {
		return new ExpenseUser(
				userDto.getName(),
				userDto.getSurname(),
				userDto.getEmail(),
				userDto.getPassword(),
				dtosToRoles(userDto.getRoleDtos()));
	}

	public static UserDto userToDto(ExpenseUser user) {
		return new UserDto(
				user.getName(),
				user.getSurname(),
				user.getEmail(),
				user.getPassword(),
				rolesToDtos(user.getRoles()));
	}

	// Role/DTO Set.
	private static Set<Role> dtosToRoles(Collection<RoleDto> roleDtos) {
		Set<Role> roles = new HashSet<Role>();
		for (RoleDto roleDto : roleDtos) {
			roles.add(RoleMapper.dtoToRole(roleDto));
		}
		return roles;
	}

	private static Set<RoleDto> rolesToDtos(Collection<Role> roles) {
		Set<RoleDto> roleDtos = new HashSet<RoleDto>();
		for (Role role : roles) {
			roleDtos.add(RoleMapper.roleToDto(role));
		}
		return roleDtos;
	}
}
