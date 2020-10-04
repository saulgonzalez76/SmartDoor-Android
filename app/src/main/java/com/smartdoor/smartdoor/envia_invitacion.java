package com.smartdoor.smartdoor;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.smartdoor.smartdoor.ui.DatePickerFragment;

import java.util.Calendar;

public class envia_invitacion extends Fragment {
    Button btnSiguiente;
    RadioGroup grupo;
    EditText txtFecha, txtHora;
    private static final String CERO = "0";
    Spinner cboPuerta;
    String[] arrEstaciones;

    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();

    //Variables para obtener la hora hora
    final int hora = c.get(Calendar.HOUR_OF_DAY);
    final int minuto = c.get(Calendar.MINUTE);
    String id = "", estaciones = "";

    public envia_invitacion() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_envia_invitacion, container, false);
        btnSiguiente = (Button) view.findViewById(R.id.btnInvitaSiguiente);
        grupo = (RadioGroup) view.findViewById(R.id.radInvita);

        txtFecha = (EditText) view.findViewById(R.id.txtInvitaFecha);
        txtHora = (EditText) view.findViewById(R.id.txtInvitaHora);
        cboPuerta = (Spinner) view.findViewById(R.id.cboPuerta);


        txtFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.txtInvitaFecha:
                        showDatePickerDialog();
                        break;
                }
            }
        });

        txtHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.txtInvitaHora:
                        obtenerHora();
                        break;
                }
            }
        });

        Bundle recupera = getArguments();
        if (recupera != null && recupera.containsKey("id")){
            id = recupera.getString("id");
            estaciones = recupera.getString("estaciones");
        } else {
            // ENVIAR A LOGIN, PERDIMOS LA INFO DEL USUARIO
            Intent i;
            i = new Intent(getContext(), LoginActivity.class);
            startActivity(i);
        }

        String[] lstData = new String[recupera.getString("estaciones").split(";").length];
        arrEstaciones = new String[recupera.getString("estaciones").split(";").length];
        for(int i=0;i<lstData.length;i++) {
            lstData[i] = recupera.getString("estaciones").split(";")[i].split(",")[2] + " - " + recupera.getString("estaciones").split(";")[i].split(",")[1];
            arrEstaciones[i] = recupera.getString("estaciones").split(";")[i].split(",")[0];
        }
        cboPuerta.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, lstData));
        cboPuerta.setSelection(0);

        btnSiguiente.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                switch (comprobarModo(v)){
                    case 0:
                        // no selecciono metodo, por defult hacer accion
                        break;
                    case 1:
                        //enviar invitaciones por email
                        break;
                    case 2:
                        if (appInstalledOrNot("com.whatsapp")) {
                            //enviar invitaciones por whats
                            Bundle bundle = new Bundle();
                            bundle.putString("id", id);
                            bundle.putString("idpuerta", arrEstaciones[cboPuerta.getSelectedItemPosition()]);
                            bundle.putString("vigencia", txtFecha.getText() + " " + txtHora.getText());
                            bundle.putString("estaciones", estaciones);
                            Fragment fragment = new whatsapp_info();
                            fragment.setArguments(bundle);
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        } else {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "com.whatsapp")));
                        }
                        break;
                }
            }
        });

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        final String selectedDate = year + "-" + twoDigits((month+1)) + "-" + twoDigits(day);
        final String selectedHora = twoDigits(c.get(Calendar.HOUR_OF_DAY)) + ":" + twoDigits(c.get(Calendar.MINUTE));
        txtFecha.setText(selectedDate);
        txtHora.setText(selectedHora);
        return view;
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getActivity().getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                final String selectedDate = year + "-" + twoDigits((month+1)) + "-" + twoDigits(day);
                //Log.e("fecha",selectedDate);
                txtFecha.setText(selectedDate);
            }
        });

        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    private void obtenerHora(){
        TimePickerDialog recogerHora = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                final String horaSelec = twoDigits(hourOfDay) + ":" + twoDigits(minute);
                txtHora.setText(horaSelec);
            }
        }, hora, minuto, false);

        recogerHora.show();
    }

    private String twoDigits(int n) {
        return (n<=9) ? ("0"+n) : String.valueOf(n);
    }

    public int comprobarModo(View view) {
        int retorno = 0;
        if (grupo.getCheckedRadioButtonId() == R.id.radInvitaMail) {
            retorno = 1;
        }
        if (grupo.getCheckedRadioButtonId() == R.id.radInvitaWhats) {
            retorno = 2;
        }
        return retorno;
    }

}
