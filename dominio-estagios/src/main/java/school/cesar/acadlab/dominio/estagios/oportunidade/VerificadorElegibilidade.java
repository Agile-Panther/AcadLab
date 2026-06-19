package school.cesar.acadlab.dominio.estagios.oportunidade;

public interface VerificadorElegibilidade {
    boolean estudanteAtendeCriterios(EstudanteId estudanteId, CriterioElegibilidade criterio);
}
