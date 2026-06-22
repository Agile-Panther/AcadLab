package school.cesar.acadlab.infraestrutura.persistencia.jpa;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

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

import school.cesar.acadlab.aplicacao.ofertaturmas.ProfessorRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.ofertaturmas.ProfessorResumo;
import school.cesar.acadlab.aplicacao.ofertaturmas.SalaRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.ofertaturmas.SalaResumo;
import school.cesar.acadlab.aplicacao.ofertaturmas.TurmaRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.ofertaturmas.TurmaResumo;
import school.cesar.acadlab.dominio.curriculo.StatusMatriz;
import school.cesar.acadlab.dominio.ofertaturmas.DisciplinaId;
import school.cesar.acadlab.dominio.ofertaturmas.PeriodoLetivoId;
import school.cesar.acadlab.dominio.ofertaturmas.professor.Professor;
import school.cesar.acadlab.dominio.ofertaturmas.professor.ProfessorId;
import school.cesar.acadlab.dominio.ofertaturmas.professor.ProfessorRepositorio;
import school.cesar.acadlab.dominio.ofertaturmas.sala.Sala;
import school.cesar.acadlab.dominio.ofertaturmas.sala.SalaId;
import school.cesar.acadlab.dominio.ofertaturmas.sala.SalaRepositorio;
import school.cesar.acadlab.dominio.ofertaturmas.turma.HorarioAula;
import school.cesar.acadlab.dominio.ofertaturmas.turma.ModalidadeTurma;
import school.cesar.acadlab.dominio.ofertaturmas.turma.StatusTurma;
import school.cesar.acadlab.dominio.ofertaturmas.turma.Turma;
import school.cesar.acadlab.dominio.ofertaturmas.turma.TurmaId;
import school.cesar.acadlab.dominio.ofertaturmas.turma.TurmaRepositorio;

// ===================== JPA Entities =====================

@Entity
@Table(name = "SALA")
class SalaJpa {
    @Id int id;
    String nome;
    int capacidade;
    boolean ativa;
}

@Entity
@Table(name = "PROFESSOR")
class ProfessorJpa {
    @Id int id;
    String nome;
    boolean ativo;
}

@Entity
@Table(name = "TURMA")
class TurmaJpa {
    @Id int id;
    int periodoLetivoId;
    int disciplinaId;
    Integer professorId;
    Integer salaId;

    @Enumerated(EnumType.STRING)
    ModalidadeTurma modalidade;

    int capacidade;
    boolean listaEsperaHabilitada;

    @Enumerated(EnumType.STRING)
    StatusTurma status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "HORARIO_AULA", joinColumns = @JoinColumn(name = "turmaId"))
    List<HorarioAulaTurmaJpa> horarios = new ArrayList<>();
}

@Embeddable
class HorarioAulaTurmaJpa {
    @Enumerated(EnumType.STRING)
    @Column(name = "diaSemana") DayOfWeek diaSemana;
    @Column(name = "horaInicio") LocalTime horaInicio;
    @Column(name = "horaFim") LocalTime horaFim;
}

// ===================== JPA Repositories =====================

interface SalaJpaRepository extends JpaRepository<SalaJpa, Integer> {
    List<SalaJpa> findByAtivaTrue();
    @Query("SELECT COALESCE(MAX(s.id), 0) + 1 FROM SalaJpa s") int proximoId();
}

interface ProfessorJpaRepository extends JpaRepository<ProfessorJpa, Integer> {
    List<ProfessorJpa> findByAtivoTrue();
    @Query("SELECT COALESCE(MAX(p.id), 0) + 1 FROM ProfessorJpa p") int proximoId();
}

interface TurmaJpaRepository extends JpaRepository<TurmaJpa, Integer> {
    List<TurmaJpa> findByPeriodoLetivoId(int periodoLetivoId);
    List<TurmaJpa> findByProfessorIdAndPeriodoLetivoId(int professorId, int periodoLetivoId);
    List<TurmaJpa> findBySalaIdAndPeriodoLetivoId(int salaId, int periodoLetivoId);
    List<TurmaJpa> findByDisciplinaIdAndPeriodoLetivoId(int disciplinaId, int periodoLetivoId);
    @Query("SELECT COALESCE(MAX(t.id), 0) + 1 FROM TurmaJpa t") int proximoId();
}

// ===================== Repository Implementations =====================

@Repository
class SalaRepositorioImpl implements SalaRepositorio, SalaRepositorioAplicacao {
    @Autowired SalaJpaRepository repository;

    @Override public SalaId proximoId() { return new SalaId(repository.proximoId()); }

    @Override public void salvar(Sala sala) {
        var jpa = repository.findById(sala.getId().getId()).orElseGet(SalaJpa::new);
        jpa.id = sala.getId().getId();
        jpa.nome = sala.getNome();
        jpa.capacidade = sala.getCapacidade();
        jpa.ativa = sala.isAtiva();
        repository.save(jpa);
    }

    @Override public Sala obter(SalaId id) {
        return repository.findById(id.getId())
                .map(this::toDomain)
                .orElseThrow(() -> new IllegalArgumentException("Sala não encontrada: " + id.getId()));
    }

    @Override public List<Sala> pesquisarAtivas() {
        return repository.findByAtivaTrue().stream().map(this::toDomain).toList();
    }

    @Override public Optional<SalaResumo> buscarPorId(int id) {
        return repository.findById(id).map(this::toResumo);
    }

    @Override public List<SalaResumo> listarAtivas() {
        return repository.findByAtivaTrue().stream().map(this::toResumo).toList();
    }

    private Sala toDomain(SalaJpa jpa) {
        var sala = new Sala(new SalaId(jpa.id), jpa.nome, jpa.capacidade);
        if (!jpa.ativa) sala.inativar();
        return sala;
    }

    private SalaResumo toResumo(SalaJpa jpa) {
        return new SalaResumo(jpa.id, jpa.nome, jpa.capacidade, jpa.ativa);
    }
}

@Repository
class ProfessorRepositorioImpl implements ProfessorRepositorio, ProfessorRepositorioAplicacao {
    @Autowired ProfessorJpaRepository repository;

    @Override public ProfessorId proximoId() { return new ProfessorId(repository.proximoId()); }

    @Override public void salvar(Professor professor) {
        var jpa = repository.findById(professor.getId().getId()).orElseGet(ProfessorJpa::new);
        jpa.id = professor.getId().getId();
        jpa.nome = professor.getNome();
        jpa.ativo = professor.isAtivo();
        repository.save(jpa);
    }

    @Override public Professor obter(ProfessorId id) {
        return repository.findById(id.getId())
                .map(this::toDomain)
                .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado: " + id.getId()));
    }

    @Override public List<Professor> pesquisarAtivos() {
        return repository.findByAtivoTrue().stream().map(this::toDomain).toList();
    }

    @Override public Optional<ProfessorResumo> buscarPorId(int id) {
        return repository.findById(id).map(this::toResumo);
    }

    @Override public List<ProfessorResumo> listarAtivos() {
        return repository.findByAtivoTrue().stream().map(this::toResumo).toList();
    }

    private Professor toDomain(ProfessorJpa jpa) {
        var p = new Professor(new ProfessorId(jpa.id), jpa.nome);
        if (!jpa.ativo) p.inativar();
        return p;
    }

    private ProfessorResumo toResumo(ProfessorJpa jpa) {
        return new ProfessorResumo(jpa.id, jpa.nome, jpa.ativo);
    }
}

@Repository
class TurmaRepositorioImpl implements TurmaRepositorio, TurmaRepositorioAplicacao {
    @Autowired TurmaJpaRepository repository;
    @Autowired MatrizCurricularJpaRepository matrizRepository;

    @Override public TurmaId proximoId() { return new TurmaId(repository.proximoId()); }

    @Override public void salvar(Turma turma) {
        var jpa = repository.findById(turma.getId().getId()).orElseGet(TurmaJpa::new);
        jpa.id = turma.getId().getId();
        jpa.periodoLetivoId = turma.getPeriodoLetivoId().getId();
        jpa.disciplinaId = turma.getDisciplinaId().getId();
        jpa.professorId = turma.getProfessorId() != null ? turma.getProfessorId().getId() : null;
        jpa.salaId = turma.getSalaId() != null ? turma.getSalaId().getId() : null;
        jpa.modalidade = turma.getModalidade();
        jpa.capacidade = turma.getCapacidade();
        jpa.listaEsperaHabilitada = turma.isListaEsperaHabilitada();
        jpa.status = turma.getStatus();
        jpa.horarios.clear();
        for (var h : turma.getHorarios()) {
            var hjpa = new HorarioAulaTurmaJpa();
            hjpa.diaSemana = h.getDiaSemana();
            hjpa.horaInicio = h.getHoraInicio();
            hjpa.horaFim = h.getHoraFim();
            jpa.horarios.add(hjpa);
        }
        repository.save(jpa);
    }

    @Override public Turma obter(TurmaId id) {
        return repository.findById(id.getId())
                .map(this::toDomain)
                .orElseThrow(() -> new IllegalArgumentException("Turma não encontrada: " + id.getId()));
    }

    @Override public List<Turma> pesquisarPorPeriodoLetivo(PeriodoLetivoId periodoId) {
        return repository.findByPeriodoLetivoId(periodoId.getId()).stream().map(this::toDomain).toList();
    }

    @Override public List<Turma> pesquisarPorProfessorEPeriodo(ProfessorId professorId, PeriodoLetivoId periodoId) {
        return repository.findByProfessorIdAndPeriodoLetivoId(professorId.getId(), periodoId.getId())
                .stream().map(this::toDomain).toList();
    }

    @Override public List<Turma> pesquisarPorSalaEPeriodo(SalaId salaId, PeriodoLetivoId periodoId) {
        return repository.findBySalaIdAndPeriodoLetivoId(salaId.getId(), periodoId.getId())
                .stream().map(this::toDomain).toList();
    }

    @Override public List<Turma> pesquisarPorDisciplinaEPeriodo(DisciplinaId disciplinaId, PeriodoLetivoId periodoId) {
        return repository.findByDisciplinaIdAndPeriodoLetivoId(disciplinaId.getId(), periodoId.getId())
                .stream().map(this::toDomain).toList();
    }

    @Override public Optional<TurmaResumo> buscarPorId(int id) {
        return repository.findById(id).map(this::toResumo);
    }

    @Override public List<TurmaResumo> listarPorPeriodo(int periodoLetivoId) {
        return repository.findByPeriodoLetivoId(periodoLetivoId).stream().map(this::toResumo).toList();
    }

    @Override public List<TurmaResumo> listarComFiltros(Integer periodoLetivoId, Integer cursoId,
            Integer disciplinaId, Integer professorId, String status) {
        var disciplinasDoCurso = disciplinasDaMatrizAtiva(cursoId);
        var statusFiltro = status == null || status.isBlank()
                ? null
                : StatusTurma.valueOf(status.toUpperCase(Locale.ROOT));

        return repository.findAll().stream()
                .filter(t -> periodoLetivoId == null || t.periodoLetivoId == periodoLetivoId)
                .filter(t -> cursoId == null || disciplinasDoCurso.contains(t.disciplinaId))
                .filter(t -> disciplinaId == null || t.disciplinaId == disciplinaId)
                .filter(t -> professorId == null || professorId.equals(t.professorId))
                .filter(t -> statusFiltro == null || t.status == statusFiltro)
                .map(this::toResumo)
                .toList();
    }

    private Turma toDomain(TurmaJpa jpa) {
        var horarios = jpa.horarios.stream()
                .map(h -> new HorarioAula(h.diaSemana, h.horaInicio, h.horaFim))
                .toList();
        return Turma.reconstituir(
                new TurmaId(jpa.id),
                new PeriodoLetivoId(jpa.periodoLetivoId),
                new DisciplinaId(jpa.disciplinaId),
                jpa.professorId != null ? new ProfessorId(jpa.professorId) : null,
                jpa.salaId != null ? new SalaId(jpa.salaId) : null,
                jpa.modalidade,
                jpa.capacidade,
                jpa.listaEsperaHabilitada,
                jpa.status,
                horarios);
    }

    private TurmaResumo toResumo(TurmaJpa jpa) {
        return new TurmaResumo(jpa.id, jpa.periodoLetivoId, jpa.disciplinaId,
                jpa.professorId, jpa.salaId,
                jpa.modalidade.name(), jpa.capacidade,
                jpa.listaEsperaHabilitada, jpa.status.name());
    }

    private Set<Integer> disciplinasDaMatrizAtiva(Integer cursoId) {
        if (cursoId == null) {
            return Set.of();
        }
        var disciplinas = new HashSet<Integer>();
        matrizRepository.findByCursoId(cursoId).stream()
                .filter(m -> m.status == StatusMatriz.ATIVA)
                .flatMap(m -> m.itens.stream())
                .forEach(i -> disciplinas.add(i.disciplinaId));
        return disciplinas;
    }
}
