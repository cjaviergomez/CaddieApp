package com.eisi.uis.caddieapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Descripción: Clase para el usuario Caddie Master
 */
public class LandingCaddieMaster extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_caddie_master);

        Button verReservas = findViewById(R.id.button3);
        Button verCaddies  = findViewById(R.id.button4);
        Button registrarCaddie = findViewById(R.id.button5);

        // Activar flecha ir atrás
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /**
         * Onclick del boton Ver Reservas
         */
        verReservas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LandingCaddieMaster.this, ReservasCaddieMaster.class);
                startActivity(intent);
            }
        });
        /**
         * Onclick del botón Ver Caddies
         */
        verCaddies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LandingCaddieMaster.this, Caddies.class);
                intent.putExtra("fromView", "CaddieMaster");
                startActivity(intent);
            }
        });
        /**
         * Onclick del boton Registrar Caddie
         */
        registrarCaddie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LandingCaddieMaster.this, RegistrarCaddieNombres.class);
                startActivity(intent);
            }
        });
    }
}
