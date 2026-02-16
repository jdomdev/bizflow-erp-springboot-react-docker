package io.sunbit.app.dao;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import io.sunbit.app.entity.Expense;

/**
 * JPA Specifications for building dynamic queries for Expense entity.
 * Uses Specification pattern to construct type-safe queries at runtime,
 * avoiding PostgreSQL null parameter type inference issues.
 */
public class ExpenseSpecifications {
    
    /**
     * Filter by user ID (null matches all users)
     */
    public static Specification<Expense> hasUserId(Long userId) {
        return (root, query, cb) -> {
            if (userId == null) {
                return cb.conjunction(); // Always true - no filter
            }
            return cb.equal(root.get("expenseUser").get("id"), userId);
        };
    }
    
    /**
     * Search in concept, note, or user name/surname fields (case insensitive)
     */
    public static Specification<Expense> searchInConceptOrNote(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isEmpty()) {
                return cb.conjunction(); // Always true - no filter
            }
            String lowerSearch = "%" + search.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("concept")), lowerSearch),
                cb.like(cb.lower(cb.coalesce(root.get("note"), "")), lowerSearch),
                cb.like(cb.lower(root.get("expenseUser").get("name")), lowerSearch),
                cb.like(cb.lower(root.get("expenseUser").get("surname")), lowerSearch),
                cb.like(cb.lower(root.get("expenseUser").get("email")), lowerSearch)
            );
        };
    }
    
    /**
     * Filter by minimum amount (null matches all)
     */
    public static Specification<Expense> hasMinAmount(Double minAmount) {
        return (root, query, cb) -> {
            if (minAmount == null) {
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("amount"), minAmount);
        };
    }
    
    /**
     * Filter by maximum amount (null matches all)
     */
    public static Specification<Expense> hasMaxAmount(Double maxAmount) {
        return (root, query, cb) -> {
            if (maxAmount == null) {
                return cb.conjunction();
            }
            return cb.lessThanOrEqualTo(root.get("amount"), maxAmount);
        };
    }
    
    /**
     * Filter by start date (null matches all)
     */
    public static Specification<Expense> hasStartDate(LocalDateTime startDate) {
        return (root, query, cb) -> {
            if (startDate == null) {
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("expenseDate"), startDate);
        };
    }
    
    /**
     * Filter by end date (null matches all)
     */
    public static Specification<Expense> hasEndDate(LocalDateTime endDate) {
        return (root, query, cb) -> {
            if (endDate == null) {
                return cb.conjunction();
            }
            return cb.lessThanOrEqualTo(root.get("expenseDate"), endDate);
        };
    }
    
    /**
     * Combines all filters into a single specification
     */
    public static Specification<Expense> withFilters(
            Long userId,
            String search,
            Double minAmount,
            Double maxAmount,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        return Specification
            .where(hasUserId(userId))
            .and(searchInConceptOrNote(search))
            .and(hasMinAmount(minAmount))
            .and(hasMaxAmount(maxAmount))
            .and(hasStartDate(startDate))
            .and(hasEndDate(endDate));
    }
}
