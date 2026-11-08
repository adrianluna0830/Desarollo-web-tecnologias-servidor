package mx.edu.backendacademico.repository;

import java.util.List;
import java.util.Optional;
import mx.edu.backendacademico.domain.Alumno;

public interface AlumnoRepository {
    Alumno guardar(Alumno alumno);
    List<Alumno> buscarTodos();
    Optional<Alumno> buscarPorId(Long id);
    boolean existePorMatricula(String matricula);
    void eliminarPorId(Long id);
}
