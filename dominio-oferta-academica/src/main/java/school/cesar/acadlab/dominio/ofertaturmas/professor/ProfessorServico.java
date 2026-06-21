package school.cesar.acadlab.dominio.ofertaturmas.professor;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Serviço de domínio do cadastro de professores. Orquestra o ciclo de vida do
 * agregado (criar, ativar, inativar) mantendo o controlador livre de acesso
 * direto ao repositório de domínio.
 */
public class ProfessorServico {

    private final ProfessorRepositorio repositorio;

    public ProfessorServico(ProfessorRepositorio repositorio) {
        notNull(repositorio, "O repositório não pode ser nulo");
        this.repositorio = repositorio;
    }

    public Professor cadastrar(String nome) {
        var id = repositorio.proximoId();
        var professor = new Professor(id, nome);
        repositorio.salvar(professor);
        return professor;
    }

    public void ativar(ProfessorId id) {
        var professor = obter(id);
        professor.ativar();
        repositorio.salvar(professor);
    }

    public void inativar(ProfessorId id) {
        var professor = obter(id);
        professor.inativar();
        repositorio.salvar(professor);
    }

    private Professor obter(ProfessorId id) {
        notNull(id, "O id do professor não pode ser nulo");
        try {
            return repositorio.obter(id);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Professor não encontrado");
        }
    }
}
