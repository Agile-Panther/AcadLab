package school.cesar.acadlab.dominio.estagios;

import school.cesar.acadlab.dominio.estagios.candidatura.CandidaturaId;
import school.cesar.acadlab.dominio.estagios.estagio.EstagioId;
import school.cesar.acadlab.dominio.estagios.oportunidade.EmpresaId;
import school.cesar.acadlab.dominio.estagios.oportunidade.EstudanteId;
import school.cesar.acadlab.dominio.estagios.oportunidade.SetorEstagiosId;

public class EstagiosFuncionalidade {
    public OportunidadeRepositorioMemoria oportunidadeRepositorio;
    public CandidaturaRepositorioMemoria candidaturaRepositorio;
    public EstagioRepositorioMemoria estagioRepositorio;
    public EstagioServico servico;
    public EstagioId estagioId;
    public CandidaturaId candidaturaId;
    public RuntimeException excecao;

    public EstagiosFuncionalidade() {
        oportunidadeRepositorio = new OportunidadeRepositorioMemoria();
        candidaturaRepositorio = new CandidaturaRepositorioMemoria();
        estagioRepositorio = new EstagioRepositorioMemoria();
        servico = new EstagioServico(oportunidadeRepositorio, candidaturaRepositorio,
                estagioRepositorio, (estudante, criterio) -> true);
    }

    public void criarEstagioEmAndamento(int estudanteId) {
        var setorId = new SetorEstagiosId(1);
        var oportunidadeId = servico.cadastrarOportunidade(new EmpresaId(10), "Estágio em TI", 480);
        servico.publicarOportunidade(oportunidadeId, setorId);
        candidaturaId = servico.registrarCandidatura(oportunidadeId, new EstudanteId(estudanteId),
                java.time.LocalDate.now());
        servico.deferir(candidaturaId);
        estagioId = servico.encaminharEConfirmar(candidaturaId, new EmpresaId(10));
    }
}
