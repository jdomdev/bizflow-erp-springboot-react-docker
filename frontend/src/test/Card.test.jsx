import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import Card from '../components/Card';

describe('Card Component', () => {
  it('renders children correctly', () => {
    render(
      <Card>
        <div>Card Content</div>
      </Card>
    );
    expect(screen.getByText('Card Content')).toBeInTheDocument();
  });

  it('applies default classes', () => {
    const { container } = render(<Card>Content</Card>);
    const card = container.firstChild;
    expect(card).toHaveClass('rounded-xl');
    expect(card).toHaveClass('border-slate-700');
    expect(card).toHaveClass('bg-gradient-to-br');
  });

  it('applies custom className', () => {
    const { container } = render(<Card className="custom-class">Content</Card>);
    const card = container.firstChild;
    expect(card).toHaveClass('custom-class');
    expect(card).toHaveClass('rounded-xl'); // Default classes should still be present
  });

  it('applies hover effect by default', () => {
    const { container } = render(<Card>Content</Card>);
    const card = container.firstChild;
    expect(card).toHaveClass('hover:shadow-glow');
  });

  it('removes hover effect when hover is false', () => {
    const { container } = render(<Card hover={false}>Content</Card>);
    const card = container.firstChild;
    expect(card).not.toHaveClass('hover:shadow-glow');
  });
});
