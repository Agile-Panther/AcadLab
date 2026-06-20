import { cn } from "@/lib/utils";

export function FormField({
  label,
  required,
  hint,
  children,
  className,
  full,
}: {
  label: string;
  required?: boolean;
  hint?: string;
  children: React.ReactNode;
  className?: string;
  full?: boolean;
}) {
  return (
    <div className={cn("flex flex-col gap-1.5", full && "col-span-2", className)}>
      <label className="text-[12px] font-semibold text-foreground">
        {label}
        {required && <span className="ml-0.5 text-destructive">*</span>}
      </label>
      {children}
      {hint && <span className="text-[11px] text-muted-foreground">{hint}</span>}
    </div>
  );
}
