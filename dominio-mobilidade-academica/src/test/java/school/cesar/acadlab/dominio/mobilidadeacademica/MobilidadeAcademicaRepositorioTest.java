package school.cesar.acadlab.dominio.mobilidadeacademica;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade.EstudanteId;
import school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade.MobilidadeAcademica;
import school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade.MobilidadeAcademicaId;
import school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade.MobilidadeAcademicaRepositorio;

public class MobilidadeAcademicaRepositorioTest implements MobilidadeAcademicaRepositorio {

    private int proximoIdSeq = 1;
    private final Map<MobilidadeAcademicaId, MobilidadeAcademica> mobilidades = new HashMap<>();

    @Override
    public MobilidadeAcademicaId proximaMobilidadeId() {
        return new MobilidadeAcademicaId(proximoIdSeq++);
    }

    @Override
    public void salvar(MobilidadeAcademica mobilidade) {
        mobilidades.put(mobilidade.getId(), mobilidade);
    }

    @Override
    public Optional<MobilidadeAcademica> buscarPorId(MobilidadeAcademicaId id) {
        return Optional.ofNullable(mobilidades.get(id));
    }

    @Override
    public List<MobilidadeAcademica> buscarPorEstudante(EstudanteId estudanteId) {
        var resultado = new ArrayList<MobilidadeAcademica>();
        for (var m : mobilidades.values()) {
            if (m.getEstudanteId().equals(estudanteId)) {
                resultado.add(m);
            }
        }
        return resultado;
    }
}
