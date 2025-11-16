package br.com.fiap.skillbridge.ai.user.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void builder_criaComTodosOsCampos() {
        var u = User.builder()
                .id(1L)
                .nome("Abner")
                .email("abner@fiap.com")
                .cpf("12345678901")
                .build();

        assertEquals(1L, u.getId());
        assertEquals("Abner", u.getNome());
        assertEquals("abner@fiap.com", u.getEmail());
        assertEquals("12345678901", u.getCpf());
    }

    @Test
    void setters_funcionam() {
        var u = new User();
        u.setId(5L);
        u.setNome("Ana");
        u.setEmail("ana@fiap.com");
        u.setCpf("98765432100");

        assertEquals(5L, u.getId());
        assertEquals("Ana", u.getNome());
        assertEquals("ana@fiap.com", u.getEmail());
        assertEquals("98765432100", u.getCpf());
    }

    @Test
    void noArgsConstructor_criaInstanciaVazia() {
        var u = new User();
        assertNull(u.getId());
        assertNull(u.getNome());
        assertNull(u.getEmail());
        assertNull(u.getCpf());
    }

    @Test
    void allArgsConstructor_defineValores() {
        var u = new User(10L, "Bruno", "bruno@fiap.com", "11122233344");

        assertEquals(10L, u.getId());
        assertEquals("Bruno", u.getNome());
        assertEquals("bruno@fiap.com", u.getEmail());
        assertEquals("11122233344", u.getCpf());
    }
}

