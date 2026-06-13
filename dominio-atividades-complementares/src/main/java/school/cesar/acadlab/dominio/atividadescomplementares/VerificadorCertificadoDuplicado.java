package school.cesar.acadlab.dominio.atividadescomplementares;
public interface VerificadorCertificadoDuplicado {
    boolean jaUtilizado(EstudanteId estudanteId, String identificadorCertificado);
}
