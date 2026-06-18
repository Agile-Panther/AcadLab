import { createFileRoute } from "@tanstack/react-router";
import {
  FeaturePage, StatsRow, DataTable, StatusBadge, RowActionButton, FormField,
  SuccessBanner, SectionTitle, ActionBar,
} from "@/components/acadlab";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";

export const Route = createFileRoute("/mobilidade")({
  head: () => ({ meta: [{ title: "Mobilidade Acadêmica — AcadLab" }] }),
  component: Page,
});

function Solicitar() {
  return (
    <div className="space-y-4">
      <SuccessBanner title="Solicitação enviada com sucesso!" description="MB-2025-018 · Aguardando análise do plano de estudos." />
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Solicitar Mobilidade Acadêmica" />
        <div className="mt-4 grid grid-cols-2 gap-4">
          <FormField label="Instituição destino" required><Input className="h-10" defaultValue="Universidade de Coimbra" /></FormField>
          <FormField label="País" required><Input className="h-10" defaultValue="Portugal" /></FormField>
          <FormField label="Período" required><Input className="h-10" defaultValue="2026.1" /></FormField>
          <FormField label="Modalidade" required><Input className="h-10" defaultValue="Intercâmbio" /></FormField>
          <FormField label="Plano de estudos" required full><Textarea rows={4} placeholder="Disciplinas pretendidas e equivalências..." /></FormField>
        </div>
        <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Enviar Solicitação</Button></div>
      </div>
    </div>
  );
}

function Analisar() {
  return (
    <div className="space-y-4">
      <SuccessBanner title="Plano de Estudos Autorizado!" description="Equivalências aprovadas pela coordenação." />
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Analisar Plano de Estudos" />
        <DataTable
          className="mt-4"
          columns={[
            { key: "ext", header: "Disciplina externa" },
            { key: "ch", header: "CH", align: "right" },
            { key: "eq", header: "Equivalente AcadLab" },
            { key: "status", header: "Decisão", render: (r) => <StatusBadge tone={r.status === "Aprovada" ? "success" : "warning"}>{r.status}</StatusBadge> },
          ]}
          rows={[
            { ext: "Estruturas de Dados (Coimbra)", ch: 60, eq: "AED201", status: "Aprovada" },
            { ext: "Engenharia de Software", ch: 60, eq: "ES302", status: "Aprovada" },
            { ext: "História da Computação", ch: 30, eq: "—", status: "Em análise" },
          ]}
        />
        <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Solicitar ajustes</Button><Button>Autorizar Plano</Button></div>
      </div>
    </div>
  );
}

function Resultado() {
  return (
    <div className="rounded-xl border bg-card p-6 shadow-card">
      <SectionTitle title="Registrar Resultado no Histórico" />
      <DataTable
        className="mt-4"
        columns={[
          { key: "eq", header: "Disciplina (AcadLab)" },
          { key: "nota", header: "Nota convertida", align: "right" },
          { key: "sit", header: "Situação", render: (r) => <StatusBadge tone={r.sit === "AP" ? "success" : "danger"}>{r.sit}</StatusBadge> },
        ]}
        rows={[
          { eq: "AED201", nota: 8.2, sit: "AP" },
          { eq: "ES302", nota: 7.9, sit: "AP" },
        ]}
      />
      <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Registrar no Histórico</Button></div>
    </div>
  );
}

function Acompanhar() {
  return (
    <>
      <StatsRow stats={[
        { label: "Solicitadas", value: 4, tone: "info" },
        { label: "Autorizadas", value: 2, tone: "success" },
        { label: "Em curso", value: 1, tone: "warning" },
        { label: "Concluídas", value: 6, tone: "neutral" as any },
      ]} />
      <ActionBar searchPlaceholder="Buscar solicitação..." primaryLabel="Nova Mobilidade" />
      <DataTable
        columns={[
          { key: "id", header: "Protocolo" },
          { key: "inst", header: "Instituição" },
          { key: "per", header: "Período" },
          { key: "status", header: "Status", render: (r) => <StatusBadge tone={r.tone as any}>{r.status}</StatusBadge> },
          { key: "acoes", header: "", render: () => <div className="flex justify-end gap-1.5"><RowActionButton>Ver</RowActionButton><RowActionButton tone="danger">Cancelar</RowActionButton></div>, align: "right" },
        ]}
        rows={[
          { id: "MB-2025-018", inst: "U. de Coimbra", per: "2026.1", status: "Solicitada", tone: "info" },
          { id: "MB-2024-009", inst: "Tec de Monterrey", per: "2025.1", status: "Em curso", tone: "warning" },
          { id: "MB-2023-014", inst: "U. de Buenos Aires", per: "2024.2", status: "Concluída", tone: "success" },
        ]}
      />
    </>
  );
}

function CancelarMob() {
  return (
    <div className="space-y-4">
      <SuccessBanner title="Cancelamento solicitado com sucesso." description="Pendente de aprovação." />
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Cancelar Mobilidade Autorizada" />
        <FormField className="mt-4" label="Justificativa" required full><Textarea rows={4} /></FormField>
        <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Voltar</Button><Button variant="destructive">Cancelar Mobilidade</Button></div>
      </div>
    </div>
  );
}

function Page() {
  return (
    <FeaturePage
      title="Gestão de Mobilidade Acadêmica"
      subtitle="Intercâmbio, aproveitamento e acompanhamento"
      sections={[
        { value: "acomp", label: "Acompanhar", content: <Acompanhar /> },
        { value: "sol", label: "Solicitar", content: <Solicitar /> },
        { value: "anal", label: "Plano de Estudos", content: <Analisar /> },
        { value: "res", label: "Resultado", content: <Resultado /> },
        { value: "canc", label: "Cancelar", content: <CancelarMob /> },
      ]}
    />
  );
}
