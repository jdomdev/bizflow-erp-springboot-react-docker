import React from 'react';

/**
 * SelectFilter component for dropdown-based filtering
 * @param {Object} props - Component props
 * @param {string} props.value - Selected value
 * @param {Function} props.onChange - Callback for selection changes
 * @param {Array} props.options - Array of option objects {value, label}
 * @param {string} props.label - Label for the select input
 * @param {string} props.placeholder - Placeholder text
 */
function SelectFilter({ 
  value, 
  onChange, 
  options = [], 
  label, 
  placeholder = 'Seleccionar...' 
}) {
  return (
    <div className="space-y-2">
      {label && <label className="block text-sm font-medium text-slate-300">{label}</label>}
      <select
        value={value}
        onChange={(e) => onChange(e.target.value)}
        className="w-full px-4 py-2.5 bg-slate-800/50 border border-slate-700 rounded-lg text-white focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
      >
        <option value="">{placeholder}</option>
        {options.map((option) => (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>
    </div>
  );
}

export default SelectFilter;
