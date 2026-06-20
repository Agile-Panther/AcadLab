package school.cesar.acadlab.dominio.integralizacao;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.List;
import school.cesar.acadlab.dominio.integralizacao.checklist.ItemChecklist;
import school.cesar.acadlab.dominio.integralizacao.integralizacao.IntegralizacaoCurricular;
import school.cesar.acadlab.dominio.integralizacao.integralizacao.IntegralizacaoId;
import school.cesar.acadlab.dominio.integralizacao.integralizacao.IntegralizacaoRepositorio;
import school.cesar.acadlab.dominio.integralizacao.integralizacao.StatusIntegralizacao;

public class IntegralizacaoServicoProxy implements IntegralizacaoOperacoes {

    private final IntegralizacaoOperacoes real;
    private final IntegralizacaoRepositorio repositorio;
    private final ConsultaPeriodoLetivoPorta consultaPeriodoLetivo;
    private final ConsultaPendenciasPorta consultaPendencias;
    private final ConsultaRequisitosIntegralizacaoPorta consultaRequisitos;

    public IntegralizacaoServicoProxy(IntegralizacaoOperacoes real,
                                       IntegralizacaoRepositorio repositorio,
                                       ConsultaPeriodoLetivoPorta consultaPeriodoLetivo,
                                       ConsultaPendenciasPorta consultaPendencias,
                                       ConsultaRequisitosIntegralizacaoPorta consultaRequisitos) {
        notNull(real, "O serviço real não pode ser nulo");
        notNull(repositorio, "O repositório não pode ser nulo");
        notNull(consultaPeriodoLetivo, "A porta de período letivo não pode ser nula");
        notNull(consultaPendencias, "A porta de pendências não pode ser nula");
        notNull(consultaRequisitos, "A porta de requisitos não pode ser nula");
        this.real = real;
        this.repositorio = repositorio;
        this.consultaPeriodoLetivo = consultaPeriodoLetivo;
        this.consultaPendencias = consultaPendencias;
        this.consultaRequisitos = consultaRequisitos;
    }

    @Override
    public IntegralizacaoCurricular iniciarAnalise(EstudanteId estudanteId,
                                                    MatrizCurricularId matrizId) {
        if (!consultaPeriodoLetivo.ultimoPeriodoEncerrado(estudanteId)) {
            throw new IllegalStateException("período letivo ainda não foi encerrado");
        }
        if (consultaPendencias.possuiPendencias(estudanteId)) {
            throw new IllegalStateException("estudante possui pendências acadêmicas");
        }
        return real.iniciarAnalise(estudanteId, matrizId);
    }

    @Override
    public void gerarChecklist(IntegralizacaoId integralizacaoId, List<ItemChecklist> itens) {
        real.gerarChecklist(integralizacaoId, itens);
    }

    @Override
    public void registrarResultado(IntegralizacaoId integralizacaoId,
                                    StatusIntegralizacao resultado) {
        real.registrarResultado(integralizacaoId, resultado);
    }

    @Override
    public void aprovarAptidao(IntegralizacaoId integralizacaoId, CoordenadorId aprovadorId) {
        IntegralizacaoCurricular integralizacao = repositorio.obter(integralizacaoId);
        if (!consultaRequisitos.cumpreTodosRequisitos(
                integralizacao.getEstudanteId(), integralizacao.getMatrizCurricularId())) {
            throw new IllegalStateException("requisitos curriculares não foram cumpridos");
        }
        real.aprovarAptidao(integralizacaoId, aprovadorId);
    }
}
