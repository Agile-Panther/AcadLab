package school.cesar.acadlab.dominio.gestaofinanceira.bolsa;

import static org.apache.commons.lang3.Validate.notNull;

import java.math.BigDecimal;
import java.time.LocalDate;

import school.cesar.acadlab.dominio.evento.EventoBarramento;
import school.cesar.acadlab.dominio.gestaofinanceira.EstudanteId;

public class BolsaServico {
    private final BolsaRepositorio repositorio;
    private final EventoBarramento barramento;

    public BolsaServico(BolsaRepositorio repositorio, EventoBarramento barramento) {
        notNull(repositorio, "repositório obrigatório");
        notNull(barramento, "barramento obrigatório");
        this.repositorio = repositorio;
        this.barramento = barramento;
    }

    public Bolsa conceder(EstudanteId estudanteId, TipoBolsa tipo, BigDecimal percentual, LocalDate validade) {
        var bolsa = Bolsa.conceder(repositorio.proximoId(), estudanteId, tipo, percentual, validade);
        repositorio.salvar(bolsa);
        barramento.postar(bolsa.eventoConcessao());
        return bolsa;
    }

    public void suspender(BolsaId id) {
        var bolsa = repositorio.obter(id);
        var evento = bolsa.suspender();
        repositorio.salvar(bolsa);
        barramento.postar(evento);
    }

    public void reativar(BolsaId id) {
        var bolsa = repositorio.obter(id);
        var evento = bolsa.reativar();
        repositorio.salvar(bolsa);
        barramento.postar(evento);
    }

    public void solicitarRenovacao(BolsaId id) {
        var bolsa = repositorio.obter(id);
        var evento = bolsa.solicitarRenovacao();
        repositorio.salvar(bolsa);
        barramento.postar(evento);
    }

    public void renovar(BolsaId id, LocalDate novaValidade) {
        var bolsa = repositorio.obter(id);
        var evento = bolsa.renovar(novaValidade);
        repositorio.salvar(bolsa);
        barramento.postar(evento);
    }
}
