package school.cesar.acadlab.dominio.ofertaturmas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import school.cesar.acadlab.dominio.ofertaturmas.professor.ProfessorId;
import school.cesar.acadlab.dominio.ofertaturmas.sala.SalaId;
import school.cesar.acadlab.dominio.ofertaturmas.turma.ModalidadeTurma;
import school.cesar.acadlab.dominio.ofertaturmas.turma.Turma;
import school.cesar.acadlab.dominio.ofertaturmas.turma.TurmaId;
import school.cesar.acadlab.dominio.ofertaturmas.turma.TurmaRepositorio;
import school.cesar.acadlab.dominio.ofertaturmas.turma.decorator.EstudanteId;

/**
 * Integra os decorators de turma (TurmaOnline / TurmaComListaEspera) ao caso de uso real
 * via OfertaTurmaServico, provando que o estado adicionado pelos decorators é persistido
 * e sobrevive ao recarregamento do agregado (§6.1).
 *
 * O repositório em memória reconstitui a Turma a partir de seus dados primitivos a cada
 * obter(), simulando o ciclo de persistência (igual ao adapter JPA).
 */
class OfertaTurmaDecoratorServicoTest {

    /** Repositório que reconstrói a turma a cada leitura — força o round-trip de persistência. */
    static class RepositorioReconstituinte implements TurmaRepositorio {
        record Estado(int periodo, int disciplina, ModalidadeTurma modalidade, int capacidade,
                      String link, List<Integer> espera) {}
        private final Map<Integer, Estado> dados = new HashMap<>();
        private int seq = 1;

        @Override public TurmaId proximoId() { return new TurmaId(seq++); }

        @Override public void salvar(Turma turma) {
            dados.put(turma.getId().getId(), new Estado(
                    turma.getPeriodoLetivoId().getId(),
                    turma.getDisciplinaId().getId(),
                    turma.getModalidade(),
                    turma.getCapacidade(),
                    turma.getLinkAcesso(),
                    turma.getListaEspera().stream().map(EstudanteId::getValor).toList()));
        }

        @Override public Turma obter(TurmaId id) {
            var e = dados.get(id.getId());
            if (e == null) throw new IllegalArgumentException("Turma não encontrada: " + id.getId());
            var turma = Turma.reconstituir(id, new PeriodoLetivoId(e.periodo()),
                    new DisciplinaId(e.disciplina()), null, null, e.modalidade(), e.capacidade(),
                    school.cesar.acadlab.dominio.ofertaturmas.turma.StatusTurma.PLANEJADA, List.of());
            turma.definirLinkAcesso(e.link());
            turma.registrarListaEspera(e.espera().stream().map(EstudanteId::new).toList());
            return turma;
        }

        @Override public List<Turma> pesquisarPorPeriodoLetivo(PeriodoLetivoId p) { return List.of(); }
        @Override public List<Turma> pesquisarPorProfessorEPeriodo(ProfessorId pr, PeriodoLetivoId p) { return List.of(); }
        @Override public List<Turma> pesquisarPorSalaEPeriodo(SalaId s, PeriodoLetivoId p) { return List.of(); }
        @Override public List<Turma> pesquisarPorDisciplinaEPeriodo(DisciplinaId d, PeriodoLetivoId p) { return List.of(); }
    }

    private OfertaTurmaServico servico(TurmaRepositorio repo) {
        return new OfertaTurmaServico(repo, new SalaRepositorioTest(), new ProfessorRepositorioTest());
    }

    private TurmaId ofertarTurma(OfertaTurmaServico s, ModalidadeTurma modalidade) {
        return s.ofertar(new PeriodoLetivoId(1), new DisciplinaId(1), modalidade, 30).getId();
    }

    @Test
    void linkDeAcessoOnlinePersisteAposRecarregar() {
        var repo = new RepositorioReconstituinte();
        var servico = servico(repo);
        var id = ofertarTurma(servico, ModalidadeTurma.EAD);

        servico.definirLinkAcessoOnline(id, "https://aulas.acadlab/turma-1");

        assertEquals("https://aulas.acadlab/turma-1", repo.obter(id).getLinkAcesso());
    }

    @Test
    void linkDeAcessoExigeModalidadeEad() {
        var repo = new RepositorioReconstituinte();
        var servico = servico(repo);
        var id = ofertarTurma(servico, ModalidadeTurma.PRESENCIAL);

        assertThrows(IllegalArgumentException.class,
                () -> servico.definirLinkAcessoOnline(id, "https://x"));
    }

    @Test
    void listaDeEsperaPersisteEImpedeDuplicidade() {
        var repo = new RepositorioReconstituinte();
        var servico = servico(repo);
        var id = ofertarTurma(servico, ModalidadeTurma.PRESENCIAL);

        servico.entrarListaEspera(id, new EstudanteId(7));
        servico.entrarListaEspera(id, new EstudanteId(9));

        var espera = repo.obter(id).getListaEspera();
        assertEquals(2, espera.size());
        assertTrue(espera.contains(new EstudanteId(7)));

        assertThrows(IllegalStateException.class,
                () -> servico.entrarListaEspera(id, new EstudanteId(7)));
    }

    @Test
    void sairDaListaDeEsperaRemoveEPersiste() {
        var repo = new RepositorioReconstituinte();
        var servico = servico(repo);
        var id = ofertarTurma(servico, ModalidadeTurma.PRESENCIAL);
        servico.entrarListaEspera(id, new EstudanteId(7));
        servico.entrarListaEspera(id, new EstudanteId(9));

        servico.sairListaEspera(id, new EstudanteId(7));

        var espera = repo.obter(id).getListaEspera();
        assertEquals(1, espera.size());
        assertTrue(espera.contains(new EstudanteId(9)));
    }
}
