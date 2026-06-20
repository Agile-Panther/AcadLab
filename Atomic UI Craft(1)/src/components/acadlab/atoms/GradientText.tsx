import { cn } from "@/lib/utils";
import type { ElementType, ReactNode } from "react";

/**
 * GradientText — reactbits-style text with an animated gradient sweep.
 * Subtle and on-brand: uses primary tokens, no rainbow.
 */
export function GradientText({
  children,
  className,
  as: Tag = "span",
}: {
  children: ReactNode;
  className?: string;
  as?: ElementType;
}) {
  return (
    <Tag
      className={cn(
        "bg-gradient-to-r from-primary via-primary/70 to-primary bg-[length:200%_100%] bg-clip-text text-transparent",
        "animate-[gradient-shift_6s_ease-in-out_infinite]",
        className,
      )}
    >
      {children}
    </Tag>
  );
}
