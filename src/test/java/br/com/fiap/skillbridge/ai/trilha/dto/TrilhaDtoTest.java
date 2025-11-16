package br.com.fiap.skillbridge.ai.trilha.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrilhaDtoTest {

    @Test
    void trilhaRequest_criaComCampos() {
        var req = new TrilhaRequest("Java", "Descrição", true);
        assertEquals("Java", req.titulo());
        assertEquals("Descrição", req.descricao());
        assertTrue(req.ativa());
    }

    @Test
    void trilhaRequest_ativaNula() {
        var req = new TrilhaRequest("Python", "Desc", null);
        assertEquals("Python", req.titulo());
        assertNull(req.ativa());
    }

    @Test
    void trilhaResponse_criaComTodosCampos() {
        var resp = new TrilhaResponse(1L, "Go", "Lang", false);
        assertEquals(1L, resp.id());
        assertEquals("Go", resp.titulo());
        assertEquals("Lang", resp.descricao());
        assertFalse(resp.ativa());
    }
}

