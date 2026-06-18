package school.cesar.acadlab.dominio.historicoacademico;

import static org.apache.commons.lang3.Validate.notNull;
import java.time.LocalDate;
import school.cesar.acadlab.dominio.historicoacademico.historico.*;

public class HistoricoAcademicoServico {
    private final HistoricoAcademicoRepositorio repositorio;

    public HistoricoAcademicoServico(HistoricoAcademicoRepositorio repositorio) {
        notNull(repositorio, "O repositório não pode ser nulo");
        this.repositorio = repositorio;
    }

    public HistoricoAcademico criarHistorico(EstudanteId estudanteId, MatrizCurricularId matrizId) {
        var historico = new HistoricoAcademico(repositorio.proximoId(), estudanteId, matrizId);
        repositorio.salvar(historico);
        return historico;
    }

    public void consolidarRegistro(HistoricoAcademicoId historicoId, DisciplinaId disciplinaId,
                                    TurmaId turmaId, PeriodoLetivoId periodoLetivoId,
                                    double nota, double frequencia,
                                    SituacaoAcademica situacao, boolean turmaEncerrada) {
        var historico = repositorio.obter(historicoId);
        historico.consolidarRegistro(repositorio.proximoRegistroId(), disciplinaId, turmaId,
                periodoLetivoId, nota, frequencia, situacao, turmaEncerrada);
        repositorio.salvar(historico);
    }

    public void atualizarSituacaoDiscente(HistoricoAcademicoId historicoId, SituacaoDiscente novaSituacao,
                                           SecretariaId responsavel, String justificativa, LocalDate data) {
        var historico = repositorio.obter(historicoId);
        historico.atualizarSituacaoDiscente(novaSituacao, responsavel, justificativa, data);
        repositorio.salvar(historico);
    }

    public void registrarAcompanhamento(HistoricoAcademicoId historicoId, String observacao,
                                         LocalDate data, boolean estudanteComVinculoAtivo) {
        var historico = repositorio.obter(historicoId);
        historico.registrarAcompanhamento(repositorio.proximoAcompanhamentoId(), observacao,
                data, estudanteComVinculoAtivo);
        repositorio.salvar(historico);
    }

    public void registrarAproveitamento(HistoricoAcademicoId historicoId, DisciplinaId disciplinaEquivalente,
                                         int cargaHorariaExterna, int cargaHorariaRequerida,
                                         String instituicaoOrigem, String disciplinaOrigem) {
        var historico = repositorio.obter(historicoId);
        historico.registrarAproveitamento(repositorio.proximoAproveitamentoId(), disciplinaEquivalente,
                cargaHorariaExterna, cargaHorariaRequerida, instituicaoOrigem, disciplinaOrigem);
        repositorio.salvar(historico);
    }

    public void retificarRegistro(HistoricoAcademicoId historicoId, RegistroDisciplinaId registroId,
                                   SituacaoAcademica novaSituacao, SecretariaId responsavel,
                                   String justificativa, LocalDate data) {
        var historico = repositorio.obter(historicoId);
        historico.retificarRegistro(repositorio.proximoRetificacaoId(), registroId,
                novaSituacao, responsavel, justificativa, data);
        repositorio.salvar(historico);
    }
}
