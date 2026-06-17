package school.cesar.acadlab.aplicacao.integralizacao;

import java.util.List;

public record IntegralizacaoResumo(
        int id,
        int estudanteId,
        int matrizCurricularId,
        String status,
        String observacao,
        Integer aprovadorId,
        String dataAprovacao,
        List<ItemChecklistResumo> itensChecklist) {
}
