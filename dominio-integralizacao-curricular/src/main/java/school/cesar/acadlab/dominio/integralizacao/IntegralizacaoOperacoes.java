package school.cesar.acadlab.dominio.integralizacao;

import java.util.List;
import school.cesar.acadlab.dominio.integralizacao.checklist.ItemChecklist;
import school.cesar.acadlab.dominio.integralizacao.integralizacao.IntegralizacaoCurricular;
import school.cesar.acadlab.dominio.integralizacao.integralizacao.IntegralizacaoId;
import school.cesar.acadlab.dominio.integralizacao.integralizacao.StatusIntegralizacao;

public interface IntegralizacaoOperacoes {
    IntegralizacaoCurricular iniciarAnalise(EstudanteId estudanteId, MatrizCurricularId matrizId);
    void gerarChecklist(IntegralizacaoId integralizacaoId, List<ItemChecklist> itens);
    void registrarResultado(IntegralizacaoId integralizacaoId, StatusIntegralizacao resultado);
    void aprovarAptidao(IntegralizacaoId integralizacaoId, CoordenadorId aprovadorId);
}
