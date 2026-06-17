package school.cesar.acadlab.dominio.integralizacao.integralizacao;

import static org.apache.commons.lang3.Validate.notNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import school.cesar.acadlab.dominio.integralizacao.CoordenadorId;
import school.cesar.acadlab.dominio.integralizacao.EstudanteId;
import school.cesar.acadlab.dominio.integralizacao.MatrizCurricularId;
import school.cesar.acadlab.dominio.integralizacao.checklist.ItemChecklist;

public class IntegralizacaoCurricular {
    private final IntegralizacaoId id;
    private final EstudanteId estudanteId;
    private final MatrizCurricularId matrizCurricularId;
    private final List<ItemChecklist> itensChecklist = new ArrayList<>();
    private StatusIntegralizacao status;
    private CoordenadorId aprovadorId;
    private LocalDate dataAprovacao;

    public IntegralizacaoCurricular(IntegralizacaoId id, EstudanteId estudanteId,
                                     MatrizCurricularId matrizCurricularId) {
        notNull(id, "O id não pode ser nulo");
        notNull(estudanteId, "O estudante não pode ser nulo");
        notNull(matrizCurricularId, "A matriz curricular não pode ser nula");
        this.id = id;
        this.estudanteId = estudanteId;
        this.matrizCurricularId = matrizCurricularId;
        this.status = StatusIntegralizacao.EM_ANALISE;
    }

    // US02 - RN3: checklist baseado em registros consolidados (dados fornecidos externamente)
    public ChecklistGeradoEvento gerarChecklist(List<ItemChecklist> itens) {
        notNull(itens, "Os itens do checklist não podem ser nulos");
        if (itens.isEmpty()) throw new IllegalArgumentException("O checklist deve ter ao menos um item");
        this.itensChecklist.clear();
        this.itensChecklist.addAll(itens);
        return new ChecklistGeradoEvento(this);
    }

    // US02 - RN4: resultado inapto exige ao menos uma pendência
    public ResultadoRegistradoEvento registrarResultado(StatusIntegralizacao resultado) {
        notNull(resultado, "O resultado não pode ser nulo");
        if (itensChecklist.isEmpty()) {
            throw new IllegalStateException("Checklist deve ser gerado antes do registro do resultado");
        }
        if (resultado == StatusIntegralizacao.INAPTO) {
            boolean temPendencia = itensChecklist.stream().anyMatch(i -> !i.isCumprido());
            if (!temPendencia) {
                throw new IllegalStateException("RN4: Resultado inapto requer ao menos uma pendência registrada");
            }
        }
        this.status = resultado;
        return new ResultadoRegistradoEvento(this);
    }

    // US03 - RN5: apenas coordenador aprova (perfil verificado externamente)
    // RN6: aptidão exige 100% obrigatórias + carga optativa + horas complementares (verificado externamente)
    public AptidaoAprovadaEvento aprovarAptidao(CoordenadorId aprovadorId) {
        notNull(aprovadorId, "O aprovador não pode ser nulo");
        if (status != StatusIntegralizacao.APTO) {
            throw new IllegalStateException("RN5: Apenas integralizações com resultado apto podem ser aprovadas");
        }
        this.aprovadorId = aprovadorId;
        this.dataAprovacao = LocalDate.now();
        return new AptidaoAprovadaEvento(this);
    }

    public boolean aptidaoAprovada() {
        return aprovadorId != null && dataAprovacao != null;
    }

    public static IntegralizacaoCurricular reconstituir(IntegralizacaoId id, EstudanteId estudanteId,
                                                        MatrizCurricularId matrizCurricularId,
                                                        StatusIntegralizacao status,
                                                        CoordenadorId aprovadorId,
                                                        LocalDate dataAprovacao,
                                                        List<ItemChecklist> itensChecklist) {
        var i = new IntegralizacaoCurricular(id, estudanteId, matrizCurricularId);
        i.status = status;
        i.aprovadorId = aprovadorId;
        i.dataAprovacao = dataAprovacao;
        if (itensChecklist != null) {
            i.itensChecklist.addAll(itensChecklist);
        }
        return i;
    }

    public IntegralizacaoId getId() { return id; }
    public EstudanteId getEstudanteId() { return estudanteId; }
    public MatrizCurricularId getMatrizCurricularId() { return matrizCurricularId; }
    public List<ItemChecklist> getItensChecklist() { return Collections.unmodifiableList(itensChecklist); }
    public StatusIntegralizacao getStatus() { return status; }
    public CoordenadorId getAprovadorId() { return aprovadorId; }
    public LocalDate getDataAprovacao() { return dataAprovacao; }

    public static abstract class IntegralizacaoEvento {
        private final IntegralizacaoCurricular integralizacao;
        protected IntegralizacaoEvento(IntegralizacaoCurricular i) { this.integralizacao = i; }
        public IntegralizacaoCurricular getIntegralizacao() { return integralizacao; }
    }

    public static class ChecklistGeradoEvento extends IntegralizacaoEvento {
        private ChecklistGeradoEvento(IntegralizacaoCurricular i) { super(i); }
    }

    public static class ResultadoRegistradoEvento extends IntegralizacaoEvento {
        private ResultadoRegistradoEvento(IntegralizacaoCurricular i) { super(i); }
    }

    public static class AptidaoAprovadaEvento extends IntegralizacaoEvento {
        private AptidaoAprovadaEvento(IntegralizacaoCurricular i) { super(i); }
    }
}
