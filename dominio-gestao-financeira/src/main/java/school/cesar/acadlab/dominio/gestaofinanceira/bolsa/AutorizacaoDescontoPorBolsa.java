package school.cesar.acadlab.dominio.gestaofinanceira.bolsa;

import school.cesar.acadlab.dominio.gestaofinanceira.VerificadorAutorizacaoDesconto;

public class AutorizacaoDescontoPorBolsa implements VerificadorAutorizacaoDesconto {
    private final BolsaRepositorio repositorio;

    public AutorizacaoDescontoPorBolsa(BolsaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public boolean autorizacaoValida(String autorizacaoId) {
        if (autorizacaoId == null) return false;
        final int id;
        try {
            id = Integer.parseInt(autorizacaoId.trim());
        } catch (NumberFormatException e) {
            return false;
        }
        return repositorio.listar().stream()
                .anyMatch(b -> b.getId().valor() == id && b.getStatus() == StatusBolsa.ATIVA);
    }
}
