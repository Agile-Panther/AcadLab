package school.cesar.acadlab.dominio.integralizacao;

public interface ConsultaRequisitosIntegralizacaoPorta {
    boolean cumpreTodosRequisitos(EstudanteId estudanteId, MatrizCurricularId matrizId);
}
