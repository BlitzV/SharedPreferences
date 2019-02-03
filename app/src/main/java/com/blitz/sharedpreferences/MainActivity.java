package com.blitz.sharedpreferences;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import static android.Manifest.permission.CHANGE_CONFIGURATION;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    private EditText simplepreference;
    private TextView file;
    private Button savesimplepreference, savefile, readfile;
    private static final int MY_PERMISSION_WRITE_EXTERNAL_DATA_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitValueMethodOne();
        InitValueMethodTwo();
        permissionWriteSD();
    }

    public void InitValueMethodOne(){
        simplepreference = (EditText) findViewById(R.id.stringpreferencesimple);
        savesimplepreference = (Button) findViewById(R.id.savepreferencessimple);

        SharedPreferences preferences = getSharedPreferences("Lista", Context.MODE_PRIVATE);
        simplepreference.setText(preferences.getString("nombre", null));

    }

    public void SaveSimplePreference (View view){
        String data = simplepreference.getText().toString();
        SharedPreferences sharedPreferences = getSharedPreferences("Lista", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("nombre", data);
        editor.commit();
    }

    public void InitValueMethodTwo(){
        file = (TextView) findViewById(R.id.file);
        savefile = (Button) findViewById(R.id.savefile);
        readfile = (Button) findViewById(R.id.readfile);
    }

    public void SaveFile(View view) {
        SaveLocal();
        SaveSD();
    }

    public void ReadFile(View view) {
        ReadLocal();
        ReadSD();
    }

    public void SaveLocal (){
        String fileName = "Archivo";
        String fileContent = "First File";

        FileOutputStream outputStream = null;

        try {
            outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(fileContent.getBytes());
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Toast.makeText(this, "Guardado con exito", Toast.LENGTH_SHORT).show();
    }

    public void ReadLocal (){
        try {
            FileInputStream fileInputStream = getApplicationContext().openFileInput("Archivo"); //Espacio asignado para el nombre de Archivo
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8"); //Traemos un formato de 0 y 1 para crear un archivo
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader); // Sostiene y recibe el archivo
            StringBuilder stringBuilder = new StringBuilder(); // esta es una cadena mutable /// por lo general las cadenas en java son inmutables, no puedes sobreescribir su valor, por debajo no sobrescribe sino que crea un nuevo string
            String line;

            while((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line).append("\n"); //append para pegar una linea con otra
            }

            Toast.makeText(this, "Leyendo...", Toast.LENGTH_SHORT).show();

            file.setText(stringBuilder.toString()); // ver contenido del archivo

        } catch (FileNotFoundException f){
            f.printStackTrace();
        } catch (UnsupportedEncodingException u) {
            u.printStackTrace();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    private void SaveSD() {
        try{
            File files = new File("/sdcard/myfile.txt");
            files.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(files);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream); //permite trabajar con elementos mas grandes, mas extensos, si son muy grandes los archivos, los manejamos con OutputStreamWriter
            outputStreamWriter.append("Este text es para el archivo sd que se crea");
            outputStreamWriter.close();
            fileOutputStream.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void ReadSD() {
        try {
            File files = new File("/sdcard/myfile.txt");
            FileInputStream fileInputStream = new FileInputStream(files);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String aDataRow = "";
            String aBuffer = "";

            while ((aDataRow = bufferedReader.readLine())!=null){
                    aBuffer += aDataRow;
            }

            file.setText(aBuffer.toString());
            fileInputStream.close();
            bufferedReader.close();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void permissionWriteSD (){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    WRITE_EXTERNAL_STORAGE)) {

                final AlertDialog.Builder dialogo=new AlertDialog.Builder(MainActivity.this);
                dialogo.setTitle("Solicitud de penmiso");
                dialogo.setMessage("Esta App requiere su aprobacion para escribir en la tarjeta SD");
                dialogo.setPositiveButton("Aceptar", new  DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,  CHANGE_CONFIGURATION},
                                    MY_PERMISSION_WRITE_EXTERNAL_DATA_STORAGE);
                        }
                    }
                });
                dialogo.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_WRITE_EXTERNAL_DATA_STORAGE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_WRITE_EXTERNAL_DATA_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permiso aceptado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "No se escribira en SD hasta que acepte el permiso", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

}
