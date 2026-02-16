import { useState, useEffect } from 'react';
import { employeeService, positionService } from '../services/api';
import { useAuthStore } from '../store/authStore';
import { Plus, Pencil, Trash2, Users, Search, X, Save, Mail, Calendar, Link2 } from 'lucide-react';

export default function EmployeesPage() {
  const { user } = useAuthStore();
  const [employees, setEmployees] = useState([]);
  const [positions, setPositions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingEmployee, setEditingEmployee] = useState(null);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [formData, setFormData] = useState({
    name: '',
    surname: '',
    email: '',
    birthDate: '',
    positionId: '',
  });
  const [formErrors, setFormErrors] = useState({});
  const [saving, setSaving] = useState(false);
  const [linkedUser, setLinkedUser] = useState(null);

  const isAdmin = user?.roleId === 1;
  const isManager = user?.roleId === 3;
  const canManageEmployees = isAdmin || isManager;

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [employeesRes, positionsRes] = await Promise.all([
        employeeService.getAll(),
        positionService.getAll(),
      ]);
      setEmployees(employeesRes.data);
      setPositions(positionsRes.data);
    } catch (error) {
      console.error('Error loading data:', error);
      setError('No se pudieron cargar los empleados.');
    } finally {
      setLoading(false);
    }
  };

  const validateForm = () => {
    const errors = {};
    if (!formData.name?.trim()) {
      errors.name = 'El nombre es requerido';
    }
    if (!formData.surname?.trim()) {
      errors.surname = 'El apellido es requerido';
    }
    if (!formData.birthDate) {
      errors.birthDate = 'La fecha de nacimiento es requerida';
    }
    if (!formData.positionId) {
      errors.positionId = 'El cargo es requerido';
    }
    if (formData.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      errors.email = 'Email no válido';
    }
    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;

    setSaving(true);
    try {
      // Format birthDate as ISO datetime for backend
      const birthDateTime = new Date(formData.birthDate);
      birthDateTime.setHours(12, 0, 0, 0);
      
      const payload = {
        name: formData.name.trim(),
        surname: formData.surname.trim(),
        email: formData.email?.trim() || null,
        birthDate: birthDateTime.toISOString(),
        positionId: parseInt(formData.positionId),
      };

      if (editingEmployee) {
        const response = await employeeService.update(editingEmployee.id, payload);
        setEmployees(employees.map(e => e.id === editingEmployee.id ? response.data : e));
      } else {
        const response = await employeeService.create(payload);
        setEmployees([response.data, ...employees]);
      }
      handleCloseForm();
    } catch (error) {
      console.error('Error saving employee:', error);
      setError(error.response?.data?.message || 'Error al guardar el empleado');
    } finally {
      setSaving(false);
    }
  };

  const handleEdit = (employee) => {
    setEditingEmployee(employee);
    
    // Parse birthDate (could be array or string)
    let birthDateStr = '';
    if (employee.birthDate) {
      if (Array.isArray(employee.birthDate)) {
        const [year, month, day] = employee.birthDate;
        birthDateStr = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
      } else {
        birthDateStr = employee.birthDate.split('T')[0];
      }
    }
    
    setFormData({
      name: employee.name || '',
      surname: employee.surname || '',
      email: employee.email || '',
      birthDate: birthDateStr,
      positionId: employee.positionId?.toString() || '',
    });
    setFormErrors({});
    setShowForm(true);
  };

  const handleCloseForm = () => {
    setShowForm(false);
    setEditingEmployee(null);
    setFormData({ name: '', surname: '', email: '', birthDate: '', positionId: '' });
    setFormErrors({});
  };

  const handleDelete = async (id) => {
    if (!window.confirm('¿Estás seguro de que deseas eliminar este empleado? Esta acción eliminará también sus nóminas y gastos asociados.')) {
      return;
    }
    try {
      await employeeService.delete(id);
      setEmployees(employees.filter(e => e.id !== id));
    } catch (error) {
      console.error('Error deleting employee:', error);
      alert(error.response?.data?.message || 'Error al eliminar el empleado.');
    }
  };

  const filteredEmployees = employees.filter(employee => {
    const fullName = `${employee.name} ${employee.surname}`.toLowerCase();
    const term = searchTerm.toLowerCase();
    return fullName.includes(term) || employee.email?.toLowerCase().includes(term);
  });

  const getPositionName = (positionId) => {
    const position = positions.find(p => p.id === positionId);
    return position?.name || 'Sin cargo';
  };

  const formatDate = (dateValue) => {
    if (!dateValue) return '-';
    
    let date;
    if (Array.isArray(dateValue)) {
      const [year, month, day] = dateValue;
      date = new Date(year, month - 1, day);
    } else {
      date = new Date(dateValue);
    }
    
    return date.toLocaleDateString('es-ES', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    });
  };

  const handleViewLinkedUser = (employee) => {
    setLinkedUser({
      id: employee.expenseUserId,
      name: employee.expenseUserName,
      email: employee.expenseUserEmail,
    });
  };

  if (!canManageEmployees) {
    return (
      <div className="flex flex-col items-center justify-center h-64 text-gray-500 dark:text-slate-400">
        <Users className="h-16 w-16 mb-4 opacity-50" />
        <p className="text-lg">No tienes permisos para acceder a esta sección.</p>
      </div>
    );
  }

  if (loading) {
    return (
      <div className="flex flex-col justify-center items-center h-64 gap-3">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        <span className="text-slate-500 dark:text-slate-400">Cargando empleados...</span>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Gestión de Empleados</h1>
          <p className="text-sm text-gray-500 dark:text-slate-400 mt-1">
            {employees.length} empleado{employees.length !== 1 ? 's' : ''} registrado{employees.length !== 1 ? 's' : ''}
          </p>
        </div>

        <button
          onClick={() => setShowForm(true)}
          className="flex items-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors shadow-sm"
        >
          <Plus className="mr-2 h-5 w-5" />
          Nuevo Empleado
        </button>
      </div>

      {/* Error */}
      {error && (
        <div className="bg-red-50 dark:bg-red-900/20 text-red-600 dark:text-red-400 p-4 rounded-lg flex items-center justify-between">
          <span>{error}</span>
          <button onClick={() => setError('')} className="text-red-400 hover:text-red-600 dark:hover:text-red-300">
            <X className="h-5 w-5" />
          </button>
        </div>
      )}

      {/* Search */}
      <div className="relative">
        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400 dark:text-slate-500" />
        <input
          type="text"
          placeholder="Buscar por nombre o email..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="w-full pl-10 pr-4 py-2 border border-gray-200 dark:border-slate-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white dark:bg-slate-800 text-gray-900 dark:text-white placeholder-gray-400 dark:placeholder-slate-500"
        />
      </div>

      {/* Form Modal */}
      {showForm && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white dark:bg-slate-800 rounded-xl shadow-xl w-full max-w-md max-h-[90vh] overflow-y-auto">
            <div className="flex items-center justify-between p-4 border-b dark:border-slate-700 sticky top-0 bg-white dark:bg-slate-800">
              <h2 className="text-lg font-semibold text-gray-900 dark:text-white">
                {editingEmployee ? 'Editar Empleado' : 'Nuevo Empleado'}
              </h2>
              <button
                onClick={handleCloseForm}
                className="text-gray-400 dark:text-slate-500 hover:text-gray-600 dark:hover:text-slate-300 transition-colors"
              >
                <X className="h-5 w-5" />
              </button>
            </div>

            <form onSubmit={handleSubmit} className="p-4 space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 dark:text-slate-300 mb-1">
                    Nombre *
                  </label>
                  <input
                    type="text"
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    className={`w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white dark:bg-slate-700 text-gray-900 dark:text-white ${
                      formErrors.name ? 'border-red-300 bg-red-50 dark:bg-red-900/20' : 'border-gray-200 dark:border-slate-600'
                    }`}
                    placeholder="Juan"
                  />
                  {formErrors.name && (
                    <p className="mt-1 text-sm text-red-600 dark:text-red-400">{formErrors.name}</p>
                  )}
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 dark:text-slate-300 mb-1">
                    Apellido *
                  </label>
                  <input
                    type="text"
                    value={formData.surname}
                    onChange={(e) => setFormData({ ...formData, surname: e.target.value })}
                    className={`w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white dark:bg-slate-700 text-gray-900 dark:text-white ${
                      formErrors.surname ? 'border-red-300 bg-red-50 dark:bg-red-900/20' : 'border-gray-200 dark:border-slate-600'
                    }`}
                    placeholder="Pérez"
                  />
                  {formErrors.surname && (
                    <p className="mt-1 text-sm text-red-600 dark:text-red-400">{formErrors.surname}</p>
                  )}
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-slate-300 mb-1">
                  Email
                </label>
                <div className="relative">
                  <Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400 dark:text-slate-500" />
                  <input
                    type="email"
                    value={formData.email}
                    onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                    className={`w-full pl-10 pr-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white dark:bg-slate-700 text-gray-900 dark:text-white ${
                      formErrors.email ? 'border-red-300 bg-red-50 dark:bg-red-900/20' : 'border-gray-200 dark:border-slate-600'
                    }`}
                    placeholder="juan.perez@empresa.com"
                  />
                </div>
                {formErrors.email && (
                  <p className="mt-1 text-sm text-red-600 dark:text-red-400">{formErrors.email}</p>
                )}
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-slate-300 mb-1">
                  Fecha de Nacimiento *
                </label>
                <div className="relative">
                  <Calendar className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400 dark:text-slate-500" />
                  <input
                    type="date"
                    value={formData.birthDate}
                    onChange={(e) => setFormData({ ...formData, birthDate: e.target.value })}
                    className={`w-full pl-10 pr-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white dark:bg-slate-700 text-gray-900 dark:text-white ${
                      formErrors.birthDate ? 'border-red-300 bg-red-50 dark:bg-red-900/20' : 'border-gray-200 dark:border-slate-600'
                    }`}
                  />
                </div>
                {formErrors.birthDate && (
                  <p className="mt-1 text-sm text-red-600 dark:text-red-400">{formErrors.birthDate}</p>
                )}
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-slate-300 mb-1">
                  Cargo *
                </label>
                <select
                  value={formData.positionId}
                  onChange={(e) => setFormData({ ...formData, positionId: e.target.value })}
                  className={`w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white dark:bg-slate-700 text-gray-900 dark:text-white ${
                    formErrors.positionId ? 'border-red-300 bg-red-50 dark:bg-red-900/20' : 'border-gray-200 dark:border-slate-600'
                  }`}
                >
                  <option value="">Seleccionar cargo...</option>
                  {positions.map((position) => (
                    <option key={position.id} value={position.id}>
                      {position.name}
                    </option>
                  ))}
                </select>
                {formErrors.positionId && (
                  <p className="mt-1 text-sm text-red-600 dark:text-red-400">{formErrors.positionId}</p>
                )}
              </div>

              <div className="flex gap-3 pt-4">
                <button
                  type="button"
                  onClick={handleCloseForm}
                  className="flex-1 px-4 py-2 border border-gray-300 dark:border-slate-600 text-gray-700 dark:text-slate-300 rounded-lg hover:bg-gray-50 dark:hover:bg-slate-700 transition-colors"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  disabled={saving}
                  className="flex-1 flex items-center justify-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {saving ? (
                    <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white" />
                  ) : (
                    <>
                      <Save className="h-4 w-4" />
                      {editingEmployee ? 'Guardar Cambios' : 'Crear Empleado'}
                    </>
                  )}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Empty State */}
      {filteredEmployees.length === 0 && (
        <div className="bg-white dark:bg-slate-800 rounded-xl shadow-sm border border-gray-100 dark:border-slate-700 p-12 text-center">
          <Users className="h-12 w-12 mx-auto mb-3 text-gray-400 dark:text-slate-500 opacity-50" />
          <p className="text-gray-500 dark:text-slate-400">
            {searchTerm ? 'No se encontraron resultados' : 'No hay empleados registrados'}
          </p>
        </div>
      )}

      {/* Mobile Cards (visible < md) */}
      {filteredEmployees.length > 0 && (
        <div className="md:hidden space-y-3">
          {filteredEmployees.map((employee) => (
            <div
              key={employee.id}
              className="bg-white dark:bg-slate-800 rounded-xl shadow-sm border border-gray-100 dark:border-slate-700 p-4"
            >
              {/* Header con avatar y nombre */}
              <div className="flex items-center gap-3 mb-3">
                <div className="w-12 h-12 rounded-full bg-gradient-to-br from-blue-500 to-indigo-600 flex items-center justify-center text-white font-semibold">
                  {employee.name?.charAt(0)}{employee.surname?.charAt(0)}
                </div>
                <div className="flex-1 min-w-0">
                  <h3 className="font-semibold text-gray-900 dark:text-white truncate">
                    {employee.name} {employee.surname}
                  </h3>
                  <span className="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-blue-100 dark:bg-blue-900/30 text-blue-800 dark:text-blue-400">
                    {getPositionName(employee.positionId)}
                  </span>
                </div>
                <span className="text-xs text-gray-400 dark:text-slate-500 font-mono">#{employee.id}</span>
              </div>

              {/* Info */}
              <div className="space-y-2 text-sm mb-3">
                <div className="flex items-center gap-2 text-gray-600 dark:text-slate-300">
                  <Mail className="h-4 w-4 text-gray-400 dark:text-slate-500 flex-shrink-0" />
                  <span className="truncate">{employee.email || 'Sin email'}</span>
                </div>
                <div className="flex items-center gap-2 text-gray-600 dark:text-slate-300">
                  <Calendar className="h-4 w-4 text-gray-400 dark:text-slate-500 flex-shrink-0" />
                  <span>{formatDate(employee.birthDate)}</span>
                </div>
                {employee.expenseUserId && (
                  <div className="flex items-center gap-2 text-emerald-600 dark:text-emerald-400">
                    <Link2 className="h-4 w-4 flex-shrink-0" />
                    <button
                      onClick={() => handleViewLinkedUser(employee)}
                      className="text-sm hover:underline"
                    >
                      Usuario vinculado
                    </button>
                  </div>
                )}
              </div>

              {/* Actions */}
              <div className="flex gap-2 pt-3 border-t border-gray-100 dark:border-slate-700">
                <button
                  onClick={() => handleEdit(employee)}
                  className="flex-1 flex items-center justify-center gap-2 px-3 py-2 text-sm text-blue-600 dark:text-blue-400 bg-blue-50 dark:bg-blue-900/20 rounded-lg hover:bg-blue-100 dark:hover:bg-blue-900/30 transition-colors"
                >
                  <Pencil className="h-4 w-4" />
                  Editar
                </button>
                <button
                  onClick={() => handleDelete(employee.id)}
                  className="flex-1 flex items-center justify-center gap-2 px-3 py-2 text-sm text-red-600 dark:text-red-400 bg-red-50 dark:bg-red-900/20 rounded-lg hover:bg-red-100 dark:hover:bg-red-900/30 transition-colors"
                >
                  <Trash2 className="h-4 w-4" />
                  Eliminar
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Desktop Table (visible >= md) */}
      {filteredEmployees.length > 0 && (
        <div className="hidden md:block bg-white dark:bg-slate-800 rounded-xl shadow-sm border border-gray-100 dark:border-slate-700 overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="bg-gray-50 dark:bg-slate-700 border-b border-gray-100 dark:border-slate-600">
                  <th className="text-left py-3 px-4 text-sm font-semibold text-gray-400 dark:text-slate-500 w-16">ID</th>
                  <th className="text-left py-3 px-4 text-sm font-semibold text-gray-600 dark:text-slate-300">Empleado</th>
                  <th className="text-left py-3 px-4 text-sm font-semibold text-gray-600 dark:text-slate-300">Email</th>
                  <th className="text-left py-3 px-4 text-sm font-semibold text-gray-600 dark:text-slate-300">Cargo</th>
                  <th className="text-left py-3 px-4 text-sm font-semibold text-gray-600 dark:text-slate-300">Fecha Nac.</th>
                  <th className="text-center py-3 px-4 text-sm font-semibold text-gray-600 dark:text-slate-300">Usuario</th>
                  <th className="text-center py-3 px-4 text-sm font-semibold text-gray-600 dark:text-slate-300">Acciones</th>
                </tr>
              </thead>
              <tbody>
                {filteredEmployees.map((employee) => (
                  <tr
                    key={employee.id}
                    className="border-b border-gray-50 dark:border-slate-700 hover:bg-gray-50 dark:hover:bg-slate-700 transition-colors"
                  >
                    <td className="py-3 px-4 text-sm text-gray-400 dark:text-slate-500 font-mono">
                      {employee.id}
                    </td>
                    <td className="py-3 px-4">
                      <div className="flex items-center gap-3">
                        <div className="w-10 h-10 rounded-full bg-gradient-to-br from-blue-500 to-indigo-600 flex items-center justify-center text-white font-semibold text-sm">
                          {employee.name?.charAt(0)}{employee.surname?.charAt(0)}
                        </div>
                        <div>
                          <span className="font-medium text-gray-900 dark:text-white">
                            {employee.name} {employee.surname}
                          </span>
                        </div>
                      </div>
                    </td>
                    <td className="py-3 px-4 text-gray-600 dark:text-slate-300">
                      {employee.email || '-'}
                    </td>
                    <td className="py-3 px-4">
                      <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 dark:bg-blue-900/30 text-blue-800 dark:text-blue-400">
                        {getPositionName(employee.positionId)}
                      </span>
                    </td>
                    <td className="py-3 px-4 text-gray-600 dark:text-slate-300">
                      {formatDate(employee.birthDate)}
                    </td>
                    <td className="py-3 px-4">
                      <div className="flex items-center justify-center">
                        {employee.expenseUserId ? (
                          <button
                            onClick={() => handleViewLinkedUser(employee)}
                            className="p-2 text-emerald-600 dark:text-emerald-400 hover:text-emerald-700 hover:bg-emerald-50 dark:hover:bg-emerald-900/30 rounded-lg transition-colors"
                            title="Ver usuario vinculado"
                          >
                            <Link2 className="h-4 w-4" />
                          </button>
                        ) : (
                          <span className="text-gray-300 dark:text-slate-600">-</span>
                        )}
                      </div>
                    </td>
                    <td className="py-3 px-4">
                      <div className="flex items-center justify-center gap-2">
                        <button
                          onClick={() => handleEdit(employee)}
                          className="p-2 text-gray-400 dark:text-slate-500 hover:text-blue-600 dark:hover:text-blue-400 hover:bg-blue-50 dark:hover:bg-blue-900/30 rounded-lg transition-colors"
                          title="Editar"
                        >
                          <Pencil className="h-4 w-4" />
                        </button>
                        <button
                          onClick={() => handleDelete(employee.id)}
                          className="p-2 text-gray-400 dark:text-slate-500 hover:text-red-600 dark:hover:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/30 rounded-lg transition-colors"
                          title="Eliminar"
                        >
                          <Trash2 className="h-4 w-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Linked User Modal */}
      {linkedUser && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white dark:bg-slate-800 rounded-xl shadow-xl w-full max-w-sm">
            <div className="flex items-center justify-between p-4 border-b dark:border-slate-700">
              <h2 className="text-lg font-semibold text-gray-900 dark:text-white flex items-center gap-2">
                <Link2 className="h-5 w-5 text-emerald-600 dark:text-emerald-400" />
                Usuario Vinculado
              </h2>
              <button
                onClick={() => setLinkedUser(null)}
                className="text-gray-400 dark:text-slate-500 hover:text-gray-600 dark:hover:text-slate-300 transition-colors"
              >
                <X className="h-5 w-5" />
              </button>
            </div>
            <div className="p-4 space-y-3">
              <div>
                <span className="text-sm text-gray-500 dark:text-slate-400">Nombre</span>
                <p className="font-medium text-gray-900 dark:text-white">{linkedUser.name || '-'}</p>
              </div>
              <div>
                <span className="text-sm text-gray-500 dark:text-slate-400">Email</span>
                <p className="font-medium text-gray-900 dark:text-white">{linkedUser.email || '-'}</p>
              </div>
              <div>
                <span className="text-sm text-gray-500 dark:text-slate-400">ID de Usuario</span>
                <p className="font-medium text-gray-900 dark:text-white">{linkedUser.id}</p>
              </div>
            </div>
            <div className="p-4 border-t dark:border-slate-700">
              <button
                onClick={() => setLinkedUser(null)}
                className="w-full px-4 py-2 bg-gray-100 dark:bg-slate-700 text-gray-700 dark:text-slate-300 rounded-lg hover:bg-gray-200 dark:hover:bg-slate-600 transition-colors"
              >
                Cerrar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
