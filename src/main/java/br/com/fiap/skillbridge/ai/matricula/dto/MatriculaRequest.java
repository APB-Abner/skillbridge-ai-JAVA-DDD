package br.com.fiap.skillbridge.ai.matricula.dto;
import jakarta.validation.constraints.NotNull;

public record MatriculaRequest(
        @NotNull Long userId,
        @NotNull Long trilhaId
) {}
