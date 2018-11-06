package com.eisi.uis.caddieapp;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrarCaddieEst extends AppCompatActivity {

    // Elementos UI
    private Button btnRegistrar;
    private RadioButton radioButtonDisponble;
    private RadioButton radioButtonOcupado;
    private RadioButton radioButtonPFC;

    // Otros valores
    private String name = "";
    private String apellidos = "";
    private String alias = "";
    private String foto = "";
    private String age = "";
    private String celular = "";
    private String categoria = "";

    // Para compartir
    public static final String PRIMERA_OPTION = "DISPONIBLE";
    public static final String SEGUNDA_OPTION = "OCUPADO";
    public static final String TERCERA_OPTION = "PFC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_caddie_est);

        // Activar la flecha para volver al activity principal
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Recogemos el nombre, apellido, alias, edad, celular y la categoria del activity anterior
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name      = bundle.getString("name");
            apellidos = bundle.getString("apellidos");
            alias     = bundle.getString("alias");
            foto      = bundle.getString("foto");
            age       = bundle.getString("age");
            celular   = bundle.getString("celular");
            categoria = bundle.getString("categoria");
        }

        // Instanciamos los elementos de la UI con sus referencias
        this.radioButtonDisponble   = findViewById(R.id.radioButtonDisponible);
        this.radioButtonOcupado     = findViewById(R.id.radioButtonOcupado);
        this.radioButtonPFC         = findViewById(R.id.radioButtonPFC);
        this.btnRegistrar           = findViewById(R.id.buttonToRegister);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Si el botón de Primera esta activo, option valdrá 1, si Segunda esta activo, option valdrá 2, si no, 3
                String estado = (radioButtonDisponble.isChecked()) ? PRIMERA_OPTION : (radioButtonOcupado.isChecked()) ? SEGUNDA_OPTION: TERCERA_OPTION;

                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

                _PojoCaddie pojoCaddie = new _PojoCaddie();

                pojoCaddie.setNombres( name );
                pojoCaddie.setApellidos( apellidos );
                pojoCaddie.setAlias( alias );
                pojoCaddie.setFoto(foto);
                pojoCaddie.setEdad( age );
                pojoCaddie.setCelular(celular);
                pojoCaddie.setCategoria( categoria );
                pojoCaddie.setEstado( estado );

                DatabaseReference newCaddieRef = dbRef.child("caddies").push();
                newCaddieRef.setValue(pojoCaddie);

                Toast.makeText(RegistrarCaddieEst.this, "Caddie Registrado", Toast.LENGTH_SHORT).show();

                //Aquí creamos el caddie y finalizamos todas las activitys de registrar caddie, quedando unicamente el activity
                //LandigCaddieMaster activo.
                //Se hace de esta forma y se planea en un futuro hacerlo madiante FRAGMENTS.
                Intent intent = new Intent(RegistrarCaddieEst.this, LandingCaddieMaster.class);
                ComponentName cn = intent.getComponent();
                Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
                startActivity(mainIntent);

            }
        });
    }
}
