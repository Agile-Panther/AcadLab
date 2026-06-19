package school.cesar.acadlab.dominio.estagios.oportunidade;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.isTrue;

public class CriterioElegibilidade {
    private final String cursoExigido;
    private final int periodoMinimoExigido;
    private final boolean regularidadeExigida;

    public CriterioElegibilidade(String cursoExigido, int periodoMinimoExigido, boolean regularidadeExigida) {
        notBlank(cursoExigido, "Curso exigido não pode ser vazio");
        isTrue(periodoMinimoExigido > 0, "Período mínimo exigido deve ser positivo");
        this.cursoExigido = cursoExigido;
        this.periodoMinimoExigido = periodoMinimoExigido;
        this.regularidadeExigida = regularidadeExigida;
    }

    public String getCursoExigido() { return cursoExigido; }
    public int getPeriodoMinimoExigido() { return periodoMinimoExigido; }
    public boolean isRegularidadeExigida() { return regularidadeExigida; }
}
