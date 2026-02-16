import React, { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
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
  Users,
  UserCheck,
  Plus,
  Pencil,
  Trash2,
  Save,
  Search,
  Filter
} from 'lucide-react';
import Card from '../components/Card';
import Button from '../components/Button';
import Pagination from '../components/Pagination';
import { useItemsPerPage } from '../hooks/useItemsPerPage';
import { payrollService, payrollAdminService, employeeService, userService } from '../services/api';

function PayrollPage() {
  const [searchParams] = useSearchParams();
  const [payrolls, setPayrolls] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedPayroll, setSelectedPayroll] = useState(null);
  const [showDetailModal, setShowDetailModal] = useState(false);
  const [profile, setProfile] = useState(null);
  const [viewMode, setViewMode] = useState('mine'); // 'mine' or 'all' (admin only)
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = useItemsPerPage(15);
  const [searchTerm, setSearchTerm] = useState('');
  const [showFilters, setShowFilters] = useState(false);
  const [filters, setFilters] = useState({
    minAmount: '',
    maxAmount: '',
    startDate: '',
    endDate: ''
  });
  
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

  // Handle search from URL params (Command Palette)
  useEffect(() => {
    const searchFromUrl = searchParams.get('search');
    if (searchFromUrl) {
      setSearchTerm(searchFromUrl);
      // Admin should see all payrolls when searching from command palette
      if (profile?.roleIds?.includes(1)) {
        setViewMode('all');
      }
    }
  }, [searchParams, profile]);

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
      const isManager = profileResponse.data.roleIds?.includes(3);
      const canViewAll = isAdmin || isManager;
      
      let payrollData = [];
      
      // Admin y Manager pueden elegir ver todas o solo las suyas
      if (canViewAll && viewMode === 'all') {
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

  // Filter payrolls by search term and filters
  const filteredPayrolls = payrolls.filter(payroll => {
    const term = searchTerm.toLowerCase();
    const employeeName = getEmployeeName(payroll).toLowerCase();
    
    // Search filter
    if (term && !employeeName.includes(term)) return false;
    
    // Amount filters
    const amount = payroll.amount || 0;
    if (filters.minAmount && amount < parseFloat(filters.minAmount)) return false;
    if (filters.maxAmount && amount > parseFloat(filters.maxAmount)) return false;
    
    // Date filters
    const payrollDate = new Date(payroll.payrollDate);
    if (filters.startDate && payrollDate < new Date(filters.startDate)) return false;
    if (filters.endDate && payrollDate > new Date(filters.endDate + 'T23:59:59')) return false;
    
    return true;
  });

  const hasActiveFilters = filters.minAmount || filters.maxAmount || filters.startDate || filters.endDate;

  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    setFilters(prev => ({ ...prev, [name]: value }));
    setCurrentPage(1);
  };

  const clearFilters = () => {
    setFilters({ minAmount: '', maxAmount: '', startDate: '', endDate: '' });
    setCurrentPage(1);
  };

  // Paginación
  const isAdmin = profile?.roleIds?.includes(1);
  const isManager = profile?.roleIds?.includes(3);
  const canViewAll = isAdmin || isManager;
  const totalPages = Math.ceil(filteredPayrolls.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const currentPayrolls = filteredPayrolls.slice(startIndex, endIndex);

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
          <h1 className="text-2xl sm:text-3xl lg:text-4xl font-bold text-slate-800 dark:text-white mb-1">
            Nóminas 💰
          </h1>
          <p className="text-slate-500 dark:text-slate-400">
            {canViewAll && viewMode === 'all'
              ? 'Gestión de nóminas de todos los empleados' 
              : 'Historial de tus pagos'}
          </p>
        </div>
        <div className="flex gap-2">
          {/* Admin/Manager Toggle */}
          {canViewAll && (
            <div className="flex bg-slate-100 dark:bg-slate-700 rounded-xl p-1">
              <button
                onClick={() => setViewMode('mine')}
                className={`flex items-center gap-2 px-3 py-2 rounded-lg text-sm font-medium transition-all ${
                  viewMode === 'mine'
                    ? 'bg-white dark:bg-slate-600 text-blue-600 dark:text-blue-400 shadow-sm'
                    : 'text-slate-600 dark:text-slate-300 hover:text-slate-800 dark:hover:text-white'
                }`}
              >
                <UserCheck className="h-4 w-4" />
                <span className="hidden sm:inline">Mis nóminas</span>
              </button>
              <button
                onClick={() => setViewMode('all')}
                className={`flex items-center gap-2 px-3 py-2 rounded-lg text-sm font-medium transition-all ${
                  viewMode === 'all'
                    ? 'bg-white dark:bg-slate-600 text-blue-600 dark:text-blue-400 shadow-sm'
                    : 'text-slate-600 dark:text-slate-300 hover:text-slate-800 dark:hover:text-white'
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
                <p className="text-xs sm:text-sm font-medium text-slate-500 dark:text-slate-400 mb-1">{stat.label}</p>
                <p className="text-lg sm:text-xl font-bold text-slate-800 dark:text-white">{stat.value}</p>
              </Card>
            </motion.div>
          );
        })}
      </div>

      {/* Search and Filters */}
      <div className="space-y-3">
        <div className="flex gap-2">
          <div className="relative flex-1">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400 dark:text-slate-500" />
            <input
              type="text"
              placeholder="Buscar por nombre de empleado..."
              value={searchTerm}
              onChange={(e) => {
                setSearchTerm(e.target.value);
                setCurrentPage(1);
              }}
              className="w-full pl-10 pr-4 py-2 border border-gray-200 dark:border-slate-600 rounded-lg bg-white dark:bg-slate-800 text-slate-900 dark:text-white placeholder-gray-400 dark:placeholder-slate-500 focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>
          <button
            onClick={() => setShowFilters(!showFilters)}
            className={`flex items-center gap-2 px-4 py-2 rounded-lg border transition-colors ${
              showFilters || hasActiveFilters
                ? 'bg-blue-50 dark:bg-blue-900/30 border-blue-300 dark:border-blue-700 text-blue-700 dark:text-blue-400'
                : 'bg-white dark:bg-slate-800 border-gray-200 dark:border-slate-600 text-gray-600 dark:text-slate-300 hover:bg-gray-50 dark:hover:bg-slate-700'
            }`}
          >
            <Filter className="h-5 w-5" />
            <span className="hidden sm:inline">Filtros</span>
            {hasActiveFilters && (
              <span className="bg-blue-600 text-white text-xs px-1.5 py-0.5 rounded-full">
                {[filters.minAmount, filters.maxAmount, filters.startDate, filters.endDate].filter(Boolean).length}
              </span>
            )}
          </button>
        </div>

        {/* Filter Panel */}
        {showFilters && (
          <div className="bg-slate-50 dark:bg-slate-800/50 border border-slate-200 dark:border-slate-700 rounded-lg p-4">
            <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-slate-300 mb-1">
                  Monto mínimo
                </label>
                <input
                  type="number"
                  name="minAmount"
                  value={filters.minAmount}
                  onChange={handleFilterChange}
                  placeholder="0.00"
                  className="w-full px-3 py-2 border border-gray-200 dark:border-slate-600 rounded-md bg-white dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-slate-300 mb-1">
                  Monto máximo
                </label>
                <input
                  type="number"
                  name="maxAmount"
                  value={filters.maxAmount}
                  onChange={handleFilterChange}
                  placeholder="10000.00"
                  className="w-full px-3 py-2 border border-gray-200 dark:border-slate-600 rounded-md bg-white dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-slate-300 mb-1">
                  Desde fecha
                </label>
                <input
                  type="date"
                  name="startDate"
                  value={filters.startDate}
                  onChange={handleFilterChange}
                  className="w-full px-3 py-2 border border-gray-200 dark:border-slate-600 rounded-md bg-white dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-slate-300 mb-1">
                  Hasta fecha
                </label>
                <input
                  type="date"
                  name="endDate"
                  value={filters.endDate}
                  onChange={handleFilterChange}
                  className="w-full px-3 py-2 border border-gray-200 dark:border-slate-600 rounded-md bg-white dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-blue-500"
                />
              </div>
            </div>
            {hasActiveFilters && (
              <div className="flex justify-end mt-4">
                <button
                  onClick={clearFilters}
                  className="flex items-center gap-1 px-3 py-1.5 text-gray-600 dark:text-slate-400 hover:text-gray-800 dark:hover:text-white"
                >
                  <X className="h-4 w-4" />
                  Limpiar filtros
                </button>
              </div>
            )}
          </div>
        )}
      </div>

      {/* Loading State */}
      {isLoading && (
        <div className="flex flex-col items-center justify-center py-12 bg-white dark:bg-slate-800 rounded-xl shadow-sm border border-gray-100 dark:border-slate-700">
          <div className="w-8 h-8 border-2 border-blue-500 border-t-transparent rounded-full animate-spin"></div>
          <span className="mt-3 text-slate-500 dark:text-slate-400">Cargando nóminas...</span>
        </div>
      )}

      {/* Error State */}
      {!isLoading && error && (
        <div className="flex flex-col items-center justify-center py-12 bg-white dark:bg-slate-800 rounded-xl shadow-sm border border-gray-100 dark:border-slate-700">
          <div className="w-16 h-16 rounded-full bg-rose-100 dark:bg-rose-900/30 flex items-center justify-center">
            <X className="h-8 w-8 text-rose-500 dark:text-rose-400" />
          </div>
          <p className="mt-3 text-rose-600 dark:text-rose-400 font-medium">{error}</p>
          <Button variant="primary" size="sm" onClick={loadData} className="mt-3">
            Reintentar
          </Button>
        </div>
      )}

      {/* Empty State */}
      {!isLoading && !error && filteredPayrolls.length === 0 && (
        <div className="flex flex-col items-center justify-center py-12 bg-white dark:bg-slate-800 rounded-xl shadow-sm border border-gray-100 dark:border-slate-700">
          <div className="w-16 h-16 rounded-full bg-slate-100 dark:bg-slate-700 flex items-center justify-center">
            <DollarSign className="h-8 w-8 text-slate-400 dark:text-slate-500" />
          </div>
          <p className="mt-3 text-slate-700 dark:text-slate-200 font-medium">
            {searchTerm ? 'No se encontraron resultados' : 'No hay nóminas disponibles'}
          </p>
          <p className="text-sm text-slate-500 dark:text-slate-400">
            {searchTerm ? 'Prueba con otro término de búsqueda' : 'Las nóminas aparecerán aquí cuando estén disponibles'}
          </p>
        </div>
      )}

      {/* Mobile Cards (visible < md) */}
      {!isLoading && !error && currentPayrolls.length > 0 && (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3 }}
          className="md:hidden space-y-3"
        >
          {currentPayrolls.map((payroll, idx) => (
            <motion.div
              key={payroll.id}
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              transition={{ delay: 0.4 + idx * 0.03 }}
              className="bg-white dark:bg-slate-800 rounded-xl shadow-sm border border-gray-100 dark:border-slate-700 p-4"
            >
              {/* Header con empleado y monto */}
              <div className="flex items-center gap-3 mb-3">
                <div className="w-12 h-12 rounded-xl bg-gradient-to-br from-emerald-100 to-teal-100 dark:from-emerald-900/30 dark:to-teal-900/30 flex items-center justify-center">
                  <User className="h-6 w-6 text-emerald-600 dark:text-emerald-400" />
                </div>
                <div className="flex-1 min-w-0">
                  <h3 className="font-semibold text-slate-800 dark:text-white truncate">
                    {getEmployeeName(payroll)}
                  </h3>
                  <span className="text-lg font-bold text-emerald-600 dark:text-emerald-400">
                    ${payroll.amount?.toLocaleString('es-ES', { minimumFractionDigits: 2 })}
                  </span>
                </div>
              </div>

              {/* Fecha */}
              <div className="flex items-center gap-2 text-sm text-slate-500 dark:text-slate-400 mb-3">
                <Calendar className="h-4 w-4" />
                <span>
                  {new Date(payroll.payrollDate).toLocaleDateString('es-ES', {
                    day: 'numeric',
                    month: 'long',
                    year: 'numeric'
                  })}
                </span>
              </div>

              {/* Actions */}
              <div className="flex gap-2 pt-3 border-t border-gray-100 dark:border-slate-700">
                <button
                  onClick={() => handleViewPayroll(payroll)}
                  className="flex-1 flex items-center justify-center gap-2 px-3 py-2 text-sm text-blue-600 dark:text-blue-400 bg-blue-50 dark:bg-blue-900/20 rounded-lg hover:bg-blue-100 dark:hover:bg-blue-900/30 transition-colors"
                >
                  <Eye className="h-4 w-4" />
                  Ver
                </button>
                {isAdmin && (
                  <>
                    <button
                      onClick={() => handleOpenForm(payroll)}
                      className="flex-1 flex items-center justify-center gap-2 px-3 py-2 text-sm text-amber-600 dark:text-amber-400 bg-amber-50 dark:bg-amber-900/20 rounded-lg hover:bg-amber-100 dark:hover:bg-amber-900/30 transition-colors"
                    >
                      <Pencil className="h-4 w-4" />
                      Editar
                    </button>
                    <button
                      onClick={() => handleDeletePayroll(payroll.id)}
                      className="flex-1 flex items-center justify-center gap-2 px-3 py-2 text-sm text-red-600 dark:text-red-400 bg-red-50 dark:bg-red-900/20 rounded-lg hover:bg-red-100 dark:hover:bg-red-900/30 transition-colors"
                    >
                      <Trash2 className="h-4 w-4" />
                      Eliminar
                    </button>
                  </>
                )}
              </div>
            </motion.div>
          ))}
          
          {/* Paginación Mobile */}
          {filteredPayrolls.length > 0 && (
            <div className="pt-4">
              <Pagination
                currentPage={currentPage}
                totalPages={totalPages}
                onPageChange={goToPage}
                totalItems={filteredPayrolls.length}
                itemsPerPage={itemsPerPage}
                showingFrom={startIndex + 1}
                showingTo={Math.min(endIndex, filteredPayrolls.length)}
                itemName="nóminas"
              />
            </div>
          )}
        </motion.div>
      )}

      {/* Desktop Table (visible >= md) */}
      {!isLoading && !error && currentPayrolls.length > 0 && (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3 }}
          className="hidden md:block"
        >
          <Card variant="solid" className="overflow-hidden">
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b border-slate-100 dark:border-slate-700 bg-slate-50/50 dark:bg-slate-700/50">
                    <th className="px-6 py-4 text-left text-xs font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wider">
                      Empleado
                    </th>
                    <th className="px-6 py-4 text-left text-xs font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wider">
                      Monto
                    </th>
                    <th className="px-6 py-4 text-left text-xs font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wider">
                      Fecha
                    </th>
                    <th className="px-6 py-4 text-right text-xs font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wider">
                      Acciones
                    </th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100 dark:divide-slate-700">
                  {currentPayrolls.map((payroll, idx) => (
                    <motion.tr 
                      key={payroll.id}
                      initial={{ opacity: 0 }}
                      animate={{ opacity: 1 }}
                      transition={{ delay: 0.4 + idx * 0.03 }}
                      className="hover:bg-slate-50/50 dark:hover:bg-slate-700/50 transition-colors"
                    >
                      <td className="px-6 py-4">
                        <div className="flex items-center gap-3">
                          <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-emerald-100 to-teal-100 dark:from-emerald-900/30 dark:to-teal-900/30 flex items-center justify-center">
                            <User className="h-5 w-5 text-emerald-600 dark:text-emerald-400" />
                          </div>
                          <p className="font-medium text-slate-800 dark:text-white">{getEmployeeName(payroll)}</p>
                        </div>
                      </td>
                      <td className="px-6 py-4">
                        <span className="font-semibold text-emerald-600">
                          ${payroll.amount?.toLocaleString('es-ES', { minimumFractionDigits: 2 })}
                        </span>
                      </td>
                      <td className="px-6 py-4 text-slate-500 dark:text-slate-400">
                        {new Date(payroll.payrollDate).toLocaleDateString('es-ES', {
                          day: 'numeric',
                          month: 'short',
                          year: 'numeric'
                        })}
                      </td>
                      <td className="px-6 py-4 text-right">
                        <div className="flex items-center justify-end gap-1">
                          <button 
                            className="p-2 rounded-lg text-slate-400 hover:text-blue-600 hover:bg-blue-50 dark:text-slate-500 dark:hover:text-blue-400 dark:hover:bg-blue-900/30 transition-colors"
                            onClick={() => handleViewPayroll(payroll)}
                            title="Ver detalle"
                          >
                            <Eye className="h-4 w-4" />
                          </button>
                          {isAdmin && (
                            <>
                              <button 
                                className="p-2 rounded-lg text-slate-400 hover:text-amber-600 hover:bg-amber-50 dark:text-slate-500 dark:hover:text-amber-400 dark:hover:bg-amber-900/30 transition-colors"
                                onClick={() => handleOpenForm(payroll)}
                                title="Editar"
                              >
                                <Pencil className="h-4 w-4" />
                              </button>
                              <button 
                                className="p-2 rounded-lg text-slate-400 hover:text-red-600 hover:bg-red-50 dark:text-slate-500 dark:hover:text-red-400 dark:hover:bg-red-900/30 transition-colors"
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
                  ))}
                </tbody>
              </table>
            </div>
            
            {/* Paginación Desktop */}
            {filteredPayrolls.length > 0 && (
              <div className="px-6 py-4">
                <Pagination
                  currentPage={currentPage}
                  totalPages={totalPages}
                  onPageChange={goToPage}
                  totalItems={filteredPayrolls.length}
                  itemsPerPage={itemsPerPage}
                  showingFrom={startIndex + 1}
                  showingTo={Math.min(endIndex, filteredPayrolls.length)}
                  itemName="nóminas"
                />
              </div>
            )}
          </Card>
        </motion.div>
      )}

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
              className="bg-white dark:bg-slate-800 rounded-2xl shadow-xl max-w-md w-full p-6"
              onClick={(e) => e.stopPropagation()}
            >
              <div className="flex items-center justify-between mb-6">
                <h3 className="text-xl font-bold text-slate-800 dark:text-white">
                  {editingPayroll ? 'Editar Nómina' : 'Nueva Nómina'}
                </h3>
                <button
                  onClick={handleCloseForm}
                  className="p-2 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-700 transition-colors"
                >
                  <X className="h-5 w-5 text-slate-500 dark:text-slate-400" />
                </button>
              </div>

              <form onSubmit={handleSubmitPayroll} className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">
                    Empleado *
                  </label>
                  <select
                    value={formData.employeeId}
                    onChange={(e) => setFormData({ ...formData, employeeId: e.target.value })}
                    className={`w-full px-3 py-2 border rounded-xl bg-white dark:bg-slate-700 text-slate-900 dark:text-white focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
                      formErrors.employeeId ? 'border-red-300 bg-red-50 dark:bg-red-900/20' : 'border-slate-200 dark:border-slate-600'
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
                  <label className="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">
                    Monto ($) *
                  </label>
                  <div className="relative">
                    <DollarSign className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-slate-400 dark:text-slate-500" />
                    <input
                      type="number"
                      step="0.01"
                      min="0"
                      value={formData.amount}
                      onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
                      className={`w-full pl-10 pr-3 py-2 border rounded-xl bg-white dark:bg-slate-700 text-slate-900 dark:text-white focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
                        formErrors.amount ? 'border-red-300 bg-red-50 dark:bg-red-900/20' : 'border-slate-200 dark:border-slate-600'
                      }`}
                      placeholder="0.00"
                    />
                  </div>
                  {formErrors.amount && (
                    <p className="mt-1 text-sm text-red-600">{formErrors.amount}</p>
                  )}
                </div>

                <div>
                  <label className="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">
                    Fecha de Pago *
                  </label>
                  <div className="relative">
                    <Calendar className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-slate-400 dark:text-slate-500" />
                    <input
                      type="date"
                      value={formData.payrollDate}
                      onChange={(e) => setFormData({ ...formData, payrollDate: e.target.value })}
                      className={`w-full pl-10 pr-3 py-2 border rounded-xl bg-white dark:bg-slate-700 text-slate-900 dark:text-white focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
                        formErrors.payrollDate ? 'border-red-300 bg-red-50 dark:bg-red-900/20' : 'border-slate-200 dark:border-slate-600'
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
              className="bg-white dark:bg-slate-800 rounded-2xl shadow-xl max-w-md w-full p-6"
              onClick={(e) => e.stopPropagation()}
            >
              <div className="flex items-center justify-between mb-6">
                <h3 className="text-xl font-bold text-slate-800 dark:text-white">Detalle de Nómina</h3>
                <button
                  onClick={() => setShowDetailModal(false)}
                  className="p-2 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-700 transition-colors"
                >
                  <X className="h-5 w-5 text-slate-500 dark:text-slate-400" />
                </button>
              </div>
              
              <div className="space-y-4">
                <div className="flex items-center gap-4 p-4 bg-gradient-to-br from-emerald-50 to-teal-50 dark:from-emerald-900/20 dark:to-teal-900/20 rounded-xl">
                  <div className="w-12 h-12 rounded-xl bg-emerald-100 dark:bg-emerald-900/30 flex items-center justify-center">
                    <DollarSign className="h-6 w-6 text-emerald-600 dark:text-emerald-400" />
                  </div>
                  <div>
                    <p className="text-sm text-slate-500 dark:text-slate-400">Monto</p>
                    <p className="text-2xl font-bold text-emerald-600 dark:text-emerald-400">
                      ${selectedPayroll.amount?.toLocaleString('es-ES', { minimumFractionDigits: 2 })}
                    </p>
                  </div>
                </div>
                
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <p className="text-sm text-slate-500 dark:text-slate-400 mb-1">Empleado</p>
                    <p className="font-medium text-slate-800 dark:text-white">{getEmployeeName(selectedPayroll)}</p>
                  </div>
                  <div>
                    <p className="text-sm text-slate-500 dark:text-slate-400 mb-1">ID Nómina</p>
                    <p className="font-medium text-slate-800 dark:text-white">#{selectedPayroll.id}</p>
                  </div>
                  <div className="col-span-2">
                    <p className="text-sm text-slate-500 dark:text-slate-400 mb-1">Fecha de Pago</p>
                    <p className="font-medium text-slate-800 dark:text-white">
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
