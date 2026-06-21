package school.cesar.acadlab.dominio.integralizacao;

import java.util.List;
import school.cesar.acadlab.dominio.integralizacao.checklist.ItemChecklist;

/**
 * Porta de geração do checklist de integralização a partir de registros
 * consolidados (US02 - RN3). A montagem dos itens não parte de dados informados
 * pelo cliente: é derivada do histórico acadêmico consolidado, das atividades
 * complementares deferidas e da situação discente oficial.
 */
public interface GeradorChecklistPorta {
    List<ItemChecklist> gerar(EstudanteId estudanteId, MatrizCurricularId matrizId);
}
