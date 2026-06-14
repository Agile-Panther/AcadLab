package school.cesar.acadlab.dominio.curriculo.curriculo;

import static org.junit.jupiter.api.Assertions.*;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import school.cesar.acadlab.dominio.curriculo.CursoId;
import school.cesar.acadlab.dominio.curriculo.CurriculoFuncionalidade;
import school.cesar.acadlab.dominio.curriculo.CurriculoRepositorioTest;
import school.cesar.acadlab.dominio.curriculo.DisciplinaId;
import school.cesar.acadlab.dominio.curriculo.MatrizCurricular;
import school.cesar.acadlab.dominio.curriculo.MatrizCurricularId;
import school.cesar.acadlab.dominio.curriculo.StatusMatriz;
import school.cesar.acadlab.dominio.curriculo.TipoDisciplina;

public class CurriculoFuncionalidade extends school.cesar.acadlab.dominio.curriculo.CurriculoFuncionalidade {

    private MatrizCurricularId matrizId;
    private MatrizCurricular matriz;
    private MatrizCurricular segundaMatriz;
    private RuntimeException excecao;

    @Before
    public void setUp() {
        repositorio = new CurriculoRepositorioTest();
        matrizId = null;
        matriz = null;
        segundaMatriz = null;
        excecao = null;
    }

    @Then("the system rejects the activation with message containing {string}")
    public void theSystemRejectsTheActivationWithMessageContaining(String mensagem) {
        assertNotNull(excecao, "Expected exception but none was thrown");
        assertTrue(excecao.getMessage().contains(mensagem),
                "Expected message containing '" + mensagem + "' but got: " + excecao.getMessage());
    }

    @Then("the system rejects the prerequisite with message containing {string}")
    public void theSystemRejectsThePrerequisiteWithMessageContaining(String mensagem) {
        assertNotNull(excecao, "Expected exception but none was thrown");
        assertTrue(excecao.getMessage().contains(mensagem),
                "Expected message containing '" + mensagem + "' but got: " + excecao.getMessage());
    }

    @Then("the system rejects the corequisite with message containing {string}")
    public void theSystemRejectsTheCorequisiteWithMessageContaining(String mensagem) {
        assertNotNull(excecao, "Expected exception but none was thrown");
        assertTrue(excecao.getMessage().contains(mensagem),
                "Expected message containing '" + mensagem + "' but got: " + excecao.getMessage());
    }

    @Then("the system rejects the addition with message containing {string}")
    public void theSystemRejectsTheAdditionWithMessageContaining(String mensagem) {
        assertNotNull(excecao, "Expected exception but none was thrown");
        assertTrue(excecao.getMessage().contains(mensagem),
                "Expected message containing '" + mensagem + "' but got: " + excecao.getMessage());
    }

    @Then("the system rejects the removal with message containing {string}")
    public void theSystemRejectsTheRemovalWithMessageContaining(String mensagem) {
        assertNotNull(excecao, "Expected exception but none was thrown");
        assertTrue(excecao.getMessage().contains(mensagem),
                "Expected message containing '" + mensagem + "' but got: " + excecao.getMessage());
    }

    @Then("the matrix status should be ATIVA")
    public void theMatrixStatusShouldBeATIVA() {
        assertEquals(StatusMatriz.ATIVA, matriz.getStatus());
    }

    @Given("a course with id {int}")
    public void aCourseWithId(int id) {
    }

    @And("a new curricular matrix named {string} with minimum workload {int} and minimum credits {int}")
    public void aNewCurricularMatrixNamed(String nome, int cargaMinima, int creditosMinimos) {
        matrizId = repositorio.proximaMatrizId();
        matriz = new MatrizCurricular(matrizId, new CursoId(10), nome, cargaMinima, creditosMinimos, 3);
    }

    @And("a discipline with id {int} is added with workload {int} and credits {int}")
    public void aDisciplineIsAddedWithWorkloadAndCredits(int discId, int cargaHoraria, int creditos) {
        matriz.adicionarDisciplina(new DisciplinaId(discId), TipoDisciplina.OBRIGATORIA, cargaHoraria, creditos);
    }

    @When("the coordinator activates the matrix")
    public void theCoordinatorActivatesTheMatrix() {
        repositorio.salvar(matriz);
        matriz.ativar(repositorio);
        repositorio.salvar(matriz);
    }

    @When("the coordinator tries to activate the matrix")
    public void theCoordinatorTriesToActivateTheMatrix() {
        repositorio.salvar(matriz);
        try {
            matriz.ativar(repositorio);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Given("a curricular matrix with two disciplines with ids {int} and {int}")
    public void aCurricularMatrixWithTwoDisciplinesWithIds(int id1, int id2) {
        matrizId = repositorio.proximaMatrizId();
        matriz = new MatrizCurricular(matrizId, new CursoId(1), "Matriz Teste", 60, 4, 3);
        matriz.adicionarDisciplina(new DisciplinaId(id1), TipoDisciplina.OBRIGATORIA, 60, 4);
        matriz.adicionarDisciplina(new DisciplinaId(id2), TipoDisciplina.OBRIGATORIA, 60, 4);
        repositorio.salvar(matriz);
    }

    @And("discipline {int} is already a prerequisite for discipline {int}")
    public void disciplineIsAlreadyAPrerequisiteForDiscipline(int preReqId, int discId) {
        matriz.adicionarPreRequisito(new DisciplinaId(discId), new DisciplinaId(preReqId));
    }

    @When("the coordinator adds discipline {int} as a prerequisite for discipline {int}")
    public void theCoordinatorAddsDisciplineAsAPrerequisiteForDiscipline(int preReqId, int discId) {
        try {
            matriz.adicionarPreRequisito(new DisciplinaId(discId), new DisciplinaId(preReqId));
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Then("discipline {int} should have discipline {int} as a prerequisite")
    public void disciplineShouldHaveDisciplineAsAPrerequisite(int discId, int preReqId) {
        DisciplinaId disc = new DisciplinaId(discId);
        DisciplinaId preReq = new DisciplinaId(preReqId);
        assertTrue(matriz.getPreRequisitos().containsKey(disc));
        assertTrue(matriz.getPreRequisitos().get(disc).contains(preReq));
    }

    @When("the coordinator tries to add discipline {int} as a prerequisite for discipline {int}")
    public void theCoordinatorTriesToAddDisciplineAsAPrerequisiteForDiscipline(int preReqId, int discId) {
        try {
            matriz.adicionarPreRequisito(new DisciplinaId(discId), new DisciplinaId(preReqId));
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @When("the coordinator adds discipline {int} as a corequisite for discipline {int}")
    public void theCoordinatorAddsDisciplineAsACorequisiteForDiscipline(int coreqId, int discId) {
        try {
            matriz.adicionarCorrequisito(new DisciplinaId(discId), new DisciplinaId(coreqId));
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Then("discipline {int} should have discipline {int} as a corequisite")
    public void disciplineShouldHaveDisciplineAsACorequisite(int discId, int coreqId) {
        DisciplinaId disc = new DisciplinaId(discId);
        DisciplinaId coreq = new DisciplinaId(coreqId);
        assertTrue(matriz.getCorrequisitos().containsKey(disc));
        assertTrue(matriz.getCorrequisitos().get(disc).contains(coreq));
    }

    @Given("a curricular matrix with one discipline with id {int}")
    public void aCurricularMatrixWithOneDisciplineWithId(int id) {
        matrizId = repositorio.proximaMatrizId();
        matriz = new MatrizCurricular(matrizId, new CursoId(1), "Matriz Teste", 60, 4, 3);
        matriz.adicionarDisciplina(new DisciplinaId(id), TipoDisciplina.OBRIGATORIA, 60, 4);
        repositorio.salvar(matriz);
    }

    @When("the coordinator tries to add discipline {int} as a corequisite for discipline {int}")
    public void theCoordinatorTriesToAddDisciplineAsACorequisiteForDiscipline(int coreqId, int discId) {
        try {
            matriz.adicionarCorrequisito(new DisciplinaId(discId), new DisciplinaId(coreqId));
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Given("a curricular matrix with sufficient workload for course {int}")
    public void aCurricularMatrixWithSufficientWorkloadForCourse(int courseId) {
        matrizId = repositorio.proximaMatrizId();
        matriz = new MatrizCurricular(matrizId, new CursoId(courseId), "Matriz Principal", 2400, 160, 3);
        matriz.adicionarDisciplina(new DisciplinaId(1), TipoDisciplina.OBRIGATORIA, 2400, 160);
        repositorio.salvar(matriz);
    }

    @Given("a curricular matrix already active for course {int}")
    public void aCurricularMatrixAlreadyActiveForCourse(int courseId) {
        MatrizCurricularId primeiraId = repositorio.proximaMatrizId();
        MatrizCurricular primeira = new MatrizCurricular(
                primeiraId, new CursoId(courseId), "Primeira Matriz", 2400, 160, 3);
        primeira.adicionarDisciplina(new DisciplinaId(1), TipoDisciplina.OBRIGATORIA, 2400, 160);
        repositorio.salvar(primeira);
        primeira.ativar(repositorio);
        repositorio.salvar(primeira);
    }

    @And("a second curricular matrix with sufficient workload for course {int}")
    public void aSecondCurricularMatrixWithSufficientWorkloadForCourse(int courseId) {
        matrizId = repositorio.proximaMatrizId();
        segundaMatriz = new MatrizCurricular(
                matrizId, new CursoId(courseId), "Segunda Matriz", 2400, 160, 3);
        segundaMatriz.adicionarDisciplina(new DisciplinaId(2), TipoDisciplina.OBRIGATORIA, 2400, 160);
        repositorio.salvar(segundaMatriz);
        matriz = segundaMatriz;
    }

    @When("the coordinator tries to activate the second matrix")
    public void theCoordinatorTriesToActivateTheSecondMatrix() {
        try {
            segundaMatriz.ativar(repositorio);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Given("a curricular matrix in RASCUNHO status")
    public void aCurricularMatrixInRASCUNHOStatus() {
        matrizId = repositorio.proximaMatrizId();
        matriz = new MatrizCurricular(matrizId, new CursoId(1), "Matriz Rascunho", 60, 4, 3);
        repositorio.salvar(matriz);
    }

    @Given("a curricular matrix in RASCUNHO status with discipline {int} already added")
    public void aCurricularMatrixInRASCUNHOStatusWithDisciplineAlreadyAdded(int discId) {
        matrizId = repositorio.proximaMatrizId();
        matriz = new MatrizCurricular(matrizId, new CursoId(1), "Matriz Rascunho", 60, 4, 3);
        matriz.adicionarDisciplina(new DisciplinaId(discId), TipoDisciplina.OBRIGATORIA, 60, 4);
        repositorio.salvar(matriz);
    }

    @When("the coordinator adds a discipline with id {int} to the matrix")
    public void theCoordinatorAddsADisciplineWithIdToTheMatrix(int discId) {
        try {
            matriz.adicionarDisciplina(new DisciplinaId(discId), TipoDisciplina.OBRIGATORIA, 60, 4);
            repositorio.salvar(matriz);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Then("the matrix should contain the discipline with id {int}")
    public void theMatrixShouldContainTheDisciplineWithId(int discId) {
        DisciplinaId expected = new DisciplinaId(discId);
        assertTrue(matriz.getItens().stream().anyMatch(i -> i.getDisciplinaId().equals(expected)));
    }

    @When("the coordinator tries to add discipline {int} again")
    public void theCoordinatorTriesToAddDisciplineAgain(int discId) {
        try {
            matriz.adicionarDisciplina(new DisciplinaId(discId), TipoDisciplina.OPTATIVA, 30, 2);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @When("the coordinator removes discipline {int} from the matrix")
    public void theCoordinatorRemovesDisciplineFromTheMatrix(int discId) {
        try {
            matriz.removerDisciplina(new DisciplinaId(discId), repositorio);
            repositorio.salvar(matriz);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Then("the matrix should not contain the discipline with id {int}")
    public void theMatrixShouldNotContainTheDisciplineWithId(int discId) {
        DisciplinaId disc = new DisciplinaId(discId);
        assertTrue(matriz.getItens().stream().noneMatch(i -> i.getDisciplinaId().equals(disc)));
    }

    @And("discipline {int} is linked to existing classes")
    public void disciplineIsLinkedToExistingClasses(int discId) {
        repositorio.adicionarDisciplinaComTurma(new DisciplinaId(discId));
    }

    @When("the coordinator tries to remove discipline {int}")
    public void theCoordinatorTriesToRemoveDiscipline(int discId) {
        try {
            matriz.removerDisciplina(new DisciplinaId(discId), repositorio);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }
}
