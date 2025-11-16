package br.com.fiap.skillbridge.ai.user.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {

    private final Validator validator =
            Validation.buildDefaultValidatorFactory().getValidator();

    // === UserRequest ========================================================

    @Test
    void userRequest_valido_naoGeraViolacoes() {
        var dto = new UserRequest("Abner", "abner@fiap.com", "12345678901");

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void userRequest_camposObrigatorios_invalidos() {
        var dto = new UserRequest(" ", "email-invalido", "123");

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("nome")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("cpf")));
    }

    // === UserUpdateRequest ==================================================

    @Test
    void userUpdateRequest_valido_naoGeraViolacoes() {
        var dto = new UserUpdateRequest(
                "Abner Atualizado",
                "novo.email@fiap.com",
                "98765432100"
        );

        Set<ConstraintViolation<UserUpdateRequest>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void userUpdateRequest_nomeMaiorQue150GeraViolacao() {
        String nomeGrande = "A".repeat(151);
        var dto = new UserUpdateRequest(
                nomeGrande,
                "email@fiap.com",
                "12345678901"
        );

        Set<ConstraintViolation<UserUpdateRequest>> violations = validator.validate(dto);

        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("nome")));
    }

    // === UserResponse =======================================================

    @Test
    void userResponse_record_expoeComponentesCorretamente() {
        var resp = new UserResponse(1L, "Abner", "abner@fiap.com", "12345678901");

        assertEquals(1L, resp.id());
        assertEquals("Abner", resp.nome());
        assertEquals("abner@fiap.com", resp.email());
        assertEquals("12345678901", resp.cpf());
    }
}
