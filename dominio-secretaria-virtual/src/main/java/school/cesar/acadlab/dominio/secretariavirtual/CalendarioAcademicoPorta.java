package school.cesar.acadlab.dominio.secretariavirtual;

import school.cesar.acadlab.dominio.secretariavirtual.periodo.PeriodoLetivoId;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.TipoSolicitacao;

public interface CalendarioAcademicoPorta {
    boolean estaDentroDoPrazo(TipoSolicitacao tipo, PeriodoLetivoId periodoLetivoId);
}
