package com.safehome.disenosoft.safehome.Servicios;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ConexionBD {

    //----------Singleton--------------
    private static ConexionBD instancia;
    private ConexionBD(){   }
    public static ConexionBD getInstancia(){
        if(instancia == null)
            instancia = new ConexionBD();
        return instancia;
    }
    //---------------------------------

    private static String urlBuscarHabitante = "https://api.mlab.com/api/1/databases/safe_home/collections/habitantes?q=%s&apiKey=JFoAa9aNXLSup9OeTp9UwsH0vVP1w4j9";
    private static String urlModificarHabitante = "https://api.mlab.com/api/1/databases/safe_home/collections/habitantes?apiKey=JFoAa9aNXLSup9OeTp9UwsH0vVP1w4j9&q=%s";
    private static String urlCrearHabitante = "https://api.mlab.com/api/1/databases/safe_home/collections/habitantes?apiKey=JFoAa9aNXLSup9OeTp9UwsH0vVP1w4j9";
    private static String urlCrearFotos = "https://api.mlab.com/api/1/databases/safe_home/collections/fotos?apiKey=JFoAa9aNXLSup9OeTp9UwsH0vVP1w4j9";
    private static String urlModificarFotos = "https://api.mlab.com/api/1/databases/safe_home/collections/fotos?apiKey=JFoAa9aNXLSup9OeTp9UwsH0vVP1w4j9&q=%s";


    private static String urlModificarSistema = "https://api.mlab.com/api/1/databases/safe_home/collections/sistema?apiKey=JFoAa9aNXLSup9OeTp9UwsH0vVP1w4j9&q=%s";
	
	
    public Habitante ObtenerHabitante(String correo){
        URL url;
        HttpURLConnection urlConexion = null;
        Habitante retorno = null;

        try {
            url = new URL(String.format(urlBuscarHabitante,URLEncoder.encode(String.format("{\"_id\": \"%s\"}",correo),"utf-8")));
            urlConexion = (HttpURLConnection) url.openConnection();
            urlConexion.setRequestMethod("GET");
            urlConexion.setRequestProperty("Content-Type","application/json;charset=utf-8");
            urlConexion.setRequestProperty("Accept","application/json");
            urlConexion.connect();

            int respuesta = urlConexion.getResponseCode();
            InputStreamReader inputStream = null;

            if(respuesta >= 200 && respuesta <400){
                inputStream = new InputStreamReader(urlConexion.getInputStream());
            }else{
                inputStream = new InputStreamReader(urlConexion.getErrorStream());
            }

            BufferedReader br = new BufferedReader(inputStream);
            String temp = null;
            do{
                temp = br.readLine();
            }while (br.ready());


            JSONArray array = new JSONArray(temp);

            if(array.length() > 0){
                JSONObject jsonObject = array.getJSONObject(0);

                retorno = new Habitante(
                        jsonObject.getString("_id"),
                        jsonObject.getString("nombres"),
                        jsonObject.getString("apellidos"),
                        jsonObject.getInt("tipoCuenta"),
                        jsonObject.getBoolean("primeraVez"),
                        jsonObject.getString("pin"),
                        jsonObject.getString("foto")
                );
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            urlConexion.disconnect();
        }
        return retorno;
    }


}
