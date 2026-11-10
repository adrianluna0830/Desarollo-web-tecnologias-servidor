package mx.edu.backendacademico.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.UUID;
import java.util.concurrent.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class IdempotenciaTest {
    @Container static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");
    @DynamicPropertySource static void properties(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url",postgres::getJdbcUrl);
        r.add("spring.datasource.username",postgres::getUsername);
        r.add("spring.datasource.password",postgres::getPassword);
    }
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;
    private MvcResult crear(String clave,String matricula,String nombre) throws Exception {
        return mvc.perform(post("/api/v1/alumnos").with(user("docente").roles("DOCENTE")).with(csrf())
            .header("idempotency-key",clave).contentType("application/json")
            .content("{\"matricula\":\""+matricula+"\",\"nombre\":\""+nombre+"\",\"correo\":\"ada@u.mx\"}"))
            .andReturn();
    }
    @Test void dosSolicitudesSimultaneasCreanUnSoloAlumno() throws Exception {
        String clave=UUID.randomUUID().toString();
        String matricula="i"+clave.substring(0,12);
        var listas=new CountDownLatch(2);
        var inicio=new CountDownLatch(1);
        try (var pool=Executors.newFixedThreadPool(2)) {
            Callable<MvcResult> tarea=() -> {
                listas.countDown();
                assertTrue(inicio.await(5,TimeUnit.SECONDS));
                return crear(clave,matricula,"ada");
            };
            var a=pool.submit(tarea);
            var b=pool.submit(tarea);
            assertTrue(listas.await(5,TimeUnit.SECONDS));
            inicio.countDown();
            var primera=a.get(20,TimeUnit.SECONDS).getResponse();
            var segunda=b.get(20,TimeUnit.SECONDS).getResponse();
            assertEquals(201,primera.getStatus());
            assertEquals(201,segunda.getStatus());
            assertEquals(primera.getHeader("Location"),segunda.getHeader("Location"));
            assertEquals(primera.getContentAsString(),segunda.getContentAsString());
            assertEquals(1,jdbc.queryForObject("select count(*) from alumnos where matricula=upper(?)",Integer.class,matricula));
            assertEquals(1,jdbc.queryForObject("select count(*) from idempotencias where usuario=? and clave=?",Integer.class,"docente",clave));
            jdbc.update("update alumnos set nombre='cambiado' where matricula=upper(?)",matricula);
            assertEquals(primera.getContentAsString(),crear(clave,matricula,"ada").getResponse().getContentAsString());
            System.out.println("misma clave simultanea: 201 y 201; mismo location y cuerpo; un alumno; repeticion conserva respuesta original");
        }
    }
    @Test void mismaClaveConOtroContenidoRecibe409() throws Exception {
        String clave=UUID.randomUUID().toString();
        String matricula="j"+clave.substring(0,12);
        assertEquals(201,crear(clave,matricula,"ada").getResponse().getStatus());
        var resultado=crear(clave,matricula,"emmy");
        status().isConflict().match(resultado);
        jsonPath("$.codigo").value("CLAVE_REUTILIZADA").match(resultado);
        assertEquals("ada",jdbc.queryForObject("select nombre from alumnos where matricula=upper(?)",String.class,matricula));
        System.out.println("misma clave y contenido distinto: 409; alumno original conservado");
    }
}
