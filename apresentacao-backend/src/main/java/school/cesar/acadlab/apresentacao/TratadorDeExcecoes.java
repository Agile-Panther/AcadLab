package school.cesar.acadlab.apresentacao;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class TratadorDeExcecoes {

    @ExceptionHandler(IllegalStateException.class)
    ResponseEntity<Map<String, String>> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("mensagem", ex.getMessage()));
    }
}
