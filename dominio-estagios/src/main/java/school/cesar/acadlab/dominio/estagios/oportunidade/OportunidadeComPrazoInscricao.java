package school.cesar.acadlab.dominio.estagios.oportunidade;

import java.time.LocalDate;
import static org.apache.commons.lang3.Validate.notNull;

public class OportunidadeComPrazoInscricao extends OportunidadeDecorador {
    private final LocalDate prazoInscricao;
    private final LocalDate dataAtual;

    public OportunidadeComPrazoInscricao(OportunidadeBase oportunidade, LocalDate prazoInscricao, LocalDate dataAtual) {
        super(oportunidade);
        this.prazoInscricao = notNull(prazoInscricao, "Prazo de inscrição não pode ser nulo");
        this.dataAtual = notNull(dataAtual, "Data atual não pode ser nula");
    }

    @Override
    public void validarCandidatura(EstudanteId estudanteId) {
        if (dataAtual.isAfter(prazoInscricao)) {
            throw new IllegalStateException("prazo de inscrição encerrado");
        }
        oportunidade.validarCandidatura(estudanteId);
    }
}
