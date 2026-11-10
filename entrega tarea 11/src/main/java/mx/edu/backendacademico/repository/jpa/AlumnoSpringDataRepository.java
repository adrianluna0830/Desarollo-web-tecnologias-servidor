package mx.edu.backendacademico.repository.jpa;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AlumnoSpringDataRepository extends JpaRepository<AlumnoEntity, Long> {
    boolean existsByMatriculaIgnoreCase(String matricula);
}
