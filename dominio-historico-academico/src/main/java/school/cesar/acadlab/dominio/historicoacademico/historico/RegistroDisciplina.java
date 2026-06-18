package school.cesar.acadlab.dominio.historicoacademico.historico;

import static org.apache.commons.lang3.Validate.notNull;

public class RegistroDisciplina {
    private final RegistroDisciplinaId id;
    private final DisciplinaId disciplinaId;
    private final TurmaId turmaId;
    private final PeriodoLetivoId periodoLetivoId;
    private final double nota;
    private final double frequencia;
    private SituacaoAcademica situacao;

    public RegistroDisciplina(RegistroDisciplinaId id, DisciplinaId disciplinaId,
                               TurmaId turmaId, PeriodoLetivoId periodoLetivoId,
                               double nota, double frequencia, SituacaoAcademica situacao) {
        notNull(id, "O id não pode ser nulo");
        notNull(disciplinaId, "A disciplina não pode ser nula");
        notNull(turmaId, "A turma não pode ser nula");
        notNull(periodoLetivoId, "O período letivo não pode ser nulo");
        notNull(situacao, "A situação acadêmica não pode ser nula");
        this.id = id;
        this.disciplinaId = disciplinaId;
        this.turmaId = turmaId;
        this.periodoLetivoId = periodoLetivoId;
        this.nota = nota;
        this.frequencia = frequencia;
        this.situacao = situacao;
    }

    public RegistroDisciplinaId getId() { return id; }
    public DisciplinaId getDisciplinaId() { return disciplinaId; }
    public TurmaId getTurmaId() { return turmaId; }
    public PeriodoLetivoId getPeriodoLetivoId() { return periodoLetivoId; }
    public double getNota() { return nota; }
    public double getFrequencia() { return frequencia; }
    public SituacaoAcademica getSituacao() { return situacao; }

    void retificar(SituacaoAcademica novaSituacao) {
        notNull(novaSituacao, "A nova situação não pode ser nula");
        this.situacao = novaSituacao;
    }
}
