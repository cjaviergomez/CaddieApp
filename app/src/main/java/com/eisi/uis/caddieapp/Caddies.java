package com.eisi.uis.caddieapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Caddies extends AppCompatActivity {

    private ArrayList<String> caddiesIDs;
    private ArrayList<String> caddiesNames;
    private ArrayList<String> caddiesEstados;
    private List<String> caddiesNames_List;
    private ListView lv;
    private String fromView;
    private Myadapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caddies);

        // Instanciamos los elementos de la UI con sus referencias.
        this.lv = (ListView) findViewById(R.id.listViewCaddies);
        this.caddiesIDs = new ArrayList<>();
        this.caddiesNames = new ArrayList<>();
        this.caddiesEstados = new ArrayList<>();

        //Activar el context menu
        registerForContextMenu(lv);

        // Tomar los datos del Intent.
        Bundle bundle = getIntent().getExtras();

        if (bundle != null && bundle.getString("fromView") != null) {
            String greeter = bundle.getString("fromView");

            this.fromView = greeter;
        } else {
            Toast.makeText(Caddies.this, "It's empty.", Toast.LENGTH_SHORT).show();
        }

        //Se da cuando se le haga un click a un item de nuestro listView
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selectionID = Caddies.this.caddiesIDs.get(position);
                Intent intent = new Intent(); // Me toca crear el new Intent(), si no me da error la línea de putExtra

                if (Caddies.this.fromView.equals("CaddieMaster")) {
                    intent = new Intent(Caddies.this, CaddieCaddieMaster.class);
                } else if (Caddies.this.fromView.equals("Golfista")) {
                    intent = new Intent(Caddies.this, CaddieGolfista.class);
                }

                intent.putExtra("caddieID", selectionID);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.caddiesIDs.clear();
        this.caddiesNames.clear();
        this.caddiesEstados.clear();

        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild("caddies")) {
                    Toast.makeText(Caddies.this, "No existen caddies!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    DatabaseReference dbCaddies = dbRef.child("caddies");
                    dbCaddies.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Map<String, Object> caddies = (Map<String, Object>) dataSnapshot.getValue();

                            //Recorremos todos los caddies existentes en la DB
                            for (Map.Entry<String, Object> entry : caddies.entrySet()) {
                                String caddieID = entry.getKey();
                                Map singleCaddie = (Map) entry.getValue();

                                //Extramos datos como ID, nombre, apellido y el estado.
                                String caddieNombres   = (String) singleCaddie.get("nombres");
                                String caddieApellidos = (String) singleCaddie.get("apellidos");
                                String caddieEstado    = (String) singleCaddie.get("estado");

                                //Almacemos los datos en un array.
                                caddiesIDs.add(caddieID);
                                caddiesNames.add(caddieNombres + " " + caddieApellidos);
                                caddiesEstados.add(caddieEstado);
                            }

                            //Lista con los datos a mostrar.
                            caddiesNames_List = new ArrayList<String>(caddiesNames);

                            /*//Adaptador, la forma visual en que mostraremos nuestros datos.
                            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                                    (Caddies.this, android.R.layout.simple_list_item_1, caddiesNames_List);

                            //Enlazamos el adaptador con nuestro List View
                            lv.setAdapter(arrayAdapter);*/

                            //Enlazamos con nuestro adaptador personalizado
                            myAdapter = new Myadapter(Caddies.this, R.layout.list_caddies, caddiesNames_List);

                            //Enlazamos el adaptador personalizado con nuestro List View
                            lv.setAdapter(myAdapter);

                            myAdapter.notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(Caddies.this, "Error DB", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Caddies.this, "Error DB", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Inflamos el layout del context menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(this.caddiesNames_List.get(info.position));
        if (Caddies.this.fromView.equals("CaddieMaster")){
            inflater.inflate(R.menu.context_menu, menu);
        }else{
            inflater.inflate(R.menu.context_menu_golfista, menu);
        }

    }

    // Manejamos eventos click en el context menu
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.delete_caddie:
                // Borramos caddie clickeado
                this.caddiesNames_List.remove(info.position);
                //Borramos de la DB
                String caddieID = Caddies.this.caddiesIDs.get(info.position);
                final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                dbRef.child("caddies/" + caddieID).removeValue();

                // Notificamos al adaptador del cambio producido
                myAdapter.notifyDataSetChanged();

                //Revisamos si con el cambio la base de datos quedó vacia.
                dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.hasChild("caddies")) {
                            Toast.makeText(Caddies.this, "No existen caddies!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(Caddies.this, "Error DB", Toast.LENGTH_LONG).show();
                    }
                });

                return true;
            case R.id.edit_caddie:
                String IDcaddie = Caddies.this.caddiesIDs.get(info.position);
                Intent intent = new Intent(Caddies.this, EditarCaddie.class);
                intent.putExtra("caddieID", IDcaddie);
                startActivity(intent);
                return true;

            case R.id.ver_caddie:
                String selectionID = Caddies.this.caddiesIDs.get(info.position);
                Intent intentvercaddie = new Intent(); // Me toca crear el new Intent(), si no me da error la línea de putExtra

                if (Caddies.this.fromView.equals("CaddieMaster")) {
                    intentvercaddie = new Intent(Caddies.this, CaddieCaddieMaster.class);
                } else if (Caddies.this.fromView.equals("Golfista")) {
                    intentvercaddie = new Intent(Caddies.this, CaddieGolfista.class);
                }

                intentvercaddie.putExtra("caddieID", selectionID);
                startActivity(intentvercaddie);
                return true;

            case R.id.reservar_caddie:

                String ID = Caddies.this.caddiesIDs.get(info.position);
                String infoCaddie = this.caddiesNames_List.get(info.position);
                String estado = Caddies.this.caddiesEstados.get(info.position);

                if(estado.equals("Disponible")) {
                    DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference();
                    _PojoReserva pojoReserva = new _PojoReserva();
                    pojoReserva.setCaddie(ID);
                    pojoReserva.setGolfista("Tiger Woods");
                    pojoReserva.setEstado("Pendiente");
                    pojoReserva.setInfocaddie(infoCaddie);

                    DatabaseReference newReserva = DbRef.child("reservas").push();
                    newReserva.setValue(pojoReserva);
                    Toast.makeText(Caddies.this, "Reserva Creada", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(Caddies.this, "Caddie no disponible", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


}

