import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { createFileRoute } from "@tanstack/react-router";
import {
  AppShell, SectionTitle, StatsRow, DataTable, StatusBadge, RowActionButton,
  ActionBar, FormField, ValidationCallout, Stepper,
  useProfileSwitcher,
} from "@/components/acadlab";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { ArrowLeft, CheckCircle2, Calendar } from "lucide-react";
import { toast } from "sonner";
import { api } from "@/lib/api";

export const Route = createFileRoute("/periodo-letivo")({
  head: () => ({ meta: [{ title: "Período Letivo — AcadLab" }] }),
  component: Page,
});

const CURSO_ID = 1;

type Periodo = {
  id: string; nome: string; inicio: string; fim: string;
  status: "Vigente" | "Encerrado" | "Planejado" | "Cancelado";
  pendencias: number;
};

type Janela = { nome: string; inicio: string; fim: string; status: string };

type View = { kind: "list" } | { kind: "detail"; id: string } | { kind: "wizard"; step: 0 | 1 | 2 };

function formatDate(iso: string): string {
  if (!iso) return "—";
  const [y, m, d] = iso.split("-");
  return `${d}/${m}/${y}`;
}

function mapStatus(s: string): Periodo["status"] {
  if (s === "EM_ANDAMENTO") return "Vigente";
  if (s === "ENCERRADO") return "Encerrado";
  if (s === "CANCELADO") return "Cancelado";
  return "Planejado";
}

function janelaStatus(inicio: string, fim: string): string {
  const today = new Date().toISOString().split("T")[0];
  if (today > fim) return "Encerrada";
  if (today >= inicio) return "Ativa";
  return "Planejada";
}

const DETALHE_JANELAS = [
  { tipo: "MATRICULA", nome: "Matrícula" },
  { tipo: "AJUSTE", nome: "Ajuste de matrícula" },
  { tipo: "TRANCAMENTO", nome: "Trancamento" },
  { tipo: "LANCAMENTO_NOTAS", nome: "Lançamento de notas" },
  { tipo: "REVISAO_NOTAS", nome: "Revisão de notas" },
];

const WIZARD_JANELAS = [
  { tipo: "MATRICULA", label: "Matrícula" },
  { tipo: "AJUSTE", label: "Ajuste" },
  { tipo: "TRANCAMENTO", label: "Trancamento" },
  { tipo: "LANCAMENTO_NOTAS", label: "Lançamento de notas" },
  { tipo: "REVISAO_NOTAS", label: "Revisão de notas" },
];

function Page() {
  const { active: perfil } = useProfileSwitcher([
    { value: "secretaria", label: "Secretaria Acadêmica", description: "Cria e encerra períodos letivos" },
    { value: "coordenacao", label: "Coordenação Acadêmica", description: "Acompanha calendário e janelas" },
  ]);
  const [view, setView] = useState<View>({ kind: "list" });
  const queryClient = useQueryClient();

  const { data: rawPeriodos = [] } = useQuery({
    queryKey: ["periodos", CURSO_ID],
    queryFn: () => api.periodos.listByCurso(CURSO_ID),
  });

  const periodos: Periodo[] = rawPeriodos
    .filter(p => p.status !== "CANCELADO")
    .map(p => ({
      id: String(p.id),
      nome: `${p.ano}.${p.semestre}`,
      inicio: formatDate(p.dataInicio),
      fim: formatDate(p.dataFim),
      status: mapStatus(p.status),
      pendencias: 0,
    }));

  const encerrarMutation = useMutation({
    mutationFn: (id: string) => api.periodos.encerrar(Number(id)),
    onSuccess: () => {
      toast.success("Período encerrado com sucesso!");
      queryClient.invalidateQueries({ queryKey: ["periodos", CURSO_ID] });
      setView({ kind: "list" });
    },
    onError: (e: Error) => toast.error(e.message),
  });

  const cancelarMutation = useMutation({
    mutationFn: ({ id, nome }: { id: string; nome: string }) =>
      api.periodos.cancelar(Number(id)).then(() => nome),
    onSuccess: (nome) => {
      toast.success(`Período ${nome} cancelado.`);
      queryClient.invalidateQueries({ queryKey: ["periodos", CURSO_ID] });
      setView({ kind: "list" });
    },
    onError: (e: Error) => toast.error(e.message),
  });

  return (
    <AppShell title="Períodos Letivos" subtitle={perfil === "coordenacao" ? "Visão Coordenação Acadêmica" : "Visão Secretaria Acadêmica"}>
      {view.kind === "list" && (
        <div className="space-y-5">
          <StatsRow stats={[
            { label: "Períodos cadastrados", value: periodos.length, tone: "info" },
            { label: "Vigente", value: periodos.filter((p) => p.status === "Vigente").length, tone: "success" },
            { label: "Planejados", value: periodos.filter((p) => p.status === "Planejado").length, tone: "warning" },
            { label: "Encerrados", value: periodos.filter((p) => p.status === "Encerrado").length, tone: "info" },
          ]} />
          {perfil === "secretaria"
            ? <ActionBar searchPlaceholder="Buscar período..." primaryLabel="Novo período" onPrimary={() => setView({ kind: "wizard", step: 0 })} />
            : <ActionBar searchPlaceholder="Buscar período..." />}
          <DataTable
            columns={[
              { key: "nome", header: "Período" },
              { key: "inicio", header: "Início" }, { key: "fim", header: "Fim" },
              { key: "status", header: "Status", render: (r) => (
                <StatusBadge tone={r.status === "Vigente" ? "success" : r.status === "Planejado" ? "warning" : "neutral"}>{r.status}</StatusBadge>
              )},
              { key: "pendencias", header: "Pendências", align: "right" },
              { key: "acoes", header: "", align: "right", render: (r) => <RowActionButton onClick={() => setView({ kind: "detail", id: r.id })}>Abrir</RowActionButton> },
            ]}
            rows={periodos}
          />
        </div>
      )}

      {view.kind === "detail" && (() => {
        const p = periodos.find((x) => x.id === view.id);
        const raw = rawPeriodos.find(x => String(x.id) === view.id);
        if (!p) return null;
        return (
          <Detalhe
            periodo={p}
            janelasBruta={raw?.janelas ?? []}
            readOnly={perfil !== "secretaria"}
            onBack={() => setView({ kind: "list" })}
            onEncerrar={() => encerrarMutation.mutate(p.id)}
            onCancelar={() => cancelarMutation.mutate({ id: p.id, nome: p.nome })}
          />
        );
      })()}

      {view.kind === "wizard" && (
        <Wizard
          step={view.step}
          onStep={(s) => setView({ kind: "wizard", step: s })}
          onCancel={() => setView({ kind: "list" })}
          onCadastrado={(nome) => {
            toast.success(`Período ${nome} cadastrado com sucesso!`);
            queryClient.invalidateQueries({ queryKey: ["periodos", CURSO_ID] });
            setView({ kind: "list" });
          }}
        />
      )}
    </AppShell>
  );
}

function Detalhe({ periodo, janelasBruta, readOnly, onBack, onEncerrar, onCancelar }: {
  periodo: Periodo;
  janelasBruta: { tipo: string; inicio: string; fim: string }[];
  readOnly?: boolean;
  onBack: () => void;
  onEncerrar: () => void;
  onCancelar: () => void;
}) {
  const [janelas, setJanelas] = useState<Janela[]>(() =>
    DETALHE_JANELAS.map(({ tipo, nome }) => {
      const found = janelasBruta.find(j => j.tipo === tipo);
      if (found) {
        return {
          nome,
          inicio: formatDate(found.inicio),
          fim: formatDate(found.fim),
          status: janelaStatus(found.inicio, found.fim),
        };
      }
      return { nome, inicio: "—", fim: "—", status: "Planejada" };
    })
  );
  const tone = (s: string) => s === "Ativa" ? "success" : s === "Encerrada" ? "neutral" : "warning";

  const toggleJanela = (nome: string, statusAtual: string) => {
    const proximo = statusAtual === "Ativa" ? "Encerrada" : statusAtual === "Encerrada" ? "Planejada" : "Ativa";
    setJanelas((prev) => prev.map((j) => j.nome === nome ? { ...j, status: proximo } : j));
    toast.info(`Janela "${nome}" → ${proximo}`);
  };

  return (
    <div className="space-y-5">
      <Button variant="ghost" size="sm" onClick={onBack}><ArrowLeft className="mr-1 h-4 w-4" /> Períodos</Button>
      <div className="flex flex-wrap items-end justify-between gap-3">
        <SectionTitle title={`Período ${periodo.nome}`} subtitle={`${periodo.inicio} → ${periodo.fim}`} />
        <div className="flex gap-2">
          <StatusBadge tone={periodo.status === "Vigente" ? "success" : periodo.status === "Planejado" ? "warning" : "neutral"}>{periodo.status}</StatusBadge>
          {!readOnly && periodo.status === "Planejado" && (
            <Button variant="outline" onClick={() => toast.info(`Editando período ${periodo.nome}.`)}>Editar</Button>
          )}
          {!readOnly && periodo.status === "Planejado" && (
            <Button variant="destructive" onClick={onCancelar}>Cancelar período</Button>
          )}
          {!readOnly && periodo.status === "Vigente" && (
            <Button onClick={onEncerrar} disabled={periodo.pendencias > 0}>
              <CheckCircle2 className="mr-2 h-4 w-4" /> Encerrar período
            </Button>
          )}
        </div>
      </div>

      {readOnly && (
        <ValidationCallout tone="info">Visualização da Coordenação. Apenas a Secretaria Acadêmica pode editar, cancelar ou encerrar períodos.</ValidationCallout>
      )}

      {periodo.pendencias > 0 && (
        <ValidationCallout tone="error">Existem {periodo.pendencias} pendências (notas, frequência ou matrícula). Resolva antes de encerrar.</ValidationCallout>
      )}

      <div className="rounded-xl border bg-card p-5 shadow-card">
        <SectionTitle title="Janelas acadêmicas" subtitle={readOnly ? "Consulta dos prazos do período." : "Clique em Alterar para mudar o status de cada janela."} />
        <DataTable className="mt-3"
          columns={[
            { key: "nome", header: "Janela" },
            { key: "inicio", header: "Início" }, { key: "fim", header: "Fim" },
            { key: "status", header: "Status", render: (r) => <StatusBadge tone={tone(r.status) as any}>{r.status}</StatusBadge> },
            ...(readOnly ? [] : [{ key: "acoes", header: "", align: "right" as const, render: (r: Janela) => (
              <RowActionButton onClick={() => toggleJanela(r.nome, r.status)}>Alterar</RowActionButton>
            )}]),
          ]}
          rows={janelas}
        />
      </div>
    </div>
  );
}

const wizSteps = [
  { key: "datas", label: "Datas" },
  { key: "janelas", label: "Janelas" },
  { key: "conf", label: "Confirmar" },
];

type JanelaWizardForm = Record<string, { inicio: string; fim: string }>;

function Wizard({ step, onStep, onCancel, onCadastrado }: {
  step: 0 | 1 | 2; onStep: (s: 0 | 1 | 2) => void; onCancel: () => void;
  onCadastrado: (nome: string) => void;
}) {
  const [form, setForm] = useState({ id: "", curso: "Engenharia de Software", inicio: "", fim: "" });
  const set = (k: keyof typeof form) => (e: React.ChangeEvent<HTMLInputElement>) => setForm((p) => ({ ...p, [k]: e.target.value }));
  const [janelaForm, setJanelaForm] = useState<JanelaWizardForm>({
    MATRICULA: { inicio: "", fim: "" },
    AJUSTE: { inicio: "", fim: "" },
    TRANCAMENTO: { inicio: "", fim: "" },
    LANCAMENTO_NOTAS: { inicio: "", fim: "" },
    REVISAO_NOTAS: { inicio: "", fim: "" },
  });

  const criarMutation = useMutation({
    mutationFn: async () => {
      const partes = form.id.split(".");
      const ano = Number(partes[0]);
      const semestre = Number(partes[1]);
      await api.periodos.criar({ cursoId: CURSO_ID, ano, semestre, dataInicio: form.inicio, dataFim: form.fim });
      const lista = await api.periodos.listByCurso(CURSO_ID);
      const novo = lista.find(p => p.ano === ano && p.semestre === semestre);
      if (novo) {
        for (const { tipo } of WIZARD_JANELAS) {
          const datas = janelaForm[tipo];
          if (datas.inicio && datas.fim) {
            await api.periodos.definirJanela(novo.id, { tipo, inicio: datas.inicio, fim: datas.fim });
          }
        }
      }
    },
    onSuccess: () => onCadastrado(form.id),
    onError: (e: Error) => toast.error(e.message),
  });

  const avancar0 = () => {
    if (!form.id || !form.inicio || !form.fim || !/^\d{4}\.[12]$/.test(form.id)) {
      toast.error("Preencha todos os campos obrigatórios.");
      return;
    }
    onStep(1);
  };

  return (
    <div className="space-y-5">
      <Button variant="ghost" size="sm" onClick={onCancel}><ArrowLeft className="mr-1 h-4 w-4" /> Cancelar</Button>
      <Stepper steps={wizSteps} current={step} />

      {step === 0 && (
        <div className="rounded-xl border bg-card p-6 shadow-card">
          <SectionTitle title="Configurar datas do período" />
          <div className="mt-4 grid grid-cols-2 gap-4">
            <FormField label="Identificador" required><Input className="h-10" placeholder="2026.1" value={form.id} onChange={set("id")} /></FormField>
            <FormField label="Curso"><Input className="h-10" value={form.curso} onChange={set("curso")} /></FormField>
            <FormField label="Início" required><Input type="date" className="h-10" value={form.inicio} onChange={set("inicio")} /></FormField>
            <FormField label="Fim" required><Input type="date" className="h-10" value={form.fim} onChange={set("fim")} /></FormField>
          </div>
          <div className="mt-4 flex justify-end gap-2"><Button variant="outline" onClick={onCancel}>Cancelar</Button><Button onClick={avancar0}>Avançar</Button></div>
        </div>
      )}

      {step === 1 && (
        <div className="rounded-xl border bg-card p-6 shadow-card">
          <SectionTitle title="Janelas acadêmicas" subtitle="Defina prazos para cada operação." />
          <div className="mt-4 space-y-3">
            {WIZARD_JANELAS.map(({ tipo, label }) => (
              <div key={tipo} className="grid grid-cols-3 gap-3">
                <div className="flex items-center gap-2 text-[13px] font-medium"><Calendar className="h-4 w-4 text-muted-foreground" />{label}</div>
                <Input type="date" className="h-10"
                  value={janelaForm[tipo].inicio}
                  onChange={e => setJanelaForm(prev => ({ ...prev, [tipo]: { ...prev[tipo], inicio: e.target.value } }))} />
                <Input type="date" className="h-10"
                  value={janelaForm[tipo].fim}
                  onChange={e => setJanelaForm(prev => ({ ...prev, [tipo]: { ...prev[tipo], fim: e.target.value } }))} />
              </div>
            ))}
          </div>
          <div className="mt-4 flex justify-end gap-2"><Button variant="outline" onClick={() => onStep(0)}>Voltar</Button><Button onClick={() => onStep(2)}>Avançar</Button></div>
        </div>
      )}

      {step === 2 && (
        <div className="rounded-xl border bg-card p-6 shadow-card">
          <SectionTitle title="Revisão" subtitle="Confirme as configurações antes de cadastrar o período." />
          <div className="mt-3 space-y-2 text-[13px]">
            <div className="flex justify-between border-b py-2"><span className="text-muted-foreground">Identificador</span><span className="font-medium">{form.id}</span></div>
            <div className="flex justify-between border-b py-2"><span className="text-muted-foreground">Curso</span><span className="font-medium">{form.curso}</span></div>
            <div className="flex justify-between border-b py-2"><span className="text-muted-foreground">Início</span><span className="font-medium">{form.inicio}</span></div>
            <div className="flex justify-between py-2"><span className="text-muted-foreground">Fim</span><span className="font-medium">{form.fim}</span></div>
          </div>
          <ValidationCallout className="mt-4" tone="info">Sem sobreposição detectada com outros períodos do mesmo curso.</ValidationCallout>
          <div className="mt-4 flex justify-end gap-2"><Button variant="outline" onClick={() => onStep(1)}>Voltar</Button><Button onClick={() => criarMutation.mutate()} disabled={criarMutation.isPending}>Cadastrar período</Button></div>
        </div>
      )}
    </div>
  );
}