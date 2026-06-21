import { useMemo, useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import {
  AppShell,
  SectionTitle,
  StatsRow,
  DataTable,
  StatusBadge,
  RowActionButton,
  FormField,
  ValidationCallout,
  SuccessBanner,
  TabsRow,
  useProfileSwitcher,
} from "@/components/acadlab";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import { Input } from "@/components/ui/input";
import { ArrowLeft, Download, CreditCard, AlertCircle, FileText, Plus } from "lucide-react";
import { toast } from "sonner";
import {
  useExtrato,
  useRegistrarPagamento,
  useContestar,
  useContestacoesAbertas,
  useDeferirContestacao,
  useIndeferirContestacao,
  type ModoAjuste,
  useBolsas,
  useConcederBolsa,
  useSuspenderBolsa,
  useReativarBolsa,
  useRenovarBolsa,
  useInadimplentes,
  useBloqueioMatricula,
  useRegistrarAcordo,
  type CobrancaResumo,
  type StatusCobranca,
  type BolsaResumo,
  type TipoBolsa,
  type StatusBolsa,
} from "@/lib/financeiro";
import { formatData, formatMoeda, formatValidade } from "@/lib/format";
import { hojeIso } from "@/lib/api";
import { USUARIO_ATUAL } from "@/lib/config";

export const Route = createFileRoute("/financeiro")({
  head: () => ({ meta: [{ title: "Financeiro — AcadLab" }] }),
  component: Page,
});

type View =
  | { kind: "overview" }
  | { kind: "detail"; id: number }
  | { kind: "pagar"; id: number }
  | { kind: "contestar"; id: number }
  | { kind: "comprovante"; id: number };

function Page() {
  const { active: perfil } = useProfileSwitcher([
    { value: "estudante", label: "Estudante", description: "Consulta cobranças e contesta" },
    {
      value: "financeiro",
      label: "Setor Financeiro",
      description: "Concilia pagamentos e contestações",
    },
  ]);
  const [view, setView] = useState<View>({ kind: "overview" });

  const extratoQuery = useExtrato();
  const cobrancas = extratoQuery.data ?? [];
  const registrarPagamento = useRegistrarPagamento();
  const contestar = useContestar();
  const hoje = hojeIso();

  const statusDisplay = (
    c: CobrancaResumo,
  ): { label: string; tone: "success" | "danger" | "warning" | "info" | "neutral" } => {
    if (c.status === "PAGA") return { label: "Paga", tone: "success" };
    if (c.status === "CANCELADA") return { label: "Cancelada", tone: "neutral" };
    if (c.status === "CONTESTADA") return { label: "Em contestação", tone: "warning" };
    return c.vencimento && c.vencimento < hoje
      ? { label: "Atrasada", tone: "danger" }
      : { label: "Em aberto", tone: "info" };
  };
  const descricaoCobranca = (c: CobrancaResumo) =>
    `Mensalidade • venc. ${formatData(c.vencimento)}`;

  const abertas = cobrancas.filter((c) => c.status === "ABERTA");
  const emAtraso = abertas.filter((c) => c.vencimento && c.vencimento < hoje);
  const totalEmAberto = abertas
    .filter((c) => !(c.vencimento && c.vencimento < hoje))
    .reduce((s, c) => s + c.valorAtual, 0);
  const totalEmAtraso = emAtraso.reduce((s, c) => s + c.valorAtual, 0);
  const totalPago = cobrancas
    .filter((c) => c.status === "PAGA")
    .reduce((s, c) => s + c.valorAtual, 0);
  const bolsaPct = Math.max(
    0,
    ...cobrancas.map((c) => c.descontos.reduce((m, d) => Math.max(m, d.percentual), 0)),
  );
  const proxima = abertas
    .slice()
    .sort((a, b) => (a.vencimento ?? "").localeCompare(b.vencimento ?? ""))[0];

  const [motivo, setMotivo] = useState("");

  const subtitle =
    perfil === "financeiro"
      ? "Visão Setor Financeiro · Conciliação"
      : `Estudante #${USUARIO_ATUAL.estudanteId} · Contrato CT-2020-0451`;

  return (
    <AppShell title="Financeiro" subtitle={subtitle}>
      {perfil === "financeiro" && <FinanceiroView />}

      {perfil === "estudante" && view.kind === "overview" && (
        <div className="space-y-5">
          <StatsRow
            stats={[
              { label: "Em aberto", value: formatMoeda(totalEmAberto), tone: "warning" },
              { label: "Em atraso", value: formatMoeda(totalEmAtraso), tone: "danger" },
              { label: "Pago no ano", value: formatMoeda(totalPago), tone: "success" },
              { label: "Bolsa aplicada", value: `${bolsaPct}%`, tone: "info" },
            ]}
          />

          {proxima && (
            <div className="rounded-xl border-l-4 border-l-warning bg-card p-5 shadow-card">
              <div className="flex flex-wrap items-center justify-between gap-3">
                <div>
                  <p className="text-[12px] text-muted-foreground">Próxima cobrança</p>
                  <p className="text-lg font-semibold text-foreground">
                    {descricaoCobranca(proxima)} — {formatMoeda(proxima.valorAtual)}
                  </p>
                  <p className="text-[12px] text-muted-foreground">
                    Vence em {formatData(proxima.vencimento)}
                  </p>
                </div>
                <div className="flex gap-2">
                  <Button onClick={() => setView({ kind: "pagar", id: proxima.id })}>
                    <CreditCard className="mr-2 h-4 w-4" /> Pagar agora
                  </Button>
                  <Button
                    variant="outline"
                    onClick={() => setView({ kind: "contestar", id: proxima.id })}
                  >
                    <AlertCircle className="mr-2 h-4 w-4" /> Contestar
                  </Button>
                </div>
              </div>
            </div>
          )}

          <SectionTitle
            title="Extrato"
            subtitle="Todas as cobranças e pagamentos do seu contrato."
          />
          <DataTable
            columns={[
              { key: "id", header: "Protocolo", render: (r) => `COB-${r.id}` },
              { key: "descricao", header: "Descrição", render: (r) => descricaoCobranca(r) },
              { key: "vencimento", header: "Vencimento", render: (r) => formatData(r.vencimento) },
              {
                key: "valor",
                header: "Valor",
                align: "right",
                render: (r) => formatMoeda(r.valorAtual),
              },
              {
                key: "status",
                header: "Status",
                render: (r) => {
                  const s = statusDisplay(r);
                  return <StatusBadge tone={s.tone}>{s.label}</StatusBadge>;
                },
              },
              {
                key: "acoes",
                header: "",
                align: "right",
                render: (r) =>
                  r.pagamento?.status === "CONFIRMADO" ? (
                    <RowActionButton onClick={() => setView({ kind: "comprovante", id: r.id })}>
                      Comprovante
                    </RowActionButton>
                  ) : (
                    <RowActionButton onClick={() => setView({ kind: "detail", id: r.id })}>
                      Abrir
                    </RowActionButton>
                  ),
              },
            ]}
            rows={cobrancas}
          />
        </div>
      )}

      {perfil === "estudante" &&
        view.kind === "detail" &&
        (() => {
          const c = cobrancas.find((x) => x.id === view.id);
          if (!c) return null;
          const s = statusDisplay(c);
          return (
            <div className="space-y-4">
              <Button variant="ghost" size="sm" onClick={() => setView({ kind: "overview" })}>
                <ArrowLeft className="mr-1 h-4 w-4" /> Extrato
              </Button>
              <div className="rounded-xl border bg-card p-6 shadow-card">
                <SectionTitle
                  title={descricaoCobranca(c)}
                  subtitle={`Vencimento: ${formatData(c.vencimento)}`}
                />
                <p className="mt-4 text-3xl font-bold text-foreground">
                  {formatMoeda(c.valorAtual)}
                </p>
                <StatusBadge className="mt-2" tone={s.tone}>
                  {s.label}
                </StatusBadge>
                <div className="mt-6 flex gap-2">
                  <Button onClick={() => setView({ kind: "pagar", id: c.id })}>
                    <CreditCard className="mr-2 h-4 w-4" /> Pagar
                  </Button>
                  <Button
                    variant="outline"
                    onClick={() => setView({ kind: "contestar", id: c.id })}
                  >
                    Contestar valor
                  </Button>
                </div>
                {s.label === "Atrasada" && (
                  <ValidationCallout className="mt-4" tone="error">
                    Mensalidade em atraso pode gerar restrições em operações acadêmicas.
                  </ValidationCallout>
                )}
              </div>
            </div>
          );
        })()}

      {perfil === "estudante" && view.kind === "pagar" && (
        <div className="space-y-4">
          <Button variant="ghost" size="sm" onClick={() => setView({ kind: "overview" })}>
            <ArrowLeft className="mr-1 h-4 w-4" /> Voltar
          </Button>
          <div className="rounded-xl border bg-card p-6 shadow-card">
            <SectionTitle title="Pagamento" subtitle={`COB-${view.id}`} />
            <div className="mt-4 grid grid-cols-2 gap-3 text-[13px]">
              <div className="rounded-lg border p-4">
                <p className="font-semibold">PIX</p>
                <p className="text-muted-foreground">Aprovação instantânea</p>
              </div>
              <div className="rounded-lg border p-4">
                <p className="font-semibold">Boleto</p>
                <p className="text-muted-foreground">Compensa em até 3 dias</p>
              </div>
            </div>
            <div className="mt-4 flex justify-end gap-2">
              <Button variant="outline" onClick={() => setView({ kind: "overview" })}>
                Cancelar
              </Button>
              <Button
                onClick={() => {
                  const c = cobrancas.find((x) => x.id === view.id);
                  if (!c) return;
                  registrarPagamento.mutate(
                    { id: c.id, valor: c.valorAtual },
                    { onSuccess: () => setView({ kind: "comprovante", id: c.id }) },
                  );
                }}
              >
                Confirmar pagamento
              </Button>
            </div>
          </div>
        </div>
      )}

      {perfil === "estudante" && view.kind === "contestar" && (
        <div className="space-y-4">
          <Button variant="ghost" size="sm" onClick={() => setView({ kind: "overview" })}>
            <ArrowLeft className="mr-1 h-4 w-4" /> Voltar
          </Button>
          <div className="rounded-xl border bg-card p-6 shadow-card">
            <SectionTitle
              title={`Contestar cobrança — COB-${view.id}`}
              subtitle="O Setor Financeiro analisará seu pedido."
            />
            <FormField className="mt-4" label="Motivo da contestação" required full>
              <Textarea rows={5} value={motivo} onChange={(e) => setMotivo(e.target.value)} />
            </FormField>
            <div className="mt-4 flex justify-end gap-2">
              <Button
                variant="outline"
                onClick={() => {
                  setMotivo("");
                  setView({ kind: "overview" });
                }}
              >
                Cancelar
              </Button>
              <Button
                onClick={() =>
                  contestar.mutate(
                    { id: view.id, justificativa: motivo },
                    {
                      onSuccess: () => {
                        toast.success("Contestação enviada.");
                        setMotivo("");
                        setView({ kind: "overview" });
                      },
                    },
                  )
                }
              >
                Enviar contestação
              </Button>
            </div>
          </div>
        </div>
      )}

      {perfil === "estudante" && view.kind === "comprovante" && (
        <div className="space-y-4">
          <SuccessBanner
            title="Pagamento confirmado!"
            description={`Cobrança COB-${view.id} quitada. Comprovante disponível abaixo.`}
          />
          <Button onClick={() => setView({ kind: "overview" })}>
            <Download className="mr-2 h-4 w-4" /> Baixar comprovante
          </Button>
          <Button variant="ghost" onClick={() => setView({ kind: "overview" })}>
            Voltar
          </Button>
        </div>
      )}
    </AppShell>
  );
}

type Lancamento = {
  id: string;
  data: string;
  descricao: string;
  metodo: "PIX" | "Boleto" | "Cartão";
  valor: string;
  status: "Conciliado" | "Pendente" | "Divergente";
};

function FinanceiroView() {
  const [tab, setTab] = useState("contestacoes");

  const contestacoesQuery = useContestacoesAbertas();
  const contestacoesAbertas = contestacoesQuery.data ?? [];
  const deferir = useDeferirContestacao();
  const indeferir = useIndeferirContestacao();
  const [resolverId, setResolverId] = useState<number | null>(null);
  const [decisao, setDecisao] = useState<"DEFERIR" | "INDEFERIR">("DEFERIR");
  const [modoAjuste, setModoAjuste] = useState<ModoAjuste>("PERCENTUAL");
  const [valorAjuste, setValorAjuste] = useState("");
  const [parecerResol, setParecerResol] = useState("");
  const cobrancaResolver = contestacoesAbertas.find((c) => c.id === resolverId) ?? null;
  const fecharResolver = () => {
    setResolverId(null);
    setValorAjuste("");
    setParecerResol("");
    setDecisao("DEFERIR");
    setModoAjuste("PERCENTUAL");
  };

  const inadimplentesQuery = useInadimplentes();
  const inadimplentes = inadimplentesQuery.data ?? [];
  const bloqueioMatricula = useBloqueioMatricula();
  const registrarAcordo = useRegistrarAcordo();
  const [acordoAbertoId, setAcordoAbertoId] = useState<number | null>(null);
  const [acordoPrazo, setAcordoPrazo] = useState("");
  const [acordoDesconto, setAcordoDesconto] = useState("");
  const [acordoObs, setAcordoObs] = useState("");
  const fecharAcordo = () => {
    setAcordoAbertoId(null);
    setAcordoPrazo("");
    setAcordoDesconto("");
    setAcordoObs("");
  };
  const bolsasQuery = useBolsas();
  const bolsas = bolsasQuery.data ?? [];
  const concederBolsa = useConcederBolsa();
  const suspenderBolsa = useSuspenderBolsa();
  const reativarBolsa = useReativarBolsa();
  const renovarBolsa = useRenovarBolsa();
  const rotuloStatusBolsa: Record<StatusBolsa, string> = {
    ATIVA: "Ativa",
    EM_RENOVACAO: "Em renovação",
    SUSPENSA: "Suspensa",
  };
  const tomStatusBolsa = (s: StatusBolsa) =>
    s === "ATIVA" ? "success" : s === "SUSPENSA" ? "danger" : "warning";
  const [lancs, setLancs] = useState<Lancamento[]>([
    {
      id: "LC-9821",
      data: "18/03/2025",
      descricao: "PIX recebido — 2021.0188",
      metodo: "PIX",
      valor: "R$ 1.420,00",
      status: "Conciliado",
    },
    {
      id: "LC-9820",
      data: "18/03/2025",
      descricao: "Boleto compensado — 2022.0345",
      metodo: "Boleto",
      valor: "R$ 1.420,00",
      status: "Conciliado",
    },
    {
      id: "LC-9819",
      data: "17/03/2025",
      descricao: "PIX sem identificação",
      metodo: "PIX",
      valor: "R$ 1.420,00",
      status: "Divergente",
    },
    {
      id: "LC-9818",
      data: "17/03/2025",
      descricao: "Cartão — parcela 3/6",
      metodo: "Cartão",
      valor: "R$ 710,00",
      status: "Pendente",
    },
  ]);
  const [filtroInad, setFiltroInad] = useState("");
  const [novaBolsaOpen, setNovaBolsaOpen] = useState(false);
  const [novaMatricula, setNovaMatricula] = useState("");
  const [novoTipo, setNovoTipo] = useState<TipoBolsa>("MERITO");
  const [novoPercentual, setNovoPercentual] = useState("");
  const [novaValidade, setNovaValidade] = useState("");

  const conciliar = (id: string) => {
    setLancs((p) => p.map((l) => (l.id === id ? { ...l, status: "Conciliado" } : l)));
    toast.success(`Lançamento ${id} conciliado.`);
  };

  const inadimpFiltrada = useMemo(
    () => inadimplentes.filter((i) => String(i.estudanteId).includes(filtroInad)),
    [inadimplentes, filtroInad],
  );

  return (
    <div className="space-y-5">
      <StatsRow
        stats={[
          { label: "Arrecadação no mês", value: "R$ 1,82M", tone: "success" },
          { label: "Inadimplência", value: `${inadimplentes.length} alunos`, tone: "warning" },
          { label: "Contestações abertas", value: contestacoesAbertas.length, tone: "info" },
          {
            label: "Bolsas ativas",
            value: bolsas.filter((b) => b.status === "ATIVA").length,
            tone: "info",
          },
        ]}
      />

      <TabsRow
        value={tab}
        onChange={setTab}
        items={[
          { value: "contestacoes", label: "Contestações", count: contestacoesAbertas.length },
          {
            value: "inadimplencia",
            label: "Inadimplência",
            count: inadimplentes.filter((i) => i.statusMatricula !== "BLOQUEADA").length,
          },
          { value: "bolsas", label: "Bolsas & Descontos", count: bolsas.length },
          {
            value: "conciliacao",
            label: "Conciliação",
            count: lancs.filter((l) => l.status !== "Conciliado").length,
          },
          { value: "relatorios", label: "Relatórios" },
        ]}
      />

      {tab === "contestacoes" && (
        <>
          <SectionTitle
            title="Fila de contestações"
            subtitle="Pedidos abertos por estudantes aguardando análise."
          />
          <DataTable
            columns={[
              { key: "id", header: "Protocolo", render: (r) => `COB-${r.id}` },
              {
                key: "estudante",
                header: "Estudante",
                render: (r) => `Estudante #${r.estudanteId}`,
              },
              {
                key: "cobranca",
                header: "Cobrança",
                render: (r) => `Mensalidade • venc. ${formatData(r.vencimento)}`,
              },
              {
                key: "valor",
                header: "Valor",
                align: "right",
                render: (r) => formatMoeda(r.valorAtual),
              },
              {
                key: "motivo",
                header: "Motivo",
                render: (r) => r.contestacao?.justificativa ?? "—",
              },
              {
                key: "status",
                header: "Status",
                render: () => <StatusBadge tone="info">Em análise</StatusBadge>,
              },
              {
                key: "acoes",
                header: "",
                align: "right",
                render: (r) => (
                  <div className="flex justify-end gap-2">
                    <RowActionButton
                      tone="info"
                      onClick={() => {
                        setResolverId(r.id);
                      }}
                    >
                      Resolver
                    </RowActionButton>
                  </div>
                ),
              },
            ]}
            rows={contestacoesAbertas}
          />
          {cobrancaResolver && (
            <div className="rounded-xl border bg-card p-5 shadow-card">
              <SectionTitle
                title={`Resolver contestação COB-${cobrancaResolver.id}`}
                subtitle={`Valor atual ${formatMoeda(cobrancaResolver.valorAtual)} • Estudante #${cobrancaResolver.estudanteId}`}
              />
              <div className="mt-3 flex flex-col gap-3">
                <div className="flex gap-2">
                  <Button
                    variant={decisao === "DEFERIR" ? "default" : "outline"}
                    onClick={() => setDecisao("DEFERIR")}
                  >
                    Deferir
                  </Button>
                  <Button
                    variant={decisao === "INDEFERIR" ? "default" : "outline"}
                    onClick={() => setDecisao("INDEFERIR")}
                  >
                    Indeferir
                  </Button>
                </div>
                {decisao === "DEFERIR" && (
                  <div className="grid grid-cols-2 gap-3">
                    <FormField label="Modo">
                      <select
                        className="h-9 w-full rounded-md border bg-background px-3 text-sm"
                        value={modoAjuste}
                        onChange={(e) => setModoAjuste(e.target.value as ModoAjuste)}
                      >
                        <option value="PERCENTUAL">Percentual (%)</option>
                        <option value="VALOR">Valor (R$)</option>
                      </select>
                    </FormField>
                    <FormField
                      label={modoAjuste === "PERCENTUAL" ? "Desconto (%)" : "Novo valor (R$)"}
                      required
                    >
                      {modoAjuste === "PERCENTUAL" ? (
                        <select
                          className="h-9 w-full rounded-md border bg-background px-3 text-sm"
                          value={valorAjuste}
                          onChange={(e) => setValorAjuste(e.target.value)}
                        >
                          <option value="">Selecione…</option>
                          {[5, 10, 15, 20, 25, 30, 35, 40, 45, 50].map((p) => (
                            <option key={p} value={p}>
                              {p}%
                            </option>
                          ))}
                        </select>
                      ) : (
                        <Input
                          type="number"
                          placeholder={`entre ${formatMoeda(cobrancaResolver.valorAtual * 0.5)} e ${formatMoeda(cobrancaResolver.valorAtual)}`}
                          value={valorAjuste}
                          onChange={(e) => setValorAjuste(e.target.value)}
                        />
                      )}
                    </FormField>
                  </div>
                )}
                <FormField label="Parecer" required>
                  <Input
                    placeholder="Justificativa da decisão"
                    value={parecerResol}
                    onChange={(e) => setParecerResol(e.target.value)}
                  />
                </FormField>
              </div>
              <div className="mt-4 flex justify-end gap-2">
                <Button variant="outline" onClick={fecharResolver}>
                  Cancelar
                </Button>
                <Button
                  onClick={() => {
                    if (!parecerResol.trim()) {
                      toast.error("Informe o parecer.");
                      return;
                    }
                    if (decisao === "INDEFERIR") {
                      indeferir.mutate(
                        { id: cobrancaResolver.id, parecer: parecerResol },
                        {
                          onSuccess: () => {
                            fecharResolver();
                            toast.success(`Contestação COB-${cobrancaResolver.id} indeferida.`);
                          },
                        },
                      );
                      return;
                    }
                    const v = Number(valorAjuste);
                    if (!valorAjuste || Number.isNaN(v) || v <= 0) {
                      toast.error("Informe o valor do deferimento.");
                      return;
                    }
                    if (
                      modoAjuste === "VALOR" &&
                      (v >= cobrancaResolver.valorAtual || v < cobrancaResolver.valorAtual * 0.5)
                    ) {
                      toast.error("Valor deve reduzir no máximo 50% e ser menor que o atual.");
                      return;
                    }
                    deferir.mutate(
                      {
                        id: cobrancaResolver.id,
                        modo: modoAjuste,
                        valor: v,
                        parecer: parecerResol,
                      },
                      {
                        onSuccess: () => {
                          fecharResolver();
                          toast.success(`Contestação COB-${cobrancaResolver.id} deferida.`);
                        },
                      },
                    );
                  }}
                >
                  Confirmar
                </Button>
              </div>
            </div>
          )}
        </>
      )}

      {tab === "inadimplencia" && (
        <>
          <SectionTitle
            title="Carteira de inadimplência"
            subtitle="Acompanhe atrasos e abra acordos."
          />
          <div className="flex flex-col gap-2 sm:flex-row sm:items-center">
            <Input
              placeholder="Buscar por ID do estudante…"
              value={filtroInad}
              onChange={(e) => setFiltroInad(e.target.value)}
              className="max-w-sm"
            />
          </div>
          <DataTable
            columns={[
              {
                key: "estudanteId",
                header: "Estudante",
                render: (r) => `Estudante #${r.estudanteId}`,
              },
              {
                key: "valorEmAtraso",
                header: "Em atraso",
                align: "right",
                render: (r) => formatMoeda(r.valorEmAtraso),
              },
              {
                key: "diasAtraso",
                header: "Dias",
                align: "right",
                render: (r) => (
                  <span
                    className={
                      r.diasAtraso > 90
                        ? "font-semibold text-destructive"
                        : r.diasAtraso > 30
                          ? "font-semibold text-amber-600"
                          : ""
                    }
                  >
                    {r.diasAtraso}
                  </span>
                ),
              },
              {
                key: "status",
                header: "Status",
                render: (r) => (
                  <StatusBadge tone={r.statusMatricula === "BLOQUEADA" ? "danger" : "warning"}>
                    {r.statusMatricula === "BLOQUEADA" ? "Bloqueado" : "Em atraso"}
                  </StatusBadge>
                ),
              },
              {
                key: "acoes",
                header: "",
                align: "right",
                render: (r) => (
                  <div className="flex justify-end gap-2">
                    <RowActionButton
                      tone="info"
                      onClick={() => {
                        fecharAcordo();
                        setAcordoAbertoId(r.estudanteId);
                      }}
                    >
                      Acordo
                    </RowActionButton>
                    {r.statusMatricula !== "BLOQUEADA" && r.matriculaId > 0 && (
                      <RowActionButton
                        tone="danger"
                        onClick={() =>
                          bloqueioMatricula.mutate(r.matriculaId, {
                            onSuccess: () =>
                              toast.warning(`Matrícula de Estudante #${r.estudanteId} bloqueada.`),
                          })
                        }
                      >
                        Bloquear
                      </RowActionButton>
                    )}
                  </div>
                ),
              },
            ]}
            rows={inadimpFiltrada}
          />
          {acordoAbertoId !== null && (
            <div className="rounded-xl border bg-card p-5 shadow-card">
              <SectionTitle
                title={`Registrar acordo — Estudante #${acordoAbertoId}`}
                subtitle="Formalize a proposta de negociação da dívida."
              />
              <div className="mt-3 grid grid-cols-2 gap-3">
                <FormField label="Prazo de quitação" required>
                  <Input
                    type="date"
                    value={acordoPrazo}
                    onChange={(e) => setAcordoPrazo(e.target.value)}
                  />
                </FormField>
                <FormField label="Desconto (%)" required>
                  <Input
                    type="number"
                    min={0}
                    max={50}
                    placeholder="0–50"
                    value={acordoDesconto}
                    onChange={(e) => setAcordoDesconto(e.target.value)}
                  />
                </FormField>
                <FormField label="Observações" full>
                  <Textarea
                    rows={3}
                    value={acordoObs}
                    onChange={(e) => setAcordoObs(e.target.value)}
                  />
                </FormField>
              </div>
              <div className="mt-4 flex justify-end gap-2">
                <Button variant="outline" onClick={fecharAcordo}>
                  Cancelar
                </Button>
                <Button
                  onClick={() => {
                    if (!acordoPrazo) {
                      toast.error("Informe o prazo de quitação.");
                      return;
                    }
                    const desc = Number(acordoDesconto);
                    if (Number.isNaN(desc) || desc < 0 || desc > 50) {
                      toast.error("Desconto deve ser entre 0 e 50%.");
                      return;
                    }
                    registrarAcordo.mutate(
                      {
                        estudanteId: acordoAbertoId,
                        prazo: acordoPrazo,
                        descontoPercentual: desc,
                        observacoes: acordoObs,
                      },
                      {
                        onSuccess: () => {
                          fecharAcordo();
                          toast.success("Acordo registrado com sucesso.");
                        },
                      },
                    );
                  }}
                >
                  Confirmar acordo
                </Button>
              </div>
            </div>
          )}
        </>
      )}

      {tab === "bolsas" && (
        <>
          <SectionTitle
            title="Bolsas e descontos"
            subtitle="Concessões ativas, renovações e suspensões."
          />
          <div className="flex">
            <Button onClick={() => setNovaBolsaOpen((v) => !v)}>
              <Plus className="mr-2 h-4 w-4" /> Nova concessão
            </Button>
          </div>
          {novaBolsaOpen && (
            <div className="rounded-xl border bg-card p-5 shadow-card">
              <SectionTitle
                title="Conceder bolsa"
                subtitle="Preencha os dados da nova concessão."
              />
              <div className="mt-3 grid grid-cols-2 gap-3">
                <FormField label="Estudante (ID)" required>
                  <Input
                    placeholder="1"
                    value={novaMatricula}
                    onChange={(e) => setNovaMatricula(e.target.value)}
                  />
                </FormField>
                <FormField label="Tipo" required>
                  <Input
                    placeholder="MERITO, PROUNI, FIES, CONVENIO"
                    value={novoTipo}
                    onChange={(e) => setNovoTipo(e.target.value as TipoBolsa)}
                  />
                </FormField>
                <FormField label="Percentual (%)" required>
                  <Input
                    type="number"
                    placeholder="0–100"
                    value={novoPercentual}
                    onChange={(e) => setNovoPercentual(e.target.value)}
                  />
                </FormField>
                <FormField label="Validade" required>
                  <Input
                    type="date"
                    value={novaValidade}
                    onChange={(e) => setNovaValidade(e.target.value)}
                  />
                </FormField>
              </div>
              <div className="mt-4 flex justify-end gap-2">
                <Button variant="outline" onClick={() => setNovaBolsaOpen(false)}>
                  Cancelar
                </Button>
                <Button
                  onClick={() => {
                    if (!novaMatricula.trim() || !Number.isInteger(Number(novaMatricula))) {
                      toast.error("Informe um ID de estudante numérico.");
                      return;
                    }
                    const tipoNorm = novoTipo.trim().toUpperCase();
                    if (
                      !(["PROUNI", "FIES", "MERITO", "CONVENIO"] as string[]).includes(tipoNorm)
                    ) {
                      toast.error("Tipo inválido. Use PROUNI, FIES, MERITO ou CONVENIO.");
                      return;
                    }
                    concederBolsa.mutate(
                      {
                        estudanteId: Number(novaMatricula),
                        tipo: tipoNorm as TipoBolsa,
                        percentual: Number(novoPercentual),
                        validade: novaValidade,
                      },
                      {
                        onSuccess: () => {
                          setNovaBolsaOpen(false);
                          toast.success("Bolsa concedida.");
                        },
                      },
                    );
                  }}
                >
                  Conceder
                </Button>
              </div>
            </div>
          )}
          <DataTable
            columns={[
              { key: "id", header: "Código", render: (r) => `BL-${r.id}` },
              {
                key: "estudante",
                header: "Beneficiário",
                render: (r) => `Estudante #${r.estudanteId}`,
              },
              { key: "tipo", header: "Tipo" },
              { key: "percentual", header: "%", align: "right", render: (r) => `${r.percentual}%` },
              { key: "validade", header: "Validade", render: (r) => formatValidade(r.validade) },
              {
                key: "status",
                header: "Status",
                render: (r) => (
                  <StatusBadge tone={tomStatusBolsa(r.status)}>
                    {rotuloStatusBolsa[r.status]}
                  </StatusBadge>
                ),
              },
              {
                key: "acoes",
                header: "",
                align: "right",
                render: (r) => (
                  <div className="flex justify-end gap-2">
                    {r.status === "SUSPENSA" ? (
                      <RowActionButton
                        tone="info"
                        onClick={() =>
                          reativarBolsa.mutate(r.id, {
                            onSuccess: () => toast.success(`Bolsa BL-${r.id} reativada.`),
                          })
                        }
                      >
                        Reativar
                      </RowActionButton>
                    ) : (
                      <RowActionButton
                        tone="danger"
                        onClick={() =>
                          suspenderBolsa.mutate(r.id, {
                            onSuccess: () => toast.warning(`Bolsa BL-${r.id} suspensa.`),
                          })
                        }
                      >
                        Suspender
                      </RowActionButton>
                    )}
                    <RowActionButton
                      onClick={() =>
                        renovarBolsa.mutate(r.id, {
                          onSuccess: () => toast.success(`Renovação de BL-${r.id} iniciada.`),
                        })
                      }
                    >
                      Renovar
                    </RowActionButton>
                  </div>
                ),
              },
            ]}
            rows={bolsas}
          />
        </>
      )}

      {tab === "conciliacao" && (
        <>
          <SectionTitle
            title="Conciliação bancária"
            subtitle="Lançamentos importados nas últimas 48h."
          />
          <ValidationCallout tone="error">
            {lancs.filter((l) => l.status === "Divergente").length} divergência(s) precisam de
            atenção.
          </ValidationCallout>
          <DataTable
            columns={[
              { key: "id", header: "Lançamento" },
              { key: "data", header: "Data" },
              { key: "descricao", header: "Descrição" },
              { key: "metodo", header: "Método" },
              { key: "valor", header: "Valor", align: "right" },
              {
                key: "status",
                header: "Status",
                render: (r) => (
                  <StatusBadge
                    tone={
                      r.status === "Conciliado"
                        ? "success"
                        : r.status === "Divergente"
                          ? "danger"
                          : "warning"
                    }
                  >
                    {r.status}
                  </StatusBadge>
                ),
              },
              {
                key: "acoes",
                header: "",
                align: "right",
                render: (r) =>
                  r.status !== "Conciliado" ? (
                    <RowActionButton tone="info" onClick={() => conciliar(r.id)}>
                      Conciliar
                    </RowActionButton>
                  ) : (
                    <span className="text-[12px] text-muted-foreground">—</span>
                  ),
              },
            ]}
            rows={lancs}
          />
        </>
      )}

      {tab === "relatorios" && (
        <>
          <SectionTitle
            title="Relatórios financeiros"
            subtitle="Gere e baixe relatórios consolidados do período."
          />
          <div className="grid grid-cols-1 gap-3 md:grid-cols-2 lg:grid-cols-3">
            {[
              { titulo: "Arrecadação mensal", desc: "Receita por curso e tipo de cobrança." },
              {
                titulo: "Aging de inadimplência",
                desc: "Distribuição por faixa de dias em atraso.",
              },
              { titulo: "Bolsas concedidas", desc: "Investimento por modalidade e curso." },
              { titulo: "Conciliação bancária", desc: "Lançamentos e divergências do período." },
              { titulo: "Contestações", desc: "Volume, deferimentos e tempo médio." },
              { titulo: "Previsão de caixa", desc: "Projeção 90 dias com base no contrato." },
            ].map((r) => (
              <div key={r.titulo} className="rounded-xl border bg-card p-5 shadow-card">
                <div className="flex items-start gap-3">
                  <FileText className="mt-0.5 h-5 w-5 text-primary" />
                  <div className="flex-1">
                    <p className="font-semibold text-foreground">{r.titulo}</p>
                    <p className="text-[12px] text-muted-foreground">{r.desc}</p>
                  </div>
                </div>
                <div className="mt-4 flex gap-2">
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={() => toast.success(`Relatório "${r.titulo}" gerado.`)}
                  >
                    <Download className="mr-2 h-4 w-4" /> Gerar PDF
                  </Button>
                  <Button
                    size="sm"
                    variant="ghost"
                    onClick={() => toast.success(`Exportado CSV de "${r.titulo}".`)}
                  >
                    CSV
                  </Button>
                </div>
              </div>
            ))}
          </div>
        </>
      )}
    </div>
  );
}
