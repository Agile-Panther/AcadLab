import { createFileRoute } from "@tanstack/react-router";
import {
  FeaturePage, StatsRow, ActionBar, DataTable, StatusBadge, RowActionButton,
  FormField, SuccessBanner, SectionTitle, ValidationCallout,
} from "@/components/acadlab";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";

export const Route = createFileRoute("/periodo-letivo")({
  head: () => ({ meta: [{ title: "Período Letivo — AcadLab" }] }),
  component: Page,
});

const periodos = [
  { codigo: "2025.2", inicio: "04/08/2025", fim: "12/12/2025", status: "Planejado" },
  { codigo: "2025.1", inicio: "10/02/2025", fim: "27/06/2025", status: "Em andamento" },
  { codigo: "2024.2", inicio: "05/08/2024", fim: "13/12/2024", status: "Encerrado" },
];

const tone = (s: string) =>
  s === "Em andamento" ? "info" : s === "Encerrado" ? "neutral" : "warning";

function Painel() {
  return (
    <>
      <StatsRow stats={[
        { label: "Períodos Planejados", value: 2, tone: "warning" },
        { label: "Em Andamento", value: 1, tone: "info" },
        { label: "Encerrados", value: 12, tone: "neutral" as any, hint: "Histórico" },
        { label: "Janelas Abertas", value: 4, tone: "success" },
      ]} />
      <ActionBar searchPlaceholder="Buscar período..." primaryLabel="Cadastrar Período" />
      <DataTable
        columns={[
          { key: "codigo", header: "Período" },
          { key: "inicio", header: "Início" },
          { key: "fim", header: "Fim" },
          { key: "status", header: "Status", render: (r) => <StatusBadge tone={tone(r.status)}>{r.status}</StatusBadge> },
          { key: "acoes", header: "Ações", align: "right", render: () => <div className="flex justify-end gap-1.5"><RowActionButton>Editar</RowActionButton><RowActionButton tone="danger">Cancelar</RowActionButton></div> },
        ]}
        rows={periodos}
      />
    </>
  );
}

function Cadastrar() {
  return (
    <div className="rounded-xl border bg-card p-6 shadow-card">
      <SectionTitle title="Cadastrar Período Letivo" subtitle="Defina código, datas e modalidade." />
      <div className="mt-4 grid grid-cols-2 gap-4">
        <FormField label="Código" required><Input className="h-10" defaultValue="2025.2" /></FormField>
        <FormField label="Ano" required><Input className="h-10" defaultValue="2025" /></FormField>
        <FormField label="Início" required><Input className="h-10" type="date" /></FormField>
        <FormField label="Fim" required><Input className="h-10" type="date" /></FormField>
        <FormField label="Modalidade" required full><Input className="h-10" defaultValue="Presencial" /></FormField>
      </div>
      <ValidationCallout className="mt-4">Sem sobreposição com outros períodos.</ValidationCallout>
      <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Cadastrar</Button></div>
    </div>
  );
}

function Janelas() {
  return (
    <div className="rounded-xl border bg-card p-6 shadow-card">
      <SectionTitle title="Definir Janelas Acadêmicas" subtitle="Intervalos de matrícula, ajuste, trancamento e lançamento de notas." />
      <DataTable
        className="mt-4"
        columns={[
          { key: "janela", header: "Janela" },
          { key: "inicio", header: "Início" },
          { key: "fim", header: "Fim" },
          { key: "acoes", header: "", render: () => <RowActionButton>Editar</RowActionButton>, align: "right" },
        ]}
        rows={[
          { janela: "Matrícula", inicio: "15/07/2025", fim: "01/08/2025" },
          { janela: "Ajuste de matrícula", inicio: "04/08/2025", fim: "15/08/2025" },
          { janela: "Trancamento", inicio: "16/08/2025", fim: "20/10/2025" },
          { janela: "Lançamento de notas", inicio: "01/12/2025", fim: "18/12/2025" },
        ]}
      />
    </div>
  );
}

function Encerrar() {
  return (
    <div className="space-y-4">
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Encerrar Período Letivo" subtitle="Checklist de pré-condições." />
        <ul className="mt-4 space-y-2 text-[13px]">
          <li className="flex items-center gap-2 text-success">✓ Todas as turmas com notas lançadas</li>
          <li className="flex items-center gap-2 text-success">✓ Sem revisões de nota em aberto</li>
          <li className="flex items-center gap-2 text-warning">! 2 solicitações da secretaria pendentes</li>
        </ul>
        <FormField className="mt-4" label="Justificativa" full><Textarea rows={3} /></FormField>
        <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Voltar</Button><Button variant="destructive">Encerrar Período</Button></div>
      </div>
    </div>
  );
}

function Editar() {
  return (
    <div className="rounded-xl border bg-card p-6 shadow-card">
      <SectionTitle title="Editar Período Não Iniciado" />
      <p className="mt-2 text-[12px] text-muted-foreground">Edição permitida apenas para períodos com status “Planejado”.</p>
      <div className="mt-4 grid grid-cols-2 gap-4">
        <FormField label="Código" required><Input className="h-10" defaultValue="2025.2" /></FormField>
        <FormField label="Modalidade"><Input className="h-10" defaultValue="Presencial" /></FormField>
        <FormField label="Início" required><Input className="h-10" type="date" /></FormField>
        <FormField label="Fim" required><Input className="h-10" type="date" /></FormField>
      </div>
      <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Salvar</Button></div>
    </div>
  );
}

function Cancelar() {
  return (
    <div className="space-y-4">
      <SuccessBanner title="Período cancelado." description="Período 2025.2 cancelado conforme justificativa." />
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Cancelar Período Letivo" />
        <FormField className="mt-4" label="Justificativa" required full><Textarea rows={4} /></FormField>
        <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Voltar</Button><Button variant="destructive">Cancelar Período</Button></div>
      </div>
    </div>
  );
}

function Page() {
  return (
    <FeaturePage
      title="Planejamento do Período Letivo"
      subtitle="Semestres e janelas acadêmicas"
      sections={[
        { value: "painel", label: "Consultar", content: <Painel /> },
        { value: "cadastrar", label: "Cadastrar", content: <Cadastrar /> },
        { value: "janelas", label: "Janelas", content: <Janelas /> },
        { value: "encerrar", label: "Encerrar", content: <Encerrar /> },
        { value: "editar", label: "Editar", content: <Editar /> },
        { value: "cancelar", label: "Cancelar", content: <Cancelar /> },
      ]}
    />
  );
}
