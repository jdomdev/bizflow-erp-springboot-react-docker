package io.sunbit.app.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for expense search/filter criteria.
 * Used for paginated expense queries with filters.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseSearchCriteria {
    
    /** Search term for concept or note */
    private String search;
    
    /** Filter by user ID */
    private Long userId;
    
    /** Filter by minimum amount */
    private Double minAmount;
    
    /** Filter by maximum amount */
    private Double maxAmount;
    
    /** Filter by start date */
    private LocalDateTime startDate;
    
    /** Filter by end date */
    private LocalDateTime endDate;
    
    /** Sort field */
    private String sortBy;
    
    /** Sort direction (asc/desc) */
    private String sortDirection;
}
