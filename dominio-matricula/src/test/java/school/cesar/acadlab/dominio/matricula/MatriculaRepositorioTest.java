package school.cesar.acadlab.dominio.matricula;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import school.cesar.acadlab.dominio.matricula.matricula.EstudanteId;
import school.cesar.acadlab.dominio.matricula.matricula.Matricula;
import school.cesar.acadlab.dominio.matricula.matricula.MatriculaId;
import school.cesar.acadlab.dominio.matricula.matricula.MatriculaRepositorio;

public class MatriculaRepositorioTest implements MatriculaRepositorio {
    private final List<Matricula> dados = new ArrayList<>();
    private final AtomicInteger contador = new AtomicInteger(0);

    @Override
    public void salvar(Matricula matricula) {
        dados.removeIf(m -> m.getId().equals(matricula.getId()));
        dados.add(matricula);
    }

    @Override
    public Optional<Matricula> buscarPorId(MatriculaId id) {
        return dados.stream().filter(m -> m.getId().equals(id)).findFirst();
    }

    @Override
    public List<Matricula> buscarPorEstudante(EstudanteId estudanteId) {
        return dados.stream().filter(m -> m.getEstudanteId().equals(estudanteId)).toList();
    }

    @Override
    public MatriculaId proximaMatriculaId() {
        return new MatriculaId(contador.incrementAndGet());
    }
}
