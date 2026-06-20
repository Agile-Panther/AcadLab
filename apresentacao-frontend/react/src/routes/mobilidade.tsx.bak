import { useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import { useQuery, useMutation } from "@tanstack/react-query";
import {
  FeaturePage, StatsRow, DataTable, StatusBadge, RowActionButton, FormField,
  SuccessBanner, SectionTitle, ActionBar,
} from "@/components/acadlab";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";
import { api } from "@/lib/api";

export const Route = createFileRoute("/mobilidade")({
  head: () => ({ meta: [{ title: "Mobilidade Acadêmica — AcadLab" }] }),
  component: Page,
});

const ESTUDANTE_ID = 1;
const MOBILIDADE_ID = 1;
const HOJE = new Date().toISOString().split("T")[0];

function Solicitar() {
  const [instituicao, setInstituicao] = useState("Universidade de Coimbra");
  const { mutate, isPending, isError } = useMutation({
    mutationFn: () =>
      api.mobilidade.solicitar({ estudanteId: ESTUDANTE_ID, instituicaoDestino: instituicao }),
  });

  return (
    <div className="space-y-4">
      <SuccessBanner title="Solicitação enviada com sucesso!" description="MB-2025-018 · Aguardando análise do plano de estudos." />
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Solicitar Mobilidade Acadêmica" />
        <div className="mt-4 grid grid-cols-2 gap-4">
          <FormField label="Instituição destino" required>
            <Input className="h-10" value={instituicao} onChange={(e) => setInstituicao(e.target.value)} />
          </FormField>
          <FormField label="País" required><Input className="h-10" defaultValue="Portugal" /></FormField>
          <FormField label="Período" required><Input className="h-10" defaultValue="2026.1" /></FormField>
          <FormField label="Modalidade" required><Input className="h-10" defaultValue="Intercâmbio" /></FormField>
          <FormField label="Plano de estudos" required full><Textarea rows={4} placeholder="Disciplinas pretendidas e equivalências..." /></FormField>
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

const mobTone = (s: string) =>
  s === "AUTORIZADA" || s === "CONCLUIDA" ? "success" : s === "EM_CURSO" ? "warning" : "info";

const mobLabel = (s: string) =>
  ({ SOLICITADA: "Solicitada", AUTORIZADA: "Autorizada", EM_CURSO: "Em curso", CONCLUIDA: "Concluída", CANCELADA: "Cancelada" }[s] ?? s);

function Acompanhar() {
  const { data, isLoading, isError } = useQuery({
    queryKey: ["mobilidade", ESTUDANTE_ID],
    queryFn: () => api.mobilidade.getByEstudante(ESTUDANTE_ID),
  });

  const rows = data ? data.map(m => ({
    id: `MB-${m.id}`,
    inst: m.instituicaoDestino,
    per: "—",
    status: mobLabel(m.status),
    _status: m.status,
  })) : [];

  const first = data?.[0];

  return (
    <>
      <StatsRow stats={[
        { label: "Status atual", value: isLoading ? "…" : mobLabel(first?.status ?? ""), tone: mobTone(first?.status ?? "") },
        { label: "Instituição", value: isLoading ? "…" : first?.instituicaoDestino ?? "—", tone: "info" },
        { label: "Estudante", value: isLoading ? "…" : first ? `ID ${first.estudanteId}` : "—", tone: "warning" },
        { label: "Mobilidades", value: isLoading ? "…" : data?.length ?? 0, tone: "neutral" as any },
      ]} />
      <ActionBar searchPlaceholder="Buscar solicitação..." primaryLabel="Nova Mobilidade" />
      {isError && <p className="text-sm text-destructive px-1">Não foi possível conectar ao servidor.</p>}
      <DataTable
        columns={[
          { key: "id", header: "Protocolo" },
          { key: "inst", header: "Instituição" },
          { key: "per", header: "Período" },
          { key: "status", header: "Status", render: (r) => <StatusBadge tone={mobTone(r._status)}>{r.status}</StatusBadge> },
          { key: "acoes", header: "", render: () => <div className="flex justify-end gap-1.5"><RowActionButton>Ver</RowActionButton><RowActionButton tone="danger">Cancelar</RowActionButton></div>, align: "right" },
        ]}
        rows={rows}
      />
    </>
  );
}

function CancelarMob() {
  const [justificativa, setJustificativa] = useState("");
  const { mutate, isPending, isError } = useMutation({
    mutationFn: () =>
      api.mobilidade.solicitarCancelamento(MOBILIDADE_ID, { justificativa, hoje: HOJE }),
  });

  return (
    <div className="space-y-4">
      <SuccessBanner title="Cancelamento solicitado com sucesso." description="Pendente de aprovação." />
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Cancelar Mobilidade Autorizada" />
        <FormField className="mt-4" label="Justificativa" required full>
          <Textarea rows={4} value={justificativa} onChange={(e) => setJustificativa(e.target.value)} />
        </FormField>
        {isError && <p className="text-sm text-destructive mt-2">Erro ao solicitar cancelamento.</p>}
        <div className="mt-4 flex justify-end gap-2">
          <Button variant="outline">Voltar</Button>
          <Button variant="destructive" onClick={() => mutate()} disabled={isPending}>
            {isPending ? "Enviando…" : "Cancelar Mobilidade"}
          </Button>
        </div>
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
