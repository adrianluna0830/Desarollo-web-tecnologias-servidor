CREATE TABLE idempotencias (
    usuario VARCHAR(120) NOT NULL,
    clave VARCHAR(120) NOT NULL,
    huella CHAR(64) NOT NULL,
    alumno_id BIGINT,
    matricula VARCHAR(30),
    nombre VARCHAR(120),
    correo VARCHAR(160),
    estatus VARCHAR(20),
    PRIMARY KEY (usuario, clave)
);
