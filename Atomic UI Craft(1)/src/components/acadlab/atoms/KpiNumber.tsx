import { cn } from "@/lib/utils";
import { CountUp } from "./CountUp";

export function KpiNumber({
  value,
  tone = "info",
  className,
  animate = true,
}: {
  value: React.ReactNode;
  tone?: "info" | "success" | "warning" | "danger";
  className?: string;
  animate?: boolean;
}) {
  const map = {
    info: "text-primary",
    success: "text-success",
    warning: "text-warning",
    danger: "text-destructive",
  } as const;

  // Try to animate numeric values (also handles "94%", "8.7", "142/200", "1.4d", "20h/sem", etc.)
  const rendered = (() => {
    if (!animate) return value;
    if (typeof value === "number") {
      const decimals = Number.isInteger(value) ? 0 : 1;
      return <CountUp end={value} decimals={decimals} />;
    }
    if (typeof value === "string") {
      const m = value.match(/^(\d+(?:[.,]\d+)?)(.*)$/);
      if (m) {
        const num = parseFloat(m[1].replace(",", "."));
        const decimals = m[1].includes(".") || m[1].includes(",") ? 1 : 0;
        return (
          <>
            <CountUp end={num} decimals={decimals} />
            {m[2]}
          </>
        );
      }
    }
    return value;
  })();

  return (
    <span className={cn("text-[26px] font-bold leading-none tabular-nums", map[tone], className)}>
      {rendered}
    </span>
  );
}
