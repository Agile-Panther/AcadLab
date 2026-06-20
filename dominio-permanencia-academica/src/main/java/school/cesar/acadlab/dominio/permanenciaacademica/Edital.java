package school.cesar.acadlab.dominio.permanenciaacademica;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;
import java.time.LocalDate;

public class Edital {
    private final EditalId id;
    private final String programa;
    private final int vagas;
    private final LocalDate prazoInscricaoInicio;
    private final LocalDate prazoInscricaoFim;
    private final LocalDate prazoRecursoInicio;
    private final LocalDate prazoRecursoFim;
    private final LocalDate prazoRenovacao;
    private StatusEdital status;

    public Edital(EditalId id, String programa, int vagas,
                  LocalDate prazoInscricaoInicio, LocalDate prazoInscricaoFim,
                  LocalDate prazoRecursoInicio, LocalDate prazoRecursoFim,
                  LocalDate prazoRenovacao) {
        notNull(id, "O id não pode ser nulo");
        notNull(programa, "O programa não pode ser nulo");
        notBlank(programa, "O programa não pode estar em branco");
        isTrue(vagas > 0, "O número de vagas deve ser positivo");
        notNull(prazoInscricaoInicio, "O início do prazo de inscrição não pode ser nulo");
        notNull(prazoInscricaoFim, "O fim do prazo de inscrição não pode ser nulo");
        notNull(prazoRecursoInicio, "O início do prazo de recurso não pode ser nulo");
        notNull(prazoRecursoFim, "O fim do prazo de recurso não pode ser nulo");
        // prazoRenovacao é opcional (nem todo programa prevê renovação) — pode ser nulo.
        this.id = id;
        this.programa = programa;
        this.vagas = vagas;
        this.prazoInscricaoInicio = prazoInscricaoInicio;
        this.prazoInscricaoFim = prazoInscricaoFim;
        this.prazoRecursoInicio = prazoRecursoInicio;
        this.prazoRecursoFim = prazoRecursoFim;
        this.prazoRenovacao = prazoRenovacao;
        this.status = StatusEdital.INSCRICOES_ABERTAS;
    }

    private Edital(EditalId id, String programa, int vagas,
                   LocalDate prazoInscricaoInicio, LocalDate prazoInscricaoFim,
                   LocalDate prazoRecursoInicio, LocalDate prazoRecursoFim,
                   LocalDate prazoRenovacao, StatusEdital status) {
        this.id = id;
        this.programa = programa;
        this.vagas = vagas;
        this.prazoInscricaoInicio = prazoInscricaoInicio;
        this.prazoInscricaoFim = prazoInscricaoFim;
        this.prazoRecursoInicio = prazoRecursoInicio;
        this.prazoRecursoFim = prazoRecursoFim;
        this.prazoRenovacao = prazoRenovacao;
        this.status = status;
    }

    public static Edital reconstituir(EditalId id, String programa, int vagas,
                                      LocalDate prazoInscricaoInicio, LocalDate prazoInscricaoFim,
                                      LocalDate prazoRecursoInicio, LocalDate prazoRecursoFim,
                                      LocalDate prazoRenovacao, StatusEdital status) {
        return new Edital(id, programa, vagas, prazoInscricaoInicio, prazoInscricaoFim,
                prazoRecursoInicio, prazoRecursoFim, prazoRenovacao, status);
    }

    // RN11: publicar resultado somente após encerramento do prazo de recursos
    public ResultadoPublicadoEvento publicarResultado(LocalDate hoje) {
        notNull(hoje, "A data não pode ser nula");
        if (!hoje.isAfter(prazoRecursoFim)) {
            throw new IllegalStateException("prazo de recursos ainda não encerrou");
        }
        this.status = StatusEdital.RESULTADO_PUBLICADO;
        return new ResultadoPublicadoEvento(this);
    }

    // RN12: encerrar somente após resultado publicado
    public EditalEncerradoEvento encerrar() {
        if (status != StatusEdital.RESULTADO_PUBLICADO) {
            throw new IllegalStateException("resultado final ainda não foi publicado");
        }
        this.status = StatusEdital.ENCERRADO;
        return new EditalEncerradoEvento(this);
    }

    public EditalId getId() { return id; }
    public String getPrograma() { return programa; }
    public int getVagas() { return vagas; }
    public LocalDate getPrazoInscricaoInicio() { return prazoInscricaoInicio; }
    public LocalDate getPrazoInscricaoFim() { return prazoInscricaoFim; }
    public LocalDate getPrazoRecursoInicio() { return prazoRecursoInicio; }
    public LocalDate getPrazoRecursoFim() { return prazoRecursoFim; }
    public LocalDate getPrazoRenovacao() { return prazoRenovacao; }
    public StatusEdital getStatus() { return status; }

    public boolean isInscricaoAberta(LocalDate hoje) {
        return !hoje.isBefore(prazoInscricaoInicio) && !hoje.isAfter(prazoInscricaoFim)
                && status == StatusEdital.INSCRICOES_ABERTAS;
    }

    public boolean isRecursoAberto(LocalDate hoje) {
        return !hoje.isBefore(prazoRecursoInicio) && !hoje.isAfter(prazoRecursoFim);
    }

    public static abstract class EditalEvento {
        private final Edital edital;
        protected EditalEvento(Edital edital) { this.edital = edital; }
        public Edital getEdital() { return edital; }
    }

    public static class ResultadoPublicadoEvento extends EditalEvento {
        private ResultadoPublicadoEvento(Edital edital) { super(edital); }
    }

    public static class EditalEncerradoEvento extends EditalEvento {
        private EditalEncerradoEvento(Edital edital) { super(edital); }
    }
}
