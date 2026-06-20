import { useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  AppShell,
  SectionTitle,
  StatsRow,
  DataTable,
  StatusBadge,
  RowActionButton,
  ValidationCallout,
  SuccessBanner,
  FormField,
  useProfileSwitcher,
} from "@/components/acadlab";
import type { StatusTone } from "@/components/acadlab/atoms/StatusBadge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
} from "@/components/ui/dialog";
import { CheckCircle2, XCircle, FileSearch } from "lucide-react";
import { toast } from "sonner";
import { api, type IntegralizacaoResumo } from "@/lib/api";

export const Route = createFileRoute("/integralizacao")({
  head: () => ({ meta: [{ title: "Integralização & Colação — AcadLab" }] }),
  component: Page,
});

// ─── Identidade da sessão (mock simples enquanto não há autenticação) ──────────
const ESTUDANTE_ID = 1; // Maria Santos (vide rodapé da sidebar)
const MATRIZ_CURRICULAR_ID = 1; // matriz ativa do curso
const COORDENADOR_ID = 1; // coordenador logado

// ─── Status da integralização (espelha o enum StatusIntegralizacao) ────────────
const STATUS: Record<string, { label: string; tone: StatusTone }> = {
  EM_ANALISE: { label: "Em análise", tone: "info" },
  APTO: { label: "Apto", tone: "success" },
  INAPTO: { label: "Inapto", tone: "danger" },
};
const statusLabel = (s: string) => STATUS[s]?.label ?? s;
const statusTone = (s: string): StatusTone => STATUS[s]?.tone ?? "neutral";

// ─── Itens do checklist (espelha o enum TipoItemChecklist) ─────────────────────
const REQUISITOS: { tipo: string; label: string }[] = [
  { tipo: "DISCIPLINAS_OBRIGATORIAS", label: "Disciplinas obrigatórias" },
  { tipo: "CARGA_OPTATIVA", label: "Carga horária optativa" },
  { tipo: "HORAS_COMPLEMENTARES", label: "Horas complementares" },
  { tipo: "SITUACAO_DISCENTE", label: "Situação discente regular" },
];
const requisitoLabel = (t: string) => REQUISITOS.find((r) => r.tipo === t)?.label ?? t;

const fmtDate = (d: string | null) => (d ? d.split("-").reverse().join("/") : "—");
const errMsg = (e: unknown) => (e instanceof Error ? e.message : "Erro inesperado");
const pendencias = (i: IntegralizacaoResumo) => i.itensChecklist.filter((it) => !it.cumprido);
const aprovada = (i: IntegralizacaoResumo) => i.aprovadorId != null && i.dataAprovacao != null;

// ═══════════════════════════════ VISÃO ESTUDANTE ══════════════════════════════

function EstudanteView() {
  const qc = useQueryClient();
  const {
    data = [],
    isLoading,
    isError,
  } = useQuery({
    queryKey: ["integralizacao", "estudante", ESTUDANTE_ID],
    queryFn: () => api.integralizacao.listByEstudante(ESTUDANTE_ID),
  });
  // A análise mais recente é a de maior id.
  const atual = data.length ? data.reduce((a, b) => (b.id > a.id ? b : a)) : null;

  const { data: colacao } = useQuery({
    queryKey: ["colacao", "estudante", ESTUDANTE_ID],
    queryFn: () => api.integralizacao.getColacaoByEstudante(ESTUDANTE_ID),
    enabled: atual?.status === "APTO",
  });

  const solicitar = useMutation({
    mutationFn: () =>
      api.integralizacao.iniciarAnalise({
        estudanteId: ESTUDANTE_ID,
        matrizCurricularId: MATRIZ_CURRICULAR_ID,
      }),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["integralizacao"] });
      toast.success("Análise de conclusão solicitada.");
    },
    onError: (e) => toast.error(errMsg(e)),
  });

  if (isLoading) return <p className="px-1 text-sm text-muted-foreground">Carregando…</p>;
  if (isError)
    return <p className="px-1 text-sm text-destructive">Não foi possível conectar ao servidor.</p>;

  return (
    <div className="space-y-5">
      {atual && atual.itensChecklist.length > 0 && (
        <div className="rounded-xl border bg-card p-5 shadow-card">
          <SectionTitle
            title="Checklist de integralização"
            subtitle="Gerado a partir do histórico consolidado."
          />
          <div className="mt-3 space-y-2">
            {atual.itensChecklist.map((it, idx) => (
              <div
                key={idx}
                className="flex items-center justify-between rounded-lg border bg-background px-3 py-2 text-[13px]"
              >
                <span className="text-foreground">
                  {requisitoLabel(it.tipo)} — {it.descricao}
                </span>
                <StatusBadge tone={it.cumprido ? "success" : "danger"}>
                  {it.cumprido ? "Cumprido" : "Pendente"}
                </StatusBadge>
              </div>
            ))}
          </div>
        </div>
      )}

      {!atual && (
        <div className="rounded-xl border bg-card p-6 text-center shadow-card">
          <p className="text-[14px] text-muted-foreground">
            Você ainda não solicitou a análise de conclusão do curso.
          </p>
          <Button
            className="mt-4"
            disabled={solicitar.isPending}
            onClick={() => solicitar.mutate()}
          >
            <FileSearch className="mr-2 h-4 w-4" />{" "}
            {solicitar.isPending ? "Solicitando…" : "Solicitar análise de conclusão"}
          </Button>
          <ValidationCallout className="mt-4" tone="info">
            A solicitação só pode ser iniciada após o encerramento do último período cursado e sem
            pendências acadêmicas.
          </ValidationCallout>
        </div>
      )}

      {atual?.status === "EM_ANALISE" && (
        <div className="rounded-xl border bg-card p-5 shadow-card">
          <div className="flex items-center justify-between">
            <SectionTitle
              title="Análise em andamento"
              subtitle="A Secretaria está verificando seus requisitos de integralização."
            />
            <StatusBadge tone="info">Em análise</StatusBadge>
          </div>
        </div>
      )}

      {atual?.status === "APTO" && (
        <div className="space-y-4">
          <SuccessBanner
            title="Você está APTO à colação de grau!"
            description={
              aprovada(atual)
                ? `Aptidão aprovada pelo Coordenador Acadêmico em ${fmtDate(atual.dataAprovacao)}.`
                : "Resultado apto registrado. Aguardando aprovação formal do Coordenador Acadêmico."
            }
          />
          {colacao && colacao.dataCerimonia && (
            <div className="rounded-xl border bg-card p-5 shadow-card">
              <SectionTitle title="Cerimônia de colação" />
              <ul className="mt-3 space-y-2 text-[13px]">
                <li>
                  • Data: {fmtDate(colacao.dataCerimonia)}
                  {colacao.horario ? ` — ${colacao.horario}` : ""}
                </li>
                <li>• Local: {colacao.local ?? "—"}</li>
                {colacao.modalidade && <li>• Modalidade: {colacao.modalidade}</li>}
                {colacao.observacoes && <li>• {colacao.observacoes}</li>}
              </ul>
            </div>
          )}
          <div className="flex items-center gap-3 rounded-xl border border-success bg-success-soft p-4 text-success">
            <CheckCircle2 className="h-6 w-6" />
            <p className="text-[13px]">
              Todos os requisitos foram verificados contra o histórico consolidado.
            </p>
          </div>
        </div>
      )}

      {atual?.status === "INAPTO" && (
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
              { key: "req", header: "Requisito", render: (r) => requisitoLabel(r.tipo) },
              { key: "descricao", header: "Pendência" },
            ]}
            rows={pendencias(atual)}
            empty={
              <div className="p-6 text-center text-sm text-muted-foreground">
                Sem pendências detalhadas.
              </div>
            }
          />
          {atual.observacao && (
            <ValidationCallout tone="error">{atual.observacao}</ValidationCallout>
          )}
        </div>
      )}
    </div>
  );
}

// ═══════════════════════════════ VISÃO SECRETARIA ACADÊMICA (US02 + US04) ═════

function SecretariaView() {
  const qc = useQueryClient();
  const {
    data = [],
    isLoading,
    isError,
  } = useQuery({
    queryKey: ["integralizacao", "todas"],
    queryFn: () => api.integralizacao.listAll(),
  });

  const [analise, setAnalise] = useState<IntegralizacaoResumo | null>(null);
  const [colacaoDe, setColacaoDe] = useState<IntegralizacaoResumo | null>(null);

  const invalidar = () => qc.invalidateQueries({ queryKey: ["integralizacao"] });

  const count = (...st: string[]) => data.filter((i) => st.includes(i.status)).length;

  return (
    <div className="space-y-5">
      <StatsRow
        stats={[
          { label: "Análises", value: isLoading ? "…" : data.length, tone: "info" },
          { label: "Em análise", value: isLoading ? "…" : count("EM_ANALISE"), tone: "warning" },
          { label: "Aptos", value: isLoading ? "…" : count("APTO"), tone: "success" },
          { label: "Inaptos", value: isLoading ? "…" : count("INAPTO"), tone: "danger" },
        ]}
      />

      {isError && (
        <p className="px-1 text-sm text-destructive">Não foi possível conectar ao servidor.</p>
      )}

      <SectionTitle
        title="Análises de integralização"
        subtitle="Gere o checklist, registre o resultado e registre a cerimônia de colação."
      />

      <DataTable
        columns={[
          { key: "id", header: "#", render: (r) => `INT-${r.id}` },
          { key: "estudanteId", header: "Estudante", render: (r) => `Estudante ${r.estudanteId}` },
          {
            key: "pend",
            header: "Pendências",
            align: "right",
            render: (r) => pendencias(r).length,
          },
          {
            key: "status",
            header: "Status",
            render: (r) => (
              <StatusBadge tone={statusTone(r.status)}>
                {statusLabel(r.status)}
                {r.status === "APTO" && aprovada(r) ? " · aprovado" : ""}
              </StatusBadge>
            ),
          },
          {
            key: "acoes",
            header: "",
            align: "right",
            render: (r) => (
              <div className="flex justify-end gap-1.5">
                {r.status === "EM_ANALISE" && (
                  <RowActionButton tone="info" onClick={() => setAnalise(r)}>
                    Analisar
                  </RowActionButton>
                )}
                {r.status === "APTO" && aprovada(r) && (
                  <RowActionButton onClick={() => setColacaoDe(r)}>
                    Registrar colação
                  </RowActionButton>
                )}
                {(r.status === "INAPTO" || (r.status === "APTO" && !aprovada(r))) && (
                  <RowActionButton onClick={() => setAnalise(r)}>
                    {r.status === "INAPTO" ? "Ver pendências" : "Ver checklist"}
                  </RowActionButton>
                )}
              </div>
            ),
          },
        ]}
        rows={data}
        empty={
          <div className="p-10 text-center text-sm text-muted-foreground">
            Nenhuma análise de integralização iniciada.
          </div>
        }
      />

      <AnaliseDialog integralizacao={analise} onClose={() => setAnalise(null)} onDone={invalidar} />
      <ColacaoDialog
        integralizacao={colacaoDe}
        onClose={() => setColacaoDe(null)}
        onDone={invalidar}
      />
    </div>
  );
}

// ═══════════════════════════════ VISÃO COORDENAÇÃO ACADÊMICA (US03) ═══════════

function CoordenacaoView() {
  const qc = useQueryClient();
  const {
    data = [],
    isLoading,
    isError,
  } = useQuery({
    queryKey: ["integralizacao", "todas"],
    queryFn: () => api.integralizacao.listAll(),
  });

  const [detalhe, setDetalhe] = useState<IntegralizacaoResumo | null>(null);

  const invalidar = () => qc.invalidateQueries({ queryKey: ["integralizacao"] });

  const aprovar = useMutation({
    mutationFn: (id: number) => api.integralizacao.aprovarAptidao(id, COORDENADOR_ID),
    onSuccess: () => {
      invalidar();
      toast.success("Aptidão aprovada para colação de grau.");
    },
    onError: (e) => toast.error(errMsg(e)),
  });

  const aptos = data.filter((i) => i.status === "APTO");
  const pendentesAprovacao = aptos.filter((i) => !aprovada(i));
  const aprovados = aptos.filter((i) => aprovada(i));

  return (
    <div className="space-y-5">
      <StatsRow
        stats={[
          { label: "Aptos (total)", value: isLoading ? "…" : aptos.length, tone: "info" },
          {
            label: "Aguardando aprovação",
            value: isLoading ? "…" : pendentesAprovacao.length,
            tone: "warning",
          },
          { label: "Aprovados", value: isLoading ? "…" : aprovados.length, tone: "success" },
        ]}
      />

      {isError && (
        <p className="px-1 text-sm text-destructive">Não foi possível conectar ao servidor.</p>
      )}

      <SectionTitle
        title="Aprovação de aptidão para colação de grau"
        subtitle="Aprove formalmente a aptidão dos estudantes que cumpriram todos os requisitos curriculares."
      />

      <DataTable
        columns={[
          { key: "id", header: "#", render: (r) => `INT-${r.id}` },
          { key: "estudanteId", header: "Estudante", render: (r) => `Estudante ${r.estudanteId}` },
          {
            key: "pend",
            header: "Pendências",
            align: "right",
            render: (r) => pendencias(r).length,
          },
          {
            key: "status",
            header: "Status",
            render: (r) => (
              <StatusBadge tone={aprovada(r) ? "success" : "warning"}>
                {aprovada(r) ? `Aprovado em ${fmtDate(r.dataAprovacao)}` : "Aguardando aprovação"}
              </StatusBadge>
            ),
          },
          {
            key: "acoes",
            header: "",
            align: "right",
            render: (r) => (
              <div className="flex justify-end gap-1.5">
                <RowActionButton onClick={() => setDetalhe(r)}>Ver checklist</RowActionButton>
                {!aprovada(r) && (
                  <RowActionButton tone="info" onClick={() => aprovar.mutate(r.id)}>
                    Aprovar aptidão
                  </RowActionButton>
                )}
              </div>
            ),
          },
        ]}
        rows={aptos}
        empty={
          <div className="p-10 text-center text-sm text-muted-foreground">
            Nenhuma integralização com resultado apto no momento.
          </div>
        }
      />

      {aprovar.isError && (
        <p className="px-1 text-sm text-destructive">Falha: {errMsg(aprovar.error)}</p>
      )}

      <AnaliseDialog integralizacao={detalhe} onClose={() => setDetalhe(null)} onDone={invalidar} />
    </div>
  );
}

// ─── Diálogo de análise: gerar checklist + registrar resultado ─────────────────

function AnaliseDialog({
  integralizacao,
  onClose,
  onDone,
}: {
  integralizacao: IntegralizacaoResumo | null;
  onClose: () => void;
  onDone: () => void;
}) {
  const base = integralizacao;
  // Lê a versão mais recente para refletir o checklist gerado no servidor (RN3).
  const { data: i } = useQuery({
    queryKey: ["integralizacao", "detalhe", base?.id],
    queryFn: () => api.integralizacao.getById(base!.id),
    enabled: !!base,
    initialData: base ?? undefined,
  });

  const gerar = useMutation({
    mutationFn: () => api.integralizacao.gerarChecklist(base!.id),
    onSuccess: () => {
      onDone();
      toast.success("Checklist gerado a partir dos registros consolidados.");
    },
    onError: (e) => toast.error(errMsg(e)),
  });

  const registrar = useMutation({
    mutationFn: (resultado: "APTO" | "INAPTO") =>
      api.integralizacao.registrarResultado(base!.id, resultado),
    onSuccess: () => {
      onDone();
      toast.success("Resultado da análise registrado.");
      onClose();
    },
    onError: (e) => toast.error(errMsg(e)),
  });

  const itens = i?.itensChecklist ?? [];
  const temChecklist = itens.length > 0;
  const todosCumpridos = temChecklist && itens.every((it) => it.cumprido);
  const temPendencia = itens.some((it) => !it.cumprido);
  const editavel = i?.status === "EM_ANALISE";
  const ocupado = gerar.isPending || registrar.isPending;

  return (
    <Dialog open={!!i} onOpenChange={(o) => !o && onClose()}>
      <DialogContent className="max-w-lg">
        {i && (
          <>
            <DialogHeader>
              <DialogTitle>
                INT-{i.id} — Estudante {i.estudanteId}
              </DialogTitle>
              <DialogDescription>
                Checklist de integralização gerado a partir dos registros consolidados.
              </DialogDescription>
            </DialogHeader>

            <div className="space-y-2 text-[13px]">
              {!temChecklist && (
                <div className="rounded-lg border bg-background px-3 py-4 text-center text-muted-foreground">
                  {editavel
                    ? "Gere o checklist para verificar os requisitos de integralização."
                    : "Nenhum item de checklist registrado."}
                </div>
              )}
              {itens.map((it, idx) => (
                <div
                  key={idx}
                  className="flex items-center justify-between rounded-lg border bg-background px-3 py-2"
                >
                  <span className="text-foreground">
                    {requisitoLabel(it.tipo)} — {it.descricao}
                  </span>
                  <StatusBadge tone={it.cumprido ? "success" : "danger"}>
                    {it.cumprido ? "Cumprido" : "Pendente"}
                  </StatusBadge>
                </div>
              ))}
              {editavel && temChecklist && (
                <p className="pt-1 text-[12px] text-muted-foreground">
                  Resultado sugerido: <strong>{todosCumpridos ? "APTO" : "INAPTO"}</strong>
                </p>
              )}
              {(gerar.isError || registrar.isError) && (
                <p className="text-sm text-destructive">
                  Falha: {errMsg(gerar.error ?? registrar.error)}
                </p>
              )}
            </div>

            <DialogFooter className="gap-2">
              {editavel && !temChecklist && (
                <Button disabled={ocupado} onClick={() => gerar.mutate()}>
                  {gerar.isPending ? "Gerando…" : "Gerar checklist"}
                </Button>
              )}
              {editavel && temChecklist && (
                <>
                  <Button variant="ghost" disabled={ocupado} onClick={() => gerar.mutate()}>
                    Regerar
                  </Button>
                  <Button
                    variant="destructive"
                    disabled={ocupado || !temPendencia}
                    onClick={() => registrar.mutate("INAPTO")}
                  >
                    Registrar INAPTO
                  </Button>
                  <Button
                    disabled={ocupado || !todosCumpridos}
                    onClick={() => registrar.mutate("APTO")}
                  >
                    Registrar APTO
                  </Button>
                </>
              )}
              {!editavel && (
                <Button variant="outline" onClick={onClose}>
                  Fechar
                </Button>
              )}
            </DialogFooter>
          </>
        )}
      </DialogContent>
    </Dialog>
  );
}

// ─── Diálogo de registro da cerimônia de colação ───────────────────────────────

function ColacaoDialog({
  integralizacao,
  onClose,
  onDone,
}: {
  integralizacao: IntegralizacaoResumo | null;
  onClose: () => void;
  onDone: () => void;
}) {
  const i = integralizacao;
  const [dataCerimonia, setDataCerimonia] = useState("");
  const [horario, setHorario] = useState("");
  const [local, setLocal] = useState("");
  const [modalidade, setModalidade] = useState("");
  const [observacoes, setObservacoes] = useState("");

  const registrar = useMutation({
    mutationFn: () =>
      api.integralizacao.registrarColacao(i!.id, {
        dataCerimonia,
        horario: horario || undefined,
        local,
        modalidade: modalidade || undefined,
        observacoes: observacoes || undefined,
      }),
    onSuccess: () => {
      onDone();
      toast.success("Cerimônia de colação registrada.");
      onClose();
    },
    onError: (e) => toast.error(errMsg(e)),
  });

  const podeRegistrar = !!dataCerimonia && !!local.trim() && !registrar.isPending;

  return (
    <Dialog open={!!i} onOpenChange={(o) => !o && onClose()}>
      <DialogContent className="max-w-lg">
        {i && (
          <>
            <DialogHeader>
              <DialogTitle>Registrar colação — Estudante {i.estudanteId}</DialogTitle>
              <DialogDescription>
                A data da cerimônia deve ser igual ou posterior à data de aprovação da aptidão (
                {fmtDate(i.dataAprovacao)}).
              </DialogDescription>
            </DialogHeader>

            <div className="grid grid-cols-2 gap-4">
              <FormField label="Data da cerimônia" required>
                <Input
                  type="date"
                  value={dataCerimonia}
                  min={i.dataAprovacao ?? undefined}
                  onChange={(e) => setDataCerimonia(e.target.value)}
                />
              </FormField>
              <FormField label="Horário">
                <Input
                  value={horario}
                  placeholder="19:00"
                  onChange={(e) => setHorario(e.target.value)}
                />
              </FormField>
              <FormField label="Local" required>
                <Input
                  value={local}
                  placeholder="Auditório Central"
                  onChange={(e) => setLocal(e.target.value)}
                />
              </FormField>
              <FormField label="Modalidade">
                <Input
                  value={modalidade}
                  placeholder="Presencial"
                  onChange={(e) => setModalidade(e.target.value)}
                />
              </FormField>
              <div className="col-span-2">
                <FormField label="Observações">
                  <Textarea
                    rows={2}
                    value={observacoes}
                    onChange={(e) => setObservacoes(e.target.value)}
                  />
                </FormField>
              </div>
            </div>

            {registrar.isError && (
              <p className="text-sm text-destructive">Falha: {errMsg(registrar.error)}</p>
            )}

            <DialogFooter className="gap-2">
              <Button variant="outline" onClick={onClose}>
                Cancelar
              </Button>
              <Button disabled={!podeRegistrar} onClick={() => registrar.mutate()}>
                {registrar.isPending ? "Registrando…" : "Registrar colação"}
              </Button>
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
    {
      value: "estudante",
      label: "Estudante",
      description: "Solicita análise e acompanha integralização",
    },
    {
      value: "secretaria",
      label: "Secretaria Acadêmica",
      description: "Analisa integralização e registra colação",
    },
    {
      value: "coordenacao",
      label: "Coordenação Acadêmica",
      description: "Aprova aptidão para colação de grau",
    },
  ]);

  const subtitle =
    perfil === "secretaria"
      ? "Visão Secretaria · Análise de integralização e registro de colação"
      : perfil === "coordenacao"
        ? "Visão Coordenação · Aprovação de aptidão para colação"
        : "Maria Santos · Engenharia de Software";

  return (
    <AppShell title="Integralização Curricular & Colação" subtitle={subtitle}>
      {perfil === "secretaria" ? (
        <SecretariaView />
      ) : perfil === "coordenacao" ? (
        <CoordenacaoView />
      ) : (
        <EstudanteView />
      )}
    </AppShell>
  );
}
