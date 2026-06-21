import { toast } from "sonner";
import { useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import {
  AppShell, SectionTitle, StatsRow, DataTable, StatusBadge, RowActionButton,
  FormField, ValidationCallout, Stepper, SuccessBanner, ProgressRow,
  useProfileSwitcher,
} from "@/components/acadlab";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { ArrowLeft, Calendar, Plus, FileText, Clock, User, CheckCircle, XCircle } from "lucide-react";

export const Route = createFileRoute("/permanencia")({
  head: () => ({ meta: [{ title: "Permanência Acadêmica — AcadLab" }] }),
  component: Page,
});

type Edital = { id: string; nome: string; vagas: number; prazo: string; status: "Aberto" | "Encerrado"; descricao?: string; criterios?: string[] };
type Beneficio = { id: string; nome: string; status: "Ativo" | "Suspenso" | "Em renovação"; vencimento: string; valor?: string; historico?: { data: string; evento: string }[] };
type Inscricao = { id: string; aluno: string; edital: string; renda: string; cr: string; status: "Em análise" | "Deferida" | "Indeferida" | "Pendente doc."; dataEnvio?: string; documentos?: string[]; parecer?: string };

const editaisIniciais: Edital[] = [
  { id: "EDT-2025-04", nome: "Bolsa Institucional 50%", vagas: 30, prazo: "30/03/2025", status: "Aberto", descricao: "Bolsa de permanência estudantil com redução de 50% nas mensalidades.", criterios: ["Renda familiar per capita até 1,5 salário mínimo", "CR mínimo: 7,0", "Matrícula ativa em curso de graduação", "Sem benefício concorrente em vigência"] },
  { id: "EDT-2025-05", nome: "Auxílio Transporte", vagas: 100, prazo: "15/04/2025", status: "Aberto", descricao: "Auxílio mensal para custeio de transporte público.", criterios: ["Residir a mais de 10km do campus", "Frequência mínima de 75%", "Matrícula ativa"] },
  { id: "EDT-2025-03", nome: "Monitoria Remunerada", vagas: 20, prazo: "10/03/2025", status: "Encerrado", descricao: "Programa de monitoria acadêmica remunerada.", criterios: ["CR mínimo: 8,0 na disciplina", "Disponibilidade de 8h/semana", "Recomendação do professor"] },
];

const beneficiosIniciais: Beneficio[] = [
  { id: "BEN-2024-112", nome: "Bolsa Institucional 50%", status: "Ativo", vencimento: "30/06/2025", valor: "R$ 850,00/mês", historico: [
    { data: "01/07/2024", evento: "Benefício concedido — EDT-2024-02" },
    { data: "15/12/2024", evento: "Renovação automática aprovada" },
    { data: "10/01/2025", evento: "Comprovante de renda atualizado" },
  ]},
];

const inscricoesIniciais: Inscricao[] = [
  { id: "INS-2025-201", aluno: "Você", edital: "Monitoria Remunerada", renda: "—", cr: "7,8", status: "Indeferida", dataEnvio: "05/03/2025", documentos: ["Comprovante de matrícula", "Histórico escolar"], parecer: "CR inferior ao exigido (8,0) na disciplina pretendida." },
  { id: "INS-2024-145", aluno: "Você", edital: "Bolsa Institucional 50%", renda: "0,9 sal.", cr: "8,4", status: "Deferida", dataEnvio: "10/02/2024", documentos: ["Comprovante de renda", "Comprovante de residência", "Declaração socioeconômica"], parecer: "Atende todos os critérios de elegibilidade." },
];

type View =
  | { kind: "overview" }
  | { kind: "edital"; id: string }
  | { kind: "inscricao"; step: 0 | 1 | 2; editalId: string }
  | { kind: "recurso"; inscricaoId: string }
  | { kind: "detail-beneficio"; id: string }
  | { kind: "detail-inscricao"; id: string };

function Page() {
  const [view, setView] = useState<View>({ kind: "overview" });
  const [beneficios, setBeneficios] = useState<Beneficio[]>(beneficiosIniciais);
  const [editais, setEditais] = useState<Edital[]>(editaisIniciais);
  const [inscricoes, setInscricoes] = useState<Inscricao[]>(inscricoesIniciais);

  const { active: perfil } = useProfileSwitcher([
    { value: "estudante", label: "Estudante", description: "Inscreve-se e acompanha benefícios" },
    { value: "assistencia", label: "Assistência Estudantil", description: "Analisa pedidos e gere editais" },
  ]);
  const subtitle = perfil === "assistencia"
    ? "Setor de Assistência Estudantil · Análise de pedidos"
    : "Bolsas e auxílios institucionais";

  const handleRenovar = (id: string) => {
    setBeneficios(beneficios.map(b => b.id === id ? { ...b, status: "Em renovação" } : b));
    toast.success("Solicitação de renovação enviada! Nova vigência será processada.");
  };

  const handleSendRecurso = (e: React.FormEvent) => {
    e.preventDefault();
    toast.success("Recurso enviado com sucesso! Aguarde a reanálise.");
    setView({ kind: "overview" });
  };

  if (perfil === "assistencia") {
    return (
      <AppShell title="Permanência Acadêmica" subtitle={subtitle}>
        <AssistenciaView
          editais={editais}
          setEditais={setEditais}
          inscricoes={inscricoes}
          setInscricoes={setInscricoes}
        />
      </AppShell>
    );
  }

  return (
    <AppShell title="Permanência Acadêmica" subtitle={subtitle}>
      {view.kind === "overview" && (
        <div className="space-y-5">
          <StatsRow stats={[
            { label: "Benefícios ativos", value: beneficios.filter((b) => b.status === "Ativo").length, tone: "success" },
            { label: "Editais abertos", value: editais.filter((e) => e.status === "Aberto").length, tone: "info" },
            { label: "Próx. renovação", value: "30/06", tone: "warning" },
            { label: "Inscrições no ano", value: inscricoes.length, tone: "info" },
          ]} />

          <SectionTitle title="Meus benefícios" />
          {beneficios.length === 0 ? (
            <ValidationCallout tone="info">Você não possui benefícios ativos no momento.</ValidationCallout>
          ) : (
            <DataTable
              columns={[
                { key: "id", header: "Protocolo" }, { key: "nome", header: "Benefício" },
                { key: "status", header: "Status", render: (r) => <StatusBadge tone={r.status === "Ativo" ? "success" : r.status === "Suspenso" ? "danger" : "warning"}>{r.status}</StatusBadge> },
                { key: "vencimento", header: "Vence em" },
                { key: "acoes", header: "", align: "right", render: (r) => (
                  <div className="flex justify-end gap-1.5">
                    <RowActionButton onClick={() => handleRenovar(r.id)}>Renovar</RowActionButton>
                    <RowActionButton tone="neutral" onClick={() => setView({ kind: "detail-beneficio", id: r.id })}>Detalhes</RowActionButton>
                  </div>
                ) },
              ]}
              rows={beneficios}
            />
          )}

          <SectionTitle title="Editais disponíveis" />
          <div className="grid gap-3 md:grid-cols-2">
            {editais.map((e) => (
              <div key={e.id} className="rounded-xl border bg-card p-5 shadow-card">
                <div className="flex items-start justify-between">
                  <div>
                    <p className="text-[12px] text-muted-foreground">{e.id}</p>
                    <h3 className="mt-1 font-semibold text-foreground">{e.nome}</h3>
                  </div>
                  <StatusBadge tone={e.status === "Aberto" ? "success" : "neutral"}>{e.status}</StatusBadge>
                </div>
                <div className="mt-3 flex items-center gap-4 text-[12px] text-muted-foreground">
                  <span>{e.vagas} vagas</span>
                  <span className="flex items-center gap-1"><Calendar className="h-3.5 w-3.5" /> até {e.prazo}</span>
                </div>
                <Button className="mt-3 w-full" disabled={e.status !== "Aberto"} onClick={() => setView({ kind: "edital", id: e.id })}>Ver edital</Button>
              </div>
            ))}
          </div>

          <div className="rounded-xl border bg-card p-5 shadow-card">
            <SectionTitle title="Inscrições anteriores" />
            <DataTable className="mt-3"
              columns={[
                { key: "id", header: "Protocolo" }, { key: "edital", header: "Edital" },
                { key: "status", header: "Status", render: (r) => <StatusBadge tone={r.status === "Deferida" ? "success" : r.status === "Indeferida" ? "danger" : "warning"}>{r.status}</StatusBadge> },
                { key: "acoes", header: "", align: "right", render: (r) => (
                  <div className="flex justify-end gap-1.5">
                    <RowActionButton tone="neutral" onClick={() => setView({ kind: "detail-inscricao", id: r.id })}>Detalhes</RowActionButton>
                    {r.status === "Indeferida" && <RowActionButton onClick={() => setView({ kind: "recurso", inscricaoId: r.id })}>Interpor recurso</RowActionButton>}
                  </div>
                ) },
              ]}
              rows={inscricoes}
            />
          </div>
        </div>
      )}

      {view.kind === "edital" && (() => {
        const e = editais.find((x) => x.id === view.id)!;
        return (
          <div className="space-y-4">
            <Button variant="ghost" size="sm" onClick={() => setView({ kind: "overview" })}><ArrowLeft className="mr-1 h-4 w-4" /> Voltar</Button>
            <SectionTitle title={`${e.id} — ${e.nome}`} subtitle={`${e.vagas} vagas · inscrições até ${e.prazo}`} />
            <div className="rounded-xl border bg-card p-5 shadow-card">
              <h3 className="font-semibold">Descrição</h3>
              <p className="mt-2 text-[13px] text-muted-foreground">{e.descricao}</p>
            </div>
            <div className="rounded-xl border bg-card p-5 shadow-card">
              <h3 className="font-semibold">Critérios de elegibilidade</h3>
              <ul className="mt-2 list-disc pl-5 text-[13px] text-muted-foreground">
                {e.criterios?.map((c, i) => <li key={i}>{c}</li>)}
              </ul>
            </div>
            <div className="flex justify-end gap-2">
              <Button variant="outline" onClick={() => setView({ kind: "overview" })}>Voltar</Button>
              {e.status === "Aberto" && <Button onClick={() => setView({ kind: "inscricao", step: 0, editalId: e.id })}>Inscrever-se</Button>}
            </div>
          </div>
        );
      })()}

      {view.kind === "inscricao" && (
        <InscricaoWizard step={view.step} onStep={(s) => setView({ kind: "inscricao", step: s, editalId: view.editalId })} onDone={() => setView({ kind: "overview" })} />
      )}

      {view.kind === "recurso" && (
        <div className="space-y-4">
          <Button variant="ghost" size="sm" onClick={() => setView({ kind: "overview" })}><ArrowLeft className="mr-1 h-4 w-4" /> Voltar</Button>
          <form onSubmit={handleSendRecurso} className="rounded-xl border bg-card p-6 shadow-card">
            <SectionTitle title="Interpor recurso" subtitle={`Inscrição ${view.inscricaoId} · Apenas um recurso por inscrição é permitido.`} />
            <FormField className="mt-4" label="Justificativa" required full><Textarea rows={5} required /></FormField>
            <FormField label="Documentação complementar" full><Input type="file" className="h-10" /></FormField>
            <div className="mt-4 flex justify-end gap-2"><Button type="button" variant="outline" onClick={() => setView({ kind: "overview" })}>Cancelar</Button><Button type="submit">Enviar recurso</Button></div>
          </form>
        </div>
      )}

      {view.kind === "detail-beneficio" && (() => {
        const b = beneficios.find((x) => x.id === view.id)!;
        return (
          <div className="space-y-4">
            <Button variant="ghost" size="sm" onClick={() => setView({ kind: "overview" })}><ArrowLeft className="mr-1 h-4 w-4" /> Voltar</Button>
            <SectionTitle title={b.nome} subtitle={`Protocolo ${b.id}`} />
            <div className="grid gap-4 md:grid-cols-2">
              <div className="rounded-xl border bg-card p-5 shadow-card space-y-3">
                <h3 className="font-semibold flex items-center gap-2"><FileText className="h-4 w-4" /> Informações</h3>
                <div className="text-[13px]"><span className="text-muted-foreground">Status:</span> <StatusBadge tone={b.status === "Ativo" ? "success" : b.status === "Suspenso" ? "danger" : "warning"}>{b.status}</StatusBadge></div>
                <div className="text-[13px]"><span className="text-muted-foreground">Vencimento:</span> {b.vencimento}</div>
                {b.valor && <div className="text-[13px]"><span className="text-muted-foreground">Valor:</span> {b.valor}</div>}
              </div>
              <div className="rounded-xl border bg-card p-5 shadow-card space-y-3">
                <h3 className="font-semibold flex items-center gap-2"><Clock className="h-4 w-4" /> Histórico</h3>
                <div className="space-y-2">
                  {b.historico?.map((h, i) => (
                    <div key={i} className="flex gap-3 text-[13px]">
                      <span className="text-muted-foreground whitespace-nowrap">{h.data}</span>
                      <span>{h.evento}</span>
                    </div>
                  ))}
                </div>
              </div>
            </div>
            <div className="flex justify-end gap-2">
              <Button variant="outline" onClick={() => setView({ kind: "overview" })}>Voltar</Button>
              {b.status === "Ativo" && <Button onClick={() => handleRenovar(b.id)}>Solicitar renovação</Button>}
            </div>
          </div>
        );
      })()}

      {view.kind === "detail-inscricao" && (() => {
        const ins = inscricoes.find((x) => x.id === view.id)!;
        return (
          <div className="space-y-4">
            <Button variant="ghost" size="sm" onClick={() => setView({ kind: "overview" })}><ArrowLeft className="mr-1 h-4 w-4" /> Voltar</Button>
            <SectionTitle title={`Inscrição ${ins.id}`} subtitle={ins.edital} />
            <div className="grid gap-4 md:grid-cols-2">
              <div className="rounded-xl border bg-card p-5 shadow-card space-y-3">
                <h3 className="font-semibold flex items-center gap-2"><User className="h-4 w-4" /> Dados da inscrição</h3>
                <div className="text-[13px]"><span className="text-muted-foreground">Status:</span> <StatusBadge tone={ins.status === "Deferida" ? "success" : ins.status === "Indeferida" ? "danger" : "warning"}>{ins.status}</StatusBadge></div>
                <div className="text-[13px]"><span className="text-muted-foreground">Data de envio:</span> {ins.dataEnvio}</div>
                <div className="text-[13px]"><span className="text-muted-foreground">Renda per capita:</span> {ins.renda}</div>
                <div className="text-[13px]"><span className="text-muted-foreground">CR:</span> {ins.cr}</div>
              </div>
              <div className="rounded-xl border bg-card p-5 shadow-card space-y-3">
                <h3 className="font-semibold flex items-center gap-2"><FileText className="h-4 w-4" /> Documentos enviados</h3>
                <ul className="list-disc pl-5 text-[13px] text-muted-foreground">
                  {ins.documentos?.map((d, i) => <li key={i}>{d}</li>)}
                </ul>
              </div>
            </div>
            {ins.parecer && (
              <div className="rounded-xl border bg-card p-5 shadow-card">
                <h3 className="font-semibold flex items-center gap-2">
                  {ins.status === "Deferida" ? <CheckCircle className="h-4 w-4 text-green-500" /> : <XCircle className="h-4 w-4 text-red-500" />}
                  Parecer
                </h3>
                <p className="mt-2 text-[13px] text-muted-foreground">{ins.parecer}</p>
              </div>
            )}
            <div className="flex justify-end gap-2">
              <Button variant="outline" onClick={() => setView({ kind: "overview" })}>Voltar</Button>
              {ins.status === "Indeferida" && <Button onClick={() => setView({ kind: "recurso", inscricaoId: ins.id })}>Interpor recurso</Button>}
            </div>
          </div>
        );
      })()}
    </AppShell>
  );
}

const steps = [{ key: "elig", label: "Elegibilidade" }, { key: "doc", label: "Documentos" }, { key: "ok", label: "Confirmação" }];

function InscricaoWizard({ step, onStep, onDone }: { step: 0 | 1 | 2; onStep: (s: 0 | 1 | 2) => void; onDone: () => void }) {
  return (
    <div className="space-y-5">
      <Button variant="ghost" size="sm" onClick={onDone}><ArrowLeft className="mr-1 h-4 w-4" /> Cancelar</Button>
      <Stepper steps={steps} current={step} />
      {step === 0 && (
        <div className="rounded-xl border bg-card p-6 shadow-card">
          <SectionTitle title="Verificação de elegibilidade" />
          <div className="mt-4 space-y-3">
            <ProgressRow label="CR mínimo (7,0)" current={8} total={10} unit="" tone="success" />
            <ProgressRow label="Renda per capita" current={1} total={2} unit="sal." tone="success" />
            <ProgressRow label="Matrícula ativa" current={1} total={1} unit="" tone="success" />
          </div>
          <ValidationCallout className="mt-4" tone="info">Você atende a todos os critérios.</ValidationCallout>
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
          <div className="mt-4 flex justify-end gap-2"><Button variant="outline" onClick={() => onStep(0)}>Voltar</Button><Button onClick={() => onStep(2)}>Enviar inscrição</Button></div>
        </div>
      )}
      {step === 2 && (
        <div className="space-y-4">
          <SuccessBanner title="Inscrição registrada!" description="Protocolo INS-2025-220 · Aguardando análise da Assistência Estudantil." />
          <Button onClick={onDone}>Voltar</Button>
        </div>
      )}
    </div>
  );
}

type InscricaoAssistencia = { id: string; aluno: string; edital: string; renda: string; cr: string; status: "Em análise" | "Deferida" | "Indeferida" | "Pendente doc."; dataEnvio?: string; documentos?: string[]; parecer?: string; matricula?: string; curso?: string; periodo?: string };

const inscricoesAssistenciaIniciais: InscricaoAssistencia[] = [
  { id: "INS-2025-219", aluno: "Maria Santos", edital: "Bolsa Institucional 50%", renda: "0,9 sal.", cr: "8,4", status: "Em análise", dataEnvio: "12/03/2025", documentos: ["Comprovante de renda", "Comprovante de residência"], matricula: "202310245", curso: "Engenharia Civil", periodo: "4º" },
  { id: "INS-2025-220", aluno: "Pedro Almeida", edital: "Auxílio Transporte", renda: "1,2 sal.", cr: "7,1", status: "Em análise", dataEnvio: "14/03/2025", documentos: ["Comprovante de matrícula", "Comprovante de residência", "Declaração de transporte"], matricula: "202210112", curso: "Direito", periodo: "6º" },
  { id: "INS-2025-221", aluno: "Júlia Rocha", edital: "Bolsa Institucional 50%", renda: "1,6 sal.", cr: "9,0", status: "Pendente doc.", dataEnvio: "10/03/2025", documentos: ["Comprovante de renda"], matricula: "202410778", curso: "Medicina", periodo: "2º" },
  { id: "INS-2025-205", aluno: "Lucas Pires", edital: "Monitoria Remunerada", renda: "—", cr: "8,8", status: "Deferida", dataEnvio: "01/03/2025", documentos: ["Histórico escolar", "Carta de recomendação"], matricula: "202115903", curso: "Física", periodo: "8º" },
];

function AssistenciaView({ editais, setEditais, inscricoes, setInscricoes }: { editais: Edital[], setEditais: (e: Edital[]) => void, inscricoes: Inscricao[], setInscricoes: (i: Inscricao[]) => void }) {
  const [inscricoesAssistencia, setInscricoesAssistencia] = useState<InscricaoAssistencia[]>(inscricoesAssistenciaIniciais);
  const [showAdd, setShowAdd] = useState(false);
  const [newEdital, setNewEdital] = useState({ nome: "", vagas: 0, prazo: "" });
  const [detailInscricao, setDetailInscricao] = useState<InscricaoAssistencia | null>(null);
  const [detailEdital, setDetailEdital] = useState<Edital | null>(null);

  const handlePublish = (e: React.FormEvent) => {
    e.preventDefault();
    const ed: Edital = {
      id: "EDT-2025-" + (editais.length + 1).toString().padStart(2, "0"),
      nome: newEdital.nome,
      vagas: newEdital.vagas,
      prazo: newEdital.prazo,
      status: "Aberto",
      descricao: "Novo edital publicado.",
      criterios: ["Renda familiar per capita até 1,5 salário mínimo", "CR mínimo: 7,0"],
    };
    setEditais([...editais, ed]);
    setShowAdd(false);
    toast.success("Edital publicado com sucesso!");
  };

  const handleEncerrar = (id: string) => {
    setEditais(editais.map(e => e.id === id ? { ...e, status: "Encerrado" } : e));
    toast.info("Edital encerrado.");
  };

  const handleUpdateInscricao = (id: string, status: InscricaoAssistencia["status"]) => {
    setInscricoesAssistencia(inscricoesAssistencia.map(i => i.id === id ? { ...i, status } : i));
    if (status === "Deferida") toast.success("Inscrição deferida!");
    if (status === "Indeferida") toast.error("Inscrição indeferida.");
  };

  if (detailInscricao) {
    return (
      <div className="space-y-4">
        <Button variant="ghost" size="sm" onClick={() => setDetailInscricao(null)}><ArrowLeft className="mr-1 h-4 w-4" /> Voltar</Button>
        <SectionTitle title={`Análise ${detailInscricao.id}`} subtitle={`${detailInscricao.aluno} · ${detailInscricao.edital}`} />
        <div className="grid gap-4 md:grid-cols-2">
          <div className="rounded-xl border bg-card p-5 shadow-card space-y-3">
            <h3 className="font-semibold flex items-center gap-2"><User className="h-4 w-4" /> Dados do aluno</h3>
            <div className="text-[13px]"><span className="text-muted-foreground">Nome:</span> {detailInscricao.aluno}</div>
            <div className="text-[13px]"><span className="text-muted-foreground">Matrícula:</span> {detailInscricao.matricula}</div>
            <div className="text-[13px]"><span className="text-muted-foreground">Curso:</span> {detailInscricao.curso}</div>
            <div className="text-[13px]"><span className="text-muted-foreground">Período:</span> {detailInscricao.periodo}</div>
            <div className="text-[13px]"><span className="text-muted-foreground">CR:</span> {detailInscricao.cr}</div>
            <div className="text-[13px]"><span className="text-muted-foreground">Renda per capita:</span> {detailInscricao.renda}</div>
          </div>
          <div className="rounded-xl border bg-card p-5 shadow-card space-y-3">
            <h3 className="font-semibold flex items-center gap-2"><FileText className="h-4 w-4" /> Documentação</h3>
            <ul className="list-disc pl-5 text-[13px] text-muted-foreground">
              {detailInscricao.documentos?.map((d, i) => <li key={i}>{d}</li>)}
            </ul>
            <div className="mt-2 text-[13px]"><span className="text-muted-foreground">Data de envio:</span> {detailInscricao.dataEnvio}</div>
            <div className="mt-2 text-[13px]"><span className="text-muted-foreground">Status atual:</span> <StatusBadge tone={detailInscricao.status === "Deferida" ? "success" : detailInscricao.status === "Indeferida" ? "danger" : "warning"}>{detailInscricao.status}</StatusBadge></div>
          </div>
        </div>
        {detailInscricao.status === "Em análise" || detailInscricao.status === "Pendente doc." ? (
          <div className="rounded-xl border bg-card p-5 shadow-card">
            <h3 className="font-semibold mb-3">Decisão</h3>
            <div className="flex gap-2">
              <Button variant="outline" className="text-red-600 border-red-200 hover:bg-red-50" onClick={() => { handleUpdateInscricao(detailInscricao.id, "Indeferida"); setDetailInscricao(null); }}><XCircle className="mr-1 h-4 w-4" /> Indeferir</Button>
              <Button className="bg-green-600 hover:bg-green-700" onClick={() => { handleUpdateInscricao(detailInscricao.id, "Deferida"); setDetailInscricao(null); }}><CheckCircle className="mr-1 h-4 w-4" /> Deferir</Button>
            </div>
          </div>
        ) : (
          <div className="rounded-xl border bg-card p-5 shadow-card">
            <h3 className="font-semibold mb-2">Parecer final</h3>
            <p className="text-[13px] text-muted-foreground">Inscrição {detailInscricao.status.toLowerCase()} em {detailInscricao.dataEnvio}.</p>
          </div>
        )}
      </div>
    );
  }

  if (detailEdital) {
    return (
      <div className="space-y-4">
        <Button variant="ghost" size="sm" onClick={() => setDetailEdital(null)}><ArrowLeft className="mr-1 h-4 w-4" /> Voltar</Button>
        <SectionTitle title={`${detailEdital.id} — ${detailEdital.nome}`} subtitle={`${detailEdital.vagas} vagas · inscrições até ${detailEdital.prazo}`} />
        <div className="rounded-xl border bg-card p-5 shadow-card">
          <h3 className="font-semibold">Descrição</h3>
          <p className="mt-2 text-[13px] text-muted-foreground">{detailEdital.descricao}</p>
        </div>
        <div className="rounded-xl border bg-card p-5 shadow-card">
          <h3 className="font-semibold">Critérios</h3>
          <ul className="mt-2 list-disc pl-5 text-[13px] text-muted-foreground">
            {detailEdital.criterios?.map((c, i) => <li key={i}>{c}</li>)}
          </ul>
        </div>
        <div className="rounded-xl border bg-card p-5 shadow-card">
          <h3 className="font-semibold">Estatísticas</h3>
          <StatsRow className="mt-3" stats={[
            { label: "Inscrições recebidas", value: inscricoesAssistencia.filter(i => i.edital === detailEdital.nome).length, tone: "info" },
            { label: "Em análise", value: inscricoesAssistencia.filter(i => i.edital === detailEdital.nome && i.status === "Em análise").length, tone: "warning" },
            { label: "Deferidas", value: inscricoesAssistencia.filter(i => i.edital === detailEdital.nome && i.status === "Deferida").length, tone: "success" },
          ]} />
        </div>
        <div className="flex justify-end">
          <Button variant="outline" onClick={() => setDetailEdital(null)}>Voltar</Button>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-5">
      <StatsRow stats={[
        { label: "Pedidos aguardando", value: inscricoesAssistencia.filter((i) => i.status === "Em análise").length, tone: "warning" },
        { label: "Pendentes de documento", value: inscricoesAssistencia.filter((i) => i.status === "Pendente doc.").length, tone: "danger" },
        { label: "Deferidas no mês", value: inscricoesAssistencia.filter((i) => i.status === "Deferida").length, tone: "success" },
        { label: "Editais vigentes", value: editais.filter((e) => e.status === "Aberto").length, tone: "info" },
      ]} />

      <div className="flex items-center justify-between">
        <SectionTitle title="Editais publicados" />
        <Button size="sm" onClick={() => setShowAdd(!showAdd)}><Plus className="mr-1 h-4 w-4" /> Publicar edital</Button>
      </div>

      {showAdd && (
        <form onSubmit={handlePublish} className="rounded-xl border bg-card p-5 shadow-card animate-in fade-in slide-in-from-top-2">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <FormField label="Título do Edital" required><Input value={newEdital.nome} onChange={e => setNewEdital({ ...newEdital, nome: e.target.value })} required /></FormField>
            <FormField label="Vagas" required><Input type="number" value={newEdital.vagas} onChange={e => setNewEdital({ ...newEdital, vagas: parseInt(e.target.value) })} required /></FormField>
            <FormField label="Prazo (DD/MM/AAAA)" required><Input value={newEdital.prazo} onChange={e => setNewEdital({ ...newEdital, prazo: e.target.value })} required /></FormField>
          </div>
          <div className="mt-4 flex justify-end gap-2">
            <Button type="button" variant="outline" onClick={() => setShowAdd(false)}>Cancelar</Button>
            <Button type="submit">Publicar</Button>
          </div>
        </form>
      )}

      <DataTable
        columns={[
          { key: "id", header: "Código" }, { key: "nome", header: "Edital" },
          { key: "vagas", header: "Vagas", align: "right" }, { key: "prazo", header: "Prazo" },
          { key: "status", header: "Status", render: (r) => <StatusBadge tone={r.status === "Aberto" ? "success" : "neutral"}>{r.status}</StatusBadge> },
          { key: "acoes", header: "", align: "right", render: (r) => (
            <div className="flex justify-end gap-1.5">
              {r.status === "Aberto" && <RowActionButton tone="danger" onClick={() => handleEncerrar(r.id)}>Encerrar</RowActionButton>}
              <RowActionButton tone="neutral" onClick={() => setDetailEdital(r)}>Detalhes</RowActionButton>
            </div>
          )},
        ]}
        rows={editais}
      />
      <SectionTitle title="Fila de análise" />
      <DataTable
        columns={[
          { key: "id", header: "Protocolo" }, { key: "aluno", header: "Aluno" },
          { key: "edital", header: "Edital" }, { key: "renda", header: "Renda per capita", align: "right" },
          { key: "cr", header: "CR", align: "right" },
          { key: "status", header: "Status", render: (r) => (
            <StatusBadge tone={r.status === "Deferida" ? "success" : r.status === "Indeferida" ? "danger" : r.status === "Pendente doc." ? "warning" : "info"}>{r.status}</StatusBadge>
          )},
          { key: "acoes", header: "", align: "right", render: (r) => (
            r.status === "Em análise" || r.status === "Pendente doc." ? (
              <div className="flex justify-end gap-1.5">
                <RowActionButton tone="danger" onClick={() => handleUpdateInscricao(r.id, "Indeferida")}>Indeferir</RowActionButton>
                <RowActionButton onClick={() => handleUpdateInscricao(r.id, "Deferida")}>Deferir</RowActionButton>
                <RowActionButton tone="neutral" onClick={() => setDetailInscricao(r)}>Detalhes</RowActionButton>
              </div>
            ) : <RowActionButton tone="neutral" onClick={() => setDetailInscricao(r)}>Detalhes</RowActionButton>
          )},
        ]}
        rows={inscricoesAssistencia}
      />
    </div>
  );
}
