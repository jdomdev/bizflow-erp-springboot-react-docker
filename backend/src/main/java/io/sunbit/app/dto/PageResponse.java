package io.sunbit.app.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic paginated response wrapper.
 * Used to return paginated data from API endpoints.
 * 
 * @param <T> The type of items in the page
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    
    /** Content of the current page */
    private List<T> content;
    
    /** Current page number (0-indexed) */
    private int pageNumber;
    
    /** Size of the page */
    private int pageSize;
    
    /** Total number of elements across all pages */
    private long totalElements;
    
    /** Total number of pages */
    private int totalPages;
    
    /** Whether this is the first page */
    private boolean first;
    
    /** Whether this is the last page */
    private boolean last;
    
    /** Whether the page has content */
    private boolean empty;
    
    /**
     * Creates a PageResponse from a Spring Data Page object.
     */
    public static <T> PageResponse<T> from(org.springframework.data.domain.Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .build();
    }
}
