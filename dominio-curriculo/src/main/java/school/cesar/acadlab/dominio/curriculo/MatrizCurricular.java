package school.cesar.acadlab.dominio.curriculo;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import school.cesar.acadlab.dominio.curriculo.porta.ConsultaMatrizAtivaPorta;
import school.cesar.acadlab.dominio.curriculo.porta.ConsultaTurmasPorta;

public class MatrizCurricular {

    private final MatrizCurricularId id;
    private final CursoId cursoId;
    private final String nome;
    private final int cargaHorariaMinima;
    private final int creditosExigidos;
    private final int maximoTrancamentos;
    private StatusMatriz status;
    private final List<ItemMatriz> itens;
    private final Map<DisciplinaId, List<DisciplinaId>> preRequisitos;
    private final Map<DisciplinaId, List<DisciplinaId>> correquisitos;

    public MatrizCurricular(MatrizCurricularId id, CursoId cursoId, String nome,
                            int cargaHorariaMinima, int creditosExigidos, int maximoTrancamentos) {
        notNull(id, "Id não pode ser nulo");
        notNull(cursoId, "CursoId não pode ser nulo");
        notBlank(nome, "Nome não pode ser vazio");
        isTrue(cargaHorariaMinima > 0, "Carga horária mínima deve ser positiva");
        isTrue(creditosExigidos > 0, "Créditos exigidos devem ser positivos");
        isTrue(maximoTrancamentos >= 0, "Máximo de trancamentos não pode ser negativo");
        this.id = id;
        this.cursoId = cursoId;
        this.nome = nome;
        this.cargaHorariaMinima = cargaHorariaMinima;
        this.creditosExigidos = creditosExigidos;
        this.maximoTrancamentos = maximoTrancamentos;
        this.status = StatusMatriz.RASCUNHO;
        this.itens = new ArrayList<>();
        this.preRequisitos = new HashMap<>();
        this.correquisitos = new HashMap<>();
    }

    public MatrizCurricularId getId() { return id; }
    public CursoId getCursoId() { return cursoId; }
    public String getNome() { return nome; }
    public int getCargaHorariaMinima() { return cargaHorariaMinima; }
    public int getCreditosExigidos() { return creditosExigidos; }
    public int getMaximoTrancamentos() { return maximoTrancamentos; }
    public StatusMatriz getStatus() { return status; }
    public List<ItemMatriz> getItens() { return Collections.unmodifiableList(itens); }
    public Map<DisciplinaId, List<DisciplinaId>> getPreRequisitos() { return Collections.unmodifiableMap(preRequisitos); }
    public Map<DisciplinaId, List<DisciplinaId>> getCorrequisitos() { return Collections.unmodifiableMap(correquisitos); }

    public void adicionarDisciplina(DisciplinaId disciplinaId, TipoDisciplina tipo, int cargaHoraria, int creditos) {
        notNull(disciplinaId, "DisciplinaId não pode ser nulo");
        notNull(tipo, "TipoDisciplina não pode ser nulo");

        if (status == StatusMatriz.ATIVA) {
            throw new IllegalStateException("RN-8: Não é possível alterar matriz ativa");
        }

        boolean jaExiste = itens.stream().anyMatch(i -> i.getDisciplinaId().equals(disciplinaId));
        if (jaExiste) {
            throw new IllegalStateException("RN-1: Disciplina já existe nessa matriz");
        }

        itens.add(new ItemMatriz(disciplinaId, tipo, cargaHoraria, creditos));
    }

    public void removerDisciplina(DisciplinaId disciplinaId, ConsultaTurmasPorta porta) {
        notNull(disciplinaId, "DisciplinaId não pode ser nulo");
        notNull(porta, "ConsultaTurmasPorta não pode ser nulo");

        if (status == StatusMatriz.ATIVA) {
            throw new IllegalStateException("RN-8: Não é possível alterar matriz ativa");
        }

        if (porta.existeTurmaParaDisciplina(disciplinaId)) {
            throw new IllegalStateException("RN-9: Disciplina vinculada a turmas não pode ser removida");
        }

        itens.removeIf(i -> i.getDisciplinaId().equals(disciplinaId));
        preRequisitos.remove(disciplinaId);
        correquisitos.remove(disciplinaId);
        preRequisitos.values().forEach(lista -> lista.remove(disciplinaId));
        correquisitos.values().forEach(lista -> lista.remove(disciplinaId));
    }

    public void adicionarPreRequisito(DisciplinaId disciplina, DisciplinaId preRequisito) {
        notNull(disciplina, "DisciplinaId não pode ser nulo");
        notNull(preRequisito, "PreRequisito DisciplinaId não pode ser nulo");

        boolean disciplinaExiste = itens.stream().anyMatch(i -> i.getDisciplinaId().equals(disciplina));
        boolean preRequisitoExiste = itens.stream().anyMatch(i -> i.getDisciplinaId().equals(preRequisito));

        if (!disciplinaExiste || !preRequisitoExiste) {
            throw new IllegalArgumentException("Ambas as disciplinas devem pertencer à matriz");
        }

        if (existeCiclo(disciplina, preRequisito)) {
            throw new IllegalStateException("RN-3: Pré-requisito cíclico detectado");
        }

        preRequisitos.computeIfAbsent(disciplina, k -> new ArrayList<>()).add(preRequisito);
    }

    public void adicionarCorrequisito(DisciplinaId disciplina, DisciplinaId correquisito) {
        notNull(disciplina, "DisciplinaId não pode ser nulo");
        notNull(correquisito, "Correquisito DisciplinaId não pode ser nulo");

        boolean disciplinaExiste = itens.stream().anyMatch(i -> i.getDisciplinaId().equals(disciplina));
        boolean corequisitoExiste = itens.stream().anyMatch(i -> i.getDisciplinaId().equals(correquisito));

        if (!disciplinaExiste || !corequisitoExiste) {
            throw new IllegalArgumentException("RN-4: Correquisito deve pertencer à mesma matriz");
        }

        correquisitos.computeIfAbsent(disciplina, k -> new ArrayList<>()).add(correquisito);
    }

    public void ativar(ConsultaMatrizAtivaPorta porta) {
        notNull(porta, "ConsultaMatrizAtivaPorta não pode ser nulo");

        if (porta.existeMatrizAtivaParaCurso(cursoId)) {
            throw new IllegalStateException("RN-5: Já existe matriz ativa para este curso");
        }

        int totalCargaHoraria = itens.stream().mapToInt(ItemMatriz::getCargaHoraria).sum();
        int totalCreditos = itens.stream().mapToInt(ItemMatriz::getCreditos).sum();

        if (totalCargaHoraria < cargaHorariaMinima || totalCreditos < creditosExigidos) {
            throw new IllegalStateException("RN-2: Carga horária ou créditos insuficientes para ativação");
        }

        this.status = StatusMatriz.ATIVA;
    }

    public void desativar() {
        this.status = StatusMatriz.INATIVA;
    }

    public static MatrizCurricular reconstituir(MatrizCurricularId id, CursoId cursoId, String nome,
            int cargaHorariaMinima, int creditosExigidos, int maximoTrancamentos,
            StatusMatriz status, List<ItemMatriz> itens,
            Map<DisciplinaId, List<DisciplinaId>> preRequisitos,
            Map<DisciplinaId, List<DisciplinaId>> correquisitos) {
        MatrizCurricular m = new MatrizCurricular(id, cursoId, nome, cargaHorariaMinima, creditosExigidos, maximoTrancamentos);
        m.status = status;
        m.itens.addAll(itens);
        m.preRequisitos.putAll(preRequisitos);
        m.correquisitos.putAll(correquisitos);
        return m;
    }

    private boolean existeCiclo(DisciplinaId disciplina, DisciplinaId preRequisito) {
        Set<DisciplinaId> visitados = new HashSet<>();
        return alcancavel(preRequisito, disciplina, visitados);
    }

    private boolean alcancavel(DisciplinaId origem, DisciplinaId destino, Set<DisciplinaId> visitados) {
        if (origem.equals(destino)) return true;
        if (!visitados.add(origem)) return false;
        List<DisciplinaId> preReqs = preRequisitos.getOrDefault(origem, Collections.emptyList());
        for (DisciplinaId pr : preReqs) {
            if (alcancavel(pr, destino, visitados)) return true;
        }
        return false;
    }
}
