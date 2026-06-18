import { createFileRoute } from "@tanstack/react-router";
import {
  FeaturePage, StatsRow, DataTable, StatusBadge, RowActionButton, FormField,
  SuccessBanner, SectionTitle, ProgressRow, ActionBar,
} from "@/components/acadlab";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";

export const Route = createFileRoute("/atividades-complementares")({
  head: () => ({ meta: [{ title: "Atividades Complementares — AcadLab" }] }),
  component: Page,
});

const tone = (s: string) =>
  s === "Deferida" ? "success" : s === "Indeferida" ? "danger" : "info";

function Saldo() {
  return (
    <>
      <StatsRow stats={[
        { label: "Horas Validadas", value: 180, tone: "success" },
        { label: "Em análise", value: 24, tone: "info" },
        { label: "Pendentes", value: 32, tone: "warning" },
        { label: "Exigência total", value: 200, tone: "danger" },
      ]} />
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Saldo de Horas por Categoria" subtitle="Acompanhe horas exigidas vs. cumpridas em cada categoria." />
        <div className="mt-4 grid grid-cols-1 gap-4 lg:grid-cols-2">
          <ProgressRow label="Cursos & Certificações" current={80} total={80} tone="success" />
          <ProgressRow label="Eventos científicos" current={40} total={60} tone="warning" />
          <ProgressRow label="Projetos de extensão" current={40} total={40} tone="success" />
          <ProgressRow label="Atividades culturais" current={20} total={20} tone="success" />
        </div>
      </div>
    </>
  );
}

function Submeter() {
  return (
    <div className="space-y-4">
      <SuccessBanner title="Atividade submetida com sucesso!" description="Protocolo AC-2025-0091 · Aguardando análise." />
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Submeter Atividade Complementar" />
        <div className="mt-4 grid grid-cols-2 gap-4">
          <FormField label="Categoria" required><Input className="h-10" defaultValue="Cursos & Certificações" /></FormField>
          <FormField label="Carga horária (h)" required><Input className="h-10" defaultValue="20" /></FormField>
          <FormField label="Descrição" required full><Textarea rows={3} /></FormField>
          <FormField label="Comprovante" required full><Input type="file" className="h-10" /></FormField>
        </div>
        <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Submeter</Button></div>
      </div>
    </div>
  );
}

function Lista() {
  return (
    <>
      <ActionBar searchPlaceholder="Buscar atividade..." primaryLabel="Nova Atividade" />
      <DataTable
        columns={[
          { key: "id", header: "Protocolo" },
          { key: "cat", header: "Categoria" },
          { key: "ch", header: "CH", align: "right" },
          { key: "data", header: "Enviada em" },
          { key: "status", header: "Status", render: (r) => <StatusBadge tone={tone(r.status)}>{r.status}</StatusBadge> },
          { key: "acoes", header: "", render: () => <div className="flex justify-end gap-1.5"><RowActionButton>Ver</RowActionButton><RowActionButton tone="danger">Cancelar</RowActionButton></div>, align: "right" },
        ]}
        rows={[
          { id: "AC-2025-0091", cat: "Cursos & Certificações", ch: 20, data: "08/03/2025", status: "Em análise" },
          { id: "AC-2025-0072", cat: "Eventos científicos", ch: 12, data: "02/03/2025", status: "Deferida" },
          { id: "AC-2025-0060", cat: "Projetos de extensão", ch: 30, data: "18/02/2025", status: "Indeferida" },
        ]}
      />
    </>
  );
}

function Revisao() {
  return (
    <div className="rounded-xl border bg-card p-6 shadow-card">
      <SectionTitle title="Solicitar Revisão de Atividade" />
      <div className="mt-4 grid grid-cols-2 gap-4">
        <FormField label="Protocolo"><Input className="h-10" defaultValue="AC-2025-0060" /></FormField>
        <FormField label="Horas pleiteadas"><Input className="h-10" defaultValue="30" /></FormField>
        <FormField label="Justificativa" required full><Textarea rows={4} /></FormField>
      </div>
      <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Enviar</Button></div>
    </div>
  );
}

function Page() {
  return (
    <FeaturePage
      title="Gestão de Atividades Complementares"
      subtitle="Submissão, análise e saldo de horas"
      sections={[
        { value: "saldo", label: "Saldo", content: <Saldo /> },
        { value: "list", label: "Minhas Atividades", content: <Lista /> },
        { value: "sub", label: "Submeter", content: <Submeter /> },
        { value: "rev", label: "Revisão", content: <Revisao /> },
      ]}
    />
  );
}
