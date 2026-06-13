package school.cesar.acadlab.dominio.gestaofinanceira;

import school.cesar.acadlab.dominio.evento.EventoBarramento;
import school.cesar.acadlab.dominio.evento.EventoObservador;
import java.util.HashSet;
import java.util.Set;

public class GestaoFinanceiraFuncionalidade {
    protected GestaoFinanceiraRepositorioTest repositorio;
    protected VerificadorMatriculaConfirmadaStub verificadorMatricula;
    protected VerificadorAutorizacaoDescontoStub verificadorAutorizacao;
    protected EventoBarramentoStub barramento;
    protected CobrancaServico servico;

    public GestaoFinanceiraFuncionalidade() {
        repositorio = new GestaoFinanceiraRepositorioTest();
        verificadorMatricula = new VerificadorMatriculaConfirmadaStub();
        verificadorAutorizacao = new VerificadorAutorizacaoDescontoStub();
        barramento = new EventoBarramentoStub();
        servico = new CobrancaServico(repositorio, verificadorMatricula, verificadorAutorizacao, barramento);
    }

    protected static class EventoBarramentoStub implements EventoBarramento {
        @Override
        public <E> void adicionar(EventoObservador<E> observador) {}
        @Override
        public <E> void postar(E evento) {}
    }

    protected static class VerificadorMatriculaConfirmadaStub implements VerificadorMatriculaConfirmada {
        private boolean matricula = true;
        public void setMatricula(boolean matricula) { this.matricula = matricula; }
        @Override
        public boolean possuiMatricula(EstudanteId e, PeriodoLetivoId p) { return matricula; }
    }

    protected static class VerificadorAutorizacaoDescontoStub implements VerificadorAutorizacaoDesconto {
        private final Set<String> validas = new HashSet<>();
        public void marcarValida(String autorizacaoId) { validas.add(autorizacaoId); }
        @Override
        public boolean autorizacaoValida(String autorizacaoId) { return validas.contains(autorizacaoId); }
    }
}
