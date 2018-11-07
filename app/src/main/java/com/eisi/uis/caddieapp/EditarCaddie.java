package com.eisi.uis.caddieapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EditarCaddie extends AppCompatActivity {
    private EditText editTextNombres;
    private EditText editTextApellidos;
    private EditText editTextAlias;
    private EditText editTextEdad;
    private Spinner spinnerCategoria;
    private Spinner spinnerEstado;
    private String caddieID;
    private Button guardarCaddie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_caddie);

        // Instanciamos los elementos de la UI con sus referencias.
        this.editTextNombres   = findViewById(R.id.editTextNombres);
        this.editTextApellidos = findViewById(R.id.editTextApellidos);
        this.editTextAlias     = findViewById(R.id.editTextAlias);
        this.editTextEdad      = findViewById(R.id.editTextEdad);
        this.spinnerCategoria  = findViewById(R.id.spinnerCategoria);
        this.spinnerEstado     = findViewById(R.id.spinnerEstado);
        this.guardarCaddie     = findViewById(R.id.button6);

        String[] categoria = {"", "Primera", "Segunda", "Tercera"};
        spinnerCategoria.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoria));

        String[] estado = {"", "Disponible", "Ocupado", "PFC"};
        spinnerEstado.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, estado));

        // Tomar los datos del Intent.
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            this.caddieID = bundle.getString("caddieID");
            //Toast.makeText(this, "Caddie ID (CaddieMaster): " + this.caddieID, Toast.LENGTH_SHORT).show();

            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference dbCaddies = dbRef.child("caddies/" + this.caddieID);

            dbCaddies.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    _PojoCaddie caddie = dataSnapshot.getValue(_PojoCaddie.class);

                    editTextNombres.setText(caddie.nombres);
                    editTextApellidos.setText(caddie.apellidos);
                    editTextAlias.setText(caddie.alias);
                    editTextEdad.setText(caddie.edad);

                    if(caddie.categoria.equals("Primera")) {
                        spinnerCategoria.setSelection(1);
                    }else if (caddie.categoria.equals("Segunda")){
                        spinnerCategoria.setSelection(2);
                    }else{
                        spinnerCategoria.setSelection(3);
                    }

                    if(caddie.estado.equals("Disponible")) {
                        spinnerEstado.setSelection(1);
                    }else if (caddie.estado.equals("Ocupado")){
                        spinnerEstado.setSelection(2);
                    }else{
                        spinnerEstado.setSelection(3);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(EditarCaddie.this, "Error DB", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(this, "It's empty.", Toast.LENGTH_SHORT).show();
        }

        /**
         * Onclick del boton Guardar
         */
        guardarCaddie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombres = editTextNombres.getText().toString();
                String apellidos = editTextApellidos.getText().toString();
                String alias = editTextAlias.getText().toString();
                String edad = editTextEdad.getText().toString();
                String categoria = spinnerCategoria.getSelectedItem().toString();
                String estado = spinnerEstado.getSelectedItem().toString();

                if (nombres.equals("") || apellidos.equals("") || alias.equals("") || edad.equals("")
                        || categoria.equals("") || estado.equals("")) {
                    Toast.makeText(EditarCaddie.this, "Por favor llene todos los campos", Toast.LENGTH_SHORT).show();
                } else {

                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference reservaRef = dbRef.child("caddies/" + caddieID);

                    Map<String, Object> reservaMap = new HashMap<>();
                    reservaMap.put("alias", alias);
                    reservaMap.put("apellidos", apellidos);
                    reservaMap.put("categoria", categoria);
                    reservaMap.put("edad", edad);
                    reservaMap.put("estado", estado);
                    reservaMap.put("nombres", nombres);

                    reservaRef.updateChildren(reservaMap);

                    Toast.makeText(EditarCaddie.this, "Caddie Modificado", Toast.LENGTH_LONG).show();
                    finish();

                }
            }
        });

    }
}
