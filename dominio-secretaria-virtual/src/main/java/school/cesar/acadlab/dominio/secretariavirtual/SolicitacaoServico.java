package school.cesar.acadlab.dominio.secretariavirtual;

import java.util.List;
import school.cesar.acadlab.dominio.secretariavirtual.documento.Documento;
import school.cesar.acadlab.dominio.secretariavirtual.estudante.EstudanteId;
import school.cesar.acadlab.dominio.secretariavirtual.periodo.PeriodoLetivoId;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.SolicitacaoAcademica;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.SolicitacaoAcademicaId;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.TipoSolicitacao;

public interface SolicitacaoServico {
    SolicitacaoAcademica abrirSolicitacao(EstudanteId estudanteId, PeriodoLetivoId periodoLetivoId,
                                           TipoSolicitacao tipo, String descricao,
                                           List<Documento> documentos);
    void complementarSolicitacao(SolicitacaoAcademicaId id, Documento documento);
    void cancelarSolicitacao(SolicitacaoAcademicaId id);
}
