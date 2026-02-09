import { Trash2, Edit2, Calendar, FileText, DollarSign, User } from 'lucide-react';

export default function ExpenseList({ expenses, onDelete, onEdit, searchTerm }) {
    if (!expenses || expenses.length === 0) {
        return (
            <div className="text-center py-10 bg-gray-50 dark:bg-slate-800 rounded-lg border border-gray-200 dark:border-slate-700">
                <DollarSign className="h-12 w-12 mx-auto mb-3 text-gray-400 dark:text-slate-500 opacity-50" />
                <p className="text-gray-500 dark:text-slate-400">
                    {searchTerm ? 'No se encontraron resultados' : 'No hay gastos registrados aún.'}
                </p>
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

    const formatCurrency = (amount) => {
        return new Intl.NumberFormat('es-ES', {
            style: 'currency',
            currency: 'EUR'
        }).format(amount || 0);
    };

    return (
        <>
            {/* Mobile Cards (visible < md) */}
            <div className="md:hidden space-y-3">
                {expenses.map((expense) => (
                    <div
                        key={expense.id}
                        className="bg-white dark:bg-slate-800 rounded-xl shadow-sm border border-gray-100 dark:border-slate-700 p-4"
                    >
                        {/* Header con concepto y monto */}
                        <div className="flex items-start justify-between mb-3">
                            <div className="flex-1 min-w-0 mr-3">
                                <h3 className="font-semibold text-blue-600 dark:text-blue-400 truncate">
                                    {expense.concept}
                                </h3>
                                {expense.expenseUserName && (
                                    <p className="text-xs text-gray-500 dark:text-slate-400 flex items-center gap-1 mt-1">
                                        <User className="h-3 w-3" />
                                        {expense.expenseUserName}
                                    </p>
                                )}
                            </div>
                            <span className="text-lg font-bold text-emerald-600 dark:text-emerald-400 whitespace-nowrap">
                                {formatCurrency(expense.amount)}
                            </span>
                        </div>

                        {/* Info */}
                        <div className="space-y-2 text-sm mb-3">
                            <div className="flex items-center gap-2 text-gray-600 dark:text-slate-300">
                                <Calendar className="h-4 w-4 text-gray-400 dark:text-slate-500 flex-shrink-0" />
                                <span>{formatDate(expense.expenseDate || expense.date)}</span>
                            </div>
                            {expense.note && (
                                <div className="flex items-start gap-2 text-gray-600 dark:text-slate-300">
                                    <FileText className="h-4 w-4 text-gray-400 dark:text-slate-500 flex-shrink-0 mt-0.5" />
                                    <span className="line-clamp-2">{expense.note}</span>
                                </div>
                            )}
                        </div>

                        {/* Actions */}
                        <div className="flex gap-2 pt-3 border-t border-gray-100 dark:border-slate-700">
                            <button
                                onClick={() => onEdit && onEdit(expense)}
                                className="flex-1 flex items-center justify-center gap-2 px-3 py-2 text-sm text-blue-600 dark:text-blue-400 bg-blue-50 dark:bg-blue-900/20 rounded-lg hover:bg-blue-100 dark:hover:bg-blue-900/30 transition-colors"
                            >
                                <Edit2 className="h-4 w-4" />
                                Editar
                            </button>
                            <button
                                onClick={() => onDelete(expense.id)}
                                className="flex-1 flex items-center justify-center gap-2 px-3 py-2 text-sm text-red-600 dark:text-red-400 bg-red-50 dark:bg-red-900/20 rounded-lg hover:bg-red-100 dark:hover:bg-red-900/30 transition-colors"
                            >
                                <Trash2 className="h-4 w-4" />
                                Eliminar
                            </button>
                        </div>
                    </div>
                ))}
            </div>

            {/* Desktop Table (visible >= md) */}
            <div className="hidden md:block bg-white dark:bg-slate-800 rounded-xl shadow-sm border border-gray-100 dark:border-slate-700 overflow-hidden">
                <div className="overflow-x-auto">
                    <table className="w-full">
                        <thead>
                            <tr className="bg-gray-50 dark:bg-slate-700 border-b border-gray-100 dark:border-slate-600">
                                <th className="text-left py-3 px-4 text-sm font-semibold text-gray-600 dark:text-slate-300">Concepto</th>
                                <th className="text-left py-3 px-4 text-sm font-semibold text-gray-600 dark:text-slate-300">Usuario</th>
                                <th className="text-left py-3 px-4 text-sm font-semibold text-gray-600 dark:text-slate-300">Fecha</th>
                                <th className="text-right py-3 px-4 text-sm font-semibold text-gray-600 dark:text-slate-300">Monto</th>
                                <th className="text-center py-3 px-4 text-sm font-semibold text-gray-600 dark:text-slate-300">Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            {expenses.map((expense) => (
                                <tr
                                    key={expense.id}
                                    className="border-b border-gray-50 dark:border-slate-700 hover:bg-gray-50 dark:hover:bg-slate-700 transition-colors"
                                >
                                    <td className="py-3 px-4">
                                        <div>
                                            <p className="font-medium text-blue-600 dark:text-blue-400">{expense.concept}</p>
                                            {expense.note && (
                                                <p className="text-sm text-gray-500 dark:text-slate-400 truncate max-w-xs">
                                                    {expense.note}
                                                </p>
                                            )}
                                        </div>
                                    </td>
                                    <td className="py-3 px-4 text-gray-600 dark:text-slate-300">
                                        {expense.expenseUserName || '-'}
                                    </td>
                                    <td className="py-3 px-4 text-gray-600 dark:text-slate-300">
                                        {formatDate(expense.expenseDate || expense.date)}
                                    </td>
                                    <td className="py-3 px-4 text-right font-semibold text-emerald-600 dark:text-emerald-400">
                                        {formatCurrency(expense.amount)}
                                    </td>
                                    <td className="py-3 px-4">
                                        <div className="flex items-center justify-center gap-2">
                                            <button
                                                onClick={() => onEdit && onEdit(expense)}
                                                className="p-2 text-gray-400 dark:text-slate-500 hover:text-blue-600 dark:hover:text-blue-400 hover:bg-blue-50 dark:hover:bg-blue-900/30 rounded-lg transition-colors"
                                                title="Editar"
                                            >
                                                <Edit2 className="h-4 w-4" />
                                            </button>
                                            <button
                                                onClick={() => onDelete(expense.id)}
                                                className="p-2 text-gray-400 dark:text-slate-500 hover:text-red-600 dark:hover:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/30 rounded-lg transition-colors"
                                                title="Eliminar"
                                            >
                                                <Trash2 className="h-4 w-4" />
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </>
    );
}
