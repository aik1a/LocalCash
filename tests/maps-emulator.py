"""Primera apertura offline con sensor real, luego reentrada/zoom/rotación/reapertura."""
import importlib.util,os,pathlib,re,signal,subprocess,sys,time
spec=importlib.util.spec_from_file_location('bio','tests/biometric-emulator.py');bio=importlib.util.module_from_spec(spec);spec.loader.exec_module(bio)
try:
    e=pathlib.Path('evidence');e.mkdir(exist_ok=True)
    bio.adb('shell','pm','grant',bio.package,'android.permission.ACCESS_COARSE_LOCATION')
    bio.adb('shell','pm','grant',bio.package,'android.permission.ACCESS_FINE_LOCATION')
    bio.adb('shell','appops','set',bio.package,'android:mock_location','allow')
    if int(bio.adb('shell','getprop','ro.build.version.sdk').strip()) < 28:
        bio.adb('shell','settings','put','secure','location_mode','3')
    else:
        bio.adb('shell','cmd','location','set-location-enabled','true')
    network_commands=[]
    for args in [('shell','svc','wifi','disable'),('shell','svc','data','disable')]:
     try:r=subprocess.run(['adb',*args],capture_output=True,text=True,timeout=20)
     except subprocess.TimeoutExpired:
      r=subprocess.CompletedProcess(['adb',*args],124,'','Tiempo de espera del cliente; se exige verificar conectividad real.')
     network_commands.append(repr(args)+' returncode='+str(r.returncode)+' '+r.stdout+' '+r.stderr)
    (e/'network-commands.log').write_text('\n'.join(network_commands))
    time.sleep(2)
    deadline=time.monotonic()+25
    while True:
     connectivity=bio.adb('shell','dumpsys','connectivity')
     default=re.search(r'Active default network:\s*([^\r\n]+)',connectivity)
     if default and default.group(1).strip().lower() in {'none','null','-1'}:break
     if time.monotonic()>deadline:raise AssertionError('Red global aún activa o no verificable: '+connectivity)
     time.sleep(.5)
    (e/'network-before-first-map.txt').write_text(connectivity)
    (e/'network-settings.txt').write_text('wifi_on='+bio.adb('shell','settings','get','global','wifi_on')+'mobile_data='+bio.adb('shell','settings','get','global','mobile_data'))
    # MapChecks exige File.exists()==false antes de abrir el mapa; sin depender de test(1) en Android 7.
    with (e/'maps-fingerprint-feed.log').open('w') as output:feed=subprocess.Popen([sys.executable,'tests/biometric-emulator.py','feed'],stdout=output,stderr=subprocess.STDOUT,start_new_session=True)
    try:
     bio.instrument('FlowChecks','maps',file='maps-flow.log',marker='LOCALCASH_MAPS_PASS')
     # Instrumentation termina el proceso. Extraer solo evidencia de fixtures ya autenticados.
     folder=e/'map-evidence';folder.mkdir(exist_ok=True)
     names=bio.adb('shell','run-as',bio.package,'ls','files/map-evidence').split()
     for name in names:
      with (folder/name).open('wb') as out:subprocess.run(['adb','exec-out','run-as',bio.package,'cat','files/map-evidence/'+name],stdout=out,check=True)
    finally:os.killpg(feed.pid,signal.SIGTERM)
    bio.launch();tree=bio.unlock();assert bio.tap(tree,text='Demo mapa')
    tree=bio.wait(lambda t:any(n.get('resource-id','').endswith(':id/abrir_mapa') for n in t.iter('node')),'maps-account')
    assert bio.tap(tree,resource='abrir_mapa')
    tree=bio.wait(lambda t:any(n.get('resource-id','').endswith(':id/map') for n in t.iter('node')),'maps-reopened');assert not bio.has(tree,'fuera de cobertura')
    bio.adb('shell','input','keyevent','3');time.sleep(1)
    bio.adb('shell','am','start','-W','-a','android.intent.action.MAIN','-c','android.intent.category.LAUNCHER','-f','0x10200000','-n',bio.package+'/.presentation.MainActivity')
    tree=bio.wait(lambda t:bio.has(t,'Desbloquear LocalCash'),'maps-background-gate');bio.blocked(tree);assert bio.tap(tree,text='Cancelar')
    tree=bio.wait(lambda t:bio.has(t,'Autenticación cancelada'),'maps-background-cancel');bio.blocked(tree)
    bio.unlock()
    # Si Android trae la raíz del task, navegar nuevamente; ninguna pantalla salta el gate.
    tree=bio.dump('maps-after-auth')
    if not any(n.get('resource-id','').endswith(':id/map') for n in tree.iter('node')):
     if bio.tap(tree,text='Demo mapa'):tree=bio.dump('maps-account-return')
     assert bio.tap(tree,resource='abrir_mapa')
    bio.wait(lambda t:any(n.get('resource-id','').endswith(':id/map') for n in t.iter('node')),'maps-after-background')
    bio.adb('shell','settings','put','system','accelerometer_rotation','0');bio.adb('shell','settings','put','system','user_rotation','1');time.sleep(1)
    bio.wait(lambda t:any(n.get('resource-id','').endswith(':id/map') for n in t.iter('node')),'maps-rotated')
    bio.adb('shell','settings','put','system','user_rotation','0');time.sleep(1)
    (e/'maps-relaunch-checks.log').write_text('PASS: mapa reabierto sin red, biometría tras background, cancelación bloqueada y reautenticación, rotación/lifecycle.\nLOCALCASH_MAPS_RELAUNCH_PASS\n')
    print('LOCALCASH_MAPS_RELAUNCH_PASS',flush=True)
    restore=[]
    for args in [('shell','svc','wifi','enable'),('shell','svc','data','enable')]:
     try:r=subprocess.run(['adb',*args],capture_output=True,text=True,timeout=20)
     except subprocess.TimeoutExpired:
      r=subprocess.CompletedProcess(['adb',*args],124,'','Tiempo de espera del cliente; se exige verificar conectividad real.')
     restore.append(repr(args)+' returncode='+str(r.returncode)+' '+r.stdout+' '+r.stderr)
    (e/'network-restore.log').write_text('\n'.join(restore))
finally:
    try:
        r=subprocess.run(['adb','logcat','-d','-t','1800'],capture_output=True,text=True,timeout=30)
        pathlib.Path('evidence/maps-final-logcat.txt').write_text(r.stdout+r.stderr)
    except Exception as failure:print('No se pudo guardar logcat:',failure,flush=True)

