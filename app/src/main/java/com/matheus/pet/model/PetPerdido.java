package com.matheus.pet.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
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

    public PetPerdido() {
    }

    public void salvar(){

        DatabaseReference firebaseref = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference pet = firebaseref.child( "pets" ).push();

        pet.setValue( this );

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
}
