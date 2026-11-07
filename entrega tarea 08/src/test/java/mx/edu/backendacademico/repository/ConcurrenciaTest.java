package mx.edu.backendacademico.repository;

import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.*;
import mx.edu.backendacademico.domain.*;
import mx.edu.backendacademico.exception.OperacionAcademicaException;
import mx.edu.backendacademico.repository.jpa.*;
import mx.edu.backendacademico.service.InscripcionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.*;

@SpringBootTest
@Testcontainers
class ConcurrenciaTest {
    @Container static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");
    @DynamicPropertySource static void properties(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
    }
    @Autowired AlumnoSpringDataRepository alumnos;
    @Autowired MateriaRepository materias;
    @Autowired InscripcionRepository inscripciones;
    @Autowired InscripcionService servicio;
    @Autowired JdbcTemplate jdbc;
    @Autowired PlatformTransactionManager manager;

    @Test void dosConexionesEsperanEnPostgresYSoloUnaObtieneCupo() throws Exception {
        var a = alumnos.saveAndFlush(new AlumnoEntity(new Alumno(null,"c1","ada","ada@u.mx",EstatusAlumno.ACTIVO)));
        var b = alumnos.saveAndFlush(new AlumnoEntity(new Alumno(null,"c2","emmy","emmy@u.mx",EstatusAlumno.ACTIVO)));
        var materia = materias.saveAndFlush(new MateriaEntity("c101","concurrencia",4,1));
        var listas = new CountDownLatch(2);
        var inicio = new CountDownLatch(1);
        try (var pool = Executors.newFixedThreadPool(2)) {
            var primera = pool.submit(() -> admitir(a.getId(),materia.getId(),listas,inicio));
            var segunda = pool.submit(() -> admitir(b.getId(),materia.getId(),listas,inicio));
            new TransactionTemplate(manager).executeWithoutResult(tx -> {
                jdbc.queryForObject("select id from materias where id=? for update",Long.class,materia.getId());
                try {
                    assertTrue(listas.await(5,TimeUnit.SECONDS));
                    inicio.countDown();
                    long limite = System.nanoTime()+TimeUnit.SECONDS.toNanos(10);
                    int esperando = 0;
                    while (System.nanoTime()<limite && esperando<2) {
                        jdbc.execute("select pg_stat_clear_snapshot()");
                        esperando = jdbc.queryForObject("select count(*) from pg_stat_activity where datname=current_database() and wait_event_type='Lock'",Integer.class);
                        Thread.sleep(25);
                    }
                    System.out.println("conexiones en espera de bloqueo postgres: " + esperando);
                    assertEquals(2,esperando);
                } catch (InterruptedException ex) { throw new RuntimeException(ex); }
                finally { inicio.countDown(); }
            });
            var resultados = java.util.List.of(primera.get(15,TimeUnit.SECONDS),segunda.get(15,TimeUnit.SECONDS));
            assertTrue(resultados.contains("creada"));
            assertTrue(resultados.contains("SIN_CUPO"));
            assertEquals(1,inscripciones.countByMateriaId(materia.getId()));
            System.out.println("admisiones: " + resultados + "; inscripciones persistidas: 1");
        }
    }
    private String admitir(Long alumno,Long materia,CountDownLatch listas,CountDownLatch inicio) throws Exception {
        listas.countDown();
        assertTrue(inicio.await(5,TimeUnit.SECONDS));
        try { servicio.inscribir(alumno,materia); return "creada"; }
        catch (OperacionAcademicaException ex) { return ex.getCodigo(); }
    }
}
