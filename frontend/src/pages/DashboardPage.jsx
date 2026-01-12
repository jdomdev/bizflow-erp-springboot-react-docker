import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { 
  TrendingUp, 
  DollarSign, 
  PlusCircle, 
  ArrowUpRight, 
  ArrowDownRight,
  Calendar,
  MoreHorizontal,
  Eye,
  Download
} from 'lucide-react';
import Card from '../components/Card';
import Button from '../components/Button';
import { useExpenseStore } from '../store/authStore';
import { userService, expenseService } from '../services/api';

function DashboardPage() {
  const expenses = useExpenseStore((state) => state.expenses);
  const setExpenses = useExpenseStore((state) => state.setExpenses);
  const [stats, setStats] = useState({
    totalExpenses: 0,
    thisMonth: 0,
    averageExpense: 0,
    lastMonth: 0,
  });
  const [profile, setProfile] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [expensesResponse, profileResponse] = await Promise.all([
        expenseService.getAll(),
        userService.getProfile()
      ]);

      const data = expensesResponse.data || [];
      setExpenses(data);
      setProfile(profileResponse.data);

      // Calcular estadísticas
      const totalExpenses = data.reduce((sum, exp) => sum + (exp.amount || 0), 0);
      const now = new Date();
      
      const thisMonth = data
        .filter((exp) => {
          const expDate = new Date(exp.date);
          return (
            expDate.getMonth() === now.getMonth() &&
            expDate.getFullYear() === now.getFullYear()
          );
        })
        .reduce((sum, exp) => sum + (exp.amount || 0), 0);

      const lastMonth = data
        .filter((exp) => {
          const expDate = new Date(exp.date);
          const lastMonthDate = new Date(now.getFullYear(), now.getMonth() - 1);
          return (
            expDate.getMonth() === lastMonthDate.getMonth() &&
            expDate.getFullYear() === lastMonthDate.getFullYear()
          );
        })
        .reduce((sum, exp) => sum + (exp.amount || 0), 0);

      setStats({
        totalExpenses,
        thisMonth,
        lastMonth,
        averageExpense: data.length > 0 ? totalExpenses / data.length : 0,
      });
    } catch (error) {
      console.error('Error cargando datos:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const monthlyChange = stats.lastMonth > 0 
    ? ((stats.thisMonth - stats.lastMonth) / stats.lastMonth * 100).toFixed(1)
    : 0;

  const statCards = [
    {
      icon: DollarSign,
      label: 'Gasto Total',
      value: `$${stats.totalExpenses.toLocaleString('es-ES', { minimumFractionDigits: 2 })}`,
      change: '+12.5%',
      trend: 'up',
      bgGradient: 'from-blue-500 to-indigo-600',
      lightBg: 'from-blue-50 to-indigo-50',
      iconBg: 'bg-blue-100',
      iconColor: 'text-blue-600',
    },
    {
      icon: Calendar,
      label: 'Este Mes',
      value: `$${stats.thisMonth.toLocaleString('es-ES', { minimumFractionDigits: 2 })}`,
      change: `${monthlyChange > 0 ? '+' : ''}${monthlyChange}%`,
      trend: monthlyChange >= 0 ? 'up' : 'down',
      bgGradient: 'from-emerald-500 to-teal-600',
      lightBg: 'from-emerald-50 to-teal-50',
      iconBg: 'bg-emerald-100',
      iconColor: 'text-emerald-600',
    },
    {
      icon: TrendingUp,
      label: 'Gasto Promedio',
      value: `$${stats.averageExpense.toLocaleString('es-ES', { minimumFractionDigits: 2 })}`,
      change: '-3.2%',
      trend: 'down',
      bgGradient: 'from-violet-500 to-purple-600',
      lightBg: 'from-violet-50 to-purple-50',
      iconBg: 'bg-violet-100',
      iconColor: 'text-violet-600',
    },
  ];

  const getStatusBadge = (status) => {
    const styles = {
      approved: 'bg-emerald-50 text-emerald-700 border-emerald-200',
      pending: 'bg-amber-50 text-amber-700 border-amber-200',
      rejected: 'bg-rose-50 text-rose-700 border-rose-200',
    };
    const labels = {
      approved: 'Aprobado',
      pending: 'Pendiente',
      rejected: 'Rechazado',
    };
    return (
      <span className={`inline-flex items-center px-2.5 py-1 rounded-lg text-xs font-medium border ${styles[status] || styles.approved}`}>
        {labels[status] || labels.approved}
      </span>
    );
  };

  return (
    <div className="space-y-6 sm:space-y-8">
      {/* Header */}
      <motion.div
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4"
      >
        <div>
          <h1 className="text-2xl sm:text-3xl lg:text-4xl font-bold text-slate-800 mb-1">
            ¡Buen día! 👋
          </h1>
          <p className="text-slate-500">
            Aquí tienes un resumen de tus finanzas
          </p>
        </div>
        <div className="flex gap-3">
          <Button variant="secondary" size="sm">
            <Download className="h-4 w-4" />
            <span className="hidden sm:inline">Exportar</span>
          </Button>
          <Button variant="primary" size="sm">
            <PlusCircle className="h-4 w-4" />
            Nuevo Gasto
          </Button>
        </div>
      </motion.div>

      {/* Stats Cards */}
      <div className="grid gap-4 sm:gap-6 grid-cols-1 sm:grid-cols-2 lg:grid-cols-3">
        {statCards.map((stat, index) => {
          const Icon = stat.icon;
          return (
            <motion.div
              key={index}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: index * 0.1 }}
            >
              <Card variant="gradient" className="p-5 sm:p-6 hover:shadow-soft-lg transition-shadow">
                <div className="flex items-start justify-between mb-4">
                  <div className={`p-3 rounded-xl ${stat.iconBg}`}>
                    <Icon className={`h-6 w-6 ${stat.iconColor}`} />
                  </div>
                  <div className={`flex items-center gap-1 text-xs font-medium px-2 py-1 rounded-lg ${
                    stat.trend === 'up' 
                      ? 'bg-emerald-50 text-emerald-600' 
                      : 'bg-rose-50 text-rose-600'
                  }`}>
                    {stat.trend === 'up' 
                      ? <ArrowUpRight className="h-3 w-3" /> 
                      : <ArrowDownRight className="h-3 w-3" />
                    }
                    {stat.change}
                  </div>
                </div>
                <p className="text-sm font-medium text-slate-500 mb-1">{stat.label}</p>
                <p className="text-2xl sm:text-3xl font-bold text-slate-800">
                  {stat.value}
                </p>
              </Card>
            </motion.div>
          );
        })}
      </div>

      {/* Recent Expenses Table */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.3 }}
      >
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-xl font-bold text-slate-800">Gastos Recientes</h2>
          <Button variant="ghost" size="sm">
            Ver todos
            <ArrowUpRight className="h-4 w-4" />
          </Button>
        </div>
        
        <Card variant="solid" className="overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b border-slate-100">
                  <th className="px-4 sm:px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">
                    Descripción
                  </th>
                  <th className="px-4 sm:px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">
                    Monto
                  </th>
                  <th className="hidden sm:table-cell px-4 sm:px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">
                    Fecha
                  </th>
                  <th className="px-4 sm:px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">
                    Estado
                  </th>
                  <th className="px-4 sm:px-6 py-4 text-right text-xs font-semibold text-slate-500 uppercase tracking-wider">
                    Acciones
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {isLoading ? (
                  <tr>
                    <td colSpan="5" className="px-6 py-12 text-center">
                      <div className="flex flex-col items-center gap-3">
                        <div className="w-8 h-8 border-2 border-blue-500 border-t-transparent rounded-full animate-spin"></div>
                        <span className="text-slate-500">Cargando gastos...</span>
                      </div>
                    </td>
                  </tr>
                ) : expenses.length === 0 ? (
                  <tr>
                    <td colSpan="5" className="px-6 py-12 text-center">
                      <div className="flex flex-col items-center gap-3">
                        <div className="w-16 h-16 rounded-full bg-slate-100 flex items-center justify-center">
                          <DollarSign className="h-8 w-8 text-slate-400" />
                        </div>
                        <div>
                          <p className="text-slate-700 font-medium">No hay gastos registrados</p>
                          <p className="text-sm text-slate-500">Comienza agregando tu primer gasto</p>
                        </div>
                        <Button variant="primary" size="sm" className="mt-2">
                          <PlusCircle className="h-4 w-4" />
                          Agregar gasto
                        </Button>
                      </div>
                    </td>
                  </tr>
                ) : (
                  expenses.slice(0, 5).map((expense, idx) => (
                    <motion.tr 
                      key={expense.id}
                      initial={{ opacity: 0 }}
                      animate={{ opacity: 1 }}
                      transition={{ delay: 0.4 + idx * 0.05 }}
                      className="hover:bg-slate-50/50 transition-colors"
                    >
                      <td className="px-4 sm:px-6 py-4">
                        <div className="flex items-center gap-3">
                          <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-blue-100 to-indigo-100 flex items-center justify-center">
                            <DollarSign className="h-5 w-5 text-blue-600" />
                          </div>
                          <div>
                            <p className="font-medium text-slate-800">{expense.description}</p>
                            <p className="text-xs text-slate-500 sm:hidden">
                              {new Date(expense.date).toLocaleDateString('es-ES')}
                            </p>
                          </div>
                        </div>
                      </td>
                      <td className="px-4 sm:px-6 py-4">
                        <span className="font-semibold text-slate-800">
                          ${expense.amount?.toLocaleString('es-ES', { minimumFractionDigits: 2 })}
                        </span>
                      </td>
                      <td className="hidden sm:table-cell px-4 sm:px-6 py-4 text-slate-500">
                        {new Date(expense.date).toLocaleDateString('es-ES', {
                          day: 'numeric',
                          month: 'short',
                          year: 'numeric'
                        })}
                      </td>
                      <td className="px-4 sm:px-6 py-4">
                        {getStatusBadge(expense.status || 'approved')}
                      </td>
                      <td className="px-4 sm:px-6 py-4 text-right">
                        <div className="flex items-center justify-end gap-1">
                          <button className="p-2 rounded-lg text-slate-400 hover:text-blue-600 hover:bg-blue-50 transition-colors">
                            <Eye className="h-4 w-4" />
                          </button>
                          <button className="p-2 rounded-lg text-slate-400 hover:text-slate-600 hover:bg-slate-100 transition-colors">
                            <MoreHorizontal className="h-4 w-4" />
                          </button>
                        </div>
                      </td>
                    </motion.tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </Card>
      </motion.div>
    </div>
  );
}

export default DashboardPage;
