import { useState, useEffect } from 'react';

/**
 * Hook to get and subscribe to items per page preference from localStorage
 */
export function useItemsPerPage(defaultValue = 10) {
  const [itemsPerPage, setItemsPerPage] = useState(() => {
    const stored = localStorage.getItem('itemsPerPage');
    return stored ? parseInt(stored, 10) : defaultValue;
  });

  useEffect(() => {
    const handleChange = (event) => {
      setItemsPerPage(event.detail);
    };

    window.addEventListener('itemsPerPageChanged', handleChange);
    return () => window.removeEventListener('itemsPerPageChanged', handleChange);
  }, []);

  return itemsPerPage;
}
