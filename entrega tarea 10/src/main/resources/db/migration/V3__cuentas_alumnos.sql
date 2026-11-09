CREATE TABLE cuentas_alumnos (
    usuario VARCHAR(120) PRIMARY KEY,
    alumno_id BIGINT NOT NULL UNIQUE REFERENCES alumnos(id)
);
