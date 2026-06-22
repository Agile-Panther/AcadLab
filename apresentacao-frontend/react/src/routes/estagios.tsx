import { useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import type { OportunidadeResumo, CandidaturaResumo } from "@/lib/api";
import { api } from "@/lib/api";
import {
  AppShell, SectionTitle, StatsRow, DataTable, StatusBadge, RowActionButton,
  ValidationCallout, SuccessBanner, TabsRow, useProfileSwitcher,
} from "@/components/acadlab";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { ArrowLeft, Briefcase, Clock, Trash2, Search } from "lucide-react";
import { toast } from "sonner";

export const Route = createFileRoute("/estagios")({
  head: () => ({ meta: [{ title: "Estágios e Oportunidades — AcadLab" }] }),
  component: Page,
});

const ESTUDANTE_ID = 1;
const SETOR_ID = 1;

const STATUS_LABEL: Record<string, string> = {
  CADASTRADA: "Rascunho",
  PUBLICADA: "Publicada",
  ENCERRADA: "Encerrada",
};

type Tone = "warning" | "success" | "danger" | "info";
const CAND_STATUS: Record<string, { label: string; tone: Tone }> = {
  EM_ANALISE: { label: "Em análise", tone: "warning" },
  DEFERIDA: { label: "Aprovada", tone: "success" },
  INDEFERIDA: { label: "Indeferida", tone: "danger" },
  CANCELADA: { label: "Cancelada", tone: "info" },
  ENCAMINHADA: { label: "Encaminhada", tone: "success" },
};
const CAND_ATIVA = ["EM_ANALISE", "DEFERIDA", "ENCAMINHADA"];

// O backend guarda só { empresaId, descricao, cargaHorariaTotal }. Convenção da
// descrição (igual ao seed): "Título — Empresa". Derivamos um empresaId do nome.
function parseDescricao(descricao: string): { empresa: string; titulo: string } {
  const partes = (descricao ?? "").split(" — ");
  if (partes.length >= 2) {
    return { empresa: partes[partes.length - 1], titulo: partes.slice(0, -1).join(" — ") };
  }
  return { empresa: "Empresa", titulo: descricao || "Estágio" };
}
function empresaIdDoNome(nome: string): number {
  let h = 0;
  for (let i = 0; i < nome.length; i++) h = (h * 31 + nome.charCodeAt(i)) % 100000;
  return h + 1;
}

type VagaView = { id: number; empresa: string; titulo: string; cargaHoraria: number; status: string };
function toVaga(o: OportunidadeResumo): VagaView {
  const { empresa, titulo } = parseDescricao(o.descricao);
  return { id: o.id, empresa, titulo, cargaHoraria: o.cargaHorariaTotal, status: o.status };
}

function filtrarVagas(vagas: VagaView[], busca: string): VagaView[] {
  const t = busca.trim().toLowerCase();
  return t ? vagas.filter((v) => v.empresa.toLowerCase().includes(t) || v.titulo.toLowerCase().includes(t)) : vagas;
}

function CampoBusca({ value, onChange }: { value: string; onChange: (v: string) => void }) {
  return (
    <div className="relative">
      <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
      <Input
        value={value}
        onChange={(e) => onChange(e.target.value)}
        placeholder="Buscar por empresa ou vaga..."
        className="h-10 rounded-lg pl-9"
      />
    </div>
  );
}

function Page() {
  const queryClient = useQueryClient();
  const { active: perfil } = useProfileSwitcher([
    { value: "estudante", label: "Estudante", description: "Candidata-se a vagas" },
    { value: "setor", label: "Setor de Estágios", description: "Publica oportunidades" },
  ]);

  const { data: oportunidades = [] } = useQuery({
    queryKey: ["oportunidades"],
    queryFn: () => api.oportunidades.listAll(),
    staleTime: 30_000,
    refetchOnWindowFocus: false,
  });
  const vagas = oportunidades.map(toVaga);
  const invalidar = () => queryClient.invalidateQueries({ queryKey: ["oportunidades"] });

  const subtitle = perfil === "setor"
    ? "Setor de Estágios · Publicação de oportunidades"
    : "Vagas publicadas para candidatura";

  return (
    <AppShell title="Centro de Estágios e Oportunidades" subtitle={subtitle}>
      {perfil === "setor"
        ? <SetorView vagas={vagas} onAtualizar={invalidar} />
        : <EstudanteView vagas={vagas} />}
    </AppShell>
  );
}

// ─── Visão do Estudante ───────────────────────────────────────────────────────
type EstView = { kind: "tabs" } | { kind: "detail"; id: number } | { kind: "candidatado" };

function EstudanteView({ vagas }: { vagas: VagaView[] }) {
  const qc = useQueryClient();
  const [tab, setTab] = useState("vagas");
  const [view, setView] = useState<EstView>({ kind: "tabs" });
  const [busca, setBusca] = useState("");
  const vagasFiltradas = filtrarVagas(vagas, busca);

  const { data: candidaturas = [] } = useQuery({
    queryKey: ["candidaturas", "estudante", ESTUDANTE_ID],
    queryFn: () => api.candidaturas.listByEstudante(ESTUDANTE_ID),
    staleTime: 5_000,
  });
  const invalidarCand = () => qc.invalidateQueries({ queryKey: ["candidaturas"] });

  const candidatarMut = useMutation({
    mutationFn: (v: VagaView) => api.oportunidades.candidatar(v.id, ESTUDANTE_ID),
    onSuccess: () => {
      invalidarCand();
      toast.success("Candidatura registrada com sucesso!");
      setView({ kind: "candidatado" });
    },
    onError: (e: Error) => toast.error(e.message || "Erro ao candidatar."),
  });

  const cancelarMut = useMutation({
    mutationFn: (id: number) => api.candidaturas.cancelar(id),
    onSuccess: () => { toast.success("Candidatura cancelada."); invalidarCand(); },
    onError: (e: Error) => toast.error(e.message || "Erro ao cancelar candidatura."),
  });

  const tabs = [
    { value: "vagas", label: "Oportunidades" },
    { value: "minhas", label: "Minhas candidaturas" },
  ];

  if (view.kind === "detail") {
    const v = vagas.find((x) => x.id === view.id);
    if (!v) { setView({ kind: "tabs" }); return null; }
    const jaCandidatado = candidaturas.some((c) => c.oportunidadeId === v.id && CAND_ATIVA.includes(c.status));
    return (
      <div className="space-y-4">
        <Button variant="ghost" size="sm" onClick={() => setView({ kind: "tabs" })}><ArrowLeft className="mr-1 h-4 w-4" /> Oportunidades</Button>
        <div className="rounded-xl border bg-card p-6 shadow-card">
          <p className="text-[12px] text-muted-foreground">{v.empresa} · OP-{v.id}</p>
          <h2 className="mt-1 text-xl font-semibold text-foreground">{v.titulo}</h2>
          <div className="mt-3 flex flex-wrap items-center gap-3 text-[13px] text-muted-foreground">
            <span className="flex items-center gap-1"><Clock className="h-4 w-4" /> {v.cargaHoraria}h totais</span>
            <StatusBadge tone={v.status === "PUBLICADA" ? "success" : "info"}>{STATUS_LABEL[v.status] ?? v.status}</StatusBadge>
          </div>
          <div className="mt-4 flex justify-end gap-2">
            <Button variant="outline" onClick={() => setView({ kind: "tabs" })}>Voltar</Button>
            <Button onClick={() => candidatarMut.mutate(v)} disabled={jaCandidatado || candidatarMut.isPending}>
              {jaCandidatado ? "Já candidatado" : candidatarMut.isPending ? "Enviando…" : "Candidatar-se"}
            </Button>
          </div>
        </div>
      </div>
    );
  }

  if (view.kind === "candidatado") {
    return (
      <div className="space-y-4">
        <SuccessBanner title="Candidatura registrada!" description="Você pode acompanhar em 'Minhas candidaturas'." />
        <Button onClick={() => { setTab("minhas"); setView({ kind: "tabs" }); }}>Ver minhas candidaturas</Button>
      </div>
    );
  }

  return (
    <>
      <TabsRow items={tabs} value={tab} onChange={setTab} className="mb-5" />
      {tab === "vagas" && (
        <div className="space-y-5">
          <StatsRow stats={[
            { label: "Vagas publicadas", value: vagas.length, tone: "success" },
            { label: "Minhas candidaturas", value: candidaturas.length, tone: "info" },
          ]} />
          <CampoBusca value={busca} onChange={setBusca} />
          {vagas.length === 0 ? (
            <ValidationCallout tone="info">Nenhuma vaga publicada no momento.</ValidationCallout>
          ) : vagasFiltradas.length === 0 ? (
            <ValidationCallout tone="info">Nenhuma vaga encontrada para "{busca}".</ValidationCallout>
          ) : (
            <div className="grid gap-3 md:grid-cols-2">
              {vagasFiltradas.map((v) => (
                <div key={v.id} className="rounded-xl border bg-card p-5 shadow-card">
                  <div className="flex items-start justify-between">
                    <div>
                      <p className="text-[12px] text-muted-foreground">{v.empresa}</p>
                      <h3 className="mt-1 font-semibold text-foreground">{v.titulo}</h3>
                    </div>
                    <Briefcase className="h-5 w-5 text-primary" />
                  </div>
                  <div className="mt-3 flex flex-wrap items-center gap-3 text-[12px] text-muted-foreground">
                    <span className="flex items-center gap-1"><Clock className="h-3.5 w-3.5" /> {v.cargaHoraria}h totais</span>
                  </div>
                  <Button className="mt-3 w-full" variant="outline" onClick={() => setView({ kind: "detail", id: v.id })}>Ver detalhes</Button>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
      {tab === "minhas" && (
        candidaturas.length === 0 ? (
          <ValidationCallout tone="info">Você ainda não se candidatou a nenhuma vaga.</ValidationCallout>
        ) : (
          <DataTable
            columns={[
              { key: "vaga", header: "Vaga", render: (c: CandidaturaResumo) => vagas.find((x) => x.id === c.oportunidadeId)?.titulo ?? `OP-${c.oportunidadeId}` },
              { key: "empresa", header: "Empresa", render: (c: CandidaturaResumo) => vagas.find((x) => x.id === c.oportunidadeId)?.empresa ?? "—" },
              { key: "status", header: "Status", render: (c: CandidaturaResumo) => {
                const s = CAND_STATUS[c.status] ?? { label: c.status, tone: "info" as Tone };
                return <StatusBadge tone={s.tone}>{s.label}</StatusBadge>;
              }},
              { key: "acoes", header: "", align: "right" as const, render: (c: CandidaturaResumo) => (
                c.status === "EM_ANALISE"
                  ? <RowActionButton tone="danger" onClick={() => cancelarMut.mutate(c.id)}><Trash2 className="mr-1 h-3.5 w-3.5" /> Cancelar</RowActionButton>
                  : <span className="text-[12px] text-muted-foreground">—</span>
              )},
            ]}
            rows={candidaturas}
          />
        )
      )}
    </>
  );
}

// ─── Visão do Setor de Estágios ───────────────────────────────────────────────
function SetorView({ vagas, onAtualizar }: { vagas: VagaView[]; onAtualizar: () => void }) {
  const qc = useQueryClient();
  const [aba, setAba] = useState("vagas");
  const [showForm, setShowForm] = useState(false);
  const [empresa, setEmpresa] = useState("");
  const [titulo, setTitulo] = useState("");
  const [cargaHoraria, setCargaHoraria] = useState(360);
  const [busca, setBusca] = useState("");
  const vagasFiltradas = filtrarVagas(vagas, busca);

  const { data: candidaturas = [] } = useQuery({
    queryKey: ["candidaturas", "todas"],
    queryFn: () => api.candidaturas.listAll(),
    staleTime: 5_000,
  });
  const pendentes = candidaturas.filter((c) => c.status === "EM_ANALISE").length;

  const decidirMut = useMutation({
    mutationFn: ({ id, acao }: { id: number; acao: "deferir" | "indeferir" }) =>
      acao === "deferir" ? api.candidaturas.deferir(id) : api.candidaturas.indeferir(id),
    onSuccess: (_d, v) => {
      toast.success(v.acao === "deferir" ? "Candidatura aprovada." : "Candidatura indeferida.");
      qc.invalidateQueries({ queryKey: ["candidaturas"] });
    },
    onError: (e: Error) => toast.error(e.message || "Erro ao decidir candidatura."),
  });

  const publicarMut = useMutation({
    mutationFn: async () => {
      const id = await api.oportunidades.criar({
        empresaId: empresaIdDoNome(empresa.trim()),
        descricao: `${titulo.trim()} — ${empresa.trim()}`,
        cargaHorariaTotal: cargaHoraria,
      });
      await api.oportunidades.publicar(id, SETOR_ID);
      return id;
    },
    onSuccess: (id) => {
      toast.success(`Vaga OP-${id} publicada com sucesso!`);
      setEmpresa(""); setTitulo(""); setShowForm(false);
      onAtualizar();
    },
    onError: (e: Error) => toast.error(e.message || "Erro ao publicar vaga."),
  });

  const excluirMut = useMutation({
    mutationFn: (id: number) => api.oportunidades.excluir(id),
    onSuccess: () => { toast.success("Vaga apagada."); onAtualizar(); },
    onError: (e: Error) => toast.error(e.message || "Erro ao apagar vaga."),
  });

  const podePublicar = empresa.trim() && titulo.trim() && cargaHoraria > 0;

  return (
    <div className="space-y-5">
      <StatsRow stats={[
        { label: "Vagas publicadas", value: vagas.length, tone: "success" },
        { label: "Candidaturas pendentes", value: pendentes, tone: pendentes > 0 ? "warning" : "success" },
        { label: "Carga horária média", value: vagas.length ? `${Math.round(vagas.reduce((s, v) => s + v.cargaHoraria, 0) / vagas.length)}h` : "—", tone: "info" },
      ]} />
      <TabsRow
        items={[
          { value: "vagas", label: "Vagas publicadas" },
          { value: "candidaturas", label: pendentes > 0 ? `Candidaturas (${pendentes})` : "Candidaturas" },
        ]}
        value={aba}
        onChange={setAba}
        className="mb-1"
      />
      {aba === "candidaturas" && (
        candidaturas.length === 0 ? (
          <ValidationCallout tone="info">Nenhuma candidatura recebida ainda.</ValidationCallout>
        ) : (
          <DataTable
            columns={[
              { key: "id", header: "#", render: (c: CandidaturaResumo) => `#${c.id}` },
              { key: "estudante", header: "Estudante", render: (c: CandidaturaResumo) => `Estudante ${c.estudanteId}` },
              { key: "vaga", header: "Vaga", render: (c: CandidaturaResumo) => vagas.find((v) => v.id === c.oportunidadeId)?.titulo ?? `OP-${c.oportunidadeId}` },
              { key: "empresa", header: "Empresa", render: (c: CandidaturaResumo) => vagas.find((v) => v.id === c.oportunidadeId)?.empresa ?? "—" },
              { key: "status", header: "Status", render: (c: CandidaturaResumo) => {
                const s = CAND_STATUS[c.status] ?? { label: c.status, tone: "info" as Tone };
                return <StatusBadge tone={s.tone}>{s.label}</StatusBadge>;
              }},
              { key: "acoes", header: "", align: "right" as const, render: (c: CandidaturaResumo) => (
                c.status === "EM_ANALISE" ? (
                  <div className="flex justify-end gap-1.5">
                    <RowActionButton tone="danger" onClick={() => decidirMut.mutate({ id: c.id, acao: "indeferir" })}>Indeferir</RowActionButton>
                    <RowActionButton tone="info" onClick={() => decidirMut.mutate({ id: c.id, acao: "deferir" })}>Aprovar</RowActionButton>
                  </div>
                ) : <span className="text-[12px] text-muted-foreground">—</span>
              )},
            ]}
            rows={candidaturas}
          />
        )
      )}
      {aba === "vagas" && (<>
      <div className="flex flex-col gap-2 sm:flex-row sm:items-center">
        <div className="flex-1"><CampoBusca value={busca} onChange={setBusca} /></div>
        <Button onClick={() => setShowForm((p) => !p)}>{showForm ? "Fechar" : "Publicar nova vaga"}</Button>
      </div>
      {showForm && (
        <div className="rounded-xl border bg-card p-5 shadow-card">
          <SectionTitle title="Nova vaga" subtitle="A vaga é publicada e fica imediatamente visível para os estudantes." />
          <div className="mt-3 grid grid-cols-2 gap-3">
            <div className="flex flex-col gap-1">
              <label className="text-[12px] font-medium text-foreground">Empresa *</label>
              <Input className="h-10" value={empresa} onChange={(e) => setEmpresa(e.target.value)} placeholder="Ex.: CESAR" />
            </div>
            <div className="flex flex-col gap-1">
              <label className="text-[12px] font-medium text-foreground">Título da vaga *</label>
              <Input className="h-10" value={titulo} onChange={(e) => setTitulo(e.target.value)} placeholder="Ex.: Estágio em Backend" />
            </div>
            <div className="flex flex-col gap-1">
              <label className="text-[12px] font-medium text-foreground">Carga horária total (h) *</label>
              <Input type="number" min={1} className="h-10" value={cargaHoraria} onChange={(e) => setCargaHoraria(Number(e.target.value))} />
            </div>
          </div>
          <div className="mt-3 flex justify-end gap-2">
            <Button variant="outline" onClick={() => setShowForm(false)}>Cancelar</Button>
            <Button onClick={() => publicarMut.mutate()} disabled={!podePublicar || publicarMut.isPending}>
              {publicarMut.isPending ? "Publicando…" : "Publicar"}
            </Button>
          </div>
        </div>
      )}
      {vagas.length === 0 ? (
        <ValidationCallout tone="info">Nenhuma vaga publicada. Use "Publicar nova vaga" para criar a primeira.</ValidationCallout>
      ) : vagasFiltradas.length === 0 ? (
        <ValidationCallout tone="info">Nenhuma vaga encontrada para "{busca}".</ValidationCallout>
      ) : (
        <DataTable
          columns={[
            { key: "id", header: "Código", render: (r: VagaView) => `OP-${r.id}` },
            { key: "empresa", header: "Empresa" },
            { key: "titulo", header: "Vaga" },
            { key: "ch", header: "CH total", align: "right" as const, render: (r: VagaView) => `${r.cargaHoraria}h` },
            { key: "status", header: "Status", render: (r: VagaView) => (
              <StatusBadge tone={r.status === "PUBLICADA" ? "success" : r.status === "ENCERRADA" ? "info" : "warning"}>{STATUS_LABEL[r.status] ?? r.status}</StatusBadge>
            )},
            { key: "acoes", header: "", align: "right" as const, render: (r: VagaView) => (
              <RowActionButton tone="danger" onClick={() => excluirMut.mutate(r.id)}>
                <Trash2 className="mr-1 h-3.5 w-3.5" /> Apagar
              </RowActionButton>
            )},
          ]}
          rows={vagasFiltradas}
        />
      )}
      </>)}
    </div>
  );
}
