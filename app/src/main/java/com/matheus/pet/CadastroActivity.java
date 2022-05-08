package com.matheus.pet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.matheus.pet.config.ConfiguracaoFirebase;
import com.matheus.pet.helper.Base64custom;
import com.matheus.pet.helper.UsuarioFirebase;
import com.matheus.pet.model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private EditText editNome, editData, editEndereco, editTelefone, editEmail, editSenha;
    private Button botaoCadastrar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        editNome = findViewById( R.id.textNomePet);
        editData = findViewById( R.id.textRacaPet);
        editEndereco = findViewById( R.id.textIdadePet);
        editTelefone = findViewById( R.id.textDataPet);
        editEmail = findViewById( R.id.textEmailCadastro );
        editSenha = findViewById( R.id.textSenhaCadastro );
        botaoCadastrar = findViewById( R.id.botaoContato);

    }

    public void cadastrarUsuario( Usuario usuario ){

        auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        auth.createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if ( task.isSuccessful() ){

                    Toast.makeText(CadastroActivity.this,
                            "Sucesso ao cadastrar usuário!",
                            Toast.LENGTH_SHORT).show();
                    UsuarioFirebase.atualizarNomeUsuario( usuario.getNome() );
                    finish();


                    try {

                        String identificadorusuario = Base64custom.codificarBase64( usuario.getEmail() );
                        usuario.setId( identificadorusuario );
                        usuario.salvar();


                    }catch ( Exception e ){
                        e.printStackTrace();
                    }


                }else {

                    String excessao = "";
                    try {
                        throw  task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        excessao = "Digite uma senha mais forte!";
                    }
                    catch (FirebaseAuthInvalidCredentialsException e){
                        excessao = "Por favor, digite um e-mail válido!";
                    }
                    catch (FirebaseAuthUserCollisionException e){
                        excessao = "Esta conta já foi cadastrada!";
                    }
                    catch (Exception e){
                        excessao = "Erro ao cadastrar usuário: " + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroActivity.this,
                            excessao,
                            Toast.LENGTH_SHORT).show();



                }
            }
        });

    }


    public void validarCampos( View view ){

        String textNome = editNome.getText().toString();
        String textData = editData.getText().toString();
        String textEndereco = editEndereco.getText().toString();
        String textTelefone = editTelefone.getText().toString();


        String textEmail = editEmail.getText().toString();
        String textSenha = editSenha.getText().toString();



            if ( !textNome.isEmpty() ){
                if ( !textData.isEmpty() ){
                    if ( !textEndereco.isEmpty() ){
                        if ( !textTelefone.isEmpty() ){
                            if ( !textEmail.isEmpty() ){
                                if ( !textSenha.isEmpty() ){

                                    Usuario usuario = new Usuario();
                                    usuario.setNome( textNome );
                                    usuario.setData( textData );
                                    usuario.setEndereco( textEndereco );
                                    usuario.setTelefone( textTelefone );
                                    usuario.setEmail( textEmail );
                                    usuario.setSenha( textSenha );

                                    cadastrarUsuario( usuario );

                                }else { Toast.makeText(CadastroActivity.this, "Preencha a senha!", Toast.LENGTH_SHORT).show(); }
                            }else { Toast.makeText(CadastroActivity.this, "Preencha o e-mail!", Toast.LENGTH_SHORT).show(); }
                        }else { Toast.makeText(CadastroActivity.this, "Preencha o telefone!", Toast.LENGTH_SHORT).show(); }
                    }else { Toast.makeText(CadastroActivity.this, "Preencha o endereço!", Toast.LENGTH_SHORT).show(); }
                }else { Toast.makeText(CadastroActivity.this, "Preencha a data!", Toast.LENGTH_SHORT).show(); }
            }else { Toast.makeText(CadastroActivity.this, "Preencha o nome!", Toast.LENGTH_SHORT).show(); }
        }


    }

