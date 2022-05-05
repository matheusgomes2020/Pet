package com.matheus.pet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.matheus.pet.databinding.ActivityMainBinding;
import com.matheus.pet.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate( getLayoutInflater() );
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        //handle click, logout
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                checkUser();
            }
        });
    }

    private void checkUser(){

        FirebaseUser firebaseUser =firebaseAuth.getCurrentUser();
        if ( firebaseUser == null ){
            //nao logado
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }else {

            //Logado
            //get user info
            String email = firebaseUser.getEmail();
            //setEmail
            binding.textView2.setText( email );
        }

    }

}