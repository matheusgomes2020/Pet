package com.matheus.pet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.matheus.pet.adapter.PetsAdapter;
import com.matheus.pet.config.ConfiguracaoFirebase;
import com.matheus.pet.helper.Base64custom;
import com.matheus.pet.helper.UsuarioFirebase;
import com.matheus.pet.model.PetPerdido;
import com.matheus.pet.model.Usuario;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth firebaseAuth;
    private DatabaseReference database;
    private DatabaseReference petsRef;
    private StorageReference storage;
    private Usuario usuarioLogado;
    private TextView textData, textEndereco, textTelefone;
    private EditText editText,editText2, editText3;
    private Button button;
    private String identificadorUsuario;
    private RecyclerView recyclerViewListaPets;
    private PetsAdapter adapter;
    private ValueEventListener valueEventListenerPets;
    private FirebaseUser usuarioAtual;
    private ArrayList<PetPerdido> listaPetPerdidos = new ArrayList<>();

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        usuarioLogado = UsuarioFirebase.getdadosUsuarioLogado();
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle( usuarioLogado.getNome() );
        toolbar.setSubtitle( usuarioLogado.getEmail() );
        //toolbar.setTitleTextColor(R.color.black);
        //.setSubtitleTextColor(R.color.black);
        setSupportActionBar( toolbar );

        textData = findViewById( R.id.textViewData );
        textEndereco = findViewById( R.id.textViewEndereco );
        textTelefone = findViewById( R.id.textViewTelefone );

        recyclerViewListaPets = findViewById(R.id.recyclerListaPets);

        editText = findViewById(R.id.editTextTextPersonName);
        editText2 = findViewById(R.id.editTextTextPersonName2);
        editText3 = findViewById(R.id.editTextTextPersonName3);
        button = findViewById(R.id.button3);



        database = ConfiguracaoFirebase.getFirebaseDatabase();
        Usuario u = UsuarioFirebase.getdadosUsuarioLogado();

        usuarioAtual = UsuarioFirebase.getUsuarioAtual();

        identificadorUsuario = Base64custom.codificarBase64( u.getEmail() );
        petsRef = ConfiguracaoFirebase.getFirebaseDatabase().child("pets").child( identificadorUsuario );

        //configurar adapter
        adapter = new PetsAdapter(listaPetPerdidos, getApplicationContext() );

        //configurar recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( getApplicationContext() );
        recyclerViewListaPets.setLayoutManager( layoutManager );
        recyclerViewListaPets.setHasFixedSize( true );
        recyclerViewListaPets.setAdapter( adapter );


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PetPerdido petPerdido = new PetPerdido();
                String nomeP = editText.getText().toString();
                String idadeP = editText2.getText().toString();
                String dataP = editText3.getText().toString();

                petPerdido.setNome( nomeP );
                petPerdido.setIdade( dataP );
                petPerdido.setDataPerdido( idadeP );

                database = ConfiguracaoFirebase.getFirebaseDatabase();
                DatabaseReference pets = database.child("pets");

                pets.setValue( petPerdido );

            }
        });

        database.child("usuarios").child( identificadorUsuario ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Usuario usuario = snapshot.getValue( Usuario.class );
                textData.setText( usuario.getData() );
                textEndereco.setText( usuario.getEndereco() );
                textTelefone.setText( usuario.getTelefone() );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });










    }


    public void a( View view ){

        Intent i = new Intent( MainActivity.this, CadastroPetActivity.class );
        startActivity( i );
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarPets();


    }

    public void deslogarUsuario(){

        try {
            firebaseAuth.signOut();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch ( item.getItemId() ){
            case R.id.menu_sair :
                deslogarUsuario();
                finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.menu_main, menu );


        return super.onCreateOptionsMenu(menu);
    }
    public void recuperarPets(){

        listaPetPerdidos.clear();

        valueEventListenerPets = petsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for ( DataSnapshot dados: snapshot.getChildren() ){

                    PetPerdido petPerdido = dados.getValue( PetPerdido.class );

                    listaPetPerdidos.add( petPerdido );

                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}

