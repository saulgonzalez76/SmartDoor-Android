package com.smartdoor.smartdoor.ui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.smartdoor.smartdoor.R;

public class ConfigFragment extends Fragment {

    public ConfigFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_config, container, false);
        final TextView textView = root.findViewById(R.id.text_config);
        final Button btnFragment = root.findViewById(R.id.btnConfigQR);

        btnFragment.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Fragment fragment = new scan_qr();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.contenedor, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        return root;
    }

}
