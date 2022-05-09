package com.matheus.pet;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.matheus.pet.model.PetPerdido;

public class DescricaoPetActivity extends AppCompatActivity {

    private TextView textNome, textIdade, textRaca, textData, textUltimalocalizacao, texNomeUsuario, textEmailUsuario;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descricao_pet);

        setSupportActionBar(findViewById(R.id.toolbar2));
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        textNome = findViewById( R.id.t1 );
        textRaca = findViewById( R.id.t2 );
        textIdade = findViewById( R.id.t3 );
        textData = findViewById( R.id.t4 );
        textUltimalocalizacao = findViewById( R.id.t5 );
        imageView = findViewById( R.id.imageView );

        texNomeUsuario = findViewById( R.id.textView10 );
        textEmailUsuario = findViewById( R.id.textView11 );


        Bundle bundle = getIntent().getExtras();

        PetPerdido petPerdido =  ( PetPerdido ) bundle.getSerializable( "petPerdidoIntent" );

        getSupportActionBar().setTitle( petPerdido.getNome() );

        textNome.setText( petPerdido.getNome() );
        textRaca.setText( petPerdido. getRaca());
        textIdade.setText( petPerdido.getIdade() );
        textData.setText( petPerdido.getDataPerdido() );
        textUltimalocalizacao.setText( petPerdido. getUltimaLocalizacao());

        texNomeUsuario.setText( petPerdido.getUsuario().getNome() );
        textEmailUsuario.setText( petPerdido.getUsuario().getEmail() );

        if ( petPerdido.getFoto() != null ){

            Uri uri = Uri.parse( petPerdido.getFoto() );
            Glide.with( getApplicationContext() ).load( uri ).into( imageView );

        }else {

            imageView.setImageResource( R.drawable.padrao );

        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_descricao_pet, menu);

        return super.onCreateOptionsMenu(menu);
    }

}