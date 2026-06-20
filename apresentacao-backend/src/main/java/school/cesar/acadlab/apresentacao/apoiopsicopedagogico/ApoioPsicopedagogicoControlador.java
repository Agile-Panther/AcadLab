package school.cesar.acadlab.apresentacao.apoiopsicopedagogico;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import school.cesar.acadlab.aplicacao.apoiopsicopedagogico.ApoioPsicopedagogicoServicoAplicacao;
import school.cesar.acadlab.aplicacao.apoiopsicopedagogico.CasoResumo;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.AgendamentoServico;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.ApoioServico;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.AtendimentoServico;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.TriagemServico;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.atendimento.Atendimento;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.CasoId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.estudante.EstudanteId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.profissional.PsicopedagogoId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.triagem.PrioridadeTriagem;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.triagem.Triagem;

@RestController
@RequestMapping("backend/apoio")
class ApoioPsicopedagogicoControlador {

    @Autowired private ApoioServico apoioServico;
    @Autowired private TriagemServico triagemServico;
    @Autowired private AtendimentoServico atendimentoServico;
    @Autowired private AgendamentoServico agendamentoServico;
    @Autowired private ApoioPsicopedagogicoServicoAplicacao servicoAplicacao;

    /* ===== Casos ===== */

    @RequestMapping(method = GET, path = "casos/{id}")
    Optional<CasoResumo> buscarCasoPorId(@PathVariable int id) {
        return servicoAplicacao.buscarCasoPorId(id);
    }

    @RequestMapping(method = GET, path = "casos")
    List<CasoResumo> buscarCasosPorResponsavel(@RequestParam int responsavelId) {
        return servicoAplicacao.buscarCasosPorResponsavel(responsavelId);
    }

    @RequestMapping(method = GET, path = "casos/abertos")
    List<CasoResumo> buscarCasosAbertos() {
        return servicoAplicacao.buscarCasosAbertos();
    }

    @RequestMapping(method = GET, path = "estudantes/{estudanteId}/casos")
    List<CasoResumo> buscarCasosPorEstudante(@PathVariable int estudanteId) {
        return servicoAplicacao.buscarCasosPorEstudante(estudanteId);
    }

    @RequestMapping(method = GET, path = "estudantes/{estudanteId}/caso-ativo")
    Optional<CasoResumo> buscarCasoAtivoPorEstudante(@PathVariable int estudanteId) {
        return servicoAplicacao.buscarCasoAtivoPorEstudante(estudanteId);
    }

    /* ===== Solicitação de apoio ===== */

    @RequestMapping(method = POST, path = "solicitacoes")
    void solicitar(@RequestBody SolicitarRequest request) {
        apoioServico.solicitar(new EstudanteId(request.estudanteId()), request.descricao());
    }

    @RequestMapping(method = PUT, path = "casos/{casoId}/reabrir")
    void reabrirCaso(@PathVariable int casoId) {
        apoioServico.reabrir(new CasoId(casoId));
    }

    /* ===== Triagem ===== */

    @RequestMapping(method = POST, path = "casos/{casoId}/triagem")
    void realizarTriagem(@PathVariable int casoId, @RequestBody TriagemRequest request) {
        var triagem = new Triagem(
                PrioridadeTriagem.valueOf(request.prioridade()),
                request.observacoes(),
                new PsicopedagogoId(request.responsavelId()),
                request.data());
        triagemServico.realizarTriagem(new CasoId(casoId), triagem);
    }

    /* ===== Atendimento ===== */

    @RequestMapping(method = POST, path = "casos/{casoId}/atendimentos")
    void registrarAtendimento(@PathVariable int casoId, @RequestBody AtendimentoRequest request) {
        var atendimento = new Atendimento(
                request.observacoes(), request.encaminhamento(),
                request.conclusaoFinal(), request.data());
        atendimentoServico.registrarAtendimento(new CasoId(casoId), atendimento);
    }

    @RequestMapping(method = PUT, path = "casos/{casoId}/encerrar")
    void encerrarCaso(@PathVariable int casoId) {
        atendimentoServico.encerrarCaso(new CasoId(casoId));
    }

    /* ===== Agendamento ===== */

    @RequestMapping(method = POST, path = "casos/{casoId}/agendamento")
    void agendar(@PathVariable int casoId, @RequestBody AgendarRequest request) {
        agendamentoServico.agendar(new CasoId(casoId), request.dataHora(), LocalDateTime.now());
    }

    @RequestMapping(method = POST, path = "casos/{casoId}/agendamento/contestacao")
    void contestarAgendamento(@PathVariable int casoId, @RequestBody ContestarAgendamentoRequest request) {
        agendamentoServico.contestar(new CasoId(casoId), request.justificativa(),
                request.horarioSugerido(), LocalDateTime.now());
    }

    /* ===== Records ===== */

    record SolicitarRequest(int estudanteId, String descricao) {}

    record TriagemRequest(String prioridade, String observacoes, int responsavelId, LocalDate data) {}

    record AtendimentoRequest(String observacoes, String encaminhamento,
                               boolean conclusaoFinal, LocalDate data) {}

    record AgendarRequest(LocalDateTime dataHora) {}

    record ContestarAgendamentoRequest(String justificativa, LocalDateTime horarioSugerido) {}
}
