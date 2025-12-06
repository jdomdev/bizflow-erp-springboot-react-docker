import React, { useEffect, useState, useMemo } from 'react';
import { motion } from 'framer-motion';
import { 
  DollarSign, 
  TrendingUp, 
  Users, 
  Filter,
  Calendar,
  Download
} from 'lucide-react';
import Card from '../components/Card';
import Button from '../components/Button';
import BarChart from '../components/charts/BarChart';
import LineChart from '../components/charts/LineChart';
import DateRangeFilter from '../components/filters/DateRangeFilter';
import SearchFilter from '../components/filters/SearchFilter';
import { usePayrollStore } from '../store/authStore';
import { payrollService, employeeService } from '../services/api';

/**
 * PayrollPage - Payroll management and visualization
 * Features:
 * - Payroll list with employee information
 * - Monthly trends and analytics
 * - Advanced filtering (date range, employee)
 * - KPI metrics
 * - Charts for visualization
 */
function PayrollPage() {
  const payrolls = usePayrollStore((state) => state.payrolls);
  const setPayrolls = usePayrollStore((state) => state.setPayrolls);

  const [isLoading, setIsLoading] = useState(true);
  const [employees, setEmployees] = useState([]);
  const [showFilters, setShowFilters] = useState(false);

  // Filter states
  const [searchTerm, setSearchTerm] = useState('');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');

  // Stats
  const [stats, setStats] = useState({
    totalPayroll: 0,
    thisMonth: 0,
    employeeCount: 0,
    averagePayroll: 0,
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setIsLoading(true);
      const [payrollResponse, employeeResponse] = await Promise.all([
        payrollService.getAll(),
        employeeService.getAll(),
      ]);
      
      const payrollData = payrollResponse.data || [];
      const employeeData = employeeResponse.data || [];
      
      setPayrolls(payrollData);
      setEmployees(employeeData);
      calculateStats(payrollData, employeeData);
    } catch (error) {
      console.error('Error cargando datos de nómina:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const calculateStats = (payrollData, employeeData) => {
    const now = new Date();
    const currentMonth = now.getMonth();
    const currentYear = now.getFullYear();

    const totalPayroll = payrollData.reduce((sum, p) => sum + (p.amount || 0), 0);
    
    const thisMonth = payrollData
      .filter((p) => {
        const payDate = new Date(p.date);
        return (
          payDate.getMonth() === currentMonth &&
          payDate.getFullYear() === currentYear
        );
      })
      .reduce((sum, p) => sum + (p.amount || 0), 0);

    setStats({
      totalPayroll,
      thisMonth,
      employeeCount: employeeData.length,
      averagePayroll: payrollData.length > 0 ? totalPayroll / payrollData.length : 0,
    });
  };

  // Filter payrolls based on search and date range
  const filteredPayrolls = useMemo(() => {
    return payrolls.filter((payroll) => {
      const employee = employees.find((e) => e.id === payroll.employee?.id);
      const employeeName = employee ? `${employee.name} ${employee.surname}` : '';
      
      const matchesSearch = employeeName.toLowerCase().includes(searchTerm.toLowerCase());
      
      const payDate = new Date(payroll.date);
      const matchesDateRange = 
        (!startDate || payDate >= new Date(startDate)) &&
        (!endDate || payDate <= new Date(endDate));

      return matchesSearch && matchesDateRange;
    });
  }, [payrolls, employees, searchTerm, startDate, endDate]);

  // Prepare monthly trend data
  const monthlyTrendData = useMemo(() => {
    const last6Months = [];
    const now = new Date();
    
    for (let i = 5; i >= 0; i--) {
      const date = new Date(now.getFullYear(), now.getMonth() - i, 1);
      last6Months.push({
        month: date.toLocaleDateString('es-ES', { month: 'short', year: '2-digit' }),
        amount: 0,
        count: 0,
      });
    }

    filteredPayrolls.forEach((payroll) => {
      const payDate = new Date(payroll.date);
      const monthIndex = last6Months.findIndex((m) => {
        const [monthName] = m.month.split(' ');
        const payMonth = payDate.toLocaleDateString('es-ES', { month: 'short' });
        return monthName === payMonth;
      });
      if (monthIndex !== -1) {
        last6Months[monthIndex].amount += payroll.amount || 0;
        last6Months[monthIndex].count += 1;
      }
    });

    return {
      labels: last6Months.map((m) => m.month),
      datasets: [
        {
          label: 'Total Nómina',
          data: last6Months.map((m) => m.amount),
          backgroundColor: 'rgba(139, 92, 246, 0.8)',
          borderColor: 'rgb(139, 92, 246)',
          borderWidth: 2,
        },
      ],
    };
  }, [filteredPayrolls]);

  // Prepare employee comparison data
  const employeeComparisonData = useMemo(() => {
    const employeePayrolls = {};
    
    filteredPayrolls.forEach((payroll) => {
      const employee = employees.find((e) => e.id === payroll.employee?.id);
      if (employee) {
        const name = `${employee.name} ${employee.surname}`;
        employeePayrolls[name] = (employeePayrolls[name] || 0) + (payroll.amount || 0);
      }
    });

    const sortedEmployees = Object.entries(employeePayrolls)
      .sort((a, b) => b[1] - a[1])
      .slice(0, 10);

    return {
      labels: sortedEmployees.map(([name]) => name),
      datasets: [
        {
          label: 'Total Pagado',
          data: sortedEmployees.map(([, amount]) => amount),
          backgroundColor: 'rgba(16, 185, 129, 0.8)',
          borderColor: 'rgb(16, 185, 129)',
          borderWidth: 2,
        },
      ],
    };
  }, [filteredPayrolls, employees]);

  const clearFilters = () => {
    setSearchTerm('');
    setStartDate('');
    setEndDate('');
  };

  const statCards = [
    {
      icon: DollarSign,
      label: 'Total Nómina',
      value: `$${stats.totalPayroll.toFixed(2)}`,
      gradient: 'from-purple-500 to-pink-700',
    },
    {
      icon: TrendingUp,
      label: 'Este Mes',
      value: `$${stats.thisMonth.toFixed(2)}`,
      gradient: 'from-green-500 to-teal-700',
    },
    {
      icon: Users,
      label: 'Empleados',
      value: stats.employeeCount,
      gradient: 'from-blue-500 to-indigo-700',
    },
    {
      icon: Calendar,
      label: 'Promedio',
      value: `$${stats.averagePayroll.toFixed(2)}`,
      gradient: 'from-orange-500 to-red-700',
    },
  ];

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      className="space-y-6"
    >
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="gradient-text text-4xl font-bold">Nómina</h1>
          <p className="mt-2 text-slate-400">Información de nómina y pagos</p>
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
            <Download className="h-5 w-5" />
            Exportar
          </Button>
        </div>
      </div>

      {/* Filters */}
      {showFilters && (
        <motion.div
          initial={{ opacity: 0, height: 0 }}
          animate={{ opacity: 1, height: 'auto' }}
          exit={{ opacity: 0, height: 0 }}
        >
          <Card className="p-6">
            <h3 className="text-lg font-semibold text-white mb-4">Filtros Avanzados</h3>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <SearchFilter
                value={searchTerm}
                onChange={setSearchTerm}
                placeholder="Buscar por empleado..."
                label="Buscar"
              />
              <DateRangeFilter
                startDate={startDate}
                endDate={endDate}
                onStartDateChange={setStartDate}
                onEndDateChange={setEndDate}
              />
              <div className="flex items-end">
                <Button
                  variant="secondary"
                  onClick={clearFilters}
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
      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
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
      </div>

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
              <BarChart data={monthlyTrendData} height="300px" />
            )}
          </Card>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.5 }}
        >
          <Card className="p-6">
            <h2 className="text-xl font-bold text-white mb-4">Top 10 Empleados</h2>
            {isLoading ? (
              <div className="h-[300px] flex items-center justify-center text-slate-400">
                Cargando datos...
              </div>
            ) : (
              <BarChart 
                data={employeeComparisonData} 
                height="300px"
                options={{
                  indexAxis: 'y',
                  scales: {
                    x: {
                      grid: {
                        color: 'rgba(51, 65, 85, 0.3)',
                      },
                      ticks: {
                        color: '#94a3b8',
                      },
                    },
                    y: {
                      grid: {
                        display: false,
                      },
                      ticks: {
                        color: '#94a3b8',
                      },
                    },
                  },
                }}
              />
            )}
          </Card>
        </motion.div>
      </div>

      {/* Payroll Table */}
      <Card className="overflow-hidden">
        <div className="p-6 border-b border-slate-700">
          <h2 className="text-xl font-bold text-white">Historial de Nómina</h2>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="border-b border-slate-700 bg-slate-800/50">
              <tr>
                <th className="px-6 py-3 text-left text-sm font-semibold text-slate-300">
                  Empleado
                </th>
                <th className="px-6 py-3 text-left text-sm font-semibold text-slate-300">
                  Monto
                </th>
                <th className="px-6 py-3 text-left text-sm font-semibold text-slate-300">
                  Fecha de Pago
                </th>
                <th className="px-6 py-3 text-left text-sm font-semibold text-slate-300">
                  Período
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-700">
              {isLoading ? (
                <tr>
                  <td colSpan="4" className="px-6 py-8 text-center text-slate-400">
                    Cargando nóminas...
                  </td>
                </tr>
              ) : filteredPayrolls.length === 0 ? (
                <tr>
                  <td colSpan="4" className="px-6 py-8 text-center text-slate-400">
                    No hay registros de nómina
                  </td>
                </tr>
              ) : (
                filteredPayrolls.map((payroll) => {
                  const employee = employees.find((e) => e.id === payroll.employee?.id);
                  const payDate = new Date(payroll.date);
                  
                  return (
                    <tr key={payroll.id} className="hover:bg-slate-800/30 transition">
                      <td className="px-6 py-4 text-white font-medium">
                        {employee ? `${employee.name} ${employee.surname}` : 'Empleado desconocido'}
                      </td>
                      <td className="px-6 py-4 text-white font-semibold">
                        ${payroll.amount?.toFixed(2)}
                      </td>
                      <td className="px-6 py-4 text-slate-400">
                        {payDate.toLocaleDateString('es-ES')}
                      </td>
                      <td className="px-6 py-4 text-slate-400">
                        {payDate.toLocaleDateString('es-ES', { month: 'long', year: 'numeric' })}
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>
      </Card>
    </motion.div>
  );
}

export default PayrollPage;

