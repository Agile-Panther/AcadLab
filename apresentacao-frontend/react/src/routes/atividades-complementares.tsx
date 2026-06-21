import { useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import {
  AppShell, SectionTitle, StatsRow, DataTable, StatusBadge, RowActionButton,
  ActionBar, ProgressRow, FormField, ValidationCallout, Stepper, SuccessBanner,
  useProfileSwitcher,
} from "@/components/acadlab";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogFooter } from "@/components/ui/dialog";
import { ArrowLeft } from "lucide-react";
import { toast } from "sonner";

export const Route = createFileRoute("/atividades-complementares")({
  head: () => ({ meta: [{ title: "Atividades Complementares — AcadLab" }] }),
  component: Page,
});

type Atividade = {
  id: string; cat: string; descricao: string; ch: number; data: string;
  status: "Em análise" | "Deferida" | "Indeferida";
};

const atividadesIniciais: Atividade[] = [
  { id: "AC-2025-0091", cat: "Cursos & Certificações", descricao: "Curso AWS Cloud Practitioner", ch: 20, data: "08/03/2025", status: "Em análise" },
  { id: "AC-2025-0072", cat: "Eventos científicos", descricao: "Semana Acadêmica de Computação", ch: 12, data: "02/03/2025", status: "Deferida" },
  { id: "AC-2025-0060", cat: "Projetos de extensão", descricao: "Projeto Inclusão Digital", ch: 30, data: "18/02/2025", status: "Indeferida" },
];

type View = { kind: "list" } | { kind: "wizard"; step: 0 | 1 | 2 } | { kind: "revisao"; id: string };

function Page() {
  const { active: perfil } = useProfileSwitcher([
    { value: "estudante", label: "Estudante", description: "Envia atividades e acompanha" },
    { value: "coordenacao", label: "Coordenação Acadêmica", description: "Valida e defere atividades" },
  ]);
  const [view, setView] = useState<View>({ kind: "list" });
  const [atividades] = useState(atividadesIniciais);
  const [ver, setVer] = useState<Atividade | null>(null);

  const subtitle = perfil === "coordenacao"
    ? "Visão Coordenação · Validação de atividades"
    : "Maria Santos · saldo de horas";

  return (
    <AppShell title="Atividades Complementares" subtitle={subtitle}>
      {perfil === "coordenacao" && <CoordView />}

      {perfil === "estudante" && view.kind === "list" && (
        <div className="space-y-5">
          <StatsRow stats={[
            { label: "Horas validadas", value: 180, tone: "success" },
            { label: "Em análise", value: 24, tone: "warning" },
            { label: "Indeferidas", value: 30, tone: "danger" },
            { label: "Exigência total", value: 200, tone: "info" },
          ]} />
          <div className="rounded-xl border bg-card p-5 shadow-card">
            <SectionTitle title="Saldo de horas por categoria" />
            <div className="mt-3 grid gap-3 lg:grid-cols-2">
              <ProgressRow label="Cursos & Certificações" current={80} total={80} tone="success" />
              <ProgressRow label="Eventos científicos" current={40} total={60} tone="warning" />
              <ProgressRow label="Projetos de extensão" current={40} total={40} tone="success" />
              <ProgressRow label="Atividades culturais" current={20} total={20} tone="success" />
            </div>
          </div>

          <ActionBar searchPlaceholder="Buscar atividade..." primaryLabel="Submeter atividade" onPrimary={() => setView({ kind: "wizard", step: 0 })} />
          <DataTable
            columns={[
              { key: "id", header: "Protocolo" }, { key: "cat", header: "Categoria" },
              { key: "descricao", header: "Descrição" }, { key: "ch", header: "CH", align: "right" },
              { key: "data", header: "Enviada" },
              { key: "status", header: "Status", render: (r) => (
                <StatusBadge tone={r.status === "Deferida" ? "success" : r.status === "Indeferida" ? "danger" : "warning"}>{r.status}</StatusBadge>
              )},
              { key: "acoes", header: "", align: "right", render: (r) => (
                r.status === "Indeferida"
                  ? <RowActionButton onClick={() => setView({ kind: "revisao", id: r.id })}>Solicitar revisão</RowActionButton>
                  : r.status === "Em análise"
                    ? <RowActionButton tone="danger" onClick={() => toast.success(`Solicitação ${r.id} cancelada.`)}>Cancelar</RowActionButton>
                    : <RowActionButton tone="neutral" onClick={() => setVer(r)}>Ver</RowActionButton>
              )},
            ]}
            rows={atividades}
          />
        </div>
      )}

      {perfil === "estudante" && view.kind === "wizard" && (
        <SubmeterWizard step={view.step} onStep={(s) => setView({ kind: "wizard", step: s })} onDone={() => setView({ kind: "list" })} />
      )}

      {perfil === "estudante" && view.kind === "revisao" && (
        <Revisao id={view.id} onBack={() => setView({ kind: "list" })} />
      )}

      <Dialog open={!!ver} onOpenChange={(o) => !o && setVer(null)}>
        <DialogContent>
          {ver && (
            <>
              <DialogHeader>
                <DialogTitle>{ver.id}</DialogTitle>
                <DialogDescription>{ver.cat}</DialogDescription>
              </DialogHeader>
              <div className="space-y-2 text-[13px]">
                <div className="flex justify-between border-b py-2"><span className="text-muted-foreground">Descrição</span><span className="font-medium text-right max-w-[60%]">{ver.descricao}</span></div>
                <div className="flex justify-between border-b py-2"><span className="text-muted-foreground">Carga horária</span><span className="font-medium">{ver.ch}h</span></div>
                <div className="flex justify-between border-b py-2"><span className="text-muted-foreground">Enviada em</span><span className="font-medium">{ver.data}</span></div>
                <div className="flex justify-between border-b py-2"><span className="text-muted-foreground">Status</span><StatusBadge tone={ver.status === "Deferida" ? "success" : ver.status === "Indeferida" ? "danger" : "warning"}>{ver.status}</StatusBadge></div>
                {ver.status === "Deferida" && <ValidationCallout tone="info">Horas computadas no saldo da categoria.</ValidationCallout>}
              </div>
              <DialogFooter>
                <Button variant="outline" onClick={() => setVer(null)}>Fechar</Button>
                <Button onClick={() => { toast.success(`Comprovante de ${ver.id} baixado.`); setVer(null); }}>Baixar comprovante</Button>
              </DialogFooter>
            </>
          )}
        </DialogContent>
      </Dialog>
    </AppShell>
  );
}

type FilaItem = { id: string; aluno: string; cat: string; ch: number; data: string; status: "Em análise" | "Deferida" | "Indeferida" };

function CoordView() {
  const [fila, setFila] = useState<FilaItem[]>([
    { id: "AC-2025-0091", aluno: "Maria Santos", cat: "Cursos & Certificações", ch: 20, data: "08/03/2025", status: "Em análise" },
    { id: "AC-2025-0092", aluno: "Pedro Almeida", cat: "Eventos científicos", ch: 12, data: "10/03/2025", status: "Em análise" },
    { id: "AC-2025-0094", aluno: "Júlia Rocha", cat: "Projetos de extensão", ch: 30, data: "12/03/2025", status: "Em análise" },
    { id: "AC-2025-0085", aluno: "Lucas Pires", cat: "Monitoria", ch: 40, data: "01/03/2025", status: "Deferida" },
  ]);
  const decidir = (id: string, status: "Deferida" | "Indeferida") => {
    setFila((p) => p.map((x) => x.id === id ? { ...x, status } : x));
    toast.success(`Atividade ${id} ${status.toLowerCase()}.`);
  };
  return (
    <div className="space-y-5">
      <StatsRow stats={[
        { label: "Aguardando validação", value: fila.filter((f) => f.status === "Em análise").length, tone: "warning" },
        { label: "Deferidas (mês)", value: 64, tone: "success" },
        { label: "Indeferidas (mês)", value: 12, tone: "danger" },
        { label: "Estudantes ativos", value: 312, tone: "info" },
      ]} />
      <SectionTitle title="Fila de validação" subtitle="Atividades complementares submetidas aguardando análise." />
      <DataTable
        columns={[
          { key: "id", header: "Protocolo" },
          { key: "aluno", header: "Estudante" },
          { key: "cat", header: "Categoria" },
          { key: "ch", header: "CH", align: "right" },
          { key: "data", header: "Enviada" },
          { key: "status", header: "Status", render: (r) => (
            <StatusBadge tone={r.status === "Deferida" ? "success" : r.status === "Indeferida" ? "danger" : "warning"}>{r.status}</StatusBadge>
          )},
          { key: "acoes", header: "", align: "right", render: (r) => (
            r.status === "Em análise" ? (
              <div className="flex justify-end gap-2">
                <RowActionButton onClick={() => decidir(r.id, "Indeferida")}>Indeferir</RowActionButton>
                <RowActionButton tone="info" onClick={() => decidir(r.id, "Deferida")}>Validar</RowActionButton>
              </div>
            ) : <span className="text-[12px] text-muted-foreground">—</span>
          )},
        ]}
        rows={fila}
      />
    </div>
  );
}

const steps = [{ key: "cat", label: "Categoria" }, { key: "dados", label: "Dados" }, { key: "ok", label: "Confirmação" }];

function SubmeterWizard({ step, onStep, onDone }: { step: 0 | 1 | 2; onStep: (s: 0 | 1 | 2) => void; onDone: () => void }) {
  const [cat, setCat] = useState<string | null>(null);
  const cats = ["Cursos & Certificações", "Eventos científicos", "Projetos de extensão", "Atividades culturais", "Monitoria", "Iniciação científica"];
  return (
    <div className="space-y-5">
      <Button variant="ghost" size="sm" onClick={onDone}><ArrowLeft className="mr-1 h-4 w-4" /> Cancelar</Button>
      <Stepper steps={steps} current={step} />
      {step === 0 && (
        <div className="rounded-xl border bg-card p-6 shadow-card">
          <SectionTitle title="Categoria da atividade" />
          <div className="mt-4 grid grid-cols-2 gap-2">
            {cats.map((c) => (
              <button key={c} onClick={() => setCat(c)} className={`rounded-lg border p-3 text-left text-[13px] transition-colors ${cat === c ? "border-primary bg-primary-soft text-primary" : "border-border hover:bg-primary/5"}`}>{c}</button>
            ))}
          </div>
          <div className="mt-4 flex justify-end"><Button disabled={!cat} onClick={() => onStep(1)}>Avançar</Button></div>
        </div>
      )}
      {step === 1 && (
        <div className="rounded-xl border bg-card p-6 shadow-card">
          <SectionTitle title={cat ?? ""} />
          <div className="mt-4 grid grid-cols-2 gap-4">
            <FormField label="Descrição" required full><Textarea rows={2} /></FormField>
            <FormField label="Carga horária (h)" required><Input type="number" className="h-10" /></FormField>
            <FormField label="Data de realização" required><Input type="date" className="h-10" /></FormField>
            <FormField label="Comprovante" required full><Input type="file" className="h-10" /></FormField>
          </div>
          <ValidationCallout className="mt-3" tone="info">O mesmo comprovante não pode ser usado em duas atividades diferentes.</ValidationCallout>
          <div className="mt-4 flex justify-end gap-2"><Button variant="outline" onClick={() => onStep(0)}>Voltar</Button><Button onClick={() => onStep(2)}>Submeter</Button></div>
        </div>
      )}
      {step === 2 && (
        <div className="space-y-4">
          <SuccessBanner title="Atividade submetida!" description="Protocolo AC-2025-0099 · Aguardando análise da coordenação." />
          <Button onClick={onDone}>Voltar à lista</Button>
        </div>
      )}
    </div>
  );
}

function Revisao({ id, onBack }: { id: string; onBack: () => void }) {
  return (
    <div className="space-y-4">
      <Button variant="ghost" size="sm" onClick={onBack}><ArrowLeft className="mr-1 h-4 w-4" /> Voltar</Button>
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title={`Solicitar revisão — ${id}`} subtitle="A revisão será analisada pela coordenação." />
        <div className="mt-4 grid grid-cols-2 gap-4">
          <FormField label="Horas pleiteadas" required><Input className="h-10" defaultValue="30" /></FormField>
          <FormField label="Justificativa" required full><Textarea rows={4} /></FormField>
          <FormField label="Documento complementar" full><Input type="file" className="h-10" /></FormField>
        </div>
        <div className="mt-4 flex justify-end gap-2"><Button variant="outline" onClick={onBack}>Cancelar</Button><Button>Enviar revisão</Button></div>
      </div>
    </div>
  );
}
