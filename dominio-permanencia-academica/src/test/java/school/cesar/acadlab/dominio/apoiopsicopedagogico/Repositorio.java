package school.cesar.acadlab.dominio.apoiopsicopedagogico;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.acaopermanencia.AcaoPermanencia;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.acaopermanencia.AcaoPermanenciaId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.acaopermanencia.AcaoPermanenciaRepositorio;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.Caso;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.CasoId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.CasoRepositorio;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.StatusCaso;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.estudante.EstudanteId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.profissional.PsicopedagogoId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.solicitacao.SolicitacaoApoio;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.solicitacao.SolicitacaoApoioId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.solicitacao.SolicitacaoApoioRepositorio;

public class Repositorio implements CasoRepositorio, SolicitacaoApoioRepositorio, AcaoPermanenciaRepositorio {

    /*-----------------------------------------------------------------------*/
    private int proximoCasoIdSeq = 1;
    private final Map<CasoId, Caso> casos = new HashMap<>();

    @Override
    public CasoId proximoId() { return new CasoId(proximoCasoIdSeq++); }

    @Override
    public void salvar(Caso caso) {
        notNull(caso, "O caso não pode ser nulo");
        casos.put(caso.getId(), caso);
    }

    @Override
    public Caso obter(CasoId id) {
        notNull(id, "O id do caso não pode ser nulo");
        return Optional.ofNullable(casos.get(id)).get();
    }

    @Override
    public Optional<Caso> pesquisarCasoAbertoPorEstudante(EstudanteId estudanteId) {
        return casos.values().stream()
                .filter(c -> c.getEstudanteId().equals(estudanteId)
                        && (c.getStatus() == StatusCaso.ABERTO || c.getStatus() == StatusCaso.EM_ATENDIMENTO))
                .findFirst();
    }

    @Override
    public Optional<Caso> pesquisarUltimoCasoEncerradoPorEstudante(EstudanteId estudanteId) {
        return casos.values().stream()
                .filter(c -> c.getEstudanteId().equals(estudanteId) && c.getStatus() == StatusCaso.ENCERRADO)
                .findFirst();
    }

    @Override
    public List<Caso> pesquisarPorResponsavel(PsicopedagogoId responsavelId) {
        var resultado = new ArrayList<Caso>();
        for (var caso : casos.values()) {
            if (responsavelId.equals(caso.getResponsavelId())) {
                resultado.add(caso);
            }
        }
        return resultado;
    }
    /*-----------------------------------------------------------------------*/

    /*-----------------------------------------------------------------------*/
    private int proximaSolicitacaoIdSeq = 1;
    private final Map<SolicitacaoApoioId, SolicitacaoApoio> solicitacoes = new HashMap<>();

    @Override
    public SolicitacaoApoioId proximaSolicitacaoId() { return new SolicitacaoApoioId(proximaSolicitacaoIdSeq++); }

    @Override
    public void salvar(SolicitacaoApoio solicitacao) {
        notNull(solicitacao, "A solicitação não pode ser nula");
        solicitacoes.put(solicitacao.getId(), solicitacao);
    }

    @Override
    public SolicitacaoApoio obter(SolicitacaoApoioId id) {
        notNull(id, "O id da solicitação não pode ser nulo");
        return Optional.ofNullable(solicitacoes.get(id)).get();
    }
    /*-----------------------------------------------------------------------*/

    /*-----------------------------------------------------------------------*/
    private int proximaAcaoIdSeq = 1;
    private final Map<AcaoPermanenciaId, AcaoPermanencia> acoes = new HashMap<>();

    @Override
    public AcaoPermanenciaId proximaAcaoId() { return new AcaoPermanenciaId(proximaAcaoIdSeq++); }

    @Override
    public void salvar(AcaoPermanencia acao) {
        notNull(acao, "A ação de permanência não pode ser nula");
        acoes.put(acao.getId(), acao);
    }

    @Override
    public AcaoPermanencia obter(AcaoPermanenciaId id) {
        notNull(id, "O id da ação não pode ser nulo");
        return Optional.ofNullable(acoes.get(id)).get();
    }
    /*-----------------------------------------------------------------------*/
}
