import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import { Mail, Lock, ArrowRight } from 'lucide-react';
import { useAuthStore } from '../store/authStore';
import { authService } from '../services/api';
import Button from '../components/Button';
import Input from '../components/Input';
import Card from '../components/Card';

function LoginPage() {
  const navigate = useNavigate();
  const { login } = useAuthStore();
  const [formData, setFormData] = useState({ email: '', password: '' });
  const [errors, setErrors] = useState({});
  const [isLoading, setIsLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    setErrors((prev) => ({ ...prev, [name]: '' }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);

    try {
      const response = await authService.login(formData.email, formData.password);
      login(
        { email: formData.email },
        response.data.accessToken
      );
      navigate('/dashboard');
    } catch (error) {
      setErrors({
        submit: error.response?.data?.message || 'Error al iniciar sesión',
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
          className="absolute top-20 -left-40 w-80 h-80 bg-blue-500 rounded-full mix-blend-multiply filter blur-3xl"
        />
        <motion.div
          animate={{
            opacity: [0.3, 0.6, 0.3],
          }}
          transition={{ duration: 8, repeat: Infinity, delay: 2 }}
          className="absolute bottom-20 -right-40 w-80 h-80 bg-purple-500 rounded-full mix-blend-multiply filter blur-3xl"
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
          <div className="flex justify-center mb-4">
            <div className="gradient-primary rounded-lg p-4 shadow-glow-lg">
              <svg
                className="h-8 w-8 text-white"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                />
              </svg>
            </div>
          </div>
          <h1 className="gradient-text text-4xl font-bold mb-2">ExpenseNote</h1>
          <p className="text-slate-400">Control de Gastos Empresariales</p>
        </motion.div>

        {/* Login Card */}
        <Card className="p-8">
          <motion.form
            variants={containerVariants}
            onSubmit={handleSubmit}
            className="space-y-6"
          >
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
                Iniciar Sesión
                {!isLoading && <ArrowRight className="h-4 w-4" />}
              </Button>
            </motion.div>
          </motion.form>
        </Card>

        {/* Signup Link */}
        <motion.p variants={itemVariants} className="mt-6 text-center text-slate-400">
          ¿No tienes cuenta?{' '}
          <Link
            to="/signup"
            className="text-blue-400 hover:text-blue-300 font-medium transition-colors"
          >
            Regístrate aquí
          </Link>
        </motion.p>
      </motion.div>
    </div>
  );
}

export default LoginPage;
