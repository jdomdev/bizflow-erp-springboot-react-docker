import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import { ArrowRight } from 'lucide-react';
import { useAuthStore } from '../store/authStore';
import { authService } from '../services/api';
import Button from '../components/Button';
import Input from '../components/Input';
import Card from '../components/Card';

function SignupPage() {
  const navigate = useNavigate();
  const { login } = useAuthStore();
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    confirmPassword: '',
    firstName: '',
    lastName: '',
  });
  const [errors, setErrors] = useState({});
  const [isLoading, setIsLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    setErrors((prev) => ({ ...prev, [name]: '' }));
  };

  const validateForm = () => {
    const newErrors = {};
    if (!formData.email) newErrors.email = 'Email requerido';
    if (!formData.password) newErrors.password = 'Contraseña requerida';
    if (formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = 'Las contraseñas no coinciden';
    }
    if (formData.password.length < 6) {
      newErrors.password = 'La contraseña debe tener mínimo 6 caracteres';
    }
    return newErrors;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const newErrors = validateForm();

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    setIsLoading(true);

    try {
      const response = await authService.signup({
        email: formData.email,
        password: formData.password,
        firstName: formData.firstName,
        lastName: formData.lastName,
      });
      login({ email: formData.email }, response.data.accessToken);
      navigate('/dashboard');
    } catch (error) {
      setErrors({
        submit: error.response?.data?.message || 'Error al registrarse',
      });
    } finally {
      setIsLoading(false);
    }
  };

  const containerVariants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: {
        staggerChildren: 0.1,
        delayChildren: 0.2,
      },
    },
  };

  const itemVariants = {
    hidden: { opacity: 0, y: 20 },
    visible: { opacity: 1, y: 0, transition: { duration: 0.5 } },
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900 flex items-center justify-center p-4">
      {/* Background decoration */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        <motion.div
          animate={{
            opacity: [0.3, 0.6, 0.3],
          }}
          transition={{ duration: 8, repeat: Infinity }}
          className="absolute top-20 -left-40 w-80 h-80 bg-purple-500 rounded-full mix-blend-multiply filter blur-3xl"
        />
        <motion.div
          animate={{
            opacity: [0.3, 0.6, 0.3],
          }}
          transition={{ duration: 8, repeat: Infinity, delay: 2 }}
          className="absolute bottom-20 -right-40 w-80 h-80 bg-pink-500 rounded-full mix-blend-multiply filter blur-3xl"
        />
      </div>

      <motion.div
        variants={containerVariants}
        initial="hidden"
        animate="visible"
        className="relative w-full max-w-md"
      >
        {/* Logo */}
        <motion.div variants={itemVariants} className="text-center mb-8">
          <h1 className="gradient-text text-3xl font-bold mb-2">Crear Cuenta</h1>
          <p className="text-slate-400">Únete a ExpenseNote hoy</p>
        </motion.div>

        {/* Signup Card */}
        <Card className="p-8">
          <motion.form
            variants={containerVariants}
            onSubmit={handleSubmit}
            className="space-y-4"
          >
            <div className="grid grid-cols-2 gap-4">
              <motion.div variants={itemVariants}>
                <Input
                  label="Nombre"
                  type="text"
                  name="firstName"
                  value={formData.firstName}
                  onChange={handleChange}
                  placeholder="Juan"
                  error={errors.firstName}
                />
              </motion.div>
              <motion.div variants={itemVariants}>
                <Input
                  label="Apellido"
                  type="text"
                  name="lastName"
                  value={formData.lastName}
                  onChange={handleChange}
                  placeholder="García"
                  error={errors.lastName}
                />
              </motion.div>
            </div>

            <motion.div variants={itemVariants}>
              <Input
                label="Correo Electrónico"
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                placeholder="correo@empresa.com"
                error={errors.email}
                required
              />
            </motion.div>

            <motion.div variants={itemVariants}>
              <Input
                label="Contraseña"
                type="password"
                name="password"
                value={formData.password}
                onChange={handleChange}
                placeholder="••••••••"
                error={errors.password}
                required
              />
            </motion.div>

            <motion.div variants={itemVariants}>
              <Input
                label="Confirmar Contraseña"
                type="password"
                name="confirmPassword"
                value={formData.confirmPassword}
                onChange={handleChange}
                placeholder="••••••••"
                error={errors.confirmPassword}
                required
              />
            </motion.div>

            {errors.submit && (
              <motion.p
                variants={itemVariants}
                className="text-sm text-red-400 bg-red-500/10 border border-red-500/20 rounded-lg p-3"
              >
                {errors.submit}
              </motion.p>
            )}

            <motion.div variants={itemVariants}>
              <Button
                type="submit"
                isLoading={isLoading}
                className="w-full"
              >
                Registrarse
                {!isLoading && <ArrowRight className="h-4 w-4" />}
              </Button>
            </motion.div>
          </motion.form>
        </Card>

        {/* Login Link */}
        <motion.p variants={itemVariants} className="mt-6 text-center text-slate-400">
          ¿Ya tienes cuenta?{' '}
          <Link
            to="/login"
            className="text-blue-400 hover:text-blue-300 font-medium transition-colors"
          >
            Inicia sesión
          </Link>
        </motion.p>
      </motion.div>
    </div>
  );
}

export default SignupPage;
