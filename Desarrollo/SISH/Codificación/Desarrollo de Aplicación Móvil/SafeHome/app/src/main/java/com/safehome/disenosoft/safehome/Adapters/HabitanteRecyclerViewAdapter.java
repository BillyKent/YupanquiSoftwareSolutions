package com.safehome.disenosoft.safehome.Adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.safehome.disenosoft.safehome.GestorHabitantesActivity;
import com.safehome.disenosoft.safehome.R;
import com.safehome.disenosoft.safehome.Servicios.Habitante;

import java.util.List;

public class HabitanteRecyclerViewAdapter extends RecyclerView.Adapter<HabitanteRecyclerViewAdapter.ViewHolder> {

    List<Habitante> habitantes;
    GestorHabitantesActivity activity;

    public HabitanteRecyclerViewAdapter(List<Habitante> habitantes, GestorHabitantesActivity activity){
        this.habitantes = habitantes;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_habitante,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Habitante habitante = habitantes.get(i);
        viewHolder.nombre.setText(habitante.getNombres()+" "+habitante.getApellidos());
        viewHolder.correo.setText(habitante.getId());
        viewHolder.foto.setImageBitmap(habitante.getFoto());
        viewHolder.eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.EliminarHabitante(habitante);
            }
        });
    }

    @Override
    public int getItemCount() {
        return habitantes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView nombre;
        public TextView correo;
        public ImageView foto;
        public Button eliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombreHabitanteItemTextView);
            correo = itemView.findViewById(R.id.correoHabitanteItemTextView);
            eliminar = itemView.findViewById(R.id.eliminarHabitanteItemButton);
            foto = itemView.findViewById(R.id.habitanteItemImageView);
        }
    }


    public void EliminarHabitante(Habitante habitante){
        if(habitantes.size() > 0){
            habitantes.remove(habitante);
            notifyDataSetChanged();
        }
    }

    public void AgregarHabitante(Habitante habitante){
        habitantes.add(habitante);
        notifyDataSetChanged();
    }
}
