import { useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import { toast } from "sonner";
import {
  AppShell, SectionTitle, StatsRow, DataTable, StatusBadge, RowActionButton,
  ActionBar, ValidationCallout, FormField, ProgressRow,
  useProfileSwitcher,
} from "@/components/acadlab";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { ArrowLeft, Plus, Power, PowerOff } from "lucide-react";

export const Route = createFileRoute("/gestao-curricular")({
  head: () => ({ meta: [{ title: "Gestão Curricular — AcadLab" }] }),
  component: Page,
});

type Matriz = {
  id: string; nome: string; versao: string; status: "Ativa" | "Inativa" | "Rascunho";
  disciplinas: number; cargaH: number; creditos: number; ano: string;
};

const matrizesIniciais: Matriz[] = [
  { id: "M2020", nome: "Eng. de Software — Matriz 2020", versao: "2020.1", status: "Ativa", disciplinas: 48, cargaH: 3600, creditos: 240, ano: "2020" },
  { id: "M2024", nome: "Eng. de Software — Matriz 2024", versao: "2024.1", status: "Rascunho", disciplinas: 52, cargaH: 3720, creditos: 248, ano: "2024" },
  { id: "M2015", nome: "Eng. de Software — Matriz 2015", versao: "2015.2", status: "Inativa", disciplinas: 46, cargaH: 3500, creditos: 230, ano: "2015" },
];

function Page() {
  const { active: perfil } = useProfileSwitcher([
    { value: "coordenacao", label: "Coordenação Acadêmica", description: "Edita matrizes e disciplinas" },
    { value: "dra", label: "DRA / Registro Acadêmico", description: "Audita versões e equivalências" },
  ]);
  const [matrizes, setMatrizes] = useState(matrizesIniciais);
  const [selected, setSelected] = useState<string | null>(null);
  const atual = matrizes.find((m) => m.id === selected);

  const toggleStatus = (id: string) =>
    setMatrizes((prev) => {
      const target = prev.find((m) => m.id === id);
      if (!target) return prev;
      const novoStatus = target.status === "Ativa" ? "Inativa" : "Ativa";
      return prev.map((m) => {
        if (m.id === id) return { ...m, status: novoStatus };
        if (novoStatus === "Ativa" && m.status === "Ativa") return { ...m, status: "Inativa" };
        return m;
      });
    });

  const subtitle = perfil === "dra"
    ? "Visão DRA · Auditoria curricular"
    : "Coordenador Acadêmico — Engenharia de Software";

  return (
    <AppShell title="Gestão Curricular" subtitle={subtitle}>
      {!atual ? (
        <div className="space-y-5">
          <StatsRow stats={[
            { label: "Matrizes do curso", value: matrizes.length, tone: "info" },
            { label: "Ativa", value: matrizes.filter((m) => m.status === "Ativa").length, tone: "success" },
            { label: "Em rascunho", value: matrizes.filter((m) => m.status === "Rascunho").length, tone: "warning" },
            { label: "Carga mínima", value: "3600h", tone: "info" },
          ]} />
          {perfil === "coordenacao"
            ? <ActionBar searchPlaceholder="Buscar matriz..." primaryLabel="Nova matriz" />
            : <ActionBar searchPlaceholder="Buscar matriz para auditar..." />}
          {perfil === "dra" && (
            <ValidationCallout tone="info">Visão DRA: somente leitura. Use a coluna "Abrir" para auditar equivalências e cargas horárias.</ValidationCallout>
          )}
          <DataTable
            columns={[
              { key: "nome", header: "Matriz" },
              { key: "versao", header: "Versão" },
              { key: "disciplinas", header: "Disciplinas", align: "right" },
              { key: "cargaH", header: "Carga H.", align: "right" },
              { key: "creditos", header: "Créditos", align: "right" },
              { key: "status", header: "Status", render: (r) => (
                <StatusBadge tone={r.status === "Ativa" ? "success" : r.status === "Rascunho" ? "warning" : "neutral"}>{r.status}</StatusBadge>
              )},
              { key: "acoes", header: "", align: "right", render: (r) => (
                <RowActionButton onClick={() => setSelected(r.id)}>Abrir</RowActionButton>
              )},
            ]}
            rows={matrizes}
          />
        </div>
      ) : (
        <DetalheMatriz matriz={atual} readOnly={perfil === "dra"} onBack={() => setSelected(null)} onToggle={() => toggleStatus(atual.id)} />
      )}
    </AppShell>
  );
}

function DetalheMatriz({ matriz, readOnly, onBack, onToggle }: { matriz: Matriz; readOnly?: boolean; onBack: () => void; onToggle: () => void }) {
  const editavel = !readOnly && matriz.status !== "Ativa";
  return (
    <div className="space-y-5">
      <Button variant="ghost" size="sm" onClick={onBack}><ArrowLeft className="mr-1 h-4 w-4" /> Matrizes</Button>
      <div className="flex flex-wrap items-end justify-between gap-3">
        <SectionTitle title={matriz.nome} subtitle={`Versão ${matriz.versao} · ${matriz.disciplinas} disciplinas`} />
        <div className="flex gap-2">
          <StatusBadge tone={matriz.status === "Ativa" ? "success" : matriz.status === "Rascunho" ? "warning" : "neutral"}>{matriz.status}</StatusBadge>
          {!readOnly && (matriz.status === "Ativa" ? (
            <Button variant="outline" onClick={onToggle}><PowerOff className="mr-2 h-4 w-4" /> Desativar</Button>
          ) : (
            <Button onClick={onToggle}><Power className="mr-2 h-4 w-4" /> Ativar</Button>
          ))}
        </div>
      </div>

      {readOnly && (
        <ValidationCallout tone="info">Visão DRA · Auditoria curricular. Apenas a Coordenação pode alterar matrizes.</ValidationCallout>
      )}

      {!editavel && (
        <ValidationCallout tone="info">Matriz ativa: a estrutura de disciplinas não pode ser alterada. Desative para editar.</ValidationCallout>
      )}

      <div className="grid gap-4 lg:grid-cols-3">
        <div className="lg:col-span-2 rounded-xl border bg-card p-5 shadow-card">
          <SectionTitle title="Disciplinas da matriz" right={editavel ? <Button size="sm" onClick={() => toast.success("Disciplina adicionada à matriz (rascunho).")}><Plus className="mr-1 h-3.5 w-3.5" /> Adicionar</Button> : undefined} />
          <DataTable className="mt-3"
            columns={[
              { key: "cod", header: "Código" },
              { key: "nome", header: "Disciplina" },
              { key: "tipo", header: "Tipo", render: (r) => <StatusBadge tone={r.tipo === "Obrigatória" ? "info" : "neutral"}>{r.tipo}</StatusBadge> },
              { key: "ch", header: "CH", align: "right" },
              { key: "preReq", header: "Pré-req" },
              { key: "acoes", header: "", align: "right", render: (r) => editavel ? <RowActionButton tone="danger" onClick={() => toast.success(`${r.cod} removida da matriz (rascunho).`)}>Remover</RowActionButton> : <RowActionButton tone="neutral" onClick={() => toast.info(`Detalhes de ${r.cod} — ${r.nome}`)}>Ver</RowActionButton> },
            ]}
            rows={[
              { cod: "AED101", nome: "Algoritmos I", tipo: "Obrigatória", ch: 80, preReq: "—" },
              { cod: "AED201", nome: "Algoritmos II", tipo: "Obrigatória", ch: 80, preReq: "AED101" },
              { cod: "AED301", nome: "Algoritmos Avançados", tipo: "Obrigatória", ch: 80, preReq: "AED201" },
              { cod: "BD301", nome: "Banco de Dados I", tipo: "Obrigatória", ch: 60, preReq: "AED201" },
              { cod: "IA401", nome: "Inteligência Artificial", tipo: "Optativa", ch: 60, preReq: "AED301" },
            ]}
          />
        </div>
        <div className="space-y-4">
          <div className="rounded-xl border bg-card p-5 shadow-card">
            <SectionTitle title="Integralização da matriz" />
            <div className="mt-3 space-y-3">
              <ProgressRow label="Carga horária total" current={matriz.cargaH} total={3600} unit="h" tone="success" />
              <ProgressRow label="Créditos" current={matriz.creditos} total={240} unit="cr" tone="success" />
              <ProgressRow label="Obrigatórias cadastradas" current={42} total={42} unit="" tone="success" />
              <ProgressRow label="Optativas cadastradas" current={6} total={10} unit="" tone="warning" />
            </div>
          </div>
          {editavel && (
            <div className="rounded-xl border bg-card p-5 shadow-card">
              <SectionTitle title="Adicionar pré-requisito" />
              <div className="mt-3 grid grid-cols-1 gap-3">
                <FormField label="Disciplina"><Input className="h-10" placeholder="ES401" /></FormField>
                <FormField label="Pré-requisito"><Input className="h-10" placeholder="AED301" /></FormField>
                <Button className="w-full" onClick={() => toast.success("Pré-requisito vinculado. Grafo validado sem ciclos.")}>Vincular</Button>
              </div>
              <ValidationCallout tone="info" className="mt-3">O sistema valida automaticamente ciclos no grafo de pré-requisitos.</ValidationCallout>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
