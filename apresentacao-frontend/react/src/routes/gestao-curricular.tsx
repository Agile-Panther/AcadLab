import { createFileRoute } from "@tanstack/react-router";
import { useQuery } from "@tanstack/react-query";
import {
  FeaturePage,
  StatsRow,
  ActionBar,
  DataTable,
  StatusBadge,
  RowActionButton,
  FormField,
  ValidationCallout,
  SuccessBanner,
  SectionTitle,
} from "@/components/acadlab";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";
import { api } from "@/lib/api";

export const Route = createFileRoute("/gestao-curricular")({
  head: () => ({ meta: [{ title: "Gestão Curricular — AcadLab" }] }),
  component: Page,
});

const CURSO_ID = 1;

const disciplinas = [
  { codigo: "AED201", nome: "Algoritmos e Estruturas de Dados", ch: 80, creditos: 4, periodo: "3º" },
  { codigo: "BD301", nome: "Banco de Dados I", ch: 60, creditos: 4, periodo: "4º" },
  { codigo: "ES303", nome: "Engenharia de Software II", ch: 60, creditos: 4, periodo: "5º" },
  { codigo: "RC305", nome: "Redes de Computadores", ch: 80, creditos: 4, periodo: "5º" },
];

function Painel() {
  const { data = [], isLoading, isError } = useQuery({
    queryKey: ["curriculo", CURSO_ID],
    queryFn: () => api.curriculo.listByCurso(CURSO_ID),
  });

  const ativas = data.filter((m) => m.status === "ATIVA").length;

  const rows = data.map((m) => ({
    codigo: `MAT-${m.id}`,
    nome: m.nome,
    curso: `Curso ${m.cursoId}`,
    ch: "—",
    creditos: "—",
    status: m.status === "ATIVA" ? "Ativa" : "Inativa",
    _status: m.status,
  }));

  return (
    <>
      <StatsRow stats={[
        { label: "Matrizes Ativas", value: isLoading ? "…" : ativas, tone: "info" },
        { label: "Total de Matrizes", value: isLoading ? "…" : data.length, tone: "success" },
        { label: "Cursos Cadastrados", value: 8, tone: "warning" },
        { label: "Com Pré-requisitos", value: 94, tone: "danger" },
      ]} />
      <ActionBar searchPlaceholder="Buscar por nome do curso ou código..." primaryLabel="Nova Matriz Curricular" />
      {isError && <p className="text-sm text-destructive px-1">Não foi possível conectar ao servidor.</p>}
      <DataTable
        columns={[
          { key: "codigo", header: "Código" },
          { key: "nome", header: "Nome da Matriz" },
          { key: "curso", header: "Curso" },
          { key: "ch", header: "CH Mínima" },
          { key: "creditos", header: "Créditos", align: "right" },
          { key: "status", header: "Status", render: (r) => <StatusBadge tone={r._status === "ATIVA" ? "success" : "neutral"}>{r.status}</StatusBadge> },
          { key: "acoes", header: "Ações", render: () => <RowActionButton>Editar</RowActionButton>, align: "right" },
        ]}
        rows={rows}
      />
    </>
  );
}

function Criar() {
  return (
    <div className="grid grid-cols-2 gap-4 rounded-xl border bg-card p-6 shadow-card">
      <SectionTitle className="col-span-2" title="Criar Matriz Curricular" subtitle="Identificação da matriz e seleção das disciplinas que a compõem." />
      <FormField label="Código" required><Input className="h-10" defaultValue="MAT-2025-01" /></FormField>
      <FormField label="Curso" required><Input className="h-10" defaultValue="Engenharia de Software" /></FormField>
      <FormField label="Nome da Matriz" required full><Input className="h-10" defaultValue="Matriz 2025 — Engenharia de Software" /></FormField>
      <FormField label="CH Mínima" required><Input className="h-10" defaultValue="3.200h" /></FormField>
      <FormField label="Total de Créditos" required><Input className="h-10" defaultValue="240" /></FormField>
      <FormField label="Disciplinas selecionadas" full>
        <DataTable
          columns={[
            { key: "codigo", header: "Código" },
            { key: "nome", header: "Disciplina" },
            { key: "ch", header: "CH", align: "right" },
            { key: "periodo", header: "Período" },
            { key: "acoes", header: "", render: () => <RowActionButton tone="danger">Remover</RowActionButton>, align: "right" },
          ]}
          rows={disciplinas}
        />
      </FormField>
      <div className="col-span-2"><ValidationCallout>CH total atinge o mínimo da matriz.</ValidationCallout></div>
      <div className="col-span-2 flex justify-end gap-2 pt-2">
        <Button variant="outline">Cancelar</Button>
        <Button>Salvar Matriz</Button>
      </div>
    </div>
  );
}

function PreReqs() {
  return (
    <div className="rounded-xl border bg-card p-6 shadow-card">
      <SectionTitle title="Pré-requisitos e Correquisitos" subtitle="Vincule dependências para cada disciplina da matriz." />
      <div className="mt-4">
        <DataTable
          columns={[
            { key: "codigo", header: "Disciplina" },
            { key: "pre", header: "Pré-requisitos" },
            { key: "co", header: "Correquisitos" },
            { key: "acoes", header: "", render: () => <RowActionButton>Editar</RowActionButton>, align: "right" },
          ]}
          rows={[
            { codigo: "BD301", pre: "AED201", co: "—" },
            { codigo: "ES303", pre: "BD301", co: "—" },
            { codigo: "RC305", pre: "AED201", co: "ES303" },
          ]}
        />
      </div>
    </div>
  );
}

function Status() {
  return (
    <div className="space-y-4">
      <SuccessBanner title="Status da matriz atualizado." description="MAT-2024-01 — Matriz 2024 agora está Ativa para novas turmas." />
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Gerenciar status da matriz" />
        <div className="mt-4 grid grid-cols-2 gap-4">
          <FormField label="Matriz" required><Input className="h-10" defaultValue="MAT-2024-01" /></FormField>
          <FormField label="Novo status" required><Input className="h-10" defaultValue="Ativa" /></FormField>
          <FormField label="Vigência início"><Input className="h-10" type="date" /></FormField>
          <FormField label="Vigência fim"><Input className="h-10" type="date" /></FormField>
          <FormField label="Justificativa" full><Textarea rows={3} /></FormField>
        </div>
        <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Confirmar</Button></div>
      </div>
    </div>
  );
}

function Disciplinas() {
  return (
    <div className="space-y-4">
      <ActionBar searchPlaceholder="Buscar disciplina..." primaryLabel="Nova Disciplina" />
      <DataTable
        columns={[
          { key: "codigo", header: "Código" },
          { key: "nome", header: "Disciplina" },
          { key: "ch", header: "CH", align: "right" },
          { key: "creditos", header: "Créditos", align: "right" },
          { key: "periodo", header: "Período Sug." },
          { key: "acoes", header: "Ações", render: () => <RowActionButton>Editar</RowActionButton>, align: "right" },
        ]}
        rows={disciplinas}
      />
    </div>
  );
}

function Page() {
  return (
    <FeaturePage
      title="Gestão Curricular do Curso"
      subtitle="Matrizes, disciplinas, pré-requisitos e equivalências"
      sections={[
        { value: "painel", label: "Matrizes Curriculares", content: <Painel /> },
        { value: "criar", label: "Criar Matriz", content: <Criar /> },
        { value: "prereqs", label: "Pré-requisitos", content: <PreReqs /> },
        { value: "status", label: "Status", content: <Status /> },
        { value: "disciplinas", label: "Disciplinas", content: <Disciplinas /> },
      ]}
    />
  );
}
