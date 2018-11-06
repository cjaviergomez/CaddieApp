package com.eisi.uis.caddieapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

//Clase adaptador para enlazar con la lista de los caddies.
public class Myadapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<String> names;

    public Myadapter(Context context, int layout, List<String> names){
        this.context = context;
        this.layout  = layout;
        this.names   = names;
    }

    @Override
    public int getCount() {
        return this.names.size();
    }

    @Override
    public Object getItem(int position) {
        return this.names.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //View Holder Pattern: Patrón para mejorar el rendimiento de la ListView
        ViewHolder holder;

        if(convertView == null){
            //Inflamos la vista que nos ha llegado con nuestro layout personalizado
            LayoutInflater layoutInflater = LayoutInflater.from(this.context);
            convertView = layoutInflater.inflate(this.layout, null);

            holder = new ViewHolder();
            //Referenciamos el elementos a modificar y lo rellenamos
            holder.nameTextView = (TextView) convertView.findViewById(R.id.textViewCaddieName);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        //Nos traemos el valor actual dependiente de la posición
        String currentName = names.get(position);
        //Referenciamos el elementos a modificar y lo rellenamos
        holder.nameTextView.setText(currentName);

        //Devolvemos la vista inflada y modificada con nuestro datos.
        return convertView;
    }

    static class ViewHolder{
        private TextView nameTextView;
    }
}
