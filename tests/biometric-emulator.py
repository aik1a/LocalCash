"""Pruebas mediante ajustes Android, BiometricPrompt y sensor de huella del emulador."""
import os
import pathlib
import re
import signal
import subprocess
import sys
import time
import xml.etree.ElementTree as ET

folder=pathlib.Path('evidence');folder.mkdir(exist_ok=True)
package='com.aik1a.localcash'
sequence=0

def adb(*args,check=True):
    r=subprocess.run(['adb',*args],capture_output=True,text=True,check=check)
    return r.stdout+r.stderr

def dump(label='window'):
    global sequence
    sequence+=1
    adb('shell','uiautomator','dump','/sdcard/bio.xml')
    path=folder/f'bio-{label}-{sequence}.xml'
    adb('pull','/sdcard/bio.xml',str(path))
    return ET.parse(path).getroot()

def texts(tree):
    return ' | '.join(n.get('text','') for n in tree.iter('node'))

def has(tree,value):
    return value.lower() in texts(tree).lower()

def tap(tree,resource=None,text=None,pattern=None):
    for n in tree.iter('node'):
        if (resource and n.get('resource-id','').endswith(':id/'+resource)) or (text and n.get('text','').lower()==text.lower()) or (pattern and re.search(pattern,n.get('text',''),re.I)):
            bounds=list(map(int,re.findall(r'\d+',n.get('bounds',''))))
            if len(bounds)==4:
                x1,y1,x2,y2=bounds
                if x2>x1 and y2>y1:
                    adb('shell','input','tap',str((x1+x2)//2),str((y1+y2)//2));return True
    return False

def wait(predicate,label,seconds=25):
    deadline=time.monotonic()+seconds
    while time.monotonic()<deadline:
        tree=dump(label)
        if predicate(tree):return tree
        time.sleep(.3)
    raise AssertionError(label+': '+texts(tree))

def private(tree):
    return any(n.get('resource-id','').split(':id/')[-1] in {'crear_cuenta','registrar_movimiento','crear_contacto','crear_compra','compra_total','guardar_compra','reparto_total'} for n in tree.iter('node'))

def blocked(tree):
    assert not private(tree),'Contenido privado antes de autenticar: '+texts(tree)

def launch():
    adb('shell','am','force-stop',package)
    adb('shell','am','start','-W','-n',package+'/.presentation.MainActivity')

def unlock():
    deadline=time.monotonic()+25
    while time.monotonic()<deadline:
        tree=dump('unlock')
        if private(tree):return tree
        blocked(tree)
        if has(tree,'Desbloquear LocalCash'):adb('emu','finger','touch','1')
        elif has(tree,'cancelada') or has(tree,'Reintentar autenticación'):tap(tree,resource='autenticar')
        time.sleep(.5)
    raise AssertionError('No desbloqueó mediante huella: '+texts(tree))

def instrument(runner,mode=None,extra=None,file='instrument.log',marker=None):
    if runner=='BiometricChecks': runner='FlowChecks';mode='bio-'+mode
    args=['shell','am','instrument','-w']
    if mode:args+=['-e','mode',mode]
    if extra:args+=['-e','activity',extra]
    args+=[package+'.test/'+package+'.'+runner]
    r=subprocess.run(['adb',*args],capture_output=True,text=True,timeout=240)
    value=r.stdout+r.stderr;(folder/file).write_text(value)
    print(value,flush=True)
    assert r.returncode==0 and (not marker or marker in value),file

def prepare():
    instrument('BiometricChecks','seed',file='bio-seed.log',marker='LOCALCASH_BIO_SEED_PASS')
    launch()
    tree=wait(lambda t:has(t,'No hay biometría registrada'),'none-enrolled')
    blocked(tree)
    (folder/'bio-none-enrolled.log').write_text('PASS: dispositivo sin huella enrolada mantiene contenido bloqueado.\n')

def enroll():
    # Este PIN pertenece a ajustes del emulador; LocalCash nunca lo solicita/acepta.
    output=adb('shell','locksettings','set-pin','1234',check=False)
    (folder/'enrollment-locksettings.log').write_text(output)
    sdk=int(adb('shell','getprop','ro.build.version.sdk').strip())
    if sdk<28:
        output=adb('shell','am','start','-a','android.settings.SECURITY_SETTINGS')
        tree=wait(lambda t:has(t,'Fingerprint'),'security-settings')
        assert tap(tree,pattern=r'^Fingerprints?$'),'Entrada Fingerprint no visible'
    else:
        output=adb('shell','am','start','-a','android.settings.FINGERPRINT_ENROLL',check=False)
        if 'Error' in output or 'SecurityException' in output:
            output=adb('shell','am','start','-a','android.settings.SECURITY_SETTINGS')
            tree=wait(lambda t:has(t,'Fingerprint'),'security-settings')
            assert tap(tree,pattern=r'^Fingerprints?$')
    (folder/'enrollment-launch.log').write_text(output)
    deadline=time.monotonic()+160
    last_edit=None
    while time.monotonic()<deadline:
        tree=dump('enroll')
        content=texts(tree)
        if has(tree,'Fingerprint added') or has(tree,'Your fingerprint is ready') or has(tree,'Fingerprint setup complete'):
            tap(tree,pattern=r'^(Done|Next)$');break
        # Enrolamiento real: repetir contactos del sensor, con levantamiento entre ellos.
        adb('emu','finger','touch','1');adb('emu','finger','remove',check=False)
        edits=[n for n in tree.iter('node') if n.get('class')=='android.widget.EditText' and n.get('enabled')=='true']
        if edits:
            signature=(content,edits[0].get('resource-id',''))
            if signature!=last_edit:
                x1,y1,x2,y2=map(int,re.findall(r'\d+',edits[0].get('bounds')))
                adb('shell','input','tap',str((x1+x2)//2),str((y1+y2)//2))
                adb('shell','input','text','1234')
                fresh=dump('pin-entered')
                if not tap(fresh,pattern=r'^(Next|Continue|OK|Confirm)$'):
                    adb('shell','input','keyevent','66')
                last_edit=signature
        else:
            # Elegir PIN solo cuando el sistema aún no tiene bloqueo configurado.
            if any(n.get('text','').upper()=='PIN' for n in tree.iter('node')):tap(tree,pattern=r'^PIN$')
            elif has(tree,'Secure start-up'):tap(tree,pattern=r'^(No thanks|Do not require.*|Continue)$')
            else:tap(tree,pattern=r'^(Next|Continue|Start|I agree|Agree|More|Done|OK)$')
        time.sleep(.5)
    else:raise AssertionError('No pudo enrolar: '+content)
    adb('shell','input','keyevent','3')
    launch()
    tree=wait(lambda t:has(t,'Desbloquear LocalCash'),'enrolled-prompt')
    blocked(tree)
    unlock()
    (folder/'bio-enrollment.log').write_text('PASS: huella 1 enrolada por ajustes Android; BiometricPrompt real desbloqueó mediante sensor.\n')

def security():
    lines=[]
    launch();tree=wait(lambda t:has(t,'Desbloquear LocalCash'),'cancel-prompt');blocked(tree)
    assert tap(tree,text='Cancelar')
    tree=wait(lambda t:has(t,'Autenticación cancelada'),'cancelled');blocked(tree)
    lines.append('PASS cancelación: sin contenido privado ni fallback.')
    assert tap(tree,resource='autenticar')
    tree=wait(lambda t:has(t,'Desbloquear LocalCash'),'wrong-prompt');blocked(tree)
    adb('emu','finger','touch','2');time.sleep(.8)
    tree=dump('wrong-finger');blocked(tree)
    lines.append('PASS dedo no enrolado: sin acceso.')
    for index in range(5):
        adb('emu','finger','touch','2');time.sleep(1.2)
    tree=wait(lambda t:has(t,'Biometría bloqueada'),'lockout',seconds=15);blocked(tree)
    lines.append('PASS error/bloqueo temporal Android: gate permanece cerrado.')
    time.sleep(35)
    assert tap(dump('retry-lockout'),resource='autenticar')
    tree=unlock();assert has(tree,'Efectivo') and has(tree,'125,50')
    lines.append('PASS éxito: cuentas/saldo visibles tras desafío.')
    # Navegación interna no invalida la sesión autenticada.
    assert tap(tree,text='Efectivo')
    tree=wait(lambda t:has(t,'Saldo: 125,50'),'account');assert private(tree)
    assert tap(tree,resource='volver')
    tree=wait(lambda t:has(t,'Efectivo'),'main-back')
    assert tap(tree,resource='abrir_contactos')
    tree=wait(lambda t:has(t,'Ana') and has(t,'Luis'),'contacts');assert private(tree)
    assert tap(tree,resource='volver')
    tree=wait(lambda t:has(t,'Efectivo'),'main-contacts-back')
    assert tap(tree,resource='abrir_compras')
    tree=wait(lambda t:has(t,'Compra previa'),'purchases');assert private(tree)
    assert tap(tree,text='Compra previa')
    tree=wait(lambda t:has(t,'Total: 100,00'),'purchase-detail');assert private(tree)
    assert tap(tree,resource='abrir_reparto')
    tree=wait(lambda t:has(t,'Ana (#1): 50,00') and has(t,'Luis (#2): 50,00'),'debts');assert private(tree)
    adb('shell','settings','put','system','accelerometer_rotation','0')
    adb('shell','settings','put','system','user_rotation','1');time.sleep(1)
    tree=wait(lambda t:has(t,'Ana (#1): 50,00') and has(t,'Luis (#2): 50,00'),'private-rotation');assert private(tree)
    adb('shell','settings','put','system','user_rotation','0');time.sleep(1)
    lines.append('PASS navegación y rotación de pantalla privada: cuentas/movimientos, contactos, compras/detalles/deudas con sesión válida.')
    adb('shell','input','keyevent','3');time.sleep(1)
    adb('shell','am','start','-W','-n',package+'/.presentation.MainActivity')
    tree=wait(lambda t:has(t,'Desbloquear LocalCash'),'background-return');blocked(tree)
    unlock();lines.append('PASS background: nuevo desafío antes de mostrar datos.')
    launch();tree=wait(lambda t:has(t,'Desbloquear LocalCash'),'process-restart');blocked(tree)
    unlock();lines.append('PASS force-stop: sesión no sobrevive al proceso.')
    launch();tree=wait(lambda t:has(t,'Desbloquear LocalCash'),'rotation-prompt');blocked(tree)
    adb('shell','settings','put','system','accelerometer_rotation','0')
    adb('shell','settings','put','system','user_rotation','1');time.sleep(1)
    tree=wait(lambda t:has(t,'Desbloquear LocalCash'),'rotated-prompt');blocked(tree)
    unlock()
    adb('shell','settings','put','system','user_rotation','0');time.sleep(1)
    lines.append('PASS rotación: conserva desafío SDK y solo desbloquea con sensor.')
    launch();tree=wait(lambda t:has(t,'Desbloquear LocalCash'),'saved-process-prompt');blocked(tree)
    adb('shell','input','keyevent','3');time.sleep(2)
    adb('shell','am','kill',package)
    adb('shell','am','start','-W','-a','android.intent.action.MAIN','-c','android.intent.category.LAUNCHER','-f','0x10200000','-n',package+'/.presentation.MainActivity')
    tree=wait(lambda t:has(t,'Desbloquear LocalCash'),'saved-process-restored');blocked(tree)
    unlock();lines.append('PASS restauración tras matar proceso en background: nuevo desafío, sin bloqueo permanente ni sesión heredada.')
    launch();tree=wait(lambda t:has(t,'Desbloquear LocalCash'),'intent-extra');blocked(tree)
    adb('shell','am','start','-n',package+'/.presentation.MainActivity','--ez','authenticated','true')
    tree=wait(lambda t:has(t,'Desbloquear LocalCash'),'malicious-extra');blocked(tree)
    lines.append('PASS extra authenticated=true ignorado; sin autenticación no muestra datos.')
    for activity in ['MainActivity','CuentaActivity','ContactosActivity','ComprasActivity','NuevaCompraActivity','CompraDetalleActivity','RepartoActivity']:
        instrument('BiometricChecks','direct',activity,file='bio-direct-'+activity+'.log',marker='LOCALCASH_BIO_DIRECT_PASS')
        tree=dump('direct-'+activity);blocked(tree)
        lines.append('PASS intent interno directo protegido: '+activity)
    external=adb('shell','am','start','-n',package+'/.presentation.CuentaActivity',check=False)
    assert 'Permission Denial' in external or 'SecurityException' in external,external
    (folder/'bio-external-intent.log').write_text(external)
    lines.append('PASS Activities internas no exportadas.')
    (folder/'biometric-checks.log').write_text('\n'.join(lines)+'\nLOCALCASH_BIOMETRIC_PASS\n')
    print('\n'.join(lines)+'\nLOCALCASH_BIOMETRIC_PASS',flush=True)

def feed():
    while True:
        adb('emu','finger','touch','1');time.sleep(.7)

def regressions():
    with (folder/'biometric-feed.log').open('w') as output:
        process=subprocess.Popen([sys.executable,__file__,'feed'],stdout=output,stderr=subprocess.STDOUT,start_new_session=True)
    try:
        instrument('FlowChecks',file='android-flow.log',marker='LOCALCASH_FLOW_PASS')
        instrument('FlowChecks','purchases',file='purchase-flow.log',marker='LOCALCASH_PURCHASE_PASS')
        launch();unlock();subprocess.run([sys.executable,'tests/relaunch-checks.py'],check=True)
        instrument('FlowChecks','shared',file='shared-flow.log',marker='LOCALCASH_SHARED_PASS')
        launch();unlock();subprocess.run([sys.executable,'tests/shared-relaunch-checks.py'],check=True)
        subprocess.run([sys.executable,'tests/gps-feed.py','start'],check=True)
        try:instrument('FlowChecks','location',file='location-flow.log',marker='LOCALCASH_LOCATION_PASS')
        finally:subprocess.run([sys.executable,'tests/gps-feed.py','stop'],check=True)
        launch();unlock();subprocess.run([sys.executable,'tests/location-relaunch-checks.py'],check=True)
        print('LOCALCASH_BIOMETRIC_REGRESSION_PASS',flush=True)
    finally:os.killpg(process.pid,signal.SIGTERM)

try:
    {'prepare':prepare,'enroll':enroll,'security':security,'unlock':unlock,'feed':feed,'regressions':regressions}[sys.argv[1]]()
except BaseException:
    if sys.argv[1]!='feed':
        try:(folder/'biometric-final-logcat.txt').write_text(adb('logcat','-d','-t','1000'))
        except Exception:pass
    raise
