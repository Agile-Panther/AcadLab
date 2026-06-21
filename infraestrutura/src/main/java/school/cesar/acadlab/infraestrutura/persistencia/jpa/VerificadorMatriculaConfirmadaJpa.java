package school.cesar.acadlab.infraestrutura.persistencia.jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import school.cesar.acadlab.dominio.gestaofinanceira.EstudanteId;
import school.cesar.acadlab.dominio.gestaofinanceira.PeriodoLetivoId;
import school.cesar.acadlab.dominio.gestaofinanceira.VerificadorMatriculaConfirmada;
import school.cesar.acadlab.dominio.matricula.matricula.StatusMatricula;

@Component
class VerificadorMatriculaConfirmadaJpa implements VerificadorMatriculaConfirmada {
    @Autowired
    MatriculaJpaRepository repositorio;

    @Override
    public boolean possuiMatricula(EstudanteId estudanteId, PeriodoLetivoId periodoLetivoId) {
        return repositorio.existsByEstudanteIdAndPeriodoLetivoIdAndStatus(
                estudanteId.valor(), periodoLetivoId.valor(), StatusMatricula.CONFIRMADA);
    }
}
