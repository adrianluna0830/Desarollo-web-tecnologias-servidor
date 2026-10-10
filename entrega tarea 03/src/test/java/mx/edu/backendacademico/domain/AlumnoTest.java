package mx.edu.backendacademico.domain;

import java.util.Locale;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AlumnoTest {
    @Test void baja() {
        var alumno = new Alumno(1L, " a1 ", "ada", "ada@u.mx", EstatusAlumno.ACTIVO);
        var baja = alumno.darDeBaja();
        assertEquals("a1".toUpperCase(Locale.ROOT), baja.matricula());
        assertEquals(alumno.id(), baja.id());
        assertEquals(EstatusAlumno.BAJA, baja.estatus());
        assertEquals(EstatusAlumno.ACTIVO, alumno.estatus());
    }

    @Test void matriculaVacia() {
        assertThrows(IllegalArgumentException.class, () ->
            new Alumno(null, " ", "ada", "ada@u.mx", EstatusAlumno.ACTIVO));
    }

    @Test void creditosInvalidos() {
        assertThrows(IllegalArgumentException.class, () ->
            new Materia(null, "m1", "analisis", 0));
    }

    @Test void reactivar() {
        var alumno = new Alumno(1L, "a1", "ada", "ada@u.mx", EstatusAlumno.BAJA);
        var activo = alumno.reactivar();
        assertEquals(new Alumno(1L, "a1", "ada", "ada@u.mx", EstatusAlumno.ACTIVO), activo);
        assertEquals(EstatusAlumno.BAJA, alumno.estatus());
        assertNotSame(alumno, activo);
    }

    @Test void igualdad() {
        var uno = new Alumno(1L, "a1", "ada", "ada@u.mx", EstatusAlumno.ACTIVO);
        var otro = new Alumno(1L, "a1", "ada", "nuevo@u.mx", EstatusAlumno.ACTIVO);
        assertEquals(uno.id(), otro.id());
        assertNotEquals(uno, otro);
    }
}
