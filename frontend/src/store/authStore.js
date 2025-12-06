import { create } from 'zustand';
import { persist } from 'zustand/middleware';

export const useAuthStore = create(
  persist(
    (set) => ({
      user: null,
      token: null,
      isAuthenticated: false,

      setUser: (user) => set({ user, isAuthenticated: !!user }),
      setToken: (token) => set({ token, isAuthenticated: !!token }),
      
      login: (user, token) => set({
        user,
        token,
        isAuthenticated: true,
      }),

      logout: () => set({
        user: null,
        token: null,
        isAuthenticated: false,
      }),
    }),
    {
      name: 'auth-store',
    }
  )
);

export const useExpenseStore = create((set, get) => ({
  expenses: [],
  loading: false,
  error: null,

  setExpenses: (expenses) => set({ expenses }),
  addExpense: (expense) => set((state) => ({
    expenses: [expense, ...state.expenses],
  })),
  updateExpense: (id, updatedExpense) => set((state) => ({
    expenses: state.expenses.map((e) => (e.id === id ? updatedExpense : e)),
  })),
  removeExpense: (id) => set((state) => ({
    expenses: state.expenses.filter((e) => e.id !== id),
  })),
  setLoading: (loading) => set({ loading }),
  setError: (error) => set({ error }),
}));

export const usePayrollStore = create((set, get) => ({
  payrolls: [],
  loading: false,
  error: null,

  setPayrolls: (payrolls) => set({ payrolls }),
  addPayroll: (payroll) => set((state) => ({
    payrolls: [payroll, ...state.payrolls],
  })),
  updatePayroll: (id, updatedPayroll) => set((state) => ({
    payrolls: state.payrolls.map((p) => (p.id === id ? updatedPayroll : p)),
  })),
  removePayroll: (id) => set((state) => ({
    payrolls: state.payrolls.filter((p) => p.id !== id),
  })),
  setLoading: (loading) => set({ loading }),
  setError: (error) => set({ error }),
}));
