import { useState, useEffect } from 'react';
import Layout from '../components/Layout';
import Card from '../components/Card';
import Button from '../components/Button';
import { userService, roleService } from '../services/api';

const SUCCESS_MESSAGE_TIMEOUT = 3000;

export default function UserManagementPage() {
  const [users, setUsers] = useState([]);
  const [roles, setRoles] = useState([]);
  const [selectedUser, setSelectedUser] = useState(null);
  const [userRoles, setUserRoles] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    loadUsers();
    loadRoles();
  }, []);

  const loadUsers = async () => {
    try {
      setLoading(true);
      const response = await userService.getAll();
      setUsers(response.data);
    } catch (err) {
      setError('Error al cargar usuarios: ' + (err.response?.data?.error || err.message));
    } finally {
      setLoading(false);
    }
  };

  const loadRoles = async () => {
    try {
      const response = await roleService.getAll();
      setRoles(response.data);
    } catch (err) {
      setError('Error al cargar roles: ' + (err.response?.data?.error || err.message));
    }
  };

  const loadUserRoles = async (userId) => {
    try {
      setLoading(true);
      const response = await userService.getUserRoles(userId);
      setUserRoles(response.data);
    } catch (err) {
      setError('Error al cargar roles del usuario: ' + (err.response?.data?.error || err.message));
    } finally {
      setLoading(false);
    }
  };

  const handleSelectUser = async (user) => {
    setSelectedUser(user);
    setError('');
    setSuccess('');
    await loadUserRoles(user.id);
  };

  const handleAssignRole = async (roleId) => {
    if (!selectedUser) return;

    try {
      setLoading(true);
      await userService.assignRole(selectedUser.id, roleId);
      setSuccess('Rol asignado correctamente');
      await loadUserRoles(selectedUser.id);
      setTimeout(() => setSuccess(''), SUCCESS_MESSAGE_TIMEOUT);
    } catch (err) {
      setError('Error al asignar rol: ' + (err.response?.data?.error || err.message));
    } finally {
      setLoading(false);
    }
  };

  const handleRemoveRole = async (roleId) => {
    if (!selectedUser) return;

    try {
      setLoading(true);
      await userService.removeRole(selectedUser.id, roleId);
      setSuccess('Rol eliminado correctamente');
      await loadUserRoles(selectedUser.id);
      setTimeout(() => setSuccess(''), SUCCESS_MESSAGE_TIMEOUT);
    } catch (err) {
      setError('Error al eliminar rol: ' + (err.response?.data?.error || err.message));
    } finally {
      setLoading(false);
    }
  };

  const userHasRole = (roleId) => {
    return userRoles.some(r => r.id === roleId);
  };

  return (
    <Layout>
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-8">
          Gestión de Usuarios y Roles
        </h1>

        {error && (
          <div className="mb-4 p-4 bg-red-100 border border-red-400 text-red-700 rounded">
            {error}
          </div>
        )}

        {success && (
          <div className="mb-4 p-4 bg-green-100 border border-green-400 text-green-700 rounded">
            {success}
          </div>
        )}

        <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
          {/* Lista de Usuarios */}
          <Card title="Usuarios">
            {loading && !selectedUser ? (
              <p className="text-gray-600">Cargando usuarios...</p>
            ) : (
              <div className="space-y-2">
                {users.map(user => (
                  <div
                    key={user.id}
                    onClick={() => handleSelectUser(user)}
                    className={`p-3 rounded cursor-pointer transition-colors ${
                      selectedUser?.id === user.id
                        ? 'bg-blue-100 border-2 border-blue-500'
                        : 'bg-gray-50 hover:bg-gray-100 border-2 border-transparent'
                    }`}
                  >
                    <div className="font-semibold">{user.name} {user.surname}</div>
                    <div className="text-sm text-gray-600">{user.email}</div>
                  </div>
                ))}
              </div>
            )}
          </Card>

          {/* Gestión de Roles */}
          <Card title={selectedUser ? `Roles de ${selectedUser.name}` : 'Selecciona un usuario'}>
            {selectedUser ? (
              <div className="space-y-4">
                <div>
                  <h3 className="font-semibold mb-2 text-gray-700">Roles Actuales:</h3>
                  {loading && !userRoles.length ? (
                    <p className="text-gray-600">Cargando roles...</p>
                  ) : userRoles.length > 0 ? (
                    <div className="space-y-2">
                      {userRoles.map(role => (
                        <div
                          key={role.id}
                          className="flex items-center justify-between p-2 bg-green-50 rounded"
                        >
                          <span className="font-medium">{role.name}</span>
                          <Button
                            variant="danger"
                            onClick={() => handleRemoveRole(role.id)}
                            disabled={loading}
                          >
                            Eliminar
                          </Button>
                        </div>
                      ))}
                    </div>
                  ) : (
                    <p className="text-gray-500 italic">Sin roles asignados</p>
                  )}
                </div>

                <div>
                  <h3 className="font-semibold mb-2 text-gray-700">Roles Disponibles:</h3>
                  <div className="space-y-2">
                    {roles
                      .filter(role => !userHasRole(role.id))
                      .map(role => (
                        <div
                          key={role.id}
                          className="flex items-center justify-between p-2 bg-gray-50 rounded"
                        >
                          <span className="font-medium">{role.name}</span>
                          <Button
                            variant="primary"
                            onClick={() => handleAssignRole(role.id)}
                            disabled={loading}
                          >
                            Asignar
                          </Button>
                        </div>
                      ))}
                  </div>
                </div>
              </div>
            ) : (
              <p className="text-gray-500 text-center py-8">
                Selecciona un usuario para gestionar sus roles
              </p>
            )}
          </Card>
        </div>
      </div>
    </Layout>
  );
}
