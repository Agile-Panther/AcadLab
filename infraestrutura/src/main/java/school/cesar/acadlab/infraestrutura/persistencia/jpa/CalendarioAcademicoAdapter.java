package school.cesar.acadlab.infraestrutura.persistencia.jpa;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.stereotype.Component;
import school.cesar.acadlab.dominio.periodoletivo.TipoJanela;
import school.cesar.acadlab.dominio.secretariavirtual.CalendarioAcademicoPorta;
import school.cesar.acadlab.dominio.secretariavirtual.periodo.PeriodoLetivoId;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.TipoSolicitacao;

@Component
class CalendarioAcademicoAdapter implements CalendarioAcademicoPorta {

    private static final Map<TipoSolicitacao, TipoJanela> MAPEAMENTO = Map.of(
            TipoSolicitacao.TRANCAMENTO_DISCIPLINA, TipoJanela.TRANCAMENTO,
            TipoSolicitacao.TRANCAMENTO_PERIODO, TipoJanela.TRANCAMENTO,
            TipoSolicitacao.REVISAO_DE_NOTA, TipoJanela.REVISAO_NOTAS
    );

    private final PeriodoLetivoJpaRepository periodoLetivoRepository;

    CalendarioAcademicoAdapter(PeriodoLetivoJpaRepository periodoLetivoRepository) {
        this.periodoLetivoRepository = periodoLetivoRepository;
    }

    @Override
    public boolean estaDentroDoPrazo(TipoSolicitacao tipo, PeriodoLetivoId periodoLetivoId) {
        var periodoJpa = periodoLetivoRepository.findById(periodoLetivoId.getId())
                .orElseThrow(() -> new IllegalArgumentException("período letivo não encontrado"));

        LocalDate hoje = LocalDate.now();
        TipoJanela tipoJanela = MAPEAMENTO.get(tipo);

        if (tipoJanela != null) {
            return periodoJpa.janelas.stream()
                    .filter(j -> j.tipo == tipoJanela)
                    .anyMatch(j -> !hoje.isBefore(j.dataInicio) && !hoje.isAfter(j.dataFim));
        }

        return !hoje.isBefore(periodoJpa.dataInicio) && !hoje.isAfter(periodoJpa.dataFim);
    }
}
