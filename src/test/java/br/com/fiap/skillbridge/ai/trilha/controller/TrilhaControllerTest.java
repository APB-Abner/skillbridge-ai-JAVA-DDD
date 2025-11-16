package br.com.fiap.skillbridge.ai.trilha.controller;

import br.com.fiap.skillbridge.ai.shared.exception.NotFoundException;
import br.com.fiap.skillbridge.ai.trilha.dto.TrilhaRequest;
import br.com.fiap.skillbridge.ai.trilha.dto.TrilhaResponse;
import br.com.fiap.skillbridge.ai.trilha.service.TrilhaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(TrilhaController.class)
class TrilhaControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @MockitoBean TrilhaService service;

    @Test
    void create_201() throws Exception {
        Mockito.when(service.create(any(TrilhaRequest.class)))
                .thenReturn(new TrilhaResponse(1L,"Java & DDD","Fundamentos",true));

        var body = om.writeValueAsString(new TrilhaRequest("Java & DDD","Fundamentos",true));

        mvc.perform(post("/api/v1/trilhas")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Java & DDD"));
    }

    @Test
    void list_200() throws Exception {
        Mockito.when(service.list())
                .thenReturn(List.of(new TrilhaResponse(2L,"ML","Básico",true)));
        mvc.perform(get("/api/v1/trilhas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2));
    }
    @Test
    void get_404() throws Exception {
        Mockito.when(service.get(99L)).thenThrow(new NotFoundException("Trilha"));
        mvc.perform(get("/api/v1/trilhas/99")).andExpect(status().isNotFound());
    }

    @Test
    void update_200() throws Exception {
        var body = om.writeValueAsString(new TrilhaRequest("New","Nova",false));
        Mockito.when(service.update(Mockito.eq(1L), any()))
                .thenReturn(new TrilhaResponse(1L,"New","Nova",false));
        mvc.perform(put("/api/v1/trilhas/1")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativa").value(false));
    }

    @Test
    void delete_204() throws Exception {
        // doNothing().when(service).delete(1L); // opcional

        mvc.perform(delete("/api/v1/trilhas/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }

}
