import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api, hojeIso } from "./api";
import { USUARIO_ATUAL } from "./config";

/* ===== Tipos (espelham os DTOs do backend) ===== */

export type StatusCobranca = "ABERTA" | "CONTESTADA" | "PAGA" | "CANCELADA";

export type PagamentoResumo = {
  valor: number;
  data: string | null;
  referencia: string | null;
  status: "CONFIRMADO" | "CANCELADO" | null;
};

export type ContestacaoResumo = {
  requerenteId: number | null;
  justificativa: string | null;
  data: string | null;
  status: "PENDENTE" | "DEFERIDA" | "INDEFERIDA" | null;
  parecer: string | null;
};

export type DescontoResumo = {
  percentual: number;
  autorizacaoId: string | null;
  dataAplicacao: string | null;
};

export type CobrancaResumo = {
  id: number;
  contratoId: number;
  estudanteId: number;
  periodoLetivoId: number;
  valorBase: number;
  valorAtual: number;
  vencimento: string | null;
  versao: number;
  status: StatusCobranca;
  pagamento: PagamentoResumo | null;
  contestacao: ContestacaoResumo | null;
  descontos: DescontoResumo[];
};

/* ===== Inadimplentes ===== */

export type InadimplentesResumo = {
  matriculaId: number;
  estudanteId: number;
  valorEmAtraso: number;
  diasAtraso: number;
  statusMatricula: string;
};

/* ===== Query keys ===== */

const keys = {
  extrato: (contratoId: number) => ["financeiro", "extrato", contratoId] as const,
  contestacoes: ["financeiro", "contestacoes-abertas"] as const,
  inadimplentes: ["financeiro", "inadimplentes"] as const,
};

/* ===== Consultas ===== */

export function useExtrato(contratoId = USUARIO_ATUAL.contratoId) {
  return useQuery({
    queryKey: keys.extrato(contratoId),
    queryFn: () => api.get<CobrancaResumo[]>(`cobrancas/contrato/${contratoId}`),
  });
}

export function useInadimplentes() {
  return useQuery({
    queryKey: keys.inadimplentes,
    queryFn: () => api.get<InadimplentesResumo[]>("inadimplentes"),
  });
}

export function useContestacoesAbertas() {
  return useQuery({
    queryKey: keys.contestacoes,
    queryFn: () => api.get<CobrancaResumo[]>("cobrancas/contestacoes-abertas"),
  });
}

/* ===== Mutações ===== */

function useInvalidate() {
  const qc = useQueryClient();
  return () => qc.invalidateQueries({ queryKey: ["financeiro"] });
}

export function useRegistrarPagamento() {
  const invalidate = useInvalidate();
  return useMutation({
    mutationFn: (vars: { id: number; valor: number }) =>
      api.post(`cobrancas/${vars.id}/registrar-pagamento`, {
        valor: vars.valor,
        data: hojeIso(),
        referencia: `PIX-${vars.id}-${hojeIso()}`,
      }),
    onSuccess: invalidate,
  });
}

export function useContestar() {
  const invalidate = useInvalidate();
  return useMutation({
    mutationFn: (vars: { id: number; justificativa: string }) =>
      api.post(`cobrancas/${vars.id}/contestar`, {
        estudanteId: USUARIO_ATUAL.estudanteId,
        justificativa: vars.justificativa,
      }),
    onSuccess: invalidate,
  });
}

export type ModoAjuste = "PERCENTUAL" | "VALOR";

export function useDeferirContestacao() {
  const invalidate = useInvalidate();
  return useMutation({
    mutationFn: (vars: { id: number; modo: ModoAjuste; valor: number; parecer: string }) =>
      api.post(`cobrancas/${vars.id}/deferir-contestacao`, {
        modo: vars.modo,
        valor: vars.valor,
        parecer: vars.parecer,
      }),
    onSuccess: invalidate,
  });
}

export function useIndeferirContestacao() {
  const invalidate = useInvalidate();
  return useMutation({
    mutationFn: (vars: { id: number; parecer: string }) =>
      api.post(`cobrancas/${vars.id}/indeferir-contestacao`, { parecer: vars.parecer }),
    onSuccess: invalidate,
  });
}

export function useBloqueioMatricula() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (matriculaId: number) => api.put(`matriculas/${matriculaId}/bloquear`),
    onSuccess: () => qc.invalidateQueries({ queryKey: keys.inadimplentes }),
  });
}

export function useRegistrarAcordo() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (vars: { estudanteId: number; prazo: string; descontoPercentual: number; observacoes: string }) =>
      api.post("acordos", vars),
    onSuccess: () => qc.invalidateQueries({ queryKey: keys.inadimplentes }),
  });
}

/* ===== Bolsas (sub-projeto A) ===== */

export type TipoBolsa = "PROUNI" | "FIES" | "MERITO" | "CONVENIO";
export type StatusBolsa = "ATIVA" | "SUSPENSA" | "EM_RENOVACAO";

export type BolsaResumo = {
  id: number;
  estudanteId: number;
  tipo: TipoBolsa;
  percentual: number;
  validade: string | null;
  status: StatusBolsa;
};

export function useBolsas() {
  return useQuery({
    queryKey: ["financeiro", "bolsas"] as const,
    queryFn: () => api.get<BolsaResumo[]>("bolsas"),
  });
}

export function useConcederBolsa() {
  const invalidate = useInvalidate();
  return useMutation({
    mutationFn: (vars: { estudanteId: number; tipo: TipoBolsa; percentual: number; validade: string }) =>
      api.post("bolsas/conceder", vars),
    onSuccess: invalidate,
  });
}

export function useSuspenderBolsa() {
  const invalidate = useInvalidate();
  return useMutation({ mutationFn: (id: number) => api.post(`bolsas/${id}/suspender`), onSuccess: invalidate });
}

export function useReativarBolsa() {
  const invalidate = useInvalidate();
  return useMutation({ mutationFn: (id: number) => api.post(`bolsas/${id}/reativar`), onSuccess: invalidate });
}

export function useRenovarBolsa() {
  const invalidate = useInvalidate();
  return useMutation({ mutationFn: (id: number) => api.post(`bolsas/${id}/renovar`), onSuccess: invalidate });
}
