package com.smartdoor.smartdoor;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.smartdoor.smartdoor.ui.home;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static android.provider.ContactsContract.Directory.DISPLAY_NAME;

public class contactos extends Fragment {
    ListView lstContactos;
    ArrayList<String> myWhatsappContacts = new ArrayList<>();
    EditText txtBuscar;
    ArrayAdapter adaptador;
    ProgressBar progressBar;
    View varView;
    private ArrayList<String> listaInvitados = new ArrayList<String>();
    String strTokens = "";
    String id = "", idpuerta = "", vigencia = "", estaciones = "";
    Button btnCancelar, btnEnvia;


    public contactos() {
        // Required empty public constructor
    }

    public static String[] GetStringArray(ArrayList<String> arr) {
        String str[] = new String[arr.size()];
        for (int j = 0; j < arr.size(); j++) {
            str[j] = arr.get(j);
        }
        return str;
    }

    @Override
    public void onStart() {
        super.onStart();
        myWhatsappContacts = displayWhatsAppContacts( "", myWhatsappContacts);
        adaptador = new layout_contactos(varView.getContext(),myWhatsappContacts);
        adaptador.notifyDataSetChanged();
        lstContactos.setAdapter(adaptador);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contactos, container, false);
        varView=view;
        lstContactos = (ListView) view.findViewById(R.id.lstContactos);
        txtBuscar = (EditText) view.findViewById(R.id.txtContactosBuscar);
        progressBar = (ProgressBar) view.findViewById(R.id.contatosProgress);
        btnCancelar = (Button) view.findViewById(R.id.btnContactoCancelar);
        btnEnvia = (Button) view.findViewById(R.id.btnContactoEnviar);

        Bundle recupera = getArguments();
        if (recupera != null && recupera.containsKey("id")){
            id = recupera.getString("id");
            vigencia = recupera.getString("vigencia");
            idpuerta = recupera.getString("idpuerta");
            estaciones = recupera.getString("estaciones");
        }

        lstContactos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) lstContactos.getItemAtPosition(position);
                if (listaInvitados.indexOf(item) > -1){
                    listaInvitados.remove(listaInvitados.indexOf(item));
                    view.setBackgroundColor(getResources().getColor(R.color.fondoListaDefault));

                } else {
                    listaInvitados.add(item);
                    view.setBackgroundColor(getResources().getColor(R.color.fondoListaSelec));
                }

                ((variables) getActivity().getApplication()).setListaInvitados(listaInvitados);

            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getHome();
            }
        });

        btnEnvia.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getTokens();
            }
        });

        txtBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adaptador = new layout_contactos(varView.getContext(),getContactos(varView, s.toString(),myWhatsappContacts));
                adaptador.notifyDataSetChanged();
                lstContactos.setAdapter(adaptador);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });



        return view;
    }

    private void getHome(){
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

    private void getTokens(){
        URL url = null;
        conexion conn = new conexion();
        conn.myContext = getContext();

        // enviar solo vigenvia y telefono
        String tmpDatos = "";
        for (int i=0;i<listaInvitados.size();i++){
            if (!tmpDatos.equals("")) { tmpDatos += ","; }
            tmpDatos += vigencia + ";" + listaInvitados.get(i).split(";")[1];
        }
        try {
            String strUrl  = null;
            try {
                strUrl = getResources().getString(R.string.strServer) + "android/token_invitados.php?gid=" + id + "&idpuerta=" + idpuerta + "&arrdatos=" + URLEncoder.encode(tmpDatos, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
//            Log.e("strUrl:", strUrl);
            url = new URL(strUrl);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            strTokens = conn.execute(url).get();
            String[] arrTokens = strTokens.split(",");
            for (int i=0;i<arrTokens.length;i++) {
                String telefono = arrTokens[i].split(":")[0];
                for(int x=0;x<listaInvitados.size();x++){
                    if (listaInvitados.get(x).split(";")[1].equals(telefono)) {
                        listaInvitados.set(x,listaInvitados.get(x) + ";" + arrTokens[i].split(":")[1]);
                        break;
                    }
                }
            }

            ((variables) getActivity().getApplication()).setListaInvitados(listaInvitados);
            Bundle bundle = new Bundle();
            bundle.putString("estaciones", estaciones);
            Fragment fragment = new progress_whatsapp();
            fragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> displayWhatsAppContacts( String nombre, ArrayList<String> orgArr) {
        ArrayList<String> tmpArr  = new ArrayList<>();
        final String[] projection = {
                ContactsContract.Data.CONTACT_ID,
                ContactsContract.Data.DISPLAY_NAME,
                ContactsContract.Data.MIMETYPE,
                "account_type",
                ContactsContract.Data.DATA3,
        };

        final String selection = ContactsContract.Data.MIMETYPE + " =? and account_type=?";
        final String[] selectionArgs = {
                "vnd.android.cursor.item/vnd.com.whatsapp.profile",
                "com.whatsapp"
        };
        progressBar.setVisibility(View.VISIBLE);

        if (orgArr.isEmpty()) {
            String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS};
            if (hasPermissions(varView.getContext(), PERMISSIONS)) {
                ContentResolver cr = varView.getContext().getContentResolver();
                Cursor c = cr.query(
                        ContactsContract.Data.CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        ContactsContract.Data.DISPLAY_NAME);

                while (c.moveToNext()) {
                    String id = c.getString(c.getColumnIndex(ContactsContract.Data.CONTACT_ID));
                    String number = c.getString(c.getColumnIndex(ContactsContract.Data.DATA3));
                    String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    number = number.replaceAll("[^0-9]", "");
                    number = number.replaceFirst("^0*(.*)", "$1");
                    if (number.length() == 10) {
                        number = getContext().getString(R.string.default_codigo_pais) + number;
                    }
                    tmpArr.add(name + ";" + number + ";" + id);

                }
                Log.v("WhatsApp", "Total WhatsApp Contacts: " + c.getCount());
                c.close();
            } else {
                Toast.makeText(varView.getContext(), "Necesito permisos para leer tus contactos !", Toast.LENGTH_LONG).show();
            }
        } else {
            for(int i = 0;i<orgArr.size()-1;i++){
                if (orgArr.get(i).toUpperCase().indexOf(nombre.toUpperCase()) > -1){
                    tmpArr.add(orgArr.get(i).toString());
                }
            }
        }
        progressBar.setVisibility(View.GONE);
        return tmpArr;
    }

    private ArrayList<String> getContactos(View view, String nombre, ArrayList<String> orgArr){
        ArrayList<String> tmpArr  = new ArrayList<>();
        if (orgArr.isEmpty()) {
            String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS};
            if (hasPermissions(view.getContext(), PERMISSIONS)) {
                progressBar.setVisibility(View.VISIBLE);
                //This class provides applications access to the content model.
                ContentResolver cr = view.getContext().getContentResolver();
                //RowContacts for filter Account Types
                Cursor contactCursor = cr.query(
                        ContactsContract.RawContacts.CONTENT_URI,
                        new String[]{ContactsContract.RawContacts._ID,
                                ContactsContract.RawContacts.CONTACT_ID},
                        ContactsContract.RawContacts.ACCOUNT_TYPE + "= ?",
                        new String[]{"com.whatsapp"},String.format("%1$s COLLATE NOCASE", DISPLAY_NAME));
                if (contactCursor != null) {
                    if (contactCursor.getCount() > 0) {
                        if (contactCursor.moveToFirst()) {
                            do {
                                //whatsappContactId for get Number,Name,Id ect... from  ContactsContract.CommonDataKinds.Phone
                                String whatsappContactId = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID));

                                if (whatsappContactId != null) {
                                    //Get Data from ContactsContract.CommonDataKinds.Phone of Specific CONTACT_ID
                                    Cursor whatsAppContactCursor = cr.query(
                                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                            new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                                                    ContactsContract.CommonDataKinds.Phone.DATA3,
                                                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                                    ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER
                                            },
                                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                            new String[]{whatsappContactId},null);

                                }
                            } while (contactCursor.moveToNext());
                            contactCursor.close();
                        }
                    }

                }

            } else {
                Toast.makeText(view.getContext(), "Necesito permisos para leer tus contactos !", Toast.LENGTH_LONG).show();
            }
        }   else{
            for(int i = 0;i<orgArr.size()-1;i++){
                if (orgArr.get(i).toUpperCase().indexOf(nombre.toUpperCase()) > -1){
                    tmpArr.add(orgArr.get(i).toString());
                }
            }
        }
        return tmpArr;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

}
