import { createFileRoute } from "@tanstack/react-router";
import { useQuery } from "@tanstack/react-query";
import {
  FeaturePage, StatsRow, DataTable, StatusBadge, RowActionButton, FormField,
  SuccessBanner, SectionTitle, ActionBar,
} from "@/components/acadlab";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";
import { api } from "@/lib/api";

export const Route = createFileRoute("/secretaria-virtual")({
  head: () => ({ meta: [{ title: "Secretaria Virtual — AcadLab" }] }),
  component: Page,
});

const solTone = (s: string) =>
  s === "DEFERIDA" ? "success" : s === "INDEFERIDA" ? "danger"
  : s === "AGUARDANDO_COMPLEMENTO" ? "warning" : "info";

const solLabel = (s: string) =>
  ({ ABERTA: "Em análise", DEFERIDA: "Deferida", INDEFERIDA: "Indeferida", AGUARDANDO_COMPLEMENTO: "Aguardando complemento" }[s] ?? s);

function MinhasSolicitacoes() {
  const { data = [], isLoading, isError } = useQuery({
    queryKey: ["solicitacoes", "pendentes"],
    queryFn: () => api.solicitacoes.listPendentes(),
  });

  const emAnalise = data.filter((s) => s.status === "ABERTA").length;
  const aguardando = data.filter((s) => s.status === "AGUARDANDO_COMPLEMENTO").length;
  const deferidas = data.filter((s) => s.status === "DEFERIDA").length;
  const indeferidas = data.filter((s) => s.status === "INDEFERIDA").length;

  const rows = data.map((s) => ({
    id: `SEC-${s.protocoloId}`,
    tipo: s.tipo,
    data: s.dataAbertura,
    status: solLabel(s.status),
    _status: s.status,
  }));

  return (
    <>
      <StatsRow stats={[
        { label: "Em análise", value: isLoading ? "…" : emAnalise, tone: "info" },
        { label: "Aguardando complemento", value: isLoading ? "…" : aguardando, tone: "warning" },
        { label: "Deferidas", value: isLoading ? "…" : deferidas, tone: "success" },
        { label: "Indeferidas", value: isLoading ? "…" : indeferidas, tone: "danger" },
      ]} />
      <ActionBar searchPlaceholder="Buscar solicitação..." primaryLabel="Nova Solicitação" />
      {isError && <p className="text-sm text-destructive px-1">Não foi possível conectar ao servidor.</p>}
      <DataTable
        columns={[
          { key: "id", header: "Protocolo" },
          { key: "tipo", header: "Tipo" },
          { key: "data", header: "Aberta em" },
          { key: "status", header: "Status", render: (r) => <StatusBadge tone={solTone(r._status)}>{r.status}</StatusBadge> },
          { key: "acoes", header: "", render: () => <div className="flex justify-end gap-1.5"><RowActionButton>Ver</RowActionButton><RowActionButton tone="danger">Cancelar</RowActionButton></div>, align: "right" },
        ]}
        rows={rows}
      />
    </>
  );
}

function Nova() {
  return (
    <div className="space-y-4">
      <SuccessBanner title="Solicitação enviada com sucesso!" description="Protocolo SEC-2025-0193 · Prazo previsto: 5 dias úteis." />
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Nova Solicitação Acadêmica" />
        <div className="mt-4 grid grid-cols-2 gap-4">
          <FormField label="Tipo de serviço" required><Input className="h-10" defaultValue="Declaração de matrícula" /></FormField>
          <FormField label="Urgência"><Input className="h-10" defaultValue="Normal" /></FormField>
          <FormField label="Descrição" required full><Textarea rows={4} /></FormField>
          <FormField label="Anexos" full><Input type="file" className="h-10" /></FormField>
        </div>
        <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Enviar Solicitação</Button></div>
      </div>
    </div>
  );
}

function Deferimento() {
  return (
    <div className="space-y-4">
      <SuccessBanner title="Deferimento registrado." description="Solicitação SEC-2025-0192 marcada como Deferida." />
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Registrar Deferimento (Secretaria)" />
        <div className="mt-4 grid grid-cols-2 gap-4">
          <FormField label="Protocolo"><Input className="h-10" defaultValue="SEC-2025-0192" /></FormField>
          <FormField label="Decisão"><Input className="h-10" defaultValue="Deferida" /></FormField>
          <FormField label="Parecer" required full><Textarea rows={4} /></FormField>
        </div>
        <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Indeferir</Button><Button>Deferir</Button></div>
      </div>
    </div>
  );
}

function Complementar() {
  return (
    <div className="rounded-xl border bg-card p-6 shadow-card">
      <SectionTitle title="Complementar Solicitação" />
      <p className="mt-2 text-[13px] text-muted-foreground">A secretaria solicitou: "Anexar comprovante de pagamento da taxa."</p>
      <FormField className="mt-4" label="Comentário" full><Textarea rows={3} /></FormField>
      <FormField className="mt-4" label="Anexo" full><Input type="file" className="h-10" /></FormField>
      <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Enviar Complemento</Button></div>
    </div>
  );
}

function CancelarSol() {
  return (
    <div className="rounded-xl border bg-card p-6 shadow-card">
      <SectionTitle title="Cancelar Solicitação" />
      <FormField className="mt-4" label="Motivo do cancelamento" required full><Textarea rows={4} /></FormField>
      <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Voltar</Button><Button variant="destructive">Cancelar Solicitação</Button></div>
    </div>
  );
}

function Page() {
  return (
    <FeaturePage
      title="Secretaria Virtual Acadêmica"
      subtitle="Solicitações e requerimentos"
      sections={[
        { value: "list", label: "Minhas Solicitações", content: <MinhasSolicitacoes /> },
        { value: "nova", label: "Nova", content: <Nova /> },
        { value: "def", label: "Deferimento", content: <Deferimento /> },
        { value: "comp", label: "Complementar", content: <Complementar /> },
        { value: "canc", label: "Cancelar", content: <CancelarSol /> },
      ]}
    />
  );
}
