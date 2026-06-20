package school.cesar.acadlab.infraestrutura.persistencia.jpa;

import java.util.EnumSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import school.cesar.acadlab.dominio.integralizacao.ConsultaPendenciasPorta;
import school.cesar.acadlab.dominio.integralizacao.EstudanteId;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.StatusSolicitacao;

/**
 * Adaptador da porta de pendências da F-08 (RN2): pendências acadêmicas ou
 * documentais registradas impedem o início da análise. Considera-se pendência
 * qualquer solicitação acadêmica do estudante que ainda não esteja encerrada
 * (concluída, cancelada ou indeferida).
 */
@Component
class PendenciasIntegralizacaoAdapter implements ConsultaPendenciasPorta {

    private static final Set<StatusSolicitacao> ENCERRADAS = EnumSet.of(
            StatusSolicitacao.CONCLUIDA,
            StatusSolicitacao.CANCELADA,
            StatusSolicitacao.INDEFERIDA);

    private final SolicitacaoAcademicaJpaRepository solicitacaoRepository;

    PendenciasIntegralizacaoAdapter(SolicitacaoAcademicaJpaRepository solicitacaoRepository) {
        this.solicitacaoRepository = solicitacaoRepository;
    }

    @Override
    public boolean possuiPendencias(EstudanteId estudanteId) {
        return solicitacaoRepository.findByEstudanteId(estudanteId.getId()).stream()
                .anyMatch(s -> !ENCERRADAS.contains(s.status));
    }
}
