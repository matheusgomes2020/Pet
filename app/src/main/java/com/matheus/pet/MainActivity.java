package com.matheus.pet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.matheus.pet.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient googleSignInClient;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate( getLayoutInflater() );
        setContentView(binding.getRoot());

        //Configure the Google Sigin
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder( GoogleSignInOptions.DEFAULT_SIGN_IN )
                .requestIdToken(  getString( R.string.default_web_client_id ))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient( this, googleSignInOptions );

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        //Botao ggogle
        binding.googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: begin Google SignIn");
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult( intent, RC_SIGN_IN );
            }
        });
    }

    private void checkUser(){

        FirebaseUser firebaseUser =firebaseAuth.getCurrentUser();
        if ( firebaseUser != null ){
            //nao logado
            Log.d( TAG, "checkUser: Já logado" );
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Log.d( TAG, "onActivityResult: Google Signin intent result" );
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                //
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle( account.getIdToken() );


            }catch (Exception e){
                Log.d(TAG,"onActivityResult: " + e.getMessage());
            }
        }
    }


    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken) {

        Log.d(TAG, "setFirebaseAuthWithGoogleAccount: begin firebase auth with google account");
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Sucesso
                        Log.d(TAG, "onSuccess: Logged in");

                        //usuario logado
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                        String uid = firebaseUser.getUid();
                        String email = firebaseUser.getEmail();

                        Log.d(TAG, "onSuccess: E-mail: " + email);
                        Log.d(TAG, "onSuccess: UID: " + uid);


                        //Start profile activity
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                        finish();
                    }
                });
    }





    private void firebaseAuthWithGoogleAccount(String idToken){
        Log.d( TAG, "setFirebaseAuthWithGoogleAccount: begin firebase auth with google account" );
        AuthCredential credential = GoogleAuthProvider.getCredential( idToken, null );
        firebaseAuth.signInWithCredential( credential )
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //Sucesso
                        Log.d( TAG, "onSuccess: Logged in" );

                        //usuario logado
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                        String uid = firebaseUser.getUid();
                        String email = firebaseUser.getEmail();

                        Log.d( TAG, "onSuccess: E-mail: " + email );
                        Log.d( TAG, "onSuccess: UID: " + uid );

                        //checra se usu é novo
                        if (authResult.getAdditionalUserInfo().isNewUser()){
                            Log.d( TAG, "onSuccess: conta criada...\n" + email );
                            Toast.makeText( MainActivity.this, "conta criada...\n" + email, Toast.LENGTH_SHORT );
                        }else {
                            Log.d( TAG, "onSuccess: Existente...\n" + email );
                            Toast.makeText( MainActivity.this, "Existente...\n" + email, Toast.LENGTH_SHORT );
                        }

                        //Start profile activity
                        startActivity( new Intent( MainActivity.this, ProfileActivity.class ) );
                        finish();



                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d( TAG, "onFailure: Logged in failled " + e.getMessage() );
                    }
                });
    }
}