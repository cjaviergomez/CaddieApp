package com.eisi.uis.caddieapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegistrarCaddieFoto extends AppCompatActivity {

    // Elementos UI
    private Button buttonToEdadActivity;
    private Button buttonSubirFoto;
    private ImageView imageViewfotoCaddie;

    //Referencia al Storage de Firebase
    private StorageReference rStorage;
    private ProgressDialog progressDialog;
    private static final int GALLERY_INTENT = 1;

    // Valores del intent anterior
    private String name = "";
    private String apellidos = "";
    private String alias = "";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_caddie_foto);

        //Instaciamos el toolbar a utilizar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_name));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Recogemos el nombre, apellido y alias  del activity anterior
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name = bundle.getString("name");
            apellidos = bundle.getString("apellidos");
            alias = bundle.getString("alias");
        }

        // Instanciamos los elementos de la UI con sus referencias.
        this.buttonToEdadActivity = findViewById(R.id.buttonToEdadActivity);
        this.buttonSubirFoto = findViewById(R.id.buttonSubirFoto);
        this.imageViewfotoCaddie = findViewById(R.id.imageViewfotoCaddie);
        this.rStorage = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);

        //Onclick del boton Subir Foto
        buttonSubirFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent para abrir la galeria.
                Intent intentGallery = new Intent(Intent.ACTION_PICK);
                intentGallery.setType("image/*");
                startActivityForResult(intentGallery, GALLERY_INTENT);
            }
        });

        //Onclick del boton para la siguiente actividad
        buttonToEdadActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Obtener la URL de la foto desde el archivo de preferencias
                SharedPreferences preference = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
                String restoredURL = preference.getString("URL", null);

                if(restoredURL == null || imageViewfotoCaddie.getDrawable() == null){
                    Toast.makeText(RegistrarCaddieFoto.this, "Por favor seleccione una foto", Toast.LENGTH_LONG).show();
                }else {
                    Intent intentEdad = new Intent(RegistrarCaddieFoto.this, RegistrarCaddieEdad.class);
                    intentEdad.putExtra("name", name);
                    intentEdad.putExtra("apellidos", apellidos);
                    intentEdad.putExtra("alias", alias);
                    intentEdad.putExtra("foto", restoredURL);
                    Toast.makeText(RegistrarCaddieFoto.this, "URL: " + restoredURL + "Nombre:" + name, Toast.LENGTH_LONG).show();
                    startActivity(intentEdad);
                }
            }
        });
    }

    //Pasa la foto al storage
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //VERIFICA QUE SE HAYA SELECCIONADO UNA FOTO
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {

            //Abre la barra de espera mientras se sube la imagen al Storage.

            progressDialog.setTitle("Subiendo...");
            progressDialog.setMessage("Subiendo Foto");
            progressDialog.setCancelable(false);//para que al clickear fuera del cuadrado no se salga
            progressDialog.show();

            Uri uri = data.getData();
            //recibe la ruta de la foto o el nombre del archivo
            final StorageReference filePath = rStorage.child("fotos/caddies").child(uri.getLastPathSegment());

            //sube la foto a la carpeta en storage que acabamos de crear
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    progressDialog.dismiss();//finaliza la barra de carga

                    //OBTIENE EL URI DE LA FOTO
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //la librería glide se añade al gradle app y al project, para guardar imágenes en caché etc
                            Glide.with(getBaseContext())
                                    .load(uri)
                                    .fitCenter()
                                    .centerCrop()
                                    .into(imageViewfotoCaddie);

                            //Guardar la URL de la foto en SharedPreferences
                            SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit();
                            editor.putString("URL", uri.toString());
                            editor.apply();
                        }
                    });
                }
            });

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}