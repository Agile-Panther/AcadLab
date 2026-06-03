package school.cesar.acadlab.dominio.periodoletivo;

import static org.apache.commons.lang3.Validate.notNull;
import java.time.LocalDate;
import school.cesar.acadlab.dominio.periodoletivo.curso.CursoId;
import school.cesar.acadlab.dominio.periodoletivo.periodo.PeriodoLetivo;
import school.cesar.acadlab.dominio.periodoletivo.periodo.PeriodoLetivoRepositorio;

public class PeriodoLetivoServico {
    private final PeriodoLetivoRepositorio repositorio;

    public PeriodoLetivoServico(PeriodoLetivoRepositorio repositorio) {
        notNull(repositorio, "O repositório não pode ser nulo");
        this.repositorio = repositorio;
    }

    // US01 - RN1: não sobreposição de períodos letivos do mesmo curso
    public PeriodoLetivo cadastrar(CursoId cursoId, int ano, int semestre,
                                   LocalDate dataInicio, LocalDate dataFim) {
        notNull(cursoId, "O curso não pode ser nulo");
        notNull(dataInicio, "A data de início não pode ser nula");
        notNull(dataFim, "A data de fim não pode ser nula");
        if (repositorio.existeSobreposicao(cursoId, dataInicio, dataFim)) {
            throw new IllegalStateException("RN1: Já existe período letivo com datas sobrepostas para este curso");
        }
        var id = repositorio.proximoId();
        var periodo = new PeriodoLetivo(id, cursoId, ano, semestre, dataInicio, dataFim);
        repositorio.salvar(periodo);
        return periodo;
    }

    // US02 - define janela acadêmica
    public void definirJanela(PeriodoLetivoId periodoId, TipoJanela tipo,
                              LocalDate inicio, LocalDate fim) {
        notNull(periodoId, "O período não pode ser nulo");
        notNull(tipo, "O tipo de janela não pode ser nulo");
        var periodo = repositorio.obter(periodoId);
        periodo.definirJanela(tipo, inicio, fim);
        repositorio.salvar(periodo);
    }

    // US03 - encerrar período; RN3 (ausência de pendências) verificada externamente
    public void encerrar(PeriodoLetivoId periodoId) {
        notNull(periodoId, "O período não pode ser nulo");
        var periodo = repositorio.obter(periodoId);
        periodo.encerrar();
        repositorio.salvar(periodo);
    }

    // US05 - RN4: edição restrita a períodos não iniciados
    public void editar(PeriodoLetivoId periodoId, LocalDate novaDataInicio, LocalDate novaDataFim) {
        notNull(periodoId, "O período não pode ser nulo");
        var periodo = repositorio.obter(periodoId);
        periodo.editar(novaDataInicio, novaDataFim);
        repositorio.salvar(periodo);
    }

    // US06 - RN5: cancelamento restrito; matrículas confirmadas verificadas externamente
    public void cancelar(PeriodoLetivoId periodoId) {
        notNull(periodoId, "O período não pode ser nulo");
        var periodo = repositorio.obter(periodoId);
        periodo.cancelar();
        repositorio.salvar(periodo);
    }
}
