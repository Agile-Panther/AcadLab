import { useQuery } from "@tanstack/react-query";
import { api } from "./api";
import { USUARIO_ATUAL } from "./config";

type MatriculaResumo = {
  id: number;
  estudanteId: number;
  periodoLetivoId: number;
  status: string;
};

export function useMatriculaAtual(estudanteId = USUARIO_ATUAL.estudanteId) {
  return useQuery({
    queryKey: ["matriculas", estudanteId] as const,
    queryFn: () => api.get<MatriculaResumo[]>(`matriculas?estudanteId=${estudanteId}`),
  });
}

export function isMatriculaBloqueada(matriculas: MatriculaResumo[] | undefined): boolean {
  return (matriculas ?? []).some((m) => m.status === "BLOQUEADA");
}
