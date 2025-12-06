import React, { useEffect, useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Users, Plus, Search, Edit2, Trash2, X } from 'lucide-react';
import Card from '../components/Card';
import Button from '../components/Button';
import Input from '../components/Input';
import { employeeService } from '../services/api';

function EmployeesPage() {
  const [employees, setEmployees] = useState([]);
  const [filteredEmployees, setFilteredEmployees] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [selectedEmployee, setSelectedEmployee] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    surname: '',
    email: '',
    birthDate: '',
    startDate: '',
    status: 'ACTIVE',
    position: { id: 1, name: 'Software Developer' } // Default position
  });
  const [errors, setErrors] = useState({});

  useEffect(() => {
    loadEmployees();
  }, []);

  useEffect(() => {
    // Filter employees based on search term
    const filtered = employees.filter((emp) =>
      emp.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      emp.surname.toLowerCase().includes(searchTerm.toLowerCase()) ||
      emp.email.toLowerCase().includes(searchTerm.toLowerCase())
    );
    setFilteredEmployees(filtered);
  }, [searchTerm, employees]);

  const loadEmployees = async () => {
    try {
      setIsLoading(true);
      const response = await employeeService.getAll();
      const data = response.data || [];
      setEmployees(data);
      setFilteredEmployees(data);
    } catch (error) {
      console.error('Error loading employees:', error);
      // Show error notification
    } finally {
      setIsLoading(false);
    }
  };

  const validateForm = () => {
    const newErrors = {};

    if (!formData.name || formData.name.trim().length < 3) {
      newErrors.name = 'Name must be at least 3 characters';
    }

    if (!formData.surname || formData.surname.trim().length < 2) {
      newErrors.surname = 'Surname must be at least 2 characters';
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!formData.email || !emailRegex.test(formData.email)) {
      newErrors.email = 'Please enter a valid email address';
    }

    if (!formData.birthDate) {
      newErrors.birthDate = 'Birth date is required';
    }

    if (!formData.startDate) {
      newErrors.startDate = 'Start date is required';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleOpenModal = (employee = null) => {
    if (employee) {
      setSelectedEmployee(employee);
      setFormData({
        name: employee.name,
        surname: employee.surname,
        email: employee.email,
        birthDate: employee.birthDate ? employee.birthDate.split('T')[0] : '',
        startDate: employee.startDate || '',
        status: employee.status || 'ACTIVE',
        position: employee.position || { id: 1, name: 'Software Developer' }
      });
    } else {
      setSelectedEmployee(null);
      setFormData({
        name: '',
        surname: '',
        email: '',
        birthDate: '',
        startDate: '',
        status: 'ACTIVE',
        position: { id: 1, name: 'Software Developer' }
      });
    }
    setErrors({});
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setSelectedEmployee(null);
    setFormData({
      name: '',
      surname: '',
      email: '',
      birthDate: '',
      startDate: '',
      status: 'ACTIVE',
      position: { id: 1, name: 'Software Developer' }
    });
    setErrors({});
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    try {
      // Convert dates to LocalDateTime format for birthDate
      const birthDateTime = new Date(formData.birthDate);
      birthDateTime.setHours(0, 0, 0, 0);
      
      const employeeData = {
        name: formData.name.trim(),
        surname: formData.surname.trim(),
        email: formData.email.trim(),
        birthDate: birthDateTime.toISOString().slice(0, 19),
        startDate: formData.startDate,
        status: formData.status,
        position: formData.position
      };

      if (selectedEmployee) {
        await employeeService.update(selectedEmployee.id, {
          ...employeeData,
          id: selectedEmployee.id
        });
      } else {
        await employeeService.create(employeeData);
      }

      await loadEmployees();
      handleCloseModal();
    } catch (error) {
      console.error('Error saving employee:', error);
      if (error.response?.data?.message) {
        setErrors({ submit: error.response.data.message });
      }
    }
  };

  const handleOpenDeleteModal = (employee) => {
    setSelectedEmployee(employee);
    setIsDeleteModalOpen(true);
  };

  const handleCloseDeleteModal = () => {
    setIsDeleteModalOpen(false);
    setSelectedEmployee(null);
  };

  const handleDelete = async () => {
    if (!selectedEmployee) return;

    try {
      await employeeService.delete(selectedEmployee.id);
      await loadEmployees();
      handleCloseDeleteModal();
    } catch (error) {
      console.error('Error deleting employee:', error);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    // Clear error for this field
    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: '' }));
    }
  };

  const getStatusBadgeColor = (status) => {
    switch (status) {
      case 'ACTIVE':
        return 'bg-green-500/20 text-green-400';
      case 'INACTIVE':
        return 'bg-yellow-500/20 text-yellow-400';
      case 'TERMINATED':
        return 'bg-red-500/20 text-red-400';
      default:
        return 'bg-gray-500/20 text-gray-400';
    }
  };

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      className="space-y-6"
    >
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="gradient-text text-4xl font-bold flex items-center gap-2">
            <Users className="w-10 h-10" />
            Employees
          </h1>
          <p className="mt-2 text-slate-400">Manage employee information</p>
        </div>
        <Button onClick={() => handleOpenModal()} className="flex items-center gap-2">
          <Plus className="w-5 h-5" />
          Add Employee
        </Button>
      </div>

      {/* Search Bar */}
      <Card className="p-4">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400 w-5 h-5" />
          <input
            type="text"
            placeholder="Search by name, surname, or email..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full pl-10 pr-4 py-2 bg-slate-800/50 border border-slate-700 rounded-lg focus:outline-none focus:border-cyan-500 text-white"
          />
        </div>
      </Card>

      {/* Employee List */}
      {isLoading ? (
        <Card className="p-8 text-center">
          <p className="text-slate-400">Loading employees...</p>
        </Card>
      ) : filteredEmployees.length === 0 ? (
        <Card className="p-8 text-center">
          <p className="text-slate-400">
            {searchTerm ? 'No employees found matching your search.' : 'No employees yet. Add your first employee!'}
          </p>
        </Card>
      ) : (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
          {filteredEmployees.map((employee) => (
            <motion.div
              key={employee.id}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -20 }}
            >
              <Card className="p-6 hover:border-cyan-500/50 transition-colors">
                <div className="flex justify-between items-start mb-4">
                  <div>
                    <h3 className="text-xl font-bold text-white">
                      {employee.name} {employee.surname}
                    </h3>
                    <p className="text-slate-400 text-sm">{employee.position?.name || 'N/A'}</p>
                  </div>
                  <span className={`px-2 py-1 rounded text-xs font-medium ${getStatusBadgeColor(employee.status)}`}>
                    {employee.status}
                  </span>
                </div>
                
                <div className="space-y-2 text-sm">
                  <p className="text-slate-300">
                    <span className="text-slate-500">Email:</span> {employee.email}
                  </p>
                  <p className="text-slate-300">
                    <span className="text-slate-500">Start Date:</span> {employee.startDate || 'N/A'}
                  </p>
                </div>

                <div className="flex gap-2 mt-4">
                  <Button
                    variant="secondary"
                    size="sm"
                    onClick={() => handleOpenModal(employee)}
                    className="flex items-center gap-1 flex-1"
                  >
                    <Edit2 className="w-4 h-4" />
                    Edit
                  </Button>
                  <Button
                    variant="danger"
                    size="sm"
                    onClick={() => handleOpenDeleteModal(employee)}
                    className="flex items-center gap-1"
                  >
                    <Trash2 className="w-4 h-4" />
                  </Button>
                </div>
              </Card>
            </motion.div>
          ))}
        </div>
      )}

      {/* Create/Edit Modal */}
      <AnimatePresence>
        {isModalOpen && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4 z-50"
            onClick={handleCloseModal}
          >
            <motion.div
              initial={{ scale: 0.95 }}
              animate={{ scale: 1 }}
              exit={{ scale: 0.95 }}
              onClick={(e) => e.stopPropagation()}
              className="bg-slate-900 rounded-xl p-6 max-w-md w-full border border-slate-800"
            >
              <div className="flex justify-between items-center mb-6">
                <h2 className="text-2xl font-bold text-white">
                  {selectedEmployee ? 'Edit Employee' : 'Add Employee'}
                </h2>
                <button
                  onClick={handleCloseModal}
                  className="text-slate-400 hover:text-white transition-colors"
                >
                  <X className="w-6 h-6" />
                </button>
              </div>

              <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                  <Input
                    label="Name"
                    name="name"
                    value={formData.name}
                    onChange={handleInputChange}
                    error={errors.name}
                    required
                  />
                </div>

                <div>
                  <Input
                    label="Surname"
                    name="surname"
                    value={formData.surname}
                    onChange={handleInputChange}
                    error={errors.surname}
                    required
                  />
                </div>

                <div>
                  <Input
                    label="Email"
                    name="email"
                    type="email"
                    value={formData.email}
                    onChange={handleInputChange}
                    error={errors.email}
                    required
                  />
                </div>

                <div>
                  <Input
                    label="Birth Date"
                    name="birthDate"
                    type="date"
                    value={formData.birthDate}
                    onChange={handleInputChange}
                    error={errors.birthDate}
                    required
                  />
                </div>

                <div>
                  <Input
                    label="Start Date"
                    name="startDate"
                    type="date"
                    value={formData.startDate}
                    onChange={handleInputChange}
                    error={errors.startDate}
                    required
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-slate-300 mb-2">
                    Status
                  </label>
                  <select
                    name="status"
                    value={formData.status}
                    onChange={handleInputChange}
                    className="w-full px-4 py-2 bg-slate-800/50 border border-slate-700 rounded-lg focus:outline-none focus:border-cyan-500 text-white"
                  >
                    <option value="ACTIVE">Active</option>
                    <option value="INACTIVE">Inactive</option>
                    <option value="TERMINATED">Terminated</option>
                  </select>
                </div>

                {errors.submit && (
                  <p className="text-red-400 text-sm">{errors.submit}</p>
                )}

                <div className="flex gap-3 pt-4">
                  <Button type="button" variant="secondary" onClick={handleCloseModal} className="flex-1">
                    Cancel
                  </Button>
                  <Button type="submit" className="flex-1">
                    {selectedEmployee ? 'Update' : 'Create'}
                  </Button>
                </div>
              </form>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Delete Confirmation Modal */}
      <AnimatePresence>
        {isDeleteModalOpen && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4 z-50"
            onClick={handleCloseDeleteModal}
          >
            <motion.div
              initial={{ scale: 0.95 }}
              animate={{ scale: 1 }}
              exit={{ scale: 0.95 }}
              onClick={(e) => e.stopPropagation()}
              className="bg-slate-900 rounded-xl p-6 max-w-md w-full border border-slate-800"
            >
              <h2 className="text-2xl font-bold text-white mb-4">Confirm Delete</h2>
              <p className="text-slate-300 mb-6">
                Are you sure you want to delete employee <strong>{selectedEmployee?.name} {selectedEmployee?.surname}</strong>? This action cannot be undone.
              </p>
              <div className="flex gap-3">
                <Button variant="secondary" onClick={handleCloseDeleteModal} className="flex-1">
                  Cancel
                </Button>
                <Button variant="danger" onClick={handleDelete} className="flex-1">
                  Delete
                </Button>
              </div>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </motion.div>
  );
}

export default EmployeesPage;
