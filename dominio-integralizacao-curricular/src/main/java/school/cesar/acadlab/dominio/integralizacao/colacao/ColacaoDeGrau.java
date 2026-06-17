package school.cesar.acadlab.dominio.integralizacao.colacao;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;
import java.time.LocalDate;
import school.cesar.acadlab.dominio.integralizacao.EstudanteId;
import school.cesar.acadlab.dominio.integralizacao.integralizacao.IntegralizacaoId;

public class ColacaoDeGrau {
    private final ColacaoId id;
    private final EstudanteId estudanteId;
    private final IntegralizacaoId integralizacaoId;
    private final LocalDate dataAptidaoAprovada;
    private LocalDate dataCerimonia;
    private String local;

    public ColacaoDeGrau(ColacaoId id, EstudanteId estudanteId,
                          IntegralizacaoId integralizacaoId, LocalDate dataAptidaoAprovada) {
        notNull(id, "O id não pode ser nulo");
        notNull(estudanteId, "O estudante não pode ser nulo");
        notNull(integralizacaoId, "A integralização não pode ser nula");
        notNull(dataAptidaoAprovada, "A data de aprovação de aptidão não pode ser nula");
        this.id = id;
        this.estudanteId = estudanteId;
        this.integralizacaoId = integralizacaoId;
        this.dataAptidaoAprovada = dataAptidaoAprovada;
    }

    // US04 - RN7: aptidão aprovada verificada antes da criação do objeto
    // RN8: data da colação igual ou posterior à data de aprovação
    public ColacaoRegistradaEvento registrar(LocalDate dataCerimonia, String local) {
        notNull(dataCerimonia, "A data da cerimônia não pode ser nula");
        notBlank(local, "O local da cerimônia não pode estar em branco");
        if (dataCerimonia.isBefore(dataAptidaoAprovada)) {
            throw new IllegalArgumentException(
                    "RN8: A data da cerimônia deve ser igual ou posterior à data de aprovação da aptidão");
        }
        this.dataCerimonia = dataCerimonia;
        this.local = local;
        return new ColacaoRegistradaEvento(this);
    }

    public static ColacaoDeGrau reconstituir(ColacaoId id, EstudanteId estudanteId,
                                              IntegralizacaoId integralizacaoId,
                                              LocalDate dataAptidaoAprovada,
                                              LocalDate dataCerimonia, String local) {
        var c = new ColacaoDeGrau(id, estudanteId, integralizacaoId, dataAptidaoAprovada);
        c.dataCerimonia = dataCerimonia;
        c.local = local;
        return c;
    }

    public ColacaoId getId() { return id; }
    public EstudanteId getEstudanteId() { return estudanteId; }
    public IntegralizacaoId getIntegralizacaoId() { return integralizacaoId; }
    public LocalDate getDataAptidaoAprovada() { return dataAptidaoAprovada; }
    public LocalDate getDataCerimonia() { return dataCerimonia; }
    public String getLocal() { return local; }

    public static class ColacaoRegistradaEvento {
        private final ColacaoDeGrau colacao;
        private ColacaoRegistradaEvento(ColacaoDeGrau colacao) { this.colacao = colacao; }
        public ColacaoDeGrau getColacao() { return colacao; }
    }
}
