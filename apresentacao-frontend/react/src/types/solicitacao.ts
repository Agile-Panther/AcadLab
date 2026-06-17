export type TipoSolicitacao =
  | 'TRANCAMENTO_DISCIPLINA'
  | 'TRANCAMENTO_PERIODO'
  | 'REVISAO_DE_NOTA'
  | 'APROVEITAMENTO_DISCIPLINA'
  | 'SEGUNDA_VIA_DOCUMENTO'
  | 'CORRECAO_HISTORICO'
  | 'DECLARACAO_VINCULO'
  | 'OUTROS';

export type StatusSolicitacao =
  | 'PENDENTE_ANALISE'
  | 'EM_ANALISE'
  | 'PENDENTE_COMPLEMENTACAO'
  | 'DEFERIDA'
  | 'INDEFERIDA'
  | 'CONCLUIDA'
  | 'CANCELADA';

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

export const TIPO_LABELS: Record<TipoSolicitacao, string> = {
  TRANCAMENTO_DISCIPLINA: 'Trancamento de Disciplina',
  TRANCAMENTO_PERIODO: 'Trancamento de Periodo',
  REVISAO_DE_NOTA: 'Revisao de Nota',
  APROVEITAMENTO_DISCIPLINA: 'Aproveitamento de Disciplina',
  SEGUNDA_VIA_DOCUMENTO: 'Segunda Via de Documento',
  CORRECAO_HISTORICO: 'Correcao de Historico',
  DECLARACAO_VINCULO: 'Declaracao de Vinculo',
  OUTROS: 'Outros',
};

export const STATUS_LABELS: Record<StatusSolicitacao, string> = {
  PENDENTE_ANALISE: 'Pendente de Analise',
  EM_ANALISE: 'Em Analise',
  PENDENTE_COMPLEMENTACAO: 'Pendente de Complementacao',
  DEFERIDA: 'Deferida',
  INDEFERIDA: 'Indeferida',
  CONCLUIDA: 'Concluida',
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
  OUTROS: [],
};
