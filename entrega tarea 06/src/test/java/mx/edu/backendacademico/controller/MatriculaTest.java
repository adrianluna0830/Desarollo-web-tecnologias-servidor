package mx.edu.backendacademico.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import mx.edu.backendacademico.exception.GlobalExceptionHandler;
import mx.edu.backendacademico.repository.AlumnoRepositoryEnMemoria;
import mx.edu.backendacademico.service.AlumnoService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class MatriculaTest {
    private final org.springframework.test.web.servlet.MockMvc mvc = MockMvcBuilders
        .standaloneSetup(new AlumnoController(new AlumnoService(new AlumnoRepositoryEnMemoria())))
        .setControllerAdvice(new GlobalExceptionHandler()).build();

    @Test void rechazaEspaciosInternosConElMismoCodigo() throws Exception {
        mvc.perform(post("/api/v1/alumnos").contentType("application/json")
            .content("""
                {"matricula":"a 1","nombre":"ada","correo":"ada@u.mx"}
                """))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.codigo").value("DATOS_INVALIDOS"))
            .andExpect(jsonPath("$.campos[0].campo").value("matricula"));
    }

    @Test void conservaLaNormalizacion() throws Exception {
        mvc.perform(post("/api/v1/alumnos").contentType("application/json")
            .content("""
                {"matricula":" a1 ","nombre":"ada","correo":"ada@u.mx"}
                """))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.matricula").value("A1"));
    }
}
