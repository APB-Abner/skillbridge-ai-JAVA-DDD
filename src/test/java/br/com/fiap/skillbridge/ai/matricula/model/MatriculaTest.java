package br.com.fiap.skillbridge.ai.matricula.model;

import br.com.fiap.skillbridge.ai.trilha.model.Trilha;
import br.com.fiap.skillbridge.ai.user.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MatriculaTest {

    @Test
    void builder_populaTodosOsCampos() {
        var user = User.builder().id(1L).nome("Abner").email("abner@fiap.com").cpf("12345678901").build();
        var trilha = Trilha.builder().id(5L).titulo("Java").descricao("API").build();
        var criadaEm = LocalDateTime.now().minusDays(1);

        var matricula = Matricula.builder()
                .id(10L)
                .user(user)
                .trilha(trilha)
                .criadaEm(criadaEm)
                .build();

        assertEquals(10L, matricula.getId());
        assertEquals(user, matricula.getUser());
        assertEquals(trilha, matricula.getTrilha());
        assertEquals(criadaEm, matricula.getCriadaEm());
    }

    @Test
    void settersEGetters_funcionam() {
        var matricula = new Matricula();
        var user = User.builder().id(2L).nome("Novo").email("novo@fiap.com").cpf("10987654321").build();
        var trilha = Trilha.builder().id(8L).titulo("Cloud").descricao("Ops").build();
        var agora = LocalDateTime.now();

        matricula.setId(20L);
        matricula.setUser(user);
        matricula.setTrilha(trilha);
        matricula.setCriadaEm(agora);

        assertEquals(20L, matricula.getId());
        assertEquals("Novo", matricula.getUser().getNome());
        assertEquals("Cloud", matricula.getTrilha().getTitulo());
        assertEquals(agora, matricula.getCriadaEm());
    }
}
