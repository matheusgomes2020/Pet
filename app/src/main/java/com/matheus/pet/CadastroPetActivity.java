package com.matheus.pet;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.matheus.pet.config.ConfiguracaoFirebase;
import com.matheus.pet.databinding.ActivityCadastroPetBinding;
import com.matheus.pet.helper.Base64custom;
import com.matheus.pet.helper.UsuarioFirebase;
import com.matheus.pet.model.PetPerdido;
import com.matheus.pet.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class CadastroPetActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityCadastroPetBinding binding;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference database;
    private DatabaseReference petsRef;
    private FirebaseUser usuarioAtual;
    private StorageReference storage;
    private Usuario usuarioLogado;
    private String identificadorUsuario;
    private ValueEventListener valueEventListenerPets;
    private FloatingActionButton fab;
    private ArrayList<PetPerdido> petPerdidoList = new ArrayList<>();
    private RecyclerView recyclerViewListaPets;

    private EditText editNome, editIdade, editData, editRaca, editUltima;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCadastroPetBinding.inflate(getLayoutInflater());
        setContentView( R.layout.activity_cadastro_pet );

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Cadastrar pet perdido");

        editNome = findViewById(R.id.textNomePet);
        editData = findViewById(R.id.textDataPet);
        editIdade = findViewById(R.id.textIdadePet);
        editRaca = findViewById(R.id.textRacaPet);
        editUltima = findViewById(R.id.textUltimaLocalizacaoPet);
        fab = findViewById(R.id.fab);

        firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        usuarioLogado = UsuarioFirebase.getdadosUsuarioLogado();
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();

        database = ConfiguracaoFirebase.getFirebaseDatabase();
        Usuario u = UsuarioFirebase.getdadosUsuarioLogado();

        identificadorUsuario = Base64custom.codificarBase64( u.getEmail() );


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PetPerdido petPerdido = new PetPerdido();
                String nomeP = editNome.getText().toString();
                String idadeP = editIdade.getText().toString();
                String fotoP = "";
                String dataP = editData.getText().toString();
                String racaP = editRaca.getText().toString();
                String ultimaP = editUltima.getText().toString();

                petPerdido.setNome( nomeP );
                petPerdido.setIdade( dataP );
                petPerdido.setDataPerdido( idadeP );
                petPerdido.setFoto( fotoP );
                petPerdido.setRaca( racaP );
                petPerdido.setUltimaLocalizacao( ultimaP );

                database = ConfiguracaoFirebase.getFirebaseDatabase();
                DatabaseReference pets = database.child("pets");
                DatabaseReference pets2 = database.child("todospets");

                pets.child( identificadorUsuario )
                        .push()
                        .setValue( petPerdido );

                pets2
                        .push()
                        .setValue( petPerdido );
            }


        });

        DatabaseReference pets2 = database.child("todospets");
        pets2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    PetPerdido petPerdido = snapshot.getValue( PetPerdido.class );
                    petPerdidoList.add( petPerdido );
                }



            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        for ( PetPerdido petPerdido : petPerdidoList){

            String info = petPerdido.getNome() + "/n" + petPerdido.getRaca();
            String nome = petPerdido.getNome();

            Log.i( "Lista", "nome" + nome);

        }



    }



    
}