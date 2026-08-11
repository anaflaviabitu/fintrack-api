package br.com.anaflavia.fintrack.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(
            ResourceNotFoundException ex
    ) {

        Map<String, Object> erro = criarResposta(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(erro);
    }



    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<Map<String, Object>> handleSaldoInsuficiente(
            SaldoInsuficienteException ex
    ) {

        Map<String, Object> erro = criarResposta(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(erro);
    }



    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<Map<String, Object>> handleEmailJaCadastrado(
            EmailJaCadastradoException ex
    ) {

        Map<String, Object> erro = criarResposta(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(erro);
    }


    @ExceptionHandler(CategoriaEmUsoException.class)
    public ResponseEntity<Map<String, Object>> handleCategoriaEmUso(
            CategoriaEmUsoException ex
    ) {

        Map<String, Object> erro = criarResposta(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(erro);
    }


    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<Map<String, Object>> handleCredenciaisInvalidas(
            CredenciaisInvalidasException ex
    ) {

        Map<String, Object> erro = criarResposta(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(erro);
    }



    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException ex
    ) {

        Map<String, Object> erro = criarResposta(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(erro);
    }



    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex
    ) {

        Map<String, String> campos = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        campos.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        Map<String, Object> erro = criarResposta(
                HttpStatus.BAD_REQUEST,
                "Erro de validação."
        );

        erro.put("campos", campos);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(erro);
    }



    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex
    ) {

        Map<String, Object> erro = criarResposta(
                HttpStatus.BAD_REQUEST,
                "Requisição inválida. Verifique os dados enviados."
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(erro);
    }



    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(
            Exception ex
    ) {

        Map<String, Object> erro = criarResposta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno no servidor."
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(erro);
    }


    private Map<String, Object> criarResposta(
            HttpStatus status,
            String mensagem
    ) {

        Map<String, Object> resposta = new HashMap<>();

        resposta.put("timestamp", LocalDateTime.now());
        resposta.put("status", status.value());
        resposta.put("erro", status.getReasonPhrase());
        resposta.put("mensagem", mensagem);

        return resposta;
    }
}