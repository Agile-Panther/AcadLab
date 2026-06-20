package school.cesar.acadlab.dominio.matricula.matricula;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Matricula {
    private MatriculaId id;
    private EstudanteId estudanteId;
    private PeriodoLetivoId periodoLetivoId;
    private int limiteCreditos;
    private StatusMatricula status;
    private List<ItemMatricula> itens;
    private List<ExcecaoMatricula> excecoes;
    private EstrategiaMatricula estrategia;

    public Matricula(MatriculaId id, EstudanteId estudanteId, PeriodoLetivoId periodoLetivoId,
                      int limiteCreditos) {
        this(id, estudanteId, periodoLetivoId, limiteCreditos, new ValidacaoRegular());
    }

    public Matricula(MatriculaId id, EstudanteId estudanteId, PeriodoLetivoId periodoLetivoId,
                      int limiteCreditos, EstrategiaMatricula estrategia) {
        notNull(id, "o id da matrícula não pode ser nulo");
        notNull(estudanteId, "o id do estudante não pode ser nulo");
        notNull(periodoLetivoId, "o id do período letivo não pode ser nulo");
        notNull(estrategia, "a estratégia de matrícula não pode ser nula");
        isTrue(limiteCreditos > 0, "o limite de créditos deve ser positivo");
        this.id = id;
        this.estudanteId = estudanteId;
        this.periodoLetivoId = periodoLetivoId;
        this.limiteCreditos = limiteCreditos;
        this.status = StatusMatricula.EM_MONTAGEM;
        this.itens = new ArrayList<>();
        this.excecoes = new ArrayList<>();
        this.estrategia = estrategia;
    }

    private Matricula() {}

    public static Matricula reconstituir(MatriculaId id, EstudanteId estudanteId,
                                          PeriodoLetivoId periodoLetivoId, int limiteCreditos,
                                          StatusMatricula status, List<ItemMatricula> itens,
                                          List<ExcecaoMatricula> excecoes,
                                          EstrategiaMatricula estrategia) {
        Matricula m = new Matricula();
        m.id = id;
        m.estudanteId = estudanteId;
        m.periodoLetivoId = periodoLetivoId;
        m.limiteCreditos = limiteCreditos;
        m.status = status;
        m.itens = new ArrayList<>(itens);
        m.excecoes = new ArrayList<>(excecoes);
        m.estrategia = estrategia;
        return m;
    }

    // US01 — Montar plano (RN-1 a RN-5 validados pela estratégia)
    public void adicionarItem(TurmaId turmaId, DisciplinaId disciplinaId, int creditos,
                               List<HorarioAula> horarios, boolean cumpriuPreRequisitos,
                               boolean correquisitosNoPlano, boolean temPendencias,
                               LocalDate hoje, LocalDate inicioJanela, LocalDate fimJanela) {
        notNull(turmaId, "O id da turma não pode ser nulo");
        notNull(disciplinaId, "O id da disciplina não pode ser nulo");
        isTrue(status == StatusMatricula.EM_MONTAGEM,
                "Itens só podem ser adicionados a matrículas em montagem");
        isTrue(itens.stream().noneMatch(i -> i.getTurmaId().equals(turmaId)),
                "A turma já está no plano de matrícula");

        EstrategiaMatricula estrategiaUsada = temExcecaoDeferida(disciplinaId)
                ? new ValidacaoExcecao() : this.estrategia;

        estrategiaUsada.validarAdicao(totalCreditos(), creditos, limiteCreditos,
                cumpriuPreRequisitos, correquisitosNoPlano, temPendencias,
                hoje, inicioJanela, fimJanela);

        itens.add(new ItemMatricula(turmaId, disciplinaId, creditos, horarios));
    }

    public void removerItem(TurmaId turmaId) {
        notNull(turmaId, "O id da turma não pode ser nulo");
        isTrue(status == StatusMatricula.EM_MONTAGEM,
                "Itens só podem ser removidos de matrículas em montagem");
        boolean removido = itens.removeIf(i -> i.getTurmaId().equals(turmaId)
                && i.getStatus() == StatusItemMatricula.SELECIONADO);
        isTrue(removido, "Turma não encontrada no plano de matrícula");
    }

    // US02 — Confirmar matrícula (RN-6 e RN-7)
    public MatriculaConfirmadaEvento confirmar(Map<TurmaId, Integer> vagasPorTurma) {
        notNull(vagasPorTurma, "O mapa de vagas não pode ser nulo");
        isTrue(status == StatusMatricula.EM_MONTAGEM,
                "Apenas matrículas em montagem podem ser confirmadas");
        isTrue(!itens.isEmpty(), "Não é possível confirmar uma matrícula sem itens");

        for (ItemMatricula item : itens) {
            if (item.getStatus() != StatusItemMatricula.SELECIONADO) continue;
            Integer vagas = vagasPorTurma.get(item.getTurmaId());
            isTrue(vagas != null && vagas > 0,
                    "não há vagas disponíveis na turma " + item.getTurmaId());
        }

        verificarConflitoHorario();

        for (ItemMatricula item : itens) {
            if (item.getStatus() == StatusItemMatricula.SELECIONADO) {
                item.confirmar();
            }
        }
        this.status = StatusMatricula.AGUARDANDO_SECRETARIA;
        return new MatriculaConfirmadaEvento(this);
    }

    // US02b — Aprovação da secretaria
    public void aprovarSecretaria() {
        isTrue(status == StatusMatricula.AGUARDANDO_SECRETARIA,
                "Somente matrículas aguardando aprovação podem ser aprovadas pela secretaria");
        this.status = StatusMatricula.CONFIRMADA;
    }

    // US03 — Ajuste de matrícula (RN-8)
    public void cancelarItem(TurmaId turmaId, LocalDate hoje,
                              LocalDate inicioAjuste, LocalDate fimAjuste) {
        notNull(turmaId, "O id da turma não pode ser nulo");
        isTrue(status == StatusMatricula.CONFIRMADA,
                "Ajustes só podem ser feitos em matrículas confirmadas");
        isTrue(!hoje.isBefore(inicioAjuste) && !hoje.isAfter(fimAjuste),
                "fora da janela de ajuste de matrícula");
        ItemMatricula item = buscarItem(turmaId);
        item.cancelar();
    }

    // US04 — Trancar disciplina (RN-9)
    public void trancarDisciplina(TurmaId turmaId, LocalDate hoje,
                                   LocalDate inicioTrancamento, LocalDate fimTrancamento) {
        notNull(turmaId, "O id da turma não pode ser nulo");
        isTrue(status == StatusMatricula.CONFIRMADA,
                "O trancamento de disciplina só é permitido em matrículas confirmadas");
        isTrue(!hoje.isBefore(inicioTrancamento) && !hoje.isAfter(fimTrancamento),
                "fora da janela de trancamento");
        ItemMatricula item = buscarItem(turmaId);
        item.trancar();
    }

    // US05 — Solicitar exceção (RN-10)
    public void solicitarExcecao(DisciplinaId disciplinaId, String motivo) {
        notNull(disciplinaId, "O id da disciplina não pode ser nulo");
        isTrue(excecoes.stream().noneMatch(e -> e.getDisciplinaId().equals(disciplinaId)),
                "Já existe uma solicitação de exceção para esta disciplina");
        excecoes.add(new ExcecaoMatricula(disciplinaId, motivo));
    }

    public void deferir(DisciplinaId disciplinaId, CoordenadorId coordenadorId) {
        notNull(disciplinaId, "O id da disciplina não pode ser nulo");
        ExcecaoMatricula excecao = excecoes.stream()
                .filter(e -> e.getDisciplinaId().equals(disciplinaId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Não há solicitação de exceção para a disciplina informada"));
        excecao.deferir(coordenadorId);
    }

    // US07 — Trancar período (RN-11 e RN-12)
    public PeriodoTrancadoEvento trancarPeriodo(LocalDate hoje, LocalDate inicioTrancamento,
                                                 LocalDate fimTrancamento,
                                                 int totalTrancamentos, int limiteTrancamentos) {
        isTrue(status == StatusMatricula.CONFIRMADA,
                "trancamento de período só é permitido em matrículas confirmadas");
        isTrue(!hoje.isBefore(inicioTrancamento) && !hoje.isAfter(fimTrancamento),
                "trancamento de período fora da janela de trancamento");
        isTrue(totalTrancamentos < limiteTrancamentos,
                "limite de trancamentos de período atingido");
        for (ItemMatricula item : itens) {
            if (item.getStatus() == StatusItemMatricula.CONFIRMADO) {
                item.trancar();
            }
        }
        this.status = StatusMatricula.TRANCADA_PERIODO;
        return new PeriodoTrancadoEvento(this);
    }

    // Reverte US04 — Destrancar disciplina (volta o item trancado para confirmado)
    public void destrancarDisciplina(TurmaId turmaId) {
        notNull(turmaId, "O id da turma não pode ser nulo");
        isTrue(status == StatusMatricula.CONFIRMADA,
                "O destrancamento de disciplina só é permitido em matrículas confirmadas");
        buscarItem(turmaId).destrancar();
    }

    // Reverte US07 — Destrancar período (reativa os itens e volta a matrícula para confirmada)
    public void destrancarPeriodo() {
        isTrue(status == StatusMatricula.TRANCADA_PERIODO,
                "O destrancamento de período só é permitido em matrículas com período trancado");
        for (ItemMatricula item : itens) {
            if (item.getStatus() == StatusItemMatricula.TRANCADO) {
                item.destrancar();
            }
        }
        this.status = StatusMatricula.CONFIRMADA;
    }

    private void verificarConflitoHorario() {
        List<ItemMatricula> ativos = itens.stream()
                .filter(i -> i.getStatus() == StatusItemMatricula.SELECIONADO)
                .toList();
        for (int i = 0; i < ativos.size(); i++) {
            for (int j = i + 1; j < ativos.size(); j++) {
                isTrue(!ativos.get(i).conflitaHorario(ativos.get(j)),
                        "conflito de horário entre turmas");
            }
        }
    }

    private ItemMatricula buscarItem(TurmaId turmaId) {
        return itens.stream()
                .filter(i -> i.getTurmaId().equals(turmaId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Turma não encontrada na matrícula"));
    }

    private boolean temExcecaoDeferida(DisciplinaId disciplinaId) {
        return excecoes.stream()
                .anyMatch(e -> e.getDisciplinaId().equals(disciplinaId) && e.isDeferida());
    }

    private int totalCreditos() {
        return itens.stream()
                .filter(i -> i.getStatus() == StatusItemMatricula.SELECIONADO
                        || i.getStatus() == StatusItemMatricula.CONFIRMADO)
                .mapToInt(ItemMatricula::getCreditos)
                .sum();
    }

    public MatriculaId getId() { return id; }
    public EstudanteId getEstudanteId() { return estudanteId; }
    public PeriodoLetivoId getPeriodoLetivoId() { return periodoLetivoId; }
    public int getLimiteCreditos() { return limiteCreditos; }
    public StatusMatricula getStatus() { return status; }
    public List<ItemMatricula> getItens() { return Collections.unmodifiableList(itens); }
    public List<ExcecaoMatricula> getExcecoes() { return Collections.unmodifiableList(excecoes); }

    public static abstract class MatriculaEvento {
        private final Matricula matricula;
        protected MatriculaEvento(Matricula matricula) { this.matricula = matricula; }
        public Matricula getMatricula() { return matricula; }
    }

    public static class MatriculaConfirmadaEvento extends MatriculaEvento {
        private MatriculaConfirmadaEvento(Matricula matricula) { super(matricula); }
    }

    public static class PeriodoTrancadoEvento extends MatriculaEvento {
        private PeriodoTrancadoEvento(Matricula matricula) { super(matricula); }
    }
}
