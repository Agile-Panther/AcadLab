package school.cesar.acadlab.dominio.secretariavirtual;

import school.cesar.acadlab.dominio.secretariavirtual.periodo.PeriodoLetivoId;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.TipoSolicitacao;

public class SecretariaVirtualFuncionalidade {
    protected Repositorio repositorio;
    protected SolicitacaoServico solicitacaoServico;
    protected AnaliseServico analiseServico;
    protected ConsultaServico consultaServico;
    protected boolean calendarioDentroDoPrazo = true;

    public SecretariaVirtualFuncionalidade() {
        repositorio = new Repositorio();
        var servicoReal = new SolicitacaoServicoReal(repositorio);
        CalendarioAcademicoPorta calendario = new CalendarioAcademicoPorta() {
            @Override
            public boolean estaDentroDoPrazo(TipoSolicitacao tipo, PeriodoLetivoId periodoLetivoId) {
                return calendarioDentroDoPrazo;
            }
        };
        solicitacaoServico = new SolicitacaoServicoProxy(servicoReal, repositorio, calendario);
        analiseServico = new AnaliseServico(repositorio);
        consultaServico = new ConsultaServico(repositorio);
    }
}
