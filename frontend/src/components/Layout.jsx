import React from 'react';
import { Outlet, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import {
  BarChart3,
  CreditCard,
  LogOut,
  Menu,
  Settings,
  X,
} from 'lucide-react';
import { useAuthStore } from '../store/authStore';
import Button from './Button';
import { useState } from 'react';

function Layout() {
  const navigate = useNavigate();
  const { logout, user } = useAuthStore();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const menuItems = [
    { icon: BarChart3, label: 'Dashboard', path: '/dashboard' },
    { icon: CreditCard, label: 'Gastos', path: '/expenses' },
    { icon: BarChart3, label: 'Nómina', path: '/payroll' },
    { icon: Settings, label: 'Configuración', path: '/settings' },
  ];

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900">
      {/* Sidebar */}
      <motion.aside
        initial={{ x: -300 }}
        animate={{ x: sidebarOpen ? 0 : -300 }}
        transition={{ type: 'spring', stiffness: 300, damping: 30 }}
        className="fixed left-0 top-0 z-40 h-screen w-64 bg-gradient-to-b from-slate-950 to-slate-900 shadow-2xl shadow-blue-500/20 lg:static lg:translate-x-0"
      >
        {/* Logo */}
        <div className="flex items-center justify-between border-b border-slate-700 p-6">
          <div className="flex items-center gap-3">
            <div className="gradient-primary rounded-lg p-2">
              <CreditCard className="h-6 w-6 text-white" />
            </div>
            <h1 className="gradient-text text-xl font-bold">ExpenseNote</h1>
          </div>
          <button
            onClick={() => setSidebarOpen(false)}
            className="lg:hidden text-slate-400 hover:text-white"
          >
            <X className="h-6 w-6" />
          </button>
        </div>

        {/* User Info */}
        <div className="border-b border-slate-700 p-6">
          <p className="text-sm text-slate-400">Bienvenido</p>
          <p className="mt-1 font-semibold text-white truncate">{user?.email}</p>
        </div>

        {/* Navigation */}
        <nav className="space-y-2 p-6">
          {menuItems.map((item, index) => {
            const Icon = item.icon;
            return (
              <motion.button
                key={item.path}
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ delay: index * 0.1 }}
                onClick={() => {
                  navigate(item.path);
                  setSidebarOpen(false);
                }}
                className="flex w-full items-center gap-3 rounded-lg px-4 py-3 text-slate-300 transition-all hover:bg-gradient-to-r hover:from-blue-500/20 hover:to-indigo-500/20 hover:text-white"
              >
                <Icon className="h-5 w-5" />
                <span>{item.label}</span>
              </motion.button>
            );
          })}
        </nav>

        {/* Logout Button */}
        <div className="absolute bottom-0 left-0 right-0 border-t border-slate-700 p-6">
          <Button
            onClick={handleLogout}
            variant="danger"
            className="w-full justify-center"
          >
            <LogOut className="h-4 w-4" />
            Cerrar sesión
          </Button>
        </div>
      </motion.aside>

      {/* Overlay */}
      {sidebarOpen && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          onClick={() => setSidebarOpen(false)}
          className="fixed inset-0 z-30 bg-black/50 lg:hidden"
        />
      )}

      {/* Main Content */}
      <div className="flex flex-col lg:flex-row">
        <div className="flex-1">
          {/* Top Bar */}
          <header className="sticky top-0 z-20 border-b border-slate-700 bg-slate-950/50 backdrop-blur-md">
            <div className="flex items-center justify-between px-6 py-4">
              <button
                onClick={() => setSidebarOpen(true)}
                className="lg:hidden text-slate-400 hover:text-white"
              >
                <Menu className="h-6 w-6" />
              </button>
              <h2 className="text-xl font-semibold text-white">Expense Note App</h2>
              <div className="h-8 w-8 rounded-full bg-gradient-to-r from-blue-500 to-purple-600" />
            </div>
          </header>

          {/* Page Content */}
          <main className="flex-1 p-6 lg:p-8">
            <Outlet />
          </main>
        </div>
      </div>
    </div>
  );
}

export default Layout;
