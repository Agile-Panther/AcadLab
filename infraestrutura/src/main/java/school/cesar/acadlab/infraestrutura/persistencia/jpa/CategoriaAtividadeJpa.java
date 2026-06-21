package school.cesar.acadlab.infraestrutura.persistencia.jpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import school.cesar.acadlab.aplicacao.atividadescomplementares.CategoriaHorasRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.atividadescomplementares.CategoriaHorasResumo;

@Entity
@Table(name = "CATEGORIA_ATIVIDADE")
class CategoriaAtividadeJpa {
    @Id
    int id;
    String nome;
    int limiteHoras;
}

interface CategoriaAtividadeJpaRepository extends JpaRepository<CategoriaAtividadeJpa, Integer> {}

@Repository
class CategoriaAtividadeRepositorioImpl implements CategoriaHorasRepositorioAplicacao {
    @Autowired
    CategoriaAtividadeJpaRepository repositorio;

    @Override
    public List<CategoriaHorasResumo> listar() {
        return repositorio.findAll().stream()
                .map(jpa -> new CategoriaHorasResumo(jpa.id, jpa.nome, jpa.limiteHoras))
                .toList();
    }
}
