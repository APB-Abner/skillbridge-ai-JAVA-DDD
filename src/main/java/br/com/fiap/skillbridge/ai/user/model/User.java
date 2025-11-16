package br.com.fiap.skillbridge.ai.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(max=150)
    private String nome;

    @Email @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank @Pattern(regexp="\\d{11}", message="CPF deve conter 11 dígitos numéricos")
    @Column(unique = true, length = 11)
    private String cpf;
}
