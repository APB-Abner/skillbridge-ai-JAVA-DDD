package br.com.fiap.skillbridge.ai.matricula.controller;

import br.com.fiap.skillbridge.ai.matricula.dto.MatriculaRequest;
import br.com.fiap.skillbridge.ai.matricula.dto.MatriculaResponse;
import br.com.fiap.skillbridge.ai.matricula.dto.MatriculaUpdateRequest;
import br.com.fiap.skillbridge.ai.matricula.service.MatriculaService;
import br.com.fiap.skillbridge.ai.shared.exception.NotFoundException;
import br.com.fiap.skillbridge.ai.shared.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MatriculaController.class)
@AutoConfigureMockMvc(addFilters = false) // ignora filtros do Spring Security
@Import(GlobalExceptionHandler.class) // ensure exception handler advice is loaded
class MatriculaControllerTest {

    @Autowired MockMvc mvc;
    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private MatriculaService service;
    @Autowired ObjectMapper om;

    @Test
    void create_201() throws Exception {
        var req  = new MatriculaRequest(1L, 1L);
        var resp = new MatriculaResponse(1L, 1L, "Abner", 1L, "Java & DDD", OffsetDateTime.parse("2025-01-01T00:00:00Z").toLocalDateTime());

        when(service.create(any())).thenReturn(resp);

        mvc.perform(post("/api/v1/matriculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userNome").value("Abner"));
    }

    @Test
    void create_400_camposObrigatorios() throws Exception {
        // corpo vazio viola @NotNull dos campos
        mvc.perform(post("/api/v1/matriculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listByUser_200() throws Exception {
        var resp = new MatriculaResponse(1L, 1L, "Abner", 1L, "Java & DDD", OffsetDateTime.now().toLocalDateTime());
        when(service.list(1L, null)).thenReturn(List.of(resp));
        mvc.perform(get("/api/v1/matriculas").param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trilhaTitulo").value("Java & DDD"));
    }

    @Test
    void delete_204() throws Exception {
        // se o service.delete não lança, já é 204
        mvc.perform(delete("/api/v1/matriculas/{id}", 99L))
                .andExpect(status().isNoContent());
        verify(service).delete(99L);
    }
    @Test
    void create_400_invalidRequest() throws Exception {
        var req = new MatriculaRequest(null, null); // invalid request with null fields

        mvc.perform(post("/api/v1/matriculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
    @Autowired MatriculaController controller;
    @Test void contextLoads() { assertNotNull(controller); }


    @Test
    void listByUserAndTrilha_200() throws Exception {
        var resp = new MatriculaResponse(1L, 1L, "Abner", 1L, "Java & DDD", OffsetDateTime.now().toLocalDateTime());
        when(service.list(1L, 1L)).thenReturn(List.of(resp));

        mvc.perform(get("/api/v1/matriculas")
                        .param("userId", "1")
                        .param("trilhaId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trilhaTitulo").value("Java & DDD"));
    }

    @Test
    void listByUser_404_noResults() throws Exception {
        when(service.list(99L, null)).thenReturn(List.of());

        mvc.perform(get("/api/v1/matriculas").param("userId", "99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void delete_404_notFound() throws Exception {
        Mockito.doThrow(new NotFoundException("Matrícula not found"))
                .when(service).delete(999L);

        mvc.perform(delete("/api/v1/matriculas/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_409_duplicateMatricula() throws Exception {
        var req = new MatriculaRequest(1L, 1L);

        Mockito.doThrow(new IllegalStateException("Duplicate matrícula"))
                .when(service).create(any());

        mvc.perform(post("/api/v1/matriculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    @Test
    void listByUserAndTrilha_400_invalidParams() throws Exception {
        mvc.perform(get("/api/v1/matriculas")
                        .param("userId", "invalid")
                        .param("trilhaId", "invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_400_invalidId() throws Exception {
        mvc.perform(delete("/api/v1/matriculas/{id}", "invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listAll_200_noFilters() throws Exception {
        var resp = new MatriculaResponse(1L, 1L, "Abner", 1L, "Java & DDD", OffsetDateTime.now().toLocalDateTime());
        when(service.list(null, null)).thenReturn(List.of(resp));

        mvc.perform(get("/api/v1/matriculas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trilhaTitulo").value("Java & DDD"));
    }

    @Test
    void update_200() throws Exception {
        var response = new MatriculaResponse(42L, 9L, "Mateus", 3L, "Cloud", OffsetDateTime.now().toLocalDateTime());
        when(service.update(eq(42L), any(MatriculaUpdateRequest.class))).thenReturn(response);

        mvc.perform(put("/api/v1/matriculas/{id}", 42L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userId":9,"trilhaId":3}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.trilhaTitulo").value("Cloud"));
    }

    @Test
    void update_404_notFound() throws Exception {
        Mockito.doThrow(new NotFoundException("Matr��cula n�o encontrada"))
                .when(service).update(eq(77L), any(MatriculaUpdateRequest.class));

        mvc.perform(put("/api/v1/matriculas/{id}", 77L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userId":1,"trilhaId":2}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_400_invalidBody() throws Exception {
        mvc.perform(put("/api/v1/matriculas/{id}", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
