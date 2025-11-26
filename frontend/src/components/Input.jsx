import React from 'react';
import { motion } from 'framer-motion';
import clsx from 'clsx';

function Input({
  label,
  error,
  isLoading = false,
  containerClassName,
  ...inputProps
}) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3 }}
      className={clsx('space-y-2', containerClassName)}
    >
      {label && (
        <label className="block text-sm font-medium text-slate-300">
          {label}
        </label>
      )}
      <input
        disabled={isLoading}
        className={clsx(
          'w-full rounded-lg border px-4 py-2 text-white placeholder-slate-500 transition-all',
          'bg-slate-800/50 backdrop-blur-sm',
          'border-slate-700 hover:border-slate-600 focus:border-blue-500',
          'focus:outline-none focus:ring-2 focus:ring-blue-500/20',
          'disabled:opacity-50 disabled:cursor-not-allowed',
          error && 'border-red-500 focus:ring-red-500/20'
        )}
        {...inputProps}
      />
      {error && (
        <motion.p
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          className="text-sm text-red-400"
        >
          {error}
        </motion.p>
      )}
    </motion.div>
  );
}

export default Input;
