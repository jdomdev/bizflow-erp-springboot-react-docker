/**
 * Utility functions for date operations
 */

/**
 * Check if two dates are in the same month and year
 * @param {Date|string} date1 - First date
 * @param {Date|string} date2 - Second date
 * @returns {boolean} - True if dates are in the same month and year
 */
export const isSameMonth = (date1, date2) => {
  const d1 = new Date(date1);
  const d2 = new Date(date2);
  
  return d1.getMonth() === d2.getMonth() && 
         d1.getFullYear() === d2.getFullYear();
};

/**
 * Get month label for a date
 * @param {Date|string} date - Date to format
 * @param {string} locale - Locale string (default: 'es-ES')
 * @returns {string} - Formatted month label (e.g., "ene 24")
 */
export const getMonthLabel = (date, locale = 'es-ES') => {
  const d = new Date(date);
  return d.toLocaleDateString(locale, { month: 'short', year: '2-digit' });
};

/**
 * Get last N months data structure for charts
 * @param {number} months - Number of months to generate (default: 6)
 * @param {Date} endDate - End date (default: now)
 * @returns {Array} - Array of month objects with date and label
 */
export const getLastNMonths = (months = 6, endDate = new Date()) => {
  const result = [];
  
  for (let i = months - 1; i >= 0; i--) {
    const date = new Date(endDate.getFullYear(), endDate.getMonth() - i, 1);
    result.push({
      date,
      label: getMonthLabel(date),
      month: date.getMonth(),
      year: date.getFullYear(),
    });
  }
  
  return result;
};

/**
 * Format date to ISO string (YYYY-MM-DD)
 * @param {Date|string} date - Date to format
 * @returns {string} - ISO formatted date string
 */
export const toISODateString = (date) => {
  const d = new Date(date);
  return d.toISOString().split('T')[0];
};

/**
 * Format date to localized string
 * @param {Date|string} date - Date to format
 * @param {string} locale - Locale string (default: 'es-ES')
 * @returns {string} - Formatted date string
 */
export const formatDate = (date, locale = 'es-ES') => {
  const d = new Date(date);
  return d.toLocaleDateString(locale);
};

/**
 * Get date range for current month
 * @returns {Object} - Object with startDate and endDate
 */
export const getCurrentMonthRange = () => {
  const now = new Date();
  const startDate = new Date(now.getFullYear(), now.getMonth(), 1);
  const endDate = new Date(now.getFullYear(), now.getMonth() + 1, 0);
  
  return { startDate, endDate };
};
