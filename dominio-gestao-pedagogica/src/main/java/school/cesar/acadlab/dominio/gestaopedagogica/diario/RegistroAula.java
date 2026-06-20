package school.cesar.acadlab.dominio.gestaopedagogica.diario;

import static org.apache.commons.lang3.Validate.notNull;
import java.time.LocalDate;

public class RegistroAula {
    private final RegistroAulaId id;
    private final ProfessorId professorId;
    private final LocalDate data;
    private String conteudo;
    private boolean corrigido;

    public RegistroAula(RegistroAulaId id, ProfessorId professorId, LocalDate data, String conteudo) {
        notNull(id, "O id da aula não pode ser nulo");
        notNull(professorId, "O professor não pode ser nulo");
        notNull(data, "A data da aula não pode ser nula");
        notNull(conteudo, "O conteúdo não pode ser nulo");
        this.id = id;
        this.professorId = professorId;
        this.data = data;
        this.conteudo = conteudo;
        this.corrigido = false;
    }

    public RegistroAulaId getId() { return id; }
    public ProfessorId getProfessorId() { return professorId; }
    public LocalDate getData() { return data; }
    public String getConteudo() { return conteudo; }
    public boolean isCorrigido() { return corrigido; }

    public static RegistroAula reconstituir(RegistroAulaId id, ProfessorId professorId,
                                             LocalDate data, String conteudo, boolean corrigido) {
        var aula = new RegistroAula(id, professorId, data, conteudo);
        aula.corrigido = corrigido;
        return aula;
    }

    // RN-11: apenas o professor responsável pela aula pode corrigi-la.
    // RN-10: correção só é permitida com o diário aberto.
    public void corrigir(ProfessorId professorId, String novoConteudo, boolean diarioAberto) {
        notNull(professorId, "O professor não pode ser nulo");
        notNull(novoConteudo, "O novo conteúdo não pode ser nulo");
        if (!this.professorId.equals(professorId)) {
            throw new IllegalStateException("RN-11: Apenas o professor responsável pela aula pode corrigi-la");
        }
        if (!diarioAberto) {
            throw new IllegalStateException("diário de turma está fechado");
        }
        this.conteudo = novoConteudo;
        this.corrigido = true;
    }
}
