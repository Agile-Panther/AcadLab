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

export const Route = createFileRoute("/permanencia")({
  head: () => ({ meta: [{ title: "Permanência — AcadLab" }] }),
  component: Page,
});

const editalTone = (s: string) =>
  s === "ABERTO" ? "info" : s === "EM_ANALISE" ? "warning" : s === "ENCERRADO" ? "neutral" : "success";

const editalLabel = (s: string) =>
  ({ ABERTO: "Aberto", EM_ANALISE: "Em análise", ENCERRADO: "Encerrado", PUBLICADO: "Publicado" }[s] ?? s);

function Editais() {
  const { data = [], isLoading, isError } = useQuery({
    queryKey: ["permanencia", "editais"],
    queryFn: () => api.permanencia.listEditais(),
  });

  const abertos = data.filter((e) => e.status === "ABERTO").length;
  const totalVagas = data.reduce((a, e) => a + e.vagas, 0);
  const emAnalise = data.filter((e) => e.status === "EM_ANALISE").length;

  const rows = data.map((e) => ({
    id: `ED-${e.id}`,
    nome: e.programa,
    vagas: e.vagas,
    prazo: e.prazoInscricaoFim ?? "—",
    status: editalLabel(e.status),
    _status: e.status,
  }));

  return (
    <>
      <StatsRow stats={[
        { label: "Editais Abertos", value: isLoading ? "…" : abertos, tone: "info" },
        { label: "Total de Vagas", value: isLoading ? "…" : totalVagas, tone: "success" },
        { label: "Em análise", value: isLoading ? "…" : emAnalise, tone: "warning" },
        { label: "Total de Editais", value: isLoading ? "…" : data.length, tone: "danger" },
      ]} />
      <ActionBar searchPlaceholder="Buscar edital..." primaryLabel="Criar Edital" />
      {isError && <p className="text-sm text-destructive px-1">Não foi possível conectar ao servidor.</p>}
      <DataTable
        columns={[
          { key: "id", header: "Edital" },
          { key: "nome", header: "Nome" },
          { key: "vagas", header: "Vagas", align: "right" },
          { key: "prazo", header: "Prazo" },
          { key: "status", header: "Status", render: (r) => <StatusBadge tone={editalTone(r._status) as any}>{r.status}</StatusBadge> },
          { key: "acoes", header: "", render: () => <RowActionButton>Ver</RowActionButton>, align: "right" },
        ]}
        rows={rows}
      />
    </>
  );
}

function CriarEdital() {
  return (
    <div className="space-y-4">
      <SuccessBanner title="Edital criado com sucesso!" description="ED-2025-003 publicado para inscrições." />
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Criar Edital de Permanência" />
        <div className="mt-4 grid grid-cols-2 gap-4">
          <FormField label="Nome" required><Input className="h-10" defaultValue="Bolsa Permanência 2025.2" /></FormField>
          <FormField label="Vagas" required><Input className="h-10" defaultValue="120" /></FormField>
          <FormField label="Início das inscrições" required><Input className="h-10" type="date" /></FormField>
          <FormField label="Fim das inscrições" required><Input className="h-10" type="date" /></FormField>
          <FormField label="Critérios" full><Textarea rows={3} /></FormField>
          <FormField label="Documentos exigidos" full><Textarea rows={2} placeholder="RG, CPF, comprovante de renda..." /></FormField>
        </div>
        <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Publicar Edital</Button></div>
      </div>
    </div>
  );
}

function Inscrever() {
  return (
    <div className="space-y-4">
      <SuccessBanner title="Inscrição registrada com sucesso!" description="Protocolo BP-2025-0214 · Aguardando análise documental." />
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Inscrever-se em Programa de Permanência" />
        <div className="mt-4 grid grid-cols-2 gap-4">
          <FormField label="Edital" required><Input className="h-10" defaultValue="ED-2025-001 · Bolsa Permanência" /></FormField>
          <FormField label="Renda familiar (R$)" required><Input className="h-10" /></FormField>
          <FormField label="Documentos" required full><Input type="file" className="h-10" /></FormField>
          <FormField label="Justificativa socioeconômica" full><Textarea rows={4} /></FormField>
        </div>
        <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Enviar Inscrição</Button></div>
      </div>
    </div>
  );
}

function Analisar() {
  return (
    <>
      <SectionTitle title="Analisar Inscrição e Documentos" />
      <DataTable
        columns={[
          { key: "prot", header: "Protocolo" },
          { key: "nome", header: "Estudante" },
          { key: "renda", header: "Renda per capita", align: "right" },
          { key: "score", header: "Score", align: "right" },
          { key: "status", header: "Status", render: (r) => <StatusBadge tone={r.tone as any}>{r.status}</StatusBadge> },
          { key: "acoes", header: "", render: () => <div className="flex justify-end gap-1.5"><RowActionButton>Deferir</RowActionButton><RowActionButton tone="danger">Indeferir</RowActionButton></div>, align: "right" },
        ]}
        rows={[
          { prot: "BP-2025-0214", nome: "Maria Santos", renda: "R$ 412,00", score: 92, status: "Em análise", tone: "info" },
          { prot: "BP-2025-0203", nome: "João P.", renda: "R$ 850,00", score: 78, status: "Documentos OK", tone: "success" },
          { prot: "BP-2025-0199", nome: "Lara M.", renda: "R$ 1.200,00", score: 60, status: "Pendência", tone: "warning" },
        ]}
      />
    </>
  );
}

function Classificacao() {
  return (
    <div className="rounded-xl border bg-card p-6 shadow-card">
      <SectionTitle title="Classificação Preliminar" subtitle="Ranking gerado pelos critérios do edital." />
      <DataTable
        className="mt-4"
        columns={[
          { key: "pos", header: "#", align: "right" },
          { key: "nome", header: "Estudante" },
          { key: "score", header: "Score", align: "right" },
          { key: "status", header: "Resultado", render: (r) => <StatusBadge tone={r.status === "Classificado" ? "success" : "warning"}>{r.status}</StatusBadge> },
        ]}
        rows={[
          { pos: 1, nome: "Maria Santos", score: 92, status: "Classificado" },
          { pos: 2, nome: "João P.", score: 78, status: "Classificado" },
          { pos: 3, nome: "Lara M.", score: 60, status: "Lista de espera" },
        ]}
      />
    </div>
  );
}

function Renovacao() {
  return (
    <div className="space-y-4">
      <SuccessBanner title="Renovação solicitada com sucesso!" description="Sua solicitação seguirá para análise." />
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Solicitar Renovação do Benefício" />
        <div className="mt-4 grid grid-cols-2 gap-4">
          <FormField label="Benefício atual"><Input className="h-10" defaultValue="Bolsa Permanência 2025.1" /></FormField>
          <FormField label="Período seguinte"><Input className="h-10" defaultValue="2025.2" /></FormField>
          <FormField label="Comprovação atualizada" full><Input type="file" className="h-10" /></FormField>
        </div>
        <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Solicitar Renovação</Button></div>
      </div>
    </div>
  );
}

function Recurso() {
  return (
    <div className="rounded-xl border bg-card p-6 shadow-card">
      <SectionTitle title="Recurso contra Indeferimento" />
      <FormField className="mt-4" label="Argumentação" required full><Textarea rows={5} /></FormField>
      <FormField className="mt-4" label="Anexos" full><Input type="file" className="h-10" /></FormField>
      <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Enviar Recurso</Button></div>
    </div>
  );
}

function Resultado() {
  return (
    <div className="space-y-4">
      <SuccessBanner title="Resultado final publicado!" description="120 contemplados · 38 em lista de espera." />
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Publicar Resultado Final" />
        <div className="mt-4 flex justify-end"><Button>Publicar</Button></div>
      </div>
    </div>
  );
}

function Encerrar() {
  return (
    <div className="rounded-xl border bg-card p-6 shadow-card">
      <SectionTitle title="Encerrar Edital" />
      <FormField className="mt-4" label="Observações finais" full><Textarea rows={3} /></FormField>
      <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button variant="destructive">Encerrar Edital</Button></div>
    </div>
  );
}

function Page() {
  return (
    <FeaturePage
      title="Gestão de Permanência Acadêmica e Bolsas"
      subtitle="Editais, inscrições, análise e resultados"
      sections={[
        { value: "list", label: "Editais", content: <Editais /> },
        { value: "criar", label: "Criar", content: <CriarEdital /> },
        { value: "ins", label: "Inscrever", content: <Inscrever /> },
        { value: "anal", label: "Analisar", content: <Analisar /> },
        { value: "class", label: "Classificação", content: <Classificacao /> },
        { value: "ren", label: "Renovação", content: <Renovacao /> },
        { value: "rec", label: "Recurso", content: <Recurso /> },
        { value: "res", label: "Resultado", content: <Resultado /> },
        { value: "enc", label: "Encerrar", content: <Encerrar /> },
      ]}
    />
  );
}
