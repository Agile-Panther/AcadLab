package school.cesar.acadlab.dominio.historicoacademico;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import school.cesar.acadlab.dominio.historicoacademico.historico.*;

public class RepositorioEmMemoria implements HistoricoAcademicoRepositorio {
    private int proximoHistoricoSeq = 1;
    private int proximoRegistroSeq = 1;
    private int proximoAproveitamentoSeq = 1;
    private int proximoRetificacaoSeq = 1;
    private int proximoAcompanhamentoSeq = 1;
    private final Map<HistoricoAcademicoId, HistoricoAcademico> historicos = new HashMap<>();

    @Override
    public HistoricoAcademicoId proximoId() { return new HistoricoAcademicoId(proximoHistoricoSeq++); }

    @Override
    public RegistroDisciplinaId proximoRegistroId() { return new RegistroDisciplinaId(proximoRegistroSeq++); }

    @Override
    public AproveitamentoId proximoAproveitamentoId() { return new AproveitamentoId(proximoAproveitamentoSeq++); }

    @Override
    public RetificacaoId proximoRetificacaoId() { return new RetificacaoId(proximoRetificacaoSeq++); }

    @Override
    public AcompanhamentoId proximoAcompanhamentoId() { return new AcompanhamentoId(proximoAcompanhamentoSeq++); }

    @Override
    public void salvar(HistoricoAcademico historico) {
        notNull(historico, "O histórico não pode ser nulo");
        historicos.put(historico.getId(), historico);
    }

    @Override
    public HistoricoAcademico obter(HistoricoAcademicoId id) {
        notNull(id, "O id não pode ser nulo");
        return Optional.ofNullable(historicos.get(id))
                .orElseThrow(() -> new IllegalArgumentException("Histórico não encontrado: " + id));
    }

    @Override
    public Optional<HistoricoAcademico> buscarPorEstudante(EstudanteId estudanteId) {
        return historicos.values().stream()
                .filter(h -> h.getEstudanteId().equals(estudanteId))
                .findFirst();
    }
}
