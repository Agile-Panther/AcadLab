import { useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import { toast } from "sonner";
import {
  AppShell, SectionTitle, StatsRow, DataTable, StatusBadge, RowActionButton,
  ProgressRow, FormField, ValidationCallout, ActionBar,
  useProfileSwitcher,
} from "@/components/acadlab";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { ArrowLeft, Download, FileSearch, Check, X } from "lucide-react";

export const Route = createFileRoute("/historico-academico")({
  head: () => ({ meta: [{ title: "Histórico Acadêmico — AcadLab" }] }),
  component: Page,
});

type View =
  | { kind: "overview" }
  | { kind: "correcao" }
  | { kind: "emitir" }
  | { kind: "sec-list" }
  | { kind: "sec-detail"; id: string }
  | { kind: "sec-aluno"; matricula: string };

const periodos = [
  { p: "2025.1", aprov: 5, reprov: 0, cr: 8.4 },
  { p: "2024.2", aprov: 4, reprov: 1, cr: 7.9 },
  { p: "2024.1", aprov: 5, reprov: 0, cr: 8.1 },
  { p: "2023.2", aprov: 4, reprov: 0, cr: 7.6 },
];

type Solicitacao = {
  id: string; aluno: string; matricula: string; disciplina: string; periodo: string;
  motivo: string; aberta: string;
  status: "Em análise" | "Pendente complemento" | "Deferida" | "Indeferida";
};

const solicitacoesIniciais: Solicitacao[] = [
  { id: "HIS-2025-041", aluno: "Maria Santos", matricula: "2021.10245", disciplina: "AED301 — Algoritmos Avançados", periodo: "2024.2", motivo: "Nota lançada divergente da prova final.", aberta: "12/03/2025", status: "Em análise" },
  { id: "HIS-2025-042", aluno: "Pedro Almeida", matricula: "2022.20988", disciplina: "BD302 — Banco de Dados II", periodo: "2024.2", motivo: "Aproveitamento externo não registrado.", aberta: "14/03/2025", status: "Em análise" },
  { id: "HIS-2025-038", aluno: "Júlia Rocha", matricula: "2020.10112", disciplina: "IA101 — Intro. à IA", periodo: "2023.2", motivo: "Frequência incorreta lançada.", aberta: "10/03/2025", status: "Pendente complemento" },
  { id: "HIS-2025-031", aluno: "Lucas Pires", matricula: "2021.10455", disciplina: "ENG201 — Engenharia de Software", periodo: "2024.1", motivo: "Equivalência não aplicada.", aberta: "02/03/2025", status: "Deferida" },
];

const alunosIniciais = [
  { matricula: "2021.10245", nome: "Maria Santos", curso: "Eng. de Software", cr: 8.4, periodo: "2025.1", situacao: "Ativo" },
  { matricula: "2022.20988", nome: "Pedro Almeida", curso: "Eng. de Software", cr: 7.6, periodo: "2025.1", situacao: "Ativo" },
  { matricula: "2020.10112", nome: "Júlia Rocha", curso: "Ciência da Computação", cr: 9.1, periodo: "2025.1", situacao: "Ativo" },
  { matricula: "2021.10455", nome: "Lucas Pires", curso: "Eng. de Software", cr: 7.2, periodo: "2025.1", situacao: "Trancado" },
  { matricula: "2019.30221", nome: "Camila Reis", curso: "Sistemas de Informação", cr: 8.8, periodo: "2025.1", situacao: "Ativo" },
];

function Page() {
  const { active: perfil } = useProfileSwitcher([
    { value: "estudante", label: "Estudante", description: "Consulta e solicita correções" },
    { value: "secretaria", label: "Secretaria Acadêmica", description: "Audita históricos e julga correções" },
  ]);
  const isSec = perfil === "secretaria";

  const [view, setView] = useState<View>(isSec ? { kind: "sec-list" } : { kind: "overview" });
  const [expanded, setExpanded] = useState<string | null>("2025.1");
  const [solicitacoes, setSolicitacoes] = useState<Solicitacao[]>(solicitacoesIniciais);
  const [busca, setBusca] = useState("");

  // sync default view when switching profile
  if (isSec && (view.kind === "overview" || view.kind === "correcao" || view.kind === "emitir")) {
    setView({ kind: "sec-list" });
  }
  if (!isSec && (view.kind === "sec-list" || view.kind === "sec-detail" || view.kind === "sec-aluno")) {
    setView({ kind: "overview" });
  }

  const subtitle = isSec
    ? "Visão Secretaria · Auditoria e correções"
    : "Maria Santos · Engenharia de Software";

  const decidir = (id: string, status: Solicitacao["status"], label: string) => {
    setSolicitacoes((prev) => prev.map((s) => s.id === id ? { ...s, status } : s));
    toast.success(`Solicitação ${id} ${label}.`);
    setView({ kind: "sec-list" });
  };

  return (
    <AppShell title="Histórico Acadêmico" subtitle={subtitle}>
      {/* =================== ESTUDANTE =================== */}
      {!isSec && view.kind === "overview" && (
        <div className="space-y-5">
          <StatsRow stats={[
            { label: "CR atual", value: 8.4, tone: "success" },
            { label: "Disciplinas cursadas", value: 38, tone: "info" },
            { label: "Integralização", value: "76%", tone: "warning" },
            { label: "Períodos cursados", value: 7, tone: "info" },
          ]} />
          <div className="flex flex-wrap gap-2">
            <Button onClick={() => setView({ kind: "emitir" })}><Download className="mr-2 h-4 w-4" /> Emitir histórico oficial</Button>
            <Button variant="outline" onClick={() => setView({ kind: "correcao" })}><FileSearch className="mr-2 h-4 w-4" /> Solicitar correção</Button>
          </div>
          <div className="rounded-xl border bg-card p-5 shadow-card">
            <SectionTitle title="Progresso de integralização" />
            <div className="mt-3 space-y-3">
              <ProgressRow label="Disciplinas obrigatórias" current={32} total={42} tone="info" />
              <ProgressRow label="Disciplinas optativas" current={6} total={10} tone="warning" />
              <ProgressRow label="Horas complementares" current={180} total={200} unit="h" tone="success" />
              <ProgressRow label="Estágio obrigatório" current={200} total={300} unit="h" tone="warning" />
            </div>
          </div>

          <SectionTitle title="Histórico por período" subtitle="Clique em um período para ver as disciplinas cursadas." />
          <div className="space-y-3">
            {periodos.map((p) => (
              <div key={p.p} className="rounded-xl border bg-card shadow-card">
                <button onClick={() => setExpanded(expanded === p.p ? null : p.p)} className="flex w-full items-center justify-between p-4 text-left">
                  <div>
                    <p className="font-semibold text-foreground">{p.p}</p>
                    <p className="text-[12px] text-muted-foreground">{p.aprov} aprovações · {p.reprov} reprovações · CR {p.cr}</p>
                  </div>
                  <span className="text-[12px] text-muted-foreground">{expanded === p.p ? "Recolher" : "Expandir"}</span>
                </button>
                {expanded === p.p && (
                  <div className="border-t border-border">
                    <DataTable
                      columns={[
                        { key: "cod", header: "Código" }, { key: "nome", header: "Disciplina" },
                        { key: "ch", header: "CH", align: "right" }, { key: "nota", header: "Nota", align: "right" },
                        { key: "freq", header: "Freq.", align: "right" },
                        { key: "sit", header: "Situação", render: (r) => (
                          <StatusBadge tone={r.sit === "Aprovado" ? "success" : r.sit === "Aproveitado" ? "info" : "danger"}>{r.sit}</StatusBadge>
                        )},
                      ]}
                      rows={[
                        { cod: "AED301", nome: "Algoritmos Avançados", ch: 80, nota: 8.5, freq: "92%", sit: "Aprovado" },
                        { cod: "BD302", nome: "Banco de Dados II", ch: 60, nota: 9.0, freq: "88%", sit: "Aprovado" },
                        { cod: "IA101", nome: "Intro. à IA", ch: 60, nota: "—", freq: "—", sit: "Aproveitado" },
                      ]}
                    />
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>
      )}

      {!isSec && view.kind === "correcao" && (
        <div className="space-y-4">
          <Button variant="ghost" size="sm" onClick={() => setView({ kind: "overview" })}><ArrowLeft className="mr-1 h-4 w-4" /> Voltar</Button>
          <div className="rounded-xl border bg-card p-6 shadow-card">
            <SectionTitle title="Solicitar correção de histórico" subtitle="Sua solicitação será analisada pela Secretaria Acadêmica." />
            <div className="mt-4 grid grid-cols-2 gap-4">
              <FormField label="Disciplina" required><Input className="h-10" placeholder="AED301 — Algoritmos Avançados" /></FormField>
              <FormField label="Período" required><Input className="h-10" placeholder="2024.2" /></FormField>
              <FormField label="O que precisa ser corrigido" full required><Textarea rows={4} placeholder="Descreva a inconsistência" /></FormField>
              <FormField label="Anexo" full><Input type="file" className="h-10" /></FormField>
            </div>
            <ValidationCallout className="mt-4" tone="info">A correção será efetivada apenas após deferimento com registro do responsável.</ValidationCallout>
            <div className="mt-4 flex justify-end gap-2">
              <Button variant="outline" onClick={() => setView({ kind: "overview" })}>Cancelar</Button>
              <Button onClick={() => { toast.success("Solicitação enviada à Secretaria Acadêmica."); setView({ kind: "overview" }); }}>Enviar solicitação</Button>
            </div>
          </div>
        </div>
      )}

      {!isSec && view.kind === "emitir" && (
        <div className="space-y-4">
          <Button variant="ghost" size="sm" onClick={() => setView({ kind: "overview" })}><ArrowLeft className="mr-1 h-4 w-4" /> Voltar</Button>
          <div className="rounded-xl border bg-card p-6 shadow-card">
            <SectionTitle title="Emitir histórico oficial" subtitle="Documento inclui apenas períodos encerrados e consolidados." />
            <ul className="mt-4 space-y-2 text-[13px] text-foreground">
              <li>✓ Períodos incluídos: 2020.1 → 2025.1</li>
              <li>✓ CR consolidado: 8.4</li>
              <li>✓ Aproveitamentos externos: 1 disciplina</li>
              <li className="text-muted-foreground">Período 2025.2 (em andamento) não será incluído.</li>
            </ul>
            <div className="mt-4 flex justify-end gap-2">
              <Button variant="outline" onClick={() => setView({ kind: "overview" })}>Cancelar</Button>
              <Button onClick={() => { toast.success("Histórico oficial gerado e baixado."); setView({ kind: "overview" }); }}><Download className="mr-2 h-4 w-4" /> Baixar PDF</Button>
            </div>
          </div>
        </div>
      )}

      {/* =================== SECRETARIA =================== */}
      {isSec && view.kind === "sec-list" && (
        <div className="space-y-5">
          <StatsRow stats={[
            { label: "Solicitações abertas", value: solicitacoes.filter((s) => s.status === "Em análise" || s.status === "Pendente complemento").length, tone: "warning" },
            { label: "Em análise", value: solicitacoes.filter((s) => s.status === "Em análise").length, tone: "info" },
            { label: "Pendentes complemento", value: solicitacoes.filter((s) => s.status === "Pendente complemento").length, tone: "danger" },
            { label: "Deferidas (mês)", value: solicitacoes.filter((s) => s.status === "Deferida").length, tone: "success" },
          ]} />

          <SectionTitle title="Fila de correções de histórico" subtitle="Solicitações abertas pelos estudantes aguardando triagem." />
          <DataTable
            columns={[
              { key: "id", header: "Protocolo" },
              { key: "aluno", header: "Estudante" },
              { key: "disciplina", header: "Disciplina" },
              { key: "periodo", header: "Período" },
              { key: "aberta", header: "Aberta em" },
              { key: "status", header: "Status", render: (r) => (
                <StatusBadge tone={r.status === "Deferida" ? "success" : r.status === "Indeferida" ? "danger" : r.status === "Pendente complemento" ? "warning" : "info"}>{r.status}</StatusBadge>
              )},
              { key: "acoes", header: "", align: "right", render: (r) => (
                <RowActionButton onClick={() => setView({ kind: "sec-detail", id: r.id })}>Analisar</RowActionButton>
              )},
            ]}
            rows={solicitacoes}
          />

          <SectionTitle title="Consulta de históricos por estudante" subtitle="Localize e emita o histórico oficial de qualquer estudante." />
          <ActionBar searchPlaceholder="Buscar por nome ou matrícula..." onSearch={setBusca} />
          <DataTable
            columns={[
              { key: "matricula", header: "Matrícula" },
              { key: "nome", header: "Nome" },
              { key: "curso", header: "Curso" },
              { key: "periodo", header: "Período atual" },
              { key: "cr", header: "CR", align: "right" },
              { key: "situacao", header: "Situação", render: (r) => (
                <StatusBadge tone={r.situacao === "Ativo" ? "success" : "warning"}>{r.situacao}</StatusBadge>
              )},
              { key: "acoes", header: "", align: "right", render: (r) => (
                <RowActionButton onClick={() => setView({ kind: "sec-aluno", matricula: r.matricula })}>Abrir histórico</RowActionButton>
              )},
            ]}
            rows={alunosIniciais.filter((a) => {
              const q = busca.trim().toLowerCase();
              if (!q) return true;
              return a.nome.toLowerCase().includes(q) || a.matricula.includes(q);
            })}
          />
        </div>
      )}

      {isSec && view.kind === "sec-detail" && (() => {
        const s = solicitacoes.find((x) => x.id === view.id);
        if (!s) return null;
        return (
          <div className="space-y-4">
            <Button variant="ghost" size="sm" onClick={() => setView({ kind: "sec-list" })}><ArrowLeft className="mr-1 h-4 w-4" /> Voltar à fila</Button>
            <div className="rounded-xl border bg-card p-6 shadow-card">
              <div className="flex items-start justify-between gap-4">
                <div>
                  <SectionTitle title={`Solicitação ${s.id}`} subtitle={`${s.aluno} · Matrícula ${s.matricula}`} />
                </div>
                <StatusBadge tone={s.status === "Deferida" ? "success" : s.status === "Indeferida" ? "danger" : s.status === "Pendente complemento" ? "warning" : "info"}>{s.status}</StatusBadge>
              </div>

              <div className="mt-4 grid grid-cols-2 gap-4 text-[13px]">
                <div><span className="text-muted-foreground">Disciplina:</span> <span className="text-foreground">{s.disciplina}</span></div>
                <div><span className="text-muted-foreground">Período:</span> <span className="text-foreground">{s.periodo}</span></div>
                <div><span className="text-muted-foreground">Aberta em:</span> <span className="text-foreground">{s.aberta}</span></div>
                <div><span className="text-muted-foreground">Anexos:</span> <span className="text-foreground">1 arquivo (boletim_assinado.pdf)</span></div>
              </div>

              <div className="mt-4">
                <p className="text-[12px] font-medium uppercase tracking-wide text-muted-foreground">Justificativa do estudante</p>
                <p className="mt-1 rounded-lg border border-border bg-muted/30 p-3 text-[13px] text-foreground">{s.motivo}</p>
              </div>

              <div className="mt-5">
                <p className="text-[12px] font-medium uppercase tracking-wide text-muted-foreground">Parecer da Secretaria</p>
                <Textarea className="mt-1" rows={3} placeholder="Descreva o parecer registrado no protocolo..." />
              </div>

              <ValidationCallout className="mt-4" tone="info">
                Toda alteração no histórico exige registro do responsável e fica auditável no log do estudante.
              </ValidationCallout>

              <div className="mt-4 flex flex-wrap justify-end gap-2">
                <Button variant="outline" onClick={() => decidir(s.id, "Pendente complemento", "marcada como pendente de complemento")}>
                  Solicitar complemento
                </Button>
                <Button variant="outline" onClick={() => decidir(s.id, "Indeferida", "indeferida")}>
                  <X className="mr-2 h-4 w-4" /> Indeferir
                </Button>
                <Button onClick={() => decidir(s.id, "Deferida", "deferida e histórico atualizado")}>
                  <Check className="mr-2 h-4 w-4" /> Deferir e atualizar histórico
                </Button>
              </div>
            </div>
          </div>
        );
      })()}

      {isSec && view.kind === "sec-aluno" && (() => {
        const a = alunosIniciais.find((x) => x.matricula === view.matricula);
        if (!a) return null;
        return (
          <div className="space-y-4">
            <Button variant="ghost" size="sm" onClick={() => setView({ kind: "sec-list" })}><ArrowLeft className="mr-1 h-4 w-4" /> Voltar</Button>
            <div className="rounded-xl border bg-card p-6 shadow-card">
              <div className="flex items-start justify-between gap-4">
                <SectionTitle title={a.nome} subtitle={`${a.curso} · Matrícula ${a.matricula}`} />
                <StatusBadge tone={a.situacao === "Ativo" ? "success" : "warning"}>{a.situacao}</StatusBadge>
              </div>
              <StatsRow className="mt-4" stats={[
                { label: "CR consolidado", value: a.cr, tone: "success" },
                { label: "Período atual", value: a.periodo, tone: "info" },
                { label: "Disciplinas cursadas", value: 38, tone: "info" },
                { label: "Integralização", value: "76%", tone: "warning" },
              ]} />

              <div className="mt-5 flex flex-wrap gap-2">
                <Button onClick={() => toast.success(`Histórico oficial de ${a.nome} emitido.`)}>
                  <Download className="mr-2 h-4 w-4" /> Emitir histórico oficial
                </Button>
                <Button variant="outline" onClick={() => toast.success("Declaração de matrícula gerada.")}>
                  Emitir declaração
                </Button>
              </div>
            </div>

            <div className="rounded-xl border bg-card p-5 shadow-card">
              <SectionTitle title="Períodos cursados" />
              <DataTable
                columns={[
                  { key: "p", header: "Período" },
                  { key: "aprov", header: "Aprovações", align: "right" },
                  { key: "reprov", header: "Reprovações", align: "right" },
                  { key: "cr", header: "CR período", align: "right" },
                  { key: "acoes", header: "", align: "right", render: () => (
                    <RowActionButton onClick={() => toast("Detalhe do período (mock).")}>Ver disciplinas</RowActionButton>
                  )},
                ]}
                rows={periodos}
              />
            </div>
          </div>
        );
      })()}
    </AppShell>
  );
}
