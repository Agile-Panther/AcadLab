export type SubmissaoAtividadeForm = {
  categoriaId: number | null;
  descricao: string;
  horas: number;
  dataRealizacao: string;
  identificadorCertificado: string;
};

export function validarSubmissaoAtividade(form: SubmissaoAtividadeForm): string | null {
  if (form.categoriaId == null) return "Selecione uma categoria.";
  if (!form.descricao.trim()) return "Informe a descrição da atividade.";
  if (!Number.isFinite(form.horas) || form.horas <= 0) {
    return "Informe uma carga horária maior que zero.";
  }
  if (!form.dataRealizacao) return "Informe a data de realização.";
  if (!form.identificadorCertificado.trim()) return "Selecione o comprovante.";
  return null;
}
