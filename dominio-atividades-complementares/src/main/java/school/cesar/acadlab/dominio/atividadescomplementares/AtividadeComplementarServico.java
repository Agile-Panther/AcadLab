package school.cesar.acadlab.dominio.atividadescomplementares;

import static org.apache.commons.lang3.Validate.notNull;
import school.cesar.acadlab.dominio.atividadescomplementares.atividade.*;
import school.cesar.acadlab.dominio.evento.EventoBarramento;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

public class AtividadeComplementarServico {
    private final AtividadeComplementarRepositorio repositorio;
    private final VerificadorVinculoEstudante verificadorVinculo;
    private final VerificadorCertificadoDuplicado verificadorCertificado;
    private final VerificadorLimiteCategoria verificadorLimite;
    private final VerificadorContabilizacaoIntegralizacao verificadorContabilizacao;
    private final EventoBarramento barramento;

    public AtividadeComplementarServico(AtividadeComplementarRepositorio repositorio,
            VerificadorVinculoEstudante verificadorVinculo,
            VerificadorCertificadoDuplicado verificadorCertificado,
            VerificadorLimiteCategoria verificadorLimite,
            VerificadorContabilizacaoIntegralizacao verificadorContabilizacao,
            EventoBarramento barramento) {
        notNull(repositorio, "repositório obrigatório");
        notNull(verificadorVinculo, "verificadorVinculo obrigatório");
        notNull(verificadorCertificado, "verificadorCertificado obrigatório");
        notNull(verificadorLimite, "verificadorLimite obrigatório");
        notNull(verificadorContabilizacao, "verificadorContabilizacao obrigatório");
        notNull(barramento, "barramento obrigatório");
        this.repositorio = repositorio;
        this.verificadorVinculo = verificadorVinculo;
        this.verificadorCertificado = verificadorCertificado;
        this.verificadorLimite = verificadorLimite;
        this.verificadorContabilizacao = verificadorContabilizacao;
        this.barramento = barramento;
    }

    public AtividadeComplementar submeter(EstudanteId estudanteId, CategoriaAtividadeId categoriaId,
            int horas, LocalDate dataRealizacao, String identificadorCertificado, String descricao) {
        notNull(estudanteId, "estudanteId obrigatório");
        if (!verificadorVinculo.estaNoVinculo(estudanteId, dataRealizacao))
            throw new IllegalStateException("RN1: A atividade deve ter sido realizada durante o período de vínculo do estudante");
        if (verificadorCertificado.jaUtilizado(estudanteId, identificadorCertificado))
            throw new IllegalStateException("RN2: O mesmo comprovante não pode ser usado em duas atividades");
        var id = repositorio.proximoId();
        var atividade = new AtividadeComplementar(id, estudanteId, categoriaId,
                identificadorCertificado, descricao, horas, dataRealizacao);
        repositorio.salvar(atividade);
        return atividade;
    }

    public void deferir(AtividadeComplementarId id, int horasAprovadas) {
        notNull(id, "id obrigatório");
        var atividade = repositorio.obter(id);
        if (verificadorLimite.excedeLimite(atividade.getEstudanteId(), atividade.getCategoriaId(), horasAprovadas))
            throw new IllegalStateException("RN3: Limite máximo de horas por categoria não pode ser excedido");
        var evento = atividade.deferir(horasAprovadas);
        repositorio.salvar(atividade);
        barramento.postar(evento);
    }

    public void indeferir(AtividadeComplementarId id, String justificativa) {
        notNull(id, "id obrigatório");
        var atividade = repositorio.obter(id);
        var evento = atividade.indeferir(justificativa);
        repositorio.salvar(atividade);
        barramento.postar(evento);
    }

    public void solicitarRevisao(AtividadeComplementarId id, String justificativa) {
        notNull(id, "id obrigatório");
        if (verificadorContabilizacao.foiContabilizada(id))
            throw new IllegalStateException("RN4: Revisão não permitida para atividade já contabilizada na integralização curricular");
        var atividade = repositorio.obter(id);
        var evento = atividade.solicitarRevisao(justificativa);
        repositorio.salvar(atividade);
        barramento.postar(evento);
    }

    public void cancelar(AtividadeComplementarId id) {
        notNull(id, "id obrigatório");
        var atividade = repositorio.obter(id);
        var evento = atividade.cancelar();
        repositorio.salvar(atividade);
        barramento.postar(evento);
    }

    public Map<CategoriaAtividadeId, Integer> calcularSaldoHoras(EstudanteId estudanteId) {
        notNull(estudanteId, "estudanteId obrigatório");
        return repositorio.pesquisarPorEstudante(estudanteId).stream()
                .filter(a -> a.getStatus() == StatusAtividade.DEFERIDA)
                .collect(Collectors.groupingBy(
                        AtividadeComplementar::getCategoriaId,
                        Collectors.summingInt(AtividadeComplementar::getHorasAprovadas)));
    }
}
