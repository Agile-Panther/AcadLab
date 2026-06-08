package school.cesar.acadlab.dominio.ofertaturmas.sala;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.ofertaturmas.OfertaTurmasFuncionalidade;

public class GerenciarSalasFuncionalidade extends OfertaTurmasFuncionalidade {

    private SalaId salaId;
    private RuntimeException excecao;

    @Dado("uma sala ativa cadastrada com capacidade para trinta pessoas")
    public void sala_ativa_cadastrada() {
        salaId = repositorio.proximoId();
        repositorio.salvar(new Sala(salaId, "Sala 101", 30));
    }

    @Quando("a secretaria inativa a sala")
    public void secretaria_inativa_sala() {
        try {
            var sala = repositorio.obter(salaId);
            sala.inativar();
            repositorio.salvar(sala);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("a sala passa a ter status inativo")
    public void sala_inativa() {
        assertNull(excecao, "Não deveria ter lançado exceção");
        var sala = repositorio.obter(salaId);
        assertFalse(sala.isAtiva());
    }

    @Quando("a secretaria tenta reduzir a capacidade da sala para vinte pessoas havendo turma com trinta vagas")
    public void reduzir_capacidade_abaixo_turma() {
        try {
            var sala = repositorio.obter(salaId);
            sala.alterarCapacidade(20, 30);
            repositorio.salvar(sala);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("o sistema rejeita a redução de capacidade da sala")
    public void rejeitar_reducao_capacidade() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
    }
}
