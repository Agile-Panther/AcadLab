import { Fragment } from "react";
import { cn } from "@/lib/utils";

export type ClassBlock = {
  day: number; // 1=Seg ... 6=Sáb
  start: number; // hour 7..18
  duration: number; // in hours
  title: string;
  code: string;
  color: "info" | "success" | "warning" | "danger" | "violet";
};

const days = ["Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado"];
const colorMap: Record<ClassBlock["color"], string> = {
  info: "bg-primary",
  success: "bg-success",
  warning: "bg-warning",
  danger: "bg-destructive",
  violet: "bg-[color:var(--chart-5)]",
};
const ROW_H = 36;

export function ScheduleGrid({
  blocks,
  startHour = 7,
  endHour = 19,
  className,
}: {
  blocks: ClassBlock[];
  startHour?: number;
  endHour?: number;
  className?: string;
}) {
  const hours = Array.from({ length: endHour - startHour }, (_, i) => startHour + i);
  const bodyHeight = hours.length * ROW_H;

  return (
    <div className={cn("overflow-hidden rounded-xl border bg-card shadow-card", className)}>
      <div className="grid" style={{ gridTemplateColumns: "72px repeat(6, 1fr)" }}>
        <div className="border-b border-r border-border bg-[color:var(--primary-soft)]/40 px-3 py-2 text-[11px] font-semibold uppercase text-primary">
          Hora
        </div>
        {days.map((d) => (
          <div
            key={d}
            className="border-b border-r border-border bg-[color:var(--primary-soft)]/40 px-3 py-2 text-center text-[12px] font-semibold text-primary"
          >
            {d}
          </div>
        ))}
      </div>
      <div
        className="relative grid"
        style={{ gridTemplateColumns: "72px repeat(6, 1fr)", height: bodyHeight }}
      >
        {hours.map((h, hIdx) => (
          <Fragment key={h}>
            <div
              className={cn(
                "border-r border-border px-3 text-[11px] text-muted-foreground tabular-nums flex items-start pt-1",
                hIdx % 2 === 1 && "bg-subtle",
              )}
              style={{ height: ROW_H }}
            >
              {String(h).padStart(2, "0")}:00
            </div>
            {days.map((_, di) => (
              <div
                key={di}
                className={cn("border-r border-b border-border", hIdx % 2 === 1 && "bg-subtle")}
                style={{ height: ROW_H }}
              />
            ))}
          </Fragment>
        ))}
        {/* Blocks overlay */}
        <div
          className="pointer-events-none absolute inset-0 grid"
          style={{ gridTemplateColumns: "72px repeat(6, 1fr)" }}
        >
          <div />
          {days.map((_, di) => (
            <div key={di} className="relative">
              {blocks
                .filter((b) => b.day === di + 1)
                .map((b, i) => (
                  <div
                    key={i}
                    className={cn(
                      "pointer-events-auto absolute inset-x-1 rounded p-1.5 text-white shadow-sm",
                      colorMap[b.color],
                    )}
                    style={{
                      top: (b.start - startHour) * ROW_H + 2,
                      height: b.duration * ROW_H - 4,
                    }}
                  >
                    <p className="truncate text-[10px] font-semibold leading-tight">{b.title}</p>
                    <p className="text-[9px] opacity-90">{b.code}</p>
                  </div>
                ))}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
