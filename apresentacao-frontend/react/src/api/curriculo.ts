const BASE_URL = "/backend/curriculo";

export type StatusMatriz = "RASCUNHO" | "ATIVA" | "INATIVA";
export type TipoDisciplina = "OBRIGATORIA" | "OPTATIVA";

export type MatrizResumo = {
  id: number;
  cursoId: number;
  nome: string;
  status: StatusMatriz;
};

export type ItemMatrizDetalhe = {
  disciplinaId: number;
  tipo: TipoDisciplina;
  cargaHoraria: number;
  creditos: number;
};

export type DependenciaDetalhe = {
  disciplinaId: number;
  dependenciaId: number;
};

export type MatrizDetalhe = {
  id: number;
  cursoId: number;
  nome: string;
  cargaHorariaMinima: number;
  creditosExigidos: number;
  maximoTrancamentos: number;
  status: StatusMatriz;
  itens: ItemMatrizDetalhe[];
  preRequisitos: DependenciaDetalhe[];
  correquisitos: DependenciaDetalhe[];
};

class ApiError extends Error {}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const res = await fetch(`${BASE_URL}${path}`, {
    headers: { "Content-Type": "application/json" },
    ...init,
  });
  if (!res.ok) {
    const body = await res.json().catch(() => null);
    throw new ApiError(body?.message || `Erro ${res.status} ao comunicar com o backend.`);
  }
  const text = await res.text();
  return (text ? JSON.parse(text) : undefined) as T;
}

export function listarMatrizesPorCurso(cursoId: number) {
  return request<MatrizResumo[]>(`/curso/${cursoId}`);
}

export function buscarDetalheMatriz(id: number) {
  return request<MatrizDetalhe | null>(`/${id}/detalhe`);
}

export function criarMatriz(payload: {
  cursoId: number;
  nome: string;
  cargaHorariaMinima: number;
  creditosExigidos: number;
  maximoTrancamentos: number;
}) {
  return request<number>("", { method: "POST", body: JSON.stringify(payload) });
}

export function adicionarDisciplina(
  matrizId: number,
  payload: { disciplinaId: number; tipo: TipoDisciplina; cargaHoraria: number; creditos: number },
) {
  return request<void>(`/${matrizId}/disciplinas`, { method: "POST", body: JSON.stringify(payload) });
}

export function removerDisciplina(matrizId: number, disciplinaId: number) {
  return request<void>(`/${matrizId}/disciplinas/${disciplinaId}`, { method: "DELETE" });
}

export function adicionarPreRequisito(matrizId: number, disciplinaId: number, dependenciaId: number) {
  return request<void>(`/${matrizId}/prerequisitos`, {
    method: "PUT",
    body: JSON.stringify({ disciplinaId, dependenciaId }),
  });
}

export function adicionarCorrequisito(matrizId: number, disciplinaId: number, dependenciaId: number) {
  return request<void>(`/${matrizId}/correquisitos`, {
    method: "PUT",
    body: JSON.stringify({ disciplinaId, dependenciaId }),
  });
}

export function ativarMatriz(id: number) {
  return request<void>(`/${id}/ativar`, { method: "PUT" });
}

export function desativarMatriz(id: number) {
  return request<void>(`/${id}/desativar`, { method: "PUT" });
}
