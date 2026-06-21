package school.cesar.acadlab.infraestrutura.persistencia.jpa;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import school.cesar.acadlab.aplicacao.gestaofinanceira.InadimplentesRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.gestaofinanceira.InadimplentesResumo;
import school.cesar.acadlab.dominio.gestaofinanceira.StatusCobranca;

@Repository
class InadimplentesRepositorioImpl implements InadimplentesRepositorioAplicacao {

    @Autowired
    CobrancaJpaRepository cobrancaRepo;

    @Autowired
    MatriculaJpaRepository matriculaRepo;

    @Override
    public List<InadimplentesResumo> buscarInadimplentes(LocalDate hoje) {
        var atrasadas = cobrancaRepo.findByStatusAndVencimentoBefore(StatusCobranca.ABERTA, hoje);

        return atrasadas.stream()
                .collect(Collectors.groupingBy(c -> c.estudanteId))
                .entrySet().stream()
                .map(entry -> {
                    int estudanteId = entry.getKey();
                    var cobranças = entry.getValue();

                    BigDecimal total = cobranças.stream()
                            .map(c -> c.valorAtual)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    LocalDate vencMaisAntigo = cobranças.stream()
                            .map(c -> c.vencimento)
                            .min(Comparator.naturalOrder())
                            .orElse(hoje);

                    int dias = (int) ChronoUnit.DAYS.between(vencMaisAntigo, hoje);

                    var matriculas = matriculaRepo.findByEstudanteId(estudanteId);
                    var ultima = matriculas.stream()
                            .max(Comparator.comparingInt(m -> m.id))
                            .orElse(null);

                    return new InadimplentesResumo(
                            ultima != null ? ultima.id : 0,
                            estudanteId,
                            total,
                            dias,
                            ultima != null ? ultima.status.name() : "SEM_MATRICULA");
                })
                .toList();
    }
}
