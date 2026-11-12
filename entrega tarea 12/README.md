# unidad 12

usa jdk 25 para maven; el pom compila para java 21 sin preview. requiere python 3 y docker con compose. las cuentas son de laboratorio: alumno/alumno123, docente/docente123, admin/admin123.

```bash
export JAVA_HOME=/ruta/al/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw --version
./mvnw clean verify
docker compose -p curso-u12 build
docker compose -p curso-u12 up -d
python3 verificar.py --proyecto curso-u12
docker compose -p curso-u12 down
```

verificar.py obtiene csrf y conserva la cookie por usuario. comprueba respuestas, seguridad, persistencia, metricas y resumen programado. detiene y recupera solo el postgres de este proyecto para comprobar readiness; puede tardar unos minutos. no borra el volumen. requests.http permite repetir peticiones manuales.

para probar el mismo jar con java 21, inicia postgres y ejecuta `/ruta/al/jdk-21/bin/java -jar target/backend-academico-0.0.12-SNAPSHOT.jar` con DB_URL, DB_USER y DB_PASSWORD de esa base. java 21 es el destino del bytecode; el contenedor usa jre 25.

con podman en linux:

```bash
systemctl --user start podman.socket
export DOCKER_HOST="unix://$XDG_RUNTIME_DIR/podman/podman.sock"
export TESTCONTAINERS_RYUK_DISABLED=true
```

las pruebas crean bases temporales con testcontainers. los registros de la ejecucion estan en evidencias. los documentos estan en documentos.

la cache es local para una sola jvm de laboratorio: ahorra lecturas y se invalida despues de confirmar cambios. con varias replicas se debe sustituir por una cache compartida con expiracion o desactivar este administrador. un resultado cacheado no sustituye la comprobacion del propietario.
