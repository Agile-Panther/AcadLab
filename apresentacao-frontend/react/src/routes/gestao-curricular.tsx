import { useEffect, useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import { toast } from "sonner";
import {
  AppShell, SectionTitle, StatsRow, DataTable, StatusBadge, RowActionButton,
  ActionBar, ValidationCallout, FormField, ProgressRow,
  useProfileSwitcher,
} from "@/components/acadlab";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogFooter } from "@/components/ui/dialog";
import { ArrowLeft, Plus, Power, PowerOff } from "lucide-react";
import {
  type MatrizResumo, type MatrizDetalhe, type TipoDisciplina,
  listarMatrizesPorCurso, buscarDetalheMatriz, criarMatriz,
  adicionarDisciplina, removerDisciplina, adicionarPreRequisito,
  ativarMatriz, desativarMatriz,
} from "@/api/curriculo";

export const Route = createFileRoute("/gestao-curricular")({
  head: () => ({ meta: [{ title: "Gestão Curricular — AcadLab" }] }),
  component: Page,
});

// Curso fixo da Coordenação logada (Engenharia de Software). O domínio ainda
// não possui um catálogo de Cursos com nome — apenas o id é persistido.
const CURSO_ID = 1;

function statusTone(status: MatrizResumo["status"]) {
  return status === "ATIVA" ? "success" : status === "RASCUNHO" ? "warning" : "neutral";
}

function statusLabel(status: MatrizResumo["status"]) {
  return status === "ATIVA" ? "Ativa" : status === "RASCUNHO" ? "Rascunho" : "Inativa";
}

function Page() {
  const { active: perfil } = useProfileSwitcher([
    { value: "coordenacao", label: "Coordenação Acadêmica", description: "Edita matrizes e disciplinas" },
    { value: "dra", label: "DRA / Registro Acadêmico", description: "Audita versões e equivalências" },
  ]);

  const [matrizes, setMatrizes] = useState<MatrizResumo[]>([]);
  const [loadingLista, setLoadingLista] = useState(true);
  const [erroLista, setErroLista] = useState<string | null>(null);

  const [selected, setSelected] = useState<number | null>(null);
  const [detalhe, setDetalhe] = useState<MatrizDetalhe | null>(null);
  const [loadingDetalhe, setLoadingDetalhe] = useState(false);

  const [novaMatrizOpen, setNovaMatrizOpen] = useState(false);

  const carregarLista = () => {
    setLoadingLista(true);
    setErroLista(null);
    listarMatrizesPorCurso(CURSO_ID)
      .then(setMatrizes)
      .catch((e: Error) => setErroLista(e.message))
      .finally(() => setLoadingLista(false));
  };

  useEffect(carregarLista, []);

  useEffect(() => {
    if (selected == null) { setDetalhe(null); return; }
    setLoadingDetalhe(true);
    buscarDetalheMatriz(selected)
      .then(setDetalhe)
      .catch((e: Error) => toast.error(e.message))
      .finally(() => setLoadingDetalhe(false));
  }, [selected]);

  const recarregarDetalhe = () => {
    if (selected == null) return;
    buscarDetalheMatriz(selected).then(setDetalhe).catch((e: Error) => toast.error(e.message));
  };

  const toggleStatus = async () => {
    if (!detalhe) return;
    try {
      if (detalhe.status === "ATIVA") {
        await desativarMatriz(detalhe.id);
        toast.success("Matriz desativada.");
      } else {
        await ativarMatriz(detalhe.id);
        toast.success("Matriz ativada.");
      }
      recarregarDetalhe();
      carregarLista();
    } catch (e) {
      toast.error((e as Error).message);
    }
  };

  const subtitle = perfil === "dra"
    ? "Visão DRA · Auditoria curricular"
    : "Coordenador Acadêmico — Engenharia de Software";

  return (
    <AppShell title="Gestão Curricular" subtitle={subtitle}>
      {selected == null ? (
        <div className="space-y-5">
          <StatsRow stats={[
            { label: "Matrizes do curso", value: matrizes.length, tone: "info" },
            { label: "Ativa", value: matrizes.filter((m) => m.status === "ATIVA").length, tone: "success" },
            { label: "Em rascunho", value: matrizes.filter((m) => m.status === "RASCUNHO").length, tone: "warning" },
            { label: "Inativa", value: matrizes.filter((m) => m.status === "INATIVA").length, tone: "info" },
          ]} />
          {perfil === "coordenacao"
            ? <ActionBar searchPlaceholder="Buscar matriz..." primaryLabel="Nova matriz" onPrimary={() => setNovaMatrizOpen(true)} />
            : <ActionBar searchPlaceholder="Buscar matriz para auditar..." />}
          {perfil === "dra" && (
            <ValidationCallout tone="info">Visão DRA: somente leitura. Use a coluna "Abrir" para auditar equivalências e cargas horárias.</ValidationCallout>
          )}
          {erroLista && (
            <ValidationCallout tone="error">{erroLista}</ValidationCallout>
          )}
          <DataTable
            columns={[
              { key: "nome", header: "Matriz" },
              { key: "cursoId", header: "Curso", align: "right" },
              { key: "status", header: "Status", render: (r) => (
                <StatusBadge tone={statusTone(r.status)}>{statusLabel(r.status)}</StatusBadge>
              )},
              { key: "acoes", header: "", align: "right", render: (r) => (
                <RowActionButton onClick={() => setSelected(r.id)}>Abrir</RowActionButton>
              )},
            ]}
            rows={matrizes}
            empty={<div className="p-10 text-center text-sm text-muted-foreground">{loadingLista ? "Carregando matrizes..." : "Nenhuma matriz curricular cadastrada para este curso."}</div>}
          />
        </div>
      ) : !detalhe ? (
        <div className="space-y-4">
          <Button variant="ghost" size="sm" onClick={() => setSelected(null)}><ArrowLeft className="mr-1 h-4 w-4" /> Matrizes</Button>
          <p className="text-sm text-muted-foreground">{loadingDetalhe ? "Carregando matriz..." : "Matriz não encontrada."}</p>
        </div>
      ) : (
        <DetalheMatriz
          matriz={detalhe}
          readOnly={perfil === "dra"}
          onBack={() => setSelected(null)}
          onToggle={toggleStatus}
          onChanged={recarregarDetalhe}
        />
      )}

      <NovaMatrizDialog
        open={novaMatrizOpen}
        onOpenChange={setNovaMatrizOpen}
        onCriada={() => { carregarLista(); }}
      />
    </AppShell>
  );
}

function NovaMatrizDialog({ open, onOpenChange, onCriada }: {
  open: boolean; onOpenChange: (v: boolean) => void; onCriada: () => void;
}) {
  const [nome, setNome] = useState("");
  const [cargaHorariaMinima, setCargaHorariaMinima] = useState("3600");
  const [creditosExigidos, setCreditosExigidos] = useState("240");
  const [maximoTrancamentos, setMaximoTrancamentos] = useState("4");
  const [salvando, setSalvando] = useState(false);

  const salvar = async () => {
    if (!nome.trim()) { toast.warning("Informe o nome da matriz."); return; }
    setSalvando(true);
    try {
      await criarMatriz({
        cursoId: CURSO_ID,
        nome: nome.trim(),
        cargaHorariaMinima: Number(cargaHorariaMinima),
        creditosExigidos: Number(creditosExigidos),
        maximoTrancamentos: Number(maximoTrancamentos),
      });
      toast.success("Matriz curricular criada em rascunho.");
      onOpenChange(false);
      setNome("");
      onCriada();
    } catch (e) {
      toast.error((e as Error).message);
    } finally {
      setSalvando(false);
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Nova matriz curricular</DialogTitle>
          <DialogDescription>Criada em rascunho — disciplinas e pré-requisitos podem ser configurados antes da ativação.</DialogDescription>
        </DialogHeader>
        <div className="grid grid-cols-2 gap-3">
          <FormField label="Nome da matriz" full required>
            <Input className="h-10" value={nome} onChange={(e) => setNome(e.target.value)} placeholder="Eng. de Software — Matriz 2026" />
          </FormField>
          <FormField label="Carga horária mínima" required>
            <Input className="h-10" type="number" value={cargaHorariaMinima} onChange={(e) => setCargaHorariaMinima(e.target.value)} />
          </FormField>
          <FormField label="Créditos exigidos" required>
            <Input className="h-10" type="number" value={creditosExigidos} onChange={(e) => setCreditosExigidos(e.target.value)} />
          </FormField>
          <FormField label="Máximo de trancamentos" required>
            <Input className="h-10" type="number" value={maximoTrancamentos} onChange={(e) => setMaximoTrancamentos(e.target.value)} />
          </FormField>
        </div>
        <DialogFooter>
          <Button variant="outline" onClick={() => onOpenChange(false)}>Cancelar</Button>
          <Button onClick={salvar} disabled={salvando}>{salvando ? "Criando..." : "Criar matriz"}</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}

function DetalheMatriz({ matriz, readOnly, onBack, onToggle, onChanged }: {
  matriz: MatrizDetalhe; readOnly?: boolean; onBack: () => void; onToggle: () => void; onChanged: () => void;
}) {
  const editavel = !readOnly && matriz.status !== "ATIVA";
  const [addingDisciplina, setAddingDisciplina] = useState(false);

  const cargaHorariaTotal = matriz.itens.reduce((acc, i) => acc + i.cargaHoraria, 0);
  const creditosTotal = matriz.itens.reduce((acc, i) => acc + i.creditos, 0);
  const obrigatorias = matriz.itens.filter((i) => i.tipo === "OBRIGATORIA").length;
  const optativas = matriz.itens.filter((i) => i.tipo === "OPTATIVA").length;

  const preRequisitosPorDisciplina = (disciplinaId: number) =>
    matriz.preRequisitos.filter((p) => p.disciplinaId === disciplinaId).map((p) => p.dependenciaId);

  const remover = async (disciplinaId: number) => {
    try {
      await removerDisciplina(matriz.id, disciplinaId);
      toast.success(`Disciplina #${disciplinaId} removida da matriz.`);
      onChanged();
    } catch (e) {
      toast.error((e as Error).message);
    }
  };

  return (
    <div className="space-y-5">
      <Button variant="ghost" size="sm" onClick={onBack}><ArrowLeft className="mr-1 h-4 w-4" /> Matrizes</Button>
      <div className="flex flex-wrap items-end justify-between gap-3">
        <SectionTitle title={matriz.nome} subtitle={`${matriz.itens.length} disciplinas · ${matriz.cargaHorariaMinima}h mín. · ${matriz.creditosExigidos} créditos exigidos`} />
        <div className="flex gap-2">
          <StatusBadge tone={statusTone(matriz.status)}>{statusLabel(matriz.status)}</StatusBadge>
          {!readOnly && (matriz.status === "ATIVA" ? (
            <Button variant="outline" onClick={onToggle}><PowerOff className="mr-2 h-4 w-4" /> Desativar</Button>
          ) : (
            <Button onClick={onToggle}><Power className="mr-2 h-4 w-4" /> Ativar</Button>
          ))}
        </div>
      </div>

      {readOnly && (
        <ValidationCallout tone="info">Visão DRA · Auditoria curricular. Apenas a Coordenação pode alterar matrizes.</ValidationCallout>
      )}

      {!editavel && !readOnly && (
        <ValidationCallout tone="info">Matriz ativa: a estrutura de disciplinas não pode ser alterada. Desative para editar.</ValidationCallout>
      )}

      <div className="grid gap-4 lg:grid-cols-3">
        <div className="lg:col-span-2 space-y-4">
          <div className="rounded-xl border bg-card p-5 shadow-card">
            <SectionTitle title="Disciplinas da matriz" right={editavel ? (
              <Button size="sm" onClick={() => setAddingDisciplina((v) => !v)}><Plus className="mr-1 h-3.5 w-3.5" /> Adicionar</Button>
            ) : undefined} />
            {addingDisciplina && (
              <AdicionarDisciplinaForm
                matrizId={matriz.id}
                onCancel={() => setAddingDisciplina(false)}
                onAdicionada={() => { setAddingDisciplina(false); onChanged(); }}
              />
            )}
            <DataTable className="mt-3"
              columns={[
                { key: "disciplinaId", header: "Disciplina", render: (r) => `#${r.disciplinaId}` },
                { key: "tipo", header: "Tipo", render: (r) => <StatusBadge tone={r.tipo === "OBRIGATORIA" ? "info" : "neutral"}>{r.tipo === "OBRIGATORIA" ? "Obrigatória" : "Optativa"}</StatusBadge> },
                { key: "cargaHoraria", header: "CH", align: "right" },
                { key: "creditos", header: "Créditos", align: "right" },
                { key: "preReq", header: "Pré-req", render: (r) => {
                  const deps = preRequisitosPorDisciplina(r.disciplinaId);
                  return deps.length ? deps.map((d) => `#${d}`).join(", ") : "—";
                }},
                { key: "acoes", header: "", align: "right", render: (r) => editavel
                  ? <RowActionButton tone="danger" onClick={() => remover(r.disciplinaId)}>Remover</RowActionButton>
                  : <RowActionButton tone="neutral" onClick={() => toast.info(`Disciplina #${r.disciplinaId} — ${r.cargaHoraria}h, ${r.creditos} créditos`)}>Ver</RowActionButton> },
              ]}
              rows={matriz.itens}
              empty={<div className="p-8 text-center text-sm text-muted-foreground">Nenhuma disciplina cadastrada nesta matriz.</div>}
            />
          </div>
        </div>
        <div className="space-y-4">
          <div className="rounded-xl border bg-card p-5 shadow-card">
            <SectionTitle title="Integralização da matriz" />
            <div className="mt-3 space-y-3">
              <ProgressRow label="Carga horária cadastrada" current={cargaHorariaTotal} total={matriz.cargaHorariaMinima} unit="h" tone={cargaHorariaTotal >= matriz.cargaHorariaMinima ? "success" : "warning"} />
              <ProgressRow label="Créditos cadastrados" current={creditosTotal} total={matriz.creditosExigidos} unit="cr" tone={creditosTotal >= matriz.creditosExigidos ? "success" : "warning"} />
              <ProgressRow label="Disciplinas obrigatórias" current={obrigatorias} total={Math.max(obrigatorias, 1)} unit="" tone="success" />
              <ProgressRow label="Disciplinas optativas" current={optativas} total={Math.max(optativas, 1)} unit="" tone="success" />
            </div>
          </div>
          {editavel && (
            <AdicionarPreRequisitoForm matrizId={matriz.id} onAdicionado={onChanged} />
          )}
        </div>
      </div>
    </div>
  );
}

function AdicionarDisciplinaForm({ matrizId, onCancel, onAdicionada }: {
  matrizId: number; onCancel: () => void; onAdicionada: () => void;
}) {
  const [disciplinaId, setDisciplinaId] = useState("");
  const [tipo, setTipo] = useState<TipoDisciplina>("OBRIGATORIA");
  const [cargaHoraria, setCargaHoraria] = useState("60");
  const [creditos, setCreditos] = useState("4");
  const [salvando, setSalvando] = useState(false);

  const salvar = async () => {
    if (!disciplinaId) { toast.warning("Informe o id da disciplina."); return; }
    setSalvando(true);
    try {
      await adicionarDisciplina(matrizId, {
        disciplinaId: Number(disciplinaId),
        tipo,
        cargaHoraria: Number(cargaHoraria),
        creditos: Number(creditos),
      });
      toast.success(`Disciplina #${disciplinaId} adicionada à matriz.`);
      setDisciplinaId("");
      onAdicionada();
    } catch (e) {
      toast.error((e as Error).message);
    } finally {
      setSalvando(false);
    }
  };

  return (
    <div className="mt-3 grid grid-cols-2 gap-3 rounded-lg border border-dashed bg-subtle p-4 sm:grid-cols-4">
      <FormField label="Disciplina (id)"><Input className="h-10" type="number" value={disciplinaId} onChange={(e) => setDisciplinaId(e.target.value)} placeholder="101" /></FormField>
      <FormField label="Tipo">
        <Select value={tipo} onValueChange={(v) => setTipo(v as TipoDisciplina)}>
          <SelectTrigger className="h-10"><SelectValue /></SelectTrigger>
          <SelectContent>
            <SelectItem value="OBRIGATORIA">Obrigatória</SelectItem>
            <SelectItem value="OPTATIVA">Optativa</SelectItem>
          </SelectContent>
        </Select>
      </FormField>
      <FormField label="Carga horária"><Input className="h-10" type="number" value={cargaHoraria} onChange={(e) => setCargaHoraria(e.target.value)} /></FormField>
      <FormField label="Créditos"><Input className="h-10" type="number" value={creditos} onChange={(e) => setCreditos(e.target.value)} /></FormField>
      <div className="col-span-2 flex gap-2 sm:col-span-4">
        <Button size="sm" onClick={salvar} disabled={salvando}>{salvando ? "Adicionando..." : "Adicionar"}</Button>
        <Button size="sm" variant="ghost" onClick={onCancel}>Cancelar</Button>
      </div>
    </div>
  );
}

function AdicionarPreRequisitoForm({ matrizId, onAdicionado }: { matrizId: number; onAdicionado: () => void }) {
  const [disciplinaId, setDisciplinaId] = useState("");
  const [preRequisitoId, setPreRequisitoId] = useState("");
  const [salvando, setSalvando] = useState(false);

  const vincular = async () => {
    if (!disciplinaId || !preRequisitoId) { toast.warning("Informe as duas disciplinas."); return; }
    setSalvando(true);
    try {
      await adicionarPreRequisito(matrizId, Number(disciplinaId), Number(preRequisitoId));
      toast.success("Pré-requisito vinculado. Grafo validado sem ciclos.");
      setDisciplinaId(""); setPreRequisitoId("");
      onAdicionado();
    } catch (e) {
      toast.error((e as Error).message);
    } finally {
      setSalvando(false);
    }
  };

  return (
    <div className="rounded-xl border bg-card p-5 shadow-card">
      <SectionTitle title="Adicionar pré-requisito" />
      <div className="mt-3 grid grid-cols-1 gap-3">
        <FormField label="Disciplina (id)"><Input className="h-10" type="number" value={disciplinaId} onChange={(e) => setDisciplinaId(e.target.value)} placeholder="301" /></FormField>
        <FormField label="Pré-requisito (id)"><Input className="h-10" type="number" value={preRequisitoId} onChange={(e) => setPreRequisitoId(e.target.value)} placeholder="201" /></FormField>
        <Button className="w-full" onClick={vincular} disabled={salvando}>{salvando ? "Vinculando..." : "Vincular"}</Button>
      </div>
      <ValidationCallout tone="info" className="mt-3">O sistema valida automaticamente ciclos no grafo de pré-requisitos.</ValidationCallout>
    </div>
  );
}
