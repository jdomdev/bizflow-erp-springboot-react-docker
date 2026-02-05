package io.sunbit.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.sunbit.app.security.dto.RoleDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseUserDto {
    private Long id;
    @NonNull
    private String email;
    @NonNull
    private String name;
    @NonNull
    private String surname;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private java.util.List<Long> roleIds;
    private java.util.List<RoleDto> roleDtos;
    private Long employeeId;
    private String employeeName;
    private String employeePosition;

    // Constructor sin roleDtos para compatibilidad
    public ExpenseUserDto(Long id, String email, String name, String surname, String password, java.util.List<Long> roleIds) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.roleIds = roleIds;
        this.roleDtos = null;
        this.employeeId = null;
    }
}
