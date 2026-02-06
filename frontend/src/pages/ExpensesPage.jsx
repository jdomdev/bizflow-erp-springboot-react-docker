import { useState, useEffect, useCallback, useMemo } from 'react';
import { useLocation, useSearchParams } from 'react-router-dom';
import { expenseService } from '../services/api';
import { useAuthStore } from '../store/authStore';
import ExpenseForm from '../components/ExpenseForm';
import ExpenseList from '../components/ExpenseList';
import Pagination from '../components/Pagination';
import { Plus, Users, UserCheck, Search, Filter, X, Download, List, Calendar, ChevronDown, ChevronRight } from 'lucide-react';

export default function ExpensesPage() {
  const location = useLocation();
  const [searchParams] = useSearchParams();
  const { user } = useAuthStore();
  
  // Data state
  const [expenses, setExpenses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  
  // Form state
  const [showForm, setShowForm] = useState(false);
  const [editingExpense, setEditingExpense] = useState(null);
  
  // View and search state
  const [viewMode, setViewMode] = useState('mine'); // 'mine' or 'all'
  const [displayMode, setDisplayMode] = useState('list'); // 'list' or 'grouped'
  const [searchTerm, setSearchTerm] = useState('');
  const [showFilters, setShowFilters] = useState(false);
  const [expandedMonths, setExpandedMonths] = useState({});
  
  // Filter state
  const [filters, setFilters] = useState({
    minAmount: '',
    maxAmount: '',
    startDate: '',
    endDate: ''
  });
  
  // Pagination state (server-side)
  const [currentPage, setCurrentPage] = useState(0); // API uses 0-indexed
  const [pageSize] = useState(15);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const isAdmin = user?.roleId === 1;

  // Debounced search
  const [debouncedSearch, setDebouncedSearch] = useState('');
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearch(searchTerm);
      setCurrentPage(0); // Reset to first page on search
    }, 300);
    return () => clearTimeout(timer);
  }, [searchTerm]);

  // Fetch expenses with server-side pagination
  const fetchExpenses = useCallback(async () => {
    try {
      setLoading(true);
      setError('');
      
      if (!user?.id) {
        setError('Por favor, cierra sesión y vuelve a iniciar sesión para actualizar tu perfil.');
        setLoading(false);
        return;
      }
      
      // Build query params for the search endpoint
      const params = new URLSearchParams({
        page: currentPage.toString(),
        size: pageSize.toString(),
        sortBy: 'expenseDate',
        sortDirection: 'desc'
      });
      
      if (debouncedSearch) params.append('search', debouncedSearch);
      if (filters.minAmount) params.append('minAmount', filters.minAmount);
      if (filters.maxAmount) params.append('maxAmount', filters.maxAmount);
      if (filters.startDate) params.append('startDate', filters.startDate + 'T00:00:00');
      if (filters.endDate) params.append('endDate', filters.endDate + 'T23:59:59');
      
      // If not admin or viewing own expenses, the backend will filter by userId automatically
      if (!isAdmin || viewMode === 'mine') {
        params.append('userId', user.id.toString());
      }
      
      const response = await expenseService.search(params.toString());
      const pageData = response.data;
      
      setExpenses(pageData.content || []);
      setTotalPages(pageData.totalPages || 0);
      setTotalElements(pageData.totalElements || 0);
    } catch (error) {
      console.error('Error loading expenses:', error);
      setError('No se pudieron cargar los gastos.');
      setExpenses([]);
    } finally {
      setLoading(false);
    }
  }, [currentPage, pageSize, debouncedSearch, filters, viewMode, user?.id, isAdmin]);

  useEffect(() => {
    fetchExpenses();
  }, [fetchExpenses]);

  // Handle search from URL params (Command Palette)
  useEffect(() => {
    const searchFromUrl = searchParams.get('search');
    if (searchFromUrl) {
      setSearchTerm(searchFromUrl);
      if (isAdmin) setViewMode('all');
    }
  }, [searchParams, isAdmin]);

  // Handle edit from navigation (e.g., from Dashboard)
  useEffect(() => {
    if (location.state?.editExpense) {
      setEditingExpense(location.state.editExpense);
      setShowForm(true);
      window.history.replaceState({}, document.title);
    }
  }, [location.state]);

  const handleExpenseSuccess = (savedExpense, isEdit) => {
    fetchExpenses(); // Refresh from server
    setShowForm(false);
    setEditingExpense(null);
  };

  const handleEdit = (expense) => {
    setEditingExpense(expense);
    setShowForm(true);
  };

  const handleCloseForm = () => {
    setShowForm(false);
    setEditingExpense(null);
  };

  const handleExpenseDeleted = async (id) => {
    if (window.confirm('¿Estás seguro de que deseas eliminar este gasto?')) {
      try {
        await expenseService.delete(id);
        fetchExpenses(); // Refresh from server
      } catch (error) {
        console.error('Error deleting expense:', error);
        alert('Error al eliminar el gasto');
      }
    }
  };

  const handlePageChange = (page) => {
    setCurrentPage(page - 1); // Convert to 0-indexed for API
  };

  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    setFilters(prev => ({ ...prev, [name]: value }));
  };

  const applyFilters = () => {
    setCurrentPage(0);
    fetchExpenses();
  };

  const clearFilters = () => {
    setFilters({ minAmount: '', maxAmount: '', startDate: '', endDate: '' });
    setCurrentPage(0);
  };

  const hasActiveFilters = filters.minAmount || filters.maxAmount || filters.startDate || filters.endDate;

  // Export to CSV
  const exportToCSV = () => {
    const headers = ['Fecha', 'Concepto', 'Nota', 'Importe', 'Usuario'];
    const csvContent = [
      headers.join(','),
      ...expenses.map(e => [
        e.expenseDate?.split('T')[0] || '',
        `"${(e.concept || '').replace(/"/g, '""')}"`,
        `"${(e.note || '').replace(/"/g, '""')}"`,
        e.amount || 0,
        `"${(e.expenseUserName || '').replace(/"/g, '""')}"`
      ].join(','))
    ].join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = `gastos_${new Date().toISOString().split('T')[0]}.csv`;
    link.click();
  };

  const showingFrom = totalElements > 0 ? currentPage * pageSize + 1 : 0;
  const showingTo = Math.min((currentPage + 1) * pageSize, totalElements);

  // Group expenses by month for grouped view - MUST be before any conditional returns
  const groupedExpenses = useMemo(() => {
    const groups = {};
    expenses.forEach(expense => {
      const date = new Date(expense.expenseDate);
      const monthKey = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;
      const monthName = date.toLocaleDateString('es-ES', { month: 'long', year: 'numeric' });
      
      if (!groups[monthKey]) {
        groups[monthKey] = {
          monthKey,
          monthName: monthName.charAt(0).toUpperCase() + monthName.slice(1),
          expenses: [],
          total: 0
        };
      }
      groups[monthKey].expenses.push(expense);
      groups[monthKey].total += expense.amount || 0;
    });
    
    // Sort by month (most recent first)
    return Object.values(groups).sort((a, b) => b.monthKey.localeCompare(a.monthKey));
  }, [expenses]);

  const toggleMonth = (monthKey) => {
    setExpandedMonths(prev => ({
      ...prev,
      [monthKey]: !prev[monthKey]
    }));
  };

  // Loading spinner - AFTER all hooks
  if (loading && expenses.length === 0) return (
    <div className="flex justify-center items-center h-64">
      <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
    </div>
  );

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div className="flex items-center gap-4">
          <h1 className="text-2xl font-bold text-gray-900">
            {viewMode === 'all' ? 'Todos los Gastos' : 'Mis Gastos'}
          </h1>
          
          {/* Admin Toggle */}
          {isAdmin && (
            <div className="flex items-center bg-slate-100 rounded-lg p-1">
              <button
                onClick={() => { setViewMode('mine'); setCurrentPage(0); }}
                className={`flex items-center gap-1.5 px-3 py-1.5 rounded-md text-sm font-medium transition-all ${
                  viewMode === 'mine'
                    ? 'bg-white text-blue-600 shadow-sm'
                    : 'text-slate-600 hover:text-slate-800'
                }`}
              >
                <UserCheck className="h-4 w-4" />
                Míos
              </button>
              <button
                onClick={() => { setViewMode('all'); setCurrentPage(0); }}
                className={`flex items-center gap-1.5 px-3 py-1.5 rounded-md text-sm font-medium transition-all ${
                  viewMode === 'all'
                    ? 'bg-white text-blue-600 shadow-sm'
                    : 'text-slate-600 hover:text-slate-800'
                }`}
              >
                <Users className="h-4 w-4" />
                Todos
              </button>
            </div>
          )}
        </div>

        <div className="flex items-center gap-2">
          {/* View Mode Toggle */}
          <div className="flex items-center bg-slate-100 rounded-lg p-1">
            <button
              onClick={() => setDisplayMode('list')}
              className={`flex items-center gap-1 px-2.5 py-1.5 rounded-md text-sm transition-all ${
                displayMode === 'list'
                  ? 'bg-white text-blue-600 shadow-sm'
                  : 'text-slate-500 hover:text-slate-700'
              }`}
              title="Vista de lista"
            >
              <List className="h-4 w-4" />
            </button>
            <button
              onClick={() => setDisplayMode('grouped')}
              className={`flex items-center gap-1 px-2.5 py-1.5 rounded-md text-sm transition-all ${
                displayMode === 'grouped'
                  ? 'bg-white text-blue-600 shadow-sm'
                  : 'text-slate-500 hover:text-slate-700'
              }`}
              title="Agrupar por mes"
            >
              <Calendar className="h-4 w-4" />
            </button>
          </div>
          
          {/* Export CSV */}
          <button
            onClick={exportToCSV}
            className="flex items-center px-3 py-2 text-slate-600 bg-slate-100 rounded-md hover:bg-slate-200 transition-colors"
            title="Exportar a CSV"
          >
            <Download className="h-5 w-5" />
          </button>
          
          {/* New Expense */}
          <button
            className="flex items-center px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors shadow-sm"
            onClick={() => setShowForm(true)}
          >
            <Plus className="mr-2 h-5 w-5" />
            Nuevo Gasto
          </button>
        </div>
      </div>

      {error && (
        <div className="bg-red-50 text-red-600 p-4 rounded-md">
          {error}
        </div>
      )}

      {showForm && (
        <ExpenseForm
          expense={editingExpense}
          onSuccess={handleExpenseSuccess}
          onCancel={handleCloseForm}
        />
      )}

      {/* Search and Filters */}
      <div className="space-y-3">
        <div className="flex gap-2">
          <div className="relative flex-1">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
            <input
              type="text"
              placeholder="Buscar por concepto, nota o usuario..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>
          <button
            onClick={() => setShowFilters(!showFilters)}
            className={`flex items-center gap-2 px-4 py-2 rounded-lg border transition-colors ${
              showFilters || hasActiveFilters
                ? 'bg-blue-50 border-blue-300 text-blue-700'
                : 'bg-white border-gray-200 text-gray-600 hover:bg-gray-50'
            }`}
          >
            <Filter className="h-5 w-5" />
            Filtros
            {hasActiveFilters && (
              <span className="bg-blue-600 text-white text-xs px-1.5 py-0.5 rounded-full">
                {[filters.minAmount, filters.maxAmount, filters.startDate, filters.endDate].filter(Boolean).length}
              </span>
            )}
          </button>
        </div>

        {/* Filter Panel */}
        {showFilters && (
          <div className="bg-slate-50 border border-slate-200 rounded-lg p-4">
            <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Importe mínimo
                </label>
                <input
                  type="number"
                  name="minAmount"
                  value={filters.minAmount}
                  onChange={handleFilterChange}
                  placeholder="0.00"
                  className="w-full px-3 py-2 border border-gray-200 rounded-md focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Importe máximo
                </label>
                <input
                  type="number"
                  name="maxAmount"
                  value={filters.maxAmount}
                  onChange={handleFilterChange}
                  placeholder="1000.00"
                  className="w-full px-3 py-2 border border-gray-200 rounded-md focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Desde fecha
                </label>
                <input
                  type="date"
                  name="startDate"
                  value={filters.startDate}
                  onChange={handleFilterChange}
                  className="w-full px-3 py-2 border border-gray-200 rounded-md focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Hasta fecha
                </label>
                <input
                  type="date"
                  name="endDate"
                  value={filters.endDate}
                  onChange={handleFilterChange}
                  className="w-full px-3 py-2 border border-gray-200 rounded-md focus:ring-2 focus:ring-blue-500"
                />
              </div>
            </div>
            <div className="flex justify-end gap-2 mt-4">
              {hasActiveFilters && (
                <button
                  onClick={clearFilters}
                  className="flex items-center gap-1 px-3 py-1.5 text-gray-600 hover:text-gray-800"
                >
                  <X className="h-4 w-4" />
                  Limpiar
                </button>
              )}
              <button
                onClick={applyFilters}
                className="px-4 py-1.5 bg-blue-600 text-white rounded-md hover:bg-blue-700"
              >
                Aplicar filtros
              </button>
            </div>
          </div>
        )}
      </div>

      {/* Loading indicator for pagination */}
      {loading && expenses.length > 0 && (
        <div className="flex justify-center py-2">
          <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-blue-600"></div>
        </div>
      )}

      {/* List View */}
      {displayMode === 'list' && (
        <>
          <ExpenseList
            expenses={expenses}
            onDelete={handleExpenseDeleted}
            onEdit={handleEdit}
            searchTerm={searchTerm}
          />

          {/* Pagination */}
          {totalElements > 0 && (
            <Pagination
              currentPage={currentPage + 1}
              totalPages={totalPages}
              onPageChange={handlePageChange}
              totalItems={totalElements}
              itemsPerPage={pageSize}
              showingFrom={showingFrom}
              showingTo={showingTo}
              itemName="gastos"
            />
          )}
        </>
      )}

      {/* Grouped by Month View */}
      {displayMode === 'grouped' && (
        <div className="space-y-4">
          {groupedExpenses.length === 0 ? (
            <div className="text-center py-12 bg-white rounded-lg border border-gray-100">
              <Calendar className="h-12 w-12 mx-auto text-gray-300 mb-4" />
              <p className="text-gray-500">No hay gastos para mostrar</p>
            </div>
          ) : (
            groupedExpenses.map(group => (
              <div key={group.monthKey} className="bg-white rounded-lg border border-gray-100 overflow-hidden">
                {/* Month Header */}
                <button
                  onClick={() => toggleMonth(group.monthKey)}
                  className="w-full flex items-center justify-between p-4 hover:bg-slate-50 transition-colors"
                >
                  <div className="flex items-center gap-3">
                    {expandedMonths[group.monthKey] ? (
                      <ChevronDown className="h-5 w-5 text-slate-400" />
                    ) : (
                      <ChevronRight className="h-5 w-5 text-slate-400" />
                    )}
                    <div className="flex items-center gap-2">
                      <Calendar className="h-5 w-5 text-blue-500" />
                      <span className="font-semibold text-slate-800">{group.monthName}</span>
                    </div>
                    <span className="text-sm text-slate-500">
                      ({group.expenses.length} {group.expenses.length === 1 ? 'gasto' : 'gastos'})
                    </span>
                  </div>
                  <div className="text-right">
                    <span className="font-bold text-emerald-600">
                      ${group.total.toLocaleString('es-ES', { minimumFractionDigits: 2 })}
                    </span>
                  </div>
                </button>

                {/* Expenses in this month */}
                {expandedMonths[group.monthKey] && (
                  <div className="border-t border-gray-100">
                    <table className="w-full">
                      <thead className="bg-slate-50">
                        <tr>
                          <th className="px-4 py-2 text-left text-xs font-semibold text-slate-500 uppercase">Fecha</th>
                          <th className="px-4 py-2 text-left text-xs font-semibold text-slate-500 uppercase">Concepto</th>
                          <th className="px-4 py-2 text-left text-xs font-semibold text-slate-500 uppercase hidden md:table-cell">Nota</th>
                          <th className="px-4 py-2 text-right text-xs font-semibold text-slate-500 uppercase">Importe</th>
                          <th className="px-4 py-2 text-right text-xs font-semibold text-slate-500 uppercase">Acciones</th>
                        </tr>
                      </thead>
                      <tbody className="divide-y divide-gray-100">
                        {group.expenses.map(expense => (
                          <tr key={expense.id} className="hover:bg-slate-50">
                            <td className="px-4 py-3 text-sm text-slate-600">
                              {new Date(expense.expenseDate).toLocaleDateString('es-ES', { day: 'numeric', month: 'short' })}
                            </td>
                            <td className="px-4 py-3 text-sm font-medium text-slate-800">{expense.concept}</td>
                            <td className="px-4 py-3 text-sm text-slate-500 hidden md:table-cell">{expense.note || '-'}</td>
                            <td className="px-4 py-3 text-sm text-right font-semibold text-emerald-600">
                              ${expense.amount?.toLocaleString('es-ES', { minimumFractionDigits: 2 })}
                            </td>
                            <td className="px-4 py-3 text-right">
                              <div className="flex items-center justify-end gap-1">
                                <button
                                  onClick={() => handleEdit(expense)}
                                  className="p-1.5 rounded hover:bg-blue-50 text-slate-400 hover:text-blue-600"
                                  title="Editar"
                                >
                                  <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
                                  </svg>
                                </button>
                                {isAdmin && (
                                  <button
                                    onClick={() => handleExpenseDeleted(expense.id)}
                                    className="p-1.5 rounded hover:bg-red-50 text-slate-400 hover:text-red-600"
                                    title="Eliminar"
                                  >
                                    <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                    </svg>
                                  </button>
                                )}
                              </div>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>
            ))
          )}

          {/* Summary for grouped view */}
          {groupedExpenses.length > 0 && (
            <div className="bg-slate-50 rounded-lg p-4 flex items-center justify-between">
              <span className="text-slate-600">
                Mostrando {expenses.length} gastos en {groupedExpenses.length} {groupedExpenses.length === 1 ? 'mes' : 'meses'}
              </span>
              <span className="font-bold text-slate-800">
                Total: ${expenses.reduce((sum, e) => sum + (e.amount || 0), 0).toLocaleString('es-ES', { minimumFractionDigits: 2 })}
              </span>
            </div>
          )}

          {/* Pagination for grouped view */}
          {totalElements > 0 && (
            <Pagination
              currentPage={currentPage + 1}
              totalPages={totalPages}
              onPageChange={handlePageChange}
              totalItems={totalElements}
              itemsPerPage={pageSize}
              showingFrom={showingFrom}
              showingTo={showingTo}
              itemName="gastos"
            />
          )}
        </div>
      )}
    </div>
  );
}
