import { useState, useEffect } from 'react';
import { expenseService } from '../services/api';
import { X } from 'lucide-react';

export default function ExpenseForm({ expense, onSuccess, onCancel }) {
    const isEditing = Boolean(expense?.id);
    
    const [formData, setFormData] = useState({
        concept: '',
        note: '',
        amount: '',
        date: new Date().toISOString().slice(0, 16), // datetime-local format
    });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    // Initialize form with expense data when editing
    useEffect(() => {
        if (expense) {
            // Handle both expenseDate (from backend) and date formats
            const dateValue = expense.expenseDate || expense.date;
            let expenseDate;
            
            if (Array.isArray(dateValue)) {
                // Java LocalDateTime array format [year, month, day, hour, minute]
                const [year, month, day, hour = 0, minute = 0] = dateValue;
                expenseDate = new Date(year, month - 1, day, hour, minute).toISOString().slice(0, 16);
            } else if (dateValue) {
                expenseDate = new Date(dateValue).toISOString().slice(0, 16);
            } else {
                expenseDate = new Date().toISOString().slice(0, 16);
            }
            
            setFormData({
                concept: expense.concept || '',
                note: expense.note || '',
                amount: expense.amount?.toString() || '',
                date: expenseDate,
            });
        }
    }, [expense]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            // Convert datetime-local to ISO format for backend
            const dateTime = new Date(formData.date).toISOString();

            const expenseData = {
                concept: formData.concept,
                note: formData.note,
                expenseDate: dateTime,  // Backend expects expenseDate, not date
                amount: parseFloat(formData.amount)
            };

            let response;
            if (isEditing) {
                response = await expenseService.update(expense.id, {
                    ...expenseData,
                    id: expense.id
                });
            } else {
                response = await expenseService.create(expenseData);
            }
            onSuccess(response.data, isEditing);
        } catch (err) {
            console.error(`Error ${isEditing ? 'updating' : 'creating'} expense:`, err);
            setError(err.response?.data?.error || `Error al ${isEditing ? 'actualizar' : 'crear'} el gasto`);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
            <div className="bg-white rounded-lg shadow-xl w-full max-w-md overflow-hidden">
                <div className="flex justify-between items-center p-4 border-b">
                    <h2 className="text-xl font-semibold text-gray-800">
                        {isEditing ? 'Editar Gasto' : 'Nuevo Gasto'}
                    </h2>
                    <button onClick={onCancel} className="text-gray-500 hover:text-gray-700">
                        <X size={24} />
                    </button>
                </div>

                <form onSubmit={handleSubmit} className="p-4 space-y-4">
                    {error && (
                        <div className="bg-red-50 text-red-600 p-3 rounded-md text-sm">
                            {error}
                        </div>
                    )}

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Concepto</label>
                        <input
                            type="text"
                            value={formData.concept}
                            onChange={(e) => setFormData({ ...formData, concept: e.target.value })}
                            required
                            minLength={3}
                            maxLength={128}
                            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                            placeholder="Ej: Almuerzo de negocios"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Nota</label>
                        <textarea
                            value={formData.note}
                            onChange={(e) => setFormData({ ...formData, note: e.target.value })}
                            required
                            minLength={3}
                            maxLength={255}
                            rows={3}
                            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                            placeholder="Detalles adicionales..."
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Monto</label>
                        <input
                            type="number"
                            step="0.01"
                            value={formData.amount}
                            onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
                            required
                            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                            placeholder="0.00"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Fecha y Hora</label>
                        <input
                            type="datetime-local"
                            value={formData.date}
                            onChange={(e) => setFormData({ ...formData, date: e.target.value })}
                            required
                            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                    </div>

                    <div className="flex justify-end space-x-3 pt-4">
                        <button
                            type="button"
                            onClick={onCancel}
                            className="px-4 py-2 text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200 transition-colors"
                        >
                            Cancelar
                        </button>
                        <button
                            type="submit"
                            disabled={loading}
                            className="px-4 py-2 text-white bg-blue-600 rounded-md hover:bg-blue-700 transition-colors disabled:bg-blue-400"
                        >
                            {loading ? 'Guardando...' : isEditing ? 'Actualizar Gasto' : 'Guardar Gasto'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
