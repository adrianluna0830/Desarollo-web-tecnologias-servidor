package mx.edu.backendacademico.controller;

import java.net.URI;
import jakarta.validation.Valid;
import mx.edu.backendacademico.dto.*;
import mx.edu.backendacademico.service.IdempotenciaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/alumnos")
public class IdempotenciaController {
    private final IdempotenciaService service;
    public IdempotenciaController(IdempotenciaService service) { this.service=service; }
    @PostMapping(headers="idempotency-key")
    public ResponseEntity<AlumnoResponse> crear(@Valid @RequestBody CrearAlumnoRequest request,
            @RequestHeader("idempotency-key") String clave,Authentication principal) {
        var creado=service.crear(principal.getName(),clave,request);
        return ResponseEntity.created(URI.create("/api/v1/alumnos/"+creado.id())).body(AlumnoMapper.toResponse(creado));
    }
}
