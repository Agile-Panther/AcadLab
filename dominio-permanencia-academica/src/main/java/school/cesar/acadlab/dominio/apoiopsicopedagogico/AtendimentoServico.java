package school.cesar.acadlab.dominio.apoiopsicopedagogico;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.List;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.atendimento.Atendimento;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.CasoId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.CasoRepositorio;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.estudante.EstudanteId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.profissional.PsicopedagogoId;
import school.cesar.acadlab.dominio.evento.EventoBarramento;

public class AtendimentoServico {
    private final CasoRepositorio casoRepositorio;
    private final EventoBarramento eventoBarramento;

    public AtendimentoServico(CasoRepositorio casoRepositorio, EventoBarramento eventoBarramento) {
        notNull(casoRepositorio, "O repositório de casos não pode ser nulo");
        notNull(eventoBarramento, "O barramento de eventos não pode ser nulo");
        this.casoRepositorio = casoRepositorio;
        this.eventoBarramento = eventoBarramento;
    }

    public void registrarAtendimento(CasoId casoId, Atendimento atendimento) {
        notNull(casoId, "O id do caso não pode ser nulo");
        notNull(atendimento, "O atendimento não pode ser nulo");

        var caso = casoRepositorio.obter(casoId);
        var evento = caso.registrarAtendimento(atendimento);
        casoRepositorio.salvar(caso);
        eventoBarramento.postar(evento);
    }

    public List<Atendimento> obterAtendimentos(CasoId casoId, PsicopedagogoId responsavelId, EstudanteId estudanteId) {
        notNull(casoId, "O id do caso não pode ser nulo");

        var caso = casoRepositorio.obter(casoId);
        boolean ehResponsavel = responsavelId != null && responsavelId.equals(caso.getResponsavelId());
        boolean ehProprioEstudante = estudanteId != null && estudanteId.equals(caso.getEstudanteId());

        if (!ehResponsavel && !ehProprioEstudante) {
            throw new IllegalStateException("As informações de atendimento são sigilosas e acessíveis apenas ao responsável e ao próprio estudante");
        }
        return caso.getAtendimentos();
    }

    public void encerrarCaso(CasoId casoId) {
        notNull(casoId, "O id do caso não pode ser nulo");

        var caso = casoRepositorio.obter(casoId);
        var evento = caso.encerrar();
        casoRepositorio.salvar(caso);
        eventoBarramento.postar(evento);
    }
}
