package school.cesar.acadlab.dominio.permanenciaacademica;

import static org.apache.commons.lang3.Validate.notNull;
import java.time.LocalDate;
import school.cesar.acadlab.dominio.evento.EventoBarramento;

public class InscricaoServico {
    private final EditalRepositorio editalRepositorio;
    private final InscricaoRepositorio inscricaoRepositorio;
    private final EventoBarramento eventoBarramento;

    public InscricaoServico(EditalRepositorio editalRepositorio,
                             InscricaoRepositorio inscricaoRepositorio,
                             EventoBarramento eventoBarramento) {
        notNull(editalRepositorio, "O repositório de editais não pode ser nulo");
        notNull(inscricaoRepositorio, "O repositório de inscrições não pode ser nulo");
        notNull(eventoBarramento, "O barramento de eventos não pode ser nulo");
        this.editalRepositorio = editalRepositorio;
        this.inscricaoRepositorio = inscricaoRepositorio;
        this.eventoBarramento = eventoBarramento;
    }

    // US02 — RN2: dentro do prazo; RN3: atende critérios (delegado ao chamador)
    public InscricaoId inscrever(EstudantePermanenciaId estudanteId, EditalId editalId,
                                  LocalDate hoje, boolean atendeElegibilidade) {
        notNull(estudanteId, "O estudante não pode ser nulo");
        notNull(editalId, "O edital não pode ser nulo");
        notNull(hoje, "A data não pode ser nula");

        var edital = editalRepositorio.obter(editalId);

        if (!edital.isInscricaoAberta(hoje)) {
            throw new IllegalStateException("inscrição está fora do prazo do edital");
        }

        // RN3: estudante deve atender a todos os critérios de elegibilidade
        if (!atendeElegibilidade) {
            throw new IllegalStateException(
                    "O estudante não atende aos critérios de elegibilidade do edital");
        }

        // Um estudante não pode ter duas inscrições no mesmo edital
        if (inscricaoRepositorio.buscarPorEstudanteEEdital(estudanteId, editalId).isPresent()) {
            throw new IllegalStateException("Estudante já possui inscrição neste edital");
        }

        var id = inscricaoRepositorio.proximoInscricaoId();
        var inscricao = new Inscricao(id, editalId, estudanteId);
        inscricaoRepositorio.salvar(inscricao);
        return id;
    }

    // US03 — RN4: apenas Assistência Estudantil pode deferir ou indeferir
    public void deferir(InscricaoId inscricaoId, AssistenciaEstudantilId responsavelId, int pontuacao) {
        notNull(inscricaoId, "O id da inscrição não pode ser nulo");
        notNull(responsavelId, "O responsável não pode ser nulo");

        var inscricao = inscricaoRepositorio.obter(inscricaoId);
        var evento = inscricao.deferir(pontuacao);
        inscricaoRepositorio.salvar(inscricao);
        eventoBarramento.postar(evento);
    }

    public void indeferir(InscricaoId inscricaoId, AssistenciaEstudantilId responsavelId) {
        notNull(inscricaoId, "O id da inscrição não pode ser nulo");
        notNull(responsavelId, "O responsável não pode ser nulo");

        var inscricao = inscricaoRepositorio.obter(inscricaoId);
        var evento = inscricao.indeferir();
        inscricaoRepositorio.salvar(inscricao);
        eventoBarramento.postar(evento);
    }

    // US06 — RN9: prazo; RN10: apenas um recurso por inscrição
    public void interporRecurso(InscricaoId inscricaoId, EditalId editalId, LocalDate hoje) {
        notNull(inscricaoId, "O id da inscrição não pode ser nulo");
        notNull(editalId, "O edital não pode ser nulo");
        notNull(hoje, "A data não pode ser nula");

        var edital = editalRepositorio.obter(editalId);
        if (!edital.isRecursoAberto(hoje)) {
            throw new IllegalStateException("Recurso fora do prazo definido no edital");
        }

        var inscricao = inscricaoRepositorio.obter(inscricaoId);
        var evento = inscricao.interporRecurso();
        inscricaoRepositorio.salvar(inscricao);
        eventoBarramento.postar(evento);
    }

    // US04 — Gerar classificação: retorna inscrições deferidas ordenadas por pontuação (RN5, RN6)
    public java.util.List<Inscricao> gerarClassificacao(EditalId editalId) {
        notNull(editalId, "O id do edital não pode ser nulo");

        var edital = editalRepositorio.obter(editalId);
        var deferidos = inscricaoRepositorio.buscarDeferidosPorEdital(editalId);

        // RN6: limite de vagas
        return deferidos.stream()
                .sorted((a, b) -> Integer.compare(b.getPontuacao(), a.getPontuacao()))
                .limit(edital.getVagas())
                .toList();
    }
}
