package school.cesar.acadlab.aplicacao.historicoacademico;

import java.util.List;

public record HistoricoAcademicoResumo(
        int id,
        int estudanteId,
        int matrizCurricularId,
        String situacaoDiscente,
        List<RegistroDisciplinaResumo> registros,
        List<AproveitamentoResumo> aproveitamentos,
        List<AcompanhamentoResumo> acompanhamentos) {
}
