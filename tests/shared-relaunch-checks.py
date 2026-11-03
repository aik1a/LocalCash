"""Verifica las deudas desde la UI después de force-stop, sin resembrar datos."""
import pathlib
import re
import subprocess
import xml.etree.ElementTree as ET
import importlib

ui = importlib.import_module('biometric-emulator')

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
    if resource == 'volver':
        for attempt in range(7):
            if ui.tap(tree, resource=resource): return
            node=next(n for n in tree.iter('node') if n.get('scrollable')=='true')
            x1,y1,x2,y2=map(int,re.findall(r'\d+',node.get('bounds')))
            adb('shell','input','swipe',str((x1+x2)//2),str(y1+(y2-y1)//5),
                str((x1+x2)//2),str(y1+(y2-y1)*4//5),'400')
            tree=screen('shared-scroll-back')
        raise AssertionError('Volver no visible tras desplazar')
    assert ui.tap_revealed(tree, resource=resource, text=text), (resource, text)

main = screen('shared-restarted-main')
contains(main, 'Saldo: USD 125,50')
tap(main,resource='abrir_compras')
for nombre, montos in [('Compartida exacta',['25,00','25,00','25,00']),
                      ('Compartida personalizada',['20,00','30,00','50,00'])]:
    compras = screen('shared-restarted-purchases')
    tap(compras,text=nombre)
    detalle = screen('shared-restarted-detail')
    tap(detalle,resource='abrir_reparto')
    for id,(contacto,monto) in enumerate(zip(['Ana','Luis','Marta'],montos),1):
        deudas = ui.wait(lambda t: any(f'{contacto}: USD {monto} · Pendiente' in n.get('text', '')
                         for n in t.iter('node')), 'shared-restarted-'+contacto, scroll=True)
        contains(deudas,f'{contacto}: USD {monto} · Pendiente')
    assert not any(n.get('resource-id')=='com.aik1a.localcash:id/guardar_reparto' for n in deudas.iter('node'))
    adb('shell','screencap','-p','/sdcard/shared-restart.png')
    adb('pull','/sdcard/shared-restart.png',str(folder/('restart-equal.png' if 'exacta' in nombre else 'restart-custom.png')))
    tap(deudas,resource='volver')
    detalle=screen('shared-restarted-back-detail')
    tap(detalle,resource='volver')
(folder/'shared-restart-checks.log').write_text('PASS: force-stop y relanzamiento; deudas equitativas/personalizadas y saldo 125,50 conservados. No se ofrece guardar un segundo reparto.\n')
print('LOCALCASH_SHARED_RESTART_PASS')

