import { cn } from "@/lib/utils";
import { StatCard, type StatTone } from "./StatCard";

export type Stat = { label: string; value: React.ReactNode; tone?: StatTone; hint?: string };

export function StatsRow({ stats, className }: { stats: Stat[]; className?: string }) {
  return (
    <div className={cn("grid grid-cols-1 gap-2.5 sm:grid-cols-2 lg:grid-cols-4", className)}>
      {stats.map((s, i) => (
        <StatCard key={i} {...s} />
      ))}
    </div>
  );
}
