package school.cesar.acadlab.dominio.atividadescomplementares.atividade;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;
import school.cesar.acadlab.dominio.atividadescomplementares.*;
import java.time.LocalDate;

public class AtividadeComplementar {
    private final AtividadeComplementarId id;
    private final EstudanteId estudanteId;
    private final CategoriaAtividadeId categoriaId;
    private final String identificadorCertificado;
    private final String descricao;
    private final int horasSubmetidas;
    private final LocalDate dataRealizacao;
    private int horasAprovadas;
    private StatusAtividade status;

    public AtividadeComplementar(AtividadeComplementarId id, EstudanteId estudanteId,
            CategoriaAtividadeId categoriaId, String identificadorCertificado,
            String descricao, int horasSubmetidas, LocalDate dataRealizacao) {
        notNull(id, "id obrigatório");
        notNull(estudanteId, "estudanteId obrigatório");
        notNull(categoriaId, "categoriaId obrigatório");
        notNull(identificadorCertificado, "identificadorCertificado obrigatório");
        isTrue(!identificadorCertificado.isBlank(), "identificadorCertificado não pode ser vazio");
        notNull(descricao, "descricao obrigatória");
        isTrue(horasSubmetidas > 0, "horasSubmetidas devem ser positivas");
        notNull(dataRealizacao, "dataRealizacao obrigatória");
        this.id = id;
        this.estudanteId = estudanteId;
        this.categoriaId = categoriaId;
        this.identificadorCertificado = identificadorCertificado;
        this.descricao = descricao;
        this.horasSubmetidas = horasSubmetidas;
        this.dataRealizacao = dataRealizacao;
        this.status = StatusAtividade.PENDENTE;
    }

    public DeferidaEvento deferir(int horasAprovadas) {
        if (status != StatusAtividade.PENDENTE && status != StatusAtividade.REVISAO_SOLICITADA)
            throw new IllegalStateException("Apenas atividades pendentes ou em revisão podem ser deferidas");
        isTrue(horasAprovadas > 0, "horasAprovadas devem ser positivas");
        isTrue(horasAprovadas <= horasSubmetidas, "horasAprovadas não podem exceder as submetidas");
        this.horasAprovadas = horasAprovadas;
        this.status = StatusAtividade.DEFERIDA;
        return new DeferidaEvento(this);
    }

    public IndeferidaEvento indeferir(String justificativa) {
        notNull(justificativa, "justificativa obrigatória");
        isTrue(!justificativa.isBlank(), "justificativa não pode ser vazia");
        if (status != StatusAtividade.PENDENTE && status != StatusAtividade.REVISAO_SOLICITADA)
            throw new IllegalStateException("Apenas atividades pendentes ou em revisão podem ser indeferidas");
        this.status = StatusAtividade.INDEFERIDA;
        return new IndeferidaEvento(this);
    }

    public RevisaoSolicitadaEvento solicitarRevisao(String justificativa) {
        notNull(justificativa, "justificativa obrigatória");
        isTrue(!justificativa.isBlank(), "justificativa não pode ser vazia");
        if (status != StatusAtividade.INDEFERIDA)
            throw new IllegalStateException("Revisão só pode ser solicitada para atividades indeferidas");
        this.status = StatusAtividade.REVISAO_SOLICITADA;
        return new RevisaoSolicitadaEvento(this);
    }

    public CanceladaEvento cancelar() {
        if (status != StatusAtividade.PENDENTE)
            throw new IllegalStateException("RN7: Cancelamento permitido apenas para atividades com status pendente de análise");
        this.status = StatusAtividade.CANCELADA;
        return new CanceladaEvento(this);
    }

    public AtividadeComplementarId getId() { return id; }
    public EstudanteId getEstudanteId() { return estudanteId; }
    public CategoriaAtividadeId getCategoriaId() { return categoriaId; }
    public String getIdentificadorCertificado() { return identificadorCertificado; }
    public String getDescricao() { return descricao; }
    public int getHorasSubmetidas() { return horasSubmetidas; }
    public int getHorasAprovadas() { return horasAprovadas; }
    public LocalDate getDataRealizacao() { return dataRealizacao; }
    public StatusAtividade getStatus() { return status; }

    public abstract static class AtividadeComplementarEvento {
        private final AtividadeComplementar atividade;
        protected AtividadeComplementarEvento(AtividadeComplementar atividade) { this.atividade = atividade; }
        public AtividadeComplementar getAtividade() { return atividade; }
    }
    public static class DeferidaEvento extends AtividadeComplementarEvento {
        public DeferidaEvento(AtividadeComplementar a) { super(a); }
    }
    public static class IndeferidaEvento extends AtividadeComplementarEvento {
        public IndeferidaEvento(AtividadeComplementar a) { super(a); }
    }
    public static class RevisaoSolicitadaEvento extends AtividadeComplementarEvento {
        public RevisaoSolicitadaEvento(AtividadeComplementar a) { super(a); }
    }
    public static class CanceladaEvento extends AtividadeComplementarEvento {
        public CanceladaEvento(AtividadeComplementar a) { super(a); }
    }
}
