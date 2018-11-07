package com.eisi.uis.caddieapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instanciamos los elementos de la UI con sus referencias.
        Button btnIngresarGolfista     = findViewById(R.id.btnIngresarGolfista);
        Button btnIngresarCaddieMaster = findViewById(R.id.btnIngresarCaddieMaster);
        final Button btnUrl                  = findViewById(R.id.buttonURL);


        /** Metodo Onclick del boton Ingresar como Caddie Master */
        btnIngresarCaddieMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LandingCaddieMaster.class);
                startActivity(intent);
            }
        });

        /** Metodo Onclick del boton Ingresar como Golfista */
        btnIngresarGolfista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LandingGolfista.class);
                startActivity(intent);

            }
        });

        /** Metodo Onclick del intent implicito para ir a la página web del club */
        btnUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String URL = btnUrl.getText().toString();
                Intent intentURL = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + URL));
                startActivity(intentURL);
            }
        });

    }

}
