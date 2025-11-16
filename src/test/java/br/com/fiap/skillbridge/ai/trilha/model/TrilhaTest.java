package br.com.fiap.skillbridge.ai.trilha.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrilhaTest {

    @Test
    void builderDefineValoresInformados() {
        var trilha = Trilha.builder()
                .id(5L)
                .titulo("Java Avançado")
                .descricao("Streams e Coleções")
                .ativa(false)
                .build();

        assertEquals(5L, trilha.getId());
        assertEquals("Java Avançado", trilha.getTitulo());
        assertEquals("Streams e Coleções", trilha.getDescricao());
        assertFalse(trilha.isAtiva());
    }

    @Test
    void builderDefaultMantemAtivaTrue() {
        var trilha = Trilha.builder()
                .titulo("Cloud")
                .descricao("Noções de AWS")
                .build();

        assertTrue(trilha.isAtiva(), "A flag ativa deve ser true por padrão");
    }

    @Test
    void settersAtualizamCampos() {
        var trilha = new Trilha();

        trilha.setId(9L);
        trilha.setTitulo("DevOps");
        trilha.setDescricao("CI/CD");
        trilha.setAtiva(false);

        assertEquals(9L, trilha.getId());
        assertEquals("DevOps", trilha.getTitulo());
        assertEquals("CI/CD", trilha.getDescricao());
        assertFalse(trilha.isAtiva());
    }
}
