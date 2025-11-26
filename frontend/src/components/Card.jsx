import React from 'react';
import { motion } from 'framer-motion';
import clsx from 'clsx';

function Card({ children, className, hover = true }) {
  return (
    <motion.div
      whileHover={hover ? { y: -4 } : {}}
      className={clsx(
        'rounded-xl border border-slate-700 bg-gradient-to-br from-slate-800/50 to-slate-900/50',
        'shadow-lg shadow-blue-500/10 backdrop-blur-sm transition-all',
        hover && 'hover:shadow-glow',
        className
      )}
    >
      {children}
    </motion.div>
  );
}

export default Card;
