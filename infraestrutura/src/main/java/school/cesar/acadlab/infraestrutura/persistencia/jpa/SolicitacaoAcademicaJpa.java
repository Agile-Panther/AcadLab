package school.cesar.acadlab.infraestrutura.persistencia.jpa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
import school.cesar.acadlab.aplicacao.secretariavirtual.DocumentoResumo;
import school.cesar.acadlab.aplicacao.secretariavirtual.SolicitacaoAcademicaRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.secretariavirtual.SolicitacaoAcademicaResumo;
import school.cesar.acadlab.dominio.secretariavirtual.analista.SecretariaId;
import school.cesar.acadlab.dominio.secretariavirtual.documento.Documento;
import school.cesar.acadlab.dominio.secretariavirtual.estudante.EstudanteId;
import school.cesar.acadlab.dominio.secretariavirtual.periodo.PeriodoLetivoId;
import school.cesar.acadlab.dominio.secretariavirtual.protocolo.Protocolo;
import school.cesar.acadlab.dominio.secretariavirtual.protocolo.ProtocoloId;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.SolicitacaoAcademica;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.SolicitacaoAcademicaId;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.SolicitacaoAcademicaRepositorio;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.StatusSolicitacao;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.TipoSolicitacao;

@Entity
@Table(name = "SOLICITACAO_ACADEMICA")
class SolicitacaoAcademicaJpa {
    @Id
    int id;
    int estudanteId;
    int periodoLetivoId;

    @Enumerated(EnumType.STRING)
    TipoSolicitacao tipo;

    int protocoloId;
    String descricao;
    LocalDate dataAbertura;

    @Enumerated(EnumType.STRING)
    StatusSolicitacao status;

    Integer analistaId;
    String justificativaAnalise;
    LocalDate dataAnalise;
    boolean possuiImpactoAcademico;
    boolean alteracoesVinculadas;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "DOCUMENTO_SOLICITACAO", joinColumns = @JoinColumn(name = "solicitacaoId"))
    List<DocumentoJpa> documentos = new ArrayList<>();
}

@Embeddable
class DocumentoJpa {
    @Column(name = "tipo")
    String tipo;

    @Column(name = "nomeArquivo")
    String nomeArquivo;

    @Column(name = "dataAnexo")
    LocalDate dataAnexo;
}

interface SolicitacaoAcademicaJpaRepository extends JpaRepository<SolicitacaoAcademicaJpa, Integer> {
    List<SolicitacaoAcademicaJpa> findByEstudanteId(int estudanteId);
    List<SolicitacaoAcademicaJpa> findByStatus(StatusSolicitacao status);
    List<SolicitacaoAcademicaJpa> findByEstudanteIdAndTipoAndPeriodoLetivoId(
            int estudanteId, TipoSolicitacao tipo, int periodoLetivoId);

    @Query("SELECT COALESCE(MAX(s.id), 0) + 1 FROM SolicitacaoAcademicaJpa s")
    int proximoId();

    @Query("SELECT COALESCE(MAX(s.protocoloId), 0) + 1 FROM SolicitacaoAcademicaJpa s")
    int proximoProtocoloId();
}

@Repository
class SolicitacaoAcademicaRepositorioImpl
        implements SolicitacaoAcademicaRepositorio, SolicitacaoAcademicaRepositorioAplicacao {

    @Autowired
    SolicitacaoAcademicaJpaRepository repository;

    @Override
    public SolicitacaoAcademicaId proximoId() {
        return new SolicitacaoAcademicaId(repository.proximoId());
    }

    @Override
    public ProtocoloId proximoProtocoloId() {
        return new ProtocoloId(repository.proximoProtocoloId());
    }

    @Override
    public void salvar(SolicitacaoAcademica solicitacao) {
        repository.save(toJpa(solicitacao));
    }

    @Override
    public SolicitacaoAcademica obter(SolicitacaoAcademicaId id) {
        return repository.findById(id.getId())
                .map(this::toDomain)
                .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada: " + id));
    }

    @Override
    public List<SolicitacaoAcademica> pesquisarPorEstudante(EstudanteId estudanteId) {
        return repository.findByEstudanteId(estudanteId.getId()).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<SolicitacaoAcademica> pesquisarAbertaPorEstudanteTipoPeriodo(
            EstudanteId estudanteId, TipoSolicitacao tipo, PeriodoLetivoId periodoLetivoId) {
        return repository.findByEstudanteIdAndTipoAndPeriodoLetivoId(
                        estudanteId.getId(), tipo, periodoLetivoId.getId()).stream()
                .filter(s -> s.status != StatusSolicitacao.CONCLUIDA
                        && s.status != StatusSolicitacao.CANCELADA
                        && s.status != StatusSolicitacao.INDEFERIDA)
                .findFirst()
                .map(this::toDomain);
    }

    @Override
    public List<SolicitacaoAcademica> pesquisarPorStatus(StatusSolicitacao status) {
        return repository.findByStatus(status).stream()
                .map(this::toDomain)
                .toList();
    }

    // --- Aplicação ---

    @Override
    public List<SolicitacaoAcademicaResumo> buscarPorEstudante(int estudanteId) {
        return repository.findByEstudanteId(estudanteId).stream()
                .map(this::toResumo)
                .toList();
    }

    @Override
    public Optional<SolicitacaoAcademicaResumo> buscarPorId(int id) {
        return repository.findById(id).map(this::toResumo);
    }

    @Override
    public List<SolicitacaoAcademicaResumo> buscarPorStatus(String status) {
        return repository.findByStatus(StatusSolicitacao.valueOf(status)).stream()
                .map(this::toResumo)
                .toList();
    }

    @Override
    public List<SolicitacaoAcademicaResumo> buscarTodas() {
        return repository.findAll().stream()
                .map(this::toResumo)
                .toList();
    }

    // --- Mapeamentos ---

    private SolicitacaoAcademicaJpa toJpa(SolicitacaoAcademica s) {
        var jpa = repository.findById(s.getId().getId()).orElseGet(SolicitacaoAcademicaJpa::new);
        jpa.id = s.getId().getId();
        jpa.estudanteId = s.getEstudanteId().getId();
        jpa.periodoLetivoId = s.getPeriodoLetivoId().getId();
        jpa.tipo = s.getTipo();
        jpa.protocoloId = s.getProtocolo().getId().getId();
        jpa.descricao = s.getDescricao();
        jpa.dataAbertura = s.getDataAbertura();
        jpa.status = s.getStatus();
        jpa.analistaId = s.getAnalistaId() != null ? s.getAnalistaId().getId() : null;
        jpa.justificativaAnalise = s.getJustificativaAnalise();
        jpa.dataAnalise = s.getDataAnalise();
        jpa.possuiImpactoAcademico = s.isPossuiImpactoAcademico();
        jpa.alteracoesVinculadas = s.isAlteracoesVinculadas();

        jpa.documentos.clear();
        for (var doc : s.getDocumentos()) {
            var docJpa = new DocumentoJpa();
            docJpa.tipo = doc.getTipo();
            docJpa.nomeArquivo = doc.getNomeArquivo();
            docJpa.dataAnexo = doc.getDataAnexo();
            jpa.documentos.add(docJpa);
        }

        return jpa;
    }

    private SolicitacaoAcademica toDomain(SolicitacaoAcademicaJpa jpa) {
        var documentos = jpa.documentos.stream()
                .map(d -> new Documento(d.tipo, d.nomeArquivo, d.dataAnexo))
                .collect(Collectors.toList());

        return new SolicitacaoAcademica(
                new SolicitacaoAcademicaId(jpa.id),
                new EstudanteId(jpa.estudanteId),
                new PeriodoLetivoId(jpa.periodoLetivoId),
                jpa.tipo,
                new Protocolo(new ProtocoloId(jpa.protocoloId), jpa.dataAbertura),
                jpa.descricao,
                jpa.dataAbertura,
                jpa.status,
                documentos,
                jpa.analistaId != null ? new SecretariaId(jpa.analistaId) : null,
                jpa.justificativaAnalise,
                jpa.dataAnalise,
                jpa.possuiImpactoAcademico,
                jpa.alteracoesVinculadas);
    }

    private SolicitacaoAcademicaResumo toResumo(SolicitacaoAcademicaJpa jpa) {
        var docs = jpa.documentos.stream()
                .map(d -> new DocumentoResumo(d.tipo, d.nomeArquivo, d.dataAnexo))
                .toList();

        return new SolicitacaoAcademicaResumo(
                jpa.id,
                jpa.estudanteId,
                jpa.periodoLetivoId,
                jpa.tipo.name(),
                jpa.status.name(),
                jpa.descricao,
                jpa.protocoloId,
                jpa.dataAbertura,
                jpa.justificativaAnalise,
                jpa.dataAnalise,
                jpa.analistaId,
                jpa.possuiImpactoAcademico,
                jpa.alteracoesVinculadas,
                docs);
    }
}
