package mx.edu.backendacademico.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import mx.edu.backendacademico.domain.*;
import mx.edu.backendacademico.repository.jpa.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ExpedienteTest {
    @Container static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");
    @DynamicPropertySource static void properties(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url",postgres::getJdbcUrl);
        r.add("spring.datasource.username",postgres::getUsername);
        r.add("spring.datasource.password",postgres::getPassword);
    }
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;
    @Autowired AlumnoSpringDataRepository alumnos;

    @Test void propietarioAjenoYAdministrador() throws Exception {
        var a = alumnos.saveAndFlush(new AlumnoEntity(new Alumno(null,"e1","ada","ada@u.mx",EstatusAlumno.ACTIVO)));
        var b = alumnos.saveAndFlush(new AlumnoEntity(new Alumno(null,"e2","emmy","emmy@u.mx",EstatusAlumno.ACTIVO)));
        jdbc.update("insert into cuentas_alumnos(usuario,alumno_id) values (?,?),(?,?)","alumno",a.getId(),"ajeno",b.getId());
        mvc.perform(get("/api/v1/mi-expediente").with(user("alumno").roles("ALUMNO")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(a.getId()));
        mvc.perform(get("/api/v1/expedientes/"+a.getId()).with(user("alumno").roles("ALUMNO")))
            .andExpect(status().isOk());
        mvc.perform(get("/api/v1/expedientes/"+a.getId()).with(user("ajeno").roles("ALUMNO")))
            .andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/expedientes/"+a.getId()).with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk());
        mvc.perform(get("/api/v1/mi-expediente?usuario=alumno").with(user("ajeno").roles("ALUMNO")))
            .andExpect(jsonPath("$.id").value(b.getId()));
        mvc.perform(get("/api/v1/alumnos/"+a.getId()).with(user("ajeno").roles("ALUMNO")))
            .andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/alumnos").with(user("alumno").roles("ALUMNO")))
            .andExpect(status().isForbidden());
    }
}
