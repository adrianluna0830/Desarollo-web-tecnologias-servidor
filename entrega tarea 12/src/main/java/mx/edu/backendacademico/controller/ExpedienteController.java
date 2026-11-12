package mx.edu.backendacademico.controller;

import mx.edu.backendacademico.dto.*;
import mx.edu.backendacademico.service.AlumnoService;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1")
public class ExpedienteController {
    private final JdbcTemplate jdbc;
    private final AlumnoService alumnos;

    public ExpedienteController(JdbcTemplate jdbc, AlumnoService alumnos) {
        this.jdbc = jdbc;
        this.alumnos = alumnos;
    }
    private Long propietario(Authentication principal) {
        return jdbc.queryForList("select alumno_id from cuentas_alumnos where usuario=?", Long.class, principal.getName())
            .stream().findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,"cuenta sin expediente"));
    }
    @GetMapping("/mi-expediente")
    public AlumnoResponse propio(Authentication principal) {
        return AlumnoMapper.toResponse(alumnos.buscar(propietario(principal)));
    }
    @GetMapping("/expedientes/{id}")
    public AlumnoResponse consultar(@PathVariable Long id, Authentication principal) {
        boolean admin = principal.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!admin && !propietario(principal).equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"expediente ajeno");
        }
        return AlumnoMapper.toResponse(alumnos.buscar(id));
    }
}
