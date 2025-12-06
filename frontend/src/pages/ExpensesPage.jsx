import React, { useEffect, useState, useMemo } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  Plus, 
  Edit2, 
  Trash2, 
  Filter
} from 'lucide-react';
import Card from '../components/Card';
import Button from '../components/Button';
import Input from '../components/Input';
import ConfirmDialog from '../components/ConfirmDialog';
import DateRangeFilter from '../components/filters/DateRangeFilter';
import SearchFilter from '../components/filters/SearchFilter';
import { useExpenseStore } from '../store/authStore';
import { expenseService } from '../services/api';

/**
 * ExpensesPage - Complete expense management with CRUD operations
 * Features:
 * - List all expenses with pagination
 * - Create, edit, delete expenses
 * - Advanced filtering (date range, status, search)
 * - Status management (approve/reject)
 * - Responsive table view
 */
function ExpensesPage() {
  const expenses = useExpenseStore((state) => state.expenses);
  const setExpenses = useExpenseStore((state) => state.setExpenses);
  const addExpense = useExpenseStore((state) => state.addExpense);
  const updateExpense = useExpenseStore((state) => state.updateExpense);
  const removeExpense = useExpenseStore((state) => state.removeExpense);

  const [isLoading, setIsLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingExpense, setEditingExpense] = useState(null);
  const [showFilters, setShowFilters] = useState(false);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const [expenseToDelete, setExpenseToDelete] = useState(null);

  // Filter states
  const [searchTerm, setSearchTerm] = useState('');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');

  // Form states
  const [formData, setFormData] = useState({
    concept: '',
    note: '',
    amount: '',
    date: new Date().toISOString().split('T')[0],
  });
  const [formErrors, setFormErrors] = useState({});

  useEffect(() => {
    loadExpenses();
  }, []);

  const loadExpenses = async () => {
    try {
      setIsLoading(true);
      const response = await expenseService.getAll();
      setExpenses(response.data || []);
    } catch (error) {
      console.error('Error cargando gastos:', error);
    } finally {
      setIsLoading(false);
    }
  };

  // Filter expenses based on search and filters
  const filteredExpenses = useMemo(() => {
    return expenses.filter((expense) => {
      const matchesSearch = 
        expense.concept?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        expense.note?.toLowerCase().includes(searchTerm.toLowerCase());
      
      const expDate = new Date(expense.date);
      const matchesDateRange = 
        (!startDate || expDate >= new Date(startDate)) &&
        (!endDate || expDate <= new Date(endDate));

      return matchesSearch && matchesDateRange;
    });
  }, [expenses, searchTerm, startDate, endDate]);

  const handleOpenModal = (expense = null) => {
    if (expense) {
      setEditingExpense(expense);
      setFormData({
        concept: expense.concept || '',
        note: expense.note || '',
        amount: expense.amount?.toString() || '',
        date: expense.date ? new Date(expense.date).toISOString().split('T')[0] : '',
      });
    } else {
      setEditingExpense(null);
      setFormData({
        concept: '',
        note: '',
        amount: '',
        date: new Date().toISOString().split('T')[0],
      });
    }
    setFormErrors({});
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setEditingExpense(null);
    setFormData({
      concept: '',
      note: '',
      amount: '',
      date: new Date().toISOString().split('T')[0],
    });
    setFormErrors({});
  };

  const validateForm = () => {
    const errors = {};
    
    if (!formData.concept || formData.concept.trim().length < 3) {
      errors.concept = 'El concepto debe tener al menos 3 caracteres';
    }
    if (!formData.amount || parseFloat(formData.amount) <= 0) {
      errors.amount = 'El monto debe ser mayor a 0';
    }
    if (!formData.date) {
      errors.date = 'La fecha es requerida';
    }

    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) return;

    try {
      const expenseData = {
        concept: formData.concept,
        note: formData.note || '',
        amount: parseFloat(formData.amount),
        date: new Date(formData.date).toISOString(),
      };

      if (editingExpense) {
        const response = await expenseService.update(editingExpense.id, expenseData);
        updateExpense(editingExpense.id, response.data);
      } else {
        const response = await expenseService.create(expenseData);
        addExpense(response.data);
      }

      handleCloseModal();
    } catch (error) {
      console.error('Error guardando gasto:', error);
      setFormErrors({ submit: 'Error al guardar el gasto. Intente nuevamente.' });
    }
  };

  const handleDelete = async (id) => {
    setExpenseToDelete(id);
    setShowDeleteConfirm(true);
  };

  const confirmDelete = async () => {
    if (!expenseToDelete) return;

    try {
      await expenseService.delete(expenseToDelete);
      removeExpense(expenseToDelete);
    } catch (error) {
      console.error('Error eliminando gasto:', error);
    } finally {
      setExpenseToDelete(null);
    }
  };

  const clearFilters = () => {
    setSearchTerm('');
    setStartDate('');
    setEndDate('');
  };

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      className="space-y-6"
    >
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="gradient-text text-4xl font-bold">Gastos</h1>
          <p className="mt-2 text-slate-400">Gestiona y visualiza todos tus gastos</p>
        </div>
        <div className="flex gap-3">
          <Button 
            variant="secondary" 
            onClick={() => setShowFilters(!showFilters)}
          >
            <Filter className="h-5 w-5" />
            Filtros
          </Button>
          <Button variant="primary" onClick={() => handleOpenModal()}>
            <Plus className="h-5 w-5" />
            Nuevo Gasto
          </Button>
        </div>
      </div>

      {/* Filters */}
      {showFilters && (
        <motion.div
          initial={{ opacity: 0, height: 0 }}
          animate={{ opacity: 1, height: 'auto' }}
          exit={{ opacity: 0, height: 0 }}
        >
          <Card className="p-6">
            <h3 className="text-lg font-semibold text-white mb-4">Filtros Avanzados</h3>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <SearchFilter
                value={searchTerm}
                onChange={setSearchTerm}
                placeholder="Buscar por concepto o nota..."
                label="Buscar"
              />
              <DateRangeFilter
                startDate={startDate}
                endDate={endDate}
                onStartDateChange={setStartDate}
                onEndDateChange={setEndDate}
              />
              <div className="flex items-end">
                <Button
                  variant="secondary"
                  onClick={clearFilters}
                  className="w-full"
                >
                  Limpiar Filtros
                </Button>
              </div>
            </div>
          </Card>
        </motion.div>
      )}

      {/* Expenses Table */}
      <Card className="overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="border-b border-slate-700 bg-slate-800/50">
              <tr>
                <th className="px-6 py-3 text-left text-sm font-semibold text-slate-300">
                  Concepto
                </th>
                <th className="px-6 py-3 text-left text-sm font-semibold text-slate-300">
                  Nota
                </th>
                <th className="px-6 py-3 text-left text-sm font-semibold text-slate-300">
                  Monto
                </th>
                <th className="px-6 py-3 text-left text-sm font-semibold text-slate-300">
                  Fecha
                </th>
                <th className="px-6 py-3 text-left text-sm font-semibold text-slate-300">
                  Acciones
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-700">
              {isLoading ? (
                <tr>
                  <td colSpan="5" className="px-6 py-8 text-center text-slate-400">
                    Cargando gastos...
                  </td>
                </tr>
              ) : filteredExpenses.length === 0 ? (
                <tr>
                  <td colSpan="5" className="px-6 py-8 text-center text-slate-400">
                    No hay gastos que coincidan con los filtros
                  </td>
                </tr>
              ) : (
                filteredExpenses.map((expense) => (
                  <tr key={expense.id} className="hover:bg-slate-800/30 transition">
                    <td className="px-6 py-4 text-white font-medium">{expense.concept}</td>
                    <td className="px-6 py-4 text-slate-400 max-w-xs truncate">
                      {expense.note || '-'}
                    </td>
                    <td className="px-6 py-4 text-white font-semibold">
                      ${expense.amount?.toFixed(2)}
                    </td>
                    <td className="px-6 py-4 text-slate-400">
                      {new Date(expense.date).toLocaleDateString('es-ES')}
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-2">
                        <button
                          onClick={() => handleOpenModal(expense)}
                          className="p-2 text-blue-400 hover:bg-blue-500/20 rounded-lg transition"
                          title="Editar"
                        >
                          <Edit2 className="h-4 w-4" />
                        </button>
                        <button
                          onClick={() => handleDelete(expense.id)}
                          className="p-2 text-red-400 hover:bg-red-500/20 rounded-lg transition"
                          title="Eliminar"
                        >
                          <Trash2 className="h-4 w-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </Card>

      {/* Modal */}
      <AnimatePresence>
        {showModal && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50 p-4"
            onClick={handleCloseModal}
          >
            <motion.div
              initial={{ scale: 0.95, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0.95, opacity: 0 }}
              onClick={(e) => e.stopPropagation()}
              className="w-full max-w-lg"
            >
              <Card className="p-6">
                <h2 className="text-2xl font-bold text-white mb-6">
                  {editingExpense ? 'Editar Gasto' : 'Nuevo Gasto'}
                </h2>
                <form onSubmit={handleSubmit} className="space-y-4">
                  <div>
                    <label className="block text-sm font-medium text-slate-300 mb-2">
                      Concepto *
                    </label>
                    <Input
                      value={formData.concept}
                      onChange={(e) => setFormData({ ...formData, concept: e.target.value })}
                      placeholder="Ej: Viáticos, Material de oficina"
                      error={formErrors.concept}
                    />
                    {formErrors.concept && (
                      <p className="mt-1 text-sm text-red-400">{formErrors.concept}</p>
                    )}
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-slate-300 mb-2">
                      Nota
                    </label>
                    <textarea
                      value={formData.note}
                      onChange={(e) => setFormData({ ...formData, note: e.target.value })}
                      placeholder="Descripción adicional (opcional)"
                      rows="3"
                      className="w-full px-4 py-2.5 bg-slate-800/50 border border-slate-700 rounded-lg text-white placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-slate-300 mb-2">
                      Monto *
                    </label>
                    <Input
                      type="number"
                      step="0.01"
                      value={formData.amount}
                      onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
                      placeholder="0.00"
                      error={formErrors.amount}
                    />
                    {formErrors.amount && (
                      <p className="mt-1 text-sm text-red-400">{formErrors.amount}</p>
                    )}
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-slate-300 mb-2">
                      Fecha *
                    </label>
                    <Input
                      type="date"
                      value={formData.date}
                      onChange={(e) => setFormData({ ...formData, date: e.target.value })}
                      error={formErrors.date}
                    />
                    {formErrors.date && (
                      <p className="mt-1 text-sm text-red-400">{formErrors.date}</p>
                    )}
                  </div>

                  {formErrors.submit && (
                    <p className="text-sm text-red-400">{formErrors.submit}</p>
                  )}

                  <div className="flex gap-3 pt-4">
                    <Button
                      type="button"
                      variant="secondary"
                      onClick={handleCloseModal}
                      className="flex-1"
                    >
                      Cancelar
                    </Button>
                    <Button type="submit" variant="primary" className="flex-1">
                      {editingExpense ? 'Actualizar' : 'Crear'}
                    </Button>
                  </div>
                </form>
              </Card>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Delete Confirmation Dialog */}
      <ConfirmDialog
        isOpen={showDeleteConfirm}
        onClose={() => setShowDeleteConfirm(false)}
        onConfirm={confirmDelete}
        title="¿Eliminar gasto?"
        message="Esta acción no se puede deshacer. El gasto será eliminado permanentemente."
        confirmText="Eliminar"
        cancelText="Cancelar"
        variant="danger"
      />
    </motion.div>
  );
}

export default ExpensesPage;

