import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import { Mail, Lock, ArrowRight, BarChart3, Shield, Zap } from 'lucide-react';
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
        { 
          id: response.data.id,
          email: response.data.email,
          roleId: response.data.roleId,
          roleName: response.data.roleName
        },
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

  const features = [
    { icon: BarChart3, title: 'Análisis en tiempo real', desc: 'Visualiza tus gastos al instante' },
    { icon: Shield, title: 'Seguridad empresarial', desc: 'Protección de datos garantizada' },
    { icon: Zap, title: 'Automatización', desc: 'Procesos simplificados y rápidos' },
  ];

  const stats = [
    { value: '500+', label: 'Empresas activas' },
    { value: '99.9%', label: 'Uptime garantizado' },
    { value: '24/7', label: 'Soporte técnico' },
  ];

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-white to-blue-50 overflow-hidden">
      {/* Background decorations */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        <div className="absolute -top-40 -right-40 w-96 h-96 bg-gradient-to-br from-blue-400/20 to-indigo-500/20 rounded-full blur-3xl" />
        <div className="absolute top-1/2 -left-40 w-80 h-80 bg-gradient-to-br from-violet-400/15 to-purple-500/15 rounded-full blur-3xl" />
        <div className="absolute -bottom-40 right-1/3 w-72 h-72 bg-gradient-to-br from-emerald-400/15 to-teal-500/15 rounded-full blur-3xl" />
        
        {/* Decorative grid pattern */}
        <div className="absolute inset-0 bg-[linear-gradient(rgba(99,102,241,0.03)_1px,transparent_1px),linear-gradient(90deg,rgba(99,102,241,0.03)_1px,transparent_1px)] bg-[size:60px_60px]" />
      </div>

      <div className="relative min-h-screen flex flex-col lg:flex-row">
        {/* Left section - Hero/Branding */}
        <motion.div 
          initial={{ opacity: 0, x: -50 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ duration: 0.8 }}
          className="lg:w-1/2 xl:w-3/5 p-6 sm:p-10 lg:p-16 flex flex-col justify-center"
        >
          {/* Logo */}
          <div className="flex items-center gap-3 mb-8 lg:mb-12">
            <div className="gradient-primary rounded-xl p-3 shadow-lg shadow-blue-500/25">
              <BarChart3 className="h-7 w-7 text-white" />
            </div>
            <span className="text-2xl font-bold text-slate-800">Bizflow ERP</span>
          </div>

          {/* Hero text */}
          <div className="mb-8 lg:mb-12">
            <h1 className="text-3xl sm:text-4xl lg:text-5xl xl:text-6xl font-bold text-slate-800 leading-tight mb-4 lg:mb-6">
              Control total de tus{' '}
              <span className="gradient-text">gastos empresariales</span>
            </h1>
            <p className="text-lg sm:text-xl text-slate-600 max-w-xl leading-relaxed">
              La plataforma inteligente que transforma la gestión financiera de tu empresa. 
              Simple, segura y potente.
            </p>
          </div>

          {/* Features - Hidden on mobile, visible on lg+ */}
          <div className="hidden lg:grid grid-cols-1 xl:grid-cols-3 gap-6 mb-12">
            {features.map((feature, index) => (
              <motion.div
                key={feature.title}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.4 + index * 0.1 }}
                className="flex items-start gap-4 p-4 rounded-xl bg-white/60 backdrop-blur-sm border border-slate-200/60 hover:shadow-soft transition-all duration-300"
              >
                <div className="flex-shrink-0 p-2.5 rounded-lg bg-gradient-to-br from-blue-50 to-indigo-100">
                  <feature.icon className="h-5 w-5 text-blue-600" />
                </div>
                <div>
                  <h3 className="font-semibold text-slate-800 text-sm">{feature.title}</h3>
                  <p className="text-slate-500 text-xs mt-0.5">{feature.desc}</p>
                </div>
              </motion.div>
            ))}
          </div>

          {/* Stats */}
          <div className="hidden sm:flex items-center gap-8 lg:gap-12">
            {stats.map((stat, index) => (
              <motion.div
                key={stat.label}
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                transition={{ delay: 0.7 + index * 0.1 }}
                className="text-center lg:text-left"
              >
                <div className="text-2xl lg:text-3xl font-bold gradient-text">{stat.value}</div>
                <div className="text-xs lg:text-sm text-slate-500">{stat.label}</div>
              </motion.div>
            ))}
          </div>
        </motion.div>

        {/* Right section - Login Form */}
        <motion.div 
          variants={containerVariants}
          initial="hidden"
          animate="visible"
          className="lg:w-1/2 xl:w-2/5 p-6 sm:p-10 lg:p-16 flex items-center justify-center"
        >
          <div className="w-full max-w-md">
            {/* Form Card */}
            <Card variant="glass" className="p-6 sm:p-8 lg:p-10">
              <motion.div variants={itemVariants} className="text-center mb-8">
                <h2 className="text-2xl sm:text-3xl font-bold text-slate-800 mb-2">
                  Bienvenido de nuevo
                </h2>
                <p className="text-slate-500">
                  Inicia sesión para continuar
                </p>
              </motion.div>

              <motion.form
                variants={containerVariants}
                onSubmit={handleSubmit}
                className="space-y-5"
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
                </motion.div>

                {/* Remember me & Forgot password */}
                <motion.div variants={itemVariants} className="flex items-center justify-between text-sm">
                  <label className="flex items-center gap-2 cursor-pointer">
                    <input
                      type="checkbox"
                      className="w-4 h-4 rounded border-slate-300 text-blue-600 focus:ring-blue-500/20"
                    />
                    <span className="text-slate-600">Recordarme</span>
                  </label>
                  <button 
                    type="button"
                    className="text-blue-600 hover:text-blue-700 font-medium transition-colors"
                  >
                    ¿Olvidaste tu contraseña?
                  </button>
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
                    className="w-full"
                  >
                    {isLoading ? 'Iniciando...' : 'Iniciar Sesión'}
                    {!isLoading && <ArrowRight className="h-5 w-5" />}
                  </Button>
                </motion.div>
              </motion.form>

              {/* Divider */}
              <motion.div variants={itemVariants} className="relative my-8">
                <div className="absolute inset-0 flex items-center">
                  <div className="w-full border-t border-slate-200"></div>
                </div>
                <div className="relative flex justify-center text-sm">
                  <span className="px-4 bg-white/80 text-slate-500">o continúa con</span>
                </div>
              </motion.div>

              {/* Social login buttons */}
              <motion.div variants={itemVariants} className="grid grid-cols-2 gap-4">
                <Button variant="secondary" className="w-full">
                  <svg className="h-5 w-5" viewBox="0 0 24 24">
                    <path fill="currentColor" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                    <path fill="currentColor" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                    <path fill="currentColor" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
                    <path fill="currentColor" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
                  </svg>
                  Google
                </Button>
                <Button variant="secondary" className="w-full">
                  <svg className="h-5 w-5" fill="currentColor" viewBox="0 0 24 24">
                    <path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"/>
                  </svg>
                  GitHub
                </Button>
              </motion.div>
            </Card>

            {/* Signup Link */}
            <motion.p 
              variants={itemVariants} 
              className="mt-8 text-center text-slate-600"
            >
              ¿No tienes cuenta?{' '}
              <Link
                to="/signup"
                className="text-blue-600 hover:text-blue-700 font-semibold transition-colors inline-flex items-center gap-1"
              >
                Crear cuenta gratis
                <ArrowRight className="h-4 w-4" />
              </Link>
            </motion.p>
          </div>
        </motion.div>
      </div>
    </div>
  );
}

export default LoginPage;
