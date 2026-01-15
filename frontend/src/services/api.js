import axios from 'axios';
import { useAuthStore } from '../store/authStore';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1';

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
};

// Servicios de gastos
export const expenseService = {
  getAll: () => apiClient.get('/expense/'),
  getById: (id) => apiClient.get(`/expense/${id}`),
  create: (data) => apiClient.post('/expense/', data),
  update: (id, data) => apiClient.put(`/expense/`, data),
  delete: (id) => apiClient.delete(`/expense/${id}`),
};

// Servicios de nómina
export const payrollService = {
  getAll: () => apiClient.get('/payroll/'),
  getMy: () => apiClient.get('/payroll/my'),
  getById: (id) => apiClient.get(`/payroll/${id}`),
  getByUserId: (userId) => apiClient.get(`/payroll/user/${userId}`),
  getByEmployeeId: (employeeId) => apiClient.get(`/payroll/employee/${employeeId}`),
};

// Servicios de empleados
export const employeeService = {
  getAll: () => apiClient.get('/employee/'),
  getById: (id) => apiClient.get(`/employee/${id}`),
  update: (id, data) => apiClient.put(`/employee/${id}`, data),
};

// Servicios de usuario
export const userService = {
  getProfile: () => apiClient.get('/user/profile'),
  updateProfile: (data) => apiClient.put('/user/profile', data),
  logout: () => apiClient.post('/user/logout'),
};
