package school.cesar.acadlab.infraestrutura.persistencia.jpa;

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
import school.cesar.acadlab.aplicacao.estagios.OportunidadeRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.estagios.OportunidadeResumo;
import school.cesar.acadlab.dominio.estagios.oportunidade.EmpresaId;
import school.cesar.acadlab.dominio.estagios.oportunidade.EstudanteId;
import school.cesar.acadlab.dominio.estagios.oportunidade.Oportunidade;
import school.cesar.acadlab.dominio.estagios.oportunidade.OportunidadeId;
import school.cesar.acadlab.dominio.estagios.oportunidade.OportunidadeRepositorio;
import school.cesar.acadlab.dominio.estagios.oportunidade.StatusOportunidade;

@Entity
@Table(name = "OPORTUNIDADE_ESTAGIO")
class OportunidadeJpa {
    @Id
    int id;
    int empresaId;
    String descricao;
    int cargaHorariaTotal;
    @Enumerated(EnumType.STRING)
    StatusOportunidade status;
    Integer candidatoId;
}

interface OportunidadeJpaRepository extends JpaRepository<OportunidadeJpa, Integer> {
    List<OportunidadeJpa> findByStatus(StatusOportunidade status);

    @Query("SELECT COALESCE(MAX(o.id), 0) + 1 FROM OportunidadeJpa o")
    int proximoId();
}

@Repository
class OportunidadeRepositorioImpl implements OportunidadeRepositorio, OportunidadeRepositorioAplicacao {

    @Autowired
    OportunidadeJpaRepository repository;

    @Override
    public OportunidadeId proximaOportunidadeId() {
        return new OportunidadeId(repository.proximoId());
    }

    @Override
    public void salvar(Oportunidade oportunidade) {
        repository.save(toJpa(oportunidade));
    }

    @Override
    public Optional<Oportunidade> buscarPorId(OportunidadeId id) {
        return repository.findById(id.getValor()).map(this::toDomain);
    }

    @Override
    public Optional<OportunidadeResumo> buscarPorId(int id) {
        return repository.findById(id).map(this::toResumo);
    }

    @Override
    public List<OportunidadeResumo> listarAbertas() {
        return repository.findByStatus(StatusOportunidade.ABERTA).stream()
                .map(this::toResumo)
                .toList();
    }

    private OportunidadeJpa toJpa(Oportunidade o) {
        var jpa = repository.findById(o.getId().getValor()).orElseGet(OportunidadeJpa::new);
        jpa.id = o.getId().getValor();
        jpa.empresaId = o.getEmpresaId().getValor();
        jpa.descricao = o.getDescricao();
        jpa.cargaHorariaTotal = o.getCargaHorariaTotal();
        jpa.status = o.getStatus();
        jpa.candidatoId = o.getCandidato() != null ? o.getCandidato().getValor() : null;
        return jpa;
    }

    private Oportunidade toDomain(OportunidadeJpa jpa) {
        return Oportunidade.reconstituir(
                new OportunidadeId(jpa.id),
                new EmpresaId(jpa.empresaId),
                jpa.descricao,
                jpa.cargaHorariaTotal,
                jpa.status,
                jpa.candidatoId != null ? new EstudanteId(jpa.candidatoId) : null);
    }

    private OportunidadeResumo toResumo(OportunidadeJpa jpa) {
        return new OportunidadeResumo(jpa.id, jpa.empresaId, jpa.descricao,
                jpa.cargaHorariaTotal, jpa.status.name());
    }
}
