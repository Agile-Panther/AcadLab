import { useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
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
  useProfileSwitcher,
  type StatusTone,
} from "@/components/acadlab";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { ArrowLeft, CheckCircle2, Calendar } from "lucide-react";
import { toast } from "sonner";

export const Route = createFileRoute("/periodo-letivo")({
  head: () => ({ meta: [{ title: "Período Letivo — AcadLab" }] }),
  component: Page,
});

type Periodo = {
  id: string;
  nome: string;
  inicio: string;
  fim: string;
  status: "Vigente" | "Encerrado" | "Planejado";
  pendencias: number;
};

type Janela = { nome: string; inicio: string; fim: string; status: string };

const periodosIniciais: Periodo[] = [
  {
    id: "2025.2",
    nome: "2025.2",
    inicio: "10/02/2025",
    fim: "10/07/2025",
    status: "Vigente",
    pendencias: 0,
  },
  {
    id: "2026.1",
    nome: "2026.1",
    inicio: "10/08/2025",
    fim: "20/12/2025",
    status: "Planejado",
    pendencias: 0,
  },
  {
    id: "2025.1",
    nome: "2025.1",
    inicio: "12/08/2024",
    fim: "20/12/2024",
    status: "Encerrado",
    pendencias: 0,
  },
];

type View = { kind: "list" } | { kind: "detail"; id: string } | { kind: "wizard"; step: 0 | 1 | 2 };

function Page() {
  const { active: perfil } = useProfileSwitcher([
    {
      value: "secretaria",
      label: "Secretaria Acadêmica",
      description: "Cria e encerra períodos letivos",
    },
    {
      value: "coordenacao",
      label: "Coordenação Acadêmica",
      description: "Acompanha calendário e janelas",
    },
  ]);
  const [periodos, setPeriodos] = useState(periodosIniciais);
  const [view, setView] = useState<View>({ kind: "list" });

  const encerrar = (id: string) => {
    setPeriodos((p) => p.map((x) => (x.id === id ? { ...x, status: "Encerrado" } : x)));
    toast.success("Período encerrado com sucesso!");
    setView({ kind: "list" });
  };

  const cancelarPeriodo = (id: string, nome: string) => {
    setPeriodos((p) => p.filter((x) => x.id !== id));
    toast.success(`Período ${nome} cancelado.`);
    setView({ kind: "list" });
  };

  const cadastrarPeriodo = (novo: Omit<Periodo, "pendencias">) => {
    setPeriodos((p) => [...p, { ...novo, pendencias: 0 }]);
    toast.success(`Período ${novo.nome} cadastrado com sucesso!`);
    setView({ kind: "list" });
  };

  return (
    <AppShell
      title="Períodos Letivos"
      subtitle={
        perfil === "coordenacao" ? "Visão Coordenação Acadêmica" : "Visão Secretaria Acadêmica"
      }
    >
      {view.kind === "list" && (
        <div className="space-y-5">
          <StatsRow
            stats={[
              { label: "Períodos cadastrados", value: periodos.length, tone: "info" },
              {
                label: "Vigente",
                value: periodos.filter((p) => p.status === "Vigente").length,
                tone: "success",
              },
              {
                label: "Planejados",
                value: periodos.filter((p) => p.status === "Planejado").length,
                tone: "warning",
              },
              {
                label: "Encerrados",
                value: periodos.filter((p) => p.status === "Encerrado").length,
                tone: "info",
              },
            ]}
          />
          {perfil === "secretaria" ? (
            <ActionBar
              searchPlaceholder="Buscar período..."
              primaryLabel="Novo período"
              onPrimary={() => setView({ kind: "wizard", step: 0 })}
            />
          ) : (
            <ActionBar searchPlaceholder="Buscar período..." />
          )}
          <DataTable
            columns={[
              { key: "nome", header: "Período" },
              { key: "inicio", header: "Início" },
              { key: "fim", header: "Fim" },
              {
                key: "status",
                header: "Status",
                render: (r) => (
                  <StatusBadge
                    tone={
                      r.status === "Vigente"
                        ? "success"
                        : r.status === "Planejado"
                          ? "warning"
                          : "neutral"
                    }
                  >
                    {r.status}
                  </StatusBadge>
                ),
              },
              { key: "pendencias", header: "Pendências", align: "right" },
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
            rows={periodos}
          />
        </div>
      )}

      {view.kind === "detail" &&
        (() => {
          const p = periodos.find((x) => x.id === view.id)!;
          return (
            <Detalhe
              periodo={p}
              readOnly={perfil !== "secretaria"}
              onBack={() => setView({ kind: "list" })}
              onEncerrar={() => encerrar(p.id)}
              onCancelar={() => cancelarPeriodo(p.id, p.nome)}
            />
          );
        })()}

      {view.kind === "wizard" && (
        <Wizard
          step={view.step}
          onStep={(s) => setView({ kind: "wizard", step: s })}
          onCancel={() => setView({ kind: "list" })}
          onCadastrar={cadastrarPeriodo}
        />
      )}
    </AppShell>
  );
}

function Detalhe({
  periodo,
  readOnly,
  onBack,
  onEncerrar,
  onCancelar,
}: {
  periodo: Periodo;
  readOnly?: boolean;
  onBack: () => void;
  onEncerrar: () => void;
  onCancelar: () => void;
}) {
  const [janelas, setJanelas] = useState<Janela[]>([
    { nome: "Matrícula", inicio: "10/02/2025", fim: "20/02/2025", status: "Encerrada" },
    { nome: "Ajuste de matrícula", inicio: "21/02/2025", fim: "10/03/2025", status: "Encerrada" },
    { nome: "Trancamento", inicio: "10/02/2025", fim: "10/05/2025", status: "Ativa" },
    { nome: "Lançamento de notas", inicio: "01/06/2025", fim: "30/06/2025", status: "Planejada" },
    { nome: "Revisão de notas", inicio: "01/07/2025", fim: "10/07/2025", status: "Planejada" },
  ]);
  const tone = (s: string) =>
    s === "Ativa" ? "success" : s === "Encerrada" ? "neutral" : "warning";

  const toggleJanela = (nome: string, statusAtual: string) => {
    const proximo =
      statusAtual === "Ativa" ? "Encerrada" : statusAtual === "Encerrada" ? "Planejada" : "Ativa";
    setJanelas((prev) => prev.map((j) => (j.nome === nome ? { ...j, status: proximo } : j)));
    toast.info(`Janela "${nome}" → ${proximo}`);
  };

  return (
    <div className="space-y-5">
      <Button variant="ghost" size="sm" onClick={onBack}>
        <ArrowLeft className="mr-1 h-4 w-4" /> Períodos
      </Button>
      <div className="flex flex-wrap items-end justify-between gap-3">
        <SectionTitle
          title={`Período ${periodo.nome}`}
          subtitle={`${periodo.inicio} → ${periodo.fim}`}
        />
        <div className="flex gap-2">
          <StatusBadge
            tone={
              periodo.status === "Vigente"
                ? "success"
                : periodo.status === "Planejado"
                  ? "warning"
                  : "neutral"
            }
          >
            {periodo.status}
          </StatusBadge>
          {!readOnly && periodo.status === "Planejado" && (
            <Button
              variant="outline"
              onClick={() => toast.info(`Editando período ${periodo.nome}.`)}
            >
              Editar
            </Button>
          )}
          {!readOnly && periodo.status === "Planejado" && (
            <Button variant="destructive" onClick={onCancelar}>
              Cancelar período
            </Button>
          )}
          {!readOnly && periodo.status === "Vigente" && (
            <Button onClick={onEncerrar} disabled={periodo.pendencias > 0}>
              <CheckCircle2 className="mr-2 h-4 w-4" /> Encerrar período
            </Button>
          )}
        </div>
      </div>

      {readOnly && (
        <ValidationCallout tone="info">
          Visualização da Coordenação. Apenas a Secretaria Acadêmica pode editar, cancelar ou
          encerrar períodos.
        </ValidationCallout>
      )}

      {periodo.pendencias > 0 && (
        <ValidationCallout tone="error">
          Existem {periodo.pendencias} pendências (notas, frequência ou matrícula). Resolva antes de
          encerrar.
        </ValidationCallout>
      )}

      <div className="rounded-xl border bg-card p-5 shadow-card">
        <SectionTitle
          title="Janelas acadêmicas"
          subtitle={
            readOnly
              ? "Consulta dos prazos do período."
              : "Clique em Alterar para mudar o status de cada janela."
          }
        />
        <DataTable
          className="mt-3"
          columns={[
            { key: "nome", header: "Janela" },
            { key: "inicio", header: "Início" },
            { key: "fim", header: "Fim" },
            {
              key: "status",
              header: "Status",
              render: (r) => (
                <StatusBadge tone={tone(r.status) as StatusTone}>{r.status}</StatusBadge>
              ),
            },
            ...(readOnly
              ? []
              : [
                  {
                    key: "acoes",
                    header: "",
                    align: "right" as const,
                    render: (r: Janela) => (
                      <RowActionButton onClick={() => toggleJanela(r.nome, r.status)}>
                        Alterar
                      </RowActionButton>
                    ),
                  },
                ]),
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

function Wizard({
  step,
  onStep,
  onCancel,
  onCadastrar,
}: {
  step: 0 | 1 | 2;
  onStep: (s: 0 | 1 | 2) => void;
  onCancel: () => void;
  onCadastrar: (p: Omit<Periodo, "pendencias">) => void;
}) {
  const [form, setForm] = useState({
    id: "",
    curso: "Engenharia de Software",
    inicio: "",
    fim: "",
  });
  const set = (k: keyof typeof form) => (e: React.ChangeEvent<HTMLInputElement>) =>
    setForm((p) => ({ ...p, [k]: e.target.value }));

  const avancar0 = () => {
    if (!form.id || !form.inicio || !form.fim) {
      toast.error("Preencha todos os campos obrigatórios.");
      return;
    }
    onStep(1);
  };

  const cadastrar = () => {
    onCadastrar({
      id: form.id,
      nome: form.id,
      inicio: form.inicio,
      fim: form.fim,
      status: "Planejado",
    });
  };

  return (
    <div className="space-y-5">
      <Button variant="ghost" size="sm" onClick={onCancel}>
        <ArrowLeft className="mr-1 h-4 w-4" /> Cancelar
      </Button>
      <Stepper steps={wizSteps} current={step} />

      {step === 0 && (
        <div className="rounded-xl border bg-card p-6 shadow-card">
          <SectionTitle title="Configurar datas do período" />
          <div className="mt-4 grid grid-cols-2 gap-4">
            <FormField label="Identificador" required>
              <Input className="h-10" placeholder="2026.1" value={form.id} onChange={set("id")} />
            </FormField>
            <FormField label="Curso">
              <Input className="h-10" value={form.curso} onChange={set("curso")} />
            </FormField>
            <FormField label="Início" required>
              <Input type="date" className="h-10" value={form.inicio} onChange={set("inicio")} />
            </FormField>
            <FormField label="Fim" required>
              <Input type="date" className="h-10" value={form.fim} onChange={set("fim")} />
            </FormField>
          </div>
          <div className="mt-4 flex justify-end gap-2">
            <Button variant="outline" onClick={onCancel}>
              Cancelar
            </Button>
            <Button onClick={avancar0}>Avançar</Button>
          </div>
        </div>
      )}

      {step === 1 && (
        <div className="rounded-xl border bg-card p-6 shadow-card">
          <SectionTitle title="Janelas acadêmicas" subtitle="Defina prazos para cada operação." />
          <div className="mt-4 space-y-3">
            {["Matrícula", "Ajuste", "Trancamento", "Lançamento de notas", "Revisão de notas"].map(
              (j) => (
                <div key={j} className="grid grid-cols-3 gap-3">
                  <div className="flex items-center gap-2 text-[13px] font-medium">
                    <Calendar className="h-4 w-4 text-muted-foreground" />
                    {j}
                  </div>
                  <Input type="date" className="h-10" />
                  <Input type="date" className="h-10" />
                </div>
              ),
            )}
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
          <SectionTitle
            title="Revisão"
            subtitle="Confirme as configurações antes de cadastrar o período."
          />
          <div className="mt-3 space-y-2 text-[13px]">
            <div className="flex justify-between border-b py-2">
              <span className="text-muted-foreground">Identificador</span>
              <span className="font-medium">{form.id}</span>
            </div>
            <div className="flex justify-between border-b py-2">
              <span className="text-muted-foreground">Curso</span>
              <span className="font-medium">{form.curso}</span>
            </div>
            <div className="flex justify-between border-b py-2">
              <span className="text-muted-foreground">Início</span>
              <span className="font-medium">{form.inicio}</span>
            </div>
            <div className="flex justify-between py-2">
              <span className="text-muted-foreground">Fim</span>
              <span className="font-medium">{form.fim}</span>
            </div>
          </div>
          <ValidationCallout className="mt-4" tone="info">
            Sem sobreposição detectada com outros períodos do mesmo curso.
          </ValidationCallout>
          <div className="mt-4 flex justify-end gap-2">
            <Button variant="outline" onClick={() => onStep(1)}>
              Voltar
            </Button>
            <Button onClick={cadastrar}>Cadastrar período</Button>
          </div>
        </div>
      )}
    </div>
  );
}
