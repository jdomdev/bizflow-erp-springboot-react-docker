import React from 'react';
import { Calendar } from 'lucide-react';
import Input from '../Input';

/**
 * DateRangeFilter component for filtering data by date range
 * @param {Object} props - Component props
 * @param {string} props.startDate - Start date value (YYYY-MM-DD)
 * @param {string} props.endDate - End date value (YYYY-MM-DD)
 * @param {Function} props.onStartDateChange - Callback for start date changes
 * @param {Function} props.onEndDateChange - Callback for end date changes
 * @param {string} props.label - Label for the filter group
 */
function DateRangeFilter({ 
  startDate, 
  endDate, 
  onStartDateChange, 
  onEndDateChange,
  label = 'Rango de Fechas' 
}) {
  return (
    <div className="space-y-2">
      <label className="block text-sm font-medium text-slate-300">{label}</label>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
        <div className="relative">
          <Input
            type="date"
            value={startDate}
            onChange={(e) => onStartDateChange(e.target.value)}
            placeholder="Fecha inicial"
            className="pl-10"
          />
          <Calendar className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-400" />
        </div>
        <div className="relative">
          <Input
            type="date"
            value={endDate}
            onChange={(e) => onEndDateChange(e.target.value)}
            placeholder="Fecha final"
            className="pl-10"
          />
          <Calendar className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-400" />
        </div>
      </div>
    </div>
  );
}

export default DateRangeFilter;
