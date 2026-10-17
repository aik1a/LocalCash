"""Primera apertura offline con sensor real, luego reentrada/zoom/rotación/reapertura."""
import importlib.util,os,pathlib,signal,subprocess,sys,time
spec=importlib.util.spec_from_file_location('bio','tests/biometric-emulator.py');bio=importlib.util.module_from_spec(spec);spec.loader.exec_module(bio)
e=pathlib.Path('evidence');e.mkdir(exist_ok=True)
for args in [('shell','svc','wifi','disable'),('shell','svc','data','disable')]:bio.adb(*args)
time.sleep(2)
(e/'network-before-first-map.txt').write_text(bio.adb('shell','dumpsys','connectivity'))
(e/'network-settings.txt').write_text('wifi_on='+bio.adb('shell','settings','get','global','wifi_on')+'mobile_data='+bio.adb('shell','settings','get','global','mobile_data'))
assert bio.adb('shell','run-as',bio.package,'test','-f','files/maps/santiago_centro.zip',check=False).strip()==''
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
tree=bio.wait(lambda t:any(n.get('resource-id','').endswith(':id/map') for n in t.iter('node')),'maps-reopened');assert bio.has(tree,'fuera de cobertura')
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
bio.adb('shell','svc','wifi','enable');bio.adb('shell','svc','data','enable')
