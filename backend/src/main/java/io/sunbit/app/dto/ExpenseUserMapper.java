package io.sunbit.app.dto;

import io.sunbit.app.security.dto.RoleDto;
import io.sunbit.app.security.entity.ExpenseUser;

import java.util.ArrayList;
import java.util.List;

public class ExpenseUserMapper {

    public static ExpenseUser dtoToExpenseUserWithId(ExpenseUserDto dto) {
        ExpenseUser user = new ExpenseUser();
        user.setId(dto.getId());
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        user.setPassword(dto.getPassword());
        // roles no se asignan aquí, solo por id
        return user;
    }

    public static ExpenseUserDto expenseUserToDtoWithId(ExpenseUser user) {
        List<Long> roleIds = new ArrayList<>();
        List<RoleDto> roleDtos = new ArrayList<>();
        
        if (user.getRoles() != null) {
            for (var role : user.getRoles()) {
                roleIds.add(role.getId());
                roleDtos.add(new RoleDto(role.getId(), role.getName()));
            }
        }
        
        ExpenseUserDto dto = new ExpenseUserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        dto.setRoleIds(roleIds);
        dto.setRoleDtos(roleDtos);
        dto.setEmployeeId(user.getEmployee() != null ? user.getEmployee().getId() : null);
        if (user.getEmployee() != null) {
            var emp = user.getEmployee();
            dto.setEmployeeName(emp.getName() + " " + emp.getSurname());
            dto.setEmployeePosition(emp.getPosition() != null ? emp.getPosition().getName() : null);
        }
        return dto;
    }
}
