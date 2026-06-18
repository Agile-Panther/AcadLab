import { createFileRoute } from "@tanstack/react-router";
import {
  FeaturePage, StatsRow, DataTable, StatusBadge, RowActionButton, FormField,
  SuccessBanner, SectionTitle, ActionBar,
} from "@/components/acadlab";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";
import { Printer } from "lucide-react";

export const Route = createFileRoute("/financeiro")({
  head: () => ({ meta: [{ title: "Gestão Financeira — AcadLab" }] }),
  component: Page,
});

const tone = (s: string) =>
  s === "Pago" ? "success" : s === "Vencido" ? "danger" : s === "Em aberto" ? "warning" : "info";

function Extrato() {
  return (
    <>
      <StatsRow stats={[
        { label: "Total a receber", value: "R$ 1.240k", tone: "info" },
        { label: "Recebido no mês", value: "R$ 892k", tone: "success" },
        { label: "Inadimplência", value: "8,4%", tone: "warning" },
        { label: "Estornos", value: 12, tone: "danger" },
      ]} />
      <ActionBar searchPlaceholder="Buscar por aluno, cobrança ou referência..." primaryLabel="Gerar Cobranças" />
      <DataTable
        columns={[
          { key: "ref", header: "Referência" },
          { key: "aluno", header: "Estudante" },
          { key: "venc", header: "Vencimento" },
          { key: "valor", header: "Valor", align: "right" },
          { key: "status", header: "Status", render: (r) => <StatusBadge tone={tone(r.status)}>{r.status}</StatusBadge> },
          { key: "acoes", header: "Ações", render: () => <div className="flex justify-end gap-1.5"><RowActionButton>Detalhes</RowActionButton><RowActionButton>Pagar</RowActionButton></div>, align: "right" },
        ]}
        rows={[
          { ref: "MENS-2025-03", aluno: "Maria Santos", venc: "10/03/2025", valor: "R$ 1.290,00", status: "Pago" },
          { ref: "MENS-2025-04", aluno: "Maria Santos", venc: "10/04/2025", valor: "R$ 1.290,00", status: "Em aberto" },
          { ref: "MENS-2025-02", aluno: "João P.", venc: "10/02/2025", valor: "R$ 1.290,00", status: "Vencido" },
          { ref: "TAXA-LAB-25", aluno: "Maria Santos", venc: "15/03/2025", valor: "R$ 180,00", status: "Pago" },
        ]}
      />
    </>
  );
}

function Contestar() {
  return (
    <div className="rounded-xl border bg-card p-6 shadow-card">
      <SectionTitle title="Contestar Cobrança Acadêmica" />
      <div className="mt-4 grid grid-cols-2 gap-4">
        <FormField label="Cobrança"><Input className="h-10" defaultValue="MENS-2025-02" /></FormField>
        <FormField label="Motivo"><Input className="h-10" defaultValue="Já efetuado" /></FormField>
        <FormField label="Detalhes" required full><Textarea rows={4} /></FormField>
        <FormField label="Anexo" full><Input type="file" className="h-10" /></FormField>
      </div>
      <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Enviar Contestação</Button></div>
    </div>
  );
}

function Gerar() {
  return (
    <div className="space-y-4">
      <SuccessBanner title="Cobranças geradas." description="412 cobranças de mensalidade emitidas para 2025.2." />
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle title="Gerar Cobranças Acadêmicas" />
        <div className="mt-4 grid grid-cols-2 gap-4">
          <FormField label="Período"><Input className="h-10" defaultValue="2025.2" /></FormField>
          <FormField label="Tipo"><Input className="h-10" defaultValue="Mensalidade" /></FormField>
          <FormField label="Valor base (R$)"><Input className="h-10" defaultValue="1290,00" /></FormField>
          <FormField label="Vencimento"><Input className="h-10" type="date" /></FormField>
        </div>
        <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Gerar Cobranças</Button></div>
      </div>
    </div>
  );
}

function Bolsa() {
  return (
    <div className="rounded-xl border bg-card p-6 shadow-card">
      <SectionTitle title="Aplicar Bolsa / Desconto" />
      <div className="mt-4 grid grid-cols-2 gap-4">
        <FormField label="Estudante"><Input className="h-10" /></FormField>
        <FormField label="Tipo"><Input className="h-10" defaultValue="Bolsa parcial" /></FormField>
        <FormField label="Percentual / Valor"><Input className="h-10" defaultValue="50%" /></FormField>
        <FormField label="Vigência início"><Input className="h-10" type="date" /></FormField>
        <FormField label="Vigência fim"><Input className="h-10" type="date" /></FormField>
      </div>
      <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Aplicar</Button></div>
    </div>
  );
}

function Pagamento() {
  return (
    <div className="rounded-xl border bg-card p-6 shadow-card">
      <SectionTitle title="Registrar Pagamento" />
      <div className="mt-4 grid grid-cols-2 gap-4">
        <FormField label="Cobrança"><Input className="h-10" defaultValue="MENS-2025-04" /></FormField>
        <FormField label="Valor pago (R$)"><Input className="h-10" defaultValue="1290,00" /></FormField>
        <FormField label="Forma"><Input className="h-10" defaultValue="PIX" /></FormField>
        <FormField label="Data"><Input className="h-10" type="date" /></FormField>
      </div>
      <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Cancelar</Button><Button>Registrar</Button></div>
    </div>
  );
}

function Comprovante() {
  return (
    <div className="rounded-xl border bg-card p-6 shadow-card">
      <SectionTitle title="Comprovante de Pagamento" right={<Button><Printer className="mr-2 h-4 w-4" /> Imprimir</Button>} />
      <div className="mt-4 rounded-md border bg-subtle p-6 text-[13px]">
        <p className="font-semibold">Recibo · MENS-2025-03</p>
        <p className="text-muted-foreground">Estudante: Maria Santos · 2023001</p>
        <div className="mt-3 grid grid-cols-2 gap-3">
          <div><p className="text-muted-foreground">Valor pago</p><p className="font-semibold">R$ 1.290,00</p></div>
          <div><p className="text-muted-foreground">Forma</p><p className="font-semibold">PIX</p></div>
          <div><p className="text-muted-foreground">Data</p><p className="font-semibold">08/03/2025</p></div>
          <div><p className="text-muted-foreground">Autenticação</p><p className="font-mono text-[12px]">XJ19-882A-44BC</p></div>
        </div>
      </div>
    </div>
  );
}

function Estornar() {
  return (
    <div className="rounded-xl border bg-card p-6 shadow-card">
      <SectionTitle title="Cancelar ou Estornar Pagamento" />
      <div className="mt-4 grid grid-cols-2 gap-4">
        <FormField label="Pagamento"><Input className="h-10" defaultValue="MENS-2025-03" /></FormField>
        <FormField label="Tipo"><Input className="h-10" defaultValue="Estorno total" /></FormField>
        <FormField label="Motivo" required full><Textarea rows={4} /></FormField>
      </div>
      <div className="mt-4 flex justify-end gap-2"><Button variant="outline">Voltar</Button><Button variant="destructive">Estornar</Button></div>
    </div>
  );
}

function Page() {
  return (
    <FeaturePage
      title="Gestão Financeira Acadêmica"
      subtitle="Cobranças, pagamentos e bolsas"
      sections={[
        { value: "ext", label: "Extrato", content: <Extrato /> },
        { value: "cont", label: "Contestar", content: <Contestar /> },
        { value: "gerar", label: "Gerar Cobranças", content: <Gerar /> },
        { value: "bolsa", label: "Bolsa/Desconto", content: <Bolsa /> },
        { value: "pag", label: "Pagamento", content: <Pagamento /> },
        { value: "comp", label: "Comprovante", content: <Comprovante /> },
        { value: "est", label: "Estornar", content: <Estornar /> },
      ]}
    />
  );
}
