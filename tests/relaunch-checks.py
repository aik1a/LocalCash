"""Navegación por bounds observados en XML, después del force-stop de CI."""
import pathlib
import re
import subprocess
import xml.etree.ElementTree as ET

folder = pathlib.Path('evidence')

def adb(*args):
    subprocess.run(['adb', *args], check=True)

def screen(name):
    path = folder / (name + '.xml')
    adb('shell', 'uiautomator', 'dump', '/sdcard/relaunch-check.xml')
    adb('pull', '/sdcard/relaunch-check.xml', str(path))
    return ET.parse(path).getroot()

def tap(tree, resource=None, text=None):
    nodes = [n for n in tree.iter('node') if (resource and n.get('resource-id') == 'com.aik1a.localcash:id/' + resource)
             or (text and n.get('text') == text)]
    assert nodes, (resource, text)
    x1, y1, x2, y2 = map(int, re.findall(r'\d+', nodes[0].get('bounds')))
    assert x2 > x1 and y2 > y1, 'Control fuera de pantalla'
    adb('shell', 'input', 'tap', str((x1+x2)//2), str((y1+y2)//2))

def contains(tree, text):
    assert any(text in n.get('text', '') for n in tree.iter('node')), text

main = screen('restarted-main')
contains(main, 'Saldo: USD 125,50')
tap(main, resource='abrir_compras')
compras = screen('restarted-purchases')
for value in ['Compra realizada', 'Compra planificada', 'REALIZADA', 'PLANIFICADA']:
    contains(compras, value)
tap(compras, text='Compra realizada')
detalle = screen('restarted-purchase-detail')
contains(detalle, 'Total: USD 232,00')
contains(detalle, 'Contacto: Ana')
tap(detalle, resource='volver')
compras = screen('restarted-purchases-back')
tap(compras, resource='volver')
main = screen('restarted-main-back')
tap(main, resource='abrir_contactos')
contactos = screen('restarted-contacts')
contains(contactos, 'Ana'); contains(contactos, 'Luis')
(folder/'restart-checks.log').write_text('PASS: force-stop + relanzamiento, saldo previo, dos compras/estados, detalle/total/contacto y dos contactos conservados.\n')
print('LOCALCASH_RESTART_PASS')

