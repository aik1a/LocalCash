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
    deadline=time.monotonic()+25
    scrolls=0
    while time.monotonic()<deadline:
        tree=b.dump('payments-before-tap')
        if b.tap(tree,resource=resource,text=text):return
        # Navegar entre Activities es asíncrono: no fallar por un snapshot de la anterior.
        if scrolls<6 and scroll(tree):scrolls+=1
        time.sleep(.3)
    raise AssertionError((resource or text)+': '+b.texts(tree))

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

# Aislar de currency-ui: mismo emulador descartable y huella ya registrada.
b.adb('shell','pm','clear',b.package)
fresh_unlock();tap(text='Guardar');b.wait(lambda t:b.private(t),'payments-main')
tap(resource='crear_cuenta');text('nombre','Efectivo');text('saldo_inicial','100');tap(text='Guardar');visible('CLP 100')
# Cuenta con otra moneda nunca debe aparecer entre las opciones de pago CLP.
tap(resource='crear_cuenta');text('nombre','Dolares');tap(resource='divisa');tap(text='Dólar estadounidense (USD)')
text('saldo_inicial','999');tap(text='Guardar')
tap(resource='abrir_compras');tap(resource='crear_compra');text('nombre_compra','Mercado')
scroll(b.dump('payments-item-scroll'));text('item_descripcion','Pan');text('item_precio','150');tap(resource='agregar_item')
tap(resource='guardar_compra');visible('Planificada');visible('Total: CLP 150')
tap(resource='volver');tap(resource='volver');tap(text='Efectivo');visible('Saldo: CLP 100');tap(resource='volver')
tap(resource='abrir_compras');tap(text='Mercado');tap(resource='realizar_compra');visible('Pagar compra')
tree=visible('Total a pagar: CLP 150');assert 'Dolares' not in b.texts(tree) and 'USD' not in b.texts(tree)
tap(text='Confirmar pago');visible('No hay dinero suficiente en esta cuenta.');tap(text='Cancelar');visible('Planificada')
# Editar el ítem planificado y pagar con fondos exactos.
tap(resource='editar_compra');tap(resource='editar_item');text('item_precio','100');tap(text='Guardar');tap(resource='guardar_compra')
visible('Total: CLP 100');tap(resource='realizar_compra');visible('Saldo disponible: CLP 100');tap(text='Confirmar pago')
visible('Realizada');visible('Pagada desde: Efectivo')
# Reinicio real: pago, estado y saldo siguen vinculados sin segundo cargo.
b.launch();b.unlock();tap(resource='abrir_compras');tap(text='Mercado');visible('Realizada');visible('Pagada desde: Efectivo')
tap(resource='volver');tap(resource='volver');tap(text='Efectivo');visible('Saldo: CLP 0')
tap(resource='registrar_movimiento');tap(text='Egreso');text('monto','1');tap(text='Guardar')
visible('No hay dinero suficiente en esta cuenta.');tap(text='Cancelar');visible('Saldo: CLP 0');tap(resource='volver')
tap(resource='abrir_compras');tap(text='Mercado');tap(resource='eliminar_compra');visible('Se devolverá CLP 100');tap(text='Eliminar')
tap(resource='volver');tap(text='Efectivo');visible('Saldo: CLP 100');tap(resource='volver')
# Crear/borrar contacto desde las acciones visibles y su confirmación.
tap(resource='abrir_contactos');tap(resource='crear_contacto');text('nombre_contacto','Temporal');tap(text='Guardar')
tap(text='Temporal');tap(text='Eliminar contacto');tap(text='Eliminar');visible('No hay contactos.')
(b.folder/'payments-crud-ui.log').write_text('PASS: planificada no descuenta, cuenta misma divisa, rechazo por fondos insuficientes, edición de ítem, confirmación y pago exacto, reinicio, egreso manual rechazado, borrar compra reembolsa, CRUD contacto con confirmación.\n')
print('LOCALCASH_PAYMENTS_UI_PASS',flush=True)
