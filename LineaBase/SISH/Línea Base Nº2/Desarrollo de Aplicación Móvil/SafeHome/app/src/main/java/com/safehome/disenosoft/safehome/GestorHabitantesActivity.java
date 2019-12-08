package com.safehome.disenosoft.safehome;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.safehome.disenosoft.safehome.Adapters.HabitanteRecyclerViewAdapter;
import com.safehome.disenosoft.safehome.Servicios.ConexionBD;
import com.safehome.disenosoft.safehome.Servicios.Habitante;

import java.util.List;

public class GestorHabitantesActivity extends AppCompatActivity {

    private int VALIDADOR_CODIGORESPUESTA_ELIMINAR = 1, VALIDADOR_CODIGORESPUESTA_AGREGAR = 2, REGISTRO_CODIGODRESPUESTA = 3;

    CoordinatorLayout rootLayout;

    RecyclerView listaHabitantes;

    Habitante miHabitante;

    Habitante habitanteAEliminar;
    HabitanteRecyclerViewAdapter adapter;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestor_habitantes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        setTitle("Gestionar habitantes");

        miHabitante = (Habitante) getIntent().getSerializableExtra("habitante");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AgregarHabitante();
            }
        });

        listaHabitantes = (RecyclerView) findViewById(R.id.gestorHabitantesRecyclerView);
        listaHabitantes.setLayoutManager(new LinearLayoutManager(this));

        rootLayout = (CoordinatorLayout)findViewById(R.id.gestorHabitantesLayout);

        progressBar = (ProgressBar) findViewById(R.id.gestorHabitantesProgressBar);

        new ObtenerHabitantesTask().execute(miHabitante);

    }

    private void mostrarHabitantes(List<Habitante> habitantes){
        adapter = new HabitanteRecyclerViewAdapter(habitantes,this);

        listaHabitantes.setAdapter(adapter);

        progressBar.setVisibility(View.GONE);
        listaHabitantes.setVisibility(View.VISIBLE);
    }

    public void EliminarHabitante(Habitante habitante){
        Intent validadorIntent = new Intent(this,ValidadorPinActivity.class);
        validadorIntent.putExtra("habitante",miHabitante);

        habitanteAEliminar = habitante;

        startActivityForResult(validadorIntent,VALIDADOR_CODIGORESPUESTA_ELIMINAR);
    }

    private void AgregarHabitante(){
        Intent validadorIntent = new Intent(this,ValidadorPinActivity.class);
        validadorIntent.putExtra("habitante",miHabitante);

        startActivityForResult(validadorIntent,VALIDADOR_CODIGORESPUESTA_AGREGAR);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == VALIDADOR_CODIGORESPUESTA_AGREGAR){
            if(resultCode == RESULT_OK){
                int respuesta = data.getExtras().getInt(ValidadorPinActivity.VALIDADOR_RESPUESTA);

                if(respuesta == ValidadorPinActivity.VALIDADOR_OK){
                    //Toast.makeText(this, "Agregando habitante...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this,RegistrarHabitanteActivity.class);
                    startActivityForResult(intent,REGISTRO_CODIGODRESPUESTA);
                }else{
                    Toast.makeText(this, "Pin incorrecto", Toast.LENGTH_SHORT).show();
                }

            }else if(resultCode == RESULT_CANCELED){

            }
        }else if(requestCode == VALIDADOR_CODIGORESPUESTA_ELIMINAR){
            if(resultCode == RESULT_OK){
                int respuesta = data.getExtras().getInt(ValidadorPinActivity.VALIDADOR_RESPUESTA);

                if(respuesta == ValidadorPinActivity.VALIDADOR_OK){

                    Snackbar.make(rootLayout,"Eliminando a "+habitanteAEliminar.getNombres() + " " + habitanteAEliminar.getApellidos(),Snackbar.LENGTH_SHORT).show();

                    new EliminarHabitantesTask().execute(habitanteAEliminar);

                }else{
                    habitanteAEliminar = null;
                    //Toast.makeText(this, "Pin incorrecto", Toast.LENGTH_SHORT).show();
                    Snackbar.make(rootLayout,"Pin incorrecto",Snackbar.LENGTH_SHORT).show();
                }
            }else if(resultCode == RESULT_CANCELED){
                habitanteAEliminar = null;
            }
        }else if(requestCode == REGISTRO_CODIGODRESPUESTA){
            if(resultCode == RESULT_OK){
                Habitante nuevoHabitante = (Habitante) data.getSerializableExtra("habitante_nuevo");

                Snackbar.make(rootLayout,"Nuevo habitante creado con éxito", Snackbar.LENGTH_LONG).show();

                adapter.AgregarHabitante(nuevoHabitante);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    public class ObtenerHabitantesTask extends AsyncTask<Habitante, Integer, List<Habitante>>{

        @Override
        protected List<Habitante> doInBackground(Habitante... administrador) {
            return ConexionBD.getInstancia().ObtenerHabitantes(administrador[0].getId());
        }

        @Override
        protected void onPostExecute(List<Habitante> habitantes) {
            mostrarHabitantes(habitantes);
        }
    }

    public class EliminarHabitantesTask extends AsyncTask<Habitante, Integer, Habitante>{

        @Override
        protected Habitante doInBackground(Habitante... habitantes) {
            ConexionBD.getInstancia().EliminarHabitante(habitantes[0]);
            return habitantes[0];
        }

        @Override
        protected void onPostExecute(Habitante habitante) {
            //Toast.makeText(getApplicationContext(), "Eliminando a "+habitanteAEliminar.getNombres()+"...", Toast.LENGTH_SHORT).show();

            adapter.EliminarHabitante(habitanteAEliminar);
            //Toast.makeText(GestorHabitantesActivity.this, "Habitante eliminado", Toast.LENGTH_SHORT).show();

            Snackbar.make(rootLayout,"Habitante eliminado",Snackbar.LENGTH_LONG).show();
            habitanteAEliminar = null;
        }
    }


}
