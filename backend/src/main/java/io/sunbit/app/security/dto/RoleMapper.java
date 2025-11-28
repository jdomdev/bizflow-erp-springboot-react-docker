package io.sunbit.app.security.dto;

import io.sunbit.app.security.entity.Role;

public class RoleMapper {
	// Without ID.
	public static Role dtoToRole(RoleDto roleDto) {
		return new Role(roleDto.getName());
	}

	public static RoleDto roleToDto(Role role) {
		return new RoleDto(role.getName());
	}

	// With ID.
	public static Role dtoToRoleWithId(RoleDto roleDto) {
		return new Role(
				roleDto.getId(),
				roleDto.getName());
	}

	public static RoleDto roleToDtoWithId(Role role) {
		return new RoleDto(
				role.getId(),
				role.getName());
	}
}
