package school.cesar.acadlab.dominio.integralizacao;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.List;
import school.cesar.acadlab.dominio.integralizacao.checklist.ItemChecklist;
import school.cesar.acadlab.dominio.integralizacao.integralizacao.IntegralizacaoCurricular;
import school.cesar.acadlab.dominio.integralizacao.integralizacao.IntegralizacaoId;
import school.cesar.acadlab.dominio.integralizacao.integralizacao.IntegralizacaoRepositorio;
import school.cesar.acadlab.dominio.integralizacao.integralizacao.StatusIntegralizacao;

public class IntegralizacaoServico {
    private final IntegralizacaoRepositorio repositorio;

    public IntegralizacaoServico(IntegralizacaoRepositorio repositorio) {
        notNull(repositorio, "O repositório não pode ser nulo");
        this.repositorio = repositorio;
    }

    // US01 - RN1/RN2: período encerrado e ausência de pendências verificados externamente
    public IntegralizacaoCurricular iniciarAnalise(EstudanteId estudanteId,
                                                    MatrizCurricularId matrizId) {
        notNull(estudanteId, "O estudante não pode ser nulo");
        notNull(matrizId, "A matriz curricular não pode ser nula");
        var id = repositorio.proximoId();
        var integralizacao = new IntegralizacaoCurricular(id, estudanteId, matrizId);
        repositorio.salvar(integralizacao);
        return integralizacao;
    }

    // US02 - RN3: itens do checklist baseados em dados consolidados (fornecidos pela camada aplicacao)
    public void gerarChecklist(IntegralizacaoId integralizacaoId, List<ItemChecklist> itens) {
        notNull(integralizacaoId, "O id da integralização não pode ser nulo");
        notNull(itens, "Os itens não podem ser nulos");
        var integralizacao = repositorio.obter(integralizacaoId);
        integralizacao.gerarChecklist(itens);
        repositorio.salvar(integralizacao);
    }

    // US02 - RN4: resultado inapto requer pendência (validado no agregado)
    public void registrarResultado(IntegralizacaoId integralizacaoId,
                                    StatusIntegralizacao resultado) {
        notNull(integralizacaoId, "O id da integralização não pode ser nulo");
        notNull(resultado, "O resultado não pode ser nulo");
        var integralizacao = repositorio.obter(integralizacaoId);
        integralizacao.registrarResultado(resultado);
        repositorio.salvar(integralizacao);
    }

    // US03 - RN5: perfil de coordenador verificado externamente; RN6: verificado externamente
    public void aprovarAptidao(IntegralizacaoId integralizacaoId, CoordenadorId aprovadorId) {
        notNull(integralizacaoId, "O id da integralização não pode ser nulo");
        notNull(aprovadorId, "O aprovador não pode ser nulo");
        var integralizacao = repositorio.obter(integralizacaoId);
        integralizacao.aprovarAptidao(aprovadorId);
        repositorio.salvar(integralizacao);
    }
}
