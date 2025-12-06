import React from 'react';
import { Search } from 'lucide-react';
import Input from '../Input';

/**
 * SearchFilter component for text-based filtering
 * @param {Object} props - Component props
 * @param {string} props.value - Search input value
 * @param {Function} props.onChange - Callback for search input changes
 * @param {string} props.placeholder - Placeholder text
 * @param {string} props.label - Label for the search input
 */
function SearchFilter({ 
  value, 
  onChange, 
  placeholder = 'Buscar...', 
  label = 'Buscar' 
}) {
  return (
    <div className="space-y-2">
      <label className="block text-sm font-medium text-slate-300">{label}</label>
      <div className="relative">
        <Input
          type="text"
          value={value}
          onChange={(e) => onChange(e.target.value)}
          placeholder={placeholder}
          className="pl-10"
        />
        <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-400" />
      </div>
    </div>
  );
}

export default SearchFilter;
