package mx.edu.backendacademico.exception;

public class MatriculaDuplicadaException extends RuntimeException {
    public MatriculaDuplicadaException(String matricula) {
        super("ya existe la matricula " + matricula);
    }
}
