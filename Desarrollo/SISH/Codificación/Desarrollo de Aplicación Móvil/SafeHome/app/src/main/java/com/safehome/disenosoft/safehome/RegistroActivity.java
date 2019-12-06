package com.safehome.disenosoft.safehome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.appcompat.app.AppCompatActivity;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.safehome.disenosoft.safehome.CustomViews.NonSwipeableViewPager;
import com.safehome.disenosoft.safehome.Servicios.ConexionBD;
import com.safehome.disenosoft.safehome.Servicios.Habitante;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class RegistroActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private static NonSwipeableViewPager mViewPager;

    private Habitante miHabitante;

    public static Activity registroActivity = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        registroActivity = this;

        miHabitante = (Habitante) getIntent().getSerializableExtra("habitante");

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (NonSwipeableViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }


    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        private static ImageView[] fotos;

        private static Button subirFotos;
        private static Button rotarFotos;
        private static Button terminarButton;

        private static List<Bitmap> fotosBitmap = null;

        private static int anguloRotacion = 0;

        private static Context context;

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber, Habitante miHabitante, Context _context) {
            context = _context;

            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putSerializable("habitante", miHabitante);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int pagina = getArguments().getInt(ARG_SECTION_NUMBER);

            View rootView = null;
            switch (pagina){
                case 1:{
                    rootView = inflater.inflate(R.layout.fragment_registro_1, container, false);

                    Button empezarButton = (Button) rootView.findViewById(R.id.empezarRegistroButton);

                    empezarButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mViewPager.setCurrentItem(1);
                        }
                    });
                    break;
                }
                case 2:{
                    rootView = inflater.inflate(R.layout.fragment_registro_2, container, false);

                    Button siguienteButton = (Button) rootView.findViewById(R.id.primerSiguienteRegistroButton);

                    siguienteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mViewPager.setCurrentItem(2);
                        }
                    });

                    break;
                }
                case 3:{
                    rootView = inflater.inflate(R.layout.fragment_registro_3, container, false);

                    terminarButton = (Button) rootView.findViewById(R.id.terminarRegistroButton);
                    subirFotos = (Button) rootView.findViewById(R.id.subirFotografiasRegistroButton);
                    rotarFotos = (Button) rootView.findViewById(R.id.rotarFotografiasRegistroButton);
                    
                    final EditText pin1 = rootView.findViewById(R.id.primerDigitoPinRegistroEditText);
                    final EditText pin2 = rootView.findViewById(R.id.segundoDigitoPinRegistroEditText);
                    final EditText pin3 = rootView.findViewById(R.id.tercerDigitoPinRegistroEditText);
                    final EditText pin4 = rootView.findViewById(R.id.cuartoDigitoPinRegistroEditText);
                    final LinearLayout pinRegistroLayout = rootView.findViewById(R.id.pinRegistroLayout);
                    final ProgressBar registroProgressBar = rootView.findViewById(R.id.registroProgressBar);

                    terminarButton.setEnabled(false);
                    subirFotos.setEnabled(false);
                    rotarFotos.setEnabled(false);

                    fotos = new ImageView[10];

                    fotos[0] = (ImageView) rootView.findViewById(R.id.fotoRegistro1);
                    fotos[1] = (ImageView) rootView.findViewById(R.id.fotoRegistro2);
                    fotos[2] = (ImageView) rootView.findViewById(R.id.fotoRegistro3);
                    fotos[3] = (ImageView) rootView.findViewById(R.id.fotoRegistro4);
                    fotos[4] = (ImageView) rootView.findViewById(R.id.fotoRegistro5);
                    fotos[5] = (ImageView) rootView.findViewById(R.id.fotoRegistro6);
                    fotos[6] = (ImageView) rootView.findViewById(R.id.fotoRegistro7);
                    fotos[7] = (ImageView) rootView.findViewById(R.id.fotoRegistro8);
                    fotos[8] = (ImageView) rootView.findViewById(R.id.fotoRegistro9);
                    fotos[9] = (ImageView) rootView.findViewById(R.id.fotoRegistro10);

                    subirFotos.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent,"Selecciona tus fotos"), 1);
                        }
                    });

                    rotarFotos.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            anguloRotacion += 90;
                            if(anguloRotacion == 360)
                                anguloRotacion = 0;
                            for(int i=0; i<10; i++){
                                Bitmap aux = fotosBitmap.get(i);
                                aux = rotarImagen(aux, anguloRotacion);
                                aux = Bitmap.createScaledBitmap(aux, 300, 400, true);
                                fotosBitmap.set(i,aux);
                                fotos[i].setImageBitmap(aux);
                            }
                        }
                    });


                    pin1.requestFocus();

                    pin1.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View view, int i, KeyEvent keyEvent) {
                            if(i != KeyEvent.KEYCODE_DEL && i != KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP)
                                pin2.requestFocus();
                            return false;
                        }
                    });

                    pin2.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View view, int i, KeyEvent keyEvent) {
                            if(keyEvent.getAction() == KeyEvent.ACTION_UP) {
                                if(i != KeyEvent.KEYCODE_DEL && i != KeyEvent.KEYCODE_BACK  && keyEvent.getAction() == KeyEvent.ACTION_UP){
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
                                    subirFotos.setEnabled(true);
                                }
                            }
                            return false;
                        }
                    });


                    terminarButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(pin1.getText().length() > 0 && pin2.getText().length() > 0 && pin3.getText().length() > 0 && pin4.getText().length() > 0){
                                String pinFinal = pin1.getText().toString()+pin2.getText().toString()+pin3.getText().toString()+pin4.getText().toString();
                                final Habitante miHabitante = (Habitante) getArguments().getSerializable("habitante");

                                miHabitante.setPrimeraVez(false);
                                miHabitante.setPin(pinFinal);

                                pin1.setEnabled(false);
                                pin2.setEnabled(false);
                                pin3.setEnabled(false);
                                pin4.setEnabled(false);
                                terminarButton.setEnabled(false);
                                rotarFotos.setEnabled(false);
                                subirFotos.setEnabled(false);

                                pinRegistroLayout.setVisibility(View.GONE);
                                registroProgressBar.setVisibility(View.VISIBLE);

                                new AsyncTask<Habitante,Integer,Habitante>(){

                                    @Override
                                    protected Habitante doInBackground(Habitante... habitante) {
                                        Habitante miHabitante = habitante[0];
                                        ConexionBD.getInstancia().ModificarHabitante(miHabitante);
                                        ConexionBD.getInstancia().CrearFotos(miHabitante.getId(),miHabitante.getNombres(), miHabitante.getPin(), fotosBitmap);
                                        return miHabitante;
                                    }

                                    @Override
                                    protected void onPostExecute(Habitante habitante) {
                                        super.onPostExecute(habitante);
                                        registroProgressBar.setVisibility(View.GONE);
                                        pinRegistroLayout.setVisibility(View.VISIBLE);
                                        //Toast.makeText(getContext(), "¡Registro exitoso!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getContext(),MainActivity.class);
                                        intent.putExtra("habitante",habitante);

                                        getContext().startActivity(intent);

                                        registroActivity.finish();

                                    }
                                }.execute(miHabitante);

                            }else{
                                
                            }
                        }
                    });

                    break;
                }
            }

            return rootView;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getClipData() != null){
                if(data.getClipData().getItemCount() >= 10){
                    try {
                        fotosBitmap = new ArrayList<Bitmap>();
                        for(int i=0; i<data.getClipData().getItemCount(); i++){
                            Uri imagenUri = data.getClipData().getItemAt(i).getUri();

                            InputStream imageStream = context.getContentResolver().openInputStream(imagenUri);
                            Bitmap foto = BitmapFactory.decodeStream(imageStream);

                            try {
                                foto = rotateImageIfRequired(foto, imagenUri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            foto = Bitmap.createScaledBitmap(foto, 450, 600, true);

                            fotos[i].setImageBitmap(foto);

                            fotosBitmap.add(foto);
                        }
                        // new PruebaTask().execute(fotos.toArray(new Bitmap[fotos.size()]));
                        //registrar.setEnabled(true);
                        rotarFotos.setEnabled(true);
                        terminarButton.setEnabled(true);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }else{
                    //Toast.makeText(this, "Debes cargar al menos 10 fotografías", Toast.LENGTH_SHORT).show();

                }
            }else{
                //Toast.makeText(this, "Debes cargar al menos 10 fotografías", Toast.LENGTH_SHORT).show();
            }
        }

        private static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {

            InputStream input = context.getContentResolver().openInputStream(selectedImage);
            ExifInterface ei;
            if (Build.VERSION.SDK_INT > 23)
                ei = new ExifInterface(input);
            else
                ei = new ExifInterface(selectedImage.getPath());

            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    anguloRotacion = 90;
                    return rotarImagen(img, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    anguloRotacion = 180;
                    return rotarImagen(img, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    anguloRotacion = 270;
                    return rotarImagen(img, 270);
                default:
                    return img;
            }
        }


        private static Bitmap rotarImagen(Bitmap img, int degree) {
            Matrix matrix = new Matrix();
            matrix.postRotate(degree);
            Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
            img.recycle();
            return rotatedImg;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1, miHabitante, getApplicationContext());
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

}
