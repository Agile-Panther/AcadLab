package school.cesar.acadlab.dominio.historicoacademico.historico;

import static org.apache.commons.lang3.Validate.notNull;
import static org.apache.commons.lang3.Validate.notBlank;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import school.cesar.acadlab.dominio.historicoacademico.iterador.IteradorHistorico;
import school.cesar.acadlab.dominio.historicoacademico.iterador.IteradorRegistrosDisciplina;

public class HistoricoAcademico {
    private final HistoricoAcademicoId id;
    private final EstudanteId estudanteId;
    private final MatrizCurricularId matrizCurricularId;
    private SituacaoDiscente situacaoDiscente;
    private final List<RegistroDisciplina> registros;
    private final List<Aproveitamento> aproveitamentos;
    private final List<Retificacao> retificacoes;
    private final List<AcompanhamentoAcademico> acompanhamentos;
    private final List<EntradaAuditoria> trilhaAuditoria;

    public HistoricoAcademico(HistoricoAcademicoId id, EstudanteId estudanteId,
                               MatrizCurricularId matrizCurricularId) {
        notNull(id, "O id não pode ser nulo");
        notNull(estudanteId, "O estudante não pode ser nulo");
        notNull(matrizCurricularId, "A matriz curricular não pode ser nula");
        this.id = id;
        this.estudanteId = estudanteId;
        this.matrizCurricularId = matrizCurricularId;
        this.situacaoDiscente = SituacaoDiscente.ATIVO;
        this.registros = new ArrayList<>();
        this.aproveitamentos = new ArrayList<>();
        this.retificacoes = new ArrayList<>();
        this.acompanhamentos = new ArrayList<>();
        this.trilhaAuditoria = new ArrayList<>();
    }

    private HistoricoAcademico(HistoricoAcademicoId id, EstudanteId estudanteId,
                                MatrizCurricularId matrizCurricularId, SituacaoDiscente situacaoDiscente,
                                List<RegistroDisciplina> registros, List<Aproveitamento> aproveitamentos,
                                List<Retificacao> retificacoes, List<AcompanhamentoAcademico> acompanhamentos,
                                List<EntradaAuditoria> trilhaAuditoria) {
        this.id = id;
        this.estudanteId = estudanteId;
        this.matrizCurricularId = matrizCurricularId;
        this.situacaoDiscente = situacaoDiscente;
        this.registros = new ArrayList<>(registros);
        this.aproveitamentos = new ArrayList<>(aproveitamentos);
        this.retificacoes = new ArrayList<>(retificacoes);
        this.acompanhamentos = new ArrayList<>(acompanhamentos);
        this.trilhaAuditoria = new ArrayList<>(trilhaAuditoria);
    }

    public static HistoricoAcademico reconstituir(HistoricoAcademicoId id, EstudanteId estudanteId,
                                                   MatrizCurricularId matrizCurricularId,
                                                   SituacaoDiscente situacaoDiscente,
                                                   List<RegistroDisciplina> registros,
                                                   List<Aproveitamento> aproveitamentos,
                                                   List<Retificacao> retificacoes,
                                                   List<AcompanhamentoAcademico> acompanhamentos,
                                                   List<EntradaAuditoria> trilhaAuditoria) {
        return new HistoricoAcademico(id, estudanteId, matrizCurricularId, situacaoDiscente,
                registros, aproveitamentos, retificacoes, acompanhamentos, trilhaAuditoria);
    }

    public HistoricoAcademicoId getId() { return id; }
    public EstudanteId getEstudanteId() { return estudanteId; }
    public MatrizCurricularId getMatrizCurricularId() { return matrizCurricularId; }
    public SituacaoDiscente getSituacaoDiscente() { return situacaoDiscente; }
    public List<RegistroDisciplina> getRegistros() { return Collections.unmodifiableList(registros); }
    public List<Aproveitamento> getAproveitamentos() { return Collections.unmodifiableList(aproveitamentos); }
    public List<Retificacao> getRetificacoes() { return Collections.unmodifiableList(retificacoes); }
    public List<AcompanhamentoAcademico> getAcompanhamentos() { return Collections.unmodifiableList(acompanhamentos); }
    public List<EntradaAuditoria> getTrilhaAuditoria() { return Collections.unmodifiableList(trilhaAuditoria); }

    // RN-1: apenas resultados de turmas encerradas podem ser consolidados
    // RN-2: situação acadêmica final é obrigatória
    public void consolidarRegistro(RegistroDisciplinaId registroId, DisciplinaId disciplinaId,
                                    TurmaId turmaId, PeriodoLetivoId periodoLetivoId,
                                    double nota, double frequencia,
                                    SituacaoAcademica situacao, boolean turmaEncerrada) {
        notNull(registroId, "O id do registro não pode ser nulo");
        notNull(disciplinaId, "A disciplina não pode ser nula");
        if (!turmaEncerrada) {
            throw new IllegalStateException("RN-1: Apenas resultados de turmas encerradas podem ser consolidados");
        }
        if (situacao == null) {
            throw new IllegalStateException("RN-2: Situação acadêmica final é obrigatória na consolidação");
        }
        registros.add(new RegistroDisciplina(registroId, disciplinaId, turmaId, periodoLetivoId,
                nota, frequencia, situacao));
    }

    // RN-5: atualização manual registrada com trilha de auditoria
    public void atualizarSituacaoDiscente(SituacaoDiscente novaSituacao, SecretariaId responsavel,
                                           String justificativa, LocalDate data) {
        notNull(novaSituacao, "A nova situação não pode ser nula");
        notNull(responsavel, "O responsável não pode ser nulo");
        notBlank(justificativa, "A justificativa não pode ser vazia");
        notNull(data, "A data não pode ser nula");
        trilhaAuditoria.add(new EntradaAuditoria(this.situacaoDiscente, novaSituacao,
                responsavel, justificativa, data));
        this.situacaoDiscente = novaSituacao;
    }

    // RN-4: acompanhamento apenas para estudante com matrícula ativa ou situação regular
    public void registrarAcompanhamento(AcompanhamentoId acompanhamentoId, String observacao,
                                         LocalDate data, boolean estudanteComVinculoAtivo) {
        notNull(acompanhamentoId, "O id do acompanhamento não pode ser nulo");
        if (!estudanteComVinculoAtivo) {
            throw new IllegalStateException("RN-4: Acompanhamento apenas para estudante com matrícula ativa ou situação regular");
        }
        acompanhamentos.add(new AcompanhamentoAcademico(acompanhamentoId, observacao, data));
    }

    // RN-7: compatibilidade de carga horária para aproveitamento
    public void registrarAproveitamento(AproveitamentoId aproveitamentoId, DisciplinaId disciplinaEquivalente,
                                         int cargaHorariaExterna, int cargaHorariaRequerida,
                                         String instituicaoOrigem, String disciplinaOrigem) {
        notNull(aproveitamentoId, "O id do aproveitamento não pode ser nulo");
        notNull(disciplinaEquivalente, "A disciplina equivalente não pode ser nula");
        if (cargaHorariaExterna < cargaHorariaRequerida) {
            throw new IllegalStateException("RN-7: Carga horária da disciplina externa insuficiente para aproveitamento");
        }
        aproveitamentos.add(new Aproveitamento(aproveitamentoId, disciplinaEquivalente,
                cargaHorariaExterna, cargaHorariaRequerida, instituicaoOrigem, disciplinaOrigem));
    }

    // RN-8: retificação preserva resultado anterior com rastreabilidade
    public void retificarRegistro(RetificacaoId retificacaoId, RegistroDisciplinaId registroId,
                                   SituacaoAcademica novaSituacao, SecretariaId responsavel,
                                   String justificativa, LocalDate data) {
        notNull(retificacaoId, "O id da retificação não pode ser nulo");
        notNull(registroId, "O registro não pode ser nulo");
        notNull(novaSituacao, "A nova situação não pode ser nula");
        var registro = registros.stream()
                .filter(r -> r.getId().equals(registroId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Registro não encontrado: " + registroId));
        SituacaoAcademica situacaoAnterior = registro.getSituacao();
        retificacoes.add(new Retificacao(retificacaoId, registroId, situacaoAnterior,
                novaSituacao, responsavel, justificativa, data));
        registro.retificar(novaSituacao);
    }

    // Padrão Iterator: percorre registros sem expor a lista interna
    public IteradorHistorico<RegistroDisciplina> iteradorRegistros() {
        return new IteradorRegistrosDisciplina(Collections.unmodifiableList(registros));
    }
}
