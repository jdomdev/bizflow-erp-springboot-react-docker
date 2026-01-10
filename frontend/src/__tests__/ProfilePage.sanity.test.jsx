import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { beforeEach, describe, expect, it, vi } from 'vitest';

vi.mock('../services/api', () => ({
  userService: {
    getProfile: vi.fn(),
    updateProfile: vi.fn(),
  },
}));

// eslint-disable-next-line import/first
import ProfilePage from '../pages/ProfilePage.jsx';
// eslint-disable-next-line import/first
import { userService } from '../services/api';

describe('ProfilePage sanity suite', () => {
  const sampleProfile = {
    email: 'admin@example.com',
    roleIds: ['ADMIN'],
  };

  beforeEach(() => {
    vi.clearAllMocks();
    userService.getProfile.mockResolvedValue({ data: sampleProfile });
  });

  it('displays the current profile once loaded', async () => {
    render(<ProfilePage />);

    expect(screen.getByText('Cargando perfil...')).toBeInTheDocument();

    await screen.findByText('Mi Perfil');
    expect(screen.getByRole('heading', { level: 2, name: 'admin@example.com' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Editar Perfil/i })).toBeInTheDocument();
  });

  it('updates the profile successfully', async () => {
    const user = userEvent.setup();
    const updatedProfile = {
      email: 'updated@example.com',
      roleIds: ['ADMIN'],
    };

    userService.updateProfile.mockResolvedValue({ data: updatedProfile });

    render(<ProfilePage />);

    await screen.findByRole('button', { name: /Editar Perfil/i });

    await user.click(screen.getByRole('button', { name: /Editar Perfil/i }));

    const emailInput = screen.getByLabelText(/^Email$/i);
    await user.clear(emailInput);
    await user.type(emailInput, 'updated@example.com');

    const passwordInput = screen.getByLabelText(/Nueva Contraseña/i);
    const updatedPassword = `sanity-${Date.now().toString(36)}`;
    await user.type(passwordInput, updatedPassword);

    const confirmInput = await screen.findByLabelText(/Confirmar Contraseña/i);
    await user.type(confirmInput, updatedPassword);

    await user.click(screen.getByRole('button', { name: /Guardar Cambios/i }));

    await waitFor(() =>
      expect(userService.updateProfile).toHaveBeenCalledWith({
        email: 'updated@example.com',
        password: updatedPassword,
      })
    );

    await screen.findByText('Perfil actualizado correctamente');
    expect(screen.getByRole('heading', { level: 2, name: 'updated@example.com' })).toBeInTheDocument();
  });
});
