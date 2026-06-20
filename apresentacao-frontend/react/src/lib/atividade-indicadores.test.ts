import { describe, expect, it } from "vitest";
import {
  calcularIndicadoresCoordenacao,
  calcularIndicadoresEstudante,
  calcularSaldoPorCategoria,
} from "./atividade-indicadores";

const atividades = [
  { categoriaId: 1, horasSubmetidas: 40, horasAprovadas: 40, status: "DEFERIDA" as const },
  { categoriaId: 2, horasSubmetidas: 60, horasAprovadas: 60, status: "DEFERIDA" as const },
  { categoriaId: 3, horasSubmetidas: 30, horasAprovadas: 0, status: "PENDENTE" as const },
  { categoriaId: 4, horasSubmetidas: 20, horasAprovadas: 20, status: "DEFERIDA" as const },
];

describe("calcularSaldoPorCategoria", () => {
  it("soma somente horas aprovadas de atividades deferidas", () => {
    expect(calcularSaldoPorCategoria(atividades)).toEqual({ 1: 40, 2: 60, 4: 20 });
  });
});

describe("calcularIndicadoresEstudante", () => {
  it("calcula horas reais por situação", () => {
    expect(calcularIndicadoresEstudante(atividades)).toEqual({
      horasValidadas: 120,
      horasEmAnalise: 30,
      horasIndeferidas: 0,
    });
  });
});

describe("calcularIndicadoresCoordenacao", () => {
  it("calcula quantidades e horas usando as consultas reais", () => {
    expect(
      calcularIndicadoresCoordenacao(
        atividades.filter((atividade) => atividade.status === "PENDENTE"),
        atividades.filter((atividade) => atividade.status === "DEFERIDA"),
        [{ categoriaId: 3, horasSubmetidas: 10, horasAprovadas: 0, status: "INDEFERIDA" as const }],
      ),
    ).toEqual({
      aguardandoValidacao: 1,
      horasAguardando: 30,
      deferidas: 3,
      indeferidas: 1,
    });
  });
});
