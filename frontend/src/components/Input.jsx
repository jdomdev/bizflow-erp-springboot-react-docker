import React from 'react';
import { motion } from 'framer-motion';
import clsx from 'clsx';

function Input({
  label,
  error,
  isLoading = false,
  containerClassName,
  icon: Icon,
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
        <label className="block text-sm font-semibold text-slate-700">
          {label}
        </label>
      )}
      <div className="relative">
        {Icon && (
          <div className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400">
            <Icon className="h-5 w-5" />
          </div>
        )}
        <input
          disabled={isLoading}
          className={clsx(
            'w-full rounded-xl border-2 py-3 text-slate-700 placeholder-slate-400 transition-all duration-200',
            'bg-white/80 backdrop-blur-sm',
            'border-slate-200 hover:border-slate-300 focus:border-blue-500',
            'focus:outline-none focus:ring-4 focus:ring-blue-500/10',
            'disabled:opacity-50 disabled:cursor-not-allowed disabled:bg-slate-100',
            Icon ? 'pl-12 pr-4' : 'px-4',
            error && 'border-rose-400 focus:border-rose-500 focus:ring-rose-500/10'
          )}
          {...inputProps}
        />
      </div>
      {error && (
        <motion.p
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          className="text-sm text-rose-500 flex items-center gap-1"
        >
          <svg className="h-4 w-4" fill="currentColor" viewBox="0 0 20 20">
            <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
          </svg>
          {error}
        </motion.p>
      )}
    </motion.div>
  );
}

export default Input;
