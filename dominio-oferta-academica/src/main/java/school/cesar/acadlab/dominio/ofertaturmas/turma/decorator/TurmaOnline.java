package school.cesar.acadlab.dominio.ofertaturmas.turma.decorator;

import static org.apache.commons.lang3.Validate.notBlank;
import school.cesar.acadlab.dominio.ofertaturmas.turma.ModalidadeTurma;

public class TurmaOnline extends TurmaDecorador {
    private String linkAcesso;

    public TurmaOnline(TurmaOferecida turma) {
        super(turma);
        if (turma.getModalidade() != ModalidadeTurma.EAD)
            throw new IllegalArgumentException("TurmaOnline requer modalidade EAD");
    }

    public void definirLinkAcesso(String link) {
        notBlank(link, "O link de acesso não pode estar em branco");
        this.linkAcesso = link;
    }

    public String getLinkAcesso() { return linkAcesso; }
}
