package school.cesar.acadlab.apresentacao.ofertaturmas;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.text.Normalizer;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import school.cesar.acadlab.aplicacao.ofertaturmas.TurmaResumo;
import school.cesar.acadlab.aplicacao.ofertaturmas.TurmaServicoAplicacao;
import school.cesar.acadlab.dominio.ofertaturmas.DisciplinaId;
import school.cesar.acadlab.dominio.ofertaturmas.OfertaTurmaServico;
import school.cesar.acadlab.dominio.ofertaturmas.PeriodoLetivoId;
import school.cesar.acadlab.dominio.ofertaturmas.professor.ProfessorId;
import school.cesar.acadlab.dominio.ofertaturmas.sala.SalaId;
import school.cesar.acadlab.dominio.ofertaturmas.turma.ModalidadeTurma;
import school.cesar.acadlab.dominio.ofertaturmas.turma.TurmaId;

@RestController
@RequestMapping("backend/turmas")
class TurmaControlador {
    @Autowired OfertaTurmaServico ofertaTurmaServico;
    @Autowired TurmaServicoAplicacao turmaServico;

    @RequestMapping(method = GET, path = "/{id}")
    TurmaResumo buscarPorId(@PathVariable int id) {
        return turmaServico.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(method = GET, path = "")
    List<TurmaResumo> listarComFiltros(
            @RequestParam(required = false) Integer periodoLetivoId,
            @RequestParam(required = false) Integer cursoId,
            @RequestParam(required = false) Integer disciplinaId,
            @RequestParam(required = false) Integer professorId,
            @RequestParam(required = false) String status) {
        return turmaServico.listarComFiltros(periodoLetivoId, cursoId, disciplinaId, professorId, status);
    }

    @RequestMapping(method = GET, path = "/periodo/{periodoId}")
    List<TurmaResumo> listarPorPeriodo(@PathVariable int periodoId) {
        return turmaServico.listarPorPeriodo(periodoId);
    }

    @RequestMapping(method = POST, path = "")
    TurmaResumo ofertar(@RequestBody OfertarTurmaRequest req) {
        var turma = ofertaTurmaServico.ofertar(
                new PeriodoLetivoId(req.periodoLetivoId()),
                new DisciplinaId(req.disciplinaId()),
                parseModalidade(req.modalidade()),
                req.capacidade());
        if (req.listaEsperaHabilitada()) {
            ofertaTurmaServico.configurarListaEspera(turma.getId(), true, 0);
        }
        return turmaServico.buscarPorId(turma.getId().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(method = PUT, path = "/{id}/modalidade")
    void alterarModalidade(@PathVariable int id, @RequestBody AlterarModalidadeRequest req) {
        ofertaTurmaServico.alterarModalidade(new TurmaId(id), parseModalidade(req.modalidade()));
    }

    @RequestMapping(method = PUT, path = "/{id}/lista-espera")
    void configurarListaEspera(@PathVariable int id, @RequestBody ConfigurarListaEsperaRequest req) {
        ofertaTurmaServico.configurarListaEspera(
                new TurmaId(id),
                req.habilitada(),
                req.estudantesPendentes());
    }

    @RequestMapping(method = PUT, path = "/{id}/professor")
    void vincularProfessor(@PathVariable int id, @RequestBody VincularProfessorRequest req) {
        ofertaTurmaServico.vincularProfessor(new TurmaId(id), new ProfessorId(req.professorId()));
    }

    @RequestMapping(method = PUT, path = "/{id}/sala")
    void vincularSala(@PathVariable int id, @RequestBody VincularSalaRequest req) {
        ofertaTurmaServico.vincularSala(new TurmaId(id), new SalaId(req.salaId()));
    }

    @RequestMapping(method = PUT, path = "/{id}/horario")
    void adicionarHorario(@PathVariable int id, @RequestBody AdicionarHorarioRequest req) {
        ofertaTurmaServico.adicionarHorario(
                new TurmaId(id),
                DayOfWeek.valueOf(req.diaSemana()),
                LocalTime.parse(req.horaInicio()),
                LocalTime.parse(req.horaFim()));
    }

    @RequestMapping(method = PUT, path = "/{id}/ofertar")
    void confirmarOferta(@PathVariable int id) {
        ofertaTurmaServico.confirmarOferta(new TurmaId(id));
    }

    @RequestMapping(method = PUT, path = "/{id}/cancelar")
    void cancelar(@PathVariable int id) {
        ofertaTurmaServico.cancelar(new TurmaId(id));
    }

    @RequestMapping(method = PUT, path = "/{id}/inativar")
    void inativar(@PathVariable int id) {
        ofertaTurmaServico.inativar(new TurmaId(id));
    }

    record OfertarTurmaRequest(int periodoLetivoId, int disciplinaId, String modalidade,
                               int capacidade, boolean listaEsperaHabilitada) {}
    record AlterarModalidadeRequest(String modalidade) {}
    record ConfigurarListaEsperaRequest(boolean habilitada, int estudantesPendentes) {}
    record VincularProfessorRequest(int professorId) {}
    record VincularSalaRequest(int salaId) {}
    record AdicionarHorarioRequest(String diaSemana, String horaInicio, String horaFim) {}

    private ModalidadeTurma parseModalidade(String modalidade) {
        var normalizada = Normalizer.normalize(modalidade, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase(Locale.ROOT);
        return ModalidadeTurma.valueOf(normalizada);
    }
}
