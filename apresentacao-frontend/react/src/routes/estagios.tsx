import { createFileRoute } from "@tanstack/react-router";
import {
  FeaturePage, StatsRow, DataTable, StatusBadge, RowActionButton, FormField,
  SuccessBanner, SectionTitle, ActionBar, ValidationCallout,
} from "@/components/acadlab";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";
import { Building2, Clock, MapPin } from "lucide-react";

export const Route = createFileRoute("/estagios")({
  head: () => ({ meta: [{ title: "Estágios — AcadLab" }] }),
  component: Page,
});

const candTone: Record<string, any> = {
  "Inscrita": "info", "Em análise": "warning", "Encaminhada": "info",
  "Selecionada": "success", "Não selecionada": "danger",
};

function Vagas() {
  return (
    <>
      <StatsRow stats={[
        { label: "Vagas publicadas", value: 18, tone: "info" },
        { label: "Minhas candidaturas", value: 4, tone: "success" },
        { label: "Encaminhamentos", value: 2, tone: "warning" },
        { label: "Selecionadas", value: 1, tone: "success" },
      ]} />
      <ActionBar searchPlaceholder="Buscar vaga, empresa ou cargo..." primaryLabel="Cadastrar Oportunidade" />
      <div className="grid grid-cols-1 gap-4 lg:grid-cols-2">
        {[
          { empresa: "Acme Tech", cargo: "Desenvolvedor(a) Front-end", bolsa: "R$ 1.800", ch: "30h/semana", local: "Remoto", req: "React, TypeScript" },
          { empresa: "DataBank Brasil", cargo: "Estagiário(a) em Dados", bolsa: "R$ 2.100", ch: "30h/semana", local: "São Paulo", req: "SQL, Python" },
          { empresa: "Studio Nova", cargo: "QA Júnior (Estágio)", bolsa: "R$ 1.500", ch: "20h/semana", local: "Híbrido", req: "Cypress, Jest" },
          { empresa: "Fintech Lúmen", cargo: "Backend Estagiário(a)", bolsa: "R$ 2.000", ch: "30h/semana", local: "Remoto", req: "Node.js, AWS" },
        ].map((v, i) => (
          <div key={i} className="flex flex-col gap-3 rounded-xl border bg-card p-5 shadow-card">
            <div className="flex items-start justify-between">
              <div>
                <p className="text-[14px] font-semibold">{v.cargo}</p>
                <p className="mt-0.5 flex items-center gap-1.5 text-[12px] text-muted-foreground"><Building2 className="h-3.5 w-3.5" /> {v.empresa}</p>
              </div>
              <StatusBadge tone="success">Aberta</StatusBadge>
            </div>
            <div className="flex flex-wrap gap-3 text-[12px] text-muted-foreground">
              <span className="flex items-center gap-1"><MapPin className="h-3.5 w-3.5" /> {v.local}</span>
              <span className="flex items-center gap-1"><Clock className="h-3.5 w-3.5" /> {v.ch}</span>
              <span>Bolsa: <span className="font-medium text-foreground">{v.bolsa}</span></span>
            </div>
            <p className="text-[12px]"><span className="text-muted-foreground">Requisitos:</span> {v.req}</p>
            <div className="flex justify-end"><Button size="sm">Candidatar-se</Button></div>
          </div>
        ))}
      </div>
    </>
  );
}

function Cadastrar() {
  return (
    <div className="rounded-xl border bg-card p-6 shadow-card">
      <SectionTitle title="Cadastrar Oportunidade de Estágio" />
      <div className="mt-4 grid grid-cols-2 gap-4">
        <FormField label="Empresa" required><Input className="h-10" /></FormField>
        <FormField label="Cargo" required><Input className="h-10" /></FormField>
        <FormField label="Bolsa (R$)"><Input className="h-10" /></FormField>
        <FormField label="Carga horária"><Input className="h-10" defaultValue="30h/semana" /></FormField>
        <FormField label="Descrição" required full><Textarea rows={3} /></FormField>
        <FormField label="Requisitos" full><Textarea rows={2} /></FormField>
      </div>
      <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Salvar</Button></div>
    </div>
  );
}

function Criterios() {
  return (
    <div className="rounded-xl border bg-card p-6 shadow-card">
      <SectionTitle title="Definir Critérios de Elegibilidade" />
      <div className="mt-4 grid grid-cols-2 gap-4">
        <FormField label="Curso(s)"><Input className="h-10" defaultValue="Eng. Software, ADS" /></FormField>
        <FormField label="Período mínimo"><Input className="h-10" defaultValue="5º" /></FormField>
        <FormField label="CR mínimo"><Input className="h-10" defaultValue="7.0" /></FormField>
        <FormField label="Disciplinas cursadas" full><Textarea rows={2} placeholder="AED201, BD301, ES303..." /></FormField>
      </div>
      <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Salvar</Button></div>
    </div>
  );
}

function Candidatar() {
  return (
    <div className="space-y-4">
      <SuccessBanner title="Candidatura enviada!" description="Acompanhe o status em 'Minhas candidaturas'." />
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Candidatar-se" />
        <ValidationCallout className="mt-4">Você atende a todos os critérios desta vaga (CR 8.4 ≥ 7.0, 6º período).</ValidationCallout>
        <FormField className="mt-4" label="Currículo" required full><Input type="file" className="h-10" /></FormField>
        <FormField className="mt-4" label="Carta de apresentação" full><Textarea rows={4} /></FormField>
        <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Confirmar Candidatura</Button></div>
      </div>
    </div>
  );
}

function Validar() {
  return (
    <div className="rounded-xl border bg-card p-6 shadow-card">
      <SectionTitle title="Validar e Publicar Oportunidade" />
      <ValidationCallout className="mt-4">Vaga conforme o regulamento.</ValidationCallout>
      <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Reprovar</Button><Button>Publicar</Button></div>
    </div>
  );
}

function Encaminhar() {
  return (
    <div className="rounded-xl border bg-card p-6 shadow-card">
      <SectionTitle title="Registrar Encaminhamento de Estudante" />
      <div className="mt-4 grid grid-cols-2 gap-4">
        <FormField label="Candidato"><Input className="h-10" defaultValue="Maria Santos" /></FormField>
        <FormField label="Vaga"><Input className="h-10" defaultValue="Acme Tech — Front-end" /></FormField>
        <FormField label="Observações" full><Textarea rows={3} /></FormField>
      </div>
      <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Encaminhar</Button></div>
    </div>
  );
}

function Acompanhar() {
  return (
    <>
      <SectionTitle title="Acompanhar Candidaturas" />
      <DataTable
        columns={[
          { key: "vaga", header: "Vaga" },
          { key: "empresa", header: "Empresa" },
          { key: "data", header: "Inscrita em" },
          { key: "status", header: "Status", render: (r) => <StatusBadge tone={candTone[r.status]}>{r.status}</StatusBadge> },
          { key: "acoes", header: "", render: () => <div className="flex justify-end gap-1.5"><RowActionButton>Ver</RowActionButton><RowActionButton tone="danger">Cancelar</RowActionButton></div>, align: "right" },
        ]}
        rows={[
          { vaga: "Front-end", empresa: "Acme Tech", data: "06/03/2025", status: "Em análise" },
          { vaga: "Estagiário(a) em Dados", empresa: "DataBank", data: "04/03/2025", status: "Encaminhada" },
          { vaga: "QA Júnior", empresa: "Studio Nova", data: "20/02/2025", status: "Não selecionada" },
          { vaga: "Backend Estagiário(a)", empresa: "Fintech Lúmen", data: "10/02/2025", status: "Selecionada" },
        ]}
      />
    </>
  );
}

function Encerrar() {
  return (
    <div className="rounded-xl border bg-card p-6 shadow-card">
      <SectionTitle title="Encerrar Oportunidade" />
      <FormField className="mt-4" label="Motivo do encerramento" full><Textarea rows={3} /></FormField>
      <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Voltar</Button><Button variant="destructive">Encerrar Vaga</Button></div>
    </div>
  );
}

function Page() {
  return (
    <FeaturePage
      title="Centro de Estágios e Oportunidades"
      subtitle="Vagas, candidaturas e encaminhamentos"
      sections={[
        { value: "vagas", label: "Vagas Abertas", content: <Vagas /> },
        { value: "cad", label: "Cadastrar", content: <Cadastrar /> },
        { value: "crit", label: "Critérios", content: <Criterios /> },
        { value: "cand", label: "Candidatar", content: <Candidatar /> },
        { value: "val", label: "Validar", content: <Validar /> },
        { value: "enc", label: "Encaminhamento", content: <Encaminhar /> },
        { value: "acomp", label: "Acompanhar", content: <Acompanhar /> },
        { value: "fim", label: "Encerrar", content: <Encerrar /> },
      ]}
    />
  );
}
