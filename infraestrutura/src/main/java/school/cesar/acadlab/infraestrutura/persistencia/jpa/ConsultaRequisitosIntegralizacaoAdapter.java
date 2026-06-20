package school.cesar.acadlab.infraestrutura.persistencia.jpa;

import org.springframework.stereotype.Component;

import school.cesar.acadlab.dominio.integralizacao.ConsultaRequisitosIntegralizacaoPorta;
import school.cesar.acadlab.dominio.integralizacao.EstudanteId;
import school.cesar.acadlab.dominio.integralizacao.MatrizCurricularId;

/**
 * Adaptador da porta de requisitos da F-08 (RN6): o estudante só é apto à colação
 * se cumpriu 100% das disciplinas obrigatórias, a carga horária optativa mínima e
 * as horas complementares exigidas pela matriz curricular. Delega o cálculo, sobre
 * registros consolidados, à {@link RequisitosIntegralizacaoCalculadora}.
 */
@Component
class ConsultaRequisitosIntegralizacaoAdapter implements ConsultaRequisitosIntegralizacaoPorta {

    private final RequisitosIntegralizacaoCalculadora calculadora;

    ConsultaRequisitosIntegralizacaoAdapter(RequisitosIntegralizacaoCalculadora calculadora) {
        this.calculadora = calculadora;
    }

    @Override
    public boolean cumpreTodosRequisitos(EstudanteId estudanteId, MatrizCurricularId matrizId) {
        return calculadora.calcular(estudanteId.getId(), matrizId.getId()).cumpreTodosRequisitos();
    }
}
