package com.safehome.disenosoft.safehome;

import android.content.Intent;
import android.os.AsyncTask;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.safehome.disenosoft.safehome.Servicios.ConexionBD;
import com.safehome.disenosoft.safehome.Servicios.Habitante;

public class RegistrarHabitanteActivity extends AppCompatActivity {

    LinearLayout rootLayout;

    TextInputLayout nombres;
    TextInputLayout apellidos;
    TextInputLayout correo;

    Button registrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_habitante);

        rootLayout = (LinearLayout) findViewById(R.id.registrarHabitanteLayout);

        nombres  = (TextInputLayout) findViewById(R.id.nombresRegistroInputLayout);
        apellidos  = (TextInputLayout) findViewById(R.id.apellidosRegistroInputLayout);
        correo = (TextInputLayout) findViewById(R.id.correoRegistroInputLayout);

        registrar = (Button) findViewById(R.id.registrarRegistroButton);


        nombres.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                nombres.setError(null);
            }
        });

        apellidos.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                apellidos.setError(null);
            }
        });

        correo.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                correo.setError(null);
            }
        });

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean esPosibleRegistrar = true;
                if(nombres.getEditText().getText().length() == 0){
                    nombres.setError("Debe llenar este campo");
                    nombres.setErrorEnabled(true);
                    esPosibleRegistrar = false;
                }
                if(apellidos.getEditText().getText().length() == 0){
                    apellidos.setError("Debe llenar este campo");
                    apellidos.setErrorEnabled(true);
                    esPosibleRegistrar = false;
                }

                if(correo.getEditText().getText().length() == 0){
                    correo.setError("Debe llenar este campo");
                    correo.setErrorEnabled(true);
                    esPosibleRegistrar = false;
                }

                if(esPosibleRegistrar)
                    empezarRegistro();
            }
        });


    }

    private void empezarRegistro(){
        nombres.setEnabled(false);
        apellidos.setEnabled(false);
        correo.setEnabled(false);
        registrar.setEnabled(false);

        Habitante nuevoHabitante = new Habitante(correo.getEditText().getText().toString(),nombres.getEditText().getText().toString(),apellidos.getEditText().getText().toString(),0,true,"","");

        new PruebaTask().execute(nuevoHabitante);

        //Toast.makeText(this, "Registrado nuevo habitante...", Toast.LENGTH_SHORT).show();

        Snackbar.make(rootLayout,"Registrando nuevo habitante...",Snackbar.LENGTH_SHORT).show();
    }

    private void terminarRegistro(Habitante habitante){
        //Toast.makeText(this, "Nuevo habitante registrado", Toast.LENGTH_SHORT).show();

        Intent retorno = new Intent();

        retorno.putExtra("habitante_nuevo",habitante);

        setResult(RESULT_OK,retorno);

        finish();
    }

    private void correoExistente(){
        nombres.setEnabled(true);
        apellidos.setEnabled(true);
        correo.setEnabled(true);
        registrar.setEnabled(true);

        correo.setError("Correo ya existente");
        correo.setErrorEnabled(true);
    }


    public class PruebaTask extends AsyncTask<Habitante,Integer,Habitante>{

        @Override
        protected Habitante doInBackground(Habitante... habitante) {
            return ConexionBD.getInstancia().CrearHabitante(habitante[0]);
        }

        @Override
        protected void onPostExecute(Habitante habitante) {
            if(habitante == null){
                correoExistente();
            }else{
                terminarRegistro(habitante);
            }
        }
    }
}
