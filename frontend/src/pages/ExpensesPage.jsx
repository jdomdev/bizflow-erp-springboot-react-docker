import React from 'react';
import { motion } from 'framer-motion';
import Card from '../components/Card';

function ExpensesPage() {
  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      className="space-y-6"
    >
      <div>
        <h1 className="gradient-text text-4xl font-bold">Gastos</h1>
        <p className="mt-2 text-slate-400">Gestiona y visualiza todos tus gastos</p>
      </div>

      <Card className="p-8 text-center">
        <p className="text-slate-400">Página de gastos en construcción...</p>
      </Card>
    </motion.div>
  );
}

export default ExpensesPage;
