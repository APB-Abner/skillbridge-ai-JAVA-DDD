package br.com.fiap.skillbridge.ai.matricula.service;

import br.com.fiap.skillbridge.ai.matricula.dto.*;
import br.com.fiap.skillbridge.ai.matricula.model.Matricula;
import br.com.fiap.skillbridge.ai.matricula.repository.MatriculaRepository;
import br.com.fiap.skillbridge.ai.shared.exception.NotFoundException;
import br.com.fiap.skillbridge.ai.trilha.repository.TrilhaRepository;
import br.com.fiap.skillbridge.ai.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MatriculaService {
    private final MatriculaRepository repo;
    private final UserRepository userRepo;
    private final TrilhaRepository trilhaRepo;

    public MatriculaService(MatriculaRepository repo, UserRepository userRepo, TrilhaRepository trilhaRepo){
        this.repo = repo; this.userRepo = userRepo; this.trilhaRepo = trilhaRepo;
    }

    @Transactional
    public MatriculaResponse create(MatriculaRequest r){
        var user = userRepo.findById(r.userId()).orElseThrow(() -> new NotFoundException("Usuário não encontrado."));
        var trilha = trilhaRepo.findById(r.trilhaId()).orElseThrow(() -> new NotFoundException("Trilha não encontrada."));
        if (repo.existsByUser_IdAndTrilha_Id(r.userId(), r.trilhaId()))
            throw new IllegalArgumentException("Usuário já matriculado nesta trilha.");

        var m = Matricula.builder().user(user).trilha(trilha).criadaEm(LocalDateTime.now()).build();
        return map(repo.save(m));
    }

    @Transactional(readOnly = true)
    public List<MatriculaResponse> list(Long userId, Long trilhaId){
        if (userId != null && trilhaId != null)
            return repo.findByUser_IdAndTrilha_Id(userId, trilhaId).stream().map(this::map).toList();
        if (userId != null)
            return repo.findByUser_Id(userId).stream().map(this::map).toList();
        if (trilhaId != null)
            return repo.findByTrilha_Id(trilhaId).stream().map(this::map).toList();
        return repo.findAll().stream().map(this::map).toList();
    }

    @Transactional
    public void delete(Long id){
        if (!repo.existsById(id)) throw new NotFoundException("Matrícula não encontrada.");
        repo.deleteById(id);
    }

    private MatriculaResponse map(Matricula m){
        return new MatriculaResponse(
                m.getId(),
                m.getUser().getId(), m.getUser().getNome(),
                m.getTrilha().getId(), m.getTrilha().getTitulo(),
                m.getCriadaEm()
        );
    }

    public MatriculaResponse update(Long id, MatriculaUpdateRequest r) {
        var m = repo.findById(id).orElseThrow(() -> new NotFoundException("Matrícula não encontrada."));

        Long newUserId = r.userId();
        Long newTrilhaId = r.trilhaId();

        boolean userChanged = newUserId != null && !newUserId.equals(m.getUser().getId());
        boolean trilhaChanged = newTrilhaId != null && !newTrilhaId.equals(m.getTrilha().getId());

        if (userChanged) {
            var user = userRepo.findById(newUserId)
                    .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));
            m.setUser(user);
        }

        if (trilhaChanged) {
            var trilha = trilhaRepo.findById(newTrilhaId)
                    .orElseThrow(() -> new NotFoundException("Trilha não encontrada."));
            m.setTrilha(trilha);
        }

        if ((userChanged || trilhaChanged)
                && repo.existsByUser_IdAndTrilha_Id(m.getUser().getId(), m.getTrilha().getId())) {
            throw new IllegalArgumentException("Usuário já matriculado nesta trilha.");
        }

        return map(repo.save(m));
    }


}
