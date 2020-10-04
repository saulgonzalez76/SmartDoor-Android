package com.smartdoor.smartdoor;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

import static android.content.Context.WINDOW_SERVICE;

public class layout_fotos extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;
    String nombrePuerta = "", codigoQR = "", nombreHome = "";
    WindowManager manager;
    Display display;
    Point point = new Point();
    Bitmap bitmap;
    ImageView qrImage, arrowUP, arrowDown;
    TextView txtpuerta, txtNombreEst;
    QRGEncoder qrgEncoder;

    public layout_fotos(Context context, String[] values) {
        super(context, R.layout.layout_fotos, values);
        this.context = context;
        this.values = values;
    }

    public static Activity getActivity(Context context) {
        if (context == null) return null;
        if (context instanceof Activity) return (Activity) context;
        if (context instanceof ContextWrapper) return getActivity(((ContextWrapper)context).getBaseContext());
        return null;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.layout_fotos, parent, false);
        manager = (WindowManager)  getActivity(context).getSystemService(WINDOW_SERVICE);
        display = manager.getDefaultDisplay();
        txtpuerta = (TextView) rowView.findViewById(R.id.txtNombrePuerta);
        txtNombreEst = (TextView) rowView.findViewById(R.id.txtNombreEstacion);
        qrImage = (ImageView) rowView.findViewById(R.id.imgQRPuerta);
        arrowUP = (ImageView) rowView.findViewById(R.id.imgArrowUP);
        arrowDown = (ImageView) rowView.findViewById(R.id.imgArrowDOWN);

        if (values.length > 0){
            if (position == 0) {
                arrowDown.setImageResource(R.drawable.ic_down);
            } else if (position == (values.length-1)) {
                arrowUP.setImageResource(R.drawable.ic_up);
            } else {
                arrowUP.setImageResource(R.drawable.ic_up);
                arrowDown.setImageResource(R.drawable.ic_down);
            }
            nombrePuerta = values[position].split(",")[1];
            codigoQR = values[position].split(",")[0];
            nombreHome = values[position].split(",")[2];
        }

        display.getSize(point);
        int smallerDimension = point.y;
        smallerDimension = smallerDimension * 1 / 2;
        qrgEncoder = new QRGEncoder(codigoQR, null, QRGContents.Type.TEXT, smallerDimension);
        try {
            bitmap = qrgEncoder.encodeAsBitmap();
            qrImage.setImageBitmap(bitmap);
            txtpuerta.setText(nombrePuerta);
            txtNombreEst.setText(nombreHome);
        } catch (WriterException e) {
            Log.e("generando qr", e.toString());
        }
        return rowView;
    }



}
