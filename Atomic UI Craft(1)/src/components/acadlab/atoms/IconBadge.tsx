import { cn } from "@/lib/utils";

export function IconBadge({
  tone = "info",
  children,
  className,
}: {
  tone?: "info" | "success" | "warning" | "danger";
  children: React.ReactNode;
  className?: string;
}) {
  const map = {
    info: "bg-primary-soft text-primary",
    success: "bg-success-soft text-success",
    warning: "bg-warning-soft text-warning",
    danger: "bg-destructive-soft text-destructive",
  } as const;
  return (
    <div
      className={cn(
        "inline-flex h-9 w-9 items-center justify-center rounded-lg",
        map[tone],
        className,
      )}
    >
      {children}
    </div>
  );
}
