import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { useAuthStore } from './store/authStore';
import { ThemeProvider } from './contexts/ThemeContext';
import Layout from './components/Layout';
import LoginPage from './pages/LoginPage';
import SignupPage from './pages/SignupPage';
import DashboardPage from './pages/DashboardPage';
import ProfilePage from './pages/ProfilePage';
import ExpensesPage from './pages/ExpensesPage';
import PayrollPage from './pages/PayrollPage';
import SettingsPage from './pages/SettingsPage';
import PositionsPage from './pages/PositionsPage';
import EmployeesPage from './pages/EmployeesPage';
import UsersPage from './pages/UsersPage';

function App() {
  const { isAuthenticated } = useAuthStore();

  return (
    <ThemeProvider>
    <Router>
      <Routes>
        {!isAuthenticated ? (
          <>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/signup" element={<SignupPage />} />
            <Route path="*" element={<Navigate to="/login" replace />} />
          </>
        ) : (
          <Route element={<Layout />}>
            <Route path="/dashboard" element={<DashboardPage />} />
            <Route path="/profile" element={<ProfilePage />} />
            <Route path="/expenses" element={<ExpensesPage />} />
            <Route path="/payroll" element={<PayrollPage />} />
            <Route path="/positions" element={<PositionsPage />} />
            <Route path="/employees" element={<EmployeesPage />} />
            <Route path="/users" element={<UsersPage />} />
            <Route path="/settings" element={<SettingsPage />} />
            <Route path="*" element={<Navigate to="/dashboard" replace />} />
          </Route>
        )}
      </Routes>
    </Router>
    </ThemeProvider>
  );
}

export default App;
