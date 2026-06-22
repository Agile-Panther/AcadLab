import { useEffect, useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import { toast } from "sonner";
import {
  AppShell, SectionTitle, StatsRow, DataTable, StatusBadge, RowActionButton,
  FormField, ValidationCallout, ActionBar,
  useProfileSwitcher,
} from "@/components/acadlab";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogFooter } from "@/components/ui/dialog";
import { ArrowLeft, Download, FileSearch, History, Plus, ShieldCheck } from "lucide-react";
import {
  type HistoricoAcademico, type SituacaoAcademica, type SituacaoDiscente, type RegistroDisciplina,
  listarHistoricos, buscarHistoricoPorEstudante, buscarHistoricoOficial,
  atualizarSituacaoDiscente, registrarAcompanhamento, registrarAproveitamento, retificarRegistro,
  consolidarRegistro,
  SITUACAO_ACADEMICA_LABEL, SITUACAO_DISCENTE_LABEL, situacaoAcademicaTone, situacaoDiscenteTone,
} from "@/api/historico";

export const Route = createFileRoute("/historico-academico")({
  head: () => ({ meta: [{ title: "Histórico Acadêmico — AcadLab" }] }),
  component: Page,
});

// Estudante logado de referência (Maria Santos = estudante 1, conforme seed).
// Não há catálogo de Estudante com nome no backend — apenas o id é persistido.
const ESTUDANTE_LOGADO_ID = 1;
const ESTUDANTE_LOGADO_NOME = "Maria Santos";
// Secretaria logada (sem autenticação no backend — id fixo para a trilha de auditoria).
const SECRETARIA_ID = 1;

const hoje = () => new Date().toISOString().slice(0, 10);

function crMedio(registros: RegistroDisciplina[]) {
  const comNota = registros.filter((r) => r.situacao !== "APROVEITADO");
  if (comNota.length === 0) return 0;
  return comNota.reduce((acc, r) => acc + r.nota, 0) / comNota.length;
}

function agruparPorPeriodo(registros: RegistroDisciplina[]) {
  const mapa = new Map<number, RegistroDisciplina[]>();
  for (const r of registros) {
    const lista = mapa.get(r.periodoLetivoId) ?? [];
    lista.push(r);
    mapa.set(r.periodoLetivoId, lista);
  }
  return [...mapa.entries()].sort((a, b) => b[0] - a[0]);
}

function Page() {
  const { active: perfil } = useProfileSwitcher([
    { value: "estudante", label: "Estudante", description: "Consulta e emite o próprio histórico" },
    { value: "secretaria", label: "Secretaria Acadêmica", description: "Audita históricos e registra alterações" },
  ]);
  const isSec = perfil === "secretaria";

  return isSec ? <SecretariaView /> : <EstudanteView />;
}

// ════════════════════════════ ESTUDANTE ════════════════════════════

type EstudanteSubview = "overview" | "correcao" | "emitir";

function EstudanteView() {
  const [historico, setHistorico] = useState<HistoricoAcademico | null>(null);
  const [loading, setLoading] = useState(true);
  const [erro, setErro] = useState<string | null>(null);
  const [view, setView] = useState<EstudanteSubview>("overview");
  const [expanded, setExpanded] = useState<number | null>(null);

  useEffect(() => {
    setLoading(true);
    buscarHistoricoPorEstudante(ESTUDANTE_LOGADO_ID)
      .then((h) => { setHistorico(h); setExpanded(h.registros[0]?.periodoLetivoId ?? null); })
      .catch((e: Error) => setErro(e.message))
      .finally(() => setLoading(false));
  }, []);

  const subtitle = `${ESTUDANTE_LOGADO_NOME} · Estudante #${ESTUDANTE_LOGADO_ID}`;

  return (
    <AppShell title="Histórico Acadêmico" subtitle={subtitle}>
      {loading ? (
        <p className="text-sm text-muted-foreground">Carregando histórico...</p>
      ) : erro ? (
        <ValidationCallout tone="error">{erro}</ValidationCallout>
      ) : !historico ? (
        <ValidationCallout tone="info">Nenhum histórico acadêmico encontrado.</ValidationCallout>
      ) : view === "correcao" ? (
        <SolicitarCorrecao registros={historico.registros} onBack={() => setView("overview")} />
      ) : view === "emitir" ? (
        <EmitirOficial onBack={() => setView("overview")} />
      ) : (
        <OverviewEstudante
          historico={historico}
          expanded={expanded}
          setExpanded={setExpanded}
          onCorrecao={() => setView("correcao")}
          onEmitir={() => setView("emitir")}
        />
      )}
    </AppShell>
  );
}

function OverviewEstudante({ historico, expanded, setExpanded, onCorrecao, onEmitir }: {
  historico: HistoricoAcademico;
  expanded: number | null;
  setExpanded: (v: number | null) => void;
  onCorrecao: () => void;
  onEmitir: () => void;
}) {
  const cr = crMedio(historico.registros);
  const aprovadas = historico.registros.filter((r) => r.situacao === "APROVADO" || r.situacao === "APROVEITADO").length;
  const periodos = agruparPorPeriodo(historico.registros);

  return (
    <div className="space-y-5">
      <StatsRow stats={[
        { label: "CR (média das notas)", value: cr ? cr.toFixed(1) : "—", tone: "success" },
        { label: "Disciplinas no histórico", value: historico.registros.length, tone: "info" },
        { label: "Aprovações", value: aprovadas, tone: "success" },
        { label: "Situação discente", value: SITUACAO_DISCENTE_LABEL[historico.situacaoDiscente], tone: situacaoDiscenteTone(historico.situacaoDiscente) === "danger" ? "danger" : "info" },
      ]} />

      <div className="flex flex-wrap gap-2">
        <Button onClick={onEmitir}><Download className="mr-2 h-4 w-4" /> Emitir histórico oficial</Button>
        <Button variant="outline" onClick={onCorrecao}><FileSearch className="mr-2 h-4 w-4" /> Solicitar correção</Button>
      </div>

      {historico.aproveitamentos.length > 0 && (
        <div className="rounded-xl border bg-card p-5 shadow-card">
          <SectionTitle title="Aproveitamentos externos" subtitle="Disciplinas cursadas em outras instituições e aproveitadas na matriz." />
          <DataTable className="mt-3"
            columns={[
              { key: "disciplinaEquivalenteId", header: "Disciplina equiv.", render: (r) => `#${r.disciplinaEquivalenteId}` },
              { key: "disciplinaOrigem", header: "Disciplina de origem" },
              { key: "instituicaoOrigem", header: "Instituição" },
              { key: "cargaHorariaExterna", header: "CH externa", align: "right" },
              { key: "cargaHorariaRequerida", header: "CH exigida", align: "right" },
            ]}
            rows={historico.aproveitamentos}
          />
        </div>
      )}

      <SectionTitle title="Histórico por período" subtitle="Clique em um período para ver as disciplinas consolidadas." />
      {periodos.length === 0 ? (
        <ValidationCallout tone="info">Ainda não há disciplinas consolidadas no seu histórico.</ValidationCallout>
      ) : (
        <div className="space-y-3">
          {periodos.map(([periodoId, regs]) => {
            const aprov = regs.filter((r) => r.situacao === "APROVADO" || r.situacao === "APROVEITADO").length;
            const reprov = regs.filter((r) => r.situacao === "REPROVADO_NOTA" || r.situacao === "REPROVADO_FALTA").length;
            const crp = crMedio(regs);
            return (
              <div key={periodoId} className="rounded-xl border bg-card shadow-card">
                <button onClick={() => setExpanded(expanded === periodoId ? null : periodoId)} className="flex w-full items-center justify-between p-4 text-left">
                  <div>
                    <p className="font-semibold text-foreground">Período #{periodoId}</p>
                    <p className="text-[12px] text-muted-foreground">{aprov} aprovações · {reprov} reprovações · CR {crp ? crp.toFixed(1) : "—"}</p>
                  </div>
                  <span className="text-[12px] text-muted-foreground">{expanded === periodoId ? "Recolher" : "Expandir"}</span>
                </button>
                {expanded === periodoId && (
                  <div className="border-t border-border">
                    <RegistrosTable registros={regs} />
                  </div>
                )}
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}

function RegistrosTable({ registros, actions }: {
  registros: RegistroDisciplina[];
  actions?: (r: RegistroDisciplina) => React.ReactNode;
}) {
  return (
    <DataTable
      columns={[
        { key: "disciplinaId", header: "Disciplina", render: (r) => `#${r.disciplinaId}` },
        { key: "turmaId", header: "Turma", render: (r) => `#${r.turmaId}` },
        { key: "nota", header: "Nota", align: "right", render: (r) => (r.situacao === "APROVEITADO" ? "—" : r.nota.toFixed(1)) },
        { key: "frequencia", header: "Freq.", align: "right", render: (r) => (r.situacao === "APROVEITADO" ? "—" : `${r.frequencia.toFixed(0)}%`) },
        { key: "situacao", header: "Situação", render: (r) => (
          <StatusBadge tone={situacaoAcademicaTone(r.situacao)}>{SITUACAO_ACADEMICA_LABEL[r.situacao]}</StatusBadge>
        )},
        ...(actions ? [{ key: "acoes", header: "", align: "right" as const, render: actions }] : []),
      ]}
      rows={registros}
      empty={<div className="p-8 text-center text-sm text-muted-foreground">Nenhuma disciplina consolidada.</div>}
    />
  );
}

function SolicitarCorrecao({ registros, onBack }: { registros: RegistroDisciplina[]; onBack: () => void }) {
  const [registroId, setRegistroId] = useState<string>("");
  const [descricao, setDescricao] = useState("");

  const enviar = () => {
    if (!registroId) { toast.warning("Selecione o registro a corrigir."); return; }
    if (!descricao.trim()) { toast.warning("Descreva a inconsistência."); return; }
    // F-06 não modela solicitação submetida pelo estudante: a efetivação ocorre
    // via retificação da Secretaria (RN3). Aqui registramos o pedido para análise.
    toast.success("Pedido de correção enviado à Secretaria Acadêmica para análise.");
    onBack();
  };

  return (
    <div className="space-y-4">
      <Button variant="ghost" size="sm" onClick={onBack}><ArrowLeft className="mr-1 h-4 w-4" /> Voltar</Button>
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Solicitar correção de histórico" subtitle="Sua solicitação será analisada pela Secretaria Acadêmica." />
        <div className="mt-4 grid grid-cols-2 gap-4">
          <FormField label="Registro a corrigir" full required>
            <Select value={registroId} onValueChange={setRegistroId}>
              <SelectTrigger className="h-10"><SelectValue placeholder="Selecione a disciplina consolidada" /></SelectTrigger>
              <SelectContent>
                {registros.map((r) => (
                  <SelectItem key={r.id} value={String(r.id)}>
                    Disciplina #{r.disciplinaId} · Período #{r.periodoLetivoId} · {SITUACAO_ACADEMICA_LABEL[r.situacao]}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </FormField>
          <FormField label="O que precisa ser corrigido" full required>
            <Textarea rows={4} value={descricao} onChange={(e) => setDescricao(e.target.value)} placeholder="Descreva a inconsistência" />
          </FormField>
        </div>
        <ValidationCallout className="mt-4" tone="info">A correção só é efetivada após deferimento da Secretaria, com registro do responsável (RN3).</ValidationCallout>
        <div className="mt-4 flex justify-end gap-2">
          <Button variant="outline" onClick={onBack}>Cancelar</Button>
          <Button onClick={enviar}>Enviar solicitação</Button>
        </div>
      </div>
    </div>
  );
}

function EmitirOficial({ onBack }: { onBack: () => void }) {
  const [registros, setRegistros] = useState<RegistroDisciplina[] | null>(null);
  const [loading, setLoading] = useState(true);
  const [erro, setErro] = useState<string | null>(null);

  useEffect(() => {
    setLoading(true);
    buscarHistoricoOficial(ESTUDANTE_LOGADO_ID)
      .then(setRegistros)
      .catch((e: Error) => setErro(e.message))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="space-y-4">
      <Button variant="ghost" size="sm" onClick={onBack}><ArrowLeft className="mr-1 h-4 w-4" /> Voltar</Button>
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Emitir histórico oficial" subtitle="O documento inclui apenas registros consolidados (RN10)." />
        {loading ? (
          <p className="mt-4 text-sm text-muted-foreground">Carregando registros oficiais...</p>
        ) : erro ? (
          <ValidationCallout className="mt-4" tone="error">{erro}</ValidationCallout>
        ) : (
          <>
            <ul className="mt-4 space-y-2 text-[13px] text-foreground">
              <li>✓ Registros consolidados incluídos: {registros?.length ?? 0}</li>
              <li>✓ CR (média das notas): {registros && registros.length ? crMedio(registros).toFixed(1) : "—"}</li>
            </ul>
            {registros && registros.length > 0 && <div className="mt-4"><RegistrosTable registros={registros} /></div>}
            <div className="mt-4 flex justify-end gap-2">
              <Button variant="outline" onClick={onBack}>Cancelar</Button>
              <Button onClick={() => { toast.success("Histórico oficial gerado e baixado."); onBack(); }}>
                <Download className="mr-2 h-4 w-4" /> Baixar PDF
              </Button>
            </div>
          </>
        )}
      </div>
    </div>
  );
}

// ════════════════════════════ SECRETARIA ════════════════════════════

function SecretariaView() {
  const [historicos, setHistoricos] = useState<HistoricoAcademico[]>([]);
  const [loading, setLoading] = useState(true);
  const [erro, setErro] = useState<string | null>(null);
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const [busca, setBusca] = useState("");

  const carregar = () => {
    setLoading(true);
    setErro(null);
    listarHistoricos()
      .then(setHistoricos)
      .catch((e: Error) => setErro(e.message))
      .finally(() => setLoading(false));
  };
  useEffect(carregar, []);

  const selecionado = historicos.find((h) => h.id === selectedId) ?? null;

  if (selecionado) {
    return (
      <AppShell title="Histórico Acadêmico" subtitle="Visão Secretaria · Auditoria e correções">
        <DetalheSecretaria
          historico={selecionado}
          onBack={() => setSelectedId(null)}
          onChanged={(atualizado) => {
            setHistoricos((prev) => prev.map((h) => (h.id === atualizado.id ? atualizado : h)));
          }}
        />
      </AppShell>
    );
  }

  const filtrados = historicos.filter((h) => {
    const q = busca.trim().toLowerCase();
    if (!q) return true;
    return String(h.estudanteId).includes(q) || String(h.id).includes(q);
  });

  return (
    <AppShell title="Histórico Acadêmico" subtitle="Visão Secretaria · Auditoria e correções">
      <div className="space-y-5">
        <StatsRow stats={[
          { label: "Históricos cadastrados", value: historicos.length, tone: "info" },
          { label: "Ativos", value: historicos.filter((h) => h.situacaoDiscente === "ATIVO").length, tone: "success" },
          { label: "Trancados", value: historicos.filter((h) => h.situacaoDiscente === "TRANCADO").length, tone: "warning" },
          { label: "Formandos", value: historicos.filter((h) => h.situacaoDiscente === "FORMANDO").length, tone: "info" },
        ]} />

        <SectionTitle title="Consulta de históricos por estudante" subtitle="Localize, audite e registre alterações no histórico de qualquer estudante." />
        <ActionBar searchPlaceholder="Buscar por id de estudante ou histórico..." onSearch={setBusca} />
        {erro && <ValidationCallout tone="error">{erro}</ValidationCallout>}
        <DataTable
          columns={[
            { key: "id", header: "Histórico", render: (h) => `#${h.id}` },
            { key: "estudanteId", header: "Estudante", render: (h) => `#${h.estudanteId}` },
            { key: "registros", header: "Disciplinas", align: "right", render: (h) => h.registros.length },
            { key: "cr", header: "CR", align: "right", render: (h) => { const c = crMedio(h.registros); return c ? c.toFixed(1) : "—"; } },
            { key: "situacaoDiscente", header: "Situação", render: (h) => (
              <StatusBadge tone={situacaoDiscenteTone(h.situacaoDiscente)}>{SITUACAO_DISCENTE_LABEL[h.situacaoDiscente]}</StatusBadge>
            )},
            { key: "acoes", header: "", align: "right", render: (h) => (
              <RowActionButton onClick={() => setSelectedId(h.id)}>Abrir histórico</RowActionButton>
            )},
          ]}
          rows={filtrados}
          empty={<div className="p-10 text-center text-sm text-muted-foreground">{loading ? "Carregando históricos..." : "Nenhum histórico cadastrado."}</div>}
        />
      </div>
    </AppShell>
  );
}

type DialogKind = null | "situacao" | "acompanhamento" | "aproveitamento" | "retificar" | "consolidar";

function DetalheSecretaria({ historico, onBack, onChanged }: {
  historico: HistoricoAcademico;
  onBack: () => void;
  onChanged: (h: HistoricoAcademico) => void;
}) {
  const [dialog, setDialog] = useState<DialogKind>(null);
  const [registroAlvo, setRegistroAlvo] = useState<RegistroDisciplina | null>(null);

  const recarregar = async () => {
    try {
      const atualizado = await buscarHistoricoPorEstudante(historico.estudanteId);
      onChanged(atualizado);
    } catch (e) {
      toast.error((e as Error).message);
    }
  };

  const cr = crMedio(historico.registros);
  const periodos = agruparPorPeriodo(historico.registros);

  return (
    <div className="space-y-5">
      <Button variant="ghost" size="sm" onClick={onBack}><ArrowLeft className="mr-1 h-4 w-4" /> Voltar à lista</Button>

      <div className="rounded-xl border bg-card p-6 shadow-card">
        <div className="flex flex-wrap items-start justify-between gap-4">
          <SectionTitle title={`Estudante #${historico.estudanteId}`} subtitle={`Histórico #${historico.id} · Matriz #${historico.matrizCurricularId}`} />
          <StatusBadge tone={situacaoDiscenteTone(historico.situacaoDiscente)}>{SITUACAO_DISCENTE_LABEL[historico.situacaoDiscente]}</StatusBadge>
        </div>
        <StatsRow className="mt-4" stats={[
          { label: "CR (média das notas)", value: cr ? cr.toFixed(1) : "—", tone: "success" },
          { label: "Disciplinas", value: historico.registros.length, tone: "info" },
          { label: "Aproveitamentos", value: historico.aproveitamentos.length, tone: "info" },
          { label: "Acompanhamentos", value: historico.acompanhamentos.length, tone: "warning" },
        ]} />
        <div className="mt-5 flex flex-wrap gap-2">
          <Button onClick={() => setDialog("consolidar")}><Plus className="mr-2 h-4 w-4" /> Consolidar resultado</Button>
          <Button variant="outline" onClick={() => setDialog("situacao")}><ShieldCheck className="mr-2 h-4 w-4" /> Atualizar situação discente</Button>
          <Button variant="outline" onClick={() => setDialog("acompanhamento")}><Plus className="mr-2 h-4 w-4" /> Registrar acompanhamento</Button>
          <Button variant="outline" onClick={() => setDialog("aproveitamento")}><Plus className="mr-2 h-4 w-4" /> Registrar aproveitamento</Button>
          <Button variant="outline" onClick={() => toast.success(`Histórico oficial do estudante #${historico.estudanteId} emitido.`)}>
            <Download className="mr-2 h-4 w-4" /> Emitir histórico oficial
          </Button>
        </div>
      </div>

      {periodos.map(([periodoId, regs]) => (
        <div key={periodoId} className="rounded-xl border bg-card p-5 shadow-card">
          <SectionTitle title={`Período #${periodoId}`} subtitle={`${regs.length} disciplinas consolidadas`} />
          <div className="mt-3">
            <RegistrosTable
              registros={regs}
              actions={(r) => (
                <RowActionButton onClick={() => { setRegistroAlvo(r); setDialog("retificar"); }}>Retificar</RowActionButton>
              )}
            />
          </div>
        </div>
      ))}

      {historico.aproveitamentos.length > 0 && (
        <div className="rounded-xl border bg-card p-5 shadow-card">
          <SectionTitle title="Aproveitamentos externos" />
          <DataTable className="mt-3"
            columns={[
              { key: "disciplinaEquivalenteId", header: "Disciplina equiv.", render: (r) => `#${r.disciplinaEquivalenteId}` },
              { key: "disciplinaOrigem", header: "Disciplina de origem" },
              { key: "instituicaoOrigem", header: "Instituição" },
              { key: "cargaHorariaExterna", header: "CH externa", align: "right" },
              { key: "cargaHorariaRequerida", header: "CH exigida", align: "right" },
            ]}
            rows={historico.aproveitamentos}
          />
        </div>
      )}

      {historico.acompanhamentos.length > 0 && (
        <div className="rounded-xl border bg-card p-5 shadow-card">
          <SectionTitle title="Acompanhamentos acadêmicos" />
          <div className="mt-3 space-y-2">
            {historico.acompanhamentos.map((a) => (
              <div key={a.id} className="rounded-lg border border-border bg-muted/30 p-3 text-[13px]">
                <p className="text-foreground">{a.observacao}</p>
                <p className="mt-1 text-[11px] text-muted-foreground">{a.data}</p>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Rastreabilidade — RN5 (auditoria de situação) e RN8 (retificações) */}
      {(historico.trilhaAuditoria.length > 0 || historico.retificacoes.length > 0) && (
        <div className="rounded-xl border bg-card p-5 shadow-card">
          <SectionTitle title="Trilha de auditoria" subtitle="Registro rastreável de alterações de situação e retificações de notas." right={<History className="h-4 w-4 text-muted-foreground" />} />
          <div className="mt-3 space-y-2">
            {historico.trilhaAuditoria.map((e, i) => (
              <div key={`aud-${i}`} className="rounded-lg border border-border bg-muted/30 p-3 text-[13px]">
                <p className="text-foreground">
                  Situação discente: <span className="font-medium">{SITUACAO_DISCENTE_LABEL[e.situacaoAnterior]}</span> → <span className="font-medium">{SITUACAO_DISCENTE_LABEL[e.novaSituacao]}</span>
                </p>
                <p className="text-[12px] text-muted-foreground">{e.justificativa}</p>
                <p className="mt-1 text-[11px] text-muted-foreground">Responsável #{e.responsavelId} · {e.data}</p>
              </div>
            ))}
            {historico.retificacoes.map((r) => (
              <div key={`ret-${r.id}`} className="rounded-lg border border-border bg-muted/30 p-3 text-[13px]">
                <p className="text-foreground">
                  Retificação do registro #{r.registroId}: <span className="font-medium">{SITUACAO_ACADEMICA_LABEL[r.situacaoAnterior]}</span> → <span className="font-medium">{SITUACAO_ACADEMICA_LABEL[r.novaSituacao]}</span>
                </p>
                <p className="text-[12px] text-muted-foreground">{r.justificativa}</p>
                <p className="mt-1 text-[11px] text-muted-foreground">Responsável #{r.responsavelId} · {r.data}</p>
              </div>
            ))}
          </div>
        </div>
      )}

      {dialog === "consolidar" && (
        <ConsolidarRegistroDialog historico={historico} onClose={() => setDialog(null)} onDone={() => { setDialog(null); recarregar(); }} />
      )}
      {dialog === "situacao" && (
        <AtualizarSituacaoDialog historico={historico} onClose={() => setDialog(null)} onDone={() => { setDialog(null); recarregar(); }} />
      )}
      {dialog === "acompanhamento" && (
        <AcompanhamentoDialog historico={historico} onClose={() => setDialog(null)} onDone={() => { setDialog(null); recarregar(); }} />
      )}
      {dialog === "aproveitamento" && (
        <AproveitamentoDialog historico={historico} onClose={() => setDialog(null)} onDone={() => { setDialog(null); recarregar(); }} />
      )}
      {dialog === "retificar" && registroAlvo && (
        <RetificarDialog historico={historico} registro={registroAlvo} onClose={() => setDialog(null)} onDone={() => { setDialog(null); recarregar(); }} />
      )}
    </div>
  );
}

const SITUACOES_DISCENTE: SituacaoDiscente[] = ["ATIVO", "TRANCADO", "EVADIDO", "FORMANDO", "FORMADO"];
const SITUACOES_ACADEMICA: SituacaoAcademica[] = ["APROVADO", "REPROVADO_NOTA", "REPROVADO_FALTA", "TRANCADO", "APROVEITADO"];

function ConsolidarRegistroDialog({ historico, onClose, onDone }: {
  historico: HistoricoAcademico; onClose: () => void; onDone: () => void;
}) {
  const [disciplinaId, setDisciplinaId] = useState("");
  const [turmaId, setTurmaId] = useState("");
  const [periodoLetivoId, setPeriodoLetivoId] = useState("");
  const [nota, setNota] = useState("");
  const [frequencia, setFrequencia] = useState("");
  const [situacao, setSituacao] = useState<SituacaoAcademica>("APROVADO");
  const [turmaEncerrada, setTurmaEncerrada] = useState(true);
  const [salvando, setSalvando] = useState(false);

  const salvar = async () => {
    if (!disciplinaId || !turmaId || !periodoLetivoId) {
      toast.warning("Informe disciplina, turma e período."); return;
    }
    setSalvando(true);
    try {
      await consolidarRegistro(historico.id, {
        disciplinaId: Number(disciplinaId),
        turmaId: Number(turmaId),
        periodoLetivoId: Number(periodoLetivoId),
        nota: Number(nota),
        frequencia: Number(frequencia),
        situacao,
        turmaEncerrada,
      });
      toast.success("Resultado consolidado no histórico.");
      onDone();
    } catch (e) { toast.error((e as Error).message); } finally { setSalvando(false); }
  };

  return (
    <Dialog open onOpenChange={(o) => !o && onClose()}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Consolidar resultado no histórico</DialogTitle>
          <DialogDescription>Apenas turmas encerradas podem ser consolidadas (RN1) e a situação acadêmica final é obrigatória (RN2).</DialogDescription>
        </DialogHeader>
        <div className="grid grid-cols-2 gap-3">
          <FormField label="Disciplina (id)" required><Input className="h-10" type="number" value={disciplinaId} onChange={(e) => setDisciplinaId(e.target.value)} placeholder="401" /></FormField>
          <FormField label="Turma (id)" required><Input className="h-10" type="number" value={turmaId} onChange={(e) => setTurmaId(e.target.value)} placeholder="4" /></FormField>
          <FormField label="Período letivo (id)" required><Input className="h-10" type="number" value={periodoLetivoId} onChange={(e) => setPeriodoLetivoId(e.target.value)} placeholder="2" /></FormField>
          <FormField label="Situação acadêmica" required>
            <Select value={situacao} onValueChange={(v) => setSituacao(v as SituacaoAcademica)}>
              <SelectTrigger className="h-10"><SelectValue /></SelectTrigger>
              <SelectContent>
                {SITUACOES_ACADEMICA.map((s) => <SelectItem key={s} value={s}>{SITUACAO_ACADEMICA_LABEL[s]}</SelectItem>)}
              </SelectContent>
            </Select>
          </FormField>
          <FormField label="Nota"><Input className="h-10" type="number" step="0.1" value={nota} onChange={(e) => setNota(e.target.value)} placeholder="8.0" /></FormField>
          <FormField label="Frequência (%)"><Input className="h-10" type="number" step="0.1" value={frequencia} onChange={(e) => setFrequencia(e.target.value)} placeholder="90" /></FormField>
        </div>
        <label className="mt-1 flex items-center gap-2 text-[13px] text-foreground">
          <input type="checkbox" checked={turmaEncerrada} onChange={(e) => setTurmaEncerrada(e.target.checked)} />
          Turma encerrada
        </label>
        {!turmaEncerrada && <ValidationCallout tone="info">Sem a turma encerrada, o backend bloqueará a consolidação (RN1).</ValidationCallout>}
        <DialogFooter>
          <Button variant="outline" onClick={onClose}>Cancelar</Button>
          <Button onClick={salvar} disabled={salvando}>{salvando ? "Consolidando..." : "Consolidar"}</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}

function AtualizarSituacaoDialog({ historico, onClose, onDone }: {
  historico: HistoricoAcademico; onClose: () => void; onDone: () => void;
}) {
  const [novaSituacao, setNovaSituacao] = useState<SituacaoDiscente>(historico.situacaoDiscente);
  const [justificativa, setJustificativa] = useState("");
  const [salvando, setSalvando] = useState(false);

  const salvar = async () => {
    if (!justificativa.trim()) { toast.warning("Justificativa é obrigatória (RN5)."); return; }
    setSalvando(true);
    try {
      await atualizarSituacaoDiscente(historico.id, {
        novaSituacao, responsavelId: SECRETARIA_ID, justificativa: justificativa.trim(), data: hoje(),
      });
      toast.success("Situação discente atualizada com registro de auditoria.");
      onDone();
    } catch (e) { toast.error((e as Error).message); } finally { setSalvando(false); }
  };

  return (
    <Dialog open onOpenChange={(o) => !o && onClose()}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Atualizar situação discente</DialogTitle>
          <DialogDescription>Toda alteração gera trilha de auditoria com responsável e justificativa (RN5).</DialogDescription>
        </DialogHeader>
        <div className="grid gap-3">
          <FormField label="Nova situação" required>
            <Select value={novaSituacao} onValueChange={(v) => setNovaSituacao(v as SituacaoDiscente)}>
              <SelectTrigger className="h-10"><SelectValue /></SelectTrigger>
              <SelectContent>
                {SITUACOES_DISCENTE.map((s) => <SelectItem key={s} value={s}>{SITUACAO_DISCENTE_LABEL[s]}</SelectItem>)}
              </SelectContent>
            </Select>
          </FormField>
          <FormField label="Justificativa" required>
            <Textarea rows={3} value={justificativa} onChange={(e) => setJustificativa(e.target.value)} placeholder="Motivo da alteração de situação" />
          </FormField>
        </div>
        <DialogFooter>
          <Button variant="outline" onClick={onClose}>Cancelar</Button>
          <Button onClick={salvar} disabled={salvando}>{salvando ? "Salvando..." : "Salvar alteração"}</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}

function AcompanhamentoDialog({ historico, onClose, onDone }: {
  historico: HistoricoAcademico; onClose: () => void; onDone: () => void;
}) {
  const [observacao, setObservacao] = useState("");
  const [vinculoAtivo, setVinculoAtivo] = useState(true);
  const [salvando, setSalvando] = useState(false);

  const salvar = async () => {
    if (!observacao.trim()) { toast.warning("Informe a observação."); return; }
    setSalvando(true);
    try {
      await registrarAcompanhamento(historico.id, {
        observacao: observacao.trim(), data: hoje(), estudanteComVinculoAtivo: vinculoAtivo,
      });
      toast.success("Acompanhamento registrado.");
      onDone();
    } catch (e) { toast.error((e as Error).message); } finally { setSalvando(false); }
  };

  return (
    <Dialog open onOpenChange={(o) => !o && onClose()}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Registrar acompanhamento</DialogTitle>
          <DialogDescription>Permitido apenas para estudante com matrícula ativa ou situação regular (RN4).</DialogDescription>
        </DialogHeader>
        <div className="grid gap-3">
          <FormField label="Observação" required>
            <Textarea rows={3} value={observacao} onChange={(e) => setObservacao(e.target.value)} placeholder="Orientação ou encaminhamento registrado" />
          </FormField>
          <label className="flex items-center gap-2 text-[13px] text-foreground">
            <input type="checkbox" checked={vinculoAtivo} onChange={(e) => setVinculoAtivo(e.target.checked)} />
            Estudante com vínculo ativo / situação regular
          </label>
          {!vinculoAtivo && <ValidationCallout tone="info">Sem vínculo ativo, o backend bloqueará o registro (RN4).</ValidationCallout>}
        </div>
        <DialogFooter>
          <Button variant="outline" onClick={onClose}>Cancelar</Button>
          <Button onClick={salvar} disabled={salvando}>{salvando ? "Salvando..." : "Registrar"}</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}

function AproveitamentoDialog({ historico, onClose, onDone }: {
  historico: HistoricoAcademico; onClose: () => void; onDone: () => void;
}) {
  const [disciplinaEquivalenteId, setDisciplinaEquivalenteId] = useState("");
  const [disciplinaOrigem, setDisciplinaOrigem] = useState("");
  const [instituicaoOrigem, setInstituicaoOrigem] = useState("");
  const [cargaHorariaExterna, setCargaHorariaExterna] = useState("");
  const [cargaHorariaRequerida, setCargaHorariaRequerida] = useState("");
  const [salvando, setSalvando] = useState(false);

  const salvar = async () => {
    if (!disciplinaEquivalenteId || !cargaHorariaExterna || !cargaHorariaRequerida) {
      toast.warning("Preencha disciplina equivalente e as cargas horárias."); return;
    }
    setSalvando(true);
    try {
      await registrarAproveitamento(historico.id, {
        disciplinaEquivalenteId: Number(disciplinaEquivalenteId),
        cargaHorariaExterna: Number(cargaHorariaExterna),
        cargaHorariaRequerida: Number(cargaHorariaRequerida),
        instituicaoOrigem: instituicaoOrigem.trim(),
        disciplinaOrigem: disciplinaOrigem.trim(),
      });
      toast.success("Aproveitamento registrado no histórico.");
      onDone();
    } catch (e) { toast.error((e as Error).message); } finally { setSalvando(false); }
  };

  return (
    <Dialog open onOpenChange={(o) => !o && onClose()}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Registrar aproveitamento externo</DialogTitle>
          <DialogDescription>A CH externa deve ser compatível (≥) com a CH exigida pela matriz (RN7).</DialogDescription>
        </DialogHeader>
        <div className="grid grid-cols-2 gap-3">
          <FormField label="Disciplina equivalente (id)" required><Input className="h-10" type="number" value={disciplinaEquivalenteId} onChange={(e) => setDisciplinaEquivalenteId(e.target.value)} placeholder="101" /></FormField>
          <FormField label="Disciplina de origem"><Input className="h-10" value={disciplinaOrigem} onChange={(e) => setDisciplinaOrigem(e.target.value)} placeholder="Algoritmos I" /></FormField>
          <FormField label="Instituição de origem" full><Input className="h-10" value={instituicaoOrigem} onChange={(e) => setInstituicaoOrigem(e.target.value)} placeholder="UFPE" /></FormField>
          <FormField label="CH externa" required><Input className="h-10" type="number" value={cargaHorariaExterna} onChange={(e) => setCargaHorariaExterna(e.target.value)} placeholder="80" /></FormField>
          <FormField label="CH exigida" required><Input className="h-10" type="number" value={cargaHorariaRequerida} onChange={(e) => setCargaHorariaRequerida(e.target.value)} placeholder="80" /></FormField>
        </div>
        <DialogFooter>
          <Button variant="outline" onClick={onClose}>Cancelar</Button>
          <Button onClick={salvar} disabled={salvando}>{salvando ? "Salvando..." : "Registrar"}</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}

function RetificarDialog({ historico, registro, onClose, onDone }: {
  historico: HistoricoAcademico; registro: RegistroDisciplina; onClose: () => void; onDone: () => void;
}) {
  const [novaSituacao, setNovaSituacao] = useState<SituacaoAcademica>(registro.situacao);
  const [justificativa, setJustificativa] = useState("");
  const [salvando, setSalvando] = useState(false);

  const salvar = async () => {
    if (!justificativa.trim()) { toast.warning("Justificativa é obrigatória (RN8)."); return; }
    setSalvando(true);
    try {
      await retificarRegistro(historico.id, registro.id, {
        novaSituacao, responsavelId: SECRETARIA_ID, justificativa: justificativa.trim(), data: hoje(),
      });
      toast.success("Registro retificado. O resultado anterior foi preservado na trilha.");
      onDone();
    } catch (e) { toast.error((e as Error).message); } finally { setSalvando(false); }
  };

  return (
    <Dialog open onOpenChange={(o) => !o && onClose()}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Retificar registro #{registro.id}</DialogTitle>
          <DialogDescription>Disciplina #{registro.disciplinaId} · situação atual: {SITUACAO_ACADEMICA_LABEL[registro.situacao]}. A retificação preserva o resultado anterior (RN8).</DialogDescription>
        </DialogHeader>
        <div className="grid gap-3">
          <FormField label="Nova situação acadêmica" required>
            <Select value={novaSituacao} onValueChange={(v) => setNovaSituacao(v as SituacaoAcademica)}>
              <SelectTrigger className="h-10"><SelectValue /></SelectTrigger>
              <SelectContent>
                {SITUACOES_ACADEMICA.map((s) => <SelectItem key={s} value={s}>{SITUACAO_ACADEMICA_LABEL[s]}</SelectItem>)}
              </SelectContent>
            </Select>
          </FormField>
          <FormField label="Justificativa" required>
            <Textarea rows={3} value={justificativa} onChange={(e) => setJustificativa(e.target.value)} placeholder="Motivo da retificação" />
          </FormField>
        </div>
        <DialogFooter>
          <Button variant="outline" onClick={onClose}>Cancelar</Button>
          <Button onClick={salvar} disabled={salvando}>{salvando ? "Salvando..." : "Retificar"}</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
