import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api, hojeIso } from "./api";
import { USUARIO_ATUAL } from "./config";

/* ===== Tipos (espelham os DTOs do backend) ===== */

export type StatusCaso = "ABERTO" | "EM_ATENDIMENTO" | "ENCERRADO";
export type PrioridadeTriagem = "BAIXA" | "MEDIA" | "ALTA" | "URGENTE";
export type StatusAgendamento = "AGENDADO" | "CONTESTADO";

export type AtendimentoResumo = {
  observacoes: string | null;
  encaminhamento: string | null;
  conclusaoFinal: boolean;
  data: string | null;
};

export type AgendamentoResumo = {
  dataHora: string;
  status: StatusAgendamento;
  justificativaContestacao: string | null;
  horarioSugerido: string | null;
};

export type CasoResumo = {
  id: number;
  estudanteId: number;
  responsavelId: number | null;
  status: StatusCaso;
  motivo: string | null;
  abertura: string | null;
  prioridadeTriagem: PrioridadeTriagem | null;
  triagemObservacoes: string | null;
  agendamento: AgendamentoResumo | null;
  atendimentos: AtendimentoResumo[];
};

/* ===== Query keys ===== */

const keys = {
  casosEstudante: (id: number) => ["apoio", "casos", "estudante", id] as const,
  casosResponsavel: (id: number) => ["apoio", "casos", "responsavel", id] as const,
  casosAbertos: ["apoio", "casos", "abertos"] as const,
};

/* ===== Consultas ===== */

export function useCasosEstudante(estudanteId = USUARIO_ATUAL.estudanteId) {
  return useQuery({
    queryKey: keys.casosEstudante(estudanteId),
    queryFn: () => api.get<CasoResumo[]>(`apoio/estudantes/${estudanteId}/casos`),
  });
}

export function useCasosResponsavel(responsavelId = USUARIO_ATUAL.psicopedagogoId) {
  return useQuery({
    queryKey: keys.casosResponsavel(responsavelId),
    queryFn: () => api.get<CasoResumo[]>(`apoio/casos?responsavelId=${responsavelId}`),
  });
}

export function useCasosAbertos() {
  return useQuery({ queryKey: keys.casosAbertos, queryFn: () => api.get<CasoResumo[]>("apoio/casos/abertos") });
}

/* ===== Mutações ===== */

function useInvalidate() {
  const qc = useQueryClient();
  return () => qc.invalidateQueries({ queryKey: ["apoio"] });
}

export function useSolicitarApoio() {
  const invalidate = useInvalidate();
  return useMutation({
    mutationFn: (descricao: string) =>
      api.post("apoio/solicitacoes", { estudanteId: USUARIO_ATUAL.estudanteId, descricao }),
    onSuccess: invalidate,
  });
}

export function useRealizarTriagem() {
  const invalidate = useInvalidate();
  return useMutation({
    mutationFn: (vars: { casoId: number; prioridade: PrioridadeTriagem; observacoes: string }) =>
      api.post(`apoio/casos/${vars.casoId}/triagem`, {
        prioridade: vars.prioridade,
        observacoes: vars.observacoes,
        responsavelId: USUARIO_ATUAL.psicopedagogoId,
        data: hojeIso(),
      }),
    onSuccess: invalidate,
  });
}

export function useRegistrarAtendimento() {
  const invalidate = useInvalidate();
  return useMutation({
    mutationFn: (vars: {
      casoId: number;
      observacoes: string;
      encaminhamento: string | null;
      conclusaoFinal: boolean;
    }) =>
      api.post(`apoio/casos/${vars.casoId}/atendimentos`, {
        observacoes: vars.observacoes,
        encaminhamento: vars.encaminhamento,
        conclusaoFinal: vars.conclusaoFinal,
        data: hojeIso(),
      }),
    onSuccess: invalidate,
  });
}

export function useEncerrarCaso() {
  const invalidate = useInvalidate();
  return useMutation({
    mutationFn: (casoId: number) => api.put(`apoio/casos/${casoId}/encerrar`),
    onSuccess: invalidate,
  });
}

export function useReabrirCaso() {
  const invalidate = useInvalidate();
  return useMutation({
    mutationFn: (casoId: number) => api.put(`apoio/casos/${casoId}/reabrir`),
    onSuccess: invalidate,
  });
}

export function useAgendar() {
  const invalidate = useInvalidate();
  return useMutation({
    mutationFn: (vars: { casoId: number; dataHora: string }) =>
      api.post(`apoio/casos/${vars.casoId}/agendamento`, { dataHora: vars.dataHora }),
    onSuccess: invalidate,
  });
}

export function useContestarAgendamento() {
  const invalidate = useInvalidate();
  return useMutation({
    mutationFn: (vars: { casoId: number; justificativa: string; horarioSugerido: string | null }) =>
      api.post(`apoio/casos/${vars.casoId}/agendamento/contestacao`, {
        justificativa: vars.justificativa,
        horarioSugerido: vars.horarioSugerido,
      }),
    onSuccess: invalidate,
  });
}
