import { createFileRoute } from "@tanstack/react-router";
import { useQuery } from "@tanstack/react-query";
import {
  FeaturePage, StatsRow, FormField, SuccessBanner, SectionTitle, ProgressRow,
  ValidationCallout,
} from "@/components/acadlab";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";
import { CheckCircle2, AlertCircle } from "lucide-react";
import { api } from "@/lib/api";

export const Route = createFileRoute("/integralizacao")({
  head: () => ({ meta: [{ title: "Integralização — AcadLab" }] }),
  component: Page,
});

function Painel() {
  const { data = [], isLoading, isError } = useQuery({
    queryKey: ["integralizacoes"],
    queryFn: () => api.integralizacao.listAll(),
  });

  const aptos = data.filter((i) => i.status === "APTO").length;
  const pendentes = data.filter((i) => i.status === "PENDENTE").length;
  const aprovados = data.filter((i) => i.status === "APROVADO").length;

  return (
    <>
      <StatsRow stats={[
        { label: "Total de Análises", value: isLoading ? "…" : data.length, tone: "info" },
        { label: "Aptos", value: isLoading ? "…" : aptos, tone: "success" },
        { label: "Pendentes", value: isLoading ? "…" : pendentes, tone: "warning" },
        { label: "Aprovados p/ colação", value: isLoading ? "…" : aprovados, tone: "success" },
      ]} />
      {isError && <p className="text-sm text-destructive px-1">Não foi possível conectar ao servidor.</p>}
    </>
  );
}

function Solicitar() {
  return (
    <div className="rounded-xl border bg-card p-6 shadow-card">
      <SectionTitle title="Solicitar Análise de Conclusão de Curso" />
      <p className="mt-2 text-[13px] text-muted-foreground">Confirme seus dados e solicite a análise oficial de integralização.</p>
      <div className="mt-4 grid grid-cols-2 gap-4">
        <FormField label="Matrícula"><Input className="h-10" defaultValue="2023001" /></FormField>
        <FormField label="Curso"><Input className="h-10" defaultValue="Engenharia de Software" /></FormField>
        <FormField label="Observações" full><Textarea rows={3} /></FormField>
      </div>
      <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Solicitar Análise</Button></div>
    </div>
  );
}

const requisitos = [
  { label: "Disciplinas obrigatórias", current: 38, total: 38, ok: true },
  { label: "Disciplinas optativas", current: 8, total: 8, ok: true },
  { label: "Carga horária total", current: 3200, total: 3200, ok: true, unit: "h" },
  { label: "Atividades complementares", current: 180, total: 200, ok: false, unit: "h" },
  { label: "Estágio obrigatório", current: 1, total: 1, ok: true, unit: "concluído" },
];

function Analisar() {
  return (
    <div className="space-y-4">
      <SuccessBanner title="Análise concluída — Estudante Apto!" description="Maria Santos · 100% das exigências curriculares atendidas." />
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Analisar Integralização e Registrar Resultado" />
        <StatsRow className="mt-4" stats={[
          { label: "Progresso geral", value: "96%", tone: "info" },
          { label: "Requisitos OK", value: 4, tone: "success" },
          { label: "Pendências", value: 1, tone: "warning" },
          { label: "Bloqueios", value: 0, tone: "danger" },
        ]} />
        <ul className="mt-4 space-y-3">
          {requisitos.map((r, i) => (
            <li key={i} className="rounded-lg border p-3">
              <div className="flex items-center gap-2">
                {r.ok ? <CheckCircle2 className="h-4 w-4 text-success" /> : <AlertCircle className="h-4 w-4 text-warning" />}
                <span className="text-[13px] font-medium">{r.label}</span>
              </div>
              <div className="mt-2"><ProgressRow label="" current={r.current} total={r.total} unit={(r as any).unit ?? ""} tone={r.ok ? "success" : "warning"} /></div>
            </li>
          ))}
        </ul>
        <FormField className="mt-4" label="Parecer da coordenação" full><Textarea rows={3} /></FormField>
        <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Reprovar</Button><Button>Registrar Apto</Button></div>
      </div>
    </div>
  );
}

function AprovarColacao() {
  return (
    <div className="rounded-xl border bg-card p-6 shadow-card">
      <SectionTitle title="Aprovar Aptidão para Colação de Grau" />
      <ValidationCallout className="mt-4">Estudante atende todos os requisitos para colação.</ValidationCallout>
      <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Aprovar Aptidão</Button></div>
    </div>
  );
}

function Cerimonia() {
  return (
    <div className="rounded-xl border bg-card p-6 shadow-card">
      <SectionTitle title="Registrar Cerimônia de Colação de Grau" />
      <div className="mt-4 grid grid-cols-2 gap-4">
        <FormField label="Data" required><Input className="h-10" type="date" /></FormField>
        <FormField label="Local" required><Input className="h-10" /></FormField>
        <FormField label="Ata" required full><Textarea rows={4} /></FormField>
      </div>
      <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Registrar Cerimônia</Button></div>
    </div>
  );
}

function Page() {
  return (
    <FeaturePage
      title="Validação de Integralização e Colação de Grau"
      subtitle="Análise curricular e colação"
      sections={[
        { value: "painel", label: "Painel", content: <Painel /> },
        { value: "sol", label: "Solicitar", content: <Solicitar /> },
        { value: "anal", label: "Analisar", content: <Analisar /> },
        { value: "apr", label: "Aprovar Aptidão", content: <AprovarColacao /> },
        { value: "cer", label: "Cerimônia", content: <Cerimonia /> },
      ]}
    />
  );
}
