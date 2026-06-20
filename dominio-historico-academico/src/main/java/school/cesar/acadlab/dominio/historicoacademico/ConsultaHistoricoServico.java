package school.cesar.acadlab.dominio.historicoacademico;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.ArrayList;
import java.util.List;
import school.cesar.acadlab.dominio.historicoacademico.historico.*;
import school.cesar.acadlab.dominio.historicoacademico.iterador.IteradorHistorico;

public class ConsultaHistoricoServico {
    private final HistoricoAcademicoRepositorio repositorio;

    public ConsultaHistoricoServico(HistoricoAcademicoRepositorio repositorio) {
        notNull(repositorio, "O repositório não pode ser nulo");
        this.repositorio = repositorio;
    }

    // RN-10: histórico oficial contém apenas registros consolidados de períodos encerrados
    public List<RegistroDisciplina> obterHistoricoOficial(EstudanteId estudanteId) {
        var historico = repositorio.buscarPorEstudante(estudanteId)
                .orElseThrow(() -> new IllegalArgumentException("estudante não possui histórico cadastrado"));
        var resultado = new ArrayList<RegistroDisciplina>();
        IteradorHistorico<RegistroDisciplina> iterador = historico.iteradorRegistros();
        while (iterador.temProximo()) {
            resultado.add(iterador.proximo());
        }
        return resultado;
    }

    public HistoricoAcademico obterHistorico(HistoricoAcademicoId id) {
        return repositorio.obter(id);
    }
}
