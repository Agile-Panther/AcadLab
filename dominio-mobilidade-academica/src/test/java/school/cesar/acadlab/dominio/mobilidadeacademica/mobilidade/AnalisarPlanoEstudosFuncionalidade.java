package school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.mobilidadeacademica.MobilidadeFuncionalidade;

public class AnalisarPlanoEstudosFuncionalidade {

    private final MobilidadeFuncionalidade ctx;
    private MobilidadeAcademica mobilidade;

    public AnalisarPlanoEstudosFuncionalidade(MobilidadeFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("uma mobilidade autorizada para análise de plano com estudante id {int}")
    public void uma_mobilidade_autorizada_para_analise_de_plano_com_estudante_id(int estudanteId) {
        var mobilidadeId = ctx.repositorio.proximaMobilidadeId();
        mobilidade = new MobilidadeAcademica(mobilidadeId, new EstudanteId(estudanteId), "Sorbonne");
        mobilidade.autorizar(new CoordenadorId(1));
        ctx.repositorio.salvar(mobilidade);
    }

    @Quando("o coordenador adiciona item ao plano com disciplina externa {int} equivalente {int} carga externa {int} carga equivalente {int}")
    public void o_coordenador_adiciona_item_ao_plano(int discExterna, int discEquivalente,
                                                      int cargaExterna, int cargaEquivalente) {
        mobilidade.adicionarItemPlano(
                new DisciplinaId(discExterna),
                new DisciplinaId(discEquivalente),
                cargaExterna,
                cargaEquivalente);
        ctx.repositorio.salvar(mobilidade);
    }

    @Entao("o item do plano tem status AUTORIZADO")
    public void o_item_do_plano_tem_status_autorizado() {
        assertFalse(mobilidade.getPlanoEstudos().isEmpty());
        assertEquals(StatusItemPlano.AUTORIZADO, mobilidade.getPlanoEstudos().get(0).getStatus());
    }

    @Quando("o coordenador tenta adicionar item ao plano com carga externa {int} menor que equivalente {int}")
    public void o_coordenador_tenta_adicionar_item_carga_insuficiente(int cargaExterna, int cargaEquivalente) {
        try {
            mobilidade.adicionarItemPlano(
                    new DisciplinaId(10),
                    new DisciplinaId(20),
                    cargaExterna,
                    cargaEquivalente);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Dado("uma mobilidade autorizada sem itens no plano para estudante id {int}")
    public void uma_mobilidade_autorizada_sem_itens_no_plano_para_estudante_id(int estudanteId) {
        var mobilidadeId = ctx.repositorio.proximaMobilidadeId();
        mobilidade = new MobilidadeAcademica(mobilidadeId, new EstudanteId(estudanteId), "Oxford");
        mobilidade.autorizar(new CoordenadorId(1));
        ctx.repositorio.salvar(mobilidade);
    }

    @Quando("a secretaria tenta registrar resultado para disciplina {int} fora do plano")
    public void a_secretaria_tenta_registrar_resultado_para_disciplina_fora_do_plano(int discId) {
        try {
            mobilidade.registrarResultado(new DisciplinaId(discId), new SecretariaId(1));
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }
}
