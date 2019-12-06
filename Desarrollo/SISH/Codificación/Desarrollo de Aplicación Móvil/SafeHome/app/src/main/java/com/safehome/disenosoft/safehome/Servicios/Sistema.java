package com.safehome.disenosoft.safehome.Servicios;

public class Sistema {
    private boolean cambiosHabitante;
    private  boolean modoAlerta;
    private boolean puertaAbierta;

    public Sistema(boolean cambiosHabitante, boolean modoAlerta, boolean puertaAbierta) {
        this.cambiosHabitante = cambiosHabitante;
        this.modoAlerta = modoAlerta;
        this.puertaAbierta = puertaAbierta;
    }

    public boolean isCambiosHabitante() {
        return cambiosHabitante;
    }

    public void setCambiosHabitante(boolean cambiosHabitante) {
        this.cambiosHabitante = cambiosHabitante;
    }

    public boolean isModoAlerta() {
        return modoAlerta;
    }

    public void setModoAlerta(boolean modoAlerta) {
        this.modoAlerta = modoAlerta;
    }

    public boolean isPuertaAbierta() {
        return puertaAbierta;
    }

    public void setPuertaAbierta(boolean puertaAbierta) {
        this.puertaAbierta = puertaAbierta;
    }
}
