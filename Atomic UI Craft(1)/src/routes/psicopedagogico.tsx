import { toast } from "sonner";
import { useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import {
  AppShell, SectionTitle, EmptyHero, FormField, ValidationCallout,
  StatusBadge, SuccessBanner, useProfileSwitcher,
  StatsRow, DataTable, RowActionButton,
} from "@/components/acadlab";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { HeartHandshake, ArrowLeft, Plus, CalendarClock } from "lucide-react";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter, DialogDescription } from "@/components/ui/dialog";

export const Route = createFileRoute("/psicopedagogico")({
  head: () => ({ meta: [{ title: "Apoio Psicopedagógico — AcadLab" }] }),
  component: Page,
});

type CasoStatus = "Em triagem" | "Em acompanhamento" | "Encerrado" | "Encerramento solicitado";
type Atendimento2 = { data: string; evt: string; obs?: string };
type Caso = {
  id: string;
  status: CasoStatus;
  abertura: string;
  motivo: string;
  responsavel: string;
  proximo?: string;
  atendimentos: Atendimento2[];
};

const casosIniciais: Caso[] = [
  {
    id: "PSI-2025-052",
    status: "Em acompanhamento",
    abertura: "02/03/2025",
    motivo: "Ansiedade relacionada a provas",
    responsavel: "Dra. Helena Costa",
    proximo: "25/03/2025 · 14:00",
    atendimentos: [
      { data: "18/03 14:00", evt: "Atendimento individual realizado", obs: "Foco em técnicas de respiração." },
      { data: "10/03 14:00", evt: "Atendimento individual realizado", obs: "Avaliação inicial." },
      { data: "05/03 09:00", evt: "Triagem concluída", obs: "Classificada como prioridade média." },
      { data: "02/03 10:12", evt: "Solicitação aberta pelo estudante" },
    ],
  },
  {
    id: "PSI-2024-118",
    status: "Encerrado",
    abertura: "10/08/2024",
    motivo: "Adaptação ao primeiro semestre",
    responsavel: "Dra. Helena Costa",
    atendimentos: [
      { data: "20/11 15:00", evt: "Encerramento do caso", obs: "Estudante adaptado, objetivos atingidos." },
      { data: "10/08 09:00", evt: "Solicitação aberta pelo estudante" },
    ],
  },
];

type View = "list" | "detail" | "solicitar" | "enviado";

function Page() {
  const [casos, setCasos] = useState<Caso[]>(casosIniciais);
  const [view, setView] = useState<View>("list");
  const [selectedId, setSelectedId] = useState<string | null>(null);
  const caso = casos.find((c) => c.id === selectedId) ?? null;
  const { active: perfil } = useProfileSwitcher([
    { value: "estudante", label: "Estudante", description: "Solicita e acompanha atendimento" },
    { value: "psicopedagogo", label: "Psicopedagogo", description: "Tria e conduz casos" },
  ]);
  const subtitle = perfil === "psicopedagogo"
    ? "Equipe NAP · Casos sob sua responsabilidade"
    : "Atendimento sigiloso e confidencial";

  const ativos = casos.filter((c) => c.status !== "Encerrado");
  const encerrados = casos.filter((c) => c.status === "Encerrado");

  const [tema, setTema] = useState("");
  const [urgencia, setUrgencia] = useState("");
  const [descricao, setDescricao] = useState("");
  const [horarios, setHorarios] = useState("");

  const resetForm = () => { setTema(""); setUrgencia(""); setDescricao(""); setHorarios(""); };

  const handleEnviar = () => {
    if (!tema || !urgencia || !descricao) {
      toast.error("Preencha tema, urgência e descrição.");
      return;
    }
    const id = "PSI-2025-" + String(Math.floor(Math.random() * 900) + 100);
    const novo: Caso = {
      id,
      status: "Em triagem",
      abertura: new Date().toLocaleDateString("pt-BR"),
      motivo: tema,
      responsavel: "A definir",
      atendimentos: [{ data: new Date().toLocaleDateString("pt-BR"), evt: "Solicitação aberta pelo estudante", obs: descricao }],
    };
    setCasos([novo, ...casos]);
    setSelectedId(id);
    toast.success(`Solicitação enviada! Protocolo: ${id}`);
    resetForm();
    setView("enviado");
  };

  const handleSolicitarEncerramento = () => {
    if (!caso) return;
    setCasos(casos.map((c) => (c.id === caso.id ? { ...c, status: "Encerramento solicitado" } : c)));
    toast.success("Solicitação de encerramento enviada!");
  };

  const handleReabrir = () => {
    if (!caso) return;
    setCasos(casos.map((c) => (c.id === caso.id ? { ...c, status: "Em acompanhamento" } : c)));
    toast.info("Caso reaberto para acompanhamento.");
  };

  const toneStatus = (s: CasoStatus) =>
    s === "Encerrado" ? "neutral" : s === "Em triagem" ? "warning" : s === "Encerramento solicitado" ? "warning" : "success";

  return (
    <AppShell title="Apoio Psicopedagógico" subtitle={subtitle}>
      {perfil === "psicopedagogo" && <PsicoView />}

      {perfil !== "psicopedagogo" && view === "list" && (
        <div className="space-y-5">
          <div className="flex flex-wrap items-end justify-between gap-3">
            <SectionTitle title="Meus apoios" subtitle="Visão geral dos atendimentos solicitados" />
            <Button onClick={() => setView("solicitar")}><Plus className="mr-1 h-4 w-4" /> Solicitar novo apoio</Button>
          </div>

          <StatsRow stats={[
            { label: "Apoios ativos", value: ativos.length, tone: "info" },
            { label: "Em triagem", value: casos.filter((c) => c.status === "Em triagem").length, tone: "warning" },
            { label: "Encerrados", value: encerrados.length, tone: "info" },
          ]} />

          {ativos.length === 0 && encerrados.length === 0 ? (
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
                  <p className="mt-3 text-sm text-muted-foreground">Nenhum apoio em andamento no momento.</p>
                ) : (
                  <div className="mt-3 grid gap-3 md:grid-cols-2">
                    {ativos.map((c) => (
                      <button
                        key={c.id}
                        onClick={() => { setSelectedId(c.id); setView("detail"); }}
                        className="text-left rounded-xl border bg-card p-5 shadow-card transition hover:border-primary/50 hover:shadow-md"
                      >
                        <div className="flex items-start justify-between gap-3">
                          <div>
                            <p className="text-[12px] text-muted-foreground">{c.id}</p>
                            <p className="mt-0.5 text-sm font-semibold text-foreground">{c.motivo}</p>
                          </div>
                          <StatusBadge tone={toneStatus(c.status)}>{c.status}</StatusBadge>
                        </div>
                        <div className="mt-3 space-y-1 text-[12px] text-muted-foreground">
                          <p>Aberto em {c.abertura} · {c.responsavel}</p>
                          {c.proximo && <p className="flex items-center gap-1.5"><CalendarClock className="h-3.5 w-3.5" /> Próximo: {c.proximo}</p>}
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
                        onClick={() => { setSelectedId(c.id); setView("detail"); }}
                        className="text-left rounded-xl border bg-card p-5 shadow-card transition hover:border-primary/50"
                      >
                        <div className="flex items-start justify-between gap-3">
                          <div>
                            <p className="text-[12px] text-muted-foreground">{c.id}</p>
                            <p className="mt-0.5 text-sm font-semibold text-foreground">{c.motivo}</p>
                          </div>
                          <StatusBadge tone="neutral">{c.status}</StatusBadge>
                        </div>
                        <p className="mt-3 text-[12px] text-muted-foreground">Aberto em {c.abertura} · {c.responsavel}</p>
                      </button>
                    ))}
                  </div>
                </div>
              )}
            </>
          )}

          <ValidationCallout tone="info">Os registros são sigilosos. Apenas você e a profissional responsável têm acesso.</ValidationCallout>
        </div>
      )}

      {perfil !== "psicopedagogo" && view === "detail" && caso && (
        <div className="space-y-5">
          <Button variant="ghost" size="sm" onClick={() => { setView("list"); setSelectedId(null); }}><ArrowLeft className="mr-1 h-4 w-4" /> Voltar aos meus apoios</Button>
          <div className="flex flex-wrap items-end justify-between gap-3">
            <SectionTitle title={caso.motivo} subtitle={`${caso.id} · Aberto em ${caso.abertura} · Responsável: ${caso.responsavel}`} />
            <StatusBadge tone={toneStatus(caso.status)}>{caso.status}</StatusBadge>
          </div>

          <div className="grid gap-4 lg:grid-cols-3">
            <div className="lg:col-span-2 rounded-xl border bg-card p-5 shadow-card">
              <SectionTitle title="Atendimentos" />
              <ol className="mt-4 space-y-3 border-l border-border pl-5">
                {caso.atendimentos.map((t, i) => (
                  <li key={i} className="relative">
                    <span className="absolute -left-[26px] top-1.5 h-2.5 w-2.5 rounded-full bg-primary" />
                    <p className="text-[12px] text-muted-foreground">{t.data}</p>
                    <p className="text-[13px] font-medium text-foreground">{t.evt}</p>
                    {t.obs && <p className="text-[12px] text-muted-foreground">{t.obs}</p>}
                  </li>
                ))}
              </ol>
            </div>
            <div className="space-y-3">
              {caso.proximo && (
                <div className="rounded-xl border bg-card p-5 shadow-card">
                  <SectionTitle title="Próximo atendimento" subtitle={caso.proximo} />
                </div>
              )}
              <ValidationCallout tone="info">Os registros são sigilosos. Apenas você e a profissional responsável têm acesso.</ValidationCallout>
              {caso.status === "Em acompanhamento" && (
                <Button variant="outline" className="w-full" onClick={handleSolicitarEncerramento}>Solicitar encerramento</Button>
              )}
              {caso.status === "Encerrado" && (
                <Button className="w-full" onClick={handleReabrir}>Reabrir caso</Button>
              )}
            </div>
          </div>
        </div>
      )}

      {perfil !== "psicopedagogo" && view === "solicitar" && (
        <div className="space-y-4">
          <Button variant="ghost" size="sm" onClick={() => setView("list")}><ArrowLeft className="mr-1 h-4 w-4" /> Voltar</Button>
          <div className="rounded-xl border bg-card p-6 shadow-card">
            <SectionTitle title="Solicitar apoio psicopedagógico" subtitle="Suas informações são confidenciais." />
            <div className="mt-4 grid grid-cols-2 gap-4">
              <FormField label="Tema principal" required><Input className="h-10" placeholder="Ansiedade, adaptação, rendimento..." value={tema} onChange={(e) => setTema(e.target.value)} /></FormField>
              <FormField label="Urgência percebida" required><Input className="h-10" placeholder="Baixa / Média / Alta" value={urgencia} onChange={(e) => setUrgencia(e.target.value)} /></FormField>
              <FormField label="Descreva sua demanda" required full><Textarea rows={5} value={descricao} onChange={(e) => setDescricao(e.target.value)} /></FormField>
              <FormField label="Horários preferidos" full><Input className="h-10" placeholder="Ex.: terças e quintas à tarde" value={horarios} onChange={(e) => setHorarios(e.target.value)} /></FormField>
            </div>
            <div className="mt-4 flex justify-end gap-2">
              <Button variant="outline" onClick={() => { resetForm(); setView("list"); }}>Cancelar</Button>
              <Button onClick={handleEnviar}>Enviar solicitação</Button>
            </div>
          </div>
        </div>
      )}

      {perfil !== "psicopedagogo" && view === "enviado" && (
        <div className="space-y-4">
          <SuccessBanner title="Solicitação registrada!" description="Sua demanda foi encaminhada para triagem. Você receberá retorno em até 5 dias úteis." />
          <div className="flex gap-2">
            <Button onClick={() => setView("detail")}>Ver detalhes do apoio</Button>
            <Button variant="outline" onClick={() => { setSelectedId(null); setView("list"); }}>Voltar aos meus apoios</Button>
          </div>
        </div>
      )}
    </AppShell>
  );
}

type Atendimento = { id: string; aluno: string; tema: string; urgencia: "Baixa" | "Média" | "Alta"; aberta: string; status: "Em triagem" | "Em acompanhamento" | "Encerrado" | "Encaminhado" };

const filaInicial: Atendimento[] = [
  { id: "PSI-2025-061", aluno: "Pedro Almeida", tema: "Ansiedade pré-prova", urgencia: "Alta", aberta: "19/03/2025", status: "Em triagem" },
  { id: "PSI-2025-058", aluno: "Júlia Rocha", tema: "Dificuldade de adaptação", urgencia: "Média", aberta: "17/03/2025", status: "Em triagem" },
  { id: "PSI-2025-052", aluno: "Maria Santos", tema: "Ansiedade relacionada a provas", urgencia: "Média", aberta: "02/03/2025", status: "Em acompanhamento" },
  { id: "PSI-2025-049", aluno: "Lucas Pires", tema: "Rendimento acadêmico", urgencia: "Baixa", aberta: "28/02/2025", status: "Em acompanhamento" },
];

function PsicoView() {
  const [fila, setFila] = useState<Atendimento[]>(filaInicial);
  const [encaminharId, setEncaminharId] = useState<string | null>(null);
  const [destino, setDestino] = useState("Psicólogo");
  const [prontuario, setProntuario] = useState<Atendimento | null>(null);

  const handleAction = (id: string, action: "triar" | "arquivar" | "encaminhar") => {
    if (action === "encaminhar") {
      setEncaminharId(id);
      return;
    }
    setFila(fila.map(f => {
      if (f.id === id) {
        if (action === "triar") {
          toast.success("Caso triado e iniciado acompanhamento.");
          return { ...f, status: "Em acompanhamento" as const };
        }
        if (action === "arquivar") {
          toast.info("Caso arquivado.");
          return { ...f, status: "Encerrado" as const };
        }
      }
      return f;
    }));
  };

  const confirmEncaminhar = () => {
    setFila(fila.map(f => f.id === encaminharId ? { ...f, status: "Encaminhado" as const } : f));
    toast.success(`Caso encaminhado para ${destino}.`);
    setEncaminharId(null);
  };

  return (
    <div className="space-y-5">
      <StatsRow stats={[
        { label: "Casos em triagem", value: fila.filter((f) => f.status === "Em triagem").length, tone: "warning" },
        { label: "Em acompanhamento", value: fila.filter((f) => f.status === "Em acompanhamento").length, tone: "info" },
        { label: "Alta prioridade", value: fila.filter((f) => f.urgencia === "Alta").length, tone: "danger" },
        { label: "Encerrados no mês", value: 4, tone: "success" },
      ]} />
      <ValidationCallout tone="info">Visão restrita à equipe NAP. Conteúdos sigilosos; registre apenas anotações pertinentes.</ValidationCallout>
      <DataTable
        columns={[
          { key: "id", header: "Protocolo" },
          { key: "aluno", header: "Aluno" },
          { key: "tema", header: "Tema" },
          { key: "urgencia", header: "Urgência", render: (r) => (
            <StatusBadge tone={r.urgencia === "Alta" ? "danger" : r.urgencia === "Média" ? "warning" : "info"}>{r.urgencia}</StatusBadge>
          )},
          { key: "aberta", header: "Aberto em" },
          { key: "status", header: "Status", render: (r) => (
            <StatusBadge tone={r.status === "Em acompanhamento" ? "success" : r.status === "Encerrado" ? "neutral" : r.status === "Encaminhado" ? "info" : "warning"}>{r.status}</StatusBadge>
          )},
          { key: "acoes", header: "", align: "right", render: (r) => (
            <div className="flex justify-end gap-1.5">
               {r.status === "Em triagem" ? (
                 <>
                   <RowActionButton onClick={() => handleAction(r.id, "triar")}>Triar</RowActionButton>
                   <RowActionButton tone="info" onClick={() => handleAction(r.id, "encaminhar")}>Encaminhar</RowActionButton>
                   <RowActionButton tone="danger" onClick={() => handleAction(r.id, "arquivar")}>Arquivar</RowActionButton>
                 </>
               ) : <RowActionButton tone="neutral" onClick={() => setProntuario(r)}>Abrir prontuário</RowActionButton>}
            </div>
          )},
        ]}
        rows={fila}
      />

      <Dialog open={!!encaminharId} onOpenChange={(o) => !o && setEncaminharId(null)}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Encaminhar Atendimento</DialogTitle>
            <DialogDescription>Selecione o destino para este encaminhamento.</DialogDescription>
          </DialogHeader>
          <div className="py-4">
            <FormField label="Destino" full>
              <select 
                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                value={destino} 
                onChange={e => setDestino(e.target.value)}
              >
                <option>Psicólogo</option>
                <option>Pedagogo</option>
                <option>Externo</option>
              </select>
            </FormField>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setEncaminharId(null)}>Cancelar</Button>
            <Button onClick={confirmEncaminhar}>Confirmar</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      <Dialog open={!!prontuario} onOpenChange={(o) => !o && setProntuario(null)}>
        <DialogContent className="max-w-xl">
          {prontuario && (
            <>
              <DialogHeader>
                <DialogTitle>Prontuário {prontuario.id}</DialogTitle>
                <DialogDescription>{prontuario.aluno} · Aberto em {prontuario.aberta}</DialogDescription>
              </DialogHeader>
              <ValidationCallout tone="info">Documento sigiloso. Acesso registrado em log de auditoria do NAP.</ValidationCallout>
              <div className="space-y-2 text-[13px]">
                <div className="flex justify-between border-b py-2"><span className="text-muted-foreground">Tema</span><span className="font-medium">{prontuario.tema}</span></div>
                <div className="flex justify-between border-b py-2"><span className="text-muted-foreground">Urgência</span><StatusBadge tone={prontuario.urgencia === "Alta" ? "danger" : prontuario.urgencia === "Média" ? "warning" : "info"}>{prontuario.urgencia}</StatusBadge></div>
                <div className="flex justify-between border-b py-2"><span className="text-muted-foreground">Status</span><StatusBadge tone={prontuario.status === "Em acompanhamento" ? "success" : prontuario.status === "Encerrado" ? "neutral" : prontuario.status === "Encaminhado" ? "info" : "warning"}>{prontuario.status}</StatusBadge></div>
              </div>
              <SectionTitle title="Linha do tempo de atendimentos" />
              <ul className="space-y-2 text-[13px] text-muted-foreground">
                <li>• {prontuario.aberta} — Caso aberto pelo estudante</li>
                <li>• Triagem inicial concluída</li>
                {prontuario.status === "Em acompanhamento" && <li className="text-success">• Acompanhamento em andamento (sessões quinzenais)</li>}
                {prontuario.status === "Encaminhado" && <li className="text-info">• Encaminhado para profissional especializado</li>}
                {prontuario.status === "Encerrado" && <li>• Caso encerrado</li>}
              </ul>
              <FormField label="Adicionar nota interna" full><Textarea rows={3} placeholder="Anotação confidencial..." /></FormField>
              <DialogFooter>
                <Button variant="outline" onClick={() => setProntuario(null)}>Fechar</Button>
                <Button onClick={() => { toast.success(`Nota adicionada ao prontuário ${prontuario.id}.`); setProntuario(null); }}>Salvar nota</Button>
              </DialogFooter>
            </>
          )}
        </DialogContent>
      </Dialog>
    </div>
  );
}
