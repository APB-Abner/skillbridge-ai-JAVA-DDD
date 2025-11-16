package br.com.fiap.skillbridge.ai.user.controller;

import br.com.fiap.skillbridge.ai.user.dto.UserRequest;
import br.com.fiap.skillbridge.ai.user.dto.UserResponse;
import br.com.fiap.skillbridge.ai.user.dto.UserUpdateRequest;
import br.com.fiap.skillbridge.ai.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @MockitoBean UserService service;

    @Test
    void create_201() throws Exception {
        var req  = new UserRequest("Abner","abner@fiap.com","12345678901");
        var resp = new UserResponse(1L,"Abner","abner@fiap.com","12345678901");

        when(service.create(any())).thenReturn(resp);

        mvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Abner"));
    }

    @Test
    void create_400_bodyVazio() throws Exception {
        mvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_409_duplicado() throws Exception {
        var req = new UserRequest("Abner","abner@fiap.com","12345678901");

        doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "CONFLICT"))
                .when(service).create(any());

        mvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    @Test
    void list_200() throws Exception {
        var u1 = new UserResponse(1L, "Abner", "abner@fiap.com", "12345678901");
        var u2 = new UserResponse(2L, "Bruno", "bruno@fiap.com", "98765432100");

        when(service.list()).thenReturn(List.of(u1, u2));

        mvc.perform(get("/api/v1/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Abner"))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void get_200() throws Exception {
        var resp = new UserResponse(1L, "Abner", "abner@fiap.com", "12345678901");

        when(service.get(1L)).thenReturn(resp);

        mvc.perform(get("/api/v1/usuarios/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Abner"))
                .andExpect(jsonPath("$.email").value("abner@fiap.com"));
    }

    @Test
    void update_200() throws Exception {
        var req  = new UserUpdateRequest("Novo Nome", "novo@fiap.com", "12345678901");
        var resp = new UserResponse(1L, "Novo Nome", "novo@fiap.com", "12345678901");

        when(service.update(eq(1L), any(UserUpdateRequest.class))).thenReturn(resp);

        mvc.perform(put("/api/v1/usuarios/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Novo Nome"))
                .andExpect(jsonPath("$.email").value("novo@fiap.com"));
    }

    @Test
    void delete_204() throws Exception {
        // doNothing().when(service).delete(1L); // opcional, Mockito já ignora void por padrão

        mvc.perform(delete("/api/v1/usuarios/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }


}
