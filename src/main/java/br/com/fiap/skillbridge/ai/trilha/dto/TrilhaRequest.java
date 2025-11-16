package br.com.fiap.skillbridge.ai.trilha.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record TrilhaRequest(
        @NotBlank @Size(max=180) String titulo,
        @Size(max=800) String descricao,
        Boolean ativa
) {}
