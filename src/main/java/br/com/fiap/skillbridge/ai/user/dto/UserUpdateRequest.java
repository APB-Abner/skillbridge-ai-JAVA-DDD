package br.com.fiap.skillbridge.ai.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @NotBlank @Size(max=150) String nome,
        @NotBlank @Email String email,
        @NotBlank @Pattern(regexp="\\d{11}") String cpf
) {}
