"""Verifica las deudas desde la UI después de force-stop, sin resembrar datos."""
import pathlib
import re
import subprocess
import xml.etree.ElementTree as ET

folder = pathlib.Path('evidence')
def adb(*args):
    subprocess.run(['adb', *args], check=True)
def screen(name):
    path = folder / (name + '.xml')
    adb('shell', 'uiautomator', 'dump', '/sdcard/shared-restart.xml')
    adb('pull', '/sdcard/shared-restart.xml', str(path))
    return ET.parse(path).getroot()
def contains(tree, text):
    assert any(text in n.get('text', '') for n in tree.iter('node')), text
def tap(tree, resource=None, text=None):
    nodes = [n for n in tree.iter('node') if
             (resource and n.get('resource-id') == 'com.aik1a.localcash:id/' + resource) or
             (text and n.get('text') == text)]
    assert nodes, (resource, text)
    x1,y1,x2,y2 = map(int,re.findall(r'\d+',nodes[0].get('bounds')))
    assert x2>x1 and y2>y1
    adb('shell','input','tap',str((x1+x2)//2),str((y1+y2)//2))

main = screen('shared-restarted-main')
contains(main, 'Saldo: 125,50')
tap(main,resource='abrir_compras')
for nombre, montos in [('Compartida exacta',['33,34','33,34','33,33']),
                      ('Compartida personalizada',['20,00','30,00','50,00'])]:
    compras = screen('shared-restarted-purchases')
    tap(compras,text=nombre)
    detalle = screen('shared-restarted-detail')
    tap(detalle,resource='abrir_reparto')
    deudas = screen('shared-restarted-' + ('equal' if 'exacta' in nombre else 'custom'))
    for id,(contacto,monto) in enumerate(zip(['Ana','Luis','Marta'],montos),1):
        contains(deudas,f'{contacto} (#{id}): {monto} · PENDIENTE')
    assert not any(n.get('resource-id')=='com.aik1a.localcash:id/guardar_reparto' for n in deudas.iter('node'))
    adb('shell','screencap','-p','/sdcard/shared-restart.png')
    adb('pull','/sdcard/shared-restart.png',str(folder/('restart-equal.png' if 'exacta' in nombre else 'restart-custom.png')))
    tap(deudas,resource='volver')
    detalle=screen('shared-restarted-back-detail')
    tap(detalle,resource='volver')
(folder/'shared-restart-checks.log').write_text('PASS: force-stop y relanzamiento; deudas equitativas/personalizadas y saldo 125,50 conservados. No se ofrece guardar un segundo reparto.\n')
print('LOCALCASH_SHARED_RESTART_PASS')
