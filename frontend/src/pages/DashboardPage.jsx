import React, { useEffect, useState, useMemo } from 'react';
import { motion } from 'framer-motion';
import { TrendingUp, DollarSign, PlusCircle, Users, Calendar, Filter } from 'lucide-react';
import Card from '../components/Card';
import Button from '../components/Button';
import LineChart from '../components/charts/LineChart';
import BarChart from '../components/charts/BarChart';
import DoughnutChart from '../components/charts/DoughnutChart';
import DateRangeFilter from '../components/filters/DateRangeFilter';
import { useExpenseStore, usePayrollStore } from '../store/authStore';
import { expenseService, payrollService } from '../services/api';
import { getLastNMonths, isSameMonth } from '../utils/dateUtils';

/**
 * DashboardPage - Main dashboard with expense and payroll analytics
 * Features:
 * - KPI cards with key metrics
 * - Trend charts for expenses over time
 * - Category breakdown charts
 * - Advanced filtering (date range, status)
 * - Responsive design
 */
function DashboardPage() {
  const expenses = useExpenseStore((state) => state.expenses);
  const setExpenses = useExpenseStore((state) => state.setExpenses);
  const payrolls = usePayrollStore((state) => state.payrolls);
  const setPayrolls = usePayrollStore((state) => state.setPayrolls);
  
  const [stats, setStats] = useState({
    totalExpenses: 0,
    thisMonth: 0,
    averageExpense: 0,
    totalPayroll: 0,
  });
  const [isLoading, setIsLoading] = useState(true);
  const [showFilters, setShowFilters] = useState(false);
  
  // Filter states
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setIsLoading(true);
      const [expenseResponse, payrollResponse] = await Promise.all([
        expenseService.getAll(),
        payrollService.getAll(),
      ]);
      
      const expenseData = expenseResponse.data || [];
      const payrollData = payrollResponse.data || [];
      
      setExpenses(expenseData);
      setPayrolls(payrollData);

      calculateStats(expenseData, payrollData);
    } catch (error) {
      console.error('Error cargando datos:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const calculateStats = (expenseData, payrollData) => {
    const now = new Date();
    const currentMonth = now.getMonth();
    const currentYear = now.getFullYear();

    const totalExpenses = expenseData.reduce((sum, exp) => sum + (exp.amount || 0), 0);
    const thisMonth = expenseData
      .filter((exp) => {
        const expDate = new Date(exp.date);
        return (
          expDate.getMonth() === currentMonth &&
          expDate.getFullYear() === currentYear
        );
      })
      .reduce((sum, exp) => sum + (exp.amount || 0), 0);

    const totalPayroll = payrollData
      .filter((p) => {
        const payDate = new Date(p.date);
        return (
          payDate.getMonth() === currentMonth &&
          payDate.getFullYear() === currentYear
        );
      })
      .reduce((sum, p) => sum + (p.amount || 0), 0);

    setStats({
      totalExpenses,
      thisMonth,
      averageExpense: expenseData.length > 0 ? totalExpenses / expenseData.length : 0,
      totalPayroll,
    });
  };

  // Filter expenses based on selected filters
  const filteredExpenses = useMemo(() => {
    return expenses.filter((expense) => {
      const expDate = new Date(expense.date);
      
      if (startDate && expDate < new Date(startDate)) return false;
      if (endDate && expDate > new Date(endDate)) return false;
      
      return true;
    });
  }, [expenses, startDate, endDate]);

  // Prepare chart data for monthly trend
  const monthlyTrendData = useMemo(() => {
    const monthsData = getLastNMonths(6);

    monthsData.forEach((monthInfo) => {
      monthInfo.expenses = 0;
      monthInfo.payroll = 0;
    });

    filteredExpenses.forEach((expense) => {
      const expDate = new Date(expense.date);
      const monthIndex = monthsData.findIndex((m) => 
        isSameMonth(m.date, expDate)
      );
      if (monthIndex !== -1) {
        monthsData[monthIndex].expenses += expense.amount || 0;
      }
    });

    payrolls.forEach((payroll) => {
      const payDate = new Date(payroll.date);
      const monthIndex = monthsData.findIndex((m) => 
        isSameMonth(m.date, payDate)
      );
      if (monthIndex !== -1) {
        monthsData[monthIndex].payroll += payroll.amount || 0;
      }
    });

    return {
      labels: monthsData.map((m) => m.label),
      datasets: [
        {
          label: 'Gastos',
          data: monthsData.map((m) => m.expenses),
          borderColor: 'rgb(59, 130, 246)',
          backgroundColor: 'rgba(59, 130, 246, 0.1)',
          fill: true,
          tension: 0.4,
        },
        {
          label: 'Nóminas',
          data: monthsData.map((m) => m.payroll),
          borderColor: 'rgb(16, 185, 129)',
          backgroundColor: 'rgba(16, 185, 129, 0.1)',
          fill: true,
          tension: 0.4,
        },
      ],
    };
  }, [filteredExpenses, payrolls]);

  // Prepare chart data for category breakdown
  const categoryData = useMemo(() => {
    const categories = {};
    
    filteredExpenses.forEach((expense) => {
      const category = expense.concept || 'Sin categoría';
      categories[category] = (categories[category] || 0) + (expense.amount || 0);
    });

    const sortedCategories = Object.entries(categories)
      .sort((a, b) => b[1] - a[1])
      .slice(0, 5);

    return {
      labels: sortedCategories.map(([name]) => name),
      datasets: [
        {
          data: sortedCategories.map(([, value]) => value),
          backgroundColor: [
            'rgba(59, 130, 246, 0.8)',
            'rgba(16, 185, 129, 0.8)',
            'rgba(245, 158, 11, 0.8)',
            'rgba(239, 68, 68, 0.8)',
            'rgba(139, 92, 246, 0.8)',
          ],
          borderColor: [
            'rgb(59, 130, 246)',
            'rgb(16, 185, 129)',
            'rgb(245, 158, 11)',
            'rgb(239, 68, 68)',
            'rgb(139, 92, 246)',
          ],
          borderWidth: 2,
        },
      ],
    };
  }, [filteredExpenses]);

  const statCards = [
    {
      icon: DollarSign,
      label: 'Gasto Total',
      value: `$${stats.totalExpenses.toFixed(2)}`,
      gradient: 'from-blue-500 to-indigo-700',
    },
    {
      icon: TrendingUp,
      label: 'Este Mes',
      value: `$${stats.thisMonth.toFixed(2)}`,
      gradient: 'from-green-500 to-teal-700',
    },
    {
      icon: Users,
      label: 'Nómina Mes',
      value: `$${stats.totalPayroll.toFixed(2)}`,
      gradient: 'from-purple-500 to-pink-700',
    },
    {
      icon: Calendar,
      label: 'Gasto Promedio',
      value: `$${stats.averageExpense.toFixed(2)}`,
      gradient: 'from-orange-500 to-red-700',
    },
  ];

  const statusOptions = [
    { value: 'PENDING', label: 'Pendiente' },
    { value: 'APPROVED', label: 'Aprobado' },
    { value: 'REJECTED', label: 'Rechazado' },
  ];

  return (
    <div className="space-y-8 animate-fade-in-up">
      {/* Header */}
      <motion.div
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex items-center justify-between"
      >
        <div>
          <h1 className="gradient-text text-4xl font-bold mb-2">Dashboard</h1>
          <p className="text-slate-400">Bienvenido de vuelta a tu panel de control</p>
        </div>
        <div className="flex gap-3">
          <Button 
            variant="secondary" 
            onClick={() => setShowFilters(!showFilters)}
          >
            <Filter className="h-5 w-5" />
            Filtros
          </Button>
          <Button variant="primary">
            <PlusCircle className="h-5 w-5" />
            Nuevo Gasto
          </Button>
        </div>
      </motion.div>

      {/* Filters */}
      {showFilters && (
        <motion.div
          initial={{ opacity: 0, height: 0 }}
          animate={{ opacity: 1, height: 'auto' }}
          exit={{ opacity: 0, height: 0 }}
        >
          <Card className="p-6">
            <h3 className="text-lg font-semibold text-white mb-4">Filtros Avanzados</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <DateRangeFilter
                startDate={startDate}
                endDate={endDate}
                onStartDateChange={setStartDate}
                onEndDateChange={setEndDate}
              />
              <div className="flex items-end">
                <Button
                  variant="secondary"
                  onClick={() => {
                    setStartDate('');
                    setEndDate('');
                  }}
                  className="w-full"
                >
                  Limpiar Filtros
                </Button>
              </div>
            </div>
          </Card>
        </motion.div>
      )}

      {/* Stats Cards */}
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ staggerChildren: 0.1 }}
        className="grid gap-6 md:grid-cols-2 lg:grid-cols-4"
      >
        {statCards.map((stat, index) => {
          const Icon = stat.icon;
          return (
            <motion.div
              key={index}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: index * 0.1 }}
            >
              <Card className={`p-6 bg-gradient-to-br ${stat.gradient}/20 border-0`}>
                <div className="flex items-start justify-between">
                  <div>
                    <p className="text-slate-400 text-sm font-medium">{stat.label}</p>
                    <p className="mt-2 text-3xl font-bold text-white">
                      {stat.value}
                    </p>
                  </div>
                  <div className={`rounded-lg bg-gradient-to-br ${stat.gradient} p-3`}>
                    <Icon className="h-6 w-6 text-white" />
                  </div>
                </div>
              </Card>
            </motion.div>
          );
        })}
      </motion.div>

      {/* Charts */}
      <div className="grid gap-6 lg:grid-cols-2">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.4 }}
        >
          <Card className="p-6">
            <h2 className="text-xl font-bold text-white mb-4">Tendencia Mensual</h2>
            {isLoading ? (
              <div className="h-[300px] flex items-center justify-center text-slate-400">
                Cargando datos...
              </div>
            ) : (
              <LineChart data={monthlyTrendData} height="300px" />
            )}
          </Card>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.5 }}
        >
          <Card className="p-6">
            <h2 className="text-xl font-bold text-white mb-4">Top 5 Categorías</h2>
            {isLoading ? (
              <div className="h-[300px] flex items-center justify-center text-slate-400">
                Cargando datos...
              </div>
            ) : (
              <DoughnutChart data={categoryData} height="300px" />
            )}
          </Card>
        </motion.div>
      </div>

      {/* Recent Expenses */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.6 }}
      >
        <h2 className="text-2xl font-bold text-white mb-4">Gastos Recientes</h2>
        <Card className="overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="border-b border-slate-700 bg-slate-800/50">
                <tr>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-slate-300">
                    Concepto
                  </th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-slate-300">
                    Monto
                  </th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-slate-300">
                    Fecha
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-700">
                {isLoading ? (
                  <tr>
                    <td colSpan="3" className="px-6 py-8 text-center text-slate-400">
                      Cargando gastos...
                    </td>
                  </tr>
                ) : filteredExpenses.length === 0 ? (
                  <tr>
                    <td colSpan="3" className="px-6 py-8 text-center text-slate-400">
                      No hay gastos registrados
                    </td>
                  </tr>
                ) : (
                  filteredExpenses.slice(0, 5).map((expense) => (
                    <tr key={expense.id} className="hover:bg-slate-800/30 transition">
                      <td className="px-6 py-4 text-white">{expense.concept}</td>
                      <td className="px-6 py-4 text-white font-semibold">
                        ${expense.amount?.toFixed(2)}
                      </td>
                      <td className="px-6 py-4 text-slate-400">
                        {new Date(expense.date).toLocaleDateString()}
                      </td>
                    </tr>
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

