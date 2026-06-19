import { createFileRoute } from "@tanstack/react-router";
import { useQuery } from "@tanstack/react-query";
import {
  FeaturePage, StatsRow, DataTable, StatusBadge, RowActionButton, FormField,
  SuccessBanner, SectionTitle,
} from "@/components/acadlab";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";
import { Lock } from "lucide-react";
import { api } from "@/lib/api";

export const Route = createFileRoute("/psicopedagogico")({
  head: () => ({ meta: [{ title: "Psicopedagógico — AcadLab" }] }),
  component: Page,
});

function ConfidentialNote() {
  return (
    <div className="flex items-center gap-2 rounded-md border border-warning/40 bg-warning-soft px-3 py-2 text-[12px] text-warning">
      <Lock className="h-4 w-4" /> Conteúdo sigiloso · acesso restrito à equipe psicopedagógica.
    </div>
  );
}

function NovaSolicitacao() {
  return (
    <div className="space-y-4">
      <ConfidentialNote />
      <SuccessBanner title="Solicitação registrada com sucesso." description="Caso encaminhado para triagem." />
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Nova Solicitação de Apoio Psicopedagógico" />
        <div className="mt-4 grid grid-cols-2 gap-4">
          <FormField label="Tipo de demanda" required><Input className="h-10" defaultValue="Apoio emocional" /></FormField>
          <FormField label="Urgência" required><Input className="h-10" defaultValue="Média" /></FormField>
          <FormField label="Descrição" required full><Textarea rows={4} /></FormField>
        </div>
        <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Enviar</Button></div>
      </div>
    </div>
  );
}

function Triagem() {
  return (
    <div className="space-y-4">
      <ConfidentialNote />
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Realizar Triagem" />
        <div className="mt-4 grid grid-cols-2 gap-4">
          <FormField label="Prioridade"><Input className="h-10" defaultValue="Alta" /></FormField>
          <FormField label="Tipo"><Input className="h-10" defaultValue="Acompanhamento contínuo" /></FormField>
          <FormField label="Direcionamento" full><Textarea rows={3} /></FormField>
        </div>
        <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Concluir Triagem</Button></div>
      </div>
    </div>
  );
}

function Atendimento() {
  return (
    <div className="space-y-4">
      <ConfidentialNote />
      <SuccessBanner title="Atendimento registrado." description="Caso PSI-2025-0034 atualizado." />
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Registrar Atendimento e Encaminhamento" />
        <div className="mt-4 grid grid-cols-2 gap-4">
          <FormField label="Data" required><Input className="h-10" type="date" /></FormField>
          <FormField label="Profissional" required><Input className="h-10" defaultValue="Dra. Helena P." /></FormField>
          <FormField label="Encaminhamento"><Input className="h-10" defaultValue="Interno — psicologia" /></FormField>
          <FormField label="Notas do atendimento" required full><Textarea rows={4} /></FormField>
        </div>
        <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Salvar</Button></div>
      </div>
    </div>
  );
}

function AcaoPermanencia() {
  return (
    <div className="space-y-4">
      <ConfidentialNote />
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Ação de Permanência" />
        <div className="mt-4 grid grid-cols-2 gap-4">
          <FormField label="Tipo"><Input className="h-10" defaultValue="Bolsa emergencial" /></FormField>
          <FormField label="Responsável"><Input className="h-10" /></FormField>
          <FormField label="Plano de ação" full><Textarea rows={4} /></FormField>
        </div>
        <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Registrar Ação</Button></div>
      </div>
    </div>
  );
}

function Encerrar() {
  return (
    <div className="space-y-4">
      <ConfidentialNote />
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Encerrar Caso Psicopedagógico" />
        <div className="mt-4 grid grid-cols-2 gap-4">
          <FormField label="Desfecho"><Input className="h-10" defaultValue="Resolvido" /></FormField>
          <FormField label="Data de encerramento"><Input className="h-10" type="date" /></FormField>
          <FormField label="Observações" full><Textarea rows={3} /></FormField>
        </div>
        <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Encerrar Caso</Button></div>
      </div>
    </div>
  );
}

function Historico() {
  const { data = [], isLoading, isError } = useQuery({
    queryKey: ["apoio", "casos"],
    queryFn: () => api.apoio.listCasos(),
  });

  const ativos = data.filter((c) => c.status === "ABERTO").length;
  const triagem = data.filter((c) => c.status === "EM_TRIAGEM").length;
  const encerrados = data.filter((c) => c.status === "ENCERRADO").length;

  const rows = data.map((c) => ({
    id: `PSI-${c.id}`,
    tipo: `Caso ${c.id}`,
    estud: `Estudante ${c.estudanteId}`,
    resp: c.responsavelId ? `Responsável ${c.responsavelId}` : "Não atribuído",
    status: c.status === "ABERTO" ? "Aberto" : c.status === "EM_TRIAGEM" ? "Em triagem" : "Encerrado",
    _status: c.status,
  }));

  return (
    <div className="space-y-4">
      <ConfidentialNote />
      <StatsRow stats={[
        { label: "Casos ativos", value: isLoading ? "…" : ativos, tone: "info" },
        { label: "Em triagem", value: isLoading ? "…" : triagem, tone: "warning" },
        { label: "Total de casos", value: isLoading ? "…" : data.length, tone: "success" },
        { label: "Encerrados", value: isLoading ? "…" : encerrados, tone: "neutral" as any },
      ]} />
      {isError && <p className="text-sm text-destructive px-1">Não foi possível conectar ao servidor.</p>}
      <DataTable
        columns={[
          { key: "id", header: "Caso" },
          { key: "estud", header: "Estudante" },
          { key: "resp", header: "Responsável" },
          { key: "status", header: "Status", render: (r) => <StatusBadge tone={r._status === "ABERTO" ? "info" : r._status === "EM_TRIAGEM" ? "warning" : "success"}>{r.status}</StatusBadge> },
          { key: "acoes", header: "", render: () => <RowActionButton>Ver</RowActionButton>, align: "right" },
        ]}
        rows={rows}
      />
    </div>
  );
}

function Page() {
  return (
    <FeaturePage
      title="Apoio Psicopedagógico e Acompanhamento Discente"
      subtitle="Dados sensíveis — confidencialidade obrigatória"
      sections={[
        { value: "hist", label: "Histórico", content: <Historico /> },
        { value: "nova", label: "Nova Solicitação", content: <NovaSolicitacao /> },
        { value: "tri", label: "Triagem", content: <Triagem /> },
        { value: "atend", label: "Atendimento", content: <Atendimento /> },
        { value: "acao", label: "Ação Permanência", content: <AcaoPermanencia /> },
        { value: "enc", label: "Encerrar", content: <Encerrar /> },
      ]}
    />
  );
}
