import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import { ArrowRight, User, Mail, Lock, Check, BarChart3, ArrowLeft } from 'lucide-react';
import { useAuthStore } from '../store/authStore';
import { authService } from '../services/api';
import Button from '../components/Button';
import Input from '../components/Input';
import Card from '../components/Card';

function SignupPage() {
  const navigate = useNavigate();
  const { login } = useAuthStore();
  const [formData, setFormData] = useState({
    name: '',
    surname: '',
    email: '',
    password: '',
    confirmPassword: '',
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
    if (!formData.name) newErrors.name = 'Nombre requerido';
    if (!formData.surname) newErrors.surname = 'Apellido requerido';
    if (!formData.email) newErrors.email = 'Email requerido';
    if (!formData.password) newErrors.password = 'Contraseña requerida';
    if (formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = 'Las contraseñas no coinciden';
    }
    if (formData.password && formData.password.length < 6) {
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
        name: formData.name,
        surname: formData.surname,
        email: formData.email,
        password: formData.password,
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
        staggerChildren: 0.08,
        delayChildren: 0.2,
      },
    },
  };

  const itemVariants = {
    hidden: { opacity: 0, y: 20 },
    visible: { opacity: 1, y: 0, transition: { duration: 0.4 } },
  };

  const benefits = [
    'Gestión completa de gastos',
    'Informes en tiempo real',
    'Control de nóminas',
    'Seguridad empresarial',
  ];

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-white to-violet-50 overflow-hidden">
      {/* Background decorations */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        <div className="absolute -top-40 -left-40 w-96 h-96 bg-gradient-to-br from-violet-400/20 to-purple-500/20 rounded-full blur-3xl" />
        <div className="absolute top-1/3 -right-40 w-80 h-80 bg-gradient-to-br from-pink-400/15 to-rose-500/15 rounded-full blur-3xl" />
        <div className="absolute -bottom-40 left-1/3 w-72 h-72 bg-gradient-to-br from-blue-400/15 to-indigo-500/15 rounded-full blur-3xl" />
        
        {/* Decorative grid pattern */}
        <div className="absolute inset-0 bg-[linear-gradient(rgba(139,92,246,0.03)_1px,transparent_1px),linear-gradient(90deg,rgba(139,92,246,0.03)_1px,transparent_1px)] bg-[size:60px_60px]" />
      </div>

      <div className="relative min-h-screen flex flex-col lg:flex-row">
        {/* Left section - Benefits */}
        <motion.div 
          initial={{ opacity: 0, x: -50 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ duration: 0.8 }}
          className="lg:w-1/2 xl:w-2/5 p-6 sm:p-10 lg:p-16 flex flex-col justify-center"
        >
          {/* Back to login */}
          <Link 
            to="/login" 
            className="inline-flex items-center gap-2 text-slate-600 hover:text-slate-800 transition-colors mb-8 w-fit"
          >
            <ArrowLeft className="h-4 w-4" />
            Volver al inicio
          </Link>

          {/* Logo */}
          <div className="flex items-center gap-3 mb-8">
            <div className="gradient-secondary rounded-xl p-3 shadow-lg shadow-purple-500/25">
              <BarChart3 className="h-7 w-7 text-white" />
            </div>
            <span className="text-2xl font-bold text-slate-800">Bizflow ERP</span>
          </div>

          {/* Hero text */}
          <div className="mb-8">
            <h1 className="text-3xl sm:text-4xl lg:text-5xl font-bold text-slate-800 leading-tight mb-4">
              Únete a <span className="bg-clip-text text-transparent bg-gradient-to-r from-violet-600 to-purple-600">miles de empresas</span>
            </h1>
            <p className="text-lg text-slate-600 max-w-md leading-relaxed">
              Crea tu cuenta gratis y comienza a optimizar la gestión financiera de tu empresa hoy mismo.
            </p>
          </div>

          {/* Benefits list */}
          <div className="space-y-4">
            {benefits.map((benefit, index) => (
              <motion.div
                key={benefit}
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ delay: 0.4 + index * 0.1 }}
                className="flex items-center gap-3"
              >
                <div className="flex-shrink-0 w-6 h-6 rounded-full bg-gradient-to-r from-emerald-400 to-green-500 flex items-center justify-center">
                  <Check className="h-3.5 w-3.5 text-white" />
                </div>
                <span className="text-slate-700">{benefit}</span>
              </motion.div>
            ))}
          </div>

          {/* Testimonial */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.8 }}
            className="hidden lg:block mt-12 p-6 rounded-2xl bg-white/60 backdrop-blur-sm border border-slate-200/60"
          >
            <p className="text-slate-600 italic mb-4">
              "Bizflow ERP ha transformado completamente la manera en que gestionamos nuestros gastos. Ahorramos horas cada semana."
            </p>
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-full bg-gradient-to-r from-violet-500 to-purple-600 flex items-center justify-center text-white font-semibold">
                MC
              </div>
              <div>
                <p className="font-semibold text-slate-800">María Castillo</p>
                <p className="text-sm text-slate-500">CFO, TechCorp</p>
              </div>
            </div>
          </motion.div>
        </motion.div>

        {/* Right section - Signup Form */}
        <motion.div 
          variants={containerVariants}
          initial="hidden"
          animate="visible"
          className="lg:w-1/2 xl:w-3/5 p-6 sm:p-10 lg:p-16 flex items-center justify-center"
        >
          <div className="w-full max-w-lg">
            {/* Form Card */}
            <Card variant="glass" className="p-6 sm:p-8 lg:p-10">
              <motion.div variants={itemVariants} className="text-center mb-8">
                <h2 className="text-2xl sm:text-3xl font-bold text-slate-800 mb-2">
                  Crear cuenta gratuita
                </h2>
                <p className="text-slate-500">
                  Completa tus datos para comenzar
                </p>
              </motion.div>

              <motion.form
                variants={containerVariants}
                onSubmit={handleSubmit}
                className="space-y-4"
              >
                {/* Name fields in row */}
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  <motion.div variants={itemVariants}>
                    <Input
                      label="Nombre"
                      type="text"
                      name="name"
                      value={formData.name}
                      onChange={handleChange}
                      placeholder="Juan"
                      error={errors.name}
                      icon={User}
                      required
                    />
                  </motion.div>
                  <motion.div variants={itemVariants}>
                    <Input
                      label="Apellido"
                      type="text"
                      name="surname"
                      value={formData.surname}
                      onChange={handleChange}
                      placeholder="García"
                      error={errors.surname}
                      icon={User}
                      required
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
                    icon={Mail}
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
                    icon={Lock}
                    required
                  />
                  <p className="mt-1.5 text-xs text-slate-500">Mínimo 6 caracteres</p>
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
                    icon={Lock}
                    required
                  />
                </motion.div>

                {/* Terms checkbox */}
                <motion.div variants={itemVariants}>
                  <label className="flex items-start gap-3 cursor-pointer">
                    <input
                      type="checkbox"
                      required
                      className="mt-1 w-4 h-4 rounded border-slate-300 text-violet-600 focus:ring-violet-500/20"
                    />
                    <span className="text-sm text-slate-600">
                      Acepto los{' '}
                      <button type="button" className="text-violet-600 hover:text-violet-700 font-medium">
                        Términos de Servicio
                      </button>
                      {' '}y la{' '}
                      <button type="button" className="text-violet-600 hover:text-violet-700 font-medium">
                        Política de Privacidad
                      </button>
                    </span>
                  </label>
                </motion.div>

                {errors.submit && (
                  <motion.div
                    variants={itemVariants}
                    className="flex items-center gap-2 text-sm text-rose-600 bg-rose-50 border border-rose-200 rounded-xl p-4"
                  >
                    <svg className="h-5 w-5 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                      <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
                    </svg>
                    {errors.submit}
                  </motion.div>
                )}

                <motion.div variants={itemVariants}>
                  <Button
                    type="submit"
                    isLoading={isLoading}
                    size="lg"
                    className="w-full bg-gradient-to-r from-violet-500 via-purple-500 to-fuchsia-500"
                  >
                    {isLoading ? 'Creando cuenta...' : 'Crear cuenta gratis'}
                    {!isLoading && <ArrowRight className="h-5 w-5" />}
                  </Button>
                </motion.div>
              </motion.form>
            </Card>

            {/* Login Link */}
            <motion.p 
              variants={itemVariants} 
              className="mt-8 text-center text-slate-600"
            >
              ¿Ya tienes cuenta?{' '}
              <Link
                to="/login"
                className="text-violet-600 hover:text-violet-700 font-semibold transition-colors"
              >
                Inicia sesión
              </Link>
            </motion.p>
          </div>
        </motion.div>
      </div>
    </div>
  );
}

export default SignupPage;
