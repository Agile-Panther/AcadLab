import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "./api";
import { USUARIO_ATUAL } from "./config";

/* ===== Tipos (espelham os DTOs do backend) ===== */

export type StatusAtividade =
  | "PENDENTE"
  | "DEFERIDA"
  | "INDEFERIDA"
  | "REVISAO_SOLICITADA"
  | "CANCELADA";

export type AtividadeComplementarResumo = {
  id: number;
  estudanteId: number;
  categoriaId: number;
  descricao: string;
  horasSubmetidas: number;
  horasAprovadas: number;
  status: StatusAtividade;
};

export type CategoriaHorasResumo = {
  id: number;
  nome: string;
  limiteHoras: number;
};

/** Resposta de saldo: horas DEFERIDAS por categoriaId. */
export type SaldoCategoria = Record<number, number>;

/** Exigência total institucional de horas complementares (requisito da matriz). */
export const EXIGENCIA_TOTAL_HORAS = 200;

const base = "atividades-complementares";

/* ===== Query keys ===== */

const keys = {
  estudante: (id: number) => ["atividades", "estudante", id] as const,
  saldo: (id: number) => ["atividades", "saldo", id] as const,
  categorias: ["atividades", "categorias"] as const,
  porStatus: (status: StatusAtividade) => ["atividades", "status", status] as const,
};

/* ===== Consultas ===== */

export function useAtividadesEstudante(estudanteId = USUARIO_ATUAL.estudanteId) {
  return useQuery({
    queryKey: keys.estudante(estudanteId),
    queryFn: () => api.get<AtividadeComplementarResumo[]>(`${base}/estudante/${estudanteId}`),
  });
}

export function useSaldoEstudante(estudanteId = USUARIO_ATUAL.estudanteId) {
  return useQuery({
    queryKey: keys.saldo(estudanteId),
    queryFn: () => api.get<SaldoCategoria>(`${base}/estudante/${estudanteId}/saldo`),
  });
}

export function useCategorias() {
  return useQuery({
    queryKey: keys.categorias,
    queryFn: () => api.get<CategoriaHorasResumo[]>(`${base}/categorias`),
  });
}

export function useAtividadesPendentes() {
  return useQuery({
    queryKey: keys.porStatus("PENDENTE"),
    queryFn: () => api.get<AtividadeComplementarResumo[]>(`${base}?status=PENDENTE`),
  });
}

/* ===== Mutações ===== */

function useInvalidate() {
  const qc = useQueryClient();
  return () => qc.invalidateQueries({ queryKey: ["atividades"] });
}

export type SubmeterInput = {
  categoriaId: number;
  horas: number;
  dataRealizacao: string;
  identificadorCertificado: string;
  descricao: string;
};

export function useSubmeter() {
  const invalidate = useInvalidate();
  return useMutation({
    mutationFn: (vars: SubmeterInput) =>
      api.post(`${base}/submeter`, {
        estudanteId: USUARIO_ATUAL.estudanteId,
        categoriaId: vars.categoriaId,
        horas: vars.horas,
        dataRealizacao: vars.dataRealizacao,
        identificadorCertificado: vars.identificadorCertificado,
        descricao: vars.descricao,
      }),
    onSuccess: invalidate,
  });
}

export function useDeferir() {
  const invalidate = useInvalidate();
  return useMutation({
    mutationFn: (vars: { id: number; horasAprovadas: number }) =>
      api.post(`${base}/${vars.id}/deferir`, vars.horasAprovadas),
    onSuccess: invalidate,
  });
}

export function useIndeferir() {
  const invalidate = useInvalidate();
  return useMutation({
    mutationFn: (vars: { id: number; justificativa: string }) =>
      api.post(`${base}/${vars.id}/indeferir`, vars.justificativa),
    onSuccess: invalidate,
  });
}

export function useSolicitarRevisao() {
  const invalidate = useInvalidate();
  return useMutation({
    mutationFn: (vars: { id: number; justificativa: string }) =>
      api.post(`${base}/${vars.id}/solicitar-revisao`, vars.justificativa),
    onSuccess: invalidate,
  });
}

export function useCancelar() {
  const invalidate = useInvalidate();
  return useMutation({
    mutationFn: (id: number) => api.delete(`${base}/${id}/cancelar`),
    onSuccess: invalidate,
  });
}
