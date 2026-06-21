import type { StatusAtividade } from "./atividades";

export type AtividadeParaIndicadores = {
  categoriaId: number;
  horasSubmetidas: number;
  horasAprovadas: number;
  status: StatusAtividade;
};

export function calcularSaldoPorCategoria(
  atividades: AtividadeParaIndicadores[],
): Record<number, number> {
  return atividades
    .filter((atividade) => atividade.status === "DEFERIDA")
    .reduce<Record<number, number>>((saldo, atividade) => {
      saldo[atividade.categoriaId] = (saldo[atividade.categoriaId] ?? 0) + atividade.horasAprovadas;
      return saldo;
    }, {});
}

export function calcularIndicadoresEstudante(atividades: AtividadeParaIndicadores[]) {
  return atividades.reduce(
    (indicadores, atividade) => {
      if (atividade.status === "DEFERIDA") {
        indicadores.horasValidadas += atividade.horasAprovadas;
      } else if (atividade.status === "PENDENTE" || atividade.status === "REVISAO_SOLICITADA") {
        indicadores.horasEmAnalise += atividade.horasSubmetidas;
      } else if (atividade.status === "INDEFERIDA") {
        indicadores.horasIndeferidas += atividade.horasSubmetidas;
      }
      return indicadores;
    },
    { horasValidadas: 0, horasEmAnalise: 0, horasIndeferidas: 0 },
  );
}

export function calcularIndicadoresCoordenacao(
  pendentes: AtividadeParaIndicadores[],
  deferidas: AtividadeParaIndicadores[],
  indeferidas: AtividadeParaIndicadores[],
) {
  return {
    aguardandoValidacao: pendentes.length,
    horasAguardando: pendentes.reduce((total, atividade) => total + atividade.horasSubmetidas, 0),
    deferidas: deferidas.length,
    indeferidas: indeferidas.length,
  };
}
