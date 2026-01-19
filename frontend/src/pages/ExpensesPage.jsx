import { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import { expenseService } from '../services/api';
import { useAuthStore } from '../store/authStore';
import ExpenseForm from '../components/ExpenseForm';
import ExpenseList from '../components/ExpenseList';
import { Plus, Users, UserCheck } from 'lucide-react';

export default function ExpensesPage() {
  const location = useLocation();
  const { user } = useAuthStore();
  const [expenses, setExpenses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingExpense, setEditingExpense] = useState(null);
  const [error, setError] = useState('');
  const [viewMode, setViewMode] = useState('mine'); // 'mine' or 'all'

  const isAdmin = user?.roleId === 1;

  useEffect(() => {
    fetchExpenses();
  }, [viewMode]);

  // Handle edit from navigation (e.g., from Dashboard)
  useEffect(() => {
    if (location.state?.editExpense) {
      setEditingExpense(location.state.editExpense);
      setShowForm(true);
      // Clear the state to prevent reopening on refresh
      window.history.replaceState({}, document.title);
    }
  }, [location.state]);

  const fetchExpenses = async () => {
    try {
      setLoading(true);
      let response;
      
      // Check if user has required data (id), if not show message to re-login
      if (!user?.id) {
        setError('Por favor, cierra sesión y vuelve a iniciar sesión para actualizar tu perfil.');
        setLoading(false);
        return;
      }
      
      if (isAdmin && viewMode === 'all') {
        response = await expenseService.getAll();
      } else {
        response = await expenseService.getByUserId(user.id);
      }
      setExpenses(response.data);
    } catch (error) {
      console.error('Error loading expenses:', error);
      setError('No se pudieron cargar los gastos.');
    } finally {
      setLoading(false);
    }
  };

  const handleExpenseSuccess = (savedExpense, isEdit) => {
    if (isEdit) {
      setExpenses(expenses.map(e => e.id === savedExpense.id ? savedExpense : e));
    } else {
      setExpenses([savedExpense, ...expenses]);
    }
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
        setExpenses(expenses.filter(e => e.id !== id));
      } catch (error) {
        console.error('Error deleting expense:', error);
        alert('Error al eliminar el gasto');
      }
    }
  };

  if (loading) return (
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
                onClick={() => setViewMode('mine')}
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
                onClick={() => setViewMode('all')}
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

        <button
          className="flex items-center px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors shadow-sm"
          onClick={() => setShowForm(true)}
        >
          <Plus className="mr-2 h-5 w-5" />
          Nuevo Gasto
        </button>
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

      <ExpenseList
        expenses={expenses}
        onDelete={handleExpenseDeleted}
        onEdit={handleEdit}
      />
    </div>
  );
}
