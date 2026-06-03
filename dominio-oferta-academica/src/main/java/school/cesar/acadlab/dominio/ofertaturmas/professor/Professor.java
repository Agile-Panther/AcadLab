package school.cesar.acadlab.dominio.ofertaturmas.professor;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

public class Professor {
    private final ProfessorId id;
    private String nome;
    private boolean ativo;

    public Professor(ProfessorId id, String nome) {
        notNull(id, "O id não pode ser nulo");
        notBlank(nome, "O nome do professor não pode estar em branco");
        this.id = id;
        this.nome = nome;
        this.ativo = true;
    }

    // US02 - RN3: professor inativo não pode ser vinculado a turma (verificado externamente via isAtivo())
    public ProfessorInativadoEvento inativar() {
        this.ativo = false;
        return new ProfessorInativadoEvento(this);
    }

    public ProfessorAtivadoEvento ativar() {
        this.ativo = true;
        return new ProfessorAtivadoEvento(this);
    }

    public ProfessorId getId() { return id; }
    public String getNome() { return nome; }
    public boolean isAtivo() { return ativo; }

    public static abstract class ProfessorEvento {
        private final Professor professor;
        protected ProfessorEvento(Professor professor) { this.professor = professor; }
        public Professor getProfessor() { return professor; }
    }

    public static class ProfessorInativadoEvento extends ProfessorEvento {
        private ProfessorInativadoEvento(Professor professor) { super(professor); }
    }

    public static class ProfessorAtivadoEvento extends ProfessorEvento {
        private ProfessorAtivadoEvento(Professor professor) { super(professor); }
    }
}
