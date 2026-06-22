package school.cesar.acadlab.infraestrutura.persistencia.jpa;

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
import school.cesar.acadlab.aplicacao.estagios.CandidaturaRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.estagios.CandidaturaResumo;
import school.cesar.acadlab.aplicacao.estagios.EstagioRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.estagios.EstagioResumo;
import school.cesar.acadlab.dominio.estagios.candidatura.Candidatura;
import school.cesar.acadlab.dominio.estagios.candidatura.CandidaturaId;
import school.cesar.acadlab.dominio.estagios.candidatura.CandidaturaRepositorio;
import school.cesar.acadlab.dominio.estagios.candidatura.StatusCandidatura;
import school.cesar.acadlab.dominio.estagios.estagio.Estagio;
import school.cesar.acadlab.dominio.estagios.estagio.EstagioId;
import school.cesar.acadlab.dominio.estagios.estagio.EstagioRepositorio;
import school.cesar.acadlab.dominio.estagios.estagio.Relatorio;
import school.cesar.acadlab.dominio.estagios.estagio.StatusEstagio;
import school.cesar.acadlab.dominio.estagios.estagio.StatusRelatorio;
import school.cesar.acadlab.dominio.estagios.oportunidade.EmpresaId;
import school.cesar.acadlab.dominio.estagios.oportunidade.EstudanteId;
import school.cesar.acadlab.dominio.estagios.oportunidade.OportunidadeId;

@Entity
@Table(name = "ESTAGIO")
class EstagioJpa {
    @Id
    int id;
    int oportunidadeId;
    int candidaturaId;
    int estudanteId;
    int empresaId;

    @Enumerated(EnumType.STRING)
    StatusEstagio status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "RELATORIO_ESTAGIO", joinColumns = @JoinColumn(name = "estagioId"))
    List<RelatorioJpa> relatorios = new ArrayList<>();
}

@Embeddable
class RelatorioJpa {
    @Column(name = "numero")
    int numero;
    @Column(name = "descricao")
    String descricao;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    StatusRelatorio status;
}

interface EstagioJpaRepository extends JpaRepository<EstagioJpa, Integer> {
    List<EstagioJpa> findByEstudanteId(int estudanteId);

    @Query("SELECT COALESCE(MAX(e.id), 0) + 1 FROM EstagioJpa e")
    int proximoId();
}

@Repository
class EstagioRepositorioImpl implements EstagioRepositorio, EstagioRepositorioAplicacao {

    @Autowired
    EstagioJpaRepository repository;

    @Override
    public EstagioId proximoEstagioId() {
        return new EstagioId(repository.proximoId());
    }

    @Override
    @Transactional
    public void salvar(Estagio estagio) {
        repository.save(toJpa(estagio));
    }

    @Override
    public Optional<Estagio> buscarPorId(EstagioId id) {
        return repository.findById(id.getValor()).map(this::toDomain);
    }

    @Override
    public List<Estagio> buscarPorEstudante(EstudanteId estudanteId) {
        return repository.findByEstudanteId(estudanteId.getValor()).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<EstagioResumo> buscarPorId(int id) {
        return repository.findById(id).map(this::toResumo);
    }

    @Override
    public List<EstagioResumo> buscarPorEstudante(int estudanteId) {
        return repository.findByEstudanteId(estudanteId).stream()
                .map(this::toResumo)
                .toList();
    }

    private EstagioJpa toJpa(Estagio e) {
        var jpa = repository.findById(e.getId().getValor()).orElseGet(EstagioJpa::new);
        jpa.id = e.getId().getValor();
        jpa.oportunidadeId = e.getOportunidadeId().getValor();
        jpa.candidaturaId = e.getCandidaturaId().getValor();
        jpa.estudanteId = e.getEstudanteId().getValor();
        jpa.empresaId = e.getEmpresaId().getValor();
        jpa.status = e.getStatus();
        jpa.relatorios.clear();
        for (var r : e.getRelatorios()) {
            var rJpa = new RelatorioJpa();
            rJpa.numero = r.getNumero();
            rJpa.descricao = r.getDescricao();
            rJpa.status = r.getStatus();
            jpa.relatorios.add(rJpa);
        }
        return jpa;
    }

    private Estagio toDomain(EstagioJpa jpa) {
        var relatorios = jpa.relatorios.stream()
                .map(r -> Relatorio.reconstituir(r.numero, r.descricao, r.status))
                .toList();
        return Estagio.reconstituir(
                new EstagioId(jpa.id),
                new OportunidadeId(jpa.oportunidadeId),
                new CandidaturaId(jpa.candidaturaId),
                new EstudanteId(jpa.estudanteId),
                new EmpresaId(jpa.empresaId),
                jpa.status,
                relatorios);
    }

    private EstagioResumo toResumo(EstagioJpa jpa) {
        return new EstagioResumo(jpa.id, jpa.oportunidadeId, jpa.estudanteId,
                jpa.empresaId, jpa.status.name());
    }
}

@Entity
@Table(name = "CANDIDATURA_ESTAGIO")
class CandidaturaJpa {
    @Id int id;
    int oportunidadeId;
    int estudanteId;
    @Enumerated(EnumType.STRING)
    StatusCandidatura status;
}

interface CandidaturaJpaRepository extends JpaRepository<CandidaturaJpa, Integer> {
    @Query("SELECT COALESCE(MAX(c.id), 0) + 1 FROM CandidaturaJpa c")
    int proximoId();

    List<CandidaturaJpa> findByEstudanteId(int estudanteId);
}

@Repository
class CandidaturaRepositorioImpl implements CandidaturaRepositorio, CandidaturaRepositorioAplicacao {

    @Autowired CandidaturaJpaRepository repository;

    @Override
    public CandidaturaId proximaCandidaturaId() {
        return new CandidaturaId(repository.proximoId());
    }

    @Override
    public List<CandidaturaResumo> listarTodas() {
        return repository.findAll().stream().map(this::toResumo).toList();
    }

    @Override
    public List<CandidaturaResumo> buscarPorEstudante(int estudanteId) {
        return repository.findByEstudanteId(estudanteId).stream().map(this::toResumo).toList();
    }

    private CandidaturaResumo toResumo(CandidaturaJpa jpa) {
        return new CandidaturaResumo(jpa.id, jpa.oportunidadeId, jpa.estudanteId, jpa.status.name());
    }

    @Override
    @Transactional
    public void salvar(Candidatura candidatura) {
        var jpa = repository.findById(candidatura.getId().getValor()).orElseGet(CandidaturaJpa::new);
        jpa.id = candidatura.getId().getValor();
        jpa.oportunidadeId = candidatura.getOportunidadeId().getValor();
        jpa.estudanteId = candidatura.getEstudanteId().getValor();
        jpa.status = candidatura.getStatus();
        repository.save(jpa);
    }

    @Override
    public Optional<Candidatura> buscarPorId(CandidaturaId id) {
        return repository.findById(id.getValor()).map(jpa ->
                Candidatura.reconstituir(
                        new CandidaturaId(jpa.id),
                        new OportunidadeId(jpa.oportunidadeId),
                        new EstudanteId(jpa.estudanteId),
                        jpa.status));
    }
}
