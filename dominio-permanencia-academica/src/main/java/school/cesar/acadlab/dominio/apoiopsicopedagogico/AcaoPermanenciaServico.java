package school.cesar.acadlab.dominio.apoiopsicopedagogico;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.acaopermanencia.AcaoPermanencia;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.acaopermanencia.AcaoPermanenciaRepositorio;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.profissional.CoordenadorId;

public class AcaoPermanenciaServico {
    private final AcaoPermanenciaRepositorio acaoPermanenciaRepositorio;

    public AcaoPermanenciaServico(AcaoPermanenciaRepositorio acaoPermanenciaRepositorio) {
        notNull(acaoPermanenciaRepositorio, "O repositório de ações de permanência não pode ser nulo");
        this.acaoPermanenciaRepositorio = acaoPermanenciaRepositorio;
    }

    public void registrar(CoordenadorId coordenadorId, String descricao, String indicadoresAgregados) {
        if (coordenadorId == null)
            throw new IllegalStateException("coordenador é obrigatório para registrar ações de permanência");
        notNull(descricao, "A descrição não pode ser nula");
        notBlank(descricao, "A descrição não pode estar em branco");
        notNull(indicadoresAgregados, "Os indicadores agregados não podem ser nulos");
        notBlank(indicadoresAgregados, "Os indicadores agregados não podem estar em branco");

        var id = acaoPermanenciaRepositorio.proximaAcaoId();
        var acao = new AcaoPermanencia(id, coordenadorId, descricao, indicadoresAgregados);
        acaoPermanenciaRepositorio.salvar(acao);
    }
}
