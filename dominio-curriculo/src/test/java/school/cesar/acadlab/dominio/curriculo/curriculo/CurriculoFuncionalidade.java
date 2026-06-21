package school.cesar.acadlab.dominio.curriculo.curriculo;

import static org.junit.jupiter.api.Assertions.*;

import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;

import school.cesar.acadlab.dominio.curriculo.CursoId;
import school.cesar.acadlab.dominio.curriculo.CurriculoRepositorioTest;
import school.cesar.acadlab.dominio.curriculo.DisciplinaId;
import school.cesar.acadlab.dominio.curriculo.MatrizCurricular;
import school.cesar.acadlab.dominio.curriculo.MatrizCurricularId;
import school.cesar.acadlab.dominio.curriculo.StatusMatriz;
import school.cesar.acadlab.dominio.curriculo.TipoDisciplina;

public class CurriculoFuncionalidade {

    private CurriculoRepositorioTest repositorio;
    private MatrizCurricularId matrizId;
    private MatrizCurricular matriz;
    private MatrizCurricular segundaMatriz;
    private CursoId cursoId;
    private RuntimeException excecao;

    @Before
    public void setUp() {
        repositorio = new CurriculoRepositorioTest();
        matrizId = null;
        matriz = null;
        segundaMatriz = null;
        excecao = null;
    }

    // --- Dado ---

    @Dado("um curso com id {int}")
    public void um_curso_com_id(int id) {
        this.cursoId = new CursoId(id);
    }

    @Dado("uma nova matriz curricular chamada {string} com carga horária mínima {int} e créditos mínimos {int}")
    public void uma_nova_matriz_curricular(String nome, int cargaMinima, int creditosMinimos) {
        matrizId = repositorio.proximaMatrizId();
        matriz = new MatrizCurricular(matrizId, cursoId, nome, cargaMinima, creditosMinimos, 3);
    }

    @Dado("uma disciplina com id {int} adicionada com carga horária {int} e créditos {int}")
    public void uma_disciplina_adicionada(int discId, int cargaHoraria, int creditos) {
        matriz.adicionarDisciplina(new DisciplinaId(discId), TipoDisciplina.OBRIGATORIA, cargaHoraria, creditos);
    }

    @Dado("uma matriz curricular com carga horária suficiente para o curso {int}")
    public void matriz_com_carga_suficiente(int courseId) {
        matrizId = repositorio.proximaMatrizId();
        matriz = new MatrizCurricular(matrizId, new CursoId(courseId), "Matriz Principal", 2400, 160, 3);
        matriz.adicionarDisciplina(new DisciplinaId(1), TipoDisciplina.OBRIGATORIA, 2400, 160);
        repositorio.salvar(matriz);
    }

    @Dado("uma matriz curricular já ativa para o curso {int}")
    public void matriz_ja_ativa_para_o_curso(int courseId) {
        MatrizCurricularId primeiraId = repositorio.proximaMatrizId();
        MatrizCurricular primeira = new MatrizCurricular(
                primeiraId, new CursoId(courseId), "Primeira Matriz", 2400, 160, 3);
        primeira.adicionarDisciplina(new DisciplinaId(1), TipoDisciplina.OBRIGATORIA, 2400, 160);
        repositorio.salvar(primeira);
        primeira.ativar(repositorio);
        repositorio.salvar(primeira);
    }

    @Dado("uma segunda matriz curricular com carga horária suficiente para o curso {int}")
    public void segunda_matriz_com_carga_suficiente(int courseId) {
        matrizId = repositorio.proximaMatrizId();
        segundaMatriz = new MatrizCurricular(
                matrizId, new CursoId(courseId), "Segunda Matriz", 2400, 160, 3);
        segundaMatriz.adicionarDisciplina(new DisciplinaId(2), TipoDisciplina.OBRIGATORIA, 2400, 160);
        repositorio.salvar(segundaMatriz);
        matriz = segundaMatriz;
    }

    @Dado("uma matriz curricular com status RASCUNHO")
    public void matriz_em_rascunho() {
        matrizId = repositorio.proximaMatrizId();
        matriz = new MatrizCurricular(matrizId, new CursoId(1), "Matriz Rascunho", 60, 4, 3);
        repositorio.salvar(matriz);
    }

    @Dado("uma matriz curricular com status RASCUNHO com a disciplina {int} já adicionada")
    public void matriz_em_rascunho_com_disciplina(int discId) {
        matrizId = repositorio.proximaMatrizId();
        matriz = new MatrizCurricular(matrizId, new CursoId(1), "Matriz Rascunho", 60, 4, 3);
        matriz.adicionarDisciplina(new DisciplinaId(discId), TipoDisciplina.OBRIGATORIA, 60, 4);
        repositorio.salvar(matriz);
    }

    @Dado("a disciplina {int} possui turmas vinculadas")
    public void disciplina_possui_turmas_vinculadas(int discId) {
        repositorio.adicionarDisciplinaComTurma(new DisciplinaId(discId));
    }

    @Dado("uma matriz curricular com duas disciplinas de ids {int} e {int}")
    public void matriz_com_duas_disciplinas(int id1, int id2) {
        matrizId = repositorio.proximaMatrizId();
        matriz = new MatrizCurricular(matrizId, new CursoId(1), "Matriz Teste", 60, 4, 3);
        matriz.adicionarDisciplina(new DisciplinaId(id1), TipoDisciplina.OBRIGATORIA, 60, 4);
        matriz.adicionarDisciplina(new DisciplinaId(id2), TipoDisciplina.OBRIGATORIA, 60, 4);
        repositorio.salvar(matriz);
    }

    @Dado("a disciplina {int} já é pré-requisito da disciplina {int}")
    public void disciplina_ja_e_prerequisito(int preReqId, int discId) {
        matriz.adicionarPreRequisito(new DisciplinaId(discId), new DisciplinaId(preReqId));
    }

    @Dado("uma matriz curricular com uma disciplina de id {int}")
    public void matriz_com_uma_disciplina(int id) {
        matrizId = repositorio.proximaMatrizId();
        matriz = new MatrizCurricular(matrizId, new CursoId(1), "Matriz Teste", 60, 4, 3);
        matriz.adicionarDisciplina(new DisciplinaId(id), TipoDisciplina.OBRIGATORIA, 60, 4);
        repositorio.salvar(matriz);
    }

    @Dado("uma matriz curricular ATIVA com as disciplinas {int} e {int}")
    public void matriz_ativa_com_duas_disciplinas(int id1, int id2) {
        matrizId = repositorio.proximaMatrizId();
        matriz = new MatrizCurricular(matrizId, new CursoId(1), "Matriz Ativa", 60, 4, 3);
        matriz.adicionarDisciplina(new DisciplinaId(id1), TipoDisciplina.OBRIGATORIA, 60, 4);
        matriz.adicionarDisciplina(new DisciplinaId(id2), TipoDisciplina.OBRIGATORIA, 60, 4);
        repositorio.salvar(matriz);
        matriz.ativar(repositorio);
        repositorio.salvar(matriz);
    }

    // --- Quando ---

    @Quando("o coordenador ativa a matriz")
    public void coordenador_ativa_matriz() {
        repositorio.salvar(matriz);
        matriz.ativar(repositorio);
        repositorio.salvar(matriz);
    }

    @Quando("o coordenador tenta ativar a matriz")
    public void coordenador_tenta_ativar_matriz() {
        repositorio.salvar(matriz);
        try {
            matriz.ativar(repositorio);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Quando("o coordenador tenta ativar a segunda matriz")
    public void coordenador_tenta_ativar_segunda_matriz() {
        try {
            segundaMatriz.ativar(repositorio);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Quando("o coordenador adiciona a disciplina com id {int} à matriz")
    public void coordenador_adiciona_disciplina(int discId) {
        try {
            matriz.adicionarDisciplina(new DisciplinaId(discId), TipoDisciplina.OBRIGATORIA, 60, 4);
            repositorio.salvar(matriz);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Quando("o coordenador tenta adicionar a disciplina {int} novamente")
    public void coordenador_tenta_adicionar_disciplina_novamente(int discId) {
        try {
            matriz.adicionarDisciplina(new DisciplinaId(discId), TipoDisciplina.OPTATIVA, 30, 2);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Quando("o coordenador remove a disciplina {int} da matriz")
    public void coordenador_remove_disciplina(int discId) {
        try {
            matriz.removerDisciplina(new DisciplinaId(discId), repositorio);
            repositorio.salvar(matriz);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Quando("o coordenador tenta remover a disciplina {int}")
    public void coordenador_tenta_remover_disciplina(int discId) {
        try {
            matriz.removerDisciplina(new DisciplinaId(discId), repositorio);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Quando("o coordenador define a disciplina {int} como pré-requisito da disciplina {int}")
    public void coordenador_define_prerequisito(int preReqId, int discId) {
        try {
            matriz.adicionarPreRequisito(new DisciplinaId(discId), new DisciplinaId(preReqId));
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Quando("o coordenador tenta definir a disciplina {int} como pré-requisito da disciplina {int}")
    public void coordenador_tenta_definir_prerequisito(int preReqId, int discId) {
        try {
            matriz.adicionarPreRequisito(new DisciplinaId(discId), new DisciplinaId(preReqId));
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Quando("o coordenador define a disciplina {int} como correquisito da disciplina {int}")
    public void coordenador_define_correquisito(int coreqId, int discId) {
        try {
            matriz.adicionarCorrequisito(new DisciplinaId(discId), new DisciplinaId(coreqId));
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Quando("o coordenador tenta definir a disciplina {int} como correquisito da disciplina {int}")
    public void coordenador_tenta_definir_correquisito(int coreqId, int discId) {
        try {
            matriz.adicionarCorrequisito(new DisciplinaId(discId), new DisciplinaId(coreqId));
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    // --- Então ---

    @Entao("o status da matriz deve ser ATIVA")
    public void status_deve_ser_ativa() {
        assertEquals(StatusMatriz.ATIVA, matriz.getStatus());
    }

    @Entao("o sistema deve rejeitar informando {string}")
    public void sistema_deve_rejeitar_informando(String mensagem) {
        assertNotNull(excecao, "Esperava exceção mas nenhuma foi lançada");
        assertTrue(excecao.getMessage().toLowerCase().contains(mensagem.toLowerCase()),
                "Mensagem esperada: \"" + mensagem + "\", mas obtida: \"" + excecao.getMessage() + "\"");
    }

    @Entao("a matriz deve conter a disciplina com id {int}")
    public void matriz_deve_conter_disciplina(int discId) {
        DisciplinaId expected = new DisciplinaId(discId);
        assertTrue(matriz.getItens().stream().anyMatch(i -> i.getDisciplinaId().equals(expected)));
    }

    @Entao("a matriz não deve conter a disciplina com id {int}")
    public void matriz_nao_deve_conter_disciplina(int discId) {
        DisciplinaId disc = new DisciplinaId(discId);
        assertTrue(matriz.getItens().stream().noneMatch(i -> i.getDisciplinaId().equals(disc)));
    }

    @Entao("a disciplina {int} deve ter a disciplina {int} como pré-requisito")
    public void disciplina_deve_ter_prerequisito(int discId, int preReqId) {
        DisciplinaId disc = new DisciplinaId(discId);
        DisciplinaId preReq = new DisciplinaId(preReqId);
        assertTrue(matriz.getPreRequisitos().containsKey(disc));
        assertTrue(matriz.getPreRequisitos().get(disc).contains(preReq));
    }

    @Entao("a disciplina {int} deve ter a disciplina {int} como correquisito")
    public void disciplina_deve_ter_correquisito(int discId, int coreqId) {
        DisciplinaId disc = new DisciplinaId(discId);
        DisciplinaId coreq = new DisciplinaId(coreqId);
        assertTrue(matriz.getCorrequisitos().containsKey(disc));
        assertTrue(matriz.getCorrequisitos().get(disc).contains(coreq));
    }
}
