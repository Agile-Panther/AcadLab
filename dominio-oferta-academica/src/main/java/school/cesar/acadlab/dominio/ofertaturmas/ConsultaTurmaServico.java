package school.cesar.acadlab.dominio.ofertaturmas;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.List;
import school.cesar.acadlab.dominio.ofertaturmas.turma.Turma;
import school.cesar.acadlab.dominio.ofertaturmas.turma.TurmaId;
import school.cesar.acadlab.dominio.ofertaturmas.turma.TurmaRepositorio;

public class ConsultaTurmaServico {
    private final TurmaRepositorio turmaRepositorio;

    public ConsultaTurmaServico(TurmaRepositorio turmaRepositorio) {
        notNull(turmaRepositorio, "O repositório de turmas não pode ser nulo");
        this.turmaRepositorio = turmaRepositorio;
    }

    public List<Turma> listarPorPeriodo(PeriodoLetivoId periodoId) {
        notNull(periodoId, "O id do período não pode ser nulo");
        return turmaRepositorio.pesquisarPorPeriodoLetivo(periodoId);
    }

    public Turma buscar(TurmaId id) {
        notNull(id, "O id não pode ser nulo");
        return turmaRepositorio.obter(id);
    }
}
