package school.cesar.acadlab.dominio.gestaofinanceira.cobranca;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.pt.Entao;
import school.cesar.acadlab.dominio.gestaofinanceira.GestaoFinanceiraFuncionalidade;

public class PassosCompartilhadosFinanceira {
    private final GestaoFinanceiraFuncionalidade ctx;

    public PassosCompartilhadosFinanceira(GestaoFinanceiraFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Entao("o sistema deve rejeitar informando {string}")
    public void o_sistema_deve_rejeitar_informando(String mensagem) {
        assertNotNull(ctx.excecao, "Esperava exceção mas nenhuma foi lançada");
        assertTrue(ctx.excecao.getMessage().toLowerCase().contains(mensagem.toLowerCase()),
            "Mensagem esperada: \"" + mensagem + "\", mas obtida: \"" + ctx.excecao.getMessage() + "\"");
    }
}
