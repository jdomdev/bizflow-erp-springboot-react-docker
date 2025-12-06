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
  getAll: () => apiClient.get('/expense'),
  getById: (id) => apiClient.get(`/expense/${id}`),
  getByEmployee: (employeeId) => apiClient.get(`/expense/employee/${employeeId}`),
  getByStatus: (status) => apiClient.get(`/expense/status/${status}`),
  create: (data) => apiClient.post('/expense', data),
  update: (data) => apiClient.put('/expense', data),
  delete: (id) => apiClient.delete(`/expense/${id}`),
  approve: (id) => apiClient.put(`/expense/${id}/approve`),
  reject: (id, reason) => apiClient.put(`/expense/${id}/reject`, { reason }),
  uploadAttachment: (id, file) => {
    const formData = new FormData();
    formData.append('file', file);
    return apiClient.post(`/expense/${id}/attachment`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  },
  getAttachments: (id) => apiClient.get(`/expense/${id}/attachment`),
  deleteAttachment: (attachmentId) => apiClient.delete(`/expense/attachment/${attachmentId}`),
};

// Servicios de nómina
export const payrollService = {
  getAll: () => apiClient.get('/payroll'),
  getById: (id) => apiClient.get(`/payroll/${id}`),
};

// Servicios de empleados
export const employeeService = {
  getAll: () => apiClient.get('/employees'),
  getById: (id) => apiClient.get(`/employees/${id}`),
  update: (id, data) => apiClient.put(`/employees/${id}`, data),
};
