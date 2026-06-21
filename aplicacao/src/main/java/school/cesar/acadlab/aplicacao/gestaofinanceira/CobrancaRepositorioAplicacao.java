package school.cesar.acadlab.aplicacao.gestaofinanceira;

import java.util.List;

public interface CobrancaRepositorioAplicacao {
    List<CobrancaResumo> pesquisarPorContrato(int contratoId);

    List<CobrancaResumo> pesquisarContestacoesAbertas();
}
