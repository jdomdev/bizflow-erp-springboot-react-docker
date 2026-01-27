import axios from 'axios';
import { useAuthStore } from '../store/authStore';

// En desarrollo con Vite, usar URL relativa para que funcione el proxy
// En producción (Docker), usar la URL completa de la variable de entorno
const API_BASE_URL = import.meta.env.VITE_API_URL || '/api/v1';

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor para agregar token a cada petición
apiClient.interceptors.request.use(
  (config) => {
    const { token } = useAuthStore.getState();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Interceptor para manejar errores
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      useAuthStore.getState().logout();
    }
    return Promise.reject(error);
  }
);

// Servicios de autenticación
export const authService = {
  login: (email, password) =>
    apiClient.post('/auth/login', { email, password }),
  signup: (userData) =>
    apiClient.post('/auth/signup', userData),
  checkEmail: (email) =>
    apiClient.get('/auth/check-email', { params: { email } }),
};

// Servicios de gastos
export const expenseService = {
  getAll: () => apiClient.get('/expense'),
  getByUserId: (userId) => apiClient.get(`/expense/user/${userId}`),
  getById: (id) => apiClient.get(`/expense/${id}`),
  create: (data) => apiClient.post('/expense/', data),
  update: (id, data) => apiClient.put('/expense/', data),
  delete: (id) => apiClient.delete(`/expense/${id}`),
};

// Servicios de nómina
export const payrollService = {
  getAll: () => apiClient.get('/payroll'),
  getMy: () => apiClient.get('/payroll/my'),
  getById: (id) => apiClient.get(`/payroll/${id}`),
  getByUserId: (userId) => apiClient.get(`/payroll/user/${userId}`),
  getByEmployeeId: (employeeId) => apiClient.get(`/payroll/employee/${employeeId}`),
};

// Servicios de empleados
export const employeeService = {
  getAll: () => apiClient.get('/employee'),
  getById: (id) => apiClient.get(`/employee/${id}`),
  getByName: (name, surname) => apiClient.get(`/employee/${name}/${surname}`),
  create: (data) => apiClient.post('/employee/', data),
  update: (id, data) => apiClient.put(`/employee/${id}`, data),
  delete: (id) => apiClient.delete(`/employee/${id}`),
};

// Servicios de usuario
export const userService = {
  getAll: () => apiClient.get('/user'),
  getById: (id) => apiClient.get(`/user/${id}`),
  getProfile: () => apiClient.get('/user/profile'),
  create: (data) => apiClient.post('/user/', data),
  update: (id, data) => apiClient.put(`/user/${id}`, data),
  updateProfile: (data) => apiClient.put('/user/profile', data),
  delete: (id) => apiClient.delete(`/user/${id}`),
  logout: () => apiClient.post('/user/logout'),
};

// Servicios de posiciones
export const positionService = {
  getAll: () => apiClient.get('/position'),
  getById: (id) => apiClient.get(`/position/${id}`),
  create: (data) => apiClient.post('/position/', data),
  update: (id, data) => apiClient.put(`/position/${id}`, data),
  delete: (id) => apiClient.delete(`/position/${id}`),
};

// Servicios de roles
export const roleService = {
  getAll: () => apiClient.get('/role'),
  getById: (id) => apiClient.get(`/role/${id}`),
  create: (data) => apiClient.post('/role/', data),
  update: (id, data) => apiClient.put(`/role/${id}`, data),
  delete: (id) => apiClient.delete(`/role/${id}`),
};

// Servicios de nómina - CRUD completo
export const payrollAdminService = {
  create: (data) => apiClient.post('/payroll/', data),
  update: (id, data) => apiClient.put(`/payroll/${id}`, data),
  delete: (id) => apiClient.delete(`/payroll/${id}`),
};

// Servicios de notificaciones
export const notificationService = {
  getAll: () => apiClient.get('/notifications'),
  getUnread: () => apiClient.get('/notifications/unread'),
  getUnreadCount: () => apiClient.get('/notifications/unread/count'),
  markAsRead: (id) => apiClient.put(`/notifications/${id}/read`),
  markAllAsRead: () => apiClient.put('/notifications/read-all'),
  delete: (id) => apiClient.delete(`/notifications/${id}`),
};

// WebSocket configuration
export const getWebSocketUrl = () => {
  const baseUrl = API_BASE_URL.replace('/api/v1', '');
  const wsProtocol = baseUrl.startsWith('https') ? 'wss' : 'ws';
  return baseUrl.replace(/^https?/, wsProtocol) + '/ws';
};
