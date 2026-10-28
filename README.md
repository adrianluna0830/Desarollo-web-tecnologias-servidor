# entregas del curso

esta version contiene las soluciones guiadas. los retos se consultan en sus ramas respectivas.

main conserva un commit por unidad, doce en total. cada rama de reto tiene un commit independiente sobre la solucion de su unidad; al contar todas las ramas se incluyen tambien esos commits.

| unidad | carpeta del proyecto | rama del reto |
| --- | --- | --- |
| 01 | entrega tarea 01 | reto-u01 |
| 02 | entrega tarea 02 | reto-u02 |
| 03 | entrega tarea 03 | reto-u03 |
| 04 | entrega tarea 04 | reto-u04 |
| 05 | entrega tarea 05 | reto-u05 |
| 06 | entrega tarea 06 | reto-u06 |

para la solucion de la unidad 5, cambia a main y abre entrega tarea 05. para su reto:

```bash
git switch reto-u05
cd 'entrega tarea 05'
./mvnw clean verify
```

el codigo del reto se integra directamente en la carpeta de la entrega dentro de su rama. no hay carpetas reto, retos ni documentos en los proyectos. la siguiente unidad de main conserva la solucion guiada comun.

configura JAVA_HOME con jdk 25; los pom conservan release 21. desde u09 las pruebas necesitan docker o la api compatible de podman; la prueba de concurrencia del reto u08 tambien lo necesita. usa bases separadas al cambiar de unidad o reto para no mezclar migraciones.

los documentos editables siguen fuera del repositorio, en la carpeta principal del curso: Documento tarea NN.docx y .txt. las evidencias de ejecucion estan en cada proyecto. para u12 consulta tambien su readme; para el reto u09 usa mutaciones.py y despues ./mvnw clean verify.

la unidad 5 utiliza el mensaje practica 5; las demas conservan unidad n. los commits de main desde u05 avanzan cinco dias, comenzando el 23/octubre/2026 a las 22:00 en la zona horaria de mexico. en github se publica cada entrega cuando se solicita.
