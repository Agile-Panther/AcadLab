package school.cesar.acadlab.dominio.atividadescomplementares.atividade;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Entao;
import org.junit.jupiter.api.Assertions;
import school.cesar.acadlab.dominio.atividadescomplementares.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SubmeterAtividadeFuncionalidade {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final AtividadesComplementaresFuncionalidade ctx;
    private AtividadeComplementar resultado;

    public SubmeterAtividadeFuncionalidade(AtividadesComplementaresFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("um estudante com vínculo ativo no período de realização da atividade")
    public void estudanteComVinculoAtivo() {
        ctx.verificadorVinculo.setVinculo(true);
    }

    @Dado("um estudante sem vínculo ativo na data de realização")
    public void estudanteSemVinculoAtivo() {
        ctx.verificadorVinculo.setVinculo(false);
    }

    @Dado("o certificado {string} ainda não foi utilizado")
    public void certificadoNaoUtilizado(String cert) {
        // por padrão o stub retorna false para qualquer cert
    }

    @Dado("o certificado {string} já foi utilizado anteriormente")
    public void certificadoJaUtilizado(String cert) {
        ctx.verificadorCertificado.marcarUtilizado(cert);
    }

    @Quando("o estudante submete a atividade da categoria {int} com {int} horas realizada em {string} com certificado {string}")
    public void submeteAtividade(int categoriaId, int horas, String data, String cert) {
        resultado = ctx.servico.submeter(new EstudanteId(1), new CategoriaAtividadeId(categoriaId),
                horas, LocalDate.parse(data, FMT), cert, "Descrição da atividade");
    }

    @Quando("o estudante tenta submeter a atividade da categoria {int} com {int} horas realizada em {string} com certificado {string}")
    public void tentaSubmeterAtividade(int categoriaId, int horas, String data, String cert) {
        try {
            ctx.servico.submeter(new EstudanteId(1), new CategoriaAtividadeId(categoriaId),
                    horas, LocalDate.parse(data, FMT), cert, "Descrição da atividade");
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("a atividade deve ser salva com status PENDENTE")
    public void atividadeDeveSerSalvaComStatusPendente() {
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(StatusAtividade.PENDENTE, resultado.getStatus());
    }
}
