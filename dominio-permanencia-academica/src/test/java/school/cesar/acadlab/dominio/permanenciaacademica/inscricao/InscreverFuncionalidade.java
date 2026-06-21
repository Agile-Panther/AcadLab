package school.cesar.acadlab.dominio.permanenciaacademica.inscricao;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.permanenciaacademica.Edital;
import school.cesar.acadlab.dominio.permanenciaacademica.EditalId;
import school.cesar.acadlab.dominio.permanenciaacademica.EstudantePermanenciaId;
import school.cesar.acadlab.dominio.permanenciaacademica.InscricaoId;
import school.cesar.acadlab.dominio.permanenciaacademica.PermanenciaAcademicaFuncionalidade;
import school.cesar.acadlab.dominio.permanenciaacademica.StatusEdital;
import school.cesar.acadlab.dominio.permanenciaacademica.StatusInscricao;

public class InscreverFuncionalidade {
    private final PermanenciaAcademicaFuncionalidade ctx;
    private final EstudantePermanenciaId estudanteId = new EstudantePermanenciaId(1);
    private EditalId editalId;
    private InscricaoId inscricaoId;
    private boolean atendeElegibilidade;

    public InscreverFuncionalidade(PermanenciaAcademicaFuncionalidade ctx) {
        this.ctx = ctx;
    }

    private EditalId criarEditalAberto() {
        var id = ctx.repositorio.proximoEditalId();
        var edital = Edital.reconstituir(id, "Bolsa Permanência", 5,
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(11), LocalDate.now().plusDays(20),
                LocalDate.now().plusDays(180), StatusEdital.INSCRICOES_ABERTAS);
        ctx.repositorio.salvar(edital);
        return id;
    }

    @Dado("existe um edital com inscrições abertas e o prazo atual é válido")
    public void edital_aberto_prazo_valido() {
        editalId = criarEditalAberto();
    }

    @Dado("o estudante atende aos critérios de elegibilidade")
    public void estudante_atende_criterios() {
        atendeElegibilidade = true;
    }

    @Quando("o estudante solicita inscrição no edital")
    public void estudante_solicita_inscricao() {
        try {
            inscricaoId = ctx.inscricaoServico.inscrever(estudanteId, editalId, LocalDate.now(), atendeElegibilidade);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("o sistema registra a inscrição com status pendente")
    public void sistema_registra_inscricao_pendente() {
        assertNotNull(inscricaoId);
        var inscricao = ctx.repositorio.obter(inscricaoId);
        assertEquals(StatusInscricao.PENDENTE, inscricao.getStatus());
    }

    @Dado("existe um edital cujo prazo de inscrição já encerrou")
    public void edital_com_prazo_encerrado() {
        var id = ctx.repositorio.proximoEditalId();
        var edital = Edital.reconstituir(id, "Auxílio Transporte", 5,
                LocalDate.now().minusDays(10), LocalDate.now().minusDays(1),
                LocalDate.now(), LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(180), StatusEdital.INSCRICOES_ABERTAS);
        ctx.repositorio.salvar(edital);
        editalId = id;
        atendeElegibilidade = true;
    }

    @Quando("o estudante tenta se inscrever no edital")
    public void estudante_tenta_inscricao() {
        try {
            ctx.inscricaoServico.inscrever(estudanteId, editalId, LocalDate.now(), atendeElegibilidade);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("o sistema informa que a inscrição está fora do prazo")
    public void sistema_informa_fora_prazo() {
        assertNotNull(ctx.excecao);
        assertInstanceOf(IllegalStateException.class, ctx.excecao);
    }

    @Dado("o estudante não atende aos critérios de elegibilidade")
    public void estudante_nao_atende_criterios() {
        atendeElegibilidade = false;
    }

    @Entao("o sistema informa que o estudante não atende aos critérios de elegibilidade")
    public void sistema_informa_nao_elegivel() {
        assertNotNull(ctx.excecao);
        assertInstanceOf(IllegalStateException.class, ctx.excecao);
    }
}
