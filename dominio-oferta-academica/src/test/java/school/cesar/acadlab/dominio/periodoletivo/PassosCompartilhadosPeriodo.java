package school.cesar.acadlab.dominio.periodoletivo;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.pt.Então;

public class PassosCompartilhadosPeriodo {
    private final PeriodoLetivoFuncionalidade ctx;

    public PassosCompartilhadosPeriodo(PeriodoLetivoFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Então("o sistema deve rejeitar informando {string}")
    public void o_sistema_deve_rejeitar_informando(String mensagem) {
        assertNotNull(ctx.excecao, "Esperava exceção mas nenhuma foi lançada");
        assertTrue(ctx.excecao.getMessage().contains(mensagem),
                "Mensagem esperada: '" + mensagem + "', obtida: '" + ctx.excecao.getMessage() + "'");
    }
}
