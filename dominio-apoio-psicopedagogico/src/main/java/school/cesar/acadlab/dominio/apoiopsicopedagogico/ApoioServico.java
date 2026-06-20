package school.cesar.acadlab.dominio.apoiopsicopedagogico;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.Caso;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.CasoId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.CasoRepositorio;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.estudante.EstudanteId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.solicitacao.SolicitacaoApoio;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.solicitacao.SolicitacaoApoioRepositorio;
import school.cesar.acadlab.dominio.evento.EventoBarramento;

public class ApoioServico {
    private final SolicitacaoApoioRepositorio solicitacaoRepositorio;
    private final CasoRepositorio casoRepositorio;
    private final EventoBarramento eventoBarramento;

    public ApoioServico(SolicitacaoApoioRepositorio solicitacaoRepositorio,
                        CasoRepositorio casoRepositorio,
                        EventoBarramento eventoBarramento) {
        notNull(solicitacaoRepositorio, "O repositório de solicitações não pode ser nulo");
        notNull(casoRepositorio, "O repositório de casos não pode ser nulo");
        notNull(eventoBarramento, "O barramento de eventos não pode ser nulo");
        this.solicitacaoRepositorio = solicitacaoRepositorio;
        this.casoRepositorio = casoRepositorio;
        this.eventoBarramento = eventoBarramento;
    }

    // Cada solicitação abre um caso NOVO. Casos encerrados anteriores permanecem no
    // histórico do estudante (a reabertura é uma ação explícita — ver reabrir).
    public void solicitar(EstudanteId estudanteId, String descricao) {
        notNull(estudanteId, "O estudante não pode ser nulo");
        notNull(descricao, "A descrição não pode ser nula");
        notBlank(descricao, "A descrição não pode estar em branco");

        var casoAtivo = casoRepositorio.pesquisarCasoAbertoPorEstudante(estudanteId);
        if (casoAtivo.isPresent()) {
            throw new IllegalStateException("O estudante já possui um caso psicopedagógico ativo");
        }

        var novoCaso = new Caso(casoRepositorio.proximoId(), estudanteId);
        casoRepositorio.salvar(novoCaso);

        var solicitacaoId = solicitacaoRepositorio.proximaSolicitacaoId();
        var solicitacao = new SolicitacaoApoio(solicitacaoId, estudanteId, descricao);
        solicitacaoRepositorio.salvar(solicitacao);
    }

    // RN1: reabertura de um caso encerrado, mediante ação explícita do estudante.
    public void reabrir(CasoId casoId) {
        notNull(casoId, "O id do caso não pode ser nulo");

        var caso = casoRepositorio.obter(casoId);
        var casoAtivo = casoRepositorio.pesquisarCasoAbertoPorEstudante(caso.getEstudanteId());
        if (casoAtivo.isPresent()) {
            throw new IllegalStateException("O estudante já possui um caso psicopedagógico ativo");
        }

        var evento = caso.reabrir();
        casoRepositorio.salvar(caso);
        eventoBarramento.postar(evento);
    }
}
