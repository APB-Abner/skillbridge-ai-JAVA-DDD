package br.com.fiap.skillbridge.ai.matricula.dto;

import java.time.LocalDateTime;

public record MatriculaResponse(
        Long id, Long userId, String userNome,
        Long trilhaId, String trilhaTitulo,
        LocalDateTime criadaEm
) {}
