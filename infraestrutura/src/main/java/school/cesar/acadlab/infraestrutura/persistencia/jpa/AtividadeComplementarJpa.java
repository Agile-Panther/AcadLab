package school.cesar.acadlab.infraestrutura.persistencia.jpa;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import school.cesar.acadlab.aplicacao.atividadescomplementares.AtividadeComplementarResumo;
import school.cesar.acadlab.aplicacao.atividadescomplementares.AtividadeComplementarRepositorioAplicacao;
import school.cesar.acadlab.dominio.atividadescomplementares.AtividadeComplementarId;
import school.cesar.acadlab.dominio.atividadescomplementares.CategoriaAtividadeId;
import school.cesar.acadlab.dominio.atividadescomplementares.EstudanteId;
import school.cesar.acadlab.dominio.atividadescomplementares.StatusAtividade;
import school.cesar.acadlab.dominio.atividadescomplementares.atividade.AtividadeComplementar;
import school.cesar.acadlab.dominio.atividadescomplementares.atividade.AtividadeComplementarRepositorio;

@Entity
@Table(name = "ATIVIDADE_COMPLEMENTAR")
class AtividadeComplementarJpa {
    @Id
    int id;

    int estudanteId;
    int categoriaId;
    String identificadorCertificado;
    String descricao;
    int horasSubmetidas;
    int horasAprovadas;
    LocalDate dataRealizacao;

    @Enumerated(EnumType.STRING)
    StatusAtividade status;
}

interface AtividadeComplementarJpaRepository extends JpaRepository<AtividadeComplementarJpa, Integer> {
    List<AtividadeComplementarJpa> findByEstudanteId(int estudanteId);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(MAX(a.id), 0) + 1 FROM AtividadeComplementarJpa a")
    int proximoId();
}

@Repository
class AtividadeComplementarRepositorioImpl implements AtividadeComplementarRepositorio, AtividadeComplementarRepositorioAplicacao {
    @Autowired
    AtividadeComplementarJpaRepository repositorio;

    @Override
    public AtividadeComplementarId proximoId() {
        return new AtividadeComplementarId(repositorio.proximoId());
    }

    @Override
    public void salvar(AtividadeComplementar atividade) {
        repositorio.save(toJpa(atividade));
    }

    @Override
    public AtividadeComplementar obter(AtividadeComplementarId id) {
        return toDomain(repositorio.findById(id.valor()).orElseThrow());
    }

    @Override
    public List<AtividadeComplementar> pesquisarPorEstudante(EstudanteId estudanteId) {
        return repositorio.findByEstudanteId(estudanteId.valor()).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<AtividadeComplementarResumo> pesquisarPorEstudante(int estudanteId) {
        return repositorio.findByEstudanteId(estudanteId).stream()
                .map(jpa -> new AtividadeComplementarResumo(
                        jpa.id,
                        jpa.estudanteId,
                        jpa.categoriaId,
                        jpa.descricao,
                        jpa.horasSubmetidas,
                        jpa.horasAprovadas,
                        jpa.status.name()))
                .toList();
    }

    private AtividadeComplementarJpa toJpa(AtividadeComplementar atividade) {
        var jpa = new AtividadeComplementarJpa();
        jpa.id = atividade.getId().valor();
        jpa.estudanteId = atividade.getEstudanteId().valor();
        jpa.categoriaId = atividade.getCategoriaId().valor();
        jpa.identificadorCertificado = atividade.getIdentificadorCertificado();
        jpa.descricao = atividade.getDescricao();
        jpa.horasSubmetidas = atividade.getHorasSubmetidas();
        jpa.horasAprovadas = atividade.getHorasAprovadas();
        jpa.dataRealizacao = atividade.getDataRealizacao();
        jpa.status = atividade.getStatus();
        return jpa;
    }

    private AtividadeComplementar toDomain(AtividadeComplementarJpa jpa) {
        return AtividadeComplementar.reconstituir(
                new AtividadeComplementarId(jpa.id),
                new EstudanteId(jpa.estudanteId),
                new CategoriaAtividadeId(jpa.categoriaId),
                jpa.identificadorCertificado,
                jpa.descricao,
                jpa.horasSubmetidas,
                jpa.dataRealizacao,
                jpa.status,
                jpa.horasAprovadas);
    }
}
