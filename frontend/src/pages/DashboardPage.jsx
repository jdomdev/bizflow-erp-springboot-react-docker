import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { TrendingUp, DollarSign, PlusCircle } from 'lucide-react';
import Card from '../components/Card';
import Button from '../components/Button';
import { useExpenseStore } from '../store/authStore';
import { expenseService } from '../services/api';

function DashboardPage() {
  const expenses = useExpenseStore((state) => state.expenses);
  const setExpenses = useExpenseStore((state) => state.setExpenses);
  const [stats, setStats] = useState({
    totalExpenses: 0,
    thisMonth: 0,
    averageExpense: 0,
  });
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    loadExpenses();
  }, []);

  const loadExpenses = async () => {
    try {
      const response = await expenseService.getAll();
      const data = response.data || [];
      setExpenses(data);

      // Calcular estadísticas
      const totalExpenses = data.reduce((sum, exp) => sum + (exp.amount || 0), 0);
      const thisMonth = data
        .filter((exp) => {
          const expDate = new Date(exp.date);
          const now = new Date();
          return (
            expDate.getMonth() === now.getMonth() &&
            expDate.getFullYear() === now.getFullYear()
          );
        })
        .reduce((sum, exp) => sum + (exp.amount || 0), 0);

      setStats({
        totalExpenses,
        thisMonth,
        averageExpense: data.length > 0 ? totalExpenses / data.length : 0,
      });
    } catch (error) {
      console.error('Error cargando gastos:', error);
    } finally {
      setIsLoading(false);
    }
  };

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
      icon: DollarSign,
      label: 'Gasto Promedio',
      value: `$${stats.averageExpense.toFixed(2)}`,
      gradient: 'from-purple-500 to-pink-700',
    },
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
        <Button variant="primary">
          <PlusCircle className="h-5 w-5" />
          Nuevo Gasto
        </Button>
      </motion.div>

      {/* Stats Cards */}
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ staggerChildren: 0.1 }}
        className="grid gap-6 md:grid-cols-3"
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

      {/* Recent Expenses */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.3 }}
      >
        <h2 className="text-2xl font-bold text-white mb-4">Gastos Recientes</h2>
        <Card className="overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="border-b border-slate-700 bg-slate-800/50">
                <tr>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-slate-300">
                    Descripción
                  </th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-slate-300">
                    Monto
                  </th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-slate-300">
                    Fecha
                  </th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-slate-300">
                    Estado
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-700">
                {isLoading ? (
                  <tr>
                    <td colSpan="4" className="px-6 py-8 text-center text-slate-400">
                      Cargando gastos...
                    </td>
                  </tr>
                ) : expenses.length === 0 ? (
                  <tr>
                    <td colSpan="4" className="px-6 py-8 text-center text-slate-400">
                      No hay gastos registrados
                    </td>
                  </tr>
                ) : (
                  expenses.slice(0, 5).map((expense) => (
                    <tr key={expense.id} className="hover:bg-slate-800/30 transition">
                      <td className="px-6 py-4 text-white">{expense.description}</td>
                      <td className="px-6 py-4 text-white font-semibold">
                        ${expense.amount?.toFixed(2)}
                      </td>
                      <td className="px-6 py-4 text-slate-400">
                        {new Date(expense.date).toLocaleDateString()}
                      </td>
                      <td className="px-6 py-4">
                        <span className="inline-block px-3 py-1 rounded-full text-sm font-medium bg-green-500/20 text-green-400 border border-green-500/30">
                          Aprobado
                        </span>
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
