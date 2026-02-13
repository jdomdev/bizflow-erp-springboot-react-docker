import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { Sun, Moon, Monitor, List, Check } from 'lucide-react';
import Card from '../components/Card';
import { useTheme } from '../contexts/ThemeContext';

function SettingsPage() {
  const { theme, setTheme, isDark } = useTheme();
  const [itemsPerPage, setItemsPerPage] = useState(() => {
    return parseInt(localStorage.getItem('itemsPerPage') || '10', 10);
  });
  const [saved, setSaved] = useState(false);

  const themeOptions = [
    { value: 'light', label: 'Claro', icon: Sun },
    { value: 'dark', label: 'Oscuro', icon: Moon },
    { value: 'system', label: 'Sistema', icon: Monitor },
  ];

  const itemsOptions = [5, 10, 15, 20, 25, 50];

  const handleThemeChange = (newTheme) => {
    setTheme(newTheme);
  };

  const handleItemsPerPageChange = (value) => {
    setItemsPerPage(value);
    localStorage.setItem('itemsPerPage', value.toString());
    window.dispatchEvent(new CustomEvent('itemsPerPageChanged', { detail: value }));
    showSaved();
  };

  const showSaved = () => {
    setSaved(true);
    setTimeout(() => setSaved(false), 2000);
  };

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      className="space-y-6"
    >
      <div>
        <h1 className="gradient-text text-4xl font-bold">Configuración</h1>
        <p className="mt-2 text-slate-500 dark:text-slate-400">Personaliza tu experiencia en Bizflow ERP</p>
      </div>

      {/* Theme Settings */}
      <Card className="p-6">
        <div className="flex items-center gap-3 mb-6">
          <div className="p-2 rounded-lg bg-gradient-to-r from-blue-500 to-indigo-600">
            {isDark ? <Moon className="h-5 w-5 text-white" /> : <Sun className="h-5 w-5 text-white" />}
          </div>
          <div>
            <h2 className="text-lg font-semibold text-slate-800 dark:text-white">Apariencia</h2>
            <p className="text-sm text-slate-500 dark:text-slate-400">Elige el tema de la aplicación</p>
          </div>
        </div>

        <div className="grid grid-cols-3 gap-4">
          {themeOptions.map((option) => {
            const Icon = option.icon;
            const isSelected = theme === option.value;
            return (
              <button
                key={option.value}
                onClick={() => handleThemeChange(option.value)}
                className={`
                  relative flex flex-col items-center gap-3 p-4 rounded-xl border-2 transition-all
                  ${isSelected 
                    ? 'border-blue-500 bg-blue-50 dark:bg-blue-900/20' 
                    : 'border-slate-200 dark:border-slate-700 hover:border-slate-300 dark:hover:border-slate-600 bg-white dark:bg-slate-800'
                  }
                `}
              >
                <div className={`p-3 rounded-lg ${isSelected ? 'bg-blue-500 text-white' : 'bg-slate-100 dark:bg-slate-700 text-slate-600 dark:text-slate-300'}`}>
                  <Icon className="h-6 w-6" />
                </div>
                <span className={`text-sm font-medium ${isSelected ? 'text-blue-600 dark:text-blue-400' : 'text-slate-700 dark:text-slate-300'}`}>
                  {option.label}
                </span>
                {isSelected && (
                  <div className="absolute top-2 right-2">
                    <Check className="h-4 w-4 text-blue-500" />
                  </div>
                )}
              </button>
            );
          })}
        </div>
      </Card>

      {/* Items Per Page Settings */}
      <Card className="p-6">
        <div className="flex items-center gap-3 mb-6">
          <div className="p-2 rounded-lg bg-gradient-to-r from-emerald-500 to-teal-600">
            <List className="h-5 w-5 text-white" />
          </div>
          <div>
            <h2 className="text-lg font-semibold text-slate-800 dark:text-white">Paginación</h2>
            <p className="text-sm text-slate-500 dark:text-slate-400">Número de elementos por página en las listas</p>
          </div>
        </div>

        <div className="flex flex-wrap gap-3">
          {itemsOptions.map((value) => (
            <button
              key={value}
              onClick={() => handleItemsPerPageChange(value)}
              className={`
                px-4 py-2 rounded-lg font-medium transition-all
                ${itemsPerPage === value 
                  ? 'bg-emerald-500 text-white shadow-lg shadow-emerald-500/25' 
                  : 'bg-slate-100 dark:bg-slate-700 text-slate-600 dark:text-slate-300 hover:bg-slate-200 dark:hover:bg-slate-600'
                }
              `}
            >
              {value}
            </button>
          ))}
        </div>
        <p className="mt-4 text-sm text-slate-500 dark:text-slate-400">
          Se mostrarán <span className="font-semibold text-slate-700 dark:text-slate-200">{itemsPerPage}</span> elementos por página en gastos, nóminas, empleados, etc.
        </p>
      </Card>

      {/* Save Notification */}
      {saved && (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          exit={{ opacity: 0, y: 20 }}
          className="fixed bottom-6 right-6 flex items-center gap-2 px-4 py-3 bg-emerald-500 text-white rounded-lg shadow-lg"
        >
          <Check className="h-5 w-5" />
          <span className="font-medium">Configuración guardada</span>
        </motion.div>
      )}
    </motion.div>
  );
}

export default SettingsPage;
