package br.com.fiap.skillbridge.ai.user.controller;

import br.com.fiap.skillbridge.ai.user.dto.*;
import br.com.fiap.skillbridge.ai.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Usuários")
@RestController
@RequestMapping("/api/v1/usuarios")
public class UserController {

    private final UserService service;
    public UserController(UserService service) { this.service = service; }

    @Operation(summary = "Cria um usuário")
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@RequestBody @Valid UserRequest req){
        return service.create(req);
    }

    @Operation(summary = "Lista usuários")
    @GetMapping
    public List<UserResponse> list(){ return service.list(); }

    @Operation(summary = "Detalha um usuário")
    @GetMapping("/{id}")
    public UserResponse get(@PathVariable Long id){ return service.get(id); }

    @Operation(summary = "Atualiza um usuário")
    @PutMapping("/{id}")
    public UserResponse update(@PathVariable Long id, @RequestBody @Valid UserUpdateRequest req){
        return service.update(id, req);
    }

    @Operation(summary = "Remove um usuário")
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){ service.delete(id); }
}
