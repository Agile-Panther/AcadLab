package school.cesar.acadlab.infraestrutura.persistencia.jpa;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import school.cesar.acadlab.aplicacao.permanenciaacademica.BeneficioResumo;
import school.cesar.acadlab.aplicacao.permanenciaacademica.EditalResumo;
import school.cesar.acadlab.aplicacao.permanenciaacademica.InscricaoResumo;
import school.cesar.acadlab.aplicacao.permanenciaacademica.PermanenciaAcademicaRepositorioAplicacao;
import school.cesar.acadlab.dominio.permanenciaacademica.Beneficio;
import school.cesar.acadlab.dominio.permanenciaacademica.BeneficioId;
import school.cesar.acadlab.dominio.permanenciaacademica.BeneficioRepositorio;
import school.cesar.acadlab.dominio.permanenciaacademica.Edital;
import school.cesar.acadlab.dominio.permanenciaacademica.EditalId;
import school.cesar.acadlab.dominio.permanenciaacademica.EditalRepositorio;
import school.cesar.acadlab.dominio.permanenciaacademica.EstudantePermanenciaId;
import school.cesar.acadlab.dominio.permanenciaacademica.Inscricao;
import school.cesar.acadlab.dominio.permanenciaacademica.InscricaoId;
import school.cesar.acadlab.dominio.permanenciaacademica.InscricaoRepositorio;
import school.cesar.acadlab.dominio.permanenciaacademica.StatusBeneficio;
import school.cesar.acadlab.dominio.permanenciaacademica.StatusEdital;
import school.cesar.acadlab.dominio.permanenciaacademica.StatusInscricao;

/* ===== JPA Entities ===== */

@Entity
@Table(name = "EDITAL_PERMANENCIA")
class EditalJpa {
    @Id int id;
    String programa;
    String descricao;
    int vagas;
    LocalDate prazoInscricaoInicio;
    LocalDate prazoInscricaoFim;
    LocalDate prazoRecursoInicio;
    LocalDate prazoRecursoFim;
    LocalDate prazoRenovacao;

    @Enumerated(EnumType.STRING)
    StatusEdital status;
}

@Entity
@Table(name = "INSCRICAO_PERMANENCIA")
class InscricaoJpa {
    @Id int id;
    int editalId;
    int estudanteId;

    @Enumerated(EnumType.STRING)
    StatusInscricao status;

    boolean recursoInterposto;
    int pontuacao;
    LocalDate dataInscricao;
}

@Entity
@Table(name = "BENEFICIO_PERMANENCIA")
class BeneficioJpa {
    @Id int id;
    int inscricaoId;
    int estudanteId;
    int editalId;

    @Enumerated(EnumType.STRING)
    StatusBeneficio status;

    LocalDate dataAtivacao;
    LocalDate prazoRenovacao;
    boolean solicitouRenovacao;
}

/* ===== JPA Repositories ===== */

interface EditalJpaRepository extends JpaRepository<EditalJpa, Integer> {
    List<EditalJpa> findByPrograma(String programa);
    boolean existsByProgramaAndStatus(String programa, StatusEdital status);

    @Query("SELECT COALESCE(MAX(e.id), 0) + 1 FROM EditalJpa e")
    int proximoId();
}

interface InscricaoJpaRepository extends JpaRepository<InscricaoJpa, Integer> {
    List<InscricaoJpa> findByEditalId(int editalId);
    List<InscricaoJpa> findByEstudanteId(int estudanteId);
    List<InscricaoJpa> findByEditalIdAndStatus(int editalId, StatusInscricao status);
    Optional<InscricaoJpa> findByEstudanteIdAndEditalId(int estudanteId, int editalId);

    @Query("SELECT COALESCE(MAX(i.id), 0) + 1 FROM InscricaoJpa i")
    int proximoId();
}

interface BeneficioJpaRepository extends JpaRepository<BeneficioJpa, Integer> {
    List<BeneficioJpa> findByEstudanteId(int estudanteId);
    Optional<BeneficioJpa> findByInscricaoId(int inscricaoId);

    @Query("SELECT COALESCE(MAX(b.id), 0) + 1 FROM BeneficioJpa b")
    int proximoId();
}

/* ===== Repository Implementations ===== */

@Repository
class PermanenciaAcademicaRepositorioImpl
        implements EditalRepositorio, InscricaoRepositorio, BeneficioRepositorio,
                   PermanenciaAcademicaRepositorioAplicacao {

    @Autowired EditalJpaRepository editalRepo;
    @Autowired InscricaoJpaRepository inscricaoRepo;
    @Autowired BeneficioJpaRepository beneficioRepo;

    /* --- EditalRepositorio --- */

    @Override
    public EditalId proximoEditalId() { return new EditalId(editalRepo.proximoId()); }

    @Override
    public void salvar(Edital edital) { editalRepo.save(toJpa(edital)); }

    @Override
    public Edital obter(EditalId id) {
        return editalRepo.findById(id.getValor())
                .map(this::toDomain)
                .orElseThrow(() -> new IllegalArgumentException("Edital não encontrado: " + id));
    }

    @Override
    public Optional<Edital> buscarPorId(EditalId id) {
        return editalRepo.findById(id.getValor()).map(this::toDomain);
    }

    @Override
    public boolean existeEditalAbertoParaPrograma(String programa) {
        return editalRepo.existsByProgramaAndStatus(programa, StatusEdital.INSCRICOES_ABERTAS);
    }

    @Override
    public List<Edital> buscarPorPrograma(String programa) {
        return editalRepo.findByPrograma(programa).stream().map(this::toDomain).toList();
    }

    /* --- InscricaoRepositorio --- */

    @Override
    public InscricaoId proximoInscricaoId() { return new InscricaoId(inscricaoRepo.proximoId()); }

    @Override
    public void salvar(Inscricao inscricao) { inscricaoRepo.save(toJpa(inscricao)); }

    @Override
    public Inscricao obter(InscricaoId id) {
        return inscricaoRepo.findById(id.getValor())
                .map(this::toDomain)
                .orElseThrow(() -> new IllegalArgumentException("Inscrição não encontrada: " + id));
    }

    @Override
    public Optional<Inscricao> buscarPorEstudanteEEdital(EstudantePermanenciaId estudanteId, EditalId editalId) {
        return inscricaoRepo.findByEstudanteIdAndEditalId(estudanteId.getValor(), editalId.getValor())
                .map(this::toDomain);
    }

    @Override
    public List<Inscricao> buscarPorEdital(EditalId editalId) {
        return inscricaoRepo.findByEditalId(editalId.getValor()).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Inscricao> buscarDeferidosPorEdital(EditalId editalId) {
        return inscricaoRepo.findByEditalIdAndStatus(editalId.getValor(), StatusInscricao.DEFERIDA)
                .stream().map(this::toDomain).toList();
    }

    /* --- BeneficioRepositorio --- */

    @Override
    public BeneficioId proximoBeneficioId() { return new BeneficioId(beneficioRepo.proximoId()); }

    @Override
    public void salvar(Beneficio beneficio) { beneficioRepo.save(toJpa(beneficio)); }

    @Override
    public Beneficio obter(BeneficioId id) {
        return beneficioRepo.findById(id.getValor())
                .map(this::toDomain)
                .orElseThrow(() -> new IllegalArgumentException("Benefício não encontrado: " + id));
    }

    @Override
    public Optional<Beneficio> buscarPorInscricao(InscricaoId inscricaoId) {
        return beneficioRepo.findByInscricaoId(inscricaoId.getValor()).map(this::toDomain);
    }

    @Override
    public List<Beneficio> buscarPorEstudante(EstudantePermanenciaId estudanteId) {
        return beneficioRepo.findByEstudanteId(estudanteId.getValor()).stream().map(this::toDomain).toList();
    }

    /* --- PermanenciaAcademicaRepositorioAplicacao --- */

    @Override
    public List<EditalResumo> buscarTodosEditais() {
        return editalRepo.findAll().stream().map(this::toResumoEdital).toList();
    }

    @Override
    public List<EditalResumo> buscarEditaisPorPrograma(String programa) {
        return editalRepo.findByPrograma(programa).stream().map(this::toResumoEdital).toList();
    }

    @Override
    public Optional<EditalResumo> buscarEditalPorId(int id) {
        return editalRepo.findById(id).map(this::toResumoEdital);
    }

    @Override
    public List<InscricaoResumo> buscarTodasInscricoes() {
        return inscricaoRepo.findAll().stream().map(this::toResumoInscricao).toList();
    }

    @Override
    public List<InscricaoResumo> buscarInscricoesPorEdital(int editalId) {
        return inscricaoRepo.findByEditalId(editalId).stream().map(this::toResumoInscricao).toList();
    }

    @Override
    public List<InscricaoResumo> buscarInscricoesPorEstudante(int estudanteId) {
        return inscricaoRepo.findByEstudanteId(estudanteId).stream().map(this::toResumoInscricao).toList();
    }

    @Override
    public List<BeneficioResumo> buscarBeneficiosPorEstudante(int estudanteId) {
        return beneficioRepo.findByEstudanteId(estudanteId).stream().map(this::toResumoBeneficio).toList();
    }

    @Override
    public Optional<BeneficioResumo> buscarBeneficioPorId(int id) {
        return beneficioRepo.findById(id).map(this::toResumoBeneficio);
    }

    /* --- Conversões --- */

    private EditalJpa toJpa(Edital e) {
        var jpa = editalRepo.findById(e.getId().getValor()).orElseGet(EditalJpa::new);
        jpa.id = e.getId().getValor();
        jpa.programa = e.getPrograma();
        jpa.descricao = e.getDescricao();
        jpa.vagas = e.getVagas();
        jpa.prazoInscricaoInicio = e.getPrazoInscricaoInicio();
        jpa.prazoInscricaoFim = e.getPrazoInscricaoFim();
        jpa.prazoRecursoInicio = e.getPrazoRecursoInicio();
        jpa.prazoRecursoFim = e.getPrazoRecursoFim();
        jpa.prazoRenovacao = e.getPrazoRenovacao();
        jpa.status = e.getStatus();
        return jpa;
    }

    private Edital toDomain(EditalJpa jpa) {
        return Edital.reconstituir(new EditalId(jpa.id), jpa.programa, jpa.descricao, jpa.vagas,
                jpa.prazoInscricaoInicio, jpa.prazoInscricaoFim,
                jpa.prazoRecursoInicio, jpa.prazoRecursoFim,
                jpa.prazoRenovacao, jpa.status);
    }

    private EditalResumo toResumoEdital(EditalJpa jpa) {
        return new EditalResumo(jpa.id, jpa.programa, jpa.descricao, jpa.vagas,
                jpa.prazoInscricaoInicio, jpa.prazoInscricaoFim,
                jpa.prazoRecursoInicio, jpa.prazoRecursoFim,
                jpa.prazoRenovacao, jpa.status.name());
    }

    private InscricaoJpa toJpa(Inscricao i) {
        var jpa = inscricaoRepo.findById(i.getId().getValor()).orElseGet(InscricaoJpa::new);
        jpa.id = i.getId().getValor();
        jpa.editalId = i.getEditalId().getValor();
        jpa.estudanteId = i.getEstudanteId().getValor();
        jpa.status = i.getStatus();
        jpa.recursoInterposto = i.isRecursoInterposto();
        jpa.pontuacao = i.getPontuacao();
        jpa.dataInscricao = i.getDataInscricao();
        return jpa;
    }

    private Inscricao toDomain(InscricaoJpa jpa) {
        return Inscricao.reconstituir(new InscricaoId(jpa.id), new EditalId(jpa.editalId),
                new EstudantePermanenciaId(jpa.estudanteId), jpa.status,
                jpa.recursoInterposto, jpa.pontuacao, jpa.dataInscricao);
    }

    private InscricaoResumo toResumoInscricao(InscricaoJpa jpa) {
        return new InscricaoResumo(jpa.id, jpa.editalId, jpa.estudanteId,
                jpa.status.name(), jpa.pontuacao, jpa.dataInscricao);
    }

    private BeneficioJpa toJpa(Beneficio b) {
        var jpa = beneficioRepo.findById(b.getId().getValor()).orElseGet(BeneficioJpa::new);
        jpa.id = b.getId().getValor();
        jpa.inscricaoId = b.getInscricaoId().getValor();
        jpa.estudanteId = b.getEstudanteId().getValor();
        jpa.editalId = b.getEditalId().getValor();
        jpa.status = b.getStatus();
        jpa.dataAtivacao = b.getDataAtivacao();
        jpa.prazoRenovacao = b.getPrazoRenovacao();
        jpa.solicitouRenovacao = b.isSolicitouRenovacao();
        return jpa;
    }

    private Beneficio toDomain(BeneficioJpa jpa) {
        return Beneficio.reconstituir(new BeneficioId(jpa.id), new InscricaoId(jpa.inscricaoId),
                new EstudantePermanenciaId(jpa.estudanteId), new EditalId(jpa.editalId),
                jpa.status, jpa.dataAtivacao, jpa.prazoRenovacao, jpa.solicitouRenovacao);
    }

    private BeneficioResumo toResumoBeneficio(BeneficioJpa jpa) {
        return new BeneficioResumo(jpa.id, jpa.inscricaoId, jpa.estudanteId,
                jpa.editalId, jpa.status.name(), jpa.dataAtivacao, jpa.prazoRenovacao);
    }
}
