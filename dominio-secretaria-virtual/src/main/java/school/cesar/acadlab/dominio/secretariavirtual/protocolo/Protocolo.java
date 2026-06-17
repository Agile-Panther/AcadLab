package school.cesar.acadlab.dominio.secretariavirtual.protocolo;

import static org.apache.commons.lang3.Validate.notNull;
import java.time.LocalDate;
import java.util.Objects;

public class Protocolo {
    private final ProtocoloId id;
    private final LocalDate dataGeracao;

    public Protocolo(ProtocoloId id) {
        notNull(id, "O id do protocolo não pode ser nulo");
        this.id = id;
        this.dataGeracao = LocalDate.now();
    }

    public Protocolo(ProtocoloId id, LocalDate dataGeracao) {
        notNull(id, "O id do protocolo não pode ser nulo");
        notNull(dataGeracao, "A data de geração não pode ser nula");
        this.id = id;
        this.dataGeracao = dataGeracao;
    }

    public ProtocoloId getId() { return id; }
    public LocalDate getDataGeracao() { return dataGeracao; }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Protocolo) {
            var protocolo = (Protocolo) obj;
            return Objects.equals(id, protocolo.id);
        }
        return false;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
