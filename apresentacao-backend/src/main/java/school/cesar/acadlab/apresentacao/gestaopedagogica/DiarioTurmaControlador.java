package school.cesar.acadlab.apresentacao.gestaopedagogica;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import school.cesar.acadlab.aplicacao.gestaopedagogica.DiarioTurmaResumo;
import school.cesar.acadlab.aplicacao.gestaopedagogica.DiarioTurmaServicoAplicacao;
import school.cesar.acadlab.dominio.gestaopedagogica.DiarioTurmaServico;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.AvaliacaoId;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.DiarioTurmaId;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.EstudanteId;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.PeriodoLetivoId;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.ProfessorId;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.RegistroAulaId;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.TurmaId;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.apuracao.RegimeApuracao;

@RestController
@RequestMapping("backend/diarios")
class DiarioTurmaControlador {
    @Autowired
    private DiarioTurmaServico servico;

    @Autowired
    private DiarioTurmaServicoAplicacao servicoAplicacao;

    @RequestMapping(method = GET, path = "turma/{turmaId}")
    List<DiarioTurmaResumo> pesquisarPorTurma(@PathVariable int turmaId) {
        return servicoAplicacao.pesquisarPorTurma(turmaId);
    }

    @RequestMapping(method = POST)
    void cadastrar(@RequestBody CadastrarRequest request) {
        servico.cadastrar(
                new TurmaId(request.turmaId()),
                new PeriodoLetivoId(request.periodoLetivoId()),
                new ProfessorId(request.professorResponsavelId()),
                request.dataInicioPeriodo(),
                request.dataFimPeriodo(),
                request.mediaMinima(),
                request.frequenciaMinima());
    }

    @RequestMapping(method = POST, path = "{id}/aulas")
    int registrarAula(@PathVariable int id, @RequestBody RegistrarAulaRequest request) {
        return servico.registrarAula(
                new DiarioTurmaId(id),
                new ProfessorId(request.professorId()),
                request.data(),
                request.conteudo()).getId();
    }

    @RequestMapping(method = PUT, path = "{id}/aulas/{aulaId}")
    void corrigirAula(@PathVariable int id, @PathVariable int aulaId,
                      @RequestBody CorrigirAulaRequest request) {
        servico.corrigirAula(
                new DiarioTurmaId(id),
                new RegistroAulaId(aulaId),
                new ProfessorId(request.professorId()),
                request.novoConteudo());
    }

    @RequestMapping(method = POST, path = "{id}/frequencias")
    void registrarFrequencia(@PathVariable int id, @RequestBody RegistrarFrequenciaRequest request) {
        servico.registrarFrequencia(
                new DiarioTurmaId(id),
                new ProfessorId(request.professorId()),
                new RegistroAulaId(request.aulaId()),
                new EstudanteId(request.estudanteId()),
                request.presente());
    }

    @RequestMapping(method = POST, path = "{id}/estudantes/{estudanteId}")
    void adicionarEstudanteAtivo(@PathVariable int id, @PathVariable int estudanteId) {
        servico.adicionarEstudanteAtivo(new DiarioTurmaId(id), new EstudanteId(estudanteId));
    }

    @RequestMapping(method = POST, path = "{id}/avaliacoes")
    int adicionarAvaliacao(@PathVariable int id, @RequestBody AdicionarAvaliacaoRequest request) {
        return servico.adicionarAvaliacao(
                new DiarioTurmaId(id),
                request.nome(),
                request.peso(),
                request.prazo()).getId();
    }

    @RequestMapping(method = POST, path = "{id}/estudantes/{estudanteId}/notas/{avaliacaoId}")
    void lancarNota(@PathVariable int id, @PathVariable int estudanteId,
                    @PathVariable int avaliacaoId, @RequestBody LancarNotaRequest request) {
        servico.lancarNota(
                new DiarioTurmaId(id),
                new EstudanteId(estudanteId),
                new AvaliacaoId(avaliacaoId),
                request.nota());
    }

    @RequestMapping(method = POST, path = "{id}/estudantes/{estudanteId}/fechar")
    void fecharResultado(@PathVariable int id, @PathVariable int estudanteId,
                         @RequestParam(name = "regime", defaultValue = "PONDERADA") RegimeApuracao regime) {
        servico.fecharResultado(new DiarioTurmaId(id), new EstudanteId(estudanteId), regime);
    }

    @RequestMapping(method = POST, path = "{id}/estudantes/{estudanteId}/revisao")
    void solicitarRevisaoNota(@PathVariable int id, @PathVariable int estudanteId,
                               @RequestBody RevisaoRequest request) {
        servico.solicitarRevisaoNota(
                new DiarioTurmaId(id),
                new EstudanteId(estudanteId),
                request.hoje(),
                request.fimJanelaRevisao());
    }

    @RequestMapping(method = POST, path = "{id}/estudantes/{estudanteId}/recuperacao")
    void lancarNotaRecuperacao(@PathVariable int id, @PathVariable int estudanteId,
                                @RequestBody RecuperacaoRequest request) {
        servico.lancarNotaRecuperacao(
                new DiarioTurmaId(id),
                new EstudanteId(estudanteId),
                request.nota(),
                request.hoje());
    }

    record CadastrarRequest(int turmaId, int periodoLetivoId, int professorResponsavelId,
                            LocalDate dataInicioPeriodo, LocalDate dataFimPeriodo,
                            double mediaMinima, double frequenciaMinima) {}

    record RegistrarAulaRequest(int professorId, LocalDate data, String conteudo) {}

    record CorrigirAulaRequest(int professorId, String novoConteudo) {}

    record RegistrarFrequenciaRequest(int professorId, int aulaId, int estudanteId, boolean presente) {}

    record AdicionarAvaliacaoRequest(String nome, double peso, LocalDate prazo) {}

    record LancarNotaRequest(double nota) {}

    record RevisaoRequest(LocalDate hoje, LocalDate fimJanelaRevisao) {}

    record RecuperacaoRequest(double nota, LocalDate hoje) {}
}
