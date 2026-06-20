package school.cesar.acadlab.aplicacao.gestaofinanceira;

import java.time.LocalDate;

public interface AcordoRepositorioAplicacao {
    void registrar(int estudanteId, LocalDate prazo, int descontoPercentual, String observacoes);
}
