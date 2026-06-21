package school.cesar.acadlab.dominio.apoiopsicopedagogico.acaopermanencia;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.ApoioPsicopedagogicoFuncionalidade;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.profissional.CoordenadorId;

public class RegistrarAcaoPermanenciaFuncionalidade {
    private final ApoioPsicopedagogicoFuncionalidade ctx;
    private final CoordenadorId coordenadorId = new CoordenadorId(1);

    public RegistrarAcaoPermanenciaFuncionalidade(ApoioPsicopedagogicoFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("um coordenador autorizado")
    public void um_coordenador_autorizado() {
        // coordenadorId já definido
    }

    @Dado("indicadores agregados de atendimento disponíveis")
    public void indicadores_disponiveis() {
        // nenhum setup necessário
    }

    @Quando("o coordenador registra uma ação de permanência com indicadores agregados")
    public void o_coordenador_registra_acao() {
        ctx.acaoPermanenciaServico.registrar(
                coordenadorId,
                "Oficina de técnicas de estudo",
                "30% dos atendimentos relataram dificuldades com organização acadêmica");
    }

    @Quando("alguém tenta registrar uma ação de permanência sem identificação de coordenador")
    public void registrar_acao_sem_coordenador() {
        try {
            ctx.acaoPermanenciaServico.registrar(null, "Ação", "Indicadores");
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("o sistema registra a ação de permanência com sucesso")
    public void o_sistema_registra_acao() {
        var acao = ctx.repositorio.obter(new AcaoPermanenciaId(1));
        assertNotNull(acao);
        assertEquals(coordenadorId, acao.getCoordenadorId());
    }

    @Entao("o sistema informa que o coordenador é obrigatório para registrar ações de permanência")
    public void o_sistema_informa_coordenador_obrigatorio() {
        assertNotNull(ctx.excecao);
    }
}
