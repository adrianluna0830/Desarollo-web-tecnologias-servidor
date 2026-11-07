# Entregas del curso

`main` contiene las soluciones guiadas de las prácticas 1 a 7. El reto de la práctica 7 se encuentra en la rama `reto-u07`, dentro de `entrega tarea 07`.

| Unidad | Carpeta del proyecto | Rama del reto |
| --- | --- | --- |
| 01 | entrega tarea 01 | reto-u01 |
| 02 | entrega tarea 02 | reto-u02 |
| 03 | entrega tarea 03 | reto-u03 |
| 04 | entrega tarea 04 | reto-u04 |
| 05 | entrega tarea 05 | reto-u05 |
| 06 | entrega tarea 06 | reto-u06 |
| 07 | entrega tarea 07 | reto-u07 |

La solución guiada de la práctica 7 está en el commit `1d3a828`; el reto está en `8a6a6ab`, en la rama `reto-u07`. El reto agrega `V2__nombre_no_vacio.sql` sin modificar V1, para rechazar nombres vacíos o compuestos solo por espacios.

Para consultar el reto:

```bash
git switch reto-u07
cd 'entrega tarea 07'
./mvnw clean verify
```

Para volver a la solución guiada, utiliza `git switch main` desde la raíz del repositorio.

## Ejecutar la práctica 7

Configura `JAVA_HOME` con JDK 25. El proyecto compila para Java 21.

```bash
cd 'entrega tarea 07'
./mvnw clean verify
```

Las cuatro pruebas de contexto y dominio utilizan H2; no requieren PostgreSQL. Para ejecutar la aplicación y comprobar las migraciones y la persistencia, utiliza PostgreSQL 17 y la configuración de `compose.yaml` y `src/main/resources/application.properties`.

En la rama `reto-u07`, usa una base separada para el reto, como `reto7`, para conservar la base de la solución con V1. El archivo `verificar.sql` de esa rama consulta el historial de Flyway, el identificador interno de la tabla y los alumnos guardados. Los registros originales de arranque, pruebas y comprobaciones de PostgreSQL están en `evidencias/` de cada versión.

Los documentos editables permanecen fuera del repositorio, en la carpeta principal del curso: `Documento tarea NN.docx` y `.txt`.
