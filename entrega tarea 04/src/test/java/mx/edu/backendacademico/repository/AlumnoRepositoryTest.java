package mx.edu.backendacademico.repository;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import mx.edu.backendacademico.domain.Alumno;
import mx.edu.backendacademico.domain.EstatusAlumno;
import mx.edu.backendacademico.exception.MatriculaDuplicadaException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AlumnoRepositoryTest {
    @Test void concurrencia() throws InterruptedException {
        var repo = new AlumnoRepositoryEnMemoria();
        var inicio = new CountDownLatch(1);
        var fin = new CountDownLatch(2);
        var exitos = new AtomicInteger();
        var conflictos = new AtomicInteger();

        try (var executor = Executors.newFixedThreadPool(2)) {
            for (int i = 0; i < 2; i++) {
                final int idx = i;
                executor.submit(() -> {
                    try {
                        inicio.await();
                        repo.guardar(new Alumno(null, "A1", "alumno " + idx, "a" + idx + "@u.mx", EstatusAlumno.ACTIVO));
                        exitos.incrementAndGet();
                    } catch (MatriculaDuplicadaException e) {
                        conflictos.incrementAndGet();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        fin.countDown();
                    }
                });
            }
            inicio.countDown();
            fin.await();
        }

        assertEquals(1, exitos.get());
        assertEquals(1, conflictos.get());
        assertEquals(1, repo.buscarTodos().size());
    }
}
