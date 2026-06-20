import { useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import {
  AppShell, SectionTitle, StatsRow, DataTable, StatusBadge, RowActionButton,
  ActionBar, FormField, ValidationCallout, Stepper, TabsRow,
  useProfileSwitcher,
} from "@/components/acadlab";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Sheet, SheetContent, SheetHeader, SheetTitle } from "@/components/ui/sheet";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogFooter } from "@/components/ui/dialog";
import { ArrowLeft, Send, MapPin } from "lucide-react";
import { toast } from "sonner";

export const Route = createFileRoute("/oferta-turmas")({
  head: () => ({ meta: [{ title: "Oferta de Turmas — AcadLab" }] }),
  component: Page,
});

type Turma = {
  id: string; codigo: string; disciplina: string; turma: string; prof: string;
  sala: string; horario: string; vagas: string; status: "Aberta" | "Lotada" | "Cancelada";
};

const turmasIniciais: Turma[] = [
  { id: "T1", codigo: "AED301", disciplina: "Algoritmos Avançados", turma: "T01", prof: "Carlos Lima", sala: "B-203", horario: "Seg/Qua 08-10", vagas: "28/30", status: "Aberta" },
  { id: "T2", codigo: "BD302", disciplina: "Banco de Dados II", turma: "T01", prof: "Ana Souza", sala: "B-105", horario: "Seg 14 · Sex 16", vagas: "30/30", status: "Lotada" },
  { id: "T3", codigo: "ES303", disciplina: "Testes de Software", turma: "T02", prof: "Marcos R.", sala: "B-301", horario: "Ter/Qui 10-12", vagas: "22/30", status: "Aberta" },
  { id: "T4", codigo: "IA401", disciplina: "Inteligência Artificial", turma: "T01", prof: "Lia Mendes", sala: "Lab-IA", horario: "Ter/Qui 14-16", vagas: "8/25", status: "Aberta" },
];

const tabsCoord = [
  { value: "turmas", label: "Turmas" },
  { value: "salas", label: "Salas" },
  { value: "profs", label: "Professores" },
];
const tabsSec = [
  { value: "turmas", label: "Turmas a alocar" },
  { value: "salas", label: "Mapa de salas" },
  { value: "publicar", label: "Publicação da oferta" },
];

function Page() {
  const { active: perfil } = useProfileSwitcher([
    { value: "coordenacao", label: "Coordenação Acadêmica", description: "Planeja e abre turmas" },
    { value: "secretaria", label: "Secretaria Acadêmica", description: "Aloca salas e publica oferta" },
  ]);
  const isSec = perfil === "secretaria";
  const [tab, setTab] = useState("turmas");
  const [selected, setSelected] = useState<Turma | null>(null);
  const [wizard, setWizard] = useState<null | 0 | 1 | 2>(null);
  const [publicada, setPublicada] = useState(false);
  const [editTurma, setEditTurma] = useState<Turma | null>(null);
  const validTabs = (isSec ? tabsSec : tabsCoord).map((t) => t.value);
  if (!validTabs.includes(tab)) {
    setTab("turmas");
  }

  const subtitle = isSec
    ? "Visão Secretaria · Alocação e publicação"
    : "Coordenador — Período 2025.2";

  return (
    <AppShell title="Planejamento e Oferta de Turmas" subtitle={subtitle}>
      <TabsRow items={isSec ? tabsSec : tabsCoord} value={tab} onChange={setTab} className="mb-5" />

      {tab === "turmas" && !wizard && (
        <div className="space-y-5">
          <StatsRow stats={isSec ? [
            { label: "Sem sala", value: 1, tone: "danger" },
            { label: "Aguardando publicação", value: turmasIniciais.length, tone: "warning" },
            { label: "Salas disponíveis", value: 12, tone: "success" },
            { label: "Conflitos de alocação", value: 0, tone: "success" },
          ] : [
            { label: "Turmas ofertadas", value: turmasIniciais.length, tone: "info" },
            { label: "Vagas abertas", value: 28, tone: "success" },
            { label: "Lotadas", value: 1, tone: "warning" },
            { label: "Conflitos detectados", value: 0, tone: "success" },
          ]} />
          {isSec ? (
            <ActionBar searchPlaceholder="Buscar turma a alocar..." />
          ) : (
            <ActionBar searchPlaceholder="Buscar turma..." primaryLabel="Nova turma" onPrimary={() => setWizard(0)} />
          )}
          <DataTable
            columns={[
              { key: "codigo", header: "Disciplina" },
              { key: "disciplina", header: "Nome" },
              { key: "turma", header: "Turma" }, { key: "prof", header: "Professor" },
              { key: "sala", header: "Sala" }, { key: "horario", header: "Horário" },
              { key: "vagas", header: "Vagas" },
              { key: "status", header: "Status", render: (r) => (
                <StatusBadge tone={r.status === "Aberta" ? "success" : r.status === "Lotada" ? "warning" : "danger"}>{r.status}</StatusBadge>
              )},
              { key: "acoes", header: "", align: "right", render: (r) => (
                isSec
                  ? <RowActionButton onClick={() => { toast.success(`Sala ${r.sala} confirmada para ${r.codigo}.`); }}><MapPin className="mr-1 h-3 w-3 inline" /> Alocar sala</RowActionButton>
                  : <RowActionButton onClick={() => setSelected(r)}>Detalhes</RowActionButton>
              )},
            ]}
            rows={turmasIniciais}
          />
        </div>
      )}

      {tab === "turmas" && wizard !== null && (
        <NovaTurmaWizard step={wizard} onStep={setWizard} onCancel={() => setWizard(null)} />
      )}

      {tab === "salas" && <SalasTab />}

      {tab === "profs" && !isSec && <ProfsTab />}

      {tab === "publicar" && isSec && (
        <div className="space-y-4">
          <div className="rounded-xl border bg-card p-6 shadow-card">
            <SectionTitle title="Publicação da oferta 2025.2" subtitle="Após publicada, as turmas ficam visíveis para os estudantes na matrícula." />
            <ul className="mt-4 space-y-2 text-[13px] text-foreground">
              <li>✓ {turmasIniciais.length} turmas conferidas</li>
              <li>✓ Todas com sala alocada</li>
              <li>✓ Sem conflitos de horário</li>
              <li className={publicada ? "text-success" : "text-muted-foreground"}>
                {publicada ? "✓ Publicada em 20/06/2026 às 14:32" : "Aguardando publicação"}
              </li>
            </ul>
            <ValidationCallout className="mt-4" tone="info">
              Publicar gera notificação a todos os estudantes elegíveis e abre a janela de matrícula.
            </ValidationCallout>
            <div className="mt-4 flex justify-end">
              <Button disabled={publicada} onClick={() => { setPublicada(true); toast.success("Oferta 2025.2 publicada com sucesso."); }}>
                <Send className="mr-2 h-4 w-4" /> {publicada ? "Oferta publicada" : "Publicar oferta"}
              </Button>
            </div>
          </div>
        </div>
      )}

      <Sheet open={!!selected} onOpenChange={(o) => !o && setSelected(null)}>
        <SheetContent className="w-[480px] sm:max-w-md">
          {selected && (
            <>
              <SheetHeader>
                <SheetTitle>{selected.codigo} — {selected.disciplina}</SheetTitle>
              </SheetHeader>
              <div className="mt-5 space-y-3 text-[13px]">
                <Field label="Turma" value={selected.turma} />
                <Field label="Professor" value={selected.prof} />
                <Field label="Sala" value={selected.sala} />
                <Field label="Horário" value={selected.horario} />
                <Field label="Vagas" value={selected.vagas} />
              </div>
              <div className="mt-6 flex gap-2">
                <Button variant="outline" className="flex-1" onClick={() => { setEditTurma(selected); setSelected(null); }}>Editar</Button>
                <Button variant="destructive" className="flex-1" onClick={() => { toast.success(`Turma ${selected.codigo} cancelada. Estudantes notificados.`); setSelected(null); }}>Cancelar turma</Button>
              </div>
              <ValidationCallout className="mt-4" tone="info">Cancelar gera notificação aos {parseInt(selected.vagas)} estudantes matriculados.</ValidationCallout>
            </>
          )}
        </SheetContent>
      </Sheet>

      <EditarTurmaDialog turma={editTurma} onClose={() => setEditTurma(null)} />
    </AppShell>
  );
}

function EditarTurmaDialog({ turma, onClose }: { turma: Turma | null; onClose: () => void }) {
  const [prof, setProf] = useState("");
  const [sala, setSala] = useState("");
  const [horario, setHorario] = useState("");
  const [vagas, setVagas] = useState("");
  return (
    <Dialog open={!!turma} onOpenChange={(o) => { if (!o) onClose(); else if (turma) { setProf(turma.prof); setSala(turma.sala); setHorario(turma.horario); setVagas(turma.vagas); } }}>
      <DialogContent>
        {turma && (
          <>
            <DialogHeader>
              <DialogTitle>Editar turma {turma.codigo} — {turma.turma}</DialogTitle>
              <DialogDescription>{turma.disciplina}</DialogDescription>
            </DialogHeader>
            <div className="grid grid-cols-2 gap-3">
              <FormField label="Professor" full><Input className="h-10" value={prof} onChange={(e) => setProf(e.target.value)} /></FormField>
              <FormField label="Sala"><Input className="h-10" value={sala} onChange={(e) => setSala(e.target.value)} /></FormField>
              <FormField label="Horário"><Input className="h-10" value={horario} onChange={(e) => setHorario(e.target.value)} /></FormField>
              <FormField label="Vagas (ocupadas/total)" full><Input className="h-10" value={vagas} onChange={(e) => setVagas(e.target.value)} /></FormField>
            </div>
            <ValidationCallout tone="info">Alterações geram notificação aos estudantes matriculados.</ValidationCallout>
            <DialogFooter>
              <Button variant="outline" onClick={onClose}>Cancelar</Button>
              <Button onClick={() => { toast.success(`Turma ${turma.codigo} atualizada.`); onClose(); }}>Salvar alterações</Button>
            </DialogFooter>
          </>
        )}
      </DialogContent>
    </Dialog>
  );
}

type Sala = { cod: string; predio: string; cap: number; tipo: string; status: "Ativa" | "Inativa" };

function SalasTab() {
  const [salas, setSalas] = useState<Sala[]>([
    { cod: "B-203", predio: "Bloco B", cap: 40, tipo: "Sala teórica", status: "Ativa" },
    { cod: "Lab-IA", predio: "Bloco C", cap: 25, tipo: "Laboratório", status: "Ativa" },
    { cod: "B-401", predio: "Bloco B", cap: 60, tipo: "Auditório", status: "Inativa" },
  ]);
  const [edit, setEdit] = useState<Sala | null>(null);
  const [cap, setCap] = useState(0);
  const [tipo, setTipo] = useState("");
  const [status, setStatus] = useState<"Ativa" | "Inativa">("Ativa");
  const salvar = () => {
    if (!edit) return;
    setSalas((p) => p.map((s) => s.cod === edit.cod ? { ...s, cap, tipo, status } : s));
    toast.success(`Sala ${edit.cod} atualizada.`);
    setEdit(null);
  };
  return (
    <>
      <DataTable
        columns={[
          { key: "cod", header: "Sala" }, { key: "predio", header: "Prédio" },
          { key: "cap", header: "Capacidade", align: "right" }, { key: "tipo", header: "Tipo" },
          { key: "status", header: "Status", render: (r) => <StatusBadge tone={r.status === "Ativa" ? "success" : "neutral"}>{r.status}</StatusBadge> },
          { key: "acoes", header: "", align: "right", render: (r) => <RowActionButton onClick={() => { setEdit(r); setCap(r.cap); setTipo(r.tipo); setStatus(r.status); }}>Editar</RowActionButton> },
        ]}
        rows={salas}
      />
      <Dialog open={!!edit} onOpenChange={(o) => !o && setEdit(null)}>
        <DialogContent>
          {edit && (
            <>
              <DialogHeader>
                <DialogTitle>Editar sala {edit.cod}</DialogTitle>
                <DialogDescription>{edit.predio}</DialogDescription>
              </DialogHeader>
              <div className="grid grid-cols-2 gap-3">
                <FormField label="Capacidade"><Input type="number" className="h-10" value={cap} onChange={(e) => setCap(Number(e.target.value))} /></FormField>
                <FormField label="Tipo"><Input className="h-10" value={tipo} onChange={(e) => setTipo(e.target.value)} /></FormField>
                <FormField label="Status" full>
                  <div className="flex gap-2">
                    <Button type="button" size="sm" variant={status === "Ativa" ? "default" : "outline"} onClick={() => setStatus("Ativa")}>Ativa</Button>
                    <Button type="button" size="sm" variant={status === "Inativa" ? "default" : "outline"} onClick={() => setStatus("Inativa")}>Inativa</Button>
                  </div>
                </FormField>
              </div>
              <DialogFooter>
                <Button variant="outline" onClick={() => setEdit(null)}>Cancelar</Button>
                <Button onClick={salvar}>Salvar</Button>
              </DialogFooter>
            </>
          )}
        </DialogContent>
      </Dialog>
    </>
  );
}

type Prof = { nome: string; depto: string; turmas: number; status: "Ativo" | "Inativo"; email?: string };

function ProfsTab() {
  const [profs, setProfs] = useState<Prof[]>([
    { nome: "Carlos Lima", depto: "Computação", turmas: 3, status: "Ativo", email: "carlos@univ.edu" },
    { nome: "Ana Souza", depto: "Computação", turmas: 2, status: "Ativo", email: "ana@univ.edu" },
    { nome: "Lia Mendes", depto: "IA", turmas: 2, status: "Ativo", email: "lia@univ.edu" },
  ]);
  const [edit, setEdit] = useState<Prof | null>(null);
  const [depto, setDepto] = useState("");
  const [email, setEmail] = useState("");
  const [status, setStatus] = useState<"Ativo" | "Inativo">("Ativo");
  const salvar = () => {
    if (!edit) return;
    setProfs((p) => p.map((x) => x.nome === edit.nome ? { ...x, depto, email, status } : x));
    toast.success(`Cadastro de ${edit.nome} atualizado.`);
    setEdit(null);
  };
  return (
    <>
      <DataTable
        columns={[
          { key: "nome", header: "Professor" }, { key: "depto", header: "Departamento" },
          { key: "turmas", header: "Turmas vinculadas", align: "right" },
          { key: "status", header: "Status", render: (r) => <StatusBadge tone={r.status === "Ativo" ? "success" : "neutral"}>{r.status}</StatusBadge> },
          { key: "acoes", header: "", align: "right", render: (r) => <RowActionButton onClick={() => { setEdit(r); setDepto(r.depto); setEmail(r.email ?? ""); setStatus(r.status); }}>Editar</RowActionButton> },
        ]}
        rows={profs}
      />
      <Dialog open={!!edit} onOpenChange={(o) => !o && setEdit(null)}>
        <DialogContent>
          {edit && (
            <>
              <DialogHeader>
                <DialogTitle>Editar professor</DialogTitle>
                <DialogDescription>{edit.nome} · {edit.turmas} turmas vinculadas</DialogDescription>
              </DialogHeader>
              <div className="grid grid-cols-2 gap-3">
                <FormField label="Departamento" full><Input className="h-10" value={depto} onChange={(e) => setDepto(e.target.value)} /></FormField>
                <FormField label="E-mail institucional" full><Input className="h-10" type="email" value={email} onChange={(e) => setEmail(e.target.value)} /></FormField>
                <FormField label="Status" full>
                  <div className="flex gap-2">
                    <Button type="button" size="sm" variant={status === "Ativo" ? "default" : "outline"} onClick={() => setStatus("Ativo")}>Ativo</Button>
                    <Button type="button" size="sm" variant={status === "Inativo" ? "default" : "outline"} onClick={() => setStatus("Inativo")}>Inativo</Button>
                  </div>
                </FormField>
              </div>
              <DialogFooter>
                <Button variant="outline" onClick={() => setEdit(null)}>Cancelar</Button>
                <Button onClick={salvar}>Salvar</Button>
              </DialogFooter>
            </>
          )}
        </DialogContent>
      </Dialog>
    </>
  );
}

function Field({ label, value }: { label: string; value: string }) {
  return (
    <div className="flex justify-between border-b border-border py-2">
      <span className="text-muted-foreground">{label}</span>
      <span className="font-medium text-foreground">{value}</span>
    </div>
  );
}

const wizSteps = [
  { key: "disc", label: "Disciplina" }, { key: "prof", label: "Professor & Sala" }, { key: "conf", label: "Confirmar" },
];

function NovaTurmaWizard({ step, onStep, onCancel }: { step: 0 | 1 | 2; onStep: (s: 0 | 1 | 2) => void; onCancel: () => void }) {
  return (
    <div className="space-y-5">
      <Button variant="ghost" size="sm" onClick={onCancel}><ArrowLeft className="mr-1 h-4 w-4" /> Cancelar</Button>
      <Stepper steps={wizSteps} current={step} />
      <div className="rounded-xl border bg-card p-6 shadow-card">
        {step === 0 && (
          <>
            <SectionTitle title="Selecionar disciplina" subtitle="Apenas disciplinas da matriz ativa." />
            <div className="mt-4 grid grid-cols-2 gap-4">
              <FormField label="Disciplina" required><Input className="h-10" placeholder="ES401 — Arquitetura de Software" /></FormField>
              <FormField label="Turma"><Input className="h-10" defaultValue="T01" /></FormField>
            </div>
          </>
        )}
        {step === 1 && (
          <>
            <SectionTitle title="Professor, sala e horário" />
            <div className="mt-4 grid grid-cols-2 gap-4">
              <FormField label="Professor" required><Input className="h-10" placeholder="Carlos Lima" /></FormField>
              <FormField label="Sala" required><Input className="h-10" placeholder="B-203" /></FormField>
              <FormField label="Horário" required full><Input className="h-10" placeholder="Seg/Qua 08:00-10:00" /></FormField>
              <FormField label="Capacidade" required><Input type="number" className="h-10" defaultValue={30} /></FormField>
            </div>
            <ValidationCallout className="mt-4" tone="info">Sem conflito de horário detectado para professor e sala.</ValidationCallout>
          </>
        )}
        {step === 2 && (
          <>
            <SectionTitle title="Revisão" />
            <p className="mt-3 text-[13px] text-muted-foreground">Confirme a oferta da turma. Após confirmar, ela ficará visível na matrícula.</p>
          </>
        )}
        <div className="mt-4 flex justify-end gap-2">
          {step > 0 && <Button variant="outline" onClick={() => onStep((step - 1) as 0 | 1)}>Voltar</Button>}
          {step < 2 && <Button onClick={() => onStep((step + 1) as 1 | 2)}>Avançar</Button>}
          {step === 2 && <Button onClick={onCancel}>Confirmar oferta</Button>}
        </div>
      </div>
    </div>
  );
}
