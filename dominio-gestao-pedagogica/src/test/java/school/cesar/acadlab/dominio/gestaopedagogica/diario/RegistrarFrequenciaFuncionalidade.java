package school.cesar.acadlab.dominio.gestaopedagogica.diario;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.gestaopedagogica.GestaoPedagogicaFuncionalidade;

public class RegistrarFrequenciaFuncionalidade {

    private final GestaoPedagogicaFuncionalidade ctx;

    private static final LocalDate INICIO = LocalDate.of(2025, 2, 1);
    private static final LocalDate FIM = LocalDate.of(2025, 7, 31);

    private final TurmaId turmaId = new TurmaId(10);
    private final PeriodoLetivoId periodoId = new PeriodoLetivoId(10);
    private final ProfessorId professorResponsavel = new ProfessorId(10);
    private final EstudanteId estudanteId = new EstudanteId(10);

    private DiarioTurma diario;
    private RegistroAulaId aulaId;

    public RegistrarFrequenciaFuncionalidade(GestaoPedagogicaFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("um diário com estudante matriculado ativo e aula registrada")
    public void diario_com_estudante_e_aula() {
        diario = new DiarioTurma(ctx.repositorio.proximoId(), turmaId, periodoId, professorResponsavel, INICIO, FIM, 6.0, 75.0);
        diario.adicionarEstudanteAtivo(estudanteId);
        aulaId = ctx.repositorio.proximoAulaId();
        diario.registrarAula(aulaId, professorResponsavel, LocalDate.of(2025, 3, 15), "Conteúdo");
        ctx.repositorio.salvar(diario);
    }

    @Quando("o professor responsável registra a frequência do estudante ativo")
    public void professor_registra_frequencia() {
        try {
            diario.registrarFrequencia(professorResponsavel, aulaId, estudanteId, true);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("o lançamento de frequência é salvo para o estudante")
    public void frequencia_salva() {
        assertNull(ctx.excecao, "Não deveria ter lançado exceção");
        assertEquals(1, diario.getFrequencias().size());
        assertTrue(diario.getFrequencias().get(0).isPresente());
    }

    @Dado("um diário sem estudantes matriculados e com aula registrada")
    public void diario_sem_estudantes_com_aula() {
        diario = new DiarioTurma(ctx.repositorio.proximoId(), turmaId, periodoId, professorResponsavel, INICIO, FIM, 6.0, 75.0);
        aulaId = ctx.repositorio.proximoAulaId();
        diario.registrarAula(aulaId, professorResponsavel, LocalDate.of(2025, 3, 15), "Conteúdo");
        ctx.repositorio.salvar(diario);
    }

    @Quando("o professor tenta registrar frequência de estudante não matriculado")
    public void professor_tenta_frequencia_nao_matriculado() {
        try {
            diario.registrarFrequencia(professorResponsavel, aulaId, estudanteId, true);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Quando("outro professor tenta registrar a frequência do estudante")
    public void outro_professor_tenta_frequencia() {
        try {
            diario.registrarFrequencia(new ProfessorId(99), aulaId, estudanteId, true);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }
}
