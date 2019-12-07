package com.safehome.disenosoft.safehome;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.safehome.disenosoft.safehome.Servicios.Habitante;

public class ValidadorPinActivity extends AppCompatActivity {

    public static String VALIDADOR_RESPUESTA = "ValidadorRespuesta";
    public static int VALIDADOR_OK = 1, VALIDADOR_NO_OK = 2;
    public static int VALIDADOR_CODIGO_RESPUESTA = 5;

    private String[] numeros = {"", "", "", ""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validador_pin);

        final EditText pin1 = (EditText) findViewById(R.id.primerDigitoPinValidadorEditText);
        final EditText pin2 = (EditText) findViewById(R.id.segundoDigitoPinValidadorEditText);
        final EditText pin3 = (EditText) findViewById(R.id.tercerDigitoPinValidadorEditText);
        final EditText pin4 = (EditText) findViewById(R.id.cuartoDigitoPinValidadorEditText);

        final Habitante miHabitante = (Habitante) getIntent().getSerializableExtra("habitante");

        final Button validar = (Button) findViewById(R.id.validarValidadorButton);

        pin1.requestFocus();

        pin1.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i != KeyEvent.KEYCODE_DEL && i != KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP){
                    numeros[0] = pin1.getText().toString();
                    pin2.requestFocus();
                }
                return false;
            }
        });

        pin2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    if(i != KeyEvent.KEYCODE_DEL && i != KeyEvent.KEYCODE_BACK  && keyEvent.getAction() == KeyEvent.ACTION_UP){
                        numeros[1] = pin2.getText().toString();
                        pin3.requestFocus();
                    }
                }
                return false;
            }
        });

        pin3.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    if(i != KeyEvent.KEYCODE_DEL && i != KeyEvent.KEYCODE_BACK  && keyEvent.getAction() == KeyEvent.ACTION_UP){
                        numeros[2] = pin3.getText().toString();
                        pin4.requestFocus();
                    }
                }
                return false;
            }
        });

        pin4.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    if(i != KeyEvent.KEYCODE_DEL && i != KeyEvent.KEYCODE_BACK  && keyEvent.getAction() == KeyEvent.ACTION_UP){
                        numeros[3] = pin4.getText().toString();
                    }
                }
                return false;
            }
        });

        pin1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    pin1.setText("");
                }else{
                    pin1.setText(numeros[0]);
                }
            }
        });

        pin2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    pin2.setText("");
                }else{
                    pin2.setText(numeros[1]);
                }
            }
        });

        pin3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    pin3.setText("");
                }else{
                    pin3.setText(numeros[2]);
                }
            }
        });

        pin4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    pin4.setText("");
                }else{
                    pin4.setText(numeros[3]);
                }
            }
        });


        validar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pin1.getText().toString().length() > 0 && pin2.getText().toString().length() > 0 && pin3.getText().toString().length() > 0 && pin4.getText().toString().length() > 0){
                    String lectura = pin1.getText().toString() + pin2.getText().toString() + pin3.getText().toString() + pin4.getText().toString();

                    Intent retorno = new Intent();

                    if(lectura.equals(miHabitante.getPin())){
                        retorno.putExtra(VALIDADOR_RESPUESTA,VALIDADOR_OK);
                    }else{
                        retorno.putExtra(VALIDADOR_RESPUESTA,VALIDADOR_NO_OK);
                    }

                    setResult(RESULT_OK,retorno);

                    finish();
                }else{
                    Toast.makeText(ValidadorPinActivity.this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
