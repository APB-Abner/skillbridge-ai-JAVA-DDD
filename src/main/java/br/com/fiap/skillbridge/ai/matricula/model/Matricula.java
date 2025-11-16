package br.com.fiap.skillbridge.ai.matricula.model;

import br.com.fiap.skillbridge.ai.trilha.model.Trilha;
import br.com.fiap.skillbridge.ai.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name="matriculas", uniqueConstraints=@UniqueConstraint(columnNames={"user_id","trilha_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Matricula {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false) @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(optional=false) @JoinColumn(name="trilha_id")
    private Trilha trilha;

    @PastOrPresent
    private LocalDateTime criadaEm;
}
