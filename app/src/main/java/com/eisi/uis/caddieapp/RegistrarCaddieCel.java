package com.eisi.uis.caddieapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegistrarCaddieCel extends AppCompatActivity {

    // Elementos UI
    private EditText editTextCelular;
    private Button siguienteActivity;

    // Otros valores
    private String name = "";
    private String apellidos = "";
    private String alias = "";
    private String foto = "";
    private String age = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_caddie_cel);

        // Activar la flecha para volver al activity principal
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Recogemos el nombre, apellido, alias y edad  del activity anterior
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name      = bundle.getString("name");
            apellidos = bundle.getString("apellidos");
            alias     = bundle.getString("alias");
            foto      = bundle.getString("foto");
            age       = bundle.getString("age");

        }
        // Instanciamos los elementos de la UI con sus referencias
        this.editTextCelular     = findViewById(R.id.editTextCelular);
        this.siguienteActivity   = findViewById(R.id.buttonToFourthActivity);

        // Evento click del botón para pasar al siguiente Activity
        siguienteActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String celular = editTextCelular.getText().toString();

                if(celular.isEmpty()){
                    Toast.makeText(RegistrarCaddieCel.this, "Introduce your phone number", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(RegistrarCaddieCel.this, RegistrarCaddieCat.class);
                    intent.putExtra("name", name);
                    intent.putExtra("apellidos", apellidos);
                    intent.putExtra("alias", alias);
                    intent.putExtra("age", age);
                    intent.putExtra("foto", foto);
                    intent.putExtra("celular", celular);
                    startActivity(intent);

                }

            }
        });






    }
}
