import React from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import {
  BarChart3,
  Briefcase,
  CreditCard,
  LogOut,
  Menu,
  Settings,
  User,
  UserCog,
  Users,
  X,
  Home,
  Wallet,
  Search,
  ChevronRight,
} from 'lucide-react';
import { useAuthStore } from '../store/authStore';
import Button from './Button';
import NotificationBell from './NotificationBell';
import { useState, useRef, useEffect } from 'react';

function Layout() {
  const navigate = useNavigate();
  const location = useLocation();
  const { logout, user } = useAuthStore();
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [userDropdownOpen, setUserDropdownOpen] = useState(false);
  
  // Command Palette state
  const [searchQuery, setSearchQuery] = useState('');
  const [showSuggestions, setShowSuggestions] = useState(false);
  
  const userDropdownRef = useRef(null);
  const searchRef = useRef(null);

  // Close dropdowns when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (userDropdownRef.current && !userDropdownRef.current.contains(event.target)) {
        setUserDropdownOpen(false);
      }
      if (searchRef.current && !searchRef.current.contains(event.target)) {
        setShowSuggestions(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const isAdmin = user?.roleId === 1;

  // Command Palette: search commands configuration
  const searchCommands = [
    { label: 'Dashboard', path: '/dashboard', icon: Home, keywords: ['inicio', 'home', 'panel'] },
    { label: 'Gastos', path: '/expenses', icon: CreditCard, keywords: ['expenses', 'gasto'], searchable: true },
    { label: 'Nóminas', path: '/payroll', icon: Wallet, keywords: ['payroll', 'nomina', 'salario'], searchable: true },
    ...(isAdmin ? [
      { label: 'Cargos', path: '/positions', icon: Briefcase, keywords: ['positions', 'cargo', 'puesto'], searchable: true },
      { label: 'Empleados', path: '/employees', icon: Users, keywords: ['employees', 'empleado'], searchable: true },
      { label: 'Usuarios', path: '/users', icon: UserCog, keywords: ['users', 'usuario'], searchable: true },
    ] : []),
    { label: 'Perfil', path: '/profile', icon: User, keywords: ['profile', 'mi perfil'] },
    { label: 'Configuración', path: '/settings', icon: Settings, keywords: ['settings', 'config'] },
  ];

  // Parse search query to extract command and search term
  const parseSearchQuery = (query) => {
    const trimmed = query.trim().toLowerCase();
    
    // Check if it's a search command like "Gastos Juan" or "Nominas Garcia"
    for (const cmd of searchCommands) {
      const cmdLabel = cmd.label.toLowerCase();
      if (trimmed.startsWith(cmdLabel + ' ') && cmd.searchable) {
        const searchTerm = query.slice(cmdLabel.length + 1).trim();
        return { type: 'search', command: cmd, searchTerm };
      }
      // Also check keywords
      for (const kw of cmd.keywords) {
        if (trimmed.startsWith(kw + ' ') && cmd.searchable) {
          const searchTerm = query.slice(kw.length + 1).trim();
          return { type: 'search', command: cmd, searchTerm };
        }
      }
    }
    
    // Otherwise, filter commands by query
    return { type: 'navigate', query: trimmed };
  };

  // Get filtered suggestions based on query
  const getSuggestions = () => {
    if (!searchQuery.trim()) return searchCommands;
    
    const parsed = parseSearchQuery(searchQuery);
    
    if (parsed.type === 'search' && parsed.searchTerm) {
      // Show the search action
      return [{
        label: `Buscar "${parsed.searchTerm}" en ${parsed.command.label}`,
        path: `${parsed.command.path}?search=${encodeURIComponent(parsed.searchTerm)}`,
        icon: Search,
        isSearch: true
      }];
    }
    
    // Filter commands
    return searchCommands.filter(cmd => {
      const q = parsed.query;
      return (
        cmd.label.toLowerCase().includes(q) ||
        cmd.keywords.some(kw => kw.includes(q))
      );
    });
  };

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    const suggestions = getSuggestions();
    if (suggestions.length > 0) {
      navigate(suggestions[0].path);
      setSearchQuery('');
      setShowSuggestions(false);
    }
  };

  const handleSuggestionClick = (suggestion) => {
    navigate(suggestion.path);
    setSearchQuery('');
    setShowSuggestions(false);
  };

  const menuItems = [
    { icon: Home, label: 'Dashboard', path: '/dashboard' },
    { icon: CreditCard, label: 'Gastos', path: '/expenses' },
    { icon: Wallet, label: 'Nómina', path: '/payroll' },
    ...(isAdmin ? [
      { icon: Briefcase, label: 'Cargos', path: '/positions' },
      { icon: Users, label: 'Empleados', path: '/employees' },
      { icon: UserCog, label: 'Usuarios', path: '/users' },
    ] : []),
    { icon: User, label: 'Perfil', path: '/profile' },
    { icon: Settings, label: 'Configuración', path: '/settings' },
  ];

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const isActive = (path) => location.pathname === path;

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-white to-blue-50">
      {/* Desktop Sidebar */}
      <aside className="hidden lg:flex lg:fixed left-0 top-0 z-40 h-screen w-72 flex-col bg-white border-r border-slate-200/80 shadow-soft">
        {/* Logo */}
        <div className="flex items-center gap-3 p-6 border-b border-slate-100">
          <div className="gradient-primary rounded-xl p-2.5 shadow-lg shadow-blue-500/25">
            <BarChart3 className="h-6 w-6 text-white" />
          </div>
          <div>
            <h1 className="text-xl font-bold text-slate-800">Bizflow ERP</h1>
            <p className="text-xs text-slate-500">Gestión empresarial</p>
          </div>
        </div>

        {/* User Info Card */}
        <div className="mx-4 mt-6 p-4 rounded-xl bg-gradient-to-br from-blue-50 to-indigo-50 border border-blue-100">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-full bg-gradient-to-r from-blue-500 to-indigo-600 flex items-center justify-center text-white font-semibold text-sm shadow-lg shadow-blue-500/25">
              {user?.email?.charAt(0).toUpperCase() || 'U'}
            </div>
            <div className="flex-1 min-w-0">
              <p className="text-sm font-semibold text-slate-800 truncate">{user?.email || 'Usuario'}</p>
              <p className="text-xs text-slate-500">Administrador</p>
            </div>
          </div>
        </div>

        {/* Navigation */}
        <nav className="flex-1 mt-6 px-4 space-y-1">
          <p className="px-3 mb-3 text-xs font-semibold text-slate-400 uppercase tracking-wider">Menú principal</p>
          {menuItems.map((item, index) => {
            const Icon = item.icon;
            const active = isActive(item.path);
            return (
              <motion.button
                key={item.path}
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ delay: index * 0.05 }}
                onClick={() => navigate(item.path)}
                className={`
                  flex w-full items-center gap-3 rounded-xl px-4 py-3 text-sm font-medium transition-all duration-200
                  ${active 
                    ? 'bg-gradient-to-r from-blue-500 to-indigo-600 text-white shadow-lg shadow-blue-500/25' 
                    : 'text-slate-600 hover:bg-slate-100 hover:text-slate-800'
                  }
                `}
              >
                <Icon className="h-5 w-5" />
                <span className="flex-1 text-left">{item.label}</span>
                {active && <ChevronRight className="h-4 w-4" />}
              </motion.button>
            );
          })}
        </nav>

        {/* Logout Button */}
        <div className="p-4 border-t border-slate-100">
          <Button
            onClick={handleLogout}
            variant="ghost"
            className="w-full justify-start text-slate-600 hover:text-rose-600 hover:bg-rose-50"
          >
            <LogOut className="h-4 w-4" />
            Cerrar sesión
          </Button>
        </div>
      </aside>

      {/* Mobile Sidebar */}
      <AnimatePresence>
        {sidebarOpen && (
          <>
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              onClick={() => setSidebarOpen(false)}
              className="fixed inset-0 z-40 bg-slate-900/20 backdrop-blur-sm lg:hidden"
            />
            <motion.aside
              initial={{ x: '-100%' }}
              animate={{ x: 0 }}
              exit={{ x: '-100%' }}
              transition={{ type: 'spring', stiffness: 300, damping: 30 }}
              className="fixed left-0 top-0 z-50 h-screen w-72 flex flex-col bg-white shadow-2xl lg:hidden"
            >
              {/* Mobile Logo */}
              <div className="flex items-center justify-between p-6 border-b border-slate-100">
                <div className="flex items-center gap-3">
                  <div className="gradient-primary rounded-xl p-2.5">
                    <BarChart3 className="h-6 w-6 text-white" />
                  </div>
                  <h1 className="text-xl font-bold text-slate-800">Bizflow ERP</h1>
                </div>
                <button
                  onClick={() => setSidebarOpen(false)}
                  className="p-2 rounded-lg text-slate-400 hover:text-slate-600 hover:bg-slate-100"
                >
                  <X className="h-5 w-5" />
                </button>
              </div>

              {/* Mobile User Info */}
              <div className="mx-4 mt-4 p-4 rounded-xl bg-gradient-to-br from-blue-50 to-indigo-50 border border-blue-100">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-full bg-gradient-to-r from-blue-500 to-indigo-600 flex items-center justify-center text-white font-semibold">
                    {user?.email?.charAt(0).toUpperCase() || 'U'}
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-semibold text-slate-800 truncate">{user?.email || 'Usuario'}</p>
                    <p className="text-xs text-slate-500">Administrador</p>
                  </div>
                </div>
              </div>

              {/* Mobile Navigation */}
              <nav className="flex-1 mt-4 px-4 space-y-1">
                {menuItems.map((item) => {
                  const Icon = item.icon;
                  const active = isActive(item.path);
                  return (
                    <button
                      key={item.path}
                      onClick={() => {
                        navigate(item.path);
                        setSidebarOpen(false);
                      }}
                      className={`
                        flex w-full items-center gap-3 rounded-xl px-4 py-3 text-sm font-medium transition-all
                        ${active 
                          ? 'bg-gradient-to-r from-blue-500 to-indigo-600 text-white shadow-lg shadow-blue-500/25' 
                          : 'text-slate-600 hover:bg-slate-100'
                        }
                      `}
                    >
                      <Icon className="h-5 w-5" />
                      <span>{item.label}</span>
                    </button>
                  );
                })}
              </nav>

              {/* Mobile Logout */}
              <div className="p-4 border-t border-slate-100">
                <Button
                  onClick={handleLogout}
                  variant="danger"
                  className="w-full"
                >
                  <LogOut className="h-4 w-4" />
                  Cerrar sesión
                </Button>
              </div>
            </motion.aside>
          </>
        )}
      </AnimatePresence>

      {/* Main Content */}
      <div className="lg:pl-72">
        {/* Top Bar */}
        <header className="sticky top-0 z-20 bg-white/80 backdrop-blur-lg border-b border-slate-200/80">
          <div className="flex items-center justify-between px-4 sm:px-6 lg:px-8 py-4">
            <div className="flex items-center gap-4">
              <button
                onClick={() => setSidebarOpen(true)}
                className="lg:hidden p-2 rounded-lg text-slate-500 hover:text-slate-700 hover:bg-slate-100"
              >
                <Menu className="h-6 w-6" />
              </button>
              
              {/* Command Palette Search Bar */}
              <div className="hidden sm:block relative" ref={searchRef}>
                <form onSubmit={handleSearchSubmit}>
                  <div className="flex items-center gap-2 px-4 py-2 rounded-xl bg-slate-100/80 border border-slate-200/60 w-64 lg:w-80">
                    <Search className="h-4 w-4 text-slate-400" />
                    <input 
                      type="text" 
                      placeholder="Buscar... (ej: Gastos Juan)" 
                      value={searchQuery}
                      onChange={(e) => {
                        setSearchQuery(e.target.value);
                        setShowSuggestions(true);
                      }}
                      onFocus={() => setShowSuggestions(true)}
                      className="bg-transparent text-sm text-slate-600 placeholder-slate-400 outline-none flex-1"
                    />
                  </div>
                </form>
                
                {/* Suggestions Dropdown */}
                <AnimatePresence>
                  {showSuggestions && searchQuery && (
                    <motion.div
                      initial={{ opacity: 0, y: -10 }}
                      animate={{ opacity: 1, y: 0 }}
                      exit={{ opacity: 0, y: -10 }}
                      className="absolute top-full left-0 right-0 mt-2 bg-white rounded-xl shadow-xl border border-slate-200 overflow-hidden z-50"
                    >
                      {getSuggestions().length > 0 ? (
                        <div className="py-2">
                          {getSuggestions().map((suggestion, idx) => {
                            const Icon = suggestion.icon;
                            return (
                              <button
                                key={idx}
                                onClick={() => handleSuggestionClick(suggestion)}
                                className="w-full flex items-center gap-3 px-4 py-2.5 hover:bg-slate-50 transition-colors text-left"
                              >
                                <div className={`p-1.5 rounded-lg ${suggestion.isSearch ? 'bg-blue-100' : 'bg-slate-100'}`}>
                                  <Icon className={`h-4 w-4 ${suggestion.isSearch ? 'text-blue-600' : 'text-slate-500'}`} />
                                </div>
                                <span className={`text-sm ${suggestion.isSearch ? 'text-blue-600 font-medium' : 'text-slate-700'}`}>
                                  {suggestion.label}
                                </span>
                                {!suggestion.isSearch && suggestion.searchable && (
                                  <span className="ml-auto text-xs text-slate-400">+ nombre para buscar</span>
                                )}
                              </button>
                            );
                          })}
                        </div>
                      ) : (
                        <div className="px-4 py-3 text-sm text-slate-500">
                          No se encontraron resultados
                        </div>
                      )}
                    </motion.div>
                  )}
                </AnimatePresence>
              </div>
            </div>

            <div className="flex items-center gap-3">
              {/* Notifications */}
              <NotificationBell />

              {/* User Avatar & Dropdown */}
              <div className="relative" ref={userDropdownRef}>
                <button 
                  onClick={() => {
                    setUserDropdownOpen(!userDropdownOpen);
                  }}
                  className="flex items-center gap-3 p-1.5 pr-4 rounded-xl hover:bg-slate-100 transition-colors"
                >
                  <div className="w-8 h-8 rounded-lg bg-gradient-to-r from-blue-500 to-indigo-600 flex items-center justify-center text-white font-semibold text-sm">
                    {user?.email?.charAt(0).toUpperCase() || 'U'}
                  </div>
                  <span className="hidden sm:block text-sm font-medium text-slate-700">
                    {user?.email?.split('@')[0] || 'Usuario'}
                  </span>
                </button>

                {/* User Dropdown */}
                <AnimatePresence>
                  {userDropdownOpen && (
                    <motion.div
                      initial={{ opacity: 0, y: -10, scale: 0.95 }}
                      animate={{ opacity: 1, y: 0, scale: 1 }}
                      exit={{ opacity: 0, y: -10, scale: 0.95 }}
                      transition={{ duration: 0.15 }}
                      className="absolute right-0 mt-2 w-56 bg-white rounded-xl shadow-xl border border-slate-200/80 overflow-hidden z-50"
                    >
                      <div className="p-3 border-b border-slate-100 bg-gradient-to-r from-slate-50 to-white">
                        <p className="text-sm font-semibold text-slate-800">{user?.email || 'Usuario'}</p>
                        <p className="text-xs text-slate-500">Administrador</p>
                      </div>
                      <div className="p-2">
                        <button
                          onClick={() => {
                            navigate('/profile');
                            setUserDropdownOpen(false);
                          }}
                          className="flex w-full items-center gap-3 px-3 py-2 text-sm text-slate-600 hover:bg-slate-100 rounded-lg transition-colors"
                        >
                          <User className="h-4 w-4" />
                          Ver perfil
                        </button>
                        <button
                          onClick={() => {
                            navigate('/settings');
                            setUserDropdownOpen(false);
                          }}
                          className="flex w-full items-center gap-3 px-3 py-2 text-sm text-slate-600 hover:bg-slate-100 rounded-lg transition-colors"
                        >
                          <Settings className="h-4 w-4" />
                          Configuración
                        </button>
                      </div>
                      <div className="p-2 border-t border-slate-100">
                        <button
                          onClick={() => {
                            handleLogout();
                            setUserDropdownOpen(false);
                          }}
                          className="flex w-full items-center gap-3 px-3 py-2 text-sm text-rose-600 hover:bg-rose-50 rounded-lg transition-colors"
                        >
                          <LogOut className="h-4 w-4" />
                          Cerrar sesión
                        </button>
                      </div>
                    </motion.div>
                  )}
                </AnimatePresence>
              </div>
            </div>
          </div>
        </header>

        {/* Page Content */}
        <main className="p-4 sm:p-6 lg:p-8">
          <Outlet />
        </main>
      </div>
    </div>
  );
}

export default Layout;
