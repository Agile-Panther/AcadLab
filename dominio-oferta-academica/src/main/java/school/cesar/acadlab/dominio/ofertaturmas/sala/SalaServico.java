package school.cesar.acadlab.dominio.ofertaturmas.sala;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Serviço de domínio do cadastro de salas. Orquestra o ciclo de vida do agregado
 * (criar, ativar, inativar, alterar capacidade) mantendo o controlador livre de
 * acesso direto ao repositório de domínio.
 */
public class SalaServico {

    private final SalaRepositorio repositorio;

    public SalaServico(SalaRepositorio repositorio) {
        notNull(repositorio, "O repositório não pode ser nulo");
        this.repositorio = repositorio;
    }

    public Sala cadastrar(String nome, int capacidade) {
        var id = repositorio.proximoId();
        var sala = new Sala(id, nome, capacidade);
        repositorio.salvar(sala);
        return sala;
    }

    public void ativar(SalaId id) {
        var sala = obter(id);
        sala.ativar();
        repositorio.salvar(sala);
    }

    public void inativar(SalaId id) {
        var sala = obter(id);
        sala.inativar();
        repositorio.salvar(sala);
    }

    public void alterarCapacidade(SalaId id, int novaCapacidade, int maiorCapacidadeTurmaVinculada) {
        var sala = obter(id);
        sala.alterarCapacidade(novaCapacidade, maiorCapacidadeTurmaVinculada);
        repositorio.salvar(sala);
    }

    private Sala obter(SalaId id) {
        notNull(id, "O id da sala não pode ser nulo");
        try {
            return repositorio.obter(id);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Sala não encontrada");
        }
    }
}
