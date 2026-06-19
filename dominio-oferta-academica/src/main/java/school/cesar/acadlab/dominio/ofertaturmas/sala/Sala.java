package school.cesar.acadlab.dominio.ofertaturmas.sala;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

public class Sala {
    private final SalaId id;
    private String nome;
    private int capacidade;
    private boolean ativa;

    public Sala(SalaId id, String nome, int capacidade) {
        notNull(id, "O id não pode ser nulo");
        notBlank(nome, "O nome da sala não pode estar em branco");
        if (capacidade <= 0) throw new IllegalArgumentException("A capacidade deve ser positiva");
        this.id = id;
        this.nome = nome;
        this.capacidade = capacidade;
        this.ativa = true;
    }

    // US01 - RN1: sala inativa não pode ser vinculada a turma (verificado externamente via isAtiva())
    public SalaInativadaEvento inativar() {
        this.ativa = false;
        return new SalaInativadaEvento(this);
    }

    public SalaAtivadaEvento ativar() {
        this.ativa = true;
        return new SalaAtivadaEvento(this);
    }

    // US01 - RN2: capacidade não pode ser reduzida abaixo da maior turma vinculada
    public CapacidadeAlteradaEvento alterarCapacidade(int novaCapacidade, int maiorCapacidadeTurmaVinculada) {
        if (novaCapacidade <= 0) throw new IllegalArgumentException("A capacidade deve ser positiva");
        if (novaCapacidade < maiorCapacidadeTurmaVinculada) {
            throw new IllegalStateException(
                    "capacidade não pode ser reduzida abaixo do número de vagas da turma vinculada");
        }
        this.capacidade = novaCapacidade;
        return new CapacidadeAlteradaEvento(this);
    }

    public SalaId getId() { return id; }
    public String getNome() { return nome; }
    public int getCapacidade() { return capacidade; }
    public boolean isAtiva() { return ativa; }

    public static abstract class SalaEvento {
        private final Sala sala;
        protected SalaEvento(Sala sala) { this.sala = sala; }
        public Sala getSala() { return sala; }
    }

    public static class SalaInativadaEvento extends SalaEvento {
        private SalaInativadaEvento(Sala sala) { super(sala); }
    }

    public static class SalaAtivadaEvento extends SalaEvento {
        private SalaAtivadaEvento(Sala sala) { super(sala); }
    }

    public static class CapacidadeAlteradaEvento extends SalaEvento {
        private CapacidadeAlteradaEvento(Sala sala) { super(sala); }
    }
}
