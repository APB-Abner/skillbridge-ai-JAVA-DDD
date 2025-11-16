package br.com.fiap.skillbridge.ai.matricula.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class MatriculaDtosTest {
    @Test void request_getters(){
        var r = new MatriculaRequest(1L,6L);
        assertEquals(6L, r.trilhaId());
    }
    @Test void response_toString(){
        var m = new MatriculaResponse(1L,1L,"U",6L,"T", LocalDateTime.now());
        assertNotNull(m.toString());
    }
    @Test void updateRequest_getters(){
        var update = new MatriculaUpdateRequest(3L, 8L);
        assertEquals(3L, update.userId());
        assertEquals(8L, update.trilhaId());
    }
}
