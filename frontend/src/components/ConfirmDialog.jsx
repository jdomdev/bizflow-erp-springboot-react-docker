import React from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { AlertTriangle, X } from 'lucide-react';
import Card from './Card';
import Button from './Button';

/**
 * ConfirmDialog - Reusable confirmation modal component
 * @param {Object} props - Component props
 * @param {boolean} props.isOpen - Whether the dialog is open
 * @param {Function} props.onClose - Callback when dialog is closed
 * @param {Function} props.onConfirm - Callback when action is confirmed
 * @param {string} props.title - Dialog title
 * @param {string} props.message - Dialog message
 * @param {string} props.confirmText - Text for confirm button (default: "Confirmar")
 * @param {string} props.cancelText - Text for cancel button (default: "Cancelar")
 * @param {string} props.variant - Visual variant: 'danger', 'warning', 'info' (default: 'danger')
 */
function ConfirmDialog({
  isOpen,
  onClose,
  onConfirm,
  title = '¿Está seguro?',
  message = 'Esta acción no se puede deshacer.',
  confirmText = 'Confirmar',
  cancelText = 'Cancelar',
  variant = 'danger',
}) {
  const handleConfirm = () => {
    onConfirm();
    onClose();
  };

  const variantStyles = {
    danger: {
      icon: AlertTriangle,
      iconColor: 'text-red-400',
      iconBg: 'bg-red-500/20',
      buttonClass: 'bg-red-600 hover:bg-red-700',
    },
    warning: {
      icon: AlertTriangle,
      iconColor: 'text-yellow-400',
      iconBg: 'bg-yellow-500/20',
      buttonClass: 'bg-yellow-600 hover:bg-yellow-700',
    },
    info: {
      icon: AlertTriangle,
      iconColor: 'text-blue-400',
      iconBg: 'bg-blue-500/20',
      buttonClass: 'bg-blue-600 hover:bg-blue-700',
    },
  };

  const style = variantStyles[variant] || variantStyles.danger;
  const Icon = style.icon;

  return (
    <AnimatePresence>
      {isOpen && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50 p-4"
          onClick={onClose}
        >
          <motion.div
            initial={{ scale: 0.95, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            exit={{ scale: 0.95, opacity: 0 }}
            onClick={(e) => e.stopPropagation()}
            className="w-full max-w-md"
          >
            <Card className="p-6">
              <div className="flex items-start justify-between mb-4">
                <div className={`rounded-lg ${style.iconBg} p-3`}>
                  <Icon className={`h-6 w-6 ${style.iconColor}`} />
                </div>
                <button
                  onClick={onClose}
                  className="text-slate-400 hover:text-white transition"
                >
                  <X className="h-5 w-5" />
                </button>
              </div>

              <h2 className="text-xl font-bold text-white mb-2">{title}</h2>
              <p className="text-slate-400 mb-6">{message}</p>

              <div className="flex gap-3">
                <Button
                  type="button"
                  variant="secondary"
                  onClick={onClose}
                  className="flex-1"
                >
                  {cancelText}
                </Button>
                <button
                  onClick={handleConfirm}
                  className={`flex-1 px-6 py-2.5 rounded-lg font-semibold text-white transition ${style.buttonClass}`}
                >
                  {confirmText}
                </button>
              </div>
            </Card>
          </motion.div>
        </motion.div>
      )}
    </AnimatePresence>
  );
}

export default ConfirmDialog;
