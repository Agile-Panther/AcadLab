package school.cesar.acadlab;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Converte exceções de domínio em respostas HTTP com mensagem legível, em vez de 500 genérico.
 * - Regras de negócio violadas (IllegalStateException) → 409 Conflict.
 * - Argumentos inválidos / não encontrados (IllegalArgumentException) → 400 Bad Request.
 * O corpo inclui o campo "message", consumido pelo frontend para exibir o motivo ao usuário.
 */
@RestControllerAdvice
class TratadorExcecoes {

    @ExceptionHandler(IllegalStateException.class)
    ResponseEntity<Map<String, Object>> regraDeNegocio(IllegalStateException e) {
        return corpo(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<Map<String, Object>> argumentoInvalido(IllegalArgumentException e) {
        return corpo(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    private ResponseEntity<Map<String, Object>> corpo(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(Map.of(
                "status", status.value(),
                "message", mensagem == null ? "Operação não permitida." : mensagem));
    }
}
