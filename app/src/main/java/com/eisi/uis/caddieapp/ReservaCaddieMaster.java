package com.eisi.uis.caddieapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ReservaCaddieMaster extends AppCompatActivity {

    private String reservaID;
    private String caddieID;
    private TextView textViewNombreCaddie;
    private TextView textViewNombreGolfista;
    private ImageView aceptarReserva;
    private ImageView rechazarReserva;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva_caddie_master);

        this.textViewNombreCaddie = findViewById(R.id.textView21);
        this.textViewNombreGolfista = findViewById(R.id.textView25);

        this.aceptarReserva = findViewById(R.id.imageView5);
        aceptarReserva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                final DatabaseReference reservaRef = dbRef.child("reservas/" + reservaID);
                final DatabaseReference caddieRef = dbRef.child("caddies/" + caddieID);

                caddieRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        _PojoCaddie caddie = dataSnapshot.getValue(_PojoCaddie.class);

                        if (caddie.estado.equals("Disponible")) {
                            Map<String, Object> reservaMap = new HashMap<>();
                            reservaMap.put("estado", "Aceptada");

                            Map<String, Object> caddieMap = new HashMap<>();
                            caddieMap.put("estado", "Ocupado");

                            reservaRef.updateChildren(reservaMap);
                            caddieRef.updateChildren(caddieMap);

                            Toast.makeText(ReservaCaddieMaster.this, "Reserva Aceptada", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(ReservaCaddieMaster.this, "Caddie no disponible", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(ReservaCaddieMaster.this, "Error DB", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        this.rechazarReserva = findViewById(R.id.imageViewEliminar);
        rechazarReserva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference reservaRef = dbRef.child("reservas/" + reservaID);

                Map<String, Object> reservaMap = new HashMap<>();
                reservaMap.put("estado", "Rechazada");

                reservaRef.updateChildren(reservaMap);

                Toast.makeText(ReservaCaddieMaster.this, "Reserva Rechazada", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        // Tomar los datos del Intent.
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            this.reservaID = bundle.getString("reservaID");

            //Toast.makeText(this, "Reserva ID (CaddieMaster): " + reservaID, Toast.LENGTH_SHORT).show();

            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference dbCaddies = dbRef.child("reservas/" + this.reservaID);

            dbCaddies.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    _PojoReserva reserva = dataSnapshot.getValue(_PojoReserva.class);

                    caddieID = reserva.caddie;

                    textViewNombreCaddie.setText(reserva.infocaddie);
                    textViewNombreGolfista.setText(reserva.golfista);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(ReservaCaddieMaster.this, "Error DB", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(this, "It's empty.", Toast.LENGTH_SHORT).show();
        }
    }
}
