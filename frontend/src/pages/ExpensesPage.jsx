import React, { useEffect, useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import {
  Plus,
  Search,
  Filter,
  Check,
  X,
  Edit,
  Trash2,
  Upload,
  Download,
  FileText,
  Clock,
  CheckCircle,
  XCircle
} from 'lucide-react';
import Card from '../components/Card';
import Button from '../components/Button';
import { expenseService } from '../services/api';
import { useAuthStore } from '../store/authStore';

const STATUS_COLORS = {
  PENDING: { bg: 'bg-yellow-500/20', text: 'text-yellow-400', border: 'border-yellow-500/30', icon: Clock },
  APPROVED: { bg: 'bg-green-500/20', text: 'text-green-400', border: 'border-green-500/30', icon: CheckCircle },
  REJECTED: { bg: 'bg-red-500/20', text: 'text-red-400', border: 'border-red-500/30', icon: XCircle },
};

const STATUS_LABELS = {
  PENDING: 'Pendiente',
  APPROVED: 'Aprobado',
  REJECTED: 'Rechazado',
};

function ExpensesPage() {
  const [expenses, setExpenses] = useState([]);
  const [filteredExpenses, setFilteredExpenses] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [showModal, setShowModal] = useState(false);
  const [showRejectModal, setShowRejectModal] = useState(false);
  const [selectedExpense, setSelectedExpense] = useState(null);
  const [formData, setFormData] = useState({
    concept: '',
    note: '',
    date: '',
    amount: '',
  });
  const [rejectionReason, setRejectionReason] = useState('');
  const [errors, setErrors] = useState({});
  const { user } = useAuthStore();
  const isAdmin = user?.roles?.includes('ROLE_ADMIN');

  useEffect(() => {
    loadExpenses();
  }, []);

  useEffect(() => {
    filterExpenses();
  }, [expenses, searchTerm, statusFilter]);

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

  const filterExpenses = () => {
    let filtered = expenses;

    // Filter by search term
    if (searchTerm) {
      filtered = filtered.filter(
        (exp) =>
          exp.concept?.toLowerCase().includes(searchTerm.toLowerCase()) ||
          exp.note?.toLowerCase().includes(searchTerm.toLowerCase())
      );
    }

    // Filter by status
    if (statusFilter !== 'ALL') {
      filtered = filtered.filter((exp) => exp.status === statusFilter);
    }

    setFilteredExpenses(filtered);
  };

  const handleOpenModal = (expense = null) => {
    if (expense) {
      setSelectedExpense(expense);
      setFormData({
        concept: expense.concept || '',
        note: expense.note || '',
        date: expense.date ? new Date(expense.date).toISOString().slice(0, 16) : '',
        amount: expense.amount || '',
      });
    } else {
      setSelectedExpense(null);
      setFormData({
        concept: '',
        note: '',
        date: new Date().toISOString().slice(0, 16),
        amount: '',
      });
    }
    setErrors({});
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setSelectedExpense(null);
    setFormData({ concept: '', note: '', date: '', amount: '' });
    setErrors({});
  };

  const validateForm = () => {
    const newErrors = {};
    if (!formData.concept || formData.concept.length < 3) {
      newErrors.concept = 'El concepto debe tener al menos 3 caracteres';
    }
    if (!formData.amount || formData.amount <= 0) {
      newErrors.amount = 'El monto debe ser mayor a 0';
    }
    if (!formData.date) {
      newErrors.date = 'La fecha es requerida';
    }
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;

    try {
      const expenseData = {
        ...formData,
        amount: parseFloat(formData.amount),
        date: new Date(formData.date).toISOString(),
        employee: { id: user.id }, // Assuming user has employee ID
      };

      if (selectedExpense) {
        expenseData.id = selectedExpense.id;
        await expenseService.update(expenseData);
      } else {
        await expenseService.create(expenseData);
      }

      await loadExpenses();
      handleCloseModal();
    } catch (error) {
      console.error('Error guardando gasto:', error);
      setErrors({ submit: 'Error al guardar el gasto' });
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('¿Estás seguro de eliminar este gasto?')) return;

    try {
      await expenseService.delete(id);
      await loadExpenses();
    } catch (error) {
      console.error('Error eliminando gasto:', error);
    }
  };

  const handleApprove = async (id) => {
    try {
      await expenseService.approve(id);
      await loadExpenses();
    } catch (error) {
      console.error('Error aprobando gasto:', error);
    }
  };

  const handleRejectClick = (expense) => {
    setSelectedExpense(expense);
    setRejectionReason('');
    setShowRejectModal(true);
  };

  const handleRejectSubmit = async () => {
    if (!rejectionReason.trim()) {
      alert('Por favor ingresa una razón para el rechazo');
      return;
    }

    try {
      await expenseService.reject(selectedExpense.id, rejectionReason);
      await loadExpenses();
      setShowRejectModal(false);
      setRejectionReason('');
      setSelectedExpense(null);
    } catch (error) {
      console.error('Error rechazando gasto:', error);
    }
  };

  const StatusBadge = ({ status }) => {
    const config = STATUS_COLORS[status] || STATUS_COLORS.PENDING;
    const Icon = config.icon;
    return (
      <span
        className={`inline-flex items-center gap-1 px-3 py-1 rounded-full text-sm font-medium ${config.bg} ${config.text} border ${config.border}`}
      >
        <Icon className="h-4 w-4" />
        {STATUS_LABELS[status] || status}
      </span>
    );
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <motion.div
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex items-center justify-between"
      >
        <div>
          <h1 className="gradient-text text-4xl font-bold">Gastos</h1>
          <p className="mt-2 text-slate-400">Gestiona y visualiza todos tus gastos</p>
        </div>
        <Button variant="primary" onClick={() => handleOpenModal()}>
          <Plus className="h-5 w-5" />
          Nuevo Gasto
        </Button>
      </motion.div>

      {/* Filters */}
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ delay: 0.1 }}
      >
        <Card className="p-4">
          <div className="flex flex-col sm:flex-row gap-4">
            <div className="flex-1 relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-slate-400" />
              <input
                type="text"
                placeholder="Buscar por concepto o nota..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full pl-10 pr-4 py-2 bg-slate-800 border border-slate-700 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
            </div>
            <div className="flex gap-2">
              <select
                value={statusFilter}
                onChange={(e) => setStatusFilter(e.target.value)}
                className="px-4 py-2 bg-slate-800 border border-slate-700 rounded-lg text-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
              >
                <option value="ALL">Todos los estados</option>
                <option value="PENDING">Pendientes</option>
                <option value="APPROVED">Aprobados</option>
                <option value="REJECTED">Rechazados</option>
              </select>
            </div>
          </div>
        </Card>
      </motion.div>

      {/* Expenses Table */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.2 }}
      >
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
                    Estado
                  </th>
                  <th className="px-6 py-3 text-right text-sm font-semibold text-slate-300">
                    Acciones
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-700">
                {isLoading ? (
                  <tr>
                    <td colSpan="6" className="px-6 py-8 text-center text-slate-400">
                      Cargando gastos...
                    </td>
                  </tr>
                ) : filteredExpenses.length === 0 ? (
                  <tr>
                    <td colSpan="6" className="px-6 py-8 text-center text-slate-400">
                      No hay gastos registrados
                    </td>
                  </tr>
                ) : (
                  filteredExpenses.map((expense) => (
                    <tr key={expense.id} className="hover:bg-slate-800/30 transition">
                      <td className="px-6 py-4 text-white font-medium">
                        {expense.concept}
                      </td>
                      <td className="px-6 py-4 text-slate-400 max-w-xs truncate">
                        {expense.note || '-'}
                      </td>
                      <td className="px-6 py-4 text-white font-semibold">
                        ${expense.amount?.toFixed(2)}
                      </td>
                      <td className="px-6 py-4 text-slate-400">
                        {expense.date ? new Date(expense.date).toLocaleDateString() : '-'}
                      </td>
                      <td className="px-6 py-4">
                        <StatusBadge status={expense.status} />
                      </td>
                      <td className="px-6 py-4">
                        <div className="flex items-center justify-end gap-2">
                          {isAdmin && expense.status === 'PENDING' && (
                            <>
                              <button
                                onClick={() => handleApprove(expense.id)}
                                className="p-2 hover:bg-green-500/20 text-green-400 rounded-lg transition"
                                title="Aprobar"
                              >
                                <Check className="h-4 w-4" />
                              </button>
                              <button
                                onClick={() => handleRejectClick(expense)}
                                className="p-2 hover:bg-red-500/20 text-red-400 rounded-lg transition"
                                title="Rechazar"
                              >
                                <X className="h-4 w-4" />
                              </button>
                            </>
                          )}
                          {expense.status === 'PENDING' && (
                            <button
                              onClick={() => handleOpenModal(expense)}
                              className="p-2 hover:bg-indigo-500/20 text-indigo-400 rounded-lg transition"
                              title="Editar"
                            >
                              <Edit className="h-4 w-4" />
                            </button>
                          )}
                          {isAdmin && (
                            <button
                              onClick={() => handleDelete(expense.id)}
                              className="p-2 hover:bg-red-500/20 text-red-400 rounded-lg transition"
                              title="Eliminar"
                            >
                              <Trash2 className="h-4 w-4" />
                            </button>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </Card>
      </motion.div>

      {/* Create/Edit Modal */}
      <AnimatePresence>
        {showModal && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 bg-black/50 backdrop-blur-sm z-50 flex items-center justify-center p-4"
            onClick={handleCloseModal}
          >
            <motion.div
              initial={{ scale: 0.9, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0.9, opacity: 0 }}
              onClick={(e) => e.stopPropagation()}
              className="w-full max-w-md"
            >
              <Card className="p-6">
                <h2 className="text-2xl font-bold text-white mb-4">
                  {selectedExpense ? 'Editar Gasto' : 'Nuevo Gasto'}
                </h2>
                <form onSubmit={handleSubmit} className="space-y-4">
                  <div>
                    <label className="block text-sm font-medium text-slate-300 mb-2">
                      Concepto *
                    </label>
                    <input
                      type="text"
                      value={formData.concept}
                      onChange={(e) => setFormData({ ...formData, concept: e.target.value })}
                      className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-lg text-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
                      placeholder="Ej: Taxi, Almuerzo, Material de oficina"
                    />
                    {errors.concept && (
                      <p className="mt-1 text-sm text-red-400">{errors.concept}</p>
                    )}
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-slate-300 mb-2">
                      Nota
                    </label>
                    <textarea
                      value={formData.note}
                      onChange={(e) => setFormData({ ...formData, note: e.target.value })}
                      rows="3"
                      className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-lg text-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
                      placeholder="Descripción adicional del gasto"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-slate-300 mb-2">
                      Monto *
                    </label>
                    <input
                      type="number"
                      step="0.01"
                      value={formData.amount}
                      onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
                      className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-lg text-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
                      placeholder="0.00"
                    />
                    {errors.amount && (
                      <p className="mt-1 text-sm text-red-400">{errors.amount}</p>
                    )}
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-slate-300 mb-2">
                      Fecha *
                    </label>
                    <input
                      type="datetime-local"
                      value={formData.date}
                      onChange={(e) => setFormData({ ...formData, date: e.target.value })}
                      className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-lg text-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
                    />
                    {errors.date && (
                      <p className="mt-1 text-sm text-red-400">{errors.date}</p>
                    )}
                  </div>

                  {errors.submit && (
                    <p className="text-sm text-red-400">{errors.submit}</p>
                  )}

                  <div className="flex gap-3 pt-4">
                    <Button type="submit" variant="primary" className="flex-1">
                      {selectedExpense ? 'Actualizar' : 'Crear'}
                    </Button>
                    <Button
                      type="button"
                      variant="secondary"
                      onClick={handleCloseModal}
                      className="flex-1"
                    >
                      Cancelar
                    </Button>
                  </div>
                </form>
              </Card>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Reject Modal */}
      <AnimatePresence>
        {showRejectModal && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 bg-black/50 backdrop-blur-sm z-50 flex items-center justify-center p-4"
            onClick={() => setShowRejectModal(false)}
          >
            <motion.div
              initial={{ scale: 0.9, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0.9, opacity: 0 }}
              onClick={(e) => e.stopPropagation()}
              className="w-full max-w-md"
            >
              <Card className="p-6">
                <h2 className="text-2xl font-bold text-white mb-4">Rechazar Gasto</h2>
                <p className="text-slate-400 mb-4">
                  ¿Estás seguro de rechazar el gasto "{selectedExpense?.concept}"?
                </p>
                <div className="mb-4">
                  <label className="block text-sm font-medium text-slate-300 mb-2">
                    Razón del rechazo *
                  </label>
                  <textarea
                    value={rejectionReason}
                    onChange={(e) => setRejectionReason(e.target.value)}
                    rows="3"
                    className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-lg text-white focus:outline-none focus:ring-2 focus:ring-red-500"
                    placeholder="Por favor explica por qué se rechaza este gasto"
                  />
                </div>
                <div className="flex gap-3">
                  <Button
                    onClick={handleRejectSubmit}
                    className="flex-1 bg-red-500 hover:bg-red-600"
                  >
                    Rechazar
                  </Button>
                  <Button
                    variant="secondary"
                    onClick={() => setShowRejectModal(false)}
                    className="flex-1"
                  >
                    Cancelar
                  </Button>
                </div>
              </Card>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}

export default ExpensesPage;
