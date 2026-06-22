package school.cesar.acadlab.infraestrutura.persistencia.jpa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import school.cesar.acadlab.aplicacao.gestaopedagogica.DiarioTurmaDetalhadoResumo;
import school.cesar.acadlab.aplicacao.gestaopedagogica.DiarioTurmaResumo;
import school.cesar.acadlab.aplicacao.gestaopedagogica.DiarioTurmaRepositorioAplicacao;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.Avaliacao;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.AvaliacaoId;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.DiarioTurma;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.DiarioTurmaId;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.DiarioTurmaRepositorio;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.EstudanteId;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.LancamentoFrequencia;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.PeriodoLetivoId;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.ProfessorId;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.RegistroAula;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.RegistroAulaId;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.ResultadoEstudante;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.SituacaoResultado;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.StatusDiario;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.TurmaId;

@Entity
@Table(name = "DIARIO_TURMA")
class DiarioTurmaJpa {
    @Id
    int id;
    int turmaId;
    int periodoLetivoId;
    int professorResponsavelId;
    LocalDate dataInicioPeriodo;
    LocalDate dataFimPeriodo;
    double mediaMinima;
    double frequenciaMinima;

    @Enumerated(EnumType.STRING)
    StatusDiario status;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "diarioId")
    List<RegistroAulaJpa> aulas = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "diarioId")
    List<AvaliacaoJpa> avaliacoes = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "LANCAMENTO_FREQUENCIA", joinColumns = @JoinColumn(name = "diarioId"))
    List<LancamentoFrequenciaJpa> frequencias = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ESTUDANTE_ATIVO_DIARIO", joinColumns = @JoinColumn(name = "diarioId"))
    @Column(name = "estudanteId")
    Set<Integer> estudantesAtivos = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "diarioId")
    List<ResultadoEstudanteJpa> resultados = new ArrayList<>();
}

@Entity
@Table(name = "REGISTRO_AULA")
class RegistroAulaJpa {
    @Id
    int id;
    int professorId;
    LocalDate data;
    String conteudo;
    boolean corrigido;
}

@Entity
@Table(name = "AVALIACAO_DIARIO")
class AvaliacaoJpa {
    @Id
    int id;
    String nome;
    double peso;
    LocalDate prazo;
}

@Embeddable
class LancamentoFrequenciaJpa {
    @Column(name = "aulaId")
    int aulaId;

    @Column(name = "estudanteId")
    int estudanteId;

    @Column(name = "presente")
    boolean presente;
}

@Entity
@Table(name = "RESULTADO_ESTUDANTE")
class ResultadoEstudanteJpa {
    @Id
    int id;
    int estudanteId;
    Double notaRecuperacao;

    @Enumerated(EnumType.STRING)
    SituacaoResultado situacao;

    boolean fechado;
    boolean revisaoSolicitada;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "NOTA_RESULTADO", joinColumns = @JoinColumn(name = "resultadoId"))
    @MapKeyColumn(name = "avaliacaoId")
    @Column(name = "nota")
    Map<Integer, Double> notas = new HashMap<>();
}

interface DiarioTurmaJpaRepository extends JpaRepository<DiarioTurmaJpa, Integer> {
    List<DiarioTurmaJpa> findByTurmaId(int turmaId);
    List<DiarioTurmaJpa> findByProfessorResponsavelId(int professorId);

    @Query("SELECT COALESCE(MAX(d.id), 0) + 1 FROM DiarioTurmaJpa d")
    int proximoId();
}

interface RegistroAulaJpaRepository extends JpaRepository<RegistroAulaJpa, Integer> {
    @Query("SELECT COALESCE(MAX(a.id), 0) + 1 FROM RegistroAulaJpa a")
    int proximoId();
}

interface AvaliacaoJpaRepository extends JpaRepository<AvaliacaoJpa, Integer> {
    @Query("SELECT COALESCE(MAX(a.id), 0) + 1 FROM AvaliacaoJpa a")
    int proximoId();
}

interface ResultadoEstudanteJpaRepository extends JpaRepository<ResultadoEstudanteJpa, Integer> {
    @Query("SELECT COALESCE(MAX(r.id), 0) + 1 FROM ResultadoEstudanteJpa r")
    int proximoId();
}

@Repository
class DiarioTurmaRepositorioImpl implements DiarioTurmaRepositorio, DiarioTurmaRepositorioAplicacao {
    @Autowired
    DiarioTurmaJpaRepository diarioRepository;

    @Autowired
    RegistroAulaJpaRepository aulaRepository;

    @Autowired
    AvaliacaoJpaRepository avaliacaoRepository;

    @Autowired
    ResultadoEstudanteJpaRepository resultadoRepository;

    @Override
    public DiarioTurmaId proximoId() {
        return new DiarioTurmaId(diarioRepository.proximoId());
    }

    @Override
    public RegistroAulaId proximoAulaId() {
        return new RegistroAulaId(aulaRepository.proximoId());
    }

    @Override
    public AvaliacaoId proximaAvaliacaoId() {
        return new AvaliacaoId(avaliacaoRepository.proximoId());
    }

    @Override
    public void salvar(DiarioTurma diario) {
        diarioRepository.save(toJpa(diario));
    }

    @Override
    public DiarioTurma obter(DiarioTurmaId id) {
        return toDomain(diarioRepository.findById(id.getId()).orElseThrow());
    }

    @Override
    public List<DiarioTurmaResumo> pesquisarPorTurma(int turmaId) {
        return diarioRepository.findByTurmaId(turmaId).stream()
                .map(this::toResumo)
                .toList();
    }

    @Override
    public List<DiarioTurmaResumo> pesquisarPorProfessor(int professorId) {
        return diarioRepository.findByProfessorResponsavelId(professorId).stream()
                .map(this::toResumo)
                .toList();
    }

    @Override
    public List<DiarioTurmaResumo> pesquisarTodos() {
        return diarioRepository.findAll().stream()
                .map(this::toResumo)
                .toList();
    }

    @Override
    public Optional<DiarioTurmaDetalhadoResumo> buscarDetalhado(int id) {
        return diarioRepository.findById(id).map(this::toDetalhado);
    }

    private DiarioTurmaJpa toJpa(DiarioTurma diario) {
        var jpa = diarioRepository.findById(diario.getId().getId()).orElseGet(DiarioTurmaJpa::new);
        jpa.id = diario.getId().getId();
        jpa.turmaId = diario.getTurmaId().getId();
        jpa.periodoLetivoId = diario.getPeriodoLetivoId().getId();
        jpa.professorResponsavelId = diario.getProfessorResponsavel().getId();
        jpa.dataInicioPeriodo = diario.getDataInicioPeriodo();
        jpa.dataFimPeriodo = diario.getDataFimPeriodo();
        jpa.mediaMinima = diario.getMediaMinima();
        jpa.frequenciaMinima = diario.getFrequenciaMinima();
        jpa.status = diario.getStatus();

        // merge por id para evitar delete+insert de entidade com mesma PK
        var aulasExistentes = new HashMap<Integer, RegistroAulaJpa>();
        jpa.aulas.forEach(a -> aulasExistentes.put(a.id, a));
        jpa.aulas.clear();
        for (var aula : diario.getAulas()) {
            var aulaJpa = aulasExistentes.getOrDefault(aula.getId().getId(), new RegistroAulaJpa());
            aulaJpa.id = aula.getId().getId();
            aulaJpa.professorId = aula.getProfessorId().getId();
            aulaJpa.data = aula.getData();
            aulaJpa.conteudo = aula.getConteudo();
            aulaJpa.corrigido = aula.isCorrigido();
            jpa.aulas.add(aulaJpa);
        }

        var avaliacoesExistentes = new HashMap<Integer, AvaliacaoJpa>();
        jpa.avaliacoes.forEach(a -> avaliacoesExistentes.put(a.id, a));
        jpa.avaliacoes.clear();
        for (var avaliacao : diario.getAvaliacoes()) {
            var avaliacaoJpa = avaliacoesExistentes.getOrDefault(avaliacao.getId().getId(), new AvaliacaoJpa());
            avaliacaoJpa.id = avaliacao.getId().getId();
            avaliacaoJpa.nome = avaliacao.getNome();
            avaliacaoJpa.peso = avaliacao.getPeso();
            avaliacaoJpa.prazo = avaliacao.getPrazo();
            jpa.avaliacoes.add(avaliacaoJpa);
        }

        // @ElementCollection: sem entidade própria, clear+re-add é seguro
        jpa.frequencias.clear();
        for (var freq : diario.getFrequencias()) {
            var freqJpa = new LancamentoFrequenciaJpa();
            freqJpa.aulaId = freq.getAulaId().getId();
            freqJpa.estudanteId = freq.getEstudanteId().getId();
            freqJpa.presente = freq.isPresente();
            jpa.frequencias.add(freqJpa);
        }

        jpa.estudantesAtivos.clear();
        for (var estudante : diario.getEstudantesAtivos()) {
            jpa.estudantesAtivos.add(estudante.getId());
        }

        // merge por estudanteId; novos recebem id sequencial
        var resultadosExistentes = new HashMap<Integer, ResultadoEstudanteJpa>();
        jpa.resultados.forEach(r -> resultadosExistentes.put(r.estudanteId, r));
        jpa.resultados.clear();
        int proximoResultadoId = resultadoRepository.proximoId();
        for (var resultado : diario.getResultados()) {
            var resultadoJpa = resultadosExistentes.get(resultado.getEstudanteId().getId());
            if (resultadoJpa == null) {
                resultadoJpa = new ResultadoEstudanteJpa();
                resultadoJpa.id = proximoResultadoId++;
            }
            resultadoJpa.estudanteId = resultado.getEstudanteId().getId();
            resultadoJpa.notaRecuperacao = resultado.getNotaRecuperacao();
            resultadoJpa.situacao = resultado.getSituacao();
            resultadoJpa.fechado = resultado.isFechado();
            resultadoJpa.revisaoSolicitada = resultado.isRevisaoSolicitada();
            resultadoJpa.notas.clear();
            for (var entry : resultado.getNotas().entrySet()) {
                resultadoJpa.notas.put(entry.getKey().getId(), entry.getValue());
            }
            jpa.resultados.add(resultadoJpa);
        }

        return jpa;
    }

    private DiarioTurma toDomain(DiarioTurmaJpa jpa) {
        var aulas = jpa.aulas.stream()
                .map(a -> RegistroAula.reconstituir(new RegistroAulaId(a.id), new ProfessorId(a.professorId),
                        a.data, a.conteudo, a.corrigido))
                .toList();

        var avaliacoes = jpa.avaliacoes.stream()
                .map(a -> new Avaliacao(new AvaliacaoId(a.id), a.nome, a.peso, a.prazo))
                .toList();

        var frequencias = jpa.frequencias.stream()
                .map(f -> new LancamentoFrequencia(new RegistroAulaId(f.aulaId),
                        new EstudanteId(f.estudanteId), f.presente))
                .toList();

        var resultados = jpa.resultados.stream().map(r -> {
            Map<AvaliacaoId, Double> notas = new HashMap<>();
            r.notas.forEach((k, v) -> notas.put(new AvaliacaoId(k), v));
            return ResultadoEstudante.reconstituir(
                    new EstudanteId(r.estudanteId), notas,
                    r.notaRecuperacao, r.situacao, r.fechado, r.revisaoSolicitada);
        }).toList();

        var estudantesAtivos = new HashSet<EstudanteId>();
        jpa.estudantesAtivos.forEach(id -> estudantesAtivos.add(new EstudanteId(id)));

        return DiarioTurma.reconstituir(
                new DiarioTurmaId(jpa.id),
                new TurmaId(jpa.turmaId),
                new PeriodoLetivoId(jpa.periodoLetivoId),
                new ProfessorId(jpa.professorResponsavelId),
                jpa.dataInicioPeriodo,
                jpa.dataFimPeriodo,
                jpa.mediaMinima,
                jpa.frequenciaMinima,
                jpa.status,
                aulas, avaliacoes, frequencias, resultados, estudantesAtivos);
    }

    private DiarioTurmaResumo toResumo(DiarioTurmaJpa jpa) {
        return new DiarioTurmaResumo(
                jpa.id, jpa.turmaId, jpa.periodoLetivoId, jpa.professorResponsavelId,
                jpa.dataInicioPeriodo, jpa.dataFimPeriodo,
                jpa.mediaMinima, jpa.frequenciaMinima, jpa.status.name(),
                jpa.aulas.size(), jpa.estudantesAtivos.size(), jpa.avaliacoes.size());
    }

    private DiarioTurmaDetalhadoResumo toDetalhado(DiarioTurmaJpa jpa) {
        var aulas = jpa.aulas.stream()
                .map(a -> new DiarioTurmaDetalhadoResumo.AulaResumo(
                        a.id, a.professorId, a.data, a.conteudo, a.corrigido))
                .toList();
        var avaliacoes = jpa.avaliacoes.stream()
                .map(a -> new DiarioTurmaDetalhadoResumo.AvaliacaoResumo(
                        a.id, a.nome, a.peso, a.prazo))
                .toList();
        var frequencias = jpa.frequencias.stream()
                .map(f -> new DiarioTurmaDetalhadoResumo.FrequenciaResumo(
                        f.aulaId, f.estudanteId, f.presente))
                .toList();
        var resultados = jpa.resultados.stream()
                .map(r -> new DiarioTurmaDetalhadoResumo.ResultadoResumo(
                        r.estudanteId,
                        r.situacao != null ? r.situacao.name() : null,
                        r.fechado, r.revisaoSolicitada, r.notaRecuperacao,
                        new HashMap<>(r.notas)))
                .toList();
        return new DiarioTurmaDetalhadoResumo(
                jpa.id, jpa.turmaId, jpa.periodoLetivoId, jpa.professorResponsavelId,
                jpa.dataInicioPeriodo, jpa.dataFimPeriodo,
                jpa.mediaMinima, jpa.frequenciaMinima, jpa.status.name(),
                aulas, avaliacoes, frequencias, resultados,
                new HashSet<>(jpa.estudantesAtivos));
    }
}
