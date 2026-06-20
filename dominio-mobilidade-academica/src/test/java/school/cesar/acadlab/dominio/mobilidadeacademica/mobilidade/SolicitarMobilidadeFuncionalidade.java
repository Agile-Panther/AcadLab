package school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.mobilidadeacademica.MobilidadeFuncionalidade;

public class SolicitarMobilidadeFuncionalidade {

    private final MobilidadeFuncionalidade ctx;
    private MobilidadeAcademica mobilidade;

    public SolicitarMobilidadeFuncionalidade(MobilidadeFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("um estudante com id {int} deseja mobilidade para {string}")
    public void um_estudante_com_id_deseja_mobilidade_para(int estudanteId, String instituicao) {
        var mobilidadeId = ctx.repositorio.proximaMobilidadeId();
        mobilidade = new MobilidadeAcademica(mobilidadeId, new EstudanteId(estudanteId), instituicao);
        ctx.repositorio.salvar(mobilidade);
    }

    @Quando("o estudante solicita a mobilidade acadêmica")
    public void o_estudante_solicita_a_mobilidade_academica() {
        // mobilidade já foi criada no Dado
    }

    @Entao("a mobilidade é registrada com status SOLICITADA")
    public void a_mobilidade_e_registrada_com_status_solicitada() {
        assertEquals(StatusMobilidade.SOLICITADA, mobilidade.getStatus());
    }

    @Quando("o coordenador com id {int} autoriza a mobilidade")
    public void o_coordenador_com_id_autoriza_a_mobilidade(int coordenadorId) {
        mobilidade.autorizar(new CoordenadorId(coordenadorId));
        ctx.repositorio.salvar(mobilidade);
    }

    @Entao("a mobilidade tem status AUTORIZADA")
    public void a_mobilidade_tem_status_autorizada() {
        assertEquals(StatusMobilidade.AUTORIZADA, mobilidade.getStatus());
    }

    @Dado("uma mobilidade acadêmica já autorizada para o estudante com id {int}")
    public void uma_mobilidade_ja_autorizada_para_o_estudante_com_id(int estudanteId) {
        var mobilidadeId = ctx.repositorio.proximaMobilidadeId();
        mobilidade = new MobilidadeAcademica(mobilidadeId, new EstudanteId(estudanteId), "Stanford");
        mobilidade.autorizar(new CoordenadorId(1));
        ctx.repositorio.salvar(mobilidade);
    }

    @Quando("o coordenador com id {int} tenta autorizar a mobilidade já autorizada")
    public void o_coordenador_tenta_autorizar_a_mobilidade_ja_autorizada(int coordenadorId) {
        try {
            mobilidade.autorizar(new CoordenadorId(coordenadorId));
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }
}
