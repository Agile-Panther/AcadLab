import { cn } from "@/lib/utils";

/**
 * BorderBeam — magicui-inspired animated border light that traces the container.
 * Place inside a `relative` parent with rounded corners.
 */
export function BorderBeam({
  className,
  size = 200,
  duration = 8,
  delay = 0,
}: {
  className?: string;
  size?: number;
  duration?: number;
  delay?: number;
}) {
  return (
    <div
      style={
        {
          "--size": `${size}px`,
          "--duration": `${duration}s`,
          "--delay": `-${delay}s`,
        } as React.CSSProperties
      }
      className={cn(
        "pointer-events-none absolute inset-0 rounded-[inherit] [border:1px_solid_transparent]",
        "![mask-clip:padding-box,border-box] ![mask-composite:intersect]",
        "[mask:linear-gradient(transparent,transparent),linear-gradient(white,white)]",
        "after:absolute after:aspect-square after:w-[var(--size)]",
        "after:animate-[border-beam_var(--duration)_linear_infinite] after:[animation-delay:var(--delay)]",
        "after:[background:linear-gradient(to_left,hsl(var(--primary)),transparent)]",
        "after:[offset-anchor:90%_50%] after:[offset-path:rect(0_auto_auto_0_round_var(--size))]",
        className,
      )}
    />
  );
}
