package school.cesar.acadlab.dominio.gestaopedagogica.diario;

import static org.apache.commons.lang3.Validate.notNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import school.cesar.acadlab.dominio.gestaopedagogica.diario.apuracao.ApuracaoMediaPonderada;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.apuracao.ApuracaoResultado;

public class DiarioTurma {
    private final DiarioTurmaId id;
    private final TurmaId turmaId;
    private final PeriodoLetivoId periodoLetivoId;
    private final ProfessorId professorResponsavel;
    private final LocalDate dataInicioPeriodo;
    private final LocalDate dataFimPeriodo;
    private final double mediaMinima;
    private final double frequenciaMinima;
    private StatusDiario status;
    private final List<RegistroAula> aulas;
    private final List<Avaliacao> avaliacoes;
    private final List<LancamentoFrequencia> frequencias;
    private final List<ResultadoEstudante> resultados;
    private final Set<EstudanteId> estudantesAtivos;

    public DiarioTurma(DiarioTurmaId id, TurmaId turmaId, PeriodoLetivoId periodoLetivoId,
                       ProfessorId professorResponsavel, LocalDate dataInicioPeriodo, LocalDate dataFimPeriodo,
                       double mediaMinima, double frequenciaMinima) {
        notNull(id, "O id do diário não pode ser nulo");
        notNull(turmaId, "O id da turma não pode ser nulo");
        notNull(periodoLetivoId, "O id do período letivo não pode ser nulo");
        notNull(professorResponsavel, "O professor responsável não pode ser nulo");
        notNull(dataInicioPeriodo, "A data de início do período não pode ser nula");
        notNull(dataFimPeriodo, "A data de fim do período não pode ser nula");
        this.id = id;
        this.turmaId = turmaId;
        this.periodoLetivoId = periodoLetivoId;
        this.professorResponsavel = professorResponsavel;
        this.dataInicioPeriodo = dataInicioPeriodo;
        this.dataFimPeriodo = dataFimPeriodo;
        this.mediaMinima = mediaMinima;
        this.frequenciaMinima = frequenciaMinima;
        this.status = StatusDiario.ABERTO;
        this.aulas = new ArrayList<>();
        this.avaliacoes = new ArrayList<>();
        this.frequencias = new ArrayList<>();
        this.resultados = new ArrayList<>();
        this.estudantesAtivos = new HashSet<>();
    }

    private DiarioTurma(DiarioTurmaId id, TurmaId turmaId, PeriodoLetivoId periodoLetivoId,
                        ProfessorId professorResponsavel, LocalDate dataInicioPeriodo,
                        LocalDate dataFimPeriodo, double mediaMinima, double frequenciaMinima,
                        StatusDiario status, List<RegistroAula> aulas, List<Avaliacao> avaliacoes,
                        List<LancamentoFrequencia> frequencias, List<ResultadoEstudante> resultados,
                        Set<EstudanteId> estudantesAtivos) {
        this.id = id;
        this.turmaId = turmaId;
        this.periodoLetivoId = periodoLetivoId;
        this.professorResponsavel = professorResponsavel;
        this.dataInicioPeriodo = dataInicioPeriodo;
        this.dataFimPeriodo = dataFimPeriodo;
        this.mediaMinima = mediaMinima;
        this.frequenciaMinima = frequenciaMinima;
        this.status = status;
        this.aulas = new ArrayList<>(aulas);
        this.avaliacoes = new ArrayList<>(avaliacoes);
        this.frequencias = new ArrayList<>(frequencias);
        this.resultados = new ArrayList<>(resultados);
        this.estudantesAtivos = new HashSet<>(estudantesAtivos);
    }

    public static DiarioTurma reconstituir(DiarioTurmaId id, TurmaId turmaId,
                                            PeriodoLetivoId periodoLetivoId,
                                            ProfessorId professorResponsavel,
                                            LocalDate dataInicioPeriodo, LocalDate dataFimPeriodo,
                                            double mediaMinima, double frequenciaMinima,
                                            StatusDiario status, List<RegistroAula> aulas,
                                            List<Avaliacao> avaliacoes,
                                            List<LancamentoFrequencia> frequencias,
                                            List<ResultadoEstudante> resultados,
                                            Set<EstudanteId> estudantesAtivos) {
        return new DiarioTurma(id, turmaId, periodoLetivoId, professorResponsavel,
                dataInicioPeriodo, dataFimPeriodo, mediaMinima, frequenciaMinima,
                status, aulas, avaliacoes, frequencias, resultados, estudantesAtivos);
    }

    public DiarioTurmaId getId() { return id; }
    public TurmaId getTurmaId() { return turmaId; }
    public PeriodoLetivoId getPeriodoLetivoId() { return periodoLetivoId; }
    public ProfessorId getProfessorResponsavel() { return professorResponsavel; }
    public LocalDate getDataInicioPeriodo() { return dataInicioPeriodo; }
    public LocalDate getDataFimPeriodo() { return dataFimPeriodo; }
    public double getMediaMinima() { return mediaMinima; }
    public double getFrequenciaMinima() { return frequenciaMinima; }
    public StatusDiario getStatus() { return status; }
    public List<RegistroAula> getAulas() { return Collections.unmodifiableList(aulas); }
    public List<Avaliacao> getAvaliacoes() { return Collections.unmodifiableList(avaliacoes); }
    public List<LancamentoFrequencia> getFrequencias() { return Collections.unmodifiableList(frequencias); }
    public List<ResultadoEstudante> getResultados() { return Collections.unmodifiableList(resultados); }
    public Set<EstudanteId> getEstudantesAtivos() { return Collections.unmodifiableSet(estudantesAtivos); }

    // RN-1: apenas o professor responsável pode registrar aulas.
    // RN-2: aula deve ser registrada dentro do período letivo.
    public void registrarAula(RegistroAulaId aulaId, ProfessorId professorId, LocalDate data, String conteudo) {
        notNull(aulaId, "O id da aula não pode ser nulo");
        notNull(professorId, "O professor não pode ser nulo");
        notNull(data, "A data não pode ser nula");
        if (!professorId.equals(professorResponsavel)) {
            throw new IllegalStateException("professor não é o responsável pelo diário");
        }
        if (data.isBefore(dataInicioPeriodo) || data.isAfter(dataFimPeriodo)) {
            throw new IllegalStateException("aula deve ser registrada dentro do período letivo");
        }
        aulas.add(new RegistroAula(aulaId, professorId, data, conteudo));
    }

    // RN-10 e RN-11: delegados ao RegistroAula.
    public void corrigirAula(RegistroAulaId aulaId, ProfessorId professorId, String novoConteudo) {
        notNull(aulaId, "O id da aula não pode ser nulo");
        var aula = aulas.stream()
                .filter(a -> a.getId().equals(aulaId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Aula não encontrada: " + aulaId));
        aula.corrigir(professorId, novoConteudo, status == StatusDiario.ABERTO);
    }

    // RN-4: apenas o professor responsável pode registrar frequência.
    // RN-3: apenas estudantes com matrícula ativa podem ter frequência registrada.
    public void registrarFrequencia(ProfessorId professorId, RegistroAulaId aulaId,
                                    EstudanteId estudanteId, boolean presente) {
        notNull(professorId, "O professor não pode ser nulo");
        notNull(aulaId, "O id da aula não pode ser nulo");
        notNull(estudanteId, "O estudante não pode ser nulo");
        if (!professorId.equals(professorResponsavel)) {
            throw new IllegalStateException("professor não é o responsável pelo diário");
        }
        if (!estudantesAtivos.contains(estudanteId)) {
            throw new IllegalStateException("estudante não está matriculado na turma");
        }
        frequencias.add(new LancamentoFrequencia(aulaId, estudanteId, presente));
    }

    public void adicionarEstudanteAtivo(EstudanteId estudanteId) {
        notNull(estudanteId, "O estudante não pode ser nulo");
        estudantesAtivos.add(estudanteId);
        boolean jaExiste = resultados.stream().anyMatch(r -> r.getEstudanteId().equals(estudanteId));
        if (!jaExiste) {
            resultados.add(new ResultadoEstudante(estudanteId));
        }
    }

    // RN-6: prazo da avaliação deve estar dentro do período letivo.
    // RN-5: soma dos pesos das avaliações não pode ultrapassar 100%.
    public void adicionarAvaliacao(AvaliacaoId avaliacaoId, String nome, double peso, LocalDate prazo) {
        notNull(avaliacaoId, "O id da avaliação não pode ser nulo");
        notNull(nome, "O nome da avaliação não pode ser nulo");
        notNull(prazo, "O prazo da avaliação não pode ser nulo");
        if (prazo.isBefore(dataInicioPeriodo) || prazo.isAfter(dataFimPeriodo)) {
            throw new IllegalStateException("prazo da avaliação está fora do período letivo");
        }
        double somaAtual = avaliacoes.stream().mapToDouble(Avaliacao::getPeso).sum();
        if (somaAtual + peso > 100.0) {
            throw new IllegalStateException("soma dos pesos das avaliações ultrapassa 100%");
        }
        avaliacoes.add(new Avaliacao(avaliacaoId, nome, peso, prazo));
    }

    // RN-8: delegado ao ResultadoEstudante.
    public void lancarNota(EstudanteId estudanteId, AvaliacaoId avaliacaoId, double nota) {
        notNull(estudanteId, "O estudante não pode ser nulo");
        notNull(avaliacaoId, "O id da avaliação não pode ser nulo");
        obterOuCriarResultado(estudanteId).adicionarNota(avaliacaoId, nota);
    }

    // RN-7: fecha o resultado do estudante. Usa, por padrão, o regime de média
    // ponderada; o cálculo segue o Template Method ApuracaoResultado.
    public void fecharResultado(EstudanteId estudanteId) {
        fecharResultado(estudanteId, new ApuracaoMediaPonderada());
    }

    // RN-7: fecha o resultado aplicando o regime de apuração informado (Template Method).
    public void fecharResultado(EstudanteId estudanteId, ApuracaoResultado apuracao) {
        notNull(estudanteId, "O estudante não pode ser nulo");
        notNull(apuracao, "O regime de apuração não pode ser nulo");
        var resultado = obterResultado(estudanteId);
        apuracao.apurar(this, resultado);
    }

    // RN-9: revisão de nota deve estar dentro da janela de revisão.
    public void solicitarRevisaoNota(EstudanteId estudanteId, LocalDate hoje, LocalDate fimJanelaRevisao) {
        notNull(estudanteId, "O estudante não pode ser nulo");
        notNull(hoje, "A data atual não pode ser nula");
        notNull(fimJanelaRevisao, "A data fim da janela de revisão não pode ser nula");
        if (hoje.isAfter(fimJanelaRevisao)) {
            throw new IllegalStateException("janela de revisão de nota encerrada");
        }
        obterResultado(estudanteId).solicitarRevisao();
    }

    // RN-13: nota de recuperação deve ser lançada dentro do período letivo.
    // RN-12: estudante deve estar em situação de recuperação.
    public void lancarNotaRecuperacao(EstudanteId estudanteId, double nota, LocalDate hoje) {
        notNull(estudanteId, "O estudante não pode ser nulo");
        notNull(hoje, "A data atual não pode ser nula");
        if (hoje.isAfter(dataFimPeriodo)) {
            throw new IllegalStateException("período letivo já encerrado");
        }
        var resultado = obterResultado(estudanteId);
        if (resultado.getSituacao() != SituacaoResultado.RECUPERACAO) {
            throw new IllegalStateException("estudante não está em situação de recuperação");
        }
        resultado.lancarRecuperacao(nota);
        resultado.atualizarSituacaoRecuperacao(nota >= mediaMinima ? SituacaoResultado.APROVADO : SituacaoResultado.REPROVADO_NOTA);
    }

    private ResultadoEstudante obterResultado(EstudanteId estudanteId) {
        return resultados.stream()
                .filter(r -> r.getEstudanteId().equals(estudanteId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Resultado não encontrado para o estudante: " + estudanteId));
    }

    private ResultadoEstudante obterOuCriarResultado(EstudanteId estudanteId) {
        return resultados.stream()
                .filter(r -> r.getEstudanteId().equals(estudanteId))
                .findFirst()
                .orElseGet(() -> {
                    var novo = new ResultadoEstudante(estudanteId);
                    resultados.add(novo);
                    return novo;
                });
    }
}
