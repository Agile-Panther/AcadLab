package school.cesar.acadlab.dominio.matricula.matricula;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemMatricula {
    private TurmaId turmaId;
    private DisciplinaId disciplinaId;
    private int creditos;
    private List<HorarioAula> horarios;
    private StatusItemMatricula status;

    public ItemMatricula(TurmaId turmaId, DisciplinaId disciplinaId, int creditos,
                          List<HorarioAula> horarios) {
        notNull(turmaId, "O id da turma não pode ser nulo");
        notNull(disciplinaId, "O id da disciplina não pode ser nulo");
        isTrue(creditos > 0, "O número de créditos deve ser positivo");
        notNull(horarios, "Os horários não podem ser nulos");
        this.turmaId = turmaId;
        this.disciplinaId = disciplinaId;
        this.creditos = creditos;
        this.horarios = new ArrayList<>(horarios);
        this.status = StatusItemMatricula.SELECIONADO;
    }

    private ItemMatricula() {}

    public static ItemMatricula reconstituir(TurmaId turmaId, DisciplinaId disciplinaId,
                                              int creditos, List<HorarioAula> horarios,
                                              StatusItemMatricula status) {
        ItemMatricula item = new ItemMatricula();
        item.turmaId = turmaId;
        item.disciplinaId = disciplinaId;
        item.creditos = creditos;
        item.horarios = new ArrayList<>(horarios);
        item.status = status;
        return item;
    }

    public void confirmar() {
        isTrue(status == StatusItemMatricula.SELECIONADO, "Apenas itens selecionados podem ser confirmados");
        this.status = StatusItemMatricula.CONFIRMADO;
    }

    public void cancelar() {
        isTrue(status == StatusItemMatricula.SELECIONADO || status == StatusItemMatricula.CONFIRMADO,
                "Apenas itens selecionados ou confirmados podem ser cancelados");
        this.status = StatusItemMatricula.CANCELADO;
    }

    public void trancar() {
        isTrue(status == StatusItemMatricula.CONFIRMADO, "Apenas itens confirmados podem ser trancados");
        this.status = StatusItemMatricula.TRANCADO;
    }

    public boolean conflitaHorario(ItemMatricula outro) {
        for (HorarioAula h1 : this.horarios) {
            for (HorarioAula h2 : outro.horarios) {
                if (h1.conflitaCom(h2)) return true;
            }
        }
        return false;
    }

    public TurmaId getTurmaId() { return turmaId; }
    public DisciplinaId getDisciplinaId() { return disciplinaId; }
    public int getCreditos() { return creditos; }
    public List<HorarioAula> getHorarios() { return Collections.unmodifiableList(horarios); }
    public StatusItemMatricula getStatus() { return status; }
}
