import { useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import {
  AppShell,
  SectionTitle,
  StatsRow,
  DataTable,
  StatusBadge,
  RowActionButton,
  ActionBar,
  ProgressRow,
  FormField,
  ValidationCallout,
  Stepper,
  SuccessBanner,
  useProfileSwitcher,
} from "@/components/acadlab";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
} from "@/components/ui/dialog";
import { ArrowLeft } from "lucide-react";
import { toast } from "sonner";
import {
  useAtividadesEstudante,
  useCategorias,
  useAtividadesPendentes,
  useAtividadesPorStatus,
  useSubmeter,
  useDeferir,
  useIndeferir,
  useSolicitarRevisao,
  useCancelar,
  EXIGENCIA_TOTAL_HORAS,
  type AtividadeComplementarResumo,
  type CategoriaHorasResumo,
  type StatusAtividade,
} from "@/lib/atividades";
import {
  validarHorasAprovadas,
  validarJustificativaIndeferimento,
  validarSubmissaoAtividade,
} from "@/lib/atividade-form";
import {
  calcularIndicadoresCoordenacao,
  calcularIndicadoresEstudante,
  calcularSaldoPorCategoria,
} from "@/lib/atividade-indicadores";
import { filtrarAtividades } from "@/lib/atividade-filtros";

export const Route = createFileRoute("/atividades-complementares")({
  head: () => ({ meta: [{ title: "Atividades Complementares — AcadLab" }] }),
  component: Page,
});

type View =
  | { kind: "list" }
  | { kind: "wizard"; step: 0 | 1 | 2 }
  | { kind: "revisao"; id: string };

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
    {
      value: "coordenacao",
      label: "Coordenação Acadêmica",
      description: "Valida e defere atividades",
    },
  ]);
  const [view, setView] = useState<View>({ kind: "list" });
  const [ver, setVer] = useState<AtividadeComplementarResumo | null>(null);
  const [busca, setBusca] = useState("");
  const [categoriaFiltro, setCategoriaFiltro] = useState<number | null>(null);
  const [statusFiltro, setStatusFiltro] = useState<StatusAtividade | null>(null);

  const atividadesQuery = useAtividadesEstudante();
  const categoriasQuery = useCategorias();
  const atividades = atividadesQuery.data ?? [];
  const categorias = categoriasQuery.data ?? [];
  const cancelar = useCancelar();

  const saldo = calcularSaldoPorCategoria(atividades);
  const { horasValidadas, horasEmAnalise, horasIndeferidas } =
    calcularIndicadoresEstudante(atividades);
  const atividadesFiltradas = filtrarAtividades(atividades, {
    busca,
    categoriaId: categoriaFiltro,
    status: statusFiltro,
  });
  const nomeCategoria = (id: number) =>
    categorias.find((c) => c.id === id)?.nome ?? `Categoria #${id}`;

  const subtitle =
    perfil === "coordenacao"
      ? "Visão Coordenação · Validação de atividades"
      : `Estudante #1 · saldo de horas`;

  return (
    <AppShell title="Atividades Complementares" subtitle={subtitle}>
      {perfil === "coordenacao" && <CoordView />}

      {perfil === "estudante" && view.kind === "list" && (
        <div className="space-y-5">
          <StatsRow
            stats={[
              { label: "Horas validadas", value: horasValidadas, tone: "success" },
              { label: "Em análise", value: horasEmAnalise, tone: "warning" },
              { label: "Indeferidas", value: horasIndeferidas, tone: "danger" },
              { label: "Exigência total", value: EXIGENCIA_TOTAL_HORAS, tone: "info" },
            ]}
          />
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

          <ActionBar
            searchPlaceholder="Buscar protocolo ou descrição..."
            onSearch={setBusca}
            showFilters={false}
            filters={
              <>
                <Select
                  value={categoriaFiltro == null ? "todas" : String(categoriaFiltro)}
                  onValueChange={(valor) =>
                    setCategoriaFiltro(valor === "todas" ? null : Number(valor))
                  }
                >
                  <SelectTrigger className="h-10 w-full sm:w-56">
                    <SelectValue placeholder="Categoria" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="todas">Todas as categorias</SelectItem>
                    {categorias.map((categoria) => (
                      <SelectItem key={categoria.id} value={String(categoria.id)}>
                        {categoria.nome}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
                <Select
                  value={statusFiltro ?? "todos"}
                  onValueChange={(valor) =>
                    setStatusFiltro(valor === "todos" ? null : (valor as StatusAtividade))
                  }
                >
                  <SelectTrigger className="h-10 w-full sm:w-48">
                    <SelectValue placeholder="Status" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="todos">Todos os status</SelectItem>
                    {Object.entries(rotuloStatus).map(([status, rotulo]) => (
                      <SelectItem key={status} value={status}>
                        {rotulo}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </>
            }
            primaryLabel="Submeter atividade"
            onPrimary={() => setView({ kind: "wizard", step: 0 })}
          />
          <DataTable
            columns={[
              { key: "id", header: "Protocolo", render: (r) => `AC-${r.id}` },
              {
                key: "categoriaId",
                header: "Categoria",
                render: (r) => nomeCategoria(r.categoriaId),
              },
              { key: "descricao", header: "Descrição" },
              { key: "horasSubmetidas", header: "CH", align: "right" },
              {
                key: "status",
                header: "Status",
                render: (r) => (
                  <StatusBadge tone={tomStatus(r.status)}>{rotuloStatus[r.status]}</StatusBadge>
                ),
              },
              {
                key: "acoes",
                header: "",
                align: "right",
                render: (r) =>
                  r.status === "INDEFERIDA" ? (
                    <RowActionButton onClick={() => setView({ kind: "revisao", id: String(r.id) })}>
                      Solicitar revisão
                    </RowActionButton>
                  ) : r.status === "PENDENTE" ? (
                    <RowActionButton
                      tone="danger"
                      onClick={() =>
                        cancelar.mutate(r.id, {
                          onSuccess: () => toast.success(`Solicitação AC-${r.id} cancelada.`),
                        })
                      }
                    >
                      Cancelar
                    </RowActionButton>
                  ) : (
                    <RowActionButton tone="neutral" onClick={() => setVer(r)}>
                      Ver
                    </RowActionButton>
                  ),
              },
            ]}
            rows={atividadesFiltradas}
          />
        </div>
      )}

      {perfil === "estudante" && view.kind === "wizard" && (
        <SubmeterWizard
          step={view.step}
          onStep={(s) => setView({ kind: "wizard", step: s })}
          onDone={() => setView({ kind: "list" })}
        />
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
                <div className="flex justify-between border-b py-2">
                  <span className="text-muted-foreground">Descrição</span>
                  <span className="font-medium text-right max-w-[60%]">{ver.descricao}</span>
                </div>
                <div className="flex justify-between border-b py-2">
                  <span className="text-muted-foreground">Carga horária</span>
                  <span className="font-medium">{ver.horasSubmetidas}h</span>
                </div>
                <div className="flex justify-between border-b py-2">
                  <span className="text-muted-foreground">Status</span>
                  <StatusBadge tone={tomStatus(ver.status)}>{rotuloStatus[ver.status]}</StatusBadge>
                </div>
                {ver.status === "DEFERIDA" && (
                  <ValidationCallout tone="info">
                    Horas computadas no saldo da categoria.
                  </ValidationCallout>
                )}
              </div>
              <DialogFooter>
                <Button variant="outline" onClick={() => setVer(null)}>
                  Fechar
                </Button>
                <Button
                  onClick={() => {
                    toast.success(`Comprovante de AC-${ver.id} baixado.`);
                    setVer(null);
                  }}
                >
                  Baixar comprovante
                </Button>
              </DialogFooter>
            </>
          )}
        </DialogContent>
      </Dialog>
    </AppShell>
  );
}

const steps = [
  { key: "cat", label: "Categoria" },
  { key: "dados", label: "Dados" },
  { key: "ok", label: "Confirmação" },
];

function SubmeterWizard({
  step,
  onStep,
  onDone,
}: {
  step: 0 | 1 | 2;
  onStep: (s: 0 | 1 | 2) => void;
  onDone: () => void;
}) {
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
      <Button variant="ghost" size="sm" onClick={onDone}>
        <ArrowLeft className="mr-1 h-4 w-4" /> Cancelar
      </Button>
      <Stepper steps={steps} current={step} />
      {step === 0 && (
        <div className="rounded-xl border bg-card p-6 shadow-card">
          <SectionTitle title="Categoria da atividade" />
          {categoriasQuery.isPending && (
            <p className="mt-4 text-[13px] text-muted-foreground">Carregando categorias...</p>
          )}
          {categoriasQuery.isError && (
            <ValidationCallout tone="danger">
              Não foi possível carregar as categorias. Tente novamente.
            </ValidationCallout>
          )}
          {categoriasQuery.isSuccess && categorias.length === 0 && (
            <ValidationCallout tone="warning">
              Nenhuma categoria de atividade foi cadastrada.
            </ValidationCallout>
          )}
          {categorias.length > 0 && (
            <div className="mt-4 grid grid-cols-2 gap-2">
              {categorias.map((c) => (
                <button
                  key={c.id}
                  onClick={() => setCatId(c.id)}
                  className={`rounded-lg border p-3 text-left text-[13px] transition-colors ${catId === c.id ? "border-primary bg-primary-soft text-primary" : "border-border hover:bg-primary/5"}`}
                >
                  {c.nome}
                </button>
              ))}
            </div>
          )}
          <div className="mt-4 flex justify-end">
            <Button disabled={catId == null} onClick={() => onStep(1)}>
              Avançar
            </Button>
          </div>
        </div>
      )}
      {step === 1 && (
        <div className="rounded-xl border bg-card p-6 shadow-card">
          <SectionTitle title={catNome} />
          <div className="mt-4 grid grid-cols-2 gap-4">
            <FormField label="Descrição" required full>
              <Textarea rows={2} value={descricao} onChange={(e) => setDescricao(e.target.value)} />
            </FormField>
            <FormField label="Carga horária (h)" required>
              <Input
                type="number"
                className="h-10"
                value={horas || ""}
                onChange={(e) => setHoras(Number(e.target.value))}
              />
            </FormField>
            <FormField label="Data de realização" required>
              <Input
                type="date"
                className="h-10"
                value={data}
                onChange={(e) => setData(e.target.value)}
              />
            </FormField>
            <FormField label="Comprovante" required full>
              <Input
                type="file"
                className="h-10"
                onChange={(e) => setCertificado(e.target.files?.[0]?.name ?? "")}
              />
            </FormField>
          </div>
          <ValidationCallout className="mt-3" tone="info">
            O mesmo comprovante não pode ser usado em duas atividades diferentes.
          </ValidationCallout>
          <div className="mt-4 flex justify-end gap-2">
            <Button variant="outline" onClick={() => onStep(0)}>
              Voltar
            </Button>
            <Button
              disabled={submeter.isPending}
              onClick={() => {
                const erro = validarSubmissaoAtividade({
                  categoriaId: catId,
                  descricao,
                  horas,
                  dataRealizacao: data,
                  identificadorCertificado: certificado,
                });
                if (erro) {
                  toast.error(erro);
                  return;
                }
                submeter.mutate(
                  {
                    categoriaId: catId!,
                    horas,
                    dataRealizacao: data,
                    identificadorCertificado: certificado,
                    descricao,
                  },
                  {
                    onSuccess: () => onStep(2),
                    onError: (falha) => toast.error(falha.message),
                  },
                );
              }}
            >
              {submeter.isPending ? "Enviando..." : "Submeter"}
            </Button>
          </div>
        </div>
      )}
      {step === 2 && (
        <div className="space-y-4">
          <SuccessBanner
            title="Atividade submetida!"
            description="Protocolo registrado · Aguardando análise da coordenação."
          />
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
      <Button variant="ghost" size="sm" onClick={onBack}>
        <ArrowLeft className="mr-1 h-4 w-4" /> Voltar
      </Button>
      <div className="rounded-xl border bg-card p-6 shadow-card">
        <SectionTitle
          title={`Solicitar revisão — AC-${id}`}
          subtitle="A revisão será analisada pela coordenação."
        />
        <div className="mt-4 grid grid-cols-2 gap-4">
          <FormField label="Horas pleiteadas" required>
            <Input className="h-10" defaultValue="30" />
          </FormField>
          <FormField label="Justificativa" required full>
            <Textarea
              rows={4}
              value={justificativa}
              onChange={(e) => setJustificativa(e.target.value)}
            />
          </FormField>
          <FormField label="Documento complementar" full>
            <Input type="file" className="h-10" />
          </FormField>
        </div>
        <div className="mt-4 flex justify-end gap-2">
          <Button variant="outline" onClick={onBack}>
            Cancelar
          </Button>
          <Button
            onClick={() =>
              solicitar.mutate(
                { id: Number(id), justificativa },
                {
                  onSuccess: () => {
                    toast.success(`Revisão de AC-${id} enviada.`);
                    onBack();
                  },
                },
              )
            }
          >
            Enviar revisão
          </Button>
        </div>
      </div>
    </div>
  );
}

function CoordView() {
  const pendentesQuery = useAtividadesPendentes();
  const deferidasQuery = useAtividadesPorStatus("DEFERIDA");
  const indeferidasQuery = useAtividadesPorStatus("INDEFERIDA");
  const categoriasQuery = useCategorias();
  const fila = pendentesQuery.data ?? [];
  const indicadores = calcularIndicadoresCoordenacao(
    fila,
    deferidasQuery.data ?? [],
    indeferidasQuery.data ?? [],
  );
  const categorias = categoriasQuery.data ?? [];
  const nomeCategoria = (id: number) =>
    categorias.find((c) => c.id === id)?.nome ?? `Categoria #${id}`;
  const deferir = useDeferir();
  const indeferir = useIndeferir();
  const [atividadeEmAnalise, setAtividadeEmAnalise] = useState<AtividadeComplementarResumo | null>(
    null,
  );
  const [horasAprovadas, setHorasAprovadas] = useState(0);
  const [justificativa, setJustificativa] = useState("");
  const [busca, setBusca] = useState("");
  const [categoriaFiltro, setCategoriaFiltro] = useState<number | null>(null);
  const filaFiltrada = filtrarAtividades(fila, {
    busca,
    categoriaId: categoriaFiltro,
    status: null,
  });

  const fecharAnalise = () => {
    setAtividadeEmAnalise(null);
    setHorasAprovadas(0);
    setJustificativa("");
  };

  const abrirAnalise = (atividade: AtividadeComplementarResumo) => {
    setAtividadeEmAnalise(atividade);
    setHorasAprovadas(atividade.horasSubmetidas);
    setJustificativa("");
  };

  return (
    <div className="space-y-5">
      <StatsRow
        stats={[
          {
            label: "Aguardando validação",
            value: indicadores.aguardandoValidacao,
            tone: "warning",
          },
          { label: "Horas aguardando", value: indicadores.horasAguardando, tone: "info" },
          { label: "Deferidas", value: indicadores.deferidas, tone: "success" },
          { label: "Indeferidas", value: indicadores.indeferidas, tone: "danger" },
        ]}
      />
      <SectionTitle
        title="Fila de validação"
        subtitle="Atividades complementares submetidas aguardando análise."
      />
      <ActionBar
        searchPlaceholder="Buscar protocolo, descrição ou estudante..."
        onSearch={setBusca}
        showFilters={false}
        filters={
          <Select
            value={categoriaFiltro == null ? "todas" : String(categoriaFiltro)}
            onValueChange={(valor) => setCategoriaFiltro(valor === "todas" ? null : Number(valor))}
          >
            <SelectTrigger className="h-10 w-full sm:w-56">
              <SelectValue placeholder="Categoria" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="todas">Todas as categorias</SelectItem>
              {categorias.map((categoria) => (
                <SelectItem key={categoria.id} value={String(categoria.id)}>
                  {categoria.nome}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        }
      />
      <DataTable
        columns={[
          { key: "id", header: "Protocolo", render: (r) => `AC-${r.id}` },
          { key: "estudanteId", header: "Estudante", render: (r) => `Estudante #${r.estudanteId}` },
          { key: "categoriaId", header: "Categoria", render: (r) => nomeCategoria(r.categoriaId) },
          { key: "horasSubmetidas", header: "CH", align: "right" },
          {
            key: "status",
            header: "Status",
            render: (r) => <StatusBadge tone="warning">Em análise</StatusBadge>,
          },
          {
            key: "acoes",
            header: "",
            align: "right",
            render: (r) => (
              <div className="flex justify-end gap-2">
                <RowActionButton tone="info" onClick={() => abrirAnalise(r)}>
                  Validar
                </RowActionButton>
              </div>
            ),
          },
        ]}
        rows={filaFiltrada}
      />
      <Dialog
        open={atividadeEmAnalise != null}
        onOpenChange={(aberto) => !aberto && fecharAnalise()}
      >
        <DialogContent className="max-w-2xl">
          {atividadeEmAnalise && (
            <>
              <DialogHeader>
                <DialogTitle>Analisar atividade AC-{atividadeEmAnalise.id}</DialogTitle>
                <DialogDescription>
                  Confira os dados enviados pelo estudante antes de registrar a decisão.
                </DialogDescription>
              </DialogHeader>

              <div className="grid grid-cols-2 gap-x-6 gap-y-3 text-[13px]">
                <div>
                  <p className="text-muted-foreground">Estudante</p>
                  <p className="font-medium">Estudante #{atividadeEmAnalise.estudanteId}</p>
                </div>
                <div>
                  <p className="text-muted-foreground">Categoria</p>
                  <p className="font-medium">{nomeCategoria(atividadeEmAnalise.categoriaId)}</p>
                </div>
                <div className="col-span-2">
                  <p className="text-muted-foreground">Descrição</p>
                  <p className="font-medium whitespace-pre-wrap">{atividadeEmAnalise.descricao}</p>
                </div>
                <div>
                  <p className="text-muted-foreground">Data de realização</p>
                  <p className="font-medium">
                    {new Date(`${atividadeEmAnalise.dataRealizacao}T00:00:00`).toLocaleDateString(
                      "pt-BR",
                    )}
                  </p>
                </div>
                <div>
                  <p className="text-muted-foreground">Carga horária submetida</p>
                  <p className="font-medium">{atividadeEmAnalise.horasSubmetidas}h</p>
                </div>
                <div className="col-span-2">
                  <p className="text-muted-foreground">Comprovante</p>
                  <p className="font-medium break-all">
                    {atividadeEmAnalise.identificadorCertificado}
                  </p>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4 border-t pt-4">
                <FormField label="Horas aprovadas" required>
                  <Input
                    type="number"
                    min={1}
                    max={atividadeEmAnalise.horasSubmetidas}
                    className="h-10"
                    value={horasAprovadas || ""}
                    onChange={(event) => setHorasAprovadas(Number(event.target.value))}
                  />
                </FormField>
                <FormField label="Justificativa para indeferimento">
                  <Textarea
                    rows={3}
                    value={justificativa}
                    onChange={(event) => setJustificativa(event.target.value)}
                  />
                </FormField>
              </div>

              <DialogFooter>
                <Button variant="outline" onClick={fecharAnalise}>
                  Cancelar
                </Button>
                <Button
                  variant="outline"
                  disabled={indeferir.isPending || deferir.isPending}
                  onClick={() => {
                    const erro = validarJustificativaIndeferimento(justificativa);
                    if (erro) {
                      toast.error(erro);
                      return;
                    }
                    indeferir.mutate(
                      { id: atividadeEmAnalise.id, justificativa: justificativa.trim() },
                      {
                        onSuccess: () => {
                          toast.success(`Atividade AC-${atividadeEmAnalise.id} indeferida.`);
                          fecharAnalise();
                        },
                        onError: (falha) => toast.error(falha.message),
                      },
                    );
                  }}
                >
                  {indeferir.isPending ? "Indeferindo..." : "Indeferir"}
                </Button>
                <Button
                  disabled={deferir.isPending || indeferir.isPending}
                  onClick={() => {
                    const erro = validarHorasAprovadas(
                      horasAprovadas,
                      atividadeEmAnalise.horasSubmetidas,
                    );
                    if (erro) {
                      toast.error(erro);
                      return;
                    }
                    deferir.mutate(
                      { id: atividadeEmAnalise.id, horasAprovadas },
                      {
                        onSuccess: () => {
                          toast.success(`Atividade AC-${atividadeEmAnalise.id} validada.`);
                          fecharAnalise();
                        },
                        onError: (falha) => toast.error(falha.message),
                      },
                    );
                  }}
                >
                  {deferir.isPending ? "Validando..." : "Deferir"}
                </Button>
              </DialogFooter>
            </>
          )}
        </DialogContent>
      </Dialog>
    </div>
  );
}
