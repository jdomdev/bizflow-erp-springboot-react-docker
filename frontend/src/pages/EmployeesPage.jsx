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

  if (!isAdmin) {
    return (
      <div className="flex flex-col items-center justify-center h-64 text-gray-500">
        <Users className="h-16 w-16 mb-4 opacity-50" />
        <p className="text-lg">No tienes permisos para acceder a esta sección.</p>
      </div>
    );
  }

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Gestión de Empleados</h1>
          <p className="text-sm text-gray-500 mt-1">
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
        <div className="bg-red-50 text-red-600 p-4 rounded-lg flex items-center justify-between">
          <span>{error}</span>
          <button onClick={() => setError('')} className="text-red-400 hover:text-red-600">
            <X className="h-5 w-5" />
          </button>
        </div>
      )}

      {/* Search */}
      <div className="relative">
        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
        <input
          type="text"
          placeholder="Buscar por nombre o email..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
        />
      </div>

      {/* Form Modal */}
      {showForm && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-xl shadow-xl w-full max-w-md max-h-[90vh] overflow-y-auto">
            <div className="flex items-center justify-between p-4 border-b sticky top-0 bg-white">
              <h2 className="text-lg font-semibold text-gray-900">
                {editingEmployee ? 'Editar Empleado' : 'Nuevo Empleado'}
              </h2>
              <button
                onClick={handleCloseForm}
                className="text-gray-400 hover:text-gray-600 transition-colors"
              >
                <X className="h-5 w-5" />
              </button>
            </div>

            <form onSubmit={handleSubmit} className="p-4 space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Nombre *
                  </label>
                  <input
                    type="text"
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    className={`w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
                      formErrors.name ? 'border-red-300 bg-red-50' : 'border-gray-200'
                    }`}
                    placeholder="Juan"
                  />
                  {formErrors.name && (
                    <p className="mt-1 text-sm text-red-600">{formErrors.name}</p>
                  )}
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Apellido *
                  </label>
                  <input
                    type="text"
                    value={formData.surname}
                    onChange={(e) => setFormData({ ...formData, surname: e.target.value })}
                    className={`w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
                      formErrors.surname ? 'border-red-300 bg-red-50' : 'border-gray-200'
                    }`}
                    placeholder="Pérez"
                  />
                  {formErrors.surname && (
                    <p className="mt-1 text-sm text-red-600">{formErrors.surname}</p>
                  )}
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Email
                </label>
                <div className="relative">
                  <Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
                  <input
                    type="email"
                    value={formData.email}
                    onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                    className={`w-full pl-10 pr-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
                      formErrors.email ? 'border-red-300 bg-red-50' : 'border-gray-200'
                    }`}
                    placeholder="juan.perez@empresa.com"
                  />
                </div>
                {formErrors.email && (
                  <p className="mt-1 text-sm text-red-600">{formErrors.email}</p>
                )}
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Fecha de Nacimiento *
                </label>
                <div className="relative">
                  <Calendar className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
                  <input
                    type="date"
                    value={formData.birthDate}
                    onChange={(e) => setFormData({ ...formData, birthDate: e.target.value })}
                    className={`w-full pl-10 pr-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
                      formErrors.birthDate ? 'border-red-300 bg-red-50' : 'border-gray-200'
                    }`}
                  />
                </div>
                {formErrors.birthDate && (
                  <p className="mt-1 text-sm text-red-600">{formErrors.birthDate}</p>
                )}
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Cargo *
                </label>
                <select
                  value={formData.positionId}
                  onChange={(e) => setFormData({ ...formData, positionId: e.target.value })}
                  className={`w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
                    formErrors.positionId ? 'border-red-300 bg-red-50' : 'border-gray-200'
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
                  <p className="mt-1 text-sm text-red-600">{formErrors.positionId}</p>
                )}
              </div>

              <div className="flex gap-3 pt-4">
                <button
                  type="button"
                  onClick={handleCloseForm}
                  className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
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

      {/* Employees Table */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="bg-gray-50 border-b border-gray-100">
                <th className="text-left py-3 px-4 text-sm font-semibold text-gray-400 w-16">ID</th>
                <th className="text-left py-3 px-4 text-sm font-semibold text-gray-600">Empleado</th>
                <th className="text-left py-3 px-4 text-sm font-semibold text-gray-600">Email</th>
                <th className="text-left py-3 px-4 text-sm font-semibold text-gray-600">Cargo</th>
                <th className="text-left py-3 px-4 text-sm font-semibold text-gray-600">Fecha Nac.</th>
                <th className="text-center py-3 px-4 text-sm font-semibold text-gray-600">Usuario</th>
                <th className="text-center py-3 px-4 text-sm font-semibold text-gray-600">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {filteredEmployees.length === 0 ? (
                <tr>
                  <td colSpan={7} className="text-center py-12 text-gray-500">
                    <Users className="h-12 w-12 mx-auto mb-3 opacity-50" />
                    <p>{searchTerm ? 'No se encontraron resultados' : 'No hay empleados registrados'}</p>
                  </td>
                </tr>
              ) : (
                filteredEmployees.map((employee) => (
                  <tr
                    key={employee.id}
                    className="border-b border-gray-50 hover:bg-gray-50 transition-colors"
                  >
                    <td className="py-3 px-4 text-sm text-gray-400 font-mono">
                      {employee.id}
                    </td>
                    <td className="py-3 px-4">
                      <div className="flex items-center gap-3">
                        <div className="w-10 h-10 rounded-full bg-gradient-to-br from-blue-500 to-indigo-600 flex items-center justify-center text-white font-semibold text-sm">
                          {employee.name?.charAt(0)}{employee.surname?.charAt(0)}
                        </div>
                        <div>
                          <span className="font-medium text-gray-900">
                            {employee.name} {employee.surname}
                          </span>
                        </div>
                      </div>
                    </td>
                    <td className="py-3 px-4 text-gray-600">
                      {employee.email || '-'}
                    </td>
                    <td className="py-3 px-4">
                      <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                        {getPositionName(employee.positionId)}
                      </span>
                    </td>
                    <td className="py-3 px-4 text-gray-600">
                      {formatDate(employee.birthDate)}
                    </td>
                    <td className="py-3 px-4">
                      <div className="flex items-center justify-center">
                        {employee.expenseUserId ? (
                          <button
                            onClick={() => handleViewLinkedUser(employee)}
                            className="p-2 text-emerald-600 hover:text-emerald-700 hover:bg-emerald-50 rounded-lg transition-colors"
                            title="Ver usuario vinculado"
                          >
                            <Link2 className="h-4 w-4" />
                          </button>
                        ) : (
                          <span className="text-gray-300">-</span>
                        )}
                      </div>
                    </td>
                    <td className="py-3 px-4">
                      <div className="flex items-center justify-center gap-2">
                        <button
                          onClick={() => handleEdit(employee)}
                          className="p-2 text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                          title="Editar"
                        >
                          <Pencil className="h-4 w-4" />
                        </button>
                        <button
                          onClick={() => handleDelete(employee.id)}
                          className="p-2 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors"
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
      </div>

      {/* Linked User Modal */}
      {linkedUser && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-xl shadow-xl w-full max-w-sm">
            <div className="flex items-center justify-between p-4 border-b">
              <h2 className="text-lg font-semibold text-gray-900 flex items-center gap-2">
                <Link2 className="h-5 w-5 text-emerald-600" />
                Usuario Vinculado
              </h2>
              <button
                onClick={() => setLinkedUser(null)}
                className="text-gray-400 hover:text-gray-600 transition-colors"
              >
                <X className="h-5 w-5" />
              </button>
            </div>
            <div className="p-4 space-y-3">
              <div>
                <span className="text-sm text-gray-500">Nombre</span>
                <p className="font-medium text-gray-900">{linkedUser.name || '-'}</p>
              </div>
              <div>
                <span className="text-sm text-gray-500">Email</span>
                <p className="font-medium text-gray-900">{linkedUser.email || '-'}</p>
              </div>
              <div>
                <span className="text-sm text-gray-500">ID de Usuario</span>
                <p className="font-medium text-gray-900">{linkedUser.id}</p>
              </div>
            </div>
            <div className="p-4 border-t">
              <button
                onClick={() => setLinkedUser(null)}
                className="w-full px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors"
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
