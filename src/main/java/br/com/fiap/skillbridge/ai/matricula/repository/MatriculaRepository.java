package br.com.fiap.skillbridge.ai.matricula.repository;

import br.com.fiap.skillbridge.ai.matricula.model.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {
    boolean existsByUser_IdAndTrilha_Id(Long userId, Long trilhaId);
    List<Matricula> findByUser_Id(Long userId);
    List<Matricula> findByTrilha_Id(Long trilhaId);
    List<Matricula> findByUser_IdAndTrilha_Id(Long userId, Long trilhaId);
}
