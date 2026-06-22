import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api, hojeIso } from "./api";
import { USUARIO_ATUAL } from "./config";

/* ===== Tipos (espelham os DTOs do backend) ===== */

export type EditalResumo = {
  id: number;
  programa: string;
  descricao: string | null;
  vagas: number;
  prazoInscricaoInicio: string | null;
  prazoInscricaoFim: string | null;
  prazoRecursoInicio: string | null;
  prazoRecursoFim: string | null;
  prazoRenovacao: string | null;
  status: "INSCRICOES_ABERTAS" | "INSCRICOES_ENCERRADAS" | "RESULTADO_PUBLICADO" | "ENCERRADO";
};

export type InscricaoResumo = {
  id: number;
  editalId: number;
  estudanteId: number;
  status: "PENDENTE" | "DEFERIDA" | "INDEFERIDA" | "RECURSO_INTERPOSTO" | "RECURSO_ANALISADO";
  pontuacao: number;
  dataInscricao: string | null;
};

export type BeneficioResumo = {
  id: number;
  inscricaoId: number;
  estudanteId: number;
  editalId: number;
  status: "ATIVO" | "SUSPENSO" | "CANCELADO";
  dataAtivacao: string | null;
  prazoRenovacao: string | null;
  solicitouRenovacao: boolean;
};

/* ===== Query keys ===== */

const keys = {
  editais: ["permanencia", "editais"] as const,
  inscricoes: ["permanencia", "inscricoes"] as const,
  inscricoesEstudante: (id: number) => ["permanencia", "inscricoes", "estudante", id] as const,
  beneficios: ["permanencia", "beneficios"] as const,
  beneficiosEstudante: (id: number) => ["permanencia", "beneficios", "estudante", id] as const,
};

/* ===== Consultas ===== */

export function useEditais() {
  return useQuery({ queryKey: keys.editais, queryFn: () => api.get<EditalResumo[]>("permanencia/editais") });
}

export function useTodasInscricoes() {
  return useQuery({ queryKey: keys.inscricoes, queryFn: () => api.get<InscricaoResumo[]>("permanencia/inscricoes") });
}

export function useInscricoesEstudante(estudanteId = USUARIO_ATUAL.estudanteId) {
  return useQuery({
    queryKey: keys.inscricoesEstudante(estudanteId),
    queryFn: () => api.get<InscricaoResumo[]>(`permanencia/estudantes/${estudanteId}/inscricoes`),
  });
}

export function useTodosBeneficios() {
  return useQuery({ queryKey: keys.beneficios, queryFn: () => api.get<BeneficioResumo[]>("permanencia/beneficios") });
}

export function useBeneficiosEstudante(estudanteId = USUARIO_ATUAL.estudanteId) {
  return useQuery({
    queryKey: keys.beneficiosEstudante(estudanteId),
    queryFn: () => api.get<BeneficioResumo[]>(`permanencia/estudantes/${estudanteId}/beneficios`),
  });
}

/* ===== Mutações ===== */

function useInvalidate() {
  const qc = useQueryClient();
  return () => qc.invalidateQueries({ queryKey: ["permanencia"] });
}

export function useInscrever() {
  const invalidate = useInvalidate();
  return useMutation({
    mutationFn: (vars: { editalId: number; atendeElegibilidade: boolean }) =>
      api.post<number>(`permanencia/editais/${vars.editalId}/inscricoes`, {
        estudanteId: USUARIO_ATUAL.estudanteId,
        hoje: hojeIso(),
        atendeElegibilidade: vars.atendeElegibilidade,
      }),
    onSuccess: invalidate,
  });
}

export function useInterporRecurso() {
  const invalidate = useInvalidate();
  return useMutation({
    mutationFn: (vars: { inscricaoId: number; editalId: number }) =>
      api.post(`permanencia/inscricoes/${vars.inscricaoId}/recurso`, {
        editalId: vars.editalId,
        hoje: hojeIso(),
      }),
    onSuccess: invalidate,
  });
}

export function useRenovarBeneficio() {
  const invalidate = useInvalidate();
  return useMutation({
    mutationFn: (beneficioId: number) =>
      api.post(`permanencia/beneficios/${beneficioId}/renovacao`, { hoje: hojeIso() }),
    onSuccess: invalidate,
  });
}

export function useCriarEdital() {
  const invalidate = useInvalidate();
  return useMutation({
    mutationFn: (vars: {
      programa: string;
      descricao: string | null;
      vagas: number;
      prazoInscricaoInicio: string;
      prazoInscricaoFim: string;
      prazoRecursoInicio: string;
      prazoRecursoFim: string;
      prazoRenovacao: string | null;
    }) => api.post<number>("permanencia/editais", vars),
    onSuccess: invalidate,
  });
}

export function usePublicarResultado() {
  const invalidate = useInvalidate();
  return useMutation({
    mutationFn: (editalId: number) =>
      api.put(`permanencia/editais/${editalId}/resultado`, { hoje: hojeIso() }),
    onSuccess: invalidate,
  });
}

export function useEncerrarEdital() {
  const invalidate = useInvalidate();
  return useMutation({
    mutationFn: (editalId: number) => api.put(`permanencia/editais/${editalId}/encerrar`),
    onSuccess: invalidate,
  });
}

export function useDeferirInscricao() {
  const invalidate = useInvalidate();
  return useMutation({
    mutationFn: (vars: { inscricaoId: number; pontuacao: number }) =>
      api.put(`permanencia/inscricoes/${vars.inscricaoId}/deferir`, {
        assistenciaId: USUARIO_ATUAL.assistenciaId,
        pontuacao: vars.pontuacao,
      }),
    onSuccess: invalidate,
  });
}

export function useIndeferirInscricao() {
  const invalidate = useInvalidate();
  return useMutation({
    mutationFn: (inscricaoId: number) =>
      api.put(`permanencia/inscricoes/${inscricaoId}/indeferir`, {
        assistenciaId: USUARIO_ATUAL.assistenciaId,
      }),
    onSuccess: invalidate,
  });
}
