import { Trash2, Edit2, Calendar, FileText, DollarSign } from 'lucide-react';

export default function ExpenseList({ expenses, onDelete, onEdit }) {
    if (!expenses || expenses.length === 0) {
        return (
            <div className="text-center py-10 bg-gray-50 rounded-lg border border-gray-200">
                <p className="text-gray-500">No hay gastos registrados aún.</p>
            </div>
        );
    }

    const formatDate = (dateString) => {
        if (!dateString) return 'Fecha no disponible';
        
        try {
            // Handle array format from Java LocalDateTime [year, month, day, hour, minute, second, nano]
            if (Array.isArray(dateString)) {
                const [year, month, day, hour = 0, minute = 0] = dateString;
                const date = new Date(year, month - 1, day, hour, minute);
                return date.toLocaleString('es-ES', {
                    year: 'numeric',
                    month: 'short',
                    day: 'numeric',
                    hour: '2-digit',
                    minute: '2-digit'
                });
            }
            
            // Handle ISO string or other string formats
            const date = new Date(dateString);
            if (isNaN(date.getTime())) {
                return 'Fecha no válida';
            }
            return date.toLocaleString('es-ES', {
                year: 'numeric',
                month: 'short',
                day: 'numeric',
                hour: '2-digit',
                minute: '2-digit'
            });
        } catch {
            return 'Fecha no válida';
        }
    };

    return (
        <div className="overflow-hidden bg-white shadow sm:rounded-md">
            <ul className="divide-y divide-gray-200">
                {expenses.map((expense) => (
                    <li key={expense.id}>
                        <div className="px-4 py-4 sm:px-6 hover:bg-gray-50 transition-colors">
                            <div className="flex items-center justify-between">
                                <div className="flex flex-col flex-1">
                                    <p className="text-sm font-medium text-blue-600 truncate">{expense.concept}</p>
                                    {expense.note && (
                                        <p className="mt-1 text-sm text-gray-500 flex items-center">
                                            <FileText className="h-4 w-4 mr-1" />
                                            {expense.note}
                                        </p>
                                    )}
                                    <div className="mt-2 flex items-center text-sm text-gray-500">
                                        <Calendar className="flex-shrink-0 mr-1.5 h-4 w-4 text-gray-400" />
                                        <p>{formatDate(expense.expenseDate || expense.date)}</p>
                                    </div>
                                </div>
                                <div className="flex items-center">
                                    <div className="flex items-center text-sm text-gray-900 font-bold mr-6">
                                        <DollarSign className="h-4 w-4 text-gray-400" />
                                        {expense.amount?.toFixed(2) || '0.00'}
                                    </div>
                                    <div className="flex space-x-2">
                                        <button
                                            onClick={() => onEdit && onEdit(expense)}
                                            className="text-gray-400 hover:text-blue-500 transition-colors"
                                            title="Editar"
                                        >
                                            <Edit2 className="h-5 w-5" />
                                        </button>
                                        <button
                                            onClick={() => onDelete(expense.id)}
                                            className="text-gray-400 hover:text-red-500 transition-colors"
                                            title="Eliminar"
                                        >
                                            <Trash2 className="h-5 w-5" />
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </li>
                ))}
            </ul>
        </div>
    );
}
