package school.cesar.acadlab.dominio.atividadescomplementares;

import school.cesar.acadlab.dominio.evento.EventoBarramento;
import school.cesar.acadlab.dominio.evento.EventoObservador;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class AtividadesComplementaresFuncionalidade {
    protected AtividadesComplementaresRepositorioTest repositorio;
    protected VerificadorVinculoEstudanteStub verificadorVinculo;
    protected VerificadorCertificadoDuplicadoStub verificadorCertificado;
    protected VerificadorLimiteCategoriaStub verificadorLimite;
    protected VerificadorContabilizacaoIntegralizacaoStub verificadorContabilizacao;
    protected EventoBarramentoStub barramento;
    protected AtividadeComplementarServico servico;

    public AtividadesComplementaresFuncionalidade() {
        repositorio = new AtividadesComplementaresRepositorioTest();
        verificadorVinculo = new VerificadorVinculoEstudanteStub();
        verificadorCertificado = new VerificadorCertificadoDuplicadoStub();
        verificadorLimite = new VerificadorLimiteCategoriaStub();
        verificadorContabilizacao = new VerificadorContabilizacaoIntegralizacaoStub();
        barramento = new EventoBarramentoStub();
        servico = new AtividadeComplementarServico(repositorio, verificadorVinculo,
                verificadorCertificado, verificadorLimite, verificadorContabilizacao, barramento);
    }

    protected static class EventoBarramentoStub implements EventoBarramento {
        @Override
        public <E> void adicionar(EventoObservador<E> observador) {}
        @Override
        public <E> void postar(E evento) {}
    }

    protected static class VerificadorVinculoEstudanteStub implements VerificadorVinculoEstudante {
        private boolean vinculo = true;
        public void setVinculo(boolean vinculo) { this.vinculo = vinculo; }
        @Override
        public boolean estaNoVinculo(EstudanteId estudanteId, LocalDate data) { return vinculo; }
    }

    protected static class VerificadorCertificadoDuplicadoStub implements VerificadorCertificadoDuplicado {
        private final Set<String> utilizados = new HashSet<>();
        public void marcarUtilizado(String cert) { utilizados.add(cert); }
        @Override
        public boolean jaUtilizado(EstudanteId estudanteId, String cert) { return utilizados.contains(cert); }
    }

    protected static class VerificadorLimiteCategoriaStub implements VerificadorLimiteCategoria {
        private boolean excede = false;
        public void setExcede(boolean excede) { this.excede = excede; }
        @Override
        public boolean excedeLimite(EstudanteId e, CategoriaAtividadeId c, int h) { return excede; }
    }

    protected static class VerificadorContabilizacaoIntegralizacaoStub implements VerificadorContabilizacaoIntegralizacao {
        private boolean contabilizada = false;
        public void setContabilizada(boolean contabilizada) { this.contabilizada = contabilizada; }
        @Override
        public boolean foiContabilizada(AtividadeComplementarId id) { return contabilizada; }
    }
}
