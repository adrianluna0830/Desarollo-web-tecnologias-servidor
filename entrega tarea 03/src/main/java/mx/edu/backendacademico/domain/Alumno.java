package mx.edu.backendacademico.domain;

import java.util.Locale;
import java.util.Objects;

public record Alumno(Long id, String matricula, String nombre, String correo,
                     EstatusAlumno estatus) {
    public Alumno {
        if (id != null && id <= 0) throw new IllegalArgumentException("id invalido");
        matricula = texto(matricula, 30).toUpperCase(Locale.ROOT);
        nombre = texto(nombre, 120);
        correo = texto(correo, 160);
        Objects.requireNonNull(estatus, "estatus obligatorio");
    }

    private static String texto(String valor, int maximo) {
        if (valor == null || valor.isBlank() || valor.strip().length() > maximo) {
            throw new IllegalArgumentException("texto invalido");
        }
        return valor.strip();
    }

    public Alumno darDeBaja() {
        return new Alumno(id, matricula, nombre, correo, EstatusAlumno.BAJA);
    }

    public Alumno reactivar() {
        return new Alumno(id, matricula, nombre, correo, EstatusAlumno.ACTIVO);
    }
}
