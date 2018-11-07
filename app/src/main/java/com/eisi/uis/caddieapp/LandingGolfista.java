package com.eisi.uis.caddieapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Descripción: Clase para el usuario golfista.
 */
public class LandingGolfista extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_golfista);

        // Instanciamos los elementos de la UI con sus referencias.
        TextView txtNombreGolfista = findViewById(R.id.txtNombreGolfista);
        TextView txtCarne          = findViewById(R.id.txtCarne);
        TextView txtEdad           = findViewById(R.id.txtEdad);
        Button reservarCaddie      = findViewById(R.id.bttReservar);
        Button verReservas         = findViewById(R.id.bttVerReservas);

        txtNombreGolfista.setText("Tiger Woods");
        txtCarne.setText("2120063");
        txtEdad.setText("24");

        /**
         * Onclick del boton Reservar Caddie
         */
        reservarCaddie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LandingGolfista.this, Caddies.class);
                intent.putExtra("fromView", "Golfista");
                startActivity(intent);
            }
        });

        /**
         * Onclick del boton Ver reservas Vigentes
         */
        verReservas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LandingGolfista.this, ReservasGolfista.class);
                startActivity(intent);
            }
        });
    }

}
