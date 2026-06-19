package school.cesar.acadlab.dominio.periodoletivo.periodo;

import static org.apache.commons.lang3.Validate.notNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import school.cesar.acadlab.dominio.periodoletivo.PeriodoLetivoId;
import school.cesar.acadlab.dominio.periodoletivo.StatusPeriodoLetivo;
import school.cesar.acadlab.dominio.periodoletivo.TipoJanela;
import school.cesar.acadlab.dominio.periodoletivo.curso.CursoId;
import school.cesar.acadlab.dominio.periodoletivo.janelaacademica.JanelaAcademica;

public class PeriodoLetivo {
    private final PeriodoLetivoId id;
    private final CursoId cursoId;
    private final int ano;
    private final int semestre;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private StatusPeriodoLetivo status;
    private final List<JanelaAcademica> janelas = new ArrayList<>();

    public PeriodoLetivo(PeriodoLetivoId id, CursoId cursoId, int ano, int semestre,
                         LocalDate dataInicio, LocalDate dataFim) {
        notNull(id, "O id não pode ser nulo");
        notNull(cursoId, "O curso não pode ser nulo");
        notNull(dataInicio, "A data de início não pode ser nula");
        notNull(dataFim, "A data de fim não pode ser nula");
        if (!dataFim.isAfter(dataInicio)) {
            throw new IllegalArgumentException("A data de fim deve ser posterior ao início");
        }
        this.id = id;
        this.cursoId = cursoId;
        this.ano = ano;
        this.semestre = semestre;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = StatusPeriodoLetivo.NAO_INICIADO;
    }

    // US02 - RN2: operações vinculadas a janelas acadêmicas ativas
    public JanelaDefinidaEvento definirJanela(TipoJanela tipo, LocalDate inicio, LocalDate fim) {
        notNull(tipo, "O tipo de janela não pode ser nulo");
        if (status != StatusPeriodoLetivo.NAO_INICIADO) {
            throw new IllegalStateException("Janelas só podem ser definidas em períodos não iniciados");
        }
        var janela = new JanelaAcademica(tipo, inicio, fim);
        janelas.removeIf(j -> j.getTipo() == tipo);
        janelas.add(janela);
        return new JanelaDefinidaEvento(this, janela);
    }

    public boolean janelaAtiva(TipoJanela tipo, LocalDate data) {
        return janelas.stream()
                .filter(j -> j.getTipo() == tipo)
                .anyMatch(j -> j.estaAtiva(data));
    }

    public Optional<JanelaAcademica> buscarJanela(TipoJanela tipo) {
        return janelas.stream().filter(j -> j.getTipo() == tipo).findFirst();
    }

    // US05 - RN4: edição restrita a períodos não iniciados
    public PeriodoLetivoEditadoEvento editar(LocalDate novaDataInicio, LocalDate novaDataFim) {
        notNull(novaDataInicio, "A nova data de início não pode ser nula");
        notNull(novaDataFim, "A nova data de fim não pode ser nula");
        if (status == StatusPeriodoLetivo.ENCERRADO) {
            throw new IllegalStateException("período letivo encerrado não pode ser editado");
        }
        if (status == StatusPeriodoLetivo.EM_ANDAMENTO) {
            throw new IllegalStateException("período letivo em andamento não pode ser editado");
        }
        if (status != StatusPeriodoLetivo.NAO_INICIADO) {
            throw new IllegalStateException("período letivo não pode ser editado no status atual");
        }
        if (!novaDataFim.isAfter(novaDataInicio)) {
            throw new IllegalArgumentException("A data de fim deve ser posterior ao início");
        }
        this.dataInicio = novaDataInicio;
        this.dataFim = novaDataFim;
        return new PeriodoLetivoEditadoEvento(this);
    }

    // US-iniciar: transição NAO_INICIADO → EM_ANDAMENTO
    public PeriodoLetivoIniciadoEvento iniciar() {
        if (status != StatusPeriodoLetivo.NAO_INICIADO) {
            throw new IllegalStateException("Apenas períodos não iniciados podem ser iniciados");
        }
        this.status = StatusPeriodoLetivo.EM_ANDAMENTO;
        return new PeriodoLetivoIniciadoEvento(this);
    }

    // US06 - RN5: cancelamento restrito a períodos não iniciados sem matrículas (verificação externa)
    public PeriodoLetivoCanceladoEvento cancelar() {
        if (status == StatusPeriodoLetivo.ENCERRADO) {
            throw new IllegalStateException("período letivo encerrado não pode ser cancelado");
        }
        if (status == StatusPeriodoLetivo.EM_ANDAMENTO) {
            throw new IllegalStateException("período letivo em andamento não pode ser cancelado");
        }
        if (status != StatusPeriodoLetivo.NAO_INICIADO) {
            throw new IllegalStateException("período letivo não pode ser cancelado no status atual");
        }
        this.status = StatusPeriodoLetivo.CANCELADO;
        return new PeriodoLetivoCanceladoEvento(this);
    }

    // US03 - RN3: ausência de pendências verificada externamente antes da chamada
    public PeriodoLetivoEncerradoEvento encerrar() {
        if (status == StatusPeriodoLetivo.ENCERRADO) {
            throw new IllegalStateException("período letivo já está encerrado");
        }
        if (status == StatusPeriodoLetivo.CANCELADO) {
            throw new IllegalStateException("período letivo cancelado não pode ser encerrado");
        }
        this.status = StatusPeriodoLetivo.ENCERRADO;
        return new PeriodoLetivoEncerradoEvento(this);
    }

    public static PeriodoLetivo reconstituir(PeriodoLetivoId id, CursoId cursoId, int ano, int semestre,
                                             LocalDate dataInicio, LocalDate dataFim,
                                             StatusPeriodoLetivo status, List<JanelaAcademica> janelas) {
        var periodo = new PeriodoLetivo(id, cursoId, ano, semestre, dataInicio, dataFim);
        periodo.status = status;
        periodo.janelas.addAll(janelas);
        return periodo;
    }

    public PeriodoLetivoId getId() { return id; }
    public CursoId getCursoId() { return cursoId; }
    public int getAno() { return ano; }
    public int getSemestre() { return semestre; }
    public LocalDate getDataInicio() { return dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public StatusPeriodoLetivo getStatus() { return status; }
    public List<JanelaAcademica> getJanelas() { return Collections.unmodifiableList(janelas); }

    public static abstract class PeriodoLetivoEvento {
        private final PeriodoLetivo periodo;
        protected PeriodoLetivoEvento(PeriodoLetivo periodo) { this.periodo = periodo; }
        public PeriodoLetivo getPeriodo() { return periodo; }
    }

    public static class JanelaDefinidaEvento extends PeriodoLetivoEvento {
        private final JanelaAcademica janela;
        private JanelaDefinidaEvento(PeriodoLetivo periodo, JanelaAcademica janela) {
            super(periodo);
            this.janela = janela;
        }
        public JanelaAcademica getJanela() { return janela; }
    }

    public static class PeriodoLetivoEncerradoEvento extends PeriodoLetivoEvento {
        private PeriodoLetivoEncerradoEvento(PeriodoLetivo periodo) { super(periodo); }
    }

    public static class PeriodoLetivoIniciadoEvento extends PeriodoLetivoEvento {
        private PeriodoLetivoIniciadoEvento(PeriodoLetivo periodo) { super(periodo); }
    }

    public static class PeriodoLetivoCanceladoEvento extends PeriodoLetivoEvento {
        private PeriodoLetivoCanceladoEvento(PeriodoLetivo periodo) { super(periodo); }
    }

    public static class PeriodoLetivoEditadoEvento extends PeriodoLetivoEvento {
        private PeriodoLetivoEditadoEvento(PeriodoLetivo periodo) { super(periodo); }
    }
}
