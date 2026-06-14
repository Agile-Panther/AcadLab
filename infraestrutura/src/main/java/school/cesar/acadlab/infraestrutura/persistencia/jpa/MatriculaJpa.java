package school.cesar.acadlab.infraestrutura.persistencia.jpa;

import java.time.DayOfWeek;
import java.time.LocalTime;
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
import school.cesar.acadlab.aplicacao.matricula.MatriculaResumo;
import school.cesar.acadlab.aplicacao.matricula.MatriculaRepositorioAplicacao;
import school.cesar.acadlab.dominio.matricula.matricula.CoordenadorId;
import school.cesar.acadlab.dominio.matricula.matricula.DisciplinaId;
import school.cesar.acadlab.dominio.matricula.matricula.EstudanteId;
import school.cesar.acadlab.dominio.matricula.matricula.ExcecaoMatricula;
import school.cesar.acadlab.dominio.matricula.matricula.HorarioAula;
import school.cesar.acadlab.dominio.matricula.matricula.ItemMatricula;
import school.cesar.acadlab.dominio.matricula.matricula.Matricula;
import school.cesar.acadlab.dominio.matricula.matricula.MatriculaId;
import school.cesar.acadlab.dominio.matricula.matricula.MatriculaRepositorio;
import school.cesar.acadlab.dominio.matricula.matricula.PeriodoLetivoId;
import school.cesar.acadlab.dominio.matricula.matricula.StatusItemMatricula;
import school.cesar.acadlab.dominio.matricula.matricula.StatusMatricula;
import school.cesar.acadlab.dominio.matricula.matricula.TurmaId;

@Entity
@Table(name = "MATRICULA")
class MatriculaJpa {
    @Id
    int id;
    int estudanteId;
    int periodoLetivoId;
    int limiteCreditos;

    @Enumerated(EnumType.STRING)
    StatusMatricula status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ITEM_MATRICULA", joinColumns = @JoinColumn(name = "matriculaId"))
    List<ItemMatriculaJpa> itens = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "EXCECAO_MATRICULA", joinColumns = @JoinColumn(name = "matriculaId"))
    List<ExcecaoMatriculaJpa> excecoes = new ArrayList<>();
}

@Embeddable
class ItemMatriculaJpa {
    int turmaId;
    int disciplinaId;
    int creditos;

    @Enumerated(EnumType.STRING)
    @Column(name = "statusItem")
    StatusItemMatricula status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "HORARIO_ITEM_MATRICULA", joinColumns = {
            @JoinColumn(name = "matriculaId", referencedColumnName = "matriculaId"),
            @JoinColumn(name = "turmaId", referencedColumnName = "turmaId")
    })
    List<HorarioAulaJpa> horarios = new ArrayList<>();
}

@Embeddable
class HorarioAulaJpa {
    @Enumerated(EnumType.STRING)
    DayOfWeek dia;
    LocalTime inicio;
    LocalTime fim;
}

@Embeddable
class ExcecaoMatriculaJpa {
    int disciplinaId;
    String motivo;
    boolean deferida;
    Integer coordenadorId;
}

interface MatriculaJpaRepository extends JpaRepository<MatriculaJpa, Integer> {
    List<MatriculaJpa> findByEstudanteId(int estudanteId);

    @Query("SELECT COALESCE(MAX(m.id), 0) + 1 FROM MatriculaJpa m")
    int proximoId();
}

@Repository
class MatriculaRepositorioImpl implements MatriculaRepositorio, MatriculaRepositorioAplicacao {

    @Autowired
    MatriculaJpaRepository jpaRepository;

    @Override
    public MatriculaId proximaMatriculaId() {
        return new MatriculaId(jpaRepository.proximoId());
    }

    @Override
    public void salvar(Matricula matricula) {
        jpaRepository.save(toJpa(matricula));
    }

    @Override
    public Optional<Matricula> buscarPorId(MatriculaId id) {
        return jpaRepository.findById(id.getId()).map(this::toDomain);
    }

    @Override
    public List<Matricula> buscarPorEstudante(EstudanteId estudanteId) {
        return jpaRepository.findByEstudanteId(estudanteId.getId()).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public List<MatriculaResumo> buscarPorEstudante(int estudanteId) {
        return jpaRepository.findByEstudanteId(estudanteId).stream()
                .map(this::toResumo).toList();
    }

    @Override
    public Optional<MatriculaResumo> buscarPorId(int id) {
        return jpaRepository.findById(id).map(this::toResumo);
    }

    private MatriculaJpa toJpa(Matricula matricula) {
        var jpa = jpaRepository.findById(matricula.getId().getId()).orElseGet(MatriculaJpa::new);
        jpa.id = matricula.getId().getId();
        jpa.estudanteId = matricula.getEstudanteId().getId();
        jpa.periodoLetivoId = matricula.getPeriodoLetivoId().getId();
        jpa.limiteCreditos = matricula.getLimiteCreditos();
        jpa.status = matricula.getStatus();

        jpa.itens.clear();
        for (var item : matricula.getItens()) {
            var itemJpa = new ItemMatriculaJpa();
            itemJpa.turmaId = item.getTurmaId().getId();
            itemJpa.disciplinaId = item.getDisciplinaId().getId();
            itemJpa.creditos = item.getCreditos();
            itemJpa.status = item.getStatus();
            for (var h : item.getHorarios()) {
                var hJpa = new HorarioAulaJpa();
                hJpa.dia = h.getDia();
                hJpa.inicio = h.getInicio();
                hJpa.fim = h.getFim();
                itemJpa.horarios.add(hJpa);
            }
            jpa.itens.add(itemJpa);
        }

        jpa.excecoes.clear();
        for (var excecao : matricula.getExcecoes()) {
            var excJpa = new ExcecaoMatriculaJpa();
            excJpa.disciplinaId = excecao.getDisciplinaId().getId();
            excJpa.motivo = excecao.getMotivo();
            excJpa.deferida = excecao.isDeferida();
            excJpa.coordenadorId = excecao.getCoordenadorId() != null
                    ? excecao.getCoordenadorId().getId() : null;
            jpa.excecoes.add(excJpa);
        }

        return jpa;
    }

    private Matricula toDomain(MatriculaJpa jpa) {
        var itens = jpa.itens.stream().map(i -> {
            var horarios = i.horarios.stream()
                    .map(h -> new HorarioAula(h.dia, h.inicio, h.fim))
                    .toList();
            return ItemMatricula.reconstituir(
                    new TurmaId(i.turmaId), new DisciplinaId(i.disciplinaId),
                    i.creditos, horarios, i.status);
        }).toList();

        var excecoes = jpa.excecoes.stream().map(e ->
                ExcecaoMatricula.reconstituir(
                        new DisciplinaId(e.disciplinaId), e.motivo, e.deferida,
                        e.coordenadorId != null ? new CoordenadorId(e.coordenadorId) : null)
        ).toList();

        return Matricula.reconstituir(
                new MatriculaId(jpa.id),
                new EstudanteId(jpa.estudanteId),
                new PeriodoLetivoId(jpa.periodoLetivoId),
                jpa.limiteCreditos,
                jpa.status,
                itens,
                excecoes);
    }

    private MatriculaResumo toResumo(MatriculaJpa jpa) {
        return new MatriculaResumo(jpa.id, jpa.estudanteId, jpa.periodoLetivoId,
                jpa.status.name());
    }
}
