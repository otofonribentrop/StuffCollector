package com.example.tomovico.stuffcollector;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.tomovico.stuffcollector.data.StuffContract;

public class StuffAdapter extends CursorAdapter {

    public StuffAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_view, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Reference na TexViewove u koje idu podaci
        TextView nameView = (TextView) view.findViewById(R.id.tv_ime);
        TextView cijenaView = (TextView) view.findViewById(R.id.tv_cijena);
        TextView kolicinaView = (TextView) view.findViewById(R.id.tv_kolicina);

        // Podaci iz cursora
        String name = cursor.getString(cursor.getColumnIndex(StuffContract.StuffEntry.COLUMN_STUFF_NAME));
        int cijena = cursor.getInt(cursor.getColumnIndex(StuffContract.StuffEntry.COLUMN_STUFF_CIJENA));
        int kolicina = cursor.getInt(cursor.getColumnIndex(StuffContract.StuffEntry.COLUMN_STUFF_QUANTITY));

        // Upisivanje podataka iz kursora u Textviewove
        nameView.setText(name);
        cijenaView.setText(String.valueOf(cijena));
        kolicinaView.setText(String.valueOf(kolicina));


    }
}
