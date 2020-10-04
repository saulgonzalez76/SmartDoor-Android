package com.smartdoor.smartdoor;

import android.content.Context;
        import android.os.AsyncTask;

        import java.io.BufferedInputStream;
        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.net.HttpURLConnection;
        import java.net.MalformedURLException;
        import java.net.URL;

/**
 * Created by saul on 13/07/16.
 */
public class conexion extends AsyncTask<URL, Void, String> {
    public Context myContext = null;
    String linea = "";
    int respuesta = 0;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected String doInBackground(URL... urlConexion) {

        StringBuilder resul = null;
        try {
            //Toast.makeText(myContext,urlConexion[0].toString() , Toast.LENGTH_LONG).show();
            HttpURLConnection conexion = (HttpURLConnection)urlConexion[0].openConnection();
            respuesta = conexion.getResponseCode();
            resul = new StringBuilder();
            if (respuesta==HttpURLConnection.HTTP_OK){
                InputStream in=new BufferedInputStream(conexion.getInputStream());
                BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                while ((linea=reader.readLine())!=null) {
                    resul.append(linea);
                }
            }
            conexion.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }  catch (IOException e) {
            e.printStackTrace();
        }

        if (resul != null) {
            if (resul.toString() != null) {
                return resul.toString();
            } else { return ""; }
        } else { return ""; }

    }
}