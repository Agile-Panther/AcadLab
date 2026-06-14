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
import school.cesar.acadlab.aplicacao.mobilidadeacademica.MobilidadeAcademicaRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.mobilidadeacademica.MobilidadeAcademicaResumo;
import school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade.CoordenadorId;
import school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade.DisciplinaId;
import school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade.EstudanteId;
import school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade.ItemPlanoEstudos;
import school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade.MobilidadeAcademica;
import school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade.MobilidadeAcademicaId;
import school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade.MobilidadeAcademicaRepositorio;
import school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade.StatusItemPlano;
import school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade.StatusMobilidade;

@Entity
@Table(name = "MOBILIDADE_ACADEMICA")
class MobilidadeAcademicaJpa {
    @Id
    int id;
    int estudanteId;
    String instituicaoDestino;

    @Enumerated(EnumType.STRING)
    StatusMobilidade status;

    Integer coordenadorAutorizacaoId;
    LocalDate dataInicioPeriodoExterno;
    String justificativaCancelamento;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ITEM_PLANO_ESTUDOS", joinColumns = @JoinColumn(name = "mobilidadeId"))
    List<ItemPlanoEstudosJpa> planoEstudos = new ArrayList<>();
}

@Embeddable
class ItemPlanoEstudosJpa {
    @Column(name = "disciplinaExternaId")
    int disciplinaExternaId;

    @Column(name = "disciplinaEquivalenteId")
    int disciplinaEquivalenteId;

    @Column(name = "cargaHorariaExterna")
    int cargaHorariaExterna;

    @Column(name = "cargaHorariaEquivalente")
    int cargaHorariaEquivalente;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    StatusItemPlano status;

    @Column(name = "comprovanteAnexado")
    boolean comprovanteAnexado;

    @Column(name = "resultadoRegistrado")
    boolean resultadoRegistrado;
}

interface MobilidadeAcademicaJpaRepository extends JpaRepository<MobilidadeAcademicaJpa, Integer> {
    List<MobilidadeAcademicaJpa> findByEstudanteId(int estudanteId);

    @Query("SELECT COALESCE(MAX(m.id), 0) + 1 FROM MobilidadeAcademicaJpa m")
    int proximoId();
}

@Repository
class MobilidadeAcademicaRepositorioImpl implements MobilidadeAcademicaRepositorio, MobilidadeAcademicaRepositorioAplicacao {

    @Autowired
    MobilidadeAcademicaJpaRepository repository;

    @Override
    public MobilidadeAcademicaId proximaMobilidadeId() {
        return new MobilidadeAcademicaId(repository.proximoId());
    }

    @Override
    public void salvar(MobilidadeAcademica mobilidade) {
        repository.save(toJpa(mobilidade));
    }

    @Override
    public Optional<MobilidadeAcademica> buscarPorId(MobilidadeAcademicaId id) {
        return repository.findById(id.getId()).map(this::toDomain);
    }

    @Override
    public List<MobilidadeAcademica> buscarPorEstudante(EstudanteId estudanteId) {
        return repository.findByEstudanteId(estudanteId.getId()).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<MobilidadeAcademicaResumo> buscarPorEstudante(int estudanteId) {
        return repository.findByEstudanteId(estudanteId).stream()
                .map(this::toResumo)
                .toList();
    }

    @Override
    public Optional<MobilidadeAcademicaResumo> buscarPorId(int id) {
        return repository.findById(id).map(this::toResumo);
    }

    private MobilidadeAcademicaJpa toJpa(MobilidadeAcademica m) {
        var jpa = repository.findById(m.getId().getId()).orElseGet(MobilidadeAcademicaJpa::new);
        jpa.id = m.getId().getId();
        jpa.estudanteId = m.getEstudanteId().getId();
        jpa.instituicaoDestino = m.getInstituicaoDestino();
        jpa.status = m.getStatus();
        jpa.coordenadorAutorizacaoId = m.getCoordenadorAutorizacao() != null
                ? m.getCoordenadorAutorizacao().getId() : null;
        jpa.dataInicioPeriodoExterno = m.getDataInicioPeriodoExterno();
        jpa.justificativaCancelamento = m.getJustificativaCancelamento();

        jpa.planoEstudos.clear();
        for (var item : m.getPlanoEstudos()) {
            var itemJpa = new ItemPlanoEstudosJpa();
            itemJpa.disciplinaExternaId = item.getDisciplinaExterna().getId();
            itemJpa.disciplinaEquivalenteId = item.getDisciplinaEquivalente().getId();
            itemJpa.cargaHorariaExterna = item.getCargaHorariaExterna();
            itemJpa.cargaHorariaEquivalente = item.getCargaHorariaEquivalente();
            itemJpa.status = item.getStatus();
            itemJpa.comprovanteAnexado = item.isComprovanteAnexado();
            itemJpa.resultadoRegistrado = item.isResultadoRegistrado();
            jpa.planoEstudos.add(itemJpa);
        }

        return jpa;
    }

    private MobilidadeAcademica toDomain(MobilidadeAcademicaJpa jpa) {
        var itens = jpa.planoEstudos.stream()
                .map(i -> ItemPlanoEstudos.reconstituir(
                        new DisciplinaId(i.disciplinaExternaId),
                        new DisciplinaId(i.disciplinaEquivalenteId),
                        i.cargaHorariaExterna,
                        i.cargaHorariaEquivalente,
                        i.status,
                        i.comprovanteAnexado,
                        i.resultadoRegistrado))
                .toList();

        return MobilidadeAcademica.reconstituir(
                new MobilidadeAcademicaId(jpa.id),
                new EstudanteId(jpa.estudanteId),
                jpa.instituicaoDestino,
                jpa.status,
                jpa.coordenadorAutorizacaoId != null ? new CoordenadorId(jpa.coordenadorAutorizacaoId) : null,
                jpa.dataInicioPeriodoExterno,
                jpa.justificativaCancelamento,
                itens);
    }

    private MobilidadeAcademicaResumo toResumo(MobilidadeAcademicaJpa jpa) {
        return new MobilidadeAcademicaResumo(
                jpa.id,
                jpa.estudanteId,
                jpa.instituicaoDestino,
                jpa.status.name());
    }
}
