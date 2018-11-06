package com.eisi.uis.caddieapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegistrarCaddieNombres extends AppCompatActivity {

    private EditText editTextNombres;
    private EditText editTextApellidos;
    private EditText editTextAlias;
    private Button buttonsiguienteActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_caddie_nombres);

        // Activar flecha ir atrás
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.editTextNombres   = findViewById(R.id.editTextNombres);
        this.editTextApellidos = findViewById(R.id.editTextApellidos);
        this.editTextAlias     = findViewById(R.id.editTextAlias);
        this.buttonsiguienteActivity = findViewById(R.id.buttonToFotoActivity);

        // Evento click del botón para pasar al siguiente Activity
        buttonsiguienteActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name   = editTextNombres.getText().toString();
                String apellidos = editTextApellidos.getText().toString();
                String alias     = editTextAlias.getText().toString();

                if (name.isEmpty() || apellidos.isEmpty() || alias.isEmpty()) {
                    Toast.makeText(RegistrarCaddieNombres.this, "Por favor llene todos los campos", Toast.LENGTH_SHORT).show();
                }else {

                    //Guardar el nombre, apellidos y alias  del caddie en el archivo de SharedPreferences
                    SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit();
                    editor.putString("name", name );
                    editor.putString("apellidos", apellidos);
                    editor.putString("alias", alias);
                    editor.apply();

                    //Se lanza el intent
                    Intent intentFoto = new Intent(RegistrarCaddieNombres.this, RegistrarCaddieFoto.class);
                    intentFoto.putExtra("name", name);
                    intentFoto.putExtra("apellidos", apellidos);
                    intentFoto.putExtra("alias", alias);
                    startActivity(intentFoto);

                }
            }
        });
    }

}
