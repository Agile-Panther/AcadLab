package school.cesar.acadlab.dominio.gestaopedagogica.diario;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.gestaopedagogica.GestaoPedagogicaFuncionalidade;

public class RegistrarAulasFuncionalidade extends GestaoPedagogicaFuncionalidade {

    private static final LocalDate INICIO = LocalDate.of(2025, 2, 1);
    private static final LocalDate FIM = LocalDate.of(2025, 7, 31);

    private final TurmaId turmaId = new TurmaId(1);
    private final PeriodoLetivoId periodoId = new PeriodoLetivoId(1);
    private final ProfessorId professorResponsavel = new ProfessorId(1);
    private final ProfessorId outroProfesor = new ProfessorId(2);

    private DiarioTurma diario;
    private RegistroAulaId aulaId;
    private RuntimeException excecao;

    @Dado("um diário de turma com professor responsável cadastrado")
    public void diario_com_professor_responsavel() {
        diario = new DiarioTurma(repositorio.proximoId(), turmaId, periodoId, professorResponsavel, INICIO, FIM, 6.0, 75.0);
        repositorio.salvar(diario);
    }

    @Quando("o professor responsável registra uma aula dentro do período")
    public void professor_registra_aula() {
        aulaId = repositorio.proximoAulaId();
        diario.registrarAula(aulaId, professorResponsavel, LocalDate.of(2025, 4, 10), "Conteúdo da aula");
        repositorio.salvar(diario);
    }

    @Entao("o registro de aula é adicionado ao diário")
    public void registro_adicionado() {
        assertNull(excecao, "Não deveria ter lançado exceção");
        assertEquals(1, diario.getAulas().size());
    }

    @Quando("outro professor tenta registrar uma aula")
    public void outro_professor_tenta_registrar() {
        aulaId = repositorio.proximoAulaId();
        try {
            diario.registrarAula(aulaId, outroProfesor, LocalDate.of(2025, 4, 10), "Conteúdo");
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("o sistema rejeita o registro informando RN-1")
    public void sistema_rejeita_rn1() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
        assertTrue(excecao.getMessage().contains("RN-1"));
    }

    @Dado("um diário de turma com um registro de aula existente")
    public void diario_com_aula_existente() {
        diario = new DiarioTurma(repositorio.proximoId(), turmaId, periodoId, professorResponsavel, INICIO, FIM, 6.0, 75.0);
        aulaId = repositorio.proximoAulaId();
        diario.registrarAula(aulaId, professorResponsavel, LocalDate.of(2025, 4, 10), "Conteúdo original");
        repositorio.salvar(diario);
    }

    @Quando("o professor responsável corrige o conteúdo da aula")
    public void professor_corrige_aula() {
        try {
            diario.corrigirAula(aulaId, professorResponsavel, "Conteúdo corrigido");
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("o registro de aula é atualizado com o novo conteúdo")
    public void registro_atualizado() {
        assertNull(excecao, "Não deveria ter lançado exceção");
        assertEquals("Conteúdo corrigido", diario.getAulas().get(0).getConteudo());
        assertTrue(diario.getAulas().get(0).isCorrigido());
    }

    @Dado("um registro de aula com diário explicitamente fechado")
    public void aula_com_diario_fechado() {
        diario = new DiarioTurma(repositorio.proximoId(), turmaId, periodoId, professorResponsavel, INICIO, FIM, 6.0, 75.0);
        aulaId = repositorio.proximoAulaId();
        diario.registrarAula(aulaId, professorResponsavel, LocalDate.of(2025, 4, 10), "Conteúdo original");
        repositorio.salvar(diario);
    }

    @Quando("o professor tenta corrigir o conteúdo com diário fechado")
    public void professor_tenta_corrigir_fechado() {
        try {
            var aula = diario.getAulas().get(0);
            aula.corrigir(professorResponsavel, "Novo conteúdo", false);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("o sistema rejeita a correção informando RN-10")
    public void sistema_rejeita_rn10() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
        assertTrue(excecao.getMessage().contains("RN-10"));
    }
}
