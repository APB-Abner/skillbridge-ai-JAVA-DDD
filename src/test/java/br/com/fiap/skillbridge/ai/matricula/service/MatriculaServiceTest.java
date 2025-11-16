package br.com.fiap.skillbridge.ai.matricula.service;

import br.com.fiap.skillbridge.ai.matricula.dto.MatriculaRequest;
import br.com.fiap.skillbridge.ai.matricula.dto.MatriculaResponse;
import br.com.fiap.skillbridge.ai.matricula.dto.MatriculaUpdateRequest;
import br.com.fiap.skillbridge.ai.matricula.model.Matricula;
import br.com.fiap.skillbridge.ai.matricula.repository.MatriculaRepository;
import br.com.fiap.skillbridge.ai.shared.exception.NotFoundException;
import br.com.fiap.skillbridge.ai.trilha.model.Trilha;
import br.com.fiap.skillbridge.ai.trilha.repository.TrilhaRepository;
import br.com.fiap.skillbridge.ai.user.model.User;
import br.com.fiap.skillbridge.ai.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatriculaServiceTest {

    @Mock MatriculaRepository repo;
    @Mock UserRepository userRepo;
    @Mock TrilhaRepository trilhaRepo;
    @InjectMocks MatriculaService service;

    @Test
    void create_ok_salvaEMapeiaCorretamente() {
        var user   = User.builder()
                .id(1L).nome("Abner").email("abner@fiap.com").cpf("12345678901")
                .build();
        var trilha = Trilha.builder()
                .id(2L).titulo("SkillBridge Java")
                .build();

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(trilhaRepo.findById(2L)).thenReturn(Optional.of(trilha));
        when(repo.existsByUser_IdAndTrilha_Id(1L, 2L)).thenReturn(false);

        when(repo.save(any(Matricula.class))).thenAnswer(inv -> {
            Matricula m = inv.getArgument(0);
            m.setId(10L);
            // se o service não seta criadaEm, pode tirar isso
            if (m.getCriadaEm() == null) {
                m.setCriadaEm(LocalDateTime.now());
            }
            return m;
        });

        MatriculaResponse resp = service.create(new MatriculaRequest(1L, 2L));

        assertNotNull(resp);
        assertEquals(10L, resp.id());
        assertEquals(1L, resp.userId());
        assertEquals("Abner", resp.userNome());
        assertEquals(2L, resp.trilhaId());
        assertEquals("SkillBridge Java", resp.trilhaTitulo());
        assertNotNull(resp.criadaEm());

        verify(repo).save(any(Matricula.class));
    }

    @Test
    void create_ok(){
        var req = new MatriculaRequest(1L, 2L);
        when(userRepo.findById(1L)).thenReturn(Optional.of(User.builder().id(1L).nome("Abner").email("a@a.com").cpf("12345678901").build()));
        when(trilhaRepo.findById(2L)).thenReturn(Optional.of(Trilha.builder().id(2L).titulo("Java").build()));
        when(repo.existsByUser_IdAndTrilha_Id(1L,2L)).thenReturn(false);
        when(repo.save(any())).thenAnswer(a -> { Matricula m=a.getArgument(0); m.setId(10L); return m; });

        var res = service.create(req);
        assertEquals(10L, res.id());
        assertEquals(1L, res.userId());
        assertEquals(2L, res.trilhaId());
    }

    @Test
    void create_userNotFound(){
        var req = new MatriculaRequest(99L, 1L);
        when(userRepo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.create(req));
    }

    @Test
    void delete_notFound(){
        when(repo.existsById(77L)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> service.delete(77L));
    }

    @Test
    void create_trilhaNotFound(){
        when(userRepo.findById(1L)).thenReturn(Optional.of(User.builder().id(1L).nome("A").email("a@a.com").cpf("12345678901").build()));
        when(trilhaRepo.findById(9L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.create(new MatriculaRequest(1L,9L)));
    }

    @Test
    void create_duplicado_badRequest(){
        when(userRepo.findById(1L)).thenReturn(Optional.of(User.builder().id(1L).nome("A").email("a@a.com").cpf("12345678901").build()));
        when(trilhaRepo.findById(2L)).thenReturn(Optional.of(Trilha.builder().id(2L).titulo("T").build()));
        when(repo.existsByUser_IdAndTrilha_Id(1L,2L)).thenReturn(true);
        var req = new MatriculaRequest(1L, 2L);
        assertThrows(IllegalArgumentException.class, () -> service.create(req));
    }

    @Test
    void delete_ok(){
        when(repo.existsById(5L)).thenReturn(true);
        service.delete(5L);
        verify(repo).deleteById(5L);
    }

    @Test
    void delete_notFound_lancaNotFoundException() {
        when(repo.existsById(99L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> service.delete(99L));

        verify(repo, never()).deleteById(anyLong());
    }

    @Test
    void list_quandoUserETrilhaPreenchidos_buscaPorUserETrilha() {
        var user = User.builder().id(1L).nome("User").build();
        var trilha = Trilha.builder().id(2L).titulo("Trilha").build();
        var m = new Matricula();
        m.setId(10L);
        m.setUser(user);
        m.setTrilha(trilha);

        when(repo.findByUser_IdAndTrilha_Id(1L, 2L)).thenReturn(List.of(m));

        var result = service.list(1L, 2L);

        assertEquals(1, result.size());
        verify(repo).findByUser_IdAndTrilha_Id(1L, 2L);
    }

    @Test
    void list_quandoSoUser_buscaPorUser() {
        var user = User.builder().id(1L).nome("User").build();
        var trilha = Trilha.builder().id(2L).titulo("Trilha").build();
        var m = new Matricula();
        m.setId(11L);
        m.setUser(user);
        m.setTrilha(trilha);

        when(repo.findByUser_Id(1L)).thenReturn(List.of(m));

        var result = service.list(1L, null);

        assertEquals(1, result.size());
        verify(repo).findByUser_Id(1L);
    }

    @Test
    void list_quandoSoTrilha_buscaPorTrilha() {
        var user = User.builder().id(1L).nome("User").build();
        var trilha = Trilha.builder().id(2L).titulo("Trilha").build();
        var m = new Matricula();
        m.setId(12L);
        m.setUser(user);
        m.setTrilha(trilha);

        when(repo.findByTrilha_Id(2L)).thenReturn(List.of(m));

        var result = service.list(null, 2L);

        assertEquals(1, result.size());
        verify(repo).findByTrilha_Id(2L);
    }

    @Test
    void list_semFiltros_buscaTodos() {
        var user = User.builder().id(1L).nome("User").build();
        var trilha = Trilha.builder().id(2L).titulo("Trilha").build();
        var m = new Matricula();
        m.setId(13L);
        m.setUser(user);
        m.setTrilha(trilha);

        when(repo.findAll()).thenReturn(List.of(m));

        var result = service.list(null, null);

        assertEquals(1, result.size());
        verify(repo).findAll();
    }

    @Test
    void update_quandoAlteraUsuarioETrilha_atualizaComSucesso() {
        var userAtual = User.builder().id(1L).nome("Atual").build();
        var trilhaAtual = Trilha.builder().id(2L).titulo("Trilha A").build();
        var matriculaExistente = Matricula.builder()
                .id(55L)
                .user(userAtual)
                .trilha(trilhaAtual)
                .criadaEm(LocalDateTime.now())
                .build();

        when(repo.findById(55L)).thenReturn(Optional.of(matriculaExistente));

        var novoUser = User.builder().id(3L).nome("Novo User").build();
        var novaTrilha = Trilha.builder().id(4L).titulo("Nova Trilha").build();

        when(userRepo.findById(3L)).thenReturn(Optional.of(novoUser));
        when(trilhaRepo.findById(4L)).thenReturn(Optional.of(novaTrilha));
        when(repo.existsByUser_IdAndTrilha_Id(3L, 4L)).thenReturn(false);
        when(repo.save(any(Matricula.class))).thenAnswer(inv -> inv.<Matricula>getArgument(0));

        var response = service.update(55L, new MatriculaUpdateRequest(3L, 4L));

        assertEquals(3L, response.userId());
        assertEquals("Novo User", response.userNome());
        assertEquals(4L, response.trilhaId());
        assertEquals("Nova Trilha", response.trilhaTitulo());
        verify(repo).save(matriculaExistente);
    }

    @Test
    void update_quandoUsuarioNaoEncontrado_lancaNotFound() {
        var userAtual = User.builder().id(1L).nome("Atual").build();
        var trilhaAtual = Trilha.builder().id(2L).titulo("Trilha A").build();
        var matriculaExistente = Matricula.builder().id(88L).user(userAtual).trilha(trilhaAtual).build();

        when(repo.findById(88L)).thenReturn(Optional.of(matriculaExistente));
        when(userRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.update(88L, new MatriculaUpdateRequest(99L, 2L)));
    }

    @Test
    void update_quandoTrilhaNaoEncontrada_lancaNotFound() {
        var userAtual = User.builder().id(1L).nome("Atual").build();
        var trilhaAtual = Trilha.builder().id(2L).titulo("Trilha A").build();
        var matriculaExistente = Matricula.builder().id(89L).user(userAtual).trilha(trilhaAtual).build();

        when(repo.findById(89L)).thenReturn(Optional.of(matriculaExistente));
        when(trilhaRepo.findById(77L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.update(89L, new MatriculaUpdateRequest(1L, 77L)));
    }

    @Test
    void update_quandoCombinacaoDuplicada_lancaIllegalArgument() {
        var userAtual = User.builder().id(1L).nome("Atual").build();
        var trilhaAtual = Trilha.builder().id(2L).titulo("Trilha A").build();
        var matriculaExistente = Matricula.builder().id(90L).user(userAtual).trilha(trilhaAtual).build();

        when(repo.findById(90L)).thenReturn(Optional.of(matriculaExistente));

        var novoUser = User.builder().id(3L).nome("Novo User").build();
        var novaTrilha = Trilha.builder().id(4L).titulo("Nova Trilha").build();

        when(userRepo.findById(3L)).thenReturn(Optional.of(novoUser));
        when(trilhaRepo.findById(4L)).thenReturn(Optional.of(novaTrilha));
        when(repo.existsByUser_IdAndTrilha_Id(3L, 4L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> service.update(90L, new MatriculaUpdateRequest(3L, 4L)));
    }

    @Test
    void update_quandoSemMudancas_naoConsultaReposDeUsuarioOuTrilha() {
        var userAtual = User.builder().id(1L).nome("Atual").build();
        var trilhaAtual = Trilha.builder().id(2L).titulo("Trilha A").build();
        var matriculaExistente = Matricula.builder().id(91L).user(userAtual).trilha(trilhaAtual).build();

        when(repo.findById(91L)).thenReturn(Optional.of(matriculaExistente));
        when(repo.save(matriculaExistente)).thenReturn(matriculaExistente);

        var response = service.update(91L, new MatriculaUpdateRequest(null, null));

        assertEquals(1L, response.userId());
        assertEquals(2L, response.trilhaId());
        verify(userRepo, never()).findById(anyLong());
        verify(trilhaRepo, never()).findById(anyLong());
        verify(repo, never()).existsByUser_IdAndTrilha_Id(anyLong(), anyLong());
    }
    @Test
    void update_200_semMudancas_naoRevalidaDuplicidade() {
        // dado: matrícula atual user=1, trilha=1
        var u = new User();   u.setId(1L);
        var t = new Trilha(); t.setId(1L);

        var m = new Matricula();
        m.setId(10L);
        m.setUser(u);
        m.setTrilha(t);
        m.setCriadaEm(LocalDateTime.now());

        when(repo.findById(10L)).thenReturn(Optional.of(m));
        when(repo.save(any(Matricula.class))).thenAnswer(i -> i.getArgument(0));

        // se você usa MatriculaUpdateRequest, troque aqui
        var req = new MatriculaUpdateRequest(1L, 1L); // nada mudou

        var out = service.update(10L, req);

        // como nada mudou, o bloco do IF não deve rodar
        verify(repo, never()).existsByUser_IdAndTrilha_Id(anyLong(), anyLong());

        assertEquals(10L, out.id());
        assertEquals(1L, out.userId());
        assertEquals(1L, out.trilhaId());
    }

    @Test
    void update_mudaSomenteTrilha_ok() {
        var user = new User(); user.setId(1L);
        var trilha1 = new Trilha(); trilha1.setId(10L);
        var trilha2 = new Trilha(); trilha2.setId(20L);

        var m = new Matricula();
        m.setId(100L); m.setUser(user); m.setTrilha(trilha1);
        m.setCriadaEm(java.time.LocalDateTime.now());

        when(repo.findById(100L)).thenReturn(java.util.Optional.of(m));
        when(trilhaRepo.findById(20L)).thenReturn(java.util.Optional.of(trilha2));
        when(repo.existsByUser_IdAndTrilha_Id(1L, 20L)).thenReturn(false);
        // devolve o próprio objeto salvo, já mutado pelo service
        when(repo.save(any(Matricula.class))).thenAnswer((Answer<Matricula>) inv -> inv.getArgument(0));

        var resp = service.update(100L, new MatriculaUpdateRequest(null, 20L));

        assertEquals(20L, resp.trilhaId());
        verify(repo).existsByUser_IdAndTrilha_Id(1L, 20L);
        verify(repo).save(any(Matricula.class));
    }

    @Test
    void update_semMudanca_naoChecaDuplicidade() {
        var user = new User(); user.setId(1L);
        var trilha = new Trilha(); trilha.setId(10L);

        var m = new Matricula();
        m.setId(100L); m.setUser(user); m.setTrilha(trilha);
        m.setCriadaEm(java.time.LocalDateTime.now());

        when(repo.findById(100L)).thenReturn(java.util.Optional.of(m));
        when(repo.save(any(Matricula.class))).thenAnswer(inv -> inv.getArgument(0));

        var resp = service.update(100L, new MatriculaUpdateRequest(null, null));

        assertEquals(10L, resp.trilhaId());
        // ramo FALSE do (userChanged || trilhaChanged): não deve consultar duplicidade
        verify(repo, never()).existsByUser_IdAndTrilha_Id(anyLong(), anyLong());
        verify(repo).save(any(Matricula.class));
    }

    @Test
    void update_mudaTrilha_duplicada_lancaExcecao() {
        var user = new User(); user.setId(1L);
        var trilha1 = new Trilha(); trilha1.setId(10L);
        var trilha2 = new Trilha(); trilha2.setId(20L);

        var m = new Matricula();
        m.setId(100L); m.setUser(user); m.setTrilha(trilha1);
        m.setCriadaEm(java.time.LocalDateTime.now());

        when(repo.findById(100L)).thenReturn(java.util.Optional.of(m));
        when(trilhaRepo.findById(20L)).thenReturn(java.util.Optional.of(trilha2));
        when(repo.existsByUser_IdAndTrilha_Id(1L, 20L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> service.update(100L, new MatriculaUpdateRequest(null, 20L)));

        verify(repo, never()).save(any(Matricula.class));
    }

}
