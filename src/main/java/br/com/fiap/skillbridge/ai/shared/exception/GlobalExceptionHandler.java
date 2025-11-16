package br.com.fiap.skillbridge.ai.shared.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req){
        var fields = ex.getBindingResult().getFieldErrors()
                .stream().map(e -> e.getField() + ": " + e.getDefaultMessage()).toList();
        return ResponseEntity.badRequest().body(new ApiError(
                Instant.now(), 400, "Validation failed", "Dados inválidos", req.getRequestURI(), fields
        ));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex, HttpServletRequest req){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(
                Instant.now(), 404, "Not Found", ex.getMessage(), req.getRequestURI(), List.of()
        ));
    }

    @ExceptionHandler({IllegalArgumentException.class, DataIntegrityViolationException.class})
    public ResponseEntity<ApiError> handleBadRequest(RuntimeException ex, HttpServletRequest req){
        return ResponseEntity.badRequest().body(new ApiError(
                Instant.now(), 400, "Bad Request", ex.getMessage(), req.getRequestURI(), List.of()
        ));
    }

    // Respeita o status da ResponseStatusException (409, 403, etc.) e usa o reason phrase quando possível
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatus(ResponseStatusException ex, HttpServletRequest req){
        HttpStatusCode status = ex.getStatusCode();
        String error = (status instanceof HttpStatus http) ? http.getReasonPhrase() : status.toString();

        return ResponseEntity.status(status).body(new ApiError(
                Instant.now(),
                status.value(),
                error,
                ex.getReason(),
                req.getRequestURI(),
                List.of()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiError(
                Instant.now(), 500, "Internal Server Error", ex.getMessage(), req.getRequestURI(), List.of()
        ));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                       HttpServletRequest req) {
        String msg = String.format("Parâmetro '%s' inválido", ex.getName());

        return ResponseEntity.badRequest().body(new ApiError(
                Instant.now(), 400, "Bad Request", msg,
                req.getRequestURI(), List.of()
        ));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleConflict(IllegalStateException ex, HttpServletRequest req){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError(
                Instant.now(), 409, "Conflict", ex.getMessage(),
                req.getRequestURI(), List.of()
        ));
    }
}
