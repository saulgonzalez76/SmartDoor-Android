package com.smartdoor.smartdoor;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.smartdoor.smartdoor.ui.home;

import java.net.URLEncoder;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class progress_whatsapp extends Fragment {
    TextView txtStatus;
    ProgressBar progressBar;
    String estaciones = "";
    Button btnRegresar;

    public progress_whatsapp() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress_whatsapp, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progProgressWhats);
        txtStatus = (TextView) view.findViewById(R.id.txtProgressWhats);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        txtStatus.setText("Iniciando ...");
        Bundle recupera = getArguments();
        if (recupera != null && recupera.containsKey("id")){
            estaciones = recupera.getString("estaciones");
        }
        ArrayList<String> listaInvitados = ((variables) getContext().getApplicationContext()).getListaInvitados();
        PackageManager packageManager = getContext().getPackageManager();
        for(int i=0;i<listaInvitados.size();i++){
            txtStatus.setText("Enviando a: " + listaInvitados.get(i).split(";")[0]);
            String mensaje = "Descarga el código de acceso en: http://intellidoor.visitas.intellibasc.com/?t=" + listaInvitados.get(i).split(";")[3] +"&envia=wahtsapp";
            String telefono = listaInvitados.get(i).split(";")[1];
            btnRegresar = (Button) view.findViewById(R.id.btnProgressRegresar);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            try {
                String urlwhats = "https://api.whatsapp.com/send?phone="+ telefono +"&text=" + URLEncoder.encode(mensaje, "UTF-8");
                intent.setPackage("com.whatsapp");
                intent.setData(Uri.parse(urlwhats));
                if (intent.resolveActivity(packageManager) != null) {
                    getContext().startActivity(intent);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            progressBar.setProgress((i/listaInvitados.size()) * 100,true);
        }

        progressBar.setProgress(100);
        txtStatus.setText("Códigos de acceso enviados !");

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("estaciones", estaciones);
                Fragment fragment = new home();
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
    }
}
