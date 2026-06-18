import { createFileRoute } from "@tanstack/react-router";
import {
  FeaturePage, StatsRow, DataTable, StatusBadge, RowActionButton, FormField,
  SuccessBanner, SectionTitle,
} from "@/components/acadlab";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";
import { Lock } from "lucide-react";

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
  return (
    <div className="space-y-4">
      <ConfidentialNote />
      <StatsRow stats={[
        { label: "Casos ativos", value: 18, tone: "info" },
        { label: "Em triagem", value: 6, tone: "warning" },
        { label: "Encaminhamentos", value: 22, tone: "success" },
        { label: "Encerrados (mês)", value: 9, tone: "neutral" as any },
      ]} />
      <DataTable
        columns={[
          { key: "id", header: "Caso" },
          { key: "tipo", header: "Tipo" },
          { key: "prior", header: "Prioridade", render: (r) => <StatusBadge tone={r.prior === "Alta" ? "danger" : r.prior === "Média" ? "warning" : "neutral"}>{r.prior}</StatusBadge> },
          { key: "atend", header: "Último atendimento" },
          { key: "status", header: "Status", render: (r) => <StatusBadge tone={r.status === "Aberto" ? "info" : "success"}>{r.status}</StatusBadge> },
          { key: "acoes", header: "", render: () => <RowActionButton>Ver</RowActionButton>, align: "right" },
        ]}
        rows={[
          { id: "PSI-2025-0034", tipo: "Apoio emocional", prior: "Alta", atend: "10/03/2025", status: "Aberto" },
          { id: "PSI-2025-0021", tipo: "Acompanhamento acadêmico", prior: "Média", atend: "02/03/2025", status: "Aberto" },
          { id: "PSI-2025-0009", tipo: "Encaminhamento externo", prior: "Baixa", atend: "12/02/2025", status: "Encerrado" },
        ]}
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
