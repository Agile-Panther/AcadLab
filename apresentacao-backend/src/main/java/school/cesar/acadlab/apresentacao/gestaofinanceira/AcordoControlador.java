package school.cesar.acadlab.apresentacao.gestaofinanceira;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import school.cesar.acadlab.aplicacao.gestaofinanceira.AcordoRepositorioAplicacao;

@RestController
@RequestMapping("backend/acordos")
class AcordoControlador {

    @Autowired
    private AcordoRepositorioAplicacao repositorio;

    @PostMapping
    void registrar(@RequestBody RegistrarAcordoRequest request) {
        repositorio.registrar(
                request.estudanteId(),
                request.prazo(),
                request.descontoPercentual(),
                request.observacoes());
    }

    record RegistrarAcordoRequest(int estudanteId, LocalDate prazo, int descontoPercentual, String observacoes) {}
}
