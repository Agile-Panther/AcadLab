package school.cesar.acadlab.dominio.estagios.estagio;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import school.cesar.acadlab.dominio.estagios.oportunidade.CoordenadorId;
import school.cesar.acadlab.dominio.estagios.oportunidade.EmpresaId;
import school.cesar.acadlab.dominio.estagios.oportunidade.EstudanteId;
import school.cesar.acadlab.dominio.estagios.oportunidade.OportunidadeId;

public class Estagio {
    private final EstagioId id;
    private final OportunidadeId oportunidadeId;
    private final EstudanteId estudanteId;
    private final EmpresaId empresaId;
    private StatusEstagio status;
    private final List<Relatorio> relatorios;

    public Estagio(EstagioId id, OportunidadeId oportunidadeId, EstudanteId estudanteId, EmpresaId empresaId) {
        this.id = notNull(id, "id é obrigatório");
        this.oportunidadeId = notNull(oportunidadeId, "oportunidadeId é obrigatório");
        this.estudanteId = notNull(estudanteId, "estudanteId é obrigatório");
        this.empresaId = notNull(empresaId, "empresaId é obrigatório");
        this.status = StatusEstagio.EM_ANDAMENTO;
        this.relatorios = new ArrayList<>();
    }

    public void submeterRelatorio(int numero, String descricao) {
        if (status != StatusEstagio.EM_ANDAMENTO) {
            throw new IllegalStateException("RN-7: Relatórios só podem ser submetidos com estágio em andamento");
        }
        boolean numeroDuplicado = relatorios.stream().anyMatch(r -> r.getNumero() == numero);
        if (numeroDuplicado) {
            throw new IllegalStateException("RN-8: Já existe relatório com o número " + numero);
        }
        relatorios.add(new Relatorio(numero, descricao));
    }

    public void avaliarRelatorio(int numero, StatusRelatorio resultado) {
        if (resultado == StatusRelatorio.PENDENTE) {
            throw new IllegalArgumentException("RN-9: Resultado da avaliação não pode ser PENDENTE");
        }
        var relatorio = encontrarRelatorioPorNumero(numero);
        if (relatorio.getStatus() != StatusRelatorio.PENDENTE) {
            throw new IllegalStateException("RN-10: Apenas relatórios pendentes podem ser avaliados");
        }
        relatorio.avaliar(resultado);
    }

    public void solicitarEncerramento() {
        if (status != StatusEstagio.EM_ANDAMENTO) {
            throw new IllegalStateException("RN-11: Encerramento só pode ser solicitado com estágio em andamento");
        }
        this.status = StatusEstagio.ENCERRAMENTO_SOLICITADO;
    }

    public void homologarEncerramento(CoordenadorId coordenadorId) {
        if (status != StatusEstagio.ENCERRAMENTO_SOLICITADO) {
            throw new IllegalStateException("RN-12: Homologação só permitida após solicitação de encerramento");
        }
        this.status = StatusEstagio.ENCERRADO;
    }

    public static Estagio reconstituir(EstagioId id, OportunidadeId oportunidadeId,
                                       EstudanteId estudanteId, EmpresaId empresaId,
                                       StatusEstagio status, List<Relatorio> relatorios) {
        var e = new Estagio(id, oportunidadeId, estudanteId, empresaId);
        e.status = status;
        e.relatorios.addAll(relatorios);
        return e;
    }

    private Relatorio encontrarRelatorioPorNumero(int numero) {
        return relatorios.stream()
                .filter(r -> r.getNumero() == numero)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Relatório " + numero + " não encontrado"));
    }

    public EstagioId getId() { return id; }
    public OportunidadeId getOportunidadeId() { return oportunidadeId; }
    public EstudanteId getEstudanteId() { return estudanteId; }
    public EmpresaId getEmpresaId() { return empresaId; }
    public StatusEstagio getStatus() { return status; }
    public List<Relatorio> getRelatorios() { return Collections.unmodifiableList(relatorios); }
}
