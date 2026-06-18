import { createFileRoute } from "@tanstack/react-router";
import {
  FeaturePage, StatsRow, DataTable, StatusBadge, RowActionButton, FormField,
  SuccessBanner, SectionTitle, ValidationCallout, ActionBar,
} from "@/components/acadlab";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";
import { Printer } from "lucide-react";

export const Route = createFileRoute("/historico-academico")({
  head: () => ({ meta: [{ title: "Histórico Acadêmico — AcadLab" }] }),
  component: Page,
});

function Painel() {
  return (
    <>
      <StatsRow stats={[
        { label: "Estudantes Ativos", value: "2.184", tone: "info" },
        { label: "Em risco", value: 47, tone: "warning" },
        { label: "Aptos a colar grau", value: 86, tone: "success" },
        { label: "Lançamentos retificados", value: 12, tone: "danger" },
      ]} />
      <ActionBar searchPlaceholder="Buscar estudante por matrícula ou nome..." primaryLabel="Consolidar Resultados" />
      <DataTable
        columns={[
          { key: "mat", header: "Matrícula" },
          { key: "nome", header: "Estudante" },
          { key: "curso", header: "Curso" },
          { key: "cr", header: "CR", align: "right" },
          { key: "sit", header: "Situação", render: (r) => <StatusBadge tone={r.tone as any}>{r.sit}</StatusBadge> },
          { key: "acoes", header: "Ações", render: () => <RowActionButton>Ver histórico</RowActionButton>, align: "right" },
        ]}
        rows={[
          { mat: "2023001", nome: "Maria Santos", curso: "Eng. Software", cr: 8.4, sit: "Regular", tone: "success" },
          { mat: "2022014", nome: "Pedro Mota", curso: "ADS", cr: 5.2, sit: "Em risco", tone: "warning" },
          { mat: "2019099", nome: "Lucas Vieira", curso: "Ciência Comp.", cr: 7.9, sit: "Apto a colar", tone: "info" },
        ]}
      />
    </>
  );
}

function Consolidar() {
  return (
    <div className="space-y-4">
      <SuccessBanner title="Consolidação concluída!" description="84 turmas tiveram seus resultados transferidos para o histórico oficial." />
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Consolidar Resultados no Histórico" />
        <DataTable
          className="mt-4"
          columns={[
            { key: "turma", header: "Turma" },
            { key: "prof", header: "Professor" },
            { key: "estud", header: "Estudantes", align: "right" },
            { key: "status", header: "Status", render: (r) => <StatusBadge tone={r.tone as any}>{r.status}</StatusBadge> },
          ]}
          rows={[
            { turma: "AED301-T01", prof: "Carlos Lima", estud: 35, status: "Pronto p/ consolidar", tone: "success" },
            { turma: "BD302-T01", prof: "Ana Souza", estud: 38, status: "Pronto p/ consolidar", tone: "success" },
            { turma: "ES303-T02", prof: "Marcos R.", estud: 32, status: "Pendente revisão", tone: "warning" },
          ]}
        />
        <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Consolidar Selecionados</Button></div>
      </div>
    </div>
  );
}

function Correcao() {
  return (
    <div className="space-y-4">
      <SuccessBanner title="Correção solicitada com sucesso." description="Aguardando análise da coordenação." />
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Solicitar Correção de Lançamento" />
        <div className="mt-4 grid grid-cols-2 gap-4">
          <FormField label="Disciplina"><Input className="h-10" defaultValue="BD201" /></FormField>
          <FormField label="Período"><Input className="h-10" defaultValue="2024.2" /></FormField>
          <FormField label="Justificativa" required full><Textarea rows={4} /></FormField>
        </div>
        <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Enviar</Button></div>
      </div>
    </div>
  );
}

function Risco() {
  return (
    <div className="rounded-xl border bg-card p-6 shadow-card">
      <SectionTitle title="Acompanhamento de Estudante em Risco" />
      <div className="mt-4 grid grid-cols-2 gap-4">
        <FormField label="Estudante"><Input className="h-10" defaultValue="Pedro Mota — 2022014" /></FormField>
        <FormField label="Tipo de risco"><Input className="h-10" defaultValue="Baixo CR" /></FormField>
        <FormField label="Observações" full><Textarea rows={4} placeholder="Plano de acompanhamento..." /></FormField>
      </div>
      <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Registrar</Button></div>
    </div>
  );
}

function Situacao() {
  return (
    <div className="rounded-xl border bg-card p-6 shadow-card">
      <SectionTitle title="Atualizar Situação Acadêmica" />
      <div className="mt-4 grid grid-cols-2 gap-4">
        <FormField label="Estudante"><Input className="h-10" /></FormField>
        <FormField label="Nova situação"><Input className="h-10" defaultValue="Regular" /></FormField>
        <FormField label="Justificativa" full><Textarea rows={3} /></FormField>
      </div>
      <ValidationCallout className="mt-4">Mudança permitida pelo regulamento.</ValidationCallout>
      <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Atualizar</Button></div>
    </div>
  );
}

function Aproveitamento() {
  return (
    <div className="rounded-xl border bg-card p-6 shadow-card">
      <SectionTitle title="Aproveitamento de Disciplina Externa" />
      <div className="mt-4 grid grid-cols-2 gap-4">
        <FormField label="Instituição"><Input className="h-10" defaultValue="UFXX" /></FormField>
        <FormField label="Disciplina cursada"><Input className="h-10" /></FormField>
        <FormField label="Disciplina equivalente"><Input className="h-10" /></FormField>
        <FormField label="Carga horária"><Input className="h-10" defaultValue="60h" /></FormField>
        <FormField label="Comprovante" full><Input type="file" className="h-10" /></FormField>
      </div>
      <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Registrar</Button></div>
    </div>
  );
}

function Retificacao() {
  return (
    <div className="rounded-xl border bg-card p-6 shadow-card">
      <SectionTitle title="Retificação de Resultado Consolidado" subtitle="Ação registrada com trilha de auditoria." />
      <div className="mt-4 grid grid-cols-2 gap-4">
        <FormField label="Lançamento"><Input className="h-10" defaultValue="BD201 · 2024.2 · Maria Santos" /></FormField>
        <FormField label="Nota corrigida"><Input className="h-10" defaultValue="8.0" /></FormField>
        <FormField label="Justificativa" full><Textarea rows={4} /></FormField>
      </div>
      <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Retificar</Button></div>
    </div>
  );
}

function Emitir() {
  return (
    <div className="rounded-xl border bg-card p-6 shadow-card">
      <SectionTitle title="Emitir Histórico Acadêmico Oficial" right={<Button><Printer className="mr-2 h-4 w-4" />Imprimir</Button>} />
      <div className="mt-4 rounded-md border bg-subtle p-6 text-[13px]">
        <p className="font-semibold">Estudante: Maria Santos — 2023001</p>
        <p className="text-muted-foreground">Curso: Engenharia de Software · Matriz MAT-2024-01</p>
        <DataTable
          className="mt-4"
          columns={[
            { key: "p", header: "Período" },
            { key: "cod", header: "Código" },
            { key: "disc", header: "Disciplina" },
            { key: "ch", header: "CH", align: "right" },
            { key: "nota", header: "Nota", align: "right" },
            { key: "sit", header: "Sit." },
          ]}
          rows={[
            { p: "2024.1", cod: "AED201", disc: "Algoritmos e Estr. Dados", ch: 80, nota: 8.4, sit: "AP" },
            { p: "2024.2", cod: "BD301", disc: "Banco de Dados I", ch: 60, nota: 7.6, sit: "AP" },
            { p: "2025.1", cod: "ES302", disc: "Engenharia de Software I", ch: 60, nota: 9.0, sit: "AP" },
          ]}
        />
      </div>
    </div>
  );
}

function Page() {
  return (
    <FeaturePage
      title="Gestão do Histórico Acadêmico"
      subtitle="Consolidação, retificações e emissão"
      sections={[
        { value: "painel", label: "Painel", content: <Painel /> },
        { value: "consol", label: "Consolidar", content: <Consolidar /> },
        { value: "corr", label: "Correção", content: <Correcao /> },
        { value: "risco", label: "Risco", content: <Risco /> },
        { value: "sit", label: "Situação", content: <Situacao /> },
        { value: "apro", label: "Aproveitamento", content: <Aproveitamento /> },
        { value: "ret", label: "Retificação", content: <Retificacao /> },
        { value: "emit", label: "Emitir Histórico", content: <Emitir /> },
      ]}
    />
  );
}
