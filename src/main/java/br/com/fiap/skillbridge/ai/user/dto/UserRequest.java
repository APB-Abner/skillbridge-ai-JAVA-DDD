package br.com.fiap.skillbridge.ai.user.dto;

import jakarta.validation.constraints.*;

public record UserRequest(
        @NotBlank @Size(max=150) String nome,
        @NotBlank @Email String email,
        @NotBlank @Pattern(regexp="\\d{11}") String cpf
) {}
