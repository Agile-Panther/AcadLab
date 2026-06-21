package school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MobilidadeAcademica {

    private final MobilidadeAcademicaId id;
    private final EstudanteId estudanteId;
    private final String instituicaoDestino;
    private LocalDate dataInicioPeriodoExterno;
    private StatusMobilidade status;
    private final List<ItemPlanoEstudos> planoEstudos;
    private String justificativaCancelamento;
    private CoordenadorId coordenadorAutorizacao;

    public MobilidadeAcademica(MobilidadeAcademicaId id, EstudanteId estudanteId, String instituicaoDestino) {
        this.id = notNull(id, "id é obrigatório");
        this.estudanteId = notNull(estudanteId, "estudanteId é obrigatório");
        this.instituicaoDestino = notBlank(instituicaoDestino, "instituicaoDestino é obrigatório");
        this.status = StatusMobilidade.SOLICITADA;
        this.planoEstudos = new ArrayList<>();
    }

    public MobilidadeAutorizadaEvento autorizar(CoordenadorId coordenadorId) {
        if (status != StatusMobilidade.SOLICITADA) {
            throw new IllegalStateException("mobilidade já se encontra autorizada");
        }
        this.status = StatusMobilidade.AUTORIZADA;
        this.coordenadorAutorizacao = coordenadorId;
        return new MobilidadeAutorizadaEvento(this);
    }

    public PeriodoExternoIniciadoEvento iniciarPeriodoExterno(LocalDate dataInicio) {
        if (status != StatusMobilidade.AUTORIZADA) {
            throw new IllegalStateException("mobilidade deve estar autorizada para iniciar");
        }
        this.status = StatusMobilidade.EM_ANDAMENTO;
        this.dataInicioPeriodoExterno = dataInicio;
        return new PeriodoExternoIniciadoEvento(this);
    }

    public void adicionarItemPlano(DisciplinaId disciplinaExterna, DisciplinaId disciplinaEquivalente,
                                   int cargaHorariaExterna, int cargaHorariaEquivalente) {
        var item = new ItemPlanoEstudos(disciplinaExterna, disciplinaEquivalente,
                cargaHorariaExterna, cargaHorariaEquivalente);
        item.autorizar();
        planoEstudos.add(item);
    }

    public void registrarResultado(DisciplinaId disciplinaExterna, SecretariaId secretariaId) {
        if (secretariaId == null) {
            throw new IllegalStateException("o registro deve ser realizado pela secretaria");
        }
        var item = encontrarItemPorDisciplinaExterna(disciplinaExterna);
        if (item.getStatus() != StatusItemPlano.AUTORIZADO) {
            throw new IllegalStateException("apenas disciplinas do plano autorizado podem ter resultado registrado");
        }
        if (!item.isComprovanteAnexado()) {
            throw new IllegalStateException("comprovante de resultado é obrigatório");
        }
        item.registrarResultado();
        if (planoEstudos.stream().allMatch(ItemPlanoEstudos::isResultadoRegistrado)) {
            this.status = StatusMobilidade.CONCLUIDA;
        }
    }

    public void anexarComprovante(DisciplinaId disciplinaExterna) {
        var item = encontrarItemPorDisciplinaExterna(disciplinaExterna);
        item.anexarComprovante();
    }

    public void solicitarCancelamento(String justificativa, LocalDate hoje) {
        if (dataInicioPeriodoExterno != null && !hoje.isBefore(dataInicioPeriodoExterno)) {
            throw new IllegalStateException("mobilidade não pode ser cancelada após o início do período externo");
        }
        notBlank(justificativa, "justificativa de cancelamento é obrigatória");
        this.justificativaCancelamento = justificativa;
    }

    public MobilidadeCanceladaEvento confirmarCancelamento(CoordenadorId coordenadorId) {
        if (justificativaCancelamento == null) {
            throw new IllegalStateException("não há justificativa de cancelamento registrada");
        }
        this.status = StatusMobilidade.CANCELADA;
        return new MobilidadeCanceladaEvento(this);
    }

    public static MobilidadeAcademica reconstituir(MobilidadeAcademicaId id, EstudanteId estudanteId,
                                                    String instituicaoDestino, StatusMobilidade status,
                                                    CoordenadorId coordenadorAutorizacao,
                                                    LocalDate dataInicioPeriodoExterno,
                                                    String justificativaCancelamento,
                                                    List<ItemPlanoEstudos> planoEstudos) {
        var m = new MobilidadeAcademica(id, estudanteId, instituicaoDestino);
        m.status = status;
        m.coordenadorAutorizacao = coordenadorAutorizacao;
        m.dataInicioPeriodoExterno = dataInicioPeriodoExterno;
        m.justificativaCancelamento = justificativaCancelamento;
        m.planoEstudos.addAll(planoEstudos);
        return m;
    }

    private ItemPlanoEstudos encontrarItemPorDisciplinaExterna(DisciplinaId disciplinaExterna) {
        return planoEstudos.stream()
                .filter(i -> i.getDisciplinaExterna().equals(disciplinaExterna))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("disciplina não consta no plano de estudos autorizado"));
    }

    public MobilidadeAcademicaId getId() { return id; }
    public EstudanteId getEstudanteId() { return estudanteId; }
    public String getInstituicaoDestino() { return instituicaoDestino; }
    public LocalDate getDataInicioPeriodoExterno() { return dataInicioPeriodoExterno; }
    public StatusMobilidade getStatus() { return status; }
    public List<ItemPlanoEstudos> getPlanoEstudos() { return Collections.unmodifiableList(planoEstudos); }
    public String getJustificativaCancelamento() { return justificativaCancelamento; }
    public CoordenadorId getCoordenadorAutorizacao() { return coordenadorAutorizacao; }

    public static abstract class MobilidadeEvento {
        private final MobilidadeAcademica mobilidade;
        protected MobilidadeEvento(MobilidadeAcademica mobilidade) { this.mobilidade = mobilidade; }
        public MobilidadeAcademica getMobilidade() { return mobilidade; }
    }

    public static class MobilidadeAutorizadaEvento extends MobilidadeEvento {
        private MobilidadeAutorizadaEvento(MobilidadeAcademica mobilidade) { super(mobilidade); }
    }

    public static class PeriodoExternoIniciadoEvento extends MobilidadeEvento {
        private PeriodoExternoIniciadoEvento(MobilidadeAcademica mobilidade) { super(mobilidade); }
    }

    public static class MobilidadeCanceladaEvento extends MobilidadeEvento {
        private MobilidadeCanceladaEvento(MobilidadeAcademica mobilidade) { super(mobilidade); }
    }
}
