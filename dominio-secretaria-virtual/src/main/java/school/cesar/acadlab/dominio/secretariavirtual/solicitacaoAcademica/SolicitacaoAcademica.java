package school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import school.cesar.acadlab.dominio.secretariavirtual.analista.SecretariaId;
import school.cesar.acadlab.dominio.secretariavirtual.documento.Documento;
import school.cesar.acadlab.dominio.secretariavirtual.estudante.EstudanteId;
import school.cesar.acadlab.dominio.secretariavirtual.periodo.PeriodoLetivoId;
import school.cesar.acadlab.dominio.secretariavirtual.protocolo.Protocolo;

public class SolicitacaoAcademica {
    private final SolicitacaoAcademicaId id;
    private final EstudanteId estudanteId;
    private final PeriodoLetivoId periodoLetivoId;
    private final TipoSolicitacao tipo;
    private final Protocolo protocolo;
    private final String descricao;
    private final LocalDate dataAbertura;
    private StatusSolicitacao status;
    private final List<Documento> documentos = new ArrayList<>();
    private SecretariaId analistaId;
    private String justificativaAnalise;
    private LocalDate dataAnalise;
    private boolean possuiImpactoAcademico;
    private boolean alteracoesVinculadas;

    public SolicitacaoAcademica(SolicitacaoAcademicaId id, EstudanteId estudanteId,
                                 PeriodoLetivoId periodoLetivoId, TipoSolicitacao tipo,
                                 Protocolo protocolo, String descricao) {
        notNull(id, "O id não pode ser nulo");
        notNull(estudanteId, "O estudante não pode ser nulo");
        notNull(periodoLetivoId, "O período letivo não pode ser nulo");
        notNull(tipo, "O tipo de solicitação não pode ser nulo");
        notNull(protocolo, "O protocolo não pode ser nulo");
        notNull(descricao, "A descrição não pode ser nula");
        notBlank(descricao, "A descrição não pode estar em branco");
        this.id = id;
        this.estudanteId = estudanteId;
        this.periodoLetivoId = periodoLetivoId;
        this.tipo = tipo;
        this.protocolo = protocolo;
        this.descricao = descricao;
        this.dataAbertura = LocalDate.now();
        this.status = StatusSolicitacao.PENDENTE_ANALISE;
    }

    public SolicitacaoAcademica(SolicitacaoAcademicaId id, EstudanteId estudanteId,
                                 PeriodoLetivoId periodoLetivoId, TipoSolicitacao tipo,
                                 Protocolo protocolo, String descricao, LocalDate dataAbertura,
                                 StatusSolicitacao status, List<Documento> documentos,
                                 SecretariaId analistaId, String justificativaAnalise,
                                 LocalDate dataAnalise, boolean possuiImpactoAcademico,
                                 boolean alteracoesVinculadas) {
        notNull(id, "O id não pode ser nulo");
        notNull(estudanteId, "O estudante não pode ser nulo");
        notNull(periodoLetivoId, "O período letivo não pode ser nulo");
        notNull(tipo, "O tipo de solicitação não pode ser nulo");
        notNull(protocolo, "O protocolo não pode ser nulo");
        notNull(descricao, "A descrição não pode ser nula");
        notNull(status, "O status não pode ser nulo");
        this.id = id;
        this.estudanteId = estudanteId;
        this.periodoLetivoId = periodoLetivoId;
        this.tipo = tipo;
        this.protocolo = protocolo;
        this.descricao = descricao;
        this.dataAbertura = dataAbertura;
        this.status = status;
        if (documentos != null) {
            this.documentos.addAll(documentos);
        }
        this.analistaId = analistaId;
        this.justificativaAnalise = justificativaAnalise;
        this.dataAnalise = dataAnalise;
        this.possuiImpactoAcademico = possuiImpactoAcademico;
        this.alteracoesVinculadas = alteracoesVinculadas;
    }

    public SolicitacaoAcademicaId getId() { return id; }
    public EstudanteId getEstudanteId() { return estudanteId; }
    public PeriodoLetivoId getPeriodoLetivoId() { return periodoLetivoId; }
    public TipoSolicitacao getTipo() { return tipo; }
    public Protocolo getProtocolo() { return protocolo; }
    public String getDescricao() { return descricao; }
    public LocalDate getDataAbertura() { return dataAbertura; }
    public StatusSolicitacao getStatus() { return status; }
    public List<Documento> getDocumentos() { return Collections.unmodifiableList(documentos); }
    public SecretariaId getAnalistaId() { return analistaId; }
    public String getJustificativaAnalise() { return justificativaAnalise; }
    public LocalDate getDataAnalise() { return dataAnalise; }
    public boolean isPossuiImpactoAcademico() { return possuiImpactoAcademico; }
    public boolean isAlteracoesVinculadas() { return alteracoesVinculadas; }

    public void anexarDocumento(Documento documento) {
        notNull(documento, "O documento não pode ser nulo");
        documentos.add(documento);
    }

    // RN3: documentos obrigatórios antes da submissão
    public void validarDocumentosObrigatorios() {
        List<String> obrigatorios = tipo.getDocumentosObrigatorios();
        if (obrigatorios.isEmpty()) {
            return;
        }
        List<String> tiposAnexados = documentos.stream()
                .map(Documento::getTipo)
                .toList();
        for (String obrigatorio : obrigatorios) {
            if (!tiposAnexados.contains(obrigatorio)) {
                throw new IllegalStateException(
                        "Documento obrigatório ausente: " + obrigatorio);
            }
        }
    }

    // RN5: complementação não permitida em solicitações encerradas ou indeferidas
    public SolicitacaoComplementadaEvento complementar(Documento documento) {
        notNull(documento, "O documento não pode ser nulo");
        if (status == StatusSolicitacao.CONCLUIDA || status == StatusSolicitacao.INDEFERIDA) {
            throw new IllegalStateException(
                    "Solicitações concluídas ou indeferidas não podem receber complementação");
        }
        documentos.add(documento);
        if (status == StatusSolicitacao.PENDENTE_COMPLEMENTACAO) {
            this.status = StatusSolicitacao.PENDENTE_ANALISE;
        }
        return new SolicitacaoComplementadaEvento(this);
    }

    public SolicitacaoEmAnaliseEvento iniciarAnalise(SecretariaId analistaId) {
        notNull(analistaId, "O analista não pode ser nulo");
        if (status != StatusSolicitacao.PENDENTE_ANALISE) {
            throw new IllegalStateException(
                    "Apenas solicitações pendentes de análise podem ser analisadas");
        }
        this.analistaId = analistaId;
        this.status = StatusSolicitacao.EM_ANALISE;
        return new SolicitacaoEmAnaliseEvento(this);
    }

    // RN4: solicitação deferida com impacto concluída somente após vinculação
    public SolicitacaoDeferidaEvento deferir(SecretariaId analistaId, String justificativa,
                                              boolean impactoAcademico) {
        notNull(analistaId, "O analista não pode ser nulo");
        notNull(justificativa, "A justificativa não pode ser nula");
        notBlank(justificativa, "A justificativa não pode estar em branco");
        if (status != StatusSolicitacao.EM_ANALISE) {
            throw new IllegalStateException(
                    "Apenas solicitações em análise podem ser deferidas");
        }
        this.analistaId = analistaId;
        this.justificativaAnalise = justificativa;
        this.dataAnalise = LocalDate.now();
        this.possuiImpactoAcademico = impactoAcademico;
        this.status = StatusSolicitacao.DEFERIDA;
        return new SolicitacaoDeferidaEvento(this);
    }

    public SolicitacaoIndeferidaEvento indeferir(SecretariaId analistaId, String justificativa) {
        notNull(analistaId, "O analista não pode ser nulo");
        notNull(justificativa, "A justificativa não pode ser nula");
        notBlank(justificativa, "A justificativa não pode estar em branco");
        if (status != StatusSolicitacao.EM_ANALISE) {
            throw new IllegalStateException(
                    "Apenas solicitações em análise podem ser indeferidas");
        }
        this.analistaId = analistaId;
        this.justificativaAnalise = justificativa;
        this.dataAnalise = LocalDate.now();
        this.status = StatusSolicitacao.INDEFERIDA;
        return new SolicitacaoIndeferidaEvento(this);
    }

    public void solicitarComplementacao(SecretariaId analistaId) {
        notNull(analistaId, "O analista não pode ser nulo");
        if (status != StatusSolicitacao.EM_ANALISE) {
            throw new IllegalStateException(
                    "Apenas solicitações em análise podem solicitar complementação");
        }
        this.analistaId = analistaId;
        this.status = StatusSolicitacao.PENDENTE_COMPLEMENTACAO;
    }

    // RN4: vincular alterações para concluir solicitação deferida com impacto
    public void vincularAlteracoes() {
        if (status != StatusSolicitacao.DEFERIDA) {
            throw new IllegalStateException(
                    "Apenas solicitações deferidas podem ter alterações vinculadas");
        }
        this.alteracoesVinculadas = true;
    }

    // RN4: conclusão exige vinculação se houver impacto acadêmico
    public SolicitacaoConcluidaEvento concluir() {
        if (status != StatusSolicitacao.DEFERIDA) {
            throw new IllegalStateException(
                    "Apenas solicitações deferidas podem ser concluídas");
        }
        if (possuiImpactoAcademico && !alteracoesVinculadas) {
            throw new IllegalStateException(
                    "Solicitação com impacto acadêmico requer vinculação de alterações antes da conclusão");
        }
        this.status = StatusSolicitacao.CONCLUIDA;
        return new SolicitacaoConcluidaEvento(this);
    }

    // RN6: cancelamento apenas para solicitações pendentes de análise
    public SolicitacaoCanceladaEvento cancelar() {
        if (status != StatusSolicitacao.PENDENTE_ANALISE) {
            throw new IllegalStateException(
                    "Apenas solicitações pendentes de análise podem ser canceladas");
        }
        this.status = StatusSolicitacao.CANCELADA;
        return new SolicitacaoCanceladaEvento(this);
    }

    public static abstract class SolicitacaoEvento {
        private final SolicitacaoAcademica solicitacao;
        protected SolicitacaoEvento(SolicitacaoAcademica solicitacao) { this.solicitacao = solicitacao; }
        public SolicitacaoAcademica getSolicitacao() { return solicitacao; }
    }

    public static class SolicitacaoComplementadaEvento extends SolicitacaoEvento {
        private SolicitacaoComplementadaEvento(SolicitacaoAcademica s) { super(s); }
    }

    public static class SolicitacaoEmAnaliseEvento extends SolicitacaoEvento {
        private SolicitacaoEmAnaliseEvento(SolicitacaoAcademica s) { super(s); }
    }

    public static class SolicitacaoDeferidaEvento extends SolicitacaoEvento {
        private SolicitacaoDeferidaEvento(SolicitacaoAcademica s) { super(s); }
    }

    public static class SolicitacaoIndeferidaEvento extends SolicitacaoEvento {
        private SolicitacaoIndeferidaEvento(SolicitacaoAcademica s) { super(s); }
    }

    public static class SolicitacaoConcluidaEvento extends SolicitacaoEvento {
        private SolicitacaoConcluidaEvento(SolicitacaoAcademica s) { super(s); }
    }

    public static class SolicitacaoCanceladaEvento extends SolicitacaoEvento {
        private SolicitacaoCanceladaEvento(SolicitacaoAcademica s) { super(s); }
    }
}
