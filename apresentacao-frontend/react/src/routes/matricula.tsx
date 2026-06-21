import { useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import {
  AppShell,
  StatsRow,
  SuccessBanner,
  ScheduleGrid,
  SectionTitle,
  FormField,
  ValidationCallout,
  DataTable,
  StatusBadge,
  RowActionButton,
  Stepper,
  useProfileSwitcher,
} from "@/components/acadlab";
import type { ClassBlock } from "@/components/acadlab/organisms/ScheduleGrid";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";
import { Printer, Pencil, Lock, Plus, ArrowLeft, AlertTriangle } from "lucide-react";
import { toast } from "sonner";
import { useMatriculaAtual, isMatriculaBloqueada } from "@/lib/matricula";

type PedidoStatus = "Em análise" | "Deferida" | "Indeferida";
type Pedido = { id: string; aluno: string; tipo: string; aberta: string; status: PedidoStatus };

export const Route = createFileRoute("/matricula")({
  head: () => ({ meta: [{ title: "Matrícula — AcadLab" }] }),
  component: Page,
});

const blocosConfirmados: ClassBlock[] = [
  { day: 1, start: 8, duration: 2, title: "Algoritmos Avançados", code: "AED301", color: "info" },
  { day: 1, start: 14, duration: 2, title: "Banco de Dados II", code: "BD302", color: "success" },
  { day: 2, start: 10, duration: 2, title: "Testes de Software", code: "ES303", color: "warning" },
  { day: 3, start: 8, duration: 2, title: "Algoritmos Avançados", code: "AED301", color: "info" },
  { day: 3, start: 16, duration: 2, title: "Gestão de Projetos", code: "GP306", color: "info" },
  { day: 4, start: 10, duration: 2, title: "Testes de Software", code: "ES303", color: "warning" },
  { day: 5, start: 14, duration: 2, title: "Redes Avançadas", code: "RC305", color: "danger" },
  { day: 5, start: 16, duration: 2, title: "Banco de Dados II", code: "BD302", color: "success" },
];

type Oferta = {
  codigo: string;
  nome: string;
  turma: string;
  prof: string;
  horario: string;
  creditos: number;
  vagas: string;
  status: "Disponível" | "Selecionada" | "Conflito" | "Sem vaga";
};

const ofertasIniciais: Oferta[] = [
  {
    codigo: "AED401",
    nome: "Compiladores",
    turma: "T01",
    prof: "Carlos Lima",
    horario: "Seg/Qua 08-10",
    creditos: 4,
    vagas: "12/30",
    status: "Disponível",
  },
  {
    codigo: "ES401",
    nome: "Arquitetura de Software",
    turma: "T01",
    prof: "Ana Souza",
    horario: "Ter/Qui 10-12",
    creditos: 4,
    vagas: "20/30",
    status: "Disponível",
  },
  {
    codigo: "IA401",
    nome: "Inteligência Artificial",
    turma: "T01",
    prof: "Lia Mendes",
    horario: "Ter/Qui 14-16",
    creditos: 4,
    vagas: "8/25",
    status: "Disponível",
  },
  {
    codigo: "RC405",
    nome: "Computação em Nuvem",
    turma: "T01",
    prof: "Pedro Alves",
    horario: "Sex 14-18",
    creditos: 4,
    vagas: "25/25",
    status: "Sem vaga",
  },
  {
    codigo: "BD402",
    nome: "Big Data",
    turma: "T01",
    prof: "Marcos R.",
    horario: "Qua 14-18",
    creditos: 4,
    vagas: "10/30",
    status: "Disponível",
  },
  {
    codigo: "ES501",
    nome: "Eng. de Software III",
    turma: "T01",
    prof: "Júlia P.",
    horario: "Seg/Qua 10-12",
    creditos: 4,
    vagas: "15/30",
    status: "Disponível",
  },
];

type View =
  | { kind: "overview" }
  | { kind: "wizard"; step: 0 | 1 | 2 | 3 }
  | { kind: "ajuste" }
  | { kind: "trancarDisc" }
  | { kind: "trancarPeriodo" }
  | { kind: "excecao"; disciplina?: string };

function Page() {
  const { active: perfil } = useProfileSwitcher([
    { value: "estudante", label: "Estudante", description: "Faz e ajusta a própria matrícula" },
    {
      value: "secretaria",
      label: "Secretaria Acadêmica",
      description: "Gerencia exceções e trancamentos",
    },
  ]);
  const [view, setView] = useState<View>({ kind: "overview" });
  const [ofertas, setOfertas] = useState(ofertasIniciais);
  const matriculasQuery = useMatriculaAtual();
  const bloqueada = isMatriculaBloqueada(matriculasQuery.data);
  const selecionadas = ofertas.filter((o) => o.status === "Selecionada");
  const creditos = selecionadas.reduce((s, o) => s + o.creditos, 0);
  const temConflito =
    selecionadas.some((o) => o.codigo === "ES401") &&
    selecionadas.some((o) => o.codigo === "IA401");

  const toggle = (codigo: string) =>
    setOfertas((prev) =>
      prev.map((o) => {
        if (o.codigo !== codigo) return o;
        if (o.status === "Sem vaga") return o;
        if (o.status === "Selecionada") return { ...o, status: "Disponível" };
        return { ...o, status: "Selecionada" };
      }),
    );

  const subtitle =
    perfil === "secretaria"
      ? "Visão Secretaria Acadêmica · Exceções e trancamentos"
      : "Estudante: Maria Santos — 2025.2";

  return (
    <AppShell title="Matrícula" subtitle={subtitle}>
      {perfil === "secretaria" && <SecretariaView />}

      {perfil === "estudante" && view.kind === "overview" && (
        <Overview
          bloqueada={bloqueada}
          onNova={() => setView({ kind: "wizard", step: 0 })}
          onAjuste={() => setView({ kind: "ajuste" })}
          onTrancarDisc={() => setView({ kind: "trancarDisc" })}
          onTrancarPer={() => setView({ kind: "trancarPeriodo" })}
        />
      )}

      {perfil === "estudante" && view.kind === "wizard" && (
        <Wizard
          step={view.step}
          ofertas={ofertas}
          selecionadas={selecionadas}
          creditos={creditos}
          temConflito={temConflito}
          onToggle={toggle}
          onStep={(step) => setView({ kind: "wizard", step })}
          onCancel={() => setView({ kind: "overview" })}
          onExcecao={(d) => setView({ kind: "excecao", disciplina: d })}
        />
      )}

      {perfil === "estudante" && view.kind === "ajuste" && (
        <Ajuste onBack={() => setView({ kind: "overview" })} />
      )}
      {perfil === "estudante" && view.kind === "trancarDisc" && (
        <TrancarDisciplina onBack={() => setView({ kind: "overview" })} />
      )}
      {perfil === "estudante" && view.kind === "trancarPeriodo" && (
        <TrancarPeriodo onBack={() => setView({ kind: "overview" })} />
      )}
      {perfil === "estudante" && view.kind === "excecao" && (
        <Excecao disciplina={view.disciplina} onBack={() => setView({ kind: "wizard", step: 1 })} />
      )}
    </AppShell>
  );
}

function SecretariaView() {
  const [pedidos, setPedidos] = useState<Pedido[]>([
    {
      id: "MAT-2025-0231",
      aluno: "Maria Santos",
      tipo: "Exceção — pré-requisito",
      aberta: "12/03/2025",
      status: "Em análise",
    },
    {
      id: "MAT-2025-0240",
      aluno: "Pedro Almeida",
      tipo: "Trancamento de período",
      aberta: "15/03/2025",
      status: "Em análise",
    },
    {
      id: "MAT-2025-0245",
      aluno: "Júlia Rocha",
      tipo: "Trancamento — BD302",
      aberta: "18/03/2025",
      status: "Em análise",
    },
    {
      id: "MAT-2025-0210",
      aluno: "Lucas Pires",
      tipo: "Ajuste fora do prazo",
      aberta: "02/02/2025",
      status: "Deferida",
    },
  ]);
  const decidir = (id: string, status: "Deferida" | "Indeferida") => {
    setPedidos((p) => p.map((x) => (x.id === id ? { ...x, status } : x)));
    toast.success(`Solicitação ${id} ${status.toLowerCase()}.`);
  };
  return (
    <div className="space-y-5">
      <StatsRow
        stats={[
          { label: "Matrículas confirmadas", value: 1284, tone: "success" },
          {
            label: "Em análise",
            value: pedidos.filter((p) => p.status === "Em análise").length,
            tone: "warning",
          },
          { label: "Trancamentos abertos", value: 8, tone: "info" },
          {
            label: "Exceções deferidas",
            value: pedidos.filter((p) => p.status === "Deferida").length,
            tone: "success",
          },
        ]}
      />
      <SectionTitle
        title="Solicitações de matrícula"
        subtitle="Exceções, trancamentos e ajustes aguardando triagem."
      />
      <DataTable
        columns={[
          { key: "id", header: "Protocolo" },
          { key: "aluno", header: "Estudante" },
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
            render: (r) =>
              r.status === "Em análise" ? (
                <div className="flex justify-end gap-2">
                  <RowActionButton onClick={() => decidir(r.id, "Indeferida")}>
                    Indeferir
                  </RowActionButton>
                  <RowActionButton tone="info" onClick={() => decidir(r.id, "Deferida")}>
                    Deferir
                  </RowActionButton>
                </div>
              ) : (
                <span className="text-[12px] text-muted-foreground">—</span>
              ),
          },
        ]}
        rows={pedidos}
      />
    </div>
  );
}

function Overview({
  bloqueada,
  onNova,
  onAjuste,
  onTrancarDisc,
  onTrancarPer,
}: {
  bloqueada: boolean;
  onNova: () => void;
  onAjuste: () => void;
  onTrancarDisc: () => void;
  onTrancarPer: () => void;
}) {
  return (
    <div className="space-y-5">
      <SuccessBanner
        title="Matrícula 2025.2 confirmada"
        description="5 disciplinas · 18 créditos · janela de ajuste aberta até 23/03"
      />
      <StatsRow
        stats={[
          { label: "Disciplinas matriculadas", value: 5, tone: "info" },
          { label: "Créditos no período", value: 18, tone: "success" },
          { label: "Janela de ajuste", value: "11 dias", tone: "warning" },
          { label: "Pendências", value: 0, tone: "success" },
        ]}
      />

      {bloqueada && (
        <ValidationCallout tone="error">
          Matrícula bloqueada por pendência financeira. Regularize sua situação junto ao Setor
          Financeiro.
        </ValidationCallout>
      )}

      <div className="flex flex-wrap gap-2">
        <Button disabled={bloqueada} onClick={onNova}>
          <Plus className="mr-2 h-4 w-4" /> Iniciar Matrícula 2026.1
        </Button>
        <Button
          disabled={bloqueada}
          variant="outline"
          className="border-primary text-primary"
          onClick={onAjuste}
        >
          <Pencil className="mr-2 h-4 w-4" /> Solicitar Ajuste
        </Button>
        <Button
          disabled={bloqueada}
          variant="outline"
          className="border-warning text-warning"
          onClick={onTrancarDisc}
        >
          <Lock className="mr-2 h-4 w-4" /> Trancar Disciplina
        </Button>
        <Button
          disabled={bloqueada}
          variant="outline"
          className="border-destructive text-destructive"
          onClick={onTrancarPer}
        >
          Trancar Período
        </Button>
        <Button variant="secondary" onClick={() => toast.success("Grade impressa (PDF gerado).")}>
          <Printer className="mr-2 h-4 w-4" /> Imprimir grade
        </Button>
      </div>

      <SectionTitle
        title="Grade de horários — 2025.2"
        subtitle="Matrícula confirmada em 12/01/2025"
      />
      <ScheduleGrid blocks={blocosConfirmados} />
    </div>
  );
}

const wizardSteps = [
  { key: "plano", label: "Montar plano" },
  { key: "valid", label: "Validação" },
  { key: "conf", label: "Confirmação" },
  { key: "ok", label: "Concluído" },
];

function Wizard({
  step,
  ofertas,
  selecionadas,
  creditos,
  temConflito,
  onToggle,
  onStep,
  onCancel,
  onExcecao,
}: {
  step: 0 | 1 | 2 | 3;
  ofertas: Oferta[];
  selecionadas: Oferta[];
  creditos: number;
  temConflito: boolean;
  onToggle: (codigo: string) => void;
  onStep: (s: 0 | 1 | 2 | 3) => void;
  onCancel: () => void;
  onExcecao: (d?: string) => void;
}) {
  return (
    <div className="space-y-5">
      <div className="flex items-center gap-3">
        <Button variant="ghost" size="sm" onClick={onCancel}>
          <ArrowLeft className="mr-1 h-4 w-4" /> Cancelar matrícula
        </Button>
      </div>
      <Stepper steps={wizardSteps} current={step} />

      {step === 0 && (
        <>
          <div className="flex flex-wrap items-center gap-6 rounded-xl border bg-card p-4 text-[13px] shadow-card">
            <div>
              <span className="text-muted-foreground">Créditos </span>
              <span className="font-semibold">{creditos} / 24</span>
            </div>
            <div>
              <span className="text-muted-foreground">Disciplinas </span>
              <span className="font-semibold">{selecionadas.length}</span>
            </div>
            <div>
              <span className="text-muted-foreground">Conflitos </span>
              <span
                className={`font-semibold ${temConflito ? "text-destructive" : "text-success"}`}
              >
                {temConflito ? 1 : 0}
              </span>
            </div>
            <div className="ml-auto flex gap-2">
              <Button disabled={selecionadas.length === 0} onClick={() => onStep(1)}>
                Validar plano
              </Button>
            </div>
          </div>
          <DataTable
            columns={[
              { key: "codigo", header: "Código" },
              { key: "nome", header: "Disciplina" },
              { key: "prof", header: "Professor" },
              { key: "horario", header: "Horário" },
              { key: "creditos", header: "Cr.", align: "right" },
              { key: "vagas", header: "Vagas" },
              {
                key: "status",
                header: "Status",
                render: (r) => (
                  <StatusBadge
                    tone={
                      r.status === "Selecionada"
                        ? "success"
                        : r.status === "Sem vaga"
                          ? "danger"
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
                render: (r) =>
                  r.status === "Sem vaga" ? (
                    <RowActionButton
                      tone="neutral"
                      onClick={() => onExcecao(`${r.codigo} — ${r.nome}`)}
                    >
                      Solicitar exceção
                    </RowActionButton>
                  ) : (
                    <RowActionButton
                      tone={r.status === "Selecionada" ? "danger" : "info"}
                      onClick={() => onToggle(r.codigo)}
                    >
                      {r.status === "Selecionada" ? "Remover" : "Selecionar"}
                    </RowActionButton>
                  ),
              },
            ]}
            rows={ofertas}
          />
        </>
      )}

      {step === 1 && (
        <div className="space-y-4">
          <SectionTitle
            title="Validação automática do plano"
            subtitle="Pré-requisitos, choque de horário, créditos e correquisitos."
          />
          {temConflito && (
            <ValidationCallout tone="error">
              Choque de horário entre ES401 e IA401 às terças 10–12.
            </ValidationCallout>
          )}
          {creditos > 24 && (
            <ValidationCallout tone="error">
              Você excedeu o limite de 24 créditos.
            </ValidationCallout>
          )}
          {!temConflito && creditos <= 24 && (
            <ValidationCallout tone="info">
              Plano válido: {selecionadas.length} disciplinas · {creditos} créditos · 0 conflitos.
            </ValidationCallout>
          )}
          <DataTable
            columns={[
              { key: "codigo", header: "Código" },
              { key: "nome", header: "Disciplina" },
              { key: "horario", header: "Horário" },
              { key: "creditos", header: "Cr.", align: "right" },
            ]}
            rows={selecionadas}
          />
          <div className="flex justify-end gap-2">
            <Button variant="outline" onClick={() => onStep(0)}>
              Voltar
            </Button>
            <Button disabled={temConflito} onClick={() => onStep(2)}>
              Avançar para confirmação
            </Button>
          </div>
        </div>
      )}

      {step === 2 && (
        <div className="rounded-xl border bg-card p-6 shadow-card">
          <SectionTitle
            title="Confirmar matrícula 2026.1"
            subtitle="Revise as turmas antes de confirmar. Esta ação reserva as vagas."
          />
          <DataTable
            className="mt-4"
            columns={[
              { key: "codigo", header: "Código" },
              { key: "nome", header: "Disciplina" },
              { key: "turma", header: "Turma" },
              { key: "horario", header: "Horário" },
              { key: "creditos", header: "Cr.", align: "right" },
            ]}
            rows={selecionadas}
          />
          <div className="mt-4 flex items-center justify-between">
            <p className="text-[13px] text-muted-foreground">
              Total: <span className="font-semibold text-foreground">{creditos} créditos</span>
            </p>
            <div className="flex gap-2">
              <Button variant="outline" onClick={() => onStep(1)}>
                Voltar
              </Button>
              <Button onClick={() => onStep(3)}>Confirmar matrícula</Button>
            </div>
          </div>
        </div>
      )}

      {step === 3 && (
        <div className="space-y-4">
          <SuccessBanner
            title="Matrícula 2026.1 confirmada!"
            description={`${selecionadas.length} disciplinas · ${creditos} créditos. Protocolo MAT-2026-0012.`}
          />
          <Button variant="outline" onClick={onCancel}>
            Voltar à visão geral
          </Button>
        </div>
      )}
    </div>
  );
}

function Ajuste({ onBack }: { onBack: () => void }) {
  return (
    <div className="space-y-4">
      <Button variant="ghost" size="sm" onClick={onBack}>
        <ArrowLeft className="mr-1 h-4 w-4" /> Voltar
      </Button>
      <SectionTitle
        title="Ajuste de matrícula"
        subtitle="Janela aberta até 23/03/2025. Inclusões e exclusões são imediatas."
      />
      <DataTable
        columns={[
          { key: "codigo", header: "Código" },
          { key: "nome", header: "Disciplina" },
          {
            key: "acao",
            header: "Ação",
            render: (r) => (
              <StatusBadge tone={r.acao === "Incluir" ? "success" : "danger"}>{r.acao}</StatusBadge>
            ),
          },
          {
            key: "acoes",
            header: "",
            align: "right",
            render: (r) => (
              <RowActionButton
                tone="danger"
                onClick={() => toast.success(`Ajuste em ${r.codigo} revertido.`)}
              >
                Reverter
              </RowActionButton>
            ),
          },
        ]}
        rows={[
          { codigo: "IA302", nome: "Aprendizado de Máquina", acao: "Incluir" },
          { codigo: "GP306", nome: "Gestão de Projetos", acao: "Excluir" },
        ]}
      />
      <div className="flex justify-end gap-2">
        <Button variant="outline" onClick={onBack}>
          Cancelar
        </Button>
        <Button>Confirmar ajustes</Button>
      </div>
    </div>
  );
}

function TrancarDisciplina({ onBack }: { onBack: () => void }) {
  return (
    <div className="space-y-4">
      <Button variant="ghost" size="sm" onClick={onBack}>
        <ArrowLeft className="mr-1 h-4 w-4" /> Voltar
      </Button>
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Trancar disciplina" />
        <div className="mt-4 grid grid-cols-2 gap-4">
          <FormField label="Disciplina" required>
            <Input className="h-10" defaultValue="GP306 — Gestão de Projetos" />
          </FormField>
          <FormField label="Motivo" required>
            <Input className="h-10" placeholder="Ex.: incompatibilidade de horário com estágio" />
          </FormField>
          <FormField label="Justificativa" full>
            <Textarea rows={4} />
          </FormField>
        </div>
        <ValidationCallout className="mt-4" tone="info">
          O trancamento não conta como reprovação, mas impacta o CR.
        </ValidationCallout>
        <div className="mt-4 flex justify-end gap-2">
          <Button variant="outline" onClick={onBack}>
            Cancelar
          </Button>
          <Button variant="destructive">Trancar disciplina</Button>
        </div>
      </div>
    </div>
  );
}

function TrancarPeriodo({ onBack }: { onBack: () => void }) {
  return (
    <div className="space-y-4">
      <Button variant="ghost" size="sm" onClick={onBack}>
        <ArrowLeft className="mr-1 h-4 w-4" /> Voltar
      </Button>
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Trancamento do período letivo" />
        <div className="mt-3 flex items-start gap-3 rounded-lg bg-destructive-soft p-3 text-destructive">
          <AlertTriangle className="mt-0.5 h-5 w-5 shrink-0" />
          <p className="text-[13px]">
            Você está prestes a trancar <strong>todo o período 2025.2</strong>. Esta ação é
            irreversível dentro do semestre.
          </p>
        </div>
        <FormField className="mt-4" label="Justificativa" required full>
          <Textarea rows={4} />
        </FormField>
        <ValidationCallout className="mt-4" tone="info">
          Limite: 2 trancamentos consecutivos permitidos pela matriz.
        </ValidationCallout>
        <div className="mt-4 flex justify-end gap-2">
          <Button variant="outline" onClick={onBack}>
            Voltar
          </Button>
          <Button variant="destructive">Trancar período</Button>
        </div>
      </div>
    </div>
  );
}

function Excecao({ disciplina, onBack }: { disciplina?: string; onBack: () => void }) {
  return (
    <div className="space-y-4">
      <Button variant="ghost" size="sm" onClick={onBack}>
        <ArrowLeft className="mr-1 h-4 w-4" /> Voltar ao plano
      </Button>
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle
          title="Solicitar exceção de matrícula"
          subtitle="A solicitação será analisada pela coordenação."
        />
        <div className="mt-4 grid grid-cols-2 gap-4">
          <FormField label="Tipo" required>
            <Input className="h-10" defaultValue="Quebra de pré-requisito / vaga adicional" />
          </FormField>
          <FormField label="Disciplina" required>
            <Input className="h-10" defaultValue={disciplina ?? ""} />
          </FormField>
          <FormField label="Justificativa" required full>
            <Textarea rows={4} />
          </FormField>
          <FormField label="Anexo" full>
            <Input type="file" className="h-10" />
          </FormField>
        </div>
        <div className="mt-4 flex justify-end gap-2">
          <Button variant="outline" onClick={onBack}>
            Cancelar
          </Button>
          <Button>Enviar solicitação</Button>
        </div>
      </div>
    </div>
  );
}
