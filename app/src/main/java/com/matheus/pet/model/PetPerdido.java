package com.matheus.pet.model;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.matheus.pet.CadastroActivity;
import com.matheus.pet.CadastroPetActivity;
import com.matheus.pet.config.ConfiguracaoFirebase;

import java.io.Serializable;

public class PetPerdido implements Serializable {

    private String id;
    private String nome;
    private String idade;
    private String foto;
    private String dataPerdido;
    private String raca;
    private String ultimaLocalizacao;
    private Usuario usuario;

    public PetPerdido() {
    }

    public void salvar( String idUsuario ){

        DatabaseReference firebaseref = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference pets = firebaseref.child( "pets" ).child( idUsuario ).push();
        pets.setValue( this );


        DatabaseReference pets2 = firebaseref.child("todospets").push();
        pets2.setValue(this);
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIdade() {
        return idade;
    }

    public void setIdade(String idade) {
        this.idade = idade;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getDataPerdido() {
        return dataPerdido;
    }

    public void setDataPerdido(String dataPerdido) {
        this.dataPerdido = dataPerdido;
    }

    public String getRaca() {
        return raca;
    }

    public void setRaca(String raca) {
        this.raca = raca;
    }

    public String getUltimaLocalizacao() {
        return ultimaLocalizacao;
    }

    public void setUltimaLocalizacao(String ultimaLocalizacao) {
        this.ultimaLocalizacao = ultimaLocalizacao;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
