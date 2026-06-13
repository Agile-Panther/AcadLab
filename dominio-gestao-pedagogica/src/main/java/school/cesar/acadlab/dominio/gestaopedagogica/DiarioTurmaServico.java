package school.cesar.acadlab.dominio.gestaopedagogica;

import static org.apache.commons.lang3.Validate.notNull;
import java.time.LocalDate;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.AvaliacaoId;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.DiarioTurma;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.DiarioTurmaId;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.DiarioTurmaRepositorio;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.EstudanteId;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.PeriodoLetivoId;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.ProfessorId;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.RegistroAulaId;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.TurmaId;

public class DiarioTurmaServico {
    private final DiarioTurmaRepositorio repositorio;

    public DiarioTurmaServico(DiarioTurmaRepositorio repositorio) {
        notNull(repositorio, "O repositório não pode ser nulo");
        this.repositorio = repositorio;
    }

    public DiarioTurma cadastrar(TurmaId turmaId, PeriodoLetivoId periodoLetivoId,
                                  ProfessorId professorResponsavel,
                                  LocalDate dataInicioPeriodo, LocalDate dataFimPeriodo,
                                  double mediaMinima, double frequenciaMinima) {
        notNull(turmaId, "O id da turma não pode ser nulo");
        notNull(periodoLetivoId, "O id do período letivo não pode ser nulo");
        notNull(professorResponsavel, "O professor responsável não pode ser nulo");
        notNull(dataInicioPeriodo, "A data de início não pode ser nula");
        notNull(dataFimPeriodo, "A data de fim não pode ser nula");
        var id = repositorio.proximoId();
        var diario = new DiarioTurma(id, turmaId, periodoLetivoId, professorResponsavel,
                dataInicioPeriodo, dataFimPeriodo, mediaMinima, frequenciaMinima);
        repositorio.salvar(diario);
        return diario;
    }

    public RegistroAulaId registrarAula(DiarioTurmaId diarioId, ProfessorId professorId,
                                         LocalDate data, String conteudo) {
        notNull(diarioId, "O id do diário não pode ser nulo");
        var diario = repositorio.obter(diarioId);
        var aulaId = repositorio.proximoAulaId();
        diario.registrarAula(aulaId, professorId, data, conteudo);
        repositorio.salvar(diario);
        return aulaId;
    }

    public void corrigirAula(DiarioTurmaId diarioId, RegistroAulaId aulaId,
                              ProfessorId professorId, String novoConteudo) {
        notNull(diarioId, "O id do diário não pode ser nulo");
        var diario = repositorio.obter(diarioId);
        diario.corrigirAula(aulaId, professorId, novoConteudo);
        repositorio.salvar(diario);
    }

    public void registrarFrequencia(DiarioTurmaId diarioId, ProfessorId professorId,
                                     RegistroAulaId aulaId, EstudanteId estudanteId, boolean presente) {
        notNull(diarioId, "O id do diário não pode ser nulo");
        var diario = repositorio.obter(diarioId);
        diario.registrarFrequencia(professorId, aulaId, estudanteId, presente);
        repositorio.salvar(diario);
    }

    public void adicionarEstudanteAtivo(DiarioTurmaId diarioId, EstudanteId estudanteId) {
        notNull(diarioId, "O id do diário não pode ser nulo");
        var diario = repositorio.obter(diarioId);
        diario.adicionarEstudanteAtivo(estudanteId);
        repositorio.salvar(diario);
    }

    public AvaliacaoId adicionarAvaliacao(DiarioTurmaId diarioId, String nome,
                                           double peso, LocalDate prazo) {
        notNull(diarioId, "O id do diário não pode ser nulo");
        var diario = repositorio.obter(diarioId);
        var avaliacaoId = repositorio.proximaAvaliacaoId();
        diario.adicionarAvaliacao(avaliacaoId, nome, peso, prazo);
        repositorio.salvar(diario);
        return avaliacaoId;
    }

    public void lancarNota(DiarioTurmaId diarioId, EstudanteId estudanteId,
                            AvaliacaoId avaliacaoId, double nota) {
        notNull(diarioId, "O id do diário não pode ser nulo");
        var diario = repositorio.obter(diarioId);
        diario.lancarNota(estudanteId, avaliacaoId, nota);
        repositorio.salvar(diario);
    }

    public void fecharResultado(DiarioTurmaId diarioId, EstudanteId estudanteId) {
        notNull(diarioId, "O id do diário não pode ser nulo");
        var diario = repositorio.obter(diarioId);
        diario.fecharResultado(estudanteId);
        repositorio.salvar(diario);
    }

    public void solicitarRevisaoNota(DiarioTurmaId diarioId, EstudanteId estudanteId,
                                      LocalDate hoje, LocalDate fimJanelaRevisao) {
        notNull(diarioId, "O id do diário não pode ser nulo");
        var diario = repositorio.obter(diarioId);
        diario.solicitarRevisaoNota(estudanteId, hoje, fimJanelaRevisao);
        repositorio.salvar(diario);
    }

    public void lancarNotaRecuperacao(DiarioTurmaId diarioId, EstudanteId estudanteId,
                                       double nota, LocalDate hoje) {
        notNull(diarioId, "O id do diário não pode ser nulo");
        var diario = repositorio.obter(diarioId);
        diario.lancarNotaRecuperacao(estudanteId, nota, hoje);
        repositorio.salvar(diario);
    }
}
