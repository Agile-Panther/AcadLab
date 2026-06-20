import { useMemo, useState } from "react";
import { Bell, HelpCircle, Check, UserCog } from "lucide-react";
import { useProfileSwitcherContext } from "../context/ProfileSwitcher";
import { useRouterState } from "@tanstack/react-router";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";
import { getPageHelp } from "../data/pageHelp";
import {
  notifications as seedNotifications,
  type NotificationTone,
} from "../data/notifications";

const toneStyles: Record<NotificationTone, string> = {
  info: "bg-primary/10 text-primary",
  success: "bg-emerald-500/10 text-emerald-600 dark:text-emerald-400",
  warning: "bg-amber-500/10 text-amber-600 dark:text-amber-400",
  danger: "bg-destructive/10 text-destructive",
};

export function TopBar({
  title,
  subtitle,
  right,
}: {
  title: string;
  subtitle?: string;
  right?: React.ReactNode;
}) {
  const pathname = useRouterState({ select: (s) => s.location.pathname });
  const help = useMemo(() => getPageHelp(pathname), [pathname]);

  const { profiles, active, setActive } = useProfileSwitcherContext();
  const activeProfile = profiles.find((p) => p.value === active) ?? null;

  const [items, setItems] = useState(seedNotifications);
  const unreadCount = items.filter((n) => n.unread).length;
  const markAllRead = () =>
    setItems((prev) => prev.map((n) => ({ ...n, unread: false })));
  const markRead = (id: string) =>
    setItems((prev) =>
      prev.map((n) => (n.id === id ? { ...n, unread: false } : n)),
    );

  return (
    <header className="sticky top-0 z-20 flex h-[72px] items-center justify-between gap-4 border-b border-border bg-card/70 px-6 backdrop-blur-xl supports-[backdrop-filter]:bg-card/60 lg:px-8">
      {/* gradient hairline accent */}
      <span
        aria-hidden
        className="pointer-events-none absolute inset-x-0 bottom-0 h-px"
        style={{
          background:
            "linear-gradient(90deg, transparent, hsl(var(--primary) / 0.35), transparent)",
        }}
      />
      <div className="flex min-w-0 flex-col">
        <h1 className="truncate text-[20px] font-semibold leading-tight tracking-tight text-foreground">
          {title}
        </h1>
        {subtitle && (
          <p className="truncate text-[12.5px] text-muted-foreground">{subtitle}</p>
        )}
      </div>
      <div className="flex items-center gap-2">
        {right}

        {profiles.length > 1 && activeProfile && (
          <Popover>
            <PopoverTrigger asChild>
              <button
                type="button"
                aria-label="Trocar perfil"
                className="flex h-9 items-center gap-2 rounded-full border border-border bg-card/60 pl-2 pr-2.5 text-[12.5px] font-medium text-foreground transition-colors hover:bg-muted"
              >
                <span className="flex h-6 w-6 items-center justify-center rounded-full bg-primary/10 text-primary">
                  <UserCog className="h-3.5 w-3.5" />
                </span>
                {activeProfile.label}
              </button>
            </PopoverTrigger>
            <PopoverContent align="end" className="w-[260px] p-2">
              <p className="px-2 py-1.5 text-[11px] font-medium uppercase tracking-wide text-muted-foreground">Trocar perfil</p>
              {profiles.map((p) => (
                <button
                  key={p.value}
                  type="button"
                  onClick={() => setActive(p.value)}
                  className={cn(
                    "flex w-full flex-col rounded-md px-3 py-2 text-left text-[13px] transition-colors hover:bg-muted",
                    p.value === active && "bg-primary/5 font-semibold text-primary",
                  )}
                >
                  <span>{p.label}</span>
                  {p.description && (
                    <span className="text-[11.5px] font-normal text-muted-foreground">{p.description}</span>
                  )}
                </button>
              ))}
            </PopoverContent>
          </Popover>
        )}

        <Popover>
          <PopoverTrigger asChild>
            <button
              type="button"
              aria-label="Sobre esta página"
              className="hidden h-9 w-9 items-center justify-center rounded-full text-muted-foreground transition-colors hover:bg-muted hover:text-foreground sm:flex"
            >
              <HelpCircle className="h-[18px] w-[18px]" />
            </button>
          </PopoverTrigger>
          <PopoverContent align="end" className="w-[340px] p-0">
            <div className="border-b border-border px-4 py-3">
              <p className="text-[11px] font-medium uppercase tracking-wide text-muted-foreground">
                Sobre esta página
              </p>
              <p className="mt-0.5 text-[14px] font-semibold text-foreground">
                {help.title}
              </p>
            </div>
            <div className="space-y-3 px-4 py-3">
              <p className="text-[13px] leading-relaxed text-muted-foreground">
                {help.summary}
              </p>
              {help.bullets && help.bullets.length > 0 && (
                <ul className="space-y-1.5">
                  {help.bullets.map((b, i) => (
                    <li
                      key={i}
                      className="flex items-start gap-2 text-[12.5px] text-foreground"
                    >
                      <span className="mt-1.5 h-1 w-1 shrink-0 rounded-full bg-primary" />
                      <span>{b}</span>
                    </li>
                  ))}
                </ul>
              )}
            </div>
            <div className="border-t border-border px-4 py-2.5 text-[11.5px] text-muted-foreground">
              Dica: use as abas no topo para alternar entre etapas.
            </div>
          </PopoverContent>
        </Popover>

        <Popover>
          <PopoverTrigger asChild>
            <button
              type="button"
              aria-label="Notificações"
              className="relative flex h-9 w-9 items-center justify-center rounded-full text-muted-foreground transition-colors hover:bg-muted hover:text-foreground"
            >
              <Bell className="h-[18px] w-[18px]" />
              {unreadCount > 0 && (
                <span className="absolute right-1.5 top-1.5 flex h-4 min-w-4 items-center justify-center rounded-full bg-destructive px-1 text-[10px] font-semibold leading-none text-destructive-foreground ring-2 ring-card">
                  {unreadCount}
                </span>
              )}
            </button>
          </PopoverTrigger>
          <PopoverContent align="end" className="w-[380px] p-0">
            <div className="flex items-center justify-between border-b border-border px-4 py-3">
              <div>
                <p className="text-[14px] font-semibold text-foreground">
                  Notificações
                </p>
                <p className="text-[11.5px] text-muted-foreground">
                  {unreadCount > 0
                    ? `${unreadCount} não lida${unreadCount > 1 ? "s" : ""}`
                    : "Tudo em dia"}
                </p>
              </div>
              <Button
                variant="ghost"
                size="sm"
                className="h-7 px-2 text-[12px]"
                onClick={markAllRead}
                disabled={unreadCount === 0}
              >
                <Check className="mr-1 h-3.5 w-3.5" />
                Marcar todas
              </Button>
            </div>
            <div className="max-h-[380px] overflow-y-auto">
              {items.length === 0 ? (
                <div className="px-4 py-10 text-center text-[13px] text-muted-foreground">
                  Você não tem notificações.
                </div>
              ) : (
                <ul className="divide-y divide-border">
                  {items.map((n) => {
                    const Icon = n.icon;
                    return (
                      <li key={n.id}>
                        <button
                          type="button"
                          onClick={() => markRead(n.id)}
                          className={cn(
                            "flex w-full items-start gap-3 px-4 py-3 text-left transition-colors hover:bg-muted/60",
                            n.unread && "bg-primary/[0.03]",
                          )}
                        >
                          <span
                            className={cn(
                              "flex h-8 w-8 shrink-0 items-center justify-center rounded-full",
                              toneStyles[n.tone],
                            )}
                          >
                            <Icon className="h-4 w-4" />
                          </span>
                          <div className="min-w-0 flex-1">
                            <div className="flex items-center gap-2">
                              <p className="truncate text-[13px] font-medium text-foreground">
                                {n.title}
                              </p>
                              {n.unread && (
                                <span className="h-1.5 w-1.5 shrink-0 rounded-full bg-primary" />
                              )}
                            </div>
                            <p className="mt-0.5 line-clamp-2 text-[12px] text-muted-foreground">
                              {n.description}
                            </p>
                            <p className="mt-1 text-[11px] text-muted-foreground/80">
                              {n.time}
                            </p>
                          </div>
                        </button>
                      </li>
                    );
                  })}
                </ul>
              )}
            </div>
          </PopoverContent>
        </Popover>
      </div>
    </header>
  );
}
