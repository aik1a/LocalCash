"""Consulta el historial persistido después de force-stop; no reinyecta datos."""
import pathlib
import re
import subprocess
import xml.etree.ElementTree as ET
import importlib
ui = importlib.import_module('biometric-emulator')
folder = pathlib.Path('evidence')
def adb(*args):
    subprocess.run(['adb',*args],check=True)
def screen(name):
    path=folder/(name+'.xml')
    adb('shell','uiautomator','dump','/sdcard/location.xml')
    adb('pull','/sdcard/location.xml',str(path))
    return ET.parse(path).getroot()
def contains(tree,text):
    return any(text in n.get('text','') for n in tree.iter('node'))
def tap(tree,text=None,resource=None):
    node=next(n for n in tree.iter('node') if (text and n.get('text')==text) or (resource and n.get('resource-id')=='com.aik1a.localcash:id/'+resource))
    x1,y1,x2,y2=map(int,re.findall(r'\d+',node.get('bounds')))
    assert x2>x1 and y2>y1
    adb('shell','input','tap',str((x1+x2)//2),str((y1+y2)//2))
coordinates=next(line.split(': ',1)[1] for line in (folder/'location-flow.log').read_text().splitlines() if line.startswith('GPS_EXPECTED: '))
main=screen('location-restarted-main')
assert contains(main,'Saldo: USD 131,50')
tap(main,text='Efectivo')
seen=set()
for index in range(5):
    tree=screen('location-restarted-history-'+str(index))
    history=next(n for n in tree.iter('node') if n.get('resource-id')=='com.aik1a.localcash:id/movimientos')
    for row in history:
        value=' | '.join(n.get('text','') for n in row.iter('node'))
        for label in ['GPS simulado','Sin consentimiento','Permiso denegado','Consentimiento retirado','GPS desactivado','Sin fix']:
            if label in value:
                assert (('Ubicación: '+coordinates) in value) if label=='GPS simulado' else ('Sin ubicación' in value)
                seen.add(label)
    adb('shell','screencap','-p','/sdcard/location-restart.png')
    adb('pull','/sdcard/location-restart.png',str(folder/('location-restarted-'+str(index)+'.png')))
    ui.scroll_forward(tree)
assert len(seen)==6,seen
(folder/'location-restart-checks.log').write_text('PASS: force-stop y relanzamiento; historial con un par geográfico, cinco nuevos sin ubicación y saldo 131,50 conservados.\n')
print('LOCALCASH_LOCATION_RESTART_PASS')

