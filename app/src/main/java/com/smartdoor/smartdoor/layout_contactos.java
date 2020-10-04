package com.smartdoor.smartdoor;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class layout_contactos extends ArrayAdapter<String> implements Filterable {
    private final Context context;
    private ArrayList<String> values;

    public layout_contactos(Context context, ArrayList<String> values) {
        super(context, R.layout.layout_contactos, values);
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
        View rowView = inflater.inflate(R.layout.layout_contactos, parent, false);
        TextView txtNombre = (TextView) rowView.findViewById(R.id.txtContacto);
        ImageView imgcontacto = (ImageView) rowView.findViewById(R.id.imgContacto);
        txtNombre.setText(values.get(position).split(";")[0]);

        ArrayList<String> listaInvitados = ((variables) context.getApplicationContext()).getListaInvitados();


        for (int i=0;i<listaInvitados.size();i++){
            if (listaInvitados.get(i).split(";")[2].equals(values.get(position).split(";")[2])){
                rowView.setBackgroundColor(context.getResources().getColor(R.color.fondoListaSelec));
                break;
            } else {
                rowView.setBackgroundColor(context.getResources().getColor(R.color.fondoListaDefault));
            }
        }


        InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(values.get(position).split(";")[2])));

        Uri photoId = ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, Long.parseLong(values.get(position).split(";")[2]));
        Uri photoUri = Uri.withAppendedPath(photoId, ContactsContract.RawContacts.DisplayPhoto.CONTENT_DIRECTORY);


        //imgcontacto.setImageURI(photoUri);
        Bitmap mThumbnail =
                loadContactPhotoThumbnail(String.valueOf(photoUri));
        /*
         * Sets the image in the QuickContactBadge
         * QuickContactBadge inherits from ImageView, so
         */
        imgcontacto.setImageBitmap(mThumbnail);


        return rowView;
    }

    private Bitmap loadContactPhotoThumbnail(String photoData) {
        // Creates an asset file descriptor for the thumbnail file.
        AssetFileDescriptor afd = null;
        // try-catch block for file not found
        try {
            // Creates a holder for the URI.
            Uri thumbUri;
            // If Android 3.0 or later
            if (Build.VERSION.SDK_INT
                    >=
                    Build.VERSION_CODES.HONEYCOMB) {
                // Sets the URI from the incoming PHOTO_THUMBNAIL_URI
                thumbUri = Uri.parse(photoData);
            } else {
                // Prior to Android 3.0, constructs a photo Uri using _ID
                /*
                 * Creates a contact URI from the Contacts content URI
                 * incoming photoData (_ID)
                 */
                final Uri contactUri = Uri.withAppendedPath(
                        ContactsContract.Contacts.CONTENT_URI, photoData);
                /*
                 * Creates a photo URI by appending the content URI of
                 * Contacts.Photo.
                 */
                thumbUri =
                        Uri.withAppendedPath(
                                contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
            }

            /*
             * Retrieves an AssetFileDescriptor object for the thumbnail
             * URI
             * using ContentResolver.openAssetFileDescriptor
             */
            afd = getContext().getContentResolver().
                    openAssetFileDescriptor(thumbUri, "r");
            /*
             * Gets a file descriptor from the asset file descriptor.
             * This object can be used across processes.
             */
            FileDescriptor fileDescriptor = afd.getFileDescriptor();
            // Decode the photo file and return the result as a Bitmap
            // If the file descriptor is valid
            if (fileDescriptor != null) {
                // Decodes the bitmap
                return BitmapFactory.decodeFileDescriptor(
                        fileDescriptor, null, null);
            }
            // If the file isn't found
        } catch (FileNotFoundException e) {
            /*
             * Handle file not found errors
             */
            // In all cases, close the asset file descriptor
        } finally {
            if (afd != null) {
                try {
                    afd.close();
                } catch (IOException e) {}
            }
        }
        return null;
    }


}
