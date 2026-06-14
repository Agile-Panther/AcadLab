package school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade;

public class ItemPlanoEstudos {

    private final DisciplinaId disciplinaExterna;
    private final DisciplinaId disciplinaEquivalente;
    private int cargaHorariaExterna;
    private int cargaHorariaEquivalente;
    private StatusItemPlano status;
    private boolean comprovanteAnexado;
    private boolean resultadoRegistrado;

    public ItemPlanoEstudos(DisciplinaId disciplinaExterna, DisciplinaId disciplinaEquivalente,
                            int cargaHorariaExterna, int cargaHorariaEquivalente) {
        this.disciplinaExterna = disciplinaExterna;
        this.disciplinaEquivalente = disciplinaEquivalente;
        this.cargaHorariaExterna = cargaHorariaExterna;
        this.cargaHorariaEquivalente = cargaHorariaEquivalente;
        this.status = StatusItemPlano.PENDENTE;
        this.comprovanteAnexado = false;
        this.resultadoRegistrado = false;
    }

    public void autorizar(int cargaHorariaExterna, int cargaHorariaEquivalente) {
        if (cargaHorariaExterna < cargaHorariaEquivalente) {
            throw new IllegalStateException("RN-3: Carga horária externa inferior à disciplina equivalente");
        }
        this.cargaHorariaExterna = cargaHorariaExterna;
        this.cargaHorariaEquivalente = cargaHorariaEquivalente;
        this.status = StatusItemPlano.AUTORIZADO;
    }

    public void anexarComprovante() {
        this.comprovanteAnexado = true;
    }

    public void registrarResultado() {
        this.resultadoRegistrado = true;
    }

    public static ItemPlanoEstudos reconstituir(DisciplinaId disciplinaExterna, DisciplinaId disciplinaEquivalente,
                                                int cargaHorariaExterna, int cargaHorariaEquivalente,
                                                StatusItemPlano status, boolean comprovanteAnexado,
                                                boolean resultadoRegistrado) {
        var item = new ItemPlanoEstudos(disciplinaExterna, disciplinaEquivalente,
                cargaHorariaExterna, cargaHorariaEquivalente);
        item.status = status;
        item.comprovanteAnexado = comprovanteAnexado;
        item.resultadoRegistrado = resultadoRegistrado;
        return item;
    }

    public DisciplinaId getDisciplinaExterna() { return disciplinaExterna; }
    public DisciplinaId getDisciplinaEquivalente() { return disciplinaEquivalente; }
    public int getCargaHorariaExterna() { return cargaHorariaExterna; }
    public int getCargaHorariaEquivalente() { return cargaHorariaEquivalente; }
    public StatusItemPlano getStatus() { return status; }
    public boolean isAutorizado() { return status == StatusItemPlano.AUTORIZADO; }
    public boolean isComprovanteAnexado() { return comprovanteAnexado; }
    public boolean isResultadoRegistrado() { return resultadoRegistrado; }
}
