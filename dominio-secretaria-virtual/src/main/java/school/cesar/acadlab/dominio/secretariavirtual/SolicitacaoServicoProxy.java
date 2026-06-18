package school.cesar.acadlab.dominio.secretariavirtual;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.List;
import school.cesar.acadlab.dominio.secretariavirtual.documento.Documento;
import school.cesar.acadlab.dominio.secretariavirtual.estudante.EstudanteId;
import school.cesar.acadlab.dominio.secretariavirtual.periodo.PeriodoLetivoId;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.SolicitacaoAcademica;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.SolicitacaoAcademicaId;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.SolicitacaoAcademicaRepositorio;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.TipoSolicitacao;

public class SolicitacaoServicoProxy implements SolicitacaoServico {
    private final SolicitacaoServico servicoReal;
    private final SolicitacaoAcademicaRepositorio repositorio;
    private final CalendarioAcademicoPorta calendario;

    public SolicitacaoServicoProxy(SolicitacaoServico servicoReal,
                                    SolicitacaoAcademicaRepositorio repositorio,
                                    CalendarioAcademicoPorta calendario) {
        notNull(servicoReal, "O serviço real não pode ser nulo");
        notNull(repositorio, "O repositório não pode ser nulo");
        notNull(calendario, "O calendário não pode ser nulo");
        this.servicoReal = servicoReal;
        this.repositorio = repositorio;
        this.calendario = calendario;
    }

    // RN1: verifica prazo do calendário
    // RN2: verifica duplicidade de solicitação do mesmo tipo por período
    @Override
    public SolicitacaoAcademica abrirSolicitacao(EstudanteId estudanteId, PeriodoLetivoId periodoLetivoId,
                                                  TipoSolicitacao tipo, String descricao,
                                                  List<Documento> documentos) {
        if (!calendario.estaDentroDoPrazo(tipo, periodoLetivoId)) {
            throw new IllegalStateException(
                    "Abertura de solicitação fora do prazo do calendário acadêmico para o tipo " + tipo);
        }

        if (!tipo.isPermiteMultiplasPorPeriodo()) {
            var existente = repositorio.pesquisarAbertaPorEstudanteTipoPeriodo(
                    estudanteId, tipo, periodoLetivoId);
            if (existente.isPresent()) {
                throw new IllegalStateException(
                        "Já existe uma solicitação do tipo " + tipo + " aberta para este período letivo");
            }
        }

        return servicoReal.abrirSolicitacao(estudanteId, periodoLetivoId, tipo, descricao, documentos);
    }

    @Override
    public void complementarSolicitacao(SolicitacaoAcademicaId id, Documento documento) {
        servicoReal.complementarSolicitacao(id, documento);
    }

    @Override
    public void cancelarSolicitacao(SolicitacaoAcademicaId id) {
        servicoReal.cancelarSolicitacao(id);
    }
}
