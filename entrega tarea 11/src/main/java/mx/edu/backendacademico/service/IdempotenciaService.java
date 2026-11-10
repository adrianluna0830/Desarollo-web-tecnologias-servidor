package mx.edu.backendacademico.service;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.HexFormat;
import mx.edu.backendacademico.domain.*;
import mx.edu.backendacademico.dto.CrearAlumnoRequest;
import mx.edu.backendacademico.exception.OperacionAcademicaException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class IdempotenciaService {
    private final JdbcTemplate jdbc;
    private final AlumnoService alumnos;
    public IdempotenciaService(JdbcTemplate jdbc,AlumnoService alumnos) {
        this.jdbc=jdbc;
        this.alumnos=alumnos;
    }
    @Transactional
    public Alumno crear(String usuario,String clave,CrearAlumnoRequest request) {
        if (clave.isBlank() || clave.length()>120) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"clave invalida");
        var canonico=new Alumno(null,request.matricula(),request.nombre(),request.correo(),EstatusAlumno.ACTIVO);
        String contenido=parte(canonico.matricula())+parte(canonico.nombre())+parte(canonico.correo());
        String huella;
        try { huella=HexFormat.of().formatHex(MessageDigest.getInstance("sha-256").digest(contenido.getBytes(StandardCharsets.UTF_8))); }
        catch (NoSuchAlgorithmException ex) { throw new IllegalStateException(ex); }
        jdbc.update("insert into idempotencias(usuario,clave,huella) values (?,?,?) on conflict do nothing",usuario,clave,huella);
        var fila=jdbc.queryForMap("select * from idempotencias where usuario=? and clave=? for update",usuario,clave);
        if (!huella.equals(fila.get("huella"))) throw new OperacionAcademicaException("CLAVE_REUTILIZADA","clave usada con otro contenido");
        if (fila.get("alumno_id")!=null) {
            return new Alumno(((Number)fila.get("alumno_id")).longValue(),(String)fila.get("matricula"),
                (String)fila.get("nombre"),(String)fila.get("correo"),EstatusAlumno.valueOf((String)fila.get("estatus")));
        }
        var creado=alumnos.crear(canonico.matricula(),canonico.nombre(),canonico.correo());
        jdbc.update("update idempotencias set alumno_id=?,matricula=?,nombre=?,correo=?,estatus=? where usuario=? and clave=?",
            creado.id(),creado.matricula(),creado.nombre(),creado.correo(),creado.estatus().name(),usuario,clave);
        return creado;
    }
    private String parte(String texto) { return texto.length()+":"+texto; }
}
