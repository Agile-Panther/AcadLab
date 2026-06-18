package school.cesar.acadlab.dominio.permanenciaacademica.beneficio;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.permanenciaacademica.Beneficio;
import school.cesar.acadlab.dominio.permanenciaacademica.BeneficioId;
import school.cesar.acadlab.dominio.permanenciaacademica.EditalId;
import school.cesar.acadlab.dominio.permanenciaacademica.EstudantePermanenciaId;
import school.cesar.acadlab.dominio.permanenciaacademica.InscricaoId;
import school.cesar.acadlab.dominio.permanenciaacademica.PermanenciaAcademicaFuncionalidade;
import school.cesar.acadlab.dominio.permanenciaacademica.StatusBeneficio;

public class BeneficioFuncionalidade extends PermanenciaAcademicaFuncionalidade {
    private final EstudantePermanenciaId estudanteId = new EstudantePermanenciaId(1);
    private BeneficioId beneficioId;
    private RuntimeException excecao;

    private BeneficioId criarBeneficioAtivo(LocalDate prazoRenovacao) {
        var id = repositorio.proximoBeneficioId();
        var beneficio = Beneficio.reconstituir(id,
                new InscricaoId(1), estudanteId, new EditalId(1),
                StatusBeneficio.ATIVO, LocalDate.now().minusDays(30),
                prazoRenovacao, false);
        repositorio.salvar(beneficio);
        return id;
    }

    @Dado("um estudante possui um benefício ativo com prazo de renovação futuro")
    public void beneficio_ativo_prazo_futuro() {
        beneficioId = criarBeneficioAtivo(LocalDate.now().plusDays(30));
    }

    @Quando("o estudante solicita a renovação do benefício")
    public void solicita_renovacao() {
        beneficioServico.solicitarRenovacao(beneficioId, LocalDate.now());
    }

    @Entao("o sistema registra a solicitação de renovação")
    public void sistema_registra_renovacao() {
        var beneficio = repositorio.obter(beneficioId);
        assertTrue(beneficio.isSolicitouRenovacao());
    }

    @Dado("um estudante possui um benefício ativo com prazo de renovação já vencido")
    public void beneficio_ativo_prazo_vencido() {
        beneficioId = criarBeneficioAtivo(LocalDate.now().minusDays(1));
    }

    @Quando("o estudante tenta solicitar a renovação do benefício")
    public void tenta_solicitar_renovacao() {
        try {
            beneficioServico.solicitarRenovacao(beneficioId, LocalDate.now());
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("o sistema informa que o prazo de renovação já encerrou")
    public void sistema_informa_prazo_vencido() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
    }

    @Dado("um estudante possui um benefício ativo")
    public void beneficio_ativo() {
        beneficioId = criarBeneficioAtivo(LocalDate.now().plusDays(30));
    }

    @Quando("o sistema suspende o benefício por não cumprimento dos critérios")
    public void suspende_beneficio() {
        beneficioServico.suspender(beneficioId);
    }

    @Entao("o status do benefício é atualizado para suspenso")
    public void status_suspenso() {
        var beneficio = repositorio.obter(beneficioId);
        assertEquals(StatusBeneficio.SUSPENSO, beneficio.getStatus());
    }

    @Entao("um evento de suspensão é publicado no barramento")
    public void evento_suspensao_publicado() {
        assertTrue(eventoBarramento.foiPostado(Beneficio.BeneficioSuspensosEvento.class));
    }

    @Quando("o sistema cancela o benefício por não cumprimento dos critérios")
    public void cancela_beneficio() {
        beneficioServico.cancelar(beneficioId);
    }

    @Entao("o status do benefício é atualizado para cancelado")
    public void status_cancelado() {
        var beneficio = repositorio.obter(beneficioId);
        assertEquals(StatusBeneficio.CANCELADO, beneficio.getStatus());
    }

    @Entao("um evento de cancelamento é publicado no barramento")
    public void evento_cancelamento_publicado() {
        assertTrue(eventoBarramento.foiPostado(Beneficio.BeneficioCanceladoEvento.class));
    }
}
