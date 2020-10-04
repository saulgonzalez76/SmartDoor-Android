package com.smartdoor.smartdoor;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.smartdoor.smartdoor.ui.ConfigFragment;
import com.smartdoor.smartdoor.ui.home;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.luseen.spacenavigation.SpaceOnLongClickListener;

public class MainActivity extends FragmentActivity {
    SpaceNavigationView spaceNavigationView;
    Bundle recupera;
    Context contexto;
    Fragment fragment;
    FragmentTransaction fragmentTransaction;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contexto = this.getBaseContext();

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS};
        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        verifyStoragePermissions(this);

        if (findViewById(R.id.nav_host_fragment) != null) {
            if (savedInstanceState != null) {
                return;
            }
            recupera = getIntent().getExtras();
            Bundle bundle = new Bundle();
            bundle.putString("estaciones", recupera.getString("estaciones"));
            home fragobj = new home();
            fragobj.setArguments(bundle);
            home fragInfo = new home();
            fragInfo.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.nav_host_fragment, fragInfo).commit();
        }

        spaceNavigationView = (SpaceNavigationView) findViewById(R.id.navSpace);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("INVITAR", R.drawable.ic_invitar));
        spaceNavigationView.addSpaceItem(new SpaceItem("CONFIG", R.drawable.ic_settings));
        spaceNavigationView.setCentreButtonSelectable(true);
        spaceNavigationView.setCentreButtonSelected();
        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                //Toast.makeText(MainActivity.this,"onCentreButtonClick", Toast.LENGTH_SHORT).show();

                Bundle bundle = new Bundle();
                bundle.putString("estaciones", recupera.getString("estaciones"));
                home fragobj = new home();
                fragobj.setArguments(bundle);

                home fragInfo = new home();
                fragInfo.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, fragInfo);
                fragmentTransaction.commit();
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                switch (itemIndex){
                    case 0:
                        Bundle bundle = new Bundle();
                        bundle.putString("id", recupera.getString("id"));
                        bundle.putString("estaciones", recupera.getString("estaciones"));
                        fragment = new envia_invitacion();
                        fragment.setArguments(bundle);
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        break;
                    case 1:
                        fragment = new ConfigFragment();
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        break;
                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                //Toast.makeText(MainActivity.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
            }
        });

        spaceNavigationView.setSpaceOnLongClickListener(new SpaceOnLongClickListener() {
            @Override
            public void onCentreButtonLongClick() {
                //Toast.makeText(MainActivity.this,"onCentreButtonLongClick", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(int itemIndex, String itemName) {
                //Toast.makeText(MainActivity.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
            }
        });



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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        spaceNavigationView.onSaveInstanceState(outState);
    }
}
