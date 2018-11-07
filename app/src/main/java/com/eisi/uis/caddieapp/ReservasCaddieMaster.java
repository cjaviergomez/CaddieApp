package com.eisi.uis.caddieapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ReservasCaddieMaster extends AppCompatActivity {

    private ArrayList<String> reservasIDs;
    private ArrayList<String> reservasName;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservas_caddie_master);

        // Instanciamos los elementos de la UI con sus referencias.
        this.lv = (ListView) findViewById(R.id.listViewReservas);
        this.reservasIDs = new ArrayList<>();
        this.reservasName = new ArrayList<>();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectionID = ReservasCaddieMaster.this.reservasIDs.get(position);
                Intent intent = new Intent(ReservasCaddieMaster.this, ReservaCaddieMaster.class);
                intent.putExtra("reservaID", selectionID);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.reservasIDs.clear();
        this.reservasName.clear();

        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild("reservas")) {
                    Toast.makeText(ReservasCaddieMaster.this, "No existen reservas!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    DatabaseReference dbReservas = dbRef.child("reservas");
                    dbReservas.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Map<String, Object> reservas = (Map<String, Object>) dataSnapshot.getValue();

                            for (Map.Entry<String, Object> entry : reservas.entrySet()) {
                                String reservaID = entry.getKey();
                                Map singleReserva = (Map) entry.getValue();

                                if (!singleReserva.get("estado").equals("Pendiente")) continue;

                                String caddieNombre = (String) singleReserva.get("infocaddie");
                                String golfistaNombre = (String) singleReserva.get("golfista");

                                reservasIDs.add(reservaID);
                                reservasName.add(golfistaNombre + " (" + caddieNombre + ")");
                            }

                            final List<String> reservasNames_List = new ArrayList<>(reservasName);

                            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                                    (ReservasCaddieMaster.this, android.R.layout.simple_list_item_1, reservasNames_List);

                            lv.setAdapter(arrayAdapter);

                            arrayAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(ReservasCaddieMaster.this, "Error DB", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ReservasCaddieMaster.this, "Error DB", Toast.LENGTH_LONG).show();
            }
        });
    }
}
