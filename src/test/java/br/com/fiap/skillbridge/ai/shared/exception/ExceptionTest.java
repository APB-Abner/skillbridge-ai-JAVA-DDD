package br.com.fiap.skillbridge.ai.exception;

import br.com.fiap.skillbridge.ai.shared.exception.ApiError;
import br.com.fiap.skillbridge.ai.shared.exception.NotFoundException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExceptionTest {

    @Test
    void notFoundException_criaComMensagem() {
        var ex = new NotFoundException("Recurso não encontrado");
        assertEquals("Recurso não encontrado", ex.getMessage());
    }

    @Test
    void apiError_recordCriaComTodosCampos() {
        var now = Instant.now();
        var error = new ApiError(now, 404, "Not Found", "Msg", "/api/v1/test", List.of("field1"));

        assertEquals(now, error.timestamp());
        assertEquals(404, error.status());
        assertEquals("Not Found", error.error());
        assertEquals("Msg", error.message());
        assertEquals("/api/v1/test", error.path());
        assertEquals(1, error.fieldErrors().size());
        assertEquals("field1", error.fieldErrors().get(0));
    }

    @Test
    void apiError_recordComListaVazia() {
        var now = Instant.now();
        var error = new ApiError(now, 500, "Internal Server Error", "Erro", "/path", List.of());

        assertTrue(error.fieldErrors().isEmpty());
    }
}

