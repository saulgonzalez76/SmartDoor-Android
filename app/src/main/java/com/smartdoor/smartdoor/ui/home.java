package com.smartdoor.smartdoor.ui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.smartdoor.smartdoor.R;
import com.smartdoor.smartdoor.layout_fotos;

public class home extends Fragment {
    ListView lstImagen;

    public home() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        lstImagen = (ListView) view.findViewById(R.id.lstEstacionesQR);
        Bundle recupera = getArguments();
        if (recupera != null && recupera.containsKey("estaciones")){
            String[] lstMedia = recupera.getString("estaciones").split(";");
            layout_fotos adaptador = new layout_fotos(view.getContext(), lstMedia);
            adaptador.notifyDataSetChanged();
            lstImagen.setAdapter(adaptador);
        }
        return view;
    }
}
