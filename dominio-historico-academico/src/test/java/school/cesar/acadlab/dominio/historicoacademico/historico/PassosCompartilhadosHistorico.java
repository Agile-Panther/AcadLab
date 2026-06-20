package school.cesar.acadlab.dominio.historicoacademico.historico;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.pt.Entao;
import school.cesar.acadlab.dominio.historicoacademico.HistoricoFuncionalidade;

public class PassosCompartilhadosHistorico {
    private final HistoricoFuncionalidade ctx;

    public PassosCompartilhadosHistorico(HistoricoFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Entao("o sistema deve rejeitar informando {string}")
    public void o_sistema_deve_rejeitar_informando(String mensagem) {
        assertNotNull(ctx.excecao, "Esperava exceção mas nenhuma foi lançada");
        assertTrue(ctx.excecao.getMessage().toLowerCase().contains(mensagem.toLowerCase()),
            "Mensagem esperada: \"" + mensagem + "\", mas obtida: \"" + ctx.excecao.getMessage() + "\"");
    }
}
