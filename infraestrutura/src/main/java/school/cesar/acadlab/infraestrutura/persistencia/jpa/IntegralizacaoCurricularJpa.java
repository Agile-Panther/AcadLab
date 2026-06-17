package school.cesar.acadlab.infraestrutura.persistencia.jpa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import school.cesar.acadlab.aplicacao.integralizacao.ColacaoRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.integralizacao.ColacaoResumo;
import school.cesar.acadlab.aplicacao.integralizacao.IntegralizacaoRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.integralizacao.IntegralizacaoResumo;
import school.cesar.acadlab.dominio.integralizacao.CoordenadorId;
import school.cesar.acadlab.dominio.integralizacao.EstudanteId;
import school.cesar.acadlab.dominio.integralizacao.MatrizCurricularId;
import school.cesar.acadlab.dominio.integralizacao.checklist.ItemChecklist;
import school.cesar.acadlab.dominio.integralizacao.checklist.TipoItemChecklist;
import school.cesar.acadlab.dominio.integralizacao.colacao.ColacaoDeGrau;
import school.cesar.acadlab.dominio.integralizacao.colacao.ColacaoId;
import school.cesar.acadlab.dominio.integralizacao.colacao.ColacaoRepositorio;
import school.cesar.acadlab.dominio.integralizacao.integralizacao.IntegralizacaoCurricular;
import school.cesar.acadlab.dominio.integralizacao.integralizacao.IntegralizacaoId;
import school.cesar.acadlab.dominio.integralizacao.integralizacao.IntegralizacaoRepositorio;
import school.cesar.acadlab.dominio.integralizacao.integralizacao.StatusIntegralizacao;

@Entity
@Table(name = "INTEGRALIZACAO_CURRICULAR")
class IntegralizacaoJpaEntity {
    @Id
    int id;
    int estudanteId;
    int matrizCurricularId;

    @Enumerated(EnumType.STRING)
    StatusIntegralizacao status;

    Integer aprovadorId;
    LocalDate dataAprovacao;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ITEM_CHECKLIST_INTEGRALIZACAO", joinColumns = @JoinColumn(name = "integralizacaoId"))
    List<ItemChecklistJpa> itensChecklist = new ArrayList<>();
}

@Embeddable
class ItemChecklistJpa {
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    TipoItemChecklist tipo;

    @Column(name = "descricao")
    String descricao;

    @Column(name = "cumprido")
    boolean cumprido;
}

@Entity
@Table(name = "COLACAO_DE_GRAU")
class ColacaoJpaEntity {
    @Id
    int id;
    int estudanteId;
    int integralizacaoId;
    LocalDate dataAptidaoAprovada;
    LocalDate dataCerimonia;
    String local;
}

interface IntegralizacaoJpaRepository extends JpaRepository<IntegralizacaoJpaEntity, Integer> {
    List<IntegralizacaoJpaEntity> findByEstudanteId(int estudanteId);

    @Query("SELECT COALESCE(MAX(i.id), 0) + 1 FROM IntegralizacaoJpaEntity i")
    int proximoId();
}

interface ColacaoJpaRepository extends JpaRepository<ColacaoJpaEntity, Integer> {
    Optional<ColacaoJpaEntity> findByEstudanteId(int estudanteId);

    @Query("SELECT COALESCE(MAX(c.id), 0) + 1 FROM ColacaoJpaEntity c")
    int proximoId();
}

@Repository
class IntegralizacaoRepositorioImpl implements IntegralizacaoRepositorio, IntegralizacaoRepositorioAplicacao {

    @Autowired
    IntegralizacaoJpaRepository repository;

    @Override
    public IntegralizacaoId proximoId() {
        return new IntegralizacaoId(repository.proximoId());
    }

    @Override
    public void salvar(IntegralizacaoCurricular integralizacao) {
        repository.save(toJpa(integralizacao));
    }

    @Override
    public IntegralizacaoCurricular obter(IntegralizacaoId id) {
        return repository.findById(id.getId()).map(this::toDomain).orElseThrow();
    }

    @Override
    public Optional<IntegralizacaoCurricular> pesquisarPorEstudante(EstudanteId estudanteId) {
        return repository.findByEstudanteId(estudanteId.getId()).stream()
                .map(this::toDomain)
                .findFirst();
    }

    @Override
    public List<IntegralizacaoResumo> buscarPorEstudante(int estudanteId) {
        return repository.findByEstudanteId(estudanteId).stream()
                .map(this::toResumo)
                .toList();
    }

    @Override
    public Optional<IntegralizacaoResumo> buscarPorId(int id) {
        return repository.findById(id).map(this::toResumo);
    }

    private IntegralizacaoJpaEntity toJpa(IntegralizacaoCurricular i) {
        var jpa = repository.findById(i.getId().getId()).orElseGet(IntegralizacaoJpaEntity::new);
        jpa.id = i.getId().getId();
        jpa.estudanteId = i.getEstudanteId().getId();
        jpa.matrizCurricularId = i.getMatrizCurricularId().getId();
        jpa.status = i.getStatus();
        jpa.aprovadorId = i.getAprovadorId() != null ? i.getAprovadorId().getId() : null;
        jpa.dataAprovacao = i.getDataAprovacao();

        jpa.itensChecklist.clear();
        for (var item : i.getItensChecklist()) {
            var itemJpa = new ItemChecklistJpa();
            itemJpa.tipo = item.getTipo();
            itemJpa.descricao = item.getDescricao();
            itemJpa.cumprido = item.isCumprido();
            jpa.itensChecklist.add(itemJpa);
        }

        return jpa;
    }

    private IntegralizacaoCurricular toDomain(IntegralizacaoJpaEntity jpa) {
        var itens = jpa.itensChecklist.stream()
                .map(i -> new ItemChecklist(i.tipo, i.descricao, i.cumprido))
                .toList();

        return IntegralizacaoCurricular.reconstituir(
                new IntegralizacaoId(jpa.id),
                new EstudanteId(jpa.estudanteId),
                new MatrizCurricularId(jpa.matrizCurricularId),
                jpa.status,
                jpa.aprovadorId != null ? new CoordenadorId(jpa.aprovadorId) : null,
                jpa.dataAprovacao,
                itens);
    }

    private IntegralizacaoResumo toResumo(IntegralizacaoJpaEntity jpa) {
        return new IntegralizacaoResumo(
                jpa.id,
                jpa.estudanteId,
                jpa.matrizCurricularId,
                jpa.status.name(),
                jpa.aprovadorId,
                jpa.dataAprovacao != null ? jpa.dataAprovacao.toString() : null);
    }
}

@Repository
class ColacaoRepositorioImpl implements ColacaoRepositorio, ColacaoRepositorioAplicacao {

    @Autowired
    ColacaoJpaRepository repository;

    @Override
    public ColacaoId proximoId() {
        return new ColacaoId(repository.proximoId());
    }

    @Override
    public void salvar(ColacaoDeGrau colacao) {
        repository.save(toJpa(colacao));
    }

    @Override
    public ColacaoDeGrau obter(ColacaoId id) {
        return repository.findById(id.getId()).map(this::toDomain).orElseThrow();
    }

    @Override
    public Optional<ColacaoDeGrau> pesquisarPorEstudante(EstudanteId estudanteId) {
        return repository.findByEstudanteId(estudanteId.getId()).map(this::toDomain);
    }

    @Override
    public Optional<ColacaoResumo> buscarPorEstudante(int estudanteId) {
        return repository.findByEstudanteId(estudanteId).map(this::toResumo);
    }

    @Override
    public Optional<ColacaoResumo> buscarPorId(int id) {
        return repository.findById(id).map(this::toResumo);
    }

    private ColacaoJpaEntity toJpa(ColacaoDeGrau c) {
        var jpa = repository.findById(c.getId().getId()).orElseGet(ColacaoJpaEntity::new);
        jpa.id = c.getId().getId();
        jpa.estudanteId = c.getEstudanteId().getId();
        jpa.integralizacaoId = c.getIntegralizacaoId().getId();
        jpa.dataAptidaoAprovada = c.getDataAptidaoAprovada();
        jpa.dataCerimonia = c.getDataCerimonia();
        jpa.local = c.getLocal();
        return jpa;
    }

    private ColacaoDeGrau toDomain(ColacaoJpaEntity jpa) {
        return ColacaoDeGrau.reconstituir(
                new ColacaoId(jpa.id),
                new EstudanteId(jpa.estudanteId),
                new IntegralizacaoId(jpa.integralizacaoId),
                jpa.dataAptidaoAprovada,
                jpa.dataCerimonia,
                jpa.local);
    }

    private ColacaoResumo toResumo(ColacaoJpaEntity jpa) {
        return new ColacaoResumo(
                jpa.id,
                jpa.estudanteId,
                jpa.integralizacaoId,
                jpa.dataAptidaoAprovada != null ? jpa.dataAptidaoAprovada.toString() : null,
                jpa.dataCerimonia != null ? jpa.dataCerimonia.toString() : null,
                jpa.local);
    }
}
