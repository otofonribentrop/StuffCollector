package com.example.tomovico.stuffcollector;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tomovico.stuffcollector.data.StuffContract;
import com.example.tomovico.stuffcollector.data.StuffDbHelper;

public class AddStuffActivity extends AppCompatActivity implements ExitActivityDialog.NoticeDialogListener {

    // Varijable u kojima cuvam Viewove sa podacima
    private EditText et_stuff_producer;
    private EditText et_stuff_name;
    private EditText et_stuff_price;
    private Spinner spinner_tip_proizvoda;
    private EditText et_supplier_name;
    private EditText et_supplier_email;
    private EditText et_supplier_phone;
    private EditText et_stuff_kolicina;

    // Objekat u kome cuvam ono sto je spinner selektovao
    private Object spinnerSelection;

    // Varijabla koja cuva stanje promjena edit polja
    private boolean stuffHasChanged = false;

    // Postavljam touch listener koji ce da promijeni state od stuffHasChanged
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            stuffHasChanged = true;
            Log.e("onItemListener", "stuffHasChanged = " + stuffHasChanged);

            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stuff);

        // Intent koji je pokrenuo ovaj activity
        Intent currentIntent = getIntent();

        // Dobijam Uri koji se nalazi u intentu
        Uri currentUri = currentIntent.getData();

        // Ukoliko je Uri prazan onda sam u add new stuff modu
        if (currentUri != null) {
            setTitle(R.string.edit_stuff_mode);
        }
        // Ukoliko Uri nije prazan onda sam u edit modu
        else {
            setTitle(R.string.add_stuff_mode);
        }

        // Referenca na spiner
        spinner_tip_proizvoda = (Spinner) findViewById(R.id.spinner_tip_proizvoda);

        // Postavljam OnItemSelectedListener na spinner
        spinner_tip_proizvoda.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerSelection = parent.getItemAtPosition(position);
               // stuffHasChanged = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Reference na Viewove
        et_stuff_producer = (EditText) findViewById(R.id.et_stuff_producer);
        et_stuff_name = (EditText) findViewById(R.id.et_stuff_name);
        et_stuff_price = (EditText) findViewById(R.id.et_stuff_price);
        spinner_tip_proizvoda = (Spinner) findViewById(R.id.spinner_tip_proizvoda);
        et_supplier_name = (EditText) findViewById(R.id.et_supplier_name);
        et_supplier_email = (EditText) findViewById(R.id.et_supplier_email);
        et_supplier_phone = (EditText) findViewById(R.id.et_stuff_price);
        et_stuff_kolicina = (EditText) findViewById(R.id.et_stuff_kolicina);

        // Postavljam OnTouchListener na sve Viewove
        et_stuff_producer.setOnTouchListener(onTouchListener);
        et_stuff_name.setOnTouchListener(onTouchListener);
        et_stuff_price.setOnTouchListener(onTouchListener);
        et_supplier_name.setOnTouchListener(onTouchListener);
        et_supplier_email.setOnTouchListener(onTouchListener);
        et_supplier_phone.setOnTouchListener(onTouchListener);
        et_stuff_kolicina.setOnTouchListener(onTouchListener);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Trazim selektovani item
        int itemSelected = item.getItemId();

        // Na osnovu izabranog itema biram akciju
        switch (itemSelected) {
            case R.id.snimi_stuff:
                snimiStuff();
            case android.R.id.home:
                // Ukoliko tekuci stuff nije izmjenjen onda izlazim normalno
                if (!stuffHasChanged) {
                    NavUtils.navigateUpFromSameTask(AddStuffActivity.this);
                    return true;
                }

                // Ukoliko stuff jeste izmjenjen onda prikazujem dijalog meni sa upozorenjem
                DialogFragment noviFragment = new ExitActivityDialog();
                noviFragment.show(getSupportFragmentManager(), "AAAAAAAAAAAAAAAAAAAAAA");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_stuff_activity_menu, menu);
        return true;
    }

    public void snimiStuff() {
        // Sakupljam podatke iz Viewa
        String stuffProducer = et_stuff_producer.getText().toString().trim();
        String stuffName = et_stuff_name.getText().toString().trim();
        String stuffPrice = et_stuff_price.getText().toString().trim();
        String supplierName = et_supplier_name.getText().toString().trim();
        String supplierEmail = et_supplier_email.getText().toString().trim();
        String supplierPhone = et_supplier_phone.getText().toString().trim();
        String tipProizvoda = spinnerSelection.toString();
        int stuffKolicina = Integer.valueOf(et_stuff_kolicina.getText().toString().trim());

        // Kreiram vrijednosti koje upisujem u bazu
        ContentValues values = new ContentValues();
        values.put(StuffContract.StuffEntry.COLUMN_STUFF_NAME, stuffName);
        values.put(StuffContract.StuffEntry.COLUMN_STUFF_PRODUCER, stuffProducer);
        values.put(StuffContract.StuffEntry.COLUMN_STUFF_CIJENA, Integer.valueOf(stuffPrice));
        values.put(StuffContract.StuffEntry.COLUMN_STUFF_QUANTITY, Integer.valueOf(stuffKolicina));
        values.put(StuffContract.StuffEntry.COLUMN_SUPPLIER_NAME, supplierName);
        values.put(StuffContract.StuffEntry.COLUMN_SUPPLIER_EMAIL, supplierEmail);
        values.put(StuffContract.StuffEntry.COLUMN_SUPPLIER_PHONE, supplierPhone);

        // Upisujem u bazu koristenjem ContentProvidera
        Uri currentUri = getContentResolver().insert(StuffContract.StuffEntry.CONTENT_URI, values);

        // Provjera upisa na osnovu povratnog urija
        if (currentUri == null) {
            Toast.makeText(this, R.string.greska_upisa, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.ispravan_upis, Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        NavUtils.navigateUpFromSameTask(this);
    }

    public void testniMetodkojiNeradiNista() {

    }

    public void testniMetodkojiNeradiNista2() {

    }
    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }
}
