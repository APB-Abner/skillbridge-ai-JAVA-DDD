package br.com.fiap.skillbridge.ai.trilha.dto;

import jakarta.validation.constraints.NotBlank;

public record TrilhaUpdateRequest(

        @NotBlank(message = "Título é obrigatório")
        String titulo,

        @NotBlank(message = "Descrição é obrigatória")
        String descricao,

        Boolean ativa
) {}
