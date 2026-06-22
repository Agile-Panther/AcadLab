const BASE_URL = "/backend/historicos";

export type SituacaoDiscente = "ATIVO" | "TRANCADO" | "EVADIDO" | "FORMANDO" | "FORMADO";
export type SituacaoAcademica =
  | "APROVADO" | "REPROVADO_NOTA" | "REPROVADO_FALTA" | "TRANCADO" | "APROVEITADO";

export type RegistroDisciplina = {
  id: number;
  disciplinaId: number;
  turmaId: number;
  periodoLetivoId: number;
  nota: number;
  frequencia: number;
  situacao: SituacaoAcademica;
};

export type Aproveitamento = {
  id: number;
  disciplinaEquivalenteId: number;
  cargaHorariaExterna: number;
  cargaHorariaRequerida: number;
  instituicaoOrigem: string;
  disciplinaOrigem: string;
};

export type Acompanhamento = {
  id: number;
  observacao: string;
  data: string;
};

export type Retificacao = {
  id: number;
  registroId: number;
  situacaoAnterior: SituacaoAcademica;
  novaSituacao: SituacaoAcademica;
  responsavelId: number;
  justificativa: string;
  data: string;
};

export type EntradaAuditoria = {
  situacaoAnterior: SituacaoDiscente;
  novaSituacao: SituacaoDiscente;
  responsavelId: number;
  justificativa: string;
  data: string;
};

export type HistoricoAcademico = {
  id: number;
  estudanteId: number;
  matrizCurricularId: number;
  situacaoDiscente: SituacaoDiscente;
  registros: RegistroDisciplina[];
  aproveitamentos: Aproveitamento[];
  acompanhamentos: Acompanhamento[];
  retificacoes: Retificacao[];
  trilhaAuditoria: EntradaAuditoria[];
};

export type RegistroOficial = {
  id: number;
  disciplinaId: number;
  turmaId: number;
  periodoLetivoId: number;
  nota: number;
  frequencia: number;
  situacao: SituacaoAcademica;
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

export function listarHistoricos() {
  return request<HistoricoAcademico[]>("");
}

export function buscarHistoricoPorEstudante(estudanteId: number) {
  return request<HistoricoAcademico>(`/estudante/${estudanteId}`);
}

export function buscarHistoricoPorId(id: number) {
  return request<HistoricoAcademico>(`/${id}`);
}

export function buscarHistoricoOficial(estudanteId: number) {
  return request<RegistroOficial[]>(`/estudante/${estudanteId}/oficial`);
}

// US04 — RN5: atualização gera trilha de auditoria (justificativa + responsável obrigatórios)
export function atualizarSituacaoDiscente(
  historicoId: number,
  payload: { novaSituacao: SituacaoDiscente; responsavelId: number; justificativa: string; data: string },
) {
  return request<void>(`/${historicoId}/situacao`, { method: "PUT", body: JSON.stringify(payload) });
}

// US03 — RN4: acompanhamento apenas para estudante com vínculo ativo
export function registrarAcompanhamento(
  historicoId: number,
  payload: { observacao: string; data: string; estudanteComVinculoAtivo: boolean },
) {
  return request<void>(`/${historicoId}/acompanhamentos`, { method: "POST", body: JSON.stringify(payload) });
}

// US05 — RN7: compatibilidade de carga horária para aproveitamento
export function registrarAproveitamento(
  historicoId: number,
  payload: {
    disciplinaEquivalenteId: number;
    cargaHorariaExterna: number;
    cargaHorariaRequerida: number;
    instituicaoOrigem: string;
    disciplinaOrigem: string;
  },
) {
  return request<void>(`/${historicoId}/aproveitamentos`, { method: "POST", body: JSON.stringify(payload) });
}

// US01 — RN1/RN2: apenas turmas encerradas, situação acadêmica obrigatória
export function consolidarRegistro(
  historicoId: number,
  payload: {
    disciplinaId: number;
    turmaId: number;
    periodoLetivoId: number;
    nota: number;
    frequencia: number;
    situacao: SituacaoAcademica;
    turmaEncerrada: boolean;
  },
) {
  return request<void>(`/${historicoId}/registros`, { method: "POST", body: JSON.stringify(payload) });
}

// US06 — RN8: retificação preserva resultado anterior com rastreabilidade
export function retificarRegistro(
  historicoId: number,
  registroId: number,
  payload: { novaSituacao: SituacaoAcademica; responsavelId: number; justificativa: string; data: string },
) {
  return request<void>(`/${historicoId}/registros/${registroId}/retificar`, {
    method: "PUT",
    body: JSON.stringify(payload),
  });
}

// ─── Helpers de apresentação ────────────────────────────────────────────────

export const SITUACAO_ACADEMICA_LABEL: Record<SituacaoAcademica, string> = {
  APROVADO: "Aprovado",
  REPROVADO_NOTA: "Reprovado por nota",
  REPROVADO_FALTA: "Reprovado por falta",
  TRANCADO: "Trancado",
  APROVEITADO: "Aproveitado",
};

export const SITUACAO_DISCENTE_LABEL: Record<SituacaoDiscente, string> = {
  ATIVO: "Ativo",
  TRANCADO: "Trancado",
  EVADIDO: "Evadido",
  FORMANDO: "Formando",
  FORMADO: "Formado",
};

export function situacaoAcademicaTone(s: SituacaoAcademica): "success" | "info" | "danger" | "warning" {
  if (s === "APROVADO") return "success";
  if (s === "APROVEITADO") return "info";
  if (s === "TRANCADO") return "warning";
  return "danger";
}

export function situacaoDiscenteTone(s: SituacaoDiscente): "success" | "warning" | "danger" | "info" {
  if (s === "ATIVO") return "success";
  if (s === "FORMANDO" || s === "FORMADO") return "info";
  if (s === "TRANCADO") return "warning";
  return "danger";
}
