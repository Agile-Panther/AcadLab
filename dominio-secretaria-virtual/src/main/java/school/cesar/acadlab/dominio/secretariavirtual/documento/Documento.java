package school.cesar.acadlab.dominio.secretariavirtual.documento;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;
import java.time.LocalDate;

public class Documento {
    private final String tipo;
    private final String nomeArquivo;
    private final LocalDate dataAnexo;

    public Documento(String tipo, String nomeArquivo) {
        notNull(tipo, "O tipo do documento não pode ser nulo");
        notBlank(tipo, "O tipo do documento não pode estar em branco");
        notNull(nomeArquivo, "O nome do arquivo não pode ser nulo");
        notBlank(nomeArquivo, "O nome do arquivo não pode estar em branco");
        this.tipo = tipo;
        this.nomeArquivo = nomeArquivo;
        this.dataAnexo = LocalDate.now();
    }

    public Documento(String tipo, String nomeArquivo, LocalDate dataAnexo) {
        notNull(tipo, "O tipo do documento não pode ser nulo");
        notBlank(tipo, "O tipo do documento não pode estar em branco");
        notNull(nomeArquivo, "O nome do arquivo não pode ser nulo");
        notBlank(nomeArquivo, "O nome do arquivo não pode estar em branco");
        notNull(dataAnexo, "A data de anexo não pode ser nula");
        this.tipo = tipo;
        this.nomeArquivo = nomeArquivo;
        this.dataAnexo = dataAnexo;
    }

    public String getTipo() { return tipo; }
    public String getNomeArquivo() { return nomeArquivo; }
    public LocalDate getDataAnexo() { return dataAnexo; }
}
