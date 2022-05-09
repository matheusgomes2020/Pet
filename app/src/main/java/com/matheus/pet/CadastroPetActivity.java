package com.matheus.pet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.matheus.pet.config.ConfiguracaoFirebase;
import com.matheus.pet.databinding.ActivityCadastroPetBinding;
import com.matheus.pet.helper.Base64custom;
import com.matheus.pet.helper.Permissao;
import com.matheus.pet.helper.UsuarioFirebase;
import com.matheus.pet.model.PetPerdido;
import com.matheus.pet.model.Usuario;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class CadastroPetActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityCadastroPetBinding binding;

    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    private static final int SELECAO_GALERIA = 200;
    private ImageButton imageButtonGaleria;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference database;
    private StorageReference storage;

    private Usuario usuarioLogado;
    private String identificadorUsuario;
    private FloatingActionButton fab;
    private ArrayList<PetPerdido> petPerdidoList = new ArrayList<>();
    private EditText editNome, editIdade, editData, editRaca, editUltima;
    private String foto;
    private TextView textView;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCadastroPetBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_cadastro_pet);

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setTitle( "Cadastar pet perdido" );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        //Validar permissões
        Permissao.validarPermissoes(permissoesNecessarias, this, 1);

        editNome = findViewById(R.id.textNomePet);
        editData = findViewById(R.id.textDataPet);
        editIdade = findViewById(R.id.textIdadePet);
        editRaca = findViewById(R.id.textRacaPet);
        editUltima = findViewById(R.id.textUltimaLocalizacaoPet);
        fab = findViewById(R.id.fab);
        imageButtonGaleria = findViewById(R.id.imageButton2);
        textView = findViewById(R.id.textView9);

        //init progressDialog
        progressDialog = new ProgressDialog(this);
        //set properties
        progressDialog.setTitle("Por favor, espere!");             //set title
        progressDialog.setMessage("Salvando imagem...");   //set message
        progressDialog.setCanceledOnTouchOutside(false);    //disable dismiss when touching outside of progress dialog

        firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        usuarioLogado = UsuarioFirebase.getdadosUsuarioLogado();
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        storage = ConfiguracaoFirebase.getFirebaseStorage();

        database = ConfiguracaoFirebase.getFirebaseDatabase();
        Usuario u = UsuarioFirebase.getdadosUsuarioLogado();

        identificadorUsuario = Base64custom.codificarBase64(u.getEmail());
        

        imageButtonGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            progressDialog.show();
            Bitmap imagem = null;

            try {
                Uri localImagemSelecionada = data.getData();
                imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);

                if (imagem != null) {

                    //Recuperar dados da imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //Salvar imagem no firebase
                    StorageReference imagemRef = storage
                            .child("imagens")
                            .child("pets")
                            //.child( identificadorUsuario )
                            .child(identificadorUsuario + ".jpeg");

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(CadastroPetActivity.this,
                                    "Erro ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @SuppressLint("ResourceAsColor")
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(CadastroPetActivity.this,
                                    "Sucesso ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            textView.setVisibility( View.VISIBLE );


                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();
                                    foto = url.toString();
                                    Log.i("fotoRecebida: ", foto);
                                }
                            });

                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void salvarPet(View view) {

        String nomeP = editNome.getText().toString();
        String idadeP = editIdade.getText().toString();
        String dataP = editData.getText().toString();
        String racaP = editRaca.getText().toString();
        String ultimaP = editUltima.getText().toString();


        if (!nomeP.isEmpty()) {
            if (!idadeP.isEmpty()) {
                if (!dataP.isEmpty()) {
                    if (!racaP.isEmpty()) {
                        if (!ultimaP.isEmpty()) {

                            PetPerdido petPerdido = new PetPerdido();

                            petPerdido.setNome(nomeP);
                            petPerdido.setIdade(idadeP);
                            petPerdido.setDataPerdido(dataP);
                            petPerdido.setRaca(racaP);
                            petPerdido.setUltimaLocalizacao(ultimaP);
                            petPerdido.setFoto(foto);
                            petPerdido.setUsuario( usuarioLogado );

                            petPerdido.salvar( identificadorUsuario );
                            finish();

                        } else {Toast.makeText(CadastroPetActivity.this, "Preencha a última localização!", Toast.LENGTH_SHORT).show(); }
                    } else { Toast.makeText(CadastroPetActivity.this, "Preencha a idade!", Toast.LENGTH_SHORT).show(); }
                } else { Toast.makeText(CadastroPetActivity.this, "Preencha a data!", Toast.LENGTH_SHORT).show(); }
            } else { Toast.makeText(CadastroPetActivity.this, "Preencha a raça!", Toast.LENGTH_SHORT).show(); }
        } else { Toast.makeText(CadastroPetActivity.this, "Preencha o nome!", Toast.LENGTH_SHORT).show(); }
    }



}