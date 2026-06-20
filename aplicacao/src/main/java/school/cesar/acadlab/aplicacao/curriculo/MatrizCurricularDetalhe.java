package school.cesar.acadlab.aplicacao.curriculo;

import java.util.List;

public record MatrizCurricularDetalhe(
        int id,
        int cursoId,
        String nome,
        int cargaHorariaMinima,
        int creditosExigidos,
        int maximoTrancamentos,
        String status,
        List<ItemDetalhe> itens,
        List<DependenciaDetalhe> preRequisitos,
        List<DependenciaDetalhe> correquisitos) {

    public record ItemDetalhe(int disciplinaId, String tipo, int cargaHoraria, int creditos) {}

    public record DependenciaDetalhe(int disciplinaId, int dependenciaId) {}
}
