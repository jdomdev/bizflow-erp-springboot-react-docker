package io.sunbit.app.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseCreateRequest {
    
    @NotNull(message = "Concept is required")
    @Length(min = 3, max = 128, message = "Concept must be between 3 and 128 characters")
    private String concept;
    
    @Length(max = 255, message = "Note must not exceed 255 characters")
    private String note;
    
    @NotNull(message = "Expense date is required")
    private LocalDateTime expenseDate;
    
    @NotNull(message = "Amount is required")
    private Double amount;
    
    // Optional: if not provided, will be extracted from JWT token
    private Long expenseUserId;
}
