import { cn } from "@/lib/utils";
import { KpiNumber } from "../atoms/KpiNumber";
import { SpotlightCard } from "../atoms/SpotlightCard";
import { BorderBeam } from "../atoms/BorderBeam";

export type StatTone = "info" | "success" | "warning" | "danger";

export function StatCard({
  label,
  value,
  tone = "info",
  hint,
  className,
}: {
  label: string;
  value: React.ReactNode;
  tone?: StatTone;
  hint?: string;
  className?: string;
}) {
  const bar = {
    info: "bg-primary",
    success: "bg-success",
    warning: "bg-warning",
    danger: "bg-destructive",
  }[tone];
  return (
    <SpotlightCard className={cn("rounded-xl", className)}>
      <div
        className={cn(
          "group/stat relative flex h-[88px] items-center overflow-hidden rounded-xl border bg-card pl-5 pr-4 shadow-card transition-all duration-300 hover:-translate-y-0.5 hover:shadow-md",
        )}
      >
        <span className={cn("absolute left-0 top-0 h-full w-1", bar)} />
        {/* subtle gradient wash on hover */}
        <span
          aria-hidden
          className="pointer-events-none absolute inset-0 opacity-0 transition-opacity duration-500 group-hover/stat:opacity-100"
          style={{
            background:
              "linear-gradient(135deg, hsl(var(--primary) / 0.04), transparent 60%)",
          }}
        />
        <div className="relative z-10 flex flex-col gap-1">
          <KpiNumber value={value} tone={tone} />
          <span className="text-[12px] text-muted-foreground">{label}</span>
          {hint && <span className="text-[10px] text-muted-foreground/70">{hint}</span>}
        </div>
        {/* animated beam, only visible on hover */}
        <div className="pointer-events-none absolute inset-0 opacity-0 transition-opacity duration-500 group-hover/stat:opacity-100">
          <BorderBeam size={120} duration={6} />
        </div>
      </div>
    </SpotlightCard>
  );
}
