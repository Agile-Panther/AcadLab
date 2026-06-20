import { Fragment } from "react";
import { cn } from "@/lib/utils";

const days = ["Seg", "Ter", "Qua", "Qui", "Sex"];
const slots = ["08-10", "10-12", "14-16", "16-18"];

export function ConflictGrid({
  occupied,
  className,
}: {
  /** array of [dayIdx 0-4, slotIdx 0-3] tuples */
  occupied: [number, number][];
  className?: string;
}) {
  const isBusy = (d: number, s: number) => occupied.some(([dd, ss]) => dd === d && ss === s);
  return (
    <div className={cn("flex flex-col gap-3", className)}>
      <div className="overflow-hidden rounded-lg border bg-card">
        <div className="grid" style={{ gridTemplateColumns: "60px repeat(5, 1fr)" }}>
          <div className="border-b border-r border-border bg-background px-2 py-1.5 text-[10px] font-semibold uppercase text-muted-foreground">
            Slot
          </div>
          {days.map((d) => (
            <div
              key={d}
              className="border-b border-r border-border bg-background px-2 py-1.5 text-center text-[11px] font-semibold text-foreground"
            >
              {d}
            </div>
          ))}
          {slots.map((slot, si) => (
            <Fragment key={slot}>
              <div className="border-r border-b border-border px-2 py-2 text-[11px] tabular-nums text-muted-foreground">
                {slot}
              </div>
              {days.map((_, di) => (
                <div
                  key={di}
                  className={cn(
                    "h-10 border-r border-b border-border",
                    isBusy(di, si) ? "bg-warning-soft" : "bg-[#F3F4F6]",
                  )}
                />
              ))}
            </Fragment>
          ))}
        </div>
      </div>
      <div className="flex items-center gap-4 text-[11px] text-muted-foreground">
        <span className="flex items-center gap-1.5">
          <span className="h-3 w-3 rounded-sm border bg-[#F3F4F6]" /> Livre
        </span>
        <span className="flex items-center gap-1.5">
          <span className="h-3 w-3 rounded-sm border bg-warning-soft" /> Ocupado pelo professor
          selecionado
        </span>
      </div>
    </div>
  );
}
