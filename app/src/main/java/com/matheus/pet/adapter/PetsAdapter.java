package com.matheus.pet.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.matheus.pet.R;
import com.matheus.pet.model.PetPerdido;

import java.util.List;

public class PetsAdapter extends RecyclerView.Adapter<PetsAdapter.MyViewHolder> {

    private List<PetPerdido> pets;
    private Context context;

    public PetsAdapter(List<PetPerdido> listapets, Context c) {
        this.pets = listapets;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemlista = LayoutInflater.from( parent.getContext() ).inflate(R.layout.adapter_pets, parent, false );
        return new MyViewHolder( itemlista );
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        PetPerdido petPerdido = pets.get( position );
        holder.nome.setText( petPerdido.getNome() );
        holder.raca.setText( petPerdido.getRaca() );
        holder.idade.setText( petPerdido.getIdade() );
        holder.data.setText( petPerdido.getDataPerdido() );
        holder.ultimaLocalizacao.setText( petPerdido.getUltimaLocalizacao() );

        if ( petPerdido.getFoto() !=null ){

            Uri uri = Uri.parse( petPerdido.getFoto() );
            Glide.with( context ).load( uri ).into( holder.foto );

        }else {
            holder.foto.setImageResource( R.drawable.padrao );
        }


    }



    @Override
    public int getItemCount() {
        return pets.size();
    }


    public class  MyViewHolder extends RecyclerView.ViewHolder {

        ImageView foto;
        TextView nome, raca, idade, data,ultimaLocalizacao;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.textViewNomeA);
            raca = itemView.findViewById(R.id.textRacaA);
            idade = itemView.findViewById(R.id.textIdadeA);
            data = itemView.findViewById(R.id.textViewDataA);
            ultimaLocalizacao = itemView.findViewById(R.id.textViewUltimaA);
            foto = itemView.findViewById(R.id.imageFotoA);
        }
    }


}
