import { toast } from "sonner";
import { useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import {
  AppShell, SectionTitle, EmptyHero, DataTable, StatusBadge, RowActionButton,
  FormField, ValidationCallout, Stepper, SuccessBanner, StatsRow,
  useProfileSwitcher,
} from "@/components/acadlab";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogFooter } from "@/components/ui/dialog";
import { Plane, ArrowLeft } from "lucide-react";

export const Route = createFileRoute("/mobilidade")({
  head: () => ({ meta: [{ title: "Mobilidade Acadêmica — AcadLab" }] }),
  component: Page,
});

type Mob = {
  instituicao: string; pais: string; inicio: string; fim: string;
  status: "Em análise" | "Autorizada" | "Em curso" | "Concluída" | "Cancelada";
};

type View = "overview" | "wizard" | "comprovantes";

function Page() {
  const [mob, setMob] = useState<Mob | null>({
    instituicao: "Universidade do Porto",
    pais: "Portugal",
    inicio: "01/09/2025",
    fim: "31/01/2026",
    status: "Autorizada",
  });
  const [view, setView] = useState<View>("overview");
  const [step, setStep] = useState<0 | 1 | 2 | 3>(0);
  const { active: perfil } = useProfileSwitcher([
    { value: "estudante", label: "Estudante", description: "Solicita e acompanha intercâmbio" },
    { value: "coordenador", label: "Coord. Mobilidade", description: "Aprova e homologa equivalências" },
  ]);
  const subtitle = perfil === "coordenador"
    ? "Coordenação de Mobilidade · Aprovação de pedidos"
    : "Programas de intercâmbio e aproveitamento externo";

  const handleCancelMob = () => {
    if (mob) {
      setMob({ ...mob, status: "Cancelada" });
      toast.info("Mobilidade cancelada com sucesso.");
    }
  };

  const handleSendComprovantes = () => {
    toast.success("Comprovantes enviados com sucesso!");
    setView("overview");
  };

  if (perfil === "coordenador") {
    return (
      <AppShell title="Mobilidade Acadêmica" subtitle={subtitle}>
        <CoordView />
      </AppShell>
    );
  }

  return (
    <AppShell title="Mobilidade Acadêmica" subtitle={subtitle}>
      {view === "overview" && !mob && (
        <EmptyHero
          icon={Plane}
          title="Você ainda não solicitou mobilidade"
          description="Solicite mobilidade acadêmica para cursar disciplinas em outra instituição com aproveitamento posterior."
          actionLabel="Solicitar mobilidade"
          onAction={() => { setView("wizard"); setStep(0); }}
        />
      )}

      {view === "overview" && mob && (
        <div className="space-y-5">
          <StatsRow stats={[
            { label: "Instituição destino", value: mob.instituicao, tone: "info" },
            { label: "Período", value: `${mob.inicio} → ${mob.fim}`, tone: "info" },
            { label: "Disciplinas no plano", value: 4, tone: "info" },
            { label: "Status", value: mob.status, tone: mob.status === "Autorizada" || mob.status === "Concluída" ? "success" : "warning" },
          ]} />

          <div className="rounded-xl border bg-card p-5 shadow-card">
            <SectionTitle title="Plano de estudos autorizado" />
            <DataTable className="mt-3"
              columns={[
                { key: "ext", header: "Disciplina externa" }, { key: "ch", header: "CH ext.", align: "right" },
                { key: "equiv", header: "Equivalência local" }, { key: "chL", header: "CH local", align: "right" },
                { key: "status", header: "Status", render: (r) => <StatusBadge tone={r.status === "Aprovada" ? "success" : "warning"}>{r.status}</StatusBadge> },
              ]}
              rows={[
                { ext: "Compiladores", ch: 80, equiv: "AED401 — Compiladores", chL: 80, status: "Aprovada" },
                { ext: "Engenharia de Software", ch: 60, equiv: "ES401 — Arq. de Software", chL: 60, status: "Aprovada" },
                { ext: "Ética em Computação", ch: 30, equiv: "Optativa Livre", chL: 30, status: "Aprovada" },
                { ext: "Linguística Aplicada", ch: 60, equiv: "—", chL: 0, status: "Não equivalente" },
              ]}
            />
          </div>

          <div className="flex flex-wrap gap-2">
            {mob.status === "Autorizada" && (
              <>
                <Button variant="outline" onClick={() => setView("comprovantes")}>Anexar comprovantes</Button>
                <Button variant="destructive" onClick={handleCancelMob}>Cancelar mobilidade</Button>
              </>
            )}
            {mob.status === "Em curso" && <Button onClick={() => setView("comprovantes")}>Anexar comprovantes</Button>}
          </div>
        </div>
      )}

      {view === "wizard" && (
        <Wizard step={step} onStep={setStep} onDone={() => { setView("overview"); setMob({ instituicao: "Nova", pais: "—", inicio: "—", fim: "—", status: "Em análise" }); }} />
      )}

      {view === "comprovantes" && (
        <div className="space-y-4">
          <Button variant="ghost" size="sm" onClick={() => setView("overview")}><ArrowLeft className="mr-1 h-4 w-4" /> Voltar</Button>
          <div className="rounded-xl border bg-card p-6 shadow-card">
            <SectionTitle title="Anexar comprovantes de conclusão" subtitle="Necessários para que a secretaria registre os resultados no histórico." />
            <div className="mt-4 grid grid-cols-2 gap-4">
              {[
                "Compiladores",
                "Engenharia de Software",
                "Ética em Computação",
              ].map((d) => (
                <FormField key={d} label={`Comprovante — ${d}`} required full><Input type="file" className="h-10" /></FormField>
              ))}
            </div>
            <ValidationCallout className="mt-3" tone="info">Apenas disciplinas com comprovante e plano autorizado serão registradas no histórico.</ValidationCallout>
            <div className="mt-4 flex justify-end gap-2"><Button variant="outline" onClick={() => setView("overview")}>Cancelar</Button><Button onClick={handleSendComprovantes}>Enviar comprovantes</Button></div>
          </div>
        </div>
      )}
    </AppShell>
  );
}

const steps = [
  { key: "inst", label: "Instituição" }, { key: "plano", label: "Plano de estudos" },
  { key: "doc", label: "Documentos" }, { key: "ok", label: "Envio" },
];

function Wizard({ step, onStep, onDone }: { step: 0 | 1 | 2 | 3; onStep: (s: 0 | 1 | 2 | 3) => void; onDone: () => void }) {
  const handleFinalSubmit = () => {
    toast.success("Candidatura enviada com sucesso! Protocolo: MOB-" + Math.floor(Math.random() * 10000));
    onStep(3);
  };

  return (
    <div className="space-y-5">
      <Button variant="ghost" size="sm" onClick={onDone}><ArrowLeft className="mr-1 h-4 w-4" /> Cancelar</Button>
      <Stepper steps={steps} current={step} />
      {step === 0 && (
        <div className="rounded-xl border bg-card p-6 shadow-card">
          <SectionTitle title="Instituição destino" />
          <div className="mt-4 grid grid-cols-2 gap-4">
            <FormField label="Instituição" required><Input className="h-10" /></FormField>
            <FormField label="País" required><Input className="h-10" /></FormField>
            <FormField label="Início" required><Input type="date" className="h-10" /></FormField>
            <FormField label="Fim" required><Input type="date" className="h-10" /></FormField>
          </div>
          <div className="mt-4 flex justify-end"><Button onClick={() => onStep(1)}>Avançar</Button></div>
        </div>
      )}
      {step === 1 && (
        <div className="rounded-xl border bg-card p-6 shadow-card">
          <SectionTitle title="Plano de estudos" subtitle="Liste as disciplinas a cursar e as equivalências pretendidas." />
          <FormField className="mt-3" label="Disciplinas externas + equivalências" full required><Textarea rows={6} placeholder="Compiladores → AED401&#10;Engenharia de Software → ES401" /></FormField>
          <ValidationCallout className="mt-3" tone="info">Carga horária externa deve ser ≥ à da disciplina local equivalente.</ValidationCallout>
          <div className="mt-4 flex justify-end gap-2"><Button variant="outline" onClick={() => onStep(0)}>Voltar</Button><Button onClick={() => onStep(2)}>Avançar</Button></div>
        </div>
      )}
      {step === 2 && (
        <div className="rounded-xl border bg-card p-6 shadow-card">
          <SectionTitle title="Documentos" />
          <FormField className="mt-3" label="Carta de aceite" required full><Input type="file" className="h-10" /></FormField>
          <FormField label="Plano em PDF" full><Input type="file" className="h-10" /></FormField>
          <div className="mt-4 flex justify-end gap-2"><Button variant="outline" onClick={() => onStep(1)}>Voltar</Button><Button onClick={handleFinalSubmit}>Enviar</Button></div>
        </div>
      )}
      {step === 3 && (
        <div className="space-y-4">
          <SuccessBanner title="Solicitação enviada!" description="A coordenação analisará o plano em até 15 dias úteis." />
          <Button onClick={onDone}>Voltar</Button>
        </div>
      )}
    </div>
  );
}

type PedidoMob = {
  id: string; aluno: string; instituicao: string; pais: string;
  periodo: string; disciplinas: number; status: "Em análise" | "Autorizada" | "Indeferida" | "Em curso";
};

const pedidosIniciais: PedidoMob[] = [
  { id: "MOB-2025-022", aluno: "Maria Santos", instituicao: "Universidade do Porto", pais: "Portugal", periodo: "2025.2 → 2026.1", disciplinas: 4, status: "Em análise" },
  { id: "MOB-2025-023", aluno: "Pedro Almeida", instituicao: "UTFSM", pais: "Chile", periodo: "2025.2", disciplinas: 3, status: "Em análise" },
  { id: "MOB-2025-019", aluno: "Júlia Rocha", instituicao: "Politecnico di Milano", pais: "Itália", periodo: "2025.2 → 2026.1", disciplinas: 5, status: "Autorizada" },
  { id: "MOB-2025-018", aluno: "Lucas Pires", instituicao: "ITESM", pais: "México", periodo: "2025.1", disciplinas: 3, status: "Em curso" },
];

function CoordView() {
  const [pedidos, setPedidos] = useState<PedidoMob[]>(pedidosIniciais);
  const [detalhe, setDetalhe] = useState<PedidoMob | null>(null);

  const updateStatus = (id: string, status: PedidoMob["status"]) => {
    setPedidos(pedidos.map(p => p.id === id ? { ...p, status } : p));
    if (status === "Autorizada") toast.success("Pedido autorizado com sucesso!");
    if (status === "Indeferida") toast.error("Pedido indeferido.");
  };

  return (
    <div className="space-y-5">
      <StatsRow stats={[
        { label: "Pedidos aguardando análise", value: pedidos.filter((p) => p.status === "Em análise").length, tone: "warning" },
        { label: "Autorizadas no semestre", value: pedidos.filter((p) => p.status === "Autorizada").length, tone: "success" },
        { label: "Em curso", value: pedidos.filter((p) => p.status === "Em curso").length, tone: "info" },
        { label: "Convênios ativos", value: 17, tone: "info" },
      ]} />
      <ValidationCallout tone="info">Equivalências devem respeitar carga horária ≥ à local e estar previstas na matriz vigente.</ValidationCallout>
      <DataTable
        columns={[
          { key: "id", header: "Protocolo" },
          { key: "aluno", header: "Aluno" },
          { key: "instituicao", header: "Instituição" },
          { key: "pais", header: "País" },
          { key: "periodo", header: "Período" },
          { key: "disciplinas", header: "Disc.", align: "right" },
          { key: "status", header: "Status", render: (r) => (
            <StatusBadge tone={r.status === "Autorizada" || r.status === "Em curso" ? "success" : r.status === "Indeferida" ? "danger" : "warning"}>{r.status}</StatusBadge>
          )},
          { key: "acoes", header: "", align: "right", render: (r) => (
            r.status === "Em análise" ? (
              <div className="flex justify-end gap-1.5">
                <RowActionButton tone="neutral" onClick={() => setDetalhe(r)}>Detalhes</RowActionButton>
                <RowActionButton tone="danger" onClick={() => updateStatus(r.id, "Indeferida")}>Indeferir</RowActionButton>
                <RowActionButton onClick={() => updateStatus(r.id, "Autorizada")}>Autorizar</RowActionButton>
              </div>
            ) : <RowActionButton tone="neutral" onClick={() => setDetalhe(r)}>Detalhes</RowActionButton>
          )},
        ]}
        rows={pedidos}
      />
      <PedidoDetalheDialog pedido={detalhe} onClose={() => setDetalhe(null)} />
    </div>
  );
}

function PedidoDetalheDialog({ pedido, onClose }: { pedido: PedidoMob | null; onClose: () => void }) {
  return (
    <Dialog open={!!pedido} onOpenChange={(o) => !o && onClose()}>
      <DialogContent className="max-w-2xl">
        {pedido && (
          <>
            <DialogHeader>
              <DialogTitle>{pedido.id} — {pedido.aluno}</DialogTitle>
              <DialogDescription>{pedido.instituicao} ({pedido.pais}) · {pedido.periodo}</DialogDescription>
            </DialogHeader>
            <div className="grid grid-cols-2 gap-3 text-[13px]">
              <div><span className="text-muted-foreground">Status: </span><StatusBadge tone={pedido.status === "Autorizada" || pedido.status === "Em curso" ? "success" : pedido.status === "Indeferida" ? "danger" : "warning"}>{pedido.status}</StatusBadge></div>
              <div><span className="text-muted-foreground">Disciplinas no plano: </span><strong>{pedido.disciplinas}</strong></div>
            </div>
            <SectionTitle title="Plano de estudos" />
            <DataTable
              columns={[
                { key: "ext", header: "Externa" }, { key: "ch", header: "CH ext.", align: "right" },
                { key: "equiv", header: "Equivalência" }, { key: "chL", header: "CH local", align: "right" },
                { key: "status", header: "Status", render: (r) => <StatusBadge tone={r.status === "Aprovada" ? "success" : r.status === "Em análise" ? "warning" : "danger"}>{r.status}</StatusBadge> },
              ]}
              rows={[
                { ext: "Compiladores", ch: 80, equiv: "AED401 — Compiladores", chL: 80, status: "Aprovada" },
                { ext: "Eng. Software", ch: 60, equiv: "ES401", chL: 60, status: "Em análise" },
                { ext: "Optativa local", ch: 30, equiv: "OPT", chL: 30, status: "Aprovada" },
              ]}
            />
            <DialogFooter>
              <Button variant="outline" onClick={onClose}>Fechar</Button>
              <Button onClick={() => { toast.success(`Mensagem enviada a ${pedido.aluno}.`); onClose(); }}>Contatar estudante</Button>
            </DialogFooter>
          </>
        )}
      </DialogContent>
    </Dialog>
  );
}
