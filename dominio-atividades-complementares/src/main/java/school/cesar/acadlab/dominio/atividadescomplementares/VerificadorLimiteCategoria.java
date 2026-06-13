package school.cesar.acadlab.dominio.atividadescomplementares;
public interface VerificadorLimiteCategoria {
    boolean excedeLimite(EstudanteId estudanteId, CategoriaAtividadeId categoriaId, int horasAdicionais);
}
