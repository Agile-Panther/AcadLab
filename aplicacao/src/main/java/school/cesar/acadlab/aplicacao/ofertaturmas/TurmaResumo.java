package school.cesar.acadlab.aplicacao.ofertaturmas;
public record TurmaResumo(int id, int periodoLetivoId, int disciplinaId,
                           Integer professorId, Integer salaId,
                           String modalidade, int capacidade, String status) {}
