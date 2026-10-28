package mx.edu.backendacademico.exception;

public class AlumnoNoEncontradoException extends RuntimeException {
    public AlumnoNoEncontradoException(Long id) {
        super("no existe el alumno con id " + id);
    }
}
