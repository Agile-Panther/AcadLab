package school.cesar.acadlab.dominio.estagios.oportunidade;

import static org.apache.commons.lang3.Validate.notNull;

public class OportunidadeComCriterioElegibilidade extends OportunidadeDecorador {
    private final CriterioElegibilidade criterio;
    private final VerificadorElegibilidade verificador;

    public OportunidadeComCriterioElegibilidade(OportunidadeBase oportunidade,
                                                 CriterioElegibilidade criterio,
                                                 VerificadorElegibilidade verificador) {
        super(oportunidade);
        this.criterio = notNull(criterio, "Critério de elegibilidade não pode ser nulo");
        this.verificador = notNull(verificador, "Verificador de elegibilidade não pode ser nulo");
    }

    @Override
    public void validarCandidatura(EstudanteId estudanteId) {
        if (!verificador.estudanteAtendeCriterios(estudanteId, criterio)) {
            throw new IllegalStateException("estudante não atende aos critérios de elegibilidade da oportunidade");
        }
        oportunidade.validarCandidatura(estudanteId);
    }
}
