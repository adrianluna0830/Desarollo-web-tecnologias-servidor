package mx.edu.backendacademico.service;

import java.util.List;
import mx.edu.backendacademico.domain.Alumno;
import mx.edu.backendacademico.domain.EstatusAlumno;
import mx.edu.backendacademico.exception.AlumnoNoEncontradoException;
import mx.edu.backendacademico.exception.MatriculaDuplicadaException;
import mx.edu.backendacademico.repository.AlumnoRepository;
import org.springframework.stereotype.Service;

@Service
public class AlumnoService {
    private final AlumnoRepository repository;

    public AlumnoService(AlumnoRepository repository) {
        this.repository = repository;
    }

    public List<Alumno> listar() {
        return repository.buscarTodos();
    }

    public Alumno buscar(Long id) {
        return repository.buscarPorId(id).orElseThrow(() -> new AlumnoNoEncontradoException(id));
    }

    public Alumno crear(String matricula, String nombre, String correo) {
        if (repository.existePorMatricula(matricula)) {
            throw new MatriculaDuplicadaException(matricula);
        }
        return repository.guardar(new Alumno(null, matricula, nombre, correo, EstatusAlumno.ACTIVO));
    }

    public Alumno actualizar(Long id, String nombre, String correo) {
        var actual = buscar(id);
        return repository.guardar(new Alumno(id, actual.matricula(), nombre, correo, actual.estatus()));
    }

    public void eliminar(Long id) {
        buscar(id);
        repository.eliminarPorId(id);
    }
}
