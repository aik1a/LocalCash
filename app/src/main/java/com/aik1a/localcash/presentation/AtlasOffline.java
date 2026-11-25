package com.aik1a.localcash.presentation;

import android.content.Context;
import java.io.*;
import java.security.*;
import java.util.zip.ZipFile;
import org.json.*;

public final class AtlasOffline {
    public static final String FUENTE = "LocalCashOSM";
    public static final double SUR=-33.465, OESTE=-70.675, NORTE=-33.435, ESTE=-70.645;
    private AtlasOffline() { }
    public static File archivo(Context context) { return new File(context.getFilesDir(), "maps/santiago_centro.zip"); }
    public static boolean cubre(double latitud,double longitud) {
        return latitud>=SUR && latitud<=NORTE && longitud>=OESTE && longitud<=ESTE;
    }
    public static File preparar(Context context) throws IOException {
        File destino=archivo(context);
        if(!destino.exists()) {
            File parent=destino.getParentFile();
            if(!parent.isDirectory() && !parent.mkdirs()) throw new IOException("No se pudo crear el directorio privado.");
            File temp=new File(parent,"atlas.tmp");
            try {
                try(InputStream in=context.getAssets().open("maps/santiago_centro.zip");OutputStream out=new FileOutputStream(temp)) {
                    byte[] buffer=new byte[16384];int count;
                    while((count=in.read(buffer))!=-1)out.write(buffer,0,count);
                }
                validar(context,temp);
                if(!temp.renameTo(destino))throw new IOException("No se pudo instalar el atlas local.");
            } finally { if(temp.exists())temp.delete(); }
        }
        validar(context,destino);return destino;
    }
    public static void validar(Context context,File archivo) throws IOException {
        try(InputStream in=context.getAssets().open("maps/metadata.json")) {
            ByteArrayOutputStream out=new ByteArrayOutputStream();byte[] buf=new byte[4096];int n;
            while((n=in.read(buf))!=-1)out.write(buf,0,n);
            JSONObject meta=new JSONObject(out.toString("UTF-8"));
            if(!sha256(archivo).equals(meta.getString("zip_sha256")))throw new IOException("El atlas local está dañado.");
            try(ZipFile zip=new ZipFile(archivo)) {
                if(zip.size()!=meta.getInt("tiles"))throw new IOException("Atlas incompleto.");
                java.util.Enumeration<? extends java.util.zip.ZipEntry> entries=zip.entries();
                while(entries.hasMoreElements()) {
                    String name=entries.nextElement().getName();
                    if(!name.matches("LocalCashOSM/(14|15|16)/[0-9]+/[0-9]+\\.png"))throw new IOException("Formato de atlas inválido.");
                }
            }
        } catch(JSONException ex) { throw new IOException("Metadatos de atlas inválidos.",ex); }
    }
    public static String sha256(File archivo) throws IOException {
        try(InputStream in=new FileInputStream(archivo)) {
            MessageDigest digest=MessageDigest.getInstance("SHA-256");byte[] buf=new byte[16384];int n;
            while((n=in.read(buf))!=-1)digest.update(buf,0,n);
            StringBuilder result=new StringBuilder();for(byte b:digest.digest())result.append(String.format(java.util.Locale.ROOT,"%02x",b&255));return result.toString();
        } catch(NoSuchAlgorithmException ex) { throw new IOException("SHA256 no disponible.",ex); }
    }
}


