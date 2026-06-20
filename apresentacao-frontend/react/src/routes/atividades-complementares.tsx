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
import {
  useAtividadesEstudante, useSaldoEstudante, useCategorias, useAtividadesPendentes,
  useSubmeter, useDeferir, useIndeferir, useSolicitarRevisao, useCancelar,
  EXIGENCIA_TOTAL_HORAS,
  type AtividadeComplementarResumo, type CategoriaHorasResumo, type StatusAtividade,
} from "@/lib/atividades";

export const Route = createFileRoute("/atividades-complementares")({
  head: () => ({ meta: [{ title: "Atividades Complementares — AcadLab" }] }),
  component: Page,
});

type View = { kind: "list" } | { kind: "wizard"; step: 0 | 1 | 2 } | { kind: "revisao"; id: string };

const rotuloStatus: Record<StatusAtividade, string> = {
  PENDENTE: "Em análise",
  REVISAO_SOLICITADA: "Em revisão",
  DEFERIDA: "Deferida",
  INDEFERIDA: "Indeferida",
  CANCELADA: "Cancelada",
};
const tomStatus = (s: StatusAtividade) =>
  s === "DEFERIDA" ? "success" : s === "INDEFERIDA" ? "danger" : "warning";

function Page() {
  const { active: perfil } = useProfileSwitcher([
    { value: "estudante", label: "Estudante", description: "Envia atividades e acompanha" },
    { value: "coordenacao", label: "Coordenação Acadêmica", description: "Valida e defere atividades" },
  ]);
  const [view, setView] = useState<View>({ kind: "list" });
  const [ver, setVer] = useState<AtividadeComplementarResumo | null>(null);

  const atividadesQuery = useAtividadesEstudante();
  const saldoQuery = useSaldoEstudante();
  const categoriasQuery = useCategorias();
  const atividades = atividadesQuery.data ?? [];
  const saldo = saldoQuery.data ?? {};
  const categorias = categoriasQuery.data ?? [];
  const cancelar = useCancelar();

  const horasValidadas = atividades.filter((a) => a.status === "DEFERIDA")
    .reduce((s, a) => s + a.horasAprovadas, 0);
  const horasEmAnalise = atividades.filter((a) => a.status === "PENDENTE" || a.status === "REVISAO_SOLICITADA")
    .reduce((s, a) => s + a.horasSubmetidas, 0);
  const horasIndeferidas = atividades.filter((a) => a.status === "INDEFERIDA")
    .reduce((s, a) => s + a.horasSubmetidas, 0);
  const nomeCategoria = (id: number) => categorias.find((c) => c.id === id)?.nome ?? `Categoria #${id}`;

  const subtitle = perfil === "coordenacao"
    ? "Visão Coordenação · Validação de atividades"
    : `Estudante #1 · saldo de horas`;

  return (
    <AppShell title="Atividades Complementares" subtitle={subtitle}>
      {perfil === "coordenacao" && <CoordView />}

      {perfil === "estudante" && view.kind === "list" && (
        <div className="space-y-5">
          <StatsRow stats={[
            { label: "Horas validadas", value: horasValidadas, tone: "success" },
            { label: "Em análise", value: horasEmAnalise, tone: "warning" },
            { label: "Indeferidas", value: horasIndeferidas, tone: "danger" },
            { label: "Exigência total", value: EXIGENCIA_TOTAL_HORAS, tone: "info" },
          ]} />
          <div className="rounded-xl border bg-card p-5 shadow-card">
            <SectionTitle title="Saldo de horas por categoria" />
            <div className="mt-3 grid gap-3 lg:grid-cols-2">
              {categorias.map((c) => {
                const atual = saldo[c.id] ?? 0;
                return (
                  <ProgressRow
                    key={c.id}
                    label={c.nome}
                    current={atual}
                    total={c.limiteHoras}
                    tone={atual >= c.limiteHoras ? "success" : "warning"}
                  />
                );
              })}
            </div>
          </div>

          <ActionBar searchPlaceholder="Buscar atividade..." primaryLabel="Submeter atividade" onPrimary={() => setView({ kind: "wizard", step: 0 })} />
          <DataTable
            columns={[
              { key: "id", header: "Protocolo", render: (r) => `AC-${r.id}` },
              { key: "categoriaId", header: "Categoria", render: (r) => nomeCategoria(r.categoriaId) },
              { key: "descricao", header: "Descrição" },
              { key: "horasSubmetidas", header: "CH", align: "right" },
              { key: "status", header: "Status", render: (r) => (
                <StatusBadge tone={tomStatus(r.status)}>{rotuloStatus[r.status]}</StatusBadge>
              )},
              { key: "acoes", header: "", align: "right", render: (r) => (
                r.status === "INDEFERIDA"
                  ? <RowActionButton onClick={() => setView({ kind: "revisao", id: String(r.id) })}>Solicitar revisão</RowActionButton>
                  : r.status === "PENDENTE"
                    ? <RowActionButton tone="danger" onClick={() => cancelar.mutate(r.id, { onSuccess: () => toast.success(`Solicitação AC-${r.id} cancelada.`) })}>Cancelar</RowActionButton>
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
                <DialogTitle>AC-{ver.id}</DialogTitle>
                <DialogDescription>{nomeCategoria(ver.categoriaId)}</DialogDescription>
              </DialogHeader>
              <div className="space-y-2 text-[13px]">
                <div className="flex justify-between border-b py-2"><span className="text-muted-foreground">Descrição</span><span className="font-medium text-right max-w-[60%]">{ver.descricao}</span></div>
                <div className="flex justify-between border-b py-2"><span className="text-muted-foreground">Carga horária</span><span className="font-medium">{ver.horasSubmetidas}h</span></div>
                <div className="flex justify-between border-b py-2"><span className="text-muted-foreground">Status</span><StatusBadge tone={tomStatus(ver.status)}>{rotuloStatus[ver.status]}</StatusBadge></div>
                {ver.status === "DEFERIDA" && <ValidationCallout tone="info">Horas computadas no saldo da categoria.</ValidationCallout>}
              </div>
              <DialogFooter>
                <Button variant="outline" onClick={() => setVer(null)}>Fechar</Button>
                <Button onClick={() => { toast.success(`Comprovante de AC-${ver.id} baixado.`); setVer(null); }}>Baixar comprovante</Button>
              </DialogFooter>
            </>
          )}
        </DialogContent>
      </Dialog>
    </AppShell>
  );
}

const steps = [{ key: "cat", label: "Categoria" }, { key: "dados", label: "Dados" }, { key: "ok", label: "Confirmação" }];

function SubmeterWizard({ step, onStep, onDone }: { step: 0 | 1 | 2; onStep: (s: 0 | 1 | 2) => void; onDone: () => void }) {
  const categoriasQuery = useCategorias();
  const categorias = categoriasQuery.data ?? [];
  const submeter = useSubmeter();
  const [catId, setCatId] = useState<number | null>(null);
  const [descricao, setDescricao] = useState("");
  const [horas, setHoras] = useState<number>(0);
  const [data, setData] = useState("");
  const [certificado, setCertificado] = useState("");

  const catNome = categorias.find((c) => c.id === catId)?.nome ?? "";

  return (
    <div className="space-y-5">
      <Button variant="ghost" size="sm" onClick={onDone}><ArrowLeft className="mr-1 h-4 w-4" /> Cancelar</Button>
      <Stepper steps={steps} current={step} />
      {step === 0 && (
        <div className="rounded-xl border bg-card p-6 shadow-card">
          <SectionTitle title="Categoria da atividade" />
          <div className="mt-4 grid grid-cols-2 gap-2">
            {categorias.map((c) => (
              <button key={c.id} onClick={() => setCatId(c.id)} className={`rounded-lg border p-3 text-left text-[13px] transition-colors ${catId === c.id ? "border-primary bg-primary-soft text-primary" : "border-border hover:bg-primary/5"}`}>{c.nome}</button>
            ))}
          </div>
          <div className="mt-4 flex justify-end"><Button disabled={catId == null} onClick={() => onStep(1)}>Avançar</Button></div>
        </div>
      )}
      {step === 1 && (
        <div className="rounded-xl border bg-card p-6 shadow-card">
          <SectionTitle title={catNome} />
          <div className="mt-4 grid grid-cols-2 gap-4">
            <FormField label="Descrição" required full><Textarea rows={2} value={descricao} onChange={(e) => setDescricao(e.target.value)} /></FormField>
            <FormField label="Carga horária (h)" required><Input type="number" className="h-10" value={horas || ""} onChange={(e) => setHoras(Number(e.target.value))} /></FormField>
            <FormField label="Data de realização" required><Input type="date" className="h-10" value={data} onChange={(e) => setData(e.target.value)} /></FormField>
            <FormField label="Comprovante" required full><Input type="file" className="h-10" onChange={(e) => setCertificado(e.target.files?.[0]?.name ?? "")} /></FormField>
          </div>
          <ValidationCallout className="mt-3" tone="info">O mesmo comprovante não pode ser usado em duas atividades diferentes.</ValidationCallout>
          <div className="mt-4 flex justify-end gap-2">
            <Button variant="outline" onClick={() => onStep(0)}>Voltar</Button>
            <Button onClick={() => {
              if (catId == null) return;
              submeter.mutate(
                { categoriaId: catId, horas, dataRealizacao: data, identificadorCertificado: certificado, descricao },
                { onSuccess: () => onStep(2) },
              );
            }}>Submeter</Button>
          </div>
        </div>
      )}
      {step === 2 && (
        <div className="space-y-4">
          <SuccessBanner title="Atividade submetida!" description="Protocolo registrado · Aguardando análise da coordenação." />
          <Button onClick={onDone}>Voltar à lista</Button>
        </div>
      )}
    </div>
  );
}

function Revisao({ id, onBack }: { id: string; onBack: () => void }) {
  const solicitar = useSolicitarRevisao();
  const [justificativa, setJustificativa] = useState("");
  return (
    <div className="space-y-4">
      <Button variant="ghost" size="sm" onClick={onBack}><ArrowLeft className="mr-1 h-4 w-4" /> Voltar</Button>
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title={`Solicitar revisão — AC-${id}`} subtitle="A revisão será analisada pela coordenação." />
        <div className="mt-4 grid grid-cols-2 gap-4">
          <FormField label="Horas pleiteadas" required><Input className="h-10" defaultValue="30" /></FormField>
          <FormField label="Justificativa" required full><Textarea rows={4} value={justificativa} onChange={(e) => setJustificativa(e.target.value)} /></FormField>
          <FormField label="Documento complementar" full><Input type="file" className="h-10" /></FormField>
        </div>
        <div className="mt-4 flex justify-end gap-2">
          <Button variant="outline" onClick={onBack}>Cancelar</Button>
          <Button onClick={() => solicitar.mutate(
            { id: Number(id), justificativa },
            { onSuccess: () => { toast.success(`Revisão de AC-${id} enviada.`); onBack(); } },
          )}>Enviar revisão</Button>
        </div>
      </div>
    </div>
  );
}

function CoordView() {
  const pendentesQuery = useAtividadesPendentes();
  const categoriasQuery = useCategorias();
  const fila = pendentesQuery.data ?? [];
  const categorias = categoriasQuery.data ?? [];
  const nomeCategoria = (id: number) => categorias.find((c) => c.id === id)?.nome ?? `Categoria #${id}`;
  const deferir = useDeferir();
  const indeferir = useIndeferir();

  return (
    <div className="space-y-5">
      <StatsRow stats={[
        { label: "Aguardando validação", value: fila.length, tone: "warning" },
        { label: "Deferidas (mês)", value: 64, tone: "success" },
        { label: "Indeferidas (mês)", value: 12, tone: "danger" },
        { label: "Estudantes ativos", value: 312, tone: "info" },
      ]} />
      <SectionTitle title="Fila de validação" subtitle="Atividades complementares submetidas aguardando análise." />
      <DataTable
        columns={[
          { key: "id", header: "Protocolo", render: (r) => `AC-${r.id}` },
          { key: "estudanteId", header: "Estudante", render: (r) => `Estudante #${r.estudanteId}` },
          { key: "categoriaId", header: "Categoria", render: (r) => nomeCategoria(r.categoriaId) },
          { key: "horasSubmetidas", header: "CH", align: "right" },
          { key: "status", header: "Status", render: (r) => <StatusBadge tone="warning">Em análise</StatusBadge> },
          { key: "acoes", header: "", align: "right", render: (r) => (
            <div className="flex justify-end gap-2">
              <RowActionButton onClick={() => indeferir.mutate({ id: r.id, justificativa: "Indeferida pela coordenação." }, { onSuccess: () => toast.success(`Atividade AC-${r.id} indeferida.`) })}>Indeferir</RowActionButton>
              <RowActionButton tone="info" onClick={() => deferir.mutate({ id: r.id, horasAprovadas: r.horasSubmetidas }, { onSuccess: () => toast.success(`Atividade AC-${r.id} validada.`) })}>Validar</RowActionButton>
            </div>
          )},
        ]}
        rows={fila}
      />
    </div>
  );
}
