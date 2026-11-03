"""Comprueba el DDL Java real y la migración aditiva v5→v6 sobre SQLite del host."""
import base64, pathlib, sqlite3, subprocess, tempfile

root = pathlib.Path(__file__).resolve().parents[1]
with __import__("contextlib").nullcontext(root/"verification"/"sqlite-payment") as temp:
    temp = pathlib.Path(temp); temp.mkdir(parents=True, exist_ok=True)
    sources = {
        'android/content/Context.java': 'package android.content; public class Context { public Context getApplicationContext(){return this;} }',
        'android/database/sqlite/SQLiteOpenHelper.java': 'package android.database.sqlite; public abstract class SQLiteOpenHelper { public SQLiteOpenHelper(android.content.Context c,String n,Object f,int v){} public void onConfigure(SQLiteDatabase d){} public abstract void onCreate(SQLiteDatabase d); public abstract void onUpgrade(SQLiteDatabase d,int a,int b); }',
        'android/database/sqlite/SQLiteDatabase.java': 'package android.database.sqlite; public class SQLiteDatabase { public void setForeignKeyConstraintsEnabled(boolean v){} public void execSQL(String s){System.out.println(java.util.Base64.getEncoder().encodeToString(s.getBytes(java.nio.charset.StandardCharsets.UTF_8)));} }',
        'Emit.java': 'import com.aik1a.localcash.data.LocalCashDbHelper; public class Emit { public static void main(String[] a) { LocalCashDbHelper h=new LocalCashDbHelper(new android.content.Context()); android.database.sqlite.SQLiteDatabase d=new android.database.sqlite.SQLiteDatabase(); if(a.length==0) h.onCreate(d); else h.onUpgrade(d,5,6); } }'
    }
    paths = []
    for name, content in sources.items():
        path = temp/name; path.parent.mkdir(parents=True, exist_ok=True); path.write_text(content)
        paths.append(str(path))
    subprocess.run(['java', '-m', 'jdk.compiler/com.sun.tools.javac.Main', '-d', str(temp), *paths,
                    str(root/'app/src/main/java/com/aik1a/localcash/data/LocalCashDbHelper.java')], check=True)
    def ddl(*args):
        return [base64.b64decode(line).decode() for line in subprocess.check_output(
            ['java', '-cp', str(temp), 'Emit', *args], text=True).splitlines()]
    fresh, migration = ddl(), ddl('upgrade')
    assert len(migration) == 1 and fresh[-1] == migration[0]
    db = sqlite3.connect(':memory:')
    db.execute('PRAGMA foreign_keys=ON')
    for sql in fresh[:-1]: db.execute(sql)
    db.execute("INSERT INTO cuentas VALUES(1,'Efectivo',100000,'CLP')")
    db.execute("INSERT INTO compras VALUES(1,'Pan','PLANIFICADA',1,NULL,'CLP')")
    db.execute("INSERT INTO detalle_compra VALUES(1,1,'Pan',1,30000)")
    before = list(db.execute('SELECT * FROM compras'))
    db.commit()
    for sql in migration: db.execute(sql)
    assert before == list(db.execute('SELECT * FROM compras'))
    assert db.execute('SELECT count(*) FROM compra_movimiento').fetchone()[0] == 0
    assert db.execute('SELECT count(*) FROM movimientos').fetchone()[0] == 0
    with db:
        db.execute("INSERT INTO movimientos(id,cuenta_id,tipo,monto,descripcion,fecha) VALUES(1,1,'EGRESO',30000,'Compra: Pan',1)")
        db.execute('INSERT INTO compra_movimiento VALUES(1,1)')
        db.execute("UPDATE compras SET estado='REALIZADA' WHERE id=1")
    def reject(sql):
        try:
            with db: db.execute(sql)
        except sqlite3.IntegrityError: return
        raise AssertionError(sql)
    reject('INSERT INTO compra_movimiento VALUES(1,1)')
    reject('INSERT INTO compra_movimiento VALUES(99,1)')
    reject('DELETE FROM movimientos WHERE id=1')
    try:
        with db:
            db.execute("INSERT INTO movimientos(id,cuenta_id,tipo,monto,fecha) VALUES(2,1,'EGRESO',30000,1)")
            db.execute('INSERT INTO compra_movimiento VALUES(1,2)')
    except sqlite3.IntegrityError: pass
    else: raise AssertionError('Duplicate payment accepted')
    assert db.execute('SELECT count(*) FROM movimientos').fetchone()[0] == 1
    assert db.execute("SELECT saldo_inicial-(SELECT SUM(monto) FROM movimientos) FROM cuentas WHERE id=1").fetchone()[0] == 70000
    assert db.execute('SELECT estado FROM compras').fetchone()[0] == 'REALIZADA'
    assert not list(db.execute('PRAGMA foreign_key_check'))
    print('PASS: DDL real v5→v6, datos previos intactos, sin cargos retroactivos, vínculo único, FK, rollback y saldo.')
