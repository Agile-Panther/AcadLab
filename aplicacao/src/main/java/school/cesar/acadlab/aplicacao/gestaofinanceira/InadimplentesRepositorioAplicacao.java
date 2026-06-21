package school.cesar.acadlab.aplicacao.gestaofinanceira;

import java.time.LocalDate;
import java.util.List;

public interface InadimplentesRepositorioAplicacao {
    List<InadimplentesResumo> buscarInadimplentes(LocalDate hoje);
}
