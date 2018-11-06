package com.eisi.uis.caddieapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CaddieCaddieMaster extends AppCompatActivity {

    private TextView textViewNombres;
    private TextView textViewApellidos;
    private TextView textViewAlias;
    private TextView textViewEdad;
    private TextView textViewCategoria;
    private TextView textViewEstado;
    private TextView textViewNoCel;
    private ImageButton ImageButtonLlamada;
    private String caddieID;
    private ImageView editarCaddie;
    private ImageView eliminarCaddie;
    private ImageView imageViewfotoCaddie;

    private final int PHONE_CALL_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caddie_caddie_master);

        this.textViewNombres     = findViewById(R.id.textViewNombres);
        this.textViewApellidos   = findViewById(R.id.textViewApellidos);
        this.textViewAlias       = findViewById(R.id.textViewAlias);
        this.textViewEdad        = findViewById(R.id.textViewEdad);
        this.textViewCategoria   = findViewById(R.id.textViewCategoria);
        this.textViewEstado      = findViewById(R.id.textViewEstado);
        this.textViewNoCel       = findViewById(R.id.textViewNoCel);
        this.editarCaddie        = findViewById(R.id.imageViewEditar);
        this.eliminarCaddie      = findViewById(R.id.imageViewEliminar);
        this.ImageButtonLlamada  = findViewById(R.id.imageButtonLlamadaMaster);
        this.imageViewfotoCaddie = findViewById(R.id.imageViewfotoCaddieCaddieMaster);

        // Tomar los datos del Intent.
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            this.caddieID = bundle.getString("caddieID");
            //Toast.makeText(this, "Caddie ID (CaddieMaster): " + this.caddieID, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "It's empty.", Toast.LENGTH_SHORT).show();
        }

        editarCaddie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CaddieCaddieMaster.this, EditarCaddie.class);
                intent.putExtra("caddieID", caddieID);
                startActivity(intent);
            }
        });

        eliminarCaddie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                dbRef.child("caddies/" + caddieID).removeValue();
                finish();
            }
        });

        ImageButtonLlamada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = textViewNoCel.getText().toString();
                if (phoneNumber != null && !phoneNumber.isEmpty()) {
                    // Teléfono 2, sin permisos requeridos
                    Intent intentPhone = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                    startActivity(intentPhone);
                } else {
                    Toast.makeText(CaddieCaddieMaster.this, "Phone number is not valid", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dbCaddies = dbRef.child("caddies/" + this.caddieID);

        dbCaddies.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                _PojoCaddie caddie = dataSnapshot.getValue(_PojoCaddie.class);

                textViewNombres.setText(caddie.nombres);
                textViewApellidos.setText(caddie.apellidos);
                textViewAlias.setText(caddie.alias);
                textViewEdad.setText(caddie.edad + " años");
                textViewCategoria.setText(caddie.categoria);
                textViewEstado.setText(caddie.estado);
                textViewNoCel.setText(caddie.celular);

                Glide.with(getBaseContext())
                        .load(caddie.foto)
                        .fitCenter()
                        .centerCrop()
                        .into(imageViewfotoCaddie);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(CaddieCaddieMaster.this, "Error DB", Toast.LENGTH_LONG).show();
            }
        });
    }

}
