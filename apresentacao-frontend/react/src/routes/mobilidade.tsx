import { toast } from "sonner";
import { useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import type { MobilidadeAcademicaResumo, ItemPlanoResumo } from "@/lib/api";
import {
  AppShell, SectionTitle, EmptyHero, DataTable, StatusBadge, RowActionButton,
  FormField, ValidationCallout, Stepper, SuccessBanner, StatsRow,
  useProfileSwitcher,
} from "@/components/acadlab";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
  Dialog, DialogContent, DialogHeader, DialogTitle,
  DialogDescription, DialogFooter,
} from "@/components/ui/dialog";
import {
  Select, SelectContent, SelectItem, SelectTrigger, SelectValue,
} from "@/components/ui/select";
import { Plane, ArrowLeft, Plus, Trash2 } from "lucide-react";
import { api } from "@/lib/api";

export const Route = createFileRoute("/mobilidade")({
  head: () => ({ meta: [{ title: "Mobilidade Acadêmica — AcadLab" }] }),
  component: Page,
});

const ESTUDANTE_ID = 1;
const COORDENADOR_ID = 1;
const SECRETARIA_ID = 1;
const HOJE = new Date().toISOString().split("T")[0];

const STATUS_LABEL: Record<string, string> = {
  SOLICITADA: "Em análise",
  AUTORIZADA: "Autorizada",
  EM_ANDAMENTO: "Em curso",
  CONCLUIDA: "Concluída",
  CANCELADA: "Cancelada",
};

function statusTone(status: string): "success" | "warning" | "info" | "danger" {
  if (status === "AUTORIZADA" || status === "CONCLUIDA") return "success";
  if (status === "CANCELADA") return "danger";
  if (status === "EM_ANDAMENTO") return "info";
  return "warning";
}

const LOCAL_DISCS = [
  { id: 101, codigo: "CS101", nome: "Cálculo 1", ch: 60 },
  { id: 201, codigo: "ES201", nome: "Engenharia de Software", ch: 60 },
  { id: 301, codigo: "GP306", nome: "Gestão de Projetos", ch: 60 },
  { id: 401, codigo: "SI401", nome: "Sistemas de Informação", ch: 60 },
  { id: 501, codigo: "ES401", nome: "Arq. de Software", ch: 60 },
  { id: 601, codigo: "IA401", nome: "Inteligência Artificial", ch: 60 },
  { id: 701, codigo: "BD402", nome: "Big Data", ch: 60 },
  { id: 801, codigo: "ES501", nome: "Eng. de Req.", ch: 60 },
];

// Sequência de IDs para disciplinas externas (demo)
let extIdSeq = 1001;
function nextExtId() { return extIdSeq++; }

type PlanoItem = {
  disciplinaExternaId: number;
  nomeDisciplinaExterna: string;
  cargaHorariaExterna: number;
  disciplinaEquivalenteId: number;
  cargaHorariaEquivalente: number;
};

type View = "overview" | "wizard" | "comprovantes";

function Page() {
  const queryClient = useQueryClient();

  const { active: perfil } = useProfileSwitcher([
    { value: "estudante", label: "Estudante", description: "Solicita e acompanha intercâmbio" },
    { value: "coordenador", label: "Coord. Mobilidade", description: "Aprova e homologa equivalências" },
    { value: "secretaria", label: "Secretaria", description: "Registra resultados no histórico" },
  ]);

  const { data: mobilidades = [], refetch: refetchMob } = useQuery({
    queryKey: ["mobilidades", "estudante", ESTUDANTE_ID],
    queryFn: () => api.mobilidade.getByEstudante(ESTUDANTE_ID),
    staleTime: 0,
  });

  const mobData: MobilidadeAcademicaResumo | null =
    mobilidades.find((m) => m.status !== "CANCELADA") ?? null;

  const [view, setView] = useState<View>("overview");
  const [step, setStep] = useState<0 | 1 | 2 | 3>(0);

  const cancelarMob = useMutation({
    mutationFn: () =>
      api.mobilidade.solicitarCancelamento(mobData!.id, {
        justificativa: "Cancelamento solicitado pelo estudante.",
        hoje: HOJE,
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["mobilidades"] });
      refetchMob();
      toast.info("Solicitação de cancelamento registrada.");
    },
    onError: (e: Error) => toast.error(e.message || "Erro ao cancelar."),
  });

  const subtitle = perfil === "coordenador"
    ? "Coordenação de Mobilidade · Aprovação de pedidos"
    : perfil === "secretaria"
    ? "Secretaria · Registro de resultados"
    : "Programas de intercâmbio e aproveitamento externo";

  const invalidar = () => {
    queryClient.invalidateQueries({ queryKey: ["mobilidades"] });
    refetchMob();
  };

  if (perfil === "coordenador") {
    return (
      <AppShell title="Mobilidade Acadêmica" subtitle={subtitle}>
        <CoordView onAtualizar={invalidar} />
      </AppShell>
    );
  }

  if (perfil === "secretaria") {
    return (
      <AppShell title="Mobilidade Acadêmica" subtitle={subtitle}>
        <SecretariaView onAtualizar={invalidar} />
      </AppShell>
    );
  }

  return (
    <AppShell title="Mobilidade Acadêmica" subtitle={subtitle}>
      {view === "overview" && !mobData && (
        <EmptyHero
          icon={Plane}
          title="Você ainda não solicitou mobilidade"
          description="Solicite mobilidade acadêmica para cursar disciplinas em outra instituição com aproveitamento posterior."
          actionLabel="Solicitar mobilidade"
          onAction={() => { setView("wizard"); setStep(0); }}
        />
      )}

      {view === "overview" && mobData && (
        <OverviewEstudante
          mob={mobData}
          onAnexar={() => setView("comprovantes")}
          onCancelar={() => cancelarMob.mutate()}
          cancelando={cancelarMob.isPending}
        />
      )}

      {view === "wizard" && (
        <Wizard
          step={step}
          onStep={setStep}
          onCriada={() => { refetchMob(); setView("overview"); }}
          onCancelar={() => { setView("overview"); setStep(0); }}
        />
      )}

      {view === "comprovantes" && mobData && (
        <ComprovantesView
          mob={mobData}
          onVoltar={() => setView("overview")}
          onEnviou={() => { refetchMob(); setView("overview"); }}
        />
      )}
    </AppShell>
  );
}

// ─── Overview do estudante ────────────────────────────────────────────────────
function OverviewEstudante({ mob, onAnexar, onCancelar, cancelando }: {
  mob: MobilidadeAcademicaResumo;
  onAnexar: () => void;
  onCancelar: () => void;
  cancelando: boolean;
}) {
  const label = STATUS_LABEL[mob.status] ?? mob.status;
  const tone = statusTone(mob.status);
  const comProva = mob.plano.filter((i) => i.comprovanteAnexado).length;

  return (
    <div className="space-y-5">
      <StatsRow stats={[
        { label: "Instituição destino", value: mob.instituicaoDestino, tone: "info" },
        { label: "Status", value: label, tone },
        { label: "Disciplinas no plano", value: mob.plano.length, tone: "info" },
        { label: "Comprovantes", value: `${comProva}/${mob.plano.length}`, tone: comProva === mob.plano.length && mob.plano.length > 0 ? "success" : "warning" },
      ]} />

      {mob.plano.length > 0 && (
        <div className="rounded-xl border bg-card p-5 shadow-card">
          <SectionTitle title="Plano de estudos" />
          <DataTable className="mt-3"
            columns={[
              { key: "ext", header: "Disciplina externa", render: (r: ItemPlanoResumo) => r.nomeDisciplinaExterna || `Disc. ${r.disciplinaExternaId}` },
              { key: "chExt", header: "CH ext.", align: "right" as const, render: (r: ItemPlanoResumo) => r.cargaHorariaExterna },
              { key: "equiv", header: "Equivalência local", render: (r: ItemPlanoResumo) => {
                const d = LOCAL_DISCS.find((l) => l.id === r.disciplinaEquivalenteId);
                return d ? `${d.codigo} — ${d.nome}` : `Disc. ${r.disciplinaEquivalenteId}`;
              }},
              { key: "chLocal", header: "CH local", align: "right" as const, render: (r: ItemPlanoResumo) => r.cargaHorariaEquivalente },
              { key: "status", header: "Status", render: (r: ItemPlanoResumo) => (
                <StatusBadge tone={r.resultadoRegistrado ? "success" : r.comprovanteAnexado ? "info" : "warning"}>
                  {r.resultadoRegistrado ? "Resultado registrado" : r.comprovanteAnexado ? "Comprovante anexado" : STATUS_LABEL[r.status] ?? r.status}
                </StatusBadge>
              )},
            ]}
            rows={mob.plano}
          />
        </div>
      )}

      {mob.justificativaCancelamento && (
        <ValidationCallout tone="warning">
          Cancelamento solicitado: <em>{mob.justificativaCancelamento}</em>
        </ValidationCallout>
      )}

      <div className="flex flex-wrap gap-2">
        {mob.status === "AUTORIZADA" && (
          <>
            <Button variant="outline" onClick={onAnexar}>Anexar comprovantes</Button>
            <Button variant="destructive" onClick={onCancelar} disabled={cancelando}>
              {cancelando ? "Cancelando…" : "Cancelar mobilidade"}
            </Button>
          </>
        )}
        {mob.status === "EM_ANDAMENTO" && (
          <Button onClick={onAnexar}>Anexar comprovantes</Button>
        )}
      </div>
    </div>
  );
}

// ─── Wizard de solicitação ────────────────────────────────────────────────────
const wizardSteps = [
  { key: "inst", label: "Instituição" },
  { key: "plano", label: "Plano de estudos" },
  { key: "doc", label: "Documentos" },
  { key: "ok", label: "Envio" },
];

function Wizard({ step, onStep, onCriada, onCancelar }: {
  step: 0 | 1 | 2 | 3;
  onStep: (s: 0 | 1 | 2 | 3) => void;
  onCriada: () => void;
  onCancelar: () => void;
}) {
  const [instituicao, setInstituicao] = useState("");
  const [pais, setPais] = useState("");
  const [mobId, setMobId] = useState<number | null>(null);
  const [planoItems, setPlanoItems] = useState<PlanoItem[]>([
    { disciplinaExternaId: nextExtId(), nomeDisciplinaExterna: "", cargaHorariaExterna: 60, disciplinaEquivalenteId: 201, cargaHorariaEquivalente: 60 },
  ]);

  const solicitarMut = useMutation({
    mutationFn: () => api.mobilidade.solicitar({
      estudanteId: ESTUDANTE_ID,
      instituicaoDestino: pais ? `${instituicao} (${pais})` : instituicao,
    }),
    onSuccess: (id) => { setMobId(id); onStep(1); },
    onError: (e: Error) => toast.error(e.message || "Erro ao solicitar mobilidade."),
  });

  const adicionarPlanoMut = useMutation({
    mutationFn: async (id: number) => {
      const validos = planoItems.filter((p) => p.nomeDisciplinaExterna.trim());
      for (const item of validos) {
        await api.mobilidade.adicionarItemPlano(id, {
          disciplinaExternaId: item.disciplinaExternaId,
          disciplinaEquivalenteId: item.disciplinaEquivalenteId,
          cargaHorariaExterna: item.cargaHorariaExterna,
          cargaHorariaEquivalente: item.cargaHorariaEquivalente,
        });
      }
    },
    onSuccess: () => onStep(2),
    onError: (e: Error) => toast.error(e.message || "Erro ao salvar plano."),
  });

  const finalizarMut = useMutation({
    mutationFn: () => Promise.resolve(),
    onSuccess: () => {
      toast.success("Candidatura enviada! Protocolo: MOB-" + Math.floor(Math.random() * 10000));
      onStep(3);
    },
  });

  const addLinha = () => setPlanoItems((p) => [
    ...p,
    { disciplinaExternaId: nextExtId(), nomeDisciplinaExterna: "", cargaHorariaExterna: 60, disciplinaEquivalenteId: 201, cargaHorariaEquivalente: 60 },
  ]);

  const removeLinha = (idx: number) => setPlanoItems((p) => p.filter((_, i) => i !== idx));

  const updateLinha = (idx: number, patch: Partial<PlanoItem>) =>
    setPlanoItems((p) => p.map((item, i) => i === idx ? { ...item, ...patch } : item));

  return (
    <div className="space-y-5">
      <Button variant="ghost" size="sm" onClick={onCancelar}>
        <ArrowLeft className="mr-1 h-4 w-4" /> Cancelar
      </Button>
      <Stepper steps={wizardSteps} current={step} />

      {step === 0 && (
        <div className="rounded-xl border bg-card p-6 shadow-card">
          <SectionTitle title="Instituição destino" />
          <div className="mt-4 grid grid-cols-2 gap-4">
            <FormField label="Instituição" required>
              <Input className="h-10" value={instituicao} onChange={(e) => setInstituicao(e.target.value)} placeholder="Ex: Universidade do Porto" />
            </FormField>
            <FormField label="País" required>
              <Input className="h-10" value={pais} onChange={(e) => setPais(e.target.value)} placeholder="Ex: Portugal" />
            </FormField>
            <FormField label="Início previsto" required><Input type="date" className="h-10" /></FormField>
            <FormField label="Fim previsto" required><Input type="date" className="h-10" /></FormField>
          </div>
          <div className="mt-4 flex justify-end">
            <Button disabled={!instituicao || solicitarMut.isPending} onClick={() => solicitarMut.mutate()}>
              {solicitarMut.isPending ? "Salvando…" : "Avançar"}
            </Button>
          </div>
        </div>
      )}

      {step === 1 && (
        <div className="rounded-xl border bg-card p-6 shadow-card">
          <SectionTitle title="Plano de estudos" subtitle="Informe as disciplinas externas e suas equivalências locais." />
          <div className="mt-4 space-y-3">
            {planoItems.map((item, idx) => (
              <div key={item.disciplinaExternaId} className="grid grid-cols-[1fr_80px_1fr_auto] gap-3 items-end">
                <FormField label={idx === 0 ? "Disciplina externa" : ""}>
                  <Input className="h-9" placeholder="Ex: Compiladores" value={item.nomeDisciplinaExterna}
                    onChange={(e) => updateLinha(idx, { nomeDisciplinaExterna: e.target.value })} />
                </FormField>
                <FormField label={idx === 0 ? "CH ext." : ""}>
                  <Input type="number" min={1} className="h-9" value={item.cargaHorariaExterna}
                    onChange={(e) => updateLinha(idx, { cargaHorariaExterna: Number(e.target.value) })} />
                </FormField>
                <FormField label={idx === 0 ? "Equivalência local" : ""}>
                  <Select value={String(item.disciplinaEquivalenteId)}
                    onValueChange={(v) => {
                      const d = LOCAL_DISCS.find((l) => l.id === Number(v));
                      updateLinha(idx, { disciplinaEquivalenteId: Number(v), cargaHorariaEquivalente: d?.ch ?? 60 });
                    }}>
                    <SelectTrigger className="h-9"><SelectValue /></SelectTrigger>
                    <SelectContent>
                      {LOCAL_DISCS.map((d) => (
                        <SelectItem key={d.id} value={String(d.id)}>{d.codigo} — {d.nome}</SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </FormField>
                <Button variant="ghost" size="icon" className="h-9 w-9 shrink-0 self-end"
                  onClick={() => removeLinha(idx)} disabled={planoItems.length === 1}>
                  <Trash2 className="h-4 w-4 text-muted-foreground" />
                </Button>
              </div>
            ))}
            <Button variant="outline" size="sm" onClick={addLinha}>
              <Plus className="mr-1 h-4 w-4" />Adicionar disciplina
            </Button>
          </div>
          <ValidationCallout className="mt-3" tone="info">
            Carga horária externa deve ser ≥ à da disciplina local equivalente (RN 3).
          </ValidationCallout>
          <div className="mt-4 flex justify-end gap-2">
            <Button variant="outline" onClick={() => onStep(0)}>Voltar</Button>
            <Button
              disabled={adicionarPlanoMut.isPending || planoItems.every((p) => !p.nomeDisciplinaExterna.trim())}
              onClick={() => mobId !== null && adicionarPlanoMut.mutate(mobId)}>
              {adicionarPlanoMut.isPending ? "Salvando…" : "Avançar"}
            </Button>
          </div>
        </div>
      )}

      {step === 2 && (
        <div className="rounded-xl border bg-card p-6 shadow-card">
          <SectionTitle title="Documentos" />
          <FormField className="mt-3" label="Carta de aceite" required full>
            <Input type="file" className="h-10" />
          </FormField>
          <FormField label="Plano em PDF" full>
            <Input type="file" className="h-10" />
          </FormField>
          <div className="mt-4 flex justify-end gap-2">
            <Button variant="outline" onClick={() => onStep(1)}>Voltar</Button>
            <Button onClick={() => finalizarMut.mutate()} disabled={finalizarMut.isPending}>
              {finalizarMut.isPending ? "Enviando…" : "Enviar"}
            </Button>
          </div>
        </div>
      )}

      {step === 3 && (
        <div className="space-y-4">
          <SuccessBanner title="Solicitação enviada!" description="A coordenação analisará o plano em até 15 dias úteis." />
          <Button onClick={onCriada}>Ver status</Button>
        </div>
      )}
    </div>
  );
}

// ─── Comprovantes ─────────────────────────────────────────────────────────────
function ComprovantesView({ mob, onVoltar, onEnviou }: {
  mob: MobilidadeAcademicaResumo;
  onVoltar: () => void;
  onEnviou: () => void;
}) {
  const anexarMut = useMutation({
    mutationFn: async () => {
      for (const item of mob.plano.filter((i) => !i.comprovanteAnexado)) {
        await api.mobilidade.anexarComprovante(mob.id, item.disciplinaExternaId);
      }
    },
    onSuccess: () => { toast.success("Comprovantes registrados!"); onEnviou(); },
    onError: (e: Error) => toast.error(e.message || "Erro ao enviar comprovantes."),
  });

  return (
    <div className="space-y-4">
      <Button variant="ghost" size="sm" onClick={onVoltar}>
        <ArrowLeft className="mr-1 h-4 w-4" /> Voltar
      </Button>
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Anexar comprovantes de conclusão" subtitle="Necessários para que a secretaria registre os resultados no histórico." />
        <div className="mt-4 grid grid-cols-2 gap-4">
          {mob.plano.map((item) => (
            <FormField key={item.disciplinaExternaId}
              label={`Comprovante — ${item.nomeDisciplinaExterna || `Disc. ${item.disciplinaExternaId}`}`}
              required full>
              <Input type="file" className="h-10" />
            </FormField>
          ))}
          {mob.plano.length === 0 && (
            <p className="text-[13px] text-muted-foreground col-span-2">Nenhuma disciplina no plano.</p>
          )}
        </div>
        <ValidationCallout className="mt-3" tone="info">
          Apenas disciplinas com comprovante e plano autorizado serão registradas no histórico (RN 4 e RN 6).
        </ValidationCallout>
        <div className="mt-4 flex justify-end gap-2">
          <Button variant="outline" onClick={onVoltar}>Cancelar</Button>
          <Button onClick={() => anexarMut.mutate()} disabled={anexarMut.isPending || mob.plano.length === 0}>
            {anexarMut.isPending ? "Enviando…" : "Enviar comprovantes"}
          </Button>
        </div>
      </div>
    </div>
  );
}

// ─── Visão do Coordenador ────────────────────────────────────────────────────
function CoordView({ onAtualizar }: { onAtualizar: () => void }) {
  const { data: todos = [], refetch } = useQuery({
    queryKey: ["mobilidades", "todas"],
    queryFn: () => api.mobilidade.listAll(),
    staleTime: 0,
  });

  const [detalhe, setDetalhe] = useState<MobilidadeAcademicaResumo | null>(null);

  const autorizarMut = useMutation({
    mutationFn: (id: number) => api.mobilidade.autorizar(id, COORDENADOR_ID),
    onSuccess: () => { toast.success("Mobilidade autorizada!"); refetch(); onAtualizar(); },
    onError: (e: Error) => toast.error(e.message || "Erro ao autorizar."),
  });

  const confirmarCancelMut = useMutation({
    mutationFn: (id: number) => api.mobilidade.confirmarCancelamento(id, COORDENADOR_ID),
    onSuccess: () => { toast.success("Cancelamento confirmado."); refetch(); onAtualizar(); },
    onError: (e: Error) => toast.error(e.message || "Erro ao confirmar cancelamento."),
  });

  const emAnalise = todos.filter((m) => m.status === "SOLICITADA").length;
  const autorizadas = todos.filter((m) => m.status === "AUTORIZADA").length;
  const emCurso = todos.filter((m) => m.status === "EM_ANDAMENTO").length;
  const pendCancel = todos.filter((m) => !!m.justificativaCancelamento && m.status !== "CANCELADA").length;

  return (
    <div className="space-y-5">
      <StatsRow stats={[
        { label: "Aguardando análise", value: emAnalise, tone: "warning" },
        { label: "Autorizadas", value: autorizadas, tone: "success" },
        { label: "Em curso", value: emCurso, tone: "info" },
        { label: "Cancelamentos pendentes", value: pendCancel, tone: pendCancel > 0 ? "danger" : "info" },
      ]} />
      {pendCancel > 0 && (
        <ValidationCallout tone="warning">
          Há {pendCancel} solicitação(ões) de cancelamento aguardando sua confirmação.
        </ValidationCallout>
      )}
      <ValidationCallout tone="info">
        Equivalências devem respeitar carga horária ≥ à local e estar previstas na matriz vigente (RN 2 e RN 3).
      </ValidationCallout>
      <DataTable
        columns={[
          { key: "id", header: "ID" },
          { key: "inst", header: "Instituição", render: (r: MobilidadeAcademicaResumo) => r.instituicaoDestino },
          { key: "disc", header: "Disc.", align: "right" as const, render: (r: MobilidadeAcademicaResumo) => r.plano.length },
          { key: "status", header: "Status", render: (r: MobilidadeAcademicaResumo) => (
            <StatusBadge tone={statusTone(r.status)}>{STATUS_LABEL[r.status] ?? r.status}</StatusBadge>
          )},
          { key: "acoes", header: "", align: "right" as const, render: (r: MobilidadeAcademicaResumo) => (
            <div className="flex justify-end gap-1.5">
              <RowActionButton tone="neutral" onClick={() => setDetalhe(r)}>Detalhes</RowActionButton>
              {r.status === "SOLICITADA" && (
                <RowActionButton onClick={() => autorizarMut.mutate(r.id)}>Autorizar</RowActionButton>
              )}
              {r.justificativaCancelamento && r.status !== "CANCELADA" && (
                <RowActionButton tone="danger" onClick={() => confirmarCancelMut.mutate(r.id)}>
                  Confirmar cancelamento
                </RowActionButton>
              )}
            </div>
          )},
        ]}
        rows={todos}
      />
      <DetalheDialog mob={detalhe} onClose={() => setDetalhe(null)} />
    </div>
  );
}

// ─── Visão da Secretaria ──────────────────────────────────────────────────────
function SecretariaView({ onAtualizar }: { onAtualizar: () => void }) {
  const { data: todos = [], refetch } = useQuery({
    queryKey: ["mobilidades", "todas"],
    queryFn: () => api.mobilidade.listAll(),
    staleTime: 0,
  });

  const registrarMut = useMutation({
    mutationFn: ({ mobId, discExtId }: { mobId: number; discExtId: number }) =>
      api.mobilidade.registrarResultado(mobId, discExtId, SECRETARIA_ID),
    onSuccess: () => { toast.success("Resultado registrado no histórico!"); refetch(); onAtualizar(); },
    onError: (e: Error) => toast.error(e.message || "Erro ao registrar resultado."),
  });

  const comCompr = todos.filter((m) =>
    m.plano.some((i) => i.comprovanteAnexado && !i.resultadoRegistrado)
  );

  return (
    <div className="space-y-5">
      <StatsRow stats={[
        { label: "Com comprovantes pendentes", value: comCompr.length, tone: comCompr.length > 0 ? "warning" : "success" },
        { label: "Concluídas", value: todos.filter((m) => m.status === "CONCLUIDA").length, tone: "success" },
        { label: "Total de mobilidades", value: todos.length, tone: "info" },
        { label: "Em curso", value: todos.filter((m) => m.status === "EM_ANDAMENTO").length, tone: "info" },
      ]} />
      <SectionTitle title="Registrar resultados" subtitle="Disciplinas com comprovante anexado prontas para registro no histórico." />
      {comCompr.length === 0 ? (
        <ValidationCallout tone="info">Nenhuma disciplina com comprovante aguardando registro.</ValidationCallout>
      ) : (
        comCompr.map((mob) => (
          <div key={mob.id} className="rounded-xl border bg-card p-4 shadow-card">
            <p className="font-semibold text-[14px] mb-3">{mob.instituicaoDestino}</p>
            <DataTable
              columns={[
                { key: "ext", header: "Disciplina externa", render: (r: ItemPlanoResumo) => r.nomeDisciplinaExterna || `Disc. ${r.disciplinaExternaId}` },
                { key: "equiv", header: "Equivalência", render: (r: ItemPlanoResumo) => {
                  const d = LOCAL_DISCS.find((l) => l.id === r.disciplinaEquivalenteId);
                  return d ? `${d.codigo} — ${d.nome}` : `Disc. ${r.disciplinaEquivalenteId}`;
                }},
                { key: "status", header: "Status", render: (r: ItemPlanoResumo) => (
                  <StatusBadge tone={r.resultadoRegistrado ? "success" : "warning"}>
                    {r.resultadoRegistrado ? "Registrado" : "Comprovante anexado"}
                  </StatusBadge>
                )},
                { key: "acao", header: "", align: "right" as const, render: (r: ItemPlanoResumo) => (
                  !r.resultadoRegistrado && r.comprovanteAnexado ? (
                    <RowActionButton tone="info"
                      onClick={() => registrarMut.mutate({ mobId: mob.id, discExtId: r.disciplinaExternaId })}>
                      Registrar no histórico
                    </RowActionButton>
                  ) : null
                )},
              ]}
              rows={mob.plano.filter((i) => i.comprovanteAnexado)}
            />
          </div>
        ))
      )}
    </div>
  );
}

// ─── Dialog de detalhes ───────────────────────────────────────────────────────
function DetalheDialog({ mob, onClose }: { mob: MobilidadeAcademicaResumo | null; onClose: () => void }) {
  return (
    <Dialog open={!!mob} onOpenChange={(o) => !o && onClose()}>
      <DialogContent className="max-w-2xl">
        {mob && (
          <>
            <DialogHeader>
              <DialogTitle>Mobilidade #{mob.id} — {mob.instituicaoDestino}</DialogTitle>
              <DialogDescription>
                <StatusBadge tone={statusTone(mob.status)}>{STATUS_LABEL[mob.status] ?? mob.status}</StatusBadge>
              </DialogDescription>
            </DialogHeader>
            <SectionTitle title="Plano de estudos" />
            {mob.plano.length === 0 ? (
              <p className="text-[13px] text-muted-foreground">Nenhuma disciplina no plano.</p>
            ) : (
              <DataTable
                columns={[
                  { key: "ext", header: "Externa", render: (r: ItemPlanoResumo) => r.nomeDisciplinaExterna || `Disc. ${r.disciplinaExternaId}` },
                  { key: "ch", header: "CH ext.", align: "right" as const, render: (r: ItemPlanoResumo) => r.cargaHorariaExterna },
                  { key: "equiv", header: "Equivalência", render: (r: ItemPlanoResumo) => {
                    const d = LOCAL_DISCS.find((l) => l.id === r.disciplinaEquivalenteId);
                    return d ? `${d.codigo} — ${d.nome}` : `Disc. ${r.disciplinaEquivalenteId}`;
                  }},
                  { key: "chL", header: "CH local", align: "right" as const, render: (r: ItemPlanoResumo) => r.cargaHorariaEquivalente },
                  { key: "status", header: "Status", render: (r: ItemPlanoResumo) => (
                    <StatusBadge tone={r.status === "AUTORIZADO" ? "success" : "warning"}>{r.status}</StatusBadge>
                  )},
                ]}
                rows={mob.plano}
              />
            )}
            {mob.justificativaCancelamento && (
              <ValidationCallout tone="warning">
                Cancelamento solicitado: <em>{mob.justificativaCancelamento}</em>
              </ValidationCallout>
            )}
            <DialogFooter>
              <Button variant="outline" onClick={onClose}>Fechar</Button>
            </DialogFooter>
          </>
        )}
      </DialogContent>
    </Dialog>
  );
}
