package com.matheus.pet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.matheus.pet.model.PetPerdido;

public class DescricaoPetActivity extends AppCompatActivity {

    private TextView textNome, textIdade, textRaca, textData, textUltimalocalizacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descricao_pet);

        textNome = findViewById( R.id.t1 );
        textRaca = findViewById( R.id.t2 );
        textIdade = findViewById( R.id.t3 );
        textData = findViewById( R.id.t4 );
        textUltimalocalizacao = findViewById( R.id.t5 );

        Bundle bundle = getIntent().getExtras();

        PetPerdido petPerdido =  ( PetPerdido ) bundle.getSerializable( "petPerdidoIntent" );

        textNome.setText( petPerdido.getNome() );
        textRaca.setText( petPerdido. getRaca());
        textIdade.setText( petPerdido.getIdade() );
        textData.setText( petPerdido.getDataPerdido() );
        textUltimalocalizacao.setText( petPerdido. getUltimaLocalizacao());

    }
}