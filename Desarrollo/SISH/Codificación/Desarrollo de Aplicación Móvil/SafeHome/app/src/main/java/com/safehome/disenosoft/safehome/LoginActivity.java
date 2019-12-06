package com.safehome.disenosoft.safehome;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.safehome.disenosoft.safehome.Servicios.ConexionBD;
import com.safehome.disenosoft.safehome.Servicios.Habitante;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout correoInputLayout;
    private CheckBox noCerrarSesionCheckBox;
    private Button iniciarSesionButton;
    private ProgressBar inicioProgressBar;

    private LinearLayout rootLayout;
    private LinearLayout inicioLayout;
    
    private  Habitante miHabitante;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        correoInputLayout = (TextInputLayout) findViewById(R.id.correoInicioInputLayout);
        noCerrarSesionCheckBox = (CheckBox) findViewById(R.id.noCerrarInicioCheckBox);
        iniciarSesionButton = (Button) findViewById(R.id.iniciarInicioButton);
        inicioProgressBar = (ProgressBar) findViewById(R.id.inicioProgressBar);


        rootLayout = (LinearLayout) findViewById(R.id.rootInicioSesionLayout);
        inicioLayout = (LinearLayout) findViewById(R.id.inicioLayout);

        iniciarSesionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sp = getSharedPreferences("local_vars", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                if(noCerrarSesionCheckBox.isChecked()){
                    editor.putInt("no_cerrar_sesion",1);
                    editor.putString("correo",correoInputLayout.getEditText().getText().toString());
                }else{
                    editor.putInt("no_cerrar_sesion",0);
                }

                editor.commit();

                empezarInicioSesion(correoInputLayout.getEditText().getText().toString(), "no_validado");

            }
        });

        correoInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                correoInputLayout.setError(null);
            }
        });

        //shared preffs

        SharedPreferences sharedPref = getSharedPreferences("local_vars",Activity.MODE_PRIVATE);
        int noCerrarSesion = sharedPref.getInt("no_cerrar_sesion", -1);

        if(noCerrarSesion == 1){
            String correo = sharedPref.getString("correo","");

            empezarInicioSesion(correo,"validado");
        }

        //-------------

    }

    private void empezarInicioSesion(String correo, String validacion){
        correoInputLayout.setEnabled(false);
        noCerrarSesionCheckBox.setEnabled(false);
        iniciarSesionButton.setEnabled(false);
        inicioLayout.setVisibility(View.GONE);
        inicioProgressBar.setVisibility(View.VISIBLE);

        Snackbar.make(rootLayout,"Iniciando sesión",Snackbar.LENGTH_SHORT).show();

        new InicioSesionTask().execute(correo,validacion);
    }

    private void validarInicioSesion(){
        inicioProgressBar.setVisibility(View.GONE);
        inicioLayout.setVisibility(View.VISIBLE);

        if(miHabitante == null){
            correoInputLayout.setEnabled(true);
            noCerrarSesionCheckBox.setEnabled(true);
            iniciarSesionButton.setEnabled(true);

            correoInputLayout.setError("Correo electrónico inválido");
        }else{
            if(miHabitante.isPrimeraVez()){
                Intent registroIntent = new Intent(this,RegistroActivity.class);
                registroIntent.putExtra("habitante",miHabitante);
                startActivity(registroIntent);
            }else{
                Intent validadorIntent = new Intent(this,ValidadorPinActivity.class);
                validadorIntent.putExtra("habitante",miHabitante);
                startActivityForResult(validadorIntent,ValidadorPinActivity.VALIDADOR_CODIGO_RESPUESTA);
            }

            correoInputLayout.setEnabled(true);
            noCerrarSesionCheckBox.setEnabled(true);
            iniciarSesionButton.setEnabled(true);
        }
    }

    private void terminarInicioSesion(){
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        intent.putExtra("habitante",miHabitante);

        startActivity(intent);

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == ValidadorPinActivity.VALIDADOR_CODIGO_RESPUESTA){
            if(resultCode == RESULT_OK){
                int respuesta = data.getExtras().getInt(ValidadorPinActivity.VALIDADOR_RESPUESTA);

                if(respuesta == ValidadorPinActivity.VALIDADOR_OK){
                    terminarInicioSesion();
                }else {
                    Snackbar.make(rootLayout, "Pin incorrecto", Snackbar.LENGTH_SHORT).show();
                }
            }else{
            }
        }
    }

    private class InicioSesionTask extends AsyncTask<String,Integer,Habitante>{

        String validado;
        @Override
        protected Habitante doInBackground(String... correo) {
            ConexionBD conexionBD = ConexionBD.getInstancia();
            validado = correo[1];
            return conexionBD.ObtenerHabitante(correo[0]);
        }

        @Override
        protected void onPostExecute(Habitante habitante) {
            miHabitante = habitante;

            if(validado == "validado"){
                terminarInicioSesion();
            }else{
                validarInicioSesion();
            }
        }
    }
}
