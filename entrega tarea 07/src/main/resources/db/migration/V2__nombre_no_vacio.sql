ALTER TABLE alumnos ADD CONSTRAINT ck_nombre_no_vacio CHECK (length(trim(nombre)) > 0);
