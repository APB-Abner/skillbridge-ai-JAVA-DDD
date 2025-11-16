package br.com.fiap.skillbridge.ai.matricula.controller;

import br.com.fiap.skillbridge.ai.matricula.dto.MatriculaRequest;
import br.com.fiap.skillbridge.ai.matricula.dto.MatriculaResponse;
import br.com.fiap.skillbridge.ai.matricula.dto.MatriculaUpdateRequest;
import br.com.fiap.skillbridge.ai.matricula.service.MatriculaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Matrículas")
@RestController
@RequestMapping("/api/v1/matriculas")
public class MatriculaController {
    private final MatriculaService service;

    public MatriculaController(MatriculaService service) {
        this.service = service;
    }

    @Operation(summary = "Matricula usuário em trilha")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MatriculaResponse create(@RequestBody @Valid MatriculaRequest r) {
        return service.create(r);
    }

    @Operation(summary = "Lista matrículas (filtros opcionais)")
    @GetMapping
    public List<MatriculaResponse> list(@RequestParam(required = false) Long userId,
                                        @RequestParam(required = false) Long trilhaId) {
        return service.list(userId, trilhaId);
    }
@Operation(summary = "Atualiza matrícula")
    @PutMapping("/{id}")
    public MatriculaResponse update(@PathVariable Long id,
                                    @RequestBody @Valid MatriculaUpdateRequest r) {
        return service.update(id, r);
    }

    @Operation(summary = "Cancela matrícula")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
