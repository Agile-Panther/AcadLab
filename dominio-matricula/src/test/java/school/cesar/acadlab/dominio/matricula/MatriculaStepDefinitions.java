package school.cesar.acadlab.dominio.matricula;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.matricula.matricula.CoordenadorId;
import school.cesar.acadlab.dominio.matricula.matricula.DisciplinaId;
import school.cesar.acadlab.dominio.matricula.matricula.EstudanteId;
import school.cesar.acadlab.dominio.matricula.matricula.HorarioAula;
import school.cesar.acadlab.dominio.matricula.matricula.ItemMatricula;
import school.cesar.acadlab.dominio.matricula.matricula.Matricula;
import school.cesar.acadlab.dominio.matricula.matricula.MatriculaId;
import school.cesar.acadlab.dominio.matricula.matricula.PeriodoLetivoId;
import school.cesar.acadlab.dominio.matricula.matricula.StatusItemMatricula;
import school.cesar.acadlab.dominio.matricula.matricula.StatusMatricula;
import school.cesar.acadlab.dominio.matricula.matricula.TurmaId;

public class MatriculaStepDefinitions {

    private final MatriculaFuncionalidade ctx;

    public MatriculaStepDefinitions(MatriculaFuncionalidade ctx) {
        this.ctx = ctx;
    }

    private static final LocalDate INICIO_JANELA = LocalDate.of(2025, 1, 10);
    private static final LocalDate FIM_JANELA = LocalDate.of(2025, 1, 20);
    private static final LocalDate DENTRO_JANELA = LocalDate.of(2025, 1, 15);
    private static final LocalDate FORA_JANELA = LocalDate.of(2025, 2, 1);

    private static final LocalDate INICIO_AJUSTE = LocalDate.of(2025, 2, 1);
    private static final LocalDate FIM_AJUSTE = LocalDate.of(2025, 2, 5);
    private static final LocalDate DENTRO_AJUSTE = LocalDate.of(2025, 2, 3);
    private static final LocalDate FORA_AJUSTE = LocalDate.of(2025, 3, 1);

    private static final LocalDate INICIO_TRANCAMENTO = LocalDate.of(2025, 3, 1);
    private static final LocalDate FIM_TRANCAMENTO = LocalDate.of(2025, 3, 15);
    private static final LocalDate DENTRO_TRANCAMENTO = LocalDate.of(2025, 3, 10);
    private static final LocalDate FORA_TRANCAMENTO = LocalDate.of(2025, 4, 1);

    // ===== Contextos =====

    @Dado("que existe uma matrícula em montagem para o estudante {int} no período letivo {int}")
    public void existeMatriculaEmMontagem(int estudanteId, int periodoLetivoId) {
        ctx.matricula = new Matricula(new MatriculaId(1), new EstudanteId(estudanteId),
                new PeriodoLetivoId(periodoLetivoId), 24);
    }

    @Dado("que existe uma matrícula confirmada para o estudante {int} no período letivo {int}")
    public void existeMatriculaConfirmada(int estudanteId, int periodoLetivoId) {
        ctx.matricula = new Matricula(new MatriculaId(1), new EstudanteId(estudanteId),
                new PeriodoLetivoId(periodoLetivoId), 24);
        ctx.matricula.adicionarItem(new TurmaId(10), new DisciplinaId(1), 4, List.of(),
                true, true, false, DENTRO_JANELA, INICIO_JANELA, FIM_JANELA);
        ctx.matricula.confirmar(Map.of(new TurmaId(10), 30));
    }

    // ===== US01 — Montar plano =====

    @Quando("o estudante adiciona a turma {int} com {int} créditos dentro da janela de matrícula")
    public void adicionaItemDentroJanela(int turmaId, int creditos) {
        ctx.matricula.adicionarItem(new TurmaId(turmaId), new DisciplinaId(turmaId), creditos,
                List.of(), true, true, false, DENTRO_JANELA, INICIO_JANELA, FIM_JANELA);
    }

    @Quando("o estudante tenta adicionar a turma {int} fora da janela de matrícula")
    public void tentaAdicionarForaJanela(int turmaId) {
        try {
            ctx.matricula.adicionarItem(new TurmaId(turmaId), new DisciplinaId(turmaId), 4,
                    List.of(), true, true, false, FORA_JANELA, INICIO_JANELA, FIM_JANELA);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Quando("o estudante tenta adicionar a turma {int} sem cumprir os pré-requisitos")
    public void tentaAdicionarSemPreRequisitos(int turmaId) {
        try {
            ctx.matricula.adicionarItem(new TurmaId(turmaId), new DisciplinaId(turmaId), 4,
                    List.of(), false, true, false, DENTRO_JANELA, INICIO_JANELA, FIM_JANELA);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Quando("o estudante tenta adicionar a turma {int} sem correquisitos no plano")
    public void tentaAdicionarSemCorrequisitos(int turmaId) {
        try {
            ctx.matricula.adicionarItem(new TurmaId(turmaId), new DisciplinaId(turmaId), 4,
                    List.of(), true, false, false, DENTRO_JANELA, INICIO_JANELA, FIM_JANELA);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Quando("o estudante tenta adicionar uma disciplina que excede o limite de créditos")
    public void tentaAdicionarExcedendoCreditos() {
        try {
            ctx.matricula.adicionarItem(new TurmaId(99), new DisciplinaId(9), 25,
                    List.of(), true, true, false, DENTRO_JANELA, INICIO_JANELA, FIM_JANELA);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Quando("o estudante tenta adicionar a turma {int} com pendências acadêmicas impeditivas")
    public void tentaAdicionarComPendencias(int turmaId) {
        try {
            ctx.matricula.adicionarItem(new TurmaId(turmaId), new DisciplinaId(turmaId), 4,
                    List.of(), true, true, true, DENTRO_JANELA, INICIO_JANELA, FIM_JANELA);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    // ===== US02 — Confirmar matrícula =====

    @Dado("o estudante adicionou a turma {int} com {int} créditos no horário segunda das {string} às {string}")
    public void adicionouItemComHorario(int turmaId, int creditos, String inicio, String fim) {
        HorarioAula horario = new HorarioAula(DayOfWeek.MONDAY,
                LocalTime.parse(inicio), LocalTime.parse(fim));
        ctx.matricula.adicionarItem(new TurmaId(turmaId), new DisciplinaId(turmaId), creditos,
                List.of(horario), true, true, false, DENTRO_JANELA, INICIO_JANELA, FIM_JANELA);
    }

    @Quando("o estudante confirma a matrícula com {int} vagas disponíveis na turma {int}")
    public void confirmaMatricula(int vagas, int turmaId) {
        ctx.matricula.confirmar(Map.of(new TurmaId(turmaId), vagas));
    }

    @Quando("o estudante tenta confirmar a matrícula com {int} vagas disponíveis na turma {int}")
    public void tentaConfirmarSemVagas(int vagas, int turmaId) {
        try {
            ctx.matricula.confirmar(Map.of(new TurmaId(turmaId), vagas));
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Quando("o estudante tenta confirmar a matrícula com vagas nas duas turmas")
    public void tentaConfirmarComConflito() {
        try {
            ctx.matricula.confirmar(Map.of(new TurmaId(10), 30, new TurmaId(20), 30));
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    // ===== US03 — Ajuste de matrícula =====

    @Quando("o estudante cancela a turma {int} dentro da janela de ajuste")
    public void cancelaItemDentroAjuste(int turmaId) {
        ctx.matricula.cancelarItem(new TurmaId(turmaId), DENTRO_AJUSTE, INICIO_AJUSTE, FIM_AJUSTE);
    }

    @Quando("o estudante tenta cancelar a turma {int} fora da janela de ajuste")
    public void tentaCancelarForaAjuste(int turmaId) {
        try {
            ctx.matricula.cancelarItem(new TurmaId(turmaId), FORA_AJUSTE, INICIO_AJUSTE, FIM_AJUSTE);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    // ===== US04 — Trancar disciplina =====

    @Quando("o estudante tranca a turma {int} dentro da janela de trancamento")
    public void trancaDisciplinaDentroJanela(int turmaId) {
        ctx.matricula.trancarDisciplina(new TurmaId(turmaId), DENTRO_TRANCAMENTO,
                INICIO_TRANCAMENTO, FIM_TRANCAMENTO);
    }

    @Quando("o estudante tenta trancar a turma {int} fora da janela de trancamento")
    public void tentaTrancarForaJanela(int turmaId) {
        try {
            ctx.matricula.trancarDisciplina(new TurmaId(turmaId), FORA_TRANCAMENTO,
                    INICIO_TRANCAMENTO, FIM_TRANCAMENTO);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    // ===== US05 — Solicitar exceção =====

    @Dado("que o estudante solicitou exceção para a disciplina {int} com motivo {string}")
    public void solicitouExcecao(int disciplinaId, String motivo) {
        ctx.matricula.solicitarExcecao(new DisciplinaId(disciplinaId), motivo);
    }

    @Dado("a coordenação deferiu a exceção para a disciplina {int}")
    public void coordenacaoDeferiu(int disciplinaId) {
        ctx.matricula.deferir(new DisciplinaId(disciplinaId), new CoordenadorId(1));
    }

    @Quando("o estudante adiciona a turma {int} sem cumprir pré-requisitos mas com exceção deferida")
    public void adicionaComExcecaoDeferida(int turmaId) {
        ctx.matricula.adicionarItem(new TurmaId(turmaId), new DisciplinaId(5), 4,
                List.of(), false, false, true, DENTRO_JANELA, INICIO_JANELA, FIM_JANELA);
    }

    @Quando("o estudante tenta adicionar a turma {int} sem pré-requisitos e sem exceção deferida")
    public void tentaAdicionarSemPreRequisitosEExcecao(int turmaId) {
        try {
            ctx.matricula.adicionarItem(new TurmaId(turmaId), new DisciplinaId(5), 4,
                    List.of(), false, false, true, DENTRO_JANELA, INICIO_JANELA, FIM_JANELA);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    // ===== US07 — Trancar período =====

    @Quando("o estudante tranca o período dentro da janela com {int} de {int} trancamentos utilizados")
    public void trancaPeriodoDentroJanela(int realizados, int limite) {
        ctx.matricula.trancarPeriodo(DENTRO_TRANCAMENTO, INICIO_TRANCAMENTO, FIM_TRANCAMENTO,
                realizados, limite);
    }

    @Quando("o estudante tenta trancar o período fora da janela de trancamento")
    public void tentaTrancarPeriodoForaJanela() {
        try {
            ctx.matricula.trancarPeriodo(FORA_TRANCAMENTO, INICIO_TRANCAMENTO, FIM_TRANCAMENTO,
                    0, 3);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Quando("o estudante tenta trancar o período com o limite de {int} trancamentos atingido")
    public void tentaTrancarPeriodoComLimiteAtingido(int limite) {
        try {
            ctx.matricula.trancarPeriodo(DENTRO_TRANCAMENTO, INICIO_TRANCAMENTO, FIM_TRANCAMENTO,
                    limite, limite);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    // ===== Resultados compartilhados =====

    @Então("a matrícula deve conter {int} item")
    public void matriculaDeveConterItens(int quantidade) {
        assertEquals(quantidade, ctx.matricula.getItens().size());
    }

    @Então("a matrícula deve estar com status {string}")
    public void matriculaDeveEstarComStatus(String status) {
        assertEquals(StatusMatricula.valueOf(status), ctx.matricula.getStatus());
    }

    @Então("o item da turma {int} deve ter status {string}")
    public void itemDeveEstarComStatus(int turmaId, String status) {
        ItemMatricula item = ctx.matricula.getItens().stream()
                .filter(i -> i.getTurmaId().equals(new TurmaId(turmaId)))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Item não encontrado para a turma " + turmaId));
        assertEquals(StatusItemMatricula.valueOf(status), item.getStatus());
    }

    @Então("o sistema deve rejeitar informando {string}")
    public void o_sistema_deve_rejeitar_informando(String mensagem) {
        assertNotNull(ctx.excecao, "Esperava exceção mas nenhuma foi lançada");
        assertTrue(ctx.excecao.getMessage().toLowerCase().contains(mensagem.toLowerCase()),
                "Mensagem esperada: \"" + mensagem + "\", mas obtida: \"" + ctx.excecao.getMessage() + "\"");
    }
}
