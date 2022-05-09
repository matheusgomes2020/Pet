package com.matheus.pet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.matheus.pet.adapter.PetsAdapter;
import com.matheus.pet.config.ConfiguracaoFirebase;
import com.matheus.pet.helper.Base64custom;
import com.matheus.pet.helper.RecyclerItemClickListener;
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
    private EditText editText, editText2, editText3;
    private Button button;
    private FloatingActionButton fab;
    private String identificadorUsuario;
    private RecyclerView recyclerViewListaPets;
    private PetsAdapter adapter;
    private ValueEventListener valueEventListenerPets;
    private FirebaseUser usuarioAtual;
    private ArrayList<PetPerdido> listaPetPerdidos = new ArrayList<>();

    @SuppressLint({"ResourceAsColor", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        usuarioLogado = UsuarioFirebase.getdadosUsuarioLogado();
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle(usuarioLogado.getNome());
        toolbar.setSubtitle(usuarioLogado.getEmail());
        //toolbar.setTitleTextColor(R.color.black);
        //.setSubtitleTextColor(R.color.black);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.button2);

        /*
        textData = findViewById( R.id.textViewData );
        textEndereco = findViewById( R.id.textViewEndereco );
        textTelefone = findViewById( R.id.textViewTelefone );



        editText = findViewById(R.id.editTextTextPersonName);
        editText2 = findViewById(R.id.editTextTextPersonName2);
        editText3 = findViewById(R.id.editTextTextP
        ersonName3);

        button = findViewById(R.id.button3);

         */

        recyclerViewListaPets = findViewById(R.id.recyclerListaPets);

        database = ConfiguracaoFirebase.getFirebaseDatabase();
        Usuario u = UsuarioFirebase.getdadosUsuarioLogado();

        usuarioAtual = UsuarioFirebase.getUsuarioAtual();

        identificadorUsuario = Base64custom.codificarBase64(u.getEmail());
        petsRef = ConfiguracaoFirebase.getFirebaseDatabase().child("pets").child(identificadorUsuario);
        DatabaseReference petsRef2 = ConfiguracaoFirebase.getFirebaseDatabase().child("pets").child(identificadorUsuario);

        //configurar adapter
        adapter = new PetsAdapter(listaPetPerdidos, getApplicationContext());

        //configurar recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewListaPets.setLayoutManager(layoutManager);
        recyclerViewListaPets.setHasFixedSize(true);
        recyclerViewListaPets.setAdapter(adapter);

        //Configurar evento de clique no recyclerview
        recyclerViewListaPets.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerViewListaPets,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                PetPerdido petPerdidoSelecionado = listaPetPerdidos.get(position);
                                Intent i = new Intent(getApplicationContext(), DescricaoPetActivity.class);
                                i.putExtra("petPerdidoIntent", petPerdidoSelecionado);
                                startActivity(i);

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                                PetPerdido petPerdidoSelecionado = listaPetPerdidos.get(position);
                                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                                dialog.setTitle("Confirmar exclusão");
                                dialog.setMessage("Deseja excluir  o pet " + petPerdidoSelecionado.getNome() + " ?");

                                dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        petsRef.child(petPerdidoSelecionado.getId()).removeValue();
                                        Log.d("keyUltima", petPerdidoSelecionado.getId());

                                    }

                                });

                                dialog.setNegativeButton("Não", null);

                                dialog.create();
                                dialog.show();

                                recarregarPets();

                            }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            }


                        }

                )

        );
    }


    public void a(View view) {

        Intent i = new Intent(MainActivity.this, CadastroPetActivity.class);
        startActivity(i);
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarPets();


    }

    public void deslogarUsuario() {

        try {
            firebaseAuth.signOut();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sair:
                deslogarUsuario();
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }


    public void recarregarPets() {
        listaPetPerdidos.clear();
        adapter = new PetsAdapter(listaPetPerdidos, getApplicationContext());
        recyclerViewListaPets.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void recuperarPets() {
        listaPetPerdidos.clear();

        valueEventListenerPets = petsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dados : snapshot.getChildren()) {

                    PetPerdido petPerdido = dados.getValue(PetPerdido.class);
                    petPerdido.setId(dados.getKey());
                    Log.d("keyUsuario", petPerdido.getId());

                    listaPetPerdidos.add(petPerdido);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}

