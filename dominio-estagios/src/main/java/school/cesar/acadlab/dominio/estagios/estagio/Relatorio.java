package school.cesar.acadlab.dominio.estagios.estagio;

public class Relatorio {
    private final int numero;
    private final String descricao;
    private StatusRelatorio status;

    public Relatorio(int numero, String descricao) {
        this.numero = numero;
        this.descricao = descricao;
        this.status = StatusRelatorio.PENDENTE;
    }

    void avaliar(StatusRelatorio resultado) {
        this.status = resultado;
    }

    public static Relatorio reconstituir(int numero, String descricao, StatusRelatorio status) {
        var r = new Relatorio(numero, descricao);
        r.status = status;
        return r;
    }

    public int getNumero() { return numero; }
    public String getDescricao() { return descricao; }
    public StatusRelatorio getStatus() { return status; }
}
