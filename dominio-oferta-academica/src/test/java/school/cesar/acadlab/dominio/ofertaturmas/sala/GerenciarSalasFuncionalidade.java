package school.cesar.acadlab.dominio.ofertaturmas.sala;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.ofertaturmas.OfertaTurmasFuncionalidade;

public class GerenciarSalasFuncionalidade {

    private final OfertaTurmasFuncionalidade ctx;
    private SalaId salaId;

    public GerenciarSalasFuncionalidade(OfertaTurmasFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("uma sala ativa cadastrada com capacidade para trinta pessoas")
    public void sala_ativa_cadastrada() {
        salaId = ctx.salaRepositorio.proximoId();
        ctx.salaRepositorio.salvar(new Sala(salaId, "Sala 101", 30));
    }

    @Quando("a secretaria inativa a sala")
    public void secretaria_inativa_sala() {
        var sala = ctx.salaRepositorio.obter(salaId);
        sala.inativar();
        ctx.salaRepositorio.salvar(sala);
    }

    @Entao("a sala passa a ter status inativo")
    public void sala_inativa() {
        var sala = ctx.salaRepositorio.obter(salaId);
        assertFalse(sala.isAtiva());
    }

    @Quando("a secretaria tenta reduzir a capacidade da sala para vinte pessoas havendo turma com trinta vagas")
    public void reduzir_capacidade_abaixo_turma() {
        try {
            var sala = ctx.salaRepositorio.obter(salaId);
            sala.alterarCapacidade(20, 30);
            ctx.salaRepositorio.salvar(sala);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }
}
