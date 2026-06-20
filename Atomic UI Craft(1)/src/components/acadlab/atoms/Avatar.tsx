import { cn } from "@/lib/utils";

export function Avatar({
  initials,
  size = 36,
  variant = "soft",
  className,
}: {
  initials: string;
  size?: number;
  variant?: "soft" | "solid";
  className?: string;
}) {
  return (
    <div
      style={{ width: size, height: size }}
      className={cn(
        "inline-flex items-center justify-center rounded-full text-[12px] font-semibold",
        variant === "soft"
          ? "bg-primary-soft text-primary"
          : "bg-primary text-primary-foreground",
        className,
      )}
    >
      {initials}
    </div>
  );
}
