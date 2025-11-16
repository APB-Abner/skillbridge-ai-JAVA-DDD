package br.com.fiap.skillbridge.ai.trilha.controller;

import br.com.fiap.skillbridge.ai.trilha.dto.*;
import br.com.fiap.skillbridge.ai.trilha.service.TrilhaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name="Trilhas")
@RestController
@RequestMapping("/api/v1/trilhas")
public class TrilhaController {
    private final TrilhaService service;
    public TrilhaController(TrilhaService service){ this.service = service; }

    @Operation(summary="Cria trilha")
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public TrilhaResponse create(@RequestBody @Valid TrilhaRequest r){ return service.create(r); }

    @Operation(summary="Lista trilhas")
    @GetMapping public List<TrilhaResponse> list(){ return service.list(); }

    @Operation(summary="Busca trilha")
    @GetMapping("/{id}") public TrilhaResponse get(@PathVariable Long id){ return service.get(id); }

    @Operation(summary = "Atualiza trilha")
    @PutMapping("/{id}")
    public TrilhaResponse update(@PathVariable Long id, @RequestBody @Valid TrilhaUpdateRequest r) {
        return service.update(id, r);
    }

    @Operation(summary="Remove trilha")
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){ service.delete(id); }
}
