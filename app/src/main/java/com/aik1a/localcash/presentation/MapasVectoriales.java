package com.aik1a.localcash.presentation;

import android.content.Context;
import android.net.Uri;
import android.os.StatFs;
import org.mapsforge.map.reader.MapFile;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;

/** Local, private map files. This class never changes financial data or uses network APIs. */
public final class MapasVectoriales {
    private MapasVectoriales() { }
    private static File directorio(Context context) { return new File(context.getFilesDir(), "maps/vector"); }
    public static File archivo(Context context, String nombre) { return new File(directorio(context), nombre); }
    public static synchronized void preparar(Context context) throws IOException {
        File dir = directorio(context);
        if (!dir.isDirectory() && !dir.mkdirs()) throw new IOException("No se pudo crear el directorio de mapas.");
        for (String nombre : new String[]{"world.map", "chile.map"}) {
            File destino = archivo(context, nombre);
            String esperado;
            try (InputStream in = context.getAssets().open("maps/vector/" + nombre + ".sha256")) {
                esperado = new String(leer(in), StandardCharsets.US_ASCII).trim();
            }
            File sello = archivo(context, nombre + ".sha256");
            String instalado = "";
            if (sello.isFile()) try (InputStream in = new FileInputStream(sello)) { instalado = new String(leer(in), StandardCharsets.US_ASCII).trim(); }
            if (destino.isFile() && destino.length() > 1000 && esperado.equals(instalado)) continue;
            File temp = archivo(context, nombre + ".part");
            try {
                MessageDigest hash = sha();
                try (InputStream in = context.getAssets().open("maps/vector/" + nombre); FileOutputStream out = new FileOutputStream(temp)) {
                    byte[] buffer = new byte[65536]; int n;
                    while ((n = in.read(buffer)) != -1) { out.write(buffer, 0, n); hash.update(buffer, 0, n); }
                    out.getFD().sync();
                }
                if (!esperado.equals(hex(hash.digest()))) throw new IOException("El mapa incluido está dañado: " + nombre);
                validar(temp);
                if (!temp.renameTo(destino)) throw new IOException("No se pudo instalar " + nombre);
                try (FileOutputStream out = new FileOutputStream(sello)) { out.write(esperado.getBytes(StandardCharsets.US_ASCII)); }
            } finally { temp.delete(); }
        }
    }
    public static File[] archivos(Context context) {
        File adicional = archivo(context, "importado.map");
        return adicional.isFile() ? new File[]{archivo(context,"world.map"),archivo(context,"chile.map"),adicional}
                : new File[]{archivo(context,"world.map"),archivo(context,"chile.map")};
    }
    public static boolean tieneAdicional(Context context) { return archivo(context,"importado.map").isFile(); }
    public static synchronized void importar(Context context, Uri uri) throws IOException {
        File temp = archivo(context, "importado.part");
        try {
            long limite = Math.min(2_000_000_000L, new StatFs(directorio(context).getPath()).getAvailableBytes() - 20_000_000L);
            long bytes = 0;
            try (InputStream in = context.getContentResolver().openInputStream(uri); FileOutputStream out = new FileOutputStream(temp)) {
                if (in == null) throw new IOException("No se pudo leer el archivo seleccionado.");
                byte[] buffer = new byte[65536]; int n;
                while ((n = in.read(buffer)) != -1) {
                    bytes += n;
                    if (bytes > limite) throw new IOException("No hay espacio suficiente o el mapa supera 2 GB.");
                    out.write(buffer,0,n);
                }
                out.getFD().sync();
            }
            validar(temp);
            if (!temp.renameTo(archivo(context,"importado.map"))) throw new IOException("No se pudo guardar el mapa importado.");
        } finally { temp.delete(); }
    }
    private static void validar(File file) throws IOException {
        MapFile map = null;
        try {
            map = new MapFile(file);
            if (map.boundingBox() == null) throw new IOException("El archivo no contiene cartografía válida.");
        } catch (RuntimeException ex) { throw new IOException("Selecciona un mapa vectorial Mapsforge (.map) válido.", ex); }
        finally { if (map != null) map.close(); }
    }
    private static byte[] leer(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(); byte[] b = new byte[1024]; int n;
        while ((n = in.read(b)) != -1) out.write(b,0,n); return out.toByteArray();
    }
    private static MessageDigest sha() throws IOException {
        try { return MessageDigest.getInstance("SHA-256"); } catch (NoSuchAlgorithmException ex) { throw new IOException(ex); }
    }
    private static String hex(byte[] bytes) {
        StringBuilder out = new StringBuilder(); for (byte b : bytes) out.append(String.format(Locale.ROOT,"%02x",b&255)); return out.toString();
    }
}
