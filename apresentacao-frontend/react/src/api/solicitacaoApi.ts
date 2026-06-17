import type { SolicitacaoResumo, AbrirSolicitacaoRequest, DocumentoRequest, Estatisticas } from '../types/solicitacao';

const BASE_URL = '/backend/solicitacoes';

async function fetchJson<T>(url: string, options?: RequestInit): Promise<T> {
  const res = await fetch(url, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  });
  if (!res.ok) {
    const text = await res.text();
    throw new Error(text || `Erro ${res.status}`);
  }
  const contentType = res.headers.get('content-type');
  if (contentType && contentType.includes('application/json')) {
    return res.json();
  }
  return undefined as unknown as T;
}

export function buscarPorEstudante(estudanteId: number): Promise<SolicitacaoResumo[]> {
  return fetchJson(`${BASE_URL}/estudante/${estudanteId}`);
}

export function buscarPorId(id: number): Promise<SolicitacaoResumo> {
  return fetchJson(`${BASE_URL}/${id}`);
}

export function buscarPendentes(): Promise<SolicitacaoResumo[]> {
  return fetchJson(`${BASE_URL}/pendentes`);
}

export function buscarTodas(): Promise<SolicitacaoResumo[]> {
  return fetchJson(`${BASE_URL}/todas`);
}

export function buscarEstatisticas(): Promise<Estatisticas> {
  return fetchJson(`${BASE_URL}/estatisticas`);
}

export function abrirSolicitacao(request: AbrirSolicitacaoRequest): Promise<number> {
  return fetchJson(`${BASE_URL}`, { method: 'POST', body: JSON.stringify(request) });
}

export function complementar(id: number, documento: DocumentoRequest): Promise<void> {
  return fetchJson(`${BASE_URL}/${id}/complementar`, { method: 'PUT', body: JSON.stringify(documento) });
}

export function cancelar(id: number): Promise<void> {
  return fetchJson(`${BASE_URL}/${id}/cancelar`, { method: 'PUT' });
}

export function iniciarAnalise(id: number, analistaId: number): Promise<void> {
  return fetchJson(`${BASE_URL}/${id}/iniciar-analise`, { method: 'PUT', body: JSON.stringify({ analistaId }) });
}

export function deferir(id: number, analistaId: number, justificativa: string, impactoAcademico: boolean): Promise<void> {
  return fetchJson(`${BASE_URL}/${id}/deferir`, { method: 'PUT', body: JSON.stringify({ analistaId, justificativa, impactoAcademico }) });
}

export function indeferir(id: number, analistaId: number, justificativa: string): Promise<void> {
  return fetchJson(`${BASE_URL}/${id}/indeferir`, { method: 'PUT', body: JSON.stringify({ analistaId, justificativa }) });
}

export function solicitarComplementacao(id: number, analistaId: number): Promise<void> {
  return fetchJson(`${BASE_URL}/${id}/solicitar-complementacao`, { method: 'PUT', body: JSON.stringify({ analistaId }) });
}

export function concluir(id: number): Promise<void> {
  return fetchJson(`${BASE_URL}/${id}/concluir`, { method: 'PUT' });
}

export function vincularEConcluir(id: number): Promise<void> {
  return fetchJson(`${BASE_URL}/${id}/vincular-e-concluir`, { method: 'PUT' });
}
