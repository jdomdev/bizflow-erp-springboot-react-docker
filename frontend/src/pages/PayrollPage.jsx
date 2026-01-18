import React, { useEffect, useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  DollarSign, 
  Calendar, 
  User, 
  Download,
  Eye,
  X,
  TrendingUp,
  Briefcase,
  ChevronLeft,
  ChevronRight,
  Users,
  UserCheck,
  Plus,
  Pencil,
  Trash2,
  Save
} from 'lucide-react';
import Card from '../components/Card';
import Button from '../components/Button';
import { payrollService, payrollAdminService, employeeService, userService } from '../services/api';

function PayrollPage() {
  const [payrolls, setPayrolls] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedPayroll, setSelectedPayroll] = useState(null);
  const [showDetailModal, setShowDetailModal] = useState(false);
  const [profile, setProfile] = useState(null);
  const [viewMode, setViewMode] = useState('mine'); // 'mine' or 'all' (admin only)
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(15);
  
  // CRUD State
  const [showForm, setShowForm] = useState(false);
  const [editingPayroll, setEditingPayroll] = useState(null);
  const [saving, setSaving] = useState(false);
  const [formData, setFormData] = useState({
    employeeId: '',
    amount: '',
    payrollDate: ''
  });
  const [formErrors, setFormErrors] = useState({});
  
  const [stats, setStats] = useState({
    totalPayrolls: 0,
    totalAmount: 0,
    averageAmount: 0,
    thisMonth: 0
  });

  useEffect(() => {
    loadData();
  }, [viewMode]);

  const loadData = async () => {
    try {
      setIsLoading(true);
      setError(null);
      setCurrentPage(1);
      
      // Cargar perfil del usuario
      const profileResponse = await userService.getProfile();
      setProfile(profileResponse.data);
      
      // Verificar si el usuario tiene rol ADMIN (roleId 1)
      // Nota: roleId 1 = ADMIN, roleId 2 = USER, roleId 3 = MANAGER
      const isAdmin = profileResponse.data.roleIds?.includes(1);
      
      let payrollData = [];
      
      // Admin puede elegir ver todas o solo las suyas
      if (isAdmin && viewMode === 'all') {
        const response = await payrollService.getAll();
        payrollData = response.data || [];
      } else {
        // Usuario normal o admin viendo "mis nóminas"
        const response = await payrollService.getMy();
        payrollData = response.data || [];
      }
      
      setPayrolls(payrollData);
      
      // Cargar empleados para mostrar nombres
      try {
        const employeesResponse = await employeeService.getAll();
        setEmployees(employeesResponse.data || []);
      } catch (empError) {
        console.warn('No se pudieron cargar empleados:', empError);
      }
      
      // Calcular estadísticas
      const totalAmount = payrollData.reduce((sum, p) => sum + (p.amount || 0), 0);
      const now = new Date();
      const thisMonthPayrolls = payrollData.filter(p => {
        const payrollDate = new Date(p.payrollDate);
        return payrollDate.getMonth() === now.getMonth() && 
               payrollDate.getFullYear() === now.getFullYear();
      });
      const thisMonthAmount = thisMonthPayrolls.reduce((sum, p) => sum + (p.amount || 0), 0);
      
      setStats({
        totalPayrolls: payrollData.length,
        totalAmount,
        averageAmount: payrollData.length > 0 ? totalAmount / payrollData.length : 0,
        thisMonth: thisMonthAmount
      });
      
    } catch (err) {
      console.error('Error cargando nóminas:', err);
      setError('No se pudieron cargar las nóminas. Por favor, intenta de nuevo.');
    } finally {
      setIsLoading(false);
    }
  };

  const getEmployeeName = (payroll) => {
    // Primero intentar con employeeName/employeeSurname si vienen en el payload
    if (payroll.employeeName && payroll.employeeSurname) {
      return `${payroll.employeeName} ${payroll.employeeSurname}`;
    }
    // Luego buscar en la lista de empleados cargada
    const employee = employees.find(e => e.id === payroll.employeeId);
    if (employee) {
      return `${employee.name} ${employee.surname}`;
    }
    // Si estamos en modo "mis nóminas" y no encontramos el empleado, usar el nombre del perfil
    if (viewMode === 'mine' && profile?.name && profile?.surname) {
      return `${profile.name} ${profile.surname}`;
    }
    // Fallback: si hay employeeId mostrar el ID, sino indicar freelance
    if (payroll.employeeId) {
      return `Empleado #${payroll.employeeId}`;
    }
    return 'Freelance';
  };

  // Paginación
  const isAdmin = profile?.roleIds?.includes(1);
  const totalPages = Math.ceil(payrolls.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const currentPayrolls = payrolls.slice(startIndex, endIndex);

  const goToPage = (page) => {
    if (page >= 1 && page <= totalPages) {
      setCurrentPage(page);
    }
  };

  const handleViewPayroll = (payroll) => {
    setSelectedPayroll(payroll);
    setShowDetailModal(true);
  };

  const handleExportCSV = () => {
    if (payrolls.length === 0) {
      alert('No hay nóminas para exportar');
      return;
    }
    const headers = ['ID', 'Empleado', 'Monto', 'Fecha'];
    const csvContent = [
      headers.join(','),
      ...payrolls.map(p => [
        p.id,
        `"${getEmployeeName(p)}"`,
        p.amount,
        new Date(p.payrollDate).toLocaleDateString('es-ES')
      ].join(','))
    ].join('\n');
    
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = `nominas_${new Date().toISOString().split('T')[0]}.csv`;
    link.click();
  };

  // CRUD Functions
  const validateForm = () => {
    const errors = {};
    if (!formData.employeeId) {
      errors.employeeId = 'El empleado es requerido';
    }
    if (!formData.amount || parseFloat(formData.amount) <= 0) {
      errors.amount = 'El monto debe ser mayor a 0';
    }
    if (!formData.payrollDate) {
      errors.payrollDate = 'La fecha es requerida';
    }
    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleOpenForm = (payroll = null) => {
    if (payroll) {
      setEditingPayroll(payroll);
      // Parse date
      let dateStr = '';
      if (payroll.payrollDate) {
        if (Array.isArray(payroll.payrollDate)) {
          const [year, month, day] = payroll.payrollDate;
          dateStr = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
        } else {
          dateStr = payroll.payrollDate.split('T')[0];
        }
      }
      setFormData({
        employeeId: payroll.employeeId?.toString() || '',
        amount: payroll.amount?.toString() || '',
        payrollDate: dateStr
      });
    } else {
      setEditingPayroll(null);
      setFormData({ employeeId: '', amount: '', payrollDate: '' });
    }
    setFormErrors({});
    setShowForm(true);
  };

  const handleCloseForm = () => {
    setShowForm(false);
    setEditingPayroll(null);
    setFormData({ employeeId: '', amount: '', payrollDate: '' });
    setFormErrors({});
  };

  const handleSubmitPayroll = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;

    setSaving(true);
    try {
      const payrollDate = new Date(formData.payrollDate);
      payrollDate.setHours(12, 0, 0, 0);

      const payload = {
        employeeId: parseInt(formData.employeeId),
        amount: parseFloat(formData.amount),
        payrollDate: payrollDate.toISOString()
      };

      if (editingPayroll) {
        await payrollAdminService.update(editingPayroll.id, payload);
      } else {
        await payrollAdminService.create(payload);
      }
      
      handleCloseForm();
      loadData(); // Reload data
    } catch (err) {
      console.error('Error saving payroll:', err);
      setError(err.response?.data?.message || 'Error al guardar la nómina');
    } finally {
      setSaving(false);
    }
  };

  const handleDeletePayroll = async (id) => {
    if (!window.confirm('¿Estás seguro de que deseas eliminar esta nómina?')) {
      return;
    }
    try {
      await payrollAdminService.delete(id);
      setPayrolls(payrolls.filter(p => p.id !== id));
    } catch (err) {
      console.error('Error deleting payroll:', err);
      alert(err.response?.data?.message || 'Error al eliminar la nómina');
    }
  };

  const statCards = [
    {
      icon: Briefcase,
      label: 'Total Nóminas',
      value: stats.totalPayrolls.toString(),
      bgColor: 'bg-blue-100',
      iconColor: 'text-blue-600'
    },
    {
      icon: DollarSign,
      label: 'Monto Total',
      value: `$${stats.totalAmount.toLocaleString('es-ES', { minimumFractionDigits: 2 })}`,
      bgColor: 'bg-emerald-100',
      iconColor: 'text-emerald-600'
    },
    {
      icon: TrendingUp,
      label: 'Promedio',
      value: `$${stats.averageAmount.toLocaleString('es-ES', { minimumFractionDigits: 2 })}`,
      bgColor: 'bg-violet-100',
      iconColor: 'text-violet-600'
    },
    {
      icon: Calendar,
      label: 'Este Mes',
      value: `$${stats.thisMonth.toLocaleString('es-ES', { minimumFractionDigits: 2 })}`,
      bgColor: 'bg-amber-100',
      iconColor: 'text-amber-600'
    }
  ];

  return (
    <div className="space-y-6 sm:space-y-8">
      {/* Header */}
      <motion.div
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4"
      >
        <div>
          <h1 className="text-2xl sm:text-3xl lg:text-4xl font-bold text-slate-800 mb-1">
            Nóminas 💰
          </h1>
          <p className="text-slate-500">
            {isAdmin && viewMode === 'all'
              ? 'Gestión de nóminas de todos los empleados' 
              : 'Historial de tus pagos'}
          </p>
        </div>
        <div className="flex gap-2">
          {/* Admin Toggle */}
          {isAdmin && (
            <div className="flex bg-slate-100 rounded-xl p-1">
              <button
                onClick={() => setViewMode('mine')}
                className={`flex items-center gap-2 px-3 py-2 rounded-lg text-sm font-medium transition-all ${
                  viewMode === 'mine'
                    ? 'bg-white text-blue-600 shadow-sm'
                    : 'text-slate-600 hover:text-slate-800'
                }`}
              >
                <UserCheck className="h-4 w-4" />
                <span className="hidden sm:inline">Mis nóminas</span>
              </button>
              <button
                onClick={() => setViewMode('all')}
                className={`flex items-center gap-2 px-3 py-2 rounded-lg text-sm font-medium transition-all ${
                  viewMode === 'all'
                    ? 'bg-white text-blue-600 shadow-sm'
                    : 'text-slate-600 hover:text-slate-800'
                }`}
              >
                <Users className="h-4 w-4" />
                <span className="hidden sm:inline">Todas</span>
              </button>
            </div>
          )}
          {isAdmin && (
            <Button variant="primary" size="sm" onClick={() => handleOpenForm()}>
              <Plus className="h-4 w-4" />
              <span className="hidden sm:inline">Nueva Nómina</span>
            </Button>
          )}
          <Button variant="secondary" size="sm" onClick={handleExportCSV}>
            <Download className="h-4 w-4" />
            <span className="hidden sm:inline">Exportar</span>
          </Button>
        </div>
      </motion.div>

      {/* Stats Cards */}
      <div className="grid gap-4 sm:gap-6 grid-cols-2 lg:grid-cols-4">
        {statCards.map((stat, index) => {
          const Icon = stat.icon;
          return (
            <motion.div
              key={index}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: index * 0.1 }}
            >
              <Card variant="gradient" className="p-4 sm:p-5">
                <div className="flex items-center gap-3 mb-3">
                  <div className={`p-2 rounded-xl ${stat.bgColor}`}>
                    <Icon className={`h-5 w-5 ${stat.iconColor}`} />
                  </div>
                </div>
                <p className="text-xs sm:text-sm font-medium text-slate-500 mb-1">{stat.label}</p>
                <p className="text-lg sm:text-xl font-bold text-slate-800">{stat.value}</p>
              </Card>
            </motion.div>
          );
        })}
      </div>

      {/* Payrolls Table */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.3 }}
      >
        <Card variant="solid" className="overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b border-slate-100 bg-slate-50/50">
                  <th className="px-4 sm:px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">
                    Empleado
                  </th>
                  <th className="px-4 sm:px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">
                    Monto
                  </th>
                  <th className="hidden sm:table-cell px-4 sm:px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">
                    Fecha
                  </th>
                  <th className="px-4 sm:px-6 py-4 text-right text-xs font-semibold text-slate-500 uppercase tracking-wider">
                    Acciones
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {isLoading ? (
                  <tr>
                    <td colSpan="4" className="px-6 py-12 text-center">
                      <div className="flex flex-col items-center gap-3">
                        <div className="w-8 h-8 border-2 border-blue-500 border-t-transparent rounded-full animate-spin"></div>
                        <span className="text-slate-500">Cargando nóminas...</span>
                      </div>
                    </td>
                  </tr>
                ) : error ? (
                  <tr>
                    <td colSpan="4" className="px-6 py-12 text-center">
                      <div className="flex flex-col items-center gap-3">
                        <div className="w-16 h-16 rounded-full bg-rose-100 flex items-center justify-center">
                          <X className="h-8 w-8 text-rose-500" />
                        </div>
                        <p className="text-rose-600 font-medium">{error}</p>
                        <Button variant="primary" size="sm" onClick={loadData}>
                          Reintentar
                        </Button>
                      </div>
                    </td>
                  </tr>
                ) : payrolls.length === 0 ? (
                  <tr>
                    <td colSpan="4" className="px-6 py-12 text-center">
                      <div className="flex flex-col items-center gap-3">
                        <div className="w-16 h-16 rounded-full bg-slate-100 flex items-center justify-center">
                          <DollarSign className="h-8 w-8 text-slate-400" />
                        </div>
                        <div>
                          <p className="text-slate-700 font-medium">No hay nóminas disponibles</p>
                          <p className="text-sm text-slate-500">Las nóminas aparecerán aquí cuando estén disponibles</p>
                        </div>
                      </div>
                    </td>
                  </tr>
                ) : (
                  currentPayrolls.map((payroll, idx) => (
                    <motion.tr 
                      key={payroll.id}
                      initial={{ opacity: 0 }}
                      animate={{ opacity: 1 }}
                      transition={{ delay: 0.4 + idx * 0.03 }}
                      className="hover:bg-slate-50/50 transition-colors"
                    >
                      <td className="px-4 sm:px-6 py-4">
                        <div className="flex items-center gap-3">
                          <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-emerald-100 to-teal-100 flex items-center justify-center">
                            <User className="h-5 w-5 text-emerald-600" />
                          </div>
                          <div>
                            <p className="font-medium text-slate-800">{getEmployeeName(payroll)}</p>
                            <p className="text-xs text-slate-500 sm:hidden">
                              {new Date(payroll.payrollDate).toLocaleDateString('es-ES')}
                            </p>
                          </div>
                        </div>
                      </td>
                      <td className="px-4 sm:px-6 py-4">
                        <span className="font-semibold text-emerald-600">
                          ${payroll.amount?.toLocaleString('es-ES', { minimumFractionDigits: 2 })}
                        </span>
                      </td>
                      <td className="hidden sm:table-cell px-4 sm:px-6 py-4 text-slate-500">
                        {new Date(payroll.payrollDate).toLocaleDateString('es-ES', {
                          day: 'numeric',
                          month: 'short',
                          year: 'numeric'
                        })}
                      </td>
                      <td className="px-4 sm:px-6 py-4 text-right">
                        <div className="flex items-center justify-end gap-1">
                          <button 
                            className="p-2 rounded-lg text-slate-400 hover:text-blue-600 hover:bg-blue-50 transition-colors"
                            onClick={() => handleViewPayroll(payroll)}
                            title="Ver detalle"
                          >
                            <Eye className="h-4 w-4" />
                          </button>
                          {isAdmin && (
                            <>
                              <button 
                                className="p-2 rounded-lg text-slate-400 hover:text-amber-600 hover:bg-amber-50 transition-colors"
                                onClick={() => handleOpenForm(payroll)}
                                title="Editar"
                              >
                                <Pencil className="h-4 w-4" />
                              </button>
                              <button 
                                className="p-2 rounded-lg text-slate-400 hover:text-red-600 hover:bg-red-50 transition-colors"
                                onClick={() => handleDeletePayroll(payroll.id)}
                                title="Eliminar"
                              >
                                <Trash2 className="h-4 w-4" />
                              </button>
                            </>
                          )}
                        </div>
                      </td>
                    </motion.tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
          
          {/* Paginación */}
          {payrolls.length > 0 && (
            <div className="px-6 py-4 border-t border-slate-100 flex flex-col sm:flex-row items-center justify-between gap-4">
              <p className="text-sm text-slate-500">
                Mostrando {startIndex + 1}-{Math.min(endIndex, payrolls.length)} de {payrolls.length} nóminas
              </p>
              {totalPages > 1 && (
                <div className="flex items-center gap-2">
                  <button
                    onClick={() => goToPage(currentPage - 1)}
                    disabled={currentPage === 1}
                    className="p-2 rounded-lg text-slate-500 hover:text-slate-700 hover:bg-slate-100 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                  >
                    <ChevronLeft className="h-5 w-5" />
                  </button>
                  <div className="flex items-center gap-1">
                    {[...Array(Math.min(5, totalPages))].map((_, i) => {
                      let pageNum;
                      if (totalPages <= 5) {
                        pageNum = i + 1;
                      } else if (currentPage <= 3) {
                        pageNum = i + 1;
                      } else if (currentPage >= totalPages - 2) {
                        pageNum = totalPages - 4 + i;
                      } else {
                        pageNum = currentPage - 2 + i;
                      }
                      return (
                        <button
                          key={pageNum}
                          onClick={() => goToPage(pageNum)}
                          className={`w-8 h-8 rounded-lg text-sm font-medium transition-colors ${
                            currentPage === pageNum
                              ? 'bg-blue-600 text-white'
                              : 'text-slate-600 hover:bg-slate-100'
                          }`}
                        >
                          {pageNum}
                        </button>
                      );
                    })}
                  </div>
                  <button
                    onClick={() => goToPage(currentPage + 1)}
                    disabled={currentPage === totalPages}
                    className="p-2 rounded-lg text-slate-500 hover:text-slate-700 hover:bg-slate-100 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                  >
                    <ChevronRight className="h-5 w-5" />
                  </button>
                </div>
              )}
            </div>
          )}
        </Card>
      </motion.div>

      {/* Modal de crear/editar nómina */}
      <AnimatePresence>
        {showForm && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
            onClick={handleCloseForm}
          >
            <motion.div
              initial={{ scale: 0.9, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0.9, opacity: 0 }}
              className="bg-white rounded-2xl shadow-xl max-w-md w-full p-6"
              onClick={(e) => e.stopPropagation()}
            >
              <div className="flex items-center justify-between mb-6">
                <h3 className="text-xl font-bold text-slate-800">
                  {editingPayroll ? 'Editar Nómina' : 'Nueva Nómina'}
                </h3>
                <button
                  onClick={handleCloseForm}
                  className="p-2 rounded-lg hover:bg-slate-100 transition-colors"
                >
                  <X className="h-5 w-5 text-slate-500" />
                </button>
              </div>

              <form onSubmit={handleSubmitPayroll} className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">
                    Empleado *
                  </label>
                  <select
                    value={formData.employeeId}
                    onChange={(e) => setFormData({ ...formData, employeeId: e.target.value })}
                    className={`w-full px-3 py-2 border rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
                      formErrors.employeeId ? 'border-red-300 bg-red-50' : 'border-slate-200'
                    }`}
                  >
                    <option value="">Seleccionar empleado...</option>
                    {employees.map((emp) => (
                      <option key={emp.id} value={emp.id}>
                        {emp.name} {emp.surname}
                      </option>
                    ))}
                  </select>
                  {formErrors.employeeId && (
                    <p className="mt-1 text-sm text-red-600">{formErrors.employeeId}</p>
                  )}
                </div>

                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">
                    Monto ($) *
                  </label>
                  <div className="relative">
                    <DollarSign className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-slate-400" />
                    <input
                      type="number"
                      step="0.01"
                      min="0"
                      value={formData.amount}
                      onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
                      className={`w-full pl-10 pr-3 py-2 border rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
                        formErrors.amount ? 'border-red-300 bg-red-50' : 'border-slate-200'
                      }`}
                      placeholder="0.00"
                    />
                  </div>
                  {formErrors.amount && (
                    <p className="mt-1 text-sm text-red-600">{formErrors.amount}</p>
                  )}
                </div>

                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">
                    Fecha de Pago *
                  </label>
                  <div className="relative">
                    <Calendar className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-slate-400" />
                    <input
                      type="date"
                      value={formData.payrollDate}
                      onChange={(e) => setFormData({ ...formData, payrollDate: e.target.value })}
                      className={`w-full pl-10 pr-3 py-2 border rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
                        formErrors.payrollDate ? 'border-red-300 bg-red-50' : 'border-slate-200'
                      }`}
                    />
                  </div>
                  {formErrors.payrollDate && (
                    <p className="mt-1 text-sm text-red-600">{formErrors.payrollDate}</p>
                  )}
                </div>

                <div className="flex gap-3 pt-4">
                  <Button
                    type="button"
                    variant="secondary"
                    className="flex-1"
                    onClick={handleCloseForm}
                  >
                    Cancelar
                  </Button>
                  <Button
                    type="submit"
                    variant="primary"
                    className="flex-1"
                    disabled={saving}
                  >
                    {saving ? (
                      <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin" />
                    ) : (
                      <>
                        <Save className="h-4 w-4" />
                        {editingPayroll ? 'Guardar' : 'Crear'}
                      </>
                    )}
                  </Button>
                </div>
              </form>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Modal de detalle de nómina */}
      <AnimatePresence>
        {showDetailModal && selectedPayroll && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
            onClick={() => setShowDetailModal(false)}
          >
            <motion.div
              initial={{ scale: 0.9, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0.9, opacity: 0 }}
              className="bg-white rounded-2xl shadow-xl max-w-md w-full p-6"
              onClick={(e) => e.stopPropagation()}
            >
              <div className="flex items-center justify-between mb-6">
                <h3 className="text-xl font-bold text-slate-800">Detalle de Nómina</h3>
                <button
                  onClick={() => setShowDetailModal(false)}
                  className="p-2 rounded-lg hover:bg-slate-100 transition-colors"
                >
                  <X className="h-5 w-5 text-slate-500" />
                </button>
              </div>
              
              <div className="space-y-4">
                <div className="flex items-center gap-4 p-4 bg-gradient-to-br from-emerald-50 to-teal-50 rounded-xl">
                  <div className="w-12 h-12 rounded-xl bg-emerald-100 flex items-center justify-center">
                    <DollarSign className="h-6 w-6 text-emerald-600" />
                  </div>
                  <div>
                    <p className="text-sm text-slate-500">Monto</p>
                    <p className="text-2xl font-bold text-emerald-600">
                      ${selectedPayroll.amount?.toLocaleString('es-ES', { minimumFractionDigits: 2 })}
                    </p>
                  </div>
                </div>
                
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <p className="text-sm text-slate-500 mb-1">Empleado</p>
                    <p className="font-medium text-slate-800">{getEmployeeName(selectedPayroll)}</p>
                  </div>
                  <div>
                    <p className="text-sm text-slate-500 mb-1">ID Nómina</p>
                    <p className="font-medium text-slate-800">#{selectedPayroll.id}</p>
                  </div>
                  <div className="col-span-2">
                    <p className="text-sm text-slate-500 mb-1">Fecha de Pago</p>
                    <p className="font-medium text-slate-800">
                      {new Date(selectedPayroll.payrollDate).toLocaleDateString('es-ES', {
                        weekday: 'long',
                        day: 'numeric',
                        month: 'long',
                        year: 'numeric'
                      })}
                    </p>
                  </div>
                </div>
              </div>
              
              <div className="flex gap-3 mt-6">
                <Button 
                  variant="primary" 
                  size="sm" 
                  className="flex-1"
                  onClick={() => setShowDetailModal(false)}
                >
                  Cerrar
                </Button>
              </div>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}

export default PayrollPage;
