import { Search, SlidersHorizontal, Plus } from "lucide-react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";

export function ActionBar({
  searchPlaceholder = "Buscar...",
  onSearch,
  primaryLabel,
  onPrimary,
  showFilters = true,
  filters,
  className,
}: {
  searchPlaceholder?: string;
  onSearch?: (v: string) => void;
  primaryLabel?: string;
  onPrimary?: () => void;
  showFilters?: boolean;
  filters?: React.ReactNode;
  className?: string;
}) {
  return (
    <div className={cn("flex flex-col gap-2 sm:flex-row sm:items-center", className)}>
      <div className="relative flex-1">
        <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          placeholder={searchPlaceholder}
          onChange={(e) => onSearch?.(e.target.value)}
          className="h-10 rounded-lg border-border bg-card pl-9"
        />
      </div>
      {filters}
      {showFilters && !filters && (
        <Button variant="outline" className="h-10 gap-2 rounded-lg">
          <SlidersHorizontal className="h-4 w-4" /> Filtros
        </Button>
      )}
      {primaryLabel && (
        <Button onClick={onPrimary} className="h-10 gap-2 rounded-lg bg-primary text-primary-foreground hover:bg-primary/90">
          <Plus className="h-4 w-4" /> {primaryLabel}
        </Button>
      )}
    </div>
  );
}
