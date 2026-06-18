package school.cesar.acadlab.dominio.permanenciaacademica;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;
import static org.apache.commons.lang3.Validate.isTrue;
import java.time.LocalDate;
import school.cesar.acadlab.dominio.evento.EventoBarramento;

public class EditalServico {
    private final EditalRepositorio editalRepositorio;
    private final EventoBarramento eventoBarramento;

    public EditalServico(EditalRepositorio editalRepositorio, EventoBarramento eventoBarramento) {
        notNull(editalRepositorio, "O repositório de editais não pode ser nulo");
        notNull(eventoBarramento, "O barramento de eventos não pode ser nulo");
        this.editalRepositorio = editalRepositorio;
        this.eventoBarramento = eventoBarramento;
    }

    // US01 — RN1: não pode existir mais de um edital com inscrições abertas para o mesmo programa
    public EditalId criar(String programa, int vagas,
                          LocalDate prazoInscricaoInicio, LocalDate prazoInscricaoFim,
                          LocalDate prazoRecursoInicio, LocalDate prazoRecursoFim,
                          LocalDate prazoRenovacao) {
        notNull(programa, "O programa não pode ser nulo");
        notBlank(programa, "O programa não pode estar em branco");
        isTrue(vagas > 0, "O número de vagas deve ser positivo");

        if (editalRepositorio.existeEditalAbertoParaPrograma(programa)) {
            throw new IllegalStateException(
                    "Já existe um edital com inscrições abertas para o programa '" + programa + "'");
        }

        var id = editalRepositorio.proximoEditalId();
        var edital = new Edital(id, programa, vagas, prazoInscricaoInicio, prazoInscricaoFim,
                prazoRecursoInicio, prazoRecursoFim, prazoRenovacao);
        editalRepositorio.salvar(edital);
        return id;
    }

    // US07 — RN11: resultado final publicado somente após encerramento do prazo de recursos
    public void publicarResultado(EditalId editalId, LocalDate hoje) {
        notNull(editalId, "O id do edital não pode ser nulo");
        notNull(hoje, "A data não pode ser nula");

        var edital = editalRepositorio.obter(editalId);
        var evento = edital.publicarResultado(hoje);
        editalRepositorio.salvar(edital);
        eventoBarramento.postar(evento);
    }

    // US08 — RN12: encerramento restrito a editais com resultado final publicado
    public void encerrar(EditalId editalId) {
        notNull(editalId, "O id do edital não pode ser nulo");

        var edital = editalRepositorio.obter(editalId);
        var evento = edital.encerrar();
        editalRepositorio.salvar(edital);
        eventoBarramento.postar(evento);
    }
}
