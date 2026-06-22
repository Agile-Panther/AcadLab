import { toast } from "sonner";
import { useMemo, useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import {
  AppShell, SectionTitle, StatsRow, DataTable, StatusBadge, RowActionButton,
  FormField, ValidationCallout, Stepper, SuccessBanner,
  useProfileSwitcher,
} from "@/components/acadlab";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { ArrowLeft, Calendar, Plus, FileText, Clock, User } from "lucide-react";
import { formatData } from "@/lib/format";
import { ApiError } from "@/lib/api";
import {
  useEditais, useTodasInscricoes, useInscricoesEstudante, useBeneficiosEstudante,
  useInscrever, useInterporRecurso, useRenovarBeneficio,
  useCriarEdital, useEncerrarEdital, useDeferirInscricao, useIndeferirInscricao,
  type EditalResumo, type InscricaoResumo, type BeneficioResumo,
} from "@/lib/permanencia";

export const Route = createFileRoute("/permanencia")({
  head: () => ({ meta: [{ title: "Permanência Acadêmica — AcadLab" }] }),
  component: Page,
});

/* ===== Mapeamentos de status ===== */

const editalAberto = (e: EditalResumo) => e.status === "INSCRICOES_ABERTAS";
const editalLabel = (e: EditalResumo) => (editalAberto(e) ? "Aberto" : "Encerrado");

function inscricaoLabel(s: InscricaoResumo["status"]): string {
  switch (s) {
    case "PENDENTE": return "Em análise";
    case "DEFERIDA": return "Deferida";
    case "INDEFERIDA": return "Indeferida";
    case "RECURSO_INTERPOSTO": return "Recurso interposto";
    case "RECURSO_ANALISADO": return "Recurso analisado";
  }
}
function inscricaoTone(s: InscricaoResumo["status"]) {
  if (s === "DEFERIDA") return "success" as const;
  if (s === "INDEFERIDA") return "danger" as const;
  if (s === "RECURSO_INTERPOSTO" || s === "RECURSO_ANALISADO") return "info" as const;
  return "warning" as const;
}
function beneficioLabel(s: BeneficioResumo["status"]): string {
  return s === "ATIVO" ? "Ativo" : s === "SUSPENSO" ? "Suspenso" : "Cancelado";
}
function beneficioTone(s: BeneficioResumo["status"]) {
  return s === "ATIVO" ? "success" as const : s === "SUSPENSO" ? "danger" as const : "neutral" as const;
}

function notifyError(e: unknown) {
  toast.error(e instanceof ApiError ? e.message : "Não foi possível concluir a operação.");
}

type View =
  | { kind: "overview" }
  | { kind: "edital"; id: number }
  | { kind: "inscricao"; step: 0 | 1 | 2; editalId: number }
  | { kind: "recurso"; inscricaoId: number; editalId: number }
  | { kind: "detail-beneficio"; id: number }
  | { kind: "detail-inscricao"; id: number };

function Page() {
  const [view, setView] = useState<View>({ kind: "overview" });

  const editaisQuery = useEditais();
  const beneficiosQuery = useBeneficiosEstudante();
  const inscricoesQuery = useInscricoesEstudante();

  const editais = editaisQuery.data ?? [];
  const beneficios = beneficiosQuery.data ?? [];
  const inscricoes = inscricoesQuery.data ?? [];

  const editalNome = useMemo(() => {
    const map = new Map<number, string>();
    editais.forEach((e) => map.set(e.id, e.programa));
    return (id: number) => map.get(id) ?? `Edital #${id}`;
  }, [editais]);

  const renovar = useRenovarBeneficio();
  const inscrever = useInscrever();
  const recurso = useInterporRecurso();

  const { active: perfil } = useProfileSwitcher([
    { value: "estudante", label: "Estudante", description: "Inscreve-se e acompanha benefícios" },
    { value: "assistencia", label: "Assistência Estudantil", description: "Analisa pedidos e gere editais" },
  ]);
  const subtitle = perfil === "assistencia"
    ? "Setor de Assistência Estudantil · Análise de pedidos"
    : "Bolsas e auxílios institucionais";

  const handleRenovar = (id: number) => {
    renovar.mutate(id, {
      onSuccess: () => toast.success("Solicitação de renovação enviada! Nova vigência será processada."),
      onError: notifyError,
    });
  };

  const handleSendRecurso = (e: React.FormEvent, inscricaoId: number, editalId: number) => {
    e.preventDefault();
    recurso.mutate({ inscricaoId, editalId }, {
      onSuccess: () => { toast.success("Recurso enviado com sucesso! Aguarde a reanálise."); setView({ kind: "overview" }); },
      onError: notifyError,
    });
  };

  if (perfil === "assistencia") {
    return (
      <AppShell title="Permanência Acadêmica" subtitle={subtitle}>
        <AssistenciaView editais={editais} editalNome={editalNome} loading={editaisQuery.isLoading} />
      </AppShell>
    );
  }

  return (
    <AppShell title="Permanência Acadêmica" subtitle={subtitle}>
      {view.kind === "overview" && (
        <div className="space-y-5">
          <StatsRow stats={[
            { label: "Benefícios ativos", value: beneficios.filter((b) => b.status === "ATIVO").length, tone: "success" },
            { label: "Editais abertos", value: editais.filter(editalAberto).length, tone: "info" },
            { label: "Inscrições", value: inscricoes.length, tone: "info" },
            { label: "Em análise", value: inscricoes.filter((i) => i.status === "PENDENTE").length, tone: "warning" },
          ]} />

          <SectionTitle title="Meus benefícios" />
          {beneficiosQuery.isLoading ? (
            <p className="text-sm text-muted-foreground">Carregando benefícios…</p>
          ) : beneficios.length === 0 ? (
            <ValidationCallout tone="info">Você não possui benefícios ativos no momento.</ValidationCallout>
          ) : (
            <DataTable
              columns={[
                { key: "protocolo", header: "Protocolo", render: (r) => `BEN-${r.id}` },
                { key: "nome", header: "Benefício", render: (r) => editalNome(r.editalId) },
                { key: "status", header: "Status", render: (r) => <StatusBadge tone={beneficioTone(r.status)}>{beneficioLabel(r.status)}</StatusBadge> },
                { key: "vencimento", header: "Renova em", render: (r) => formatData(r.prazoRenovacao) },
                { key: "acoes", header: "", align: "right", render: (r) => (
                  <div className="flex justify-end gap-1.5">
                    {r.status === "ATIVO" && <RowActionButton onClick={() => handleRenovar(r.id)}>Renovar</RowActionButton>}
                    <RowActionButton tone="neutral" onClick={() => setView({ kind: "detail-beneficio", id: r.id })}>Detalhes</RowActionButton>
                  </div>
                ) },
              ]}
              rows={beneficios}
            />
          )}

          <SectionTitle title="Editais disponíveis" />
          {editaisQuery.isLoading ? (
            <p className="text-sm text-muted-foreground">Carregando editais…</p>
          ) : (
            <div className="grid gap-3 md:grid-cols-2">
              {editais.map((e) => (
                <div key={e.id} className="rounded-xl border bg-card p-5 shadow-card">
                  <div className="flex items-start justify-between">
                    <div>
                      <p className="text-[12px] text-muted-foreground">EDT-{e.id}</p>
                      <h3 className="mt-1 font-semibold text-foreground">{e.programa}</h3>
                    </div>
                    <StatusBadge tone={editalAberto(e) ? "success" : "neutral"}>{editalLabel(e)}</StatusBadge>
                  </div>
                  {e.descricao && <p className="mt-2 text-[13px] text-muted-foreground line-clamp-2">{e.descricao}</p>}
                  <div className="mt-3 flex items-center gap-4 text-[12px] text-muted-foreground">
                    <span>{e.vagas} vagas</span>
                    <span className="flex items-center gap-1"><Calendar className="h-3.5 w-3.5" /> até {formatData(e.prazoInscricaoFim)}</span>
                  </div>
                  <Button className="mt-3 w-full" disabled={!editalAberto(e)} onClick={() => setView({ kind: "edital", id: e.id })}>Ver edital</Button>
                </div>
              ))}
            </div>
          )}

          <div className="rounded-xl border bg-card p-5 shadow-card">
            <SectionTitle title="Inscrições anteriores" />
            {inscricoes.length === 0 ? (
              <p className="mt-3 text-sm text-muted-foreground">Você ainda não realizou inscrições.</p>
            ) : (
              <DataTable className="mt-3"
                columns={[
                  { key: "protocolo", header: "Protocolo", render: (r) => `INS-${r.id}` },
                  { key: "edital", header: "Edital", render: (r) => editalNome(r.editalId) },
                  { key: "status", header: "Status", render: (r) => <StatusBadge tone={inscricaoTone(r.status)}>{inscricaoLabel(r.status)}</StatusBadge> },
                  { key: "acoes", header: "", align: "right", render: (r) => (
                    <div className="flex justify-end gap-1.5">
                      <RowActionButton tone="neutral" onClick={() => setView({ kind: "detail-inscricao", id: r.id })}>Detalhes</RowActionButton>
                      {r.status === "INDEFERIDA" && <RowActionButton onClick={() => setView({ kind: "recurso", inscricaoId: r.id, editalId: r.editalId })}>Interpor recurso</RowActionButton>}
                    </div>
                  ) },
                ]}
                rows={inscricoes}
              />
            )}
          </div>
        </div>
      )}

      {view.kind === "edital" && (() => {
        const e = editais.find((x) => x.id === view.id);
        if (!e) return null;
        return (
          <div className="space-y-4">
            <Button variant="ghost" size="sm" onClick={() => setView({ kind: "overview" })}><ArrowLeft className="mr-1 h-4 w-4" /> Voltar</Button>
            <SectionTitle title={`EDT-${e.id} — ${e.programa}`} subtitle={`${e.vagas} vagas · inscrições até ${formatData(e.prazoInscricaoFim)}`} />
            {e.descricao && (
              <div className="rounded-xl border bg-card p-5 shadow-card">
                <h3 className="font-semibold">Sobre o programa</h3>
                <p className="mt-2 text-[13px] text-muted-foreground">{e.descricao}</p>
              </div>
            )}
            <div className="rounded-xl border bg-card p-5 shadow-card">
              <h3 className="font-semibold">Prazos do edital</h3>
              <div className="mt-3 grid gap-2 text-[13px] text-muted-foreground sm:grid-cols-2">
                <div><span className="text-foreground">Inscrições:</span> {formatData(e.prazoInscricaoInicio)} a {formatData(e.prazoInscricaoFim)}</div>
                <div><span className="text-foreground">Recursos:</span> {formatData(e.prazoRecursoInicio)} a {formatData(e.prazoRecursoFim)}</div>
                <div><span className="text-foreground">Renovação:</span> {formatData(e.prazoRenovacao)}</div>
                <div><span className="text-foreground">Vagas:</span> {e.vagas}</div>
              </div>
            </div>
            <ValidationCallout tone="info">Ao se inscrever, confirme que atende aos critérios de elegibilidade do edital.</ValidationCallout>
            <div className="flex justify-end gap-2">
              <Button variant="outline" onClick={() => setView({ kind: "overview" })}>Voltar</Button>
              {editalAberto(e) && <Button onClick={() => setView({ kind: "inscricao", step: 0, editalId: e.id })}>Inscrever-se</Button>}
            </div>
          </div>
        );
      })()}

      {view.kind === "inscricao" && (
        <InscricaoWizard
          step={view.step}
          submitting={inscrever.isPending}
          onStep={(s) => setView({ kind: "inscricao", step: s, editalId: view.editalId })}
          onSubmit={() =>
            inscrever.mutate({ editalId: view.editalId, atendeElegibilidade: true }, {
              onSuccess: (id) => { toast.success(`Inscrição registrada! Protocolo: INS-${id}`); setView({ kind: "inscricao", step: 2, editalId: view.editalId }); },
              onError: notifyError,
            })
          }
          onDone={() => setView({ kind: "overview" })}
        />
      )}

      {view.kind === "recurso" && (
        <div className="space-y-4">
          <Button variant="ghost" size="sm" onClick={() => setView({ kind: "overview" })}><ArrowLeft className="mr-1 h-4 w-4" /> Voltar</Button>
          <form onSubmit={(e) => handleSendRecurso(e, view.inscricaoId, view.editalId)} className="rounded-xl border bg-card p-6 shadow-card">
            <SectionTitle title="Interpor recurso" subtitle={`Inscrição INS-${view.inscricaoId} · Apenas um recurso por inscrição é permitido.`} />
            <FormField className="mt-4" label="Justificativa" required full><Textarea rows={5} required /></FormField>
            <FormField label="Documentação complementar" full><Input type="file" className="h-10" /></FormField>
            <div className="mt-4 flex justify-end gap-2"><Button type="button" variant="outline" onClick={() => setView({ kind: "overview" })}>Cancelar</Button><Button type="submit" disabled={recurso.isPending}>Enviar recurso</Button></div>
          </form>
        </div>
      )}

      {view.kind === "detail-beneficio" && (() => {
        const b = beneficios.find((x) => x.id === view.id);
        if (!b) return null;
        return (
          <div className="space-y-4">
            <Button variant="ghost" size="sm" onClick={() => setView({ kind: "overview" })}><ArrowLeft className="mr-1 h-4 w-4" /> Voltar</Button>
            <SectionTitle title={editalNome(b.editalId)} subtitle={`Protocolo BEN-${b.id}`} />
            <div className="grid gap-4 md:grid-cols-2">
              <div className="rounded-xl border bg-card p-5 shadow-card space-y-3">
                <h3 className="font-semibold flex items-center gap-2"><FileText className="h-4 w-4" /> Informações</h3>
                <div className="text-[13px]"><span className="text-muted-foreground">Status:</span> <StatusBadge tone={beneficioTone(b.status)}>{beneficioLabel(b.status)}</StatusBadge></div>
                <div className="text-[13px]"><span className="text-muted-foreground">Ativado em:</span> {formatData(b.dataAtivacao)}</div>
                <div className="text-[13px]"><span className="text-muted-foreground">Renova em:</span> {formatData(b.prazoRenovacao)}</div>
              </div>
              <div className="rounded-xl border bg-card p-5 shadow-card space-y-3">
                <h3 className="font-semibold flex items-center gap-2"><Clock className="h-4 w-4" /> Origem</h3>
                <div className="text-[13px]"><span className="text-muted-foreground">Edital:</span> {editalNome(b.editalId)}</div>
                <div className="text-[13px]"><span className="text-muted-foreground">Inscrição:</span> INS-{b.inscricaoId}</div>
              </div>
            </div>
            <div className="flex justify-end gap-2">
              <Button variant="outline" onClick={() => setView({ kind: "overview" })}>Voltar</Button>
              {b.status === "ATIVO" && <Button disabled={renovar.isPending} onClick={() => handleRenovar(b.id)}>Solicitar renovação</Button>}
            </div>
          </div>
        );
      })()}

      {view.kind === "detail-inscricao" && (() => {
        const ins = inscricoes.find((x) => x.id === view.id);
        if (!ins) return null;
        return (
          <div className="space-y-4">
            <Button variant="ghost" size="sm" onClick={() => setView({ kind: "overview" })}><ArrowLeft className="mr-1 h-4 w-4" /> Voltar</Button>
            <SectionTitle title={`Inscrição INS-${ins.id}`} subtitle={editalNome(ins.editalId)} />
            <div className="grid gap-4 md:grid-cols-2">
              <div className="rounded-xl border bg-card p-5 shadow-card space-y-3">
                <h3 className="font-semibold flex items-center gap-2"><User className="h-4 w-4" /> Dados da inscrição</h3>
                <div className="text-[13px]"><span className="text-muted-foreground">Status:</span> <StatusBadge tone={inscricaoTone(ins.status)}>{inscricaoLabel(ins.status)}</StatusBadge></div>
                <div className="text-[13px]"><span className="text-muted-foreground">Data de envio:</span> {formatData(ins.dataInscricao)}</div>
                <div className="text-[13px]"><span className="text-muted-foreground">Pontuação:</span> {ins.pontuacao}</div>
              </div>
              <div className="rounded-xl border bg-card p-5 shadow-card space-y-3">
                <h3 className="font-semibold flex items-center gap-2"><FileText className="h-4 w-4" /> Edital</h3>
                <div className="text-[13px]"><span className="text-muted-foreground">Programa:</span> {editalNome(ins.editalId)}</div>
                <div className="text-[13px]"><span className="text-muted-foreground">Código:</span> EDT-{ins.editalId}</div>
              </div>
            </div>
            <div className="flex justify-end gap-2">
              <Button variant="outline" onClick={() => setView({ kind: "overview" })}>Voltar</Button>
              {ins.status === "INDEFERIDA" && <Button onClick={() => setView({ kind: "recurso", inscricaoId: ins.id, editalId: ins.editalId })}>Interpor recurso</Button>}
            </div>
          </div>
        );
      })()}
    </AppShell>
  );
}

const steps = [{ key: "elig", label: "Elegibilidade" }, { key: "doc", label: "Documentos" }, { key: "ok", label: "Confirmação" }];

function InscricaoWizard({ step, submitting, onStep, onSubmit, onDone }: {
  step: 0 | 1 | 2; submitting: boolean;
  onStep: (s: 0 | 1 | 2) => void; onSubmit: () => void; onDone: () => void;
}) {
  return (
    <div className="space-y-5">
      <Button variant="ghost" size="sm" onClick={onDone}><ArrowLeft className="mr-1 h-4 w-4" /> Cancelar</Button>
      <Stepper steps={steps} current={step} />
      {step === 0 && (
        <div className="rounded-xl border bg-card p-6 shadow-card">
          <SectionTitle title="Verificação de elegibilidade" />
          <ValidationCallout className="mt-4" tone="info">Confirmo que atendo aos critérios de elegibilidade descritos no edital.</ValidationCallout>
          <div className="mt-4 flex justify-end"><Button onClick={() => onStep(1)}>Avançar</Button></div>
        </div>
      )}
      {step === 1 && (
        <div className="rounded-xl border bg-card p-6 shadow-card">
          <SectionTitle title="Documentação" />
          <div className="mt-4 grid grid-cols-2 gap-4">
            <FormField label="Comprovante de renda" required full><Input type="file" className="h-10" /></FormField>
            <FormField label="Comprovante de residência" required full><Input type="file" className="h-10" /></FormField>
            <FormField label="Declaração socioeconômica" full><Input type="file" className="h-10" /></FormField>
          </div>
          <div className="mt-4 flex justify-end gap-2"><Button variant="outline" onClick={() => onStep(0)}>Voltar</Button><Button disabled={submitting} onClick={onSubmit}>Enviar inscrição</Button></div>
        </div>
      )}
      {step === 2 && (
        <div className="space-y-4">
          <SuccessBanner title="Inscrição registrada!" description="Aguardando análise da Assistência Estudantil." />
          <Button onClick={onDone}>Voltar</Button>
        </div>
      )}
    </div>
  );
}

/* ===== Visão da Assistência Estudantil ===== */

function AssistenciaView({ editais, editalNome, loading }: {
  editais: EditalResumo[];
  editalNome: (id: number) => string;
  loading: boolean;
}) {
  const inscricoesQuery = useTodasInscricoes();
  const inscricoes = inscricoesQuery.data ?? [];

  const criar = useCriarEdital();
  const encerrar = useEncerrarEdital();
  const deferir = useDeferirInscricao();
  const indeferir = useIndeferirInscricao();

  const [showAdd, setShowAdd] = useState(false);
  const [form, setForm] = useState({
    programa: "", descricao: "", vagas: 0,
    prazoInscricaoInicio: "", prazoInscricaoFim: "",
    prazoRecursoInicio: "", prazoRecursoFim: "",
  });
  const [detailEdital, setDetailEdital] = useState<EditalResumo | null>(null);

  const handlePublish = (e: React.FormEvent) => {
    e.preventDefault();
    criar.mutate({
      programa: form.programa,
      descricao: form.descricao || null,
      vagas: form.vagas,
      prazoInscricaoInicio: form.prazoInscricaoInicio,
      prazoInscricaoFim: form.prazoInscricaoFim,
      prazoRecursoInicio: form.prazoRecursoInicio,
      prazoRecursoFim: form.prazoRecursoFim,
      prazoRenovacao: null,
    }, {
      onSuccess: () => {
        setShowAdd(false);
        setForm({ programa: "", descricao: "", vagas: 0, prazoInscricaoInicio: "", prazoInscricaoFim: "", prazoRecursoInicio: "", prazoRecursoFim: "" });
        toast.success("Edital publicado com sucesso!");
      },
      onError: notifyError,
    });
  };

  const handleEncerrar = (id: number) => {
    encerrar.mutate(id, { onSuccess: () => toast.info("Edital encerrado."), onError: notifyError });
  };

  const handleDeferir = (id: number) => {
    deferir.mutate({ inscricaoId: id, pontuacao: 0 }, { onSuccess: () => toast.success("Inscrição deferida!"), onError: notifyError });
  };
  const handleIndeferir = (id: number) => {
    indeferir.mutate(id, { onSuccess: () => toast.error("Inscrição indeferida."), onError: notifyError });
  };

  if (detailEdital) {
    const doEdital = inscricoes.filter((i) => i.editalId === detailEdital.id);
    return (
      <div className="space-y-4">
        <Button variant="ghost" size="sm" onClick={() => setDetailEdital(null)}><ArrowLeft className="mr-1 h-4 w-4" /> Voltar</Button>
        <SectionTitle title={`EDT-${detailEdital.id} — ${detailEdital.programa}`} subtitle={`${detailEdital.vagas} vagas · inscrições até ${formatData(detailEdital.prazoInscricaoFim)}`} />
        {detailEdital.descricao && (
          <div className="rounded-xl border bg-card p-5 shadow-card">
            <h3 className="font-semibold">Sobre o programa</h3>
            <p className="mt-2 text-[13px] text-muted-foreground">{detailEdital.descricao}</p>
          </div>
        )}
        <div className="rounded-xl border bg-card p-5 shadow-card">
          <h3 className="font-semibold">Prazos</h3>
          <div className="mt-3 grid gap-2 text-[13px] text-muted-foreground sm:grid-cols-2">
            <div><span className="text-foreground">Inscrições:</span> {formatData(detailEdital.prazoInscricaoInicio)} a {formatData(detailEdital.prazoInscricaoFim)}</div>
            <div><span className="text-foreground">Recursos:</span> {formatData(detailEdital.prazoRecursoInicio)} a {formatData(detailEdital.prazoRecursoFim)}</div>
          </div>
        </div>
        <div className="rounded-xl border bg-card p-5 shadow-card">
          <h3 className="font-semibold">Estatísticas</h3>
          <StatsRow className="mt-3" stats={[
            { label: "Inscrições recebidas", value: doEdital.length, tone: "info" },
            { label: "Em análise", value: doEdital.filter((i) => i.status === "PENDENTE").length, tone: "warning" },
            { label: "Deferidas", value: doEdital.filter((i) => i.status === "DEFERIDA").length, tone: "success" },
          ]} />
        </div>
        <div className="flex justify-end"><Button variant="outline" onClick={() => setDetailEdital(null)}>Voltar</Button></div>
      </div>
    );
  }

  return (
    <div className="space-y-5">
      <StatsRow stats={[
        { label: "Pedidos aguardando", value: inscricoes.filter((i) => i.status === "PENDENTE").length, tone: "warning" },
        { label: "Recursos", value: inscricoes.filter((i) => i.status === "RECURSO_INTERPOSTO").length, tone: "danger" },
        { label: "Deferidas", value: inscricoes.filter((i) => i.status === "DEFERIDA").length, tone: "success" },
        { label: "Editais vigentes", value: editais.filter(editalAberto).length, tone: "info" },
      ]} />

      <div className="flex items-center justify-between">
        <SectionTitle title="Editais publicados" />
        <Button size="sm" onClick={() => setShowAdd(!showAdd)}><Plus className="mr-1 h-4 w-4" /> Publicar edital</Button>
      </div>

      {showAdd && (
        <form onSubmit={handlePublish} className="rounded-xl border bg-card p-5 shadow-card animate-in fade-in slide-in-from-top-2">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <FormField label="Programa" required><Input value={form.programa} onChange={(e) => setForm({ ...form, programa: e.target.value })} required /></FormField>
            <FormField label="Vagas" required><Input type="number" value={form.vagas} onChange={(e) => setForm({ ...form, vagas: parseInt(e.target.value) || 0 })} required /></FormField>
            <FormField label="Início inscrições" required><Input type="date" value={form.prazoInscricaoInicio} onChange={(e) => setForm({ ...form, prazoInscricaoInicio: e.target.value })} required /></FormField>
            <FormField label="Fim inscrições" required><Input type="date" value={form.prazoInscricaoFim} onChange={(e) => setForm({ ...form, prazoInscricaoFim: e.target.value })} required /></FormField>
            <FormField label="Início recursos" required><Input type="date" value={form.prazoRecursoInicio} onChange={(e) => setForm({ ...form, prazoRecursoInicio: e.target.value })} required /></FormField>
            <FormField label="Fim recursos" required><Input type="date" value={form.prazoRecursoFim} onChange={(e) => setForm({ ...form, prazoRecursoFim: e.target.value })} required /></FormField>
            <FormField label="Descrição" full className="md:col-span-3"><Textarea rows={3} placeholder="Descreva o programa, critérios e informações relevantes para os estudantes." value={form.descricao} onChange={(e) => setForm({ ...form, descricao: e.target.value })} /></FormField>
          </div>
          <div className="mt-4 flex justify-end gap-2">
            <Button type="button" variant="outline" onClick={() => setShowAdd(false)}>Cancelar</Button>
            <Button type="submit" disabled={criar.isPending}>Publicar</Button>
          </div>
        </form>
      )}

      {loading ? (
        <p className="text-sm text-muted-foreground">Carregando editais…</p>
      ) : (
        <DataTable
          columns={[
            { key: "codigo", header: "Código", render: (r) => `EDT-${r.id}` },
            { key: "programa", header: "Edital" },
            { key: "vagas", header: "Vagas", align: "right" },
            { key: "prazo", header: "Prazo", render: (r) => formatData(r.prazoInscricaoFim) },
            { key: "status", header: "Status", render: (r) => <StatusBadge tone={editalAberto(r) ? "success" : "neutral"}>{editalLabel(r)}</StatusBadge> },
            { key: "acoes", header: "", align: "right", render: (r) => (
              <div className="flex justify-end gap-1.5">
                {editalAberto(r) && <RowActionButton tone="danger" onClick={() => handleEncerrar(r.id)}>Encerrar</RowActionButton>}
                <RowActionButton tone="neutral" onClick={() => setDetailEdital(r)}>Detalhes</RowActionButton>
              </div>
            )},
          ]}
          rows={editais}
        />
      )}

      <SectionTitle title="Fila de análise" />
      {inscricoesQuery.isLoading ? (
        <p className="text-sm text-muted-foreground">Carregando inscrições…</p>
      ) : inscricoes.length === 0 ? (
        <ValidationCallout tone="info">Nenhuma inscrição recebida até o momento.</ValidationCallout>
      ) : (
        <DataTable
          columns={[
            { key: "protocolo", header: "Protocolo", render: (r) => `INS-${r.id}` },
            { key: "aluno", header: "Estudante", render: (r) => `Estudante #${r.estudanteId}` },
            { key: "edital", header: "Edital", render: (r) => editalNome(r.editalId) },
            { key: "pontuacao", header: "Pontuação", align: "right" },
            { key: "status", header: "Status", render: (r) => <StatusBadge tone={inscricaoTone(r.status)}>{inscricaoLabel(r.status)}</StatusBadge> },
            { key: "acoes", header: "", align: "right", render: (r) => (
              r.status === "PENDENTE" ? (
                <div className="flex justify-end gap-1.5">
                  <RowActionButton tone="danger" onClick={() => handleIndeferir(r.id)}>Indeferir</RowActionButton>
                  <RowActionButton onClick={() => handleDeferir(r.id)}>Deferir</RowActionButton>
                </div>
              ) : <span className="text-[12px] text-muted-foreground">—</span>
            )},
          ]}
          rows={inscricoes}
        />
      )}
    </div>
  );
}
