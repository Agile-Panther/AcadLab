package school.cesar.acadlab.apresentacao;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import school.cesar.acadlab.aplicacao.matricula.MatriculaResumo;
import school.cesar.acadlab.aplicacao.matricula.MatriculaServicoAplicacao;
import school.cesar.acadlab.dominio.matricula.MatriculaServico;
import school.cesar.acadlab.dominio.matricula.matricula.CoordenadorId;
import school.cesar.acadlab.dominio.matricula.matricula.DisciplinaId;
import school.cesar.acadlab.dominio.matricula.matricula.EstudanteId;
import school.cesar.acadlab.dominio.matricula.matricula.HorarioAula;
import school.cesar.acadlab.dominio.matricula.matricula.MatriculaId;
import school.cesar.acadlab.dominio.matricula.matricula.PeriodoLetivoId;
import school.cesar.acadlab.dominio.matricula.matricula.TurmaId;

@RestController
@RequestMapping("backend/matriculas")
public class MatriculaControlador {

    @Autowired
    MatriculaServico servico;

    @Autowired
    MatriculaServicoAplicacao servicoAplicacao;

    @GetMapping
    public List<MatriculaResumo> buscarPorEstudante(@RequestParam int estudanteId) {
        return servicoAplicacao.buscarPorEstudante(estudanteId);
    }

    @GetMapping("/{id}")
    public MatriculaResumo buscarPorId(@PathVariable int id) {
        return servicoAplicacao.buscarPorId(id).orElseThrow();
    }

    @PostMapping
    public void iniciarMatricula(@RequestBody IniciarMatriculaRequest req) {
        servico.iniciarMatricula(
                new EstudanteId(req.estudanteId()),
                new PeriodoLetivoId(req.periodoLetivoId()),
                req.limiteCreditos());
    }

    @PostMapping("/{id}/itens")
    public void adicionarItem(@PathVariable int id,
                               @RequestBody AdicionarItemRequest req) {
        servico.adicionarItem(
                new MatriculaId(id),
                new TurmaId(req.turmaId()),
                new DisciplinaId(req.disciplinaId()),
                req.creditos(),
                List.of(),
                req.cumpriuPreRequisitos(),
                req.correquisitosNoPlano(),
                req.temPendencias(),
                req.hoje(),
                req.inicioJanela(),
                req.fimJanela());
    }

    @PutMapping("/{id}/confirmar")
    public void confirmar(@PathVariable int id,
                           @RequestBody Map<Integer, Integer> vagasPorTurma) {
        var mapaConvertido = new java.util.HashMap<TurmaId, Integer>();
        vagasPorTurma.forEach((k, v) -> mapaConvertido.put(new TurmaId(k), v));
        servico.confirmar(new MatriculaId(id), mapaConvertido);
    }

    @PutMapping("/{id}/itens/{turmaId}/cancelar")
    public void cancelarItem(@PathVariable int id, @PathVariable int turmaId,
                              @RequestBody JanelaRequest req) {
        servico.cancelarItem(new MatriculaId(id), new TurmaId(turmaId),
                req.hoje(), req.inicio(), req.fim());
    }

    @PutMapping("/{id}/itens/{turmaId}/trancar")
    public void trancarDisciplina(@PathVariable int id, @PathVariable int turmaId,
                                   @RequestBody JanelaRequest req) {
        servico.trancarDisciplina(new MatriculaId(id), new TurmaId(turmaId),
                req.hoje(), req.inicio(), req.fim());
    }

    @PostMapping("/{id}/excecoes")
    public void solicitarExcecao(@PathVariable int id,
                                  @RequestBody SolicitarExcecaoRequest req) {
        servico.solicitarExcecao(new MatriculaId(id),
                new DisciplinaId(req.disciplinaId()), req.motivo());
    }

    @PutMapping("/{id}/excecoes/{disciplinaId}/deferir")
    public void deferir(@PathVariable int id, @PathVariable int disciplinaId,
                         @RequestParam int coordenadorId) {
        servico.deferir(new MatriculaId(id), new DisciplinaId(disciplinaId),
                new CoordenadorId(coordenadorId));
    }

    @PutMapping("/{id}/trancar-periodo")
    public void trancarPeriodo(@PathVariable int id,
                                @RequestBody TrancarPeriodoRequest req) {
        servico.trancarPeriodo(new MatriculaId(id), req.hoje(),
                req.inicioTrancamento(), req.fimTrancamento(),
                req.totalTrancamentos(), req.limiteTrancamentos());
    }

    record IniciarMatriculaRequest(int estudanteId, int periodoLetivoId, int limiteCreditos) {}

    record AdicionarItemRequest(int turmaId, int disciplinaId, int creditos,
                                 List<HorarioAula> horarios,
                                 boolean cumpriuPreRequisitos, boolean correquisitosNoPlano,
                                 boolean temPendencias, LocalDate hoje,
                                 LocalDate inicioJanela, LocalDate fimJanela) {}

    record JanelaRequest(LocalDate hoje, LocalDate inicio, LocalDate fim) {}

    record SolicitarExcecaoRequest(int disciplinaId, String motivo) {}

    record TrancarPeriodoRequest(LocalDate hoje, LocalDate inicioTrancamento,
                                  LocalDate fimTrancamento,
                                  int totalTrancamentos, int limiteTrancamentos) {}
}
