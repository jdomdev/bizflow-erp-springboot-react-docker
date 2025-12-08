package io.sunbit.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class ExpenseUserDto {
    @NonNull
    private Long id;
    @NonNull
    private String email;
    @NonNull
    private String name;
    @NonNull
    private String surname;
}
