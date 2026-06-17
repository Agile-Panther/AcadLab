import type { ReactNode } from 'react';

type AlertVariant = 'success' | 'warning' | 'danger' | 'info';

const ICONS: Record<AlertVariant, string> = {
  success: '✓',
  warning: '⚠',
  danger: '⚠',
  info: 'ℹ',
};

interface AlertBoxProps {
  variant: AlertVariant;
  children: ReactNode;
}

export default function AlertBox({ variant, children }: AlertBoxProps) {
  return (
    <div className={`alert alert-${variant}`}>
      <span className="alert-icon">{ICONS[variant]}</span>
      <span>{children}</span>
    </div>
  );
}
