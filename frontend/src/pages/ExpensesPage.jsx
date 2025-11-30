import { useState, useEffect } from 'react';
import { expenseService } from '../services/api';
import ExpenseForm from '../components/ExpenseForm';
import ExpenseList from '../components/ExpenseList';
import { Plus } from 'lucide-react';

export default function ExpensesPage() {
  const [expenses, setExpenses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchExpenses();
  }, []);

  const fetchExpenses = async () => {
    try {
      const response = await expenseService.getAll();
      setExpenses(response.data);
    } catch (error) {
      console.error('Error loading expenses:', error);
      setError('No se pudieron cargar los gastos.');
    } finally {
      setLoading(false);
    }
  };

  const handleExpenseCreated = (newExpense) => {
    setExpenses([newExpense, ...expenses]);
    setShowForm(false);
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
        <h1 className="text-2xl font-bold text-gray-900">Mis Gastos</h1>

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
          onSuccess={handleExpenseCreated}
          onCancel={() => setShowForm(false)}
        />
      )}

      <ExpenseList
        expenses={expenses}
        onDelete={handleExpenseDeleted}
        onEdit={(expense) => console.log('Edit not implemented yet', expense)}
      />
    </div>
  );
}
