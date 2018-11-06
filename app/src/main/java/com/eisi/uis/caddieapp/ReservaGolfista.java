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

public class ReservaGolfista extends AppCompatActivity {

    private String reservaID;
    private TextView textViewNombreCaddie;
    private TextView textViewEstado;
    private ImageView eliminarReserva;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva_golfista);

        this.textViewNombreCaddie = findViewById(R.id.textView21);
        this.textViewEstado = findViewById(R.id.textView25);
        this.eliminarReserva = findViewById(R.id.imageViewEliminar);

        eliminarReserva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                dbRef.child("reservas/" + reservaID).removeValue();
                finish();
                Toast.makeText(ReservaGolfista.this, "Reserva eliminada", Toast.LENGTH_LONG).show();
            }
        });

        // Tomar los datos del Intent.
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            this.reservaID = bundle.getString("reservaID");

            //Toast.makeText(this, "Reserva ID (Golfista): " + reservaID, Toast.LENGTH_SHORT).show();

            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference dbCaddies = dbRef.child("reservas/" + this.reservaID);

            dbCaddies.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    _PojoReserva reserva = dataSnapshot.getValue(_PojoReserva.class);

                    textViewNombreCaddie.setText(reserva.infocaddie);
                    textViewEstado.setText(reserva.estado);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(ReservaGolfista.this, "Error DB", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(this, "It's empty.", Toast.LENGTH_SHORT).show();
        }
    }

}
