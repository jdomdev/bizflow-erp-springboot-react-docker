package io.sunbit.app.entity;

/**
 * Types of notifications in the system
 */
public enum NotificationType {
    // Expense notifications
    EXPENSE_CREATED,
    EXPENSE_UPDATED,
    EXPENSE_APPROVED,
    EXPENSE_REJECTED,
    EXPENSE_DELETED,
    
    // Payroll notifications
    PAYROLL_GENERATED,
    PAYROLL_AVAILABLE,
    PAYROLL_REMINDER,
    
    // Employee notifications
    EMPLOYEE_LINKED,
    EMPLOYEE_UNLINKED,
    
    // User notifications
    USER_REGISTERED,
    USER_ROLE_CHANGED,
    
    // System notifications
    SYSTEM_ALERT,
    BUDGET_EXCEEDED,
    
    // General
    INFO,
    WARNING
}
