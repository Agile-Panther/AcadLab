package school.cesar.acadlab.dominio.gestaopedagogica.diario;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.pt.Entao;
import school.cesar.acadlab.dominio.gestaopedagogica.GestaoPedagogicaFuncionalidade;

public class PassosCompartilhadosGestaoPedagogica {
    private final GestaoPedagogicaFuncionalidade ctx;

    public PassosCompartilhadosGestaoPedagogica(GestaoPedagogicaFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Entao("o sistema deve rejeitar informando {string}")
    public void o_sistema_deve_rejeitar_informando(String mensagem) {
        assertNotNull(ctx.excecao, "Esperava exceção mas nenhuma foi lançada");
        assertTrue(ctx.excecao.getMessage().toLowerCase().contains(mensagem.toLowerCase()),
            "Mensagem esperada: \"" + mensagem + "\", mas obtida: \"" + ctx.excecao.getMessage() + "\"");
    }
}
