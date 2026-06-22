import { cn } from "@/lib/utils";

export function ProgressRow({
  label,
  current,
  total,
  unit = "h",
  tone = "info",
  className,
}: {
  label: string;
  current: number;
  total: number;
  unit?: string;
  tone?: "info" | "success" | "warning" | "danger";
  className?: string;
}) {
  const pct = Math.min(100, Math.round((current / total) * 100));
  const map = {
    info: "bg-primary",
    success: "bg-success",
    warning: "bg-warning",
    danger: "bg-destructive",
  }[tone];
  return (
    <div className={cn("flex flex-col gap-1.5", className)}>
      <div className="flex items-center justify-between text-[12px]">
        <span className="font-medium text-foreground">{label}</span>
        <span className="tabular-nums text-muted-foreground">
          {current}/{total} {unit}{" "}
          <span className="ml-1 font-semibold text-foreground">{pct}%</span>
        </span>
      </div>
      <div className="h-2 w-full overflow-hidden rounded-full bg-muted">
        <div className={cn("h-full rounded-full", map)} style={{ width: `${pct}%` }} />
      </div>
    </div>
  );
}
