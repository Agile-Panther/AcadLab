package school.cesar.acadlab.infraestrutura.persistencia.jpa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import school.cesar.acadlab.aplicacao.historicoacademico.AcompanhamentoResumo;
import school.cesar.acadlab.aplicacao.historicoacademico.AproveitamentoResumo;
import school.cesar.acadlab.aplicacao.historicoacademico.EntradaAuditoriaResumo;
import school.cesar.acadlab.aplicacao.historicoacademico.HistoricoAcademicoRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.historicoacademico.HistoricoAcademicoResumo;
import school.cesar.acadlab.aplicacao.historicoacademico.RegistroDisciplinaResumo;
import school.cesar.acadlab.aplicacao.historicoacademico.RetificacaoResumo;
import school.cesar.acadlab.dominio.historicoacademico.historico.AcompanhamentoAcademico;
import school.cesar.acadlab.dominio.historicoacademico.historico.AcompanhamentoId;
import school.cesar.acadlab.dominio.historicoacademico.historico.Aproveitamento;
import school.cesar.acadlab.dominio.historicoacademico.historico.AproveitamentoId;
import school.cesar.acadlab.dominio.historicoacademico.historico.DisciplinaId;
import school.cesar.acadlab.dominio.historicoacademico.historico.EntradaAuditoria;
import school.cesar.acadlab.dominio.historicoacademico.historico.EstudanteId;
import school.cesar.acadlab.dominio.historicoacademico.historico.HistoricoAcademico;
import school.cesar.acadlab.dominio.historicoacademico.historico.HistoricoAcademicoId;
import school.cesar.acadlab.dominio.historicoacademico.historico.HistoricoAcademicoRepositorio;
import school.cesar.acadlab.dominio.historicoacademico.historico.MatrizCurricularId;
import school.cesar.acadlab.dominio.historicoacademico.historico.PeriodoLetivoId;
import school.cesar.acadlab.dominio.historicoacademico.historico.RegistroDisciplina;
import school.cesar.acadlab.dominio.historicoacademico.historico.RegistroDisciplinaId;
import school.cesar.acadlab.dominio.historicoacademico.historico.Retificacao;
import school.cesar.acadlab.dominio.historicoacademico.historico.RetificacaoId;
import school.cesar.acadlab.dominio.historicoacademico.historico.SecretariaId;
import school.cesar.acadlab.dominio.historicoacademico.historico.SituacaoAcademica;
import school.cesar.acadlab.dominio.historicoacademico.historico.SituacaoDiscente;
import school.cesar.acadlab.dominio.historicoacademico.historico.TurmaId;
import school.cesar.acadlab.dominio.historicoacademico.historico.ConsultaPeriodoEncerradoPorta;
import school.cesar.acadlab.dominio.periodoletivo.StatusPeriodoLetivo;

@Entity
@Table(name = "HISTORICO_ACADEMICO")
class HistoricoAcademicoJpa {
    @Id
    int id;
    int estudanteId;
    int matrizCurricularId;

    @Enumerated(EnumType.STRING)
    SituacaoDiscente situacaoDiscente;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "REGISTRO_DISCIPLINA", joinColumns = @JoinColumn(name = "historicoId"))
    List<RegistroDisciplinaJpa> registros = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "APROVEITAMENTO_HISTORICO", joinColumns = @JoinColumn(name = "historicoId"))
    List<AproveitamentoJpa> aproveitamentos = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "RETIFICACAO_HISTORICO", joinColumns = @JoinColumn(name = "historicoId"))
    List<RetificacaoJpa> retificacoes = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ACOMPANHAMENTO_ACADEMICO", joinColumns = @JoinColumn(name = "historicoId"))
    List<AcompanhamentoAcademicoJpa> acompanhamentos = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ENTRADA_AUDITORIA_HISTORICO", joinColumns = @JoinColumn(name = "historicoId"))
    List<EntradaAuditoriaJpa> trilhaAuditoria = new ArrayList<>();
}

@Embeddable
class RegistroDisciplinaJpa {
    int registroId;
    int disciplinaId;
    int turmaId;
    int periodoLetivoId;
    double nota;
    double frequencia;
    @Enumerated(EnumType.STRING)
    @Column(name = "situacaoAcademica")
    SituacaoAcademica situacao;
}

@Embeddable
class AproveitamentoJpa {
    int aproveitamentoId;
    int disciplinaEquivalenteId;
    int cargaHorariaExterna;
    int cargaHorariaRequerida;
    String instituicaoOrigem;
    String disciplinaOrigem;
}

@Embeddable
class RetificacaoJpa {
    int retificacaoId;
    int registroId;
    @Enumerated(EnumType.STRING)
    @Column(name = "situacaoAnterior")
    SituacaoAcademica situacaoAnterior;
    @Enumerated(EnumType.STRING)
    @Column(name = "novaSituacao")
    SituacaoAcademica novaSituacao;
    int responsavelId;
    String justificativa;
    LocalDate data;
}

@Embeddable
class AcompanhamentoAcademicoJpa {
    int acompanhamentoId;
    String observacao;
    LocalDate data;
}

@Embeddable
class EntradaAuditoriaJpa {
    @Enumerated(EnumType.STRING)
    @Column(name = "situacaoDiscenteAnterior")
    SituacaoDiscente situacaoAnterior;
    @Enumerated(EnumType.STRING)
    @Column(name = "novaSituacaoDiscente")
    SituacaoDiscente novaSituacao;
    int responsavelId;
    String justificativa;
    LocalDate data;
}

interface HistoricoAcademicoJpaRepository extends JpaRepository<HistoricoAcademicoJpa, Integer> {
    Optional<HistoricoAcademicoJpa> findByEstudanteId(int estudanteId);

    @Query("SELECT COALESCE(MAX(h.id), 0) + 1 FROM HistoricoAcademicoJpa h")
    int proximoHistoricoId();

    @Query("SELECT COALESCE((SELECT MAX(r.registroId) FROM HistoricoAcademicoJpa h JOIN h.registros r), 0) + 1")
    int proximoRegistroId();

    @Query("SELECT COALESCE((SELECT MAX(a.aproveitamentoId) FROM HistoricoAcademicoJpa h JOIN h.aproveitamentos a), 0) + 1")
    int proximoAproveitamentoId();

    @Query("SELECT COALESCE((SELECT MAX(r.retificacaoId) FROM HistoricoAcademicoJpa h JOIN h.retificacoes r), 0) + 1")
    int proximoRetificacaoId();

    @Query("SELECT COALESCE((SELECT MAX(a.acompanhamentoId) FROM HistoricoAcademicoJpa h JOIN h.acompanhamentos a), 0) + 1")
    int proximoAcompanhamentoId();
}

@Repository
class HistoricoAcademicoRepositorioImpl implements HistoricoAcademicoRepositorio,
        HistoricoAcademicoRepositorioAplicacao {

    @Autowired
    HistoricoAcademicoJpaRepository repository;

    @Override
    public HistoricoAcademicoId proximoId() {
        return new HistoricoAcademicoId(repository.proximoHistoricoId());
    }

    @Override
    public RegistroDisciplinaId proximoRegistroId() {
        return new RegistroDisciplinaId(repository.proximoRegistroId());
    }

    @Override
    public AproveitamentoId proximoAproveitamentoId() {
        return new AproveitamentoId(repository.proximoAproveitamentoId());
    }

    @Override
    public RetificacaoId proximoRetificacaoId() {
        return new RetificacaoId(repository.proximoRetificacaoId());
    }

    @Override
    public AcompanhamentoId proximoAcompanhamentoId() {
        return new AcompanhamentoId(repository.proximoAcompanhamentoId());
    }

    @Override
    @Transactional
    public void salvar(HistoricoAcademico historico) {
        repository.save(toJpa(historico));
    }

    @Override
    public HistoricoAcademico obter(HistoricoAcademicoId id) {
        return repository.findById(id.getId())
                .map(this::toDomain)
                .orElseThrow(() -> new IllegalArgumentException("Histórico não encontrado: " + id));
    }

    @Override
    public java.util.Optional<HistoricoAcademico> buscarPorEstudante(EstudanteId estudanteId) {
        return repository.findByEstudanteId(estudanteId.getId()).map(this::toDomain);
    }

    @Override
    public Optional<HistoricoAcademicoResumo> buscarPorId(int id) {
        return repository.findById(id).map(this::toResumo);
    }

    @Override
    public Optional<HistoricoAcademicoResumo> buscarPorEstudante(int estudanteId) {
        return repository.findByEstudanteId(estudanteId).map(this::toResumo);
    }

    @Override
    public List<HistoricoAcademicoResumo> buscarTodos() {
        return repository.findAll().stream().map(this::toResumo).toList();
    }

    private HistoricoAcademicoJpa toJpa(HistoricoAcademico h) {
        var jpa = repository.findById(h.getId().getId()).orElseGet(HistoricoAcademicoJpa::new);
        jpa.id = h.getId().getId();
        jpa.estudanteId = h.getEstudanteId().getId();
        jpa.matrizCurricularId = h.getMatrizCurricularId().getId();
        jpa.situacaoDiscente = h.getSituacaoDiscente();

        jpa.registros.clear();
        for (var r : h.getRegistros()) {
            var rj = new RegistroDisciplinaJpa();
            rj.registroId = r.getId().getId();
            rj.disciplinaId = r.getDisciplinaId().getId();
            rj.turmaId = r.getTurmaId().getId();
            rj.periodoLetivoId = r.getPeriodoLetivoId().getId();
            rj.nota = r.getNota();
            rj.frequencia = r.getFrequencia();
            rj.situacao = r.getSituacao();
            jpa.registros.add(rj);
        }

        jpa.aproveitamentos.clear();
        for (var a : h.getAproveitamentos()) {
            var aj = new AproveitamentoJpa();
            aj.aproveitamentoId = a.getId().getId();
            aj.disciplinaEquivalenteId = a.getDisciplinaEquivalente().getId();
            aj.cargaHorariaExterna = a.getCargaHorariaExterna();
            aj.cargaHorariaRequerida = a.getCargaHorariaRequerida();
            aj.instituicaoOrigem = a.getInstituicaoOrigem();
            aj.disciplinaOrigem = a.getDisciplinaOrigem();
            jpa.aproveitamentos.add(aj);
        }

        jpa.retificacoes.clear();
        for (var r : h.getRetificacoes()) {
            var rj = new RetificacaoJpa();
            rj.retificacaoId = r.getId().getId();
            rj.registroId = r.getRegistroId().getId();
            rj.situacaoAnterior = r.getSituacaoAnterior();
            rj.novaSituacao = r.getNovaSituacao();
            rj.responsavelId = r.getResponsavel().getId();
            rj.justificativa = r.getJustificativa();
            rj.data = r.getData();
            jpa.retificacoes.add(rj);
        }

        jpa.acompanhamentos.clear();
        for (var a : h.getAcompanhamentos()) {
            var aj = new AcompanhamentoAcademicoJpa();
            aj.acompanhamentoId = a.getId().getId();
            aj.observacao = a.getObservacao();
            aj.data = a.getData();
            jpa.acompanhamentos.add(aj);
        }

        jpa.trilhaAuditoria.clear();
        for (var e : h.getTrilhaAuditoria()) {
            var ej = new EntradaAuditoriaJpa();
            ej.situacaoAnterior = e.getSituacaoAnterior();
            ej.novaSituacao = e.getNovaSituacao();
            ej.responsavelId = e.getResponsavel().getId();
            ej.justificativa = e.getJustificativa();
            ej.data = e.getData();
            jpa.trilhaAuditoria.add(ej);
        }

        return jpa;
    }

    private HistoricoAcademico toDomain(HistoricoAcademicoJpa jpa) {
        var registros = jpa.registros.stream()
                .map(r -> new RegistroDisciplina(
                        new RegistroDisciplinaId(r.registroId),
                        new DisciplinaId(r.disciplinaId),
                        new TurmaId(r.turmaId),
                        new PeriodoLetivoId(r.periodoLetivoId),
                        r.nota, r.frequencia, r.situacao))
                .toList();

        var aproveitamentos = jpa.aproveitamentos.stream()
                .map(a -> new Aproveitamento(
                        new AproveitamentoId(a.aproveitamentoId),
                        new DisciplinaId(a.disciplinaEquivalenteId),
                        a.cargaHorariaExterna, a.cargaHorariaRequerida,
                        a.instituicaoOrigem, a.disciplinaOrigem))
                .toList();

        var retificacoes = jpa.retificacoes.stream()
                .map(r -> new Retificacao(
                        new RetificacaoId(r.retificacaoId),
                        new RegistroDisciplinaId(r.registroId),
                        r.situacaoAnterior, r.novaSituacao,
                        new SecretariaId(r.responsavelId),
                        r.justificativa, r.data))
                .toList();

        var acompanhamentos = jpa.acompanhamentos.stream()
                .map(a -> new AcompanhamentoAcademico(
                        new AcompanhamentoId(a.acompanhamentoId),
                        a.observacao, a.data))
                .toList();

        var trilhaAuditoria = jpa.trilhaAuditoria.stream()
                .map(e -> new EntradaAuditoria(
                        e.situacaoAnterior, e.novaSituacao,
                        new SecretariaId(e.responsavelId),
                        e.justificativa, e.data))
                .toList();

        return HistoricoAcademico.reconstituir(
                new HistoricoAcademicoId(jpa.id),
                new EstudanteId(jpa.estudanteId),
                new MatrizCurricularId(jpa.matrizCurricularId),
                jpa.situacaoDiscente,
                registros, aproveitamentos, retificacoes,
                acompanhamentos, trilhaAuditoria);
    }

    private HistoricoAcademicoResumo toResumo(HistoricoAcademicoJpa jpa) {
        var registros = jpa.registros.stream()
                .map(r -> new RegistroDisciplinaResumo(
                        r.registroId, r.disciplinaId, r.turmaId, r.periodoLetivoId,
                        r.nota, r.frequencia, r.situacao.name()))
                .toList();

        var aproveitamentos = jpa.aproveitamentos.stream()
                .map(a -> new AproveitamentoResumo(
                        a.aproveitamentoId, a.disciplinaEquivalenteId,
                        a.cargaHorariaExterna, a.cargaHorariaRequerida,
                        a.instituicaoOrigem, a.disciplinaOrigem))
                .toList();

        var acompanhamentos = jpa.acompanhamentos.stream()
                .map(a -> new AcompanhamentoResumo(a.acompanhamentoId, a.observacao, a.data))
                .toList();

        var retificacoes = jpa.retificacoes.stream()
                .map(r -> new RetificacaoResumo(
                        r.retificacaoId, r.registroId,
                        r.situacaoAnterior.name(), r.novaSituacao.name(),
                        r.responsavelId, r.justificativa, r.data))
                .toList();

        var trilhaAuditoria = jpa.trilhaAuditoria.stream()
                .map(e -> new EntradaAuditoriaResumo(
                        e.situacaoAnterior.name(), e.novaSituacao.name(),
                        e.responsavelId, e.justificativa, e.data))
                .toList();

        return new HistoricoAcademicoResumo(
                jpa.id, jpa.estudanteId, jpa.matrizCurricularId,
                jpa.situacaoDiscente.name(),
                registros, aproveitamentos, acompanhamentos,
                retificacoes, trilhaAuditoria);
    }
}

// RN-10: consulta somente-leitura ao status do Período Letivo (F-02), sem alterar
// aquela feature (reutiliza a entidade PeriodoLetivoJpa do mesmo pacote).
interface PeriodoEncerradoConsultaRepository extends JpaRepository<PeriodoLetivoJpa, Integer> {
    boolean existsByIdAndStatus(int id, StatusPeriodoLetivo status);
}

@Repository
class ConsultaPeriodoEncerradoAdapter implements ConsultaPeriodoEncerradoPorta {

    @Autowired
    PeriodoEncerradoConsultaRepository repository;

    @Override
    public boolean estaEncerrado(PeriodoLetivoId periodoLetivoId) {
        return repository.existsByIdAndStatus(periodoLetivoId.getId(), StatusPeriodoLetivo.ENCERRADO);
    }
}
