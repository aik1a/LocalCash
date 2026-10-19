"""Smoke UX real por UIAutomator y sensor, en un emulador sin datos del usuario."""
import importlib.util
import pathlib
import time

spec=importlib.util.spec_from_file_location('bio',pathlib.Path(__file__).with_name('biometric-emulator.py'))
b=importlib.util.module_from_spec(spec);spec.loader.exec_module(b)

def scroll(tree):
    import re
    node=next((n for n in tree.iter('node') if n.get('scrollable')=='true'),None)
    if node is None:return False
    x1,y1,x2,y2=map(int,re.findall(r'\d+',node.get('bounds')))
    x=(x1+x2)//2;start=y1+(y2-y1)*4//5;end=y1+(y2-y1)//4
    b.adb('shell','input','swipe',str(x),str(start),str(x),str(end),'300')
    return True

def tap(resource=None,text=None):
    for attempt in range(6):
        tree=b.dump('currency-before-tap')
        if b.tap(tree,resource=resource,text=text):return
        if not scroll(tree):break
    raise AssertionError(resource or text)

def text(resource,value):
    import re
    tree=b.wait(lambda t:any(n.get('resource-id','').endswith(':id/'+resource) for n in t.iter('node')),'currency-input')
    node=next(n for n in tree.iter('node') if n.get('resource-id','').endswith(':id/'+resource))
    x1,y1,x2,y2=map(int,re.findall(r'\d+',node.get('bounds')))
    assert x2>x1 and y2>y1,resource
    b.adb('shell','input','tap',str((x1+x2)//2),str((y1+y2)//2))
    # ACTION_SET_TEXT por Accessibility se verifica en native runner; aquí usa teclado real.
    b.adb('shell','input','keyevent','KEYCODE_MOVE_END')
    b.adb('shell','input','keyevent',*['67']*80)
    b.adb('shell','input','text',value)
    b.adb('shell','input','keyevent','4')

def visible(value):return b.wait(lambda t:b.has(t,value),'currency-'+value.replace(' ','-'))

def fresh_unlock():
    b.launch()
    b.wait(lambda t:b.has(t,'Desbloquear LocalCash'),'currency-biometric')
    b.adb('emu','finger','touch','1')
    return b.wait(lambda t:b.has(t,'Tu moneda principal'),'currency-first-run')

b.adb('shell','pm','clear',b.package)
fresh_unlock()
tap(text='Guardar')
b.wait(lambda t:b.private(t),'currency-main')
# Main preference CLP, integer field and integer display.
tap(resource='crear_cuenta');text('nombre','Efectivo');text('saldo_inicial','100');tap(text='Guardar')
visible('CLP 100')
# Independent USD account, selected explicitly.
tap(resource='crear_cuenta');text('nombre','Dolares');tap(resource='divisa');tap(text='Dólar estadounidense (USD)')
text('saldo_inicial','25.50');tap(text='Guardar');visible('USD 25,50')
# Exactly one contact.
tap(resource='abrir_contactos');tap(resource='crear_contacto');text('nombre_contacto','Valentina');tap(text='Guardar');visible('Valentina');tap(resource='volver')
tap(resource='abrir_compras');tap(resource='crear_compra');text('nombre_compra','Almuerzo')
tap(resource='estado_compra');tap(text='Realizada')
tap(resource='contacto_compra')
tree=visible('Valentina');assert '(#' not in b.texts(tree);tap(text='Valentina')
# Scroll to product inputs.
scroll(b.dump('currency-product-scroll'))
text('item_descripcion','Pan');text('item_precio','100');tap(resource='agregar_item')
# Both labels remain concise. No leaked validation requirements or database IDs.
tree=b.dump('currency-clean-labels');assert 'hasta 2 decimales' not in b.texts(tree) and 'entera positiva' not in b.texts(tree)
tap(resource='guardar_compra');visible('Pagar compra');tap(text='Confirmar pago');visible('Contacto: Valentina');visible('Total: CLP 100');tap(resource='abrir_reparto')
visible('Tú');visible('Valentina');tap(resource='guardar_reparto')
visible('Tú: CLP 50');visible('Valentina: CLP 50')
# Real process restart: preference and per-purchase allocations survive.
b.launch();tree=b.unlock();assert not b.has(tree,'Tu moneda principal')
tap(resource='abrir_compras');tap(text='Almuerzo');tap(resource='abrir_reparto')
visible('Tú: CLP 50');visible('Valentina: CLP 50')
(b.folder/'currency-ui.log').write_text('PASS: onboarding CLP, cuenta USD independiente, nombres sin IDs, compra CLP y reparto Tú + un contacto, persistencia tras force-stop.\n')
print('LOCALCASH_CURRENCY_UI_PASS',flush=True)
