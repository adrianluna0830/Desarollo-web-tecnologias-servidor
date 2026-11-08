from pathlib import Path
import subprocess

raiz = Path(__file__).resolve().parent
casos = [
    ("http", "src/main/java/mx/edu/backendacademico/controller/AlumnoController.java",
     'ResponseEntity.created(URI.create("/api/v1/alumnos/" + creado.id()))', 'ResponseEntity.ok()',
     "AlumnoControllerTest#crearPublica201YLocation", "was:<200>"),
    ("unicidad", "src/main/resources/db/migration/V1__crear_alumnos.sql",
     "    CONSTRAINT uk_alumnos_matricula UNIQUE (matricula),\n", "",
     "PostgresIntegrationTest#postgresqlRechazaUnaMatriculaRepetida", "but nothing was thrown")
]
for nombre, ruta, antes, despues, prueba, causa in casos:
    archivo = raiz / ruta
    original = archivo.read_text()
    assert antes in original
    try:
        archivo.write_text(original.replace(antes, despues))
        resultado = subprocess.run(["./mvnw", "-B", "-ntp", "clean", "test", "-Dtest="+prueba], cwd=raiz, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        (raiz/"evidencias"/("mutacion-"+nombre+".log")).write_text(resultado.stdout)
        assert resultado.returncode != 0 and causa in resultado.stdout, nombre
        print(nombre + ": la prueba detecto la falla esperada")
    finally:
        archivo.write_text(original)
