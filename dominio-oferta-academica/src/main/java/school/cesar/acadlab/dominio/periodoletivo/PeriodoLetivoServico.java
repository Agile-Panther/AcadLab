package school.cesar.acadlab.dominio.periodoletivo;

import static org.apache.commons.lang3.Validate.notNull;
import java.time.LocalDate;
import java.util.List;
import school.cesar.acadlab.dominio.periodoletivo.curso.CursoId;
import school.cesar.acadlab.dominio.periodoletivo.periodo.PeriodoLetivo;
import school.cesar.acadlab.dominio.periodoletivo.periodo.PeriodoLetivoRepositorio;

public class PeriodoLetivoServico {
    private final PeriodoLetivoRepositorio repositorio;
    private final VerificadorPendenciasPeriodo verificadorPendencias;
    private final VerificadorMatriculasPeriodo verificadorMatriculas;

    public PeriodoLetivoServico(PeriodoLetivoRepositorio repositorio,
                                VerificadorPendenciasPeriodo verificadorPendencias,
                                VerificadorMatriculasPeriodo verificadorMatriculas) {
        notNull(repositorio, "O repositório não pode ser nulo");
        notNull(verificadorPendencias, "O verificador de pendências não pode ser nulo");
        notNull(verificadorMatriculas, "O verificador de matrículas não pode ser nulo");
        this.repositorio = repositorio;
        this.verificadorPendencias = verificadorPendencias;
        this.verificadorMatriculas = verificadorMatriculas;
    }

    // US01 - RN1: não sobreposição de períodos letivos do mesmo curso
    public PeriodoLetivo cadastrar(CursoId cursoId, int ano, int semestre,
                                   LocalDate dataInicio, LocalDate dataFim) {
        notNull(cursoId, "O curso não pode ser nulo");
        notNull(dataInicio, "A data de início não pode ser nula");
        notNull(dataFim, "A data de fim não pode ser nula");
        if (repositorio.existeSobreposicao(cursoId, dataInicio, dataFim)) {
            throw new IllegalStateException("datas se sobrepõem a período letivo já existente");
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

    // US03 - RN3: verifica pendências externas antes de encerrar
    public void encerrar(PeriodoLetivoId periodoId) {
        notNull(periodoId, "O período não pode ser nulo");
        if (verificadorPendencias.possuiPendencias(periodoId)) {
            throw new IllegalStateException("período letivo possui pendências abertas que impedem o encerramento");
        }
        var periodo = repositorio.obter(periodoId);
        periodo.encerrar();
        repositorio.salvar(periodo);
    }

    // US-iniciar: inicia o período letivo (NAO_INICIADO → EM_ANDAMENTO)
    public void iniciar(PeriodoLetivoId periodoId) {
        notNull(periodoId, "O período não pode ser nulo");
        var periodo = repositorio.obter(periodoId);
        periodo.iniciar();
        repositorio.salvar(periodo);
    }

    // US05 - RN1+RN4: edição restrita a períodos não iniciados, sem sobreposição nas novas datas
    public void editar(PeriodoLetivoId periodoId, LocalDate novaDataInicio, LocalDate novaDataFim) {
        notNull(periodoId, "O período não pode ser nulo");
        var periodo = repositorio.obter(periodoId);
        if (repositorio.existeSobreposicaoExcluindo(periodo.getCursoId(), novaDataInicio, novaDataFim, periodoId)) {
            throw new IllegalStateException("datas se sobrepõem a período letivo já existente");
        }
        periodo.editar(novaDataInicio, novaDataFim);
        repositorio.salvar(periodo);
    }

    // US06 - RN5: verifica matrículas externas antes de cancelar
    public void cancelar(PeriodoLetivoId periodoId) {
        notNull(periodoId, "O período não pode ser nulo");
        if (verificadorMatriculas.possuiMatriculasConfirmadas(periodoId)) {
            throw new IllegalStateException("período letivo possui matrículas confirmadas e não pode ser cancelado");
        }
        var periodo = repositorio.obter(periodoId);
        periodo.cancelar();
        repositorio.salvar(periodo);
    }

    // US04 - consultar
    public List<PeriodoLetivo> pesquisarPorCurso(CursoId cursoId) {
        notNull(cursoId, "O curso não pode ser nulo");
        return repositorio.pesquisarPorCurso(cursoId);
    }
}
