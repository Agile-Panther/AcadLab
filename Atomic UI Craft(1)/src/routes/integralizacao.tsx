import { useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import {
  AppShell, SectionTitle, StatsRow, DataTable, StatusBadge, RowActionButton,
  ProgressRow, ValidationCallout, SuccessBanner,
  useProfileSwitcher,
} from "@/components/acadlab";
import { Button } from "@/components/ui/button";
import { CheckCircle2, XCircle, FileSearch, ArrowLeft } from "lucide-react";
import { toast } from "sonner";

export const Route = createFileRoute("/integralizacao")({
  head: () => ({ meta: [{ title: "Integralização & Colação — AcadLab" }] }),
  component: Page,
});

type Status = "Não solicitada" | "Em análise" | "Apto" | "Inapto";

function Page() {
  const { active: perfil } = useProfileSwitcher([
    { value: "estudante", label: "Estudante", description: "Acompanha integralização e colação" },
    { value: "coordenacao", label: "Coordenação Acadêmica", description: "Audita integralização e libera colação" },
    { value: "dra", label: "DRA / Registro Acadêmico", description: "Emite diplomas e atas" },
  ]);
  const [status, setStatus] = useState<Status>("Não solicitada");

  const subtitle = perfil === "coordenacao"
    ? "Visão Coordenação · Auditoria de integralização"
    : perfil === "dra"
    ? "Visão DRA · Emissão de diplomas"
    : "Maria Santos · Engenharia de Software";

  if (perfil === "coordenacao") {
    return (
      <AppShell title="Integralização Curricular & Colação" subtitle={subtitle}>
        <CoordView />
      </AppShell>
    );
  }
  if (perfil === "dra") {
    return (
      <AppShell title="Integralização Curricular & Colação" subtitle={subtitle}>
        <DraView />
      </AppShell>
    );
  }

  return (
    <AppShell title="Integralização Curricular & Colação" subtitle={subtitle}>
      <div className="space-y-5">
        <StatsRow stats={[
          { label: "Obrigatórias cumpridas", value: "32/42", tone: "info" },
          { label: "Optativas", value: "6/10", tone: "warning" },
          { label: "Horas complementares", value: "180/200", tone: "warning" },
          { label: "Estágio", value: "200/300h", tone: "warning" },
        ]} />

        <div className="rounded-xl border bg-card p-5 shadow-card">
          <SectionTitle title="Checklist de integralização" subtitle="Atualizado a partir do histórico consolidado." />
          <div className="mt-3 space-y-3">
            <ProgressRow label="Disciplinas obrigatórias" current={32} total={42} tone="info" />
            <ProgressRow label="Carga horária optativa" current={360} total={600} unit="h" tone="warning" />
            <ProgressRow label="Horas complementares" current={180} total={200} unit="h" tone="warning" />
            <ProgressRow label="Estágio supervisionado" current={200} total={300} unit="h" tone="warning" />
            <ProgressRow label="TCC" current={0} total={1} unit="" tone="danger" />
          </div>
        </div>

        {status === "Não solicitada" && (
          <div className="rounded-xl border bg-card p-6 text-center shadow-card">
            <p className="text-[14px] text-muted-foreground">Você ainda não solicitou a análise de conclusão do curso.</p>
            <Button className="mt-4" onClick={() => setStatus("Em análise")}><FileSearch className="mr-2 h-4 w-4" /> Solicitar análise de conclusão</Button>
            <ValidationCallout className="mt-4" tone="info">A solicitação só pode ser iniciada após o encerramento do último período cursado.</ValidationCallout>
          </div>
        )}

        {status === "Em análise" && (
          <div className="space-y-4">
            <div className="rounded-xl border bg-card p-5 shadow-card">
              <div className="flex items-center justify-between">
                <SectionTitle title="Análise em andamento" subtitle="A Secretaria está gerando seu checklist. Prazo: 10 dias úteis." />
                <StatusBadge tone="warning">Em análise</StatusBadge>
              </div>
            </div>
            <div className="flex gap-2">
              <Button variant="outline" onClick={() => setStatus("Apto")}>(simular) Apto</Button>
              <Button variant="outline" onClick={() => setStatus("Inapto")}>(simular) Inapto</Button>
            </div>
          </div>
        )}

        {status === "Apto" && (
          <div className="space-y-4">
            <SuccessBanner title="Você está APTO à colação de grau!" description="Aprovado pelo Coordenador Acadêmico em 22/03/2025. Aguarde a data da cerimônia." />
            <div className="rounded-xl border bg-card p-5 shadow-card">
              <SectionTitle title="Próximos passos" />
              <ul className="mt-3 space-y-2 text-[13px]">
                <li>• Cerimônia agendada: 15/08/2025 — Auditório Central</li>
                <li>• Confirme presença até 15/07/2025</li>
                <li>• Retire toga e beca na secretaria a partir de 01/08</li>
              </ul>
            </div>
          </div>
        )}

        {status === "Inapto" && (
          <div className="space-y-4">
            <div className="flex items-center gap-3 rounded-xl border border-destructive bg-destructive-soft p-4 text-destructive">
              <XCircle className="h-6 w-6" />
              <div>
                <p className="font-semibold">Análise concluída: INAPTO</p>
                <p className="text-[13px]">Existem pendências que impedem a colação de grau.</p>
              </div>
            </div>
            <DataTable
              columns={[
                { key: "req", header: "Requisito" }, { key: "pend", header: "Pendência" },
                { key: "acao", header: "", align: "right", render: (r) => <RowActionButton onClick={() => toast.info(`Orientação: ${r.pend}`)}>Como resolver</RowActionButton> },
              ]}
              rows={[
                { req: "TCC", pend: "Não cumprido — matricule-se em 2026.1" },
                { req: "Horas complementares", pend: "Faltam 20h (categoria Eventos)" },
                { req: "Estágio supervisionado", pend: "Faltam 100h" },
              ]}
            />
            <Button variant="ghost" onClick={() => setStatus("Não solicitada")}><ArrowLeft className="mr-1 h-4 w-4" /> Voltar</Button>
          </div>
        )}

        {status === "Apto" && (
          <div className="flex items-center gap-3 rounded-xl border border-success bg-success-soft p-4 text-success">
            <CheckCircle2 className="h-6 w-6" />
            <p className="text-[13px]">Todos os requisitos foram verificados contra o histórico consolidado.</p>
          </div>
        )}
      </div>
    </AppShell>
  );
}

type Formando = { matricula: string; nome: string; cr: number; status: "Apto" | "Pendente" | "Inapto"; pend: number };

function CoordView() {
  const [formandos, setFormandos] = useState<Formando[]>([
    { matricula: "2021.10245", nome: "Maria Santos", cr: 8.4, status: "Pendente", pend: 1 },
    { matricula: "2021.10277", nome: "André Costa", cr: 9.0, status: "Apto", pend: 0 },
    { matricula: "2021.10309", nome: "Renata Lima", cr: 7.8, status: "Inapto", pend: 3 },
    { matricula: "2021.10342", nome: "Bruno Tavares", cr: 8.6, status: "Pendente", pend: 2 },
  ]);
  const liberar = (m: string) => {
    setFormandos((p) => p.map((f) => f.matricula === m ? { ...f, status: "Apto", pend: 0 } : f));
    toast.success("Estudante liberado para colação.");
  };
  return (
    <div className="space-y-5">
      <StatsRow stats={[
        { label: "Turma de formandos", value: formandos.length, tone: "info" },
        { label: "Aptos", value: formandos.filter((f) => f.status === "Apto").length, tone: "success" },
        { label: "Pendentes", value: formandos.filter((f) => f.status === "Pendente").length, tone: "warning" },
        { label: "Inaptos", value: formandos.filter((f) => f.status === "Inapto").length, tone: "danger" },
      ]} />
      <SectionTitle title="Análise da turma de formandos 2025.2" subtitle="Audite a integralização e libere a colação de grau." />
      <DataTable
        columns={[
          { key: "matricula", header: "Matrícula" },
          { key: "nome", header: "Estudante" },
          { key: "cr", header: "CR", align: "right" },
          { key: "pend", header: "Pendências", align: "right" },
          { key: "status", header: "Status", render: (r) => (
            <StatusBadge tone={r.status === "Apto" ? "success" : r.status === "Pendente" ? "warning" : "danger"}>{r.status}</StatusBadge>
          )},
          { key: "acoes", header: "", align: "right", render: (r) => (
            r.status === "Apto"
              ? <span className="text-[12px] text-muted-foreground">Liberado</span>
              : <RowActionButton tone="info" onClick={() => liberar(r.matricula)}>Liberar colação</RowActionButton>
          )},
        ]}
        rows={formandos}
      />
    </div>
  );
}

function DraView() {
  return (
    <div className="space-y-5">
      <StatsRow stats={[
        { label: "Aptos a colação", value: 28, tone: "success" },
        { label: "Diplomas em emissão", value: 12, tone: "warning" },
        { label: "Diplomas emitidos (ano)", value: 86, tone: "info" },
        { label: "Atas pendentes", value: 4, tone: "danger" },
      ]} />
      <SectionTitle title="Diplomas e atas" subtitle="Emissão oficial após colação de grau." />
      <DataTable
        columns={[
          { key: "matricula", header: "Matrícula" },
          { key: "nome", header: "Estudante" },
          { key: "curso", header: "Curso" },
          { key: "data", header: "Colação" },
          { key: "status", header: "Status", render: (r) => (
            <StatusBadge tone={r.status === "Emitido" ? "success" : "warning"}>{r.status}</StatusBadge>
          )},
          { key: "acoes", header: "", align: "right", render: (r) => (
            r.status === "Emitido"
              ? <RowActionButton onClick={() => toast.success("Diploma reemitido.")}>Reemitir</RowActionButton>
              : <RowActionButton tone="info" onClick={() => toast.success("Diploma emitido com sucesso.")}>Emitir diploma</RowActionButton>
          )},
        ]}
        rows={[
          { matricula: "2020.10101", nome: "Luiza Tavares", curso: "Eng. de Software", data: "10/02/2026", status: "Pendente" },
          { matricula: "2020.10112", nome: "André Costa", curso: "Eng. de Software", data: "10/02/2026", status: "Pendente" },
          { matricula: "2020.10099", nome: "Pedro Maia", curso: "Eng. de Software", data: "15/12/2025", status: "Emitido" },
        ]}
      />
    </div>
  );
}
