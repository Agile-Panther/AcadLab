package school.cesar.acadlab.dominio.historicoacademico.historico;

import static org.apache.commons.lang3.Validate.notNull;
import static org.apache.commons.lang3.Validate.notBlank;

public class Aproveitamento {
    private final AproveitamentoId id;
    private final DisciplinaId disciplinaEquivalente;
    private final int cargaHorariaExterna;
    private final int cargaHorariaRequerida;
    private final String instituicaoOrigem;
    private final String disciplinaOrigem;

    public Aproveitamento(AproveitamentoId id, DisciplinaId disciplinaEquivalente,
                           int cargaHorariaExterna, int cargaHorariaRequerida,
                           String instituicaoOrigem, String disciplinaOrigem) {
        notNull(id, "O id não pode ser nulo");
        notNull(disciplinaEquivalente, "A disciplina equivalente não pode ser nula");
        notBlank(instituicaoOrigem, "A instituição de origem não pode ser vazia");
        notBlank(disciplinaOrigem, "A disciplina de origem não pode ser vazia");
        this.id = id;
        this.disciplinaEquivalente = disciplinaEquivalente;
        this.cargaHorariaExterna = cargaHorariaExterna;
        this.cargaHorariaRequerida = cargaHorariaRequerida;
        this.instituicaoOrigem = instituicaoOrigem;
        this.disciplinaOrigem = disciplinaOrigem;
    }

    public AproveitamentoId getId() { return id; }
    public DisciplinaId getDisciplinaEquivalente() { return disciplinaEquivalente; }
    public int getCargaHorariaExterna() { return cargaHorariaExterna; }
    public int getCargaHorariaRequerida() { return cargaHorariaRequerida; }
    public String getInstituicaoOrigem() { return instituicaoOrigem; }
    public String getDisciplinaOrigem() { return disciplinaOrigem; }
}
