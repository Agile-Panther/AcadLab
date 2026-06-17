package school.cesar.acadlab.apresentacao.ofertaturmas;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @RequestMapping(method = GET, path = "/periodo/{periodoId}")
    List<TurmaResumo> listarPorPeriodo(@PathVariable int periodoId) {
        return turmaServico.listarPorPeriodo(periodoId);
    }

    @RequestMapping(method = POST, path = "")
    TurmaResumo ofertar(@RequestBody OfertarTurmaRequest req) {
        var turma = ofertaTurmaServico.ofertar(
                new PeriodoLetivoId(req.periodoLetivoId()),
                new DisciplinaId(req.disciplinaId()),
                ModalidadeTurma.valueOf(req.modalidade()),
                req.capacidade());
        return turmaServico.buscarPorId(turma.getId().getId()).orElseThrow();
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

    record OfertarTurmaRequest(int periodoLetivoId, int disciplinaId, String modalidade, int capacidade) {}
    record VincularProfessorRequest(int professorId) {}
    record VincularSalaRequest(int salaId) {}
    record AdicionarHorarioRequest(String diaSemana, String horaInicio, String horaFim) {}
}
