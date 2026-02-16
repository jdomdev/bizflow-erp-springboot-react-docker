import { useState, useEffect } from 'react';
import { userService, roleService, employeeService } from '../services/api';
import { useAuthStore } from '../store/authStore';
import { Plus, Pencil, Trash2, UserCog, Search, X, Save, Mail, Lock, Eye, EyeOff, Link2 } from 'lucide-react';

export default function UsersPage() {
  const { user: currentUser } = useAuthStore();
  const [users, setUsers] = useState([]);
  const [roles, setRoles] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingUser, setEditingUser] = useState(null);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    surname: '',
    email: '',
    password: '',
    roleId: '',
    employeeId: '',
  });
  const [formErrors, setFormErrors] = useState({});
  const [saving, setSaving] = useState(false);
  const [linkedEmployee, setLinkedEmployee] = useState(null);

  const isAdmin = currentUser?.roleId === 1;

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [usersRes, rolesRes, employeesRes] = await Promise.all([
        userService.getAll(),
        roleService.getAll(),
        employeeService.getAll(),
      ]);
      setUsers(usersRes.data);
      setRoles(rolesRes.data);
      setEmployees(employeesRes.data);
    } catch (error) {
      console.error('Error loading data:', error);
      setError('No se pudieron cargar los usuarios.');
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
    if (!formData.email?.trim()) {
      errors.email = 'El email es requerido';
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      errors.email = 'Email no válido';
    }
    if (!editingUser && !formData.password?.trim()) {
      errors.password = 'La contraseña es requerida para nuevos usuarios';
    } else if (formData.password && formData.password.length < 6) {
      errors.password = 'La contraseña debe tener al menos 6 caracteres';
    }
    if (!formData.roleId) {
      errors.roleId = 'El rol es requerido';
    }
    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;

    setSaving(true);
    try {
      const selectedRoleId = formData.roleId ? parseInt(formData.roleId) : null;
      
      const payload = {
        name: formData.name.trim(),
        surname: formData.surname.trim(),
        email: formData.email.trim(),
        roleIds: selectedRoleId ? [selectedRoleId] : [],
      };

      // Only include password if provided
      if (formData.password?.trim()) {
        payload.password = formData.password;
      }

      // Include employeeId if selected
      if (formData.employeeId) {
        payload.employeeId = parseInt(formData.employeeId);
      }

      if (editingUser) {
        const response = await userService.update(editingUser.id, payload);
        setUsers(users.map(u => u.id === editingUser.id ? response.data : u));
      } else {
        const response = await userService.create(payload);
        setUsers([response.data, ...users]);
      }
      handleCloseForm();
    } catch (error) {
      console.error('Error saving user:', error);
      setError(error.response?.data?.message || 'Error al guardar el usuario');
    } finally {
      setSaving(false);
    }
  };

  const handleEdit = (user) => {
    setEditingUser(user);
    
    // Get the first role ID if exists
    const userRoleId = user.roleDtos?.[0]?.id || '';
    
    setFormData({
      name: user.name || '',
      surname: user.surname || '',
      email: user.email || '',
      password: '', // Don't show existing password
      roleId: userRoleId.toString(),
      employeeId: user.employeeId?.toString() || '',
    });
    setFormErrors({});
    setShowPassword(false);
    setShowForm(true);
  };

  const handleCloseForm = () => {
    setShowForm(false);
    setEditingUser(null);
    setFormData({ name: '', surname: '', email: '', password: '', roleId: '', employeeId: '' });
    setFormErrors({});
    setShowPassword(false);
  };

  const handleDelete = async (id) => {
    if (id === currentUser?.id) {
      alert('No puedes eliminar tu propio usuario.');
      return;
    }
    if (!window.confirm('¿Estás seguro de que deseas eliminar este usuario?')) {
      return;
    }
    try {
      await userService.delete(id);
      setUsers(users.filter(u => u.id !== id));
    } catch (error) {
      console.error('Error deleting user:', error);
      alert(error.response?.data?.message || 'Error al eliminar el usuario.');
    }
  };

  const filteredUsers = users.filter(user => {
    const fullName = `${user.name} ${user.surname}`.toLowerCase();
    const term = searchTerm.toLowerCase();
    return fullName.includes(term) || user.email?.toLowerCase().includes(term);
  });

  const getRoleName = (user) => {
    return user.roleDtos?.[0]?.name || 'Sin rol';
  };

  const getRoleBadgeClass = (roleName) => {
    switch (roleName?.toLowerCase()) {
      case 'admin':
        return 'bg-purple-100 dark:bg-purple-900/30 text-purple-800 dark:text-purple-400';
      case 'user':
        return 'bg-green-100 dark:bg-green-900/30 text-green-800 dark:text-green-400';
      default:
        return 'bg-gray-100 dark:bg-slate-700 text-gray-800 dark:text-slate-300';
    }
  };

  const handleViewLinkedEmployee = (user) => {
    const employee = employees.find(e => e.id === user.employeeId);
    if (employee) {
      setLinkedEmployee({ user, employee });
    }
  };

  if (!isAdmin) {
    return (
      <div className="flex flex-col items-center justify-center h-64 text-gray-500 dark:text-slate-400">
        <UserCog className="h-16 w-16 mb-4 opacity-50" />
        <p className="text-lg">No tienes permisos para acceder a esta sección.</p>
      </div>
    );
  }

  if (loading) {
    return (
      <div className="flex flex-col justify-center items-center h-64 gap-3">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        <span className="text-slate-500 dark:text-slate-400">Cargando usuarios...</span>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Gestión de Usuarios</h1>
          <p className="text-sm text-gray-500 dark:text-slate-400 mt-1">
            {users.length} usuario{users.length !== 1 ? 's' : ''} registrado{users.length !== 1 ? 's' : ''}
          </p>
        </div>

        <button
          onClick={() => setShowForm(true)}
          className="flex items-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors shadow-sm"
        >
          <Plus className="mr-2 h-5 w-5" />
          Nuevo Usuario
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
                {editingUser ? 'Editar Usuario' : 'Nuevo Usuario'}
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
                  Email *
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
                    placeholder="usuario@empresa.com"
                  />
                </div>
                {formErrors.email && (
                  <p className="mt-1 text-sm text-red-600 dark:text-red-400">{formErrors.email}</p>
                )}
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-slate-300 mb-1">
                  Contraseña {editingUser ? '(dejar vacío para mantener)' : '*'}
                </label>
                <div className="relative">
                  <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400 dark:text-slate-500" />
                  <input
                    type={showPassword ? 'text' : 'password'}
                    value={formData.password}
                    onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                    className={`w-full pl-10 pr-10 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white dark:bg-slate-700 text-gray-900 dark:text-white ${
                      formErrors.password ? 'border-red-300 bg-red-50 dark:bg-red-900/20' : 'border-gray-200 dark:border-slate-600'
                    }`}
                    placeholder="••••••••"
                  />
                  <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 dark:text-slate-500 hover:text-gray-600 dark:hover:text-slate-300"
                  >
                    {showPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                  </button>
                </div>
                {formErrors.password && (
                  <p className="mt-1 text-sm text-red-600 dark:text-red-400">{formErrors.password}</p>
                )}
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-slate-300 mb-1">
                  Rol *
                </label>
                <select
                  value={formData.roleId}
                  onChange={(e) => setFormData({ ...formData, roleId: e.target.value })}
                  className={`w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white dark:bg-slate-700 text-gray-900 dark:text-white ${
                    formErrors.roleId ? 'border-red-300 bg-red-50 dark:bg-red-900/20' : 'border-gray-200 dark:border-slate-600'
                  }`}
                >
                  <option value="">Seleccionar rol...</option>
                  {roles.map((role) => (
                    <option key={role.id} value={role.id}>
                      {role.name}
                    </option>
                  ))}
                </select>
                {formErrors.roleId && (
                  <p className="mt-1 text-sm text-red-600 dark:text-red-400">{formErrors.roleId}</p>
                )}
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-slate-300 mb-1">
                  Empleado asociado (opcional)
                </label>
                <select
                  value={formData.employeeId}
                  onChange={(e) => setFormData({ ...formData, employeeId: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-200 dark:border-slate-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white dark:bg-slate-700 text-gray-900 dark:text-white"
                >
                  <option value="">Sin asociar</option>
                  {employees.map((employee) => (
                    <option key={employee.id} value={employee.id}>
                      {employee.name} {employee.surname}
                    </option>
                  ))}
                </select>
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
                      {editingUser ? 'Guardar Cambios' : 'Crear Usuario'}
                    </>
                  )}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Empty State */}
      {filteredUsers.length === 0 && (
        <div className="bg-white dark:bg-slate-800 rounded-xl shadow-sm border border-gray-100 dark:border-slate-700 p-12 text-center">
          <UserCog className="h-12 w-12 mx-auto mb-3 text-gray-400 dark:text-slate-500 opacity-50" />
          <p className="text-gray-500 dark:text-slate-400">
            {searchTerm ? 'No se encontraron resultados' : 'No hay usuarios registrados'}
          </p>
        </div>
      )}

      {/* Mobile Cards (visible < md) */}
      {filteredUsers.length > 0 && (
        <div className="md:hidden space-y-3">
          {filteredUsers.map((user) => {
            const roleName = getRoleName(user);
            const isCurrentUser = user.id === currentUser?.id;
            
            return (
              <div
                key={user.id}
                className={`bg-white dark:bg-slate-800 rounded-xl shadow-sm border border-gray-100 dark:border-slate-700 p-4 ${
                  isCurrentUser ? 'ring-2 ring-blue-500/30' : ''
                }`}
              >
                {/* Header con avatar y nombre */}
                <div className="flex items-center gap-3 mb-3">
                  <div className="w-12 h-12 rounded-full bg-gradient-to-br from-indigo-500 to-purple-600 flex items-center justify-center text-white font-semibold">
                    {user.name?.charAt(0)}{user.surname?.charAt(0)}
                  </div>
                  <div className="flex-1 min-w-0">
                    <h3 className="font-semibold text-gray-900 dark:text-white truncate">
                      {user.name} {user.surname}
                      {isCurrentUser && (
                        <span className="ml-2 text-xs text-blue-600 dark:text-blue-400">(Tú)</span>
                      )}
                    </h3>
                    <span className={`inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium ${getRoleBadgeClass(roleName)}`}>
                      {roleName}
                    </span>
                  </div>
                  <span className="text-xs text-gray-400 dark:text-slate-500 font-mono">#{user.id}</span>
                </div>

                {/* Info */}
                <div className="space-y-2 text-sm mb-3">
                  <div className="flex items-center gap-2 text-gray-600 dark:text-slate-300">
                    <Mail className="h-4 w-4 text-gray-400 dark:text-slate-500 flex-shrink-0" />
                    <span className="truncate">{user.email}</span>
                  </div>
                  {user.employeeId && (
                    <div className="flex items-center gap-2 text-emerald-600 dark:text-emerald-400">
                      <Link2 className="h-4 w-4 flex-shrink-0" />
                      <button
                        onClick={() => handleViewLinkedEmployee(user)}
                        className="text-sm hover:underline"
                      >
                        Empleado vinculado
                      </button>
                    </div>
                  )}
                </div>

                {/* Actions */}
                <div className="flex gap-2 pt-3 border-t border-gray-100 dark:border-slate-700">
                  <button
                    onClick={() => handleEdit(user)}
                    className="flex-1 flex items-center justify-center gap-2 px-3 py-2 text-sm text-blue-600 dark:text-blue-400 bg-blue-50 dark:bg-blue-900/20 rounded-lg hover:bg-blue-100 dark:hover:bg-blue-900/30 transition-colors"
                  >
                    <Pencil className="h-4 w-4" />
                    Editar
                  </button>
                  <button
                    onClick={() => handleDelete(user.id)}
                    disabled={isCurrentUser}
                    className={`flex-1 flex items-center justify-center gap-2 px-3 py-2 text-sm rounded-lg transition-colors ${
                      isCurrentUser
                        ? 'text-gray-400 dark:text-slate-600 bg-gray-100 dark:bg-slate-700 cursor-not-allowed'
                        : 'text-red-600 dark:text-red-400 bg-red-50 dark:bg-red-900/20 hover:bg-red-100 dark:hover:bg-red-900/30'
                    }`}
                  >
                    <Trash2 className="h-4 w-4" />
                    Eliminar
                  </button>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Desktop Table (visible >= md) */}
      {filteredUsers.length > 0 && (
        <div className="hidden md:block bg-white dark:bg-slate-800 rounded-xl shadow-sm border border-gray-100 dark:border-slate-700 overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="bg-gray-50 dark:bg-slate-700 border-b border-gray-100 dark:border-slate-600">
                  <th className="text-left py-3 px-4 text-sm font-semibold text-gray-400 dark:text-slate-500 w-16">ID</th>
                  <th className="text-left py-3 px-4 text-sm font-semibold text-gray-600 dark:text-slate-300">Usuario</th>
                  <th className="text-left py-3 px-4 text-sm font-semibold text-gray-600 dark:text-slate-300">Email</th>
                  <th className="text-left py-3 px-4 text-sm font-semibold text-gray-600 dark:text-slate-300">Rol</th>
                  <th className="text-center py-3 px-4 text-sm font-semibold text-gray-600 dark:text-slate-300">Empleado</th>
                  <th className="text-center py-3 px-4 text-sm font-semibold text-gray-600 dark:text-slate-300">Acciones</th>
                </tr>
              </thead>
              <tbody>
                {filteredUsers.map((user) => {
                  const roleName = getRoleName(user);
                  const isCurrentUser = user.id === currentUser?.id;
                  
                  return (
                    <tr
                      key={user.id}
                      className={`border-b border-gray-50 dark:border-slate-700 hover:bg-gray-50 dark:hover:bg-slate-700 transition-colors ${
                        isCurrentUser ? 'bg-blue-50/50 dark:bg-blue-900/20' : ''
                      }`}
                    >
                      <td className="py-3 px-4 text-sm text-gray-400 dark:text-slate-500 font-mono">
                        {user.id}
                      </td>
                      <td className="py-3 px-4">
                        <div className="flex items-center gap-3">
                          <div className="w-10 h-10 rounded-full bg-gradient-to-br from-indigo-500 to-purple-600 flex items-center justify-center text-white font-semibold text-sm">
                            {user.name?.charAt(0)}{user.surname?.charAt(0)}
                          </div>
                          <div>
                            <span className="font-medium text-gray-900 dark:text-white">
                              {user.name} {user.surname}
                            </span>
                            {isCurrentUser && (
                              <span className="ml-2 text-xs text-blue-600 dark:text-blue-400">(Tú)</span>
                            )}
                          </div>
                        </div>
                      </td>
                      <td className="py-3 px-4 text-gray-600 dark:text-slate-300">
                        {user.email}
                      </td>
                      <td className="py-3 px-4">
                        <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getRoleBadgeClass(roleName)}`}>
                          {roleName}
                        </span>
                      </td>
                      <td className="py-3 px-4 text-center">
                        {user.employeeId ? (
                          <button
                            onClick={() => handleViewLinkedEmployee(user)}
                            className="p-2 text-emerald-500 dark:text-emerald-400 hover:text-emerald-700 hover:bg-emerald-50 dark:hover:bg-emerald-900/30 rounded-lg transition-colors"
                            title="Ver empleado vinculado"
                          >
                            <Link2 className="h-4 w-4" />
                          </button>
                        ) : (
                          <span className="text-gray-300 dark:text-slate-600">—</span>
                        )}
                      </td>
                      <td className="py-3 px-4">
                        <div className="flex items-center justify-center gap-2">
                          <button
                            onClick={() => handleEdit(user)}
                            className="p-2 text-gray-400 dark:text-slate-500 hover:text-blue-600 dark:hover:text-blue-400 hover:bg-blue-50 dark:hover:bg-blue-900/30 rounded-lg transition-colors"
                            title="Editar"
                          >
                            <Pencil className="h-4 w-4" />
                          </button>
                          <button
                            onClick={() => handleDelete(user.id)}
                            disabled={isCurrentUser}
                            className={`p-2 rounded-lg transition-colors ${
                              isCurrentUser
                                ? 'text-gray-300 dark:text-slate-600 cursor-not-allowed'
                                : 'text-gray-400 dark:text-slate-500 hover:text-red-600 dark:hover:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/30'
                            }`}
                            title={isCurrentUser ? 'No puedes eliminar tu propio usuario' : 'Eliminar'}
                          >
                            <Trash2 className="h-4 w-4" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Modal Empleado Vinculado */}
      {linkedEmployee && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white dark:bg-slate-800 rounded-xl shadow-xl w-full max-w-md">
            <div className="flex items-center justify-between p-4 border-b dark:border-slate-700">
              <div className="flex items-center gap-2">
                <Link2 className="h-5 w-5 text-emerald-500 dark:text-emerald-400" />
                <h2 className="text-lg font-semibold text-gray-900 dark:text-white">Empleado Vinculado</h2>
              </div>
              <button
                onClick={() => setLinkedEmployee(null)}
                className="text-gray-400 dark:text-slate-500 hover:text-gray-600 dark:hover:text-slate-300 transition-colors"
              >
                <X className="h-5 w-5" />
              </button>
            </div>
            <div className="p-4 space-y-4">
              <div className="p-4 rounded-lg bg-emerald-50 dark:bg-emerald-900/20 border border-emerald-200 dark:border-emerald-800">
                <p className="text-xs text-emerald-600 dark:text-emerald-400 mb-1">Empleado</p>
                <p className="font-semibold text-emerald-800 dark:text-emerald-300">
                  {linkedEmployee.employee.name} {linkedEmployee.employee.surname}
                </p>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div className="p-3 rounded-lg bg-gray-50 dark:bg-slate-700">
                  <p className="text-xs text-gray-500 dark:text-slate-400 mb-1">Email</p>
                  <p className="text-sm text-gray-700 dark:text-slate-300">{linkedEmployee.employee.email}</p>
                </div>
                <div className="p-3 rounded-lg bg-gray-50 dark:bg-slate-700">
                  <p className="text-xs text-gray-500 dark:text-slate-400 mb-1">ID Empleado</p>
                  <p className="text-sm text-gray-700 dark:text-slate-300">{linkedEmployee.employee.id}</p>
                </div>
              </div>
              <div className="pt-2 border-t dark:border-slate-700">
                <p className="text-xs text-gray-400 dark:text-slate-500">
                  Usuario: {linkedEmployee.user.name} {linkedEmployee.user.surname} ({linkedEmployee.user.email})
                </p>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
