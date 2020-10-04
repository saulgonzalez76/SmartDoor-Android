package com.smartdoor.smartdoor;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.smartdoor.smartdoor.ui.home;

public class whatsapp_info extends Fragment {
    Button btnSiguiente, btnCancelar;
    String id = "", idpuerta = "", vigencia = "", estaciones = "";

    public whatsapp_info() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.whatsapp_info, container, false);
        btnSiguiente = (Button) view.findViewById(R.id.btnInfoWhats);
        btnCancelar = (Button) view.findViewById(R.id.btnInfoCancelar);

        Bundle recupera = getArguments();
        if (recupera != null && recupera.containsKey("id")){
            id = recupera.getString("id");
            vigencia = recupera.getString("vigencia");
            idpuerta = recupera.getString("idpuerta");
            estaciones = recupera.getString("estaciones");
        }


        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!isAccessibilityOn (getActivity(), WhatsappAccessibilityService.class)) {
                    Intent intent = new Intent (Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    getActivity().startActivity (intent);
                }
                Bundle bundle = new Bundle();
                bundle.putString("id", id);
                bundle.putString("idpuerta", idpuerta);
                bundle.putString("vigencia", vigencia);
                bundle.putString("estaciones", estaciones);
                Fragment fragment = new contactos();
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
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

    private boolean isAccessibilityOn (Context context, Class<? extends AccessibilityService> clazz) {
        int accessibilityEnabled = 0;
        final String service = context.getPackageName () + "/" + clazz.getCanonicalName ();
        try {
            accessibilityEnabled = Settings.Secure.getInt (context.getApplicationContext ().getContentResolver (), Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException ignored) {  }

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter (':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString (context.getApplicationContext ().getContentResolver (), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                colonSplitter.setString (settingValue);
                while (colonSplitter.hasNext ()) {
                    String accessibilityService = colonSplitter.next ();

                    if (accessibilityService.equalsIgnoreCase (service)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

}
