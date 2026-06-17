import { get, post, put } from './api';
import type {
  IntegralizacaoResumo,
  ColacaoResumo,
  IniciarAnaliseRequest,
  ItemChecklistRequest,
  ResultadoRequest,
  AprovarAptidaoRequest,
  RegistrarColacaoRequest,
  EstudanteInfo,
} from '../types/integralizacao';

const BASE = '/integralizacoes';

export function buscarTodas(): Promise<IntegralizacaoResumo[]> {
  return get<IntegralizacaoResumo[]>(BASE);
}

export function buscarPorId(id: number): Promise<IntegralizacaoResumo> {
  return get<IntegralizacaoResumo>(`${BASE}/${id}`);
}

export function buscarPorEstudante(estudanteId: number): Promise<IntegralizacaoResumo[]> {
  return get<IntegralizacaoResumo[]>(`${BASE}/estudante/${estudanteId}`);
}

export function iniciarAnalise(req: IniciarAnaliseRequest): Promise<number> {
  return post<number>(BASE, req);
}

export function gerarChecklist(id: number, itens: ItemChecklistRequest[]): Promise<void> {
  return put<void>(`${BASE}/${id}/checklist`, itens);
}

export function registrarResultado(id: number, req: ResultadoRequest): Promise<void> {
  return put<void>(`${BASE}/${id}/resultado`, req);
}

export function aprovarAptidao(id: number, req: AprovarAptidaoRequest): Promise<void> {
  return put<void>(`${BASE}/${id}/aptidao`, req);
}

export function buscarColacaoPorEstudante(estudanteId: number): Promise<ColacaoResumo | null> {
  return get<ColacaoResumo | null>(`${BASE}/colacao/estudante/${estudanteId}`);
}

export function registrarColacao(id: number, req: RegistrarColacaoRequest): Promise<number> {
  return post<number>(`${BASE}/${id}/colacao`, req);
}

const estudantesMock: Record<number, EstudanteInfo> = {
  1: { id: 1, nome: 'Carlos Lima', matricula: '2021001234', curso: 'Engenharia de Software', periodo: '8º Período' },
  2: { id: 2, nome: 'Fernanda Costa', matricula: '2020002345', curso: 'Engenharia de Software', periodo: '9º Período' },
  3: { id: 3, nome: 'Ricardo Alves', matricula: '2019003456', curso: 'Engenharia de Software', periodo: '8º Período' },
  4: { id: 4, nome: 'Juliana Ferreira', matricula: '2020004567', curso: 'Engenharia de Software', periodo: '9º Período' },
  5: { id: 5, nome: 'Thiago Mendes', matricula: '2021005678', curso: 'Engenharia de Software', periodo: '8º Período' },
};

export function getEstudanteInfo(id: number): EstudanteInfo {
  return estudantesMock[id] ?? {
    id,
    nome: `Estudante ${id}`,
    matricula: `${2020000000 + id}`,
    curso: 'Engenharia de Software',
    periodo: '8º Período',
  };
}

export function gerarProtocolo(id: number): string {
  return `#CON-2025-${String(id).padStart(3, '0')}`;
}
