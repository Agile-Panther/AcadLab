package school.cesar.acadlab.dominio.atividadescomplementares;
import java.time.LocalDate;
public interface VerificadorVinculoEstudante {
    boolean estaNoVinculo(EstudanteId estudanteId, LocalDate data);
}
