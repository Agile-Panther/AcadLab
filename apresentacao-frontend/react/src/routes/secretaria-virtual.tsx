import { useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import { toast } from "sonner";
import {
  AppShell,
  SectionTitle,
  StatsRow,
  DataTable,
  StatusBadge,
  RowActionButton,
  ActionBar,
  FormField,
  ValidationCallout,
  Stepper,
  SuccessBanner,
  useProfileSwitcher,
} from "@/components/acadlab";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { ArrowLeft, Check, X } from "lucide-react";

export const Route = createFileRoute("/secretaria-virtual")({
  head: () => ({ meta: [{ title: "Secretaria Virtual — AcadLab" }] }),
  component: Page,
});

type Sol = {
  id: string;
  tipo: string;
  aluno?: string;
  aberta: string;
  status: "Em análise" | "Pendente complemento" | "Deferida" | "Indeferida";
};

const minhasSolicitacoes: Sol[] = [
  {
    id: "PRO-2025-0231",
    tipo: "Declaração de matrícula",
    aberta: "12/03/2025",
    status: "Deferida",
  },
  {
    id: "PRO-2025-0240",
    tipo: "Revisão de nota — BD302",
    aberta: "15/03/2025",
    status: "Em análise",
  },
  {
    id: "PRO-2025-0245",
    tipo: "2ª via de carteirinha",
    aberta: "18/03/2025",
    status: "Pendente complemento",
  },
  {
    id: "PRO-2025-0210",
    tipo: "Aproveitamento de disciplina",
    aberta: "02/02/2025",
    status: "Indeferida",
  },
];

const filaSecretaria: Sol[] = [
  {
    id: "PRO-2025-0301",
    tipo: "Aproveitamento de disciplina",
    aluno: "Pedro Almeida",
    aberta: "19/03/2025",
    status: "Em análise",
  },
  {
    id: "PRO-2025-0302",
    tipo: "Revisão de nota — AED301",
    aluno: "Maria Santos",
    aberta: "19/03/2025",
    status: "Em análise",
  },
  {
    id: "PRO-2025-0303",
    tipo: "Trancamento de matrícula",
    aluno: "Júlia Rocha",
    aberta: "20/03/2025",
    status: "Em análise",
  },
  {
    id: "PRO-2025-0299",
    tipo: "Declaração de conclusão",
    aluno: "Lucas Pires",
    aberta: "18/03/2025",
    status: "Pendente complemento",
  },
  {
    id: "PRO-2025-0289",
    tipo: "2ª via de carteirinha",
    aluno: "Camila Reis",
    aberta: "17/03/2025",
    status: "Em análise",
  },
];

type View =
  | { kind: "list" }
  | { kind: "detail"; id: string }
  | { kind: "wizard"; step: 0 | 1 | 2 | 3 }
  | { kind: "triage"; id: string };

function Page() {
  const [view, setView] = useState<View>({ kind: "list" });
  const { active: perfil } = useProfileSwitcher([
    { value: "estudante", label: "Estudante", description: "Abre e acompanha protocolos" },
    { value: "secretaria", label: "Secretaria Acadêmica", description: "Tria, defere e indefere" },
  ]);

  const isSec = perfil === "secretaria";
  const subtitle = isSec ? "Visão Secretaria · Triagem de protocolos" : "Estudante: Maria Santos";
  const dataset = isSec ? filaSecretaria : minhasSolicitacoes;

  const [fila, setFila] = useState(filaSecretaria);
  const decidir = (id: string, status: Sol["status"]) =>
    setFila((p) => p.map((s) => (s.id === id ? { ...s, status } : s)));

  return (
    <AppShell title="Secretaria Virtual" subtitle={subtitle}>
      {view.kind === "list" && !isSec && (
        <div className="space-y-5">
          <StatsRow
            stats={[
              { label: "Minhas solicitações", value: minhasSolicitacoes.length, tone: "info" },
              {
                label: "Em análise",
                value: minhasSolicitacoes.filter((s) => s.status === "Em análise").length,
                tone: "warning",
              },
              {
                label: "Pendentes de complemento",
                value: minhasSolicitacoes.filter((s) => s.status === "Pendente complemento").length,
                tone: "danger",
              },
              {
                label: "Deferidas",
                value: minhasSolicitacoes.filter((s) => s.status === "Deferida").length,
                tone: "success",
              },
            ]}
          />
          <ActionBar
            searchPlaceholder="Buscar protocolo..."
            primaryLabel="Nova solicitação"
            onPrimary={() => setView({ kind: "wizard", step: 0 })}
          />
          <DataTable
            columns={[
              { key: "id", header: "Protocolo" },
              { key: "tipo", header: "Tipo" },
              { key: "aberta", header: "Aberta em" },
              {
                key: "status",
                header: "Status",
                render: (r) => (
                  <StatusBadge
                    tone={
                      r.status === "Deferida"
                        ? "success"
                        : r.status === "Indeferida"
                          ? "danger"
                          : r.status === "Pendente complemento"
                            ? "warning"
                            : "info"
                    }
                  >
                    {r.status}
                  </StatusBadge>
                ),
              },
              {
                key: "acoes",
                header: "",
                align: "right",
                render: (r) => (
                  <RowActionButton onClick={() => setView({ kind: "detail", id: r.id })}>
                    Abrir
                  </RowActionButton>
                ),
              },
            ]}
            rows={dataset}
          />
        </div>
      )}

      {view.kind === "list" && isSec && (
        <div className="space-y-5">
          <StatsRow
            stats={[
              {
                label: "Fila de triagem",
                value: fila.filter((s) => s.status === "Em análise").length,
                tone: "warning",
              },
              {
                label: "Aguardando aluno",
                value: fila.filter((s) => s.status === "Pendente complemento").length,
                tone: "danger",
              },
              {
                label: "Deferidas hoje",
                value: fila.filter((s) => s.status === "Deferida").length,
                tone: "success",
              },
              { label: "SLA médio", value: "1.8 dias", tone: "info" },
            ]}
          />
          <ActionBar
            searchPlaceholder="Buscar por aluno, tipo ou protocolo..."
            primaryLabel="Exportar fila"
          />
          <DataTable
            columns={[
              { key: "id", header: "Protocolo" },
              { key: "aluno", header: "Aluno" },
              { key: "tipo", header: "Tipo" },
              { key: "aberta", header: "Aberta em" },
              {
                key: "status",
                header: "Status",
                render: (r) => (
                  <StatusBadge
                    tone={
                      r.status === "Deferida"
                        ? "success"
                        : r.status === "Indeferida"
                          ? "danger"
                          : r.status === "Pendente complemento"
                            ? "warning"
                            : "info"
                    }
                  >
                    {r.status}
                  </StatusBadge>
                ),
              },
              {
                key: "acoes",
                header: "",
                align: "right",
                render: (r) => (
                  <RowActionButton onClick={() => setView({ kind: "triage", id: r.id })}>
                    Triar
                  </RowActionButton>
                ),
              },
            ]}
            rows={fila}
          />
        </div>
      )}

      {view.kind === "triage" &&
        (() => {
          const s = fila.find((x) => x.id === view.id)!;
          return (
            <div className="space-y-4">
              <Button variant="ghost" size="sm" onClick={() => setView({ kind: "list" })}>
                <ArrowLeft className="mr-1 h-4 w-4" /> Fila
              </Button>
              <div className="rounded-xl border bg-card p-6 shadow-card">
                <SectionTitle
                  title={`${s.id} — ${s.tipo}`}
                  subtitle={`${s.aluno} · Aberta em ${s.aberta}`}
                />
                <div className="mt-4 grid gap-3 text-[13px] sm:grid-cols-2">
                  <Info label="Aluno" value={s.aluno ?? "—"} />
                  <Info label="Tipo" value={s.tipo} />
                  <Info label="Curso" value="Engenharia de Software" />
                  <Info label="Período" value="2025.2" />
                </div>
                <FormField className="mt-4" label="Parecer da secretaria" full>
                  <Textarea
                    rows={4}
                    placeholder="Justifique a decisão (obrigatório em indeferimentos)."
                  />
                </FormField>
                <ValidationCallout className="mt-3" tone="info">
                  A decisão é registrada com seu usuário e horário no histórico do protocolo.
                </ValidationCallout>
                <div className="mt-4 flex flex-wrap justify-end gap-2">
                  <Button
                    variant="outline"
                    onClick={() => {
                      decidir(s.id, "Pendente complemento");
                      setView({ kind: "list" });
                    }}
                  >
                    Solicitar complemento
                  </Button>
                  <Button
                    variant="destructive"
                    onClick={() => {
                      decidir(s.id, "Indeferida");
                      setView({ kind: "list" });
                    }}
                  >
                    <X className="mr-1 h-4 w-4" /> Indeferir
                  </Button>
                  <Button
                    onClick={() => {
                      decidir(s.id, "Deferida");
                      setView({ kind: "list" });
                    }}
                  >
                    <Check className="mr-1 h-4 w-4" /> Deferir
                  </Button>
                </div>
              </div>
            </div>
          );
        })()}

      {view.kind === "detail" && <Detalhe id={view.id} onBack={() => setView({ kind: "list" })} />}

      {view.kind === "wizard" && (
        <Wizard
          step={view.step}
          onStep={(s) => setView({ kind: "wizard", step: s })}
          onDone={() => setView({ kind: "list" })}
        />
      )}
    </AppShell>
  );
}

function Info({ label, value }: { label: string; value: string }) {
  return (
    <div>
      <p className="text-[11px] uppercase tracking-wide text-muted-foreground">{label}</p>
      <p className="text-[13px] font-medium text-foreground">{value}</p>
    </div>
  );
}

function Detalhe({ id, onBack }: { id: string; onBack: () => void }) {
  const sol = minhasSolicitacoes.find((s) => s.id === id)!;
  const timeline = [
    { data: "18/03 09:12", evento: "Solicitação aberta" },
    { data: "18/03 14:30", evento: "Encaminhada para análise da secretaria" },
    ...(sol.status === "Pendente complemento"
      ? [
          {
            data: "20/03 10:00",
            evento: "Secretaria solicitou complemento: comprovante de residência atualizado",
          },
        ]
      : []),
    ...(sol.status === "Deferida"
      ? [
          {
            data: "21/03 16:20",
            evento: "Solicitação deferida — documento disponível para download",
          },
        ]
      : []),
    ...(sol.status === "Indeferida"
      ? [
          {
            data: "21/03 16:20",
            evento: "Solicitação indeferida — disciplina não equivalente conforme matriz",
          },
        ]
      : []),
  ];
  return (
    <div className="space-y-4">
      <Button variant="ghost" size="sm" onClick={onBack}>
        <ArrowLeft className="mr-1 h-4 w-4" /> Solicitações
      </Button>
      <div className="flex flex-wrap items-end justify-between gap-3">
        <SectionTitle title={`${sol.id} — ${sol.tipo}`} subtitle={`Aberta em ${sol.aberta}`} />
        <StatusBadge
          tone={
            sol.status === "Deferida"
              ? "success"
              : sol.status === "Indeferida"
                ? "danger"
                : sol.status === "Pendente complemento"
                  ? "warning"
                  : "info"
          }
        >
          {sol.status}
        </StatusBadge>
      </div>
      <div className="grid gap-4 lg:grid-cols-3">
        <div className="lg:col-span-2 rounded-xl border bg-card p-5 shadow-card">
          <SectionTitle title="Movimentações" />
          <ol className="mt-4 space-y-3 border-l border-border pl-5">
            {timeline.map((t, i) => (
              <li key={i} className="relative">
                <span className="absolute -left-[26px] top-1.5 h-2.5 w-2.5 rounded-full bg-primary" />
                <p className="text-[12px] text-muted-foreground">{t.data}</p>
                <p className="text-[13px] text-foreground">{t.evento}</p>
              </li>
            ))}
          </ol>
        </div>
        <div className="space-y-3">
          {sol.status === "Pendente complemento" && (
            <div className="rounded-xl border bg-card p-5 shadow-card">
              <SectionTitle title="Complementar solicitação" />
              <FormField className="mt-3" label="Documento" full>
                <Input type="file" className="h-10" />
              </FormField>
              <Button
                className="mt-3 w-full"
                onClick={() => {
                  toast.success("Complemento enviado. Solicitação retorna para análise.");
                  onBack();
                }}
              >
                Enviar complemento
              </Button>
            </div>
          )}
          {sol.status === "Em análise" && (
            <Button
              variant="destructive"
              className="w-full"
              onClick={() => {
                toast.success(`Solicitação ${sol.id} cancelada.`);
                onBack();
              }}
            >
              Cancelar solicitação
            </Button>
          )}
          {sol.status === "Deferida" && (
            <Button
              className="w-full"
              onClick={() => toast.success(`Documento de ${sol.id} baixado.`)}
            >
              Baixar documento
            </Button>
          )}
        </div>
      </div>
    </div>
  );
}

const wizSteps = [
  { key: "tipo", label: "Tipo" },
  { key: "form", label: "Dados" },
  { key: "doc", label: "Anexos" },
  { key: "rev", label: "Confirmação" },
];

function Wizard({
  step,
  onStep,
  onDone,
}: {
  step: 0 | 1 | 2 | 3;
  onStep: (s: 0 | 1 | 2 | 3) => void;
  onDone: () => void;
}) {
  const [tipo, setTipo] = useState<string | null>(null);
  const tipos = [
    "Declaração de matrícula",
    "Declaração de conclusão",
    "Revisão de nota",
    "Aproveitamento de disciplina",
    "2ª via de carteirinha",
    "Trancamento de matrícula",
  ];

  return (
    <div className="space-y-5">
      <Button variant="ghost" size="sm" onClick={onDone}>
        <ArrowLeft className="mr-1 h-4 w-4" /> Cancelar
      </Button>
      <Stepper steps={wizSteps} current={step} />
      {step === 0 && (
        <div className="rounded-xl border bg-card p-6 shadow-card">
          <SectionTitle title="Tipo de solicitação" />
          <div className="mt-4 grid grid-cols-2 gap-2">
            {tipos.map((t) => (
              <button
                key={t}
                onClick={() => setTipo(t)}
                className={`rounded-lg border p-3 text-left text-[13px] transition-colors ${tipo === t ? "border-primary bg-primary-soft text-primary" : "border-border bg-card hover:bg-primary/5"}`}
              >
                {t}
              </button>
            ))}
          </div>
          <div className="mt-4 flex justify-end">
            <Button disabled={!tipo} onClick={() => onStep(1)}>
              Avançar
            </Button>
          </div>
        </div>
      )}
      {step === 1 && (
        <div className="rounded-xl border bg-card p-6 shadow-card">
          <SectionTitle title={tipo ?? "Dados da solicitação"} />
          <div className="mt-4 grid grid-cols-2 gap-4">
            <FormField label="Finalidade" required full>
              <Input className="h-10" placeholder="Ex.: comprovação para estágio" />
            </FormField>
            <FormField label="Observações" full>
              <Textarea rows={3} />
            </FormField>
          </div>
          <div className="mt-4 flex justify-end gap-2">
            <Button variant="outline" onClick={() => onStep(0)}>
              Voltar
            </Button>
            <Button onClick={() => onStep(2)}>Avançar</Button>
          </div>
        </div>
      )}
      {step === 2 && (
        <div className="rounded-xl border bg-card p-6 shadow-card">
          <SectionTitle title="Documentos comprobatórios" />
          <FormField className="mt-3" label="Anexo" full>
            <Input type="file" className="h-10" />
          </FormField>
          <ValidationCallout className="mt-3" tone="info">
            Documentos obrigatórios variam por tipo de solicitação.
          </ValidationCallout>
          <div className="mt-4 flex justify-end gap-2">
            <Button variant="outline" onClick={() => onStep(1)}>
              Voltar
            </Button>
            <Button onClick={() => onStep(3)}>Avançar</Button>
          </div>
        </div>
      )}
      {step === 3 && (
        <div className="space-y-4">
          <SuccessBanner
            title="Solicitação registrada!"
            description="Protocolo PRO-2025-0312 · Aguardando análise da secretaria."
          />
          <Button onClick={onDone}>Voltar à lista</Button>
        </div>
      )}
    </div>
  );
}
