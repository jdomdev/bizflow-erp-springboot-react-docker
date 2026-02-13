import React from 'react';
import { motion } from 'framer-motion';
import clsx from 'clsx';

function Card({ children, className, hover = true, variant = 'default' }) {
  const variants = {
    default: 'bg-white/80 dark:bg-slate-800/80 border-slate-200/60 dark:border-slate-700 shadow-soft',
    glass: 'bg-white/60 dark:bg-slate-800/60 backdrop-blur-xl border-white/30 dark:border-slate-700 shadow-soft-lg',
    solid: 'bg-white dark:bg-slate-800 border-slate-200 dark:border-slate-700 shadow-soft',
    gradient: 'bg-gradient-to-br from-white via-slate-50 to-blue-50/50 dark:from-slate-800 dark:via-slate-800 dark:to-slate-800 border-slate-200/60 dark:border-slate-700 shadow-soft-lg',
  };

  return (
    <motion.div
      whileHover={hover ? { y: -4, boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.08), 0 8px 10px -6px rgba(0, 0, 0, 0.05)' } : {}}
      className={clsx(
        'rounded-2xl border backdrop-blur-sm transition-all duration-300',
        variants[variant],
        className
      )}
    >
      {children}
    </motion.div>
  );
}

export default Card;
