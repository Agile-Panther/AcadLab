package school.cesar.acadlab.dominio.secretariavirtual;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.List;
import school.cesar.acadlab.dominio.secretariavirtual.documento.Documento;
import school.cesar.acadlab.dominio.secretariavirtual.estudante.EstudanteId;
import school.cesar.acadlab.dominio.secretariavirtual.periodo.PeriodoLetivoId;
import school.cesar.acadlab.dominio.secretariavirtual.protocolo.Protocolo;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.SolicitacaoAcademica;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.SolicitacaoAcademicaId;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.SolicitacaoAcademicaRepositorio;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.TipoSolicitacao;

public class SolicitacaoServicoReal implements SolicitacaoServico {
    private final SolicitacaoAcademicaRepositorio repositorio;

    public SolicitacaoServicoReal(SolicitacaoAcademicaRepositorio repositorio) {
        notNull(repositorio, "O repositório não pode ser nulo");
        this.repositorio = repositorio;
    }

    @Override
    public SolicitacaoAcademica abrirSolicitacao(EstudanteId estudanteId, PeriodoLetivoId periodoLetivoId,
                                                  TipoSolicitacao tipo, String descricao,
                                                  List<Documento> documentos) {
        var id = repositorio.proximoId();
        var protocolo = new Protocolo(repositorio.proximoProtocoloId());
        var solicitacao = new SolicitacaoAcademica(id, estudanteId, periodoLetivoId, tipo, protocolo, descricao);

        if (documentos != null) {
            for (Documento doc : documentos) {
                solicitacao.anexarDocumento(doc);
            }
        }

        // RN3: valida documentos obrigatórios
        solicitacao.validarDocumentosObrigatorios();

        repositorio.salvar(solicitacao);
        return solicitacao;
    }

    @Override
    public void complementarSolicitacao(SolicitacaoAcademicaId id, Documento documento) {
        var solicitacao = repositorio.obter(id);
        solicitacao.complementar(documento);
        repositorio.salvar(solicitacao);
    }

    @Override
    public void cancelarSolicitacao(SolicitacaoAcademicaId id) {
        var solicitacao = repositorio.obter(id);
        solicitacao.cancelar();
        repositorio.salvar(solicitacao);
    }
}
