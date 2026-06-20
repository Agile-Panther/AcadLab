import { CheckCircle2, AlertCircle, AlertTriangle, Info } from "lucide-react";
import { cn } from "@/lib/utils";

export function ValidationCallout({
  tone = "success",
  children,
  className,
}: {
  tone?: "success" | "error" | "info" | "warning";
  children: React.ReactNode;
  className?: string;
}) {
  const map = {
    success: { cls: "bg-success-soft text-success", Icon: CheckCircle2 },
    error: { cls: "bg-destructive-soft text-destructive", Icon: AlertCircle },
    info: { cls: "bg-primary-soft text-primary", Icon: Info },
    warning: { cls: "bg-warning/10 text-warning", Icon: AlertTriangle },
  } as const;
  const { cls, Icon } = map[tone];
  return (
    <div
      className={cn(
        "flex h-11 items-center gap-2 rounded-md px-3 text-[13px] font-semibold",
        cls,
        className,
      )}
    >
      <Icon className="h-4 w-4 shrink-0" />
      <span>{children}</span>
    </div>
  );
}
