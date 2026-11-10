# Entregas del curso

`main` contiene las soluciones guiadas de las tareas 1 a 11. Cada reto se consulta en su rama independiente.

| Unidad | Carpeta del proyecto | Rama del reto |
| --- | --- | --- |
| 01 | entrega tarea 01 | reto-u01 |
| 02 | entrega tarea 02 | reto-u02 |
| 03 | entrega tarea 03 | reto-u03 |
| 04 | entrega tarea 04 | reto-u04 |
| 05 | entrega tarea 05 | reto-u05 |
| 06 | entrega tarea 06 | reto-u06 |
| 07 | entrega tarea 07 | reto-u07 |
| 08 | entrega tarea 08 | reto-u08 |
| 09 | entrega tarea 09 | reto-u09 |
| 10 | entrega tarea 10 | reto-u10 |
| 11 | entrega tarea 11 | reto-u11 |

Para ejecutar la solución de la tarea 11, usa `main`. Para ejecutar su reto, usa `reto-u11`. Cada rama de reto parte del commit de la solución de su tarea.

```bash
git switch main
cd 'entrega tarea 11'
./mvnw clean verify
```

Configura `JAVA_HOME` con JDK 25; los proyectos compilan para Java 21. Usa bases separadas al cambiar de tarea o reto para conservar sus migraciones. Las pruebas con Testcontainers desde la tarea 9 y la prueba de concurrencia del reto 8 requieren Docker o una API compatible de Podman.

Los archivos de ejecución y pruebas se encuentran en `evidencias/` de cada proyecto. Los documentos Word, TXT y PDF permanecen fuera del repositorio, en la carpeta principal del curso.
