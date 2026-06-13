package school.cesar.acadlab.dominio.gestaopedagogica.diario;

import static org.apache.commons.lang3.Validate.notNull;
import java.time.LocalDate;

public class Avaliacao {
    private final AvaliacaoId id;
    private final String nome;
    private final double peso;
    private final LocalDate prazo;

    public Avaliacao(AvaliacaoId id, String nome, double peso, LocalDate prazo) {
        notNull(id, "O id da avaliação não pode ser nulo");
        notNull(nome, "O nome da avaliação não pode ser nulo");
        notNull(prazo, "O prazo da avaliação não pode ser nulo");
        this.id = id;
        this.nome = nome;
        this.peso = peso;
        this.prazo = prazo;
    }

    public AvaliacaoId getId() { return id; }
    public String getNome() { return nome; }
    public double getPeso() { return peso; }
    public LocalDate getPrazo() { return prazo; }
}
