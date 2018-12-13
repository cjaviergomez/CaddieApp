package com.eisi.uis.caddieapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Caddies extends AppCompatActivity {

    private List<_PojoCaddie> listaCaddies;

    private RecyclerView mRecyclerView;
    // Puede ser declarado como 'RecyclerView.Adapter' o como nuetra clase adaptador 'MyAdapter'
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public static String fromView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caddies);

        // Instanciamos los elementos de la UI con sus referencias.
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);

        this.listaCaddies  = new ArrayList<>();


        // Tomar los datos del Intent.
        Bundle bundle = getIntent().getExtras();

        if (bundle != null && bundle.getString("fromView") != null) {
            String greeter = bundle.getString("fromView");

            this.fromView = greeter;
        } else {
            Toast.makeText(Caddies.this, "It's empty.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        this.listaCaddies.clear();


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

                                _PojoCaddie caddie = new _PojoCaddie();
                                caddie.setId(caddieID);
                                caddie.setNombres((String) singleCaddie.get("nombres"));
                                caddie.setApellidos((String) singleCaddie.get("apellidos"));
                                caddie.setAlias((String) singleCaddie.get("alias"));
                                caddie.setFoto((String) singleCaddie.get("foto"));
                                caddie.setEdad((String) singleCaddie.get("edad"));
                                caddie.setCelular((String) singleCaddie.get("celular"));
                                caddie.setCategoria((String) singleCaddie.get("categoria"));
                                caddie.setEstado((String) singleCaddie.get("estado"));

                                //Almacemos los caddies en un array.
                                listaCaddies.add(caddie);

                            }
                            //Ordenamos la lista de los caddies por nombre y apellido.
                            Collections.sort(listaCaddies, new Comparator<_PojoCaddie>(){
                                @Override
                                public int compare(_PojoCaddie c1, _PojoCaddie c2) {
                                    return (c1.nombres + c1.apellidos).compareTo(c2.nombres + c2.apellidos);
                                }
                            });

                            //Enlazamos con nuestro adaptador personalizado
                            // Implementamos nuestro OnItemClickListener propio, sobreescribiendo el método que nosotros
                            // definimos en el adaptador, y recibiendo los parámetros que necesitamos
                            mAdapter = new MyAdapter(listaCaddies, R.layout.list_caddies,Caddies.this, fromView, new MyAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(_PojoCaddie caddie, int position) {
                                    String selectionID = caddie.id;
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

                            // Lo usamos en caso de que sepamos que el layout no va a cambiar de tamaño, mejorando la performance
                            mRecyclerView.setHasFixedSize(true);
                            // Añade un efecto por defecto, si le pasamos null lo desactivamos por completo
                            mRecyclerView.setItemAnimator(new DefaultItemAnimator());

                            // Enlazamos el layout manager y adaptor directamente al recycler view
                            mRecyclerView.setLayoutManager(mLayoutManager);
                            mRecyclerView.setAdapter(mAdapter);
                            // No registramos para el menu contexto nada aquí, lo movemos al ViewHolder del adaptador

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
}

