package com.matheus.pet.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.matheus.pet.config.ConfiguracaoFirebase;

import java.io.Serializable;

public class Usuario implements Serializable {

    private String id;
    private String nome;
    private String data;
    private String endereco;
    private String telefone;
    private String email;
    private String senha;
    private String foto;

    public Usuario() {
    }

    public void salvar(){

        DatabaseReference firebaseref = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuario = firebaseref.child( "usuarios" ).child( getId() );

        usuario.setValue( this );

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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
