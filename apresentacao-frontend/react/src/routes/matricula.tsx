import { useState, useMemo } from "react";
import { createFileRoute } from "@tanstack/react-router";
import { useMutation, useQuery } from "@tanstack/react-query";
import type { TurmaResumo, ItemMatriculaResumo } from "@/lib/api";
import {
  AppShell, StatsRow, SuccessBanner, ScheduleGrid, SectionTitle, FormField,
  ValidationCallout, DataTable, StatusBadge, RowActionButton, Stepper,
  useProfileSwitcher,
} from "@/components/acadlab";
import type { ClassBlock } from "@/components/acadlab/organisms/ScheduleGrid";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Printer, Pencil, Lock, Plus, ArrowLeft, AlertTriangle } from "lucide-react";
import { toast } from "sonner";
import { api } from "@/lib/api";

type PedidoStatus = "Em análise" | "Deferida" | "Indeferida";
type Pedido = { id: string; aluno: string; tipo: string; aberta: string; status: PedidoStatus };

export const Route = createFileRoute("/matricula")({
  head: () => ({ meta: [{ title: "Matrícula — AcadLab" }] }),
  component: Page,
});

const MATRICULA_ID = 1;
const DISCIPLINA_EXCECAO_ID = 1;
const HOJE = new Date().toISOString().split("T")[0];
// Mapeamento fixo turmaId → disciplinaId (reflete o seed)
const TURMA_DISC: Record<number, number> = { 1: 101, 2: 201, 3: 301, 4: 401, 5: 501, 6: 601, 7: 701, 8: 801 };

const blocosConfirmados: ClassBlock[] = [
  { day: 1, start: 8, duration: 2, title: "Algoritmos Avançados", code: "AED301", color: "info" },
  { day: 1, start: 14, duration: 2, title: "Banco de Dados II", code: "BD302", color: "success" },
  { day: 2, start: 10, duration: 2, title: "Testes de Software", code: "ES303", color: "warning" },
  { day: 3, start: 8, duration: 2, title: "Algoritmos Avançados", code: "AED301", color: "info" },
  { day: 3, start: 16, duration: 2, title: "Gestão de Projetos", code: "GP306", color: "info" },
  { day: 4, start: 10, duration: 2, title: "Testes de Software", code: "ES303", color: "warning" },
  { day: 5, start: 14, duration: 2, title: "Redes Avançadas", code: "RC305", color: "danger" },
  { day: 5, start: 16, duration: 2, title: "Banco de Dados II", code: "BD302", color: "success" },
];

type Oferta = {
  turmaId: number; disciplinaId: number;
  codigo: string; nome: string; turma: string; prof: string; horario: string;
  creditos: number; vagas: string; status: "Disponível" | "Selecionada" | "Conflito" | "Sem vaga";
};

type View =
  | { kind: "overview" }
  | { kind: "wizard"; step: 0 | 1 | 2 | 3 }
  | { kind: "ajuste" }
  | { kind: "trancarDisc" }
  | { kind: "trancarPeriodo" }
  | { kind: "excecao"; disciplina?: string };

// ─── Persistência de sessão do wizard ────────────────────────────────────────
const WIZARD_KEY = "matricula_wizard_v1";
type WizardSession = { step: 0 | 1 | 2 | 3; ofertas: Oferta[] };

function salvarSessaoWizard(step: 0 | 1 | 2 | 3, ofertas: Oferta[]) {
  sessionStorage.setItem(WIZARD_KEY, JSON.stringify({ step, ofertas }));
}
function limparSessaoWizard() {
  sessionStorage.removeItem(WIZARD_KEY);
}
function restaurarSessaoWizard(): WizardSession | null {
  try {
    const raw = sessionStorage.getItem(WIZARD_KEY);
    return raw ? (JSON.parse(raw) as WizardSession) : null;
  } catch { return null; }
}

// ─── Persistência da grade confirmada ────────────────────────────────────────
const GRADE_KEY = "matricula_grade_v1";
const DIAS: Record<string, number> = { Seg: 1, Ter: 2, Qua: 3, Qui: 4, Sex: 5, Sab: 6, Sáb: 6 };
const CORES: ClassBlock["color"][] = ["info", "success", "warning", "violet", "danger"];

function ofertasParaBlocos(selecionadas: Oferta[]): ClassBlock[] {
  const blocos: ClassBlock[] = [];
  selecionadas.forEach((o, idx) => {
    const color = CORES[idx % CORES.length];
    // Formato: "Seg/Qua 08-10" ou "Ter/Qui 10-12" ou "Sex 14-18"
    const partes = o.horario.split(" ");
    const diasStr = partes[0] ?? "";
    const horas = partes[1] ?? "";
    const [hIni, hFim] = horas.split("-").map(Number);
    const duration = hFim - hIni;
    diasStr.split("/").forEach((d) => {
      const day = DIAS[d];
      if (day && !isNaN(hIni) && duration > 0) {
        blocos.push({ day, start: hIni, duration, title: o.nome, code: o.codigo, color });
      }
    });
  });
  return blocos;
}

function salvarGrade(selecionadas: Oferta[]) {
  localStorage.setItem(GRADE_KEY, JSON.stringify(ofertasParaBlocos(selecionadas)));
}

function carregarGrade(): ClassBlock[] {
  try {
    const raw = localStorage.getItem(GRADE_KEY);
    return raw ? (JSON.parse(raw) as ClassBlock[]) : blocosConfirmados;
  } catch { return blocosConfirmados; }
}

function turmaParaOferta(t: TurmaResumo, matriculadosIds: Set<number>): Oferta {
  const disc = DISC[t.disciplinaId] ?? { codigo: `DISC${t.disciplinaId}`, nome: "Desconhecida", creditos: 4, prof: "—", horario: "—" };
  const vagasOcupadas = 0; // sem endpoint de contagem — usamos placeholder
  const semVaga = vagasOcupadas >= t.capacidade;
  const jaSelecionada = matriculadosIds.has(t.id);
  return {
    turmaId: t.id,
    disciplinaId: t.disciplinaId,
    codigo: disc.codigo,
    nome: disc.nome,
    turma: `T0${t.id}`,
    prof: disc.prof,
    horario: disc.horario,
    creditos: disc.creditos,
    vagas: `—/${t.capacidade}`,
    status: semVaga ? "Sem vaga" : jaSelecionada ? "Selecionada" : "Disponível",
  };
}

function Page() {
  const { active: perfil } = useProfileSwitcher([
    { value: "estudante", label: "Estudante", description: "Faz e ajusta a própria matrícula" },
    { value: "secretaria", label: "Secretaria Acadêmica", description: "Gerencia exceções e trancamentos" },
  ]);

  // Busca o status real da matrícula (itens incluídos)
  const { data: matricula, refetch: refetchMatricula } = useQuery({
    queryKey: ["matricula", MATRICULA_ID],
    queryFn: () => api.matricula.getById(MATRICULA_ID),
    staleTime: 0,
  });
  const jaConfirmada = matricula?.status === "CONFIRMADA" || matricula?.status === "AGUARDANDO_SECRETARIA";
  const aguardandoSecretaria = matricula?.status === "AGUARDANDO_SECRETARIA";

  // IDs das turmas já adicionadas à matrícula no DB
  const turmasNoDb = useMemo(
    () => new Set((matricula?.itens ?? []).map((i) => i.turmaId)),
    [matricula]
  );

  // Busca turmas ofertadas no período 1
  const { data: turmasDB = [] } = useQuery({
    queryKey: ["turmas", "periodo", 1],
    queryFn: () => api.turmas.listByPeriodo(1),
    select: (r) => r ?? [],
  });

  // Monta lista de ofertas a partir das turmas do DB
  const ofertasFromDB = useMemo<Oferta[]>(
    () => turmasDB.map((t) => turmaParaOferta(t, turmasNoDb)),
    [turmasDB, turmasNoDb]
  );

  // Restaura sessão do wizard salva antes de um refresh
  const sessao = restaurarSessaoWizard();
  const [view, setView] = useState<View>(
    sessao && !jaConfirmada ? { kind: "wizard", step: sessao.step } : { kind: "overview" }
  );
  // Inicializa ofertas: da sessão salva, ou do DB (quando carregado)
  const [ofertas, setOfertas] = useState<Oferta[]>(sessao?.ofertas ?? []);
  // Quando DB carrega e não há sessão, sincroniza estado
  const ofertasVisiveis = ofertas.length > 0 ? ofertas : ofertasFromDB;

  const [grade, setGrade] = useState<ClassBlock[]>(() => carregarGrade());
  const [trancados, setTrancados] = useState<Trancados>(() => carregarTrancados());
  const [solicitacoes, setSolicitacoes] = useState<SolicitacaoAjuste[]>(() => carregarSolicitacoes());

  const decidirSolicitacao = (turmaId: number, aprovado: boolean) => {
    const sol = solicitacoes.find((s) => s.turmaId === turmaId);
    const novas = solicitacoes.filter((s) => s.turmaId !== turmaId);
    salvarSolicitacoes(novas);
    setSolicitacoes(novas);
    if (aprovado && sol) {
      const codigo = DISC[sol.disciplinaId]?.codigo;
      if (codigo) {
        const novaGrade = grade.filter((b) => b.code !== codigo);
        localStorage.setItem(GRADE_KEY, JSON.stringify(novaGrade));
        setGrade(novaGrade);
      }
      refetchMatricula();
    }
  };
  const selecionadas = ofertasVisiveis.filter((o) => o.status === "Selecionada");
  const creditos = selecionadas.reduce((s, o) => s + o.creditos, 0);
  const temConflito = selecionadas.some((o) => o.disciplinaId === 501) && selecionadas.some((o) => o.disciplinaId === 601);

  const toggle = (codigo: string) => {
    const base = ofertas.length > 0 ? ofertas : ofertasFromDB;
    const novas = base.map((o) => {
      if (o.codigo !== codigo) return o;
      if (o.status === "Sem vaga") return o;
      if (o.status === "Selecionada") return { ...o, status: "Disponível" as const };
      return { ...o, status: "Selecionada" as const };
    });
    if (view.kind === "wizard") salvarSessaoWizard(view.step, novas);
    setOfertas(novas);
  };

  const irParaStep = (step: 0 | 1 | 2 | 3) => {
    if (step === 3) {
      limparSessaoWizard();
    } else {
      salvarSessaoWizard(step, ofertasVisiveis);
    }
    setView({ kind: "wizard", step });
  };

  const voltarOverview = () => {
    limparSessaoWizard();
    setOfertas([]);
    setView({ kind: "overview" });
  };

  const subtitle = perfil === "secretaria"
    ? "Visão Secretaria Acadêmica · Exceções e trancamentos"
    : "Estudante: Maria Santos — 2025.2";

  return (
    <AppShell title="Matrícula" subtitle={subtitle}>
      {perfil === "secretaria" && <SecretariaView matriculaId={MATRICULA_ID} aguardandoSecretaria={aguardandoSecretaria} onAprovada={() => refetchMatricula()} solicitacoes={solicitacoes} onDecidiu={decidirSolicitacao} />}
      {perfil === "estudante" && view.kind === "overview" && (
        <Overview
          jaConfirmada={jaConfirmada}
          aguardandoSecretaria={aguardandoSecretaria}
          solicitacoes={solicitacoes}
          grade={grade}
          trancados={trancados}
          onNova={() => { setOfertas(ofertasFromDB); salvarSessaoWizard(0, ofertasFromDB); setView({ kind: "wizard", step: 0 }); }}
          onAjuste={() => setView({ kind: "ajuste" })}
          onTrancarDisc={() => setView({ kind: "trancarDisc" })}
          onTrancarPer={() => setView({ kind: "trancarPeriodo" })}
          onDestrancarDisc={() => setView({ kind: "excecao", disciplina: "Destrancamento de disciplina" })}
          onDestrancarPer={() => setView({ kind: "excecao", disciplina: "Destrancamento de período" })}
        />
      )}
      {perfil === "estudante" && view.kind === "wizard" && (
        <Wizard
          step={view.step}
          ofertas={ofertasVisiveis}
          selecionadas={selecionadas}
          creditos={creditos}
          temConflito={temConflito}
          inicialTurmaIds={[...turmasNoDb]}
          onToggle={toggle}
          onStep={irParaStep}
          onCancel={voltarOverview}
          onExcecao={(d) => setView({ kind: "excecao", disciplina: d })}
          onConfirmada={(sel) => { const blocos = ofertasParaBlocos(sel); salvarGrade(sel); setGrade(blocos); refetchMatricula(); }}
        />
      )}
      {perfil === "estudante" && view.kind === "ajuste" && (
        <Ajuste
          itensMatricula={matricula?.itens ?? []}
          onBack={() => setView({ kind: "overview" })}
          onSolicitar={(pendentes) => {
            const novas: SolicitacaoAjuste[] = pendentes.map((p) => ({
              turmaId: p.turmaId,
              disciplinaId: p.disciplinaId,
              estudante: "Maria Santos",
              dataHora: new Date().toISOString(),
            }));
            const todas = [...solicitacoes, ...novas];
            salvarSolicitacoes(todas);
            setSolicitacoes(todas);
            setView({ kind: "overview" });
          }}
        />
      )}
      {perfil === "estudante" && view.kind === "trancarDisc" && (
        <TrancarDisciplina
          itensMatricula={matricula?.itens ?? []}
          onBack={() => setView({ kind: "overview" })}
          onTrancou={(disc) => {
            const novo = { ...trancados, disciplinas: [...trancados.disciplinas, disc] };
            salvarTrancados(novo);
            setTrancados(novo);
          }}
        />
      )}
      {perfil === "estudante" && view.kind === "trancarPeriodo" && (
        <TrancarPeriodo
          onBack={() => setView({ kind: "overview" })}
          onTrancou={() => {
            const novo = { ...trancados, periodo: true };
            salvarTrancados(novo);
            setTrancados(novo);
          }}
        />
      )}
      {perfil === "estudante" && view.kind === "excecao" && (
        <Excecao disciplina={view.disciplina} onBack={() => setView({ kind: "wizard", step: 1 })} />
      )}
    </AppShell>
  );
}

function SecretariaView({ matriculaId, aguardandoSecretaria, onAprovada, solicitacoes, onDecidiu }: {
  matriculaId: number; aguardandoSecretaria: boolean; onAprovada: () => void;
  solicitacoes: SolicitacaoAjuste[]; onDecidiu: (turmaId: number, aprovado: boolean) => void;
}) {
  const [pedidos, setPedidos] = useState<Pedido[]>([
    { id: "MAT-2025-0231", aluno: "Maria Santos", tipo: "Exceção — pré-requisito", aberta: "12/03/2025", status: "Em análise" },
    { id: "MAT-2025-0240", aluno: "Pedro Almeida", tipo: "Trancamento de período", aberta: "15/03/2025", status: "Em análise" },
    { id: "MAT-2025-0245", aluno: "Júlia Rocha", tipo: "Trancamento — BD302", aberta: "18/03/2025", status: "Em análise" },
    { id: "MAT-2025-0210", aluno: "Lucas Pires", tipo: "Ajuste fora do prazo", aberta: "02/02/2025", status: "Deferida" },
  ]);
  const decidir = (id: string, status: "Deferida" | "Indeferida") => {
    setPedidos((p) => p.map((x) => x.id === id ? { ...x, status } : x));
    toast.success(`Solicitação ${id} ${status.toLowerCase()}.`);
  };

  const aprovar = useMutation({
    mutationFn: () => api.matricula.aprovarSecretaria(matriculaId),
    onSuccess: () => { toast.success("Matrícula aprovada pela secretaria!"); onAprovada(); },
    onError: () => toast.error("Erro ao aprovar matrícula."),
  });
  return (
    <div className="space-y-5">
      <StatsRow stats={[
        { label: "Matrículas confirmadas", value: 1284, tone: "success" },
        { label: "Em análise", value: pedidos.filter((p) => p.status === "Em análise").length + solicitacoes.length, tone: "warning" },
        { label: "Trancamentos abertos", value: 8, tone: "info" },
        { label: "Exceções deferidas", value: pedidos.filter((p) => p.status === "Deferida").length, tone: "success" },
      ]} />
      {solicitacoes.length > 0 && (
        <>
          <SectionTitle title="Solicitações de ajuste" subtitle="Cancelamentos solicitados por estudantes aguardando análise." />
          <DataTable
            columns={[
              { key: "estudante", header: "Estudante" },
              { key: "disciplina", header: "Disciplina", render: (r: SolicitacaoAjuste) => {
                const d = DISC[r.disciplinaId];
                return d ? `${d.codigo} — ${d.nome}` : `Turma ${r.turmaId}`;
              }},
              { key: "data", header: "Solicitado em", render: (r: SolicitacaoAjuste) =>
                new Date(r.dataHora).toLocaleString("pt-BR", { day: "2-digit", month: "2-digit", hour: "2-digit", minute: "2-digit" })
              },
              { key: "acoes", header: "", align: "right" as const, render: (r: SolicitacaoAjuste) => (
                <div className="flex justify-end gap-2">
                  <RowActionButton onClick={() => { onDecidiu(r.turmaId, false); toast.success("Cancelamento rejeitado."); }}>Rejeitar</RowActionButton>
                  <RowActionButton tone="info" onClick={() => {
                    api.matricula.cancelarItem(matriculaId, r.turmaId, { hoje: HOJE, inicio: HOJE, fim: HOJE })
                      .then(() => { onDecidiu(r.turmaId, true); toast.success("Cancelamento aprovado."); })
                      .catch((e: Error) => toast.error(e.message || "Erro ao aprovar."));
                  }}>Aprovar</RowActionButton>
                </div>
              )},
            ]}
            rows={solicitacoes}
          />
        </>
      )}
      <SectionTitle title="Solicitações de matrícula" subtitle="Exceções, trancamentos e ajustes aguardando triagem." />
      <DataTable
        columns={[
          { key: "id", header: "Protocolo" },
          { key: "aluno", header: "Estudante" },
          { key: "tipo", header: "Tipo" },
          { key: "aberta", header: "Aberta em" },
          { key: "status", header: "Status", render: (r) => (
            <StatusBadge tone={r.status === "Deferida" ? "success" : r.status === "Indeferida" ? "danger" : "info"}>{r.status}</StatusBadge>
          )},
          { key: "acoes", header: "", align: "right", render: (r) => (
            r.status === "Em análise" ? (
              <div className="flex justify-end gap-2">
                <RowActionButton onClick={() => decidir(r.id, "Indeferida")}>Indeferir</RowActionButton>
                <RowActionButton tone="info" onClick={() => decidir(r.id, "Deferida")}>Deferir</RowActionButton>
              </div>
            ) : <span className="text-[12px] text-muted-foreground">—</span>
          )},
        ]}
        rows={pedidos}
      />
      {aguardandoSecretaria && (
        <div className="rounded-xl border border-warning bg-warning/10 p-4 flex items-center justify-between">
          <div>
            <p className="font-semibold text-warning">Matrícula de Maria Santos aguardando aprovação</p>
            <p className="text-[13px] text-muted-foreground mt-1">O estudante confirmou a seleção de disciplinas. Aprove para finalizar.</p>
          </div>
          <Button onClick={() => aprovar.mutate()} disabled={aprovar.isPending} className="shrink-0">
            {aprovar.isPending ? "Aprovando…" : "Aprovar Matrícula"}
          </Button>
        </div>
      )}
    </div>
  );
}

function Overview({ jaConfirmada, aguardandoSecretaria, solicitacoes, grade, trancados, onNova, onAjuste, onTrancarDisc, onTrancarPer, onDestrancarDisc, onDestrancarPer }: {
  jaConfirmada: boolean; aguardandoSecretaria: boolean; solicitacoes: SolicitacaoAjuste[]; grade: ClassBlock[]; trancados: Trancados;
  onNova: () => void; onAjuste: () => void; onTrancarDisc: () => void; onTrancarPer: () => void;
  onDestrancarDisc: () => void; onDestrancarPer: () => void;
}) {
  const discCount = grade.reduce((s, b) => { s.add(b.code); return s; }, new Set<string>()).size || (jaConfirmada ? 3 : 5);
  const credCount = discCount * 4;
  const temTrancDisc = trancados.disciplinas.length > 0;
  const temTrancPer = trancados.periodo;
  return (
    <div className="space-y-5">
      {aguardandoSecretaria && (
        <ValidationCallout tone="warning">
          Matrícula enviada — <strong>aguardando aprovação da secretaria</strong>. Você será notificado quando for confirmada.
        </ValidationCallout>
      )}
      {solicitacoes.length > 0 && (
        <ValidationCallout tone="info">
          <strong>{solicitacoes.length} disciplina(s)</strong> com solicitação de cancelamento aguardando análise da secretaria:{" "}
          {solicitacoes.map((s) => DISC[s.disciplinaId]?.codigo ?? `Turma ${s.turmaId}`).join(", ")}.
        </ValidationCallout>
      )}
      {jaConfirmada && !aguardandoSecretaria
        ? <SuccessBanner title="Matrícula 2026.1 confirmada" description={`${discCount} disciplinas · ${credCount} créditos · janela de ajuste aberta`} />
        : !aguardandoSecretaria && <SuccessBanner title="Matrícula 2025.2 confirmada" description="5 disciplinas · 18 créditos · janela de ajuste aberta até 23/03" />
      }
      {temTrancPer && (
        <ValidationCallout tone="warning">
          Período trancado. Para solicitar destrancamento,{" "}
          <button className="underline font-medium" onClick={onDestrancarPer}>clique aqui</button>.
        </ValidationCallout>
      )}
      {temTrancDisc && (
        <ValidationCallout tone="warning">
          {trancados.disciplinas.length} disciplina(s) trancada(s): {trancados.disciplinas.join(", ")}.{" "}
          <button className="underline font-medium" onClick={onDestrancarDisc}>Solicitar destrancamento</button>.
        </ValidationCallout>
      )}
      <StatsRow stats={[
        { label: "Disciplinas matriculadas", value: jaConfirmada ? discCount : 5, tone: "info" },
        { label: "Créditos no período", value: jaConfirmada ? credCount : 18, tone: "success" },
        { label: "Janela de ajuste", value: "11 dias", tone: "warning" },
        { label: "Pendências", value: temTrancDisc || temTrancPer ? 1 : 0, tone: temTrancDisc || temTrancPer ? "warning" : "success" },
      ]} />
      <div className="flex flex-wrap gap-2">
        {!jaConfirmada && (
          <Button onClick={onNova}><Plus className="mr-2 h-4 w-4" /> Iniciar Matrícula 2026.1</Button>
        )}
        {(() => {
          // Ajuste, Trancar Disciplina e Trancar Período só funcionam com status CONFIRMADA
          const soConfirmada = jaConfirmada && !aguardandoSecretaria;
          const motivoBloqueio = aguardandoSecretaria
            ? "Aguardando aprovação da secretaria"
            : !jaConfirmada
            ? "Inicie e confirme a matrícula primeiro"
            : undefined;
          return (
            <>
              <Button
                variant="outline" className="border-primary text-primary"
                onClick={onAjuste}
                disabled={!soConfirmada}
                title={motivoBloqueio}
              >
                <Pencil className="mr-2 h-4 w-4" /> Solicitar Ajuste
              </Button>
              {temTrancDisc
                ? <Button variant="outline" className="border-warning text-warning" onClick={onDestrancarDisc}><Lock className="mr-2 h-4 w-4" /> Disciplina trancada · Desfazer</Button>
                : <Button variant="outline" className="border-warning text-warning" onClick={onTrancarDisc} disabled={!soConfirmada} title={motivoBloqueio}><Lock className="mr-2 h-4 w-4" /> Trancar Disciplina</Button>
              }
              {temTrancPer
                ? <Button variant="outline" className="border-destructive text-destructive" onClick={onDestrancarPer}>Período trancado · Desfazer</Button>
                : <Button variant="outline" className="border-destructive text-destructive" onClick={onTrancarPer} disabled={!soConfirmada} title={motivoBloqueio}>Trancar Período</Button>
              }
            </>
          );
        })()}
        <Button variant="secondary" onClick={() => toast.success("Grade impressa (PDF gerado).")}><Printer className="mr-2 h-4 w-4" /> Imprimir grade</Button>
      </div>
      <SectionTitle title="Grade de horários — 2026.1" subtitle={jaConfirmada ? "Disciplinas da matrícula confirmada" : "Matrícula confirmada em 12/01/2025"} />
      <ScheduleGrid blocks={grade} />
    </div>
  );
}

const wizardSteps = [
  { key: "plano", label: "Montar plano" },
  { key: "valid", label: "Validação" },
  { key: "conf", label: "Confirmação" },
  { key: "ok", label: "Concluído" },
];

function Wizard({ step, ofertas, selecionadas, creditos, temConflito, inicialTurmaIds, onToggle, onStep, onCancel, onExcecao, onConfirmada }: {
  step: 0 | 1 | 2 | 3; ofertas: Oferta[]; selecionadas: Oferta[]; creditos: number;
  temConflito: boolean; inicialTurmaIds: number[];
  onToggle: (c: string) => void; onStep: (s: 0|1|2|3) => void;
  onCancel: () => void; onExcecao: (d?: string) => void; onConfirmada: (sel: Oferta[]) => void;
}) {
  const confirmar = useMutation({
    mutationFn: async () => {
      const selIds = new Set(selecionadas.map((o) => o.turmaId));
      const iniciais = new Set(inicialTurmaIds);
      // Adicionar turmas novas (selecionadas mas não no DB)
      const adicionar = selecionadas.filter((o) => !iniciais.has(o.turmaId));
      // Remover turmas que estavam no DB mas foram desmarcadas
      const remover = inicialTurmaIds.filter((id) => !selIds.has(id));
      await Promise.all([
        ...adicionar.map((o) =>
          api.matricula.adicionarItem(MATRICULA_ID, {
            turmaId: o.turmaId,
            disciplinaId: o.disciplinaId,
            creditos: o.creditos,
            cumpriuPreRequisitos: true,
            correquisitosNoPlano: true,
            temPendencias: false,
            hoje: HOJE,
            inicioJanela: HOJE,
            fimJanela: HOJE,
          })
        ),
        ...remover.map((id) => api.matricula.removerItem(MATRICULA_ID, id)),
      ]);
      const vagasPorTurma = Object.fromEntries(selecionadas.map((o) => [o.turmaId, o.turmaId * 10 + 30]));
      await api.matricula.confirmar(MATRICULA_ID, vagasPorTurma);
    },
    onSuccess: () => { toast.success("Matrícula enviada para aprovação da secretaria!"); onConfirmada(selecionadas); onStep(3); },
    onError: (e) => toast.error(`Erro ao confirmar: ${e instanceof Error ? e.message : "tente novamente"}`),
  });
  return (
    <div className="space-y-5">
      <Button variant="ghost" size="sm" onClick={onCancel}><ArrowLeft className="mr-1 h-4 w-4" /> Cancelar matrícula</Button>
      <Stepper steps={wizardSteps} current={step} />
      {step === 0 && (
        <>
          <div className="flex flex-wrap items-center gap-6 rounded-xl border bg-card p-4 text-[13px] shadow-card">
            <div><span className="text-muted-foreground">Créditos </span><span className="font-semibold">{creditos} / 24</span></div>
            <div><span className="text-muted-foreground">Disciplinas </span><span className="font-semibold">{selecionadas.length}</span></div>
            <div><span className="text-muted-foreground">Conflitos </span><span className={`font-semibold ${temConflito ? "text-destructive" : "text-success"}`}>{temConflito ? 1 : 0}</span></div>
            <div className="ml-auto"><Button disabled={selecionadas.length === 0} onClick={() => onStep(1)}>Validar plano</Button></div>
          </div>
          <DataTable
            columns={[
              { key: "codigo", header: "Código" }, { key: "nome", header: "Disciplina" },
              { key: "prof", header: "Professor" }, { key: "horario", header: "Horário" },
              { key: "creditos", header: "Cr.", align: "right" }, { key: "vagas", header: "Vagas" },
              { key: "status", header: "Status", render: (r) => (
                <StatusBadge tone={r.status === "Selecionada" ? "success" : r.status === "Sem vaga" ? "danger" : "info"}>{r.status}</StatusBadge>
              )},
              { key: "acoes", header: "", align: "right", render: (r) => (
                r.status === "Sem vaga"
                  ? <RowActionButton tone="neutral" onClick={() => onExcecao(`${r.codigo} — ${r.nome}`)}>Solicitar exceção</RowActionButton>
                  : <RowActionButton tone={r.status === "Selecionada" ? "danger" : "info"} onClick={() => onToggle(r.codigo)}>{r.status === "Selecionada" ? "Remover" : "Selecionar"}</RowActionButton>
              )},
            ]}
            rows={ofertas}
          />
        </>
      )}
      {step === 1 && (
        <div className="space-y-4">
          <SectionTitle title="Validação automática do plano" subtitle="Pré-requisitos, choque de horário, créditos e correquisitos." />
          {temConflito && <ValidationCallout tone="error">Choque de horário entre ES401 e IA401 às terças 10–12.</ValidationCallout>}
          {creditos > 24 && <ValidationCallout tone="error">Você excedeu o limite de 24 créditos.</ValidationCallout>}
          {!temConflito && creditos <= 24 && (
            <ValidationCallout tone="info">Plano válido: {selecionadas.length} disciplinas · {creditos} créditos · 0 conflitos.</ValidationCallout>
          )}
          <DataTable columns={[
            { key: "codigo", header: "Código" }, { key: "nome", header: "Disciplina" },
            { key: "horario", header: "Horário" }, { key: "creditos", header: "Cr.", align: "right" },
          ]} rows={selecionadas} />
          <div className="flex justify-end gap-2">
            <Button variant="outline" onClick={() => onStep(0)}>Voltar</Button>
            <Button disabled={temConflito} onClick={() => onStep(2)}>Avançar para confirmação</Button>
          </div>
        </div>
      )}
      {step === 2 && (
        <div className="rounded-xl border bg-card p-6 shadow-card">
          <SectionTitle title="Confirmar matrícula 2026.1" subtitle="Revise as turmas antes de confirmar. Esta ação reserva as vagas." />
          <DataTable className="mt-4" columns={[
            { key: "codigo", header: "Código" }, { key: "nome", header: "Disciplina" },
            { key: "turma", header: "Turma" }, { key: "horario", header: "Horário" },
            { key: "creditos", header: "Cr.", align: "right" },
          ]} rows={selecionadas} />
          <div className="mt-4 flex items-center justify-between">
            <p className="text-[13px] text-muted-foreground">Total: <span className="font-semibold text-foreground">{creditos} créditos</span></p>
            <div className="flex gap-2">
              <Button variant="outline" onClick={() => onStep(1)}>Voltar</Button>
              <Button onClick={() => confirmar.mutate()} disabled={confirmar.isPending}>
                {confirmar.isPending ? "Confirmando…" : "Confirmar matrícula"}
              </Button>
            </div>
          </div>
        </div>
      )}
      {step === 3 && (
        <div className="space-y-4">
          <SuccessBanner title="Matrícula 2026.1 enviada para aprovação!" description={`${selecionadas.length} disciplinas · ${creditos} créditos. Aguardando confirmação da secretaria.`} />
          <Button variant="outline" onClick={onCancel}>Voltar à visão geral</Button>
        </div>
      )}
    </div>
  );
}

// Mapa estático: disciplinaId → { codigo, nome, creditos, prof, horario }
const DISC: Record<number, { codigo: string; nome: string; creditos: number; prof: string; horario: string }> = {
  101: { codigo: "AED301", nome: "Algoritmos e Estruturas de Dados", creditos: 4, prof: "Carlos Lima",       horario: "Seg/Qua 08-10" },
  201: { codigo: "BD302",  nome: "Banco de Dados II",                creditos: 4, prof: "Ana Souza",         horario: "Seg/Sex 14-18" },
  301: { codigo: "ES303",  nome: "Testes de Software",               creditos: 4, prof: "Marcos Rodrigues",  horario: "Ter/Qui 10-12" },
  401: { codigo: "GP306",  nome: "Gestão de Projetos",               creditos: 4, prof: "Carlos Lima",       horario: "Qua/Sex 19-21" },
  501: { codigo: "ES401",  nome: "Arquitetura de Software",          creditos: 4, prof: "Ana Souza",         horario: "Sex 08-12" },
  601: { codigo: "IA401",  nome: "Inteligência Artificial",          creditos: 4, prof: "Dra. Lúcia Mendes", horario: "Ter/Qui 14-16" },
  701: { codigo: "BD402",  nome: "Big Data",                         creditos: 4, prof: "Marcos Rodrigues",  horario: "Qua 14-18" },
  801: { codigo: "ES501",  nome: "Eng. de Software III",             creditos: 4, prof: "Carlos Lima",       horario: "Seg/Qua 10-12" },
};

// ─── Trancamentos persistidos ────────────────────────────────────────────────
const TRANCADOS_KEY = "matricula_trancados_v1";
type Trancados = { disciplinas: string[]; periodo: boolean };

function carregarTrancados(): Trancados {
  try {
    const raw = localStorage.getItem(TRANCADOS_KEY);
    return raw ? (JSON.parse(raw) as Trancados) : { disciplinas: [], periodo: false };
  } catch { return { disciplinas: [], periodo: false }; }
}

function salvarTrancados(t: Trancados) {
  localStorage.setItem(TRANCADOS_KEY, JSON.stringify(t));
}

// ─── Solicitações de ajuste (cancelamento via secretaria) ────────────────────
const AJUSTE_SOL_KEY = "ajuste_solicitacoes_v1";
type SolicitacaoAjuste = { turmaId: number; disciplinaId: number; estudante: string; dataHora: string };

function carregarSolicitacoes(): SolicitacaoAjuste[] {
  try {
    const raw = localStorage.getItem(AJUSTE_SOL_KEY);
    return raw ? (JSON.parse(raw) as SolicitacaoAjuste[]) : [];
  } catch { return []; }
}
function salvarSolicitacoes(s: SolicitacaoAjuste[]) {
  localStorage.setItem(AJUSTE_SOL_KEY, JSON.stringify(s));
}

type AjustePendente = { turmaId: number; disciplinaId: number };

function Ajuste({ itensMatricula, onBack, onSolicitar }: {
  itensMatricula: ItemMatriculaResumo[];
  onBack: () => void;
  onSolicitar: (pendentes: AjustePendente[]) => void;
}) {
  const { data: turmas = [] } = useQuery({
    queryKey: ["turmas", "periodo", 1],
    queryFn: () => api.turmas.listByPeriodo(1),
    select: (r) => r ?? [],
  });

  const [pendentes, setPendentes] = useState<AjustePendente[]>([]);

  // Apenas itens com statusItem CONFIRMADO podem ser cancelados no ajuste
  // Exclui os que já estão marcados para cancelar (desaparecem da lista ao marcar)
  const idsPendentes = new Set(pendentes.map((p) => p.turmaId));
  const idsConfirmados = new Set(
    itensMatricula.filter((i) => i.statusItem === "CONFIRMADO").map((i) => i.turmaId)
  );
  const matriculadas = turmas.filter((t) => idsConfirmados.has(t.id) && !idsPendentes.has(t.id));

  const marcarExcluir = (t: typeof turmas[0]) => {
    if (pendentes.some((p) => p.turmaId === t.id)) return;
    setPendentes((prev) => [...prev, { turmaId: t.id, disciplinaId: t.disciplinaId }]);
  };

  const reverter = (turmaId: number) => {
    const d = DISC[pendentes.find((p) => p.turmaId === turmaId)?.disciplinaId ?? 0];
    setPendentes((prev) => prev.filter((p) => p.turmaId !== turmaId));
    toast.success(`Cancelamento de ${d?.codigo ?? turmaId} revertido.`);
  };

  const confirmar = useMutation({
    mutationFn: () =>
      Promise.all(
        pendentes.map((p) =>
          api.matricula.cancelarItem(MATRICULA_ID, p.turmaId, { hoje: HOJE, inicio: HOJE, fim: HOJE })
        )
      ),
    onSuccess: () => {
      toast.success(`${pendentes.length} solicitação(es) de cancelamento enviada(s) para a secretaria.`);
      onSolicitar(pendentes);
    },
    onError: (e: Error) => toast.error(e.message || "Erro ao confirmar ajustes."),
  });

  return (
    <div className="space-y-5">
      <Button variant="ghost" size="sm" onClick={onBack}><ArrowLeft className="mr-1 h-4 w-4" /> Voltar</Button>
      <SectionTitle title="Ajuste de matrícula" subtitle="Janela aberta até 23/03/2025. Cancelamentos são imediatos após confirmação." />

      {/* Disciplinas matriculadas */}
      <div>
        <p className="mb-2 text-sm font-medium text-muted-foreground">Disciplinas matriculadas</p>
        <DataTable
          columns={[
            { key: "codigo", header: "Código",     render: (r) => DISC[r.disciplinaId]?.codigo ?? r.disciplinaId },
            { key: "nome",   header: "Disciplina", render: (r) => DISC[r.disciplinaId]?.nome   ?? "—" },
            { key: "modal",  header: "Modalidade", render: (r) => r.modalidade },
            { key: "acoes",  header: "", align: "right", render: (r) => {
              const marcada = pendentes.some((p) => p.turmaId === r.id);
              return marcada
                ? <StatusBadge tone="danger">A cancelar</StatusBadge>
                : <RowActionButton tone="danger" onClick={() => marcarExcluir(r)}>Cancelar</RowActionButton>;
            }},
          ]}
          rows={matriculadas}
        />
      </div>

      {/* Pendentes */}
      {pendentes.length > 0 && (
        <div>
          <p className="mb-2 text-sm font-medium text-muted-foreground">Cancelamentos pendentes</p>
          <DataTable
            columns={[
              { key: "codigo", header: "Código",     render: (r) => DISC[r.disciplinaId]?.codigo ?? r.turmaId },
              { key: "nome",   header: "Disciplina", render: (r) => DISC[r.disciplinaId]?.nome   ?? "—" },
              { key: "acoes",  header: "", align: "right", render: (r) => (
                <RowActionButton onClick={() => reverter(r.turmaId)}>Reverter</RowActionButton>
              )},
            ]}
            rows={pendentes}
          />
        </div>
      )}

      <ValidationCallout tone="info">
        Para incluir novas disciplinas durante o período de ajuste, use "Solicitar Ajuste Excepcional".
      </ValidationCallout>

      <div className="flex justify-end gap-2">
        <Button variant="outline" onClick={onBack}>Cancelar</Button>
        <Button onClick={() => confirmar.mutate()} disabled={confirmar.isPending || pendentes.length === 0}>
          {confirmar.isPending ? "Enviando…" : `Solicitar cancelamento${pendentes.length > 0 ? ` (${pendentes.length})` : ""}`}
        </Button>
      </div>
    </div>
  );
}

function TrancarDisciplina({ itensMatricula, onBack, onTrancou }: {
  itensMatricula: ItemMatriculaResumo[]; onBack: () => void; onTrancou: (disc: string) => void;
}) {
  // Apenas itens CONFIRMADO podem ser trancados (domínio: item.trancar() exige CONFIRMADO)
  const turmasDisponiveis = itensMatricula
    .filter((i) => i.statusItem === "CONFIRMADO")
    .map((i) => { const d = DISC[i.disciplinaId]; return { turmaId: i.turmaId, label: d ? `${d.codigo} — ${d.nome}` : `Turma ${i.turmaId}` }; });

  const primeiro = turmasDisponiveis[0];
  const [turmaId, setTurmaId] = useState<number>(primeiro?.turmaId ?? TURMAS_BASE[0]);
  const label = turmasDisponiveis.find((t) => t.turmaId === turmaId)?.label ?? `Turma ${turmaId}`;

  const trancar = useMutation({
    mutationFn: () => api.matricula.trancarDisciplina(MATRICULA_ID, turmaId, { hoje: HOJE, inicio: HOJE, fim: HOJE }),
    onSuccess: () => { toast.success("Disciplina trancada."); onTrancou(label); onBack(); },
    onError: (e: Error) => toast.error(e.message || "Erro ao trancar disciplina."),
  });
  return (
    <div className="space-y-4">
      <Button variant="ghost" size="sm" onClick={onBack}><ArrowLeft className="mr-1 h-4 w-4" /> Voltar</Button>
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Trancar disciplina" />
        {turmasDisponiveis.length === 0 ? (
          <ValidationCallout className="mt-4" tone="error">Não há disciplinas disponíveis para trancar.</ValidationCallout>
        ) : (
          <div className="mt-4 grid grid-cols-2 gap-4">
            <FormField label="Disciplina" required>
              <Select value={String(turmaId)} onValueChange={(v) => setTurmaId(Number(v))}>
                <SelectTrigger className="h-10"><SelectValue /></SelectTrigger>
                <SelectContent>
                  {turmasDisponiveis.map((t) => (
                    <SelectItem key={t.turmaId} value={String(t.turmaId)}>{t.label}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </FormField>
            <FormField label="Motivo" required><Input className="h-10" placeholder="Ex.: incompatibilidade de horário com estágio" /></FormField>
            <FormField label="Justificativa" full><Textarea rows={4} /></FormField>
          </div>
        )}
        <ValidationCallout className="mt-4" tone="info">O trancamento não conta como reprovação, mas impacta o CR.</ValidationCallout>
        <div className="mt-4 flex justify-end gap-2">
          <Button variant="outline" onClick={onBack}>Cancelar</Button>
          <Button variant="destructive" onClick={() => trancar.mutate()} disabled={trancar.isPending || turmasDisponiveis.length === 0}>
            {trancar.isPending ? "Trancando…" : "Trancar disciplina"}
          </Button>
        </div>
      </div>
    </div>
  );
}

function TrancarPeriodo({ onBack, onTrancou }: { onBack: () => void; onTrancou: () => void }) {
  const trancar = useMutation({
    mutationFn: () => api.matricula.trancarPeriodo(MATRICULA_ID, { hoje: HOJE, inicioTrancamento: HOJE, fimTrancamento: HOJE, totalTrancamentos: 0, limiteTrancamentos: 2 }),
    onSuccess: () => { toast.success("Período trancado."); onTrancou(); onBack(); },
    onError: (e: Error) => toast.error(e.message || "Erro ao trancar período."),
  });
  return (
    <div className="space-y-4">
      <Button variant="ghost" size="sm" onClick={onBack}><ArrowLeft className="mr-1 h-4 w-4" /> Voltar</Button>
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Trancamento do período letivo" />
        <div className="mt-3 flex items-start gap-3 rounded-lg bg-destructive/10 p-3 text-destructive">
          <AlertTriangle className="mt-0.5 h-5 w-5 shrink-0" />
          <p className="text-[13px]">Você está prestes a trancar <strong>todo o período 2025.2</strong>. Esta ação é irreversível dentro do semestre.</p>
        </div>
        <FormField className="mt-4" label="Justificativa" required full><Textarea rows={4} /></FormField>
        <ValidationCallout className="mt-4" tone="info">Limite: 2 trancamentos consecutivos permitidos pela matriz.</ValidationCallout>
        <div className="mt-4 flex justify-end gap-2">
          <Button variant="outline" onClick={onBack}>Voltar</Button>
          <Button variant="destructive" onClick={() => trancar.mutate()} disabled={trancar.isPending}>
            {trancar.isPending ? "Trancando…" : "Trancar período"}
          </Button>
        </div>
      </div>
    </div>
  );
}

function Excecao({ disciplina, onBack }: { disciplina?: string; onBack: () => void }) {
  const [motivo, setMotivo] = useState("");
  const enviar = useMutation({
    mutationFn: () => api.matricula.solicitarExcecao(MATRICULA_ID, { disciplinaId: DISCIPLINA_EXCECAO_ID, motivo }),
    onSuccess: () => { toast.success("Solicitação enviada."); onBack(); },
    onError: () => toast.error("Erro ao enviar solicitação."),
  });
  return (
    <div className="space-y-4">
      <Button variant="ghost" size="sm" onClick={onBack}><ArrowLeft className="mr-1 h-4 w-4" /> Voltar ao plano</Button>
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Solicitar exceção de matrícula" subtitle="A solicitação será analisada pela coordenação." />
        <div className="mt-4 grid grid-cols-2 gap-4">
          <FormField label="Tipo" required><Input className="h-10" defaultValue="Quebra de pré-requisito / vaga adicional" /></FormField>
          <FormField label="Disciplina" required><Input className="h-10" defaultValue={disciplina ?? ""} /></FormField>
          <FormField label="Justificativa" required full>
            <Textarea rows={4} value={motivo} onChange={(e) => setMotivo(e.target.value)} />
          </FormField>
          <FormField label="Anexo" full><Input type="file" className="h-10" /></FormField>
        </div>
        <div className="mt-4 flex justify-end gap-2">
          <Button variant="outline" onClick={onBack}>Cancelar</Button>
          <Button onClick={() => enviar.mutate()} disabled={enviar.isPending}>
            {enviar.isPending ? "Enviando…" : "Enviar solicitação"}
          </Button>
        </div>
      </div>
    </div>
  );
}
