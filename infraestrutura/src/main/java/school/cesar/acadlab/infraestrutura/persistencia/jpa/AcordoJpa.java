package school.cesar.acadlab.infraestrutura.persistencia.jpa;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import school.cesar.acadlab.aplicacao.gestaofinanceira.AcordoRepositorioAplicacao;

@Entity
@Table(name = "ACORDO")
class AcordoJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    int estudanteId;
    LocalDate prazo;
    int descontoPercentual;
    String observacoes;
    LocalDateTime criadoEm;
}

interface AcordoJpaRepository extends JpaRepository<AcordoJpa, Integer> {}

@Repository
class AcordoRepositorioImpl implements AcordoRepositorioAplicacao {

    @Autowired
    AcordoJpaRepository jpaRepository;

    @Override
    @Transactional
    public void registrar(int estudanteId, LocalDate prazo, int descontoPercentual, String observacoes) {
        var acordo = new AcordoJpa();
        acordo.estudanteId = estudanteId;
        acordo.prazo = prazo;
        acordo.descontoPercentual = descontoPercentual;
        acordo.observacoes = observacoes;
        acordo.criadoEm = LocalDateTime.now();
        jpaRepository.save(acordo);
    }
}
