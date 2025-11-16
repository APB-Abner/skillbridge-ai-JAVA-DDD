package br.com.fiap.skillbridge.ai.matricula.dto;

import jakarta.validation.constraints.NotNull;

public record MatriculaUpdateRequest(

        @NotNull(message = "Usuário é obrigatório")
        Long userId,

        @NotNull(message = "Trilha é obrigatória")
        Long trilhaId
) {}
