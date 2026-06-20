package school.cesar.acadlab.apresentacao.gestaofinanceira;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import school.cesar.acadlab.aplicacao.gestaofinanceira.InadimplentesRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.gestaofinanceira.InadimplentesResumo;

@RestController
@RequestMapping("backend/inadimplentes")
class InadimplentesControlador {

    @Autowired
    private InadimplentesRepositorioAplicacao repositorio;

    @GetMapping
    List<InadimplentesResumo> buscar() {
        return repositorio.buscarInadimplentes(LocalDate.now());
    }
}
