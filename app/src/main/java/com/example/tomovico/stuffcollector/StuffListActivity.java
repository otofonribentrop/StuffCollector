package com.example.tomovico.stuffcollector;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tomovico.stuffcollector.data.StuffContract;
import com.example.tomovico.stuffcollector.data.StuffDbHelper;

public class StuffListActivity extends AppCompatActivity {
    private StuffAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stuff_list);

        showStuff();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.stuff_activity_menu, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        showStuff();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemSelected = item.getItemId();

        switch (itemSelected) {
            case R.id.menu_item_add_dummy:
                addDummyStuff();
                showStuff();
                break;
            case R.id.menu_item_add_one:
                addOneStuff();
                break;
            case R.id.menu_item_delete_all:
                deleteAll();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addDummyStuff() {

        // Kreiram podatke koji se unose u bazu
        ContentValues values = new ContentValues();
        values.put(StuffContract.StuffEntry.COLUMN_STUFF_NAME, "Zagor Strip");
        values.put(StuffContract.StuffEntry.COLUMN_STUFF_CIJENA, 4);
        values.put(StuffContract.StuffEntry.COLUMN_STUFF_QUANTITY, 10);
        values.put(StuffContract.StuffEntry.COLUMN_STUFF_PRODUCER, "Veseli Četvrtak");
        values.put(StuffContract.StuffEntry.COLUMN_SUPPLIER_NAME, "Veseli Četvrtak");
        values.put(StuffContract.StuffEntry.COLUMN_SUPPLIER_PHONE, "+381111234567");
        values.put(StuffContract.StuffEntry.COLUMN_SUPPLIER_EMAIL, "nabavka@veselicetvrtak.com");

        // Unosim podatke u db pomocu ContentProvider klase
        Uri currentUri = getContentResolver().insert(StuffContract.StuffEntry.CONTENT_URI, values);

        // Provjera validnosti unesenog dummy podatka
        if (currentUri == null) {
            Toast.makeText(this, R.string.greska_upisa, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.ispravan_upis, Toast.LENGTH_SHORT).show();
        }
    }

    private void showStuff() {

        // Kreiram projection
        String projection[] = new String[] {
                StuffContract.StuffEntry._ID,
                StuffContract.StuffEntry.COLUMN_STUFF_NAME,
                StuffContract.StuffEntry.COLUMN_STUFF_CIJENA,
                StuffContract.StuffEntry.COLUMN_STUFF_QUANTITY,
                StuffContract.StuffEntry.COLUMN_STUFF_PRODUCER,
                StuffContract.StuffEntry.COLUMN_SUPPLIER_NAME,
                StuffContract.StuffEntry.COLUMN_SUPPLIER_PHONE,
                StuffContract.StuffEntry.COLUMN_SUPPLIER_EMAIL
        };

        // Cursor preko ContentProvidera
        Cursor cursor = getContentResolver().query(StuffContract.StuffEntry.CONTENT_URI, projection, null, null, null, null);

        // Kreiram Adapter
        adapter = new StuffAdapter(this, cursor);

        // Referenca na listView
        ListView listView = (ListView) findViewById(R.id.activity_stuff_list_view);

        // Povezujem adapter sa listom
        listView.setAdapter(adapter);

        // Postavljam OnListItemClickListener
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Pokrecem novi Activity i moram da mu posaljem Uri koji editujem
                Uri currentUri = ContentUris.withAppendedId(StuffContract.StuffEntry.CONTENT_URI, id);
                Intent intent = new Intent(StuffListActivity.this, AddStuffActivity.class);
                intent.setData(currentUri);
                startActivity(intent);
            }
        });
    }

    private void addOneStuff() {
        // Pokrecem activity AddStuffActivity
        Intent intent = new Intent(StuffListActivity.this, AddStuffActivity.class);
        startActivity(intent);
    }

    private void deleteAll() {

    }
}
