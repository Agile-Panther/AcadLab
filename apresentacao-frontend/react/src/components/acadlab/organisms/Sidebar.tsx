import { useMemo, useState } from "react";
import { Link, useRouterState } from "@tanstack/react-router";
import {
  BookOpen,
  CalendarDays,
  Layers,
  GraduationCap,
  BookMarked,
  FileText,
  Building2,
  ListChecks,
  Briefcase,
  Wallet,
  BriefcaseBusiness,
  HandHeart,
  Heart,
  Plane,
  Search,
  Settings,
  LogOut,
  GraduationCap as Logo,
} from "lucide-react";
import { cn } from "@/lib/utils";

type NavItem = { to: string; label: string; icon: typeof BookOpen };
type NavGroup = { label: string; items: NavItem[] };

const groups: NavGroup[] = [
  {
    label: "Acadêmico",
    items: [
      { to: "/matricula", label: "Matrícula", icon: GraduationCap },
      { to: "/gestao-pedagogica", label: "Notas & Frequência", icon: BookMarked },
      { to: "/historico-academico", label: "Histórico", icon: FileText },
      { to: "/integralizacao", label: "Integralização", icon: ListChecks },
      { to: "/atividades-complementares", label: "Ativ. Complementares", icon: Briefcase },
    ],
  },
  {
    label: "Serviços",
    items: [
      { to: "/secretaria-virtual", label: "Secretaria Virtual", icon: Building2 },
      { to: "/financeiro", label: "Financeiro", icon: Wallet },
      { to: "/estagios", label: "Estágios", icon: BriefcaseBusiness },
      { to: "/mobilidade", label: "Mobilidade", icon: Plane },
    ],
  },
  {
    label: "Apoio ao Estudante",
    items: [
      { to: "/permanencia", label: "Permanência", icon: HandHeart },
      { to: "/psicopedagogico", label: "Psicopedagógico", icon: Heart },
    ],
  },
  {
    label: "Gestão Institucional",
    items: [
      { to: "/gestao-curricular", label: "Gestão Curricular", icon: BookOpen },
      { to: "/periodo-letivo", label: "Período Letivo", icon: CalendarDays },
      { to: "/oferta-turmas", label: "Oferta de Turmas", icon: Layers },
    ],
  },
];

export function Sidebar() {
  const pathname = useRouterState({ select: (s) => s.location.pathname });
  const [q, setQ] = useState("");

  const filtered = useMemo(() => {
    if (!q.trim()) return groups;
    const needle = q.toLowerCase();
    return groups
      .map((g) => ({ ...g, items: g.items.filter((i) => i.label.toLowerCase().includes(needle)) }))
      .filter((g) => g.items.length > 0);
  }, [q]);

  return (
    <aside className="fixed left-0 top-0 hidden h-screen w-[248px] flex-col bg-sidebar text-sidebar-foreground lg:flex">
      {/* ambient gradient blobs */}
      <div aria-hidden className="pointer-events-none absolute inset-0 overflow-hidden opacity-60">
        <div
          className="absolute -left-12 top-10 h-40 w-40 rounded-full blur-3xl animate-[aurora-drift_14s_ease-in-out_infinite]"
          style={{
            background: "radial-gradient(circle, hsl(var(--primary) / 0.35), transparent 70%)",
          }}
        />
        <div
          className="absolute -right-10 bottom-24 h-44 w-44 rounded-full blur-3xl animate-[aurora-drift_18s_ease-in-out_infinite_reverse]"
          style={{
            background: "radial-gradient(circle, hsl(var(--chart-5) / 0.25), transparent 70%)",
          }}
        />
      </div>

      {/* Brand */}
      <div className="relative flex h-[72px] items-center gap-2.5 px-5">
        <div className="relative flex h-9 w-9 items-center justify-center rounded-lg bg-primary text-primary-foreground shadow-sm">
          <Logo className="h-[18px] w-[18px]" />
          <span className="absolute inset-0 rounded-lg ring-1 ring-white/20" />
        </div>
        <div className="flex flex-col leading-tight">
          <span className="text-[15px] font-bold tracking-tight text-sidebar-text-active">
            AcadLab
          </span>
          <span className="text-[10.5px] text-sidebar-text">Portal do Estudante</span>
        </div>
      </div>

      {/* Search */}
      <div className="px-3 pb-3">
        <div className="relative">
          <Search className="pointer-events-none absolute left-2.5 top-1/2 h-3.5 w-3.5 -translate-y-1/2 text-sidebar-text" />
          <input
            value={q}
            onChange={(e) => setQ(e.target.value)}
            placeholder="Buscar módulo..."
            className="h-9 w-full rounded-md bg-sidebar-active/60 pl-8 pr-2.5 text-[12.5px] text-sidebar-text-active placeholder:text-sidebar-text focus:outline-none focus:ring-1 focus:ring-white/15"
          />
        </div>
      </div>

      {/* Nav groups */}
      <nav className="flex-1 overflow-y-auto px-2 pb-4">
        {filtered.map((group) => (
          <div key={group.label} className="mb-3">
            <div className="px-3 pb-1.5 pt-2 text-[10px] font-semibold uppercase tracking-wider text-sidebar-text/80">
              {group.label}
            </div>
            <ul className="flex flex-col gap-0.5">
              {group.items.map(({ to, label, icon: Icon }) => {
                const active = pathname === to || pathname.startsWith(to + "/");
                return (
                  <li key={to}>
                    <Link
                      to={to}
                      className={cn(
                        "group relative flex h-9 items-center gap-3 overflow-hidden rounded-md px-3 text-[13px] transition-all",
                        active
                          ? "bg-sidebar-active font-medium text-sidebar-text-active shadow-[inset_0_0_0_1px_hsl(var(--primary)/0.25)]"
                          : "text-sidebar-text hover:bg-sidebar-active/60 hover:text-sidebar-text-active hover:translate-x-0.5",
                      )}
                    >
                      {active && (
                        <>
                          <span className="absolute inset-y-1.5 left-0 w-0.5 rounded-r-full bg-primary shadow-[0_0_8px_hsl(var(--primary)/0.6)]" />
                          {/* shimmer sweep */}
                          <span
                            aria-hidden
                            className="pointer-events-none absolute inset-y-0 left-0 w-1/3 animate-[shimmer-slide_3s_ease-in-out_infinite]"
                            style={{
                              background:
                                "linear-gradient(90deg, transparent, hsl(var(--primary-foreground) / 0.08), transparent)",
                            }}
                          />
                        </>
                      )}
                      <Icon
                        className={cn(
                          "h-[16px] w-[16px] shrink-0 transition-transform",
                          active && "text-primary",
                        )}
                      />
                      <span className="relative truncate">{label}</span>
                    </Link>
                  </li>
                );
              })}
            </ul>
          </div>
        ))}
        {filtered.length === 0 && (
          <p className="px-3 py-4 text-center text-[11.5px] text-sidebar-text">
            Nenhum módulo encontrado
          </p>
        )}
      </nav>

      {/* User footer */}
      <div className="border-t border-white/5 p-3">
        <Link
          to="/perfil"
          className="flex items-center gap-2.5 rounded-md px-2 py-2 hover:bg-sidebar-active/60"
        >
          <div className="flex h-8 w-8 items-center justify-center rounded-full bg-primary text-[11px] font-semibold text-primary-foreground">
            MS
          </div>
          <div className="flex min-w-0 flex-1 flex-col leading-tight">
            <span className="truncate text-[12.5px] font-medium text-sidebar-text-active">
              Maria Santos
            </span>
            <span className="truncate text-[10.5px] text-sidebar-text">
              Estudante · Eng. Software
            </span>
          </div>
          <span
            aria-label="Configurações"
            className="rounded p-1 text-sidebar-text hover:text-sidebar-text-active"
          >
            <Settings className="h-3.5 w-3.5" />
          </span>
          <span
            aria-label="Sair"
            className="rounded p-1 text-sidebar-text hover:text-sidebar-text-active"
          >
            <LogOut className="h-3.5 w-3.5" />
          </span>
        </Link>
      </div>
    </aside>
  );
}
