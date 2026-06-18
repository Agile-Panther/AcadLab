export type TipoSolicitacao =
  | 'TRANCAMENTO_DISCIPLINA'
  | 'TRANCAMENTO_PERIODO'
  | 'REVISAO_DE_NOTA'
  | 'APROVEITAMENTO_DISCIPLINA'
  | 'SEGUNDA_VIA_DOCUMENTO'
  | 'CORRECAO_HISTORICO'
  | 'DECLARACAO_VINCULO'
  | 'COLACAO_DE_GRAU'
  | 'CERTIFICADO_MATRICULA'
  | 'HISTORICO_ESCOLAR'
  | 'OUTROS';

export type StatusSolicitacao =
  | 'PENDENTE_ANALISE'
  | 'EM_ANALISE'
  | 'PENDENTE_COMPLEMENTACAO'
  | 'DEFERIDA'
  | 'INDEFERIDA'
  | 'CONCLUIDA'
  | 'CANCELADA';

export interface DocumentoResumo {
  tipo: string;
  nomeArquivo: string;
  dataAnexo: string | null;
}

export interface SolicitacaoResumo {
  id: number;
  estudanteId: number;
  periodoLetivoId: number;
  tipo: TipoSolicitacao;
  status: StatusSolicitacao;
  descricao: string;
  protocoloId: number;
  dataAbertura: string;
  justificativaAnalise: string | null;
  dataAnalise: string | null;
  analistaId: number | null;
  possuiImpactoAcademico: boolean;
  alteracoesVinculadas: boolean;
  documentos: DocumentoResumo[];
}

export interface DocumentoRequest {
  tipo: string;
  nomeArquivo: string;
}

export interface AbrirSolicitacaoRequest {
  estudanteId: number;
  periodoLetivoId: number;
  tipo: string;
  descricao: string;
  documentos: DocumentoRequest[];
}

export interface Estatisticas {
  PENDENTE_ANALISE: number;
  EM_ANALISE: number;
  DEFERIDA: number;
  INDEFERIDA: number;
  CONCLUIDA: number;
  CANCELADA: number;
  PENDENTE_COMPLEMENTACAO: number;
}

export const TIPO_LABELS: Record<TipoSolicitacao, string> = {
  TRANCAMENTO_DISCIPLINA: 'Trancamento de Disciplina',
  TRANCAMENTO_PERIODO: 'Trancamento de Período',
  REVISAO_DE_NOTA: 'Revisão de Nota',
  APROVEITAMENTO_DISCIPLINA: 'Aproveitamento de Disciplina',
  SEGUNDA_VIA_DOCUMENTO: 'Segunda Via de Documento',
  CORRECAO_HISTORICO: 'Correção de Histórico',
  DECLARACAO_VINCULO: 'Declaração de Vínculo',
  COLACAO_DE_GRAU: 'Colação de Grau',
  CERTIFICADO_MATRICULA: 'Certificado de Matrícula',
  HISTORICO_ESCOLAR: 'Histórico Escolar',
  OUTROS: 'Outros',
};

export const STATUS_LABELS: Record<StatusSolicitacao, string> = {
  PENDENTE_ANALISE: 'Pendente',
  EM_ANALISE: 'Em Análise',
  PENDENTE_COMPLEMENTACAO: 'Pend. Complementação',
  DEFERIDA: 'Deferido',
  INDEFERIDA: 'Indeferido',
  CONCLUIDA: 'Concluída',
  CANCELADA: 'Cancelada',
};

export const DOCUMENTOS_OBRIGATORIOS: Record<TipoSolicitacao, string[]> = {
  TRANCAMENTO_DISCIPLINA: ['comprovante_matricula'],
  TRANCAMENTO_PERIODO: ['comprovante_matricula', 'justificativa_formal'],
  REVISAO_DE_NOTA: ['comprovante_avaliacao'],
  APROVEITAMENTO_DISCIPLINA: ['historico_origem', 'ementa_disciplina'],
  SEGUNDA_VIA_DOCUMENTO: [],
  CORRECAO_HISTORICO: ['documento_comprobatorio'],
  DECLARACAO_VINCULO: [],
  COLACAO_DE_GRAU: [],
  CERTIFICADO_MATRICULA: [],
  HISTORICO_ESCOLAR: [],
  OUTROS: [],
};

export function formatProtocolo(protocoloId: number): string {
  const year = new Date().getFullYear();
  return `#${year}-${String(protocoloId).padStart(6, '0')}`;
}

export function formatPeriodo(periodoLetivoId: number): string {
  const map: Record<number, string> = { 1: '2025.1', 2: '2025.2' };
  return map[periodoLetivoId] || `${periodoLetivoId}`;
}
