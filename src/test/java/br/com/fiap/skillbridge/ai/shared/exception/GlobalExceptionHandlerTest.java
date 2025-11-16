package br.com.fiap.skillbridge.ai.exception;

import br.com.fiap.skillbridge.ai.shared.exception.ApiError;
import br.com.fiap.skillbridge.ai.shared.exception.GlobalExceptionHandler;
import br.com.fiap.skillbridge.ai.shared.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    GlobalExceptionHandler handler = new GlobalExceptionHandler();

    private HttpServletRequest mockRequest(String uri) {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn(uri);
        return req;
    }

    @Test
    void handleValidation_retorna400_e_listaDeCampos() {
        // given
        BindingResult bindingResult = mock(BindingResult.class);
        var fieldError = new FieldError("user", "email", "must not be blank");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        var ex = new MethodArgumentNotValidException(mock(org.springframework.core.MethodParameter.class), bindingResult);
        var req = mockRequest("/api/v1/usuarios");

        // when
        ResponseEntity<ApiError> resp = handler.handleValidation(ex, req);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertNotNull(resp.getBody());
        ApiError body = resp.getBody();
        assertEquals(400, body.status());
        assertEquals("Validation failed", body.error());
        assertEquals("/api/v1/usuarios", body.path());
        assertEquals(1, body.fieldErrors().size());
        assertTrue(body.fieldErrors().getFirst().contains("email"));
    }

    @Test
    void handleNotFound_retorna404() {
        // given
        var ex = new NotFoundException("Usuário não encontrado.");
        var req = mockRequest("/api/v1/usuarios/99");

        // when
        ResponseEntity<ApiError> resp = handler.handleNotFound(ex, req);

        // then
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
        ApiError body = resp.getBody();
        assertNotNull(body);
        assertEquals(404, body.status());
        assertEquals("Not Found", body.error());
        assertEquals("Usuário não encontrado.", body.message());
        assertEquals("/api/v1/usuarios/99", body.path());
    }

    @Test
    void handleBadRequest_IllegalArgument_retorna400() {
        // given
        var ex = new IllegalArgumentException("E-mail já cadastrado.");
        var req = mockRequest("/api/v1/usuarios");

        // when
        ResponseEntity<ApiError> resp = handler.handleBadRequest(ex, req);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        ApiError body = resp.getBody();
        assertNotNull(body);
        assertEquals(400, body.status());
        assertEquals("Bad Request", body.error());
        assertEquals("E-mail já cadastrado.", body.message());
    }

    @Test
    void handleBadRequest_DataIntegrity_retorna400() {
        // given
        var ex = new DataIntegrityViolationException("Violação de integridade");
        var req = mockRequest("/api/v1/usuarios");

        // when
        ResponseEntity<ApiError> resp = handler.handleBadRequest(ex, req);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        ApiError body = resp.getBody();
        assertNotNull(body);
        assertEquals(400, body.status());
        assertEquals("Bad Request", body.error());
        assertEquals("Violação de integridade", body.message());
    }

    @Test
    void handleGeneric_retorna500() {
        // given
        var ex = new Exception("Erro inesperado");
        var req = mockRequest("/alguma-coisa");

        // when
        ResponseEntity<ApiError> resp = handler.handleGeneric(ex, req);

        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        ApiError body = resp.getBody();
        assertNotNull(body);
        assertEquals(500, body.status());
        assertEquals("Internal Server Error", body.error());
        assertEquals("Erro inesperado", body.message());
        assertEquals("/alguma-coisa", body.path());
        assertNotNull(body.timestamp());
    }

    @Test
    void handleResponseStatus_respeitaStatusDaExcecao() {
        // given
        var ex  = new ResponseStatusException(HttpStatus.CONFLICT, "CONFLICT");
        var req = mockRequest("/api/v1/usuarios");

        // when
        ResponseEntity<ApiError> resp = handler.handleResponseStatus(ex, req);

        // then
        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());

        ApiError body = resp.getBody();
        assertNotNull(body);
        // se ApiError for record, usa body.status() / body.error() / body.message()
        // se for classe, troca pra getStatus(), getError(), getMessage()
        assertEquals(409, body.status());
        assertEquals("Conflict", body.error());
        assertEquals("CONFLICT", body.message());
        assertEquals("/api/v1/usuarios", body.path());
    }


    @Test
    void handleResponseStatus_quandoStatusNaoEhHttpStatus_usaToStringSemEstourar() {
        // given: um status que NÃO é HttpStatus enum
        HttpStatusCode status = HttpStatusCode.valueOf(499); // 499 não existe no HttpStatus
        var ex = new ResponseStatusException(status, "X");
        var req = mockRequest("/api/v1/teste");

        // when
        ResponseEntity<ApiError> resp = handler.handleResponseStatus(ex, req);

        // then: só garante que passou pelo handler com esse status
        ApiError body = resp.getBody();
        assertNotNull(body);
        assertEquals(499, body.status());
        assertEquals("/api/v1/teste", body.path());
    }
}
