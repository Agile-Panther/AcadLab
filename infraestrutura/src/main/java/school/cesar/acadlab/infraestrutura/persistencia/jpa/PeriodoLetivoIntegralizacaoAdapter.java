package school.cesar.acadlab.infraestrutura.persistencia.jpa;

import java.util.Comparator;

import org.springframework.stereotype.Component;

import school.cesar.acadlab.dominio.integralizacao.ConsultaPeriodoLetivoPorta;
import school.cesar.acadlab.dominio.integralizacao.EstudanteId;
import school.cesar.acadlab.dominio.periodoletivo.StatusPeriodoLetivo;

/**
 * Adaptador da porta de período letivo da F-08 (RN1): a análise de conclusão só
 * pode ser iniciada após o encerramento do último período letivo cursado pelo
 * estudante. O "último período cursado" é o de maior data de fim entre os períodos
 * em que o estudante possui matrícula.
 */
@Component
class PeriodoLetivoIntegralizacaoAdapter implements ConsultaPeriodoLetivoPorta {

    private final MatriculaJpaRepository matriculaRepository;
    private final PeriodoLetivoJpaRepository periodoLetivoRepository;

    PeriodoLetivoIntegralizacaoAdapter(MatriculaJpaRepository matriculaRepository,
                                       PeriodoLetivoJpaRepository periodoLetivoRepository) {
        this.matriculaRepository = matriculaRepository;
        this.periodoLetivoRepository = periodoLetivoRepository;
    }

    @Override
    public boolean ultimoPeriodoEncerrado(EstudanteId estudanteId) {
        var periodoIds = matriculaRepository.findByEstudanteId(estudanteId.getId()).stream()
                .map(m -> m.periodoLetivoId)
                .distinct()
                .toList();
        if (periodoIds.isEmpty()) {
            return false;
        }
        return periodoLetivoRepository.findAllById(periodoIds).stream()
                .max(Comparator.comparing((PeriodoLetivoJpa p) -> p.dataFim))
                .map(p -> p.status == StatusPeriodoLetivo.ENCERRADO)
                .orElse(false);
    }
}
