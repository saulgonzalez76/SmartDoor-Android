package com.smartdoor.smartdoor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    int RC_SIGN_IN = 0;
    LoginButton loginButton;
    CallbackManager callbackManager;
    SignInButton singInButton;
    GoogleSignInClient mGoogleSignInClient;
    GoogleApiClient apiClient;
    String codigo_estaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        singInButton = findViewById(R.id.btnGoogleLogin);
        singInButton.setSize(SignInButton.SIZE_STANDARD);


        // FACEBOOK LOGIN BUTTON
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.fa_login_button);
        loginButton.setPermissions("public_profile email");
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                RequestData();
            }
            @Override
            public void onCancel() {
                // App code
            }
            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        // GOOGLE LOGIN BUTTON
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .build();

        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        singInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(apiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

    }

    private void syncLogin(String gid,String nombre, String email, String apellido, String foto, String token, int accesso) {
        URL url = null;
        conexion conn = new conexion();
        conn.myContext = this;
        try {
            String strUrl  = getResources().getString(R.string.strServer) + "android/login.php?" + "acceso=" + accesso + "&id=" + URLEncoder.encode(gid) + "&nombre=" + URLEncoder.encode(nombre) + "&email=" + URLEncoder.encode(email) + "&foto=" + URLEncoder.encode(foto)  + "&apellido=" + URLEncoder.encode(apellido) + "&token=" + URLEncoder.encode(token) + "&version=" + getResources().getInteger(R.integer.version);
            Log.e("strUrl:", strUrl);
            url = new URL(strUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String result = null;
        try {
            codigo_estaciones = conn.execute(url).get();
            Log.e("estaciones encontradas", codigo_estaciones);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Error de conexion!", Toast.LENGTH_SHORT).show();
        //Log.e("GoogleSignIn", "OnConnectionFailed: " + connectionResult);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // callback from facebook
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            //Toast.makeText(this, "Id=" + account.getId(), Toast.LENGTH_SHORT).show();
            getMain(account.getId(),account.getGivenName(), account.getEmail(), account.getFamilyName(), account.getPhotoUrl().toString(), account.getIdToken(),2);
            // Signed in successfully, show authenticated UI.
            //updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            //Log.w("Google Signin", "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // facebook login resumed
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn) {
            RequestData();
        }
        // Google login resumed
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null){
            getMain(account.getId(),account.getGivenName(), account.getEmail(), account.getFamilyName(), account.getPhotoUrl().toString(), account.getIdToken(),2);
        }
    }

    private void RequestData() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                final JSONObject json = response.getJSONObject();
                try {
                    if(json != null){
                        getMain(json.getString("id"),json.getString("first_name"), json.getString("email"), json.getString("last_name"), "https://graph.facebook.com/" + json.getString("id") + "/picture?type=large", "",1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,email,last_name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void getMain(String id,String nombre, String email, String apellido, String foto, String token, int accesso){
        syncLogin(id,nombre, email, apellido, foto, token,accesso);
        Intent i;
        i = new Intent(this, MainActivity.class);
        i.putExtra("estaciones", codigo_estaciones);
        i.putExtra("id", id);
        startActivity(i);
    }
}
