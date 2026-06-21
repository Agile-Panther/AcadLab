package school.cesar.acadlab.infraestrutura.persistencia.jpa;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import school.cesar.acadlab.aplicacao.apoiopsicopedagogico.AgendamentoResumo;
import school.cesar.acadlab.aplicacao.apoiopsicopedagogico.ApoioPsicopedagogicoRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.apoiopsicopedagogico.AtendimentoResumo;
import school.cesar.acadlab.aplicacao.apoiopsicopedagogico.CasoResumo;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.acaopermanencia.AcaoPermanencia;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.agendamento.Agendamento;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.agendamento.StatusAgendamento;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.acaopermanencia.AcaoPermanenciaId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.acaopermanencia.AcaoPermanenciaRepositorio;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.atendimento.Atendimento;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.Caso;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.CasoId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.CasoRepositorio;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.StatusCaso;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.estudante.EstudanteId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.profissional.CoordenadorId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.profissional.PsicopedagogoId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.solicitacao.SolicitacaoApoio;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.solicitacao.SolicitacaoApoioId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.solicitacao.SolicitacaoApoioRepositorio;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.triagem.PrioridadeTriagem;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.triagem.Triagem;

/* ===== JPA Entities ===== */

@Entity
@Table(name = "CASO_PSICOPEDAGOGICO")
class CasoJpa {
    @Id int id;
    int estudanteId;
    Integer responsavelId;

    @Enumerated(EnumType.STRING)
    StatusCaso status;

    // triagem embutida
    String triagemPrioridade;
    String triagemObservacoes;
    Integer triagemResponsavelId;
    LocalDate triagemData;

    // agendamento embutido
    LocalDateTime agendamentoDataHora;
    String agendamentoStatus;
    String agendamentoJustificativa;
    LocalDateTime agendamentoHorarioSugerido;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ATENDIMENTO_CASO", joinColumns = @JoinColumn(name = "casoId"))
    List<AtendimentoJpa> atendimentos = new ArrayList<>();
}

@Embeddable
class AtendimentoJpa {
    @Column(name = "observacoes") String observacoes;
    @Column(name = "encaminhamento") String encaminhamento;
    @Column(name = "conclusaoFinal") boolean conclusaoFinal;
    @Column(name = "data") LocalDate data;
}

@Entity
@Table(name = "SOLICITACAO_APOIO")
class SolicitacaoApoioJpa {
    @Id int id;
    int estudanteId;
    String descricao;
    LocalDate dataSolicitacao;
}

@Entity
@Table(name = "ACAO_PERMANENCIA")
class AcaoPermanenciaJpa {
    @Id int id;
    int coordenadorId;
    String descricao;
    String indicadoresAgregados;
    LocalDate data;
}

/* ===== JPA Repositories ===== */

interface CasoJpaRepository extends JpaRepository<CasoJpa, Integer> {
    List<CasoJpa> findByEstudanteIdAndStatusIn(int estudanteId, List<StatusCaso> statuses);
    List<CasoJpa> findByEstudanteIdAndStatus(int estudanteId, StatusCaso status);
    List<CasoJpa> findByEstudanteId(int estudanteId);
    List<CasoJpa> findByResponsavelId(int responsavelId);
    List<CasoJpa> findByStatus(StatusCaso status);

    @Query("SELECT COALESCE(MAX(c.id), 0) + 1 FROM CasoJpa c")
    int proximoId();
}

interface SolicitacaoApoioJpaRepository extends JpaRepository<SolicitacaoApoioJpa, Integer> {
    Optional<SolicitacaoApoioJpa> findFirstByEstudanteIdOrderByIdDesc(int estudanteId);

    @Query("SELECT COALESCE(MAX(s.id), 0) + 1 FROM SolicitacaoApoioJpa s")
    int proximoId();
}

interface AcaoPermanenciaJpaRepository extends JpaRepository<AcaoPermanenciaJpa, Integer> {
    @Query("SELECT COALESCE(MAX(a.id), 0) + 1 FROM AcaoPermanenciaJpa a")
    int proximoId();
}

/* ===== Repository Implementations ===== */

@Repository
class ApoioPsicopedagogicoRepositorioImpl
        implements CasoRepositorio, SolicitacaoApoioRepositorio, AcaoPermanenciaRepositorio,
                   ApoioPsicopedagogicoRepositorioAplicacao {

    @Autowired CasoJpaRepository casoRepo;
    @Autowired SolicitacaoApoioJpaRepository solicitacaoRepo;
    @Autowired AcaoPermanenciaJpaRepository acaoRepo;

    /* --- CasoRepositorio --- */

    @Override
    public CasoId proximoId() { return new CasoId(casoRepo.proximoId()); }

    @Override
    @Transactional
    public void salvar(Caso caso) { casoRepo.save(toJpa(caso)); }

    @Override
    public Caso obter(CasoId id) {
        return casoRepo.findById(id.getId())
                .map(this::toDomain)
                .orElseThrow(() -> new IllegalArgumentException("Caso não encontrado: " + id));
    }

    @Override
    public Optional<Caso> pesquisarCasoAbertoPorEstudante(EstudanteId estudanteId) {
        return casoRepo.findByEstudanteIdAndStatusIn(estudanteId.getId(),
                List.of(StatusCaso.ABERTO, StatusCaso.EM_ATENDIMENTO))
                .stream().findFirst().map(this::toDomain);
    }

    @Override
    public Optional<Caso> pesquisarUltimoCasoEncerradoPorEstudante(EstudanteId estudanteId) {
        return casoRepo.findByEstudanteIdAndStatus(estudanteId.getId(), StatusCaso.ENCERRADO)
                .stream().findFirst().map(this::toDomain);
    }

    @Override
    public List<Caso> pesquisarPorResponsavel(PsicopedagogoId responsavelId) {
        return casoRepo.findByResponsavelId(responsavelId.getId())
                .stream().map(this::toDomain).toList();
    }

    /* --- SolicitacaoApoioRepositorio --- */

    @Override
    public SolicitacaoApoioId proximaSolicitacaoId() {
        return new SolicitacaoApoioId(solicitacaoRepo.proximoId());
    }

    @Override
    @Transactional
    public void salvar(SolicitacaoApoio solicitacao) { solicitacaoRepo.save(toJpa(solicitacao)); }

    @Override
    public SolicitacaoApoio obter(SolicitacaoApoioId id) {
        return solicitacaoRepo.findById(id.getId())
                .map(this::toDomain)
                .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada: " + id));
    }

    /* --- AcaoPermanenciaRepositorio --- */

    @Override
    public AcaoPermanenciaId proximaAcaoId() {
        return new AcaoPermanenciaId(acaoRepo.proximoId());
    }

    @Override
    @Transactional
    public void salvar(AcaoPermanencia acao) { acaoRepo.save(toJpa(acao)); }

    @Override
    public AcaoPermanencia obter(AcaoPermanenciaId id) {
        return acaoRepo.findById(id.getId())
                .map(this::toDomain)
                .orElseThrow(() -> new IllegalArgumentException("Ação não encontrada: " + id));
    }

    /* --- ApoioPsicopedagogicoRepositorioAplicacao --- */

    @Override
    public Optional<CasoResumo> buscarCasoPorId(int id) {
        return casoRepo.findById(id).map(this::toResumo);
    }

    @Override
    public List<CasoResumo> buscarCasosPorResponsavel(int responsavelId) {
        return casoRepo.findByResponsavelId(responsavelId).stream().map(this::toResumo).toList();
    }

    @Override
    public List<CasoResumo> buscarCasosPorEstudante(int estudanteId) {
        return casoRepo.findByEstudanteId(estudanteId).stream().map(this::toResumo).toList();
    }

    @Override
    public List<CasoResumo> buscarCasosAbertos() {
        return casoRepo.findByStatus(StatusCaso.ABERTO).stream().map(this::toResumo).toList();
    }

    @Override
    public Optional<CasoResumo> buscarCasoAtivoPorEstudante(int estudanteId) {
        return casoRepo.findByEstudanteIdAndStatusIn(estudanteId,
                List.of(StatusCaso.ABERTO, StatusCaso.EM_ATENDIMENTO))
                .stream().findFirst().map(this::toResumo);
    }

    /* --- Conversões --- */

    private CasoJpa toJpa(Caso c) {
        var jpa = casoRepo.findById(c.getId().getId()).orElseGet(CasoJpa::new);
        jpa.id = c.getId().getId();
        jpa.estudanteId = c.getEstudanteId().getId();
        jpa.responsavelId = c.getResponsavelId() != null ? c.getResponsavelId().getId() : null;
        jpa.status = c.getStatus();

        if (c.getTriagem() != null) {
            jpa.triagemPrioridade = c.getTriagem().getPrioridade().name();
            jpa.triagemObservacoes = c.getTriagem().getObservacoes();
            jpa.triagemResponsavelId = c.getTriagem().getResponsavelId().getId();
            jpa.triagemData = c.getTriagem().getData();
        } else {
            jpa.triagemPrioridade = null;
            jpa.triagemObservacoes = null;
            jpa.triagemResponsavelId = null;
            jpa.triagemData = null;
        }

        if (c.getAgendamento() != null) {
            var ag = c.getAgendamento();
            jpa.agendamentoDataHora = ag.getDataHora();
            jpa.agendamentoStatus = ag.getStatus().name();
            jpa.agendamentoJustificativa = ag.getJustificativaContestacao();
            jpa.agendamentoHorarioSugerido = ag.getHorarioSugerido();
        } else {
            jpa.agendamentoDataHora = null;
            jpa.agendamentoStatus = null;
            jpa.agendamentoJustificativa = null;
            jpa.agendamentoHorarioSugerido = null;
        }

        jpa.atendimentos.clear();
        for (var a : c.getAtendimentos()) {
            var aj = new AtendimentoJpa();
            aj.observacoes = a.getObservacoes();
            aj.encaminhamento = a.getEncaminhamento();
            aj.conclusaoFinal = a.isConclusaoFinal();
            aj.data = a.getData();
            jpa.atendimentos.add(aj);
        }
        return jpa;
    }

    private Caso toDomain(CasoJpa jpa) {
        Triagem triagem = null;
        if (jpa.triagemPrioridade != null) {
            triagem = new Triagem(
                    PrioridadeTriagem.valueOf(jpa.triagemPrioridade),
                    jpa.triagemObservacoes,
                    new PsicopedagogoId(jpa.triagemResponsavelId),
                    jpa.triagemData);
        }

        Agendamento agendamento = null;
        if (jpa.agendamentoDataHora != null) {
            agendamento = new Agendamento(
                    jpa.agendamentoDataHora,
                    StatusAgendamento.valueOf(jpa.agendamentoStatus),
                    jpa.agendamentoJustificativa,
                    jpa.agendamentoHorarioSugerido);
        }

        List<Atendimento> atendimentos = jpa.atendimentos.stream()
                .map(a -> new Atendimento(a.observacoes, a.encaminhamento, a.conclusaoFinal, a.data))
                .toList();

        return new Caso(new CasoId(jpa.id), new EstudanteId(jpa.estudanteId),
                jpa.responsavelId != null ? new PsicopedagogoId(jpa.responsavelId) : null,
                jpa.status, triagem, agendamento, atendimentos);
    }

    private CasoResumo toResumo(CasoJpa jpa) {
        var solicitacao = solicitacaoRepo.findFirstByEstudanteIdOrderByIdDesc(jpa.estudanteId);
        String motivo = solicitacao.map(s -> s.descricao).orElse(null);
        LocalDate abertura = solicitacao.map(s -> s.dataSolicitacao).orElse(null);

        List<AtendimentoResumo> atendimentos = jpa.atendimentos.stream()
                .map(a -> new AtendimentoResumo(a.observacoes, a.encaminhamento, a.conclusaoFinal, a.data))
                .toList();

        AgendamentoResumo agendamento = null;
        if (jpa.agendamentoDataHora != null) {
            agendamento = new AgendamentoResumo(jpa.agendamentoDataHora, jpa.agendamentoStatus,
                    jpa.agendamentoJustificativa, jpa.agendamentoHorarioSugerido);
        }

        return new CasoResumo(jpa.id, jpa.estudanteId, jpa.responsavelId, jpa.status.name(),
                motivo, abertura, jpa.triagemPrioridade, jpa.triagemObservacoes, agendamento, atendimentos);
    }

    private SolicitacaoApoioJpa toJpa(SolicitacaoApoio s) {
        var jpa = solicitacaoRepo.findById(s.getId().getId()).orElseGet(SolicitacaoApoioJpa::new);
        jpa.id = s.getId().getId();
        jpa.estudanteId = s.getEstudanteId().getId();
        jpa.descricao = s.getDescricao();
        jpa.dataSolicitacao = s.getDataSolicitacao();
        return jpa;
    }

    private SolicitacaoApoio toDomain(SolicitacaoApoioJpa jpa) {
        return new SolicitacaoApoio(new SolicitacaoApoioId(jpa.id),
                new EstudanteId(jpa.estudanteId), jpa.descricao);
    }

    private AcaoPermanenciaJpa toJpa(AcaoPermanencia a) {
        var jpa = acaoRepo.findById(a.getId().getId()).orElseGet(AcaoPermanenciaJpa::new);
        jpa.id = a.getId().getId();
        jpa.coordenadorId = a.getCoordenadorId().getId();
        jpa.descricao = a.getDescricao();
        jpa.indicadoresAgregados = a.getIndicadoresAgregados();
        jpa.data = a.getData();
        return jpa;
    }

    private AcaoPermanencia toDomain(AcaoPermanenciaJpa jpa) {
        return new AcaoPermanencia(new AcaoPermanenciaId(jpa.id),
                new CoordenadorId(jpa.coordenadorId), jpa.descricao, jpa.indicadoresAgregados);
    }
}
