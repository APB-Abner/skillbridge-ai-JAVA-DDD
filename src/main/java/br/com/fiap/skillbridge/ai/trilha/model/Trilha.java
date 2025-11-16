package br.com.fiap.skillbridge.ai.trilha.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity @Table(name="trilhas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Trilha {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(max=180)
    private String titulo;

    @Size(max=800)
    private String descricao;

    @Builder.Default
    private boolean ativa = true;
}
