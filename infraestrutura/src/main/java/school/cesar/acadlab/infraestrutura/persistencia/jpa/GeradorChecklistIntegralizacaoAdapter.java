package school.cesar.acadlab.infraestrutura.persistencia.jpa;

import java.util.List;

import org.springframework.stereotype.Component;

import school.cesar.acadlab.dominio.integralizacao.EstudanteId;
import school.cesar.acadlab.dominio.integralizacao.GeradorChecklistPorta;
import school.cesar.acadlab.dominio.integralizacao.MatrizCurricularId;
import school.cesar.acadlab.dominio.integralizacao.checklist.ItemChecklist;
import school.cesar.acadlab.dominio.integralizacao.checklist.TipoItemChecklist;

/**
 * Gera o checklist de integralização (RN3) a partir dos registros consolidados,
 * delegando o cálculo dos requisitos à {@link RequisitosIntegralizacaoCalculadora}.
 */
@Component
class GeradorChecklistIntegralizacaoAdapter implements GeradorChecklistPorta {

    private final RequisitosIntegralizacaoCalculadora calculadora;

    GeradorChecklistIntegralizacaoAdapter(RequisitosIntegralizacaoCalculadora calculadora) {
        this.calculadora = calculadora;
    }

    @Override
    public List<ItemChecklist> gerar(EstudanteId estudanteId, MatrizCurricularId matrizId) {
        var r = calculadora.calcular(estudanteId.getId(), matrizId.getId());
        return List.of(
                new ItemChecklist(TipoItemChecklist.DISCIPLINAS_OBRIGATORIAS,
                        String.format("Disciplinas obrigatórias aprovadas: %d/%d",
                                r.obrigatoriasCumpridas(), r.obrigatoriasTotal()),
                        r.obrigatoriasOk()),
                new ItemChecklist(TipoItemChecklist.CARGA_OPTATIVA,
                        String.format("Carga horária optativa cumprida: %d/%d h",
                                r.cargaOptativaCumprida(), r.cargaOptativaExigida()),
                        r.optativaOk()),
                new ItemChecklist(TipoItemChecklist.HORAS_COMPLEMENTARES,
                        String.format("Horas complementares deferidas: %d/%d h",
                                r.horasComplementares(), r.horasComplementaresExigidas()),
                        r.complementaresOk()),
                new ItemChecklist(TipoItemChecklist.SITUACAO_DISCENTE,
                        "Situação discente: " + (r.situacaoDiscente() != null
                                ? r.situacaoDiscente().name() : "NÃO INFORMADA"),
                        r.situacaoRegular()));
    }
}
