package school.cesar.acadlab.infraestrutura.persistencia.jpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import school.cesar.acadlab.aplicacao.curriculo.MatrizCurricularRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.curriculo.MatrizCurricularResumo;
import school.cesar.acadlab.dominio.curriculo.CursoId;
import school.cesar.acadlab.dominio.curriculo.DisciplinaId;
import school.cesar.acadlab.dominio.curriculo.ItemMatriz;
import school.cesar.acadlab.dominio.curriculo.MatrizCurricular;
import school.cesar.acadlab.dominio.curriculo.MatrizCurricularId;
import school.cesar.acadlab.dominio.curriculo.MatrizCurricularRepositorio;
import school.cesar.acadlab.dominio.curriculo.StatusMatriz;
import school.cesar.acadlab.dominio.curriculo.TipoDisciplina;
import school.cesar.acadlab.dominio.curriculo.porta.ConsultaMatrizAtivaPorta;
import school.cesar.acadlab.dominio.curriculo.porta.ConsultaTurmasPorta;

@Entity
@Table(name = "MATRIZ_CURRICULAR")
class MatrizCurricularJpa {
    @Id
    int id;
    int cursoId;
    String nome;
    int cargaHorariaMinima;
    int creditosExigidos;
    int maximoTrancamentos;

    @Enumerated(EnumType.STRING)
    StatusMatriz status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ITEM_MATRIZ", joinColumns = @JoinColumn(name = "matrizId"))
    List<ItemMatrizJpa> itens = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "PRE_REQUISITO", joinColumns = @JoinColumn(name = "matrizId"))
    List<PreRequisitoJpa> preRequisitos = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "CORREQUISITO", joinColumns = @JoinColumn(name = "matrizId"))
    List<CorrequisitoJpa> correquisitos = new ArrayList<>();
}

@Embeddable
class ItemMatrizJpa {
    @Column(name = "disciplinaId")
    int disciplinaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    TipoDisciplina tipo;

    @Column(name = "cargaHoraria")
    int cargaHoraria;

    @Column(name = "creditos")
    int creditos;
}

@Embeddable
class PreRequisitoJpa {
    @Column(name = "disciplinaId")
    int disciplinaId;

    @Column(name = "preRequisitoId")
    int preRequisitoId;
}

@Embeddable
class CorrequisitoJpa {
    @Column(name = "disciplinaId")
    int disciplinaId;

    @Column(name = "corequisitoId")
    int corequisitoId;
}

interface MatrizCurricularJpaRepository extends JpaRepository<MatrizCurricularJpa, Integer> {
    List<MatrizCurricularJpa> findByCursoId(int cursoId);

    @Query("SELECT COALESCE(MAX(m.id), 0) + 1 FROM MatrizCurricularJpa m")
    int proximoId();

    boolean existsByCursoIdAndStatus(int cursoId, StatusMatriz status);
}

@Repository
class MatrizCurricularRepositorioImpl implements MatrizCurricularRepositorio,
        MatrizCurricularRepositorioAplicacao, ConsultaMatrizAtivaPorta, ConsultaTurmasPorta {

    @Autowired
    MatrizCurricularJpaRepository repository;

    @Override
    public MatrizCurricularId proximaMatrizId() {
        return new MatrizCurricularId(repository.proximoId());
    }

    @Override
    public void salvar(MatrizCurricular matriz) {
        repository.save(toJpa(matriz));
    }

    @Override
    public Optional<MatrizCurricular> buscarPorId(MatrizCurricularId id) {
        return repository.findById(id.getValor()).map(this::toDomain);
    }

    @Override
    public List<MatrizCurricular> buscarPorCurso(CursoId cursoId) {
        return repository.findByCursoId(cursoId.getValor()).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<MatrizCurricularResumo> buscarPorCurso(int cursoId) {
        return repository.findByCursoId(cursoId).stream()
                .map(this::toResumo)
                .toList();
    }

    @Override
    public Optional<MatrizCurricularResumo> buscarPorId(int id) {
        return repository.findById(id).map(this::toResumo);
    }

    @Override
    public boolean existeMatrizAtivaParaCurso(CursoId cursoId) {
        return repository.existsByCursoIdAndStatus(cursoId.getValor(), StatusMatriz.ATIVA);
    }

    @Override
    public boolean existeTurmaParaDisciplina(DisciplinaId disciplinaId) {
        // TODO: conectar ao dominio-oferta-academica quando implementado
        return false;
    }

    private MatrizCurricularJpa toJpa(MatrizCurricular m) {
        var jpa = repository.findById(m.getId().getValor()).orElseGet(MatrizCurricularJpa::new);
        jpa.id = m.getId().getValor();
        jpa.cursoId = m.getCursoId().getValor();
        jpa.nome = m.getNome();
        jpa.cargaHorariaMinima = m.getCargaHorariaMinima();
        jpa.creditosExigidos = m.getCreditosExigidos();
        jpa.maximoTrancamentos = m.getMaximoTrancamentos();
        jpa.status = m.getStatus();

        jpa.itens.clear();
        for (var item : m.getItens()) {
            var itemJpa = new ItemMatrizJpa();
            itemJpa.disciplinaId = item.getDisciplinaId().getValor();
            itemJpa.tipo = item.getTipo();
            itemJpa.cargaHoraria = item.getCargaHoraria();
            itemJpa.creditos = item.getCreditos();
            jpa.itens.add(itemJpa);
        }

        jpa.preRequisitos.clear();
        for (var entry : m.getPreRequisitos().entrySet()) {
            for (var dep : entry.getValue()) {
                var prJpa = new PreRequisitoJpa();
                prJpa.disciplinaId = entry.getKey().getValor();
                prJpa.preRequisitoId = dep.getValor();
                jpa.preRequisitos.add(prJpa);
            }
        }

        jpa.correquisitos.clear();
        for (var entry : m.getCorrequisitos().entrySet()) {
            for (var dep : entry.getValue()) {
                var cqJpa = new CorrequisitoJpa();
                cqJpa.disciplinaId = entry.getKey().getValor();
                cqJpa.corequisitoId = dep.getValor();
                jpa.correquisitos.add(cqJpa);
            }
        }

        return jpa;
    }

    private MatrizCurricular toDomain(MatrizCurricularJpa jpa) {
        List<ItemMatriz> itens = jpa.itens.stream()
                .map(i -> new ItemMatriz(
                        new DisciplinaId(i.disciplinaId),
                        i.tipo,
                        i.cargaHoraria,
                        i.creditos))
                .toList();

        Map<DisciplinaId, List<DisciplinaId>> preRequisitos = new HashMap<>();
        for (var pr : jpa.preRequisitos) {
            preRequisitos.computeIfAbsent(new DisciplinaId(pr.disciplinaId), k -> new ArrayList<>())
                    .add(new DisciplinaId(pr.preRequisitoId));
        }

        Map<DisciplinaId, List<DisciplinaId>> correquisitos = new HashMap<>();
        for (var cq : jpa.correquisitos) {
            correquisitos.computeIfAbsent(new DisciplinaId(cq.disciplinaId), k -> new ArrayList<>())
                    .add(new DisciplinaId(cq.corequisitoId));
        }

        return MatrizCurricular.reconstituir(
                new MatrizCurricularId(jpa.id),
                new CursoId(jpa.cursoId),
                jpa.nome,
                jpa.cargaHorariaMinima,
                jpa.creditosExigidos,
                jpa.maximoTrancamentos,
                jpa.status,
                itens,
                preRequisitos,
                correquisitos);
    }

    private MatrizCurricularResumo toResumo(MatrizCurricularJpa jpa) {
        return new MatrizCurricularResumo(
                jpa.id,
                jpa.cursoId,
                jpa.nome,
                jpa.status.name());
    }
}
