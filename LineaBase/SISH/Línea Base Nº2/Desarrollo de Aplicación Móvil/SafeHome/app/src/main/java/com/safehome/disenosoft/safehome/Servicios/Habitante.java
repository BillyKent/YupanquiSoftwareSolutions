package com.safehome.disenosoft.safehome.Servicios;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class Habitante implements Serializable {
    private String _id;
    private String nombres;
    private String apellidos;
    private int tipoCuenta;
    private boolean primeraVez;
    private String pin;
    private String fotoBase64;

    public Habitante(String _id, String nombres, String apellidos, int tipoCuenta, boolean primeraVez, String pin, String fotoBase64) {
        this._id = _id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.tipoCuenta = tipoCuenta;
        this.primeraVez = primeraVez;
        this.pin = pin;
        this.fotoBase64 = fotoBase64;
    }
    public Habitante(String _id, String nombres, String apellidos, int tipoCuenta, boolean primeraVez, String pin, Bitmap foto) {
        this._id = _id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.tipoCuenta = tipoCuenta;
        this.primeraVez = primeraVez;
        this.pin = pin;
        setFoto(foto);
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public int getTipoCuenta() {
        return tipoCuenta;
    }


    public void setTipoCuenta(int tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public boolean isPrimeraVez() {
        return primeraVez;
    }

    public void setPrimeraVez(boolean primeraVez) {
        this.primeraVez = primeraVez;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin){
        this.pin = pin;
    }

    public String getId(){
        return  _id;
    }

    public Bitmap getFoto() {

        byte[] bytes = Base64.decode(
                fotoBase64.substring(fotoBase64.indexOf(",")  + 1),
                Base64.DEFAULT
        );

        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public String getFotoBase64(){
        return fotoBase64;
    }

    public void setFoto(Bitmap foto) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        foto.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        fotoBase64 = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
    }
    public void setFotoBase64(String base64){
        fotoBase64 = base64;
    }
}
