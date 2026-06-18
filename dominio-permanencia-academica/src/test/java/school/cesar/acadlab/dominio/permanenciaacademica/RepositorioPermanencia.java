package school.cesar.acadlab.dominio.permanenciaacademica;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RepositorioPermanencia
        implements EditalRepositorio, InscricaoRepositorio, BeneficioRepositorio {

    /*-- Edital --*/
    private int proximoEditalIdSeq = 1;
    private final Map<EditalId, Edital> editais = new HashMap<>();

    @Override
    public EditalId proximoEditalId() { return new EditalId(proximoEditalIdSeq++); }

    @Override
    public void salvar(Edital edital) {
        notNull(edital, "O edital não pode ser nulo");
        editais.put(edital.getId(), edital);
    }

    @Override
    public Edital obter(EditalId id) {
        notNull(id, "O id não pode ser nulo");
        return Optional.ofNullable(editais.get(id))
                .orElseThrow(() -> new IllegalArgumentException("Edital não encontrado: " + id));
    }

    @Override
    public Optional<Edital> buscarPorId(EditalId id) {
        return Optional.ofNullable(editais.get(id));
    }

    @Override
    public boolean existeEditalAbertoParaPrograma(String programa) {
        return editais.values().stream()
                .anyMatch(e -> e.getPrograma().equals(programa)
                        && e.getStatus() == StatusEdital.INSCRICOES_ABERTAS);
    }

    @Override
    public List<Edital> buscarPorPrograma(String programa) {
        var resultado = new ArrayList<Edital>();
        for (var e : editais.values()) {
            if (e.getPrograma().equals(programa)) resultado.add(e);
        }
        return resultado;
    }

    /*-- Inscricao --*/
    private int proximaInscricaoIdSeq = 1;
    private final Map<InscricaoId, Inscricao> inscricoes = new HashMap<>();

    @Override
    public InscricaoId proximoInscricaoId() { return new InscricaoId(proximaInscricaoIdSeq++); }

    @Override
    public void salvar(Inscricao inscricao) {
        notNull(inscricao, "A inscrição não pode ser nula");
        inscricoes.put(inscricao.getId(), inscricao);
    }

    @Override
    public Inscricao obter(InscricaoId id) {
        notNull(id, "O id não pode ser nulo");
        return Optional.ofNullable(inscricoes.get(id))
                .orElseThrow(() -> new IllegalArgumentException("Inscrição não encontrada: " + id));
    }

    @Override
    public Optional<Inscricao> buscarPorEstudanteEEdital(EstudantePermanenciaId estudanteId, EditalId editalId) {
        return inscricoes.values().stream()
                .filter(i -> i.getEstudanteId().equals(estudanteId) && i.getEditalId().equals(editalId))
                .findFirst();
    }

    @Override
    public List<Inscricao> buscarPorEdital(EditalId editalId) {
        var resultado = new ArrayList<Inscricao>();
        for (var i : inscricoes.values()) {
            if (i.getEditalId().equals(editalId)) resultado.add(i);
        }
        return resultado;
    }

    @Override
    public List<Inscricao> buscarDeferidosPorEdital(EditalId editalId) {
        var resultado = new ArrayList<Inscricao>();
        for (var i : inscricoes.values()) {
            if (i.getEditalId().equals(editalId) && i.getStatus() == StatusInscricao.DEFERIDA) {
                resultado.add(i);
            }
        }
        return resultado;
    }

    /*-- Beneficio --*/
    private int proximoBeneficioIdSeq = 1;
    private final Map<BeneficioId, Beneficio> beneficios = new HashMap<>();

    @Override
    public BeneficioId proximoBeneficioId() { return new BeneficioId(proximoBeneficioIdSeq++); }

    @Override
    public void salvar(Beneficio beneficio) {
        notNull(beneficio, "O benefício não pode ser nulo");
        beneficios.put(beneficio.getId(), beneficio);
    }

    @Override
    public Beneficio obter(BeneficioId id) {
        notNull(id, "O id não pode ser nulo");
        return Optional.ofNullable(beneficios.get(id))
                .orElseThrow(() -> new IllegalArgumentException("Benefício não encontrado: " + id));
    }

    @Override
    public Optional<Beneficio> buscarPorInscricao(InscricaoId inscricaoId) {
        return beneficios.values().stream()
                .filter(b -> b.getInscricaoId().equals(inscricaoId))
                .findFirst();
    }

    @Override
    public List<Beneficio> buscarPorEstudante(EstudantePermanenciaId estudanteId) {
        var resultado = new ArrayList<Beneficio>();
        for (var b : beneficios.values()) {
            if (b.getEstudanteId().equals(estudanteId)) resultado.add(b);
        }
        return resultado;
    }
}
