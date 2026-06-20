package school.cesar.acadlab.dominio.gestaopedagogica;

public class GestaoPedagogicaFuncionalidade {
    public Repositorio repositorio;
    public RuntimeException excecao;

    public GestaoPedagogicaFuncionalidade() {
        repositorio = new Repositorio();
    }
}
