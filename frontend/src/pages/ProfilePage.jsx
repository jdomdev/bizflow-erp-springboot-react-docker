import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { User, Mail, Shield, Save, X, Briefcase } from 'lucide-react';
import Card from '../components/Card';
import Button from '../components/Button';
import { userService } from '../services/api';
import { useAuthStore } from '../store/authStore';

function ProfilePage() {
    const [profile, setProfile] = useState(null);
    const [isEditing, setIsEditing] = useState(false);
    const [formData, setFormData] = useState({
        email: '',
        password: '',
        confirmPassword: ''
    });
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    useEffect(() => {
        loadProfile();
    }, []);

    const loadProfile = async () => {
        try {
            const response = await userService.getProfile();
            setProfile(response.data);
            setFormData(prev => ({ ...prev, email: response.data.email }));
        } catch (err) {
            setError('Error al cargar el perfil');
            console.error(err);
        } finally {
            setIsLoading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        if (formData.password && formData.password !== formData.confirmPassword) {
            setError('Las contraseñas no coinciden');
            return;
        }

        try {
            const updateData = {
                email: formData.email,
                ...(formData.password ? { password: formData.password } : {})
            };

            const response = await userService.updateProfile(updateData);
            setProfile(response.data);
            setSuccess('Perfil actualizado correctamente');
            setIsEditing(false);
            setFormData(prev => ({ ...prev, password: '', confirmPassword: '' }));
        } catch (err) {
            setError('Error al actualizar el perfil');
            console.error(err);
        }
    };

    if (isLoading) {
        return <div className="text-center text-slate-400 mt-8">Cargando perfil...</div>;
    }

    return (
        <div className="space-y-8 animate-fade-in-up max-w-2xl mx-auto">
            <motion.div
                initial={{ opacity: 0, y: -20 }}
                animate={{ opacity: 1, y: 0 }}
            >
                <h1 className="gradient-text text-4xl font-bold mb-2">Mi Perfil</h1>
                <p className="text-slate-400">Gestiona tu información personal</p>
            </motion.div>

            <Card className="p-8">
                <div className="flex items-center space-x-4 mb-8">
                    <div className="h-20 w-20 rounded-full bg-gradient-to-br from-blue-500 to-indigo-600 flex items-center justify-center">
                        <User className="h-10 w-10 text-white" />
                    </div>
                    <div>
                        <h2 className="text-2xl font-bold text-white">
                            {profile?.name && profile?.surname 
                                ? `${profile.name} ${profile.surname}` 
                                : profile?.email}
                        </h2>
                        <p className="text-slate-400 text-sm">{profile?.email}</p>
                        <div className="flex gap-2 mt-2">
                            {profile?.roleDtos?.map(role => (
                                <span key={role.id} className="px-3 py-1 rounded-full text-xs font-medium bg-blue-500/20 text-blue-300 border border-blue-500/30">
                                    {role.name?.replace('ROLE_', '')}
                                </span>
                            )) || profile?.roleIds?.map(roleId => (
                                <span key={roleId} className="px-3 py-1 rounded-full text-xs font-medium bg-slate-700 text-slate-300 border border-slate-600">
                                    {roleId === 1 ? 'ADMIN' : roleId === 2 ? 'USER' : roleId === 3 ? 'MANAGER' : `Role ${roleId}`}
                                </span>
                            ))}
                        </div>
                    </div>
                </div>

                {isEditing ? (
                    <form onSubmit={handleSubmit} className="space-y-6">
                        <div>
                            <label htmlFor="profile-email" className="block text-sm font-medium text-slate-300 mb-2">
                                Email
                            </label>
                            <div className="relative">
                                <Mail className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-slate-400" />
                                <input
                                    id="profile-email"
                                    type="email"
                                    value={formData.email}
                                    onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                                    className="w-full bg-slate-800/50 border border-slate-700 rounded-lg py-2.5 pl-10 pr-4 text-white placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                                    required
                                />
                            </div>
                        </div>

                        <div>
                            <label htmlFor="profile-password" className="block text-sm font-medium text-slate-300 mb-2">
                                Nueva Contraseña (opcional)
                            </label>
                            <div className="relative">
                                <Shield className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-slate-400" />
                                <input
                                    id="profile-password"
                                    type="password"
                                    value={formData.password}
                                    onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                                    className="w-full bg-slate-800/50 border border-slate-700 rounded-lg py-2.5 pl-10 pr-4 text-white placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                                    placeholder="Dejar en blanco para mantener la actual"
                                />
                            </div>
                        </div>

                        {formData.password && (
                            <div>
                                <label htmlFor="profile-confirm-password" className="block text-sm font-medium text-slate-300 mb-2">
                                    Confirmar Contraseña
                                </label>
                                <div className="relative">
                                    <Shield className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-slate-400" />
                                    <input
                                        id="profile-confirm-password"
                                        type="password"
                                        value={formData.confirmPassword}
                                        onChange={(e) => setFormData({ ...formData, confirmPassword: e.target.value })}
                                        className="w-full bg-slate-800/50 border border-slate-700 rounded-lg py-2.5 pl-10 pr-4 text-white placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                                    />
                                </div>
                            </div>
                        )}

                        {error && (
                            <div className="p-3 rounded-lg bg-red-500/10 border border-red-500/20 text-red-400 text-sm">
                                {error}
                            </div>
                        )}

                        <div className="flex gap-4 pt-4">
                            <Button type="submit" variant="primary" className="flex-1">
                                <Save className="h-4 w-4 mr-2" />
                                Guardar Cambios
                            </Button>
                            <Button
                                type="button"
                                variant="secondary"
                                className="flex-1"
                                onClick={() => {
                                    setIsEditing(false);
                                    setFormData(prev => ({ ...prev, email: profile.email, password: '', confirmPassword: '' }));
                                    setError('');
                                }}
                            >
                                <X className="h-4 w-4 mr-2" />
                                Cancelar
                            </Button>
                        </div>
                    </form>
                ) : (
                    <div className="space-y-6">
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                            <div className="p-4 rounded-lg bg-slate-800/50 border border-slate-700">
                                <p className="text-sm text-slate-500 mb-1">Nombre</p>
                                <p className="text-slate-700 font-medium">{profile?.name || '-'}</p>
                            </div>
                            <div className="p-4 rounded-lg bg-slate-800/50 border border-slate-700">
                                <p className="text-sm text-slate-500 mb-1">Apellido</p>
                                <p className="text-slate-700 font-medium">{profile?.surname || '-'}</p>
                            </div>
                            <div className="p-4 rounded-lg bg-slate-800/50 border border-slate-700">
                                <p className="text-sm text-slate-500 mb-1">Email</p>
                                <p className="text-slate-700 font-medium">{profile?.email}</p>
                            </div>
                            <div className="p-4 rounded-lg bg-slate-800/50 border border-slate-700">
                                <p className="text-sm text-slate-500 mb-1">Contraseña</p>
                                <p className="text-slate-700 font-medium">••••••••</p>
                            </div>
                        </div>

                        {profile?.employeeId && (
                            <div className="p-4 rounded-lg bg-gradient-to-r from-emerald-500/10 to-teal-500/10 border border-emerald-500/20">
                                <div className="flex items-center gap-2 mb-3">
                                    <Briefcase className="h-5 w-5 text-emerald-500" />
                                    <p className="text-sm font-medium text-emerald-500">Empleado Vinculado</p>
                                </div>
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                    <div>
                                        <p className="text-xs text-slate-500">Nombre</p>
                                        <p className="text-emerald-700 font-medium">{profile.employeeName || '-'}</p>
                                    </div>
                                    <div>
                                        <p className="text-xs text-slate-500">Cargo</p>
                                        <p className="text-emerald-700 font-medium">{profile.employeePosition || '-'}</p>
                                    </div>
                                </div>
                            </div>
                        )}

                        {success && (
                            <div className="p-3 rounded-lg bg-green-500/10 border border-green-500/20 text-green-400 text-sm">
                                {success}
                            </div>
                        )}

                        <Button
                            onClick={() => setIsEditing(true)}
                            variant="primary"
                            className="w-full md:w-auto"
                        >
                            Editar Perfil
                        </Button>
                    </div>
                )}
            </Card>
        </div>
    );
}

export default ProfilePage;
