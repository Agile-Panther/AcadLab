package school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.mobilidadeacademica.MobilidadeFuncionalidade;

public class RegistrarResultadoFuncionalidade {

    private final MobilidadeFuncionalidade ctx;
    private MobilidadeAcademica mobilidade;

    public RegistrarResultadoFuncionalidade(MobilidadeFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("uma mobilidade com item autorizado e comprovante anexado para estudante id {int}")
    public void uma_mobilidade_com_item_autorizado_e_comprovante_anexado(int estudanteId) {
        var mobilidadeId = ctx.repositorio.proximaMobilidadeId();
        mobilidade = new MobilidadeAcademica(mobilidadeId, new EstudanteId(estudanteId), "Harvard");
        mobilidade.autorizar(new CoordenadorId(1));
        mobilidade.adicionarItemPlano(new DisciplinaId(10), new DisciplinaId(20), 60, 60);
        mobilidade.anexarComprovante(new DisciplinaId(10));
        ctx.repositorio.salvar(mobilidade);
    }

    @Quando("a secretaria com id {int} registra o resultado da disciplina externa {int}")
    public void a_secretaria_registra_resultado_da_disciplina_externa(int secretariaId, int discId) {
        mobilidade.registrarResultado(new DisciplinaId(discId), new SecretariaId(secretariaId));
        ctx.repositorio.salvar(mobilidade);
    }

    @Entao("o resultado da disciplina externa {int} é registrado com sucesso")
    public void o_resultado_da_disciplina_externa_e_registrado_com_sucesso(int discId) {
        var item = mobilidade.getPlanoEstudos().stream()
                .filter(i -> i.getDisciplinaExterna().equals(new DisciplinaId(discId)))
                .findFirst()
                .orElseThrow();
        assertTrue(item.isResultadoRegistrado());
    }

    @Dado("uma mobilidade com item autorizado sem comprovante para estudante id {int}")
    public void uma_mobilidade_com_item_autorizado_sem_comprovante(int estudanteId) {
        var mobilidadeId = ctx.repositorio.proximaMobilidadeId();
        mobilidade = new MobilidadeAcademica(mobilidadeId, new EstudanteId(estudanteId), "Cambridge");
        mobilidade.autorizar(new CoordenadorId(1));
        mobilidade.adicionarItemPlano(new DisciplinaId(10), new DisciplinaId(20), 60, 60);
        ctx.repositorio.salvar(mobilidade);
    }

    @Quando("a secretaria com id {int} tenta registrar resultado da disciplina {int} sem comprovante")
    public void a_secretaria_tenta_registrar_resultado_sem_comprovante(int secretariaId, int discId) {
        try {
            mobilidade.registrarResultado(new DisciplinaId(discId), new SecretariaId(secretariaId));
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }
}
