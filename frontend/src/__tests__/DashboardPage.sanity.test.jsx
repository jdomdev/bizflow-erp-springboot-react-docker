import React from 'react';
import { render, screen, waitFor, within } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';

vi.mock('../services/api', () => ({
  userService: {
    getProfile: vi.fn(),
  },
  expenseService: {
    getAll: vi.fn(),
  },
}));

import DashboardPage from '../pages/DashboardPage.jsx';
import { userService, expenseService } from '../services/api';

describe('DashboardPage sanity suite', () => {
  const currentMonthExpense = {
    id: 'exp-1',
    description: 'Factura de hotel',
    amount: 150,
    date: new Date().toISOString(),
  };

  const previousExpense = {
    id: 'exp-2',
    description: 'Cena de equipo',
    amount: 75,
    date: '2023-01-15T10:00:00.000Z',
  };

  const sampleProfile = {
    email: 'admin@example.com',
    roleIds: ['ADMIN', 'USER'],
  };

  beforeEach(() => {
    vi.clearAllMocks();

    expenseService.getAll.mockResolvedValue({
      data: [currentMonthExpense, previousExpense],
    });
    userService.getProfile.mockResolvedValue({
      data: sampleProfile,
    });
  });

  it('renders dashboard metrics and recent expenses', async () => {
    render(<DashboardPage />);

    expect(screen.getByText('Dashboard')).toBeInTheDocument();

    await waitFor(() => expect(expenseService.getAll).toHaveBeenCalledTimes(1));
    await waitFor(() => expect(userService.getProfile).toHaveBeenCalledTimes(1));

    await screen.findByText('Factura de hotel');
    expect(screen.getByText('Cena de equipo')).toBeInTheDocument();
    expect(screen.getByText('ADMIN, USER')).toBeInTheDocument();

    const totalCard = screen.getByText('Gasto Total').closest('div');
    const thisMonthCard = screen.getByText('Este Mes').closest('div');
    const averageCard = screen.getByText('Gasto Promedio').closest('div');

    expect(within(totalCard).getByText('$225.00')).toBeInTheDocument();
    expect(within(thisMonthCard).getByText('$150.00')).toBeInTheDocument();
    expect(within(averageCard).getByText('$112.50')).toBeInTheDocument();

    expect(screen.getByRole('button', { name: /Nuevo Gasto/i })).toBeInTheDocument();
  });
});
