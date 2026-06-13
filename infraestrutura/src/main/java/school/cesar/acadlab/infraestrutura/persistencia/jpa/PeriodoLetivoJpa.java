package school.cesar.acadlab.infraestrutura.persistencia.jpa;

import java.time.LocalDate;
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
import school.cesar.acadlab.aplicacao.periodoletivo.PeriodoLetivoRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.periodoletivo.PeriodoLetivoResumo;
import school.cesar.acadlab.dominio.periodoletivo.PeriodoLetivoId;
import school.cesar.acadlab.dominio.periodoletivo.StatusPeriodoLetivo;
import school.cesar.acadlab.dominio.periodoletivo.TipoJanela;
import school.cesar.acadlab.dominio.periodoletivo.curso.CursoId;
import school.cesar.acadlab.dominio.periodoletivo.janelaacademica.JanelaAcademica;
import school.cesar.acadlab.dominio.periodoletivo.periodo.PeriodoLetivo;
import school.cesar.acadlab.dominio.periodoletivo.periodo.PeriodoLetivoRepositorio;

@Entity
@Table(name = "PERIODO_LETIVO")
class PeriodoLetivoJpa {
    @Id
    int id;
    int cursoId;
    int ano;
    int semestre;
    LocalDate dataInicio;
    LocalDate dataFim;

    @Enumerated(EnumType.STRING)
    StatusPeriodoLetivo status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "JANELA_ACADEMICA", joinColumns = @JoinColumn(name = "periodoLetivoId"))
    List<JanelaAcademicaJpa> janelas;
}

@Embeddable
class JanelaAcademicaJpa {
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    TipoJanela tipo;

    @Column(name = "dataInicio")
    LocalDate dataInicio;

    @Column(name = "dataFim")
    LocalDate dataFim;
}

interface PeriodoLetivoJpaRepository extends JpaRepository<PeriodoLetivoJpa, Integer> {
    List<PeriodoLetivoJpa> findByCursoId(int cursoId);

    boolean existsByCursoIdAndStatusNotAndDataFimGreaterThanEqualAndDataInicioLessThanEqual(
            int cursoId, StatusPeriodoLetivo status, LocalDate inicio, LocalDate fim);

    @Query("SELECT COALESCE(MAX(p.id), 0) + 1 FROM PeriodoLetivoJpa p")
    int proximoId();
}

@Repository
class PeriodoLetivoRepositorioImpl implements PeriodoLetivoRepositorio, PeriodoLetivoRepositorioAplicacao {
    @Autowired
    PeriodoLetivoJpaRepository jpaRepository;

    @Override
    public PeriodoLetivoId proximoId() {
        return new PeriodoLetivoId(jpaRepository.proximoId());
    }

    @Override
    public void salvar(PeriodoLetivo periodo) {
        jpaRepository.save(toJpa(periodo));
    }

    @Override
    public PeriodoLetivo obter(PeriodoLetivoId id) {
        return toDomain(jpaRepository.findById(id.getId()).orElseThrow());
    }

    @Override
    public List<PeriodoLetivo> pesquisarPorCurso(CursoId cursoId) {
        return jpaRepository.findByCursoId(cursoId.getId()).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<PeriodoLetivo> pesquisarPorCursoEStatus(CursoId cursoId, StatusPeriodoLetivo status) {
        return jpaRepository.findByCursoId(cursoId.getId()).stream()
                .filter(p -> p.status == status)
                .findFirst()
                .map(this::toDomain);
    }

    @Override
    public boolean existeSobreposicao(CursoId cursoId, LocalDate inicio, LocalDate fim) {
        return jpaRepository
                .existsByCursoIdAndStatusNotAndDataFimGreaterThanEqualAndDataInicioLessThanEqual(
                        cursoId.getId(), StatusPeriodoLetivo.CANCELADO, inicio, fim);
    }

    @Override
    public List<PeriodoLetivoResumo> pesquisarPorCurso(int cursoId) {
        return jpaRepository.findByCursoId(cursoId).stream()
                .map(this::toResumo)
                .toList();
    }

    private PeriodoLetivoJpa toJpa(PeriodoLetivo periodo) {
        var jpa = new PeriodoLetivoJpa();
        jpa.id = periodo.getId().valor();
        jpa.cursoId = periodo.getCursoId().valor();
        jpa.ano = periodo.getAno();
        jpa.semestre = periodo.getSemestre();
        jpa.dataInicio = periodo.getDataInicio();
        jpa.dataFim = periodo.getDataFim();
        jpa.status = periodo.getStatus();
        jpa.janelas = periodo.getJanelas().stream().map(j -> {
            var janelaJpa = new JanelaAcademicaJpa();
            janelaJpa.tipo = j.getTipo();
            janelaJpa.dataInicio = j.getInicio();
            janelaJpa.dataFim = j.getFim();
            return janelaJpa;
        }).toList();
        return jpa;
    }

    private PeriodoLetivo toDomain(PeriodoLetivoJpa jpa) {
        var periodo = PeriodoLetivo.reconstituir(
                new PeriodoLetivoId(jpa.id),
                new CursoId(jpa.cursoId),
                jpa.ano,
                jpa.semestre,
                jpa.dataInicio,
                jpa.dataFim,
                jpa.status,
                jpa.janelas.stream()
                        .map(j -> new JanelaAcademica(j.tipo, j.dataInicio, j.dataFim))
                        .toList());
        return periodo;
    }

    private PeriodoLetivoResumo toResumo(PeriodoLetivoJpa jpa) {
        var janelas = jpa.janelas.stream()
                .map(j -> new PeriodoLetivoResumo.JanelaResumo(j.tipo.name(), j.dataInicio, j.dataFim))
                .toList();
        return new PeriodoLetivoResumo(
                jpa.id, jpa.cursoId, jpa.ano, jpa.semestre,
                jpa.dataInicio, jpa.dataFim, jpa.status.name(), janelas);
    }
}
