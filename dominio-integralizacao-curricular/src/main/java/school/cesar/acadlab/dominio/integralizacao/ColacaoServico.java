package school.cesar.acadlab.dominio.integralizacao;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;
import java.time.LocalDate;
import school.cesar.acadlab.dominio.integralizacao.colacao.ColacaoDeGrau;
import school.cesar.acadlab.dominio.integralizacao.colacao.ColacaoRepositorio;
import school.cesar.acadlab.dominio.integralizacao.integralizacao.IntegralizacaoId;
import school.cesar.acadlab.dominio.integralizacao.integralizacao.IntegralizacaoRepositorio;

public class ColacaoServico {
    private final ColacaoRepositorio colacaoRepositorio;
    private final IntegralizacaoRepositorio integralizacaoRepositorio;

    public ColacaoServico(ColacaoRepositorio colacaoRepositorio,
                           IntegralizacaoRepositorio integralizacaoRepositorio) {
        notNull(colacaoRepositorio, "O repositório de colações não pode ser nulo");
        notNull(integralizacaoRepositorio, "O repositório de integralizações não pode ser nulo");
        this.colacaoRepositorio = colacaoRepositorio;
        this.integralizacaoRepositorio = integralizacaoRepositorio;
    }

    public ColacaoDeGrau registrar(IntegralizacaoId integralizacaoId,
                                    LocalDate dataCerimonia, String local) {
        return registrar(integralizacaoId, dataCerimonia, null, local, null, null);
    }

    // US04 - RN7: aptidão aprovada; RN8: data da cerimônia >= data da aprovação (validado no agregado)
    public ColacaoDeGrau registrar(IntegralizacaoId integralizacaoId,
                                    LocalDate dataCerimonia, String horario,
                                    String local, String modalidade, String observacoes) {
        notNull(integralizacaoId, "O id da integralização não pode ser nulo");
        notNull(dataCerimonia, "A data da cerimônia não pode ser nula");
        notBlank(local, "O local não pode estar em branco");

        var integralizacao = integralizacaoRepositorio.obter(integralizacaoId);

        if (!integralizacao.aptidaoAprovada()) {
            throw new IllegalStateException("RN7: A colação só pode ser registrada para estudante com aptidão aprovada");
        }

        var id = colacaoRepositorio.proximoId();
        var colacao = new ColacaoDeGrau(id, integralizacao.getEstudanteId(),
                integralizacaoId, integralizacao.getDataAprovacao());
        colacao.registrar(dataCerimonia, horario, local, modalidade, observacoes);
        colacaoRepositorio.salvar(colacao);
        return colacao;
    }
}
