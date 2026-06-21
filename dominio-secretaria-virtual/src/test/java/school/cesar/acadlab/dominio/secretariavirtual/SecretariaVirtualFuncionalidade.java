package school.cesar.acadlab.dominio.secretariavirtual;

import school.cesar.acadlab.dominio.secretariavirtual.periodo.PeriodoLetivoId;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.TipoSolicitacao;

public class SecretariaVirtualFuncionalidade {
    public Repositorio repositorio;
    public SolicitacaoServico solicitacaoServico;
    public AnaliseServico analiseServico;
    public ConsultaServico consultaServico;
    public boolean calendarioDentroDoPrazo = true;
    public RuntimeException excecao;

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
