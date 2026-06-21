package school.cesar.acadlab.dominio.apoiopsicopedagogico;

import static org.apache.commons.lang3.Validate.notNull;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.CasoId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.CasoRepositorio;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.profissional.PsicopedagogoId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.triagem.Triagem;
import school.cesar.acadlab.dominio.evento.EventoBarramento;

public class TriagemServico {
    private final CasoRepositorio casoRepositorio;
    private final EventoBarramento eventoBarramento;

    public TriagemServico(CasoRepositorio casoRepositorio, EventoBarramento eventoBarramento) {
        notNull(casoRepositorio, "O repositório de casos não pode ser nulo");
        notNull(eventoBarramento, "O barramento de eventos não pode ser nulo");
        this.casoRepositorio = casoRepositorio;
        this.eventoBarramento = eventoBarramento;
    }

    public void realizarTriagem(CasoId casoId, Triagem triagem) {
        notNull(casoId, "O id do caso não pode ser nulo");
        notNull(triagem, "A triagem não pode ser nula");

        var caso = casoRepositorio.obter(casoId);
        var evento = caso.realizarTriagem(triagem);
        casoRepositorio.salvar(caso);
        eventoBarramento.postar(evento);
    }

    public Triagem obterTriagem(CasoId casoId, PsicopedagogoId solicitanteId) {
        notNull(casoId, "O id do caso não pode ser nulo");
        notNull(solicitanteId, "O id do solicitante não pode ser nulo");

        var caso = casoRepositorio.obter(casoId);
        if (!solicitanteId.equals(caso.getResponsavelId())) {
            throw new IllegalStateException("As informações de triagem são sigilosas e acessíveis apenas ao profissional responsável");
        }
        return caso.getTriagem();
    }
}
