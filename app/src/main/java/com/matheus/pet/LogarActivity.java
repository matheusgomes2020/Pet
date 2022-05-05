package com.matheus.pet;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class LogarActivity extends AppCompatActivity {

    private static int AUTH_REQUEST_CODE = 7192;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;
    private List<AuthUI.IdpConfig> providers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logar);

        init();
    }

    private void init(){

        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(), // Email
                new AuthUI.IdpConfig.GoogleBuilder().build(), // Google
                new AuthUI.IdpConfig.AnonymousBuilder().build() // Anonimo
        );

        firebaseAuth = FirebaseAuth.getInstance();
        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                //Get user
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if ( user != null ){
                    Toast.makeText( LogarActivity.this,
                            "Voce ja esta logado com o id: " + user.getUid(),
                            Toast.LENGTH_SHORT).show();
                }else {

                    //Login
                    startActivityForResult( AuthUI.getInstance()
                    .createSignInIntentBuilder()
                            .setAvailableProviders( providers )
                            .build(), AUTH_REQUEST_CODE );
                }

            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener( listener );
    }

    @Override
    protected void onStop() {
        if ( listener != null ) {
            firebaseAuth.removeAuthStateListener( listener );
        }
        super.onStop();
    }
}