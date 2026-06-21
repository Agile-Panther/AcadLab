import { cn } from "@/lib/utils";

export function BusinessRulesBanner({ rules, className }: { rules: string[]; className?: string }) {
  return (
    <div
      className={cn(
        "mt-4 rounded-md border px-4 py-2.5 text-[11px] leading-relaxed text-warning",
        className,
      )}
      style={{
        backgroundColor: "color-mix(in oklab, var(--warning) 8%, white)",
        borderColor: "color-mix(in oklab, var(--warning) 35%, white)",
      }}
    >
      <span className="font-semibold">Regras de Negócio: </span>
      {rules.join(" · ")}
    </div>
  );
}
