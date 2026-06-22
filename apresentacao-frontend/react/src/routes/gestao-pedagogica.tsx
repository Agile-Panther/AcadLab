import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { createFileRoute } from "@tanstack/react-router";
import { toast } from "sonner";
import {
  AppShell, SectionTitle, StatsRow, DataTable, StatusBadge, RowActionButton,
  ProgressRow, ValidationCallout, FormField, TabsRow, useProfileSwitcher,
} from "@/components/acadlab";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import {
  Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogFooter,
} from "@/components/ui/dialog";
import { ArrowLeft, CheckCircle2 } from "lucide-react";
import { api, type DiarioTurmaDetalhadoResumo, type ResultadoResumo } from "@/lib/api";

export const Route = createFileRoute("/gestao-pedagogica")({
  head: () => ({ meta: [{ title: "Gestão Pedagógica — AcadLab" }] }),
  component: Page,
});

// Identificador do professor logado (sem autenticação, usa valor fixo)
const PROFESSOR_ID = 1;

type Turma = {
  id: string; codigo: string; disciplina: string; matriculados: number;
  aulasDadas: number; aulasTotal: number; notasFechadas: boolean;
};

type TurmaCurso = Turma & { prof: string; freq: number; risco: number };

const AULAS_TOTAL_PADRAO = 30;

const subTabs = [
  { value: "overview", label: "Visão geral" },
  { value: "aulas", label: "Aulas" },
  { value: "freq", label: "Frequência" },
  { value: "aval", label: "Avaliações & Notas" },
  { value: "fechar", label: "Fechamento" },
];

function Page() {
  const [selected, setSelected] = useState<string | null>(null);
  const [tab, setTab] = useState("overview");
  const [acompanhar, setAcompanhar] = useState<TurmaCurso | null>(null);

  const { active: perfil } = useProfileSwitcher([
    { value: "professor", label: "Professor", description: "Lança aulas, frequência e notas" },
    { value: "coordenador", label: "Coordenador", description: "Acompanha turmas do curso" },
  ]);
  const isCoord = perfil === "coordenador";
  const subtitle = isCoord
    ? "Coordenação · Acompanhamento das turmas — 2025.2"
    : "Professor: Carlos Lima — 2025.2";

  const { data: diariosProf = [] } = useQuery({
    queryKey: ["diarios", "professor", PROFESSOR_ID],
    queryFn: () => api.diarios.getByProfessor(PROFESSOR_ID),
    enabled: !isCoord,
  });

  const { data: todosDiarios = [] } = useQuery({
    queryKey: ["diarios", "todos"],
    queryFn: () => api.diarios.getTodos(),
    enabled: isCoord,
  });

  const { data: diarioDetalhado } = useQuery({
    queryKey: ["diarios", selected],
    queryFn: () => api.diarios.getDetalhado(Number(selected!)),
    enabled: selected !== null,
  });

  const turmas: Turma[] = diariosProf.map((d) => ({
    id: String(d.id),
    codigo: `T${d.turmaId}`,
    disciplina: `Turma ${d.turmaId}`,
    matriculados: d.estudantesCount,
    aulasDadas: d.aulasCount,
    aulasTotal: AULAS_TOTAL_PADRAO,
    notasFechadas: d.status === "FECHADO",
  }));

  const turmasCurso: TurmaCurso[] = todosDiarios.map((d) => ({
    id: String(d.id),
    codigo: `T${d.turmaId}`,
    disciplina: `Turma ${d.turmaId}`,
    prof: `Prof. ${d.professorResponsavelId}`,
    matriculados: d.estudantesCount,
    aulasDadas: d.aulasCount,
    aulasTotal: AULAS_TOTAL_PADRAO,
    freq: 0,
    risco: 0,
    notasFechadas: d.status === "FECHADO",
  }));

  const turma = turmas.find((t) => t.id === selected);

  return (
    <AppShell title="Gestão Pedagógica" subtitle={subtitle}>
      {isCoord ? (
        <div className="space-y-5">
          <StatsRow stats={[
            { label: "Turmas do curso", value: turmasCurso.length, tone: "info" },
            { label: "Estudantes", value: turmasCurso.reduce((s, t) => s + t.matriculados, 0), tone: "info" },
            { label: "Em risco", value: turmasCurso.reduce((s, t) => s + t.risco, 0), tone: "danger" },
            { label: "Diários fechados", value: turmasCurso.filter((t) => t.notasFechadas).length, tone: "success" },
          ]} />
          <ValidationCallout tone="info">Visão somente-leitura. Coordenação não lança notas nem frequência — apenas monitora.</ValidationCallout>
          <DataTable
            columns={[
              { key: "codigo", header: "Código" },
              { key: "disciplina", header: "Disciplina" },
              { key: "prof", header: "Professor" },
              { key: "matriculados", header: "Alunos", align: "right" },
              { key: "freq", header: "Freq. média", align: "right", render: (r) => `${r.freq}%` },
              { key: "risco", header: "Em risco", align: "right", render: (r) => (
                <StatusBadge tone={r.risco >= 5 ? "danger" : r.risco >= 3 ? "warning" : "success"}>{r.risco}</StatusBadge>
              )},
              { key: "status", header: "Diário", render: (r) => (
                <StatusBadge tone={r.notasFechadas ? "success" : "warning"}>{r.notasFechadas ? "Fechado" : "Aberto"}</StatusBadge>
              )},
              { key: "acoes", header: "", align: "right", render: (r) => <RowActionButton onClick={() => setAcompanhar(r)}>Acompanhar</RowActionButton> },
            ]}
            rows={turmasCurso}
          />
          <AcompanharDialog turma={acompanhar} onClose={() => setAcompanhar(null)} />
        </div>
      ) : !turma ? (
        <div className="space-y-5">
          <StatsRow stats={[
            { label: "Minhas turmas", value: turmas.length, tone: "info" },
            { label: "Estudantes", value: turmas.reduce((s, t) => s + t.matriculados, 0), tone: "info" },
            { label: "Fechadas", value: turmas.filter((t) => t.notasFechadas).length, tone: "success" },
            { label: "Em andamento", value: turmas.filter((t) => !t.notasFechadas).length, tone: "warning" },
          ]} />
          <DataTable
            columns={[
              { key: "codigo", header: "Código" }, { key: "disciplina", header: "Disciplina" },
              { key: "matriculados", header: "Alunos", align: "right" },
              { key: "aulas", header: "Aulas", render: (r) => `${r.aulasDadas}/${r.aulasTotal}` },
              { key: "status", header: "Status", render: (r) => (
                <StatusBadge tone={r.notasFechadas ? "success" : "warning"}>{r.notasFechadas ? "Fechada" : "Em andamento"}</StatusBadge>
              )},
              { key: "acoes", header: "", align: "right", render: (r) => <RowActionButton onClick={() => { setSelected(r.id); setTab("overview"); }}>Abrir diário</RowActionButton> },
            ]}
            rows={turmas}
          />
        </div>
      ) : (
        <div className="space-y-5">
          <Button variant="ghost" size="sm" onClick={() => setSelected(null)}><ArrowLeft className="mr-1 h-4 w-4" /> Minhas turmas</Button>
          <SectionTitle title={`${turma.codigo} — ${turma.disciplina}`} subtitle={`${turma.matriculados} estudantes · ${turma.aulasDadas}/${turma.aulasTotal} aulas registradas`} />
          <TabsRow items={subTabs} value={tab} onChange={setTab} />
          {tab === "overview" && <OverviewTurma turma={turma} diario={diarioDetalhado ?? null} />}
          {tab === "aulas" && <Aulas diarioId={Number(selected)} diario={diarioDetalhado ?? null} />}
          {tab === "freq" && <Frequencia diarioId={Number(selected)} diario={diarioDetalhado ?? null} />}
          {tab === "aval" && <Avaliacoes diarioId={Number(selected)} diario={diarioDetalhado ?? null} />}
          {tab === "fechar" && <Fechar turma={turma} diarioId={Number(selected)} diario={diarioDetalhado ?? null} />}
        </div>
      )}
    </AppShell>
  );
}

function AcompanharDialog({ turma, onClose }: { turma: TurmaCurso | null; onClose: () => void }) {
  return (
    <Dialog open={!!turma} onOpenChange={(o) => !o && onClose()}>
      <DialogContent className="max-w-2xl">
        {turma && (
          <>
            <DialogHeader>
              <DialogTitle>{turma.codigo} — {turma.disciplina}</DialogTitle>
              <DialogDescription>Professor: {turma.prof} · {turma.matriculados} estudantes matriculados</DialogDescription>
            </DialogHeader>
            <div className="space-y-3 py-2">
              <ProgressRow label="Aulas ministradas" current={turma.aulasDadas} total={turma.aulasTotal} unit="" tone="info" />
              <ProgressRow label="Frequência média" current={turma.freq} total={100} unit="%" tone={turma.freq >= 80 ? "success" : "warning"} />
              <ProgressRow label="Estudantes em risco" current={turma.risco} total={turma.matriculados} unit="" tone={turma.risco >= 5 ? "danger" : "warning"} />
            </div>
            <SectionTitle title="Estudantes em alerta" />
            <DataTable
              columns={[
                { key: "nome", header: "Estudante" },
                { key: "freq", header: "Freq.", align: "right" },
                { key: "media", header: "Média parcial", align: "right" },
                { key: "alerta", header: "Alerta", render: (r) => <StatusBadge tone="danger">{r.alerta}</StatusBadge> },
              ]}
              rows={[
                { nome: "João Silva", freq: "62%", media: 4.2, alerta: "Frequência baixa" },
                { nome: "Lucas Pereira", freq: "70%", media: 5.1, alerta: "Risco reprovação" },
              ].slice(0, Math.max(1, Math.min(turma.risco, 3)))}
            />
            <DialogFooter>
              <Button variant="outline" onClick={onClose}>Fechar</Button>
              <Button onClick={() => { toast.success(`Mensagem enviada ao professor ${turma.prof}.`); onClose(); }}>Falar com o professor</Button>
            </DialogFooter>
          </>
        )}
      </DialogContent>
    </Dialog>
  );
}

function OverviewTurma({ turma, diario }: { turma: Turma; diario: DiarioTurmaDetalhadoResumo | null }) {
  const aulasCount = diario?.aulas.length ?? turma.aulasDadas;
  const avaliacoesCount = diario?.avaliacoes.length ?? 0;
  const pesoTotal = diario?.avaliacoes.reduce((s, a) => s + a.peso, 0) ?? 0;

  const totalAulas = diario?.aulas.length ?? 1;
  const totalEstudantes = diario?.estudantesAtivos.length ?? 1;
  const totalPresencas = diario?.frequencias.filter((f) => f.presente).length ?? 0;
  const freqMedia = totalAulas > 0 && totalEstudantes > 0
    ? Math.round((totalPresencas / (totalAulas * totalEstudantes)) * 100)
    : 0;

  return (
    <div className="grid gap-4 lg:grid-cols-3">
      <div className="lg:col-span-2 rounded-xl border bg-card p-5 shadow-card">
        <SectionTitle title="Progresso do período" />
        <div className="mt-3 space-y-3">
          <ProgressRow label="Aulas ministradas" current={aulasCount} total={turma.aulasTotal} unit="" tone="info" />
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

type AulaRow = { id?: number; data: string; conteudo: string; presentes: number };

function Aulas({ diarioId, diario }: { diarioId: number; diario: DiarioTurmaDetalhadoResumo | null }) {
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
    mutationFn: () => api.diarios.registrarAula(diarioId, {
      professorId: PROFESSOR_ID,
      data: dataAula,
      conteudo,
    }),
    onSuccess: () => {
      toast.success("Aula registrada e enviada para conferência.");
      queryClient.invalidateQueries({ queryKey: ["diarios", String(diarioId)] });
      queryClient.invalidateQueries({ queryKey: ["diarios", "professor", PROFESSOR_ID] });
      setDataAula("");
      setConteudo("");
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
          <Button
            onClick={() => registrarMutation.mutate()}
            disabled={!dataAula || !conteudo || registrarMutation.isPending}
          >
            Registrar aula
          </Button>
        </div>
      </div>
      <DataTable
        columns={[
          { key: "data", header: "Data" }, { key: "conteudo", header: "Conteúdo" },
          { key: "presentes", header: "Presentes", align: "right" },
          { key: "acoes", header: "", align: "right", render: (r) => <RowActionButton onClick={() => setEditAula(r)}>Editar</RowActionButton> },
        ]}
        rows={aulas}
      />
      <EditarAulaDialog
        aula={editAula}
        onClose={() => setEditAula(null)}
        onSave={(orig, novoConteudo) => {
          if (orig.id !== undefined) {
            corrigirMutation.mutate({ aulaId: orig.id, novoConteudo });
          } else {
            toast.error("Aula sem identificador — não foi possível editar.");
            setEditAula(null);
          }
        }}
      />
    </div>
  );
}

function EditarAulaDialog({
  aula, onClose, onSave,
}: {
  aula: AulaRow | null;
  onClose: () => void;
  onSave: (orig: AulaRow, novoConteudo: string) => void;
}) {
  const [data, setData] = useState("");
  const [conteudo, setConteudo] = useState("");
  const [presentes, setPresentes] = useState(0);
  return (
    <Dialog open={!!aula} onOpenChange={(o) => { if (!o) onClose(); else if (aula) { setData(aula.data); setConteudo(aula.conteudo); setPresentes(aula.presentes); } }}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Editar aula</DialogTitle>
          <DialogDescription>Ajuste data, conteúdo e número de presentes.</DialogDescription>
        </DialogHeader>
        <div className="grid grid-cols-2 gap-3">
          <FormField label="Data"><Input className="h-10" value={data} onChange={(e) => setData(e.target.value)} /></FormField>
          <FormField label="Presentes"><Input type="number" className="h-10" value={presentes} onChange={(e) => setPresentes(Number(e.target.value))} /></FormField>
          <FormField label="Conteúdo" full><Textarea rows={3} value={conteudo} onChange={(e) => setConteudo(e.target.value)} /></FormField>
        </div>
        <DialogFooter>
          <Button variant="outline" onClick={onClose}>Cancelar</Button>
          <Button onClick={() => aula && onSave(aula, conteudo)}>Salvar alterações</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}

function Frequencia({ diarioId, diario }: { diarioId: number; diario: DiarioTurmaDetalhadoResumo | null }) {
  const queryClient = useQueryClient();
  const aulas = diario?.aulas ?? [];
  const estudantes = diario?.estudantesAtivos ?? [];
  const aulaAtual = aulas[aulas.length - 1];

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
      if (vars.presente) toast.success(msg); else toast.error(msg);
      queryClient.invalidateQueries({ queryKey: ["diarios", String(diarioId)] });
    },
    onError: (e: Error) => toast.error(e.message),
  });

  const aulaLabel = aulaAtual ? aulaAtual.data : "—";

  const rows = estudantes.map((eId) => {
    const totalAulas = aulas.length;
    const presencas = diario?.frequencias.filter((f) => f.estudanteId === eId && f.presente).length ?? 0;
    const pct = totalAulas > 0 ? Math.round((presencas / totalAulas) * 100) : 0;
    return { estudanteId: eId, nome: `Estudante ${eId}`, freq: `${pct}%` };
  });

  return (
    <div className="rounded-xl border bg-card p-5 shadow-card">
      <SectionTitle title={`Chamada — Aula de ${aulaLabel}`} subtitle="Marque presença ou falta para cada estudante." />
      <DataTable className="mt-3"
        columns={[
          { key: "nome", header: "Estudante" }, { key: "freq", header: "% Frequência", align: "right" },
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

type AvaliacaoRow = { id: number; nome: string; peso: string; prazo: string; status: string };

function Avaliacoes({ diarioId, diario }: { diarioId: number; diario: DiarioTurmaDetalhadoResumo | null }) {
  const [lancando, setLancando] = useState<AvaliacaoRow | null>(null);

  const avaliacoes: AvaliacaoRow[] = (diario?.avaliacoes ?? []).map((a) => {
    const temNota = diario?.resultados.some((r) => r.notas[String(a.id)] !== undefined) ?? false;
    return {
      id: a.id,
      nome: a.nome,
      peso: `${a.peso}%`,
      prazo: a.prazo,
      status: temNota ? "Notas lançadas" : "Pendente",
    };
  });

  const pesoTotal = diario?.avaliacoes.reduce((s, a) => s + a.peso, 0) ?? 0;

  return (
    <div className="space-y-4">
      <DataTable
        columns={[
          { key: "nome", header: "Avaliação" }, { key: "peso", header: "Peso", align: "right" },
          { key: "prazo", header: "Prazo" },
          { key: "status", header: "Status", render: (r) => <StatusBadge tone={r.status === "Notas lançadas" ? "success" : "warning"}>{r.status}</StatusBadge> },
          { key: "acoes", header: "", align: "right", render: (r) => (
            <RowActionButton onClick={() => setLancando(r)}>
              {r.status === "Notas lançadas" ? "Revisar notas" : "Lançar notas"}
            </RowActionButton>
          )},
        ]}
        rows={avaliacoes}
      />
      <ValidationCallout tone="info">Soma dos pesos: {pesoTotal}% {pesoTotal === 100 ? "✓" : `(falta ${100 - pesoTotal}%)`}</ValidationCallout>
      <LancarNotasDialog
        aval={lancando}
        diarioId={diarioId}
        estudantesAtivos={diario?.estudantesAtivos ?? []}
        resultados={diario?.resultados ?? []}
        onClose={() => setLancando(null)}
      />
    </div>
  );
}

function LancarNotasDialog({
  aval, diarioId, estudantesAtivos, resultados, onClose,
}: {
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
      const promises = Object.entries(notas)
        .filter(([, v]) => v !== "")
        .map(([estudanteId, nota]) =>
          api.diarios.lancarNota(diarioId, Number(estudanteId), aval.id, Number(nota))
        );
      await Promise.all(promises);
    },
    onSuccess: () => {
      const qtd = Object.values(notas).filter((v) => v !== "").length;
      toast.success(`Notas de ${aval?.nome} lançadas (${qtd}/${estudantesAtivos.length}).`);
      queryClient.invalidateQueries({ queryKey: ["diarios", String(diarioId)] });
      setNotas({});
      onClose();
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
                      type="number" step="0.1" min="0" max="10"
                      className="h-9 w-24"
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

function situacaoLabel(s: string | null): string {
  if (!s) return "Pendente";
  if (s === "APROVADO") return "Aprovado";
  if (s === "RECUPERACAO") return "Recuperação";
  return "Reprovado";
}

function Fechar({ turma, diarioId, diario }: {
  turma: Turma;
  diarioId: number;
  diario: DiarioTurmaDetalhadoResumo | null;
}) {
  const queryClient = useQueryClient();
  const podeFechar = turma.aulasDadas === turma.aulasTotal;

  const fecharMutation = useMutation({
    mutationFn: async () => {
      const estudantes = diario?.estudantesAtivos ?? [];
      for (const eId of estudantes) {
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

  const resultadosRows = (diario?.resultados ?? []).map((r) => {
    const avaliacoes = diario?.avaliacoes ?? [];
    const somaPonderada = avaliacoes.reduce((sum, av) => {
      const nota = r.notas[String(av.id)];
      return nota !== undefined ? sum + nota * av.peso : sum;
    }, 0);
    const somaPesos = avaliacoes.reduce((sum, av) => {
      const nota = r.notas[String(av.id)];
      return nota !== undefined ? sum + av.peso : sum;
    }, 0);
    const media = somaPesos > 0 ? Math.round((somaPonderada / somaPesos) * 10) / 10 : 0;

    const totalAulas = diario?.aulas.length ?? 1;
    const presencas = diario?.frequencias.filter((f) => f.estudanteId === r.estudanteId && f.presente).length ?? 0;
    const freq = totalAulas > 0 ? `${Math.round((presencas / totalAulas) * 100)}%` : "0%";

    return { nome: `Estudante ${r.estudanteId}`, media, freq, situacao: situacaoLabel(r.situacao) };
  });

  return (
    <div className="space-y-4">
      {!podeFechar && <ValidationCallout tone="error">Fechamento bloqueado: ainda há {turma.aulasTotal - turma.aulasDadas} aulas ou avaliações pendentes.</ValidationCallout>}
      <DataTable
        columns={[
          { key: "nome", header: "Estudante" },
          { key: "media", header: "Média", align: "right" },
          { key: "freq", header: "Freq.", align: "right" },
          { key: "situacao", header: "Situação", render: (r) => (
            <StatusBadge tone={r.situacao === "Aprovado" ? "success" : r.situacao === "Recuperação" ? "warning" : "danger"}>{r.situacao}</StatusBadge>
          )},
        ]}
        rows={resultadosRows}
      />
      <div className="rounded-xl border bg-card p-4 shadow-card">
        <FormField label="Observação do fechamento" full><Textarea rows={3} /></FormField>
        <div className="mt-3 flex justify-end gap-2">
          <Button variant="outline" onClick={() => toast.success("Rascunho do fechamento salvo.")}>Salvar rascunho</Button>
          <Button
            disabled={!podeFechar || fecharMutation.isPending}
            onClick={() => fecharMutation.mutate()}
          >
            <CheckCircle2 className="mr-2 h-4 w-4" /> Fechar resultado final
          </Button>
        </div>
      </div>
    </div>
  );
}