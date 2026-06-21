import { useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  FeaturePage, StatsRow, DataTable, StatusBadge, RowActionButton, FormField,
  SuccessBanner, SectionTitle, useProfileSwitcher,
} from "@/components/acadlab";
import type { StatusTone } from "@/components/acadlab/atoms/StatusBadge";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";
import { Checkbox } from "@/components/ui/checkbox";
import {
  Select, SelectContent, SelectItem, SelectTrigger, SelectValue,
} from "@/components/ui/select";
import {
  Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogFooter,
} from "@/components/ui/dialog";
import { api, type SolicitacaoAcademicaResumo } from "@/lib/api";

export const Route = createFileRoute("/secretaria-virtual")({
  head: () => ({ meta: [{ title: "Secretaria Virtual — AcadLab" }] }),
  component: Page,
});

// ─── Identidade da sessão (mock simples enquanto não há autenticação) ──────────
const ESTUDANTE_ID = 1; // Maria Santos (vide rodapé da sidebar)
const ANALISTA_ID = 1; // Secretaria / analista logado
const PERIODO_LETIVO_ID = 1; // 2025.2 (período vigente)

// ─── Tipos de solicitação (espelha o enum TipoSolicitacao do back-end) ─────────
const TIPOS: { value: string; label: string; docs: string[] }[] = [
  { value: "REVISAO_DE_NOTA", label: "Revisão de Nota", docs: ["comprovante_avaliacao"] },
  { value: "TRANCAMENTO_DISCIPLINA", label: "Trancamento de Disciplina", docs: ["comprovante_matricula"] },
  { value: "TRANCAMENTO_PERIODO", label: "Trancamento de Período", docs: ["comprovante_matricula", "justificativa_formal"] },
  { value: "APROVEITAMENTO_DISCIPLINA", label: "Aproveitamento de Disciplina", docs: ["historico_origem", "ementa_disciplina"] },
  { value: "SEGUNDA_VIA_DOCUMENTO", label: "Segunda Via de Documento", docs: [] },
  { value: "CORRECAO_HISTORICO", label: "Correção de Histórico", docs: ["documento_comprobatorio"] },
  { value: "DECLARACAO_VINCULO", label: "Declaração de Vínculo", docs: [] },
  { value: "OUTROS", label: "Outros", docs: [] },
];
const tipoLabel = (t: string) => TIPOS.find((x) => x.value === t)?.label ?? t;

// ─── Status (espelha o enum StatusSolicitacao do back-end) ─────────────────────
const STATUS: Record<string, { label: string; tone: StatusTone }> = {
  PENDENTE_ANALISE: { label: "Pendente de análise", tone: "info" },
  EM_ANALISE: { label: "Em análise", tone: "info" },
  PENDENTE_COMPLEMENTACAO: { label: "Aguardando complemento", tone: "warning" },
  DEFERIDA: { label: "Deferida", tone: "success" },
  INDEFERIDA: { label: "Indeferida", tone: "danger" },
  CONCLUIDA: { label: "Concluída", tone: "success" },
  CANCELADA: { label: "Cancelada", tone: "neutral" },
};
const statusLabel = (s: string) => STATUS[s]?.label ?? s;
const statusTone = (s: string) => STATUS[s]?.tone ?? "neutral";

const fmtDate = (d: string | null) => (d ? d.split("-").reverse().join("/") : "—");
const protocolo = (s: SolicitacaoAcademicaResumo) => `SEC-${s.protocoloId}`;
const errMsg = (e: unknown) => (e instanceof Error ? e.message : "Erro inesperado");

// ═══════════════════════════════ VISÃO ALUNO ═══════════════════════════════════

function MinhasSolicitacoes() {
  const qc = useQueryClient();
  const { data = [], isLoading, isError } = useQuery({
    queryKey: ["solicitacoes", "estudante", ESTUDANTE_ID],
    queryFn: () => api.solicitacoes.listByEstudante(ESTUDANTE_ID),
  });
  const [detalhe, setDetalhe] = useState<SolicitacaoAcademicaResumo | null>(null);

  const cancelar = useMutation({
    mutationFn: (id: number) => api.solicitacoes.cancelar(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["solicitacoes"] }),
  });

  const count = (...st: string[]) => data.filter((s) => st.includes(s.status)).length;

  return (
    <>
      <StatsRow stats={[
        { label: "Em análise", value: isLoading ? "…" : count("PENDENTE_ANALISE", "EM_ANALISE"), tone: "info" },
        { label: "Aguardando complemento", value: isLoading ? "…" : count("PENDENTE_COMPLEMENTACAO"), tone: "warning" },
        { label: "Deferidas / Concluídas", value: isLoading ? "…" : count("DEFERIDA", "CONCLUIDA"), tone: "success" },
        { label: "Indeferidas", value: isLoading ? "…" : count("INDEFERIDA"), tone: "danger" },
      ]} />

      {isError && <p className="px-1 text-sm text-destructive">Não foi possível conectar ao servidor.</p>}
      {cancelar.isError && <p className="px-1 text-sm text-destructive">Falha ao cancelar: {errMsg(cancelar.error)}</p>}

      <DataTable
        columns={[
          { key: "protocolo", header: "Protocolo", render: (r) => protocolo(r) },
          { key: "tipo", header: "Tipo", render: (r) => tipoLabel(r.tipo) },
          { key: "dataAbertura", header: "Aberta em", render: (r) => fmtDate(r.dataAbertura) },
          { key: "status", header: "Status", render: (r) => <StatusBadge tone={statusTone(r.status)}>{statusLabel(r.status)}</StatusBadge> },
          {
            key: "acoes", header: "", align: "right",
            render: (r) => (
              <div className="flex justify-end gap-1.5">
                <RowActionButton onClick={() => setDetalhe(r)}>Ver</RowActionButton>
                {r.status === "PENDENTE_ANALISE" && (
                  <RowActionButton tone="danger" onClick={() => {
                    if (confirm(`Cancelar a solicitação ${protocolo(r)}?`)) cancelar.mutate(r.id);
                  }}>Cancelar</RowActionButton>
                )}
              </div>
            ),
          },
        ]}
        rows={data}
        empty={<div className="p-10 text-center text-sm text-muted-foreground">Você ainda não abriu solicitações. Use a aba "Nova".</div>}
      />

      <DetalheDialog solicitacao={detalhe} onClose={() => setDetalhe(null)} />
    </>
  );
}

function NovaSolicitacao() {
  const qc = useQueryClient();
  const [tipo, setTipo] = useState<string>("");
  const [descricao, setDescricao] = useState("");
  const [arquivo, setArquivo] = useState<string>("");

  const tipoInfo = TIPOS.find((t) => t.value === tipo);
  const docsObrigatorios = tipoInfo?.docs ?? [];

  const abrir = useMutation({
    mutationFn: () => {
      const documentos = docsObrigatorios.length > 0
        ? docsObrigatorios.map((d) => ({ tipo: d, nomeArquivo: arquivo }))
        : arquivo
          ? [{ tipo: "anexo", nomeArquivo: arquivo }]
          : [];
      return api.solicitacoes.abrir({
        estudanteId: ESTUDANTE_ID, periodoLetivoId: PERIODO_LETIVO_ID,
        tipo, descricao, documentos,
      });
    },
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["solicitacoes"] });
      setTipo(""); setDescricao(""); setArquivo("");
    },
  });

  const faltaDocumento = docsObrigatorios.length > 0 && !arquivo;
  const podeEnviar = tipo && descricao.trim() && !faltaDocumento && !abrir.isPending;

  return (
    <div className="space-y-4">
      {abrir.isSuccess && (
        <SuccessBanner title="Solicitação enviada com sucesso!" description={`Protocolo SEC-${abrir.data} · status: Pendente de análise.`} />
      )}

      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Nova Solicitação Acadêmica" subtitle="Abra um requerimento formal para a secretaria." />
        <div className="mt-4 grid grid-cols-2 gap-4">
          <FormField label="Tipo de serviço" required>
            <Select value={tipo} onValueChange={setTipo}>
              <SelectTrigger className="h-10"><SelectValue placeholder="Selecione o tipo..." /></SelectTrigger>
              <SelectContent>
                {TIPOS.map((t) => <SelectItem key={t.value} value={t.value}>{t.label}</SelectItem>)}
              </SelectContent>
            </Select>
          </FormField>

          <FormField label="Período letivo">
            <Input className="h-10" value={`Período ${PERIODO_LETIVO_ID}`} disabled />
          </FormField>

          <FormField label="Descrição" required full>
            <Textarea rows={4} value={descricao} onChange={(e) => setDescricao(e.target.value)}
              placeholder="Descreva sua solicitação..." />
          </FormField>

          <FormField
            label="Anexos"
            required={docsObrigatorios.length > 0}
            full
            hint={docsObrigatorios.length > 0
              ? `Documentos obrigatórios: ${docsObrigatorios.join(", ")}`
              : "Anexo opcional para este tipo de solicitação."}
          >
            <Input type="file" className="h-10"
              onChange={(e) => setArquivo(e.target.files?.[0]?.name ?? "")} />
          </FormField>
        </div>

        {abrir.isError && <p className="mt-3 text-sm text-destructive">Falha ao enviar: {errMsg(abrir.error)}</p>}
        {faltaDocumento && <p className="mt-3 text-sm text-warning">Anexe o(s) documento(s) obrigatório(s) antes de enviar.</p>}

        <div className="mt-4 flex justify-end gap-2">
          <Button variant="outline" onClick={() => { setTipo(""); setDescricao(""); setArquivo(""); }}>Limpar</Button>
          <Button disabled={!podeEnviar} onClick={() => abrir.mutate()}>
            {abrir.isPending ? "Enviando..." : "Enviar Solicitação"}
          </Button>
        </div>
      </div>
    </div>
  );
}

function ComplementarSolicitacao() {
  const qc = useQueryClient();
  const { data = [], isLoading } = useQuery({
    queryKey: ["solicitacoes", "estudante", ESTUDANTE_ID],
    queryFn: () => api.solicitacoes.listByEstudante(ESTUDANTE_ID),
  });
  const pendentes = data.filter((s) => s.status === "PENDENTE_COMPLEMENTACAO");

  const [sel, setSel] = useState<number | "">("");
  const [docTipo, setDocTipo] = useState("");
  const [arquivo, setArquivo] = useState("");

  const complementar = useMutation({
    mutationFn: () => api.solicitacoes.complementar(Number(sel), { tipo: docTipo || "anexo", nomeArquivo: arquivo }),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["solicitacoes"] });
      setSel(""); setDocTipo(""); setArquivo("");
    },
  });

  if (!isLoading && pendentes.length === 0) {
    return (
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Complementar Solicitação" />
        <p className="mt-3 text-[13px] text-muted-foreground">
          Nenhuma solicitação aguardando complementação no momento.
        </p>
      </div>
    );
  }

  return (
    <div className="rounded-xl border bg-card p-6 shadow-card">
      <SectionTitle title="Complementar Solicitação" subtitle="Anexe os documentos solicitados pela secretaria." />
      <div className="mt-4 grid grid-cols-2 gap-4">
        <FormField label="Solicitação" required>
          <Select value={sel === "" ? "" : String(sel)} onValueChange={(v) => setSel(Number(v))}>
            <SelectTrigger className="h-10"><SelectValue placeholder="Selecione..." /></SelectTrigger>
            <SelectContent>
              {pendentes.map((s) => (
                <SelectItem key={s.id} value={String(s.id)}>{protocolo(s)} — {tipoLabel(s.tipo)}</SelectItem>
              ))}
            </SelectContent>
          </Select>
        </FormField>
        <FormField label="Tipo do documento">
          <Input className="h-10" value={docTipo} onChange={(e) => setDocTipo(e.target.value)} placeholder="ex.: comprovante_pagamento" />
        </FormField>
        <FormField label="Anexo" required full>
          <Input type="file" className="h-10" onChange={(e) => setArquivo(e.target.files?.[0]?.name ?? "")} />
        </FormField>
      </div>

      {complementar.isError && <p className="mt-3 text-sm text-destructive">Falha: {errMsg(complementar.error)}</p>}
      {complementar.isSuccess && <p className="mt-3 text-sm text-success">Complemento enviado. A solicitação voltou para análise.</p>}

      <div className="mt-4 flex justify-end gap-2">
        <Button disabled={!sel || !arquivo || complementar.isPending} onClick={() => complementar.mutate()}>
          {complementar.isPending ? "Enviando..." : "Enviar Complemento"}
        </Button>
      </div>
    </div>
  );
}

// ═══════════════════════════════ VISÃO SECRETARIA ══════════════════════════════

function FilaAnalise() {
  const { data = [], isLoading, isError } = useQuery({
    queryKey: ["solicitacoes", "todas"],
    queryFn: () => api.solicitacoes.listTodas(),
  });
  const [sel, setSel] = useState<SolicitacaoAcademicaResumo | null>(null);

  const count = (...st: string[]) => data.filter((s) => st.includes(s.status)).length;

  return (
    <>
      <StatsRow stats={[
        { label: "Pendentes de análise", value: isLoading ? "…" : count("PENDENTE_ANALISE"), tone: "warning" },
        { label: "Em análise", value: isLoading ? "…" : count("EM_ANALISE"), tone: "info" },
        { label: "Aguardando complemento", value: isLoading ? "…" : count("PENDENTE_COMPLEMENTACAO"), tone: "warning" },
        { label: "Deferidas", value: isLoading ? "…" : count("DEFERIDA"), tone: "success" },
      ]} />

      {isError && <p className="px-1 text-sm text-destructive">Não foi possível conectar ao servidor.</p>}

      <DataTable
        columns={[
          { key: "protocolo", header: "Protocolo", render: (r) => protocolo(r) },
          { key: "estudante", header: "Estudante", render: (r) => `Estudante ${r.estudanteId}` },
          { key: "tipo", header: "Tipo", render: (r) => tipoLabel(r.tipo) },
          { key: "dataAbertura", header: "Aberta em", render: (r) => fmtDate(r.dataAbertura) },
          { key: "status", header: "Status", render: (r) => <StatusBadge tone={statusTone(r.status)}>{statusLabel(r.status)}</StatusBadge> },
          {
            key: "acoes", header: "", align: "right",
            render: (r) => (
              <div className="flex justify-end">
                <RowActionButton onClick={() => setSel(r)}>
                  {["PENDENTE_ANALISE", "EM_ANALISE", "DEFERIDA"].includes(r.status) ? "Analisar" : "Ver"}
                </RowActionButton>
              </div>
            ),
          },
        ]}
        rows={data}
        empty={<div className="p-10 text-center text-sm text-muted-foreground">Nenhuma solicitação recebida.</div>}
      />

      <AnaliseDialog solicitacao={sel} onClose={() => setSel(null)} />
    </>
  );
}

function AnaliseDialog({ solicitacao, onClose }: { solicitacao: SolicitacaoAcademicaResumo | null; onClose: () => void }) {
  const qc = useQueryClient();
  const [justificativa, setJustificativa] = useState("");
  const [impacto, setImpacto] = useState(false);

  const done = () => { qc.invalidateQueries({ queryKey: ["solicitacoes"] }); setJustificativa(""); setImpacto(false); onClose(); };

  const iniciar = useMutation({ mutationFn: (id: number) => api.solicitacoes.iniciarAnalise(id, ANALISTA_ID), onSuccess: () => qc.invalidateQueries({ queryKey: ["solicitacoes"] }) });
  const deferir = useMutation({ mutationFn: (id: number) => api.solicitacoes.deferir(id, { analistaId: ANALISTA_ID, justificativa, impactoAcademico: impacto }), onSuccess: done });
  const indeferir = useMutation({ mutationFn: (id: number) => api.solicitacoes.indeferir(id, { analistaId: ANALISTA_ID, justificativa }), onSuccess: done });
  const solicitarComp = useMutation({ mutationFn: (id: number) => api.solicitacoes.solicitarComplementacao(id, ANALISTA_ID), onSuccess: done });
  const concluir = useMutation({
    mutationFn: (s: SolicitacaoAcademicaResumo) =>
      s.possuiImpactoAcademico ? api.solicitacoes.vincularEConcluir(s.id) : api.solicitacoes.concluir(s.id),
    onSuccess: done,
  });

  const s = solicitacao;
  const busy = iniciar.isPending || deferir.isPending || indeferir.isPending || solicitarComp.isPending || concluir.isPending;
  const anyError = deferir.error || indeferir.error || iniciar.error || solicitarComp.error || concluir.error;
  const precisaJustificativa = !justificativa.trim();

  return (
    <Dialog open={!!s} onOpenChange={(o) => !o && onClose()}>
      <DialogContent className="max-w-lg">
        {s && (
          <>
            <DialogHeader>
              <DialogTitle>{protocolo(s)} — {tipoLabel(s.tipo)}</DialogTitle>
              <DialogDescription>
                Estudante {s.estudanteId} · aberta em {fmtDate(s.dataAbertura)} ·{" "}
                <StatusBadge tone={statusTone(s.status)}>{statusLabel(s.status)}</StatusBadge>
              </DialogDescription>
            </DialogHeader>

            <div className="space-y-3 text-[13px]">
              <div>
                <div className="text-[11px] font-semibold uppercase tracking-wide text-muted-foreground">Descrição</div>
                <p className="mt-1 text-foreground">{s.descricao}</p>
              </div>
              {s.documentos.length > 0 && (
                <div>
                  <div className="text-[11px] font-semibold uppercase tracking-wide text-muted-foreground">Documentos</div>
                  <ul className="mt-1 list-inside list-disc text-foreground">
                    {s.documentos.map((d, i) => <li key={i}>{d.tipo}: {d.nomeArquivo}</li>)}
                  </ul>
                </div>
              )}
              {s.justificativaAnalise && (
                <div>
                  <div className="text-[11px] font-semibold uppercase tracking-wide text-muted-foreground">Parecer registrado</div>
                  <p className="mt-1 text-foreground">{s.justificativaAnalise}</p>
                </div>
              )}

              {/* Decisão exige status EM_ANALISE no domínio */}
              {s.status === "EM_ANALISE" && (
                <div className="space-y-3 border-t pt-3">
                  <FormField label="Parecer / justificativa" required>
                    <Textarea rows={3} value={justificativa} onChange={(e) => setJustificativa(e.target.value)}
                      placeholder="Fundamente a decisão..." />
                  </FormField>
                  <label className="flex items-center gap-2 text-[13px] text-foreground">
                    <Checkbox checked={impacto} onCheckedChange={(v) => setImpacto(v === true)} />
                    Deferimento impacta registros acadêmicos (exige vinculação ao concluir)
                  </label>
                </div>
              )}

              {anyError && <p className="text-sm text-destructive">Falha: {errMsg(anyError)}</p>}
            </div>

            <DialogFooter className="gap-2">
              {s.status === "PENDENTE_ANALISE" && (
                <Button disabled={busy} onClick={() => iniciar.mutate(s.id)}>
                  {iniciar.isPending ? "..." : "Iniciar análise"}
                </Button>
              )}
              {s.status === "EM_ANALISE" && (
                <>
                  <Button variant="outline" disabled={busy} onClick={() => solicitarComp.mutate(s.id)}>Pedir complemento</Button>
                  <Button variant="destructive" disabled={busy || precisaJustificativa} onClick={() => indeferir.mutate(s.id)}>Indeferir</Button>
                  <Button disabled={busy || precisaJustificativa} onClick={() => deferir.mutate(s.id)}>Deferir</Button>
                </>
              )}
              {s.status === "DEFERIDA" && (
                <Button disabled={busy} onClick={() => concluir.mutate(s)}>
                  {concluir.isPending ? "..." : s.possuiImpactoAcademico ? "Vincular alterações e concluir" : "Concluir"}
                </Button>
              )}
              {!["PENDENTE_ANALISE", "EM_ANALISE", "DEFERIDA"].includes(s.status) && (
                <Button variant="outline" onClick={onClose}>Fechar</Button>
              )}
            </DialogFooter>
          </>
        )}
      </DialogContent>
    </Dialog>
  );
}

// ─── Detalhe (visão aluno) ─────────────────────────────────────────────────────

function DetalheDialog({ solicitacao, onClose }: { solicitacao: SolicitacaoAcademicaResumo | null; onClose: () => void }) {
  const s = solicitacao;
  return (
    <Dialog open={!!s} onOpenChange={(o) => !o && onClose()}>
      <DialogContent className="max-w-lg">
        {s && (
          <>
            <DialogHeader>
              <DialogTitle>{protocolo(s)} — {tipoLabel(s.tipo)}</DialogTitle>
              <DialogDescription>
                Aberta em {fmtDate(s.dataAbertura)} ·{" "}
                <StatusBadge tone={statusTone(s.status)}>{statusLabel(s.status)}</StatusBadge>
              </DialogDescription>
            </DialogHeader>
            <div className="space-y-3 text-[13px]">
              <div>
                <div className="text-[11px] font-semibold uppercase tracking-wide text-muted-foreground">Descrição</div>
                <p className="mt-1 text-foreground">{s.descricao}</p>
              </div>
              {s.documentos.length > 0 && (
                <div>
                  <div className="text-[11px] font-semibold uppercase tracking-wide text-muted-foreground">Documentos anexados</div>
                  <ul className="mt-1 list-inside list-disc text-foreground">
                    {s.documentos.map((d, i) => <li key={i}>{d.tipo}: {d.nomeArquivo}</li>)}
                  </ul>
                </div>
              )}
              {s.justificativaAnalise && (
                <div>
                  <div className="text-[11px] font-semibold uppercase tracking-wide text-muted-foreground">Parecer da secretaria</div>
                  <p className="mt-1 text-foreground">{s.justificativaAnalise}</p>
                  <p className="mt-0.5 text-[11px] text-muted-foreground">Analisado em {fmtDate(s.dataAnalise)}</p>
                </div>
              )}
            </div>
            <DialogFooter>
              <Button variant="outline" onClick={onClose}>Fechar</Button>
            </DialogFooter>
          </>
        )}
      </DialogContent>
    </Dialog>
  );
}

// ═══════════════════════════════ PÁGINA ═══════════════════════════════════════

function Page() {
  const { active: perfil } = useProfileSwitcher([
    { value: "estudante", label: "Estudante", description: "Abre e acompanha protocolos" },
    { value: "secretaria", label: "Secretaria Acadêmica", description: "Tria, defere e indefere" },
  ]);

  const isAluno = perfil === "estudante";

  const sections = isAluno
    ? [
        { value: "list", label: "Minhas Solicitações", content: <MinhasSolicitacoes /> },
        { value: "nova", label: "Nova Solicitação", content: <NovaSolicitacao /> },
        { value: "comp", label: "Complementar", content: <ComplementarSolicitacao /> },
      ]
    : [
        { value: "fila", label: "Fila de Análise", content: <FilaAnalise /> },
      ];

  return (
    <FeaturePage
      key={perfil}
      title="Secretaria Virtual Acadêmica"
      subtitle={isAluno ? "Suas solicitações e requerimentos" : "Análise de solicitações dos estudantes"}
      sections={sections}
    />
  );
}
