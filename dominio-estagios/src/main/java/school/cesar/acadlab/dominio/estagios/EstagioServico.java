package school.cesar.acadlab.dominio.estagios;

import static org.apache.commons.lang3.Validate.notNull;
import school.cesar.acadlab.dominio.estagios.estagio.Estagio;
import school.cesar.acadlab.dominio.estagios.estagio.EstagioId;
import school.cesar.acadlab.dominio.estagios.estagio.EstagioRepositorio;
import school.cesar.acadlab.dominio.estagios.estagio.StatusRelatorio;
import school.cesar.acadlab.dominio.estagios.oportunidade.CoordenadorId;
import school.cesar.acadlab.dominio.estagios.oportunidade.EmpresaId;
import school.cesar.acadlab.dominio.estagios.oportunidade.EstudanteId;
import school.cesar.acadlab.dominio.estagios.oportunidade.Oportunidade;
import school.cesar.acadlab.dominio.estagios.oportunidade.OportunidadeId;
import school.cesar.acadlab.dominio.estagios.oportunidade.OportunidadeRepositorio;

public class EstagioServico {

    private final OportunidadeRepositorio oportunidadeRepositorio;
    private final EstagioRepositorio estagioRepositorio;

    public EstagioServico(OportunidadeRepositorio oportunidadeRepositorio,
                          EstagioRepositorio estagioRepositorio) {
        notNull(oportunidadeRepositorio, "Repositório de oportunidades obrigatório");
        notNull(estagioRepositorio, "Repositório de estágios obrigatório");
        this.oportunidadeRepositorio = oportunidadeRepositorio;
        this.estagioRepositorio = estagioRepositorio;
    }

    public OportunidadeId cadastrarOportunidade(EmpresaId empresaId, String descricao, int cargaHorariaTotal) {
        var id = oportunidadeRepositorio.proximaOportunidadeId();
        var oportunidade = new Oportunidade(id, empresaId, descricao, cargaHorariaTotal);
        oportunidadeRepositorio.salvar(oportunidade);
        return id;
    }

    public void candidatar(OportunidadeId oportunidadeId, EstudanteId estudanteId) {
        notNull(oportunidadeId, "Id da oportunidade obrigatório");
        var oportunidade = obterOportunidade(oportunidadeId);
        oportunidade.candidatar(estudanteId);
        oportunidadeRepositorio.salvar(oportunidade);
    }

    public void encaminhar(OportunidadeId oportunidadeId, CoordenadorId coordenadorId) {
        notNull(oportunidadeId, "Id da oportunidade obrigatório");
        var oportunidade = obterOportunidade(oportunidadeId);
        oportunidade.encaminhar(coordenadorId);
        oportunidadeRepositorio.salvar(oportunidade);
    }

    public EstagioId confirmar(OportunidadeId oportunidadeId, EmpresaId empresaId) {
        notNull(oportunidadeId, "Id da oportunidade obrigatório");
        var oportunidade = obterOportunidade(oportunidadeId);
        oportunidade.confirmar(empresaId);
        oportunidadeRepositorio.salvar(oportunidade);

        var estagioId = estagioRepositorio.proximoEstagioId();
        var estagio = new Estagio(estagioId, oportunidade.getId(),
                oportunidade.getCandidato(), oportunidade.getEmpresaId());
        estagioRepositorio.salvar(estagio);
        return estagioId;
    }

    public void recusar(OportunidadeId oportunidadeId, EmpresaId empresaId) {
        notNull(oportunidadeId, "Id da oportunidade obrigatório");
        var oportunidade = obterOportunidade(oportunidadeId);
        oportunidade.recusar(empresaId);
        oportunidadeRepositorio.salvar(oportunidade);
    }

    public void submeterRelatorio(EstagioId estagioId, int numero, String descricao) {
        notNull(estagioId, "Id do estágio obrigatório");
        var estagio = obterEstagio(estagioId);
        estagio.submeterRelatorio(numero, descricao);
        estagioRepositorio.salvar(estagio);
    }

    public void avaliarRelatorio(EstagioId estagioId, int numero, StatusRelatorio resultado) {
        notNull(estagioId, "Id do estágio obrigatório");
        var estagio = obterEstagio(estagioId);
        estagio.avaliarRelatorio(numero, resultado);
        estagioRepositorio.salvar(estagio);
    }

    public void solicitarEncerramento(EstagioId estagioId) {
        notNull(estagioId, "Id do estágio obrigatório");
        var estagio = obterEstagio(estagioId);
        estagio.solicitarEncerramento();
        estagioRepositorio.salvar(estagio);
    }

    public void homologarEncerramento(EstagioId estagioId, CoordenadorId coordenadorId) {
        notNull(estagioId, "Id do estágio obrigatório");
        var estagio = obterEstagio(estagioId);
        estagio.homologarEncerramento(coordenadorId);
        estagioRepositorio.salvar(estagio);
    }

    private Oportunidade obterOportunidade(OportunidadeId id) {
        return oportunidadeRepositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Oportunidade não encontrada: " + id));
    }

    private Estagio obterEstagio(EstagioId id) {
        return estagioRepositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Estágio não encontrado: " + id));
    }
}
