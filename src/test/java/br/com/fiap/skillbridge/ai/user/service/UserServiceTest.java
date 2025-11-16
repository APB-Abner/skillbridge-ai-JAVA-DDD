package br.com.fiap.skillbridge.ai.user.service;

import br.com.fiap.skillbridge.ai.shared.exception.NotFoundException;
import br.com.fiap.skillbridge.ai.user.dto.UserRequest;
import br.com.fiap.skillbridge.ai.user.dto.UserResponse;
import br.com.fiap.skillbridge.ai.user.dto.UserUpdateRequest;
import br.com.fiap.skillbridge.ai.user.model.User;
import br.com.fiap.skillbridge.ai.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository repo;

    @InjectMocks
    UserService service;

    private User fakeUser(Long id) {
        var u = new User();
        u.setId(id);
        u.setNome("Abner");
        u.setEmail("abner@fiap.com");
        u.setCpf("12345678901");
        return u;
    }

    // ============ CREATE ===================================

    @Test
    void create_ok_mapeiaEntityPraResponse() {
        var req = new UserRequest("Abner", "abner@fiap.com", "12345678901");

        when(repo.existsByEmail(req.email())).thenReturn(false);
        when(repo.existsByCpf(req.cpf())).thenReturn(false);
        when(repo.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        UserResponse resp = service.create(req);

        assertEquals(1L, resp.id());
        assertEquals("Abner", resp.nome());
        assertEquals("abner@fiap.com", resp.email());
        assertEquals("12345678901", resp.cpf());
        verify(repo).save(any(User.class));
    }

    @Test
    void create_emailDuplicado_lanca409() {
        var req = new UserRequest("Abner", "abner@fiap.com", "12345678901");

        when(repo.existsByEmail(req.email())).thenReturn(true);

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> service.create(req)
        );

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(repo, never()).save(any());
    }

    @Test
    void create_cpfDuplicado_lanca409() {
        var req = new UserRequest("Abner", "abner@fiap.com", "12345678901");

        when(repo.existsByEmail(req.email())).thenReturn(false);
        when(repo.existsByCpf(req.cpf())).thenReturn(true);

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> service.create(req)
        );

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(repo, never()).save(any());
    }

    // ============ LIST =====================================

    @Test
    void list_retornaListaMapeada() {
        when(repo.findAll()).thenReturn(List.of(
                fakeUser(1L),
                fakeUser(2L)
        ));

        var result = service.list();

        assertEquals(2, result.size());
        assertEquals(1L, result.getFirst().id());
        assertEquals(2L, result.get(1).id());
        verify(repo).findAll();
    }

    // ============ GET ======================================

    @Test
    void get_existente_retornaUserResponse() {
        when(repo.findById(1L)).thenReturn(Optional.of(fakeUser(1L)));

        var resp = service.get(1L);

        assertEquals(1L, resp.id());
        assertEquals("Abner", resp.nome());
        verify(repo).findById(1L);
    }

    @Test
    void get_inexistente_lancaNotFound() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.get(99L));
    }

    // ============ UPDATE ===================================

    @Test
    void update_ok_atualizaEDevolveResponse() {
        var req = new UserUpdateRequest("Novo Nome", "novo@fiap.com", "98765432100");

        when(repo.findById(1L)).thenReturn(Optional.of(fakeUser(1L)));
        when(repo.existsByEmailAndIdNot(req.email(), 1L)).thenReturn(false);
        when(repo.existsByCpfAndIdNot(req.cpf(), 1L)).thenReturn(false);
        when(repo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        var resp = service.update(1L, req);

        assertEquals(1L, resp.id());
        assertEquals("Novo Nome", resp.nome());
        assertEquals("novo@fiap.com", resp.email());
        assertEquals("98765432100", resp.cpf());
        verify(repo).save(any(User.class));
    }

    @Test
    void update_emailDuplicadoPorOutro_lanca409() {
        var req = new UserUpdateRequest("Novo Nome", "dup@fiap.com", "98765432100");

        when(repo.findById(1L)).thenReturn(Optional.of(fakeUser(1L)));
        when(repo.existsByEmailAndIdNot(req.email(), 1L)).thenReturn(true);

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> service.update(1L, req)
        );

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(repo, never()).save(any());
    }

    @Test
    void update_cpfDuplicadoPorOutro_lanca409() {
        var req = new UserUpdateRequest("Novo Nome", "novo@fiap.com", "98765432100");

        when(repo.findById(1L)).thenReturn(Optional.of(fakeUser(1L)));
        when(repo.existsByEmailAndIdNot(req.email(), 1L)).thenReturn(false);
        when(repo.existsByCpfAndIdNot(req.cpf(), 1L)).thenReturn(true);

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> service.update(1L, req)
        );

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(repo, never()).save(any());
    }

    @Test
    void update_inexistente_lancaNotFound() {
        var req = new UserUpdateRequest("Nome", "email@fiap.com", "12345678901");

        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.update(99L, req));
    }

    // ============ DELETE ===================================

    @Test
    void delete_existente_chamaDeleteById() {
        when(repo.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repo).deleteById(1L);
    }

    @Test
    void delete_inexistente_lancaNotFound() {
        when(repo.existsById(99L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> service.delete(99L));

        verify(repo, never()).deleteById(anyLong());
    }
}
