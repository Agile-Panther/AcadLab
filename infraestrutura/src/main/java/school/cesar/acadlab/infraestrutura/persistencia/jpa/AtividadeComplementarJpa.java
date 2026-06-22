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
import school.cesar.acadlab.dominio.atividadescomplementares.VerificadorCertificadoDuplicado;
import school.cesar.acadlab.dominio.atividadescomplementares.VerificadorContabilizacaoIntegralizacao;
import school.cesar.acadlab.dominio.atividadescomplementares.VerificadorLimiteCategoria;
import school.cesar.acadlab.dominio.atividadescomplementares.VerificadorVinculoEstudante;
import school.cesar.acadlab.dominio.atividadescomplementares.atividade.AtividadeComplementar;
import school.cesar.acadlab.dominio.atividadescomplementares.atividade.AtividadeComplementarRepositorio;
import school.cesar.acadlab.dominio.matricula.matricula.StatusMatricula;

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
    boolean contabilizadaIntegralizacao;

    @Enumerated(EnumType.STRING)
    StatusAtividade status;
}

interface AtividadeComplementarJpaRepository extends JpaRepository<AtividadeComplementarJpa, Integer> {
    List<AtividadeComplementarJpa> findByEstudanteId(int estudanteId);
    List<AtividadeComplementarJpa> findByStatus(StatusAtividade status);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(MAX(a.id), 0) + 1 FROM AtividadeComplementarJpa a")
    int proximoId();
}

@Repository
class AtividadeComplementarRepositorioImpl implements
        AtividadeComplementarRepositorio,
        AtividadeComplementarRepositorioAplicacao,
        VerificadorVinculoEstudante,
        VerificadorCertificadoDuplicado,
        VerificadorLimiteCategoria,
        VerificadorContabilizacaoIntegralizacao {
    @Autowired
    AtividadeComplementarJpaRepository repositorio;

    @Autowired
    MatriculaJpaRepository matriculaRepositorio;

    @Autowired
    CategoriaAtividadeJpaRepository categoriaRepositorio;

    @Override
    public AtividadeComplementarId proximoId() {
        return new AtividadeComplementarId(repositorio.proximoId());
    }

    @Override
    public void salvar(AtividadeComplementar atividade) {
        var jpa = toJpa(atividade);
        repositorio.findById(jpa.id).ifPresent(
                existente -> jpa.contabilizadaIntegralizacao = existente.contabilizadaIntegralizacao);
        repositorio.save(jpa);
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
                        jpa.status.name(),
                        jpa.dataRealizacao,
                        jpa.identificadorCertificado))
                .toList();
    }

    @Override
    public List<AtividadeComplementarResumo> pesquisarPorStatus(String status) {
        return repositorio.findByStatus(StatusAtividade.valueOf(status)).stream()
                .map(jpa -> new AtividadeComplementarResumo(
                        jpa.id,
                        jpa.estudanteId,
                        jpa.categoriaId,
                        jpa.descricao,
                        jpa.horasSubmetidas,
                        jpa.horasAprovadas,
                        jpa.status.name(),
                        jpa.dataRealizacao,
                        jpa.identificadorCertificado))
                .toList();
    }

    @Override
    public boolean estaNoVinculo(EstudanteId estudanteId, LocalDate data) {
        return matriculaRepositorio.findByEstudanteId(estudanteId.valor()).stream()
                .anyMatch(matricula -> matricula.status == StatusMatricula.CONFIRMADA);
    }

    @Override
    public boolean jaUtilizado(EstudanteId estudanteId, String identificadorCertificado) {
        return repositorio.findByEstudanteId(estudanteId.valor()).stream()
                .anyMatch(atividade -> java.util.Objects.equals(
                        atividade.identificadorCertificado, identificadorCertificado));
    }

    @Override
    public boolean excedeLimite(
            EstudanteId estudanteId,
            CategoriaAtividadeId categoriaId,
            int horasAdicionais) {
        var categoria = categoriaRepositorio.findById(categoriaId.valor())
                .orElseThrow(() -> new IllegalArgumentException("categoria de atividade não encontrada"));
        int horasDeferidas = repositorio.findByEstudanteId(estudanteId.valor()).stream()
                .filter(atividade -> atividade.categoriaId == categoriaId.valor())
                .filter(atividade -> atividade.status == StatusAtividade.DEFERIDA)
                .mapToInt(atividade -> atividade.horasAprovadas)
                .sum();
        return horasDeferidas + horasAdicionais > categoria.limiteHoras;
    }

    @Override
    public boolean foiContabilizada(AtividadeComplementarId id) {
        return repositorio.findById(id.valor())
                .map(atividade -> atividade.contabilizadaIntegralizacao)
                .orElse(false);
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
