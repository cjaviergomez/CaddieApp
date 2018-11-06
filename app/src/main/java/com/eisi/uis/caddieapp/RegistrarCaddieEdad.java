package com.eisi.uis.caddieapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class RegistrarCaddieEdad extends AppCompatActivity {

    private TextView textViewAge;
    private Button btnNext;

    // Otros valores
    private String name = "";
    private String apellidos = "";
    private String alias = "";
    private String foto = "";
    private int age = 18;
    private final int MAX_AGE = 85;
    private final int MIN_AGE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_caddie_edad);

        // Activar la flecha para volver al activity principal
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Recogemos el nombre, apellido y alias  del activity anterior
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name      = bundle.getString("name");
            apellidos = bundle.getString("apellidos");
            alias     = bundle.getString("alias");
            foto      = bundle.getString("foto");
        }

        // Instanciamos los elementos de la UI con sus referencias
        SeekBar seekBarAge = (SeekBar) findViewById(R.id.seekBarAge);
        textViewAge = (TextView) findViewById(R.id.textViewCurrentAge);
        btnNext = (Button) findViewById(R.id.buttonToThirdActivity);


        // Evento change para el SeekBar
        seekBarAge.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int currentAge, boolean b) {
                age = currentAge;
                textViewAge.setText(age + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Aunque no lo sobreescribamos con alguna funcionalidad, OnSeekBarChangeListener es una interfaz
                // y como interfaz que es, necesitamos sobreescribir todos sus métodos, aunque lo dejemos vacío.
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Declaramos nuestras restricciones de edad en el evento en que el usuario suelta/deja el seekbar.
                age = seekBar.getProgress();
                textViewAge.setText(age + "");

                if (age > MAX_AGE) {
                    btnNext.setVisibility(View.INVISIBLE);
                    Toast.makeText(RegistrarCaddieEdad.this, "The max age allowed is: "+MAX_AGE+" years old.", Toast.LENGTH_LONG).show();
                } else if (age < MIN_AGE) {
                    btnNext.setVisibility(View.INVISIBLE);
                    Toast.makeText(RegistrarCaddieEdad.this, "The min age allowed is: "+MIN_AGE+" years old.", Toast.LENGTH_LONG).show();
                } else {
                    btnNext.setVisibility(View.VISIBLE);
                }
            }
        });


        // Evento click del botón para pasar al siguiente Activity
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String edad = textViewAge.getText().toString();
                Intent intent = new Intent(RegistrarCaddieEdad.this, RegistrarCaddieCel.class);
                intent.putExtra("name", name);
                intent.putExtra("apellidos", apellidos);
                intent.putExtra("alias", alias);
                intent.putExtra("foto", foto);
                intent.putExtra("age", edad);
                startActivity(intent);
            }
        });

    }
}
