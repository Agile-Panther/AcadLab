import { useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import { toast } from "sonner";
import {
  AppShell, SectionTitle, StatsRow, DataTable, StatusBadge, RowActionButton,
  ProgressRow, ValidationCallout, FormField, TabsRow, useProfileSwitcher,
} from "@/components/acadlab";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import {
  Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogFooter,
} from "@/components/ui/dialog";
import { ArrowLeft, CheckCircle2 } from "lucide-react";

export const Route = createFileRoute("/gestao-pedagogica")({
  head: () => ({ meta: [{ title: "Gestão Pedagógica — AcadLab" }] }),
  component: Page,
});

type Turma = {
  id: string; codigo: string; disciplina: string; matriculados: number;
  aulasDadas: number; aulasTotal: number; notasFechadas: boolean;
};

const turmas: Turma[] = [
  { id: "T1", codigo: "AED301", disciplina: "Algoritmos Avançados", matriculados: 28, aulasDadas: 22, aulasTotal: 30, notasFechadas: false },
  { id: "T2", codigo: "BD302", disciplina: "Banco de Dados II", matriculados: 30, aulasDadas: 28, aulasTotal: 30, notasFechadas: false },
  { id: "T3", codigo: "ES303", disciplina: "Testes de Software", matriculados: 22, aulasDadas: 30, aulasTotal: 30, notasFechadas: true },
];

const subTabs = [
  { value: "overview", label: "Visão geral" },
  { value: "aulas", label: "Aulas" },
  { value: "freq", label: "Frequência" },
  { value: "aval", label: "Avaliações & Notas" },
  { value: "fechar", label: "Fechamento" },
];

type TurmaCurso = Turma & { prof: string; freq: number; risco: number };

const turmasCurso: TurmaCurso[] = [
  { id: "T1", codigo: "AED301", disciplina: "Algoritmos Avançados", prof: "Carlos Lima", matriculados: 28, aulasDadas: 22, aulasTotal: 30, freq: 87, risco: 3, notasFechadas: false },
  { id: "T2", codigo: "BD302", disciplina: "Banco de Dados II", prof: "Carlos Lima", matriculados: 30, aulasDadas: 28, aulasTotal: 30, freq: 84, risco: 5, notasFechadas: false },
  { id: "T4", codigo: "ENG201", disciplina: "Eng. de Requisitos", prof: "Ana Souza", matriculados: 26, aulasDadas: 20, aulasTotal: 30, freq: 72, risco: 8, notasFechadas: false },
  { id: "T5", codigo: "POO101", disciplina: "POO", prof: "Renato Dias", matriculados: 34, aulasDadas: 30, aulasTotal: 30, freq: 91, risco: 2, notasFechadas: true },
  { id: "T3", codigo: "ES303", disciplina: "Testes de Software", prof: "Carlos Lima", matriculados: 22, aulasDadas: 30, aulasTotal: 30, freq: 89, risco: 1, notasFechadas: true },
];

function Page() {
  const [selected, setSelected] = useState<string | null>(null);
  const turma = turmas.find((t) => t.id === selected);
  const [tab, setTab] = useState("overview");
  const [acompanhar, setAcompanhar] = useState<TurmaCurso | null>(null);
  const { active: perfil } = useProfileSwitcher([
    { value: "professor", label: "Professor", description: "Lança aulas, frequência e notas" },
    { value: "coordenador", label: "Coordenador", description: "Acompanha turmas do curso" },
  ]);
  const isCoord = perfil === "coordenador";
  const subtitle = isCoord
    ? "Coordenação · Acompanhamento das turmas — 2025.2"
    : "Professor: Carlos Lima — 2025.2";

  return (
    <AppShell title="Gestão Pedagógica" subtitle={subtitle}>
      {isCoord ? (
        <div className="space-y-5">
          <StatsRow stats={[
            { label: "Turmas do curso", value: turmasCurso.length, tone: "info" },
            { label: "Estudantes", value: turmasCurso.reduce((s, t) => s + t.matriculados, 0), tone: "info" },
            { label: "Em risco", value: turmasCurso.reduce((s, t) => s + t.risco, 0), tone: "danger" },
            { label: "Diários fechados", value: turmasCurso.filter((t) => t.notasFechadas).length, tone: "success" },
          ]} />
          <ValidationCallout tone="info">Visão somente-leitura. Coordenação não lança notas nem frequência — apenas monitora.</ValidationCallout>
          <DataTable
            columns={[
              { key: "codigo", header: "Código" },
              { key: "disciplina", header: "Disciplina" },
              { key: "prof", header: "Professor" },
              { key: "matriculados", header: "Alunos", align: "right" },
              { key: "freq", header: "Freq. média", align: "right", render: (r) => `${r.freq}%` },
              { key: "risco", header: "Em risco", align: "right", render: (r) => (
                <StatusBadge tone={r.risco >= 5 ? "danger" : r.risco >= 3 ? "warning" : "success"}>{r.risco}</StatusBadge>
              )},
              { key: "status", header: "Diário", render: (r) => (
                <StatusBadge tone={r.notasFechadas ? "success" : "warning"}>{r.notasFechadas ? "Fechado" : "Aberto"}</StatusBadge>
              )},
              { key: "acoes", header: "", align: "right", render: (r) => <RowActionButton onClick={() => setAcompanhar(r)}>Acompanhar</RowActionButton> },
            ]}
            rows={turmasCurso}
          />
          <AcompanharDialog turma={acompanhar} onClose={() => setAcompanhar(null)} />
        </div>
      ) : !turma ? (
        <div className="space-y-5">
          <StatsRow stats={[
            { label: "Minhas turmas", value: turmas.length, tone: "info" },
            { label: "Estudantes", value: turmas.reduce((s, t) => s + t.matriculados, 0), tone: "info" },
            { label: "Fechadas", value: turmas.filter((t) => t.notasFechadas).length, tone: "success" },
            { label: "Em andamento", value: turmas.filter((t) => !t.notasFechadas).length, tone: "warning" },
          ]} />
          <DataTable
            columns={[
              { key: "codigo", header: "Código" }, { key: "disciplina", header: "Disciplina" },
              { key: "matriculados", header: "Alunos", align: "right" },
              { key: "aulas", header: "Aulas", render: (r) => `${r.aulasDadas}/${r.aulasTotal}` },
              { key: "status", header: "Status", render: (r) => (
                <StatusBadge tone={r.notasFechadas ? "success" : "warning"}>{r.notasFechadas ? "Fechada" : "Em andamento"}</StatusBadge>
              )},
              { key: "acoes", header: "", align: "right", render: (r) => <RowActionButton onClick={() => { setSelected(r.id); setTab("overview"); }}>Abrir diário</RowActionButton> },
            ]}
            rows={turmas}
          />
        </div>
      ) : (
        <div className="space-y-5">
          <Button variant="ghost" size="sm" onClick={() => setSelected(null)}><ArrowLeft className="mr-1 h-4 w-4" /> Minhas turmas</Button>
          <SectionTitle title={`${turma.codigo} — ${turma.disciplina}`} subtitle={`${turma.matriculados} estudantes · ${turma.aulasDadas}/${turma.aulasTotal} aulas registradas`} />
          <TabsRow items={subTabs} value={tab} onChange={setTab} />
          {tab === "overview" && <OverviewTurma turma={turma} />}
          {tab === "aulas" && <Aulas />}
          {tab === "freq" && <Frequencia />}
          {tab === "aval" && <Avaliacoes />}
          {tab === "fechar" && <Fechar turma={turma} />}
        </div>
      )}
    </AppShell>
  );
}

function AcompanharDialog({ turma, onClose }: { turma: TurmaCurso | null; onClose: () => void }) {
  return (
    <Dialog open={!!turma} onOpenChange={(o) => !o && onClose()}>
      <DialogContent className="max-w-2xl">
        {turma && (
          <>
            <DialogHeader>
              <DialogTitle>{turma.codigo} — {turma.disciplina}</DialogTitle>
              <DialogDescription>Professor: {turma.prof} · {turma.matriculados} estudantes matriculados</DialogDescription>
            </DialogHeader>
            <div className="space-y-3 py-2">
              <ProgressRow label="Aulas ministradas" current={turma.aulasDadas} total={turma.aulasTotal} unit="" tone="info" />
              <ProgressRow label="Frequência média" current={turma.freq} total={100} unit="%" tone={turma.freq >= 80 ? "success" : "warning"} />
              <ProgressRow label="Estudantes em risco" current={turma.risco} total={turma.matriculados} unit="" tone={turma.risco >= 5 ? "danger" : "warning"} />
            </div>
            <SectionTitle title="Estudantes em alerta" />
            <DataTable
              columns={[
                { key: "nome", header: "Estudante" },
                { key: "freq", header: "Freq.", align: "right" },
                { key: "media", header: "Média parcial", align: "right" },
                { key: "alerta", header: "Alerta", render: (r) => <StatusBadge tone="danger">{r.alerta}</StatusBadge> },
              ]}
              rows={[
                { nome: "João Silva", freq: "62%", media: 4.2, alerta: "Frequência baixa" },
                { nome: "Lucas Pereira", freq: "70%", media: 5.1, alerta: "Risco reprovação" },
              ].slice(0, Math.max(1, Math.min(turma.risco, 3)))}
            />
            <DialogFooter>
              <Button variant="outline" onClick={onClose}>Fechar</Button>
              <Button onClick={() => { toast.success(`Mensagem enviada ao professor ${turma.prof}.`); onClose(); }}>Falar com o professor</Button>
            </DialogFooter>
          </>
        )}
      </DialogContent>
    </Dialog>
  );
}

function OverviewTurma({ turma }: { turma: Turma }) {
  return (
    <div className="grid gap-4 lg:grid-cols-3">
      <div className="lg:col-span-2 rounded-xl border bg-card p-5 shadow-card">
        <SectionTitle title="Progresso do período" />
        <div className="mt-3 space-y-3">
          <ProgressRow label="Aulas ministradas" current={turma.aulasDadas} total={turma.aulasTotal} unit="" tone="info" />
          <ProgressRow label="Frequência média da turma" current={87} total={100} unit="%" tone="success" />
          <ProgressRow label="Notas lançadas (P1, P2)" current={2} total={3} unit="aval" tone="warning" />
        </div>
      </div>
      <div className="rounded-xl border bg-card p-5 shadow-card">
        <SectionTitle title="Pendências" />
        <ul className="mt-3 space-y-2 text-[13px]">
          <li className="text-warning">• 1 avaliação sem nota (P3)</li>
          <li className="text-warning">• 2 aulas pendentes de registro</li>
          <li className="text-muted-foreground">• Fechamento abre em 15/06</li>
        </ul>
      </div>
    </div>
  );
}

type AulaRow = { data: string; conteudo: string; presentes: number };

function Aulas() {
  const [aulas, setAulas] = useState<AulaRow[]>([
    { data: "12/03/2025", conteudo: "Algoritmos gulosos", presentes: 26 },
    { data: "10/03/2025", conteudo: "Programação dinâmica II", presentes: 24 },
    { data: "05/03/2025", conteudo: "Programação dinâmica I", presentes: 27 },
  ]);
  const [editAula, setEditAula] = useState<AulaRow | null>(null);

  const salvar = (orig: AulaRow, novo: AulaRow) => {
    setAulas((p) => p.map((a) => a === orig ? novo : a));
    toast.success(`Aula de ${novo.data} atualizada.`);
    setEditAula(null);
  };

  return (
    <div className="space-y-4">
      <div className="rounded-xl border bg-card p-5 shadow-card">
        <SectionTitle title="Registrar nova aula" />
        <div className="mt-3 grid grid-cols-3 gap-3">
          <FormField label="Data" required><Input type="date" className="h-10" /></FormField>
          <FormField label="Conteúdo" required full><Input className="h-10" placeholder="Ex.: Análise de complexidade O(n log n)" /></FormField>
        </div>
        <div className="mt-3 flex justify-end"><Button onClick={() => toast.success("Aula registrada e enviada para conferência.")}>Registrar aula</Button></div>
      </div>
      <DataTable
        columns={[
          { key: "data", header: "Data" }, { key: "conteudo", header: "Conteúdo" },
          { key: "presentes", header: "Presentes", align: "right" },
          { key: "acoes", header: "", align: "right", render: (r) => <RowActionButton onClick={() => setEditAula(r)}>Editar</RowActionButton> },
        ]}
        rows={aulas}
      />
      <EditarAulaDialog aula={editAula} onClose={() => setEditAula(null)} onSave={salvar} />
    </div>
  );
}

function EditarAulaDialog({ aula, onClose, onSave }: { aula: AulaRow | null; onClose: () => void; onSave: (orig: AulaRow, novo: AulaRow) => void }) {
  const [data, setData] = useState("");
  const [conteudo, setConteudo] = useState("");
  const [presentes, setPresentes] = useState(0);
  return (
    <Dialog open={!!aula} onOpenChange={(o) => { if (!o) onClose(); else if (aula) { setData(aula.data); setConteudo(aula.conteudo); setPresentes(aula.presentes); } }}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Editar aula</DialogTitle>
          <DialogDescription>Ajuste data, conteúdo e número de presentes.</DialogDescription>
        </DialogHeader>
        <div className="grid grid-cols-2 gap-3">
          <FormField label="Data"><Input className="h-10" value={data} onChange={(e) => setData(e.target.value)} /></FormField>
          <FormField label="Presentes"><Input type="number" className="h-10" value={presentes} onChange={(e) => setPresentes(Number(e.target.value))} /></FormField>
          <FormField label="Conteúdo" full><Textarea rows={3} value={conteudo} onChange={(e) => setConteudo(e.target.value)} /></FormField>
        </div>
        <DialogFooter>
          <Button variant="outline" onClick={onClose}>Cancelar</Button>
          <Button onClick={() => aula && onSave(aula, { data, conteudo, presentes })}>Salvar alterações</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}

function Frequencia() {
  return (
    <div className="rounded-xl border bg-card p-5 shadow-card">
      <SectionTitle title="Chamada — Aula de 12/03/2025" subtitle="Marque presença ou falta para cada estudante." />
      <DataTable className="mt-3"
        columns={[
          { key: "nome", header: "Estudante" }, { key: "freq", header: "% Frequência", align: "right" },
          { key: "marcar", header: "Marcar", align: "right", render: (r) => (
            <div className="flex justify-end gap-1.5"><RowActionButton onClick={() => toast.success(`${r.nome} marcado(a) como presente.`)}>Presente</RowActionButton><RowActionButton tone="danger" onClick={() => toast.error(`${r.nome} marcado(a) como falta.`)}>Falta</RowActionButton></div>
          )},
        ]}
        rows={[
          { nome: "Maria Santos", freq: "92%" },
          { nome: "João Silva", freq: "78%" },
          { nome: "Lucas Pereira", freq: "65%" },
          { nome: "Ana Lima", freq: "88%" },
        ]}
      />
      <div className="mt-4 flex justify-end"><Button onClick={() => toast.success("Chamada salva com sucesso.")}>Salvar chamada</Button></div>
    </div>
  );
}

type Avaliacao = { nome: string; peso: string; prazo: string; status: string };

function Avaliacoes() {
  const [lancando, setLancando] = useState<Avaliacao | null>(null);
  return (
    <div className="space-y-4">
      <DataTable
        columns={[
          { key: "nome", header: "Avaliação" }, { key: "peso", header: "Peso", align: "right" },
          { key: "prazo", header: "Prazo" },
          { key: "status", header: "Status", render: (r) => <StatusBadge tone={r.status === "Notas lançadas" ? "success" : "warning"}>{r.status}</StatusBadge> },
          { key: "acoes", header: "", align: "right", render: (r) => <RowActionButton onClick={() => setLancando(r)}>{r.status === "Notas lançadas" ? "Revisar notas" : "Lançar notas"}</RowActionButton> },
        ]}
        rows={[
          { nome: "P1", peso: "30%", prazo: "20/03/2025", status: "Notas lançadas" },
          { nome: "P2", peso: "30%", prazo: "10/05/2025", status: "Notas lançadas" },
          { nome: "P3", peso: "40%", prazo: "20/06/2025", status: "Pendente" },
        ]}
      />
      <ValidationCallout tone="info">Soma dos pesos: 100% ✓</ValidationCallout>
      <LancarNotasDialog aval={lancando} onClose={() => setLancando(null)} />
    </div>
  );
}

function LancarNotasDialog({ aval, onClose }: { aval: Avaliacao | null; onClose: () => void }) {
  const alunos = ["Maria Santos", "João Silva", "Lucas Pereira", "Ana Lima", "Pedro Almeida"];
  const [notas, setNotas] = useState<Record<string, string>>({});
  return (
    <Dialog open={!!aval} onOpenChange={(o) => !o && onClose()}>
      <DialogContent className="max-w-xl">
        {aval && (
          <>
            <DialogHeader>
              <DialogTitle>Lançar notas — {aval.nome}</DialogTitle>
              <DialogDescription>Peso {aval.peso} · Prazo {aval.prazo}</DialogDescription>
            </DialogHeader>
            <div className="space-y-2 max-h-[360px] overflow-y-auto pr-2">
              {alunos.map((a) => (
                <div key={a} className="flex items-center justify-between gap-3 border-b py-2">
                  <span className="text-[13px] text-foreground">{a}</span>
                  <Input
                    type="number" step="0.1" min="0" max="10"
                    className="h-9 w-24"
                    placeholder="0,0"
                    value={notas[a] ?? ""}
                    onChange={(e) => setNotas({ ...notas, [a]: e.target.value })}
                  />
                </div>
              ))}
            </div>
            <ValidationCallout tone="info">Notas de 0,0 a 10,0. Valores em branco serão considerados pendentes.</ValidationCallout>
            <DialogFooter>
              <Button variant="outline" onClick={onClose}>Cancelar</Button>
              <Button onClick={() => { toast.success(`Notas de ${aval.nome} lançadas (${Object.keys(notas).length}/${alunos.length}).`); onClose(); }}>Salvar notas</Button>
            </DialogFooter>
          </>
        )}
      </DialogContent>
    </Dialog>
  );
}

function Fechar({ turma }: { turma: Turma }) {
  const podeFechar = turma.aulasDadas === turma.aulasTotal;
  return (
    <div className="space-y-4">
      {!podeFechar && <ValidationCallout tone="error">Fechamento bloqueado: ainda há {turma.aulasTotal - turma.aulasDadas} aulas ou avaliações pendentes.</ValidationCallout>}
      <DataTable
        columns={[
          { key: "nome", header: "Estudante" },
          { key: "media", header: "Média", align: "right" },
          { key: "freq", header: "Freq.", align: "right" },
          { key: "situacao", header: "Situação", render: (r) => (
            <StatusBadge tone={r.situacao === "Aprovado" ? "success" : r.situacao === "Recuperação" ? "warning" : "danger"}>{r.situacao}</StatusBadge>
          )},
        ]}
        rows={[
          { nome: "Maria Santos", media: 8.7, freq: "92%", situacao: "Aprovado" },
          { nome: "João Silva", media: 5.4, freq: "78%", situacao: "Recuperação" },
          { nome: "Lucas Pereira", media: 4.1, freq: "65%", situacao: "Reprovado" },
        ]}
      />
      <div className="rounded-xl border bg-card p-4 shadow-card">
        <FormField label="Observação do fechamento" full><Textarea rows={3} /></FormField>
        <div className="mt-3 flex justify-end gap-2">
          <Button variant="outline" onClick={() => toast.success("Rascunho do fechamento salvo.")}>Salvar rascunho</Button>
          <Button disabled={!podeFechar} onClick={() => toast.success("Resultado final fechado e enviado à Secretaria.")}><CheckCircle2 className="mr-2 h-4 w-4" /> Fechar resultado final</Button>
        </div>
      </div>
    </div>
  );
}
