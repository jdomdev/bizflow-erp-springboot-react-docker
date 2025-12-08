package io.sunbit.app.dto;

import io.sunbit.app.security.entity.ExpenseUser;

public class ExpenseUserMapper {
    public static ExpenseUser dtoToExpenseUserWithId(ExpenseUserDto dto) {
        ExpenseUser user = new ExpenseUser();
        user.setId(dto.getId());
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        return user;
    }

    public static ExpenseUserDto expenseUserToDtoWithId(ExpenseUser user) {
        return new ExpenseUserDto(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getSurname()
        );
    }
}
