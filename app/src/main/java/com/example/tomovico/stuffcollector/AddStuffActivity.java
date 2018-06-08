package com.example.tomovico.stuffcollector;

import android.app.Dialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tomovico.stuffcollector.data.StuffContract;
import com.example.tomovico.stuffcollector.data.StuffDbHelper;

public class AddStuffActivity
        extends AppCompatActivity
        implements ExitActivityDialog.NoticeDialogListener, LoaderManager.LoaderCallbacks<Cursor> {

    // ID loadera
    public static final int EDIT_STUFF_LOADER_ID = 1;

    // Lokalna varijabla za tekuci uri edit activitija
    private Uri currentUri = null;

    // Lokalna varijabla koja cuva tekuci cursor
    private Cursor currentCursor = null;

    // Varijable u kojima cuvam Viewove sa podacima
    private EditText et_stuff_producer;
    private EditText et_stuff_name;
    private EditText et_stuff_price;
    private Spinner spinner_tip_proizvoda;
    private EditText et_supplier_name;
    private EditText et_supplier_email;
    private EditText et_supplier_phone;
    private EditText et_stuff_kolicina;

    // Varijabla u kome cuvam ono sto je spinner selektovao
    private int stuffType;

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
        currentUri = currentIntent.getData();

        Log.e("AddStuff onCreate","curentUri = " + currentUri);

        // Postavljam reference na Viewove
        et_stuff_producer = (EditText) findViewById(R.id.et_stuff_producer);
        et_stuff_name = (EditText) findViewById(R.id.et_stuff_name);
        et_stuff_price = (EditText) findViewById(R.id.et_stuff_price);
        spinner_tip_proizvoda = (Spinner) findViewById(R.id.spinner_tip_proizvoda);
        et_supplier_name = (EditText) findViewById(R.id.et_supplier_name);
        et_supplier_email = (EditText) findViewById(R.id.et_supplier_email);
        et_supplier_phone = (EditText) findViewById(R.id.et_supplier_phone);
        et_stuff_kolicina = (EditText) findViewById(R.id.et_stuff_kolicina);

        // Postavljam OnTouchListener na sve Viewove
        et_stuff_producer.setOnTouchListener(onTouchListener);
        et_stuff_name.setOnTouchListener(onTouchListener);
        et_stuff_price.setOnTouchListener(onTouchListener);
        et_supplier_name.setOnTouchListener(onTouchListener);
        et_supplier_email.setOnTouchListener(onTouchListener);
        et_supplier_phone.setOnTouchListener(onTouchListener);
        et_stuff_kolicina.setOnTouchListener(onTouchListener);

        // Podesim spinner
        setupSpinner();

        // Ukoliko Uri nije prazan onda sam u edit modu
        if (currentUri != null) {
            setTitle(R.string.edit_stuff_mode);

            //Inicijalizujem loader
            getLoaderManager().initLoader(EDIT_STUFF_LOADER_ID, null, this);
        }
        // Ukoliko je Uri prazan onda sam u add new stuff modu
        else {
            setTitle(R.string.add_stuff_mode);
        }


    }

    private void setupSpinner() {
        // Referenca na spiner
        spinner_tip_proizvoda = (Spinner) findViewById(R.id.spinner_tip_proizvoda);

        // Adapter koji puni spinner
        ArrayAdapter spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.tipovi_proizvoda, android.R.layout.simple_spinner_item);

        // Specifikacija dropdown styla
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Asociranje spinera sa array adapterom
        spinner_tip_proizvoda.setAdapter(spinnerAdapter);

        // Postavljam OnItemSelectedListener na spinner i ocitavam njegovo stanje kada se aktivira listener
        spinner_tip_proizvoda.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String spinnerSelection = (String) parent.getItemAtPosition(position);

                if (!TextUtils.isEmpty(spinnerSelection)) {
                    if (spinnerSelection.equals("Nov")) {
                        stuffType = StuffContract.StuffEntry.TYPE_NEW;
                    } else if (spinnerSelection.equals("Polovan")) {
                        stuffType = StuffContract.StuffEntry.TYPE_USED;
                    } else if (spinnerSelection.equals("Nepoznato")) {
                        stuffType = StuffContract.StuffEntry.TYPE_UNKNOWN;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void addStuffMode() {

    }

    private void editStuffMode() {

        Log.e("editStuffMode", "usao u metod EditStuffMode");

        if (currentCursor == null) {
            Log.e("editStuffMode", "currentCursor je NULL");
            return;
        }

        // Pozicioniranje kursora
        currentCursor.moveToFirst();

        // Uzimam podatke iz kursora
        String stuffProducer = currentCursor.getString(currentCursor.getColumnIndex(StuffContract.StuffEntry.COLUMN_STUFF_PRODUCER));
        String stuffName = currentCursor.getString(currentCursor.getColumnIndex(StuffContract.StuffEntry.COLUMN_STUFF_NAME));
        String stuffPrice = Float.toString(currentCursor.getFloat(currentCursor.getColumnIndex(StuffContract.StuffEntry.COLUMN_STUFF_CIJENA)));
        int tipProizvoda = currentCursor.getInt(currentCursor.getColumnIndex(StuffContract.StuffEntry.COLUMN_STUFF_TYPE));
        String supplierName = currentCursor.getString(currentCursor.getColumnIndex(StuffContract.StuffEntry.COLUMN_SUPPLIER_NAME));
        String supplierEmail = currentCursor.getString(currentCursor.getColumnIndex(StuffContract.StuffEntry.COLUMN_SUPPLIER_EMAIL));
        String supplierPhone = currentCursor.getString(currentCursor.getColumnIndex(StuffContract.StuffEntry.COLUMN_SUPPLIER_PHONE));
        String stuffKolicina = Integer.toString(currentCursor.getInt(currentCursor.getColumnIndex(StuffContract.StuffEntry.COLUMN_STUFF_QUANTITY)));

        // Popunjavam polja activitija sa podacima iz cursora
        et_stuff_producer.setText(stuffProducer);
        Log.e("editStuffMode", "stuffProducer: " + stuffProducer);

        et_stuff_name.setText(stuffName);
        Log.e("editStuffMode", "stuffName: " + stuffName);

        et_stuff_price.setText(stuffPrice + "");
        Log.e("editStuffMode", "stuffPrice: " + stuffPrice);

        et_supplier_name.setText(supplierName);
        Log.e("editStuffMode", "supplierName: " + supplierName);

        et_supplier_email.setText(supplierEmail);
        Log.e("editStuffMode", "supplierEmail: " + supplierEmail);

        et_supplier_phone.setText(supplierPhone);
        Log.e("editStuffMode", "supplierPhone: " + supplierPhone);

        et_stuff_kolicina.setText(stuffKolicina + "");
        Log.e("editStuffMode", "stuffKolicina: " + stuffKolicina);


        switch (tipProizvoda) {
            case StuffContract.StuffEntry.TYPE_NEW:
                spinner_tip_proizvoda.setSelection(StuffContract.StuffEntry.TYPE_NEW);
                stuffType = StuffContract.StuffEntry.TYPE_NEW;
                break;
            case StuffContract.StuffEntry.TYPE_USED:
                spinner_tip_proizvoda.setSelection(StuffContract.StuffEntry.TYPE_NEW);
                stuffType = StuffContract.StuffEntry.TYPE_USED;
                break;
            case StuffContract.StuffEntry.TYPE_UNKNOWN:
                spinner_tip_proizvoda.setSelection(StuffContract.StuffEntry.TYPE_NEW);
                stuffType = StuffContract.StuffEntry.TYPE_UNKNOWN;
                break;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Trazim selektovani item
        int itemSelected = item.getItemId();

        // Na osnovu izabranog itema biram akciju
        switch (itemSelected) {
            case R.id.snimi_stuff:
                // Ukoliko tekuci stuff nije izmjenjen onda posalji poruku da se pokusava snimati nesto sto nije izmjenjeno
                if (!stuffHasChanged) {
                    // Prikaz poruke
                    DialogFragment snimasBezIzmjene = new NoChangeDialog();
                    snimasBezIzmjene.show(getSupportFragmentManager(), "EE");
                    return true;
                } else {
                    snimiStuff();
                    finish();
                    return true;
                }

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
        String stuffName = et_stuff_name.getText().toString().trim();
        String stuffProducer = et_stuff_producer.getText().toString().trim();
        float stuffPrice = Float.valueOf(et_stuff_price.getText().toString().trim());
        int spinnerState = stuffType;
        String supplierName = et_supplier_name.getText().toString().trim();
        String supplierEmail = et_supplier_email.getText().toString().trim();
        String supplierPhone = et_supplier_phone.getText().toString().trim();
        int stuffKolicina = Integer.valueOf(et_stuff_kolicina.getText().toString().trim());


        // Kreiram vrijednosti koje upisujem u bazu
        ContentValues values = new ContentValues();
        values.put(StuffContract.StuffEntry.COLUMN_STUFF_NAME, stuffName);
        values.put(StuffContract.StuffEntry.COLUMN_STUFF_PRODUCER, stuffProducer);
        values.put(StuffContract.StuffEntry.COLUMN_STUFF_CIJENA, stuffPrice);
        values.put(StuffContract.StuffEntry.COLUMN_STUFF_TYPE, spinnerState);
        values.put(StuffContract.StuffEntry.COLUMN_SUPPLIER_NAME, supplierName);
        values.put(StuffContract.StuffEntry.COLUMN_SUPPLIER_EMAIL, supplierEmail);
        values.put(StuffContract.StuffEntry.COLUMN_SUPPLIER_PHONE, supplierPhone);
        values.put(StuffContract.StuffEntry.COLUMN_STUFF_QUANTITY, stuffKolicina);

        // Provjeravam u kom sam modu
        // Ako sam u Add New Stuff Modu onda zovem insert metod content provajdera
        if (currentUri == null) {
            // Upisujem u bazu koristenjem ContentProvidera
            Uri vraceniUri = getContentResolver().insert(StuffContract.StuffEntry.CONTENT_URI, values);

            // Provjera upisa na osnovu povratnog urija
            if (vraceniUri == null) {
                Toast.makeText(this, R.string.greska_upisa, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.ispravan_upis, Toast.LENGTH_SHORT).show();
            }
        } else {
            // Ako sam u edit stuff modu onda zovem update metod content provajdera
            int rowId = getContentResolver().update(currentUri, values, null, null);

            if (rowId < 0) {
                Toast.makeText(this, R.string.greska_upisa, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.ispravan_upis, Toast.LENGTH_SHORT).show();
            }

        }



    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.e("onCreateLoader", "usao u onCreateLoader");
        String projection[] = {
                StuffContract.StuffEntry._ID,
                StuffContract.StuffEntry.COLUMN_STUFF_NAME,
                StuffContract.StuffEntry.COLUMN_STUFF_PRODUCER,
                StuffContract.StuffEntry.COLUMN_STUFF_CIJENA,
                StuffContract.StuffEntry.COLUMN_STUFF_QUANTITY,
                StuffContract.StuffEntry.COLUMN_STUFF_TYPE,
                StuffContract.StuffEntry.COLUMN_SUPPLIER_NAME,
                StuffContract.StuffEntry.COLUMN_SUPPLIER_PHONE,
                StuffContract.StuffEntry.COLUMN_SUPPLIER_EMAIL
        };

        switch (id) {
            case EDIT_STUFF_LOADER_ID:
                return new CursorLoader(this, currentUri, projection, null, null, null);
            default:
                throw new IllegalArgumentException("NESTO NAOPAKO SA KURSOROM EDIT ACTIVITIJA BRALE!");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        currentCursor = data;

        // Pozivam metod koji cita podatke iz tekuceg kursora i popunjava polja activitija
        editStuffMode();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        currentCursor = null;
    }
}
