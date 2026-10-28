package mx.edu.backendacademico.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import mx.edu.backendacademico.domain.*;
import mx.edu.backendacademico.repository.AlumnoRepositoryEnMemoria;
import mx.edu.backendacademico.service.AlumnoService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class FiltroTest {
    @Test void filtraSinModificarElRepositorio() throws Exception {
        var repo = new AlumnoRepositoryEnMemoria();
        repo.guardar(new Alumno(null, "a1", "ada", "ada@u.mx", EstatusAlumno.ACTIVO));
        repo.guardar(new Alumno(null, "a2", "emmy", "emmy@u.mx", EstatusAlumno.BAJA));
        var antes = repo.buscarTodos();
        var mvc = MockMvcBuilders.standaloneSetup(new AlumnoController(new AlumnoService(repo))).build();
        mvc.perform(get("/api/v1/alumnos")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));
        mvc.perform(get("/api/v1/alumnos?estatus=ACTIVO")).andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1)).andExpect(jsonPath("$[0].estatus").value("ACTIVO"));
        mvc.perform(get("/api/v1/alumnos?estatus=BAJA")).andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1)).andExpect(jsonPath("$[0].estatus").value("BAJA"));
        assertEquals(antes, repo.buscarTodos());
    }
}
