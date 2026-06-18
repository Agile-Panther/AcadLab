import { useState, type ReactNode } from "react";
import { createFileRoute } from "@tanstack/react-router";
import {
  AppShell, SectionTitle, StatusBadge, StatCard, Avatar, FormField, GradientText, DotPattern,
} from "@/components/acadlab";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Separator } from "@/components/ui/separator";
import {
  GraduationCap, BookOpen, Briefcase, ShieldCheck, Heart,
  Mail, Phone, MapPin, Calendar, IdCard, Pencil, Camera,
  BookMarked, Users, Building2, FileText, Award, Clock, Activity,
} from "lucide-react";
import { cn } from "@/lib/utils";

export const Route = createFileRoute("/perfil")({
  head: () => ({ meta: [{ title: "Perfil — AcadLab" }] }),
  component: Page,
});

type ProfileType = "estudante" | "professor" | "coordenador" | "secretaria" | "psicologo";

const profiles: { value: ProfileType; label: string; icon: typeof GraduationCap }[] = [
  { value: "estudante", label: "Estudante", icon: GraduationCap },
  { value: "professor", label: "Professor", icon: BookOpen },
  { value: "coordenador", label: "Coordenador", icon: Briefcase },
  { value: "secretaria", label: "Secretaria / Admin", icon: ShieldCheck },
  { value: "psicologo", label: "Psicólogo", icon: Heart },
];

function Page() {
  const [type, setType] = useState<ProfileType>("estudante");
  const current = profiles.find((p) => p.value === type)!;
  return (
    <AppShell title="Meu Perfil" subtitle="Informações pessoais, acadêmicas e profissionais">
      <div className="mb-6 flex flex-wrap items-center gap-2">
        <span className="mr-2 text-[12px] font-medium text-muted-foreground">Visualizar como:</span>
        {profiles.map((p) => {
          const Icon = p.icon;
          const active = p.value === type;
          return (
            <button
              key={p.value}
              onClick={() => setType(p.value)}
              className={cn(
                "inline-flex h-8 items-center gap-1.5 rounded-full border px-3 text-[12.5px] transition-colors",
                active
                  ? "border-primary bg-primary text-primary-foreground"
                  : "border-border bg-card text-foreground hover:bg-muted",
              )}
            >
              <Icon className="h-3.5 w-3.5" />
              {p.label}
            </button>
          );
        })}
      </div>

      {type === "estudante" && <EstudanteProfile />}
      {type === "professor" && <ProfessorProfile />}
      {type === "coordenador" && <CoordenadorProfile />}
      {type === "secretaria" && <SecretariaProfile />}
      {type === "psicologo" && <PsicologoProfile />}

      <p className="mt-8 text-center text-[11px] text-muted-foreground">
        Perfil ativo: <span className="font-medium">{current.label}</span>
      </p>
    </AppShell>
  );
}

/* ---------- Shared building blocks ---------- */

function ProfileHeader({
  initials, name, role, tags, accent = "bg-primary",
}: {
  initials: string;
  name: string;
  role: string;
  tags: { label: string; tone?: "info" | "success" | "warning" | "neutral" }[];
  accent?: string;
}) {
  return (
    <div className="overflow-hidden rounded-xl border bg-card shadow-card">
      <div
        className={cn(
          "relative h-28 overflow-hidden bg-gradient-to-r from-primary via-primary/80 to-primary/60 bg-[length:200%_100%]",
          "animate-[gradient-shift_8s_ease-in-out_infinite]",
          accent,
        )}
      >
        <DotPattern className="text-primary-foreground/20" width={18} height={18} cr={1} />
        <div
          aria-hidden
          className="absolute -left-10 top-0 h-40 w-40 rounded-full blur-3xl animate-[aurora-drift_12s_ease-in-out_infinite]"
          style={{ background: "radial-gradient(circle, hsl(var(--primary-foreground) / 0.35), transparent 70%)" }}
        />
        <div
          aria-hidden
          className="absolute right-0 bottom-0 h-44 w-44 rounded-full blur-3xl animate-[aurora-drift_16s_ease-in-out_infinite_reverse]"
          style={{ background: "radial-gradient(circle, hsl(var(--chart-5) / 0.4), transparent 70%)" }}
        />
      </div>
      <div className="flex flex-col gap-4 px-6 pb-6 sm:flex-row sm:items-end sm:justify-between">
        <div className="flex flex-col gap-3 sm:flex-row sm:items-end">
          <div className="relative -mt-10 sm:-mt-12">
            <div className="relative">
              <Avatar initials={initials} size={88} variant="solid" className="ring-4 ring-card" />
              <button
                aria-label="Alterar foto"
                className="absolute bottom-1 right-1 inline-flex h-7 w-7 items-center justify-center rounded-full bg-card text-foreground shadow ring-1 ring-border hover:bg-muted"
              >
                <Camera className="h-3.5 w-3.5" />
              </button>
            </div>
          </div>
          <div className="pb-1">
            <GradientText as="h2" className="text-[20px] font-semibold leading-tight">{name}</GradientText>
            <p className="mt-0.5 text-[13px] text-muted-foreground">{role}</p>

            <div className="mt-2 flex flex-wrap gap-1.5">
              {tags.map((t, i) => (
                <StatusBadge key={i} tone={(t.tone ?? "info") as any}>{t.label}</StatusBadge>
              ))}
            </div>
          </div>
        </div>
        <div className="flex gap-2">
          <Button variant="outline" size="sm"><Pencil className="mr-1.5 h-3.5 w-3.5" /> Editar perfil</Button>
        </div>
      </div>
    </div>
  );
}

function InfoRow({ icon: Icon, label, value }: { icon: any; label: string; value: ReactNode }) {
  return (
    <div className="flex items-start gap-3 py-2.5">
      <span className="mt-0.5 inline-flex h-7 w-7 items-center justify-center rounded-md bg-primary-soft text-primary">
        <Icon className="h-3.5 w-3.5" />
      </span>
      <div className="min-w-0 flex-1">
        <div className="text-[11px] uppercase tracking-wide text-muted-foreground">{label}</div>
        <div className="text-[13.5px] text-foreground">{value}</div>
      </div>
    </div>
  );
}

function Card({ title, subtitle, action, children }: { title: string; subtitle?: string; action?: ReactNode; children: ReactNode }) {
  return (
    <div className="rounded-xl border bg-card p-5 shadow-card">
      <div className="flex items-start justify-between gap-3">
        <SectionTitle title={title} subtitle={subtitle} />
        {action}
      </div>
      <div className="mt-3">{children}</div>
    </div>
  );
}

function TwoCol({ children }: { children: ReactNode }) {
  return <div className="grid gap-5 lg:grid-cols-3">{children}</div>;
}

/* ---------- Estudante ---------- */

function EstudanteProfile() {
  return (
    <div className="space-y-6">
      <ProfileHeader
        initials="MS"
        name="Maria Santos"
        role="Engenharia de Software · 6º período"
        tags={[
          { label: "Matrícula ativa", tone: "success" },
          { label: "2025.2", tone: "info" },
          { label: "Bolsista PROUNI", tone: "warning" },
        ]}
      />

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <StatCard label="CR Geral" value="8.7" tone="success" hint="Coeficiente de rendimento" />
        <StatCard label="Créditos cursados" value="142/200" tone="info" />
        <StatCard label="Disciplinas matriculadas" value={5} tone="info" />
        <StatCard label="Frequência média" value="94%" tone="success" />
      </div>

      <TwoCol>
        <div className="lg:col-span-2 space-y-5">
          <Card title="Dados pessoais" action={<Button variant="ghost" size="sm"><Pencil className="h-3.5 w-3.5" /></Button>}>
            <div className="grid gap-1 sm:grid-cols-2">
              <InfoRow icon={IdCard} label="Matrícula" value="2022.1.08.0142" />
              <InfoRow icon={Calendar} label="Data de nascimento" value="14/03/2003" />
              <InfoRow icon={Mail} label="E-mail" value="maria.santos@acad.edu.br" />
              <InfoRow icon={Phone} label="Telefone" value="(11) 98765-4321" />
              <InfoRow icon={MapPin} label="Endereço" value="Rua das Acácias, 120 — São Paulo/SP" />
              <InfoRow icon={Activity} label="Status acadêmico" value={<StatusBadge tone="success">Regular</StatusBadge>} />
            </div>
          </Card>

          <Card title="Histórico recente" subtitle="Últimas disciplinas concluídas">
            <div className="divide-y">
              {[
                { d: "Programação Web", n: "9.2", s: "Aprovada" },
                { d: "Estruturas de Dados", n: "8.5", s: "Aprovada" },
                { d: "Cálculo III", n: "7.0", s: "Aprovada" },
              ].map((r) => (
                <div key={r.d} className="flex items-center justify-between py-2.5 text-[13px]">
                  <span>{r.d}</span>
                  <div className="flex items-center gap-3">
                    <span className="font-semibold">{r.n}</span>
                    <StatusBadge tone="success">{r.s}</StatusBadge>
                  </div>
                </div>
              ))}
            </div>
          </Card>
        </div>

        <div className="space-y-5">
          <Card title="Curso">
            <InfoRow icon={GraduationCap} label="Curso" value="Engenharia de Software" />
            <InfoRow icon={Building2} label="Instituição" value="Campus Centro" />
            <InfoRow icon={Calendar} label="Ingresso" value="2022.1" />
            <InfoRow icon={Clock} label="Previsão de conclusão" value="2026.1" />
          </Card>

          <Card title="Acessibilidade & preferências">
            <InfoRow icon={Award} label="Necessidades específicas" value="Nenhuma declarada" />
            <InfoRow icon={Mail} label="Notificações" value="E-mail e Push" />
          </Card>
        </div>
      </TwoCol>
    </div>
  );
}

/* ---------- Professor ---------- */

function ProfessorProfile() {
  return (
    <div className="space-y-6">
      <ProfileHeader
        initials="CL"
        name="Prof. Dr. Carlos Lima"
        role="Departamento de Computação"
        tags={[
          { label: "Doutor", tone: "info" },
          { label: "Dedicação exclusiva", tone: "success" },
          { label: "20 anos de carreira", tone: "neutral" },
        ]}
      />

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <StatCard label="Turmas no semestre" value={4} tone="info" />
        <StatCard label="Alunos ativos" value={132} tone="success" />
        <StatCard label="Carga horária" value="20h/sem" tone="warning" />
        <StatCard label="Avaliação docente" value="4.8/5" tone="success" />
      </div>

      <TwoCol>
        <div className="lg:col-span-2 space-y-5">
          <Card title="Dados profissionais">
            <div className="grid gap-1 sm:grid-cols-2">
              <InfoRow icon={IdCard} label="Matrícula funcional" value="DOC-2005-0341" />
              <InfoRow icon={Mail} label="E-mail institucional" value="carlos.lima@acad.edu.br" />
              <InfoRow icon={Award} label="Titulação" value="Doutor em Ciência da Computação" />
              <InfoRow icon={Building2} label="Lotação" value="Depto. de Computação · Bloco B" />
              <InfoRow icon={Clock} label="Regime" value="Dedicação Exclusiva (40h)" />
              <InfoRow icon={Calendar} label="Admissão" value="03/02/2005" />
            </div>
          </Card>

          <Card title="Turmas que leciona" subtitle="Semestre 2025.2">
            <div className="divide-y">
              {[
                { d: "AED301 — Algoritmos Avançados", t: "T01 · 38 alunos", h: "Seg/Qua 08-10" },
                { d: "AED301 — Algoritmos Avançados", t: "T02 · 35 alunos", h: "Ter/Qui 08-10" },
                { d: "IA501 — Tópicos em IA", t: "T01 · 29 alunos", h: "Sex 14-18" },
                { d: "PG901 — Orientação de TCC", t: "8 orientandos", h: "Quintas 16-18" },
              ].map((r) => (
                <div key={r.d + r.t} className="flex items-center justify-between gap-3 py-2.5 text-[13px]">
                  <div>
                    <div className="font-medium">{r.d}</div>
                    <div className="text-[12px] text-muted-foreground">{r.t}</div>
                  </div>
                  <StatusBadge tone="info">{r.h}</StatusBadge>
                </div>
              ))}
            </div>
          </Card>

          <Card title="Áreas de pesquisa">
            <div className="flex flex-wrap gap-1.5">
              {["Algoritmos", "Otimização", "Aprendizado de Máquina", "Grafos", "Sistemas Distribuídos"].map((a) => (
                <StatusBadge key={a} tone="info">{a}</StatusBadge>
              ))}
            </div>
          </Card>
        </div>

        <div className="space-y-5">
          <Card title="Contato">
            <InfoRow icon={Mail} label="E-mail" value="carlos.lima@acad.edu.br" />
            <InfoRow icon={Phone} label="Ramal" value="2341" />
            <InfoRow icon={MapPin} label="Sala" value="B-204" />
          </Card>
          <Card title="Atendimento">
            <InfoRow icon={Clock} label="Horário fixo" value="Quartas, 14h–16h" />
            <InfoRow icon={Calendar} label="Agendamento" value="Aberto via Portal" />
          </Card>
        </div>
      </TwoCol>
    </div>
  );
}

/* ---------- Coordenador ---------- */

function CoordenadorProfile() {
  return (
    <div className="space-y-6">
      <ProfileHeader
        initials="AS"
        name="Profa. Ana Souza"
        role="Coordenação · Engenharia de Software"
        tags={[
          { label: "Coordenadora", tone: "info" },
          { label: "Mandato 2024–2026", tone: "neutral" },
          { label: "32 docentes", tone: "success" },
        ]}
      />

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <StatCard label="Alunos no curso" value={642} tone="info" />
        <StatCard label="Turmas ofertadas" value={48} tone="info" />
        <StatCard label="Conceito MEC" value={4} tone="success" />
        <StatCard label="Evasão semestre" value="3.2%" tone="warning" />
      </div>

      <TwoCol>
        <div className="lg:col-span-2 space-y-5">
          <Card title="Curso coordenado">
            <div className="grid gap-1 sm:grid-cols-2">
              <InfoRow icon={GraduationCap} label="Curso" value="Engenharia de Software" />
              <InfoRow icon={Building2} label="Unidade" value="Campus Centro" />
              <InfoRow icon={FileText} label="Matriz vigente" value="2023.1" />
              <InfoRow icon={Calendar} label="Início do mandato" value="01/03/2024" />
            </div>
          </Card>

          <Card title="Equipe de docentes" subtitle="Resumo por titulação">
            <div className="grid grid-cols-3 gap-3">
              {[
                { l: "Doutores", v: 18 },
                { l: "Mestres", v: 11 },
                { l: "Especialistas", v: 3 },
              ].map((r) => (
                <div key={r.l} className="rounded-lg border bg-muted/30 p-4 text-center">
                  <div className="text-[22px] font-semibold">{r.v}</div>
                  <div className="text-[12px] text-muted-foreground">{r.l}</div>
                </div>
              ))}
            </div>
          </Card>

          <Card title="Atribuições" subtitle="Responsabilidades como coordenação">
            <ul className="ml-5 list-disc space-y-1 text-[13px] text-foreground/90">
              <li>Aprovação de plano de oferta e ementas</li>
              <li>Análise de exceções de matrícula e quebras de pré-requisito</li>
              <li>Acompanhamento do colegiado e NDE</li>
              <li>Indicadores de evasão, retenção e integralização</li>
            </ul>
          </Card>
        </div>

        <div className="space-y-5">
          <Card title="Contato">
            <InfoRow icon={Mail} label="E-mail" value="coord.eng.soft@acad.edu.br" />
            <InfoRow icon={Phone} label="Ramal" value="2100" />
            <InfoRow icon={MapPin} label="Sala da coordenação" value="A-110" />
          </Card>
          <Card title="Permissões do sistema">
            <div className="flex flex-wrap gap-1.5">
              {["Aprovar matrícula", "Oferta de turmas", "Gestão curricular", "Dashboards"].map((p) => (
                <StatusBadge key={p} tone="success">{p}</StatusBadge>
              ))}
            </div>
          </Card>
        </div>
      </TwoCol>
    </div>
  );
}

/* ---------- Secretaria / Admin ---------- */

function SecretariaProfile() {
  return (
    <div className="space-y-6">
      <ProfileHeader
        initials="JR"
        name="João Ribeiro"
        role="Secretaria Acadêmica · Administrador"
        tags={[
          { label: "Admin", tone: "warning" },
          { label: "Acesso total", tone: "info" },
          { label: "Ativo", tone: "success" },
        ]}
      />

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <StatCard label="Solicitações abertas" value={27} tone="warning" />
        <StatCard label="Resolvidas no mês" value={184} tone="success" />
        <StatCard label="SLA médio" value="1.4d" tone="info" />
        <StatCard label="Documentos emitidos" value={312} tone="info" />
      </div>

      <TwoCol>
        <div className="lg:col-span-2 space-y-5">
          <Card title="Dados funcionais">
            <div className="grid gap-1 sm:grid-cols-2">
              <InfoRow icon={IdCard} label="Matrícula funcional" value="ADM-2018-0091" />
              <InfoRow icon={Building2} label="Setor" value="Secretaria Acadêmica Central" />
              <InfoRow icon={Mail} label="E-mail" value="joao.ribeiro@acad.edu.br" />
              <InfoRow icon={Phone} label="Ramal" value="1010" />
              <InfoRow icon={Clock} label="Turno" value="Integral (08h–17h)" />
              <InfoRow icon={Calendar} label="Admissão" value="12/08/2018" />
            </div>
          </Card>

          <Card title="Permissões e papéis" subtitle="Controle de acesso baseado em função">
            <div className="space-y-3">
              {[
                { g: "Acadêmico", perms: ["Matrículas", "Histórico", "Trancamentos", "Exceções"] },
                { g: "Documentos", perms: ["Emitir declaração", "Assinar atestado", "Diplomas"] },
                { g: "Administração", perms: ["Gestão de usuários", "Auditoria", "Configurações"] },
              ].map((b) => (
                <div key={b.g}>
                  <div className="mb-1.5 text-[11px] font-semibold uppercase tracking-wide text-muted-foreground">{b.g}</div>
                  <div className="flex flex-wrap gap-1.5">
                    {b.perms.map((p) => (
                      <StatusBadge key={p} tone="info">{p}</StatusBadge>
                    ))}
                  </div>
                </div>
              ))}
            </div>
          </Card>

          <Card title="Atividade recente">
            <div className="divide-y">
              {[
                { t: "Aprovou exceção #2842", w: "há 2h" },
                { t: "Emitiu 14 declarações de matrícula", w: "há 5h" },
                { t: "Criou novo período letivo 2026.1", w: "ontem" },
                { t: "Atualizou política de senha", w: "há 3 dias" },
              ].map((a) => (
                <div key={a.t} className="flex items-center justify-between py-2.5 text-[13px]">
                  <span>{a.t}</span>
                  <span className="text-[12px] text-muted-foreground">{a.w}</span>
                </div>
              ))}
            </div>
          </Card>
        </div>

        <div className="space-y-5">
          <Card title="Segurança">
            <InfoRow icon={ShieldCheck} label="Autenticação" value="2FA ativo" />
            <InfoRow icon={Clock} label="Último acesso" value="Hoje, 09:14" />
            <InfoRow icon={MapPin} label="Localidade" value="São Paulo/SP" />
          </Card>
          <Card title="Setores que atende">
            <div className="flex flex-wrap gap-1.5">
              {["Graduação", "Pós-Graduação", "Extensão"].map((s) => (
                <StatusBadge key={s} tone="neutral">{s}</StatusBadge>
              ))}
            </div>
          </Card>
        </div>
      </TwoCol>
    </div>
  );
}

/* ---------- Psicólogo ---------- */

function PsicologoProfile() {
  return (
    <div className="space-y-6">
      <ProfileHeader
        initials="LM"
        name="Dra. Lia Mendes"
        role="Núcleo Psicopedagógico"
        tags={[
          { label: "Psicóloga", tone: "info" },
          { label: "CRP 06/123456", tone: "neutral" },
          { label: "Atendendo", tone: "success" },
        ]}
      />

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <StatCard label="Atendimentos no mês" value={68} tone="info" />
        <StatCard label="Estudantes em acompanhamento" value={42} tone="success" />
        <StatCard label="Encaminhamentos" value={9} tone="warning" />
        <StatCard label="Tempo médio de sessão" value="50min" tone="info" />
      </div>

      <TwoCol>
        <div className="lg:col-span-2 space-y-5">
          <Card title="Dados profissionais">
            <div className="grid gap-1 sm:grid-cols-2">
              <InfoRow icon={IdCard} label="Registro" value="CRP 06/123456" />
              <InfoRow icon={Award} label="Formação" value="Doutora em Psicologia Clínica" />
              <InfoRow icon={Building2} label="Setor" value="Núcleo de Apoio Psicopedagógico" />
              <InfoRow icon={Mail} label="E-mail" value="lia.mendes@acad.edu.br" />
              <InfoRow icon={Phone} label="Ramal" value="3320" />
              <InfoRow icon={MapPin} label="Sala" value="C-005 (acesso reservado)" />
            </div>
          </Card>

          <Card title="Especialidades">
            <div className="flex flex-wrap gap-1.5">
              {["Ansiedade acadêmica", "Orientação vocacional", "TDAH", "Acolhimento institucional", "Mediação de conflitos"].map((e) => (
                <StatusBadge key={e} tone="info">{e}</StatusBadge>
              ))}
            </div>
          </Card>

          <Card title="Agenda" subtitle="Próximos atendimentos (anonimizado)">
            <div className="divide-y">
              {[
                { h: "09:00", a: "Estudante #4821 — Acolhimento", t: "Presencial" },
                { h: "10:30", a: "Estudante #5102 — Retorno", t: "Online" },
                { h: "13:00", a: "Reunião com coordenação", t: "Interno" },
                { h: "15:00", a: "Estudante #4998 — Avaliação", t: "Presencial" },
              ].map((s) => (
                <div key={s.h} className="flex items-center justify-between py-2.5 text-[13px]">
                  <div className="flex items-center gap-3">
                    <span className="w-12 font-mono text-[12.5px] text-muted-foreground">{s.h}</span>
                    <span>{s.a}</span>
                  </div>
                  <StatusBadge tone="neutral">{s.t}</StatusBadge>
                </div>
              ))}
            </div>
          </Card>
        </div>

        <div className="space-y-5">
          <Card title="Atendimento">
            <InfoRow icon={Clock} label="Horários" value="Seg–Sex, 08h–17h" />
            <InfoRow icon={Calendar} label="Agendamento" value="Via portal do estudante" />
          </Card>
          <Card title="Confidencialidade">
            <p className="text-[12.5px] text-muted-foreground">
              Todos os registros seguem o código de ética profissional. Dados clínicos não ficam visíveis a outros perfis.
            </p>
          </Card>
        </div>
      </TwoCol>

      <Card title="Editar nota pública do perfil">
        <div className="grid gap-4 sm:grid-cols-2">
          <FormField label="Bio resumida"><Input className="h-10" defaultValue="Atendimento humanizado, foco em saúde mental estudantil." /></FormField>
          <FormField label="Mensagem de boas-vindas"><Input className="h-10" defaultValue="Você não está sozinho(a). Agende um horário." /></FormField>
          <FormField label="Sobre" full><Textarea rows={3} defaultValue="Psicóloga com 12 anos de experiência em contexto universitário." /></FormField>
        </div>
        <Separator className="my-4" />
        <div className="flex justify-end gap-2">
          <Button variant="outline">Cancelar</Button>
          <Button>Salvar alterações</Button>
        </div>
      </Card>
    </div>
  );
}
