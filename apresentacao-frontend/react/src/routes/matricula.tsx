import { useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import { useQuery, useMutation } from "@tanstack/react-query";
import {
  FeaturePage, StatsRow, SuccessBanner, ScheduleGrid, SectionTitle, FormField,
  ValidationCallout, DataTable, StatusBadge, RowActionButton, ActionBar,
} from "@/components/acadlab";
import type { ClassBlock } from "@/components/acadlab/organisms/ScheduleGrid";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";
import { Printer, Pencil, Lock } from "lucide-react";
import { api } from "@/lib/api";

export const Route = createFileRoute("/matricula")({
  head: () => ({ meta: [{ title: "Matrícula — AcadLab" }] }),
  component: Page,
});

const MATRICULA_ID = 1;
const TURMA_PLACEHOLDER_ID = 1;
const DISCIPLINA_EXCECAO_ID = 1;
const HOJE = new Date().toISOString().split("T")[0];

const blocos: ClassBlock[] = [
  { day: 1, start: 8, duration: 2, title: "Algoritmos Avançados", code: "AED301", color: "info" },
  { day: 1, start: 14, duration: 2, title: "Banco de Dados II", code: "BD302", color: "success" },
  { day: 2, start: 10, duration: 2, title: "Testes de Software", code: "ES303", color: "warning" },
  { day: 3, start: 8, duration: 2, title: "Algoritmos Avançados", code: "AED301", color: "info" },
  { day: 3, start: 16, duration: 2, title: "Gestão de Projetos", code: "GP306", color: "info" },
  { day: 4, start: 10, duration: 2, title: "Testes de Software", code: "ES303", color: "warning" },
  { day: 5, start: 14, duration: 2, title: "Redes Avançadas", code: "RC305", color: "danger" },
  { day: 5, start: 16, duration: 2, title: "Banco de Dados II", code: "BD302", color: "success" },
];

const ofertas = [
  { codigo: "AED301", nome: "Algoritmos Avançados", turma: "T01", prof: "Carlos Lima", horario: "Seg/Qua 08-10", creditos: 4, status: "Selecionada", tone: "success" },
  { codigo: "BD302", nome: "Banco de Dados II", turma: "T01", prof: "Ana Souza", horario: "Seg 14 · Sex 16", creditos: 4, status: "Selecionada", tone: "success" },
  { codigo: "ES303", nome: "Testes de Software", turma: "T02", prof: "Marcos R.", horario: "Ter/Qui 10-12", creditos: 4, status: "Selecionada", tone: "success" },
  { codigo: "IA301", nome: "Inteligência Artificial", turma: "T01", prof: "Lia Mendes", horario: "Ter 14-16", creditos: 4, status: "Conflito", tone: "danger" },
  { codigo: "RC305", nome: "Redes Avançadas", turma: "T01", prof: "Pedro Alves", horario: "Sex 14-16", creditos: 4, status: "Disponível", tone: "info" },
];

function Painel() {
  const { data, isLoading, isError } = useQuery({
    queryKey: ["matricula", MATRICULA_ID],
    queryFn: () => api.matricula.getById(MATRICULA_ID),
  });

  const statusLabel = (s: string) =>
    ({ CONFIRMADA: "Confirmada", PENDENTE: "Pendente", CANCELADA: "Cancelada", EM_AJUSTE: "Em ajuste" }[s] ?? s);
  const statusTone = (s: string) =>
    s === "CONFIRMADA" ? "success" : s === "CANCELADA" ? "danger" : "warning";

  return (
    <>
      <StatsRow stats={[
        { label: "Status", value: isLoading ? "…" : data ? statusLabel(data.status) : "—", tone: data ? statusTone(data.status) : "neutral" as any },
        { label: "Período Letivo", value: isLoading ? "…" : data ? `Período ${data.periodoLetivoId}` : "—", tone: "info" },
        { label: "Janela de Ajuste", value: "—", tone: "warning" },
        { label: "Pendências", value: 0, tone: "neutral" as any },
      ]} />
      {isError && <p className="text-sm text-destructive px-1">Não foi possível conectar ao servidor.</p>}
      <div className="rounded-xl border bg-card p-5 shadow-card">
        <SectionTitle
          title="Visão geral da matrícula"
          subtitle={isLoading ? "Carregando..." : data ? `Matrícula ${data.id} · Estudante ${data.estudanteId} · ${statusLabel(data.status)}` : "Nenhuma matrícula ativa para este período"}
        />
      </div>
    </>
  );
}

function Montar() {
  return (
    <div className="space-y-4">
      <SectionTitle title="Montar Plano de Matrícula" subtitle="Selecione as turmas. O sistema valida pré-requisitos, choque de horário e limite de créditos." />
      <div className="flex flex-wrap items-center gap-3 rounded-xl border bg-card p-4 shadow-card">
        <div className="flex flex-1 items-center gap-6 text-[13px]">
          <div><span className="text-muted-foreground">Créditos selecionados </span><span className="font-semibold">16 / 24</span></div>
          <div><span className="text-muted-foreground">Disciplinas </span><span className="font-semibold">4</span></div>
          <div><span className="text-muted-foreground">Conflitos </span><span className="font-semibold text-destructive">1</span></div>
        </div>
        <Button>Avançar para Confirmação</Button>
      </div>
      <ActionBar searchPlaceholder="Buscar disciplina..." primaryLabel={undefined} showFilters />
      <DataTable
        columns={[
          { key: "codigo", header: "Código" },
          { key: "nome", header: "Disciplina" },
          { key: "turma", header: "Turma" },
          { key: "prof", header: "Professor" },
          { key: "horario", header: "Horário" },
          { key: "creditos", header: "Créditos", align: "right" },
          { key: "status", header: "Status", render: (r) => <StatusBadge tone={r.tone as any}>{r.status}</StatusBadge> },
          { key: "acoes", header: "", render: () => <RowActionButton>Selecionar</RowActionButton>, align: "right" },
        ]}
        rows={ofertas}
      />
      <ValidationCallout tone="error">Choque de horário entre ES303 e IA301 às terças 10–12.</ValidationCallout>
    </div>
  );
}

function Confirmar() {
  const { mutate, isPending, isError, isSuccess } = useMutation({
    mutationFn: () => api.matricula.confirmar(MATRICULA_ID, {}),
  });

  return (
    <div className="space-y-4">
      <div className="rounded-xl border bg-card p-5 shadow-card">
        <SectionTitle title="Confirmar Matrícula" subtitle="Revise o plano antes de confirmar." />
        <DataTable
          className="mt-4"
          columns={[
            { key: "codigo", header: "Código" },
            { key: "nome", header: "Disciplina" },
            { key: "turma", header: "Turma" },
            { key: "horario", header: "Horário" },
            { key: "creditos", header: "Créditos", align: "right" },
          ]}
          rows={ofertas.slice(0, 4)}
        />
        <div className="mt-4 flex items-center justify-between">
          <p className="text-[13px] text-muted-foreground">Total: <span className="font-semibold text-foreground">18 créditos</span></p>
          <div className="flex gap-2">
            <Button variant="outline">Voltar</Button>
            <Button onClick={() => mutate()} disabled={isPending}>
              {isPending ? "Confirmando…" : "Confirmar Matrícula"}
            </Button>
          </div>
        </div>
        {isError && <p className="mt-2 text-sm text-destructive">Erro ao confirmar matrícula.</p>}
        {isSuccess && <p className="mt-2 text-sm text-green-600">Matrícula confirmada com sucesso.</p>}
      </div>
    </div>
  );
}

function Confirmada() {
  return (
    <div className="space-y-5">
      <SuccessBanner
        title="Matrícula confirmada com sucesso!"
        description="Você está matriculado em 5 disciplinas · 18 créditos · Período 2025.2"
      />
      <SectionTitle title="Grade de Horários — 2025.2" subtitle="Matrícula confirmada em 12/01/2025" />
      <ScheduleGrid blocks={blocos} />
      <div className="flex flex-wrap gap-2 pt-2">
        <Button variant="outline" className="border-primary text-primary hover:bg-primary-soft"><Pencil className="mr-2 h-4 w-4" /> Solicitar Ajuste de Matrícula</Button>
        <Button variant="outline" className="border-warning text-warning hover:bg-warning-soft"><Lock className="mr-2 h-4 w-4" /> Trancar Disciplina</Button>
        <Button variant="secondary"><Printer className="mr-2 h-4 w-4" /> Imprimir</Button>
      </div>
    </div>
  );
}

function Ajuste() {
  return (
    <div className="space-y-4">
      <SectionTitle title="Ajuste de Matrícula" subtitle="Inclua ou exclua disciplinas dentro da janela de ajuste." />
      <DataTable
        columns={[
          { key: "codigo", header: "Código" },
          { key: "nome", header: "Disciplina" },
          { key: "acao", header: "Ação Atual", render: (r) => <StatusBadge tone={r.acao === "Incluir" ? "success" : "danger"}>{r.acao}</StatusBadge> },
          { key: "acoes", header: "", render: () => <RowActionButton tone="danger">Reverter</RowActionButton>, align: "right" },
        ]}
        rows={[
          { codigo: "IA302", nome: "Aprendizado de Máquina", acao: "Incluir" },
          { codigo: "GP306", nome: "Gestão de Projetos", acao: "Excluir" },
        ]}
      />
      <div className="flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Solicitar Ajuste</Button></div>
    </div>
  );
}

function Trancar() {
  const { mutate, isPending, isError } = useMutation({
    mutationFn: () =>
      api.matricula.trancarDisciplina(MATRICULA_ID, TURMA_PLACEHOLDER_ID, {
        hoje: HOJE,
        inicio: HOJE,
        fim: HOJE,
      }),
  });

  return (
    <div className="rounded-xl border bg-card p-6 shadow-card">
      <SectionTitle title="Trancar Disciplina" />
      <div className="mt-4 grid grid-cols-2 gap-4">
        <FormField label="Disciplina" required><Input className="h-10" defaultValue="GP306 — Gestão de Projetos" /></FormField>
        <FormField label="Motivo" required><Input className="h-10" /></FormField>
        <FormField label="Justificativa" full><Textarea rows={4} /></FormField>
      </div>
      <ValidationCallout className="mt-4" tone="info">O trancamento não conta como reprovação, mas impacta CR.</ValidationCallout>
      {isError && <p className="mt-2 text-sm text-destructive">Erro ao trancar disciplina.</p>}
      <div className="mt-4 flex justify-end gap-2">
        <Button variant="outline">Cancelar</Button>
        <Button variant="destructive" onClick={() => mutate()} disabled={isPending}>
          {isPending ? "Trancando…" : "Trancar Disciplina"}
        </Button>
      </div>
    </div>
  );
}

function Excecao() {
  const [motivo, setMotivo] = useState("");
  const { mutate, isPending, isError } = useMutation({
    mutationFn: () =>
      api.matricula.solicitarExcecao(MATRICULA_ID, {
        disciplinaId: DISCIPLINA_EXCECAO_ID,
        motivo,
      }),
  });

  return (
    <div className="space-y-4">
      <SuccessBanner title="Exceção deferida!" description="Sua solicitação de quebra de pré-requisito foi aprovada pela coordenação." />
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Solicitar Exceção de Matrícula" />
        <div className="mt-4 grid grid-cols-2 gap-4">
          <FormField label="Tipo de exceção" required><Input className="h-10" defaultValue="Quebra de pré-requisito" /></FormField>
          <FormField label="Disciplina" required><Input className="h-10" defaultValue="ES501 — Engenharia de Software III" /></FormField>
          <FormField label="Justificativa" required full>
            <Textarea rows={4} value={motivo} onChange={(e) => setMotivo(e.target.value)} />
          </FormField>
          <FormField label="Anexo" full><Input type="file" className="h-10" /></FormField>
        </div>
        {isError && <p className="text-sm text-destructive mt-2">Erro ao enviar solicitação.</p>}
        <div className="mt-4 flex justify-end gap-2">
          <Button variant="outline">Cancelar</Button>
          <Button onClick={() => mutate()} disabled={isPending}>
            {isPending ? "Enviando…" : "Enviar Solicitação"}
          </Button>
        </div>
      </div>
    </div>
  );
}

function TrancarPeriodo() {
  const { mutate, isPending, isError } = useMutation({
    mutationFn: () =>
      api.matricula.trancarPeriodo(MATRICULA_ID, {
        hoje: HOJE,
        inicioTrancamento: HOJE,
        fimTrancamento: HOJE,
        totalTrancamentos: 0,
        limiteTrancamentos: 2,
      }),
  });

  return (
    <div className="rounded-xl border bg-card p-6 shadow-card">
      <SectionTitle title="Trancamento do Período Letivo" />
      <p className="mt-2 text-[13px] text-muted-foreground">
        Você está prestes a trancar <span className="font-semibold text-foreground">todo o período 2025.2</span>. Esta ação é irreversível dentro do semestre.
      </p>
      <FormField className="mt-4" label="Justificativa" required full><Textarea rows={4} /></FormField>
      <ValidationCallout className="mt-4" tone="error">Limite máximo de 2 trancamentos consecutivos.</ValidationCallout>
      {isError && <p className="mt-2 text-sm text-destructive">Erro ao trancar período.</p>}
      <div className="mt-4 flex justify-end gap-2">
        <Button variant="outline">Voltar</Button>
        <Button variant="destructive" onClick={() => mutate()} disabled={isPending}>
          {isPending ? "Trancando…" : "Trancar Período"}
        </Button>
      </div>
    </div>
  );
}

function Page() {
  return (
    <FeaturePage
      title="Montagem e Ajuste de Matrícula"
      subtitle="Estudante: Maria Santos — 2025.2"
      sections={[
        { value: "painel", label: "Painel", content: <Painel /> },
        { value: "montar", label: "Montar Plano", content: <Montar /> },
        { value: "confirmar", label: "Confirmar", content: <Confirmar /> },
        { value: "confirmada", label: "Matrícula Confirmada", content: <Confirmada /> },
        { value: "ajuste", label: "Ajuste", content: <Ajuste /> },
        { value: "trancar", label: "Trancar Disciplina", content: <Trancar /> },
        { value: "excecao", label: "Exceção", content: <Excecao /> },
        { value: "tranperiodo", label: "Trancar Período", content: <TrancarPeriodo /> },
      ]}
    />
  );
}
