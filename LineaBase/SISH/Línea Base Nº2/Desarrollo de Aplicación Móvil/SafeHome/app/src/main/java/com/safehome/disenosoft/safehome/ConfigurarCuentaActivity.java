package com.safehome.disenosoft.safehome;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.safehome.disenosoft.safehome.Servicios.ConexionBD;
import com.safehome.disenosoft.safehome.Servicios.Habitante;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ConfigurarCuentaActivity extends AppCompatActivity {

    private final int FOTO_CODIGORESPUESTA = 1;

    TextInputLayout nombre;
    TextInputLayout apellido;
    Button modificar;

    ImageView foto;

    Habitante miHabitante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configurar_cuenta);

        miHabitante = (Habitante) getIntent().getSerializableExtra("habitante");

        nombre = (TextInputLayout) findViewById(R.id.nombresModificarInputLayout);
        apellido = (TextInputLayout) findViewById(R.id.apellidosModificarInputLayout);
        foto = (ImageView) findViewById(R.id.fotoModificarHabitante);

        modificar = (Button) findViewById(R.id.modificarButton);

        nombre.getEditText().setText(miHabitante.getNombres());
        apellido.getEditText().setText(miHabitante.getApellidos());

        if(miHabitante.getFoto() != null){
            foto.setImageBitmap(miHabitante.getFoto());
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.configurarCuentaToolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        setTitle("Modificar cuenta");

        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,FOTO_CODIGORESPUESTA);
            }
        });

        nombre.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                nombre.setError(null);
            }
        });

        apellido.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                apellido.setError(null);
            }
        });

        modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean esValido = true;

                if(nombre.getEditText().getText().length() == 0){
                    esValido = false;
                    nombre.setError("Debe llenar este campo");
                    nombre.setErrorEnabled(true);
                }
                if(apellido.getEditText().getText().length() == 0){
                    esValido = false;
                    apellido.setError("Debe llenar este campo");
                    apellido.setErrorEnabled(true);
                }

                if(esValido){
                    miHabitante.setNombres(nombre.getEditText().getText().toString());
                    miHabitante.setApellidos(apellido.getEditText().getText().toString());

                    empezarModificacion();
                }
            }
        });

    }

    private void empezarModificacion(){
        foto.setEnabled(false);
        nombre.setEnabled(false);
        apellido.setEnabled(false);
        modificar.setEnabled(false);

        new ModificarHabitanteTask().execute(miHabitante);

        //Toast.makeText(this, "Modificando cuenta...", Toast.LENGTH_SHORT).show();
        Snackbar.make((CoordinatorLayout)findViewById(R.id.configurarCuentaLayout),"Modificando cuenta...",Snackbar.LENGTH_SHORT).show();
    }

    private void terminarModificacion(){
        foto.setEnabled(true);
        nombre.setEnabled(true);
        apellido.setEnabled(true);
        modificar.setEnabled(true);

        Intent retorno = new Intent();
        retorno.putExtra("habitante_modificado",miHabitante);

        setResult(RESULT_OK,retorno);

        //Toast.makeText(this, "Cuenta modificada", Toast.LENGTH_SHORT).show();

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == FOTO_CODIGORESPUESTA && resultCode == RESULT_OK && data != null){
            try {
                Uri imagenUri = data.getData();

                InputStream imageStream = getContentResolver().openInputStream(imagenUri);
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);

                bitmap = Bitmap.createScaledBitmap(bitmap, 375, 500, true);

                foto.setImageBitmap(bitmap);
                foto.setVisibility(View.VISIBLE);

                miHabitante.setFoto(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
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

    private class ModificarHabitanteTask extends AsyncTask<Habitante, Integer, Habitante>{

        @Override
        protected Habitante doInBackground(Habitante... habitantes) {
            ConexionBD.getInstancia().ModificarHabitante(habitantes[0]);
            return habitantes[0];
        }

        @Override
        protected void onPostExecute(Habitante habitante) {
            terminarModificacion();
        }
    }
}
