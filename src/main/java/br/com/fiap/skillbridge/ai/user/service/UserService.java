package br.com.fiap.skillbridge.ai.user.service;

import br.com.fiap.skillbridge.ai.user.dto.*;
import br.com.fiap.skillbridge.ai.user.model.User;
import br.com.fiap.skillbridge.ai.user.repository.UserRepository;
import br.com.fiap.skillbridge.ai.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repo;
    public UserService(UserRepository repo) { this.repo = repo; }

    @Transactional
    public UserResponse create(UserRequest req){
        if (repo.existsByEmail(req.email()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado.");
        if (repo.existsByCpf(req.cpf()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado.");

        var saved = repo.save(User.builder()
                .nome(req.nome()).email(req.email()).cpf(req.cpf()).build());
        return map(saved);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> list(){
        return repo.findAll().stream().map(this::map).toList();
    }

    @Transactional(readOnly = true)
    public UserResponse get(Long id){
        var u = repo.findById(id).orElseThrow(() -> new NotFoundException("Usuário não encontrado."));
        return map(u);
    }

    @Transactional
    public UserResponse update(Long id, UserUpdateRequest req){
        var u = repo.findById(id).orElseThrow(() -> new NotFoundException("Usuário não encontrado."));

        if (repo.existsByEmailAndIdNot(req.email(), id))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado por outro usuário.");
        if (repo.existsByCpfAndIdNot(req.cpf(), id))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado por outro usuário.");

        u.setNome(req.nome()); u.setEmail(req.email()); u.setCpf(req.cpf());
        return map(repo.save(u));
    }


    @Transactional
    public void delete(Long id){
        if (!repo.existsById(id)) throw new NotFoundException("Usuário não encontrado.");
        repo.deleteById(id);
    }

    private UserResponse map(User u){
        return new UserResponse(u.getId(), u.getNome(), u.getEmail(), u.getCpf());
    }}
