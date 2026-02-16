package io.sunbit.app.dao;

import java.time.LocalDateTime;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

import org.springframework.data.jpa.domain.Specification;

import io.sunbit.app.entity.Employee;
import io.sunbit.app.entity.Payroll;

/**
 * JPA Specifications for building dynamic queries for Payroll entity.
 * Uses Specification pattern to construct type-safe queries at runtime,
 * avoiding PostgreSQL null parameter type inference issues.
 */
public class PayrollSpecifications {
    
    /**
     * Filter by user ID (null matches all users)
     */
    public static Specification<Payroll> hasUserId(Long userId) {
        return (root, query, cb) -> {
            if (userId == null) {
                return cb.conjunction(); // Always true - no filter
            }
            return cb.equal(root.get("expenseUser").get("id"), userId);
        };
    }
    
    /**
     * Search in employee name or surname fields (case insensitive)
     */
    public static Specification<Payroll> searchInEmployeeName(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isEmpty()) {
                return cb.conjunction(); // Always true - no filter
            }
            String lowerSearch = "%" + search.toLowerCase() + "%";
            Join<Payroll, Employee> employee = root.join("employee", JoinType.LEFT);
            return cb.or(
                cb.like(cb.lower(employee.get("name")), lowerSearch),
                cb.like(cb.lower(employee.get("surname")), lowerSearch)
            );
        };
    }
    
    /**
     * Filter by minimum amount (null matches all)
     */
    public static Specification<Payroll> hasMinAmount(Double minAmount) {
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
    public static Specification<Payroll> hasMaxAmount(Double maxAmount) {
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
    public static Specification<Payroll> hasStartDate(LocalDateTime startDate) {
        return (root, query, cb) -> {
            if (startDate == null) {
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("payrollDate"), startDate);
        };
    }
    
    /**
     * Filter by end date (null matches all)
     */
    public static Specification<Payroll> hasEndDate(LocalDateTime endDate) {
        return (root, query, cb) -> {
            if (endDate == null) {
                return cb.conjunction();
            }
            return cb.lessThanOrEqualTo(root.get("payrollDate"), endDate);
        };
    }
    
    /**
     * Combines all filters into a single specification
     */
    public static Specification<Payroll> withFilters(
            Long userId,
            String search,
            Double minAmount,
            Double maxAmount,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        return Specification
            .where(hasUserId(userId))
            .and(searchInEmployeeName(search))
            .and(hasMinAmount(minAmount))
            .and(hasMaxAmount(maxAmount))
            .and(hasStartDate(startDate))
            .and(hasEndDate(endDate));
    }
}
