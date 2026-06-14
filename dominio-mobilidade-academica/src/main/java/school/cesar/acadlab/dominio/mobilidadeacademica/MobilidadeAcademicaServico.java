package school.cesar.acadlab.dominio.mobilidadeacademica;

import static org.apache.commons.lang3.Validate.notNull;
import java.time.LocalDate;
import school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade.CoordenadorId;
import school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade.DisciplinaId;
import school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade.EstudanteId;
import school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade.MobilidadeAcademica;
import school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade.MobilidadeAcademicaId;
import school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade.MobilidadeAcademicaRepositorio;
import school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade.SecretariaId;

public class MobilidadeAcademicaServico {

    private final MobilidadeAcademicaRepositorio repositorio;

    public MobilidadeAcademicaServico(MobilidadeAcademicaRepositorio repositorio) {
        notNull(repositorio, "O repositório não pode ser nulo");
        this.repositorio = repositorio;
    }

    public MobilidadeAcademicaId solicitar(EstudanteId estudanteId, String instituicaoDestino) {
        notNull(estudanteId, "O id do estudante não pode ser nulo");
        var id = repositorio.proximaMobilidadeId();
        var mobilidade = new MobilidadeAcademica(id, estudanteId, instituicaoDestino);
        repositorio.salvar(mobilidade);
        return id;
    }

    public void autorizar(MobilidadeAcademicaId id, CoordenadorId coordenadorId) {
        notNull(id, "O id da mobilidade não pode ser nulo");
        var mobilidade = obter(id);
        mobilidade.autorizar(coordenadorId);
        repositorio.salvar(mobilidade);
    }

    public void iniciarPeriodoExterno(MobilidadeAcademicaId id, LocalDate dataInicio) {
        notNull(id, "O id da mobilidade não pode ser nulo");
        var mobilidade = obter(id);
        mobilidade.iniciarPeriodoExterno(dataInicio);
        repositorio.salvar(mobilidade);
    }

    public void adicionarItemPlano(MobilidadeAcademicaId id, DisciplinaId disciplinaExterna,
                                   DisciplinaId disciplinaEquivalente,
                                   int cargaHorariaExterna, int cargaHorariaEquivalente) {
        notNull(id, "O id da mobilidade não pode ser nulo");
        var mobilidade = obter(id);
        mobilidade.adicionarItemPlano(disciplinaExterna, disciplinaEquivalente,
                cargaHorariaExterna, cargaHorariaEquivalente);
        repositorio.salvar(mobilidade);
    }

    public void anexarComprovante(MobilidadeAcademicaId id, DisciplinaId disciplinaExterna) {
        notNull(id, "O id da mobilidade não pode ser nulo");
        var mobilidade = obter(id);
        mobilidade.anexarComprovante(disciplinaExterna);
        repositorio.salvar(mobilidade);
    }

    public void registrarResultado(MobilidadeAcademicaId id, DisciplinaId disciplinaExterna,
                                   SecretariaId secretariaId) {
        notNull(id, "O id da mobilidade não pode ser nulo");
        var mobilidade = obter(id);
        mobilidade.registrarResultado(disciplinaExterna, secretariaId);
        repositorio.salvar(mobilidade);
    }

    public void solicitarCancelamento(MobilidadeAcademicaId id, String justificativa, LocalDate hoje) {
        notNull(id, "O id da mobilidade não pode ser nulo");
        var mobilidade = obter(id);
        mobilidade.solicitarCancelamento(justificativa, hoje);
        repositorio.salvar(mobilidade);
    }

    public void confirmarCancelamento(MobilidadeAcademicaId id, CoordenadorId coordenadorId) {
        notNull(id, "O id da mobilidade não pode ser nulo");
        var mobilidade = obter(id);
        mobilidade.confirmarCancelamento(coordenadorId);
        repositorio.salvar(mobilidade);
    }

    private MobilidadeAcademica obter(MobilidadeAcademicaId id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Mobilidade não encontrada: " + id));
    }
}
