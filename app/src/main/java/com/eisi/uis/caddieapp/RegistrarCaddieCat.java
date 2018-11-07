package com.eisi.uis.caddieapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

public class RegistrarCaddieCat extends AppCompatActivity {

    // Elementos UI
    private Button btnNext;
    private RadioButton radioButtonPrimera;
    private RadioButton radioButtonSegunda;
    private RadioButton radioButtonTercera;

    // Otros valores
    private String name = "";
    private String apellidos = "";
    private String alias = "";
    private String foto = "";
    private String age = "";
    private String celular = "";

    // Para compartir
    public static final String PRIMERA_OPTION = "PRIMERA";
    public static final String SEGUNDA_OPTION = "SEGUNDA";
    public static final String TERCERA_OPTION = "TERCERA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_caddie_cat);

        // Recogemos el nombre, apellido, alias, edad y el celular  del activity anterior
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name      = bundle.getString("name");
            apellidos = bundle.getString("apellidos");
            alias     = bundle.getString("alias");
            foto      = bundle.getString("foto");
            age       = bundle.getString("age");
            celular   = bundle.getString("celular");
        }

        // Instanciamos los elementos de la UI con sus referencias
        this.radioButtonPrimera   = findViewById(R.id.radioButtonPrimera);
        this.radioButtonSegunda   = findViewById(R.id.radioButtonSegunda);
        this.radioButtonTercera   = findViewById(R.id.radioButtonTercera);
        this.btnNext              = findViewById(R.id.buttonToFiveActivity);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrarCaddieCat.this, RegistrarCaddieEst.class);
                intent.putExtra("name", name);
                intent.putExtra("apellidos", apellidos);
                intent.putExtra("alias", alias);
                intent.putExtra("age", age);
                intent.putExtra("celular", celular);
                intent.putExtra("foto", foto);
                // Si el botón de Primera esta activo, option valdrá 1, si Segunda esta activo, option valdrá 2, si no, 3
                String categoria = (radioButtonPrimera.isChecked()) ? PRIMERA_OPTION : (radioButtonSegunda.isChecked()) ? SEGUNDA_OPTION: TERCERA_OPTION;
                intent.putExtra("categoria", categoria);
                startActivity(intent);
            }
        });

    }
}
