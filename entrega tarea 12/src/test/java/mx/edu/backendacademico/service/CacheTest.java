package mx.edu.backendacademico.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import mx.edu.backendacademico.repository.AlumnoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.*;

@SpringBootTest
@Testcontainers
class CacheTest {
    @Container static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");
    @DynamicPropertySource static void properties(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url",postgres::getJdbcUrl);
        r.add("spring.datasource.username",postgres::getUsername);
        r.add("spring.datasource.password",postgres::getPassword);
    }
    @Autowired AlumnoService alumnos;
    @MockitoSpyBean AlumnoRepository repository;

    @Test void dosConsultasUsanCacheYUnCambioLaInvalida() {
        var alumno=alumnos.crear("cache12","ada","ada@u.mx");
        clearInvocations(repository);
        assertEquals("ada",alumnos.buscar(alumno.id()).nombre());
        assertEquals("ada",alumnos.buscar(alumno.id()).nombre());
        verify(repository,times(1)).buscarPorId(alumno.id());
        alumnos.actualizar(alumno.id(),"ada byron","ada@u.mx");
        clearInvocations(repository);
        assertEquals("ada byron",alumnos.buscar(alumno.id()).nombre());
        assertEquals("ada byron",alumnos.buscar(alumno.id()).nombre());
        verify(repository,times(1)).buscarPorId(alumno.id());
        System.out.println("cache: dos consultas, una lectura; actualizacion invalida y siguiente lectura obtiene el cambio");
    }
}
