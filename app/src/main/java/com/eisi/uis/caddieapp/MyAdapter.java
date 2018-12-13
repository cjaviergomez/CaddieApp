package com.eisi.uis.caddieapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

//Clase adaptador para enlazar con la lista de los caddies.
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<_PojoCaddie> list_caddies;
    private int layout;
    private Activity activity;
    private String fromView;
    private OnItemClickListener itemClickListener;


    public MyAdapter( List<_PojoCaddie> list_caddies, int layout, Activity activity, String fromView, OnItemClickListener listener) {
        this.list_caddies      = list_caddies;
        this.layout            = layout;
        this.activity          = activity;
        this.fromView          = fromView;
        this.itemClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflamos el layout y se lo pasamos al constructor del ViewHolder, donde manejaremos
        // toda la lógica como extraer los datos, referencias...
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Llamamos al método Bind del ViewHolder pasándole objeto y listener
        holder.bind(list_caddies.get(position),itemClickListener);
    }

    @Override
    public int getItemCount() {
        return list_caddies.size();
    }

    //Clase ViewHolder

    // Implementamos las interfaces OnCreateContextMenuListener y OnMenuItemClickListener
    // para hacer uso del context menu en RecyclerView, y sobreescribimos los métodos
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{
        // Elementos UI a rellenar
        public TextView textViewName;
        public ImageView imageViewFoto;
        public TextView textViewCategoria;
        public TextView textViewEstado;

        public ViewHolder(View itemView) {
            // Recibe la View completa. La pasa al constructor padre y enlazamos referencias UI
            // con nuestras propiedades ViewHolder declaradas justo arriba.
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewCaddieName);
            imageViewFoto = itemView.findViewById(R.id.imageViewCaddieFoto);
            textViewCategoria = itemView.findViewById(R.id.textViewCategoria);
            textViewEstado = itemView.findViewById(R.id.textViewEstado);
            // Añadimos al view el listener para el context menu, en vez de hacerlo en
            // el activity mediante el método registerForContextMenu
            itemView.setOnCreateContextMenuListener(this);

        }

        public void bind(final _PojoCaddie caddie,final OnItemClickListener listener) {
            // Procesamos los datos a renderizar
            this.textViewName.setText(caddie.nombres + " " + caddie.apellidos);
            Glide.with(activity).load(caddie.foto).fitCenter().centerCrop().into(imageViewFoto);
            this.textViewCategoria.setText(caddie.categoria);
            this.textViewEstado.setText(caddie.estado);
            if(caddie.estado.equals("Ocupado") || caddie.estado.equals("PFC")){
                textViewEstado.setTextColor(ContextCompat.getColor(activity, R.color.colorAlert));
                textViewEstado.setTypeface(null, Typeface.BOLD);
            }else{
                textViewEstado.setTextColor(ContextCompat.getColor(activity, R.color.colorllamada));
                textViewEstado.setTypeface(null, Typeface.NORMAL);
            }
            // Definimos que por cada elemento de nuestro recycler view, tenemos un click listener
            // que se comporta de la siguiente manera...
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // ... pasamos nuestro objeto modelo (este caso String) y posición
                    listener.onItemClick(caddie, getAdapterPosition());
                }
            });
        }
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            // Recogemos la posición con el método getAdapterPosition
            _PojoCaddie caddieSelected = list_caddies.get(this.getAdapterPosition());
            // Establecemos título e icono para cada elemento, mirando en sus propiedades
            menu.setHeaderTitle(caddieSelected.nombres + " " + caddieSelected.apellidos);
            // Inflamos el menú
            MenuInflater inflater = activity.getMenuInflater();
            if (fromView.equals("CaddieMaster")){
                inflater.inflate(R.menu.context_menu, menu);
            }else{
                inflater.inflate(R.menu.context_menu_golfista, menu);
            }
            // Por último, añadimos uno por uno, el listener onMenuItemClick para
            // controlar las acciones en el contextMenu, anteriormente lo manejábamos
            // con el método onContextItemSelected en el activity
            for (int i = 0; i < menu.size(); i++)
                menu.getItem(i).setOnMenuItemClickListener(this);

        }

        // Sobreescribimos onMenuItemClick, dentro del ViewHolder,
        // en vez de hacerlo en el activity bajo el nombre onContextItemSelected
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            // No obtenemos nuestro objeto info
            // porque la posición la podemos rescatar desde getAdapterPosition
            switch (item.getItemId()) {
                case R.id.delete_caddie:
                    //Borramos de la DB
                    String caddieID = list_caddies.get(getAdapterPosition()).id;
                    final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                    dbRef.child("caddies/" + caddieID).removeValue();

                    // Borramos caddie clickeado de la lista de caddies
                    list_caddies.remove(getAdapterPosition());

                    // Notificamos al adaptador del cambio producido
                    notifyItemRemoved(getAdapterPosition());

                    //Revisamos si con el cambio la base de datos quedó vacia.
                    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.hasChild("caddies")) {
                                Toast.makeText(activity, "No existen caddies!", Toast.LENGTH_SHORT).show();
                                activity.finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(activity, "Error DB", Toast.LENGTH_LONG).show();
                        }
                    });

                    return true;
                case R.id.edit_caddie:
                    String IDcaddie = list_caddies.get(getAdapterPosition()).id;
                    Intent intent = new Intent(activity, EditarCaddie.class);
                    intent.putExtra("caddieID", IDcaddie);
                    activity.startActivity(intent);
                    return true;

                case R.id.ver_caddie:
                    String selectionID = list_caddies.get(getAdapterPosition()).id;
                    Intent intentvercaddie = new Intent(); // Me toca crear el new Intent(), si no me da error la línea de putExtra

                    if (fromView.equals("CaddieMaster")) {
                        intentvercaddie = new Intent(activity, CaddieCaddieMaster.class);
                    } else if (fromView.equals("Golfista")) {
                        intentvercaddie = new Intent(activity, CaddieGolfista.class);
                    }

                    intentvercaddie.putExtra("caddieID", selectionID);
                    activity.startActivity(intentvercaddie);
                    return true;

                case R.id.reservar_caddie:

                    String ID = list_caddies.get(getAdapterPosition()).id;
                    String infoCaddie = list_caddies.get(getAdapterPosition()).nombres + " " + list_caddies.get(getAdapterPosition()).apellidos;
                    String estado = list_caddies.get(getAdapterPosition()).estado;

                    if(estado.equals("Disponible")) {
                        DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference();
                        _PojoReserva pojoReserva = new _PojoReserva();
                        pojoReserva.setCaddie(ID);
                        pojoReserva.setGolfista("Tiger Woods");
                        pojoReserva.setEstado("Pendiente");
                        pojoReserva.setInfocaddie(infoCaddie);

                        DatabaseReference newReserva = DbRef.child("reservas").push();
                        newReserva.setValue(pojoReserva);
                        Toast.makeText(activity, "Reserva Creada", Toast.LENGTH_SHORT).show();
                        activity.finish();
                    } else {
                        Toast.makeText(activity, "Caddie no disponible", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                default:
                    return false;
            }
        }

    }
    // Declaramos nuestra interfaz con el/los método/s a implementar
    public interface OnItemClickListener {
        void onItemClick(_PojoCaddie caddie, int position);
    }
}
