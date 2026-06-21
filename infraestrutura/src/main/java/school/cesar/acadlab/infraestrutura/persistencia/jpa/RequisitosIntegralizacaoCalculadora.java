package school.cesar.acadlab.infraestrutura.persistencia.jpa;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import school.cesar.acadlab.dominio.atividadescomplementares.StatusAtividade;
import school.cesar.acadlab.dominio.curriculo.TipoDisciplina;
import school.cesar.acadlab.dominio.historicoacademico.historico.SituacaoAcademica;
import school.cesar.acadlab.dominio.historicoacademico.historico.SituacaoDiscente;

/**
 * Cálculo, a partir de registros consolidados, dos requisitos de integralização
 * curricular usados pela F-08 (RN3 e RN6). Cruza:
 *  - histórico acadêmico consolidado (disciplinas aprovadas/aproveitadas e situação discente);
 *  - matriz curricular (disciplinas obrigatórias/optativas e carga horária mínima);
 *  - atividades complementares deferidas.
 *
 * Reutilizado tanto pela porta de requisitos (RN6) quanto pelo gerador de checklist (RN3).
 */
@Component
class RequisitosIntegralizacaoCalculadora {

    // A matriz curricular (F-01) ainda não modela a carga de horas complementares
    // exigida; adota-se o padrão institucional de 200 h (vide V1__seed.sql).
    static final int HORAS_COMPLEMENTARES_EXIGIDAS = 200;

    private final MatrizCurricularJpaRepository matrizRepository;
    private final HistoricoAcademicoJpaRepository historicoRepository;
    private final AtividadeComplementarJpaRepository atividadeRepository;

    RequisitosIntegralizacaoCalculadora(MatrizCurricularJpaRepository matrizRepository,
                                        HistoricoAcademicoJpaRepository historicoRepository,
                                        AtividadeComplementarJpaRepository atividadeRepository) {
        this.matrizRepository = matrizRepository;
        this.historicoRepository = historicoRepository;
        this.atividadeRepository = atividadeRepository;
    }

    Resultado calcular(int estudanteId, int matrizId) {
        var matriz = matrizRepository.findById(matrizId).orElse(null);
        var historico = historicoRepository.findByEstudanteId(estudanteId).orElse(null);

        Set<Integer> aprovadas = disciplinasCumpridas(historico);

        // --- Disciplinas obrigatórias: 100% aprovadas/aproveitadas ---
        List<ItemMatrizJpa> obrigatorias = matriz == null ? List.of()
                : matriz.itens.stream().filter(i -> i.tipo == TipoDisciplina.OBRIGATORIA).toList();
        long obrigCumpridas = obrigatorias.stream().filter(i -> aprovadas.contains(i.disciplinaId)).count();
        boolean obrigatoriasOk = matriz != null && obrigCumpridas == obrigatorias.size();

        // --- Carga horária optativa mínima ---
        int cargaObrigatorias = obrigatorias.stream().mapToInt(i -> i.cargaHoraria).sum();
        int cargaOptativaExigida = matriz == null ? Integer.MAX_VALUE
                : Math.max(0, matriz.cargaHorariaMinima - cargaObrigatorias);
        int cargaOptativaCumprida = matriz == null ? 0
                : matriz.itens.stream()
                        .filter(i -> i.tipo == TipoDisciplina.OPTATIVA && aprovadas.contains(i.disciplinaId))
                        .mapToInt(i -> i.cargaHoraria).sum();
        boolean optativaOk = cargaOptativaCumprida >= cargaOptativaExigida;

        // --- Horas complementares deferidas ---
        int horasComplementares = atividadeRepository.findByEstudanteId(estudanteId).stream()
                .filter(a -> a.status == StatusAtividade.DEFERIDA)
                .mapToInt(a -> a.horasAprovadas).sum();
        boolean complementaresOk = horasComplementares >= HORAS_COMPLEMENTARES_EXIGIDAS;

        // --- Situação discente oficial regular e apta à colação ---
        SituacaoDiscente situacao = historico != null ? historico.situacaoDiscente : null;
        boolean situacaoRegular = situacao == SituacaoDiscente.ATIVO
                || situacao == SituacaoDiscente.FORMANDO;

        return new Resultado(
                obrigatoriasOk, (int) obrigCumpridas, obrigatorias.size(),
                optativaOk, cargaOptativaCumprida, cargaOptativaExigida,
                complementaresOk, horasComplementares, HORAS_COMPLEMENTARES_EXIGIDAS,
                situacaoRegular, situacao);
    }

    private static Set<Integer> disciplinasCumpridas(HistoricoAcademicoJpa historico) {
        Set<Integer> cumpridas = new HashSet<>();
        if (historico == null) {
            return cumpridas;
        }
        historico.registros.stream()
                .filter(r -> r.situacao == SituacaoAcademica.APROVADO
                        || r.situacao == SituacaoAcademica.APROVEITADO)
                .forEach(r -> cumpridas.add(r.disciplinaId));
        historico.aproveitamentos.forEach(a -> cumpridas.add(a.disciplinaEquivalenteId));
        return cumpridas;
    }

    record Resultado(
            boolean obrigatoriasOk, int obrigatoriasCumpridas, int obrigatoriasTotal,
            boolean optativaOk, int cargaOptativaCumprida, int cargaOptativaExigida,
            boolean complementaresOk, int horasComplementares, int horasComplementaresExigidas,
            boolean situacaoRegular, SituacaoDiscente situacaoDiscente) {

        // RN6: aptidão exige 100% das obrigatórias + carga optativa mínima + horas complementares.
        boolean cumpreTodosRequisitos() {
            return obrigatoriasOk && optativaOk && complementaresOk;
        }
    }
}
