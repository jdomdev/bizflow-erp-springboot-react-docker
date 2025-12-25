package io.sunbit.app.dto;

import io.sunbit.app.security.entity.ExpenseUser;

public class ExpenseUserMapper {

    public static ExpenseUser dtoToExpenseUserWithId(ExpenseUserDto dto) {
        ExpenseUser user = new ExpenseUser();
        user.setId(dto.getId());
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        // roles no se asignan aquí, solo por id
        return user;
    }

    public static ExpenseUserDto expenseUserToDtoWithId(ExpenseUser user) {
        java.util.List<Long> roleIds = null;
        if (user.getRoles() != null) {
            roleIds = user.getRoles().stream().map(r -> r.getId()).toList();
        }
        return new ExpenseUserDto(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getSurname(),
            null,
            roleIds
        );
    }
}
