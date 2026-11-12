from pathlib import Path
import argparse,base64,http.cookiejar,json,subprocess,time,urllib.error,urllib.request,uuid

raiz=Path(__file__).resolve().parent
args=argparse.ArgumentParser()
args.add_argument('--base',default='http://localhost:8080')
args.add_argument('--proyecto',default='curso-u12')
args.add_argument('--propietario',action='store_true')
opciones=args.parse_args()
lineas=[]
def registrar(texto):
    print(texto,flush=True)
    lineas.append(texto)
def compose(*partes):
    return subprocess.run(['docker','compose','-p',opciones.proyecto,*partes],cwd=raiz,check=True,capture_output=True,text=True).stdout
sesiones={}
def peticion(metodo,ruta,datos=None,usuario=None,csrf=False):
    headers={}
    if usuario:
        if usuario not in sesiones:
            jar=http.cookiejar.CookieJar()
            cliente=urllib.request.build_opener(urllib.request.HTTPCookieProcessor(jar))
            sesiones[usuario]=(cliente,None)
        cliente,token=sesiones[usuario]
        headers['Authorization']='Basic '+base64.b64encode((usuario+':'+usuario+'123').encode()).decode()
        if csrf:
            if token is None:
                estado,cab,cuerpo=peticion('GET','/api/v1/csrf',usuario=usuario)
                assert estado==200
                token=cuerpo['token']
                sesiones[usuario]=(cliente,token)
            headers['X-CSRF-TOKEN']=token
    else:cliente=urllib.request.build_opener()
    if datos is not None:
        datos=json.dumps(datos).encode()
        headers['Content-Type']='application/json'
    request=urllib.request.Request(opciones.base+ruta,data=datos,headers=headers,method=metodo)
    try:respuesta=cliente.open(request,timeout=40)
    except urllib.error.HTTPError as error:respuesta=error
    raw=respuesta.read()
    try:cuerpo=json.loads(raw) if raw else None
    except json.JSONDecodeError:cuerpo=raw.decode()
    return respuesta.status,dict(respuesta.headers),cuerpo
def esperar(ruta,estado=200,segundos=90):
    limite=time.monotonic()+segundos
    while time.monotonic()<limite:
        try:
            s,h,b=peticion('GET',ruta)
            if s==estado:return b
        except (OSError,TimeoutError):pass
        time.sleep(1)
    raise AssertionError(ruta)
def exigir(estado,metodo,ruta,**kwargs):
    s,h,b=peticion(metodo,ruta,**kwargs)
    assert s==estado,(ruta,s,b)
    return h,b

try:
    esperar('/actuator/health/readiness')
    matricula='f'+uuid.uuid4().hex[:12]
    datos={'matricula':matricula,'nombre':'ada final','correo':'ada@u.mx'}
    h,b=exigir(201,'POST','/api/v1/alumnos',datos=datos,usuario='docente',csrf=True)
    id=b['id'];ruta='/api/v1/alumnos/'+str(id)
    assert h['Location']==ruta
    registrar(f'exito: 201; location={ruta}; matricula={matricula}')
    exigir(400,'POST','/api/v1/alumnos',datos={'matricula':'','nombre':'a','correo':'mal'},usuario='docente',csrf=True)
    registrar('validacion: 400')
    exigir(404,'GET','/api/v1/alumnos/9223372036854775807',usuario='docente')
    registrar('ausencia: 404')
    exigir(409,'POST','/api/v1/alumnos',datos=datos,usuario='docente',csrf=True)
    registrar('conflicto: 409')
    exigir(401,'GET','/api/v1/alumnos')
    registrar('sin credenciales: 401')
    exigir(403,'POST','/api/v1/alumnos',datos=datos,usuario='docente')
    registrar('docente sin csrf: 403')
    exigir(403,'POST','/api/v1/alumnos',datos=datos,usuario='alumno',csrf=True)
    registrar('alumno con csrf y rol insuficiente: 403')
    exigir(403,'GET','/actuator/metrics',usuario='alumno')
    exigir(200,'GET','/actuator/metrics',usuario='admin')
    registrar('metricas: alumno 403; admin 200')
    exigir(200,'GET','/openapi.yaml')
    registrar('openapi: 200')
    exigir(200,'GET',ruta,usuario='docente')
    exigir(200,'GET',ruta,usuario='docente')
    exigir(200,'PUT',ruta,datos={'nombre':'ada modificada','correo':'ada@u.mx'},usuario='docente',csrf=True)
    h,b=exigir(200,'GET',ruta,usuario='docente');assert b['nombre']=='ada modificada'
    registrar('cache: dos consultas; cambio y lectura actualizada; el test de cache comprueba llamadas al repositorio')
    if opciones.propietario:
        compose('exec','-T','postgres','psql','-U','academico','-d','academico','-v','ON_ERROR_STOP=1','-c',f"insert into cuentas_alumnos(usuario,alumno_id) values ('alumno',{id}) on conflict (usuario) do update set alumno_id=excluded.alumno_id;")
        h,b=exigir(200,'GET','/api/v1/mi-expediente',usuario='alumno');assert b['id']==id
        exigir(403,'GET','/api/v1/expedientes/9223372036854775807',usuario='alumno')
        exigir(200,'GET','/api/v1/expedientes/'+str(id),usuario='admin')
        registrar('propietario: expediente propio 200; ajeno 403; admin 200')
    limite=time.monotonic()+85
    while True:
        s,h,b=peticion('GET','/actuator/metrics/academico.resumen.ejecuciones',usuario='admin')
        if s==200 and b['measurements'][0]['value']>=1:break
        assert time.monotonic()<limite,'sin resumen programado'
        time.sleep(2)
    registrar('resumen programado: contador >= 1')
    compose('restart','app')
    esperar('/actuator/health/readiness');sesiones.clear()
    h,b=exigir(200,'GET',ruta,usuario='docente');assert b['nombre']=='ada modificada'
    registrar('reinicio de app: alumno persistente, consulta 200')
    compose('stop','postgres')
    try:
        b=esperar('/actuator/health/readiness',503)
        assert b['status']=='DOWN'
        registrar('postgres detenido: readiness 503 DOWN')
        b=esperar('/actuator/health/liveness')
        assert b['status']=='UP'
        registrar('postgres detenido: liveness 200 UP')
    finally:compose('start','postgres')
    esperar('/actuator/health/readiness')
    registrar('postgres recuperado: readiness 200 UP')
finally:
    (raiz/'evidencias').mkdir(exist_ok=True)
    (raiz/'evidencias/verificacion-http.txt').write_text('\n'.join(lineas)+'\n')
