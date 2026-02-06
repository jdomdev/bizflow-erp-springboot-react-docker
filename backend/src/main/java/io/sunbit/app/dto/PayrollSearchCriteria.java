package io.sunbit.app.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criterios de búsqueda y filtrado de nóminas con soporte de paginación.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollSearchCriteria {
    
    // Búsqueda de texto (en nombre/apellido del empleado)
    private String search;
    
    // Filtro por usuario propietario
    private Long userId;
    
    // Filtro por empleado específico
    private Long employeeId;
    
    // Filtros de rango de salario
    private Double minAmount;
    private Double maxAmount;
    
    // Filtros de fecha
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    // Ordenamiento
    private String sortBy;
    private String sortDirection;
}
