import { createFileRoute } from "@tanstack/react-router";
import { useQuery } from "@tanstack/react-query";
import {
  FeaturePage, StatsRow, DataTable, StatusBadge, RowActionButton, FormField,
  ValidationCallout, SuccessBanner, SectionTitle, ProgressRow,
} from "@/components/acadlab";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";
import { api } from "@/lib/api";

export const Route = createFileRoute("/gestao-pedagogica")({
  head: () => ({ meta: [{ title: "Gestão Pedagógica — AcadLab" }] }),
  component: Page,
});

const TURMA_ID = 1;

function Painel() {
  const { data, isLoading, isError } = useQuery({
    queryKey: ["diario", TURMA_ID],
    queryFn: () => api.diarios.getByTurma(TURMA_ID),
  });

  const diarTone = (s: string) =>
    s === "ABERTO" ? "info" : s === "FECHADO" ? "success" : "warning";
  const diarLabel = (s: string) =>
    ({ ABERTO: "Aberto", FECHADO: "Fechado", PENDENTE: "Pendente" }[s] ?? s);

  return (
    <>
      <StatsRow stats={[
        { label: "Status do Diário", value: isLoading ? "…" : diarLabel(data?.status ?? ""), tone: diarTone(data?.status ?? "") },
        { label: "Turma", value: isLoading ? "…" : data ? `Turma ${data.turmaId}` : "—", tone: "info" },
        { label: "Média Mínima", value: isLoading ? "…" : data?.mediaMinima ?? "—", tone: "warning" },
        { label: "Freq. Mínima", value: isLoading ? "…" : data ? `${data.frequenciaMinima}%` : "—", tone: "danger" },
      ]} />
      {isError && <p className="text-sm text-destructive px-1">Não foi possível conectar ao servidor.</p>}
      <div className="rounded-xl border bg-card p-5 shadow-card">
        <SectionTitle
          title="Visão Geral do Diário de Turma"
          subtitle={isLoading ? "Carregando..." : data ? `Período ${data.dataInicioPeriodo} a ${data.dataFimPeriodo} · Prof. ${data.professorResponsavelId}` : "Nenhum diário de turma encontrado"}
        />
      </div>
    </>
  );
}

function RegistrarAula() {
  return (
    <div className="space-y-4">
      <SuccessBanner title="Aula registrada com sucesso!" description="Aula de 12/03/2025 incluída no plano de AED301-T01." />
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Registrar Aula" />
        <div className="mt-4 grid grid-cols-2 gap-4">
          <FormField label="Turma" required><Input className="h-10" defaultValue="AED301-T01" /></FormField>
          <FormField label="Data" required><Input className="h-10" type="date" /></FormField>
          <FormField label="Conteúdo ministrado" required full><Textarea rows={3} /></FormField>
          <FormField label="Observações" full><Textarea rows={2} /></FormField>
        </div>
        <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Registrar</Button></div>
      </div>
    </div>
  );
}

function Frequencia() {
  return (
    <div className="space-y-4">
      <SectionTitle title="Registrar Frequência" subtitle="AED301-T01 · 12/03/2025" />
      <DataTable
        columns={[
          { key: "mat", header: "Matrícula" },
          { key: "nome", header: "Estudante" },
          { key: "pres", header: "Presença", render: (r) => <StatusBadge tone={r.pres === "Presente" ? "success" : "danger"}>{r.pres}</StatusBadge> },
          { key: "faltas", header: "Faltas no semestre", align: "right" },
          { key: "acoes", header: "", render: () => <RowActionButton>Alternar</RowActionButton>, align: "right" },
        ]}
        rows={[
          { mat: "2023001", nome: "Maria Santos", pres: "Presente", faltas: 2 },
          { mat: "2023002", nome: "João Souza", pres: "Falta", faltas: 6 },
          { mat: "2023003", nome: "Ana Lima", pres: "Presente", faltas: 0 },
          { mat: "2023004", nome: "Carlos R.", pres: "Presente", faltas: 1 },
        ]}
      />
    </div>
  );
}

function Avaliacao() {
  return (
    <div className="rounded-xl border bg-card p-6 shadow-card">
      <SectionTitle title="Criar Avaliação" subtitle="Compõe a fórmula da média final." />
      <div className="mt-4 grid grid-cols-2 gap-4">
        <FormField label="Nome" required><Input className="h-10" defaultValue="Prova 1" /></FormField>
        <FormField label="Tipo" required><Input className="h-10" defaultValue="Prova" /></FormField>
        <FormField label="Peso" required><Input className="h-10" defaultValue="3" /></FormField>
        <FormField label="Data" required><Input className="h-10" type="date" /></FormField>
      </div>
      <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Salvar</Button></div>
    </div>
  );
}

function Fechar() {
  return (
    <div className="space-y-4">
      <SuccessBanner title="Resultado final fechado!" description="AED301-T01 · 28 aprovados, 5 em recuperação, 2 reprovados." />
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Fechar Resultado Final" />
        <StatsRow className="mt-4" stats={[
          { label: "Aprovados", value: 28, tone: "success" },
          { label: "Recuperação", value: 5, tone: "warning" },
          { label: "Reprovados", value: 2, tone: "danger" },
          { label: "Média da turma", value: "7.4", tone: "info" },
        ]} />
        <ValidationCallout className="mt-4">Todas as avaliações lançadas.</ValidationCallout>
        <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Fechar Resultado</Button></div>
      </div>
    </div>
  );
}

function Revisao() {
  return (
    <div className="rounded-xl border bg-card p-6 shadow-card">
      <SectionTitle title="Solicitar Revisão de Nota" />
      <div className="mt-4 grid grid-cols-2 gap-4">
        <FormField label="Disciplina" required><Input className="h-10" defaultValue="BD302" /></FormField>
        <FormField label="Avaliação" required><Input className="h-10" defaultValue="Prova 2" /></FormField>
        <FormField label="Justificativa" required full><Textarea rows={4} /></FormField>
      </div>
      <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Enviar</Button></div>
    </div>
  );
}

function CorrigirAula() {
  return (
    <div className="rounded-xl border bg-card p-6 shadow-card">
      <SectionTitle title="Corrigir Registro de Aula" subtitle="Alterações registradas em trilha de auditoria." />
      <div className="mt-4 grid grid-cols-2 gap-4">
        <FormField label="Aula"><Input className="h-10" defaultValue="AED301-T01 · 12/03/2025" /></FormField>
        <FormField label="Nova data"><Input className="h-10" type="date" /></FormField>
        <FormField label="Conteúdo corrigido" full><Textarea rows={3} /></FormField>
      </div>
      <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Salvar Correção</Button></div>
    </div>
  );
}

function Recuperacao() {
  return (
    <div className="space-y-4">
      <SectionTitle title="Nota de Recuperação" />
      <DataTable
        columns={[
          { key: "nome", header: "Estudante" },
          { key: "media", header: "Média final", align: "right" },
          { key: "rec", header: "Nota Rec.", align: "right", render: () => <Input className="h-8 w-20" defaultValue="6.5" /> },
          { key: "sit", header: "Situação Final", render: (r) => <StatusBadge tone={r.sit === "Aprovado" ? "success" : "danger"}>{r.sit}</StatusBadge> },
        ]}
        rows={[
          { nome: "João Souza", media: 4.5, sit: "Aprovado" },
          { nome: "Lia Tavares", media: 5.0, sit: "Aprovado" },
          { nome: "Roberto P.", media: 3.0, sit: "Reprovado" },
        ]}
      />
    </div>
  );
}

function VisualizarEstudante() {
  return (
    <div className="space-y-4">
      <SectionTitle title="Notas e Frequência (Estudante)" subtitle="Disciplina: Banco de Dados II · 2025.2" />
      <div className="grid grid-cols-1 gap-4 lg:grid-cols-3">
        <div className="rounded-xl border bg-card p-5 shadow-card lg:col-span-2">
          <DataTable
            columns={[
              { key: "av", header: "Avaliação" },
              { key: "peso", header: "Peso", align: "right" },
              { key: "nota", header: "Nota", align: "right" },
            ]}
            rows={[
              { av: "Prova 1", peso: 3, nota: 8.0 },
              { av: "Prova 2", peso: 3, nota: 7.5 },
              { av: "Trabalho", peso: 2, nota: 9.0 },
              { av: "Atividades", peso: 2, nota: 8.5 },
            ]}
          />
        </div>
        <div className="rounded-xl border bg-card p-5 shadow-card">
          <SectionTitle title="Resumo" />
          <div className="mt-3 space-y-3 text-[13px]">
            <ProgressRow label="Frequência" current={92} total={100} unit="%" tone="success" />
            <ProgressRow label="Média parcial" current={82} total={100} unit="pts" tone="info" />
            <p>Situação: <StatusBadge tone="success">Aprovado parcial</StatusBadge></p>
          </div>
        </div>
      </div>
    </div>
  );
}

function Page() {
  return (
    <FeaturePage
      title="Gestão Pedagógica da Turma"
      subtitle="Aulas, frequência, avaliações e fechamento"
      sections={[
        { value: "painel", label: "Painel", content: <Painel /> },
        { value: "aula", label: "Registrar Aula", content: <RegistrarAula /> },
        { value: "freq", label: "Frequência", content: <Frequencia /> },
        { value: "av", label: "Criar Avaliação", content: <Avaliacao /> },
        { value: "fechar", label: "Fechar Resultado", content: <Fechar /> },
        { value: "rev", label: "Revisão de Nota", content: <Revisao /> },
        { value: "corr", label: "Corrigir Aula", content: <CorrigirAula /> },
        { value: "rec", label: "Recuperação", content: <Recuperacao /> },
        { value: "vis", label: "Notas & Freq.", content: <VisualizarEstudante /> },
      ]}
    />
  );
}
