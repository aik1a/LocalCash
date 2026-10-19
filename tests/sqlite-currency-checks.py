"""Ejecuta el DDL real de LocalCashDbHelper sobre SQLite del host; no sustituye Android."""
import base64
import pathlib
import sqlite3
import subprocess
import tempfile

root = pathlib.Path(__file__).resolve().parents[1]
with tempfile.TemporaryDirectory() as tmp:
    tmp = pathlib.Path(tmp)
    sources = {
        'android/content/Context.java': 'package android.content; public class Context { public Context getApplicationContext(){return this;} }',
        'android/database/sqlite/SQLiteOpenHelper.java': '''package android.database.sqlite; import android.content.Context;
public abstract class SQLiteOpenHelper {
public SQLiteOpenHelper(Context c,String n,Object f,int v){} public void onConfigure(SQLiteDatabase d){}
public abstract void onCreate(SQLiteDatabase d); public abstract void onUpgrade(SQLiteDatabase d,int a,int b); }''',
        'android/database/sqlite/SQLiteDatabase.java': '''package android.database.sqlite;
public class SQLiteDatabase { public void setForeignKeyConstraintsEnabled(boolean v){}
public void execSQL(String s){System.out.println(java.util.Base64.getEncoder().encodeToString(s.getBytes(java.nio.charset.StandardCharsets.UTF_8)));} }''',
        'Emit.java': '''import com.aik1a.localcash.data.LocalCashDbHelper;
public class Emit { public static void main(String[] a) { LocalCashDbHelper h=new LocalCashDbHelper(new android.content.Context());
android.database.sqlite.SQLiteDatabase d=new android.database.sqlite.SQLiteDatabase();
if(a.length==0) h.onCreate(d); else h.onUpgrade(d,Integer.parseInt(a[0]),5); } }'''
    }
    paths = []
    for name, text in sources.items():
        path = tmp / name; path.parent.mkdir(parents=True, exist_ok=True); path.write_text(text); paths.append(str(path))
    subprocess.run(['java','-m','jdk.compiler/com.sun.tools.javac.Main','-d',str(tmp),*paths,
                    str(root/'app/src/main/java/com/aik1a/localcash/data/LocalCashDbHelper.java')], check=True)
    def sql(*args):
        output = subprocess.check_output(['java','-cp',str(tmp),'Emit',*map(str,args)],text=True)
        return [base64.b64decode(line).decode() for line in output.splitlines()]
    fresh = sql()
    legacy = fresh[:10]  # v4 DDL ends after both optional coordinate columns; first 3/7/8 are v1/v2/v3.
    assert legacy[-1].startswith('ALTER TABLE movimientos ADD COLUMN longitud'), legacy
    checks = 0
    def check(value):
        global checks
        assert value; checks += 1
    def reject(db, statement):
        global checks
        try: db.execute(statement)
        except sqlite3.IntegrityError: checks += 1
        else: raise AssertionError(statement)
    for version, count in [(1,3),(2,7),(3,8),(4,10)]:
        db=sqlite3.connect(':memory:'); db.execute('PRAGMA foreign_keys=ON')
        for statement in legacy[:count]: db.execute(statement)
        db.execute("INSERT INTO cuentas(id,nombre,saldo_inicial) VALUES(1,'Anterior',10025)")
        db.execute("INSERT INTO movimientos(id,cuenta_id,tipo,monto,fecha) VALUES(1,1,'INGRESO',2550,1234)")
        if version >= 2:
            db.execute("INSERT INTO contactos VALUES(1,'Valentina')")
            db.execute("INSERT INTO compras VALUES(1,'Compra','REALIZADA',1234,1)")
            db.execute("INSERT INTO detalle_compra VALUES(1,1,'Pan',1,10000)")
        if version >= 3: db.execute("INSERT INTO deudas VALUES(1,1,5001,'PENDIENTE')")
        if version == 4: db.execute("UPDATE movimientos SET latitud=-33.45,longitud=-70.66")
        tables = [row[0] for row in db.execute("SELECT name FROM sqlite_master WHERE type='table'")]
        before = {t:(list(db.execute(f'SELECT * FROM {t}')), [r[1] for r in db.execute(f'PRAGMA table_info({t})')]) for t in tables}
        db.commit()
        with db:
            for statement in sql(version): db.execute(statement)
        for table,(rows,cols) in before.items(): check(rows == list(db.execute(f"SELECT {','.join(cols)} FROM {table}")))
        check(db.execute('SELECT divisa FROM cuentas').fetchone() == (None,))
        reject(db,"UPDATE cuentas SET divisa='CLP' WHERE id=1")
        check(db.execute('SELECT saldo_inicial FROM cuentas').fetchone() == (10025,))
        db.execute("UPDATE cuentas SET divisa='USD' WHERE id=1")
        reject(db,"UPDATE cuentas SET divisa='CLP' WHERE id=1")
        if version >= 3:
            reject(db,"UPDATE compras SET divisa='CLP' WHERE id=1")
            check(db.execute('SELECT monto FROM deudas').fetchone() == (5001,))
        check(not list(db.execute('PRAGMA foreign_key_check')))
        db.close()
    db=sqlite3.connect(':memory:');db.execute('PRAGMA foreign_keys=ON')
    for statement in fresh: db.execute(statement)
    db.execute("INSERT INTO configuracion VALUES(1,'CLP')")
    reject(db,"INSERT INTO configuracion VALUES(2,'USD')")
    reject(db,"INSERT INTO cuentas(nombre,saldo_inicial,divisa) VALUES('X',0,'XXX')")
    reject(db,"INSERT INTO cuentas(nombre,saldo_inicial,divisa) VALUES('X',1,'CLP')")
    db.execute("INSERT INTO cuentas VALUES(1,'CLP',10000,'CLP')")
    reject(db,"INSERT INTO movimientos(cuenta_id,tipo,monto,fecha) VALUES(1,'INGRESO',1,0)")
    db.execute("INSERT INTO movimientos(cuenta_id,tipo,monto,fecha) VALUES(1,'INGRESO',100,0)")
    db.execute("INSERT INTO contactos VALUES(1,'Valentina')")
    db.execute("INSERT INTO compras VALUES(1,'Compra','REALIZADA',0,1,'CLP')")
    reject(db,"INSERT INTO detalle_compra VALUES(1,1,'Pan',1,1)")
    db.execute("INSERT INTO detalle_compra VALUES(1,1,'Pan',1,10000)")
    reject(db,"INSERT INTO deudas VALUES(1,1,1,'PENDIENTE')")
    reject(db,"INSERT INTO reparto_usuario VALUES(1,1)")
    db.execute("INSERT INTO deudas VALUES(1,1,5000,'PENDIENTE')")
    db.execute("INSERT INTO reparto_usuario VALUES(1,5000)")
    check(db.execute('SELECT monto FROM deudas').fetchone() == (5000,))
    check(db.execute('SELECT monto FROM reparto_usuario').fetchone() == (5000,))
    reject(db,"UPDATE cuentas SET divisa=NULL WHERE id=1")
    reject(db,"UPDATE compras SET divisa='USD' WHERE id=1")
    check(not list(db.execute('PRAGMA foreign_key_check')))
    print(f'PASS: {checks} host SQLite checks; actual Java DDL, fresh install and migrations v1-v4→v5.')
