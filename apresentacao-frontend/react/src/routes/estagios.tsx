import { useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import {
  AppShell,
  SectionTitle,
  StatsRow,
  DataTable,
  StatusBadge,
  RowActionButton,
  ActionBar,
  ValidationCallout,
  SuccessBanner,
  TabsRow,
  useProfileSwitcher,
} from "@/components/acadlab";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
} from "@/components/ui/dialog";
import { ArrowLeft, Briefcase, MapPin, Calendar } from "lucide-react";
import { toast } from "sonner";

export const Route = createFileRoute("/estagios")({
  head: () => ({ meta: [{ title: "Estágios e Oportunidades — AcadLab" }] }),
  component: Page,
});

type Vaga = {
  id: string;
  empresa: string;
  titulo: string;
  local: string;
  modalidade: string;
  prazo: string;
  reqs: string[];
};

const vagasIniciais: Vaga[] = [
  {
    id: "OP-2025-101",
    empresa: "TechNova",
    titulo: "Estágio em Desenvolvimento Backend",
    local: "Híbrido — São Paulo",
    modalidade: "30h/sem",
    prazo: "30/03/2025",
    reqs: ["A partir do 5º período", "Java ou Python", "CR ≥ 6,5"],
  },
  {
    id: "OP-2025-102",
    empresa: "DataCorp",
    titulo: "Estágio em Ciência de Dados",
    local: "Remoto",
    modalidade: "20h/sem",
    prazo: "25/03/2025",
    reqs: ["A partir do 6º período", "Python, SQL", "CR ≥ 7,0"],
  },
  {
    id: "OP-2025-103",
    empresa: "FinHub",
    titulo: "Estágio em QA",
    local: "Presencial — Rio de Janeiro",
    modalidade: "30h/sem",
    prazo: "28/03/2025",
    reqs: ["A partir do 4º período"],
  },
];

type Cand = {
  id: string;
  vagaId: string;
  vaga: string;
  empresa: string;
  status: "Em análise" | "Deferida" | "Indeferida" | "Encaminhada";
};

const candidaturasIniciais: Cand[] = [
  {
    id: "CAND-2025-051",
    vagaId: "OP-2025-090",
    vaga: "Estágio Frontend",
    empresa: "WebPro",
    status: "Em análise",
  },
  {
    id: "CAND-2025-040",
    vagaId: "OP-2025-080",
    vaga: "Monitor de Algoritmos",
    empresa: "UFXX",
    status: "Encaminhada",
  },
];

const tabs = [
  { value: "vagas", label: "Oportunidades" },
  { value: "minhas", label: "Minhas candidaturas" },
];

type View = { kind: "tabs" } | { kind: "detail"; id: string } | { kind: "candidatado"; id: string };

function Page() {
  const [tab, setTab] = useState("vagas");
  const [view, setView] = useState<View>({ kind: "tabs" });
  const [candidaturas, setCandidaturas] = useState(candidaturasIniciais);
  const [detalheCand, setDetalheCand] = useState<Cand | null>(null);
  const [vagas] = useState(vagasIniciais);
  const { active: perfil } = useProfileSwitcher([
    { value: "estudante", label: "Estudante", description: "Candidata-se a vagas" },
    { value: "setor", label: "Setor de Estágios", description: "Publica e tria oportunidades" },
  ]);

  const candidatar = (v: Vaga) => {
    const jaExiste = candidaturas.some((c) => c.vagaId === v.id);
    if (jaExiste) {
      toast.warning("Você já se candidatou a esta vaga.");
      return;
    }
    setCandidaturas((p) => [
      {
        id: `CAND-2025-${100 + p.length}`,
        vagaId: v.id,
        vaga: v.titulo,
        empresa: v.empresa,
        status: "Em análise",
      },
      ...p,
    ]);
    toast.success("Candidatura registrada com sucesso!");
    setView({ kind: "candidatado", id: v.id });
  };

  const cancelar = (id: string) => {
    setCandidaturas((p) => p.filter((c) => c.id !== id));
    toast.info("Candidatura cancelada.");
  };

  const subtitle =
    perfil === "setor"
      ? "Setor de Estágios · Gestão de vagas e candidaturas"
      : "Vagas elegíveis ao seu perfil acadêmico";

  const isSetor = perfil === "setor";

  return (
    <AppShell title="Centro de Estágios e Oportunidades" subtitle={subtitle}>
      {view.kind === "tabs" && !isSetor && (
        <>
          <TabsRow items={tabs} value={tab} onChange={setTab} className="mb-5" />
          {tab === "vagas" && (
            <div className="space-y-5">
              <StatsRow
                stats={[
                  { label: "Vagas elegíveis", value: vagas.length, tone: "success" },
                  { label: "Minhas candidaturas", value: candidaturas.length, tone: "info" },
                  {
                    label: "Em análise",
                    value: candidaturas.filter((c) => c.status === "Em análise").length,
                    tone: "warning",
                  },
                  {
                    label: "Encaminhadas",
                    value: candidaturas.filter((c) => c.status === "Encaminhada").length,
                    tone: "success",
                  },
                ]}
              />
              <ActionBar searchPlaceholder="Buscar por empresa, área..." />
              <div className="grid gap-3 md:grid-cols-2">
                {vagas.map((v) => (
                  <div key={v.id} className="rounded-xl border bg-card p-5 shadow-card">
                    <div className="flex items-start justify-between">
                      <div>
                        <p className="text-[12px] text-muted-foreground">{v.empresa}</p>
                        <h3 className="mt-1 font-semibold text-foreground">{v.titulo}</h3>
                      </div>
                      <Briefcase className="h-5 w-5 text-primary" />
                    </div>
                    <div className="mt-3 flex flex-wrap items-center gap-3 text-[12px] text-muted-foreground">
                      <span className="flex items-center gap-1">
                        <MapPin className="h-3.5 w-3.5" /> {v.local}
                      </span>
                      <span>{v.modalidade}</span>
                      <span className="flex items-center gap-1">
                        <Calendar className="h-3.5 w-3.5" /> até {v.prazo}
                      </span>
                    </div>
                    <Button
                      className="mt-3 w-full"
                      variant="outline"
                      onClick={() => setView({ kind: "detail", id: v.id })}
                    >
                      Ver detalhes
                    </Button>
                  </div>
                ))}
              </div>
            </div>
          )}
          {tab === "minhas" && (
            <DataTable
              columns={[
                { key: "id", header: "Protocolo" },
                { key: "vaga", header: "Vaga" },
                { key: "empresa", header: "Empresa" },
                {
                  key: "status",
                  header: "Status",
                  render: (r) => (
                    <StatusBadge
                      tone={
                        r.status === "Deferida" || r.status === "Encaminhada"
                          ? "success"
                          : r.status === "Indeferida"
                            ? "danger"
                            : "warning"
                      }
                    >
                      {r.status}
                    </StatusBadge>
                  ),
                },
                {
                  key: "acoes",
                  header: "",
                  align: "right",
                  render: (r) =>
                    r.status === "Em análise" ? (
                      <RowActionButton tone="danger" onClick={() => cancelar(r.id)}>
                        Cancelar
                      </RowActionButton>
                    ) : (
                      <RowActionButton tone="neutral" onClick={() => setDetalheCand(r)}>
                        Detalhes
                      </RowActionButton>
                    ),
                },
              ]}
              rows={candidaturas}
            />
          )}
        </>
      )}

      {view.kind === "tabs" && isSetor && <SetorView />}

      {view.kind === "detail" &&
        (() => {
          const v = vagas.find((x) => x.id === view.id)!;
          return (
            <div className="space-y-4">
              <Button variant="ghost" size="sm" onClick={() => setView({ kind: "tabs" })}>
                <ArrowLeft className="mr-1 h-4 w-4" /> Oportunidades
              </Button>
              <div className="rounded-xl border bg-card p-6 shadow-card">
                <p className="text-[12px] text-muted-foreground">
                  {v.empresa} · {v.id}
                </p>
                <h2 className="mt-1 text-xl font-semibold text-foreground">{v.titulo}</h2>
                <div className="mt-3 flex flex-wrap items-center gap-3 text-[13px] text-muted-foreground">
                  <span className="flex items-center gap-1">
                    <MapPin className="h-4 w-4" /> {v.local}
                  </span>
                  <span>{v.modalidade}</span>
                  <span className="flex items-center gap-1">
                    <Calendar className="h-4 w-4" /> até {v.prazo}
                  </span>
                </div>
                <div className="mt-5">
                  <p className="text-[13px] font-semibold">Requisitos</p>
                  <ul className="mt-1 list-disc pl-5 text-[13px] text-muted-foreground">
                    {v.reqs.map((r) => (
                      <li key={r}>{r}</li>
                    ))}
                  </ul>
                </div>
                <ValidationCallout className="mt-4" tone="info">
                  Você atende a todos os requisitos desta vaga.
                </ValidationCallout>
                <div className="mt-4 flex justify-end gap-2">
                  <Button variant="outline" onClick={() => setView({ kind: "tabs" })}>
                    Voltar
                  </Button>
                  <Button onClick={() => candidatar(v)}>Candidatar-se</Button>
                </div>
              </div>
            </div>
          );
        })()}

      {view.kind === "candidatado" && (
        <div className="space-y-4">
          <SuccessBanner
            title="Candidatura registrada!"
            description="Você pode acompanhar o status em 'Minhas candidaturas'."
          />
          <Button
            onClick={() => {
              setTab("minhas");
              setView({ kind: "tabs" });
            }}
          >
            Ver minhas candidaturas
          </Button>
        </div>
      )}
      <CandidaturaDetalheDialog cand={detalheCand} onClose={() => setDetalheCand(null)} />
    </AppShell>
  );
}

function CandidaturaDetalheDialog({ cand, onClose }: { cand: Cand | null; onClose: () => void }) {
  return (
    <Dialog open={!!cand} onOpenChange={(o) => !o && onClose()}>
      <DialogContent className="max-w-lg">
        {cand && (
          <>
            <DialogHeader>
              <DialogTitle>{cand.id}</DialogTitle>
              <DialogDescription>
                {cand.vaga} · {cand.empresa}
              </DialogDescription>
            </DialogHeader>
            <div className="space-y-3 text-[13px]">
              <div className="flex justify-between border-b py-2">
                <span className="text-muted-foreground">Status</span>
                <StatusBadge
                  tone={
                    cand.status === "Encaminhada" || cand.status === "Deferida"
                      ? "success"
                      : cand.status === "Indeferida"
                        ? "danger"
                        : "warning"
                  }
                >
                  {cand.status}
                </StatusBadge>
              </div>
              <div className="flex justify-between border-b py-2">
                <span className="text-muted-foreground">Vaga (protocolo)</span>
                <span className="font-medium">{cand.vagaId}</span>
              </div>
              <SectionTitle title="Linha do tempo" />
              <ul className="space-y-2 text-muted-foreground">
                <li>• 02/03/2025 — Candidatura registrada</li>
                <li>• 05/03/2025 — Triagem do Setor de Estágios</li>
                {cand.status === "Encaminhada" && (
                  <li className="text-success">• 10/03/2025 — Encaminhada à empresa</li>
                )}
                {cand.status === "Indeferida" && (
                  <li className="text-destructive">• 10/03/2025 — Indeferida pelo setor</li>
                )}
              </ul>
            </div>
            <DialogFooter>
              <Button variant="outline" onClick={onClose}>
                Fechar
              </Button>
              <Button
                onClick={() => {
                  toast.success(`Empresa ${cand.empresa} contatada.`);
                  onClose();
                }}
              >
                Contatar empresa
              </Button>
            </DialogFooter>
          </>
        )}
      </DialogContent>
    </Dialog>
  );
}

type VagaAdmin = {
  id: string;
  empresa: string;
  titulo: string;
  pub: string;
  status: "Publicada" | "Rascunho" | "Encerrada";
  candidatos: number;
  local: string;
  modalidade: string;
  prazo: string;
};
type CandFila = {
  id: string;
  aluno: string;
  vaga: string;
  cr: string;
  status: "Em análise" | "Encaminhada" | "Indeferida";
};

const vagasAdminIniciais: VagaAdmin[] = [
  {
    id: "OP-2025-101",
    empresa: "TechNova",
    titulo: "Estágio em Desenvolvimento Backend",
    pub: "12/03/2025",
    status: "Publicada",
    candidatos: 14,
    local: "Híbrido — São Paulo",
    modalidade: "30h/sem",
    prazo: "30/03/2025",
  },
  {
    id: "OP-2025-102",
    empresa: "DataCorp",
    titulo: "Estágio em Ciência de Dados",
    pub: "10/03/2025",
    status: "Publicada",
    candidatos: 22,
    local: "Remoto",
    modalidade: "20h/sem",
    prazo: "25/03/2025",
  },
  {
    id: "OP-2025-103",
    empresa: "FinHub",
    titulo: "Estágio em QA",
    pub: "08/03/2025",
    status: "Publicada",
    candidatos: 6,
    local: "Presencial — Rio de Janeiro",
    modalidade: "30h/sem",
    prazo: "28/03/2025",
  },
  {
    id: "OP-2025-098",
    empresa: "RetailX",
    titulo: "Estágio em Marketing",
    pub: "—",
    status: "Rascunho",
    candidatos: 0,
    local: "Híbrido — Belo Horizonte",
    modalidade: "20h/sem",
    prazo: "15/04/2025",
  },
  {
    id: "OP-2025-090",
    empresa: "WebPro",
    titulo: "Estágio Frontend",
    pub: "01/02/2025",
    status: "Encerrada",
    candidatos: 31,
    local: "Remoto",
    modalidade: "30h/sem",
    prazo: "20/02/2025",
  },
];

const candFilaIniciais: CandFila[] = [
  {
    id: "CAND-2025-061",
    aluno: "Maria Santos",
    vaga: "Estágio em Desenvolvimento Backend",
    cr: "8,4",
    status: "Em análise",
  },
  {
    id: "CAND-2025-062",
    aluno: "Pedro Almeida",
    vaga: "Estágio em Ciência de Dados",
    cr: "7,9",
    status: "Em análise",
  },
  {
    id: "CAND-2025-063",
    aluno: "Júlia Rocha",
    vaga: "Estágio em QA",
    cr: "6,8",
    status: "Em análise",
  },
  {
    id: "CAND-2025-058",
    aluno: "Lucas Pires",
    vaga: "Estágio em Desenvolvimento Backend",
    cr: "8,1",
    status: "Encaminhada",
  },
];

type EncerrarModal = { vagaId: string; motivo: string } | null;

function SetorView() {
  const [aba, setAba] = useState("vagas");
  const [vagasAdmin, setVagasAdmin] = useState(vagasAdminIniciais);
  const [candFila, setCandFila] = useState(candFilaIniciais);
  const [encerrar, setEncerrar] = useState<EncerrarModal>(null);
  const [editar, setEditar] = useState<VagaAdmin | null>(null);
  const [novaEmpresa, setNovaEmpresa] = useState("");
  const [novaTitulo, setNovaTitulo] = useState("");
  const [showNovaForm, setShowNovaForm] = useState(false);

  const publicarVaga = () => {
    if (!novaEmpresa.trim() || !novaTitulo.trim()) {
      toast.error("Preencha empresa e título para publicar.");
      return;
    }
    const id = `OP-2025-${110 + vagasAdmin.length}`;
    setVagasAdmin((p) => [
      {
        id,
        empresa: novaEmpresa,
        titulo: novaTitulo,
        pub: new Date().toLocaleDateString("pt-BR"),
        status: "Publicada",
        candidatos: 0,
        local: "A definir",
        modalidade: "20h/sem",
        prazo: "—",
      },
      ...p,
    ]);
    toast.success(`Vaga ${id} publicada com sucesso!`);
    setNovaEmpresa("");
    setNovaTitulo("");
    setShowNovaForm(false);
  };

  const encerrarVaga = (vagaId: string, motivo: string) => {
    if (!motivo) {
      toast.error("Selecione o motivo do encerramento.");
      return;
    }
    setVagasAdmin((p) => p.map((v) => (v.id === vagaId ? { ...v, status: "Encerrada" } : v)));
    toast.success(`Vaga encerrada · Motivo: ${motivo}`);
    setEncerrar(null);
  };

  const salvarEdicao = () => {
    if (!editar) return;
    if (!editar.empresa.trim() || !editar.titulo.trim()) {
      toast.error("Empresa e título são obrigatórios.");
      return;
    }
    setVagasAdmin((p) => p.map((v) => (v.id === editar.id ? editar : v)));
    toast.success(`Vaga ${editar.id} atualizada.`);
    setEditar(null);
  };

  const publicarRascunho = (id: string) => {
    setVagasAdmin((p) =>
      p.map((v) =>
        v.id === id
          ? {
              ...v,
              status: "Publicada",
              pub: v.pub === "—" ? new Date().toLocaleDateString("pt-BR") : v.pub,
            }
          : v,
      ),
    );
    toast.success(`Vaga ${id} publicada.`);
  };

  const reabrirVaga = (id: string) => {
    setVagasAdmin((p) => p.map((v) => (v.id === id ? { ...v, status: "Publicada" } : v)));
    toast.success(`Vaga ${id} reaberta.`);
  };

  const encaminhar = (id: string, aluno: string) => {
    setCandFila((p) => p.map((c) => (c.id === id ? { ...c, status: "Encaminhada" } : c)));
    toast.success(`Candidatura de ${aluno} encaminhada à empresa.`);
  };

  const indeferir = (id: string, aluno: string) => {
    setCandFila((p) => p.map((c) => (c.id === id ? { ...c, status: "Indeferida" } : c)));
    toast.error(`Candidatura de ${aluno} indeferida.`);
  };

  return (
    <>
      <TabsRow
        items={[
          { value: "vagas", label: "Vagas publicadas" },
          { value: "fila", label: "Pipeline de candidatos" },
        ]}
        value={aba}
        onChange={setAba}
        className="mb-5"
      />
      {aba === "vagas" && (
        <div className="space-y-5">
          <StatsRow
            stats={[
              {
                label: "Vagas publicadas",
                value: vagasAdmin.filter((v) => v.status === "Publicada").length,
                tone: "success",
              },
              {
                label: "Rascunhos",
                value: vagasAdmin.filter((v) => v.status === "Rascunho").length,
                tone: "warning",
              },
              {
                label: "Candidatos totais",
                value: vagasAdmin.reduce((s, v) => s + v.candidatos, 0),
                tone: "info",
              },
              {
                label: "Encerradas no ano",
                value: vagasAdmin.filter((v) => v.status === "Encerrada").length,
                tone: "info",
              },
            ]}
          />
          <ActionBar
            searchPlaceholder="Buscar por empresa ou título..."
            primaryLabel="Publicar nova vaga"
            onPrimary={() => setShowNovaForm((p) => !p)}
          />
          {showNovaForm && (
            <div className="rounded-xl border bg-card p-5 shadow-card">
              <SectionTitle title="Nova vaga" />
              <div className="mt-3 grid grid-cols-2 gap-3">
                <div className="flex flex-col gap-1">
                  <label className="text-[12px] font-medium text-foreground">Empresa *</label>
                  <Input
                    className="h-10"
                    value={novaEmpresa}
                    onChange={(e) => setNovaEmpresa(e.target.value)}
                    placeholder="Nome da empresa"
                  />
                </div>
                <div className="flex flex-col gap-1">
                  <label className="text-[12px] font-medium text-foreground">
                    Título da vaga *
                  </label>
                  <Input
                    className="h-10"
                    value={novaTitulo}
                    onChange={(e) => setNovaTitulo(e.target.value)}
                    placeholder="Ex.: Estágio em Backend"
                  />
                </div>
              </div>
              <div className="mt-3 flex justify-end gap-2">
                <Button variant="outline" onClick={() => setShowNovaForm(false)}>
                  Cancelar
                </Button>
                <Button onClick={publicarVaga}>Publicar</Button>
              </div>
            </div>
          )}
          <DataTable
            columns={[
              { key: "id", header: "Código" },
              { key: "empresa", header: "Empresa" },
              { key: "titulo", header: "Vaga" },
              { key: "pub", header: "Publicada em" },
              { key: "candidatos", header: "Candidatos", align: "right" },
              {
                key: "status",
                header: "Status",
                render: (r) => (
                  <StatusBadge
                    tone={
                      r.status === "Publicada"
                        ? "success"
                        : r.status === "Rascunho"
                          ? "warning"
                          : "info"
                    }
                  >
                    {r.status}
                  </StatusBadge>
                ),
              },
              {
                key: "acoes",
                header: "",
                align: "right",
                render: (r) => (
                  <div className="flex justify-end gap-1.5">
                    {r.status === "Publicada" && (
                      <RowActionButton
                        tone="danger"
                        onClick={() => setEncerrar({ vagaId: r.id, motivo: "" })}
                      >
                        Encerrar
                      </RowActionButton>
                    )}
                    {r.status === "Rascunho" && (
                      <RowActionButton tone="info" onClick={() => publicarRascunho(r.id)}>
                        Publicar
                      </RowActionButton>
                    )}
                    {r.status === "Encerrada" && (
                      <RowActionButton tone="info" onClick={() => reabrirVaga(r.id)}>
                        Reabrir
                      </RowActionButton>
                    )}
                    <RowActionButton onClick={() => setEditar({ ...r })}>Editar</RowActionButton>
                  </div>
                ),
              },
            ]}
            rows={vagasAdmin}
          />
          {encerrar && (
            <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
              <div className="w-full max-w-sm rounded-xl bg-card border p-6 shadow-xl space-y-4">
                <SectionTitle
                  title="Encerrar vaga"
                  subtitle="Selecione o motivo do encerramento."
                />
                <Select onValueChange={(v) => setEncerrar((e) => (e ? { ...e, motivo: v } : e))}>
                  <SelectTrigger className="h-10">
                    <SelectValue placeholder="Motivo do encerramento" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="Prazo vencido">Prazo vencido</SelectItem>
                    <SelectItem value="Vagas preenchidas">Vagas preenchidas</SelectItem>
                    <SelectItem value="Decisão administrativa">Decisão administrativa</SelectItem>
                  </SelectContent>
                </Select>
                <div className="flex justify-end gap-2">
                  <Button variant="outline" onClick={() => setEncerrar(null)}>
                    Cancelar
                  </Button>
                  <Button
                    variant="destructive"
                    onClick={() => encerrarVaga(encerrar.vagaId, encerrar.motivo)}
                  >
                    Confirmar encerramento
                  </Button>
                </div>
              </div>
            </div>
          )}
          {editar && (
            <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
              <div className="w-full max-w-lg rounded-xl bg-card border p-6 shadow-xl space-y-4 max-h-[90vh] overflow-y-auto">
                <SectionTitle
                  title={`Editar vaga · ${editar.id}`}
                  subtitle="Atualize as informações da oportunidade."
                />
                <div className="grid grid-cols-2 gap-3">
                  <div className="flex flex-col gap-1 col-span-2">
                    <label className="text-[12px] font-medium text-foreground">Empresa *</label>
                    <Input
                      className="h-10"
                      value={editar.empresa}
                      onChange={(e) => setEditar({ ...editar, empresa: e.target.value })}
                    />
                  </div>
                  <div className="flex flex-col gap-1 col-span-2">
                    <label className="text-[12px] font-medium text-foreground">Título *</label>
                    <Input
                      className="h-10"
                      value={editar.titulo}
                      onChange={(e) => setEditar({ ...editar, titulo: e.target.value })}
                    />
                  </div>
                  <div className="flex flex-col gap-1 col-span-2">
                    <label className="text-[12px] font-medium text-foreground">Local</label>
                    <Input
                      className="h-10"
                      value={editar.local}
                      onChange={(e) => setEditar({ ...editar, local: e.target.value })}
                      placeholder="Ex.: Remoto, Híbrido — São Paulo"
                    />
                  </div>
                  <div className="flex flex-col gap-1">
                    <label className="text-[12px] font-medium text-foreground">Modalidade</label>
                    <Input
                      className="h-10"
                      value={editar.modalidade}
                      onChange={(e) => setEditar({ ...editar, modalidade: e.target.value })}
                      placeholder="Ex.: 30h/sem"
                    />
                  </div>
                  <div className="flex flex-col gap-1">
                    <label className="text-[12px] font-medium text-foreground">Prazo</label>
                    <Input
                      className="h-10"
                      value={editar.prazo}
                      onChange={(e) => setEditar({ ...editar, prazo: e.target.value })}
                      placeholder="dd/mm/aaaa"
                    />
                  </div>
                  <div className="flex flex-col gap-1 col-span-2">
                    <label className="text-[12px] font-medium text-foreground">Status</label>
                    <Select
                      value={editar.status}
                      onValueChange={(v) =>
                        setEditar({ ...editar, status: v as VagaAdmin["status"] })
                      }
                    >
                      <SelectTrigger className="h-10">
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="Rascunho">Rascunho</SelectItem>
                        <SelectItem value="Publicada">Publicada</SelectItem>
                        <SelectItem value="Encerrada">Encerrada</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                </div>
                <div className="flex justify-end gap-2 pt-2">
                  <Button variant="outline" onClick={() => setEditar(null)}>
                    Cancelar
                  </Button>
                  <Button onClick={salvarEdicao}>Salvar alterações</Button>
                </div>
              </div>
            </div>
          )}
        </div>
      )}
      {aba === "fila" && (
        <FilaCandidatos candFila={candFila} indeferir={indeferir} encaminhar={encaminhar} />
      )}
    </>
  );
}

function FilaCandidatos({
  candFila,
  indeferir,
  encaminhar,
}: {
  candFila: CandFila[];
  indeferir: (id: string, aluno: string) => void;
  encaminhar: (id: string, aluno: string) => void;
}) {
  const [detalhe, setDetalhe] = useState<CandFila | null>(null);
  return (
    <div className="space-y-5">
      <ValidationCallout tone="info">
        Encerramento de vaga deve registrar motivo (vencimento, vagas preenchidas ou decisão
        administrativa).
      </ValidationCallout>
      <DataTable
        columns={[
          { key: "id", header: "Protocolo" },
          { key: "aluno", header: "Aluno" },
          { key: "vaga", header: "Vaga" },
          { key: "cr", header: "CR", align: "right" },
          {
            key: "status",
            header: "Status",
            render: (r) => (
              <StatusBadge
                tone={
                  r.status === "Encaminhada"
                    ? "success"
                    : r.status === "Indeferida"
                      ? "danger"
                      : "warning"
                }
              >
                {r.status}
              </StatusBadge>
            ),
          },
          {
            key: "acoes",
            header: "",
            align: "right",
            render: (r) =>
              r.status === "Em análise" ? (
                <div className="flex justify-end gap-1.5">
                  <RowActionButton tone="neutral" onClick={() => setDetalhe(r)}>
                    Detalhes
                  </RowActionButton>
                  <RowActionButton tone="danger" onClick={() => indeferir(r.id, r.aluno)}>
                    Indeferir
                  </RowActionButton>
                  <RowActionButton onClick={() => encaminhar(r.id, r.aluno)}>
                    Encaminhar
                  </RowActionButton>
                </div>
              ) : (
                <RowActionButton tone="neutral" onClick={() => setDetalhe(r)}>
                  Detalhes
                </RowActionButton>
              ),
          },
        ]}
        rows={candFila}
      />
      <Dialog open={!!detalhe} onOpenChange={(o) => !o && setDetalhe(null)}>
        <DialogContent className="max-w-lg">
          {detalhe && (
            <>
              <DialogHeader>
                <DialogTitle>
                  {detalhe.id} — {detalhe.aluno}
                </DialogTitle>
                <DialogDescription>
                  {detalhe.vaga} · CR {detalhe.cr}
                </DialogDescription>
              </DialogHeader>
              <div className="space-y-2 text-[13px]">
                <div className="flex justify-between border-b py-2">
                  <span className="text-muted-foreground">Status</span>
                  <StatusBadge
                    tone={
                      detalhe.status === "Encaminhada"
                        ? "success"
                        : detalhe.status === "Indeferida"
                          ? "danger"
                          : "warning"
                    }
                  >
                    {detalhe.status}
                  </StatusBadge>
                </div>
                <div className="flex justify-between border-b py-2">
                  <span className="text-muted-foreground">Período do aluno</span>
                  <span className="font-medium">7º semestre</span>
                </div>
                <div className="flex justify-between border-b py-2">
                  <span className="text-muted-foreground">Disciplinas matriculadas</span>
                  <span className="font-medium">5</span>
                </div>
                <SectionTitle title="Histórico no processo" />
                <ul className="space-y-1 text-muted-foreground">
                  <li>• Candidatura recebida</li>
                  <li>• Pré-requisitos verificados ✓</li>
                  <li>• CR compatível com a vaga ✓</li>
                </ul>
              </div>
              <DialogFooter>
                <Button variant="outline" onClick={() => setDetalhe(null)}>
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
