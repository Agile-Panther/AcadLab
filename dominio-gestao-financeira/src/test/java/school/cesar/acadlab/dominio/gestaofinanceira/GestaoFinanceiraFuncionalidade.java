package school.cesar.acadlab.dominio.gestaofinanceira;

import school.cesar.acadlab.dominio.evento.EventoBarramento;
import school.cesar.acadlab.dominio.evento.EventoObservador;
import java.util.HashSet;
import java.util.Set;

public class GestaoFinanceiraFuncionalidade {
    public GestaoFinanceiraRepositorioTest repositorio;
    public VerificadorMatriculaConfirmadaStub verificadorMatricula;
    public VerificadorAutorizacaoDescontoStub verificadorAutorizacao;
    public EventoBarramentoStub barramento;
    public CobrancaServico servico;
    public RuntimeException excecao;

    public GestaoFinanceiraFuncionalidade() {
        repositorio = new GestaoFinanceiraRepositorioTest();
        verificadorMatricula = new VerificadorMatriculaConfirmadaStub();
        verificadorAutorizacao = new VerificadorAutorizacaoDescontoStub();
        barramento = new EventoBarramentoStub();
        servico = new CobrancaServico(repositorio, verificadorMatricula, verificadorAutorizacao, barramento);
    }

    public static class EventoBarramentoStub implements EventoBarramento {
        @Override
        public <E> void adicionar(EventoObservador<E> observador) {}
        @Override
        public <E> void postar(E evento) {}
    }

    public static class VerificadorMatriculaConfirmadaStub implements VerificadorMatriculaConfirmada {
        private boolean matricula = true;
        public void setMatricula(boolean matricula) { this.matricula = matricula; }
        @Override
        public boolean possuiMatricula(EstudanteId e, PeriodoLetivoId p) { return matricula; }
    }

    public static class VerificadorAutorizacaoDescontoStub implements VerificadorAutorizacaoDesconto {
        private final Set<String> validas = new HashSet<>();
        public void marcarValida(String autorizacaoId) { validas.add(autorizacaoId); }
        @Override
        public boolean autorizacaoValida(String autorizacaoId) { return validas.contains(autorizacaoId); }
    }
}
