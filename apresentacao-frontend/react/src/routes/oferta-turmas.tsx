import { createFileRoute } from "@tanstack/react-router";
import {
  FeaturePage, StatsRow, ActionBar, DataTable, StatusBadge, RowActionButton,
  FormField, ValidationCallout, SectionTitle, ConflictGrid, SuccessBanner,
} from "@/components/acadlab";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";

export const Route = createFileRoute("/oferta-turmas")({
  head: () => ({ meta: [{ title: "Oferta de Turmas — AcadLab" }] }),
  component: Page,
});

function Painel() {
  return (
    <>
      <StatsRow stats={[
        { label: "Turmas em 2025.2", value: 48, tone: "info" },
        { label: "Pendentes de Prof.", value: 5, tone: "warning" },
        { label: "Vagas Abertas", value: "1.920", tone: "success" },
        { label: "Com Conflito", value: 2, tone: "danger" },
      ]} />
      <ActionBar searchPlaceholder="Buscar turma ou disciplina..." primaryLabel="Ofertar Turma" />
      <DataTable
        columns={[
          { key: "turma", header: "Turma" },
          { key: "disc", header: "Disciplina" },
          { key: "prof", header: "Professor" },
          { key: "horario", header: "Horário" },
          { key: "sala", header: "Sala" },
          { key: "vagas", header: "Vagas", align: "right" },
          { key: "status", header: "Status", render: (r) => <StatusBadge tone={r.tone as any}>{r.status}</StatusBadge> },
          { key: "acoes", header: "Ações", render: () => <RowActionButton>Editar</RowActionButton>, align: "right" },
        ]}
        rows={[
          { turma: "AED201-T01", disc: "Algoritmos e Estruturas de Dados", prof: "Carlos Lima", horario: "Seg/Qua 10-12", sala: "L-203", vagas: "30/40", status: "Ativa", tone: "success" },
          { turma: "BD302-T01", disc: "Banco de Dados II", prof: "Ana Souza", horario: "Ter/Qui 14-16", sala: "L-205", vagas: "28/40", status: "Ativa", tone: "success" },
          { turma: "ES303-T02", disc: "Testes de Software", prof: "—", horario: "Qua 18-22", sala: "L-101", vagas: "0/35", status: "Pendente", tone: "warning" },
          { turma: "RC305-T01", disc: "Redes Avançadas", prof: "Pedro Alves", horario: "Sex 16-20", sala: "S-302", vagas: "12/30", status: "Conflito", tone: "danger" },
        ]}
      />
    </>
  );
}

function Salas() {
  return (
    <>
      <ActionBar searchPlaceholder="Buscar sala..." primaryLabel="Nova Sala" />
      <DataTable
        columns={[
          { key: "codigo", header: "Código" },
          { key: "bloco", header: "Bloco" },
          { key: "tipo", header: "Tipo" },
          { key: "cap", header: "Capacidade", align: "right" },
          { key: "acoes", header: "", render: () => <RowActionButton>Editar</RowActionButton>, align: "right" },
        ]}
        rows={[
          { codigo: "L-203", bloco: "Bloco A", tipo: "Laboratório", cap: 30 },
          { codigo: "S-105", bloco: "Bloco B", tipo: "Sala", cap: 45 },
          { codigo: "AUD-01", bloco: "Bloco Central", tipo: "Auditório", cap: 120 },
        ]}
      />
    </>
  );
}

function Professores() {
  return (
    <>
      <ActionBar searchPlaceholder="Buscar professor..." primaryLabel="Novo Professor" />
      <DataTable
        columns={[
          { key: "nome", header: "Nome" },
          { key: "dept", header: "Departamento" },
          { key: "email", header: "E-mail" },
          { key: "disp", header: "Disponibilidade" },
          { key: "acoes", header: "", render: () => <RowActionButton>Editar</RowActionButton>, align: "right" },
        ]}
        rows={[
          { nome: "Prof. Carlos Lima", dept: "Computação", email: "carlos.lima@acadlab.edu", disp: "Seg–Sex manhã" },
          { nome: "Profa. Ana Souza", dept: "Computação", email: "ana.souza@acadlab.edu", disp: "Ter/Qui tarde" },
          { nome: "Prof. Pedro Alves", dept: "Redes", email: "pedro.alves@acadlab.edu", disp: "Sex noite" },
        ]}
      />
    </>
  );
}

function Definir() {
  return (
    <div className="grid grid-cols-1 gap-6 lg:grid-cols-[380px_1fr]">
      <div className="rounded-xl border bg-card p-5 shadow-card">
        <SectionTitle title="Turma em Configuração" />
        <div className="mt-4 space-y-3 text-[13px]">
          <div>
            <p className="text-[11px] uppercase text-muted-foreground">Disciplina</p>
            <p className="font-semibold">AED201 — Algoritmos e Estruturas de Dados</p>
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <p className="text-[11px] uppercase text-muted-foreground">Período</p>
              <p className="font-medium">2025.2</p>
            </div>
            <div>
              <p className="text-[11px] uppercase text-muted-foreground">Modalidade</p>
              <p className="font-medium">Presencial</p>
            </div>
            <div>
              <p className="text-[11px] uppercase text-muted-foreground">Vagas</p>
              <p className="font-medium">40 vagas</p>
            </div>
            <div>
              <p className="text-[11px] uppercase text-muted-foreground">Lista de espera</p>
              <p className="font-medium">10</p>
            </div>
          </div>
          <div className="pt-2">
            <p className="mb-2 text-[12px] font-semibold">Grade de conflitos (Prof. selecionado)</p>
            <ConflictGrid occupied={[[0, 1], [2, 2], [3, 0]]} />
          </div>
        </div>
      </div>
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Configurar Recursos da Turma" subtitle="Professor, horário, sala e capacidade" />
        <div className="mt-4 grid grid-cols-2 gap-4">
          <FormField label="Professor Responsável" required full>
            <Input className="h-10" defaultValue="Prof. Carlos Lima — Computação" />
          </FormField>
          <FormField label="Dias da Semana" required><Input className="h-10" defaultValue="Segunda, Quarta" /></FormField>
          <FormField label="Horário" required><Input className="h-10" defaultValue="10:00 – 12:00" /></FormField>
          <div className="col-span-2">
            <ValidationCallout>Sem conflito de horário para o Prof. Carlos Lima neste período.</ValidationCallout>
          </div>
          <FormField label="Sala" required><Input className="h-10" defaultValue="L-203 (Lab, cap. 30)" /></FormField>
          <FormField label="Capacidade da Turma" required><Input className="h-10" defaultValue="30" /></FormField>
          <div className="col-span-2">
            <ValidationCallout>Sem conflito de sala · Capacidade 30 ≤ 30 vagas.</ValidationCallout>
          </div>
          <FormField label="Avaliação Pedagógica" full>
            <Textarea rows={3} placeholder="Observações opcionais sobre a oferta..." />
          </FormField>
        </div>
        <div className="mt-5 flex justify-end gap-2">
          <Button variant="outline">Voltar</Button>
          <Button>Confirmar Oferta de Turma</Button>
        </div>
      </div>
    </div>
  );
}

function Alterar() {
  return (
    <div className="space-y-4">
      <SuccessBanner title="Turma atualizada." description="Recursos da turma AED201-T01 foram revalidados sem conflitos." />
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Alterar Dados de Turma Ofertada" />
        <div className="mt-4 grid grid-cols-2 gap-4">
          <FormField label="Turma"><Input className="h-10" defaultValue="AED201-T01" /></FormField>
          <FormField label="Professor"><Input className="h-10" defaultValue="Prof. Carlos Lima" /></FormField>
          <FormField label="Horário"><Input className="h-10" defaultValue="Seg/Qua 10:00–12:00" /></FormField>
          <FormField label="Sala"><Input className="h-10" defaultValue="L-203" /></FormField>
        </div>
        <div className="mt-4"><ValidationCallout>Sem conflitos após alteração.</ValidationCallout></div>
        <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Salvar</Button></div>
      </div>
    </div>
  );
}

function CancelarTurma() {
  return (
    <div className="rounded-xl border bg-card p-6 shadow-card">
      <SectionTitle title="Cancelar Turma Ofertada" />
      <p className="mt-2 text-[13px] text-muted-foreground">
        Esta ação impacta <span className="font-semibold text-foreground">22 matrículas</span>. Os estudantes receberão notificação.
      </p>
      <FormField className="mt-4" label="Motivo" required full><Textarea rows={4} /></FormField>
      <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Voltar</Button><Button variant="destructive">Cancelar Turma</Button></div>
    </div>
  );
}

function Page() {
  return (
    <FeaturePage
      title="Planejamento e Oferta de Turmas"
      subtitle="Salas, professores e oferta de turmas em 2025.2"
      sections={[
        { value: "painel", label: "Painel", content: <Painel /> },
        { value: "definir", label: "Definir Recursos", content: <Definir /> },
        { value: "salas", label: "Salas", content: <Salas /> },
        { value: "profs", label: "Professores", content: <Professores /> },
        { value: "alterar", label: "Alterar", content: <Alterar /> },
        { value: "cancelar", label: "Cancelar", content: <CancelarTurma /> },
      ]}
    />
  );
}
