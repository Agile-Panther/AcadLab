package school.cesar.acadlab.infraestrutura.persistencia.jpa;

import org.springframework.stereotype.Component;
import school.cesar.acadlab.dominio.secretariavirtual.CalendarioAcademicoPorta;
import school.cesar.acadlab.dominio.secretariavirtual.periodo.PeriodoLetivoId;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.TipoSolicitacao;

@Component
class CalendarioAcademicoAdapter implements CalendarioAcademicoPorta {

    @Override
    public boolean estaDentroDoPrazo(TipoSolicitacao tipo, PeriodoLetivoId periodoLetivoId) {
        return true;
    }
}
