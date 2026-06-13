package school.cesar.acadlab.dominio.periodoletivo;

import static org.apache.commons.lang3.Validate.notNull;
import java.time.LocalDate;
import java.util.*;
import school.cesar.acadlab.dominio.periodoletivo.curso.CursoId;
import school.cesar.acadlab.dominio.periodoletivo.periodo.PeriodoLetivo;
import school.cesar.acadlab.dominio.periodoletivo.periodo.PeriodoLetivoRepositorio;

public class Repositorio implements PeriodoLetivoRepositorio {

    private int proximoIdSeq = 1;
    private final Map<PeriodoLetivoId, PeriodoLetivo> periodos = new HashMap<>();

    @Override
    public PeriodoLetivoId proximoId() { return new PeriodoLetivoId(proximoIdSeq++); }

    @Override
    public void salvar(PeriodoLetivo periodo) {
        notNull(periodo, "O período não pode ser nulo");
        periodos.put(periodo.getId(), periodo);
    }

    @Override
    public PeriodoLetivo obter(PeriodoLetivoId id) {
        notNull(id, "O id não pode ser nulo");
        return Optional.ofNullable(periodos.get(id)).get();
    }

    @Override
    public List<PeriodoLetivo> pesquisarPorCurso(CursoId cursoId) {
        var resultado = new ArrayList<PeriodoLetivo>();
        for (var p : periodos.values()) {
            if (p.getCursoId().equals(cursoId)) resultado.add(p);
        }
        return resultado;
    }

    @Override
    public Optional<PeriodoLetivo> pesquisarPorCursoEStatus(CursoId cursoId, StatusPeriodoLetivo status) {
        return periodos.values().stream()
                .filter(p -> p.getCursoId().equals(cursoId) && p.getStatus() == status)
                .findFirst();
    }

    @Override
    public boolean existeSobreposicao(CursoId cursoId, LocalDate inicio, LocalDate fim) {
        return periodos.values().stream()
                .filter(p -> p.getCursoId().equals(cursoId))
                .filter(p -> p.getStatus() != StatusPeriodoLetivo.CANCELADO && p.getStatus() != StatusPeriodoLetivo.ENCERRADO)
                .anyMatch(p -> !p.getDataFim().isBefore(inicio) && !p.getDataInicio().isAfter(fim));
    }

    @Override
    public boolean existeSobreposicaoExcluindo(CursoId cursoId, LocalDate inicio, LocalDate fim, PeriodoLetivoId excluindo) {
        return periodos.values().stream()
                .filter(p -> p.getCursoId().equals(cursoId))
                .filter(p -> !p.getId().equals(excluindo))
                .filter(p -> p.getStatus() != StatusPeriodoLetivo.CANCELADO && p.getStatus() != StatusPeriodoLetivo.ENCERRADO)
                .anyMatch(p -> !p.getDataFim().isBefore(inicio) && !p.getDataInicio().isAfter(fim));
    }
}
