package school.cesar.acadlab.dominio.matricula;

import static org.apache.commons.lang3.Validate.notNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import school.cesar.acadlab.dominio.matricula.matricula.CoordenadorId;
import school.cesar.acadlab.dominio.matricula.matricula.DisciplinaId;
import school.cesar.acadlab.dominio.matricula.matricula.EstudanteId;
import school.cesar.acadlab.dominio.matricula.matricula.HorarioAula;
import school.cesar.acadlab.dominio.matricula.matricula.Matricula;
import school.cesar.acadlab.dominio.matricula.matricula.MatriculaId;
import school.cesar.acadlab.dominio.matricula.matricula.MatriculaRepositorio;
import school.cesar.acadlab.dominio.matricula.matricula.PeriodoLetivoId;
import school.cesar.acadlab.dominio.matricula.matricula.TurmaId;

public class MatriculaServico {
    private final MatriculaRepositorio repositorio;

    public MatriculaServico(MatriculaRepositorio repositorio) {
        notNull(repositorio, "O repositório não pode ser nulo");
        this.repositorio = repositorio;
    }

    public Matricula iniciarMatricula(EstudanteId estudanteId, PeriodoLetivoId periodoLetivoId,
                                       int limiteCreditos) {
        notNull(estudanteId, "O id do estudante não pode ser nulo");
        notNull(periodoLetivoId, "O id do período letivo não pode ser nulo");
        var id = repositorio.proximaMatriculaId();
        var matricula = new Matricula(id, estudanteId, periodoLetivoId, limiteCreditos);
        repositorio.salvar(matricula);
        return matricula;
    }

    public void adicionarItem(MatriculaId matriculaId, TurmaId turmaId, DisciplinaId disciplinaId,
                               int creditos, List<HorarioAula> horarios,
                               boolean cumpriuPreRequisitos, boolean correquisitosNoPlano,
                               boolean temPendencias, LocalDate hoje,
                               LocalDate inicioJanela, LocalDate fimJanela) {
        notNull(matriculaId, "O id da matrícula não pode ser nulo");
        var matricula = repositorio.buscarPorId(matriculaId).orElseThrow();
        matricula.adicionarItem(turmaId, disciplinaId, creditos, horarios,
                cumpriuPreRequisitos, correquisitosNoPlano, temPendencias,
                hoje, inicioJanela, fimJanela);
        repositorio.salvar(matricula);
    }

    public void removerItem(MatriculaId matriculaId, TurmaId turmaId) {
        notNull(matriculaId, "O id da matrícula não pode ser nulo");
        var matricula = repositorio.buscarPorId(matriculaId).orElseThrow();
        matricula.removerItem(turmaId);
        repositorio.salvar(matricula);
    }

    public void confirmar(MatriculaId matriculaId, Map<TurmaId, Integer> vagasPorTurma) {
        notNull(matriculaId, "O id da matrícula não pode ser nulo");
        var matricula = repositorio.buscarPorId(matriculaId).orElseThrow();
        matricula.confirmar(vagasPorTurma);
        repositorio.salvar(matricula);
    }

    public void cancelarItem(MatriculaId matriculaId, TurmaId turmaId,
                              LocalDate hoje, LocalDate inicioAjuste, LocalDate fimAjuste) {
        notNull(matriculaId, "O id da matrícula não pode ser nulo");
        var matricula = repositorio.buscarPorId(matriculaId).orElseThrow();
        matricula.cancelarItem(turmaId, hoje, inicioAjuste, fimAjuste);
        repositorio.salvar(matricula);
    }

    public void trancarDisciplina(MatriculaId matriculaId, TurmaId turmaId,
                                   LocalDate hoje, LocalDate inicioTrancamento,
                                   LocalDate fimTrancamento) {
        notNull(matriculaId, "O id da matrícula não pode ser nulo");
        var matricula = repositorio.buscarPorId(matriculaId).orElseThrow();
        matricula.trancarDisciplina(turmaId, hoje, inicioTrancamento, fimTrancamento);
        repositorio.salvar(matricula);
    }

    public void solicitarExcecao(MatriculaId matriculaId, DisciplinaId disciplinaId,
                                  String motivo) {
        notNull(matriculaId, "O id da matrícula não pode ser nulo");
        var matricula = repositorio.buscarPorId(matriculaId).orElseThrow();
        matricula.solicitarExcecao(disciplinaId, motivo);
        repositorio.salvar(matricula);
    }

    public void deferir(MatriculaId matriculaId, DisciplinaId disciplinaId,
                         CoordenadorId coordenadorId) {
        notNull(matriculaId, "O id da matrícula não pode ser nulo");
        var matricula = repositorio.buscarPorId(matriculaId).orElseThrow();
        matricula.deferir(disciplinaId, coordenadorId);
        repositorio.salvar(matricula);
    }

    public void bloquear(MatriculaId matriculaId) {
        notNull(matriculaId, "O id da matrícula não pode ser nulo");
        var matricula = repositorio.buscarPorId(matriculaId).orElseThrow();
        matricula.bloquear();
        repositorio.salvar(matricula);
    }

    public void trancarPeriodo(MatriculaId matriculaId, LocalDate hoje,
                                LocalDate inicioTrancamento, LocalDate fimTrancamento,
                                int totalTrancamentos, int limiteTrancamentos) {
        notNull(matriculaId, "O id da matrícula não pode ser nulo");
        var matricula = repositorio.buscarPorId(matriculaId).orElseThrow();
        matricula.trancarPeriodo(hoje, inicioTrancamento, fimTrancamento,
                totalTrancamentos, limiteTrancamentos);
        repositorio.salvar(matricula);
    }
}
