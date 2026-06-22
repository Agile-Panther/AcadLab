import { toast } from "sonner";
import { useMemo, useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import {
  AppShell,
  SectionTitle,
  EmptyHero,
  FormField,
  ValidationCallout,
  StatusBadge,
  SuccessBanner,
  useProfileSwitcher,
  StatsRow,
  DataTable,
  RowActionButton,
} from "@/components/acadlab";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { HeartHandshake, ArrowLeft, Plus, CalendarClock } from "lucide-react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
  DialogDescription,
} from "@/components/ui/dialog";
import { formatData, formatDataHora, agoraParaInput } from "@/lib/format";
import { ApiError } from "@/lib/api";
import {
  useCasosEstudante,
  useCasosResponsavel,
  useCasosAbertos,
  useSolicitarApoio,
  useRealizarTriagem,
  useRegistrarAtendimento,
  useEncerrarCaso,
  useReabrirCaso,
  useAgendar,
  useContestarAgendamento,
  type CasoResumo,
  type StatusCaso,
  type PrioridadeTriagem,
  type StatusAgendamento,
} from "@/lib/apoio";

export const Route = createFileRoute("/psicopedagogico")({
  head: () => ({ meta: [{ title: "Apoio Psicopedagógico — AcadLab" }] }),
  component: Page,
});

/* ===== Mapeamentos ===== */

function statusLabel(s: StatusCaso): string {
  return s === "ABERTO" ? "Em triagem" : s === "EM_ATENDIMENTO" ? "Em acompanhamento" : "Encerrado";
}
function statusTone(s: StatusCaso) {
  return s === "EM_ATENDIMENTO"
    ? ("success" as const)
    : s === "ENCERRADO"
      ? ("neutral" as const)
      : ("warning" as const);
}
function prioridadeLabel(p: PrioridadeTriagem | null): string {
  if (!p) return "—";
  return p === "BAIXA" ? "Baixa" : p === "MEDIA" ? "Média" : p === "ALTA" ? "Alta" : "Urgente";
}
function prioridadeTone(p: PrioridadeTriagem | null) {
  if (p === "ALTA" || p === "URGENTE") return "danger" as const;
  if (p === "MEDIA") return "warning" as const;
  return "info" as const;
}
function agendamentoLabel(s: StatusAgendamento): string {
  return s === "AGENDADO" ? "Agendado" : "Troca solicitada";
}
const motivoDe = (c: CasoResumo) => c.motivo ?? "Apoio psicopedagógico";
const responsavelDe = (c: CasoResumo) =>
  c.responsavelId ? `Psicopedagogo #${c.responsavelId}` : "A definir";

function notifyError(e: unknown) {
  toast.error(e instanceof ApiError ? e.message : "Não foi possível concluir a operação.");
}

type View = "list" | "detail" | "solicitar" | "enviado";

function Page() {
  const { active: perfil } = useProfileSwitcher([
    { value: "estudante", label: "Estudante", description: "Solicita e acompanha atendimento" },
    { value: "psicopedagogo", label: "Psicopedagogo", description: "Tria e conduz casos" },
  ]);
  const subtitle =
    perfil === "psicopedagogo"
      ? "Equipe NAP · Casos sob sua responsabilidade"
      : "Atendimento sigiloso e confidencial";

  return (
    <AppShell title="Apoio Psicopedagógico" subtitle={subtitle}>
      {perfil === "psicopedagogo" ? <PsicoView /> : <EstudanteView />}
    </AppShell>
  );
}

/* ===== Visão do Estudante ===== */

function EstudanteView() {
  const casosQuery = useCasosEstudante();
  const casos = casosQuery.data ?? [];
  const solicitar = useSolicitarApoio();
  const reabrir = useReabrirCaso();
  const contestar = useContestarAgendamento();

  const [view, setView] = useState<View>("list");
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const caso = casos.find((c) => c.id === selectedId) ?? null;

  const [contestando, setContestando] = useState(false);
  const [justificativa, setJustificativa] = useState("");
  const [horarioSugerido, setHorarioSugerido] = useState("");

  const handleContestar = () => {
    if (!caso) return;
    if (!justificativa.trim()) {
      toast.error("Informe o motivo da troca.");
      return;
    }
    contestar.mutate(
      { casoId: caso.id, justificativa, horarioSugerido: horarioSugerido || null },
      {
        onSuccess: () => {
          toast.success("Pedido de troca de horário enviado.");
          setContestando(false);
          setJustificativa("");
          setHorarioSugerido("");
        },
        onError: notifyError,
      },
    );
  };

  const ativos = casos.filter((c) => c.status !== "ENCERRADO");
  const encerrados = casos.filter((c) => c.status === "ENCERRADO");

  const [tema, setTema] = useState("");
  const [urgencia, setUrgencia] = useState("");
  const [descricao, setDescricao] = useState("");
  const [horarios, setHorarios] = useState("");
  const resetForm = () => {
    setTema("");
    setUrgencia("");
    setDescricao("");
    setHorarios("");
  };

  const handleEnviar = () => {
    if (!tema || !urgencia || !descricao) {
      toast.error("Preencha tema, urgência e descrição.");
      return;
    }
    const texto = `${tema} — ${descricao}${urgencia ? ` (urgência: ${urgencia})` : ""}${horarios ? ` · horários: ${horarios}` : ""}`;
    solicitar.mutate(texto, {
      onSuccess: () => {
        toast.success("Solicitação enviada para triagem!");
        resetForm();
        setView("enviado");
      },
      onError: notifyError,
    });
  };

  const handleReabrir = () => {
    if (!caso) return;
    reabrir.mutate(caso.id, {
      onSuccess: () => {
        toast.info("Caso reaberto para acompanhamento.");
        setView("list");
        setSelectedId(null);
      },
      onError: notifyError,
    });
  };

  if (view === "list") {
    return (
      <div className="space-y-5">
        <div className="flex flex-wrap items-end justify-between gap-3">
          <SectionTitle title="Meus apoios" subtitle="Visão geral dos atendimentos solicitados" />
          <Button onClick={() => setView("solicitar")}>
            <Plus className="mr-1 h-4 w-4" /> Solicitar novo apoio
          </Button>
        </div>

        <StatsRow
          stats={[
            { label: "Apoios ativos", value: ativos.length, tone: "info" },
            {
              label: "Em triagem",
              value: casos.filter((c) => c.status === "ABERTO").length,
              tone: "warning",
            },
            { label: "Encerrados", value: encerrados.length, tone: "info" },
          ]}
        />

        {casosQuery.isLoading ? (
          <p className="text-sm text-muted-foreground">Carregando seus apoios…</p>
        ) : casos.length === 0 ? (
          <EmptyHero
            icon={HeartHandshake}
            title="Você não possui apoios registrados"
            description="Se enfrentar dificuldades acadêmicas, emocionais ou de adaptação, solicite apoio. O atendimento é gratuito e sigiloso."
            actionLabel="Solicitar apoio"
            onAction={() => setView("solicitar")}
          />
        ) : (
          <>
            <div>
              <SectionTitle title="Em andamento" />
              {ativos.length === 0 ? (
                <p className="mt-3 text-sm text-muted-foreground">
                  Nenhum apoio em andamento no momento.
                </p>
              ) : (
                <div className="mt-3 grid gap-3 md:grid-cols-2">
                  {ativos.map((c) => (
                    <button
                      key={c.id}
                      onClick={() => {
                        setSelectedId(c.id);
                        setView("detail");
                      }}
                      className="text-left rounded-xl border bg-card p-5 shadow-card transition hover:border-primary/50 hover:shadow-md"
                    >
                      <div className="flex items-start justify-between gap-3">
                        <div>
                          <p className="text-[12px] text-muted-foreground">PSI-{c.id}</p>
                          <p className="mt-0.5 text-sm font-semibold text-foreground">
                            {motivoDe(c)}
                          </p>
                        </div>
                        <StatusBadge tone={statusTone(c.status)}>
                          {statusLabel(c.status)}
                        </StatusBadge>
                      </div>
                      <div className="mt-3 space-y-1 text-[12px] text-muted-foreground">
                        <p>
                          Aberto em {formatData(c.abertura)} · {responsavelDe(c)}
                        </p>
                        {c.agendamento && (
                          <p className="flex items-center gap-1.5 text-foreground">
                            <CalendarClock className="h-3.5 w-3.5" />
                            Próximo atendimento: {formatDataHora(c.agendamento.dataHora)}
                            {c.agendamento.status === "CONTESTADO" && " · troca solicitada"}
                          </p>
                        )}
                      </div>
                    </button>
                  ))}
                </div>
              )}
            </div>

            {encerrados.length > 0 && (
              <div>
                <SectionTitle title="Histórico" />
                <div className="mt-3 grid gap-3 md:grid-cols-2">
                  {encerrados.map((c) => (
                    <button
                      key={c.id}
                      onClick={() => {
                        setSelectedId(c.id);
                        setView("detail");
                      }}
                      className="text-left rounded-xl border bg-card p-5 shadow-card transition hover:border-primary/50"
                    >
                      <div className="flex items-start justify-between gap-3">
                        <div>
                          <p className="text-[12px] text-muted-foreground">PSI-{c.id}</p>
                          <p className="mt-0.5 text-sm font-semibold text-foreground">
                            {motivoDe(c)}
                          </p>
                        </div>
                        <StatusBadge tone="neutral">{statusLabel(c.status)}</StatusBadge>
                      </div>
                      <p className="mt-3 text-[12px] text-muted-foreground">
                        Aberto em {formatData(c.abertura)} · {responsavelDe(c)}
                      </p>
                    </button>
                  ))}
                </div>
              </div>
            )}
          </>
        )}

        <ValidationCallout tone="info">
          Os registros são sigilosos. Apenas você e a profissional responsável têm acesso.
        </ValidationCallout>
      </div>
    );
  }

  if (view === "detail" && caso) {
    return (
      <div className="space-y-5">
        <Button
          variant="ghost"
          size="sm"
          onClick={() => {
            setView("list");
            setSelectedId(null);
          }}
        >
          <ArrowLeft className="mr-1 h-4 w-4" /> Voltar aos meus apoios
        </Button>
        <div className="flex flex-wrap items-end justify-between gap-3">
          <SectionTitle
            title={motivoDe(caso)}
            subtitle={`PSI-${caso.id} · Aberto em ${formatData(caso.abertura)} · Responsável: ${responsavelDe(caso)}`}
          />
          <StatusBadge tone={statusTone(caso.status)}>{statusLabel(caso.status)}</StatusBadge>
        </div>

        <div className="grid gap-4 lg:grid-cols-3">
          <div className="lg:col-span-2 rounded-xl border bg-card p-5 shadow-card">
            <SectionTitle title="Atendimentos" />
            {caso.atendimentos.length === 0 ? (
              <p className="mt-4 text-[13px] text-muted-foreground">
                {caso.status === "ABERTO"
                  ? "Nenhum atendimento registrado ainda. Seu caso está aguardando triagem."
                  : "Triagem concluída. Aguardando o primeiro atendimento."}
              </p>
            ) : (
              <ol className="mt-4 space-y-3 border-l border-border pl-5">
                {caso.atendimentos.map((t, i) => (
                  <li key={i} className="relative">
                    <span className="absolute -left-[26px] top-1.5 h-2.5 w-2.5 rounded-full bg-primary" />
                    <p className="text-[12px] text-muted-foreground">{formatData(t.data)}</p>
                    <p className="text-[13px] font-medium text-foreground">
                      {t.conclusaoFinal ? "Atendimento final" : "Atendimento realizado"}
                    </p>
                    {t.observacoes && (
                      <p className="text-[12px] text-muted-foreground">{t.observacoes}</p>
                    )}
                    {t.encaminhamento && (
                      <p className="text-[12px] text-muted-foreground">
                        Encaminhamento: {t.encaminhamento}
                      </p>
                    )}
                  </li>
                ))}
              </ol>
            )}
          </div>
          <div className="space-y-3">
            {caso.agendamento && (
              <div className="rounded-xl border bg-card p-5 shadow-card space-y-2">
                <h3 className="font-semibold flex items-center gap-2">
                  <CalendarClock className="h-4 w-4" /> Próximo atendimento
                </h3>
                <p className="text-[13px] text-foreground">
                  {formatDataHora(caso.agendamento.dataHora)}
                </p>
                <StatusBadge tone={caso.agendamento.status === "AGENDADO" ? "success" : "warning"}>
                  {agendamentoLabel(caso.agendamento.status)}
                </StatusBadge>
                {caso.agendamento.status === "CONTESTADO" ? (
                  <p className="text-[12px] text-muted-foreground">
                    Troca solicitada
                    {caso.agendamento.justificativaContestacao
                      ? `: ${caso.agendamento.justificativaContestacao}`
                      : "."}{" "}
                    Aguarde o reagendamento.
                  </p>
                ) : (
                  <Button variant="outline" className="w-full" onClick={() => setContestando(true)}>
                    Solicitar troca de horário
                  </Button>
                )}
              </div>
            )}
            <ValidationCallout tone="info">
              Os registros são sigilosos. Apenas você e a profissional responsável têm acesso.
            </ValidationCallout>
            {caso.status === "ENCERRADO" && (
              <Button className="w-full" disabled={reabrir.isPending} onClick={handleReabrir}>
                Reabrir caso
              </Button>
            )}
          </div>
        </div>

        <Dialog open={contestando} onOpenChange={(o) => !o && setContestando(false)}>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Solicitar troca de horário</DialogTitle>
              <DialogDescription>
                Explique o motivo e, se quiser, sugira um horário melhor para você.
              </DialogDescription>
            </DialogHeader>
            <div className="space-y-4 py-2">
              <FormField label="Motivo" required full>
                <Textarea
                  rows={3}
                  value={justificativa}
                  onChange={(e) => setJustificativa(e.target.value)}
                  placeholder="Ex.: tenho aula nesse horário"
                />
              </FormField>
              <FormField label="Horário sugerido (opcional)" full>
                <Input
                  type="datetime-local"
                  className="h-10"
                  min={agoraParaInput()}
                  value={horarioSugerido}
                  onChange={(e) => setHorarioSugerido(e.target.value)}
                />
              </FormField>
            </div>
            <DialogFooter>
              <Button variant="outline" onClick={() => setContestando(false)}>
                Cancelar
              </Button>
              <Button disabled={contestar.isPending} onClick={handleContestar}>
                Enviar pedido
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </div>
    );
  }

  if (view === "solicitar") {
    return (
      <div className="space-y-4">
        <Button variant="ghost" size="sm" onClick={() => setView("list")}>
          <ArrowLeft className="mr-1 h-4 w-4" /> Voltar
        </Button>
        <div className="rounded-xl border bg-card p-6 shadow-card">
          <SectionTitle
            title="Solicitar apoio psicopedagógico"
            subtitle="Suas informações são confidenciais."
          />
          <div className="mt-4 grid grid-cols-2 gap-4">
            <FormField label="Tema principal" required>
              <Input
                className="h-10"
                placeholder="Ansiedade, adaptação, rendimento..."
                value={tema}
                onChange={(e) => setTema(e.target.value)}
              />
            </FormField>
            <FormField label="Urgência percebida" required>
              <Input
                className="h-10"
                placeholder="Baixa / Média / Alta"
                value={urgencia}
                onChange={(e) => setUrgencia(e.target.value)}
              />
            </FormField>
            <FormField label="Descreva sua demanda" required full>
              <Textarea rows={5} value={descricao} onChange={(e) => setDescricao(e.target.value)} />
            </FormField>
            <FormField label="Horários preferidos" full>
              <Input
                className="h-10"
                placeholder="Ex.: terças e quintas à tarde"
                value={horarios}
                onChange={(e) => setHorarios(e.target.value)}
              />
            </FormField>
          </div>
          <div className="mt-4 flex justify-end gap-2">
            <Button
              variant="outline"
              onClick={() => {
                resetForm();
                setView("list");
              }}
            >
              Cancelar
            </Button>
            <Button disabled={solicitar.isPending} onClick={handleEnviar}>
              Enviar solicitação
            </Button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <SuccessBanner
        title="Solicitação registrada!"
        description="Sua demanda foi encaminhada para triagem. Você receberá retorno em breve."
      />
      <Button
        variant="outline"
        onClick={() => {
          setSelectedId(null);
          setView("list");
        }}
      >
        Voltar aos meus apoios
      </Button>
    </div>
  );
}

/* ===== Visão do Psicopedagogo ===== */

function PsicoView() {
  const responsavelQuery = useCasosResponsavel();
  const abertosQuery = useCasosAbertos();

  // Fila = casos sob responsabilidade + casos abertos aguardando triagem (sem duplicar).
  const fila = useMemo(() => {
    const map = new Map<number, CasoResumo>();
    (responsavelQuery.data ?? []).forEach((c) => map.set(c.id, c));
    (abertosQuery.data ?? []).forEach((c) => map.set(c.id, c));
    return [...map.values()].sort((a, b) => a.id - b.id);
  }, [responsavelQuery.data, abertosQuery.data]);

  const loading = responsavelQuery.isLoading || abertosQuery.isLoading;
  const contestacoes = fila.filter((c) => c.agendamento?.status === "CONTESTADO");

  const triar = useRealizarTriagem();
  const atender = useRegistrarAtendimento();
  const encerrar = useEncerrarCaso();
  const agendar = useAgendar();

  const [triagemCaso, setTriagemCaso] = useState<CasoResumo | null>(null);
  const [prioridade, setPrioridade] = useState<PrioridadeTriagem>("MEDIA");
  const [obsTriagem, setObsTriagem] = useState("");

  const [atendimentoCaso, setAtendimentoCaso] = useState<CasoResumo | null>(null);
  const [obsAtend, setObsAtend] = useState("");
  const [encaminhamento, setEncaminhamento] = useState("");
  const [conclusaoFinal, setConclusaoFinal] = useState(false);

  const [agendarCaso, setAgendarCaso] = useState<CasoResumo | null>(null);
  const [dataHoraAgendar, setDataHoraAgendar] = useState("");

  const [prontuario, setProntuario] = useState<CasoResumo | null>(null);

  const confirmAgendar = () => {
    if (!agendarCaso) return;
    if (!dataHoraAgendar) {
      toast.error("Escolha a data e hora do atendimento.");
      return;
    }
    agendar.mutate(
      { casoId: agendarCaso.id, dataHora: dataHoraAgendar },
      {
        onSuccess: () => {
          toast.success("Horário agendado.");
          setAgendarCaso(null);
          setDataHoraAgendar("");
        },
        onError: notifyError,
      },
    );
  };

  const confirmTriagem = () => {
    if (!triagemCaso) return;
    triar.mutate(
      { casoId: triagemCaso.id, prioridade, observacoes: obsTriagem },
      {
        onSuccess: () => {
          toast.success("Triagem registrada.");
          setTriagemCaso(null);
          setObsTriagem("");
          setPrioridade("MEDIA");
        },
        onError: notifyError,
      },
    );
  };

  const confirmAtendimento = () => {
    if (!atendimentoCaso) return;
    if (!obsAtend.trim()) {
      toast.error("Descreva as observações do atendimento.");
      return;
    }
    atender.mutate(
      {
        casoId: atendimentoCaso.id,
        observacoes: obsAtend,
        encaminhamento: encaminhamento.trim() || null,
        conclusaoFinal,
      },
      {
        onSuccess: () => {
          toast.success("Atendimento registrado.");
          setAtendimentoCaso(null);
          setObsAtend("");
          setEncaminhamento("");
          setConclusaoFinal(false);
        },
        onError: notifyError,
      },
    );
  };

  const handleEncerrar = (c: CasoResumo) => {
    encerrar.mutate(c.id, { onSuccess: () => toast.info("Caso encerrado."), onError: notifyError });
  };

  return (
    <div className="space-y-5">
      <StatsRow
        stats={[
          {
            label: "Casos em triagem",
            value: fila.filter((c) => c.status === "ABERTO").length,
            tone: "warning",
          },
          {
            label: "Em acompanhamento",
            value: fila.filter((c) => c.status === "EM_ATENDIMENTO").length,
            tone: "info",
          },
          {
            label: "Alta prioridade",
            value: fila.filter(
              (c) => c.prioridadeTriagem === "ALTA" || c.prioridadeTriagem === "URGENTE",
            ).length,
            tone: "danger",
          },
          {
            label: "Encerrados",
            value: fila.filter((c) => c.status === "ENCERRADO").length,
            tone: "success",
          },
        ]}
      />
      <ValidationCallout tone="info">
        Visão restrita à equipe NAP. Conteúdos sigilosos; registre apenas anotações pertinentes.
      </ValidationCallout>

      {contestacoes.length > 0 && (
        <div className="rounded-xl border bg-card p-5 shadow-card space-y-3">
          <div className="flex items-center gap-2">
            <SectionTitle
              title="Solicitações de troca de horário"
              subtitle="Alunos pediram remarcação do atendimento"
            />
            <StatusBadge tone="warning">{contestacoes.length}</StatusBadge>
          </div>
          <div className="space-y-2">
            {contestacoes.map((c) => (
              <div
                key={c.id}
                className="flex flex-wrap items-center justify-between gap-3 rounded-lg border bg-subtle p-3"
              >
                <div className="text-[13px]">
                  <p className="font-medium text-foreground">
                    PSI-{c.id} · Estudante #{c.estudanteId} · {motivoDe(c)}
                  </p>
                  <p className="text-[12px] text-muted-foreground">
                    Horário atual: {formatDataHora(c.agendamento!.dataHora)}
                    {c.agendamento!.horarioSugerido
                      ? ` · sugerido pelo aluno: ${formatDataHora(c.agendamento!.horarioSugerido)}`
                      : ""}
                  </p>
                  {c.agendamento!.justificativaContestacao && (
                    <p className="text-[12px] text-muted-foreground">
                      Motivo: {c.agendamento!.justificativaContestacao}
                    </p>
                  )}
                </div>
                <Button
                  size="sm"
                  onClick={() => {
                    setAgendarCaso(c);
                    setDataHoraAgendar(c.agendamento!.horarioSugerido?.slice(0, 16) ?? "");
                  }}
                >
                  Reagendar
                </Button>
              </div>
            ))}
          </div>
        </div>
      )}

      {loading ? (
        <p className="text-sm text-muted-foreground">Carregando casos…</p>
      ) : fila.length === 0 ? (
        <ValidationCallout tone="info">
          Nenhum caso sob sua responsabilidade no momento.
        </ValidationCallout>
      ) : (
        <DataTable
          columns={[
            { key: "protocolo", header: "Protocolo", render: (r) => `PSI-${r.id}` },
            { key: "estudante", header: "Estudante", render: (r) => `Estudante #${r.estudanteId}` },
            { key: "tema", header: "Tema", render: (r) => motivoDe(r) },
            {
              key: "prioridade",
              header: "Prioridade",
              render: (r) => (
                <StatusBadge tone={prioridadeTone(r.prioridadeTriagem)}>
                  {prioridadeLabel(r.prioridadeTriagem)}
                </StatusBadge>
              ),
            },
            { key: "aberta", header: "Aberto em", render: (r) => formatData(r.abertura) },
            {
              key: "status",
              header: "Status",
              render: (r) => (
                <StatusBadge tone={statusTone(r.status)}>{statusLabel(r.status)}</StatusBadge>
              ),
            },
            {
              key: "acoes",
              header: "",
              align: "right",
              render: (r) => (
                <div className="flex justify-end gap-1.5">
                  {r.status === "ABERTO" && !r.prioridadeTriagem && (
                    <RowActionButton onClick={() => setTriagemCaso(r)}>Triar</RowActionButton>
                  )}
                  {r.status !== "ENCERRADO" &&
                    (!!r.prioridadeTriagem || r.status === "EM_ATENDIMENTO") && (
                      <RowActionButton tone="info" onClick={() => setAtendimentoCaso(r)}>
                        Atender
                      </RowActionButton>
                    )}
                  {r.status !== "ENCERRADO" && (
                    <RowActionButton
                      tone="neutral"
                      onClick={() => {
                        setAgendarCaso(r);
                        setDataHoraAgendar("");
                      }}
                    >
                      {r.agendamento ? "Reagendar" : "Agendar"}
                    </RowActionButton>
                  )}
                  {r.status === "EM_ATENDIMENTO" && (
                    <RowActionButton tone="danger" onClick={() => handleEncerrar(r)}>
                      Encerrar
                    </RowActionButton>
                  )}
                  <RowActionButton tone="neutral" onClick={() => setProntuario(r)}>
                    Prontuário
                  </RowActionButton>
                </div>
              ),
            },
          ]}
          rows={fila}
        />
      )}

      {/* Dialog: Triagem */}
      <Dialog open={!!triagemCaso} onOpenChange={(o) => !o && setTriagemCaso(null)}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Realizar triagem</DialogTitle>
            <DialogDescription>
              {triagemCaso && `PSI-${triagemCaso.id} · ${motivoDe(triagemCaso)}`}
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-2">
            <FormField label="Prioridade" full>
              <select
                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
                value={prioridade}
                onChange={(e) => setPrioridade(e.target.value as PrioridadeTriagem)}
              >
                <option value="BAIXA">Baixa</option>
                <option value="MEDIA">Média</option>
                <option value="ALTA">Alta</option>
                <option value="URGENTE">Urgente</option>
              </select>
            </FormField>
            <FormField label="Observações" full>
              <Textarea
                rows={3}
                value={obsTriagem}
                onChange={(e) => setObsTriagem(e.target.value)}
                placeholder="Anotações da triagem..."
              />
            </FormField>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setTriagemCaso(null)}>
              Cancelar
            </Button>
            <Button disabled={triar.isPending} onClick={confirmTriagem}>
              Confirmar triagem
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Dialog: Atendimento */}
      <Dialog open={!!atendimentoCaso} onOpenChange={(o) => !o && setAtendimentoCaso(null)}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Registrar atendimento</DialogTitle>
            <DialogDescription>
              {atendimentoCaso && `PSI-${atendimentoCaso.id} · ${motivoDe(atendimentoCaso)}`}
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-2">
            <FormField label="Observações" required full>
              <Textarea
                rows={3}
                value={obsAtend}
                onChange={(e) => setObsAtend(e.target.value)}
                placeholder="Registro do atendimento..."
              />
            </FormField>
            <FormField label="Encaminhamento (opcional)" full>
              <Input
                className="h-10"
                value={encaminhamento}
                onChange={(e) => setEncaminhamento(e.target.value)}
                placeholder="Ex.: Psicólogo, Pedagogo, Externo"
              />
            </FormField>
            <label className="flex items-center gap-2 text-[13px] text-foreground">
              <input
                type="checkbox"
                checked={conclusaoFinal}
                onChange={(e) => setConclusaoFinal(e.target.checked)}
              />
              Registrar como conclusão final do caso
            </label>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setAtendimentoCaso(null)}>
              Cancelar
            </Button>
            <Button disabled={atender.isPending} onClick={confirmAtendimento}>
              Salvar atendimento
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Dialog: Agendamento */}
      <Dialog open={!!agendarCaso} onOpenChange={(o) => !o && setAgendarCaso(null)}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>
              {agendarCaso?.agendamento ? "Reagendar horário" : "Agendar horário"}
            </DialogTitle>
            <DialogDescription>
              {agendarCaso && `PSI-${agendarCaso.id} · Estudante #${agendarCaso.estudanteId}`}
            </DialogDescription>
          </DialogHeader>
          {agendarCaso?.agendamento?.status === "CONTESTADO" && (
            <ValidationCallout tone="info">
              O aluno pediu troca: {agendarCaso.agendamento.justificativaContestacao}
              {agendarCaso.agendamento.horarioSugerido
                ? ` · sugeriu ${formatDataHora(agendarCaso.agendamento.horarioSugerido)}`
                : ""}
            </ValidationCallout>
          )}
          <div className="space-y-4 py-2">
            <FormField label="Data e hora" required full>
              <Input
                type="datetime-local"
                className="h-10"
                min={agoraParaInput()}
                value={dataHoraAgendar}
                onChange={(e) => setDataHoraAgendar(e.target.value)}
              />
            </FormField>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setAgendarCaso(null)}>
              Cancelar
            </Button>
            <Button disabled={agendar.isPending} onClick={confirmAgendar}>
              Confirmar horário
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Dialog: Prontuário */}
      <Dialog open={!!prontuario} onOpenChange={(o) => !o && setProntuario(null)}>
        <DialogContent className="max-w-xl">
          {prontuario && (
            <>
              <DialogHeader>
                <DialogTitle>Prontuário PSI-{prontuario.id}</DialogTitle>
                <DialogDescription>
                  Estudante #{prontuario.estudanteId} · Aberto em {formatData(prontuario.abertura)}
                </DialogDescription>
              </DialogHeader>
              <ValidationCallout tone="info">
                Documento sigiloso. Acesso registrado em log de auditoria do NAP.
              </ValidationCallout>
              <div className="space-y-2 text-[13px]">
                <div className="flex justify-between border-b py-2">
                  <span className="text-muted-foreground">Tema</span>
                  <span className="font-medium">{motivoDe(prontuario)}</span>
                </div>
                <div className="flex justify-between border-b py-2">
                  <span className="text-muted-foreground">Prioridade</span>
                  <StatusBadge tone={prioridadeTone(prontuario.prioridadeTriagem)}>
                    {prioridadeLabel(prontuario.prioridadeTriagem)}
                  </StatusBadge>
                </div>
                <div className="flex justify-between border-b py-2">
                  <span className="text-muted-foreground">Status</span>
                  <StatusBadge tone={statusTone(prontuario.status)}>
                    {statusLabel(prontuario.status)}
                  </StatusBadge>
                </div>
              </div>

              {prontuario.triagemObservacoes && (
                <div className="rounded-lg border bg-subtle p-3">
                  <p className="text-[12px] font-semibold text-muted-foreground">
                    Observações da triagem
                  </p>
                  <p className="mt-1 text-[13px] text-foreground">
                    {prontuario.triagemObservacoes}
                  </p>
                </div>
              )}

              {prontuario.agendamento && (
                <div className="rounded-lg border p-3 flex items-start justify-between gap-3">
                  <div>
                    <p className="text-[12px] font-semibold text-muted-foreground flex items-center gap-1.5">
                      <CalendarClock className="h-3.5 w-3.5" /> Próximo atendimento
                    </p>
                    <p className="mt-1 text-[13px] text-foreground">
                      {formatDataHora(prontuario.agendamento.dataHora)}
                    </p>
                    {prontuario.agendamento.status === "CONTESTADO" && (
                      <p className="mt-1 text-[12px] text-muted-foreground">
                        Troca pedida: {prontuario.agendamento.justificativaContestacao}
                        {prontuario.agendamento.horarioSugerido
                          ? ` · sugeriu ${formatDataHora(prontuario.agendamento.horarioSugerido)}`
                          : ""}
                      </p>
                    )}
                  </div>
                  <StatusBadge
                    tone={prontuario.agendamento.status === "AGENDADO" ? "success" : "warning"}
                  >
                    {agendamentoLabel(prontuario.agendamento.status)}
                  </StatusBadge>
                </div>
              )}

              {prontuario.status !== "ENCERRADO" && (
                <Button
                  variant="outline"
                  className="w-full"
                  onClick={() => {
                    const c = prontuario;
                    setProntuario(null);
                    setAgendarCaso(c);
                    setDataHoraAgendar("");
                  }}
                >
                  {prontuario.agendamento ? "Reagendar horário" : "Agendar horário"}
                </Button>
              )}

              <SectionTitle title="Linha do tempo de atendimentos" />
              {prontuario.atendimentos.length === 0 ? (
                <p className="text-[13px] text-muted-foreground">Nenhum atendimento registrado.</p>
              ) : (
                <ul className="space-y-2 text-[13px] text-muted-foreground">
                  {prontuario.atendimentos.map((a, i) => (
                    <li key={i}>
                      • {formatData(a.data)} — {a.observacoes ?? "Atendimento"}
                      {a.encaminhamento ? ` (encaminhado: ${a.encaminhamento})` : ""}
                      {a.conclusaoFinal ? " · conclusão final" : ""}
                    </li>
                  ))}
                </ul>
              )}
              <DialogFooter>
                <Button variant="outline" onClick={() => setProntuario(null)}>
                  Fechar
                </Button>
              </DialogFooter>
            </>
          )}
        </DialogContent>
      </Dialog>
    </div>
  );
}
