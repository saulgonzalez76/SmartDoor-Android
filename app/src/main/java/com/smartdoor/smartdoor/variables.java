package com.smartdoor.smartdoor;

import android.app.Application;

import java.util.ArrayList;

public class variables extends Application {

    ArrayList<String> listaInvitados = new ArrayList<String>();

    public ArrayList<String> getListaInvitados() {
        return listaInvitados;
    }

    public void setListaInvitados(ArrayList<String> lstLista) {
        this.listaInvitados = lstLista;
    }


}
