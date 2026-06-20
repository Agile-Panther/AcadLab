package school.cesar.acadlab.dominio.permanenciaacademica;

import static org.apache.commons.lang3.Validate.notNull;
import java.time.LocalDate;
import java.util.List;
import school.cesar.acadlab.dominio.evento.EventoBarramento;

// Observer: publicar eventos de mudança de status do Beneficio
public class BeneficioServico {
    private final BeneficioRepositorio beneficioRepositorio;
    private final InscricaoRepositorio inscricaoRepositorio;
    private final EditalRepositorio editalRepositorio;
    private final EventoBarramento eventoBarramento;

    public BeneficioServico(BeneficioRepositorio beneficioRepositorio,
                             InscricaoRepositorio inscricaoRepositorio,
                             EditalRepositorio editalRepositorio,
                             EventoBarramento eventoBarramento) {
        notNull(beneficioRepositorio, "O repositório de benefícios não pode ser nulo");
        notNull(inscricaoRepositorio, "O repositório de inscrições não pode ser nulo");
        notNull(editalRepositorio, "O repositório de editais não pode ser nulo");
        notNull(eventoBarramento, "O barramento de eventos não pode ser nulo");
        this.beneficioRepositorio = beneficioRepositorio;
        this.inscricaoRepositorio = inscricaoRepositorio;
        this.editalRepositorio = editalRepositorio;
        this.eventoBarramento = eventoBarramento;
    }

    // Ativa o benefício correspondente a uma inscrição deferida (estudante aceito).
    // Idempotente: se já existe benefício para a inscrição, retorna o existente.
    public BeneficioId ativarParaInscricao(InscricaoId inscricaoId) {
        notNull(inscricaoId, "O id da inscrição não pode ser nulo");

        var existente = beneficioRepositorio.buscarPorInscricao(inscricaoId);
        if (existente.isPresent()) {
            return existente.get().getId();
        }

        var inscricao = inscricaoRepositorio.obter(inscricaoId);
        var edital = editalRepositorio.obter(inscricao.getEditalId());
        var beneficioId = beneficioRepositorio.proximoBeneficioId();
        var beneficio = new Beneficio(beneficioId, inscricaoId, inscricao.getEstudanteId(),
                inscricao.getEditalId(), edital.getPrazoRenovacao());
        beneficioRepositorio.salvar(beneficio);
        return beneficioId;
    }

    // Ativa benefícios para os classificados após publicação do resultado
    public void ativarParaClassificados(EditalId editalId, List<Inscricao> classificados) {
        notNull(editalId, "O id do edital não pode ser nulo");
        notNull(classificados, "A lista de classificados não pode ser nula");

        var edital = editalRepositorio.obter(editalId);
        for (var inscricao : classificados) {
            var beneficioId = beneficioRepositorio.proximoBeneficioId();
            var beneficio = new Beneficio(beneficioId, inscricao.getId(),
                    inscricao.getEstudanteId(), editalId, edital.getPrazoRenovacao());
            beneficioRepositorio.salvar(beneficio);
        }
    }

    // RN8: suspender benefício por não cumprimento dos critérios — Observer
    public void suspender(BeneficioId beneficioId) {
        notNull(beneficioId, "O id do benefício não pode ser nulo");

        var beneficio = beneficioRepositorio.obter(beneficioId);
        var evento = beneficio.suspender();
        beneficioRepositorio.salvar(beneficio);
        eventoBarramento.postar(evento);
    }

    // RN8: cancelar benefício por não cumprimento dos critérios — Observer
    public void cancelar(BeneficioId beneficioId) {
        notNull(beneficioId, "O id do benefício não pode ser nulo");

        var beneficio = beneficioRepositorio.obter(beneficioId);
        var evento = beneficio.cancelar();
        beneficioRepositorio.salvar(beneficio);
        eventoBarramento.postar(evento);
    }

    // US05 — RN7: renovação dentro do prazo — Observer
    public void solicitarRenovacao(BeneficioId beneficioId, LocalDate hoje) {
        notNull(beneficioId, "O id do benefício não pode ser nulo");
        notNull(hoje, "A data não pode ser nula");

        var beneficio = beneficioRepositorio.obter(beneficioId);
        var evento = beneficio.solicitarRenovacao(hoje);
        beneficioRepositorio.salvar(beneficio);
        eventoBarramento.postar(evento);
    }

    public List<Beneficio> listarPorEstudante(EstudantePermanenciaId estudanteId) {
        notNull(estudanteId, "O estudante não pode ser nulo");
        return beneficioRepositorio.buscarPorEstudante(estudanteId);
    }
}
