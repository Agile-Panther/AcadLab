import { CheckCircle2 } from "lucide-react";
import { cn } from "@/lib/utils";

export function SuccessBanner({
  title,
  description,
  className,
}: {
  title: string;
  description?: string;
  className?: string;
}) {
  return (
    <div
      className={cn(
        "flex h-[88px] items-center gap-4 rounded-xl bg-success px-6 text-success-foreground shadow-card",
        className,
      )}
    >
      <CheckCircle2 className="h-8 w-8 shrink-0" />
      <div>
        <p className="text-[18px] font-semibold leading-tight">{title}</p>
        {description && (
          <p className="mt-0.5 text-[14px] text-success-foreground/90">{description}</p>
        )}
      </div>
    </div>
  );
}
