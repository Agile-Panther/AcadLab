package school.cesar.acadlab.dominio.estagios;

import school.cesar.acadlab.dominio.estagios.estagio.EstagioId;
import school.cesar.acadlab.dominio.estagios.oportunidade.CoordenadorId;
import school.cesar.acadlab.dominio.estagios.oportunidade.EmpresaId;
import school.cesar.acadlab.dominio.estagios.oportunidade.EstudanteId;

public class EstagiosFuncionalidade {
    protected OportunidadeRepositorioMemoria oportunidadeRepositorio;
    protected EstagioRepositorioMemoria estagioRepositorio;
    protected EstagioServico servico;
    protected EstagioId estagioId;
    protected RuntimeException excecao;

    public EstagiosFuncionalidade() {
        oportunidadeRepositorio = new OportunidadeRepositorioMemoria();
        estagioRepositorio = new EstagioRepositorioMemoria();
        servico = new EstagioServico(oportunidadeRepositorio, estagioRepositorio);
    }

    protected void criarEstagioEmAndamento(int estudanteId) {
        var oportunidadeId = servico.cadastrarOportunidade(new EmpresaId(10), "Estágio em TI", 480);
        servico.candidatar(oportunidadeId, new EstudanteId(estudanteId));
        servico.encaminhar(oportunidadeId, new CoordenadorId(30));
        estagioId = servico.confirmar(oportunidadeId, new EmpresaId(10));
    }
}
