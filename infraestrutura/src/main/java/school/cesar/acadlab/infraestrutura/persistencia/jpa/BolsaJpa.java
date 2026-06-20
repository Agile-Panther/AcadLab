package school.cesar.acadlab.infraestrutura.persistencia.jpa;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import school.cesar.acadlab.aplicacao.gestaofinanceira.BolsaRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.gestaofinanceira.BolsaResumo;
import school.cesar.acadlab.dominio.gestaofinanceira.EstudanteId;
import school.cesar.acadlab.dominio.gestaofinanceira.bolsa.Bolsa;
import school.cesar.acadlab.dominio.gestaofinanceira.bolsa.BolsaId;
import school.cesar.acadlab.dominio.gestaofinanceira.bolsa.BolsaRepositorio;
import school.cesar.acadlab.dominio.gestaofinanceira.bolsa.StatusBolsa;
import school.cesar.acadlab.dominio.gestaofinanceira.bolsa.TipoBolsa;

@Entity
@Table(name = "BOLSA")
class BolsaJpa {
    @Id
    int id;
    int estudanteId;
    @Enumerated(EnumType.STRING)
    TipoBolsa tipo;
    BigDecimal percentual;
    LocalDate validade;
    @Enumerated(EnumType.STRING)
    StatusBolsa status;
}

interface BolsaJpaRepository extends JpaRepository<BolsaJpa, Integer> {
    @Query("SELECT COALESCE(MAX(b.id), 0) + 1 FROM BolsaJpa b")
    int proximoId();
}

@Repository
class BolsaRepositorioImpl implements BolsaRepositorio, BolsaRepositorioAplicacao {
    @Autowired
    BolsaJpaRepository repositorio;

    @Override
    public BolsaId proximoId() { return new BolsaId(repositorio.proximoId()); }

    @Override
    @Transactional
    public void salvar(Bolsa bolsa) {
        var jpa = new BolsaJpa();
        jpa.id = bolsa.getId().valor();
        jpa.estudanteId = bolsa.getEstudanteId().valor();
        jpa.tipo = bolsa.getTipo();
        jpa.percentual = bolsa.getPercentual();
        jpa.validade = bolsa.getValidade();
        jpa.status = bolsa.getStatus();
        repositorio.save(jpa);
    }

    @Override
    public Bolsa obter(BolsaId id) { return toDomain(repositorio.findById(id.valor()).orElseThrow()); }

    @Override
    public List<Bolsa> listar() { return repositorio.findAll().stream().map(this::toDomain).toList(); }

    @Override
    public List<BolsaResumo> listarResumos() {  // BolsaRepositorioAplicacao
        return repositorio.findAll().stream()
                .map(jpa -> new BolsaResumo(jpa.id, jpa.estudanteId, jpa.tipo.name(),
                        jpa.percentual, jpa.validade, jpa.status.name()))
                .toList();
    }

    private Bolsa toDomain(BolsaJpa jpa) {
        return Bolsa.reconstituir(new BolsaId(jpa.id), new EstudanteId(jpa.estudanteId),
                jpa.tipo, jpa.percentual, jpa.validade, jpa.status);
    }
}
