package school.cesar.acadlab.dominio.estagios;

import static org.apache.commons.lang3.Validate.notNull;
import java.time.LocalDate;
import school.cesar.acadlab.dominio.estagios.candidatura.Candidatura;
import school.cesar.acadlab.dominio.estagios.candidatura.CandidaturaId;
import school.cesar.acadlab.dominio.estagios.candidatura.CandidaturaRepositorio;
import school.cesar.acadlab.dominio.estagios.estagio.Estagio;
import school.cesar.acadlab.dominio.estagios.estagio.EstagioId;
import school.cesar.acadlab.dominio.estagios.estagio.EstagioRepositorio;
import school.cesar.acadlab.dominio.estagios.estagio.StatusRelatorio;
import school.cesar.acadlab.dominio.estagios.oportunidade.CoordenadorId;
import school.cesar.acadlab.dominio.estagios.oportunidade.CriterioElegibilidade;
import school.cesar.acadlab.dominio.estagios.oportunidade.EmpresaId;
import school.cesar.acadlab.dominio.estagios.oportunidade.EstudanteId;
import school.cesar.acadlab.dominio.estagios.oportunidade.MotivoEncerramento;
import school.cesar.acadlab.dominio.estagios.oportunidade.Oportunidade;
import school.cesar.acadlab.dominio.estagios.oportunidade.OportunidadeBase;
import school.cesar.acadlab.dominio.estagios.oportunidade.OportunidadeComCriterioElegibilidade;
import school.cesar.acadlab.dominio.estagios.oportunidade.OportunidadeComPrazoInscricao;
import school.cesar.acadlab.dominio.estagios.oportunidade.OportunidadeId;
import school.cesar.acadlab.dominio.estagios.oportunidade.OportunidadeRepositorio;
import school.cesar.acadlab.dominio.estagios.oportunidade.SetorEstagiosId;
import school.cesar.acadlab.dominio.estagios.oportunidade.VerificadorElegibilidade;

public class EstagioServico {

    private final OportunidadeRepositorio oportunidadeRepositorio;
    private final CandidaturaRepositorio candidaturaRepositorio;
    private final EstagioRepositorio estagioRepositorio;
    private final VerificadorElegibilidade verificadorElegibilidade;

    public EstagioServico(OportunidadeRepositorio oportunidadeRepositorio,
                          CandidaturaRepositorio candidaturaRepositorio,
                          EstagioRepositorio estagioRepositorio,
                          VerificadorElegibilidade verificadorElegibilidade) {
        notNull(oportunidadeRepositorio, "Repositório de oportunidades obrigatório");
        notNull(candidaturaRepositorio, "Repositório de candidaturas obrigatório");
        notNull(estagioRepositorio, "Repositório de estágios obrigatório");
        notNull(verificadorElegibilidade, "Verificador de elegibilidade obrigatório");
        this.oportunidadeRepositorio = oportunidadeRepositorio;
        this.candidaturaRepositorio = candidaturaRepositorio;
        this.estagioRepositorio = estagioRepositorio;
        this.verificadorElegibilidade = verificadorElegibilidade;
    }

    public OportunidadeId cadastrarOportunidade(EmpresaId empresaId, String descricao, int cargaHorariaTotal) {
        var id = oportunidadeRepositorio.proximaOportunidadeId();
        var oportunidade = new Oportunidade(id, empresaId, descricao, cargaHorariaTotal);
        oportunidadeRepositorio.salvar(oportunidade);
        return id;
    }

    public void publicarOportunidade(OportunidadeId oportunidadeId, SetorEstagiosId setorId) {
        var oportunidade = obterOportunidade(oportunidadeId);
        oportunidade.publicar(setorId);
        oportunidadeRepositorio.salvar(oportunidade);
    }

    public void definirCriterios(OportunidadeId oportunidadeId, SetorEstagiosId setorId,
                                  CriterioElegibilidade criterio) {
        var oportunidade = obterOportunidade(oportunidadeId);
        oportunidade.definirCriterios(setorId, criterio);
        oportunidadeRepositorio.salvar(oportunidade);
    }

    public void encerrarOportunidade(OportunidadeId oportunidadeId, MotivoEncerramento motivo) {
        var oportunidade = obterOportunidade(oportunidadeId);
        oportunidade.encerrar(motivo);
        oportunidadeRepositorio.salvar(oportunidade);
    }

    public CandidaturaId registrarCandidatura(OportunidadeId oportunidadeId, EstudanteId estudanteId,
                                               LocalDate dataAtual) {
        var base = obterOportunidade(oportunidadeId);

        OportunidadeBase oportunidade = base;
        if (base.getCriterioElegibilidade() != null) {
            oportunidade = new OportunidadeComCriterioElegibilidade(oportunidade,
                    base.getCriterioElegibilidade(), verificadorElegibilidade);
        }

        oportunidade.validarCandidatura(estudanteId);

        var candidaturaId = candidaturaRepositorio.proximaCandidaturaId();
        var candidatura = new Candidatura(candidaturaId, oportunidadeId, estudanteId);
        candidaturaRepositorio.salvar(candidatura);
        return candidaturaId;
    }

    public CandidaturaId registrarCandidaturaComPrazo(OportunidadeId oportunidadeId, EstudanteId estudanteId,
                                                       LocalDate prazoInscricao, LocalDate dataAtual) {
        var base = obterOportunidade(oportunidadeId);

        OportunidadeBase oportunidade = new OportunidadeComPrazoInscricao(base, prazoInscricao, dataAtual);
        if (base.getCriterioElegibilidade() != null) {
            oportunidade = new OportunidadeComCriterioElegibilidade(oportunidade,
                    base.getCriterioElegibilidade(), verificadorElegibilidade);
        }

        oportunidade.validarCandidatura(estudanteId);

        var candidaturaId = candidaturaRepositorio.proximaCandidaturaId();
        var candidatura = new Candidatura(candidaturaId, oportunidadeId, estudanteId);
        candidaturaRepositorio.salvar(candidatura);
        return candidaturaId;
    }

    public void deferir(CandidaturaId candidaturaId) {
        var candidatura = obterCandidatura(candidaturaId);
        candidatura.deferir();
        candidaturaRepositorio.salvar(candidatura);
    }

    public void indeferir(CandidaturaId candidaturaId) {
        var candidatura = obterCandidatura(candidaturaId);
        candidatura.indeferir();
        candidaturaRepositorio.salvar(candidatura);
    }

    public void cancelarCandidatura(CandidaturaId candidaturaId) {
        var candidatura = obterCandidatura(candidaturaId);
        candidatura.cancelar();
        candidaturaRepositorio.salvar(candidatura);
    }

    public EstagioId encaminharEConfirmar(CandidaturaId candidaturaId, EmpresaId empresaId) {
        var candidatura = obterCandidatura(candidaturaId);
        candidatura.encaminhar();
        candidaturaRepositorio.salvar(candidatura);

        var oportunidade = obterOportunidade(candidatura.getOportunidadeId());
        var estagioId = estagioRepositorio.proximoEstagioId();
        var estagio = new Estagio(estagioId, oportunidade.getId(), candidaturaId,
                candidatura.getEstudanteId(), oportunidade.getEmpresaId());
        estagioRepositorio.salvar(estagio);
        return estagioId;
    }

    public void submeterRelatorio(EstagioId estagioId, int numero, String descricao) {
        var estagio = obterEstagio(estagioId);
        estagio.submeterRelatorio(numero, descricao);
        estagioRepositorio.salvar(estagio);
    }

    public void avaliarRelatorio(EstagioId estagioId, int numero, StatusRelatorio resultado) {
        var estagio = obterEstagio(estagioId);
        estagio.avaliarRelatorio(numero, resultado);
        estagioRepositorio.salvar(estagio);
    }

    public void solicitarEncerramento(EstagioId estagioId) {
        var estagio = obterEstagio(estagioId);
        estagio.solicitarEncerramento();
        estagioRepositorio.salvar(estagio);
    }

    public void homologarEncerramento(EstagioId estagioId, CoordenadorId coordenadorId) {
        var estagio = obterEstagio(estagioId);
        estagio.homologarEncerramento(coordenadorId);
        estagioRepositorio.salvar(estagio);
    }

    private Oportunidade obterOportunidade(OportunidadeId id) {
        return oportunidadeRepositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("oportunidade não encontrada"));
    }

    private Candidatura obterCandidatura(CandidaturaId id) {
        return candidaturaRepositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("candidatura não encontrada"));
    }

    private Estagio obterEstagio(EstagioId id) {
        return estagioRepositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("estágio não encontrado"));
    }
}
