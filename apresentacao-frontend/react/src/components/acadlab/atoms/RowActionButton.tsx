import { cn } from "@/lib/utils";

export function RowActionButton({
  children,
  tone = "info",
  onClick,
  className,
}: {
  children: React.ReactNode;
  tone?: "info" | "danger" | "neutral";
  onClick?: () => void;
  className?: string;
}) {
  const map = {
    info: "bg-primary-soft text-primary hover:bg-primary/15",
    danger: "bg-destructive-soft text-destructive hover:bg-destructive/15",
    neutral: "bg-muted text-foreground hover:bg-border",
  } as const;
  return (
    <button
      type="button"
      onClick={onClick}
      className={cn(
        "inline-flex h-7 items-center rounded-md px-2.5 text-[11px] font-medium transition-colors",
        map[tone],
        className,
      )}
    >
      {children}
    </button>
  );
}
