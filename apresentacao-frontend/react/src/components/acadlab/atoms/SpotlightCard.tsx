import { useRef, type ReactNode } from "react";
import { cn } from "@/lib/utils";

/**
 * SpotlightCard — reactbits-style hover spotlight wrapper.
 * Tracks the cursor and reveals a soft radial highlight using the primary color token.
 */
export function SpotlightCard({
  children,
  className,
  radius = 280,
}: {
  children: ReactNode;
  className?: string;
  radius?: number;
}) {
  const ref = useRef<HTMLDivElement | null>(null);

  const onMove = (e: React.MouseEvent<HTMLDivElement>) => {
    const el = ref.current;
    if (!el) return;
    const rect = el.getBoundingClientRect();
    el.style.setProperty("--spot-x", `${e.clientX - rect.left}px`);
    el.style.setProperty("--spot-y", `${e.clientY - rect.top}px`);
  };

  return (
    <div
      ref={ref}
      onMouseMove={onMove}
      style={{ ["--spot-r" as any]: `${radius}px` }}
      className={cn("group/spot relative isolate", className)}
    >
      <div
        aria-hidden
        className="pointer-events-none absolute inset-0 z-10 rounded-[inherit] opacity-0 transition-opacity duration-300 group-hover/spot:opacity-100"
        style={{
          background:
            "radial-gradient(var(--spot-r) circle at var(--spot-x) var(--spot-y), hsl(var(--primary) / 0.12), transparent 60%)",
        }}
      />
      {children}
    </div>
  );
}
