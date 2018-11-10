package com.eisi.uis.caddieapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservasCaddieMaster extends AppCompatActivity {

    private ArrayList<String> reservasIDs;
    private ArrayList<String> reservasName;
    private ListView lv;
    private String reservaID;  //Id de la reserva a utilizar en el context menu
    private String caddieID;   //Id del caddie a utilizar en el context menu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservas_caddie_master);

        // Instanciamos los elementos de la UI con sus referencias.
        this.lv = (ListView) findViewById(R.id.listViewReservas);
        this.reservasIDs = new ArrayList<>();
        this.reservasName = new ArrayList<>();

        //Activar el context menu
        registerForContextMenu(lv);

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
                            if(reservasIDs.isEmpty()){
                                Toast.makeText(ReservasCaddieMaster.this, "No existen reservas pendientes!", Toast.LENGTH_SHORT).show();
                                finish();
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
    // Inflamos el layout del context menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(this.reservasName.get(info.position));

        inflater.inflate(R.menu.context_menu_reservas_master, menu);
    }

    // Manejamos eventos click en el context menu
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        //Obtenemos el ID de la reserva seleccionada.
        reservaID = this.reservasIDs.get(info.position);

        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference reservaRef = dbRef.child("reservas/" + this.reservaID);

        switch (item.getItemId()) {

            case R.id.aceptar_reserva:
                //Obtener el ID del caddie
                reservaRef.addValueEventListener(new ValueEventListener()  {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        _PojoReserva reserva = dataSnapshot.getValue(_PojoReserva.class);

                        caddieID = reserva.caddie;

                        final DatabaseReference caddieRef = dbRef.child("caddies/" + caddieID);
                        //Consultar el estado del caddie.
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

                                    Toast.makeText(ReservasCaddieMaster.this, "Reserva Aceptada", Toast.LENGTH_LONG).show();
                                    finish();
                                } else {
                                    Toast.makeText(ReservasCaddieMaster.this, "Caddie no disponible", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(ReservasCaddieMaster.this, "Error DB", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(ReservasCaddieMaster.this, "Error DB", Toast.LENGTH_LONG).show();
                    }
                });


                return true;
            case R.id.rechazar_reserva:

                Map<String, Object> reservaMap = new HashMap<>();
                reservaMap.put("estado", "Rechazada");

                reservaRef.updateChildren(reservaMap);

                Toast.makeText(ReservasCaddieMaster.this, "Reserva Rechazada", Toast.LENGTH_LONG).show();
                finish();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }
}
