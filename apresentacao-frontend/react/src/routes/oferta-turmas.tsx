import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { createFileRoute } from "@tanstack/react-router";
import {
  AppShell, SectionTitle, StatsRow, DataTable, StatusBadge, RowActionButton,
  ActionBar, FormField, ValidationCallout, Stepper, TabsRow, ProgressRow,
  useProfileSwitcher,
} from "@/components/acadlab";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Sheet, SheetContent, SheetHeader, SheetTitle } from "@/components/ui/sheet";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogFooter } from "@/components/ui/dialog";
import { ArrowLeft, Send, MapPin, CheckCircle2 } from "lucide-react";
import { toast } from "sonner";
import { api, type DiarioTurmaDetalhadoResumo, type ResultadoResumo } from "@/lib/api";

export const Route = createFileRoute("/oferta-turmas")({
  head: () => ({ meta: [{ title: "Oferta de Turmas — AcadLab" }] }),
  component: Page,
});

// ─── Constantes professor (F05) ───────────────────────────────────────────────

const PROFESSOR_ID = 1;
const AULAS_TOTAL_PADRAO = 30;

// ─── Tipos e dados mock (Coord / Secretaria) ──────────────────────────────────

type Turma = {
  id: string; codigo: string; disciplina: string; turma: string; prof: string;
  sala: string; horario: string; vagas: string; status: "Aberta" | "Lotada" | "Cancelada";
};

const turmasIniciais: Turma[] = [
  { id: "T1", codigo: "AED301", disciplina: "Algoritmos Avançados", turma: "T01", prof: "Carlos Lima", sala: "B-203", horario: "Seg/Qua 08-10", vagas: "28/30", status: "Aberta" },
  { id: "T2", codigo: "BD302", disciplina: "Banco de Dados II", turma: "T01", prof: "Ana Souza", sala: "B-105", horario: "Seg 14 · Sex 16", vagas: "30/30", status: "Lotada" },
  { id: "T3", codigo: "ES303", disciplina: "Testes de Software", turma: "T02", prof: "Marcos R.", sala: "B-301", horario: "Ter/Qui 10-12", vagas: "22/30", status: "Aberta" },
  { id: "T4", codigo: "IA401", disciplina: "Inteligência Artificial", turma: "T01", prof: "Lia Mendes", sala: "Lab-IA", horario: "Ter/Qui 14-16", vagas: "8/25", status: "Aberta" },
];

const tabsCoord = [
  { value: "turmas", label: "Turmas" },
  { value: "salas", label: "Salas" },
  { value: "profs", label: "Professores" },
];
const tabsSec = [
  { value: "turmas", label: "Turmas a alocar" },
  { value: "salas", label: "Mapa de salas" },
  { value: "publicar", label: "Publicação da oferta" },
];
const subTabsProf = [
  { value: "overview", label: "Visão geral" },
  { value: "aulas", label: "Aulas" },
  { value: "freq", label: "Frequência" },
  { value: "aval", label: "Avaliações & Notas" },
  { value: "fechar", label: "Fechamento" },
];

// ─── Page ─────────────────────────────────────────────────────────────────────

function Page() {
  const { active: perfil } = useProfileSwitcher([
    { value: "coordenacao", label: "Coordenação Acadêmica", description: "Planeja e abre turmas" },
    { value: "secretaria", label: "Secretaria Acadêmica", description: "Aloca salas e publica oferta" },
    { value: "professor", label: "Professor", description: "Lança aulas, frequência e notas" },
  ]);

  const isProf = perfil === "professor";
  const isSec  = perfil === "secretaria";

  const [tab, setTab] = useState("turmas");
  const [selected, setSelected] = useState<Turma | null>(null);
  const [wizard, setWizard] = useState<null | 0 | 1 | 2>(null);
  const [publicada, setPublicada] = useState(false);
  const [editTurma, setEditTurma] = useState<Turma | null>(null);

  const validTabs = (isSec ? tabsSec : tabsCoord).map((t) => t.value);
  if (!isProf && !validTabs.includes(tab)) setTab("turmas");

  const subtitle = isProf
    ? "Professor: Carlos Lima — 2025.2"
    : isSec
    ? "Visão Secretaria · Alocação e publicação"
    : "Coordenador — Período 2025.2";

  return (
    <AppShell title="Planejamento e Oferta de Turmas" subtitle={subtitle}>
      {!isProf && (
        <TabsRow items={isSec ? tabsSec : tabsCoord} value={tab} onChange={setTab} className="mb-5" />
      )}

      {/* ── Visão Professor (F05) ── */}
      {isProf && <ProfessorView />}

      {/* ── Visão Coordenação / Secretaria ── */}
      {!isProf && tab === "turmas" && !wizard && (
        <div className="space-y-5">
          <StatsRow stats={isSec ? [
            { label: "Sem sala", value: 1, tone: "danger" },
            { label: "Aguardando publicação", value: turmasIniciais.length, tone: "warning" },
            { label: "Salas disponíveis", value: 12, tone: "success" },
            { label: "Conflitos de alocação", value: 0, tone: "success" },
          ] : [
            { label: "Turmas ofertadas", value: turmasIniciais.length, tone: "info" },
            { label: "Vagas abertas", value: 28, tone: "success" },
            { label: "Lotadas", value: 1, tone: "warning" },
            { label: "Conflitos detectados", value: 0, tone: "success" },
          ]} />
          {isSec ? (
            <ActionBar searchPlaceholder="Buscar turma a alocar..." />
          ) : (
            <ActionBar searchPlaceholder="Buscar turma..." primaryLabel="Nova turma" onPrimary={() => setWizard(0)} />
          )}
          <DataTable
            columns={[
              { key: "codigo", header: "Disciplina" },
              { key: "disciplina", header: "Nome" },
              { key: "turma", header: "Turma" }, { key: "prof", header: "Professor" },
              { key: "sala", header: "Sala" }, { key: "horario", header: "Horário" },
              { key: "vagas", header: "Vagas" },
              { key: "status", header: "Status", render: (r) => (
                <StatusBadge tone={r.status === "Aberta" ? "success" : r.status === "Lotada" ? "warning" : "danger"}>{r.status}</StatusBadge>
              )},
              { key: "acoes", header: "", align: "right", render: (r) => (
                isSec
                  ? <RowActionButton onClick={() => toast.success(`Sala ${r.sala} confirmada para ${r.codigo}.`)}><MapPin className="mr-1 h-3 w-3 inline" /> Alocar sala</RowActionButton>
                  : <RowActionButton onClick={() => setSelected(r)}>Detalhes</RowActionButton>
              )},
            ]}
            rows={turmasIniciais}
          />
        </div>
      )}

      {!isProf && tab === "turmas" && wizard !== null && (
        <NovaTurmaWizard step={wizard} onStep={setWizard} onCancel={() => setWizard(null)} />
      )}

      {!isProf && tab === "salas" && <SalasTab />}
      {!isProf && tab === "profs" && !isSec && <ProfsTab />}

      {!isProf && tab === "publicar" && isSec && (
        <div className="space-y-4">
          <div className="rounded-xl border bg-card p-6 shadow-card">
            <SectionTitle title="Publicação da oferta 2025.2" subtitle="Após publicada, as turmas ficam visíveis para os estudantes na matrícula." />
            <ul className="mt-4 space-y-2 text-[13px] text-foreground">
              <li>✓ {turmasIniciais.length} turmas conferidas</li>
              <li>✓ Todas com sala alocada</li>
              <li>✓ Sem conflitos de horário</li>
              <li className={publicada ? "text-success" : "text-muted-foreground"}>
                {publicada ? "✓ Publicada em 20/06/2026 às 14:32" : "Aguardando publicação"}
              </li>
            </ul>
            <ValidationCallout className="mt-4" tone="info">
              Publicar gera notificação a todos os estudantes elegíveis e abre a janela de matrícula.
            </ValidationCallout>
            <div className="mt-4 flex justify-end">
              <Button disabled={publicada} onClick={() => { setPublicada(true); toast.success("Oferta 2025.2 publicada com sucesso."); }}>
                <Send className="mr-2 h-4 w-4" /> {publicada ? "Oferta publicada" : "Publicar oferta"}
              </Button>
            </div>
          </div>
        </div>
      )}

      <Sheet open={!!selected} onOpenChange={(o) => !o && setSelected(null)}>
        <SheetContent className="w-[480px] sm:max-w-md">
          {selected && (
            <>
              <SheetHeader><SheetTitle>{selected.codigo} — {selected.disciplina}</SheetTitle></SheetHeader>
              <div className="mt-5 space-y-3 text-[13px]">
                <Field label="Turma" value={selected.turma} />
                <Field label="Professor" value={selected.prof} />
                <Field label="Sala" value={selected.sala} />
                <Field label="Horário" value={selected.horario} />
                <Field label="Vagas" value={selected.vagas} />
              </div>
              <div className="mt-6 flex gap-2">
                <Button variant="outline" className="flex-1" onClick={() => { setEditTurma(selected); setSelected(null); }}>Editar</Button>
                <Button variant="destructive" className="flex-1" onClick={() => { toast.success(`Turma ${selected.codigo} cancelada. Estudantes notificados.`); setSelected(null); }}>Cancelar turma</Button>
              </div>
              <ValidationCallout className="mt-4" tone="info">Cancelar gera notificação aos {parseInt(selected.vagas)} estudantes matriculados.</ValidationCallout>
            </>
          )}
        </SheetContent>
      </Sheet>

      <EditarTurmaDialog turma={editTurma} onClose={() => setEditTurma(null)} />
    </AppShell>
  );
}

// ─── Visão Professor (F05) ────────────────────────────────────────────────────

function ProfessorView() {
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const [subTab, setSubTab] = useState("overview");

  const { data: diarios = [] } = useQuery({
    queryKey: ["diarios", "professor", PROFESSOR_ID],
    queryFn: () => api.diarios.getByProfessor(PROFESSOR_ID),
  });

  const { data: diarioDetalhado } = useQuery({
    queryKey: ["diarios", String(selectedId)],
    queryFn: () => api.diarios.getDetalhado(selectedId!),
    enabled: selectedId !== null,
  });

  const diarioSel = diarios.find((d) => d.id === selectedId);

  if (!selectedId || !diarioSel) {
    return (
      <div className="space-y-5">
        <StatsRow stats={[
          { label: "Minhas turmas", value: diarios.length, tone: "info" },
          { label: "Estudantes", value: diarios.reduce((s, d) => s + d.estudantesCount, 0), tone: "info" },
          { label: "Fechadas", value: diarios.filter((d) => d.status === "FECHADO").length, tone: "success" },
          { label: "Em andamento", value: diarios.filter((d) => d.status !== "FECHADO").length, tone: "warning" },
        ]} />
        <DataTable
          columns={[
            { key: "codigo", header: "Código", render: (r) => `T${r.turmaId}` },
            { key: "disciplina", header: "Disciplina", render: (r) => `Turma ${r.turmaId}` },
            { key: "matriculados", header: "Alunos", align: "right", render: (r) => r.estudantesCount },
            { key: "aulas", header: "Aulas", render: (r) => `${r.aulasCount}/${AULAS_TOTAL_PADRAO}` },
            { key: "status", header: "Status", render: (r) => (
              <StatusBadge tone={r.status === "FECHADO" ? "success" : "warning"}>
                {r.status === "FECHADO" ? "Fechada" : "Em andamento"}
              </StatusBadge>
            )},
            { key: "acoes", header: "", align: "right", render: (r) => (
              <RowActionButton onClick={() => { setSelectedId(r.id); setSubTab("overview"); }}>Abrir diário</RowActionButton>
            )},
          ]}
          rows={diarios}
        />
      </div>
    );
  }

  return (
    <div className="space-y-5">
      <Button variant="ghost" size="sm" onClick={() => setSelectedId(null)}>
        <ArrowLeft className="mr-1 h-4 w-4" /> Minhas turmas
      </Button>
      <SectionTitle
        title={`T${diarioSel.turmaId} — Turma ${diarioSel.turmaId}`}
        subtitle={`${diarioSel.estudantesCount} estudantes · ${diarioSel.aulasCount}/${AULAS_TOTAL_PADRAO} aulas registradas`}
      />
      <TabsRow items={subTabsProf} value={subTab} onChange={setSubTab} />
      {subTab === "overview" && <OverviewProf diario={diarioDetalhado ?? null} />}
      {subTab === "aulas"    && <AulasProf diarioId={selectedId} diario={diarioDetalhado ?? null} />}
      {subTab === "freq"     && <FrequenciaProf diarioId={selectedId} diario={diarioDetalhado ?? null} />}
      {subTab === "aval"     && <AvaliacoesProf diarioId={selectedId} diario={diarioDetalhado ?? null} />}
      {subTab === "fechar"   && <FecharProf diarioId={selectedId} diario={diarioDetalhado ?? null} aulasTotal={AULAS_TOTAL_PADRAO} />}
    </div>
  );
}

// ── Visão geral ───────────────────────────────────────────────────────────────

function OverviewProf({ diario }: { diario: DiarioTurmaDetalhadoResumo | null }) {
  const aulasCount      = diario?.aulas.length ?? 0;
  const avaliacoesCount = diario?.avaliacoes.length ?? 0;
  const pesoTotal       = diario?.avaliacoes.reduce((s, a) => s + a.peso, 0) ?? 0;
  const totalAulas      = Math.max(diario?.aulas.length ?? 0, 1);
  const totalEstudantes = Math.max(diario?.estudantesAtivos.length ?? 0, 1);
  const totalPresencas  = diario?.frequencias.filter((f) => f.presente).length ?? 0;
  const freqMedia       = diario
    ? Math.round((totalPresencas / (totalAulas * totalEstudantes)) * 100)
    : 0;

  return (
    <div className="grid gap-4 lg:grid-cols-3">
      <div className="lg:col-span-2 rounded-xl border bg-card p-5 shadow-card">
        <SectionTitle title="Progresso do período" />
        <div className="mt-3 space-y-3">
          <ProgressRow label="Aulas ministradas" current={aulasCount} total={AULAS_TOTAL_PADRAO} unit="" tone="info" />
          <ProgressRow label="Frequência média da turma" current={freqMedia} total={100} unit="%" tone={freqMedia >= 75 ? "success" : "warning"} />
          <ProgressRow label="Notas lançadas" current={avaliacoesCount} total={Math.max(avaliacoesCount, 3)} unit="aval" tone={pesoTotal >= 100 ? "success" : "warning"} />
        </div>
      </div>
      <div className="rounded-xl border bg-card p-5 shadow-card">
        <SectionTitle title="Pendências" />
        <ul className="mt-3 space-y-2 text-[13px]">
          {diario && pesoTotal < 100 && (
            <li className="text-warning">• Peso total das avaliações: {pesoTotal}% (falta {100 - pesoTotal}%)</li>
          )}
          {diario && diario.estudantesAtivos.length === 0 && (
            <li className="text-warning">• Nenhum estudante adicionado ao diário</li>
          )}
          {diario && pesoTotal >= 100 && diario.estudantesAtivos.length > 0 && (
            <li className="text-muted-foreground">• Sem pendências críticas</li>
          )}
          {!diario && <li className="text-muted-foreground">• Carregando pendências...</li>}
        </ul>
      </div>
    </div>
  );
}

// ── Aulas ─────────────────────────────────────────────────────────────────────

type AulaRow = { id?: number; data: string; conteudo: string; presentes: number };

function AulasProf({ diarioId, diario }: { diarioId: number; diario: DiarioTurmaDetalhadoResumo | null }) {
  const queryClient = useQueryClient();
  const [dataAula, setDataAula] = useState("");
  const [conteudo, setConteudo] = useState("");
  const [editAula, setEditAula] = useState<AulaRow | null>(null);

  const aulas: AulaRow[] = (diario?.aulas ?? []).map((a) => ({
    id: a.id,
    data: a.data,
    conteudo: a.conteudo,
    presentes: diario?.frequencias.filter((f) => f.aulaId === a.id && f.presente).length ?? 0,
  }));

  const registrarMutation = useMutation({
    mutationFn: () => api.diarios.registrarAula(diarioId, { professorId: PROFESSOR_ID, data: dataAula, conteudo }),
    onSuccess: () => {
      toast.success("Aula registrada e enviada para conferência.");
      queryClient.invalidateQueries({ queryKey: ["diarios", String(diarioId)] });
      queryClient.invalidateQueries({ queryKey: ["diarios", "professor", PROFESSOR_ID] });
      setDataAula(""); setConteudo("");
    },
    onError: (e: Error) => toast.error(e.message),
  });

  const corrigirMutation = useMutation({
    mutationFn: ({ aulaId, novoConteudo }: { aulaId: number; novoConteudo: string }) =>
      api.diarios.corrigirAula(diarioId, aulaId, { professorId: PROFESSOR_ID, novoConteudo }),
    onSuccess: () => {
      toast.success("Aula atualizada.");
      queryClient.invalidateQueries({ queryKey: ["diarios", String(diarioId)] });
      setEditAula(null);
    },
    onError: (e: Error) => toast.error(e.message),
  });

  return (
    <div className="space-y-4">
      <div className="rounded-xl border bg-card p-5 shadow-card">
        <SectionTitle title="Registrar nova aula" />
        <div className="mt-3 grid grid-cols-3 gap-3">
          <FormField label="Data" required>
            <Input type="date" className="h-10" value={dataAula} onChange={(e) => setDataAula(e.target.value)} />
          </FormField>
          <FormField label="Conteúdo" required full>
            <Input className="h-10" placeholder="Ex.: Análise de complexidade O(n log n)" value={conteudo} onChange={(e) => setConteudo(e.target.value)} />
          </FormField>
        </div>
        <div className="mt-3 flex justify-end">
          <Button onClick={() => registrarMutation.mutate()} disabled={!dataAula || !conteudo || registrarMutation.isPending}>
            Registrar aula
          </Button>
        </div>
      </div>
      <DataTable
        columns={[
          { key: "data", header: "Data" },
          { key: "conteudo", header: "Conteúdo" },
          { key: "presentes", header: "Presentes", align: "right" },
          { key: "acoes", header: "", align: "right", render: (r: AulaRow) => (
            <RowActionButton onClick={() => setEditAula(r)}>Editar</RowActionButton>
          )},
        ]}
        rows={aulas}
      />
      <EditarAulaDialogProf
        aula={editAula}
        onClose={() => setEditAula(null)}
        onSave={(orig, novoConteudo) => {
          if (orig.id !== undefined) corrigirMutation.mutate({ aulaId: orig.id, novoConteudo });
          else { toast.error("Aula sem identificador — não foi possível editar."); setEditAula(null); }
        }}
      />
    </div>
  );
}

function EditarAulaDialogProf({ aula, onClose, onSave }: {
  aula: AulaRow | null;
  onClose: () => void;
  onSave: (orig: AulaRow, novoConteudo: string) => void;
}) {
  const [conteudo, setConteudo] = useState("");
  return (
    <Dialog open={!!aula} onOpenChange={(o) => { if (!o) onClose(); else if (aula) setConteudo(aula.conteudo); }}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Editar aula</DialogTitle>
          <DialogDescription>Corrija o conteúdo registrado para o diário.</DialogDescription>
        </DialogHeader>
        <FormField label="Conteúdo" full>
          <Textarea rows={3} value={conteudo} onChange={(e) => setConteudo(e.target.value)} />
        </FormField>
        <DialogFooter>
          <Button variant="outline" onClick={onClose}>Cancelar</Button>
          <Button onClick={() => aula && onSave(aula, conteudo)}>Salvar alterações</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}

// ── Frequência ────────────────────────────────────────────────────────────────

function FrequenciaProf({ diarioId, diario }: { diarioId: number; diario: DiarioTurmaDetalhadoResumo | null }) {
  const queryClient = useQueryClient();
  const aulas      = diario?.aulas ?? [];
  const estudantes = diario?.estudantesAtivos ?? [];
  const aulaAtual  = aulas[aulas.length - 1];

  const freqMutation = useMutation({
    mutationFn: ({ estudanteId, presente }: { estudanteId: number; presente: boolean }) => {
      if (!aulaAtual) return Promise.reject(new Error("Nenhuma aula registrada"));
      return api.diarios.registrarFrequencia(diarioId, {
        professorId: PROFESSOR_ID,
        aulaId: aulaAtual.id,
        estudanteId,
        presente,
      });
    },
    onSuccess: (_data, vars) => {
      const msg = vars.presente
        ? `Estudante ${vars.estudanteId} marcado(a) como presente.`
        : `Estudante ${vars.estudanteId} marcado(a) como falta.`;
      vars.presente ? toast.success(msg) : toast.error(msg);
      queryClient.invalidateQueries({ queryKey: ["diarios", String(diarioId)] });
    },
    onError: (e: Error) => toast.error(e.message),
  });

  const rows = estudantes.map((eId) => {
    const presencas = diario?.frequencias.filter((f) => f.estudanteId === eId && f.presente).length ?? 0;
    const pct = aulas.length > 0 ? Math.round((presencas / aulas.length) * 100) : 0;
    return { estudanteId: eId, nome: `Estudante ${eId}`, freq: `${pct}%` };
  });

  return (
    <div className="rounded-xl border bg-card p-5 shadow-card">
      <SectionTitle
        title={`Chamada — Aula de ${aulaAtual ? aulaAtual.data : "—"}`}
        subtitle="Marque presença ou falta para cada estudante."
      />
      <DataTable className="mt-3"
        columns={[
          { key: "nome", header: "Estudante" },
          { key: "freq", header: "% Frequência", align: "right" },
          { key: "marcar", header: "Marcar", align: "right", render: (r) => (
            <div className="flex justify-end gap-1.5">
              <RowActionButton onClick={() => freqMutation.mutate({ estudanteId: r.estudanteId, presente: true })}>Presente</RowActionButton>
              <RowActionButton tone="danger" onClick={() => freqMutation.mutate({ estudanteId: r.estudanteId, presente: false })}>Falta</RowActionButton>
            </div>
          )},
        ]}
        rows={rows}
      />
      <div className="mt-4 flex justify-end">
        <Button onClick={() => toast.success("Chamada salva com sucesso.")}>Salvar chamada</Button>
      </div>
    </div>
  );
}

// ── Avaliações & Notas ────────────────────────────────────────────────────────

type AvaliacaoRow = { id: number; nome: string; peso: string; prazo: string; status: string };

function AvaliacoesProf({ diarioId, diario }: { diarioId: number; diario: DiarioTurmaDetalhadoResumo | null }) {
  const [lancando, setLancando] = useState<AvaliacaoRow | null>(null);

  const avaliacoes: AvaliacaoRow[] = (diario?.avaliacoes ?? []).map((a) => {
    const temNota = diario?.resultados.some((r) => r.notas[String(a.id)] !== undefined) ?? false;
    return { id: a.id, nome: a.nome, peso: `${a.peso}%`, prazo: a.prazo, status: temNota ? "Notas lançadas" : "Pendente" };
  });

  const pesoTotal = diario?.avaliacoes.reduce((s, a) => s + a.peso, 0) ?? 0;

  return (
    <div className="space-y-4">
      <DataTable
        columns={[
          { key: "nome", header: "Avaliação" },
          { key: "peso", header: "Peso", align: "right" },
          { key: "prazo", header: "Prazo" },
          { key: "status", header: "Status", render: (r: AvaliacaoRow) => (
            <StatusBadge tone={r.status === "Notas lançadas" ? "success" : "warning"}>{r.status}</StatusBadge>
          )},
          { key: "acoes", header: "", align: "right", render: (r: AvaliacaoRow) => (
            <RowActionButton onClick={() => setLancando(r)}>
              {r.status === "Notas lançadas" ? "Revisar notas" : "Lançar notas"}
            </RowActionButton>
          )},
        ]}
        rows={avaliacoes}
      />
      <ValidationCallout tone="info">
        Soma dos pesos: {pesoTotal}% {pesoTotal === 100 ? "✓" : `(falta ${100 - pesoTotal}%)`}
      </ValidationCallout>
      <LancarNotasDialogProf
        aval={lancando}
        diarioId={diarioId}
        estudantesAtivos={diario?.estudantesAtivos ?? []}
        resultados={diario?.resultados ?? []}
        onClose={() => setLancando(null)}
      />
    </div>
  );
}

function LancarNotasDialogProf({ aval, diarioId, estudantesAtivos, resultados, onClose }: {
  aval: AvaliacaoRow | null;
  diarioId: number;
  estudantesAtivos: number[];
  resultados: ResultadoResumo[];
  onClose: () => void;
}) {
  const queryClient = useQueryClient();
  const [notas, setNotas] = useState<Record<string, string>>({});

  const lancarMutation = useMutation({
    mutationFn: async () => {
      if (!aval) return;
      await Promise.all(
        Object.entries(notas)
          .filter(([, v]) => v !== "")
          .map(([estudanteId, nota]) =>
            api.diarios.lancarNota(diarioId, Number(estudanteId), aval.id, Number(nota))
          )
      );
    },
    onSuccess: () => {
      const qtd = Object.values(notas).filter((v) => v !== "").length;
      toast.success(`Notas de ${aval?.nome} lançadas (${qtd}/${estudantesAtivos.length}).`);
      queryClient.invalidateQueries({ queryKey: ["diarios", String(diarioId)] });
      setNotas({}); onClose();
    },
    onError: (e: Error) => toast.error(e.message),
  });

  return (
    <Dialog open={!!aval} onOpenChange={(o) => { if (!o) { setNotas({}); onClose(); } }}>
      <DialogContent className="max-w-xl">
        {aval && (
          <>
            <DialogHeader>
              <DialogTitle>Lançar notas — {aval.nome}</DialogTitle>
              <DialogDescription>Peso {aval.peso} · Prazo {aval.prazo}</DialogDescription>
            </DialogHeader>
            <div className="space-y-2 max-h-[360px] overflow-y-auto pr-2">
              {estudantesAtivos.map((eId) => {
                const notaExistente = resultados.find((r) => r.estudanteId === eId)?.notas[String(aval.id)];
                return (
                  <div key={eId} className="flex items-center justify-between gap-3 border-b py-2">
                    <span className="text-[13px] text-foreground">Estudante {eId}</span>
                    <Input
                      type="number" step="0.1" min="0" max="10" className="h-9 w-24"
                      placeholder={notaExistente !== undefined ? String(notaExistente) : "0,0"}
                      value={notas[String(eId)] ?? ""}
                      onChange={(e) => setNotas({ ...notas, [String(eId)]: e.target.value })}
                    />
                  </div>
                );
              })}
            </div>
            <ValidationCallout tone="info">Notas de 0,0 a 10,0. Valores em branco serão considerados pendentes.</ValidationCallout>
            <DialogFooter>
              <Button variant="outline" onClick={() => { setNotas({}); onClose(); }}>Cancelar</Button>
              <Button onClick={() => lancarMutation.mutate()} disabled={lancarMutation.isPending}>Salvar notas</Button>
            </DialogFooter>
          </>
        )}
      </DialogContent>
    </Dialog>
  );
}

// ── Fechamento ────────────────────────────────────────────────────────────────

function FecharProf({ diarioId, diario, aulasTotal }: {
  diarioId: number;
  diario: DiarioTurmaDetalhadoResumo | null;
  aulasTotal: number;
}) {
  const queryClient = useQueryClient();
  const aulasCount = diario?.aulas.length ?? 0;
  const podeFechar = aulasCount >= aulasTotal;

  const fecharMutation = useMutation({
    mutationFn: async () => {
      for (const eId of diario?.estudantesAtivos ?? []) {
        await api.diarios.fecharResultado(diarioId, eId);
      }
    },
    onSuccess: () => {
      toast.success("Resultado final fechado e enviado à Secretaria.");
      queryClient.invalidateQueries({ queryKey: ["diarios", String(diarioId)] });
      queryClient.invalidateQueries({ queryKey: ["diarios", "professor", PROFESSOR_ID] });
    },
    onError: (e: Error) => toast.error(e.message),
  });

  const situacaoLabel = (s: string | null) => {
    if (!s) return "Pendente";
    if (s === "APROVADO") return "Aprovado";
    if (s === "RECUPERACAO") return "Recuperação";
    return "Reprovado";
  };

  const resultadosRows = (diario?.resultados ?? []).map((r) => {
    const avs = diario?.avaliacoes ?? [];
    const somaPonderada = avs.reduce((sum, av) => {
      const nota = r.notas[String(av.id)];
      return nota !== undefined ? sum + nota * av.peso : sum;
    }, 0);
    const somaPesos = avs.reduce((sum, av) => r.notas[String(av.id)] !== undefined ? sum + av.peso : sum, 0);
    const media = somaPesos > 0 ? Math.round((somaPonderada / somaPesos) * 10) / 10 : 0;
    const totalAulas = Math.max(diario?.aulas.length ?? 0, 1);
    const presencas  = diario?.frequencias.filter((f) => f.estudanteId === r.estudanteId && f.presente).length ?? 0;
    const freq = `${Math.round((presencas / totalAulas) * 100)}%`;
    return { nome: `Estudante ${r.estudanteId}`, media, freq, situacao: situacaoLabel(r.situacao) };
  });

  return (
    <div className="space-y-4">
      {!podeFechar && (
        <ValidationCallout tone="error">
          Fechamento bloqueado: ainda há {aulasTotal - aulasCount} aulas ou avaliações pendentes.
        </ValidationCallout>
      )}
      <DataTable
        columns={[
          { key: "nome", header: "Estudante" },
          { key: "media", header: "Média", align: "right" },
          { key: "freq", header: "Freq.", align: "right" },
          { key: "situacao", header: "Situação", render: (r) => (
            <StatusBadge tone={r.situacao === "Aprovado" ? "success" : r.situacao === "Recuperação" ? "warning" : "danger"}>
              {r.situacao}
            </StatusBadge>
          )},
        ]}
        rows={resultadosRows}
      />
      <div className="rounded-xl border bg-card p-4 shadow-card">
        <FormField label="Observação do fechamento" full>
          <Textarea rows={3} />
        </FormField>
        <div className="mt-3 flex justify-end gap-2">
          <Button variant="outline" onClick={() => toast.success("Rascunho do fechamento salvo.")}>Salvar rascunho</Button>
          <Button disabled={!podeFechar || fecharMutation.isPending} onClick={() => fecharMutation.mutate()}>
            <CheckCircle2 className="mr-2 h-4 w-4" /> Fechar resultado final
          </Button>
        </div>
      </div>
    </div>
  );
}

// ─── Componentes Coord / Secretaria (inalterados) ─────────────────────────────

function EditarTurmaDialog({ turma, onClose }: { turma: Turma | null; onClose: () => void }) {
  const [prof, setProf]     = useState("");
  const [sala, setSala]     = useState("");
  const [horario, setHorario] = useState("");
  const [vagas, setVagas]   = useState("");
  return (
    <Dialog open={!!turma} onOpenChange={(o) => { if (!o) onClose(); else if (turma) { setProf(turma.prof); setSala(turma.sala); setHorario(turma.horario); setVagas(turma.vagas); } }}>
      <DialogContent>
        {turma && (
          <>
            <DialogHeader>
              <DialogTitle>Editar turma {turma.codigo} — {turma.turma}</DialogTitle>
              <DialogDescription>{turma.disciplina}</DialogDescription>
            </DialogHeader>
            <div className="grid grid-cols-2 gap-3">
              <FormField label="Professor" full><Input className="h-10" value={prof} onChange={(e) => setProf(e.target.value)} /></FormField>
              <FormField label="Sala"><Input className="h-10" value={sala} onChange={(e) => setSala(e.target.value)} /></FormField>
              <FormField label="Horário"><Input className="h-10" value={horario} onChange={(e) => setHorario(e.target.value)} /></FormField>
              <FormField label="Vagas (ocupadas/total)" full><Input className="h-10" value={vagas} onChange={(e) => setVagas(e.target.value)} /></FormField>
            </div>
            <ValidationCallout tone="info">Alterações geram notificação aos estudantes matriculados.</ValidationCallout>
            <DialogFooter>
              <Button variant="outline" onClick={onClose}>Cancelar</Button>
              <Button onClick={() => { toast.success(`Turma ${turma.codigo} atualizada.`); onClose(); }}>Salvar alterações</Button>
            </DialogFooter>
          </>
        )}
      </DialogContent>
    </Dialog>
  );
}

type Sala = { cod: string; predio: string; cap: number; tipo: string; status: "Ativa" | "Inativa" };

function SalasTab() {
  const [salas, setSalas] = useState<Sala[]>([
    { cod: "B-203", predio: "Bloco B", cap: 40, tipo: "Sala teórica", status: "Ativa" },
    { cod: "Lab-IA", predio: "Bloco C", cap: 25, tipo: "Laboratório", status: "Ativa" },
    { cod: "B-401", predio: "Bloco B", cap: 60, tipo: "Auditório", status: "Inativa" },
  ]);
  const [edit, setEdit]   = useState<Sala | null>(null);
  const [cap, setCap]     = useState(0);
  const [tipo, setTipo]   = useState("");
  const [status, setStatus] = useState<"Ativa" | "Inativa">("Ativa");
  const salvar = () => {
    if (!edit) return;
    setSalas((p) => p.map((s) => s.cod === edit.cod ? { ...s, cap, tipo, status } : s));
    toast.success(`Sala ${edit.cod} atualizada.`); setEdit(null);
  };
  return (
    <>
      <DataTable
        columns={[
          { key: "cod", header: "Sala" }, { key: "predio", header: "Prédio" },
          { key: "cap", header: "Capacidade", align: "right" }, { key: "tipo", header: "Tipo" },
          { key: "status", header: "Status", render: (r) => <StatusBadge tone={r.status === "Ativa" ? "success" : "neutral"}>{r.status}</StatusBadge> },
          { key: "acoes", header: "", align: "right", render: (r) => <RowActionButton onClick={() => { setEdit(r); setCap(r.cap); setTipo(r.tipo); setStatus(r.status); }}>Editar</RowActionButton> },
        ]}
        rows={salas}
      />
      <Dialog open={!!edit} onOpenChange={(o) => !o && setEdit(null)}>
        <DialogContent>
          {edit && (
            <>
              <DialogHeader>
                <DialogTitle>Editar sala {edit.cod}</DialogTitle>
                <DialogDescription>{edit.predio}</DialogDescription>
              </DialogHeader>
              <div className="grid grid-cols-2 gap-3">
                <FormField label="Capacidade"><Input type="number" className="h-10" value={cap} onChange={(e) => setCap(Number(e.target.value))} /></FormField>
                <FormField label="Tipo"><Input className="h-10" value={tipo} onChange={(e) => setTipo(e.target.value)} /></FormField>
                <FormField label="Status" full>
                  <div className="flex gap-2">
                    <Button type="button" size="sm" variant={status === "Ativa" ? "default" : "outline"} onClick={() => setStatus("Ativa")}>Ativa</Button>
                    <Button type="button" size="sm" variant={status === "Inativa" ? "default" : "outline"} onClick={() => setStatus("Inativa")}>Inativa</Button>
                  </div>
                </FormField>
              </div>
              <DialogFooter>
                <Button variant="outline" onClick={() => setEdit(null)}>Cancelar</Button>
                <Button onClick={salvar}>Salvar</Button>
              </DialogFooter>
            </>
          )}
        </DialogContent>
      </Dialog>
    </>
  );
}

type Prof = { nome: string; depto: string; turmas: number; status: "Ativo" | "Inativo"; email?: string };

function ProfsTab() {
  const [profs, setProfs] = useState<Prof[]>([
    { nome: "Carlos Lima", depto: "Computação", turmas: 3, status: "Ativo", email: "carlos@univ.edu" },
    { nome: "Ana Souza",   depto: "Computação", turmas: 2, status: "Ativo", email: "ana@univ.edu" },
    { nome: "Lia Mendes",  depto: "IA",         turmas: 2, status: "Ativo", email: "lia@univ.edu" },
  ]);
  const [edit, setEdit]     = useState<Prof | null>(null);
  const [depto, setDepto]   = useState("");
  const [email, setEmail]   = useState("");
  const [status, setStatus] = useState<"Ativo" | "Inativo">("Ativo");
  const salvar = () => {
    if (!edit) return;
    setProfs((p) => p.map((x) => x.nome === edit.nome ? { ...x, depto, email, status } : x));
    toast.success(`Cadastro de ${edit.nome} atualizado.`); setEdit(null);
  };
  return (
    <>
      <DataTable
        columns={[
          { key: "nome", header: "Professor" }, { key: "depto", header: "Departamento" },
          { key: "turmas", header: "Turmas vinculadas", align: "right" },
          { key: "status", header: "Status", render: (r) => <StatusBadge tone={r.status === "Ativo" ? "success" : "neutral"}>{r.status}</StatusBadge> },
          { key: "acoes", header: "", align: "right", render: (r) => <RowActionButton onClick={() => { setEdit(r); setDepto(r.depto); setEmail(r.email ?? ""); setStatus(r.status); }}>Editar</RowActionButton> },
        ]}
        rows={profs}
      />
      <Dialog open={!!edit} onOpenChange={(o) => !o && setEdit(null)}>
        <DialogContent>
          {edit && (
            <>
              <DialogHeader>
                <DialogTitle>Editar professor</DialogTitle>
                <DialogDescription>{edit.nome} · {edit.turmas} turmas vinculadas</DialogDescription>
              </DialogHeader>
              <div className="grid grid-cols-2 gap-3">
                <FormField label="Departamento" full><Input className="h-10" value={depto} onChange={(e) => setDepto(e.target.value)} /></FormField>
                <FormField label="E-mail institucional" full><Input className="h-10" type="email" value={email} onChange={(e) => setEmail(e.target.value)} /></FormField>
                <FormField label="Status" full>
                  <div className="flex gap-2">
                    <Button type="button" size="sm" variant={status === "Ativo" ? "default" : "outline"} onClick={() => setStatus("Ativo")}>Ativo</Button>
                    <Button type="button" size="sm" variant={status === "Inativo" ? "default" : "outline"} onClick={() => setStatus("Inativo")}>Inativo</Button>
                  </div>
                </FormField>
              </div>
              <DialogFooter>
                <Button variant="outline" onClick={() => setEdit(null)}>Cancelar</Button>
                <Button onClick={salvar}>Salvar</Button>
              </DialogFooter>
            </>
          )}
        </DialogContent>
      </Dialog>
    </>
  );
}

function Field({ label, value }: { label: string; value: string }) {
  return (
    <div className="flex justify-between border-b border-border py-2">
      <span className="text-muted-foreground">{label}</span>
      <span className="font-medium text-foreground">{value}</span>
    </div>
  );
}

const wizSteps = [
  { key: "disc", label: "Disciplina" },
  { key: "prof", label: "Professor & Sala" },
  { key: "conf", label: "Confirmar" },
];

function NovaTurmaWizard({ step, onStep, onCancel }: { step: 0 | 1 | 2; onStep: (s: 0 | 1 | 2) => void; onCancel: () => void }) {
  return (
    <div className="space-y-5">
      <Button variant="ghost" size="sm" onClick={onCancel}><ArrowLeft className="mr-1 h-4 w-4" /> Cancelar</Button>
      <Stepper steps={wizSteps} current={step} />
      <div className="rounded-xl border bg-card p-6 shadow-card">
        {step === 0 && (
          <>
            <SectionTitle title="Selecionar disciplina" subtitle="Apenas disciplinas da matriz ativa." />
            <div className="mt-4 grid grid-cols-2 gap-4">
              <FormField label="Disciplina" required><Input className="h-10" placeholder="ES401 — Arquitetura de Software" /></FormField>
              <FormField label="Turma"><Input className="h-10" defaultValue="T01" /></FormField>
            </div>
          </>
        )}
        {step === 1 && (
          <>
            <SectionTitle title="Professor, sala e horário" />
            <div className="mt-4 grid grid-cols-2 gap-4">
              <FormField label="Professor" required><Input className="h-10" placeholder="Carlos Lima" /></FormField>
              <FormField label="Sala" required><Input className="h-10" placeholder="B-203" /></FormField>
              <FormField label="Horário" required full><Input className="h-10" placeholder="Seg/Qua 08:00-10:00" /></FormField>
              <FormField label="Capacidade" required><Input type="number" className="h-10" defaultValue={30} /></FormField>
            </div>
            <ValidationCallout className="mt-4" tone="info">Sem conflito de horário detectado para professor e sala.</ValidationCallout>
          </>
        )}
        {step === 2 && (
          <>
            <SectionTitle title="Revisão" />
            <p className="mt-3 text-[13px] text-muted-foreground">Confirme a oferta da turma. Após confirmar, ela ficará visível na matrícula.</p>
          </>
        )}
        <div className="mt-4 flex justify-end gap-2">
          {step > 0 && <Button variant="outline" onClick={() => onStep((step - 1) as 0 | 1)}>Voltar</Button>}
          {step < 2 && <Button onClick={() => onStep((step + 1) as 1 | 2)}>Avançar</Button>}
          {step === 2 && <Button onClick={onCancel}>Confirmar oferta</Button>}
        </div>
      </div>
    </div>
  );
}
