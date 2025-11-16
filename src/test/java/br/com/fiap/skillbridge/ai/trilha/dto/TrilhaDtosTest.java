package br.com.fiap.skillbridge.ai.trilha.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TrilhaDtosTest {
    @Test void request_getters(){
        var r = new TrilhaRequest("Java & DDD","Fund", true);
        assertTrue(r.ativa());
    }
    @Test void response_equals(){
        var a = new TrilhaResponse(1L,"T","D",true);
        var b = new TrilhaResponse(1L,"T","D",true);
        assertEquals(a,b);
    }
}
