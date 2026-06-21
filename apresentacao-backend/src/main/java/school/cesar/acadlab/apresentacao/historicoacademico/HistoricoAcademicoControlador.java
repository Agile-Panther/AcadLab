package school.cesar.acadlab.apresentacao.historicoacademico;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import school.cesar.acadlab.aplicacao.historicoacademico.HistoricoAcademicoResumo;
import school.cesar.acadlab.aplicacao.historicoacademico.HistoricoAcademicoServicoAplicacao;
import school.cesar.acadlab.dominio.historicoacademico.ConsultaHistoricoServico;
import school.cesar.acadlab.dominio.historicoacademico.HistoricoAcademicoServico;
import school.cesar.acadlab.dominio.historicoacademico.historico.DisciplinaId;
import school.cesar.acadlab.dominio.historicoacademico.historico.EstudanteId;
import school.cesar.acadlab.dominio.historicoacademico.historico.HistoricoAcademicoId;
import school.cesar.acadlab.dominio.historicoacademico.historico.MatrizCurricularId;
import school.cesar.acadlab.dominio.historicoacademico.historico.PeriodoLetivoId;
import school.cesar.acadlab.dominio.historicoacademico.historico.RegistroDisciplinaId;
import school.cesar.acadlab.dominio.historicoacademico.historico.SecretariaId;
import school.cesar.acadlab.dominio.historicoacademico.historico.SituacaoAcademica;
import school.cesar.acadlab.dominio.historicoacademico.historico.SituacaoDiscente;
import school.cesar.acadlab.dominio.historicoacademico.historico.TurmaId;

@RestController
@RequestMapping("backend/historicos")
class HistoricoAcademicoControlador {

    @Autowired
    private HistoricoAcademicoServico servico;

    @Autowired
    private ConsultaHistoricoServico consultaServico;

    @Autowired
    private HistoricoAcademicoServicoAplicacao servicoAplicacao;

    @RequestMapping(method = GET)
    java.util.List<HistoricoAcademicoResumo> listarTodos() {
        return servicoAplicacao.buscarTodos();
    }

    @RequestMapping(method = GET, path = "{id}")
    HistoricoAcademicoResumo buscarPorId(@PathVariable int id) {
        return servicoAplicacao.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Histórico não encontrado: " + id));
    }

    @RequestMapping(method = GET, path = "estudante/{estudanteId}")
    HistoricoAcademicoResumo buscarPorEstudante(@PathVariable int estudanteId) {
        return servicoAplicacao.buscarPorEstudante(estudanteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Histórico não encontrado para estudante: " + estudanteId));
    }

    @RequestMapping(method = GET, path = "estudante/{estudanteId}/oficial")
    java.util.List<RegistroOficialResponse> buscarHistoricoOficial(@PathVariable int estudanteId) {
        return consultaServico.obterHistoricoOficial(new EstudanteId(estudanteId))
                .stream()
                .map(r -> new RegistroOficialResponse(
                        r.getId().getId(),
                        r.getDisciplinaId().getId(),
                        r.getTurmaId().getId(),
                        r.getPeriodoLetivoId().getId(),
                        r.getNota(), r.getFrequencia(),
                        r.getSituacao().name()))
                .toList();
    }

    @RequestMapping(method = POST)
    void criarHistorico(@RequestBody CriarHistoricoRequest req) {
        servico.criarHistorico(new EstudanteId(req.estudanteId()),
                new MatrizCurricularId(req.matrizCurricularId()));
    }

    @RequestMapping(method = POST, path = "{id}/registros")
    void consolidarRegistro(@PathVariable int id, @RequestBody ConsolidarRegistroRequest req) {
        servico.consolidarRegistro(
                new HistoricoAcademicoId(id),
                new DisciplinaId(req.disciplinaId()),
                new TurmaId(req.turmaId()),
                new PeriodoLetivoId(req.periodoLetivoId()),
                req.nota(), req.frequencia(),
                SituacaoAcademica.valueOf(req.situacao()),
                req.turmaEncerrada());
    }

    @RequestMapping(method = PUT, path = "{id}/situacao")
    void atualizarSituacaoDiscente(@PathVariable int id, @RequestBody AtualizarSituacaoRequest req) {
        servico.atualizarSituacaoDiscente(
                new HistoricoAcademicoId(id),
                SituacaoDiscente.valueOf(req.novaSituacao()),
                new SecretariaId(req.responsavelId()),
                req.justificativa(),
                req.data());
    }

    @RequestMapping(method = POST, path = "{id}/acompanhamentos")
    void registrarAcompanhamento(@PathVariable int id, @RequestBody RegistrarAcompanhamentoRequest req) {
        servico.registrarAcompanhamento(
                new HistoricoAcademicoId(id),
                req.observacao(),
                req.data(),
                req.estudanteComVinculoAtivo());
    }

    @RequestMapping(method = POST, path = "{id}/aproveitamentos")
    void registrarAproveitamento(@PathVariable int id, @RequestBody RegistrarAproveitamentoRequest req) {
        servico.registrarAproveitamento(
                new HistoricoAcademicoId(id),
                new DisciplinaId(req.disciplinaEquivalenteId()),
                req.cargaHorariaExterna(),
                req.cargaHorariaRequerida(),
                req.instituicaoOrigem(),
                req.disciplinaOrigem());
    }

    @RequestMapping(method = PUT, path = "{id}/registros/{registroId}/retificar")
    void retificarRegistro(@PathVariable int id, @PathVariable int registroId,
                           @RequestBody RetificarRegistroRequest req) {
        servico.retificarRegistro(
                new HistoricoAcademicoId(id),
                new RegistroDisciplinaId(registroId),
                SituacaoAcademica.valueOf(req.novaSituacao()),
                new SecretariaId(req.responsavelId()),
                req.justificativa(),
                req.data());
    }

    record CriarHistoricoRequest(int estudanteId, int matrizCurricularId) {}

    record ConsolidarRegistroRequest(int disciplinaId, int turmaId, int periodoLetivoId,
                                     double nota, double frequencia,
                                     String situacao, boolean turmaEncerrada) {}

    record AtualizarSituacaoRequest(String novaSituacao, int responsavelId,
                                    String justificativa, LocalDate data) {}

    record RegistrarAcompanhamentoRequest(String observacao, LocalDate data,
                                          boolean estudanteComVinculoAtivo) {}

    record RegistrarAproveitamentoRequest(int disciplinaEquivalenteId,
                                          int cargaHorariaExterna, int cargaHorariaRequerida,
                                          String instituicaoOrigem, String disciplinaOrigem) {}

    record RetificarRegistroRequest(String novaSituacao, int responsavelId,
                                    String justificativa, LocalDate data) {}

    record RegistroOficialResponse(int id, int disciplinaId, int turmaId, int periodoLetivoId,
                                   double nota, double frequencia, String situacao) {}
}
